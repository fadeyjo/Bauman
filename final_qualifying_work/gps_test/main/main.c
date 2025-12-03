#include <stdio.h>
#include <stdbool.h>
#include <unistd.h>
#include "esp_system.h"
#include "esp_timer.h"
#include "freertos/projdefs.h"
#include "hal/gpio_types.h"
#include "lwip/err.h"
#include "driver/spi_master.h"
#include "driver/gpio.h"
#include "esp_log.h"
#include "portmacro.h"
#include "driver/uart.h"

#define GPS_UART_NUM UART_NUM_2
#define GPS_TX_PIN 17
#define GPS_RX_PIN 16
#define GPS_BUF_SIZE 1024
#define GPS_BAUD_RATE 9600

TaskHandle_t gpsDataTaskHandle = NULL;

void ubx_crc(uint8_t *payload, uint16_t length, uint8_t *ck_a, uint8_t *ck_b)
{
    *ck_a = 0;
    *ck_b = 0;
    for (int i = 0; i < length; i++)
    {
        *ck_a += payload[i];
        *ck_b += *ck_a;
    }
}

void send_ubx_packet(uint8_t cls, uint8_t id, uint8_t *payload, uint16_t length) {
    uint8_t packet[8 + length]; // 2 header + 2 class/id + 2 length + payload + 2 CRC
    packet[0] = 0xB5;
    packet[1] = 0x62;
    packet[2] = cls;
    packet[3] = id;
    packet[4] = length & 0xFF;
    packet[5] = (length >> 8) & 0xFF;

    memcpy(&packet[6], payload, length);

    uint8_t ck_a, ck_b;
    ubx_crc(&packet[2], 2 + 2 + length, &ck_a, &ck_b); // Class + ID + Length + Payload
    packet[6 + length] = ck_a;
    packet[7 + length] = ck_b;

    uart_write_bytes(GPS_UART_NUM, (const char *)packet, 8 + length);
}

void gps_init(void)
{
    uart_config_t uart_config = {
        .baud_rate = GPS_BAUD_RATE,
        .data_bits = UART_DATA_8_BITS,
        .parity    = UART_PARITY_DISABLE,
        .stop_bits = UART_STOP_BITS_1,
        .flow_ctrl = UART_HW_FLOWCTRL_DISABLE
    };

    uart_param_config(GPS_UART_NUM, &uart_config);
    uart_set_pin(GPS_UART_NUM, GPS_TX_PIN, GPS_RX_PIN, UART_PIN_NO_CHANGE, UART_PIN_NO_CHANGE);
    uart_driver_install(
        GPS_UART_NUM,
        GPS_BUF_SIZE * 2,
        GPS_BUF_SIZE * 2,
        0,
        NULL,
        0
    );

	uint8_t cfg_msg_payload[] = {
    	0xF0, 0x00, 0x00, 0x00, // GLL off
    	0xF0, 0x01, 0x00, 0x00, // GSA off
    	0xF0, 0x02, 0x00, 0x00, // GSV off
    	0xF0, 0x03, 0x00, 0x00, // RMC off
    	0xF0, 0x04, 0x00, 0x00, // VTG off
    	0xF0, 0x00, 0x01, 0x00  // GGA on (rate = 1)
	};

    send_ubx_packet(0x06, 0x01, cfg_msg_payload, sizeof(cfg_msg_payload));
    vTaskDelay(pdMS_TO_TICKS(50));
    
    uint8_t cfg_rate_payload[] = {
    	0xC8, 0x00, // measurement rate = 200 ms (5 Гц)
    	0x01, 0x00, // navRate = 1
    	0x01, 0x00  // timeRef = GPS
	};

	send_ubx_packet(0x06, 0x08, cfg_rate_payload, sizeof(cfg_rate_payload));
	vTaskDelay(pdMS_TO_TICKS(50));
}

void gps_task(void *arg)
{
    uint8_t* data = (uint8_t*) malloc(GPS_BUF_SIZE);
    while (1)
    {
        int len = uart_read_bytes(GPS_UART_NUM, data, GPS_BUF_SIZE - 1, pdMS_TO_TICKS(100));
        if (len > 0)
        {
            data[len] = '\0';
            
            printf("-----------------------------------------------------------------\n");
            ESP_LOGI("GPS", "RX: %s", (char*)data);
        }
        else
        {
            ESP_LOGI("GPS", "Нет данных");
        }
    }
    
    free(data);
}

void app_main(void)
{
	gps_init();
	
	xTaskCreate(
		gps_task,
    	"GPS Data Task",
    	4096,
    	NULL,
    	7,
		&gpsDataTaskHandle
	);
}
