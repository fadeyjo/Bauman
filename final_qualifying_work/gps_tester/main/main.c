#include <stdio.h>
#include <stdbool.h>
#include <unistd.h>
#include "driver/uart.h"
#include <string.h>

#define GPS_UART_NUM UART_NUM_2
#define GPS_TX_PIN 17
#define GPS_RX_PIN 16
#define GPS_BUF_SIZE 1024
#define GPS_BAUD_RATE 9600
#define GPS_TIME_FOR_WAIT_DATA_MS 100
#define GPS_TASK_DELAY_MS 10

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
        0,
        0,
        NULL,
        0
    );
}

void gps_task(void *arg)
{
    uint8_t data[GPS_BUF_SIZE];

    while (1) {
        int len = uart_read_bytes(
            GPS_UART_NUM,
            data,
            sizeof(data) - 1,
            pdMS_TO_TICKS(GPS_TIME_FOR_WAIT_DATA_MS)
        );

        if (len > 0) {
            data[len] = '\0';  // Завершаем строку

            // Проверяем наличие подстроки GLL
            if (strstr((char*)data, "GGA") != NULL) {
                printf("Получена строка GLL: %s", (char*)data);
            }
        }

        vTaskDelay(pdMS_TO_TICKS(GPS_TASK_DELAY_MS));
    }
}


void app_main(void)
{
    gps_init();

    xTaskCreate(
        gps_task,
        "gps_task",
        4096,
        NULL,
        10,
        NULL
    );
}
