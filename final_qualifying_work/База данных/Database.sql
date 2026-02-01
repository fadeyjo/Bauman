-- Справочники

create table access_rights
(
    right_level tinyint unsigned primary key,
    right_description text not null,

    check (char_length(right_description) > 0)
);

insert into access_rights (right_level, right_description) values
    (0, 'Пользователь'),
    (1, 'Оператор'),
    (2, 'Администратор');

create table OBDII_services
(
    service_id tinyint unsigned primary key,
    service_description text not null,

    check (char_length(service_description) > 0)
);

insert into OBDII_services value (1, 'Текущие параметры систем управления');

create table OBDII_PIDs
(
    OBDII_PID_id mediumint unsigned auto_increment primary key,
    service_id tinyint unsigned not null,
    PID smallint unsigned not null,
    PID_description text not null,

    foreign key (service_id) references OBDII_services (service_id) on delete cascade,

    constraint unique_service_id_PID unique (service_id, PID),

    check (char_length(PID_description) > 0)
);

insert into OBDII_PIDs (service_id, PID, PID_description) values
    (1, 0, 'Список поддерживаемых PID’ов (0-20)'), (1, 1, 'Состояние после устранения кодов неисправностей'),
    (1, 2, 'Обнаруженные диагностические коды ошибок'), (1, 3, 'Состояние топливной системы'),
    (1, 4, 'Расчетное значение нагрузки на двигатель'), (1, 5, 'Температура охлаждающей жидкости'),
    (1, 6, 'Кратковременная топливная коррекция—Bank 1'), (1, 7, 'Долговременная топливная коррекция—Bank 1'),
    (1, 8, 'Кратковременная топливная коррекция—Bank 2'), (1, 9, 'Долговременная топливная коррекция—Bank 2'),
    (1, 10, 'Давление топлива'), (1, 11, 'Давление во впускном коллекторе (абсолютное)'),
    (1, 12, 'Обороты двигателя'), (1, 13, 'Скорость автомобиля'),
    (1, 14, 'Угол опережения зажигания'), (1, 15, 'Температура всасываемого воздуха'),
    (1, 16, 'Массовый расход воздуха'), (1, 17, 'Положение дроссельной заслонки'),
    (1, 18, 'Запрограммированный режим подачи вторичного воздуха'), (1, 19, 'Наличие датчиков кислорода'),
    (1, 20, 'Bank 1, Sensor 1: напряжение датчика кислорода, кратковременный запас топлива'),
    (1, 21, 'Bank 1, Sensor 2: напряжение датчика кислорода, кратковременный запас топлива'),
    (1, 22, 'Bank 1, Sensor 3: напряжение датчика кислорода, кратковременный запас топлива'),
    (1, 23, 'Bank 1, Sensor 4: напряжение датчика кислорода, кратковременный запас топлива'),
    (1, 24, 'Bank 2, Sensor 1: напряжение датчика кислорода, кратковременный запас топлива'),
    (1, 25, 'Bank 2, Sensor 2: напряжение датчика кислорода, кратковременный запас топлива'),
    (1, 26, 'Bank 2, Sensor 3: напряжение датчика кислорода, кратковременный запас топлива'),
    (1, 27, 'Bank 2, Sensor 4: напряжение датчика кислорода, кратковременный запас топлива'),
    (1, 28, 'Соответствие этого автомобиля стандартам OBD'), (1, 29, 'Наличие датчиков кислорода'),
    (1, 30, 'Состояние вспомогательного входного сигнала'), (1, 31, 'Время, прошедшее с запуска двигателя');

create table car_bodies
(
    body_id tinyint unsigned auto_increment primary key,
    body_name varchar(30) not null unique,

    check (char_length(body_name) > 0)
);

insert into car_bodies (body_name) values
    ('Седан'), ('Купе'),
    ('Хэтчбек'), ('Лифтбек'),
    ('Фастбек'), ('Универсал'),
    ('Кроссовер'), ('Внедорожник'),
    ('Пикап'), ('Кабриолет'),
    ('Лимузин');
    
