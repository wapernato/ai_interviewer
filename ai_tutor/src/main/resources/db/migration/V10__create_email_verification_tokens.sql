create table if not exists email_verification_tokens (
    id bigserial primary key,
    user_id bigint not null,
    token varchar(255) not null unique,
    expires_at timestamp not null,
    used_at timestamp,
    created_at timestamp not null default current_timestamp,

    constraint fk_email_verification_tokens_user
        foreign key (user_id) references users(id)
);

create index if not exists idx_email_verification_tokens_user_id
    on email_verification_tokens(user_id);

create index if not exists idx_email_verification_tokens_expires_at
    on email_verification_tokens(expires_at);
