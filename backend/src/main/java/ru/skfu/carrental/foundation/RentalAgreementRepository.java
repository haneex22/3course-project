package ru.skfu.carrental.foundation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.skfu.carrental.entity.RentalAgreement;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RentalAgreementRepository extends JpaRepository<RentalAgreement, UUID> {
    Optional<RentalAgreement> findByReservationId(UUID reservationId);
}
