package com.project.shopapp.controller;

import com.project.shopapp.dtos.CategoryDTO;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.models.Category;
import com.project.shopapp.services.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/categories")
//@Validated
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;
    @PostMapping("")
    public ResponseEntity<?> createCategory(@Valid @RequestBody CategoryDTO categoryDTO, BindingResult result){
        try{
            if (result.hasErrors()){
                List<String> errorMess = result.getFieldErrors().stream().map(FieldError::getDefaultMessage).toList();
                return ResponseEntity.badRequest().body(errorMess);
            }
            categoryService.createCategory(categoryDTO);
            return ResponseEntity.ok("Thêm category thành công: " + categoryDTO.getName());
        }
        catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());

        }

    }
    @GetMapping("")
    public ResponseEntity<?> getAllCategory(){
        List<Category> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCategorybyId(@PathVariable("id") long id){
        Category categories = categoryService.getCategoryById(id);
        if (categories == null){
            return ResponseEntity.badRequest().body("Không có category có id: " + id);
        }
        return ResponseEntity.ok(categories);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateCategory(@PathVariable Long id, @RequestBody CategoryDTO categoryDTO){
        try{
            Category category = categoryService.updateCategory(id, categoryDTO);
            if (category == null){
                return ResponseEntity.badRequest().body("Không có category có id: " + id);
            }
            return ResponseEntity.ok("Cập nhật thành công");
        }
        catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/delete/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long id){
        try {
            categoryService.deleteCategory(id);
            return ResponseEntity.ok("Xóa thành công category " + id);

        } catch (DataNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
