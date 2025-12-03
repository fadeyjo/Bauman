-- Необходимо хранить
-- Адрес контроллера, с которым идёт взаимодействие
-- GPS данные
-- Сессии
-- Телеметрия во время сессий

create table controllers
(
    controller_id int unsigned auto_increment primary key,
    esp32_mac_address bigint unsigned not null unique
);

create table data_sessions
(
    session_id int unsigned auto_increment primary key,
    starting_datetime datetime not null,
    controller_id int unsigned not null,
    ending_datetime datetime,
    foreign key (controller_id) references controllers (controller_id) on delete cascade
);

-- https://wiki.iarduino.ru/page/NMEA-0183/
-- получаю GGA и GLL
create table geo_data
(
    geo_data_id int unsigned auto_increment primary key,
    geo_datetime datetime not null,
    latitude_direction varchar(1) not null, -- широта
    latitude decimal(9,6) not null,
    longitude_direction varchar(1) not null, -- долгота
    longitude decimal(9,6) not null,
    data_is_reliable boolean not null,
    session_id int unsigned not null,
    foreign key (session_id) references data_sessions (session_id) on delete cascade
);

create table PID_services
(
    service_id tinyint unsigned auto_increment primary key,
    service_description text not null
);

create table units_of_measurement
(
    uom_id tinyint unsigned auto_increment primary key,
    uom varchar(15) not null
);

create table PID_commands
(
    command_id int unsigned auto_increment primary key,
    service_id tinyint unsigned not null,
    PID smallint unsigned not null,
    uom_id tinyint unsigned not null,
    PID_description text not null,
    foreign key (service_id) references PID_services (service_id) on delete cascade,
    foreign key (uom_id) references units_of_measurement (uom_id) on delete cascade
);

create table PID_responses
(
    response_id int unsigned auto_increment primary key,
    request_command_id int unsigned not null,
    response_datetime datetime not null,
    response_data smallint not null,
    session_id int unsigned not null,
    foreign key (request_command_id) references PID_commands (command_id) on delete cascade,
    foreign key (session_id) references data_sessions (session_id) on delete cascade
);
