alter table users
    alter column email set not null;

alter table users
    alter column password_hash set not null;
