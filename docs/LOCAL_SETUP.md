# Локальный запуск AI Interviewer

Этот файл описывает запуск проекта локально без деплоя.

Проект состоит из двух частей:

- backend: Java / Spring Boot / PostgreSQL;
- frontend: React / Vite.

---

## 1. Что нужно установить

Для локального запуска нужны:

- Java 17;
- Maven;
- Node.js и npm;
- PostgreSQL;
- Git.

DBeaver не обязателен. Его можно использовать только как удобный инструмент для просмотра таблиц и выполнения SQL-запросов.

Вместо DBeaver можно использовать:

- pgAdmin;
- psql;
- любой другой PostgreSQL-клиент.

---

## 2. Создать локальную базу данных

Один раз нужно создать базу данных:

```sql
create database ai_interviewer;
```

Это можно сделать через DBeaver, pgAdmin или psql.

Пример через psql:

```bash
psql -U postgres
```

Затем внутри psql:

```sql
create database ai_interviewer;
\q
```

---

## 3. Запустить backend

Перейти в папку backend:

```bash
cd ai_tutor
```

Задать переменные окружения для подключения к PostgreSQL.

Для Git Bash / macOS / Linux:

```bash
export DB_URL="jdbc:postgresql://localhost:5432/ai_interviewer"
export DB_USERNAME="postgres"
export DB_PASSWORD="your_password"
```

Для PowerShell:

```powershell
$env:DB_URL="jdbc:postgresql://localhost:5432/ai_interviewer"
$env:DB_USERNAME="postgres"
$env:DB_PASSWORD="your_password"
```

Запустить backend:

```bash
mvn spring-boot:run
```

Backend по умолчанию запускается на:

```text
http://localhost:8080
```

---

## 4. Автоматическая инициализация базы

При запуске backend Spring Boot выполняет SQL-файлы из `src/main/resources`:

```text
schema.sql
```

создаёт таблицы:

- `users`;
- `topics`;
- `ai_profiles`;
- `questions`;
- `answers`.

```text
data.sql
```

добавляет demo-данные:

- demo-пользователя;
- несколько тем;
- активный AI-профиль.

Поэтому после создания самой базы `ai_interviewer` таблицы вручную создавать не нужно.

---

## 5. Запустить frontend

В отдельном терминале перейти в папку frontend:

```bash
cd frontend
```

Установить зависимости:

```bash
npm install
```

Запустить frontend:

```bash
npm run dev
```

Frontend обычно доступен по адресу:

```text
http://localhost:5173
```

---

## 6. Проверка работы

После запуска backend и frontend нужно проверить основной сценарий:

1. Открыть сайт.
2. Проверить, что пользователи и темы появились в dropdown.
3. Выбрать пользователя.
4. Выбрать тему.
5. Нажать `Сгенерировать вопрос`.
6. Написать ответ.
7. Нажать `Отправить ответ`.
8. Проверить, что появился feedback.
9. Нажать `Показать историю`.
10. Проверить, что вопрос и ответ отображаются в истории.

---

## 7. Полезные SQL-запросы для проверки

```sql
select * from users;
select * from topics;
select * from ai_profiles;
select * from questions order by id desc;
select * from answers order by id desc;
```

Проверить активный AI-профиль:

```sql
select *
from ai_profiles
where active = true;
```

---

## 8. Важное замечание про `data.sql`

В проекте включена настройка:

```properties
spring.sql.init.mode=always
```

Это значит, что при каждом запуске backend Spring Boot пытается выполнить `schema.sql` и `data.sql`.

Для текущего учебного MVP это удобно: demo-пользователь, темы и активный AI-профиль гарантированно есть в базе.

Но нужно понимать ограничение: `data.sql` может обновлять demo-данные при каждом запуске. Например, если вручную изменить активный AI-профиль в базе, при следующем запуске backend demo-скрипт может снова активировать профиль из `data.sql`.

Для учебной защиты это нормально. Для production-подхода лучше использовать миграции через Flyway или Liquibase и разделять dev/test/prod-конфигурации.

---

## 9. Если backend не подключается к базе

Проверь:

1. PostgreSQL запущен.
2. База `ai_interviewer` создана.
3. `DB_URL` указывает на правильную базу.
4. `DB_USERNAME` и `DB_PASSWORD` совпадают с настройками PostgreSQL.
5. PostgreSQL слушает порт `5432`.

В Git Bash можно проверить переменные так:

```bash
echo "$DB_URL"
echo "$DB_USERNAME"
echo "$DB_PASSWORD"
```

---

## 10. Краткая схема запуска

Backend:

```bash
cd ai_tutor
export DB_URL="jdbc:postgresql://localhost:5432/ai_interviewer"
export DB_USERNAME="postgres"
export DB_PASSWORD="your_password"
mvn spring-boot:run
```

Frontend:

```bash
cd frontend
npm install
npm run dev
```
