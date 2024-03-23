# Форум на Spring

Этот проект представляет собой простой форум, реализованный с использованием Spring Framework. Форум позволяет пользователям создавать топики (темы) и публиковать сообщения в этих топиках. Основная функциональность включает в себя выполнение CRUD-операций с топиками и сообщениями, а также предоставление REST-API для взаимодействия с форумом.

## Начало работы

Чтобы запустить проект локально, следуйте приведенным ниже инструкциям.

### Предварительные требования

Убедитесь, что у вас установлены:

- JDK 11 или выше
- Maven 3.6.3 или выше

### Установка

1. Клонируйте репозиторий проекта:

```bash
git clone https://github.com/SebastynY/forum.git
cd forum-spring
```
2. Соберите проект с помощью Maven:

```bash
mvn clean install
```

3. Запустите приложение:
```bash
mvn spring-boot:run
```

### Использование

После запуска приложения доступ к REST-API можно получить через следующие HTTP-запросы:

1. Получение списка топиков:
```bash
curl http://localhost:8080/api/topics
```
2. Получение списка топиков:
```bash
curl http://localhost:8080/api/topics/{topicId}/messages
```
3. Создание топика с первым сообщением:
```bash
curl -X POST http://localhost:8080/api/topics -H 'Content-Type: application/json' -d '{
  "topicName": "Тестовая тема",
  "message": {
    "id": "2603eb0f-9295-402b-a958-8934bf400119",
    "text": "Первое сообщение",
    "author": "Ivan",
    "created": "2024-12-03T15:23:17+03:00"
  }
}'
```
4. Создание сообщения в топике:
```bash
curl -X POST http://localhost:8080/api/topics/{topicId}/messages -H 'Content-Type: application/json' -d '{
  "id": "2603eb0f-9295-402b-a958-8934bf400119",
  "text": "Первое сообщение",
  "author": "Ivan",
  "created": "2024-12-03T15:23:17+03:00"
}'
```
5. Редактирование сообщения:
```bash
curl -X PUT http://localhost:8080/api/messages/{messageId} -H 'Content-Type: application/json' -d '{
  "id": "2603eb0f-9295-402b-a958-8934bf400119",
  "text": "Первое сообщение",
  "author": "Ivan",
  "created": "2024-12-03T15:23:17+03:00"
}'
```

6. Удаление сообщения:
```bash
curl -X DELETE http://localhost:8080/api/messages/{messageId}
```