create table car_gearboxes
(
    gearbox_id tinyint unsigned auto_increment primary key,
    gearbox_name varchar(30) not null unique,
    
    check (char_length(gearbox_name) > 0)
);

insert into car_gearboxes (gearbox_name) values
    ('МКПП'), ('АКПП'),
    ('РКПП'), ('Вариатор (CVT)');

create table fuel_types
(
    type_id tinyint unsigned auto_increment primary key,
    type_name varchar(30) not null unique,
    
    check (char_length(type_name) > 0)
);

insert into fuel_types (type_name) values
    ('АИ-92'), ('АИ-95'),
    ('АИ-98'), ('АИ-100'),
    ('ДТ');

create table car_drives
(
    drive_id tinyint unsigned auto_increment primary key,
    drive_name varchar(30) not null unique,
    check (char_length(drive_name) > 0)
);

insert into car_drives (drive_name) values
    ('Передний'), ('Задний'), ('Полный');

create table engine_types
(
    type_id tinyint unsigned not null auto_increment primary key,
    type_name varchar(30) not null unique,
    check (char_length(type_name) > 0)
);

insert into engine_types (type_name) values
    ('ДВС (бензиновый)'), ('ДВС (дизельный)');

-- Рабочие таблицы

create table OBDII_devices
(
    device_id mediumint unsigned auto_increment primary key,
    MAC_address char(17) not null unique,

    check (lower(MAC_address) regexp '^([0-9a-f]{2}[:]){5}[0-9a-f]{2}$')
);

create table persons
(
    person_id mediumint unsigned auto_increment primary key,
    email varchar(320) not null unique,
    phone varchar(12) not null unique,
    last_name varchar(50) not null,
    first_name varchar(50) not null,
    patronymic varchar(50),
    birth date not null,
    hashed_password varchar(500) not null,
    drive_lisense char(10) unique,
    right_level tinyint unsigned not null default 0,

    foreign key (right_level) references access_rights (right_level) on delete cascade,

    check (email regexp '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$'),
    check (phone regexp '\\+7[0-9]{10}'),
    check (char_length(last_name) > 1),
    check (char_length(first_name) > 1),
    check (patronymic is null or char_length(patronymic) > 1),
    check (drive_lisense is null or drive_lisense regexp '^[0-9]{10}$'),
    check (char_length(hashed_password) > 1)
);

create table car_brands
(
    brand_id smallint unsigned auto_increment primary key,
    brand_name varchar(30) not null unique,
    
    check (char_length(brand_name) > 0)
);

create table car_brands_models
(
    car_brand_model_id mediumint unsigned auto_increment primary key,
    model_name varchar(30) not null,
    brand_id smallint unsigned not null,

    foreign key (brand_id) references car_brands (brand_id) on delete cascade,
    
    check (char_length(model_name) > 0),
    
    constraint unique_model_name_brand_id unique (model_name, brand_id)
);

create table engine_configurations
(
    engine_config_id mediumint unsigned auto_increment primary key,
    engine_power_hp smallint unsigned not null,
    engine_power_kW decimal(5,1) not null,
    engine_type_id tinyint unsigned not null,
    engine_capacity_l decimal(3, 1) not null,
    tank_capacity_l tinyint unsigned not null,
    fuel_type_id tinyint unsigned not null,
    
    foreign key (engine_type_id) references engine_types (type_id) on delete cascade,
    foreign key (fuel_type_id) references fuel_types (type_id) on delete cascade,

    check (engine_power_hp > 0),
    check (engine_power_kW > 0),
    check (engine_capacity_l > 0),
    check (tank_capacity_l > 0),

    constraint unique_engine_configurations unique
    (
        engine_power_hp,
        engine_power_kW,
        engine_type_id,
        engine_capacity_l,
        tank_capacity_l,
        fuel_type_id
    )
);

