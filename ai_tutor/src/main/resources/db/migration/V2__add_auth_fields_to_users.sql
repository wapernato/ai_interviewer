alter table users
    add column if not exists email varchar(100);

alter table users
    add column if not exists password_hash varchar(255);

alter table users
    add column if not exists role varchar(30) not null default 'USER';

alter table users
    add column if not exists enabled boolean not null default true;

alter table users
    add column if not exists created_at timestamp not null default current_timestamp;

alter table users
    add constraint uk_users_email unique (email);
