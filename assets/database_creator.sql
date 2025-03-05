CREATE TABLE banks (
    id SERIAL PRIMARY KEY,
    swift_code VARCHAR(11) UNIQUE NOT NULL,
    name TEXT NOT NULL,
    address TEXT,
    town_name TEXT,
    country_code VARCHAR(2) NOT NULL,
    country_name TEXT NOT NULL,
    time_zone TEXT NOT NULL
);

CREATE TABLE branches (
    id SERIAL PRIMARY KEY,
    swift_code VARCHAR(11) UNIQUE NOT NULL,
    bank_id INTEGER REFERENCES banks(id),
    branch_name TEXT,
    address TEXT,
    town_name TEXT
);

drop table branches
drop table banks

select * from branches
select * from banks