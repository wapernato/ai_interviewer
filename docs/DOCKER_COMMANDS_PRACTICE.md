# Docker Commands Practice

Этот файл нужен для тренировки Docker-команд на проекте `ai_interviewer`.

Цель: научиться читать команды, понимать флаги и уверенно управлять контейнером PostgreSQL.

## Текущий учебный контейнер

В проекте мы используем контейнер:

```bash
ai-postgres
```

Он должен быть создан из image:

```bash
postgres:16
```

Подключение с Mac:

```text
localhost:5433 -> container:5432
```

База:

```text
ai_interviewer
```

Пользователь:

```text
postgres
```

## Как читать Docker-команду

Общий вид:

```bash
docker <command> <options> <arguments>
```

Пример:

```bash
docker run --name ai-postgres -e POSTGRES_DB=ai_interviewer -p 5433:5432 -d postgres:16
```

Разбор:

```text
docker                  главная программа
run                     команда: создать и запустить контейнер
--name ai-postgres      имя контейнера
-e POSTGRES_DB=...      environment variable
-p 5433:5432            проброс порта
-d                      запуск в фоне
postgres:16             image
```

## Базовые команды

### Версия Docker

```bash
docker --version
```

Проверяет, установлен ли Docker CLI.

### Запущенные контейнеры

```bash
docker ps
```

Показывает только работающие контейнеры.

Проверяй:

```text
STATUS = Up
PORTS = 0.0.0.0:5433->5432/tcp
NAMES = ai-postgres
```

### Все контейнеры

```bash
docker ps -a
```

Показывает и запущенные, и остановленные контейнеры.

Используй, если контейнер пропал из `docker ps`.

### Логи контейнера

```bash
docker logs ai-postgres
```

Используй первым делом, если контейнер упал.

### Скачанные images

```bash
docker images
```

Показывает локальные Docker images.

Ожидаем:

```text
postgres    16
```

## Управление контейнером

### Остановить контейнер

```bash
docker stop ai-postgres
```

Контейнер станет `Exited`.

Проверь:

```bash
docker ps
docker ps -a
```

### Запустить остановленный контейнер

```bash
docker start ai-postgres
```

Проверь:

```bash
docker ps
```

### Перезапустить контейнер

```bash
docker restart ai-postgres
```

Это короткая форма:

```text
stop + start
```

### Удалить контейнер

Сначала остановить:

```bash
docker stop ai-postgres
```

Потом удалить:

```bash
docker rm ai-postgres
```

Не выполняй эту команду случайно. После удаления контейнер надо будет создавать заново.

## Создание PostgreSQL-контейнера

Если контейнера нет, создать заново:

```bash
docker run --name ai-postgres -e POSTGRES_DB=ai_interviewer -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=2773 -p 5433:5432 -d postgres:16
```

Разбор:

```text
--name ai-postgres              имя контейнера
-e POSTGRES_DB=ai_interviewer   имя базы при первой инициализации
-e POSTGRES_USER=postgres       пользователь
-e POSTGRES_PASSWORD=2773       пароль
-p 5433:5432                    localhost:5433 -> container:5432
-d                              detached mode, запуск в фоне
postgres:16                     image
```

Важно: `POSTGRES_DB`, `POSTGRES_USER`, `POSTGRES_PASSWORD` применяются при первой инициализации базы. Если контейнер уже создан, новые значения этих `-e` не поменяют старую БД.

## Работа с PostgreSQL внутри контейнера

### Войти в psql

```bash
docker exec -it ai-postgres psql -U postgres -d ai_interviewer
```

Разбор:

```text
docker exec       выполнить команду внутри контейнера
-i                interactive, держать stdin открытым
-t                tty, нормальный терминальный режим
ai-postgres       имя контейнера
psql              команда внутри контейнера
-U postgres       пользователь psql
-d ai_interviewer база psql
```

### Полезные команды внутри psql

