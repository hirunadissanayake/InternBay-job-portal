package lk.ijse.gdse.main.internbaybackend.repository;

import lk.ijse.gdse.main.internbaybackend.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category,Long> {

}
