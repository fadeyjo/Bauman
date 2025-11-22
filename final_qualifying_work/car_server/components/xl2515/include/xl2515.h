#include "driver/spi_master.h"
#include "hal/spi_types.h"
#include <stdint.h>

typedef struct {
	spi_host_device_t host;
	int sclk_io;
	int miso_io;
	int mosi_io;
	int int_io;
	int cs_io;
	spi_device_handle_t spi;
} xl2515_config_t;

typedef struct {
	uint32_t id;
	uint8_t dlc;
	uint8_t data[8];
	bool extended;
	bool rtr;
} xl2515_frame_t;

esp_err_t xl2515_init(xl2515_config_t *);
esp_err_t xl2515_deinit(xl2515_config_t *);
esp_err_t xl2515_set_bitrate(spi_device_handle_t, uint32_t);
esp_err_t xl2515_send_frame(spi_device_handle_t, const xl2515_frame_t *);
esp_err_t xl2515_receive_frame(spi_device_handle_t, xl2515_frame_t *, TickType_t);
esp_err_t xl2515_reset(spi_device_handle_t);
esp_err_t xl2515_read_register(spi_device_handle_t, uint8_t, uint8_t *);
esp_err_t xl2515_write_register(spi_device_handle_t, uint8_t, uint8_t);
esp_err_t xl2515_bit_modify(spi_device_handle_t, uint8_t, uint8_t, uint8_t);