create table car_configurations
(
    car_config_id mediumint unsigned auto_increment primary key,
    car_brand_model_id mediumint unsigned not null,
    body_id tinyint unsigned not null,
    release_year year not null,
    gearbox_id tinyint unsigned not null,
    drive_id tinyint unsigned not null,
    engine_conf_id mediumint unsigned not null,
    vehicle_weight_kg smallint unsigned not null,

    check (vehicle_weight_kg > 0),
    
    foreign key (car_brand_model_id) references car_brands_models (car_brand_model_id) on delete cascade,
    foreign key (body_id) references car_bodies (body_id) on delete cascade,
    foreign key (gearbox_id) references car_gearboxes (gearbox_id) on delete cascade,
    foreign key (drive_id) references car_drives (drive_id) on delete cascade,
    foreign key (engine_conf_id) references engine_configurations (engine_config_id) on delete cascade,

    constraint unique_car_configurations unique
    (
        car_brand_model_id,
        body_id,
        release_year,
        gearbox_id,
        drive_id,
        engine_conf_id,
        vehicle_weight_kg
    )
);

create table cars
(
    car_id int unsigned auto_increment primary key,
    person_id mediumint unsigned not null,
    VIN_number char(17) unique not null,
    state_number varchar(6) unique,
    car_config_id mediumint unsigned not null,

    foreign key (person_id) references persons (person_id) on delete cascade,
    foreign key (car_config_id) references car_configurations (car_config_id) on delete cascade,

    check (state_number is null or lower(state_number) regexp '^[авекмнорстух][0-9]{3}[авекмнорстух]{2}[0-9]{2,3}$')
);

create table trips
(
    trip_id bigint unsigned auto_increment primary key,
    start_datetime datetime not null,
    device_id mediumint unsigned not null,
    car_id int unsigned not null,
    end_datetime datetime,

    foreign key (device_id) references OBDII_devices (device_id) on delete cascade,
    foreign key (car_id) references cars (car_id) on delete cascade,

    check (end_datetime is null or end_datetime >= start_datetime)
);

create table telemetry_data
(
    rec_id bigint unsigned auto_increment primary key,
    rec_datetime datetime not null,
    OBDII_PID_id mediumint unsigned not null,
    ECU_id varbinary(3) not null,
    response_dlc tinyint unsigned not null,
    response varbinary(8),
    trip_id bigint unsigned not null,

    foreign key (OBDII_PID_id) references OBDII_PIDs (OBDII_PID_id) on delete cascade,
    foreign key (trip_id) references trips (trip_id) on delete cascade,

    check (response_dlc between 0 and 8)
);

create table GPS_data
(
    rec_id bigint unsigned auto_increment primary key,
    rec_datetime datetime not null,
    trip_id bigint unsigned not null,
    latitude_deg decimal(8, 6) not null,
    longitude_deg decimal(9, 6) not null,
    accuracy_m decimal(8, 3),
    speed_kmh mediumint,
    bearing_deg smallint unsigned,

    foreign key (trip_id) references trips (trip_id) on delete cascade,

    check (latitude_deg between -90 and 90),
    check ((longitude_deg > -180) and (longitude_deg <= 180)),
    check (bearing_deg is null or ((bearing_deg >= 0) and (bearing_deg < 360)))
);

-- Начальные данные

insert into car_brands (brand_name) values
('Toyota');

insert into car_brands_models (brand_id, model_name) values
(1, 'Camry');

insert into persons
(
    email,
    phone,
    last_name,
    first_name,
    patronymic,
    birth,
    hashed_password,
    drive_lisense,
    right_level
) value (
    'komrad.gubi2017@yandex.ru',
    '+79229060764',
    'Губин',
    'Егор',
    'Вячеславович',
    '2025-08-02',
    '123',
    '9930376835',
    2
);
