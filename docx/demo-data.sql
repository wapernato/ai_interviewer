-- Demo data for AI Interviewer

insert into users(username)
values ('rodion_demo')
on conflict (username) do nothing;

insert into topics(name)
values
    ('Java Core'),
    ('SQL / PostgreSQL'),
    ('Spring Boot'),
    ('Algorithms')
on conflict (name) do nothing;

update ai_profiles
set active = false;

insert into ai_profiles(
    id,
    mode,
    description_mode,
    instruction_mode,
    model_name,
    language,
    answer_style,
    difficulty,
    feedback_mode,
    hint_mode,
    active,
    temperature,
    max_tokens
)
values (
    1,
    'strict_java_interviewer',
    'Strict Java backend interviewer for internship preparation',
    'Ask Java Backend interview questions, evaluate answers strictly, point out weak places and give recommendations.',
    'local-demo-model',
    'ru',
    'strict',
    'junior',
    'detailed',
    false,
    true,
    0.70,
    1000
)
on conflict (id) do update
set
    mode = excluded.mode,
    description_mode = excluded.description_mode,
    instruction_mode = excluded.instruction_mode,
    model_name = excluded.model_name,
    language = excluded.language,
    answer_style = excluded.answer_style,
    difficulty = excluded.difficulty,
    feedback_mode = excluded.feedback_mode,
    hint_mode = excluded.hint_mode,
    active = excluded.active,
    temperature = excluded.temperature,
    max_tokens = excluded.max_tokens;