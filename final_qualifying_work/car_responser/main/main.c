#include <stdio.h>
#include "freertos/FreeRTOS.h"
#include "freertos/task.h"
#include "driver/spi_master.h"
#include "driver/gpio.h"
#include "mcp2515.h"
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
#define MCP2515_ONE_SHOT_MODE true

static const char* MCP2515_SPI = "MCP2515 SPI";
static const char* MCP2515_DEVICE = "MCP2515";

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

void can_bus_init(void)
{
	ESP_LOGI(MCP2515_DEVICE, "Starting configuration of MCP2515...");
	
	MCP2515_init();
	ESP_LOGI(MCP2515_DEVICE, "MCP2515 object successfully initialized");
	
	can_bus_spi_init();
    ESP_LOGI(MCP2515_DEVICE, "MCP2515 SPI successfully initialized");
    
    ESP_LOGI(MCP2515_DEVICE, "MCP2515 SPI INTR successfully initialized");
	
	MCP2515_reset();
	ESP_LOGI(MCP2515_DEVICE, "MCP2515 successfully reseted");
	
	MCP2515_setBitrate(MCP2515_CAN_SPEED, MCP2515_CAN_CLOCK);
	ESP_LOGI(MCP2515_DEVICE, "MCP2515 has successfully set the bitrate");

	MCP2515_setOneShotMode(MCP2515_ONE_SHOT_MODE);
	MCP2515_setNormalMode();
	
	ESP_LOGI(MCP2515_DEVICE, "MCP2515 has successfully set Normal Operation mode");
	
	ESP_LOGI(MCP2515_DEVICE, "MCP2515 has successfully configured");
}

void send_speed_response(uint8_t speed_km_h)
{
    CAN_FRAME_t frame;
    frame->can_id = 0x7EC;
    frame->can_dlc = 3;

	frame->data[0] = 0x3;
	frame->data[1] = 0x41;
    frame->data[2] = 0x0D;
    frame->data[3] = speed_km_h;

    for (int i = 4; i < 8; i++)
		frame->data[i] = 0x00;
	
	ERROR_t ret = MCP2515_sendMessageAfterCtrlCheck(&frame);
	
    if(ret == ERROR_OK)
    {
        ESP_LOGI(MCP2515_DEVICE, "OK sending speed frame!");
    }
    else if (ret == ERROR_FAIL)
    {
		ESP_LOGE(MCP2515_DEVICE, "ERROR_FAIL sending speed frame!");
	}
	else if (ret == ERROR_ALLTXBUSY)
    {
		ESP_LOGE(MCP2515_DEVICE, "ERROR_ALLTXBUSY sending speed frame!");
	}
	else if (ret == ERROR_FAILINIT)
    {
		ESP_LOGE(MCP2515_DEVICE, "ERROR_FAILINIT sending speed frame!");
	}
	else if (ret == ERROR_FAILTX)
    {
		ESP_LOGE(MCP2515_DEVICE, "ERROR_FAILTX sending speed frame!");
	}
	else
    {
		ESP_LOGE(MCP2515_DEVICE, "ERROR_NOMSG sending speed frame!");
	}
}

void can_tx_task(void *arg)
{
    CAN_FRAME_t frame;
    
    int i = 0;

    while (1)
    {
		if (i == 2147483647)
			i = 0;
		
		if (i++ % 2 == 0)
		{
			send_speed_response(90);
		}
		else
		{
			send_speed_response(1);
		}
        
        vTaskDelay(pdMS_TO_TICKS(15));
    }
}


void app_main(void)
{
    can_bus_init();
    
    xTaskCreatePinnedToCore(can_tx_task, "can_tx", 4096, NULL, 10, NULL, 0);
}
