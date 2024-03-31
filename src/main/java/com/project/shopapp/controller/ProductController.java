package com.project.shopapp.controller;

import com.github.javafaker.Faker;
import com.project.shopapp.dtos.CategoryDTO;
import com.project.shopapp.dtos.ProductDTO;
import com.project.shopapp.dtos.ProductImageDTO;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.models.Product;
import com.project.shopapp.models.ProductImage;
import com.project.shopapp.responses.ProductListResponse;
import com.project.shopapp.responses.ProductResponse;
import com.project.shopapp.services.IProductService;
import com.project.shopapp.services.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("${api.prefix}/products")
@RequiredArgsConstructor
public class ProductController {
    private final IProductService productService;
    @GetMapping("")
    public ResponseEntity<?> getAllProducts(
            @RequestParam("page") int page,
            @RequestParam("limit") int limit
    ){
        //Tạo ra đối tượng pageRequest
        PageRequest pageRequest = PageRequest.of(page, limit, Sort.by("createdAt").descending());
        Page<ProductResponse> productPage = productService.getAllProducts(pageRequest);
        int totalPages = productPage.getTotalPages();
        List<ProductResponse> products = productPage.getContent();
        return ResponseEntity.ok(ProductListResponse.builder()
                        .productResponses(products)
                        .totalPage(totalPages)
                .build());
    }

    @GetMapping("/category/{id}")
    public ResponseEntity<?> getProductsByCategory(
            @RequestParam("page") int page,
            @RequestParam("limit") int limit,
            @PathVariable("id") long categoryId
    ){
        //Tạo ra đối tượng pageRequest
        PageRequest pageRequest = PageRequest.of(page, limit, Sort.by("createdAt").descending());
        Page<ProductResponse> productPage = productService.getProductsByCategory(pageRequest, categoryId);
        int totalPages = productPage.getTotalPages();
        List<ProductResponse> products = productPage.getContent();
        return ResponseEntity.ok(ProductListResponse.builder()
                .productResponses(products)
                .totalPage(totalPages)
                .build());
    }

