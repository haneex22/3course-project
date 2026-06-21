package ru.skfu.carrental.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "rental_agreements")
public class RentalAgreement {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false, unique = true)
    private Reservation reservation;

    @Column(name = "agreement_number", nullable = false, unique = true)
    private String agreementNumber;

    @Column(name = "signed_at", nullable = false)
    private LocalDateTime signedAt = LocalDateTime.now();

    @Column(name = "initial_mileage")
    private long initialMileage = 0;

    @Column(name = "initial_fuel_level")
    private int initialFuelLevel = 100;

    @Column(name = "final_mileage")
    private Long finalMileage;

    @Column(name = "final_fuel_level")
    private Integer finalFuelLevel;

    @Column(name = "is_active")
    private boolean active = true;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public Reservation getReservation() { return reservation; }
    public void setReservation(Reservation reservation) { this.reservation = reservation; }
    public String getAgreementNumber() { return agreementNumber; }
    public void setAgreementNumber(String agreementNumber) { this.agreementNumber = agreementNumber; }
    public LocalDateTime getSignedAt() { return signedAt; }
    public void setSignedAt(LocalDateTime signedAt) { this.signedAt = signedAt; }
    public long getInitialMileage() { return initialMileage; }
    public void setInitialMileage(long initialMileage) { this.initialMileage = initialMileage; }
    public int getInitialFuelLevel() { return initialFuelLevel; }
    public void setInitialFuelLevel(int initialFuelLevel) { this.initialFuelLevel = initialFuelLevel; }
    public Long getFinalMileage() { return finalMileage; }
    public void setFinalMileage(Long finalMileage) { this.finalMileage = finalMileage; }
    public Integer getFinalFuelLevel() { return finalFuelLevel; }
    public void setFinalFuelLevel(Integer finalFuelLevel) { this.finalFuelLevel = finalFuelLevel; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
