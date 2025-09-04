
package lk.ijse.gdse.main.internbaybackend.service.impl;

import lk.ijse.gdse.main.internbaybackend.dto.CategoryDTO;
import lk.ijse.gdse.main.internbaybackend.repository.CategoryRepository;
import lk.ijse.gdse.main.internbaybackend.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<CategoryDTO> findAll() {
        return categoryRepository.findAll()
                .stream()
                .map(category -> modelMapper.map(category, CategoryDTO.class))
                .collect(Collectors.toList());
    }
}