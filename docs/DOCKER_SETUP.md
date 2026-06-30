# Docker Setup

Краткая инструкция для запуска backend и PostgreSQL через Docker.

## Файлы

```text
docker-compose.yml
ai_tutor/Dockerfile
```

`Dockerfile` собирает backend image.

`docker-compose.yml` запускает два контейнера:

```text
backend
postgres
```

## Сборка backend image

Из папки `ai_tutor`:

```bash
docker build -t ai-interviewer-backend .
```

Image:

```text
ai-interviewer-backend:latest
```

## Запуск проекта

Из корня проекта:

```bash
docker compose up -d
```

Проверить контейнеры:

```bash
docker compose ps
```

Посмотреть логи backend:

```bash
docker compose logs backend
```

Посмотреть логи PostgreSQL:

```bash
docker compose logs postgres
```

## Проверка API

```bash
curl http://localhost:8080/api/health
```

Ожидаемый ответ:

```text
AI Tutor API is running
```

```bash
curl http://localhost:8080/api/topics
```

## Подключение к PostgreSQL

```bash
docker compose exec postgres psql -U postgres -d ai_interviewer
```

Полезные команды внутри `psql`:

```sql
\dt
select * from topics;
\q
```

## Порты

```text
localhost:8080 -> backend:8080
localhost:5433 -> postgres:5432
```

Внутри Docker-сети backend подключается к БД так:

```text
jdbc:postgresql://postgres:5432/ai_interviewer
```

С Mac подключение идёт так:

```text
jdbc:postgresql://localhost:5433/ai_interviewer
```

## Данные PostgreSQL

Данные хранятся в named volume:

```text
ai_interviewer_postgres_data
```

Остановить контейнеры без удаления volume:

```bash
docker compose down
```

Удалить контейнеры вместе с volume:

```bash
docker compose down -v
```

`down -v` удалит данные PostgreSQL.

