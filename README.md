# AI Interviewer

AI Interviewer — учебный fullstack-проект для тренировки технических собеседований.

Пользователь выбирает пользователя и тему собеседования, получает вопрос, пишет ответ, после чего система сохраняет вопрос и ответ в PostgreSQL и возвращает предварительный feedback. Также пользователь может посмотреть историю своих вопросов и ответов.

Проект разрабатывается как учебный backend-проект для последовательного изучения Java, Spring Boot, REST, JPA/Hibernate и тестирования.

---

## Цель проекта

Цель проекта — создать личного AI-интервьюера, который помогает готовиться к техническим собеседованиям по backend-направлению.

Основной сценарий работы:

1. Пользователь открывает React-интерфейс.
2. Frontend загружает пользователей, темы и активный AI-профиль с backend.
3. Пользователь выбирает тему собеседования.
4. Backend берёт активный AI-профиль из базы данных.
5. Система генерирует вопрос по выбранной теме.
6. Пользователь отправляет ответ.
7. Backend сохраняет вопрос и ответ в PostgreSQL.
8. Система возвращает feedback.
9. Пользователь может открыть историю вопросов и ответов.

---

## Текущий статус проекта

На текущем этапе реализован локальный MVP:

- Spring Boot REST API;
- React frontend;
- PostgreSQL database;
- Spring Data JPA repository layer;
- Hibernate ORM и JPA-связи между сущностями;
- service layer с бизнес-логикой;
- DTO и mapper-слой для API-ответов;
- Bean Validation и централизованная обработка ошибок;
- генерация вопросов по теме;
- сохранение вопросов и ответов;
- feedback по ответу пользователя;
- история пользователя;
- активный AI-профиль;
- unit-тесты сервисного слоя на JUnit 5, Mockito и AssertJ;
- автоматическое создание таблиц через `schema.sql`;
- автоматическое добавление demo-данных через `data.sql`.

Проект запускается локально и не требует деплоя.

---

## Стек технологий

### Backend

- Java 17
- Spring Boot 3.5.12
- Spring Web / REST
- Spring Data JPA
- Hibernate ORM
- Jakarta Validation
- PostgreSQL Driver
- Maven
- JUnit 5
- Mockito
- AssertJ

### Frontend

- React 19
- Vite 8
- JavaScript
- CSS

### Database / Tools

- PostgreSQL
- DBeaver / pgAdmin / psql
- Postman
- Git / GitHub

---

## Архитектура

Основная цепочка работы приложения:

```text
React Frontend → Spring Controller → Service → Repository → Hibernate → PostgreSQL
```

Объекты API отделены от JPA-сущностей:

```text
HTTP Request DTO → Controller → Service → Entity → Repository
Repository → Entity → Mapper → Response DTO → HTTP Response
```

### Controller

Контроллеры принимают HTTP-запросы от frontend.

Примеры endpoint'ов:

- `GET /api/users` — получить пользователей;
- `GET /api/topics` — получить темы;
- `GET /api/ai-profiles` — получить AI-профили;
- `POST /api/interview/question` — сгенерировать вопрос;
- `POST /api/interview/answer` — отправить ответ;
- `GET /api/users/{userId}/history` — получить историю пользователя.

### Service

Service layer содержит основную бизнес-логику:

- проверка пользователя;
- проверка темы;
- получение активного AI-профиля;
- генерация вопроса;
- сохранение ответа;
- формирование feedback;
- получение истории пользователя.

Изменяющие операции выполняются в транзакциях через `@Transactional`, а операции чтения используют `@Transactional(readOnly = true)`.

### Repository

Repository layer построен на Spring Data JPA. Репозитории наследуются от `JpaRepository`, а Hibernate преобразует операции с Java-сущностями в SQL-запросы к PostgreSQL.

### Entity и Mapper

Таблицы представлены JPA-сущностями `User`, `Topic`, `Question`, `Answer` и `AiProfile`. Связи вопроса с пользователем и темой, а также ответа с вопросом и AI-профилем описаны через `@ManyToOne` с ленивой загрузкой.

Mapper-классы преобразуют сущности в Response DTO. Благодаря этому структура REST-ответа не зависит напрямую от структуры таблиц и Hibernate-сущностей.

---

## База данных

Основные таблицы:

- `users` — пользователи;
- `topics` — темы собеседования;
- `ai_profiles` — настройки поведения AI-интервьюера;
- `questions` — сгенерированные вопросы;
- `answers` — ответы пользователя.

Схема создаётся автоматически из файла:

```text
ai_tutor/src/main/resources/schema.sql
```

Стартовые demo-данные добавляются из файла:

```text
ai_tutor/src/main/resources/data.sql
```

На текущем этапе схема управляется SQL-скриптами, а JPA/Hibernate используется для работы с сущностями и запросами. Миграции Flyway или Liquibase пока не подключены.

---

## AI-профиль

Frontend не передаёт `aiProfileId` напрямую при генерации вопроса.

Backend сам берёт активный AI-профиль из таблицы `ai_profiles`:

```sql
select *
from ai_profiles
where active = true
limit 1;
```

