# Kotlin API Tests

Отдельный проект для black-box API-тестов backend-приложения `ai_tutor`.

Этот проект должен ходить в backend только через HTTP и не должен импортировать Java-классы из `ai_tutor`.

## Запуск

Сначала запусти backend:

```bash
cd ../ai_tutor
mvn spring-boot:run
```

Потом запусти тесты:

```bash
gradle test
```

Если backend запущен не на `http://localhost:8080`, передай другой адрес:

```bash
gradle test -DbaseUrl=http://localhost:8081
```
