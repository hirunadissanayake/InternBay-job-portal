package lk.ijse.gdse.main.internbaybackend.repository;

import lk.ijse.gdse.main.internbaybackend.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * Find category by name
     * @param name the category name
     * @return Category entity or null if not found
     */
    Category findByName(String name);

    /**
     * Check if category exists by name
     * @param name the category name
     * @return true if exists, false otherwise
     */
    boolean existsByName(String name);
}