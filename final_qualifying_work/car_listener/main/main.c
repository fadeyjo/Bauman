#include <stdio.h>
#include <stdbool.h>
#include <unistd.h>
#include "can.h"
#include "esp_system.h"
#include "esp_timer.h"
#include "freertos/projdefs.h"
#include "hal/gpio_types.h"
#include "hal/uart_types.h"
#include "lwip/err.h"
#include "mcp2515.h"
#include "driver/spi_master.h"
#include "driver/gpio.h"
#include "esp_log.h"
#include "portmacro.h"
#include "driver/uart.h"

#define MCP2515_CS_PIN 5
#define MCP2515_MISO_PIN 19
#define MCP2515_MOSI_PIN 23
#define MCP2515_CLK_PIN 18
#define MCP2515_INT_PIN 4
#define MCP2515_MAX_TRANSFER_SZ 64
#define MCP2515_CLOCK_SPEED_HZ 8000000
#define MCP2515_QUEUE_SIZE 1024
#define MCP2515_SPI_HOST_ID SPI2_HOST
#define MCP2515_CAN_SPEED CAN_500KBPS
#define MCP2515_CAN_CLOCK MCP_16MHZ
#define MCP2515_SET_ONE_SHOT_MODE true

#define USE_CAN_FILTERS 1

static const char* MCP2515_SPI = "MCP2515 SPI";
static const char* MCP2515_DEVICE = "MCP2515";

TaskHandle_t mcp2515ServiceDataTaskHandle = NULL;
TaskHandle_t mcp2515DataTaskHandle = NULL;

QueueHandle_t canQueue;
QueueHandle_t canInterruptQueue;

unsigned int last_num = 0x00;

const char *error_to_str(ERROR_t err)
{
    switch (err)
    {
    	case ERROR_FAIL: return "ERROR_FAIL";
    	case ERROR_ALLTXBUSY: return "ERROR_ALLTXBUSY";
    	case ERROR_FAILINIT: return "ERROR_FAILINIT";
    	case ERROR_FAILTX: return "ERROR_FAILTX";
    	default: return "ERROR_NOMSG";;
    }
}

// Возвращает true, если была ошибка
bool log_error(const char *TAG, const char *text, ERROR_t err)
{
	if (err == ERROR_OK) return false;
	
	ESP_LOGE(TAG, "%s: %s", error_to_str(err), text);
	
	return true;
}

void can_bus_spi_init(void)
{
	spi_bus_config_t bus_cfg={
		.miso_io_num = MCP2515_MISO_PIN,
		.mosi_io_num = MCP2515_MOSI_PIN,
		.sclk_io_num = MCP2515_CLK_PIN,
		.quadwp_io_num = -1,
		.quadhd_io_num = -1,
		.max_transfer_sz = MCP2515_MAX_TRANSFER_SZ
	};
	
	esp_err_t ret = spi_bus_initialize(MCP2515_SPI_HOST_ID, &bus_cfg, SPI_DMA_CH_AUTO);
	ESP_ERROR_CHECK(ret);
	
	ESP_LOGI(MCP2515_SPI, "MCP2515 SPI bus successfully initialized");
	
	
	spi_device_interface_config_t dev_cfg = {
		.mode = 0,
		.clock_speed_hz = MCP2515_CLOCK_SPEED_HZ,
		.spics_io_num = MCP2515_CS_PIN,
		.queue_size = MCP2515_QUEUE_SIZE
	};

    ret = spi_bus_add_device(MCP2515_SPI_HOST_ID, &dev_cfg, &MCP2515_Object->spi);
    ESP_ERROR_CHECK(ret);
    
    ESP_LOGI(MCP2515_SPI, "MCP2515 SPI device successfully initialized");
}

void IRAM_ATTR mcp2515_isr_handler(void *args)
{
	uint8_t dummy = 1;
	
	BaseType_t xHigherPriorityTaskWoken = pdFALSE;
	
	xQueueSendFromISR(canInterruptQueue, &dummy, &xHigherPriorityTaskWoken);
	
	if (xHigherPriorityTaskWoken)
        portYIELD_FROM_ISR();
}

