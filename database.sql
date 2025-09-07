-- create database hotel_db;
use hotel_db;

create table reservations(
	reserv_id INT auto_increment primary key,
    guest_name varchar(255) not null,
    room_num int not null,
    contact_num varchar(10) not null,
    reserv_date timestamp default current_timestamp
);

describe reservations;


