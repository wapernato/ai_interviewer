insert into users(id, username)
values (1, 'gnazem')
on conflict (id) do update
set username = excluded.username;

insert into topics(id, name)
values
    (1, 'Java core'),
    (2, 'SQL / PostgreSQL'),
    (3, 'Spring Boot'),
    (4, 'Algorithms')
on conflict (id) do update
set name = excluded.name;

update ai_profiles
set active = false
where active = true;

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

select setval('users_id_seq', (select max(id) from users));
select setval('topics_id_seq', (select max(id) from topics));
select setval('ai_profiles_id_seq', (select max(id) from ai_profiles));