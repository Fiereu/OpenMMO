create extension if not exists pgcrypto;

create table
    "user"
(
    id         serial      not null,
    created_at timestamp   not null default current_timestamp,
    username   varchar(12) not null unique,
    password   bytea       not null, -- SHA-1 hash of the password
    primary key (id)
);

insert into "user"(username, password)
select 'admin',
       digest('admin', 'sha1')
where not exists(select 1
                 from "user");