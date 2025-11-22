#include "xl2515.h"
#include "driver/spi_master.h"
#include "esp_err.h"
#include <stdint.h>
#include <string.h>

#define RESET_INSTRUCTION 0xC0
#define READ_INSTRUCTION 0x03
#define WRITE_INSTRUCTION 0x02
#define READ_STATUS_INSTRUCTION 0xA0
#define RX_STATUS_INSTRUCTION 0xB0
#define BIT_MODIFY_INSTRUCTION 0x05

static esp_err_t spi_transfer(spi_device_handle_t spi, const uint8_t *tx, uint8_t *rx, size_t len)
{
	if (!spi) return ESP_ERR_INVALID_ARG;
	
	spi_transaction_t t;
	memset(&t, 0, sizeof(t));
	t.length = len * 8;
	t.tx_buffer = tx;
	t.rx_buffer = rx;
	return spi_device_transmit(spi, &t);
}

esp_err_t xl2515_init(xl2515_config_t *cfg)
{
	
}

esp_err_t xl2515_deinit(xl2515_config_t *cfg)
{
	
}

esp_err_t xl2515_set_bitrate(spi_device_handle_t spi, uint32_t bitrate)
{
	
}

esp_err_t xl2515_send_frame(spi_device_handle_t spi, const xl2515_frame_t *frame)
{
	
}

esp_err_t xl2515_receive_frame(spi_device_handle_t spi, xl2515_frame_t *frame, TickType_t ticks_to_wait)
{
	
}

esp_err_t xl2515_reset(spi_device_handle_t spi)
{
	uint8_t cmd = RESET_INSTRUCTION;
	return spi_transfer(spi, &cmd, NULL, 1)
}

esp_err_t xl2515_read_register(spi_device_handle_t spi, uint8_t addr, uint8_t *data)
{
	uint8_t cmd = READ_INSTRUCTION;
	esp_err_t ret = spi_transfer(spi, &cmd, data, 1)
	
}

esp_err_t xl2515_write_register(spi_device_handle_t spi, uint8_t addr, uint8_t data)
{
	
}

esp_err_t xl2515_bit_modify(spi_device_handle_t spi, uint8_t addr, uint8_t mask, uint8_t data)
{
	
}
