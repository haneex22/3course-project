# Информационная система проката автомобилей 🚗

[Java](https://adoptium.net/)
[Kotlin](https://kotlinlang.org/)
[Spring Boot](https://spring.io/projects/spring-boot)
[License](LICENSE)
[CI Pipeline](https://github.com/haneex22/3course-project/actions)

## Описание проекта

**CarRentalApp** — мобильное приложение для аренды автомобилей, позволяющее пользователям регистрироваться, просматривать каталог автомобилей, оформлять бронирования и управлять ими, а администраторам — управлять автопарком и пользователями.  
Траектория В (Мобильная разработка), СКФУ, 2026.

**Автор:** Джабраилов Бекхан Магомедович  
**Группа:** ПИЖ-б-о-23-1  
**Траектория:** Mobile  
**Дата начала:** 15.02.2026  
**Дата сдачи:** [ДД.ММ.ГГГГ]

## Стек технологий


| Компонент      | Технология                                                      |
| -------------- | --------------------------------------------------------------- |
| Backend        | Java 17 + Spring Boot 3.2 + Spring Security                     |
| Database       | PostgreSQL 16                                                   |
| ORM            | Spring Data JPA / Hibernate                                     |
| API            | REST                                                            |
| Auth           | JWT + BCrypt                                                    |
| Mobile         | Android (Kotlin + Jetpack Compose + Material 3)                 |
| Network        | Retrofit 2 + OkHttp + Coil                                      |
| Local DB       | Room (офлайн-кэш)                                               |
| Архитектура    | PCMEF (Presentation → Control → Mediator → Entity → Foundation) |
| Инфраструктура | Docker + Docker Compose                                         |


## Структура проекта

```
📦 CarRentalApp
├── 📱 app/                          # Android-клиент
│   ├── apiclient/                   # Retrofit, API-сервисы, JWT Interceptor
│   ├── localcache/                  # Room (AppDatabase, DAO, TokenStorage)
│   ├── model/                       # DTO запросов и ответов
│   ├── presentation/                # UI (Jetpack Compose)
│   │   ├── admin/                   # Панель администратора и менеджера
│   │   ├── auth/                    # Авторизация и регистрация
│   │   ├── booking/                 # Бронирование автомобиля
│   │   ├── catalog/                 # Каталог и карточка автомобиля
│   │   ├── common/                  # Общие UI-компоненты
│   │   └── profile/                 # Профиль пользователя
│   ├── statemanagement/             # ViewModel, StateFlow, MVVM
│   └── ui/theme/                    # Material 3 (цвета, типографика, тема)
│
├── 🖥️ backend/                      # Серверная часть (Spring Boot)
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/ru/skfu/carrental/
│   │   │   │   ├── control/         # REST-контроллеры
│   │   │   │   ├── mediator/        # Сервисы, Facade, Strategy
│   │   │   │   ├── entity/          # JPA-сущности
│   │   │   │   │   ├── enums/       # Перечисления
│   │   │   │   │   └── state/       # Паттерн State
│   │   │   │   ├── foundation/      # Spring Data JPA репозитории
│   │   │   │   ├── security/        # JWT, SecurityConfig
│   │   │   │   ├── exception/       # GlobalExceptionHandler
│   │   │   │   ├── scheduler/       # Планировщик отмены просроченных броней
│   │   │   │   └── dto/
│   │   │   │       ├── request/     # DTO запросов
│   │   │   │       └── response/    # DTO ответов
│   │   │   └── resources/           # application.yml и ресурсы
│   │   └── test/                    # Unit и Integration тесты
│   └── target/                      # Артефакты сборки и отчёты JaCoCo
│
├── 📚 docs/                         # Документация проекта
│   ├── 01-business-model/           # Паспорт проекта, IDEF0, BUC, SWOT, ROI
│   ├── 02-requirements/             # Use Case, Domain Model, требования
│   ├── 03-architecture/             # PCMEF, ADR, архитектурные решения
│   ├── 04-database/                 # ER-диаграмма, DDL
│   ├── 05-design/                   # Sequence, Class Diagram, GoF
│   ├── 06-testing/                  # План тестирования, JaCoCo
│   ├── 07-refactoring/              # Рефакторинг и паттерны
│   ├── 08-ui/                       # Пользовательский интерфейс
│   ├── 09-api/                      # Документация REST API
│   ├── 10-deployment/               # Развёртывание и администрирование
│   ├── 11-guides/                   # Руководство пользователя, менеджера и администратора
│   ├── 12-project-management/       # Управление проектом
│   ├── 13-final-report/             # Пояснительная записка и презентация
│   ├── images/                      # Скриншоты и диаграммы
│   └── presentation.md              # Презентация к защите
│
├── 📂 .github/workflows/            # GitHub Actions CI/CD
├── 📄 README.md                     # Описание проекта
├── 📄 LICENSE                       # Лицензия
└── 🐳 docker-compose.yml            # Контейнеризация
```

## Быстрый старт 🚀

### Запуск бэкенда (Docker)

```bash
docker-compose up -d
```

или  

```bash
Backend
mvn spring-boot:run
```

### Запуск Android-приложения

1. Откройте папку `app/` в Android Studio
2. Запустите эмулятор Android
3. Приложение автоматически подключится к `http://10.0.2.2:8080`

### Доступные эндпоинты

- Сервер запустится на [http://localhost:8080](http://localhost:8080)
- Swagger UI: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- OpenAPI JSON: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

### Тестовые учётные записи


| Роль          | Email                | Пароль        |
| ------------- | -------------------- | ------------- |
| Администратор | `admin@carrent.ru`   | `password123` |
| Менеджер      | `manager@carrent.ru` | `password123` |
| Клиент        | `client@carrent.ru`  | `password123` |


## Git-статистика 📊

- 👨‍💻 Разработчик: Джабраилов (1 контрибьютор)  
- 📅 Период: 15.02.2026 – 01.06.2026.  
- 📝 Коммитов: 30  
- 👨‍💻 Покрытие тестами (Jacoco): 65%

📸 Скриншоты GitHub

> Commit Activity
> Punch Card

---

## Архитектура (PCMEF) 🏗️

PCMEF Architecture

Проект использует архитектурный паттерн PCMEF:

- **Presentation** — экраны Android (Jetpack Compose)
- **Control** — REST-контроллеры (Spring MVC)
- **Mediator** — бизнес-логика (Services + Facade + Strategy)
- **Entity** — доменные сущности (JPA + State pattern)
- **Foundation** — доступ к данным (JPA Repositories + Room)

## API Endpoints

### Auth (публичные)


| Метод | Path                    | Описание       |
| ----- | ----------------------- | -------------- |
| POST  | `/api/v1/auth/login`    | Аутентификация |
| POST  | `/api/v1/auth/register` | Регистрация    |


### Cars (требуют JWT)


| Метод | Path                       | Роль          | Описание            |
| ----- | -------------------------- | ------------- | ------------------- |
| GET   | `/api/v1/cars`             | Любая         | Каталог с фильтрами |
| GET   | `/api/v1/cars/{id}`        | Любая         | Детали авто         |
| GET   | `/api/v1/cars/{id}/busy`   | Любая         | Занятые даты        |
| PUT   | `/api/v1/cars/{id}/status` | MANAGER/ADMIN | Смена статуса       |


### Bookings (требуют роль CLIENT)


| Метод | Path                           | Описание             |
| ----- | ------------------------------ | -------------------- |
| POST  | `/api/v1/bookings`             | Создать бронирование |
| GET   | `/api/v1/bookings/my`          | Мои бронирования     |
| GET   | `/api/v1/bookings/{id}`        | Детали брони         |
| POST  | `/api/v1/bookings/{id}/cancel` | Отменить бронь       |


### Admin (требуют роль ADMIN/MANAGER)


| Метод  | Path                                    | Роль          | Описание                 |
| ------ | --------------------------------------- | ------------- | ------------------------ |
| GET    | `/api/v1/admin/cars`                    | MANAGER/ADMIN | Весь автопарк            |
| POST   | `/api/v1/admin/cars`                    | ADMIN         | Добавить авто            |
| PUT    | `/api/v1/admin/cars/{id}`               | ADMIN         | Редактировать авто       |
| DELETE | `/api/v1/admin/cars/{id}`               | ADMIN         | Удалить авто             |
| GET    | `/api/v1/admin/bookings`                | MANAGER/ADMIN | Все бронирования         |
| GET    | `/api/v1/admin/bookings/{id}`           | MANAGER/ADMIN | Детали брони             |
| POST   | `/api/v1/admin/bookings/{id}/cancel`    | ADMIN         | Отменить бронь           |
| POST   | `/api/v1/admin/bookings/{id}/handover`  | MANAGER       | Выдать авто (UC-007)     |
| POST   | `/api/v1/admin/bookings/{id}/return`    | MANAGER       | Принять возврат (UC-008) |
| GET    | `/api/v1/admin/clients/unverified`      | MANAGER/ADMIN | Неверифиц. клиенты       |
| PUT    | `/api/v1/admin/clients/{userId}/verify` | ADMIN         | Верифицировать клиента   |


## Документация 📚

## 📚 Документация проекта


| Раздел                         | Файл                                                                            | Описание                                                                |
| ------------------------------ | ------------------------------------------------------------------------------- | ----------------------------------------------------------------------- |
| **Бизнес-модель**              | [README.md](docs/01-business-model/README.md)                                   | Описание этапа бизнес-моделирования                                     |
|                                | [buc-diagram.md](docs/01-business-model/buc-diagram.md)                         | Диаграмма бизнес-прецедентов (BUC)                                      |
|                                | [business-class-model.md](docs/01-business-model/business-class-model.md)       | Модель бизнес-классов                                                   |
|                                | [context-diagram.md](docs/01-business-model/context-diagram.md)                 | Контекстная диаграмма IDEF0                                             |
|                                | [glossary.md](docs/01-business-model/glossary.md)                               | Глоссарий предметной области                                            |
|                                | [swot.md](docs/01-business-model/swot.md)                                       | SWOT-анализ проекта                                                     |
|                                | [roi.md](docs/01-business-model/roi.md)                                         | Оценка экономической эффективности (ROI)                                |
| **Требования**                 | [README.md](docs/02-requirements/README.md)                                     | Описание этапа анализа требований                                       |
|                                | [use-case-diagram.md](docs/02-requirements/use-case-diagram.md)                 | Диаграмма вариантов использования                                       |
|                                | [domain-model.md](docs/02-requirements/domain-model.md)                         | Доменная модель                                                         |
|                                | [use-case-specifications.md](docs/02-requirements/use-case-specifications.md)   | Спецификации вариантов использования                                    |
| **Архитектура**                | [README.md](docs/03-architecture/README.md)                                     | Описание архитектуры проекта                                            |
|                                | [pcmef-diagram.md](docs/03-architecture/pcmef-diagram.md)                       | Архитектура PCMEF                                                       |
|                                | [interfaces.md](docs/03-architecture/interfaces.md)                             | Интерфейсы между слоями                                                 |
|                                | [adr.md](docs/03-architecture/adr.md)                                           | Architectural Decision Records (ADR)                                    |
| **База данных**                | [README.md](docs/04-database/README.md)                                         | Документация базы данных                                                |
|                                | [er-diagram.md](docs/04-database/er-diagram.md)                                 | ER-диаграмма                                                            |
|                                | [ddl.sql](docs/04-database/ddl.sql)                                             | DDL-скрипт PostgreSQL                                                   |
|                                | [wbs-gantt-cocomo.md](docs/04-database/wbs-gantt-cocomo.md)                     | WBS, диаграмма Ганта и оценка COCOMO                                    |
| **Детальное проектирование**   | [README.md](docs/05-design/README.md)                                           | Документация проектирования                                             |
|                                | [sequence-diagrams.md](docs/05-design/sequence-diagrams.md)                     | Диаграммы последовательности                                            |
|                                | [executive-summary.md](docs/05-design/executive-summary.md)                     | Краткое описание архитектурных решений                                  |
| **Тестирование**               | [README.md](docs/06-testing/README.md)                                          | План тестирования, результаты тестов и отчёт JaCoCo                     |
| **Рефакторинг**                | [README.md](docs/07-refactoring/README.md)                                      | Анализ качества кода и применённых паттернов                            |
| **Пользовательский интерфейс** | [README.md](docs/08-ui/README.md)                                               | Описание экранов мобильного приложения                                  |
| **REST API**                   | [README.md](docs/09-api/README.md)                                              | Документация REST API                                                   |
| **Развёртывание**              | [README.md](docs/10-deployment/README.md)                                       | Развёртывание и администрирование системы                               |
| **Руководства**                | [README.md](docs/11-guides/README.md)                                           | Руководства пользователей системы                                       |
|                                | [client-guide.md](docs/11-guides/client-guide.md)                               | Руководство пользователя                                                |
|                                | [manager-guide.md](docs/11-guides/manager-guide.md)                             | Руководство менеджера                                                   |
|                                | [admin-guide.md](docs/11-guides/admin-guide.md)                                 | Руководство администратора                                              |
| **Управление проектом**        | [README.md](docs/12-project-management/README.md)                               | Планирование проекта, WBS, диаграмма Ганта, COCOMO и управление рисками |
| **Итоговые материалы**         | [Пояснительная записка.docx](docs/13-final-report/Пояснительная%20записка.docx) | Итоговая пояснительная записка                                          |
|                                | [Презентация.pptx](docs/13-final-report/Презентация.pptx)                       | Презентация для защиты проекта                                          |


## Реализованные паттерны GoF

- **State** — жизненный цикл бронирования (Pending → Confirmed → Active → Cancelled)
- **Strategy** — ценообразование (StandardPricing / WeekendPricing)
- **Facade** — BookingFacade для сложного процесса бронирования
- **Singleton** — ApiClient, AppDatabase

## Функциональные возможности

- ✅ JWT-аутентификация (BCrypt, роли CLIENT/MANAGER/ADMIN)
- ✅ Каталог автомобилей с фильтрацией по классу и датам
- ✅ Поиск по модели, сортировка по цене
- ✅ Бронирование с проверкой доступности
- ✅ State-паттерн для жизненного цикла бронирования
- ✅ Панель управления автопарком (ADMIN/MANAGER)
- ✅ Офлайн-режим (Room-кэш каталога и бронирований)
- ✅ Pull-to-refresh на всех экранах
- ✅ Docker-контейнеризация
- ✅ Material 3 Design (светлая/тёмная тема)
- ✅ EncryptedSharedPreferences для JWT
- ✅ Swagger UI с JWT Bearer авторизацией
- ✅ GitHub Actions CI/CD
- ✅ Покрытие тестами >40% (12 классов backend + Android)
- ✅ Выдача и приём автомобилей (UC-007, UC-008)
- ✅ Обработка ошибок с понятными сообщениями на русском

### Авторы

Джабраилов Бекхан — разработчик, документация  
Группа ПИЖ-б-о-23-1, email: [bekhan_dzh@mail.ru](mailto:bekhan_dzh@mail.ru), GitHub: haneex22

## Лицензия

Проект распространяется под лицензией [MIT](LICENSE). Учебный проект, СКФУ, 2026.