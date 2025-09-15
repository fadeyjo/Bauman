set global transaction isolation level serializable;

-- Для описания статусов изделия, тестов и тд и тп
create table statuses (
    status_id int unsigned auto_increment primary key,
    status_name varchar(21) not null unique
);

-- Для описания отношений сущностей оператор-место-права
create table seats (
    seat_number int unsigned auto_increment primary key,
    hashed_password varchar(255),
    min_time int unsigned,
    seat_name text
);

create table seats_chain (
    seats_chain_id int unsigned auto_increment primary key,
    seat_number int unsigned not null,
    previous_seat_number int unsigned,
    one_type_seats boolean default false, -- флаг ставится у РМ, у которых два предыдущих одинаковых места (КФ, 12 и 13)
    foreign key (seat_number) references seats (seat_number) on delete cascade,
    foreign key (previous_seat_number) references seats (seat_number) on delete cascade
);

create table persons (
    card_number int unsigned primary key,
    surname_name varchar(60) not null,
    author_card_number int unsigned,
    record_ok boolean
);

create table access_rights (
    rights int unsigned primary key,
    access_rights_description text
);

create table seats_persons (
    seats_persons_id int unsigned auto_increment primary key,
    card_number int unsigned not null,
    seat_number int unsigned not null,
    access_rights int unsigned not null,
    author_card_number int unsigned,
    card_number_redact int unsigned,
    record_ok boolean,
    foreign key (card_number) references persons (card_number) on delete cascade,
    foreign key (seat_number) references seats (seat_number) on delete cascade,
    foreign key (access_rights) references access_rights (rights) on delete cascade,
    foreign key (author_card_number) references persons (card_number) on delete cascade,
    foreign key (card_number_redact) references persons (card_number) on delete cascade
);

-- Здесь только вариации исполнения тахографа
create table part_numbers (
    part_number_id int unsigned auto_increment primary key,
    part_number varchar(14) not null unique
);

-- Сменные планы
create table plans (
    plan_id int unsigned auto_increment primary key,
    plan_date date not null,
    plan_time time not null,
    part_number_id int unsigned,
    amount int unsigned,
    is_prioritize boolean,
    foreign key (part_number_id) references part_numbers (part_number_id) on delete cascade
);

create table seats_remains(
    seats_remains_id int unsigned auto_increment primary key,
    plan_id int unsigned,
    remain int unsigned not null,
    seat_number int unsigned not null,
    foreign key (plan_id) references plans (plan_id) on delete cascade,
    foreign key (seat_number) references seats (seat_number) on delete cascade
);

-- Здесь просто названия элементов, напримр, 'Лицевая панель'
create table component_names (
    component_name_id int unsigned auto_increment primary key,
    component_name varchar(50) not null unique
);

-- Здесь название компонента с его номенклатурой
create table components (
    component_id int unsigned auto_increment primary key,
    component_name_id int unsigned not null,
    component_number varchar(50) not null unique,
    amount int unsigned default 0,
    QR_code_info varchar(13) not null,
    comment_to_redact text,
    foreign key (component_name_id) references component_names (component_name_id) on delete cascade
);

-- Связть типа комонента и места
create table components_seats (
    components_seats_id int unsigned auto_increment primary key,
    component_id int unsigned not null,
    seat_number int unsigned not null,
    max_amount int unsigned not null,
    foreign key (component_id) references components (component_id) on delete cascade,
    foreign key (seat_number) references seats (seat_number) on delete cascade
);

-- Здесь связь конкретного элемента с номенклотурой и вариации, в которой он используется
create table components_variations (
    components_variations_id int unsigned auto_increment primary key,
    part_number_id int unsigned not null,
    component_id int unsigned not null,
    foreign key (part_number_id) references part_numbers (part_number_id) on delete cascade,
    foreign key (component_id) references components (component_id) on delete cascade
);

create table defectivity_types (
    defectivity_type_id int unsigned auto_increment primary key,
    defectivity_type varchar(12) not null unique
);

create table defectivity (
    defectivity_id int unsigned auto_increment primary key,
    defectivity_date date not null,
    defectivity_time time not null,
    defectivity_type_id int unsigned not null,
    defectivity_component_id int unsigned not null,
    amount int unsigned not null,
    scanned_info_from_box_to_scan text,
    seat_number int unsigned not null, -- РМ, на котором заметили брак
    tf_id int unsigned,-- id в таблице, на котором заметили брак, NULL если поставщик
    seat_number_source int unsigned not null, -- РМ с которого брак, может быть и педыдущее
    record_ok boolean,
    foreign key (defectivity_component_id) references components (component_id) on delete cascade,
    foreign key (defectivity_type_id) references defectivity_types (defectivity_type_id) on delete cascade,
    foreign key (seat_number) references seats (seat_number) on delete cascade
);

create table tf_1 (
    tf_1_id int unsigned auto_increment primary key,
    operator_card_number int unsigned not null,
    scan_date date not null,
    scan_time time not null,

    first_scan_date date,
    first_scan_time time,
    first_scanned_info text,

    second_scan_date date,
    second_scan_time time,
    second_scanned_info text,

    end_scan_date date,
    end_scan_time time,
    end_scanned_info text,

    plan_id int unsigned,

    is_success boolean,
    foreign key (operator_card_number) references persons (card_number) on delete cascade,
    foreign key (plan_id) references plans (plan_id) on delete cascade
);

create table tf_1_trays (
    tf_1_tray_id int unsigned auto_increment primary key,
    tray_id int unsigned not null,
    is_dopog boolean not null,
    current_amount int unsigned not null default 0,
    max_amount int unsigned not null
);

create table tf_2 (
    tf_2_id int unsigned auto_increment primary key,
    operator_card_number int unsigned not null,
    scan_date date not null,
    scan_time time not null,
    LCD_scanned_info text,
    LCD_scanned_date date,
    LCD_scanned_time time,
    input_tray text,
    input_tray_date date,
    input_tray_time time,
    plan_id int unsigned,
    is_success boolean,
    foreign key (operator_card_number) references persons (card_number) on delete cascade,
    foreign key (plan_id) references plans (plan_id) on delete cascade
);

create table tf_2_archive (
    tf_2_id int unsigned auto_increment primary key,
    operator_card_number int unsigned not null,
    scan_date date not null,
    scan_time time not null,
    LCD_scanned_info text,
    LCD_scanned_date date,
    LCD_scanned_time time,
    input_tray text,
    input_tray_date date,
    input_tray_time time,
    plan_id int unsigned,
    is_success boolean,
    foreign key (operator_card_number) references persons (card_number) on delete cascade,
    foreign key (plan_id) references plans (plan_id) on delete cascade
);

create table tf_3 (
    tf_3_id int unsigned auto_increment primary key,
    operator_card_number int unsigned not null,
    scan_date date not null,
    scan_time time not null,
    LCD_scanned_info text,
    LCD_scanned_date date,
    LCD_scanned_time time,
    plan_id int unsigned,
    is_success boolean,
    foreign key (operator_card_number) references persons (card_number) on delete cascade,
    foreign key (plan_id) references plans (plan_id) on delete cascade
);

create table tf_3_archive (
    tf_3_id int unsigned auto_increment primary key,
    operator_card_number int unsigned not null,
    scan_date date not null,
    scan_time time not null,
    LCD_scanned_info text,
    LCD_scanned_date date,
    LCD_scanned_time time,
    plan_id int unsigned,
    is_success boolean,
    foreign key (operator_card_number) references persons (card_number) on delete cascade,
    foreign key (plan_id) references plans (plan_id) on delete cascade
);

create table LCD_from_repair (
    LCD_from_repair_id int unsigned auto_increment primary key,
    LCD_scanned_info text not null,
    repair_date date not null,
    repair_time date not null,
    tf_1_id int unsigned default null,
    tf_2_id int unsigned default null,
    tf_3_id int unsigned default null,
    tf_4_id int unsigned default null,
    tf_5_id int unsigned default null,
    tf_6_id int unsigned default null,
    tf_7_id int unsigned default null,
    tf_8_id int unsigned default null,
    tf_9_id int unsigned default null,
    tf_10_id int unsigned default null,
    tf_11_id int unsigned default null,
    tf_12_13_id int unsigned default null,
    tf_14_id int unsigned default null,
    tf_15_16_id int unsigned default null,
    tf_17_id int unsigned default null,
    tf_18_id int unsigned default null,
    tf_19_id int unsigned default null,
    tf_20_id int unsigned default null,
    foreign key (tf_1_id) references tf_1 (tf_1_id) on delete cascade,
    foreign key (tf_2_id) references tf_2 (tf_2_id) on delete cascade,
    foreign key (tf_3_id) references tf_3 (tf_3_id) on delete cascade,
    foreign key (tf_4_id) references tf_4 (tf_4_id) on delete cascade,
    foreign key (tf_5_id) references tf_5 (tf_5_id) on delete cascade,
    foreign key (tf_6_id) references tf_6 (tf_6_id) on delete cascade,
    foreign key (tf_7_id) references tf_7 (tf_7_id) on delete cascade,
    foreign key (tf_8_id) references tf_8 (tf_8_id) on delete cascade,
    foreign key (tf_9_id) references tf_9 (tf_9_id) on delete cascade,
    foreign key (tf_10_id) references tf_10 (tf_10_id) on delete cascade,
    foreign key (tf_11_id) references tf_11 (tf_11_id) on delete cascade,
    foreign key (tf_12_13_id) references tf_12_13 (tf_12_13_id) on delete cascade,
    foreign key (tf_14_id) references tf_14 (tf_14_id) on delete cascade,
    foreign key (tf_15_16_id) references tf_15_16 (tf_15_16_id) on delete cascade,
    foreign key (tf_17_id) references tf_17 (tf_17_id) on delete cascade,
    foreign key (tf_18_id) references tf_18 (tf_18_id) on delete cascade,
    foreign key (tf_19_id) references tf_19 (tf_19_id) on delete cascade,
    foreign key (tf_20_id) references tf_20 (tf_20_id) on delete cascade
);

create table main_board_from_repair (
    main_board_from_repair_id int unsigned auto_increment primary key,
    main_board_scanned_info text not null,
    repair_date date not null,
    repair_time date not null,
    tf_1_id int unsigned default null,
    tf_2_id int unsigned default null,
    tf_3_id int unsigned default null,
    tf_4_id int unsigned default null,
    tf_5_id int unsigned default null,
    tf_6_id int unsigned default null,
    tf_7_id int unsigned default null,
    tf_8_id int unsigned default null,
    tf_9_id int unsigned default null,
    tf_10_id int unsigned default null,
    tf_11_id int unsigned default null,
    tf_12_13_id int unsigned default null,
    tf_14_id int unsigned default null,
    tf_15_16_id int unsigned default null,
    tf_17_id int unsigned default null,
    tf_18_id int unsigned default null,
    tf_19_id int unsigned default null,
    tf_20_id int unsigned default null,
    foreign key (tf_1_id) references tf_1 (tf_1_id) on delete cascade,
    foreign key (tf_2_id) references tf_2 (tf_2_id) on delete cascade,
    foreign key (tf_3_id) references tf_3 (tf_3_id) on delete cascade,
    foreign key (tf_4_id) references tf_4 (tf_4_id) on delete cascade,
    foreign key (tf_5_id) references tf_5 (tf_5_id) on delete cascade,
    foreign key (tf_6_id) references tf_6 (tf_6_id) on delete cascade,
    foreign key (tf_7_id) references tf_7 (tf_7_id) on delete cascade,
    foreign key (tf_8_id) references tf_8 (tf_8_id) on delete cascade,
    foreign key (tf_9_id) references tf_9 (tf_9_id) on delete cascade,
    foreign key (tf_10_id) references tf_10 (tf_10_id) on delete cascade,
    foreign key (tf_11_id) references tf_11 (tf_11_id) on delete cascade,
    foreign key (tf_12_13_id) references tf_12_13 (tf_12_13_id) on delete cascade,
    foreign key (tf_14_id) references tf_14 (tf_14_id) on delete cascade,
    foreign key (tf_15_16_id) references tf_15_16 (tf_15_16_id) on delete cascade,
    foreign key (tf_17_id) references tf_17 (tf_17_id) on delete cascade,
    foreign key (tf_18_id) references tf_18 (tf_18_id) on delete cascade,
    foreign key (tf_19_id) references tf_19 (tf_19_id) on delete cascade,
    foreign key (tf_20_id) references tf_20 (tf_20_id) on delete cascade
);

create table serial_number_from_repair (
    serial_number_from_repair_id int unsigned auto_increment primary key,
    serial_number int unsigned not null,
    repair_date date not null,
    repair_time date not null,
    tf_1_id int unsigned default null,
    tf_2_id int unsigned default null,
    tf_3_id int unsigned default null,
    tf_4_id int unsigned default null,
    tf_5_id int unsigned default null,
    tf_6_id int unsigned default null,
    tf_7_id int unsigned default null,
    tf_8_id int unsigned default null,
    tf_9_id int unsigned default null,
    tf_10_id int unsigned default null,
    tf_11_id int unsigned default null,
    tf_12_13_id int unsigned default null,
    tf_14_id int unsigned default null,
    tf_15_16_id int unsigned default null,
    tf_17_id int unsigned default null,
    tf_18_id int unsigned default null,
    tf_19_id int unsigned default null,
    tf_20_id int unsigned default null,
    foreign key (tf_1_id) references tf_1 (tf_1_id) on delete cascade,
    foreign key (tf_2_id) references tf_2 (tf_2_id) on delete cascade,
    foreign key (tf_3_id) references tf_3 (tf_3_id) on delete cascade,
    foreign key (tf_4_id) references tf_4 (tf_4_id) on delete cascade,
    foreign key (tf_5_id) references tf_5 (tf_5_id) on delete cascade,
    foreign key (tf_6_id) references tf_6 (tf_6_id) on delete cascade,
    foreign key (tf_7_id) references tf_7 (tf_7_id) on delete cascade,
    foreign key (tf_8_id) references tf_8 (tf_8_id) on delete cascade,
    foreign key (tf_9_id) references tf_9 (tf_9_id) on delete cascade,
    foreign key (tf_10_id) references tf_10 (tf_10_id) on delete cascade,
    foreign key (tf_11_id) references tf_11 (tf_11_id) on delete cascade,
    foreign key (tf_12_13_id) references tf_12_13 (tf_12_13_id) on delete cascade,
    foreign key (tf_14_id) references tf_14 (tf_14_id) on delete cascade,
    foreign key (tf_15_16_id) references tf_15_16 (tf_15_16_id) on delete cascade,
    foreign key (tf_17_id) references tf_17 (tf_17_id) on delete cascade,
    foreign key (tf_18_id) references tf_18 (tf_18_id) on delete cascade,
    foreign key (tf_19_id) references tf_19 (tf_19_id) on delete cascade,
    foreign key (tf_20_id) references tf_20 (tf_20_id) on delete cascade
);

create table BT_board_from_repair (
    BT_board_from_repair_id int unsigned auto_increment primary key,
    BT_board_scanned_info int unsigned not null,
    repair_date date not null,
    repair_time date not null,
    tf_1_id int unsigned default null,
    tf_2_id int unsigned default null,
    tf_3_id int unsigned default null,
    tf_4_id int unsigned default null,
    tf_5_id int unsigned default null,
    tf_6_id int unsigned default null,
    tf_7_id int unsigned default null,
    tf_8_id int unsigned default null,
    tf_9_id int unsigned default null,
    tf_10_id int unsigned default null,
    tf_11_id int unsigned default null,
    tf_12_13_id int unsigned default null,
    tf_14_id int unsigned default null,
    tf_15_16_id int unsigned default null,
    tf_17_id int unsigned default null,
    tf_18_id int unsigned default null,
    tf_19_id int unsigned default null,
    tf_20_id int unsigned default null,
    foreign key (tf_1_id) references tf_1 (tf_1_id) on delete cascade,
    foreign key (tf_2_id) references tf_2 (tf_2_id) on delete cascade,
    foreign key (tf_3_id) references tf_3 (tf_3_id) on delete cascade,
    foreign key (tf_4_id) references tf_4 (tf_4_id) on delete cascade,
    foreign key (tf_5_id) references tf_5 (tf_5_id) on delete cascade,
    foreign key (tf_6_id) references tf_6 (tf_6_id) on delete cascade,
    foreign key (tf_7_id) references tf_7 (tf_7_id) on delete cascade,
    foreign key (tf_8_id) references tf_8 (tf_8_id) on delete cascade,
    foreign key (tf_9_id) references tf_9 (tf_9_id) on delete cascade,
    foreign key (tf_10_id) references tf_10 (tf_10_id) on delete cascade,
    foreign key (tf_11_id) references tf_11 (tf_11_id) on delete cascade,
    foreign key (tf_12_13_id) references tf_12_13 (tf_12_13_id) on delete cascade,
    foreign key (tf_14_id) references tf_14 (tf_14_id) on delete cascade,
    foreign key (tf_15_16_id) references tf_15_16 (tf_15_16_id) on delete cascade,
    foreign key (tf_17_id) references tf_17 (tf_17_id) on delete cascade,
    foreign key (tf_18_id) references tf_18 (tf_18_id) on delete cascade,
    foreign key (tf_19_id) references tf_19 (tf_19_id) on delete cascade,
    foreign key (tf_20_id) references tf_20 (tf_20_id) on delete cascade
);

