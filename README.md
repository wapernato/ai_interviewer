# AI Interviewer

AI Interviewer - учебный fullstack-проект с backend-фокусом для тренировки технических собеседований.

Основная цель проекта - практиковать Java Backend разработку на реальном REST API: Spring Boot, PostgreSQL, JPA/Hibernate, JWT-авторизацию, миграции, Docker, CI и тестирование.

## Текущий статус

На текущем этапе актуальная часть проекта - backend API и набор тестов вокруг него.

Реализовано:

- REST API на Spring Boot;
- регистрация и login пользователей;
- JWT-based authentication;
- разделение пользовательских и административных endpoint'ов;
- пользовательский API через `/api/me`;
- admin API через `/api/admin/**`;
- генерация вопросов для собеседования;
- отправка ответа и получение feedback;
- сохранение вопросов и ответов в PostgreSQL;
- история ответов текущего пользователя;
- AI profile для настройки поведения интервьюера;
- Flyway-миграции;
- Dockerfile для backend;
- Docker Compose для PostgreSQL, backend и frontend;
- backend-тесты на JUnit 5, Mockito, AssertJ, MockMvc, RestAssured и Testcontainers;
- отдельный Kotlin-проект с black-box API-тестами на RestAssured;
- GitHub Actions CI.

Frontend в репозитории есть, но сейчас он частично отстает от новой backend-модели с JWT и `/api/me`. Его нужно синхронизировать отдельно.

## Стек

### Backend

- Java 17
- Spring Boot 3.5.12
- Spring Web
- Spring Security
- OAuth2 Resource Server
- JWT
- Spring Data JPA
- Hibernate
- PostgreSQL
- Flyway
- Maven

### Tests

- JUnit 5
- Mockito
- AssertJ
- MockMvc
- RestAssured
- Testcontainers
- Kotlin API tests
- Gradle

### Frontend

- React
- Vite
- JavaScript
- CSS

### Infrastructure

- Docker
- Docker Compose
- GitHub Actions

## Архитектура

Основная цепочка backend-приложения:

```text
HTTP client -> Controller -> Service -> Repository -> Hibernate -> PostgreSQL
```

DTO и JPA-сущности разделены:

```text
Request DTO -> Controller -> Service -> Entity -> Repository
Repository -> Entity -> Mapper -> Response DTO -> HTTP Response
```

Ответственность слоев:

- `controller` - принимает HTTP-запросы, валидирует входные DTO, достает текущего пользователя из JWT;
- `service` - содержит бизнес-логику и транзакционные операции;
- `repository` - работает с базой через Spring Data JPA;
- `model` - JPA-сущности;
- `dto` - request/response модели API;
- `mapper` - преобразование entity в response DTO;
- `security` - работа с JWT;
- `config` - конфигурация Spring Security, JWT и приложения.

## Security

Пользователь регистрируется через `/api/auth/register`, затем получает JWT через `/api/auth/login`.

JWT используется как Bearer token:

```http
Authorization: Bearer <token>
```

Основные правила доступа:

- `/api/auth/**` - публичные endpoint'ы;
- `/api/health` - публичный endpoint;
- `/api/me` и `/api/me/**` - текущий авторизованный пользователь;
- `/api/interview/**` - авторизованный пользователь;
- `/api/admin/**` - только пользователь с ролью `ADMIN`;
- остальные endpoint'ы требуют авторизации.

Роль пользователя хранится в JWT claim `role`. В Spring Security она конвертируется в authority формата `ROLE_<role>`.

## Основные Endpoint'ы

### Auth

- `POST /api/auth/register` - регистрация пользователя;
- `POST /api/auth/login` - login и получение JWT.

### Current User

- `GET /api/me` - получить текущего пользователя;
- `PUT /api/me` - обновить текущего пользователя;
- `DELETE /api/me` - удалить текущего пользователя;
- `GET /api/me/interview-history` - получить историю текущего пользователя.

### Admin Users

- `GET /api/admin/users` - получить всех пользователей;
- `GET /api/admin/users/{id}` - получить пользователя по id;
- `DELETE /api/admin/users/{id}` - удалить пользователя по id;
- `GET /api/admin/users/search?username=...` - найти пользователя по username.

### Interview

- `POST /api/interview/question` - получить вопрос;
- `POST /api/interview/answer` - отправить ответ и получить feedback.

### Topics

- `GET /api/topics`
- `GET /api/topics/{id}`
- `POST /api/topics`
- `PUT /api/topics/{id}`
- `DELETE /api/topics/{id}`
- `GET /api/topics/search?name=...`

### AI Profiles

- `GET /api/ai-profiles`
- `GET /api/ai-profiles/{id}`
- `POST /api/ai-profiles`
- `PUT /api/ai-profiles/{id}`
- `DELETE /api/ai-profiles/{id}`
- `PUT /api/ai-profiles/{id}/activate`
- `GET /api/ai-profiles/search?mode=...`
- `GET /api/ai-profiles/filter`
- `GET /api/ai-profiles/language`

## База Данных

Используется PostgreSQL.

Основные таблицы:

