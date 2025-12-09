#include <stdint.h>
#include <stdio.h>
#include "can.h"
#include "freertos/FreeRTOS.h"
#include "freertos/projdefs.h"
#include "freertos/task.h"
#include "driver/spi_master.h"
#include "driver/gpio.h"
#include "mcp2515.h"
#include "esp_log.h"
#include "esp_timer.h"

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
#define MCP2515_CAN_CLOCK MCP_8MHZ
#define MCP2515_SET_ONE_SHOT_MODE true

#define DELAY_FOR_SENDING 20

static const char* MCP2515_SPI = "MCP2515 SPI";
static const char* MCP2515_DEVICE = "MCP2515";

TaskHandle_t mcp2515ServiceDataTaskHandle = NULL;
TaskHandle_t mcp2515DataTaskHandle = NULL;
TaskHandle_t mcp2515SpamCANTaskHandle = NULL;

QueueHandle_t canQueue;
QueueHandle_t canInterruptQueue;

uint8_t current_pid = 0x00;
uint32_t checked_pids = 0b00000000000110000000000000000000;


uint8_t rpm_byte_a = 0x1a;
uint8_t rpm_byte_b = 0xf8;
uint8_t speed = 0x50;

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
						
    			}

    			if (irq & CANINTF_RX1IF)
    			{
        			ret = MCP2515_readMessage(RXB1, &frame);
		
    				if (!log_error(MCP2515_DEVICE, "reading message from RXB1", ret))
    				{
						xQueueSend(canQueue, &frame, pdMS_TO_TICKS(0));
						has_data = true;
					}
    			}
    			
    			if (!has_data)
                	break;
			}
			
		}
    }
}

void set_current_pid_to_request()
{
    if (!checked_pids)
    {
		esp_restart();
		return;
	}
    
    uint8_t start_bit =
    	current_pid == 0x20 || current_pid == 0x00 ?
    	0x00 :
    	current_pid;
    
    while (1)
    {
		if (start_bit == 0x20)
			start_bit = 0x00;
		
		if ((checked_pids << start_bit) & 0x80000000)
		{
			current_pid = start_bit + 0x01;
			return;
		}
		
		start_bit++;
	}
}

void send_rpm_response()
{
	CAN_FRAME_t frame;
	
	frame->can_id = 0x7e8;
	frame->can_dlc = 8;
	// 03 41 0C AA BB 00 00 00
	frame->data[0] = 0x03;
	frame->data[1] = 0x41;
	frame->data[2] = 0x0c;
	frame->data[3] = rpm_byte_a;
	frame->data[4] = rpm_byte_b;
	
	for (int i = 5; i < 8; i++)
		frame->data[i] = 0x00;
		
	ERROR_t ret = MCP2515_sendMessageAfterCtrlCheck(&frame);
			
	log_error(MCP2515_DEVICE, "sending PID response", ret);
}

void send_speed_response()
{
	CAN_FRAME_t frame;
	
	frame->can_id = 0x7e9;
	frame->can_dlc = 8;
	// 02 41 0d AA 00 00 00 00
	frame->data[0] = 0x02;
	frame->data[1] = 0x41;
	frame->data[2] = 0x0d;
	frame->data[3] = speed;
	
	for (int i = 4; i < 8; i++)
		frame->data[i] = 0x00;
		
	ERROR_t ret = MCP2515_sendMessageAfterCtrlCheck(&frame);
			
	log_error(MCP2515_DEVICE, "sending PID response", ret);
}

void send_supported_pid_response()
{
	CAN_FRAME_t frame;
	
	frame->can_id = 0x7ea;
	frame->can_dlc = 8;
	// 06 41 00 AA BB CC DD 00
	frame->data[0] = 0x06;
	frame->data[1] = 0x41;
	frame->data[2] = 0x00;
	frame->data[3] = checked_pids >> 24;
	frame->data[4] = (checked_pids >> 16) & 0xff;
	frame->data[5] = (checked_pids >> 8) & 0xff;
	frame->data[6] = checked_pids & 0xff;
	frame->data[7] = 0x00;
		
	ERROR_t ret = MCP2515_sendMessageAfterCtrlCheck(&frame);
			
	log_error(MCP2515_DEVICE, "sending PID response", ret);
}

void spam_can_bus_task(void *pvParameters)
{
	// 06 41 00 AA BB CC DD 00
	while (1)
	{
		set_current_pid_to_request();
		
		if (current_pid == 0x0c)
		{
			send_rpm_response();
		}
		else if (current_pid == 0x0d)
		{
			send_speed_response();
		}
		
		vTaskDelay(pdMS_TO_TICKS(DELAY_FOR_SENDING));
	}
}

bool is_request(CAN_FRAME_t frame)
{
	return frame->can_id == 0x7df && frame->data[0] != 0x00 && frame->data[1] == 0x01;
}

void mcp2515_data_task(void *pvParameters)
{
    CAN_FRAME_t frame;

    while (1) {
        if (xQueueReceive(canQueue, &frame, portMAX_DELAY))
        {
            print_can_frame(&frame);
			
			if (!is_request(frame))
				continue;
			
			if (frame->data[2] == 0x00)
			{
				send_supported_pid_response();
			}
			else if (frame->data[2] == 0x0c)
			{
				send_rpm_response();
			}
			else if (frame->data[2] == 0x0d)
			{
				send_speed_response();
			}
			else
			{
				continue;
			}
        }
    }
}

void app_main(void)
{
	canQueue = xQueueCreate(64, sizeof(CAN_FRAME_t));
	if (!canQueue) {
    	ESP_LOGE(MCP2515_DEVICE, "Failed to create canQueue!");
    	esp_restart();
    	return;
	}
	
	canInterruptQueue = xQueueCreate(64, sizeof(uint8_t));
	if (!canInterruptQueue) {
    	ESP_LOGE(MCP2515_DEVICE, "Failed to create canInterruptQueue!");
    	esp_restart();
    	return;
	}
	
	xTaskCreate(
		mcp2515_service_data_task,
    	"MCP2515 Service Data Task",
    	4096,
    	NULL,
    	20,
		&mcp2515ServiceDataTaskHandle
	);
	
	xTaskCreate(
		mcp2515_data_task,
    	"MCP2515 Data Task",
    	4096,
    	NULL,
    	15,
		&mcp2515DataTaskHandle
	);
	
	can_bus_init();
	
	if (false)
	xTaskCreate(
		spam_can_bus_task,
    	"MCP2515 Spam Data Task",
    	4096,
    	NULL,
    	10,
		&mcp2515SpamCANTaskHandle
	);
}