create table transport_monitoring_board_from_repair (
    transport_monitoring_board_from_repair_id int unsigned auto_increment primary key,
    transport_monitoring_board_scanned_info int unsigned not null,
    repair_date date not null,
    repair_time date not null,
    tf_1_id int unsigned default null,
    tf_2_id int unsigned default null,
    tf_3_id int unsigned default null,
    tf_4_id int unsigned default null,
    tf_5_id int unsigned default null,
    tf_6_id int unsigned default null,
    tf_7_id int unsigned default null,
    tf_8_id int unsigned default null,
    tf_9_id int unsigned default null,
    tf_10_id int unsigned default null,
    tf_11_id int unsigned default null,
    tf_12_13_id int unsigned default null,
    tf_14_id int unsigned default null,
    tf_15_16_id int unsigned default null,
    tf_17_id int unsigned default null,
    tf_18_id int unsigned default null,
    tf_19_id int unsigned default null,
    tf_20_id int unsigned default null,
    foreign key (tf_1_id) references tf_1 (tf_1_id) on delete cascade,
    foreign key (tf_2_id) references tf_2 (tf_2_id) on delete cascade,
    foreign key (tf_3_id) references tf_3 (tf_3_id) on delete cascade,
    foreign key (tf_4_id) references tf_4 (tf_4_id) on delete cascade,
    foreign key (tf_5_id) references tf_5 (tf_5_id) on delete cascade,
    foreign key (tf_6_id) references tf_6 (tf_6_id) on delete cascade,
    foreign key (tf_7_id) references tf_7 (tf_7_id) on delete cascade,
    foreign key (tf_8_id) references tf_8 (tf_8_id) on delete cascade,
    foreign key (tf_9_id) references tf_9 (tf_9_id) on delete cascade,
    foreign key (tf_10_id) references tf_10 (tf_10_id) on delete cascade,
    foreign key (tf_11_id) references tf_11 (tf_11_id) on delete cascade,
    foreign key (tf_12_13_id) references tf_12_13 (tf_12_13_id) on delete cascade,
    foreign key (tf_14_id) references tf_14 (tf_14_id) on delete cascade,
    foreign key (tf_15_16_id) references tf_15_16 (tf_15_16_id) on delete cascade,
    foreign key (tf_17_id) references tf_17 (tf_17_id) on delete cascade,
    foreign key (tf_18_id) references tf_18 (tf_18_id) on delete cascade,
    foreign key (tf_19_id) references tf_19 (tf_19_id) on delete cascade,
    foreign key (tf_20_id) references tf_20 (tf_20_id) on delete cascade
);

create table SKZI_board_from_repair (
    SKZI_board_from_repair_id int unsigned auto_increment primary key,
    SKZI_board_scanned_info int unsigned not null,
    repair_date date not null,
    repair_time date not null,
    tf_1_id int unsigned default null,
    tf_2_id int unsigned default null,
    tf_3_id int unsigned default null,
    tf_4_id int unsigned default null,
    tf_5_id int unsigned default null,
    tf_6_id int unsigned default null,
    tf_7_id int unsigned default null,
    tf_8_id int unsigned default null,
    tf_9_id int unsigned default null,
    tf_10_id int unsigned default null,
    tf_11_id int unsigned default null,
    tf_12_13_id int unsigned default null,
    tf_14_id int unsigned default null,
    tf_15_16_id int unsigned default null,
    tf_17_id int unsigned default null,
    tf_18_id int unsigned default null,
    tf_19_id int unsigned default null,
    tf_20_id int unsigned default null,
    foreign key (tf_1_id) references tf_1 (tf_1_id) on delete cascade,
    foreign key (tf_2_id) references tf_2 (tf_2_id) on delete cascade,
    foreign key (tf_3_id) references tf_3 (tf_3_id) on delete cascade,
    foreign key (tf_4_id) references tf_4 (tf_4_id) on delete cascade,
    foreign key (tf_5_id) references tf_5 (tf_5_id) on delete cascade,
    foreign key (tf_6_id) references tf_6 (tf_6_id) on delete cascade,
    foreign key (tf_7_id) references tf_7 (tf_7_id) on delete cascade,
    foreign key (tf_8_id) references tf_8 (tf_8_id) on delete cascade,
    foreign key (tf_9_id) references tf_9 (tf_9_id) on delete cascade,
    foreign key (tf_10_id) references tf_10 (tf_10_id) on delete cascade,
    foreign key (tf_11_id) references tf_11 (tf_11_id) on delete cascade,
    foreign key (tf_12_13_id) references tf_12_13 (tf_12_13_id) on delete cascade,
    foreign key (tf_14_id) references tf_14 (tf_14_id) on delete cascade,
    foreign key (tf_15_16_id) references tf_15_16 (tf_15_16_id) on delete cascade,
    foreign key (tf_17_id) references tf_17 (tf_17_id) on delete cascade,
    foreign key (tf_18_id) references tf_18 (tf_18_id) on delete cascade,
    foreign key (tf_19_id) references tf_19 (tf_19_id) on delete cascade,
    foreign key (tf_20_id) references tf_20 (tf_20_id) on delete cascade
);

create table tf_1_2_3 (
    tf_1_2_3_id int unsigned auto_increment primary key,
    tf_1_id int unsigned,
    tf_2_id int unsigned,
    tf_3_id int unsigned,
    repair_from_seat int unsigned
    foreign key (tf_1_id) references tf_1 (tf_1_id) on delete cascade,
    foreign key (tf_2_id) references tf_2 (tf_2_id) on delete cascade,
    foreign key (tf_3_id) references tf_3 (tf_3_id) on delete cascade,
    foreign key (repair_from_seat) references seats (seat_number) on delete cascade
);

-- Необходимо для записи файла на рабочем месте загрузчика изменить my.ini: свойство secure-file-priv=""
create table bin_files (
    bin_file_version varchar(4) primary key,
    bin_file_data MEDIUMBLOB not null,
    bin_file_data_hash varchar(64) unique,
    is_current boolean default false
);

-- План 
create table tf_4 (
    tf_4_id int unsigned auto_increment primary key,
    operator_card_number int unsigned not null,
    loading_date date not null,
    loading_time time not null,
    MCU_id varchar(24) unique,
    board_type varchar(24),
    board_date_of_manufacture date,
    board_number int unsigned,
    part_number_id int unsigned,
    bin_file_version varchar(4),
    plan_id int unsigned,
    loading_status_id int unsigned, -- 1, 2
    foreign key (operator_card_number) references persons (card_number) on delete cascade,
    foreign key (loading_status_id) references statuses (status_id) on delete cascade,
    foreign key (part_number_id) references part_numbers (part_number_id) on delete cascade,
    foreign key (plan_id) references plans (plan_id) on delete cascade
);

create table tf_4_archive (
    tf_4_id int unsigned auto_increment primary key,
    operator_card_number int unsigned not null,
    loading_date date not null,
    loading_time time not null,
    MCU_id varchar(24),
    board_type varchar(24),
    board_date_of_manufacture date,
    board_number int unsigned,
    part_number_id int unsigned,
    bin_file_version varchar(4),
    plan_id int unsigned,
    loading_status_id int unsigned, -- 1, 2
    foreign key (operator_card_number) references persons (card_number) on delete cascade,
    foreign key (loading_status_id) references statuses (status_id) on delete cascade,
    foreign key (part_number_id) references part_numbers (part_number_id) on delete cascade,
    foreign key (plan_id) references plans (plan_id) on delete cascade
);

create table tf_5 (
    tf_5_id int unsigned auto_increment primary key,
    operator_card_number int unsigned not null,
    scan_date date not null,
    scan_time time not null,
    main_board_scanned_info text,
    main_board_scanned_date date,
    main_board_scanned_time time,
    plan_id int unsigned,
    is_success boolean,
    foreign key (operator_card_number) references persons (card_number) on delete cascade,
    foreign key (plan_id) references plans (plan_id) on delete cascade
);

create table tf_5_archive (
    tf_5_id int unsigned auto_increment primary key,
    operator_card_number int unsigned not null,
    scan_date date not null,
    scan_time time not null,
    main_board_scanned_info text,
    main_board_scanned_date date,
    main_board_scanned_time time,
    plan_id int unsigned,
    is_success boolean,
    foreign key (operator_card_number) references persons (card_number) on delete cascade,
    foreign key (plan_id) references plans (plan_id) on delete cascade
);

create table tf_6 (
    tf_6_id int unsigned auto_increment primary key,
    operator_card_number int unsigned not null,
    scan_date date not null,
    scan_time time not null,
    
    main_board_scanned_date date,
    main_board_scanned_time time,
    main_board_scanned_info text,

    LCD_scanned_date date,
    LCD_scanned_time time,
    LCD_scanned_info text,
    
    plan_id int unsigned,

    is_success boolean,
    foreign key (operator_card_number) references persons (card_number) on delete cascade,
    foreign key (plan_id) references plans (plan_id) on delete cascade
);

create table tf_6_archive (
    tf_6_id int unsigned auto_increment primary key,
    operator_card_number int unsigned not null,
    scan_date date not null,
    scan_time time not null,
    
    main_board_scanned_date date,
    main_board_scanned_time time,
    main_board_scanned_info text,

    LCD_scanned_date date,
    LCD_scanned_time time,
    LCD_scanned_info text,

    plan_id int unsigned,

    is_success boolean,
    foreign key (operator_card_number) references persons (card_number) on delete cascade,
    foreign key (plan_id) references plans (plan_id) on delete cascade
);

create table tf_7 (
    tf_7_id int unsigned auto_increment primary key,
    operator_card_number int unsigned not null,
    scan_date date not null,
    scan_time time not null,
    main_board_scanned_info text,
    main_board_scanned_date date,
    main_board_scanned_time time,
    plan_id int unsigned,
    is_success boolean,
    foreign key (operator_card_number) references persons (card_number) on delete cascade,
    foreign key (plan_id) references plans (plan_id) on delete cascade
);

create table tf_7_archive (
    tf_7_id int unsigned auto_increment primary key,
    operator_card_number int unsigned not null,
    scan_date date not null,
    scan_time time not null,
    main_board_scanned_info text,
    main_board_scanned_date date,
    main_board_scanned_time time,
    plan_id int unsigned,
    is_success boolean,
    foreign key (operator_card_number) references persons (card_number) on delete cascade,
    foreign key (plan_id) references plans (plan_id) on delete cascade
);

create table AT_32_SW_files (
    AT_32_SW_version varchar(4) primary key,
    bin_file_data MEDIUMBLOB not null,
    bin_file_data_hash varchar(64) unique not null,
    is_current boolean default false
);

create table ESP_32_SW_files (
    ESP_32_SW_version varchar(4) primary key,
    bin_file_data MEDIUMBLOB not null,
    bin_file_data_hash varchar(64) unique not null,
    file_type int unsigned not null
);

create table ESP_32_files_groups (
    ESP_32_files_group_version varchar(4) primary key,
    bin_1_id varchar(4) not null,
    bin_2_id varchar(4) not null,
    bin_3_id varchar(4) not null,
    bin_4_id varchar(4) not null,
    bin_5_id varchar(4) not null,
    bin_6_id varchar(4) not null,
    foreign key (bin_1_id) references ESP_32_SW_files (ESP_32_SW_version) on delete cascade,
    foreign key (bin_2_id) references ESP_32_SW_files (ESP_32_SW_version) on delete cascade,
    foreign key (bin_3_id) references ESP_32_SW_files (ESP_32_SW_version) on delete cascade,
    foreign key (bin_4_id) references ESP_32_SW_files (ESP_32_SW_version) on delete cascade,
    foreign key (bin_5_id) references ESP_32_SW_files (ESP_32_SW_version) on delete cascade,
    foreign key (bin_6_id) references ESP_32_SW_files (ESP_32_SW_version) on delete cascade
);

create table tf_8 (
    tf_8_id int unsigned auto_increment primary key,
    operator_card_number int unsigned not null,
    scan_date date not null,
    scan_time time not null,
    transport_monitoring_board_scanned_info varchar(100) unique,
    modem_scanned_info varchar(100) unique,
    AT_32_MCU_id varchar(24),
    AT_32_SW_version varchar(4),
    AT_32_is_success boolean,
    ESP_32_MAC_address varchar(17),
    ESP_32_files_group_version varchar(4),
    ESP_32_is_success boolean,
    plan_id int unsigned,
    foreign key (operator_card_number) references persons (card_number) on delete cascade,
    foreign key (plan_id) references plans (plan_id) on delete cascade
);

create table tf_8_archive (
    tf_8_id int unsigned auto_increment primary key,
    operator_card_number int unsigned not null,
    scan_date date not null,
    scan_time time not null,
    transport_monitoring_board_scanned_info text,
    modem_scanned_info text,
    AT_32_MCU_id varchar(24),
    AT_32_SW_version varchar(4),
    AT_32_is_success boolean,
    ESP_32_MAC_address varchar(17),
    ESP_32_files_group_version int unsigned,
    ESP_32_is_success boolean,
    plan_id int unsigned,
    foreign key (operator_card_number) references persons (card_number) on delete cascade,
    foreign key (plan_id) references plans (plan_id) on delete cascade,
    foreign key (AT_32_SW_version) references AT_32_SW_files (AT_32_SW_version) on delete cascade
);

create table tf_9 (
    tf_9_id int unsigned auto_increment primary key,
    operator_card_number int unsigned not null,
    scan_date date not null,
    scan_time time not null,
    transport_monitoring_board_scanned_info text,
    transport_monitoring_board_scanned_date date,
    transport_monitoring_board_scanned_time time,
    BT_board_scanned_info text,
    BT_board_scanned_date date,
    BT_board_scanned_time time,
    plan_id int unsigned,
    is_success boolean,
    foreign key (operator_card_number) references persons (card_number) on delete cascade,
    foreign key (plan_id) references plans (plan_id) on delete cascade
);

create table tf_9_archive (
    tf_9_id int unsigned auto_increment primary key,
    operator_card_number int unsigned not null,
    scan_date date not null,
    scan_time time not null,
    transport_monitoring_board_scanned_info text,
    transport_monitoring_board_scanned_date date,
    transport_monitoring_board_scanned_time time,
    BT_board_scanned_info text,
    BT_board_scanned_date date,
    BT_board_scanned_time time,
    plan_id int unsigned,
    is_success boolean,
    foreign key (operator_card_number) references persons (card_number) on delete cascade,
    foreign key (plan_id) references plans (plan_id) on delete cascade
);

create table tf_8_9 (
    tf_8_9_id int unsigned auto_increment primary key,
    tf_8_id int unsigned,
    tf_9_id int unsigned,
    repair_from_seat int unsigned,
    foreign key (tf_8_id) references tf_8 (tf_8_id) on delete cascade,
    foreign key (tf_9_id) references tf_9 (tf_9_id) on delete cascade,
    foreign key (repair_from_seat) references seats (seat_number) on delete cascade
);

create table tf_10 (
    tf_10_id int unsigned auto_increment primary key,
    operator_card_number int unsigned not null,
    scan_date date not null,
    scan_time time not null,
    main_board_scanned_info text,
    main_board_scanned_date date,
    main_board_scanned_time time,
    plan_id int unsigned,
    is_success boolean,
    foreign key (operator_card_number) references persons (card_number) on delete cascade,
    foreign key (plan_id) references plans (plan_id) on delete cascade
);

create table tf_10_archive (
    tf_10_id int unsigned auto_increment primary key,
    operator_card_number int unsigned not null,
    scan_date date not null,
    scan_time time not null,
    main_board_scanned_info text,
    main_board_scanned_date date,
    main_board_scanned_time time,
    plan_id int unsigned,
    is_success boolean,
    foreign key (operator_card_number) references persons (card_number) on delete cascade,
    foreign key (plan_id) references plans (plan_id) on delete cascade
);

create table tf_11 (
    tf_11_id int unsigned auto_increment primary key,
    operator_card_number int unsigned not null,
    scan_date date not null,
    scan_time time not null,
    main_board_scanned_info text,
    main_board_scanned_date date,
    main_board_scanned_time time,
    BT_board_scanned_info text,
    BT_board_scanned_date date,
    BT_board_scanned_time time,
    transport_monitoring_board_scanned_info text,
    transport_monitoring_board_scanned_date date,
    transport_monitoring_board_scanned_time time,
    SKZI_board_scanned_info text,
    SKZI_board_scanned_date date,
    SKZI_board_scanned_time time,
    plan_id int unsigned,
    is_success boolean,
    foreign key (operator_card_number) references persons (card_number) on delete cascade,
    foreign key (plan_id) references plans (plan_id) on delete cascade
);

create table tf_11_archive (
    tf_11_id int unsigned auto_increment primary key,
    operator_card_number int unsigned not null,
    scan_date date not null,
    scan_time time not null,
    main_board_scanned_info text,
    main_board_scanned_date date,
    main_board_scanned_time time,
    BT_board_scanned_info text,
    BT_board_scanned_date date,
    BT_board_scanned_time time,
    transport_monitoring_board_scanned_info text,
    transport_monitoring_board_scanned_date date,
    transport_monitoring_board_scanned_time time,
    SKZI_board_scanned_info text,
    SKZI_board_scanned_date date,
    SKZI_board_scanned_time time,
    plan_id int unsigned,
    is_success boolean,
    foreign key (operator_card_number) references persons (card_number) on delete cascade,
    foreign key (plan_id) references plans (plan_id) on delete cascade
);

