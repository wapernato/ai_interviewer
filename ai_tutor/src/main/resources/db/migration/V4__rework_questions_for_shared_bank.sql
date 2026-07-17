-- A shared question has no owner. We keep only who initiated its generation.
alter table questions
    drop constraint fk_questions_user;

alter table questions
    rename column user_id to created_by_user_id;

alter table questions
    alter column created_by_user_id drop not null;

alter table questions
    add constraint fk_questions_created_by_user
        foreign key (created_by_user_id)
        references users(id)
        on delete set null;

alter table questions
    add column ai_profile_id bigint;

alter table questions
    add constraint fk_questions_ai_profile
        foreign key (ai_profile_id)
        references ai_profiles(id)
        on delete set null;

-- Existing questions need valid values before the columns become NOT NULL.
alter table questions
    add column difficulty varchar(20);

update questions
set difficulty = 'MEDIUM'
where difficulty is null;

alter table questions
    alter column difficulty set not null;

alter table questions
    add constraint chk_questions_difficulty
        check (difficulty in ('EASY', 'MEDIUM', 'HARD'));

alter table questions
    add column publication_status varchar(30);

update questions
set publication_status = 'PUBLISHED'
where publication_status is null;

alter table questions
    alter column publication_status set not null;

-- Newly generated questions are hidden from the public bank until reviewed.
alter table questions
    alter column publication_status set default 'PENDING_REVIEW';

alter table questions
    add constraint chk_questions_publication_status
        check (publication_status in ('PENDING_REVIEW', 'PUBLISHED', 'REJECTED'));

alter table questions
    add column created_at timestamp not null default current_timestamp;

create index idx_questions_bank_filter
    on questions (publication_status, topic_id, difficulty);
