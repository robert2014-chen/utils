create table if not exists t_address(
	address_id varchar(200),
	address_name varchar(200)
);
create table if not exists t_person(
	person_id int,
	person_name varchar(200),
	address_id varchar(200)
)