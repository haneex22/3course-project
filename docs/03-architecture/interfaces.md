# Интерфейсы между слоями PCMEF

## Control → Mediator

| Точка взаимодействия | Метод | Описание |
|----------------------|-------|----------|
| `BookingController` → `BookingFacade` | `processCompleteBooking(clientId, BookingRequest)` | Создание бронирования через Facade |
| `CarController` → `CarService` | `findAvailableCars(carClass, start, end)` | Поиск доступных ТС с фильтрацией |
| `CarController` → `CarService` | `getCarById(id)` | Детальная информация об авто |
| `CarController` → `CarService` | `updateCarStatus(id, CarStatusUpdateRequest)` | Смена статуса |
| `CarController` → `ReservationService` | `getBusyPeriods(carId)` | Занятые периоды авто |
| `AuthController` → `AuthService` | `login(LoginRequest)`, `register(RegisterRequest)` | Аутентификация и регистрация |
| `AdminController` → `CarService` | `createCar(CarCreateRequest)`, `getAllCars()` | Управление автопарком |
| `AdminController` → `CarService` | `updateCar(id, CarCreateRequest)`, `deleteCar(id)` | Редактирование/удаление авто |
| `AdminController` → `ReservationService` | `getAllReservations()`, `getReservationByIdForAdmin(id)` | Просмотр броней |
| `AdminController` → `ReservationService` | `cancelReservation(id)` | Отмена брони админом |
| `AdminController` → `ReservationService` | `handoverCar(id, mileage, fuel, managerId)` | Выдача автомобиля (UC-007) |
| `AdminController` → `ReservationService` | `returnCar(id, mileage, fuel, managerId)` | Приём возврата (UC-008) |
| `AdminController` → `ClientProfileRepository` | `findAllUnverified()`, `findById()` | Верификация клиентов |

## Mediator → Entity

| Взаимодействие | Описание |
|----------------|----------|
| `ReservationServiceImpl` создаёт `Reservation` | Устанавливает статус, клиента, ТС, суммы |
| `BookingFacade` вызывает `reservation.getState()` | Использует паттерн State для смены статуса |
| `ReservationServiceImpl` выбирает `PricingStrategy` | Паттерн Strategy на основе дат периода |
| `ReservationServiceImpl` создаёт `RentalAgreement` | Договор аренды при выдаче авто |

## Mediator → Foundation

| Медиатор | Репозиторий | Метод |
|----------|-------------|-------|
| `ReservationServiceImpl` | `ReservationRepository` | `existsConflictingReservation()`, `findActiveByCarId()`, `save()`, `findAllByOrderByCreatedAtDesc()` |
| `ReservationServiceImpl` | `RentalAgreementRepository` | `findByReservationId()`, `save()` |
| `CarServiceImpl` | `CarRepository` | `findAvailableCars()`, `findByStatus()`, `findById()`, `save()`, `delete()` |
| `AuthServiceImpl` | `UserRepository` | `findByEmail()`, `existsByEmail()`, `save()` |
| `AuthServiceImpl` | `ClientProfileRepository` | `save()` |
| `AdminController` | `ClientProfileRepository` | `findAllUnverified()`, `findById()`, `save()` |

## ApiClient → Control (REST API Контракт)

| Endpoint | Method | Auth | Описание |
|----------|--------|------|----------|
| `/api/v1/auth/login` | POST | Public | Логин, возвращает JWT |
| `/api/v1/auth/register` | POST | Public | Регистрация |
| `/api/v1/cars` | GET | Bearer | Каталог с фильтрами |
| `/api/v1/cars/{id}` | GET | Bearer | Карточка ТС |
| `/api/v1/cars/{id}/busy` | GET | Bearer | Занятые даты |
| `/api/v1/cars/{id}/status` | PUT | MANAGER/ADMIN | Смена статуса |
| `/api/v1/bookings` | POST | CLIENT | Создать бронирование |
| `/api/v1/bookings/my` | GET | CLIENT | Мои бронирования |
| `/api/v1/bookings/{id}` | GET | CLIENT | Детали брони |
| `/api/v1/bookings/{id}/cancel` | POST | CLIENT | Отменить бронь (клиент) |
| `/api/v1/admin/cars` | GET | MANAGER/ADMIN | Весь автопарк |
| `/api/v1/admin/cars` | POST | ADMIN | Добавить ТС |
| `/api/v1/admin/cars/{id}` | PUT | ADMIN | Редактировать ТС |
| `/api/v1/admin/cars/{id}` | DELETE | ADMIN | Удалить ТС |
| `/api/v1/admin/bookings` | GET | MANAGER/ADMIN | Все бронирования |
| `/api/v1/admin/bookings/{id}` | GET | MANAGER/ADMIN | Детали брони |
| `/api/v1/admin/bookings/{id}/cancel` | POST | ADMIN | Отменить бронь |
| `/api/v1/admin/bookings/{id}/handover` | POST | MANAGER | Выдать авто (UC-007) |
| `/api/v1/admin/bookings/{id}/return` | POST | MANAGER | Принять возврат (UC-008) |
| `/api/v1/admin/clients/unverified` | GET | MANAGER/ADMIN | Неверифицированные клиенты |
| `/api/v1/admin/clients/{userId}/verify` | PUT | ADMIN | Верифицировать клиента |
