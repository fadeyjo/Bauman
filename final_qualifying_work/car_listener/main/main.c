#include <stdio.h>
#include <stdbool.h>
#include <unistd.h>
#include "can.h"
#include "esp_timer.h"
#include "hal/gpio_types.h"
#include "lwip/err.h"
#include "mcp2515.h"
#include "driver/spi_master.h"
#include "driver/gpio.h"
#include "esp_log.h"

#define MCP2515_CS_PIN 5
#define MCP2515_MISO_PIN 19
#define MCP2515_MOSI_PIN 23
#define MCP2515_CLK_PIN 18
#define MCP2515_INT_PIN 4
#define MCP2515_MAX_TRANSFER_SZ 3
#define MCP2515_CLOCK_SPEED_HZ 8000000
#define MCP2515_QUEUE_SIZE 1024
#define MCP2515_SPI_HOST_ID SPI2_HOST
#define MCP2515_CAN_SPEED CAN_500KBPS
#define MCP2515_CAN_CLOCK MCP_8MHZ

#define USE_CAN_FILTERS 1

static const char* MCP2515_SPI = "MCP2515 SPI";
static const char* MCP2515_DEVICE = "MCP2515";

volatile bool frame_is_ready = false;

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
	frame_is_ready = true;
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

    gpio_config(&io_conf);
    
    gpio_install_isr_service(0);
    
    gpio_isr_handler_add(MCP2515_INT_PIN, mcp2515_isr_handler, mcp2515_isr_handler);
}

void can_bus_init(void)
{
	ESP_LOGI(MCP2515_DEVICE, "Starting configuration of MCP2515...");
	
	MCP2515_init();
	ESP_LOGI(MCP2515_DEVICE, "MCP2515 object successfully initialized");
	
	can_bus_spi_init();
    ESP_LOGI(MCP2515_DEVICE, "MCP2515 SPI successfully initialized");
    
    mcp2515_init_intr();
    ESP_LOGI(MCP2515_DEVICE, "MCP2515 SPI INTR successfully initialized");
	
	MCP2515_reset();
	ESP_LOGI(MCP2515_DEVICE, "MCP2515 successfully reseted");
	
	MCP2515_setBitrate(MCP2515_CAN_SPEED, MCP2515_CAN_CLOCK);
	ESP_LOGI(MCP2515_DEVICE, "MCP2515 has successfully set the bitrate");
	
#if USE_CAN_FILTERS

	ERROR_t ret =  MCP2515_setFilterMask(MASK0, false, 0xff8);
	if (ret != ERROR_OK)
	{
		if (ret == ERROR_FAIL)
		{
			ESP_LOGE(MCP2515_DEVICE, "ERROR_FAIL after trying to set mask0, starting to restart controller...");
		}
		else if (ret == ERROR_ALLTXBUSY)
		{
			ESP_LOGE(MCP2515_DEVICE, "ERROR_ALLTXBUSY after trying to set mask0, starting to restart controller...");
		}
		else if (ret == ERROR_FAILINIT)
		{
			ESP_LOGE(MCP2515_DEVICE, "ERROR_FAILINIT after trying to set mask0, starting to restart controller...");
		}
		else if (ret == ERROR_FAILTX)
		{
			ESP_LOGE(MCP2515_DEVICE, "ERROR_FAILTX after trying to set mask0, starting to restart controller...");
		}
		else
		{
			ESP_LOGE(MCP2515_DEVICE, "ERROR_NOMSG after trying to set mask0, starting to restart controller...");
		}
		
		esp_restart();
		return;
	}
	else
	{
		ESP_LOGI(MCP2515_DEVICE, "MCP2515 has successfully set the mask0");
	}

	ret = MCP2515_setFilter(RXF0, false, 0x7e8);
	if (ret != ERROR_OK)
	{
		if (ret == ERROR_FAIL)
		{
			ESP_LOGE(MCP2515_DEVICE, "ERROR_FAIL after trying to set RXF0, starting to restart controller...");
		}
		else if (ret == ERROR_ALLTXBUSY)
		{
			ESP_LOGE(MCP2515_DEVICE, "ERROR_ALLTXBUSY after trying to set RXF0, starting to restart controller...");
		}
		else if (ret == ERROR_FAILINIT)
		{
			ESP_LOGE(MCP2515_DEVICE, "ERROR_FAILINIT after trying to set RXF0, starting to restart controller...");
		}
		else if (ret == ERROR_FAILTX)
		{
			ESP_LOGE(MCP2515_DEVICE, "ERROR_FAILTX after trying to set RXF0, starting to restart controller...");
		}
		else
		{
			ESP_LOGE(MCP2515_DEVICE, "ERROR_NOMSG after trying to set RXF0, starting to restart controller...");
		}
		
		esp_restart();
		return;
	}
	else
	{
		ESP_LOGI(MCP2515_DEVICE, "MCP2515 has successfully set the RXF0");
	}
	
	ret =  MCP2515_setFilterMask(MASK1, false, 0xff8);
	if (ret != ERROR_OK)
	{
		if (ret == ERROR_FAIL)
		{
			ESP_LOGE(MCP2515_DEVICE, "ERROR_FAIL after trying to set mask1, starting to restart controller...");
		}
		else if (ret == ERROR_ALLTXBUSY)
		{
			ESP_LOGE(MCP2515_DEVICE, "ERROR_ALLTXBUSY after trying to set mask1, starting to restart controller...");
		}
		else if (ret == ERROR_FAILINIT)
		{
			ESP_LOGE(MCP2515_DEVICE, "ERROR_FAILINIT after trying to set mask1, starting to restart controller...");
		}
		else if (ret == ERROR_FAILTX)
		{
			ESP_LOGE(MCP2515_DEVICE, "ERROR_FAILTX after trying to set mask1, starting to restart controller...");
		}
		else
		{
			ESP_LOGE(MCP2515_DEVICE, "ERROR_NOMSG after trying to set mask1, starting to restart controller...");
		}
		
		esp_restart();
		return;
	}
	else
	{
		ESP_LOGI(MCP2515_DEVICE, "MCP2515 has successfully set the mask1");
	}
	
	ret = MCP2515_setFilter(RXF2, false, 0x7e8);
	if (ret != ERROR_OK)
	{
		if (ret == ERROR_FAIL)
		{
			ESP_LOGE(MCP2515_DEVICE, "ERROR_FAIL after trying to set RXF2, starting to restart controller...");
		}
		else if (ret == ERROR_ALLTXBUSY)
		{
			ESP_LOGE(MCP2515_DEVICE, "ERROR_ALLTXBUSY after trying to set RXF2, starting to restart controller...");
		}
		else if (ret == ERROR_FAILINIT)
		{
			ESP_LOGE(MCP2515_DEVICE, "ERROR_FAILINIT after trying to set RXF2, starting to restart controller...");
		}
		else if (ret == ERROR_FAILTX)
		{
			ESP_LOGE(MCP2515_DEVICE, "ERROR_FAILTX after trying to set RXF2, starting to restart controller...");
		}
		else
		{
			ESP_LOGE(MCP2515_DEVICE, "ERROR_NOMSG after trying to set RXF2, starting to restart controller...");
		}
		
		esp_restart();
		return;
	}
	else
	{
		ESP_LOGI(MCP2515_DEVICE, "MCP2515 has successfully set the RXF2");
	}
	
	ESP_LOGI(MCP2515_DEVICE, "MCP2515 has successfully set the RXF0, RXF2, mask0 and mask1");
	
#endif

	MCP2515_setNormalMode();
	ESP_LOGI(MCP2515_DEVICE, "MCP2515 has successfully set Normal Operation mode");
	
	ESP_LOGI(MCP2515_DEVICE, "MCP2515 has successfully configured");
}

