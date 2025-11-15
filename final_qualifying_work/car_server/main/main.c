#include <stdio.h>
#include <stdbool.h>
#include <unistd.h>
#include "driver/spi_common.h"
#include "esp_err.h"
#include "freertos/FreeRTOS.h"
#include "freertos/projdefs.h"
#include "freertos/task.h"
#include "driver/spi_master.h"
#include "hal/spi_types.h"
#include "esp_log.h"
#include "mcp2515.h"
#include "can.h"

// Пины для CAN Bus
#define CAN_BUS_SPI_SCK_PIN 18
#define CAN_BUS_SPI_SS_PIN 5
#define CAN_BUS_SPI_MOSI_PIN 23
#define CAN_BUS_SPI_MISO_PIN 19
#define CAN_BUS_SPI_INT_PIN  4

// Настройки шины SPI для CAN Bus
#define CAN_BUS_HOST_DEVICE SPI3_HOST
#define CAN_BUS_MAX_TRANSFER_SZ 64

// Настройки устройства CAN Bus
#define CAN_BUS_CLOCK_SPEED_HZ 8000000
#define CAN_BUS_MODE 0
#define CAN_BUS_QUEUE_SIZE 3

// Теги логов
static const char *CAN_BUS_TAG = "CAN_BUS";
static const char *MCP2515_BUS_TAG = "MCP2515";

spi_device_handle_t can_bus_spi_handle;

void SPI_Init(void)
{
	ESP_LOGI(CAN_BUS_TAG, "Starting initializing of CAN Bus...");
	
	spi_bus_config_t can_bus_spi_config =
	{
		.miso_io_num = CAN_BUS_SPI_MISO_PIN,
		.mosi_io_num = CAN_BUS_SPI_MOSI_PIN,
		.sclk_io_num = CAN_BUS_SPI_SCK_PIN,
		.quadwp_io_num = -1,
        .quadhd_io_num = -1,
        .max_transfer_sz = CAN_BUS_MAX_TRANSFER_SZ
	};
	
	esp_err_t ret = spi_bus_initialize(CAN_BUS_HOST_DEVICE, &can_bus_spi_config, SPI_DMA_CH_AUTO);
	ESP_ERROR_CHECK(ret);
	
	spi_device_interface_config_t can_bus_device_interface_config =
	{
		.clock_speed_hz = CAN_BUS_CLOCK_SPEED_HZ,
		.mode = CAN_BUS_MODE,
		.spics_io_num = CAN_BUS_SPI_SS_PIN,
		.queue_size = CAN_BUS_QUEUE_SIZE,
	};
	
	ret = spi_bus_add_device(CAN_BUS_HOST_DEVICE, &can_bus_device_interface_config, &can_bus_spi_handle);
	ESP_ERROR_CHECK(ret);
	
	ESP_LOGI(CAN_BUS_TAG, "CAN Bus was successfully initialized");
}

void init_can_bus(void)
{
	ESP_LOGI(MCP2515_BUS_TAG, "Starting initializing of MCP2515...");
	MCP2515_init();
	ESP_LOGI(MCP2515_BUS_TAG, "MCP2515 was successfully initialized");
	
	SPI_Init();
	
	ESP_LOGI(MCP2515_BUS_TAG, "Starting reseting of MCP2515...");
	MCP2515_reset();
	ESP_LOGI(MCP2515_BUS_TAG, "MCP2515 was successfully reset");
	
	MCP2515_setBitrate(CAN_1000KBPS, MCP_8MHZ);
	MCP2515_setNormalMode();
}

void app_main(void)
{
	init_can_bus();
	
	CAN_FRAME_t can_frame_rx[1];
	
	can_frame_rx[0]->can_id = (0x12344321) | CAN_EFF_FLAG;
	can_frame_rx[0]->can_dlc = 8;
	can_frame_rx[0]->data[0] = 0x01;
	can_frame_rx[0]->data[1] = 0x02;
	can_frame_rx[0]->data[2] = 0x03;
	can_frame_rx[0]->data[3] = 0x04;
	can_frame_rx[0]->data[4] = 0x05;
	can_frame_rx[0]->data[5] = 0x06;
	can_frame_rx[0]->data[6] = 0x07;
	can_frame_rx[0]->data[7] = 0x08;
	
	while (true)
	{
		ESP_ERROR_CHECK(
			MCP2515_sendMessageAfterCtrlCheck(can_frame_rx[0])
		);
		
		vTaskDelay(1000);
	}
}
