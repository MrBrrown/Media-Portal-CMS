# Media Portal CMS

Система управления контентом для мультиформатного медиа-портала. Бэкенд приложение на Spring Boot с поддержкой статей, видео и подкастов.

## Технологический стек

- **Java 17**
- **Spring Boot 3.2.0**
- **PostgreSQL** - основная база данных для контента и пользователей
- **MongoDB** - хранение комментариев и JWT токенов
- **JWT** - аутентификация
- **Docker** - контейнеризация

## Функциональность

### Основные возможности

- Управление пользователями (регистрация, аутентификация)
- CRUD операции для статей
- CRUD операции для видео
- CRUD операции для подкастов
- Система комментариев с поддержкой вложенности
- JWT аутентификация с хранением токенов в MongoDB

- **MongoDB для комментариев**: вложенные комментарии с гибкой схемой документов
- **MongoDB для JWT токенов**: управление сессиями и возможность отзыва токенов

## Развёртывание

### Требования

- Docker и Docker Compose
- Maven (для локальной разработки)

### Запуск через Docker Compose

1. Клонируйте репозиторий или скопируйте файлы проекта

3. Запустите все сервисы:
```bash
docker-compose up -d
```

4. Дождитесь запуска всех контейнеров (PostgreSQL, MongoDB, приложение)

5. Приложение будет доступно по адресу: `http://localhost:8080`

### Остановка

```bash
docker-compose down
```

## API Документация

После запуска приложения документация Swagger доступна по адресу:
- Swagger UI: `http://localhost:8080/swagger-ui.html` или `http://localhost:8080/swagger-ui/index.html`
## Использование API

### Регистрация пользователя

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "password123"
  }'
```

Ответ:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": 1,
    "username": "testuser",
    "email": "test@example.com",
    "role": "USER"
  }
}
```

### Вход в систему

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123"
  }'
```

### Создание статьи

```bash
curl -X POST http://localhost:8080/api/articles \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "title": "Пример статьи",
    "content": "Содержание статьи...",
    "publicationDate": "2024-12-20T10:00:00"
  }'
```

### Получение всех статей

```bash
curl -X GET http://localhost:8080/api/articles \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Создание видео

```bash
curl -X POST http://localhost:8080/api/videos \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "title": "Пример видео",
    "url": "https://example.com/video.mp4",
    "duration": "PT1H30M"
  }'
```

### Создание подкаста

```bash
curl -X POST http://localhost:8080/api/podcasts \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "title": "Пример подкаста",
    "audioUrl": "https://example.com/podcast.mp3",
    "episodes": ["Эпизод 1", "Эпизод 2"]
  }'
```

### Создание комментария

```bash
curl -X POST http://localhost:8080/api/comments \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "contentId": 1,
    "contentType": "ARTICLE",
    "text": "Отличная статья!"
  }'
```

### Создание вложенного комментария

```bash
curl -X POST http://localhost:8080/api/comments \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "contentId": 1,
    "contentType": "ARTICLE",
    "text": "Согласен!",
    "parentCommentId": "COMMENT_ID"
  }'
```

### Получение комментариев к контенту

```bash
curl -X GET "http://localhost:8080/api/comments?contentId=1&contentType=ARTICLE" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## Базы данных

### PostgreSQL

- **База данных**: `cmsdb`
- **Пользователь**: `cmsuser`
- **Пароль**: `cmspass`
- **Порт**: `5432`

Таблицы:
- `users` - пользователи
- `articles` - статьи
- `videos` - видео
- `podcasts` - подкасты
- `podcast_episodes` - эпизоды подкастов

### MongoDB

- **База данных**: `cmsdb`
- **Порт**: `27017`

Коллекции:
- `comments` - комментарии с вложенностью
- `jwt_tokens` - JWT токены для управления сессиями

## Разработка

### Локальный запуск (без Docker)

1. Убедитесь, что PostgreSQL и MongoDB запущены локально
2. Обновите `application.yml` с правильными настройками подключения
3. Запустите приложение:
```bash
mvn spring-boot:run
```

### Сборка проекта

```bash
mvn clean package
```

### Запуск тестов

```bash
mvn test
```

Для запуска конкретного теста:
```bash
mvn test -Dtest=ArticleControllerTest
```

## Тестирование

Проект включает комплексные тесты для основных компонентов:

- **Контроллеры**: тесты для всех REST endpoints (Auth, Article, Video, Podcast, Comment)
- **Сервисы**: unit-тесты для бизнес-логики (UserService, ArticleService, CommentService)
- **Security**: тесты для JWT токенов

Тесты используют:
- JUnit 5
- Mockito для мокирования зависимостей
- Spring Boot Test для интеграционного тестирования
- Spring Security Test для тестирования защищенных endpoints

