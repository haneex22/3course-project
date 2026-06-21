# Доменная модель

```plantuml
@startuml DomainModel
skinparam classAttributeIconSize 0

class User {
  id: UUID
  email: String
  passwordHash: String
  role: UserRole
  registrationDate: LocalDateTime
}

class ClientProfile {
  userId: UUID
  isVerified: Boolean
  bonusBalance: Int
  passportSeries: String
  passportNumber: String
  licenseNumber: String
}

class Car {
  id: UUID
  vin: String
  licensePlate: String
  modelName: String
  currentMileage: Long
  fuelLevelPercentage: Int
  carClass: String
  baseDailyRate: BigDecimal
  status: CarStatus
}

class Reservation {
  id: UUID
  startDateTime: LocalDateTime
  endDateTime: LocalDateTime
  status: ReservationStatus
  amount: BigDecimal
  currency: String
}

class RentalAgreement {
  id: UUID
  agreementNumber: String
  signedAt: LocalDateTime
  initialMileage: Long
  initialFuelLevel: Int
  isActive: Boolean
}

class Payment {
  id: UUID
  transactionId: String
  isSuccess: Boolean
  paymentType: PaymentType
  amount: BigDecimal
}

enum UserRole { CLIENT, MANAGER, ADMIN }
enum CarStatus { AVAILABLE, RESERVED, RENTED, MAINTENANCE }
enum ReservationStatus { PENDING, CONFIRMED, ACTIVE, CANCELLED, COMPLETED }
enum PaymentType { CARD, CASH, ONLINE_TRANSFER, BONUS }

User "1" -- "0..1" ClientProfile
ClientProfile "1" -- "0..*" Reservation : creates
Car "1" -- "0..*" Reservation : subject of
Reservation "1" -- "0..1" RentalAgreement
RentalAgreement "1" -- "0..*" Payment
@enduml
```