create table tf_12_13_adc_reference_value (
    record_id int unsigned auto_increment primary key,

    power_suply_voltage float,

    -- Референс-значения для считанных показаний с белого коннектора платой PCI ADC
    WhiteConnectorADC_AKK_MinValue float(5, 3), 
    WhiteConnectorADC_AKK_MaxValue float(5, 3),
    WhiteConnectorADC_ILLUM_MinValue float(5, 3),
    WhiteConnectorADC_ILLUM_MaxValue float(5, 3),
    WhiteConnectorADC_ZAJ_MinValue float(5, 3),
    WhiteConnectorADC_ZAJ_MaxValue float(5, 3),
    WhiteConnectorADC_CANH1_MinValue float(5, 3),
    WhiteConnectorADC_CANH1_MaxValue float(5, 3),
    WhiteConnectorADC_euroT2_MinValue float(5, 3),
    WhiteConnectorADC_euroT2_MaxValue float(5, 3),
    WhiteConnectorADC_KOP_C_MinValue float(5, 3),
    WhiteConnectorADC_KOP_C_MaxValue float(5, 3),
    WhiteConnectorADC_GND_MinValue float(5, 3),
    WhiteConnectorADC_GND_MaxValue float(5, 3),
    WhiteConnectorADC_CANL1_MinValue float(5, 3),
    WhiteConnectorADC_CANL1_MaxValue float(5, 3),
    -- Референс-значения для считанных показаний с желтого коннектора платой PCI ADC
    YellowConnectorADC_9V_MinValue float(5,3),
    YellowConnectorADC_9V_MaxValue float(5, 3),
	YellowConnectorADC_GND_MinValue float(5,3),
    YellowConnectorADC_GND_MaxValue float(5, 3),
	YellowConnectorADC_DAT_I_MinValue float(5,3),
    YellowConnectorADC_DAT_I_MaxValue float(5, 3),
	YellowConnectorADC_EMPTY1_MinValue float(5,3),
    YellowConnectorADC_EMPTY1_MaxValue float(5, 3),
	YellowConnectorADC_EMPTY2_MinValue float(5,3),
    YellowConnectorADC_EMPTY2_MaxValue float(5, 3),
	YellowConnectorADC_OUT_P1_MinValue float(5,3),
    YellowConnectorADC_OUT_P1_MaxValue float(5, 3),
	YellowConnectorADC_OUT_P2_MinValue float(5,3),
    YellowConnectorADC_OUT_P2_MaxValue float(5, 3),
	YellowConnectorADC_X_B8_MinValue float(5,3),
    YellowConnectorADC_X_B8_MaxValue float(5,3),
    -- Референс-значения для считанных показаний с красного коннектора платой PCI ADC
    RedConnectorADC_9V_MinValue float(5, 3),
    RedConnectorADC_9V_MaxValue   float(5, 3),
	RedConnectorADC_GND_MinValue float(5, 3),
    RedConnectorADC_GND_MaxValue float(5, 3),
	RedConnectorADC_oborot_dvig_MinValue float(5, 3),
    RedConnectorADC_oborot_dvig_MaxValue float(5, 3),
	RedConnectorADC_EMPTY1_MinValue float(5, 3),
    RedConnectorADC_EMPTY1_MaxValue float(5, 3),
	RedConnectorADC_CANH2_MinValue float(5, 3),
    RedConnectorADC_CANH2_MaxValue float(5, 3),
	RedConnectorADC_GND_CAN_MinValue float(5, 3),
    RedConnectorADC_GND_CAN_MaxValue float(5, 3),
	RedConnectorADC_CANL2_MinValue float(5, 3),
    RedConnectorADC_CANL2_MaxValue float(5, 3),
	RedConnectorADC_CANH2_T_MinValue float(5, 3),
    RedConnectorADC_CANH2_T_MaxValue float(5, 3),
    -- Референс-значения для считанных показаний с коричневого коннектора платой PCI ADC
    BrownConnectorADC_X_D6_MinValue float(5, 3),
    BrownConnectorADC_X_D6_MaxValue float(5, 3),
	BrownConnectorADC_X_D8_MinValue float(5, 3),
    BrownConnectorADC_X_D8_MaxValue float(5, 3),
	BrownConnectorADC_GND_MinValue float(5, 3),
    BrownConnectorADC_GND_MaxValue float(5, 3),
	BrownConnectorADC_X_D4_MinValue float(5, 3),
    BrownConnectorADC_X_D4_MaxValue float(5, 3),
	BrownConnectorADC_X_D5_MinValue float(5, 3),
    BrownConnectorADC_X_D5_MaxValue float(5, 3),
	BrownConnectorADC_EMPTY1_MinValue float(5, 3),
    BrownConnectorADC_EMPTY1_MaxValue float(5, 3),
	BrownConnectorADC_CALIBR_MinValue float(5, 3),
    BrownConnectorADC_CALIBR_MaxValue float(5, 3),
	BrownConnectorADC_EMPTY2_MinValue float(5, 3),
    BrownConnectorADC_EMPTY2_MaxValue float(5, 3)
);

create table tf_12_13_FLASH_memory_errors (
    error_id varchar(10) primary key,
    error_description text not null
);

create table tf_12_13_FRAM_errors (
    error_id varchar(10) primary key,
    error_description text not null
);

create table tf_12_13_SKZI_errors (
    error_id varchar(10) primary key,
    error_description text not null
);

create table tf_12_13_card_reader_errors (
    error_id varchar(10) primary key,
    error_description text not null
);

create table tf_12_13_front_RS232_errors (
    error_id varchar(10) primary key,
    error_description text not null
);

create table tf_12_13_KLine_errors (
    error_id varchar(10) primary key,
    error_description text not null
);

create table tf_12_13_CAN_errors (
    error_id varchar(10) primary key,
    error_description text not null
);

create table tf_12_13_printer_errors (
    error_id varchar(10) primary key,
    error_description text not null
);

create table tf_12_13 (
    tf_12_13_id int unsigned auto_increment primary key,
    seats_persons_id int unsigned not null,
    scan_date date not null,
    scan_time time not null,
    SKZI_board_id varchar(50),
    SKZI_id varchar(50),
    MCU_id varchar(24),
    software_version varchar(10),
    software_date date,
    board_date_of_manufacture date,
    board_number int unsigned,
    board_type varchar(24),
    current float,
    buttons_test_status int unsigned, -- 1, 2
    buttons_error_code varchar(10),
    illumination_test_status int unsigned, -- 1, 2
    illumination_error_code varchar(10),
    LCD_test_status int unsigned, -- 1, 2
    LCD_error_code varchar(10),
    buzzer_test_status int unsigned, -- 1, 2
    buzzer_error_code varchar(10),
    flash_memory_test_status int unsigned, -- 7, 8, 9, 10
    flash_memory_error_code varchar(10),
    flash_memory_BAD_sector int unsigned,
    flash_memory_BAD_address int unsigned,
    FRAM_test_status int unsigned, -- 1, 2
    FRAM_error_code varchar(10),
    FRAM_DataBus int unsigned,
    FRAM_AddBus int unsigned,
    FRAM_AddDevice int unsigned,
    SKZI_test_status int unsigned, -- 1, 2
    SKZI_SPI_error_code varchar(10),
    SKZI_I2C_error_code varchar(10),
    card_reader_test_status int unsigned, -- 1, 2
    card_reader1_error_code varchar(10),
    card_reader2_error_code varchar(10),
    front_RS232_test_status int unsigned, -- 1, 2
    front_RS232_error_code varchar(10),
    KLine_test_status int unsigned, -- 1, 2
    KLine_error_code varchar(10),
    CAN_test_status int unsigned, -- 1, 2
    CAN_error_code varchar(10),
    printer_test_status int unsigned, -- 1, 2
    printer_error_code varchar(10),
    board_status int unsigned, -- 3, 4
    dopog boolean, -- допог или не допог: посмотреть маркировку на пластике, старший смены выбирает кол-во изделий этого типа по плану в программе КФ
    part_number varchar(30), -- старший смены выбирает кол-во изделий этого типа по плану в программе КФ
    serial_number int unsigned unique,
    date_of_serial_number date,
    ready_to_PSI boolean,
    startV float,
    charge float,
    endV float,
    statusV boolean,
    tacho_VDDA float,
    tacho_VDDA_status boolean,
    foreign key (seats_persons_id) references seats_persons (seats_persons_id) on delete cascade,
    foreign key (buttons_test_status) references statuses (status_id) on delete cascade,
    foreign key (illumination_test_status) references statuses (status_id) on delete cascade,
    foreign key (LCD_test_status) references statuses (status_id) on delete cascade,
    foreign key (buzzer_test_status) references statuses (status_id) on delete cascade,
    foreign key (flash_memory_test_status) references statuses (status_id) on delete cascade,
    foreign key (FRAM_test_status) references statuses (status_id) on delete cascade,
    foreign key (SKZI_test_status) references statuses (status_id) on delete cascade,
    foreign key (card_reader_test_status) references statuses (status_id) on delete cascade,
    foreign key (front_RS232_test_status) references statuses (status_id) on delete cascade,
    foreign key (KLine_test_status) references statuses (status_id) on delete cascade,
    foreign key (CAN_test_status) references statuses (status_id) on delete cascade,
    foreign key (printer_test_status) references statuses (status_id) on delete cascade,
    foreign key (board_status) references statuses (status_id) on delete cascade
);

create table tf_12_13_archive (
    tf_12_13_id int unsigned auto_increment primary key,
    seats_persons_id int unsigned not null,
    scan_date date not null,
    scan_time time not null,
    SKZI_board_id varchar(50),
    SKZI_id varchar(50),
    MCU_id varchar(24),
    software_version varchar(5),
    software_date date,
    board_date_of_manufacture date,
    board_number int unsigned,
    board_type varchar(24),
    current float,
    buttons_test_status int unsigned, -- 1, 2
    buttons_error_code varchar(10),
    illumination_test_status int unsigned, -- 1, 2
    illumination_error_code varchar(10),
    LCD_test_status int unsigned, -- 1, 2
    LCD_error_code varchar(10),
    buzzer_test_status int unsigned, -- 1, 2
    buzzer_error_code varchar(10),
    flash_memory_test_status int unsigned, -- 7, 8, 9, 10
    flash_memory_error_code varchar(10),
    flash_memory_BAD_sector int unsigned,
    flash_memory_BAD_address int unsigned,
    FRAM_test_status int unsigned, -- 1, 2
    FRAM_error_code varchar(10),
    FRAM_DataBus int unsigned,
    FRAM_AddBus int unsigned,
    FRAM_AddDevice int unsigned,
    SKZI_test_status int unsigned, -- 1, 2
    SKZI_SPI_error_code varchar(10),
    SKZI_I2C_error_code varchar(10),
    card_reader_test_status int unsigned, -- 1, 2
    card_reader1_error_code varchar(10),
    card_reader2_error_code varchar(10),
    front_RS232_test_status int unsigned, -- 1, 2
    front_RS232_error_code varchar(10),
    KLine_test_status int unsigned, -- 1, 2
    KLine_error_code varchar(10),
    CAN_test_status int unsigned, -- 1, 2
    CAN_error_code varchar(10),
    printer_test_status int unsigned, -- 1, 2
    printer_error_code varchar(10),
    board_status int unsigned, -- 3, 4
    dopog boolean, -- допог или не допог: посмотреть маркировку на пластике, старший смены выбирает кол-во изделий этого типа по плану в программе КФ
    part_number varchar(30), -- старший смены выбирает кол-во изделий этого типа по плану в программе КФ
    serial_number int unsigned,
    date_of_serial_number date,
    ready_to_PSI boolean,
    startV float,
    charge float,
    endV float,
    statusV boolean,
    tacho_VDDA float,
    tacho_VDDA_status boolean,
    foreign key (seats_persons_id) references seats_persons (seats_persons_id) on delete cascade,
    foreign key (buttons_test_status) references statuses (status_id) on delete cascade,
    foreign key (illumination_test_status) references statuses (status_id) on delete cascade,
    foreign key (LCD_test_status) references statuses (status_id) on delete cascade,
    foreign key (buzzer_test_status) references statuses (status_id) on delete cascade,
    foreign key (flash_memory_test_status) references statuses (status_id) on delete cascade,
    foreign key (FRAM_test_status) references statuses (status_id) on delete cascade,
    foreign key (SKZI_test_status) references statuses (status_id) on delete cascade,
    foreign key (card_reader_test_status) references statuses (status_id) on delete cascade,
    foreign key (front_RS232_test_status) references statuses (status_id) on delete cascade,
    foreign key (KLine_test_status) references statuses (status_id) on delete cascade,
    foreign key (CAN_test_status) references statuses (status_id) on delete cascade,
    foreign key (printer_test_status) references statuses (status_id) on delete cascade,
    foreign key (board_status) references statuses (status_id) on delete cascade
);

create table tf_12_13_ADC (
    tf_12_13_ADC_id int unsigned auto_increment primary key,
    MCU_id varchar(24),

    rec_date date not null,
    rec_time time not null,

    power_suply_voltage float not null,

    -- БЕЛЫЙ РАЗЪЁМ A
    WhiteConnectorADC_AKK float(5, 3),
    WhiteConnectorADC_ILLUM float(5, 3),
    WhiteConnectorADC_ZAJ float(5, 3),
    WhiteConnectorADC_CANH1 float(5, 3),
    WhiteConnectorADC_euroT2 float(5, 3),
    WhiteConnectorADC_KOP_C float(5, 3),
    WhiteConnectorADC_GND float(5, 3),
    WhiteConnectorADC_CANL1 float(5, 3),
    -- ЖЕЛТЫЙ РАЗЪЁМ B
    YellowConnectorADC_9V float(5, 3),
    YellowConnectorADC_GND float(5, 3),
    YellowConnectorADC_DAT_I float(5, 3),
    YellowConnectorADC_EMPTY1 float(5, 3),
    YellowConnectorADC_EMPTY2 float(5, 3),
    YellowConnectorADC_OUT_P1 float(5, 3),
    YellowConnectorADC_OUT_P2 float(5, 3),
    YellowConnectorADC_X_B8 float(5, 3),
    -- КРАСНЫЙ РАЗЪЁМ C
    RedConnectorADC_9V float(5, 3),
    RedConnectorADC_GND float(5, 3),
    RedConnectorADC_oborot_dvig float(5, 3),
    RedConnectorADC_EMPTY1 float(5, 3),
    RedConnectorADC_CANH2 float(5, 3),
    RedConnectorADC_GND_CAN float(5, 3),
    RedConnectorADC_CANL2 float(5, 3),
    RedConnectorADC_CANH2_ float(5, 3),
    -- КОРИЧНЕВЫЙ РАЗЪЁМ D
    BrownConnectorADC_X_D6 float(5, 3),
    BrownConnectorADC_X_D8 float(5, 3),
    BrownConnectorADC_GND float(5, 3),
    BrownConnectorADC_X_D4 float(5, 3),
    BrownConnectorADC_X_D5 float(5, 3),
    BrownConnectorADC_EMPTY1 float(5, 3),
    BrownConnectorADC_CALIBR float(5, 3),  
    BrownConnectorADC_EMPTY2 float(5, 3),

    board_voltage_status boolean default false
);

create table tf_12_13_internal_ADC (
    tf_12_13_internal_ADC_id int unsigned auto_increment primary key,
    rec_date date,
    rec_time time,
    MCU_id varchar(24),
    Vdda int unsigned,
    VREFINT_MCU int unsigned,
    VBAT_MCU int unsigned,
    VBAT_GB1 int unsigned,
    VBAT_Termo_Rez int unsigned,
    VPRN_Termo_Rez int unsigned,
    VAKB int unsigned,
    VZAJ int unsigned,
    ILLUM_CONTROL int unsigned,
    MASS_CONTROL int unsigned
);

create table tf_14 (
    tf_14_id int unsigned auto_increment primary key,
    operator_card_number int unsigned not null,
    scan_date date not null,
    scan_time time not null,
    
    serial_number int unsigned,
    date_of_serial_number date,
    serial_number_and_date_of_serial_number_date date,
    serial_number_and_date_of_serial_number_time time,

    plan_id int unsigned,

    is_success boolean,
    foreign key (operator_card_number) references persons (card_number) on delete cascade,
    foreign key (plan_id) references plans (plan_id) on delete cascade
);

create table tf_14_archive (
    tf_14_id int unsigned auto_increment primary key,
    operator_card_number int unsigned not null,
    scan_date date not null,
    scan_time time not null,
    
    serial_number int unsigned,
    date_of_serial_number date,
    serial_number_and_date_of_serial_number_date date,
    serial_number_and_date_of_serial_number_time time,

    plan_id int unsigned,

    is_success boolean,
    foreign key (operator_card_number) references persons (card_number) on delete cascade,
    foreign key (plan_id) references plans (plan_id) on delete cascade
);

create table tf_15_16_errors (
    error_id int unsigned primary key,
    error_description text not null
);

create table tf_15_16 (
    tf_15_16_id int unsigned auto_increment primary key,
    scan_date date not null,
    scan_time time not null,
    current float,
    seats_persons_id int unsigned,
    scanned_info_serial_number int unsigned,
    scanned_info_date_of_manufacture date,
    tacho_serial_number int unsigned,
    serial_numbers_equal boolean,
    MCU_id varchar(24),
    SKZI_id varchar(50),
    SKZI_status int unsigned, -- 1, 2
    SKZI_error int unsigned,
    card_reader_1_status int unsigned, -- 1, 2
    card_reader_1_error int unsigned,
    card_reader_2_status int unsigned, -- 1, 2
    card_reader_2_error int unsigned,
    CAN_1_status int unsigned, -- 1, 2
    CAN_1_error int unsigned,
    CAN_2_status int unsigned, -- 1, 2
    CAN_2_error int unsigned,
    tacho_status int unsigned, -- 1, 2
    tacho_error int unsigned,
    odometr_status int unsigned, -- 1, 2
    odometr_error int unsigned,
    can_speed float,
    generator_speed float,
    can_number int unsigned,
    KFactor int unsigned,
    PSI_status int unsigned, -- 3, 4
    ready_to_verify boolean,
    foreign key (seats_persons_id) references seats_persons (seats_persons_id) on delete cascade,
    foreign key (card_reader_1_status) references statuses (status_id) on delete cascade,
    foreign key (card_reader_2_status) references statuses (status_id) on delete cascade,
    foreign key (SKZI_status) references statuses (status_id) on delete cascade,
    foreign key (CAN_1_status) references statuses (status_id) on delete cascade,
    foreign key (CAN_2_status) references statuses (status_id) on delete cascade,
    foreign key (PSI_status) references statuses (status_id) on delete cascade,
    foreign key (card_reader_1_error) references tf_15_16_errors (error_id) on delete cascade,
    foreign key (card_reader_2_error) references tf_15_16_errors (error_id) on delete cascade,
    foreign key (SKZI_error) references tf_15_16_errors (error_id) on delete cascade,
    foreign key (CAN_1_error) references tf_15_16_errors (error_id) on delete cascade,
    foreign key (CAN_2_error) references tf_15_16_errors (error_id) on delete cascade
);

