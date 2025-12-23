#include <stdio.h>
#include <stdbool.h>
#include <unistd.h>
#include "driver/twai.h"
#include "esp_err.h"
#include "esp_system.h"
#include "freertos/projdefs.h"
#include "esp_log.h"
#include "xtensa/config/specreg.h"
#include <string.h>

#define TWAI_TX_GPIO GPIO_NUM_5
#define TWAI_RX_GPIO GPIO_NUM_4

#define USE_FILTRATION false
#define TWAI_FILTER 0x7e8
#define TWAI_MASK 0x7f8

#define CAN_RX_QUEUE_SIZE 64
#define CAN_TX_QUEUE_SIZE 64

#define TRIES_TO_REQUEST_SUPPORTED_PIDS 5
#define TIME_FOR_SUPPORTED_PIDS_REQUEST_MS 1000
#define STEP_FOR_TIME_FOR_SUPPORTED_PIDS_REQUEST_MS 10

#define CHECKED_PIDS 0b00011111111111111100000000000010

#define PERIOD_TO_SEND_PID_REQUEST 200

#define DELAY_FOR_TWAI_TRANSMIT_MS 10
#define DELAY_FOR_TWAI_RECEIVE_MS 0

/*
Список PID для чтения, которые я хочу знать, в CHECKED_PIDS
0x04 - нагрузка двигателя = A*100/255 %
0x05 - температура охлаждающей жидкости = A-40 C
0x06 - кратковременная топливная коррекция—Bank 1 = (A-128) * 100/128 %
0x07 - долговременная топливная коррекция—Bank 1 = (A-128) * 100/128 %
0x08 - кратковременная топливная коррекция—Bank 2 = (A-128) * 100/128 %
0x09 - долговременная топливная коррекция—Bank 1 = (A-128) * 100/128 %
0x0a - давление топлива = A*3 kPa
0x0b - давление во впускном коллекторе = A kPa
0x0c - RPM = ((A*256)+B)/4 rpm
0x0d - скорость = A km/h
0x0e - угол опережения зажигания = A/2-64 deg relative to #1 cylinder
0x0f - температура всасываемого воздуха = A-40 C
0x10 - массовый расход воздуха = ((A*256)+B)/100) grams/sec
0x11 - положение дроссельной заслонки = A*100/255 %
0x12 - статус вторичного воздуха, байт A:
	0 - такой системы нет
	1 — подача воздуха до катализатора (upstream)
	2 — после катализатора (downstream)
	4 — в атмосферу
	8 — помпа включена
0x1f - время, прошедшее с запуска двигателя
*/

static const char* TWAI_DEVICE = "TWAI";

static QueueHandle_t can_rx_queue = NULL;
static QueueHandle_t can_tx_queue = NULL;
static QueueHandle_t can_log_queue = NULL;

uint32_t supported_pids = 0;
volatile bool supported_pids_received = false;
uint32_t control_pids = 0;

uint8_t current_pid = 0x00;

void twai_init(void)
{
    twai_general_config_t g_config = TWAI_GENERAL_CONFIG_DEFAULT(
        TWAI_TX_GPIO,
        TWAI_RX_GPIO,
        TWAI_MODE_NORMAL
    );

    g_config.tx_queue_len = CAN_TX_QUEUE_SIZE;
    g_config.rx_queue_len = CAN_RX_QUEUE_SIZE;
    g_config.alerts_enabled = TWAI_ALERT_NONE;
    g_config.clkout_divider = 0;

	twai_timing_config_t t_config = TWAI_TIMING_CONFIG_500KBITS();

	twai_filter_config_t f_config;

	if (USE_FILTRATION)
	{
    	f_config.acceptance_code = (TWAI_FILTER << 21);
    	f_config.acceptance_mask = ~(TWAI_MASK << 21);
    	f_config.single_filter   = true;
	}
	else
	{
    	f_config = (twai_filter_config_t)TWAI_FILTER_CONFIG_ACCEPT_ALL();
	}
    
    ESP_ERROR_CHECK(twai_driver_install(&g_config, &t_config, &f_config));
    ESP_ERROR_CHECK(twai_start());
}

void print_can_frame(const twai_message_t *msg)
{
    printf(
        "CAN RX | ID: 0x%03X | DLC: %d | DATA:",
        (unsigned int)msg->identifier,
        msg->data_length_code
    );

    for (int i = 0; i < msg->data_length_code; i++)
        printf(" %02X", msg->data[i]);

    printf("\n");
}

void can_logger_task(void *arg)
{
    twai_message_t msg;

    while (1)
        if (xQueueReceive(can_log_queue, &msg, portMAX_DELAY))
            print_can_frame(&msg);
}

void can_rx_task(void *arg)
{
    twai_message_t msg;

    while (1)
        if (twai_receive(&msg, portMAX_DELAY) == ESP_OK)
	    {
			xQueueSend(can_rx_queue, &msg, DELAY_FOR_TWAI_RECEIVE_MS);
	        xQueueSend(can_log_queue, &msg, 0);
		}
}