void print_can_frame(CAN_FRAME frame, RXBn_t RXBn) {
    if (!frame) return;
    
    const char* RXBn_name;

    if (RXBn == RXB0) {
        RXBn_name = "RXB0";
    } else if (RXBn == RXB1) {
        RXBn_name = "RXB1";
    } else {
        RXBn_name = "UNKNOWN";
    }

    int64_t timestamp_us = esp_timer_get_time();
    
    printf("[%s][Time: %lld.%03lld ms] ", RXBn_name, timestamp_us / 1000, timestamp_us % 1000);

    printf("CAN ID: 0x%08lX, DLC: %u, Data: ", frame->can_id, frame->can_dlc);

    for (int i = 0; i < frame->can_dlc; i++)
        printf("%02X ", frame->data[i]);
    
    for (int i = 0; i < 8 - frame->can_dlc; i++)
        printf("00 ");
    
    printf("\n");
}

void app_main(void)
{
	can_bus_init();
	
    while (true)
    {
		if (frame_is_ready)
		{
			frame_is_ready = false;
			
			uint8_t mcp2515_interrupts = MCP2515_getInterrupts();
			
			CAN_FRAME_t received_frame;
			ERROR_t ret;
			
			if (mcp2515_interrupts & CANINTF_RX0IF)
			{
				ret = MCP2515_readMessage(RXB0, &received_frame);
				
				if (ret == ERROR_FAIL)
				{
					ESP_LOGE(MCP2515_DEVICE, "ERROR_FAIL after trying to read RXB0 after INT");
				}
				else if (ret == ERROR_ALLTXBUSY)
				{
					ESP_LOGE(MCP2515_DEVICE, "ERROR_ALLTXBUSY after trying to read RXB0 after INT");
				}
				else if (ret == ERROR_FAILINIT)
				{
					ESP_LOGE(MCP2515_DEVICE, "ERROR_FAILINIT after trying to read RXB0 after INT");
				}
				else if (ret == ERROR_FAILTX)
				{
					ESP_LOGE(MCP2515_DEVICE, "ERROR_FAILTX after trying to read RXB0 after INT");
				}
				else if (ret == ERROR_NOMSG)
				{
					ESP_LOGE(MCP2515_DEVICE, "ERROR_NOMSG after trying to read RXB0 after INT");
				}
				else
				{
					print_can_frame(received_frame, RXB0);
				}
			}
			
			if (mcp2515_interrupts & CANINTF_RX1IF)
			{
				ret = MCP2515_readMessage(RXB1, &received_frame);
				
				if (ret == ERROR_FAIL)
				{
					ESP_LOGE(MCP2515_DEVICE, "ERROR_FAIL after trying to read RXB1 after INT");
				}
				else if (ret == ERROR_ALLTXBUSY)
				{
					ESP_LOGE(MCP2515_DEVICE, "ERROR_ALLTXBUSY after trying to read RXB1 after INT");
				}
				else if (ret == ERROR_FAILINIT)
				{
					ESP_LOGE(MCP2515_DEVICE, "ERROR_FAILINIT after trying to read RXB1 after INT");
				}
				else if (ret == ERROR_FAILTX)
				{
					ESP_LOGE(MCP2515_DEVICE, "ERROR_FAILTX after trying to read RXB1 after INT");
				}
				else if (ret == ERROR_NOMSG)
				{
					ESP_LOGE(MCP2515_DEVICE, "ERROR_NOMSG after trying to read RXB1 after INT");
				}
				else
				{
					print_can_frame(received_frame, RXB0);
				}
			}
		}
        
        vTaskDelay(pdMS_TO_TICKS(20));
    }
}
