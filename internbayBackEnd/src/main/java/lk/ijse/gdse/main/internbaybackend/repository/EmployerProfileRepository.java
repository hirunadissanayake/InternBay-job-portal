package lk.ijse.gdse.main.internbaybackend.repository;

import lk.ijse.gdse.main.internbaybackend.entity.EmployerProfile;
import lk.ijse.gdse.main.internbaybackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployerProfileRepository extends JpaRepository<EmployerProfile, Long> {

    /**
     * Find employer profile by user
     */
    Optional<EmployerProfile> findByUser(User user);

    /**
     * Find employer profile by user email
     */
    @Query("SELECT ep FROM EmployerProfile ep WHERE ep.user.email = :email")
    Optional<EmployerProfile> findByUserEmail(@Param("email") String email);

    /**
     * Check if employer profile exists for user
     */
    boolean existsByUser(User user);

    /**
     * Delete employer profile by user
     */
    void deleteByUser(User user);
}