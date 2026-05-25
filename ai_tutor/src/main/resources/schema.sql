create table if not exists users (
    id bigserial primary key,
    username varchar(100) not null unique
);

create table if not exists topics (
    id bigserial primary key,
    name varchar(100) not null unique
);

create table if not exists ai_profiles (
    id bigserial primary key,
    mode varchar(100) not null unique,
    description_mode text,
    instruction_mode text not null,
    model_name varchar(100),
    language varchar(20) default 'ru',
    answer_style varchar(50),
    difficulty varchar(30),
    feedback_mode varchar(50),
    hint_mode boolean default false,
    active boolean default false,
    temperature numeric(3, 2),
    max_tokens integer
);

create unique index if not exists ux_ai_profiles_one_active
on ai_profiles(active)
where active = true;

create table if not exists questions (
    id bigserial primary key,
    user_id bigint not null references users(id) on delete cascade,
    topic_id bigint references topics(id) on delete set null,
    text_question text not null,
    source varchar(30) default 'manual',
    language varchar(20) default 'ru'
);

create table if not exists answers (
    id bigserial primary key,
    question_id bigint not null references questions(id) on delete cascade,
    ai_profile_id bigint references ai_profiles(id) on delete set null,
    answer_text text not null,
    model_name varchar(100) not null
);