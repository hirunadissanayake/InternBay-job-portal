package lk.ijse.gdse.main.internbaybackend.repository;

import lk.ijse.gdse.main.internbaybackend.entity.CandidateProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CandidateProfileRepository extends JpaRepository<CandidateProfile, Long> {
    Optional<CandidateProfile> findByUserUserId(Long userId);
    boolean existsByUserUserId(Long userId);
}