Список таблиц:

```sql
\dt
```

Посмотреть темы:

```sql
select * from topics;
```

Посмотреть пользователей:

```sql
select * from users;
```

Посмотреть AI-профили:

```sql
select * from ai_profiles;
```

Отключить pager, если появляется `END`:

```sql
\pset pager off
```

Сбросить незавершённую команду, если prompt стал `ai_interviewer-#`:

```sql
\r
```

Выйти из psql:

```sql
\q
```

### Выполнить SQL без входа в psql

```bash
docker exec -it ai-postgres psql -U postgres -d ai_interviewer -c "select * from topics;"
```

Флаг `-c` у `psql` означает: выполнить команду и выйти.

## Проверка Spring Boot подключения

Если PostgreSQL запущен на `localhost:5433`, приложение запускать так:

```bash
export DB_URL=jdbc:postgresql://localhost:5433/ai_interviewer
export DB_USERNAME=postgres
export DB_PASSWORD=2773
mvn spring-boot:run
```

Проверить переменные:

```bash
echo $DB_URL
echo $DB_USERNAME
echo $DB_PASSWORD
```

## Практика 1: базовая диагностика

Выполни по очереди и объясни вслух, что делает каждая команда:

```bash
docker --version
docker ps
docker ps -a
docker images
docker logs ai-postgres
```

Контрольные вопросы:

```text
1. Чем docker ps отличается от docker ps -a?
2. Где смотреть причину падения контейнера?
3. Что такое image?
4. Что такое container?
```

## Практика 2: stop/start

Выполни:

```bash
docker stop ai-postgres
docker ps
docker ps -a
docker start ai-postgres
docker ps
```

Контрольные вопросы:

```text
1. Почему после docker stop контейнер пропал из docker ps?
2. Почему он остался в docker ps -a?
3. Чем docker start отличается от docker run?
```

## Практика 3: psql

Выполни:

```bash
docker exec -it ai-postgres psql -U postgres -d ai_interviewer
```

Внутри:

```sql
\pset pager off
\dt
select * from topics;
select * from users;
\q
```

Контрольные вопросы:

```text
1. Зачем нужен docker exec?
2. К чему относятся флаги -U и -d: к docker или к psql?
3. Что значит -it?
```

## Практика 4: чтение docker run

Прочитай команду:

```bash
docker run --name ai-postgres -e POSTGRES_DB=ai_interviewer -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=2773 -p 5433:5432 -d postgres:16
```

Заполни:

```text
--name =
-e =
-p =
-d =
postgres:16 =
5433 =
5432 =
```

## Практика 5: частые ошибки

Ошибка:

```text
docker: 'docker run' requires at least 1 argument
```

Причина:

```text
Не указан image, например postgres:16.
```

Ошибка:

```text
zsh: command not found: -e
```

Причина:

```text
-e ввели отдельно, а не внутри docker run.
```

Ошибка:

```text
POSTGRES_PASSWORD is not specified
```

Причина:

```text
Контейнер PostgreSQL создали без -e POSTGRES_PASSWORD=...
```

Ошибка:

```text
password authentication failed
```

Причина:

```text
Spring Boot передал неправильный пароль.
```

Ошибка:

```text
The server requested SCRAM-based authentication, but no password was provided.
```

Причина:

```text
Spring Boot вообще не получил DB_PASSWORD.
```

## Мини-экзамен

Ответь письменно:

```text
1. Что делает docker run?
2. Что делает docker start?
3. Что делает docker stop?
4. Что делает docker rm?
5. Что делает docker exec?
6. Зачем нужен -p 5433:5432?
7. Зачем нужен -e POSTGRES_PASSWORD=2773?
8. Почему docker ps может быть пустым, а docker ps -a показывает контейнер?
9. Почему Spring Boot подключается к localhost:5433, а PostgreSQL внутри контейнера слушает 5432?
10. Чем image отличается от container?
```

