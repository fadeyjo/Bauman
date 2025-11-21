#include "driver/spi_master.h"
#include "driver/gpio.h"
#include "freertos/FreeRTOS.h"
#include "freertos/task.h"
#include "esp_log.h"
#include "hal/gpio_types.h"
#include "mcp2515.h"

#define CAN_BUS_MISO_PIN 19
#define CAN_BUS_MOSI_PIN 23
#define CAN_BUS_SCK_PIN 18
#define CAN_BUS_CS_PIN 5
#define CAN_INT_PIN 4        // INT MCP2515 → GPIO4 ESP32
#define CAN_BUS_SPI_HOST_DEVICE SPI3_HOST
#define CAN_BUS_DEVICE_CLOCK_SPEED 8000000
#define CAN_BUS_DEVICE_QUEUE_SIZE 3

static const char *TAG = "CAN_ISR";

CAN_FRAME_t can_frame_rx;
volatile bool frame_ready = false;

// Прерывание INT MCP2515
static void IRAM_ATTR can_isr_handler(void* arg)
{
    frame_ready = true;
}

// SPI и MCP2515 инициализация
void can_bus_spi_init(void)
{
    spi_bus_config_t buscfg = {
        .mosi_io_num = CAN_BUS_MOSI_PIN,
        .miso_io_num = CAN_BUS_MISO_PIN,
        .sclk_io_num = CAN_BUS_SCK_PIN,
        .quadwp_io_num = -1,
        .quadhd_io_num = -1,
    };

    spi_device_interface_config_t devcfg = {
        .clock_speed_hz = CAN_BUS_DEVICE_CLOCK_SPEED,
        .mode = 0,
        .spics_io_num = CAN_BUS_CS_PIN,
        .queue_size = CAN_BUS_DEVICE_QUEUE_SIZE
    };

    ESP_ERROR_CHECK(spi_bus_initialize(CAN_BUS_SPI_HOST_DEVICE, &buscfg, SPI_DMA_CH_AUTO));
    ESP_ERROR_CHECK(spi_bus_add_device(CAN_BUS_SPI_HOST_DEVICE, &devcfg, &MCP2515_Object->spi));
}

void can_bus_init(void)
{
	MCP2515_init();
    can_bus_spi_init();
    MCP2515_reset();
    MCP2515_setBitrate(CAN_500KBPS, MCP_8MHZ);
    MCP2515_setNormalMode();

    // Настройка INT пина
    gpio_config_t io_conf = {
        .intr_type = GPIO_INTR_NEGEDGE,    // MCP2515 INT активен низким уровнем
        .mode = GPIO_MODE_INPUT,
        .pin_bit_mask = (1ULL << CAN_INT_PIN),
        .pull_up_en = GPIO_PULLUP_ENABLE
    };
    gpio_config(&io_conf);
    gpio_install_isr_service(0);
    gpio_isr_handler_add(CAN_INT_PIN, can_isr_handler, NULL);

    ESP_LOGI(TAG, "CAN initialized and INT handler attached.");
}

void app_main(void)
{
    can_bus_init();

    ESP_LOGI(TAG, "Waiting for CAN frames...");
    while (1)
    {
        if (frame_ready)
        {
            frame_ready = false;

			ERROR_t ret = MCP2515_readMessageAfterStatCheck(&can_frame_rx);
			
            if (ret == ERROR_OK)
            {
                ESP_LOGI(TAG, "Received DLC=%d", can_frame_rx->can_dlc);
                printf("DATA: ");
                for (int i = 0; i < can_frame_rx->can_dlc; i++)
                    printf("%02X ", can_frame_rx->data[i]);
                printf("\n");
            }
            else
            {
				ESP_LOGE(TAG, "Error");
			}
        }

        vTaskDelay(pdMS_TO_TICKS(10)); // маленькая задержка для безопасности
    }
}
