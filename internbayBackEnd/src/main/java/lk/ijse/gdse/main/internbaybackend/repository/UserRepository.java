package lk.ijse.gdse.main.internbaybackend.repository;


import lk.ijse.gdse.main.internbaybackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, String> {

    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u WHERE LOWER(u.email) = LOWER(:email)")
    User findByEmail(@Param("email") String email);   // Case-insensitive search
}