    @GetMapping("/{id}")
    // PathVariable là lấy giá trị trong ngoặc trên url
    public ResponseEntity<?> getProductsById(@PathVariable("id") long productId){
        try {
            Product product = productService.getProductById(productId);
            ProductResponse productResponse = ProductResponse.builder()
                    .id(product.getId())
                    .name(product.getName())
                    .price(product.getPrice())
                    .description(product.getDescription())
                    .thumnail(product.getThumnail())
                    .categoryName(product.getCategory().getName())
                    .build();
            productResponse.setCreatedAt(product.getCreatedAt());
            productResponse.setUpdatedAt(product.getUpdatedAt());

            // Gửi ảnh về phía client
//            Path imgPath = Paths.get("uploads/" + product.getThumnail());
//            UrlResource urlResource = new UrlResource(imgPath.toUri());
//            if (urlResource.exists()){
//                return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(urlResource);
//            }
//            else {
//                return ResponseEntity.notFound().build();
//            }

            return ResponseEntity.ok(productResponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/image/{thumnail}")
    public ResponseEntity<?> getImageOfProduct(@PathVariable("thumnail") String thumnail){
        try {
            // Gửi ảnh về phía client
            Path imgPath = Paths.get("uploads/" + thumnail);
            UrlResource urlResource = new UrlResource(imgPath.toUri());
            if (urlResource.exists()){
                return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(urlResource);
            }
            else {
                return ResponseEntity.notFound().build();
            }
        }
        catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @PostMapping("")
    public ResponseEntity<?> insertProducts(
            @Valid @RequestBody ProductDTO productDTO,
//            @ModelAttribute("files") MultipartFile file,
            BindingResult result
    ){
        try {
                if (result.hasErrors()) {
                List<String> errorMess = result.getFieldErrors().stream().map(FieldError::getDefaultMessage).toList();
                return ResponseEntity.badRequest().body(errorMess);
            }

            Product newProduct = productService.createProduct(productDTO);

            return ResponseEntity.ok(newProduct);
        }
        catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping(value = "/uploads/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadImages(@ModelAttribute("files") List<MultipartFile> files, @PathVariable("id") long id){
        try {
            Product existingProduct = productService.getProductById(id);
            List<ProductImage> productImages = new ArrayList<>();
            files = files == null ? new ArrayList<MultipartFile>() : files;
            if (files.size() > ProductImage.MAXIMUM_IMAGES_PER_PRODUCT){
                return ResponseEntity.badRequest().body("Bạn chỉ có thể upload tối đa " +ProductImage.MAXIMUM_IMAGES_PER_PRODUCT+" ảnh!");
            }
            for(MultipartFile file:files){
                if (file != null){
                    if (file.getSize() == 0){
                        continue;
                    }
                    if (file.getSize() > 10 * 1024 *1024){
                        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body("File is too large!");
                    }
                    String contentType = file.getContentType();
                    if (contentType == null || !contentType.startsWith("image/")){
                        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body("File must be an image!");
                    }
                    // set thumnail cho sản phẩm khi thêm ảnh mới
                    String filename = storeFile(file);
                    if (existingProduct.getThumnail().equals("")){
                        existingProduct.setThumnail(filename);
                        productService.updateProductThumnail(filename, id);
                    }
                    ProductImage productImage = productService.createProductImage(existingProduct.getId(), ProductImageDTO.builder()
                            .imgeUrl(filename)
                            .build());
                    productImages.add(productImage);
                }
            }
            return ResponseEntity.ok().body(productImages);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }
    // Hàm xử lý lưu file ảnh
    private String storeFile(MultipartFile file) throws IOException {
//        if (!isImageFile(file) || file.getOriginalFilename() == null){
//            throw new IOException("Invalid image format");
//        }
        String filename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        // Khi up load file lên có thể tên trùng nhau nó s bị ghi đè, nên phải tạo ra 1 cái tên file duy nhất bằng random
        String uniqueFilename = UUID.randomUUID().toString() + "_" + filename;
        // Lấy ra path của uploads
        java.nio.file.Path uploadDir = Paths.get("uploads");
        // Kiểm tra thư mục uploadDir tồn tại hay chưa, nếu chưa thì tạo mới, rồi thì thôi
        if (!Files.exists(uploadDir)){
            Files.createDirectories(uploadDir);
        }
        java.nio.file.Path destination = Paths.get(uploadDir.toString(), uniqueFilename);
        // Sao chép file vào thư mục đích
        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
        return uniqueFilename;
    }

    //Kiểm tra định dạng file ảnh hay không
    private boolean isImageFile(MultipartFile file){
        String contentType = file.getContentType();
        return contentType != null && contentType.startsWith("/image");
    }
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateProducts(@PathVariable Long id, @Valid @RequestBody ProductDTO productDTO){
        try {
            Product product = productService.updateProduct(productDTO, id);
            ProductResponse productResponse = ProductResponse.builder()
                    .id(product.getId())
                    .name(product.getName())
                    .price(product.getPrice())
                    .description(product.getDescription())
                    .thumnail(product.getThumnail())
                    .categoryName(product.getCategory().getName())
                    .build();
            productResponse.setCreatedAt(product.getCreatedAt());
            productResponse.setUpdatedAt(product.getUpdatedAt());
            return ResponseEntity.ok(productResponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProducts(@PathVariable Long id){
        try {
            if (!productService.existProduct(id)){
                return ResponseEntity.badRequest().body("Không có sản phẩm này");
            }
            productService.deleteProduct(id);
            return ResponseEntity.ok("Xóa thành công " + id);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

//    @PostMapping("/generateFakeProducts")
    private ResponseEntity<?> generateFakeProducts(){
        Faker faker = new Faker();
        for (int i=0; i<100; i++){
            String fakeName = faker.commerce().productName();
            if(productService.existsByName(fakeName)){
                continue;
            }
            ProductDTO productDTO = ProductDTO.builder()
                    .name(fakeName)
                    .price(faker.number().numberBetween(0, 9000000))
                    .description(faker.lorem().sentence())
                    .thumnail("")
                    .categoryId(faker.number().numberBetween(2,4))
                    .build();
            try {
                productService.createProduct(productDTO);
            } catch (DataNotFoundException e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }
        return ResponseEntity.ok("Tạo dữ liệu giả thành công");

    }
}
