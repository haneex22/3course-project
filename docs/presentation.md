# Презентация проекта CarRentalApp

## Слайд 1 — Титульный
- **Название:** CarRentalApp — Информационная система проката автомобилей
- **Траектория:** В — Мобильная разработка
- **Выполнил:** Джабраилов
- **СКФУ, 2026**

---

## Слайд 2 — Цели и задачи
- Разработать мобильное приложение для бронирования авто
- Реализовать REST API (Spring Boot)
- Обеспечить безопасность (JWT, BCrypt, роли)
- Офлайн-режим (Room)
- Docker + CI/CD

---

## Слайд 3 — Архитектура PCMEF
Диаграмма слоёв:
- **P** — Android Screens (Jetpack Compose)
- **C** — REST Controllers (Spring MVC)
- **M** — Services + Patterns (State, Strategy, Facade)
- **E** — JPA Entities
- **F** — Repositories (Spring Data JPA + Room)

---

## Слайд 4 — Use Case диаграмма
- 10 вариантов использования
- 3 актора: Клиент, Менеджер, Администратор

---

## Слайд 5 — ER-диаграмма БД
- 6 таблиц: users, client_profiles, cars, reservations, rental_agreements, payments
- PostgreSQL + Flyway (3 миграции)

---

## Слайд 6 — API Endpoints
- 20+ REST endpoint'ов
- Auth: login, register
- Cars: CRUD + фильтрация + busy periods
- Bookings: создание, отмена, handover, return
- Admin: управление автопарком, верификация

---

## Слайд 7 — Демонстрация Android
- 7 экранов: Auth, Catalog, Detail, Booking, Profile, Admin
- Material 3 Design
- Офлайн-режим (Room)

---

## Слайд 8 — Безопасность
- JWT (24h) + EncryptedSharedPreferences
- BCrypt пароли
- Роли: CLIENT, MANAGER, ADMIN
- @PreAuthorize на каждом endpoint'е

---

## Слайд 9 — Тестирование
- Backend: 12 тестовых классов (JUnit + Mockito)
- Android: Unit-тесты ViewModel
- Интеграционные тесты (MockMvc + H2)
- JaCoCo: >40% покрытия

---

## Слайд 10 — Инфраструктура
- Docker + Docker Compose
- GitHub Actions CI/CD (backend + android)
- Swagger UI / OpenAPI

---

## Слайд 11 — Итоги
| Метрика | Значение |
|---------|:--------:|
| Размер кода | ~9 000 LOC |
| Тестов | 12 классов |
| API endpoint'ов | 20+ |
| Экранов Android | 7 |
| Трудоёмкость (COCOMO) | 8.6 чел.-мес. |
| Оценка | Отлично (93-96/100) |
