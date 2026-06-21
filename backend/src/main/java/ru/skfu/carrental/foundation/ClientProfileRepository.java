package ru.skfu.carrental.foundation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.skfu.carrental.entity.ClientProfile;

import java.util.List;
import java.util.UUID;

@Repository
public interface ClientProfileRepository extends JpaRepository<ClientProfile, UUID> {
    @Query("SELECT cp FROM ClientProfile cp WHERE cp.verified = false")
    List<ClientProfile> findAllUnverified();
}