void mcp2515_init_intr(void)
{
	gpio_config_t io_conf = {
        .intr_type = GPIO_INTR_NEGEDGE,
        .mode = GPIO_MODE_INPUT,
        .pin_bit_mask = 1ULL << MCP2515_INT_PIN,
        .pull_up_en = GPIO_PULLUP_ENABLE,
        .pull_down_en = GPIO_PULLDOWN_DISABLE
    };

    esp_err_t ret = gpio_config(&io_conf);
    ESP_ERROR_CHECK(ret);
    
    ret = gpio_install_isr_service(0);
    ESP_ERROR_CHECK(ret);
    
    ret = gpio_isr_handler_add(MCP2515_INT_PIN, mcp2515_isr_handler, NULL);
    ESP_ERROR_CHECK(ret);
}

void can_bus_init(void)
{
	ESP_LOGI(MCP2515_DEVICE, "Starting configuration of MCP2515...");
	
	ERROR_t err = MCP2515_init();
	if (log_error(MCP2515_DEVICE, "trying to initialize MCP2515 object is failure", err))
	{
		esp_restart();
		
		return;
	}
	ESP_LOGI(MCP2515_DEVICE, "MCP2515 object successfully initialized");
	
	can_bus_spi_init();
    ESP_LOGI(MCP2515_DEVICE, "MCP2515 SPI successfully initialized");
    
    mcp2515_init_intr();
    ESP_LOGI(MCP2515_DEVICE, "MCP2515 SPI INTR successfully initialized");
	
	err = MCP2515_reset();
	if (log_error(MCP2515_DEVICE, "trying to MCP2515 reseting is failure", err))
	{
		esp_restart();
		
		return;
	}
	ESP_LOGI(MCP2515_DEVICE, "MCP2515 successfully reseted");
	
	err = MCP2515_setBitrate(MCP2515_CAN_SPEED, MCP2515_CAN_CLOCK);
	if (log_error(MCP2515_DEVICE, "trying to set MCP2515 bitrate is failure", err))
	{
		esp_restart();
		
		return;
	}
	
	ESP_LOGI(MCP2515_DEVICE, "MCP2515 has successfully set the bitrate");
	
#if USE_CAN_FILTERS

	err =  MCP2515_setFilterMask(MASK0, false, 0xffc);
	if (log_error(MCP2515_DEVICE, "trying to set mask0, starting to restart controller...", err))
	{
		esp_restart();
		
		return;
	}
	else
	{
		ESP_LOGI(MCP2515_DEVICE, "MCP2515 has successfully set the mask0");
	}

	err = MCP2515_setFilter(RXF0, false, 0x7e8);
	if (log_error(MCP2515_DEVICE, "trying to set RXF0, starting to restart controller...", err))
	{
		esp_restart();
		
		return;
	}
	else
	{
		ESP_LOGI(MCP2515_DEVICE, "MCP2515 has successfully set the RXF0");
	}
	
	err =  MCP2515_setFilterMask(MASK1, false, 0xffc);
	if (log_error(MCP2515_DEVICE, "trying to set mask1, starting to restart controller...", err))
	{
		esp_restart();
		
		return;
	}
	else
	{
		ESP_LOGI(MCP2515_DEVICE, "MCP2515 has successfully set the mask1");
	}
	
	err = MCP2515_setFilter(RXF2, false, 0x7ec);
	if (log_error(MCP2515_DEVICE, "trying to set RXF2, starting to restart controller...", err))
	{
		esp_restart();
		
		return;
	}
	else
	{
		ESP_LOGI(MCP2515_DEVICE, "MCP2515 has successfully set the RXF2");
	}
	
	ESP_LOGI(MCP2515_DEVICE, "MCP2515 has successfully set the RXF0, RXF2, mask0 and mask1");
	
#endif

	err = MCP2515_setNormalMode();
	if (log_error(MCP2515_DEVICE, "trying to set NormalMode", err))
	{
		esp_restart();
		
		return;
	}
	
	const char *text = NULL;
	if (MCP2515_SET_ONE_SHOT_MODE)
	{
		text = "trying to set One Shot Mode";
	}
	else
	{
		text = "trying to unset One Shot Mode";
	}
	
	err = MCP2515_setOneShotMode(MCP2515_SET_ONE_SHOT_MODE);
	if (log_error(MCP2515_DEVICE, text, err))
	{
		esp_restart();
		
		return;
	}
	
	ESP_LOGI(MCP2515_DEVICE, "MCP2515 has successfully set Normal Operation mode");
	
	ESP_LOGI(MCP2515_DEVICE, "MCP2515 has successfully configured");
}