create table tf_15_16_archive (
    tf_15_16_id int unsigned auto_increment primary key,
    scan_date date,
    scan_time time,
    current float,
    seats_persons_id int unsigned,
    scanned_info_serial_number int unsigned,
    scanned_info_date_of_manufacture date,
    tacho_serial_number int unsigned,
    serial_numbers_equal boolean,
    MCU_id varchar(24),
    SKZI_id varchar(50),
    SKZI_status int unsigned, -- 1, 2
    SKZI_error int unsigned,
    card_reader_1_status int unsigned, -- 1, 2
    card_reader_1_error int unsigned,
    card_reader_2_status int unsigned, -- 1, 2
    card_reader_2_error int unsigned,
    CAN_1_status int unsigned, -- 1, 2
    CAN_1_error int unsigned,
    CAN_2_status int unsigned, -- 1, 2
    CAN_2_error int unsigned,
    tacho_status int unsigned, -- 1, 2
    tacho_error int unsigned,
    odometr_status int unsigned, -- 1, 2
    odometr_error int unsigned,
    can_speed float,
    generator_speed float,
    can_number int unsigned,
    KFactor int unsigned,
    PSI_status int unsigned, -- 3, 4
    ready_to_verify boolean,
    foreign key (seats_persons_id) references seats_persons (seats_persons_id) on delete cascade,
    foreign key (card_reader_1_status) references statuses (status_id) on delete cascade,
    foreign key (card_reader_2_status) references statuses (status_id) on delete cascade,
    foreign key (SKZI_status) references statuses (status_id) on delete cascade,
    foreign key (CAN_1_status) references statuses (status_id) on delete cascade,
    foreign key (CAN_2_status) references statuses (status_id) on delete cascade,
    foreign key (PSI_status) references statuses (status_id) on delete cascade,
    foreign key (card_reader_1_error) references tf_15_16_errors (error_id) on delete cascade,
    foreign key (card_reader_2_error) references tf_15_16_errors (error_id) on delete cascade,
    foreign key (SKZI_error) references tf_15_16_errors (error_id) on delete cascade,
    foreign key (CAN_1_error) references tf_15_16_errors (error_id) on delete cascade,
    foreign key (CAN_2_error) references tf_15_16_errors (error_id) on delete cascade
);

create table tf_15_16_ADC (
    tf_15_16_ADC_id int unsigned auto_increment primary key,
    MCU_id varchar(24),

    rec_date date not null,
    rec_time time not null,

    power_suply_voltage float not null,

    -- БЕЛЫЙ РАЗЪЁМ A
    WhiteConnectorADC_AKK float(5, 3),
    WhiteConnectorADC_ILLUM float(5, 3),
    WhiteConnectorADC_ZAJ float(5, 3),
    WhiteConnectorADC_CANH1 float(5, 3),
    WhiteConnectorADC_euroT2 float(5, 3),
    WhiteConnectorADC_KOP_C float(5, 3),
    WhiteConnectorADC_GND float(5, 3),
    WhiteConnectorADC_CANL1 float(5, 3),
    -- ЖЕЛТЫЙ РАЗЪЁМ B
    YellowConnectorADC_9V float(5, 3),
    YellowConnectorADC_GND float(5, 3),
    YellowConnectorADC_DAT_I float(5, 3),
    YellowConnectorADC_EMPTY1 float(5, 3),
    YellowConnectorADC_EMPTY2 float(5, 3),
    YellowConnectorADC_OUT_P1 float(5, 3),
    YellowConnectorADC_OUT_P2 float(5, 3),
    YellowConnectorADC_X_B8 float(5, 3),
    -- КРАСНЫЙ РАЗЪЁМ C
    RedConnectorADC_9V float(5, 3),
    RedConnectorADC_GND float(5, 3),
    RedConnectorADC_oborot_dvig float(5, 3),
    RedConnectorADC_EMPTY1 float(5, 3),
    RedConnectorADC_CANH2 float(5, 3),
    RedConnectorADC_GND_CAN float(5, 3),
    RedConnectorADC_CANL2 float(5, 3),
    RedConnectorADC_CANH2_ float(5, 3),
    -- КОРИЧНЕВЫЙ РАЗЪЁМ D
    BrownConnectorADC_X_D6 float(5, 3),
    BrownConnectorADC_X_D8 float(5, 3),
    BrownConnectorADC_GND float(5, 3),
    BrownConnectorADC_X_D4 float(5, 3),
    BrownConnectorADC_X_D5 float(5, 3),
    BrownConnectorADC_EMPTY1 float(5, 3),
    BrownConnectorADC_CALIBR float(5, 3),  
    BrownConnectorADC_EMPTY2 float(5, 3),

    board_voltage_status boolean default false
);

create table tf_15_16_adc_reference_value (
    record_id int unsigned auto_increment primary key,

    power_suply_voltage float,

    -- Референс-значения для считанных показаний с белого коннектора платой PCI ADC
    WhiteConnectorADC_AKK_MinValue float(5, 3), 
    WhiteConnectorADC_AKK_MaxValue float(5, 3),
    WhiteConnectorADC_ILLUM_MinValue float(5, 3),
    WhiteConnectorADC_ILLUM_MaxValue float(5, 3),
    WhiteConnectorADC_ZAJ_MinValue float(5, 3),
    WhiteConnectorADC_ZAJ_MaxValue float(5, 3),
    WhiteConnectorADC_CANH1_MinValue float(5, 3),
    WhiteConnectorADC_CANH1_MaxValue float(5, 3),
    WhiteConnectorADC_euroT2_MinValue float(5, 3),
    WhiteConnectorADC_euroT2_MaxValue float(5, 3),
    WhiteConnectorADC_KOP_C_MinValue float(5, 3),
    WhiteConnectorADC_KOP_C_MaxValue float(5, 3),
    WhiteConnectorADC_GND_MinValue float(5, 3),
    WhiteConnectorADC_GND_MaxValue float(5, 3),
    WhiteConnectorADC_CANL1_MinValue float(5, 3),
    WhiteConnectorADC_CANL1_MaxValue float(5, 3),
    -- Референс-значения для считанных показаний с желтого коннектора платой PCI ADC
    YellowConnectorADC_9V_MinValue float(5,3),
    YellowConnectorADC_9V_MaxValue float(5, 3),
	YellowConnectorADC_GND_MinValue float(5,3),
    YellowConnectorADC_GND_MaxValue float(5, 3),
	YellowConnectorADC_DAT_I_MinValue float(5,3),
    YellowConnectorADC_DAT_I_MaxValue float(5, 3),
	YellowConnectorADC_EMPTY1_MinValue float(5,3),
    YellowConnectorADC_EMPTY1_MaxValue float(5, 3),
	YellowConnectorADC_EMPTY2_MinValue float(5,3),
    YellowConnectorADC_EMPTY2_MaxValue float(5, 3),
	YellowConnectorADC_OUT_P1_MinValue float(5,3),
    YellowConnectorADC_OUT_P1_MaxValue float(5, 3),
	YellowConnectorADC_OUT_P2_MinValue float(5,3),
    YellowConnectorADC_OUT_P2_MaxValue float(5, 3),
	YellowConnectorADC_X_B8_MinValue float(5,3),
    YellowConnectorADC_X_B8_MaxValue float(5, 3),
    -- Референс-значения для считанных показаний с красного коннектора платой PCI ADC
    RedConnectorADC_9V_MinValue float(5, 3),
    RedConnectorADC_9V_MaxValue   float(5, 3),
	RedConnectorADC_GND_MinValue float(5, 3),
    RedConnectorADC_GND_MaxValue float(5, 3),
	RedConnectorADC_oborot_dvig_MinValue float(5, 3),
    RedConnectorADC_oborot_dvig_MaxValue float(5, 3),
	RedConnectorADC_EMPTY1_MinValue float(5, 3),
    RedConnectorADC_EMPTY1_MaxValue float(5, 3),
	RedConnectorADC_CANH2_MinValue float(5, 3),
    RedConnectorADC_CANH2_MaxValue float(5, 3),
	RedConnectorADC_GND_CAN_MinValue float(5, 3),
    RedConnectorADC_GND_CAN_MaxValue float(5, 3),
	RedConnectorADC_CANL2_MinValue float(5, 3),
    RedConnectorADC_CANL2_MaxValue float(5, 3),
	RedConnectorADC_CANH2_T_MinValue float(5, 3),
    RedConnectorADC_CANH2_T_MaxValue float(5, 3),
    -- Референс-значения для считанных показаний с коричневого коннектора платой PCI ADC
    BrownConnectorADC_X_D6_MinValue float(5, 3),
    BrownConnectorADC_X_D6_MaxValue float(5, 3),
	BrownConnectorADC_X_D8_MinValue float(5, 3),
    BrownConnectorADC_X_D8_MaxValue float(5, 3),
	BrownConnectorADC_GND_MinValue float(5, 3),
    BrownConnectorADC_GND_MaxValue float(5, 3),
	BrownConnectorADC_X_D4_MinValue float(5, 3),
    BrownConnectorADC_X_D4_MaxValue float(5, 3),
	BrownConnectorADC_X_D5_MinValue float(5, 3),
    BrownConnectorADC_X_D5_MaxValue float(5, 3),
	BrownConnectorADC_EMPTY1_MinValue float(5, 3),
    BrownConnectorADC_EMPTY1_MaxValue float(5, 3),
	BrownConnectorADC_CALIBR_MinValue float(5, 3),
    BrownConnectorADC_CALIBR_MaxValue float(5, 3),
	BrownConnectorADC_EMPTY2_MinValue float(5, 3),
    BrownConnectorADC_EMPTY2_MaxValue float(5, 3)
);

create table tf_17 (
    tf_17_id int unsigned auto_increment primary key,
    operator_card_number int unsigned not null,
    scan_date date not null,
    scan_time time not null,
    
    serial_number int unsigned,
    date_of_manufacture date,
    serial_number_and_date_of_manufacture_date date,
    serial_number_and_date_of_manufacture_time time,

    plan_id int unsigned,

    is_success boolean,
    foreign key (operator_card_number) references persons (card_number) on delete cascade,
    foreign key (plan_id) references plans (plan_id) on delete cascade
);

create table tf_17_archive (
    tf_17_id int unsigned auto_increment primary key,
    operator_card_number int unsigned not null,
    scan_date date not null,
    scan_time time not null,
    
    serial_number int unsigned,
    date_of_manufacture date,
    serial_number_and_date_of_manufacture_date date,
    serial_number_and_date_of_manufacture_time time,

    plan_id int unsigned,

    is_success boolean,
    foreign key (operator_card_number) references persons (card_number) on delete cascade,
    foreign key (plan_id) references plans (plan_id) on delete cascade
);

create table customers (
    customer_id int unsigned auto_increment primary key,
    customer_name varchar(30) not null
);

create table customers_versions (
    customers_versions_id int unsigned auto_increment primary key,
    customer_id int unsigned,
    v int unsigned,
    foreign key (customer_id) references customers (customer_id) on delete cascade
);

create table tables (
    table_id int unsigned auto_increment primary key,
    table_name varchar(40) not null
);

create table tables_bins (
    tables_bins_id int unsigned auto_increment primary key,
    bin_data blob not null,
    table_id int unsigned,
    foreign key (table_id) references tables (table_id) on delete cascade
);

create table customers_tables_bins (
    customers_tables_bins_id int unsigned auto_increment primary key,
    tables_bins_id int unsigned,
    customers_versions_id int unsigned,
    foreign key (tables_bins_id) references tables_bins (tables_bins_id) on delete cascade,
    foreign key (customers_versions_id) references customers_versions (customers_versions_id) on delete cascade
);

create table tf_18 (
    tf_18_id int unsigned auto_increment primary key,
    operator_card_number int unsigned not null,
    scan_date date not null,
    scan_time time not null,
    serial_number int unsigned,
    date_of_manufacture date,
    is_verify_success boolean,
    is_verify_success_in_menu boolean default false,
    foreign key (operator_card_number) references persons (card_number) on delete cascade,
    unique key unique_serial_date (serial_number, date_of_manufacture)
);

create table tf_18_archive (
    tf_18_id int unsigned auto_increment primary key,
    operator_card_number int unsigned not null,
    scan_date date not null,
    scan_time time not null,
    serial_number int unsigned,
    date_of_manufacture date,
    is_verify_success boolean,
    is_verify_success_in_menu boolean,
    foreign key (operator_card_number) references persons (card_number) on delete cascade
);

create table tf_19 (
    tf_19_id int unsigned auto_increment primary key,
    operator_card_number int unsigned not null,
    scan_date date not null,
    scan_time time not null,
    current float,
    scanned_info_serial_number int unsigned,
    scanned_info_date_of_manufacture date,
    tacho_serial_number int unsigned,
    serial_numbers_equal boolean,
    MCU_id varchar(24),
    SKZI_id varchar(50),
    scanned_SKZI_id varchar(50),
    SKZI_equal boolean,
    part_number_id int unsigned,
    customer_id int unsigned,
    ready_to_sale boolean,
    foreign key (operator_card_number) references persons (card_number) on delete cascade,
    foreign key (part_number_id) references part_numbers (part_number_id) on delete cascade,
    foreign key (customer_id) references customers (customer_id) on delete cascade
);

create table FBU (
    FBU_id int unsigned auto_increment primary key,
    tf_chain_id int unsigned unique,
    is_sended boolean default false,
    is_registered boolean default false,
    foreign key (tf_chain_id) references tf_chain (tf_chain_id) on delete cascade
);

create table tf_19_archive (
    tf_19_id int unsigned auto_increment primary key,
    operator_card_number int unsigned,
    scan_date date,
    scan_time time,
    current float,
    scanned_info_serial_number int unsigned,
    scanned_info_date_of_manufacture date,
    tacho_serial_number int unsigned,
    serial_numbers_equal boolean,
    MCU_id varchar(24),
    SKZI_id varchar(50),
    scanned_SKZI_id varchar(50),
    SKZI_equal boolean,
    part_number_id int unsigned,
    customer_id int unsigned,
    ready_to_sale boolean,
    foreign key (operator_card_number) references persons (card_number) on delete cascade,
    foreign key (customer_id) references customers (customer_id) on delete cascade
);

create table tf_19_ADC (
    tf_19_ADC_id int unsigned auto_increment primary key,
    MCU_id varchar(24),

    rec_date date not null,
    rec_time time not null,

    power_suply_voltage float not null,

    -- БЕЛЫЙ РАЗЪЁМ A
    WhiteConnectorADC_AKK float(5, 3),
    WhiteConnectorADC_ILLUM float(5, 3),
    WhiteConnectorADC_ZAJ float(5, 3),
    WhiteConnectorADC_CANH1 float(5, 3),
    WhiteConnectorADC_euroT2 float(5, 3),
    WhiteConnectorADC_KOP_C float(5, 3),
    WhiteConnectorADC_GND float(5, 3),
    WhiteConnectorADC_CANL1 float(5, 3),
    -- ЖЕЛТЫЙ РАЗЪЁМ B
    YellowConnectorADC_9V float(5, 3),
    YellowConnectorADC_GND float(5, 3),
    YellowConnectorADC_DAT_I float(5, 3),
    YellowConnectorADC_EMPTY1 float(5, 3),
    YellowConnectorADC_EMPTY2 float(5, 3),
    YellowConnectorADC_OUT_P1 float(5, 3),
    YellowConnectorADC_OUT_P2 float(5, 3),
    YellowConnectorADC_X_B8 float(5, 3),
    -- КРАСНЫЙ РАЗЪЁМ C
    RedConnectorADC_9V float(5, 3),
    RedConnectorADC_GND float(5, 3),
    RedConnectorADC_oborot_dvig float(5, 3),
    RedConnectorADC_EMPTY1 float(5, 3),
    RedConnectorADC_CANH2 float(5, 3),
    RedConnectorADC_GND_CAN float(5, 3),
    RedConnectorADC_CANL2 float(5, 3),
    RedConnectorADC_CANH2_ float(5, 3),
    -- КОРИЧНЕВЫЙ РАЗЪЁМ D
    BrownConnectorADC_X_D6 float(5, 3),
    BrownConnectorADC_X_D8 float(5, 3),
    BrownConnectorADC_GND float(5, 3),
    BrownConnectorADC_X_D4 float(5, 3),
    BrownConnectorADC_X_D5 float(5, 3),
    BrownConnectorADC_EMPTY1 float(5, 3),
    BrownConnectorADC_CALIBR float(5, 3),  
    BrownConnectorADC_EMPTY2 float(5, 3),

    board_voltage_status boolean default false
);

