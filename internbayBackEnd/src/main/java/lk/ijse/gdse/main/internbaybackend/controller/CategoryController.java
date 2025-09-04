package lk.ijse.gdse.main.internbaybackend.controller;

import lk.ijse.gdse.main.internbaybackend.dto.CategoryDTO;
import lk.ijse.gdse.main.internbaybackend.dto.ResponsDto;
import lk.ijse.gdse.main.internbaybackend.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/category")
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping("/getAll")
    public ResponseEntity<ResponsDto> getAllCategories(){
        List<CategoryDTO> categoryDTOS = categoryService.findAll();
        return ResponseEntity.ok(new ResponsDto(200,"OK",categoryDTOS));
    }
}