Это позволяет централизованно менять режим интервьюера.

Например, активный профиль может задавать:

- режим интервьюера;
- сложность вопроса;
- стиль ответа;
- режим feedback;
- название модели;
- язык.

На текущем этапе используется один активный demo-профиль.

---

## AI-слой

На текущем этапе AI-слой реализован как mock / rule-based logic.

Это означает, что генерация вопросов и feedback работают по заранее описанной логике внутри приложения. Архитектура уже выделяет отдельные интерфейсы для генерации вопросов и оценки ответов, поэтому в будущем этот слой можно заменить на интеграцию с реальной AI API без полного переписывания основного сценария.

Планируемое развитие:

- подключить реальную GPT / LLM API;
- формировать prompt на основе темы и AI-профиля;
- получать вопрос от модели;
- отправлять ответ пользователя на AI-оценку;
- возвращать более умный feedback.

---

## Локальный запуск

Подробная инструкция находится в файле:

```text
docs/LOCAL_SETUP.md
```

Короткий вариант запуска:

### 1. Создать базу PostgreSQL

```sql
create database ai_interviewer;
```

### 2. Запустить backend

```bash
cd ai_tutor

export DB_URL="jdbc:postgresql://localhost:5432/ai_interviewer"
export DB_USERNAME="postgres"
export DB_PASSWORD="your_password"

mvn spring-boot:run
```

### 3. Запустить frontend

```bash
cd frontend
npm install
npm run dev
```

Frontend обычно открывается по адресу:

```text
http://localhost:5173
```

Backend запускается на:

```text
http://localhost:8080
```

---

## Переменные окружения

Backend использует переменные окружения для подключения к PostgreSQL:

| Variable | Description | Example |
|---|---|---|
| `DB_URL` | JDBC URL базы данных | `jdbc:postgresql://localhost:5432/ai_interviewer` |
| `DB_USERNAME` | пользователь PostgreSQL | `postgres` |
| `DB_PASSWORD` | пароль PostgreSQL | `your_password` |
| `SERVER_PORT` | порт backend-сервера | `8080` |

Пароль от базы данных не должен храниться в репозитории.

Пример конфигурации находится в:

```text
ai_tutor/src/main/resources/application-example.properties
```

---

## Тестирование

Сервисный слой покрыт unit-тестами с использованием JUnit 5, Mockito и AssertJ.

Проверяются:

- валидация входных данных;
- сценарии `NotFound` и конфликтов;
- успешные CRUD-операции;
- формирование Response DTO;
- взаимодействия с репозиториями через `verify`, `argThat` и `verifyNoInteractions`;
- генерация вопросов и оценка ответов;
- активация AI-профиля и деактивация остальных профилей.

Всего в проекте находится 198 unit-тестов сервисного слоя.

Запуск тестов:

```bash
cd ai_tutor
mvn test
```

Проект собирается для Java 17. При запуске Mockito на более новых JDK может потребоваться явное подключение Mockito Java Agent.

---

## Демонстрационный сценарий

1. Запустить PostgreSQL.
2. Создать базу `ai_interviewer`, если она ещё не создана.
3. Запустить backend.
4. Запустить frontend.
5. Открыть сайт.
6. Выбрать пользователя.
7. Выбрать тему.
8. Нажать `Сгенерировать вопрос`.
9. Написать ответ.
10. Нажать `Отправить ответ`.
11. Посмотреть feedback.
12. Нажать `Показать историю`.
13. Проверить сохранённые данные в PostgreSQL.

---

## Что уже реализовано

- REST API на Spring Boot;
- разделение на controller / service / repository;
- JPA-сущности и связи Hibernate;
- DTO и mapper-слой;
- транзакционная бизнес-логика;
- Bean Validation и `GlobalExceptionHandler`;
- DTO для interview-flow;
- PostgreSQL-схема;
- demo-data для локального запуска;
- React-интерфейс;
- загрузка пользователей и тем в dropdown;
- отображение активного AI-профиля;
- генерация вопроса;
- отправка ответа;
- feedback;
- история пользователя;
- локальная конфигурация через переменные окружения;
- unit-тесты всех реализаций сервисного слоя.

---

## Что можно улучшить дальше

- подключить реальную AI API;
- добавить выбор AI-профиля на frontend;
- добавить полноценные interview sessions;
- добавить регистрацию и авторизацию;
- добавить статистику по слабым темам пользователя;
- добавить Docker Compose для PostgreSQL;
- добавить миграции через Flyway или Liquibase;
- добавить `@DataJpaTest` для JPA-репозиториев;
- добавить `@WebMvcTest` для REST-контроллеров;
- добавить интеграционные тесты с Testcontainers и PostgreSQL;
- улучшить обработку ошибок;
- добавить отдельную страницу администрирования тем и AI-профилей.

---

## Назначение проекта

Проект создан как учебный fullstack-проект с backend-фокусом для практики Java 17, Spring Boot, REST API, Spring Data JPA, Hibernate, PostgreSQL, DTO, Bean Validation, JUnit 5, Mockito и React.