create table tf_19_adc_reference_value (
    record_id int unsigned auto_increment primary key,

    power_suply_voltage float,

    -- Референс-значения для считанных показаний с белого коннектора платой PCI ADC
    WhiteConnectorADC_AKK_MinValue float(5, 3), 
    WhiteConnectorADC_AKK_MaxValue float(5, 3),
    WhiteConnectorADC_ILLUM_MinValue float(5, 3),
    WhiteConnectorADC_ILLUM_MaxValue float(5, 3),
    WhiteConnectorADC_ZAJ_MinValue float(5, 3),
    WhiteConnectorADC_ZAJ_MaxValue float(5, 3),
    WhiteConnectorADC_CANH1_MinValue float(5, 3),
    WhiteConnectorADC_CANH1_MaxValue float(5, 3),
    WhiteConnectorADC_euroT2_MinValue float(5, 3),
    WhiteConnectorADC_euroT2_MaxValue float(5, 3),
    WhiteConnectorADC_KOP_C_MinValue float(5, 3),
    WhiteConnectorADC_KOP_C_MaxValue float(5, 3),
    WhiteConnectorADC_GND_MinValue float(5, 3),
    WhiteConnectorADC_GND_MaxValue float(5, 3),
    WhiteConnectorADC_CANL1_MinValue float(5, 3),
    WhiteConnectorADC_CANL1_MaxValue float(5, 3),
    -- Референс-значения для считанных показаний с желтого коннектора платой PCI ADC
    YellowConnectorADC_9V_MinValue float(5,3),
    YellowConnectorADC_9V_MaxValue float(5, 3),
	YellowConnectorADC_GND_MinValue float(5,3),
    YellowConnectorADC_GND_MaxValue float(5, 3),
	YellowConnectorADC_DAT_I_MinValue float(5,3),
    YellowConnectorADC_DAT_I_MaxValue float(5, 3),
	YellowConnectorADC_EMPTY1_MinValue float(5,3),
    YellowConnectorADC_EMPTY1_MaxValue float(5, 3),
	YellowConnectorADC_EMPTY2_MinValue float(5,3),
    YellowConnectorADC_EMPTY2_MaxValue float(5, 3),
	YellowConnectorADC_OUT_P1_MinValue float(5,3),
    YellowConnectorADC_OUT_P1_MaxValue float(5, 3),
	YellowConnectorADC_OUT_P2_MinValue float(5,3),
    YellowConnectorADC_OUT_P2_MaxValue float(5, 3),
	YellowConnectorADC_X_B8_MinValue float(5,3),
    YellowConnectorADC_X_B8_MaxValue float(5, 3),
    -- Референс-значения для считанных показаний с красного коннектора платой PCI ADC
    RedConnectorADC_9V_MinValue float(5, 3),
    RedConnectorADC_9V_MaxValue   float(5, 3),
	RedConnectorADC_GND_MinValue float(5, 3),
    RedConnectorADC_GND_MaxValue float(5, 3),
	RedConnectorADC_oborot_dvig_MinValue float(5, 3),
    RedConnectorADC_oborot_dvig_MaxValue float(5, 3),
	RedConnectorADC_EMPTY1_MinValue float(5, 3),
    RedConnectorADC_EMPTY1_MaxValue float(5, 3),
	RedConnectorADC_CANH2_MinValue float(5, 3),
    RedConnectorADC_CANH2_MaxValue float(5, 3),
	RedConnectorADC_GND_CAN_MinValue float(5, 3),
    RedConnectorADC_GND_CAN_MaxValue float(5, 3),
	RedConnectorADC_CANL2_MinValue float(5, 3),
    RedConnectorADC_CANL2_MaxValue float(5, 3),
	RedConnectorADC_CANH2_T_MinValue float(5, 3),
    RedConnectorADC_CANH2_T_MaxValue float(5, 3),
    -- Референс-значения для считанных показаний с коричневого коннектора платой PCI ADC
    BrownConnectorADC_X_D6_MinValue float(5, 3),
    BrownConnectorADC_X_D6_MaxValue float(5, 3),
	BrownConnectorADC_X_D8_MinValue float(5, 3),
    BrownConnectorADC_X_D8_MaxValue float(5, 3),
	BrownConnectorADC_GND_MinValue float(5, 3),
    BrownConnectorADC_GND_MaxValue float(5, 3),
	BrownConnectorADC_X_D4_MinValue float(5, 3),
    BrownConnectorADC_X_D4_MaxValue float(5, 3),
	BrownConnectorADC_X_D5_MinValue float(5, 3),
    BrownConnectorADC_X_D5_MaxValue float(5, 3),
	BrownConnectorADC_EMPTY1_MinValue float(5, 3),
    BrownConnectorADC_EMPTY1_MaxValue float(5, 3),
	BrownConnectorADC_CALIBR_MinValue float(5, 3),
    BrownConnectorADC_CALIBR_MaxValue float(5, 3),
	BrownConnectorADC_EMPTY2_MinValue float(5, 3),
    BrownConnectorADC_EMPTY2_MaxValue float(5, 3)
);

create table tf_20 (
    tf_20_id int unsigned auto_increment primary key,
    operator_card_number int unsigned not null,
    scan_date date not null,
    scan_time time not null,
    scanned_info text,
    scan_info_writed_date date,
    scan_info_writed_time time,
    is_success boolean,
    record_ok boolean,
    foreign key (operator_card_number) references persons (card_number) on delete cascade
);

-- -----------------------------------------
create table tacho_repair (
    tacho_repair_id int unsigned auto_increment primary key,
    operator_card_number int unsigned not null,
    scan_date date not null,
    scan_time time not null,
    begin_scan_date date,
    begin_scan_time time,
    tf_chain_id int unsigned,
    tf_1_2_3_id int unsigned,
    tf_8_9_id int unsigned,
    end_scan_date date,
    end_scan_time time,

    last_SKZI_id text,
    new_SKZI_id text,
    last_SKZI_board_id text,
    new_SKZI_board_id text,
    last_main_board text,
    new_main_board text,
    last_LCD text,
    new_LCD text,
    last_BT_board text,
    new_BT_board text,
    last_transport_monitoring_board text,
    new_transport_monitoring_board text,

    replace_card_reader_1_flag boolean,
    replace_card_reader_2_flag boolean,
    replace_LCD_flag boolean,
    replace_termoprinter_flag boolean,
    replace_X_16_train_flag boolean,
    replace_X_18_train_flag boolean,
    is_completed boolean,
    foreign key (operator_card_number) references persons (card_number) on delete cascade
);

create table tacho_repair_archive (
    tacho_repair_id int unsigned auto_increment primary key,
    operator_card_number int unsigned not null,
    scan_date date not null,
    scan_time time not null,
    begin_scan_date date,
    begin_scan_time time,
    tf_chain_id int unsigned,
    tf_1_2_3_id int unsigned,
    tf_8_9_id int unsigned,
    end_scan_date date,
    end_scan_time time,
    last_SKZI_id text,
    new_SKZI_id text,
    last_SKZI_board_id text,
    new_SKZI_board_id text,
    last_main_board text,
    new_main_board text,
    last_LCD text,
    new_LCD text,
    last_BT_board text,
    new_BT_board text,
    last_transport_monitoring_board text,
    new_transport_monitoring_board text,
    replace_card_reader_1_flag boolean,
    replace_card_reader_2_flag boolean,
    replace_LCD_flag boolean,
    replace_termoprinter_flag boolean,
    replace_X_16_train_flag boolean,
    replace_X_18_train_flag boolean,
    is_completed boolean,
    foreign key (operator_card_number) references persons (card_number) on delete cascade
);

create table types_of_elements (
	type_id int unsigned primary key auto_increment,
    type_name text not null
);

create table main_board_elements_notes (
	note_id int unsigned primary key auto_increment,
    note text not null
);

create table main_board_elements_names (
	name_id int unsigned primary key auto_increment,
    element_type_id int unsigned,
    element_name text not null,
    note_id int unsigned,
    foreign key (element_type_id) references types_of_elements (type_id) on delete cascade,
    foreign key (note_id) references main_board_elements_notes (note_id) on delete cascade
);

create table main_board_elements (
	element_id int unsigned primary key auto_increment,
    element_name_id int unsigned not null,
    designation text,
    foreign key (element_name_id) references main_board_elements_names (name_id) on delete cascade
);

create table LCD_elements_notes (
	note_id int unsigned primary key auto_increment,
    note text not null
);

create table LCD_elements_names (
	name_id int unsigned primary key auto_increment,
    element_type_id int unsigned,
    element_name text not null,
    note_id int unsigned,
    foreign key (element_type_id) references types_of_elements (type_id) on delete cascade,
    foreign key (note_id) references LCD_elements_notes (note_id) on delete cascade
);

create table LCD_elements (
	element_id int unsigned primary key auto_increment,
    element_name_id int unsigned not null,
    designation text,
    foreign key (element_name_id) references LCD_elements_names (name_id) on delete cascade
);

create table BT_board_elements_notes (
	note_id int unsigned primary key auto_increment,
    note text not null
);

create table BT_board_elements_names (
	name_id int unsigned primary key auto_increment,
    element_type_id int unsigned,
    element_name text not null,
    note_id int unsigned,
    foreign key (element_type_id) references types_of_elements (type_id) on delete cascade,
    foreign key (note_id) references BT_board_elements_notes (note_id) on delete cascade
);

create table BT_board_elements (
	element_id int unsigned primary key auto_increment,
    element_name_id int unsigned not null,
    designation text,
    foreign key (element_name_id) references BT_board_elements_names (name_id) on delete cascade
);

create table SKZI_board_elements_notes (
	note_id int unsigned primary key auto_increment,
    note text not null
);

create table SKZI_board_elements_names (
	name_id int unsigned primary key auto_increment,
    element_type_id int unsigned,
    element_name text not null,
    note_id int unsigned,
    foreign key (element_type_id) references types_of_elements (type_id) on delete cascade,
    foreign key (note_id) references SKZI_board_elements_notes (note_id) on delete cascade
);

create table SKZI_board_elements (
	element_id int unsigned primary key auto_increment,
    element_name_id int unsigned not null,
    designation text,
    foreign key (element_name_id) references SKZI_board_elements_names (name_id) on delete cascade
);

create table transport_monitoring_board_elements_notes (
	note_id int unsigned primary key auto_increment,
    note text not null
);

create table transport_monitoring_board_elements_names (
	name_id int unsigned primary key auto_increment,
    element_type_id int unsigned,
    element_name text not null,
    note_id int unsigned,
    foreign key (element_type_id) references types_of_elements (type_id) on delete cascade,
    foreign key (note_id) references transport_monitoring_board_elements_notes (note_id) on delete cascade
);

create table transport_monitoring_board_elements (
	element_id int unsigned primary key auto_increment,
    element_name_id int unsigned not null,
    designation text,
    foreign key (element_name_id) references transport_monitoring_board_elements_names (name_id) on delete cascade
);

create table main_board_solding (
	solding_id int unsigned auto_increment primary key,
    tacho_repair_id int unsigned,
    element_id int unsigned not null,
    foreign key (element_id) references main_board_elements (element_id) on delete cascade,
    foreign key (tacho_repair_id) references tacho_repair (tacho_repair_id) on delete cascade
);

create table LCD_solding (
	solding_id int unsigned auto_increment primary key,
    tacho_repair_id int unsigned,
    element_id int unsigned not null,
    foreign key (element_id) references LCD_elements (element_id) on delete cascade,
    foreign key (tacho_repair_id) references tacho_repair (tacho_repair_id) on delete cascade
);

create table BT_board_solding (
	solding_id int unsigned auto_increment primary key,
    tacho_repair_id int unsigned,
    element_id int unsigned not null,
    foreign key (element_id) references BT_board_elements (element_id) on delete cascade,
    foreign key (tacho_repair_id) references tacho_repair (tacho_repair_id) on delete cascade
);

create table SKZI_board_solding (
	solding_id int unsigned auto_increment primary key,
    tacho_repair_id int unsigned,
    element_id int unsigned not null,
    foreign key (element_id) references SKZI_board_elements (element_id) on delete cascade,
    foreign key (tacho_repair_id) references tacho_repair (tacho_repair_id) on delete cascade
);

create table transport_monitoring_board_solding (
	solding_id int unsigned auto_increment primary key,
    tacho_repair_id int unsigned,
    element_id int unsigned not null,
    foreign key (element_id) references transport_monitoring_board_elements (element_id) on delete cascade,
    foreign key (tacho_repair_id) references tacho_repair (tacho_repair_id) on delete cascade
);

create table main_board_el_comp_replacement (
	el_comp_replacement_id int unsigned auto_increment primary key,
    tacho_repair_id int unsigned,
    element_id int unsigned not null,
    foreign key (element_id) references main_board_elements (element_id) on delete cascade,
    foreign key (tacho_repair_id) references tacho_repair (tacho_repair_id) on delete cascade
);

create table LCD_el_comp_replacement (
	el_comp_replacement_id int unsigned auto_increment primary key,
    tacho_repair_id int unsigned,
    element_id int unsigned not null,
    foreign key (element_id) references LCD_elements (element_id) on delete cascade,
    foreign key (tacho_repair_id) references tacho_repair (tacho_repair_id) on delete cascade
);

create table BT_board_el_comp_replacement (
	el_comp_replacement_id int unsigned auto_increment primary key,
    tacho_repair_id int unsigned,
    element_id int unsigned not null,
    foreign key (element_id) references BT_board_elements (element_id) on delete cascade,
    foreign key (tacho_repair_id) references tacho_repair (tacho_repair_id) on delete cascade
);

create table SKZI_board_el_comp_replacement (
	el_comp_replacement_id int unsigned auto_increment primary key,
    tacho_repair_id int unsigned,
    element_id int unsigned not null,
    foreign key (element_id) references SKZI_board_elements (element_id) on delete cascade,
    foreign key (tacho_repair_id) references tacho_repair (tacho_repair_id) on delete cascade
);

create table transport_monitoring_board_el_comp_replacement (
	el_comp_replacement_id int unsigned auto_increment primary key,
    tacho_repair_id int unsigned,
    element_id int unsigned not null,
    foreign key (element_id) references transport_monitoring_board_elements (element_id) on delete cascade,
    foreign key (tacho_repair_id) references tacho_repair (tacho_repair_id) on delete cascade
);

create table main_board_missing_el_comp (
	missing_el_comp_id int unsigned auto_increment primary key,
    tacho_repair_id int unsigned,
    element_id int unsigned not null,
    foreign key (element_id) references main_board_elements (element_id) on delete cascade,
    foreign key (tacho_repair_id) references tacho_repair (tacho_repair_id) on delete cascade
);

create table LCD_missing_el_comp (
	missing_el_comp_id int unsigned auto_increment primary key,
    tacho_repair_id int unsigned,
    element_id int unsigned not null,
    foreign key (element_id) references LCD_elements (element_id) on delete cascade,
    foreign key (tacho_repair_id) references tacho_repair (tacho_repair_id) on delete cascade
);

create table BT_board_missing_el_comp (
	missing_el_comp_id int unsigned auto_increment primary key,
    tacho_repair_id int unsigned,
    element_id int unsigned not null,
    foreign key (element_id) references BT_board_elements (element_id) on delete cascade,
    foreign key (tacho_repair_id) references tacho_repair (tacho_repair_id) on delete cascade
);

create table SKZI_board_missing_el_comp (
	missing_el_comp_id int unsigned auto_increment primary key,
    tacho_repair_id int unsigned,
    element_id int unsigned not null,
    foreign key (element_id) references SKZI_board_elements (element_id) on delete cascade,
    foreign key (tacho_repair_id) references tacho_repair (tacho_repair_id) on delete cascade
);

create table transport_monitoring_board_missing_el_comp (
	missing_el_comp_id int unsigned auto_increment primary key,
    tacho_repair_id int unsigned,
    element_id int unsigned not null,
    foreign key (element_id) references transport_monitoring_board_elements (element_id) on delete cascade,
    foreign key (tacho_repair_id) references tacho_repair (tacho_repair_id) on delete cascade
);
-- --------------------------------------------------------------------------------------------------------

create table tf_chain (
    tf_chain_id int unsigned auto_increment primary key,
    tf_1_2_3_id int unsigned,
    tf_4_id int unsigned,
    tf_5_id int unsigned,
    tf_6_id int unsigned,
    tf_10_id int unsigned,
    tf_7_id int unsigned,
    tf_8_9_id int unsigned,
    tf_11_id int unsigned,
    tf_12_13_id int unsigned,
    tf_14_id int unsigned,
    tf_15_16_id int unsigned,
    tf_17_id int unsigned,
    tf_18_id int unsigned,
    tf_19_id int unsigned,
    tf_20_id int unsigned,
    repair_from_seat int unsigned,
    foreign key (tf_1_2_3_id) references tf_1_2_3 (tf_1_2_3_id) on delete cascade,
    foreign key (tf_4_id) references tf_4 (tf_4_id) on delete cascade,
    foreign key (tf_5_id) references tf_5 (tf_5_id) on delete cascade,
    foreign key (tf_6_id) references tf_6 (tf_6_id) on delete cascade,
    foreign key (tf_10_id) references tf_10 (tf_10_id) on delete cascade,
    foreign key (tf_7_id) references tf_7 (tf_7_id) on delete cascade,
    foreign key (tf_8_9_id) references tf_8_9 (tf_8_9_id) on delete cascade,
    foreign key (tf_11_id) references tf_11 (tf_11_id) on delete cascade,
    foreign key (tf_12_13_id) references tf_12_13 (tf_12_13_id) on delete cascade,
    foreign key (tf_14_id) references tf_14 (tf_14_id) on delete cascade,
    foreign key (tf_15_16_id) references tf_15_16 (tf_15_16_id) on delete cascade,
    foreign key (tf_17_id) references tf_17 (tf_17_id) on delete cascade,
    foreign key (tf_18_id) references tf_18 (tf_18_id) on delete cascade,
    foreign key (tf_19_id) references tf_19 (tf_19_id) on delete cascade,
    foreign key (tf_20_id) references tf_20 (tf_20_id) on delete cascade,
    foreign key (repair_from_seat) references seats (seat_number) on delete cascade
);

