create extension if not exists pgcrypto;

create table
    "user"
(
    id         serial      not null,
    created_at timestamp   not null default current_timestamp,
    username   varchar(12) not null unique, -- SHA-1 hash of the password
    password   bytea       not null,
    primary key (id)
);

insert into "user"(username, password)
select 'admin',
       digest('admin', 'sha1')
where not exists(select 1
                 from "user");