void can_tx_task(void *arg)
{
    twai_message_t msg;

    while (1)
        if (xQueueReceive(can_tx_queue, &msg, portMAX_DELAY))
		{
			ESP_ERROR_CHECK(twai_transmit(&msg, pdMS_TO_TICKS(DELAY_FOR_TWAI_TRANSMIT_MS)));
			print_can_frame(&msg);
		}
}

void get_supported_pids(void)
{
	twai_message_t msg = {
        .identifier = 0x7df,
        .data_length_code = 8,
        .flags = TWAI_MSG_FLAG_NONE,
        .data = { 0x02, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 }
    };
    
   	uint8_t try = 0;
    
    while (!supported_pids_received && try++ < TRIES_TO_REQUEST_SUPPORTED_PIDS)
    {
		uint16_t delay_sum = 0;
		
		xQueueSend(can_tx_queue, &msg, 0);
		
		while (!supported_pids_received && delay_sum < TIME_FOR_SUPPORTED_PIDS_REQUEST_MS)
		{
			delay_sum += STEP_FOR_TIME_FOR_SUPPORTED_PIDS_REQUEST_MS;
			vTaskDelay(pdMS_TO_TICKS(STEP_FOR_TIME_FOR_SUPPORTED_PIDS_REQUEST_MS));
		}
	}
	
	if (!supported_pids)
	{
		ESP_LOGE(TWAI_DEVICE, "CAN do not respond");
		esp_restart();
	}
}

void set_current_pid_to_request(void)
{
    if (!control_pids)
		esp_restart();
    
    uint8_t start_bit =
    	current_pid == 0x20 ?
    	0x00 :
    	current_pid;
    
    while (1)
    {
		if (start_bit == 0x20)
			start_bit = 0x00;
		
		if ((control_pids << start_bit) & 0x80000000)
		{
			current_pid = start_bit + 0x01;
			return;
		}
		
		start_bit++;
	}
}

void poll_can_task(void *arg)
{
	twai_message_t msg = {
        .identifier = 0x7df,
        .data_length_code = 8,
        .flags = TWAI_MSG_FLAG_NONE
    };
    
    uint8_t data[8] = {0x02, 0x01, 0, 0, 0, 0, 0, 0};
    
    while(1)
    {
		set_current_pid_to_request();
		
		data[2] = current_pid;
		
		memcpy(msg.data, data, 8);
		
		xQueueSend(can_tx_queue, &msg, 0);
		
		vTaskDelay(pdMS_TO_TICKS(PERIOD_TO_SEND_PID_REQUEST));
	}
}

bool is_response(twai_message_t msg)
{
	return
		msg.identifier >= 0x7e8 &&
		msg.identifier <= 0x7ef &&
		msg.data_length_code == 8 &&
		msg.data[0] > 0x02 &&
		msg.data[1] == 0x41;
}

void can_processing_task(void *arg)
{
    twai_message_t msg;

    while (1)
        if (xQueueReceive(can_rx_queue, &msg, portMAX_DELAY))
        {
			if (!is_response(msg))
				continue;
			
            if (!supported_pids_received)
            {
				if (msg.data[2] != 0x00)
					continue;
				
				supported_pids_received = true;
				
				supported_pids =
					(msg.data[3] << 24) |
                    (msg.data[4] << 16) |
					(msg.data[5] << 8) |
					msg.data[6];
				
				control_pids = supported_pids & CHECKED_PIDS;
				 
				if (!control_pids)
				{
					ESP_LOGE(TWAI_DEVICE, "Control PIDs are 0");
					esp_restart();
				}
				
				xTaskCreate(poll_can_task, "poll_can", 4096, NULL, 10, NULL);
				
				continue;
			}
			
			// обработка телеметрии
        }
}

void app_main(void)
{
	can_rx_queue = xQueueCreate(
        CAN_RX_QUEUE_SIZE,
        sizeof(twai_message_t)
    );
    assert(can_rx_queue != NULL);
    
    can_tx_queue = xQueueCreate(
        CAN_TX_QUEUE_SIZE,
        sizeof(twai_message_t)
    );
    assert(can_tx_queue != NULL);
    
    can_log_queue = xQueueCreate(
    	CAN_RX_QUEUE_SIZE,
    	sizeof(twai_message_t)
	);
	assert(can_log_queue != NULL);
	
    twai_init();
	
	xTaskCreate(can_processing_task, "can_proc", 4096, NULL, 10, NULL);
	xTaskCreate(can_logger_task, "can_logger", 4096, NULL, 5, NULL);
	xTaskCreate(can_rx_task, "can_rx", 4096, NULL, 20, NULL);
	xTaskCreate(can_tx_task, "can_tx", 4096, NULL, 12, NULL);
	
	get_supported_pids();
}