create table logs_types (
    log_type_id int unsigned auto_increment primary key,
    log_type varchar(30) not null unique
);

create table logs (
    log_id int unsigned auto_increment primary key,
    log_date date not null,
    log_time time not null,
    card_number int unsigned,
    seat_number int unsigned,
    log_type int unsigned not null,
    log_description text not null,
    record_ok boolean,
    foreign key (seat_number) references seats (seat_number) on delete cascade
);

CREATE TABLE FBU_documents (
    FBU_documents_id INT unsigned AUTO_INCREMENT PRIMARY KEY,
    documets_date date not null,
    documets_time time not null,
    XML_document_data LONGBLOB not null,
    DOCX_document_data LONGBLOB not null,
    tacho_model_number int unsigned not null,
    SKZI_model_number int unsigned not null,
    tacho_manufacturer_number int unsigned not null,
    foreign key (tacho_model_number) references tacho_models (tacho_model_number) on delete cascade,
    foreign key (SKZI_model_number) references SKZI_model (SKZI_model_number) on delete cascade,
    foreign key (tacho_manufacturer_number) references tacho_manufacturer (tacho_manufacturer_number) on delete cascade
);

create table FBU_documents_tf_chain (
	FBU_documents_tf_chain_id int unsigned AUTO_INCREMENT PRIMARY KEY,
    FBU_documents_id int unsigned not null,
    tf_chain_id int unsigned not null,
    foreign key (FBU_documents_id) references FBU_documents (FBU_documents_id) on delete cascade,
    foreign key (tf_chain_id) references tf_chain (tf_chain_id) on delete cascade
);

create table tacho_models (
    tacho_model_number int unsigned primary key,
    tacho_model_name text not null,
    is_current boolean not null default false,
    is_archive boolean not null default false,
    date_redact date,
    time_redact time
);

create table SKZI_model (
    SKZI_model_number int unsigned primary key,
    SKZI_model_name text not null,
    is_archive boolean not null default false
);

create table tacho_manufacturer (
    tacho_manufacturer_number int unsigned primary key,
    is_current boolean default false not null
);

-- -----------------------------------------------------------------------------------
insert into types_of_elements (
	type_name
) values
	('Диод'), -- 1
    ('Индуктивность или дроссель'), -- 2
    ('Конденсатор'), -- 3
    ('Микросхема'), -- 4
    ('Резистор'), -- 5
    ('Резонатор'), -- 6
    ('Соединитель'), -- 7
    ('Транзистор'), -- 8
    ('Прочее'), -- 9
    ('Тампер'); -- 10
    

insert into main_board_elements_notes (
	note
) values
	('DC Components Co'), -- 1
    ('NXP / Nexperia'), -- 2
    ('MIC'), -- 3
    ('Yangzhou Yangjie Elctronic'), -- 4
    ('STM'), -- 5
    ('Littelfuse'), -- 6
    ('CODACA'), -- 7
    ('TA-I TECHNOLOGY'), -- 8
    ('CCTC'), -- 9
    ('Yageo'), -- 10
    ('ROQANG'), -- 11
    ('XIANGYEE'), -- 12
    ('SGMICRO'), -- 13
    ('XINLUDA'), -- 14
    ('AEROSEMI'), -- 15
    ('Orient-Chip'), -- 16
    ('Unisonic Technologies'), -- 17
    ('FUJITSU'), -- 18
    ('Runic'), -- 19
    ('Macronix'), -- 20
    ('Sit Electronic Technology'), -- 21
    ('TI'), -- 22
    ('NXP Semicon'), -- 23
    ('XINLUDA'), -- 24
    ('Royal Ohm'); -- 25
    

insert into main_board_elements_names (
	element_type_id,
    element_name,
    note_id
) values
	(1, 'GS1G', 1), -- 1
    (1, 'BAT54C', 2); -- 2
    
    
insert into main_board_elements (
	element_name_id,
    designation
) values
	(1, 'VD1'),
    (1, 'VD2'),
    (2, 'VD3');


insert into LCD_elements_notes (
	note
) values
	('Foryard'), -- 1
    ('KLS'), -- 2
    ('MIC'), -- 3
    ('RoyalOhm'), -- 4
    ('NXP / Nexpria'), -- 5
    ('GUANGDONG XINYUE ELECTRONIC'), -- 6
    ('KLS'); -- 7
    

insert into LCD_elements_names (
	element_type_id,
    element_name,
    note_id
) values
	(1, 'FYLS-0805RGBC-CA', 1), -- 1
    (7, 'L-KLS1-1242E-4.8-22-LT-P-R', 2); -- 2
    
    
insert into LCD_elements (
	element_name_id,
    designation
) values
	(1, 'VD300'),
    (1, 'VD301'),
    (1, 'VD302'),
    (1, 'VD303'),
    (1, 'VD304'),
    (2, 'X302'),
    (2, 'X303');


insert into BT_board_elements_notes (
	note
) values
	('KLS'), -- 1
    ('RoyalOhm'), -- 2
    ('NXP / Nexpria'), -- 3
    ('Panjit'); -- 4
    

insert into BT_board_elements_names (
	element_type_id,
    element_name,
    note_id
) values
	(7, 'L-KLS1-B0208-057M40-T308', 1), -- 1
    (5, '0603WAJ0202T5E (0603-0,1-2kOm+-5%)', 2), -- 2
    (5, '0603WAJ0332T5E (0603-0,1-3,3kOm+-5%)', 2), -- 3
    (5, '0603WAJ0683T5E (0603-0,1-68kOm+-5%)', 2), -- 4
    (8, 'BC847B', 3), -- 5
    (8, 'PJA3401', 4); -- 6
insert into BT_board_elements_names (
	element_type_id,
    element_name
) values
    (9, 'HC-06 V2 4pin Bluetooth Module'); -- 7
    
    
insert into BT_board_elements (
	element_name_id,
    designation
) values
	(1, 'X400'),
    (7, 'U400'),
    (2, 'R3'),
    (3, 'R1'),
    (3, 'R2'),
    (4, 'R4'),
    (5, 'VT1'),
    (6, 'VT2');


insert into SKZI_board_elements_notes (
	note
) values
	('KLS'), -- 1
    ('WE'), -- 2
    ('TCC'); -- 3
    

insert into SKZI_board_elements_names (
	element_type_id,
    element_name,
    note_id
) values
	(7, 'L-KLS1-B0208-057M40-T3R08', 1), -- 1
    (7, 'L-KLS1-XL1-2.00-03-R', 1), -- 2
    (7, '62302021021', 2), -- 3
    (10, 'KLS7-KW10-Z1R045', 1), -- 4
    (3, 'TCC0603X7R104K500CT (0603-X7R-50V-0,1uF+-10%)', 3); -- 5
    
    
insert into SKZI_board_elements (
	element_name_id,
    designation
) values
	(1, 'X200'),
    (2, 'X202'),
    (3, 'X203'),
    (4, null),
    (5, 'C200');


insert into transport_monitoring_board_elements_notes (
	note
) values
	('Foryard'); -- 1
    

insert into transport_monitoring_board_elements_names (
	element_type_id,
    element_name,
    note_id
) values
	(1, 'FYLS-0603UBC', 1), -- 1
    (1, 'FULS-0603UGC', 1), -- 2
    (1, 'FYLA-0603URC', 1), -- 3
    (1, '1N4 14 8WS ф.HOTTECH', 1), -- 4
    (1, 'ESD9L5.OST5G ф. MSKSEMI Semiconductor', 1), -- 5
    (1, 'ESD5Z3.3 ф. SHIKUES', 1); -- 6
    
    
insert into transport_monitoring_board_elements (
	element_name_id,
    designation
) values
	(1, 'VD24'),
    (2, 'VD25'),
    (3, 'VD26'),
    (4, 'VD10'),
    (4, 'VD14'),
    (4, 'VD15'),
    (4, 'VD16'),
    (5, 'VD7'),
    (6, 'VD1'),
    (6, 'VD2'),
    (6, 'VD4'),
    (6, 'VD5'),
    (6, 'VD6'),
    (6, 'VD9');
-- ---------------------------------------------------------------------------


insert into tacho_models (
    tacho_model_number,
    tacho_model_name,
    is_current,
    date_redact,
    time_redact
) values
(
    18,
    'Спутник',
    true,
    '2025-08-12',
    '9:47'
);


insert into SKZI_model (
    SKZI_model_number,
    SKZI_model_name
) values
(
    5,
    '"НКМ-2.11" НДПА.467756.001-01 ТУ'
),
(
    6,
    '"НКМ-2.11 исполнение ИН" НДПА.467756.001-01.02 ТУ'
),
(
    7,
    '"НКМ-2.11 исполнение АВ" НДПА.467756.001-01.01 ТУ'
),
(
    8,
    '"НКМ-3.1 СП" НДПА.467756.001-04.03 ТУ'
),
(
    9,
    '"НКМ-3.1 ИН" НДПА.467756.001-04.02 ТУ'
);


insert into tacho_manufacturer (
    tacho_manufacturer_number,
    is_current
) values
(
    15,
    true
);


insert into statuses (status_name) values
    ('Пройдено'),
    ('Не пройдено'),
    ('Годен'),
    ('Не годен'),
    ('Записано'),
    ('Не записано'),
    ('Пройдено (полный)'),
    ('Пройдено (быстрый)'),
    ('Не пройдено (полный)'),
    ('Не пройдено (быстрый)');


insert into seats (hashed_password, min_time, seat_name) values
    (
        '7026f7f0c4527090294b8850e888dcf64b5705534c0ee13f19e31cfc62da973e',
        450,
        'Приклеивание (стекла к панели)'
    ),
    (
        'dcbbe789d40b77fdc737b16bc2e32c32b06b7c90c95e873252d37edc15fd5281',
        600,
        'Свинчивание (блок индикации и управления)'
    ),
    (
        '6ecd5a0c6bfcfb4b782068e24750c1c19ff6b9b877b06a4d5732ca12f8e2d2cf',
        60,
        'Свинчивание (термопринтер)'
    ),
    (
        '358710bcbb41a226785b9dedb43c7f85987d6b727db23f6c28442ed20b41c11e',
        10,
        'Программирование (запись загрузчика)'
    ),
    (
        'd29587df7fe4d34abad28c538a7e4c6543fddcab3b3093bcfb21f04ff272f5c5',
        120,
        'Свинчивание (установка считывателей и платы)'
    ),
    (
        'a0a4d5dd77c5c54e56dab28dbef82f326514a984fb5f18e07fc5a8e4448b8674',
        10,
        'Сборка (установка лицевой панели)'
    ),
    (
        '1fd2861f9414f227820bdf9d91abe021575c8038525c6dc32737f11043d6adb5',
        10,
        'Запрессовка пружин'
    ),
    (
        '59d23ef18e3b5ff24f26d8cb55ad8961b9b5c7b280aad1cc79258820fa6beac5',
        10,
        'Программирование внешнего модуля'
    ),
    (
        '340294de3dcd5aa41774840afa8856ab139c53d6cad05940a0f6292f084a78fc',
        10,
        'КФ внешнего модуля'
    ),
    (
        'f0fdefad4380606a7ad8ee273aa07e98b2a94da6e3a409a64d85d7fb9362ddf3',
        10,
        'Программирование (запись ПО)'
    ),
    (
        'b69d15fea9355a47a8fff342aa2d13ed22a009184ecafc86fcff7b9eb7e7b573',
        10,
        'Свинчивание'
    ),
    (
        'c4eb7e3c936785773da2e09d63a48dce324e94159b5b4f55f230593ad582e0e6',
        10,
        'Контроль функционирования (КФ)'
    ),
    (
        '816f26bb30029144f85e5f8763ba41f71be12e952c325bcbc3a273658f6f40a7',
        10,
        'Контроль функционирования (КФ)'
    ),
    (
        '4de7421bb5696c248b34475ead2350f4fa4f35739355cb650378a2f02f654671',
        10,
        'Свинчивание'
    ),
    (
        'dedf6064814fc0f9d1582846f4e4f4d6669027215629afe2617d5e8cc69cb6d0',
        10,
        'ПСИ'
    ),
    (
        '5a290f244c2ab3e99cf51363173196ffc8b32d0682c1f5a7d9a464cf6da96a45',
        10,
        'ПСИ'
    ),
    (
        'c296a12c41b0fd635bf5fc673787086b47e8d64a3b01bec874766a4eb35a4fda',
        10,
        'Пломбирование'
    ),
    (
        'df590918bd8bb0e3f6f46761e24fbe5be21732a4d88b1c60aa855471737e4328',
        10,
        'Поверка'
    ),
    (
        '9d81cc1082fdf0c0006ad9101b7f379c0fbd478267891294a0cebdd1bf903faa',
        10,
        'Предпродажная подготовка'
    ),
    (
        'dfaa842beb904eb0c716c8a41356b04b0c179d5d5009b2293fb20968eed5802b',
        10,
        'Комплектование'
    ),
    (
        '352d74db06e47fe1707a86fc8fb9c8d237013b5cb18bd0aa24bda287f4746b6c',
        10,
        'Доработка'
    );
insert into seats (seat_number, seat_name) values
    (
        100,
        'Место бригадира'
    );


insert into seats_chain (seat_number) values
    (1), (4), (8);
insert into seats_chain (seat_number, previous_seat_number) values
    (2, 1), (3, 2), (5, 4), (6, 5), (6, 3), (10, 6), (7, 10), (11, 7), (11, 9), (9, 8),
    (12, 11), (13, 11), (15, 14), (16, 14), (18, 17), (19, 18), (20, 19);
insert into seats_chain (seat_number, previous_seat_number, one_type_seats) values
    (14, 12, true), (14, 13, true), (17, 15, true), (17, 16, true);


insert into persons (card_number, surname_name) values (15215358, 'Горлов Кирилл Дмитриевич');
insert into persons (card_number, surname_name, author_card_number) values
    (16662831, 'Губин Егор Вячеславович', 16662831),
    (16860265, 'Жаров Дмитрий Сергеевич', 16662831),
    (3734075, 'Губанкова Александра Юрьевна', 16662831),
    (16521016, 'Перевалов Олег Иванович', 16662831),
-- Рабочие
    (5915670, 'Марудина Татьяна Александровна', 16662831),
    (1135010, 'Ильюшкина Галина Афанасьевна', 16662831),
    (1159502, 'Завертяева Мария Сергеевна', 16662831),
    (16906673, 'Конюхова Елена Анатольевна', 16662831);


insert into access_rights (rights, access_rights_description) values
    (0, 'Снят'),
    (1, 'Работник'),
    (2, 'Статрший смены'),
    (3, 'Бригадир'),
    (255, 'Пропуск не действителен');


insert into seats_persons (card_number, seat_number, access_rights) values (15215358, 1, 3);
insert into seats_persons (card_number, seat_number, access_rights) values (16662831, 1, 3);
insert into seats_persons (card_number, seat_number, access_rights) values (15215358, 2, 3);
insert into seats_persons (card_number, seat_number, access_rights) values (16662831, 2, 3);
insert into seats_persons (card_number, seat_number, access_rights) values (15215358, 3, 3);
insert into seats_persons (card_number, seat_number, access_rights) values (16662831, 3, 3);
insert into seats_persons (card_number, seat_number, access_rights) values (15215358, 4, 3);
insert into seats_persons (card_number, seat_number, access_rights) values (16662831, 4, 3);
insert into seats_persons (card_number, seat_number, access_rights) values (15215358, 5, 3);
insert into seats_persons (card_number, seat_number, access_rights) values (16662831, 5, 3);
insert into seats_persons (card_number, seat_number, access_rights) values (15215358, 6, 3);
insert into seats_persons (card_number, seat_number, access_rights) values (16662831, 6, 3);
insert into seats_persons (card_number, seat_number, access_rights) values (15215358, 7, 3);
insert into seats_persons (card_number, seat_number, access_rights) values (16662831, 7, 3);
insert into seats_persons (card_number, seat_number, access_rights) values (15215358, 8, 3);
insert into seats_persons (card_number, seat_number, access_rights) values (16662831, 8, 3);
insert into seats_persons (card_number, seat_number, access_rights) values (15215358, 9, 3);
insert into seats_persons (card_number, seat_number, access_rights) values (16662831, 9, 3);
insert into seats_persons (card_number, seat_number, access_rights) values (15215358, 10, 3);
insert into seats_persons (card_number, seat_number, access_rights) values (16662831, 10, 3);
insert into seats_persons (card_number, seat_number, access_rights) values (15215358, 11, 3);
insert into seats_persons (card_number, seat_number, access_rights) values (16662831, 11, 3);
insert into seats_persons (card_number, seat_number, access_rights) values (15215358, 12, 3);
insert into seats_persons (card_number, seat_number, access_rights) values (16662831, 12, 3);
insert into seats_persons (card_number, seat_number, access_rights) values (15215358, 13, 3);
insert into seats_persons (card_number, seat_number, access_rights) values (16662831, 13, 3);
insert into seats_persons (card_number, seat_number, access_rights) values (15215358, 14, 3);
insert into seats_persons (card_number, seat_number, access_rights) values (16662831, 14, 3);
insert into seats_persons (card_number, seat_number, access_rights) values (15215358, 15, 3);
insert into seats_persons (card_number, seat_number, access_rights) values (16662831, 15, 3);
insert into seats_persons (card_number, seat_number, access_rights) values (15215358, 16, 3);
insert into seats_persons (card_number, seat_number, access_rights) values (16662831, 16, 3);
insert into seats_persons (card_number, seat_number, access_rights) values (15215358, 17, 3);
insert into seats_persons (card_number, seat_number, access_rights) values (16662831, 17, 3);
insert into seats_persons (card_number, seat_number, access_rights) values (15215358, 18, 3);
insert into seats_persons (card_number, seat_number, access_rights) values (16662831, 18, 3);
insert into seats_persons (card_number, seat_number, access_rights) values (15215358, 19, 3);
insert into seats_persons (card_number, seat_number, access_rights) values (16662831, 19, 3);
insert into seats_persons (card_number, seat_number, access_rights) values (15215358, 20, 3);
insert into seats_persons (card_number, seat_number, access_rights) values (16662831, 20, 3);
insert into seats_persons (card_number, seat_number, access_rights) values (15215358, 21, 3);
insert into seats_persons (card_number, seat_number, access_rights) values (16662831, 21, 3);
insert into seats_persons (card_number, seat_number, access_rights) values (15215358, 100, 3);
insert into seats_persons (card_number, seat_number, access_rights) values (16662831, 100, 3);

