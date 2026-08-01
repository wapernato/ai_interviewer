# Java Backend Interview Notes

Этот файл - конспект важных идей для подготовки к Java Backend Junior/Intern собеседованию.
Он привязан к реальному backend-проекту: controller, service, repository, DTO, JPA, tests.

## 1. Архитектура Слоев

Типичная backend-цепочка:

```text
Controller -> Service -> Repository -> Database
```

### Controller

Controller отвечает за HTTP:

- принять request body;
- принять path/query параметры;
- вызвать service;
- вернуть response и HTTP status;
- не писать бизнес-логику.

Плохой признак: controller вызывает repository напрямую.

### Service

Service отвечает за бизнес-логику:

- проверка входных данных;
- проверка существования сущностей;
- проверка дубликатов;
- выбор правильного exception;
- работа с транзакцией;
- изменение entity;
- вызов repository.

Хороший service-метод читается так:

```text
проверил входные данные
нашел нужные сущности
проверил бизнес-правила
изменил модель
сохранил
вернул response
```

### Repository

Repository отвечает только за доступ к данным:

- `findById`;
- `save`;
- `deleteById`;
- `existsBy...`;
- кастомные SQL/JPQL-запросы.

Repository не должен решать бизнес-правила приложения.

### Формулировка На Собесе

Service layer нужен, чтобы отделить HTTP-логику от бизнес-логики и доступа к данным.
Controller работает с API, Repository работает с БД, а Service описывает сценарий приложения.

## 2. DTO И Entity

Главная идея:

```text
Entity - внутренняя модель БД.
DTO - внешний контракт API.
```

Их нельзя воспринимать как одно и то же.

```text
HTTP Request/Response <-> DTO <-> Service <-> Entity <-> Database
```

### Почему Не Возвращают Entity Из Controller

Если возвращать entity напрямую, появляются риски:

- можно случайно отдать лишние поля;
- API начинает зависеть от структуры таблиц;
- возможна бесконечная рекурсия в JSON;
- lazy loading может вызвать ошибки или лишние SQL-запросы;
- сложнее менять внутреннюю модель без поломки API.

### Request DTO И Response DTO

Request DTO нужен для входящих данных:

```java
public class CreateTopicRequest {
    @NotBlank
    @Size(min = 2, max = 100)
    private String name;
}
```

Response DTO нужен для ответа клиенту:

```java
public class TopicResponse {
    private Long id;
    private String name;
}
```

Лучше разделять:

```text
CreateTopicRequest
UpdateTopicRequest
TopicResponse
```

Один общий `TopicDto` на все случаи часто становится грязным, потому что создание, обновление и ответ имеют разные правила.

### Mapper

Mapper переводит entity в DTO и обратно.

Mapper не должен:

- ходить в базу;
- проверять дубликаты;
- бросать бизнес-исключения;
- решать бизнес-правила.

Mapper - простой переводчик структуры.

### Формулировка На Собесе

DTO отделяет внешний контракт API от внутренней модели хранения.
Entity управляется JPA/Hibernate и отражает структуру БД, а DTO описывает, что клиент может отправить и получить.

## 3. Валидация

Есть два уровня валидации.

### DTO Validation

Проверяет формат входных данных:

- строка не пустая;
- длина в допустимом диапазоне;
- число положительное;
- email похож на email.

Пример:

```java
@NotBlank
@Size(min = 2, max = 100)
private String name;
```

### Service Validation

Проверяет бизнес-правила:

- пользователь существует;
- тема существует;
- имя не занято;
- нельзя создать дубликат;
- нельзя создать второй активный профиль;
- нельзя обновить сущность, которой нет.

Пример:

```java
if (topicRepository.existsByName(topicName)) {
    throw new TopicAlreadyExistsException("Тема уже существует.");
}
```

### Формулировка На Собесе

Простую проверку формата я держу в DTO через Bean Validation.
Бизнес-проверки, которым нужен доступ к базе или правила приложения, держу в service layer.

## 4. Exceptions И HTTP Status

Backend должен возвращать не просто ошибку, а правильный смысл ошибки.

### Основные Статусы

```text
201 CREATED     - сущность создана
200 OK          - успешное чтение или обновление
204 NO_CONTENT  - успешное удаление без тела ответа
400 BAD_REQUEST - неправильный ввод
404 NOT_FOUND   - сущность не найдена
409 CONFLICT    - конфликт или дубликат
```

### Примеры

Неправильный id:

```text
400 BAD_REQUEST
```

Тема не найдена:

```text
404 NOT_FOUND
```

Тема с таким именем уже существует:

```text
409 CONFLICT
```

### Формулировка На Собесе

Exception в service описывает бизнес-проблему, а `GlobalExceptionHandler` переводит ее в HTTP-ответ.
Так controller остается чистым, а обработка ошибок централизована.

## 5. Transactional

`@Transactional` нужен, чтобы операция выполнилась целиком или откатилась целиком.

Пример:

```java
@Transactional
public AnswerResponse addAnswer(...) {
    Question question = findQuestionOrThrow(questionId);
    Answer answer = new Answer();
    answer.setQuestion(question);
    return mapper.toResponse(answerRepository.save(answer));
}
```