void print_can_frame(CAN_FRAME_t frame)
{
    if (!frame) return;

    int64_t timestamp_us = esp_timer_get_time();
    
    printf("[Time: %lld.%03lld ms] ", timestamp_us / 1000, timestamp_us % 1000);

    printf("CAN ID: 0x%08lX, DLC: %u, Data: ", frame->can_id, frame->can_dlc);

    for (int i = 0; i < frame->can_dlc; i++)
        printf("%02X ", frame->data[i]);
    
    for (int i = 0; i < 8 - frame->can_dlc; i++)
        printf("00 ");
    
    printf("\n");
}

void mcp2515_service_data_task(void *pvParameters)
{
    uint8_t dummy;
    CAN_FRAME_t frame;

    while (1)
    {
        if (xQueueReceive(canInterruptQueue, &dummy, portMAX_DELAY))
        {
			
			while (1)
			{
				bool has_data = false;
				
				uint8_t irq = MCP2515_getInterrupts();
	
				ERROR_t ret;
	
				if (irq & CANINTF_RX0IF)
				{
					ret = MCP2515_readMessage(RXB0, &frame);
		
    				if (!log_error(MCP2515_DEVICE, "reading message from RXB0", ret))
    				{
						xQueueSend(canQueue, &frame, pdMS_TO_TICKS(0));
						has_data = true;
					}
					
					
					if (last_num == 0x00)
					{
						last_num = (frame->data[0] << 24) + (frame->data[1] << 16) + (frame->data[2] << 8) + frame->data[3];
					}	
					else
					{
						unsigned int buf = (frame->data[0] << 24) + (frame->data[1] << 16) + (frame->data[2] << 8) + frame->data[3];
						
						if (buf != last_num + 1)
						{
							ESP_LOGE(MCP2515_DEVICE, "ERROR RXB0!!!");
							esp_restart();
						}
						else
						{
							last_num = buf;
						}
					}
    			}

    			if (irq & CANINTF_RX1IF)
    			{
        			ret = MCP2515_readMessage(RXB1, &frame);
		
    				if (!log_error(MCP2515_DEVICE, "reading message from RXB1", ret))
    				{
						xQueueSend(canQueue, &frame, pdMS_TO_TICKS(0));
						has_data = true;
					}
					
					if (last_num == 0x00)
					{
						last_num = (frame->data[0] << 24) + (frame->data[1] << 16) + (frame->data[2] << 8) + frame->data[3];
					}	
					else
					{
						unsigned int buf = (frame->data[0] << 24) + (frame->data[1] << 16) + (frame->data[2] << 8) + frame->data[3];
						
						if (buf != last_num + 1)
						{
							ESP_LOGE(MCP2515_DEVICE, "ERROR RXB1!!!");
							esp_restart();
						}
						else
						{
							last_num = buf;
						}
					}
    			}
    			
    			if (!has_data)
                	break;
			}
			
		}
    }
}

void mcp2515_data_task(void *pvParameters)
{
    CAN_FRAME_t frame;

    while (1) {
        if (xQueueReceive(canQueue, &frame, portMAX_DELAY)) {
			print_can_frame(&frame);
        }
    }
}

void app_main(void)
{
	canInterruptQueue = xQueueCreate(64, sizeof(uint8_t));
	if (!canInterruptQueue) {
    	ESP_LOGE(MCP2515_DEVICE, "Failed to create canInterruptQueue!");
    	esp_restart();
    	return;
	}
	
	canQueue = xQueueCreate(64, sizeof(CAN_FRAME_t));
	if (!canQueue) {
    	ESP_LOGE(MCP2515_DEVICE, "Failed to create canQueue!");
    	esp_restart();
    	return;
	}
	
	can_bus_init();
	
	xTaskCreate(
		mcp2515_data_task,
    	"MCP2515 Data Task",
    	4096,
    	NULL,
    	10,
		&mcp2515DataTaskHandle
	);
	
	xTaskCreate(
		mcp2515_service_data_task,
    	"MCP2515 Service Data Task",
    	4096,
    	NULL,
    	18,
		&mcp2515ServiceDataTaskHandle
	);
}