insert into seats_persons (card_number, seat_number, access_rights) values (16860265, 1, 3);
insert into seats_persons (card_number, seat_number, access_rights) values (16860265, 2, 3);
insert into seats_persons (card_number, seat_number, access_rights) values (16860265, 3, 3);
insert into seats_persons (card_number, seat_number, access_rights) values (16860265, 4, 3);
insert into seats_persons (card_number, seat_number, access_rights) values (16860265, 5, 3);
insert into seats_persons (card_number, seat_number, access_rights) values (16860265, 6, 3);
insert into seats_persons (card_number, seat_number, access_rights) values (16860265, 7, 3);
insert into seats_persons (card_number, seat_number, access_rights) values (16860265, 8, 3);
insert into seats_persons (card_number, seat_number, access_rights) values (16860265, 9, 3);
insert into seats_persons (card_number, seat_number, access_rights) values (16860265, 10, 3);
insert into seats_persons (card_number, seat_number, access_rights) values (16860265, 11, 3);
insert into seats_persons (card_number, seat_number, access_rights) values (16860265, 12, 3);
insert into seats_persons (card_number, seat_number, access_rights) values (16860265, 13, 3);
insert into seats_persons (card_number, seat_number, access_rights) values (16860265, 14, 3);
insert into seats_persons (card_number, seat_number, access_rights) values (16860265, 15, 3);
insert into seats_persons (card_number, seat_number, access_rights) values (16860265, 16, 3);
insert into seats_persons (card_number, seat_number, access_rights) values (16860265, 17, 3);
insert into seats_persons (card_number, seat_number, access_rights) values (16860265, 18, 3);
insert into seats_persons (card_number, seat_number, access_rights) values (16860265, 19, 3);
insert into seats_persons (card_number, seat_number, access_rights) values (16860265, 20, 3);
insert into seats_persons (card_number, seat_number, access_rights) values (16860265, 21, 3);
insert into seats_persons (card_number, seat_number, access_rights) values (16860265, 100, 3);

insert into seats_persons (card_number, seat_number, access_rights) values (3734075, 1, 3);
insert into seats_persons (card_number, seat_number, access_rights) values (3734075, 2, 3);
insert into seats_persons (card_number, seat_number, access_rights) values (3734075, 3, 3);
insert into seats_persons (card_number, seat_number, access_rights) values (3734075, 4, 3);
insert into seats_persons (card_number, seat_number, access_rights) values (3734075, 5, 3);
insert into seats_persons (card_number, seat_number, access_rights) values (3734075, 6, 3);
insert into seats_persons (card_number, seat_number, access_rights) values (3734075, 7, 3);
insert into seats_persons (card_number, seat_number, access_rights) values (3734075, 8, 3);
insert into seats_persons (card_number, seat_number, access_rights) values (3734075, 9, 3);
insert into seats_persons (card_number, seat_number, access_rights) values (3734075, 10, 3);
insert into seats_persons (card_number, seat_number, access_rights) values (3734075, 11, 3);
insert into seats_persons (card_number, seat_number, access_rights) values (3734075, 12, 3);
insert into seats_persons (card_number, seat_number, access_rights) values (3734075, 13, 3);
insert into seats_persons (card_number, seat_number, access_rights) values (3734075, 14, 3);
insert into seats_persons (card_number, seat_number, access_rights) values (3734075, 15, 3);
insert into seats_persons (card_number, seat_number, access_rights) values (3734075, 16, 3);
insert into seats_persons (card_number, seat_number, access_rights) values (3734075, 17, 3);
insert into seats_persons (card_number, seat_number, access_rights) values (3734075, 18, 3);
insert into seats_persons (card_number, seat_number, access_rights) values (3734075, 19, 3);
insert into seats_persons (card_number, seat_number, access_rights) values (3734075, 20, 3);
insert into seats_persons (card_number, seat_number, access_rights) values (3734075, 21, 3);
insert into seats_persons (card_number, seat_number, access_rights) values (3734075, 100, 3);

insert into seats_persons (card_number, seat_number, access_rights) values (16521016, 1, 3);
insert into seats_persons (card_number, seat_number, access_rights) values (16521016, 2, 3);
insert into seats_persons (card_number, seat_number, access_rights) values (16521016, 3, 3);
insert into seats_persons (card_number, seat_number, access_rights) values (16521016, 4, 3);
insert into seats_persons (card_number, seat_number, access_rights) values (16521016, 5, 3);
insert into seats_persons (card_number, seat_number, access_rights) values (16521016, 6, 3);
insert into seats_persons (card_number, seat_number, access_rights) values (16521016, 7, 3);
insert into seats_persons (card_number, seat_number, access_rights) values (16521016, 8, 3);
insert into seats_persons (card_number, seat_number, access_rights) values (16521016, 9, 3);
insert into seats_persons (card_number, seat_number, access_rights) values (16521016, 10, 3);
insert into seats_persons (card_number, seat_number, access_rights) values (16521016, 11, 3);
insert into seats_persons (card_number, seat_number, access_rights) values (16521016, 12, 3);
insert into seats_persons (card_number, seat_number, access_rights) values (16521016, 13, 3);
insert into seats_persons (card_number, seat_number, access_rights) values (16521016, 14, 3);
insert into seats_persons (card_number, seat_number, access_rights) values (16521016, 15, 3);
insert into seats_persons (card_number, seat_number, access_rights) values (16521016, 16, 3);
insert into seats_persons (card_number, seat_number, access_rights) values (16521016, 17, 3);
insert into seats_persons (card_number, seat_number, access_rights) values (16521016, 18, 3);
insert into seats_persons (card_number, seat_number, access_rights) values (16521016, 19, 3);
insert into seats_persons (card_number, seat_number, access_rights) values (16521016, 20, 3);
insert into seats_persons (card_number, seat_number, access_rights) values (16521016, 21, 3);
insert into seats_persons (card_number, seat_number, access_rights) values (16521016, 100, 3);
-- Рабочие!!!
insert into seats_persons (card_number, seat_number, access_rights) values (5915670, 4, 1);
insert into seats_persons (card_number, seat_number, access_rights) values (5915670, 10, 1);
insert into seats_persons (card_number, seat_number, access_rights) values (5915670, 12, 1);
insert into seats_persons (card_number, seat_number, access_rights) values (5915670, 13, 1);
insert into seats_persons (card_number, seat_number, access_rights) values (5915670, 15, 1);
insert into seats_persons (card_number, seat_number, access_rights) values (5915670, 16, 1);
insert into seats_persons (card_number, seat_number, access_rights) values (5915670, 19, 1);
insert into seats_persons (card_number, seat_number, access_rights) values (1135010, 2, 1);
insert into seats_persons (card_number, seat_number, access_rights) values (1135010, 5, 1);
insert into seats_persons (card_number, seat_number, access_rights) values (1159502, 5, 2);
insert into seats_persons (card_number, seat_number, access_rights) values (16906673, 1, 1);
insert into seats_persons (card_number, seat_number, access_rights) values (16906673, 2, 1);
insert into seats_persons (card_number, seat_number, access_rights) values (16906673, 3, 1);
insert into seats_persons (card_number, seat_number, access_rights) values (16906673, 5, 1);
insert into seats_persons (card_number, seat_number, access_rights) values (16906673, 6, 1);
insert into seats_persons (card_number, seat_number, access_rights) values (16906673, 12, 1);


insert into part_numbers (part_number) values ('21.3840000-10');
insert into part_numbers (part_number) values ('21.3840000-11');
insert into part_numbers (part_number) values ('21.3840000-12');
insert into part_numbers (part_number) values ('21.3840000-13');


insert into component_names (component_name) values ('Панель лицевая_АЭ');
insert into components (component_name_id, component_number, amount, QR_code_info) values (1, '21.3840501-10', 100, 2000000168142);
insert into components_variations (part_number_id, component_id) values (2, 1);
insert into components_variations (part_number_id, component_id) values (1, 1);
insert into components (component_name_id, component_number, amount, QR_code_info) values (1, '21.3840501-10Д', 100, 2000000168166);
insert into components_variations (part_number_id, component_id) values (4, 2);
insert into components_variations (part_number_id, component_id) values (3, 2);
insert into component_names (component_name) values ('Кнопка_АЭ');
insert into components (component_name_id, component_number, amount, QR_code_info) values (2, '21.3840530-10', 100, 2000000168975);
insert into components_variations (part_number_id, component_id) values (2, 3);
insert into components_variations (part_number_id, component_id) values (1, 3);
insert into components (component_name_id, component_number, amount, QR_code_info) values (2, '21.3840530-10Д', 100, 2000000168968);
insert into components_variations (part_number_id, component_id) values (3, 4);
insert into components_variations (part_number_id, component_id) values (4, 4);
insert into components (component_name_id, component_number, amount, QR_code_info) values (2, '21.3840530-01-10', 100, 2000000169071);
insert into components_variations (part_number_id, component_id) values (2, 5);
insert into components_variations (part_number_id, component_id) values (1, 5);
insert into components (component_name_id, component_number, amount, QR_code_info) values (2, '21.3840530-01-10Д', 100, 2000000169064);
insert into components_variations (part_number_id, component_id) values (4, 6);
insert into components_variations (part_number_id, component_id) values (3, 6);
insert into components (component_name_id, component_number, amount, QR_code_info) values (2, '21.3840530-02-10', 100, 2000000169057);
insert into components_variations (part_number_id, component_id) values (2, 7);
insert into components_variations (part_number_id, component_id) values (1, 7);
insert into components (component_name_id, component_number, amount, QR_code_info) values (2, '21.3840530-02-10Д', 100, 2000000169040);
insert into components_variations (part_number_id, component_id) values (4, 8);
insert into components_variations (part_number_id, component_id) values (3, 8);
insert into components (component_name_id, component_number, amount, QR_code_info) values (2, '21.3840530-03-10', 100, 2000000169033);
insert into components_variations (part_number_id, component_id) values (2, 9);
insert into components_variations (part_number_id, component_id) values (1, 9);
insert into components (component_name_id, component_number, amount, QR_code_info) values (2, '21.3840530-03-10Д', 100, 2000000169026);
insert into components_variations (part_number_id, component_id) values (4, 10);
insert into components_variations (part_number_id, component_id) values (3, 10);
insert into components (component_name_id, component_number, amount, QR_code_info) values (2, '21.3840530-04-10', 100, 2000000169019);
insert into components_variations (part_number_id, component_id) values (2, 11);
insert into components_variations (part_number_id, component_id) values (1, 11);
insert into components (component_name_id, component_number, amount, QR_code_info) values (2, '21.3840530-04-10Д', 100, 2000000169002);
insert into components_variations (part_number_id, component_id) values (4, 12);
insert into components_variations (part_number_id, component_id) values (3, 12);
insert into components (component_name_id, component_number, amount, QR_code_info) values (2, '21.3840530-05-10', 100, 2000000168999);
insert into components_variations (part_number_id, component_id) values (2, 13);
insert into components_variations (part_number_id, component_id) values (1, 13);
insert into components (component_name_id, component_number, amount, QR_code_info) values (2, '21.3840530-05-10Д', 100, 2000000168982);
insert into components_variations (part_number_id, component_id) values (4, 14);
insert into components_variations (part_number_id, component_id) values (3, 14);
insert into component_names (component_name) values ('Кнопка касеты принтера_АЭ');
insert into components (component_name_id, component_number, amount, QR_code_info) values (3, '21.3840521-10Д', 100, 2000000169101);
insert into components_variations (part_number_id, component_id) values (4, 15);
insert into components_variations (part_number_id, component_id) values (3, 15);
insert into component_names (component_name) values ('Крышка корпуса');
insert into components (component_name_id, component_number, amount, QR_code_info) values (4, '21.3840701-10Д', 100, 2000000181714);
insert into components_variations (part_number_id, component_id) values (4, 16);
insert into component_names (component_name) values ('Корпус нижний_АЭ');
insert into components (component_name_id, component_number, amount, QR_code_info) values (5, '21.3840601-10', 100, 2000000169392);
insert into components_variations (part_number_id, component_id) values (2, 17);
insert into components_variations (part_number_id, component_id) values (1, 17);
insert into components (component_name_id, component_number, amount, QR_code_info) values (5, '21.3840601-10Д', 100, 2000000169408);
insert into components_variations (part_number_id, component_id) values (4, 18);
insert into components_variations (part_number_id, component_id) values (3, 18);
insert into component_names (component_name) values ('Блок мониторинга ТС и связи');
insert into components (component_name_id, component_number, amount, QR_code_info) values (6, 'MT-900AE', 100, 2000000169347);
insert into components_variations (part_number_id, component_id) values (3, 19);
insert into components_variations (part_number_id, component_id) values (2, 19);
insert into component_names (component_name) values ('Крышка НКМ');
insert into components (component_name_id, component_number, amount, QR_code_info) values (7, '21.3840002-10', 100, 2000000168937);
insert into components_variations (part_number_id, component_id) values (2, 20);
insert into components_variations (part_number_id, component_id) values (1, 20);
insert into components (component_name_id, component_number, amount, QR_code_info) values (7, '21.3840002-10Д', 100, 2000000168913);
insert into components_variations (part_number_id, component_id) values (4, 21);
insert into components_variations (part_number_id, component_id) values (3, 21);
insert into component_names (component_name) values ('Крышка внешнего модуля');
insert into components (component_name_id, component_number, amount, QR_code_info) values (8, '21.3840001-10', 100, 2000000168951);
insert into components_variations (part_number_id, component_id) values (2, 22);
insert into components_variations (part_number_id, component_id) values (1, 22);
insert into component_names (component_name) values ('Заглушка USB');
insert into components (component_name_id, component_number, amount, QR_code_info) values (9, '21.3840502-10', 100, 2000000168173);
insert into components_variations (part_number_id, component_id) values (2, 23);
insert into components_variations (part_number_id, component_id) values (1, 23);
insert into components (component_name_id, component_number, amount, QR_code_info) values (9, '21.3840502-10Д', 100, 2000000168227);
insert into components_variations (part_number_id, component_id) values (4, 24);
insert into components_variations (part_number_id, component_id) values (3, 24);
insert into component_names (component_name) values ('Стекло');
insert into components (component_name_id, component_number, amount, QR_code_info) values (10, '21.3840503-10', 100, 2000000168203);
insert into component_names (component_name) values ('DIN7981 Саморез с полукруглой головкой');
insert into components (component_name_id, component_number, amount, QR_code_info) values (11, 'PH d2.9x9.5 Цинк (CN)', 100, 2000000181608);
insert into component_names (component_name) values ('Шлейф');
insert into components (component_name_id, component_number, amount, QR_code_info) values (12, 'JK_L-KLS17-FFC-0.5-22P-L030A4/8_KLS', 100, 2000000157153);
insert into component_names (component_name) values ('Термопринтер');
insert into components (component_name_id, component_number, amount, QR_code_info) values (13, 'fujitsu ftp-62dmcl101=PT48GF-8', 100, 2000000151106);
insert into components (component_name_id, component_number, amount, QR_code_info) values (11, 'PH d2.2x6.5 Цинк', 100, 2000000163734);
insert into component_names (component_name) values ('Считыватель смарт-карты');
insert into components (component_name_id, component_number, amount, QR_code_info) values (14, 'RU618 CIXI', 100, 2000000143064);
insert into component_names (component_name) values ('Пружина');
insert into components (component_name_id, component_number, amount, QR_code_info) values (15, '21.3840702-10', 100, 2000000165318);
insert into component_names (component_name) values ('Кабельная сборка');
insert into components (component_name_id, component_number, amount, QR_code_info) values (16, 'GKZS-FAKRACZG-RG174-MMCXWG-85_GOA', 100, 2000000163710);
insert into component_names (component_name) values ('Крышка разъемов внешнего модуля');
insert into components (component_name_id, component_number, amount, QR_code_info) values (17, '21.3840703-10', 100, 2000000169507);
insert into component_names (component_name) values ('Крышка SIM');
insert into components (component_name_id, component_number, amount, QR_code_info) values (18, '21.3840704-10', 100, 2000000169330);
insert into components (component_name_id, component_number, amount, QR_code_info) values (11, 'PH d2.9х16', 100, 2000000163765);
insert into component_names (component_name) values ('DIN7985 PH М3х10 Винт с полукруглой головкой');
insert into components (component_name_id, component_number, amount, QR_code_info) values (19, 'PH М3х10', 100, 2000000170114);
insert into component_names (component_name) values ('Пломба малая красная');
insert into components (component_name_id, component_number, amount, QR_code_info) values (20, '330072', 100, 2000000002941);
insert into component_names (component_name) values ('Блок тахографического мониторинга (ФС)');
insert into components (component_name_id, component_number, amount, QR_code_info) values (21, '21.3840610019 лит. 5', 100, 2000000192000);
insert into components_variations (part_number_id, component_id) values (1, 40);
insert into components_variations (part_number_id, component_id) values (2, 40);
insert into components_variations (part_number_id, component_id) values (3, 40);
insert into components_variations (part_number_id, component_id) values (4, 40);
insert components_seats (component_id, seat_number, max_amount) values (1, 1, 1);
insert components_seats (component_id, seat_number, max_amount) values (2, 1, 1);
insert components_seats (component_id, seat_number, max_amount) values (25, 1, 1);
insert components_seats (component_id, seat_number, max_amount) values (3, 2, 1);
insert components_seats (component_id, seat_number, max_amount) values (4, 2, 1);
insert components_seats (component_id, seat_number, max_amount) values (5, 2, 1);
insert components_seats (component_id, seat_number, max_amount) values (6, 2, 1);
insert components_seats (component_id, seat_number, max_amount) values (7, 2, 1);
insert components_seats (component_id, seat_number, max_amount) values (8, 2, 1);
insert components_seats (component_id, seat_number, max_amount) values (9, 2, 1);
insert components_seats (component_id, seat_number, max_amount) values (10, 2, 1);
insert components_seats (component_id, seat_number, max_amount) values (11, 2, 1);
insert components_seats (component_id, seat_number, max_amount) values (12, 2, 1);
insert components_seats (component_id, seat_number, max_amount) values (13, 2, 1);
insert components_seats (component_id, seat_number, max_amount) values (14, 2, 1);
insert components_seats (component_id, seat_number, max_amount) values (26, 2, 2);
insert components_seats (component_id, seat_number, max_amount) values (27, 2, 2);
insert components_seats (component_id, seat_number, max_amount) values (31, 2, 6);
insert components_seats (component_id, seat_number, max_amount) values (28, 3, 1);
insert components_seats (component_id, seat_number, max_amount) values (29, 3, 2);
insert components_seats (component_id, seat_number, max_amount) values (15, 3, 1);
insert into component_names (component_name) values ('Держатель рулона_АЭ');
insert into components (component_name_id, component_number, amount, QR_code_info) values (21, '21.3840522-10', 100, 2000000169095);
insert into components_variations (part_number_id, component_id) values (2, 38);
insert into components_variations (part_number_id, component_id) values (1, 38);
insert into components (component_name_id, component_number, amount, QR_code_info) values (21, '21.3840522-10Д', 100, 2000000169088);
insert into components_variations (part_number_id, component_id) values (3, 39);
insert into components_variations (part_number_id, component_id) values (4, 39);
insert components_seats (component_id, seat_number, max_amount) values (38, 3, 1);
insert components_seats (component_id, seat_number, max_amount) values (39, 3, 1);
insert components_seats (component_id, seat_number, max_amount) values (17, 5, 1);
insert components_seats (component_id, seat_number, max_amount) values (18, 5, 1);
insert components_seats (component_id, seat_number, max_amount) values (16, 7, 1);
insert components_seats (component_id, seat_number, max_amount) values (33, 11, 1);
insert components_seats (component_id, seat_number, max_amount) values (19, 11, 1);
insert components_seats (component_id, seat_number, max_amount) values (20, 14, 1);
insert components_seats (component_id, seat_number, max_amount) values (21, 14, 1);
insert components_seats (component_id, seat_number, max_amount) values (22, 14, 1);
insert components_seats (component_id, seat_number, max_amount) values (23, 14, 1);
insert components_seats (component_id, seat_number, max_amount) values (24, 14, 1);
insert components_seats (component_id, seat_number, max_amount) values (30, 5, 2);
insert components_seats (component_id, seat_number, max_amount) values (26, 5, 6);
insert components_seats (component_id, seat_number, max_amount) values (16, 7, 1);
insert components_seats (component_id, seat_number, max_amount) values (31, 7, 2);
insert components_seats (component_id, seat_number, max_amount) values (32, 7, 1);
insert components_seats (component_id, seat_number, max_amount) values (29, 11, 4);
insert components_seats (component_id, seat_number, max_amount) values (34, 14, 1);
insert components_seats (component_id, seat_number, max_amount) values (26, 14, 6);
insert components_seats (component_id, seat_number, max_amount) values (35, 14, 2);
insert components_seats (component_id, seat_number, max_amount) values (37, 17, 3);
insert components_seats (component_id, seat_number, max_amount) values (36, 14, 3);
insert components_seats (component_id, seat_number, max_amount) values (40, 5, 1);


