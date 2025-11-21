#include "can.h"
#include "driver/gpio.h"
#include "driver/spi_common.h"
#include "esp_err.h"
#include <stdio.h>
#include <stdbool.h>
#include <unistd.h>
#include "driver/spi_master.h"
#include "esp_log.h"
#include "freertos/projdefs.h"
#include "hal/gpio_types.h"
#include "mcp2515.h"

#define CAN_BUS_MISO_PIN 19
#define CAN_BUS_MOSI_PIN 23
#define CAN_BUS_SCK_PIN 18
#define CAN_BUS_CS_PIN 5
#define CAN_BUS_INT_PIN 4
#define CAN_BUS_MAX_TRANSFER_SZ 0
#define CAN_BUS_DEVICE_CLOCK_SPEED 8000000
#define CAN_BUS_DEVICE_QUEUE_SIZE 3
#define CAN_BUS_SPI_HOST_DEVICE SPI3_HOST

char *CAN_BUS_TAG = "CAN Bus";

volatile bool frame_ready;

static void IRAM_ATTR can_isr_handler(void* arg)
{
    frame_ready = true;
}

void can_bus_spi_init(void)
{
	ESP_LOGE(CAN_BUS_TAG, "Start initializing of SPI interface...");
	
	spi_bus_config_t can_bus_spi_bus_config =
	{
		.mosi_io_num = CAN_BUS_MOSI_PIN,
		.miso_io_num = CAN_BUS_MISO_PIN,
		.max_transfer_sz = CAN_BUS_MAX_TRANSFER_SZ,
		.quadhd_io_num = -1,
		.quadwp_io_num = -1,
		.sclk_io_num = CAN_BUS_SCK_PIN
	};
	
	esp_err_t ret = spi_bus_initialize(CAN_BUS_SPI_HOST_DEVICE, &can_bus_spi_bus_config, SPI_DMA_CH_AUTO);
	ESP_ERROR_CHECK(ret);
	
	spi_device_interface_config_t can_bus_spi_device_interface =
	{
		.clock_speed_hz = CAN_BUS_DEVICE_CLOCK_SPEED,
		.queue_size = CAN_BUS_DEVICE_QUEUE_SIZE,
		.mode = 0,
		.spics_io_num = CAN_BUS_CS_PIN
	};
	
	ret = spi_bus_add_device(CAN_BUS_SPI_HOST_DEVICE, &can_bus_spi_device_interface, &MCP2515_Object->spi);
	ESP_ERROR_CHECK(ret);
	
	ESP_LOGE(CAN_BUS_TAG, "Successfully initializing of SPI interface");
}

void can_bus_init(void)
{
	ESP_LOGE(CAN_BUS_TAG, "Start initializing of CAN Bus...");
	
	MCP2515_init();
	can_bus_spi_init();
	MCP2515_reset();
	MCP2515_setBitrate(CAN_500KBPS, MCP_8MHZ);
	MCP2515_setNormalMode();
	
	ESP_LOGE(CAN_BUS_TAG, "Successfully initializing of CAN Bus");
	
	gpio_config_t int_pin_gpio_config =
	{
		.mode = GPIO_MODE_INPUT,
		.intr_type = GPIO_INTR_NEGEDGE,
		.pin_bit_mask = (1ULL << CAN_BUS_INT_PIN),
		.pull_up_en = GPIO_PULLUP_ENABLE
	};
	
	gpio_config(&int_pin_gpio_config);
	gpio_install_isr_service(0);
	gpio_isr_handler_add(CAN_BUS_INT_PIN, can_isr_handler, NULL);
}

void app_main(void)
{
    can_bus_init();

    while(1) {
        
    }
}

