# Интерфейсы между слоями PCMEF

## Control → Mediator

| Точка взаимодействия | Метод | Описание |
|----------------------|-------|----------|
| `BookingController` → `BookingFacade` | `processCompleteBooking(clientId, BookingRequest)` | Создание бронирования через Facade |
| `CarController` → `CarService` | `findAvailableCars(carClass, start, end)` | Поиск доступных ТС с фильтрацией |
| `AuthController` → `AuthService` | `login(LoginRequest)`, `register(RegisterRequest)` | Аутентификация и регистрация |
| `AdminController` → `CarService` | `createCar(CarCreateRequest)`, `getAllCars()` | Управление автопарком |

## Mediator → Entity

| Взаимодействие | Описание |
|----------------|----------|
| `ReservationServiceImpl` создаёт `Reservation` | Устанавливает статус, клиента, ТС, суммы |
| `BookingFacade` вызывает `reservation.getState()` | Использует паттерн State для смены статуса |
| `ReservationServiceImpl` выбирает `PricingStrategy` | Паттерн Strategy на основе дат периода |

## Mediator → Foundation

| Медиатор | Репозиторий | Метод |
|----------|-------------|-------|
| `ReservationServiceImpl` | `ReservationRepository` | `existsConflictingReservation()`, `save()` |
| `CarServiceImpl` | `CarRepository` | `findAvailableCars()`, `findByStatus()` |
| `AuthServiceImpl` | `UserRepository` | `findByEmail()`, `existsByEmail()` |

## ApiClient → Control (REST API Контракт)

| Endpoint | Method | Auth | Описание |
|----------|--------|------|----------|
| `/api/v1/auth/login` | POST | Public | Логин, возвращает JWT |
| `/api/v1/auth/register` | POST | Public | Регистрация |
| `/api/v1/cars` | GET | Bearer | Каталог с фильтрами |
| `/api/v1/cars/{id}` | GET | Bearer | Карточка ТС |
| `/api/v1/cars/{id}/status` | PUT | MANAGER/ADMIN | Смена статуса |
| `/api/v1/bookings` | POST | CLIENT | Создать бронирование |
| `/api/v1/bookings/my` | GET | CLIENT | Мои бронирования |
| `/api/v1/bookings/{id}` | GET | CLIENT | Детали брони |
| `/api/v1/admin/cars` | POST | ADMIN | Добавить ТС |
| `/api/v1/admin/cars` | GET | MANAGER/ADMIN | Весь автопарк |
