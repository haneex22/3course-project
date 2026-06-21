# Архитектурная диаграмма PCMEF

![PCMEF Architecture](../images/diagram_PCMEF_Architecture_1.png)

*Исходный код диаграммы:*

```plantuml
@startuml PCMEF_Architecture
skinparam packageStyle rectangle
skinparam linetype ortho

' === SERVER SIDE ===
package "BACKEND (Spring Boot)" {

  package "Control (REST Controllers)" #LightBlue {
    class AuthController
    class CarController
    class BookingController
    class AdminController
  }

  package "Mediator (Services + Patterns)" #LightGreen {
    class AuthServiceImpl
    class CarServiceImpl
    class ReservationServiceImpl
    class PaymentServiceImpl
    class NotificationServiceImpl
    class BookingFacade <<Facade>>
    interface PricingStrategy <<Strategy>>
    class StandardPricingStrategy
    class WeekendPricingStrategy
  }

  package "Entity (JPA + State)" #LightYellow {
    class User
    class Car
    class Reservation
    class RentalAgreement
    class Payment
    class ClientProfile
    interface ReservationState <<State>>
    class PendingState
    class ConfirmedState
    class ActiveState
    class CancelledState
  }

  package "Foundation (Repositories)" #LightCoral {
    interface UserRepository
    interface CarRepository
    interface ReservationRepository
    interface RentalAgreementRepository
    interface PaymentRepository
  }

  package "Database" #Gray {
    database PostgreSQL
  }
}

' === CLIENT SIDE ===
package "ANDROID CLIENT (Kotlin + Compose)" {

  package "Presentation (Screens)" #LightBlue {
    class LoginScreen
    class CatalogScreen
    class CarDetailScreen
    class BookingScreen
    class ProfileScreen
  }

  package "StateManagement (ViewModels)" #LightGreen {
    class AuthViewModel
    class CatalogViewModel
    class BookingViewModel
    class ProfileViewModel
  }

  package "ApiClient (Retrofit)" #LightYellow {
    class ApiClient
    class AuthApiService
    class CarApiService
    class BookingApiService
    class JwtInterceptor
  }

  package "LocalCache (Room)" #LightCoral {
    class AppDatabase
    class CarDao
    class CarCacheEntity
    class TokenStorage
  }
}

' Dependencies (top-down only)
Control ..> Mediator : uses
Mediator ..> Entity : uses
Mediator ..> Foundation : uses
Foundation ..> Database : SQL

Presentation ..> StateManagement : observes StateFlow
StateManagement ..> ApiClient : calls
StateManagement ..> LocalCache : reads/writes
ApiClient ..> Control : HTTP/REST
@enduml
```