insert into tables (table_name) values ("VUIDENTIIFICATION");
insert into tables (table_name) values ("TABLE_CODE_NKM_FBU");
insert into tables (table_name) values ("LOGO_128X64");
insert into tables (table_name) values ("LOGO_64X51");
insert into tables (table_name) values ("SETTINGS_LEDS");
insert into tables (table_name) values ("PRE_SALE_SETTINGS");
insert into tables (table_name) values ("POST_SALE_SETTINGS");
insert into tables (table_name) values ("EXT_MODULE");
insert into tables (table_name) values ("SETTINGS_BATTERY");
insert into tables (table_name) values ("TACHO_SETTINGS");
insert into tables (table_name) values ("SETTINGS_ADVERTISING_LINE");
insert into tables (table_name) values ("VUCALIBRATION_RECORD");


insert into defectivity_types (defectivity_type) values ("Поставщик");
insert into defectivity_types (defectivity_type) values ("Производство");


insert into tf_12_13_flash_memory_errors (error_id, error_description) values
	('0x00000001', 'Ошибка инициализации'),
    ('0x00000002', 'Ошибка чтения MX25R6435'),
    ('0x00000003', 'Ошибка записи MX25R6435'),
    ('0x00000004', 'Ошибка стирания сектора MX25R6435'),
    ('0x00000005', 'Ошибка записи команды MX25R6435'),
    ('0x00000006', 'Ошибка параметра команды MX25R6435');
    
    
insert into tf_12_13_FRAM_errors (error_id, error_description) values
	('0x00000001', 'Ошибка инициализации'),
    ('0x00000002', 'Ошибка чтения MX25R6435'),
    ('0x00000003', 'Ошибка записи MX25R6435'),
    ('0x00000004', 'Ошибка стирания сектора MX25R6435'),
    ('0x00000005', 'Ошибка записи команды MX25R6435'),
    ('0x00000006', 'Ошибка параметра команды MX25R6435');
    

insert into tf_12_13_SKZI_errors (error_id, error_description) values
	('0x00000012', 'Команда не поддерживается'),
	('0x00000013', 'Ошибка проверки LRC. Требуется повторный запуск команды на исполнение'),
	('0x00000015', 'Неверные входные данные'),
	('0x00000016', 'Неверная длина входных данных'),
	('0x00000018', 'Неверный диапазон времени'),
	('0x00000021', 'Неверное состояние СКЗИ'),
	('0x00000027', 'Конец отчета (Нет запрошенных данных)'),
	('0x00000031', 'Неверный формат сертификата. FT12'),
	('0x00000032', 'Срок действия сертификата истёк. FT12'),
	('0x00000033', 'Подпись сертификата не верна. FT12'),
    ('0x00000034', 'Неизвестный ключ'),
	('0x0000003F', 'Внутренняя неисправность СКЗИ'),
	('0x00000040', 'Не проведена аутентификация с данной картой. FT1F'),
	('0x00000041', 'Нет привилегий доступа к данной операции. FT1F'),
	('0x00000042', 'Нарушена последовательность команд аутентификации. FT12'),
	('0x00000045', 'Команда не может исполняться во время движения ТС'),
	('0x00000070', 'Требуется новый ключ для проверки сертификатов. Подать сертификат нового ключа, подписанный старым ключом'),
	('0x00000071', 'Нет готовности СКЗИ. Требуется повторный запуск команды на исполнение'),
	('0x00000080', 'Не было команды ввода карты с данным номером в тахограф. FT10'),
	('0x00000081', 'Карта с таким номером или в таком слоте уже была вставлена'),
	('0x00000095', 'Данные с ГЛОНАСС GPS верны'),
	('0x000000A6', 'Данные с ГЛОНАСС GPS не верны'),
	('0x000000A0', 'Сертификаты не загружены'),
	('0x000000A1', 'Транспортное средство уже активировано'),
	('0x000000A2', 'Ошибка активации транспортного средства'),
	('0x000000E0', 'Рассинхронизация между элементами СКЗИ'),
	('0x000000E1', 'Внутрення ошибка СКЗИ при передаче данных'),
	('0x000000E2', 'Недостаточное напряжение питания СКЗИ'),
	('0x000000F0', 'Последний месяц работы СКЗИ'),
	('0x000000F1', 'Превышен таймаут сигнала INT'),
	('0x000000F2', 'Получен ответ не в том протоколе, в котором была отправлена команда'),
	('0x000000F3', 'Срок действия сертификата НКМ окончен'),
    ('0x000000F4', 'СКЗИ заблокирован'),
	('0x000000F5', 'Ошибка инициализации при включении питания'),
	('0x000000F6', 'Ошибка таймаута SPI или I2C СКЗИ'),
	('0x000000F7', 'Ошибка состояния оборудования СКЗИ'),
	('0x000000F8', 'Ошибка захвата xMutexNKM СКЗИ'),
	('0x000000F9', 'Превышен таймаут приёма данных'),
	('0x00000100', 'Ошибка начального пуска НКМ'),
	('0x000001FF', 'Произведена перезагрузка НКМ'),
	('0x0000FFFF', 'Неизвестная ошибка НКМ');

    
insert into tf_12_13_card_reader_errors (error_id, error_description) values
	('0x00008FF9', 'Неизвестный тип карты'),
    ('0x00008FFA', 'Ошибка таймаута приёма по USART от карты'),
    ('0x00008FFB', 'Карта не ответила на сброс или карта вставлена не той стороной'),
    ('0x00008FFD', 'Карта не зафиксирована в картридере'),
    ('0x00008FFC', 'Обмен PPSS, PPS0, PPS1 и PCK не удался'),
    ('0x0000FFFF', 'Неизвестная ошибка карты'),
    ('0x00008FEB', 'Карта не извлеклась');
    

insert into tf_12_13_front_RS232_errors (error_id, error_description) values
	('0x00000001', 'Ошибка выбора типа теста. Необходимо выбрать тест RS232 Front (0x0C), либо тест RS232 внешний модуль (0x0E)'),
    ('0x00000002', 'Ошибка заданной скорости. Запуск на требуемой скорости невозможен'),
    ('0x00000004', 'Ошибка размера. Некорректный размер отправленных данных'),
    ('0x00000008', 'Ошибка отправки данных на скорости 9600Bd. Front RS232'),
    ('0x00000010', 'Ошибка приёма данных на скорости 9600Bd. Отправленные данные не соответствуют полученным. Front RS232'),
    ('0x00000020', 'Ошибка отправки данных на скорости 19200Bd. Front RS232'),
    ('0x00000040', 'Ошибка приёма данных на скорости 19200Bd. Отправленные данные не соответствуют полученным. Front RS232'),
    ('0x00000080', 'Ошибка отправки данных на скорости 38400Bd. Front RS232'),
    ('0x00000100', 'Ошибка приёма данных на скорости 38400Bd. Отправленные данные не соответствуют полученным. Front RS232'),
    ('0x00000200', 'Ошибка отправки данных на скорости 57600Bd. Front RS232'),
    ('0x00000400', 'Ошибка приёма данных на скорости 57600Bd. Отправленные данные не соответствуют полученным. Front RS232'),
    ('0x00000800', 'Ошибка отправки данных на скорости 115200Bd. Front RS232'),
    ('0x00001000', 'Ошибка приёма данных на скорости 115200Bd. Отправленные данные не соответствуют полученным. Front RS232');
    

insert into tf_12_13_KLine_errors (error_id, error_description) values
	('0x00000001', 'Ошибка выбора теста'),
    ('0x00000002', 'Не удалось отправить данные из Front RS232 в K-Line'),
    ('0x00000004', 'Не удалось отправить данные из K-Line в Front RS232'),
    ('0x00000008', 'Размер отправленного массива из Front RS232 не равен размеру принятого массива в K-Line'),
    ('0x00000010', 'Размер отправленного массива из K-Line не равен размеру принятого массива в Front RS232'),
    ('0x00000020', 'Ошибка отправки данных на скорости 10400Bd. K-Line to TS232'),
    ('0x00000040', 'Ошибка приёма данных на скорости 10400Bd. Отправленные данные не соответствуют полученным. K-Line to RS232');


insert into tf_12_13_CAN_errors (error_id, error_description) values
	('0x00000001', 'Ошибка инициализации CAN1'),
    ('0x00000002', 'Ошибка инициализации CAN2'),
    ('0x00000004', 'Ошибка инициализации CAN'),
    ('0x00000008', 'Ошибка инициализации фильтра CAN1'),
    ('0x00000010', 'Ошибка инициализации фильтра CAN2'),
    ('0x00000020', 'Ошибка запуска CAN1'),
    ('0x00000040', 'Ошибка запуска CAN2'),
    ('0x00000080', 'Ошибка активации уведомлений CAN1'),
    ('0x00000100', 'Ошибка активации уведомлений CAN2'),
    ('0x00000200', 'Ошибка деинициализации CAN1'),
    ('0x00000400', 'Ошибка деинициализации CAN2'),
    ('0x00000800', 'Ошибка выбранной для CAN скорости'),
    ('0x00001000', 'Ошибка отправки или приёма данных из CAN1 в CAN2 со скоростью 125 кб/с'),
    ('0x00002000', 'Ошибка отправки или приёма данных из CAN1 в CAN2 со скоростью 250 кб/с'),
    ('0x00004000', 'Ошибка отправки или приёма данных из CAN1 в CAN2 со скоростью 500 кб/с'),
    ('0x00008000', 'Ошибка отправки или приёма данных из CAN1 в CAN2 со скоростью 1000 кб/с'),
    ('0x00010000', 'Ошибка отправки или приёма данных из CAN2 в CAN1 со скоростью 125 кб/с'),
    ('0x00020000', 'Ошибка отправки или приёма данных из CAN2 в CAN1 со скоростью 250 кб/с'),
    ('0x00040000', 'Ошибка отправки или приёма данных из CAN2 в CAN1 со скоростью 500 кб/с'),
    ('0x00080000', 'Ошибка отправки или приёма данных из CAN2 в CAN1 со скоростью 1000 кб/с');
    

insert into tf_12_13_printer_errors (error_id, error_description) values
	('0x00000001', 'Ошибка зполнения термопринтера тестовым буфером'),
    ('0x00000002', 'Ошибка прокрутки бумаги в термопринтере вперёд'),
    ('0x00000004', 'Ошибка прокрутки бумаги в термопринтере назад'),
    ('0x00000008', 'Ошибка печати'),
    ('0x00000010', 'В принтере отсутствует бумага');


-- Порядок должен соответствовать коду C#
-- NoError
insert into tf_15_16_errors (error_id, error_description) values (0, 'Ошибок нет');-- 0
-- card_reader
insert into tf_15_16_errors (error_id, error_description) values (1, 'Ошибка вставки');-- 1
insert into tf_15_16_errors (error_id, error_description) values (2, 'Ошибка извлечения');-- 2
insert into tf_15_16_errors (error_id, error_description) values (3, 'Таймаут считывателя карт вышел');-- 3
-- SKZI
insert into tf_15_16_errors (error_id, error_description) values (4, 'Некорректный part_number');-- 4
insert into tf_15_16_errors (error_id, error_description) values (5, 'Некорректный ref_number');-- 5
insert into tf_15_16_errors (error_id, error_description) values (6, 'Отсутствуют данные о спутниках');-- 6
insert into tf_15_16_errors (error_id, error_description) values (7, 'Не совпадает время');-- 7
-- Tacho
insert into tf_15_16_errors (error_id, error_description) values (8, 'Не удалось отправить команду тахографу');-- 8
insert into tf_15_16_errors (error_id, error_description) values (9, 'Не удалось получить команду от тахографа');-- 9
-- CAN
insert into tf_15_16_errors (error_id, error_description) values (10, 'Не удалось изменить К фактор');-- 10
insert into tf_15_16_errors (error_id, error_description) values (11, 'Не удалось получить К фактор');-- 11
insert into tf_15_16_errors (error_id, error_description) values (12, 'Несовпадение скорости, выставленной на генераторе, со скоростью, выставленной в CAN');-- 12
-- Odometr
insert into tf_15_16_errors (error_id, error_description) values (13, 'Не удалось сбросить Дневной одометр');-- 13
insert into tf_15_16_errors (error_id, error_description) values (14, 'Не удалось сбросить Глобальный одометр');-- 14
insert into tf_15_16_errors (error_id, error_description) values (15, 'Адаптер не подключен к системе');-- 14


insert into logs_types (log_type) values ("Ошибка подключения");
insert into logs_types (log_type) values ("Ошибка запроса");
insert into logs_types (log_type) values ("Ошибка логики");
insert into logs_types (log_type) values ("Информация");


insert into customers (customer_name) values ('КАМАЗ');
insert into customers (customer_name) values ('Тест');


insert into plans (plan_date, plan_time, part_number_id, amount, is_prioritize) values ('2025-02-25', '11:49:11', 3, 1200, false);