- `users`;
- `topics`;
- `ai_profiles`;
- `questions`;
- `answers`.

Схема управляется через Flyway:

```text
ai_tutor/src/main/resources/db/migration
```

Текущие миграции:

- `V1__create_initial_schema.sql`;
- `V2__add_auth_fields_to_users.sql`;
- `V3__make_user_auth_fields_required.sql`;
- `V4__rework_questions_for_shared_bank.sql`;
- `V5__enforce_single_active_ai_profile.sql`;
- `V6__seed_default_ai_profile.sql`.

`V6` добавляет дефолтный активный AI profile, чтобы API-тесты и локальный запуск не зависели от ручного создания профиля.

## Локальный Запуск

### Backend через Docker Compose

Сначала собрать backend image:

```bash
docker build -t ai-interviewer-backend:latest ./ai_tutor
```

Поднять PostgreSQL и backend:

```bash
docker compose up -d postgres backend
```

Backend будет доступен на:

```text
http://localhost:8080
```

PostgreSQL из Docker Compose доступен с host-машины на порту `5433`:

```text
jdbc:postgresql://localhost:5433/ai_interviewer
```

### Backend через Maven

Если PostgreSQL уже запущен локально:

```bash
cd ai_tutor

export DB_URL="jdbc:postgresql://localhost:5432/ai_interviewer"
export DB_USERNAME="postgres"
export DB_PASSWORD="your_password"
export JWT_SECRET="change-this-secret-key-must-be-at-least-32-characters-long"

mvn spring-boot:run
```

Если PostgreSQL поднят через `docker compose`, для Maven-запуска backend используй порт `5433`:

```bash
export DB_URL="jdbc:postgresql://localhost:5433/ai_interviewer"
```

### Frontend

```bash
cd frontend
npm install
npm run dev
```

Vite dev server обычно запускается на:

```text
http://localhost:5173
```

Через Docker Compose frontend публикуется на:

```text
http://localhost:3000
```

## Переменные Окружения

Backend использует следующие переменные:

| Variable | Description | Example |
|---|---|---|
| `DB_URL` | JDBC URL PostgreSQL | `jdbc:postgresql://localhost:5432/ai_interviewer` |
| `DB_USERNAME` | пользователь PostgreSQL | `postgres` |
| `DB_PASSWORD` | пароль PostgreSQL | `your_password` |
| `SERVER_PORT` | порт backend | `8080` |
| `JWT_SECRET` | секрет для подписи JWT | `change-this-secret-key-must-be-at-least-32-characters-long` |

Пример конфигурации:

```text
ai_tutor/src/main/resources/application-example.properties
```

## Тестирование

### Backend Tests

Запуск всех backend-тестов:

```bash
cd ai_tutor
mvn test
```

Покрываются:

- service layer;
- controller layer через MockMvc;
- валидация DTO;
- обработка ошибок;
- security-related логика на уровне JWT/ролей;
- repository/integration сценарии;
- REST-сценарии через RestAssured;
- PostgreSQL integration через Testcontainers.

### Kotlin API Tests

Kotlin API tests - это отдельный black-box test project. Он ходит в backend только по HTTP и не импортирует Java-классы из `ai_tutor`.

Перед запуском нужно поднять backend:

```bash
docker build -t ai-interviewer-backend:latest ./ai_tutor
docker compose up -d postgres backend
```

Потом запустить тесты:

```bash
cd api-tests-kotlin
./gradlew test
```

На локальной машине проект нужно запускать с Java 17. Если системная Java новее и Gradle/Kotlin падает до запуска тестов, можно явно указать `JAVA_HOME`:

```bash
cd api-tests-kotlin
JAVA_HOME="/Applications/PyCharm CE.app/Contents/jbr/Contents/Home" ./gradlew test
```

## CI

GitHub Actions workflow находится здесь:

```text
.github/workflows/ci.yml
```

CI выполняет:

1. checkout репозитория;
2. установку JDK 17;
3. запуск backend-тестов через Maven;
4. сборку Docker image для backend;
5. запуск PostgreSQL и backend через Docker Compose;
6. ожидание готовности backend;
7. запуск Kotlin API tests через Gradle.

Workflow запускается на:

- push в `main`;
- pull request в `main`.

## Учебный Фокус

Проект используется как учебная база для роста в Java Backend:

- проектирование REST API;
- правильные HTTP-статусы;
- DTO и валидация;
- слой service и транзакции;
- JPA/Hibernate связи;
- SQL и PostgreSQL;
- Flyway-миграции;
- Spring Security и JWT;
- роли и разграничение доступа;
- unit, controller, integration и API-тесты;
- Docker и CI.

## Что Делать Дальше

Ближайшие логичные улучшения:

- синхронизировать frontend с JWT и `/api/me`;
- явно закрыть admin endpoint'ы тестами с реальным ADMIN-токеном;
- добавить нормальный seed/admin setup для локальной разработки;
- разделить публичные и admin endpoint'ы для topics и AI profiles;
- добавить полноценные interview sessions;
- улучшить модель истории и статистики;
- подключить реальную LLM API вместо mock/rule-based логики.