Если внутри метода произойдет ошибка, изменения не должны сохраниться частично.

### readOnly

Для чтения ставят:

```java
@Transactional(readOnly = true)
```

Это показывает намерение метода: он только читает данные.
Hibernate может работать экономнее, а код становится понятнее.

### Где Ставить Transactional

Обычно `@Transactional` ставят на service-методы, а не на controller.

Причина:

- service описывает бизнес-операцию;
- одна операция может включать несколько repository-вызовов;
- транзакция должна покрывать весь бизнес-сценарий.

## 6. JPA И Связи

Entity - это объектное представление таблиц.

Пример:

```text
User 1 -> N Question
Topic 1 -> N Question
Question 1 -> N Answer
AiProfile 1 -> N Answer
```

### Важная Идея

Когда сохраняешь связанную сущность, нужно передавать реальные entity:

```java
question.setUser(user);
question.setTopic(topic);
```

Не нужно вручную хардкодить id после `save`.
База сама выдает id, а Hibernate кладет его в entity.

Правильно:

```java
User user = userRepository.save(new User("Yakov"));
Long userId = user.getId();
```

Плохо:

```java
user.setId(1L);
```

### Почему Нельзя Хардкодить ID В Тестах

Потому что id генерирует база.
Сегодня он может быть `1`, завтра `5`, особенно если тестов много или sequence не сбросился.

## 7. Repository Tests

Repository-тесты проверяют работу SQL/JPA с реальной базой.

Они нужны, когда важно проверить:

- уникальные ограничения;
- кастомные query;
- join;
- projection/DTO из запроса;
- корректную работу PostgreSQL, а не H2.

### Что Проверять В Repository Test

Хороший repository-тест проверяет не только размер списка:

```java
assertThat(history).hasSize(2);
```

Но и поля:

```java
assertThat(history)
        .extracting(UserHistoryItem::getUsername)
        .containsExactly("Yakov", "Yakov");
```

Так ты проверяешь, что join реально собрал данные из нужных таблиц.

### Формулировка На Собесе

Repository-тесты я пишу для запросов, ограничений и SQL-поведения, которое важно проверить на реальной БД.
Если метод стандартный, например простой `findById`, отдельно тестировать его обычно не нужно.

## 8. Unit Tests Для Service

Service unit-тесты проверяют бизнес-логику без реальной БД.

Обычно там используют Mockito:

```java
@Mock
private TopicRepository topicRepository;

@InjectMocks
private TopicServiceImpl topicService;
```

### Что Проверять В Service Test

Нужно проверять:

- успешный сценарий;
- неправильный ввод;
- сущность не найдена;
- дубликат;
- что repository вызывается правильно;
- что save/delete не вызывается при ошибке.

Пример:

```java
verify(topicRepository, never()).save(any());
```

### Зачем Это Нужно

Service test быстро показывает, не сломал ли ты бизнес-логику при рефакторинге.

## 9. Refactoring

Рефакторинг - это изменение структуры кода без изменения поведения.

Хороший рефакторинг:

- убирает дублирование;
- делает public-методы короче;
- улучшает имена;
- не меняет бизнес-логику;
- подтверждается тестами.

Плохой рефакторинг:

- выносит методы ради выноса;
- делает код сложнее;
- случайно меняет порядок проверок;
- ломает сообщения ошибок;
- смешивает много задач в одном коммите.

### Что Можно Выносить В Private Methods

Можно выносить:

- повторяющуюся валидацию id;
- нормализацию строк;
- поиск entity или `throw NotFoundException`;
- проверку длины;
- проверку дубликата.

Пример:

```java
private Topic findTopicOrThrow(Long id) {
    return topicRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Тема не найдена."));
}
```

### Как Проверять Себя

После рефакторинга спроси себя:

- public-метод стал читаться проще?
- поведение осталось тем же?
- тесты проходят?
- не появилась ли слишком абстрактная логика?
- имя private-метода понятно без комментария?

## 10. Git Workflow

Нормальный рабочий цикл:

```text
изменил код
запустил тесты
посмотрел git diff
добавил только нужные файлы
сделал commit
push
```

### Перед Commit

Проверить:

```bash
git status
git diff
```

Если менял сервисы:

```bash
mvn test -Dtest='*ServiceImplTest'
```

Если менял repository:

```bash
mvn test -Dtest='*RepositoryTest'
```

### Хороший Commit

Commit должен быть про одну понятную задачу.

Примеры:

```text
refactor topic service validation
add user history repository test
fix duplicate topic validation
```

## 11. Docker, Testcontainers И Postman

Важно разделять:

```text
Testcontainers - контейнеры для тестов.
Docker Compose - контейнеры для локального запуска.
Postman - HTTP-клиент для приложения.
```

### Как Работает Postman

Postman не ходит напрямую в PostgreSQL.

Схема такая:

```text
Postman -> Spring Boot app -> PostgreSQL
```

Чтобы Postman работал:

- Docker запущен;
- PostgreSQL контейнер запущен;
- Spring Boot приложение запущено;
- приложение подключилось к БД;
- Postman отправляет запрос на `http://localhost:8080/...`.

