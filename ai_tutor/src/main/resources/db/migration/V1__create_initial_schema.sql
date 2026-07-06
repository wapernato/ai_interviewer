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
    language varchar(20),
    answer_style varchar(50),
    difficulty varchar(30),
    feedback_mode varchar(50),
    hint_mode boolean,
    active boolean,
    temperature numeric(3, 2),
    max_tokens integer
);

create table if not exists questions (
    id bigserial primary key,
    user_id bigint not null,
    topic_id bigint,
    text_question text not null,
    source varchar(30),
    language varchar(20),

    constraint fk_questions_user
        foreign key (user_id) references users(id),

    constraint fk_questions_topic
        foreign key (topic_id) references topics(id)
);

create table if not exists answers (
    id bigserial primary key,
    question_id bigint not null,
    ai_profile_id bigint,
    answer_text text not null,
    model_name varchar(100) not null,

    constraint fk_answers_question
        foreign key (question_id) references questions(id),

    constraint fk_answers_ai_profile
        foreign key (ai_profile_id) references ai_profiles(id)
);