### Testcontainers

Testcontainers поднимает PostgreSQL автоматически для тестов.
Это не то же самое, что база для ручной работы через Postman.

## 12. Что Должен Уметь Объяснить Junior

Минимальный список тем:

- чем `ArrayList` отличается от `LinkedList`;
- контракт `equals` и `hashCode`;
- как работает `HashMap` на базовом уровне;
- interface vs abstract class;
- инкапсуляция, наследование, полиморфизм;
- checked vs unchecked exceptions;
- `Optional` и почему `get()` без проверки опасен;
- Stream API: `map`, `filter`, `collect`;
- `final`, `static`;
- что такое Spring Bean;
- что такое DI;
- зачем нужен constructor injection;
- что делает `@Transactional`;
- зачем нужны DTO;
- зачем нужен service layer;
- зачем нужны repository tests и service tests;
- что такое HTTP status codes.

## 13. Сильные Формулировки Для Собеса

### DTO

DTO отделяет внешний контракт API от внутренней модели хранения.
Entity отражает структуру БД и управляется Hibernate, поэтому я не возвращаю entity напрямую из controller.

### Service

Service layer содержит бизнес-логику приложения.
Controller работает с HTTP, Repository работает с БД, а Service связывает сценарий целиком.

### Repository Test

Repository-тесты нужны там, где я хочу проверить реальное SQL/JPA-поведение: join, unique constraint, projection, custom query.

### Transactional

Транзакция гарантирует, что бизнес-операция выполнится целиком или откатится целиком.
Обычно транзакцию ставят на service-метод, потому что он описывает бизнес-сценарий.

### Refactoring

Рефакторинг меняет структуру кода без изменения поведения.
Я делаю его маленькими шагами и проверяю тестами.

## 14. Практический Чек-Лист Для Любого Endpoint

Когда добавляешь новый endpoint, проверь:

- есть request DTO;
- есть response DTO;
- controller не содержит бизнес-логику;
- service содержит сценарий;
- repository не содержит бизнес-правила;
- есть правильные exceptions;
- HTTP status соответствует смыслу;
- есть тест на успешный сценарий;
- есть тест на основные ошибки;
- в Postman можно проверить happy path и bad path.

## 15. Как Учиться Дальше

Хороший ритм:

```text
фича -> тесты -> рефакторинг -> commit
фича -> тесты -> рефакторинг -> commit
```

Не нужно бесконечно только рефакторить или только писать тесты.
Проект должен двигаться пользовательскими сценариями.

Следующие полезные темы:

- pagination и sorting;
- Flyway/Liquibase migrations;
- Spring Security basics;
- JWT;
- validation groups;
- integration tests для controller;
- Docker Compose для локальной разработки;
- logging;
- profiles: `dev`, `test`, `prod`;
- OpenAPI/Swagger;
- N+1 problem в Hibernate;
- optimistic locking;
- database indexes.

## 16. Мой Прогресс

Этот раздел нужен, чтобы отслеживать рост по фактам, а не по ощущениям.

После каждой backend-задачи заполняй короткую запись:

```text
Дата:
Задача:

Что я сделал:
- 

Какие слои были затронуты:
- controller:
- service:
- repository:
- security:
- config:
- database:

Что я понял:
- 

Что сначала понял неправильно:
- 

Какие edge cases есть:
- 

Какие HTTP-статусы важны:
- 

Что я могу объяснить без подсказки:
- 

Что нужно повторить:
- 
```

### 2026-08-01 - Internal API Key

Задача:

```text
Защитить внутренний endpoint /api/internal/** через service-to-service API key.
```

Что было сделано:

- добавлен `InternalApiProperties` для чтения `internal.api-key`;
- добавлен `InternalApiKeyFilter`;
- filter проверяет header `X-Internal-Api-Key`;
- `/api/internal/**` пропускается без user JWT, потому что API key уже проверяется раньше;
- Docker Compose передает `JAVA_INTERNAL_API_KEY` в backend;
- `.env.example` показывает dev-переменную для локального запуска;
- при неверном ключе filter возвращает `401 UNAUTHORIZED` с JSON body.

Что понял:

- security-проверку нельзя класть в controller;
- filter работает до controller;
- `filterChain.doFilter(request, response)` пропускает запрос дальше по цепочке;
- `401` означает, что клиент не прошел authentication;
- `403` означает, что клиент распознан, но у него нет прав;
- env variable хранит секрет внутри сервиса;
- HTTP header передает секрет между сервисами;
- `ObjectMapper` превращает Java object в JSON.

Что путал:

- authentication и authorization;
- env variable и HTTP header;
- `permitAll()` и публичный endpoint.

Правильная формулировка:

```text
/api/internal/** не требует user JWT, потому что доступ к этому path проверяется кастомным InternalApiKeyFilter через X-Internal-Api-Key.
```

Что повторить:

- Spring Security filter chain;
- порядок `requestMatchers`;
- `AuthenticationEntryPoint`;
- service-to-service authentication: API key, service JWT, OAuth2 Client Credentials, mTLS.
