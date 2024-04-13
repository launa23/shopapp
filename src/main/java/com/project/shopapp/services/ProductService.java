package com.project.shopapp.services;

import com.project.shopapp.dtos.ProductDTO;
import com.project.shopapp.dtos.ProductImageDTO;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.exceptions.InvalidParamException;
import com.project.shopapp.models.Category;
import com.project.shopapp.models.Product;
import com.project.shopapp.models.ProductImage;
import com.project.shopapp.repositories.CategoryRepository;
import com.project.shopapp.repositories.ProductImageRepository;
import com.project.shopapp.repositories.ProductRepository;
import com.project.shopapp.responses.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService{
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductImageRepository productImageRepository;
    @Override
    public Product createProduct(ProductDTO productDTO) throws DataNotFoundException {
        if (productRepository.existsByNameAndActive(productDTO.getName(), true)){
            throw new DataNotFoundException("Product's name is already exists");
        }
        Category existingCategory = categoryRepository.findByIdAndActive(productDTO.getCategoryId(), true)
                .orElseThrow(() ->
                        new DataNotFoundException("Cannot find category with id: " + productDTO.getCategoryId()));
        Product newProduct = Product.builder()
                .name(productDTO.getName())
                .price(productDTO.getPrice())
                .description(productDTO.getDescription())
                .thumnail(productDTO.getThumnail())
                .active(true)
                .category(existingCategory)
                .build();
        return productRepository.save(newProduct);
    }

    @Override
    public Product getProductById(long id) throws Exception {
        return productRepository.findByIdAndActive(id, true)
                .orElseThrow(() -> new DataNotFoundException("Cannot find product with id: " + id));
    }

    @Override
    public Page<ProductResponse> getAllProducts(PageRequest pageRequest) {
        // Lấy sản phẩm có phân trang page và limit
        return productRepository.findAllByActive(pageRequest, true).map(product -> {
            ProductResponse productResponse = ProductResponse.builder()
                    .id(product.getId())
                    .name(product.getName())
                    .price(product.getPrice())
                    .thumnail(product.getThumnail())
                    .description(product.getDescription())
                    .categoryName(product.getCategory().getName())
                    .categoryId(product.getCategory().getId())
                    .build();
            productResponse.setCreatedAt(product.getCreatedAt());
            productResponse.setUpdatedAt(product.getUpdatedAt());
            return productResponse;
        });
    }

    @Override
    public Page<ProductResponse> getProductsByCategory(PageRequest pageRequest, long cateId) {
        return productRepository.findByCategoryIdAndActive(pageRequest, cateId, true).map(product -> {
            ProductResponse productResponse = ProductResponse.builder()
                    .id(product.getId())
                    .name(product.getName())
                    .price(product.getPrice())
                    .thumnail(product.getThumnail())
                    .description(product.getDescription())
                    .categoryName(product.getCategory().getName())
                    .categoryId(product.getCategory().getId())
                    .build();
            productResponse.setCreatedAt(product.getCreatedAt());
            productResponse.setUpdatedAt(product.getUpdatedAt());
            return productResponse;
        });
    }


    @Override
    public Product updateProduct(ProductDTO productDTO, long id) throws Exception {

        Product existingProduct = getProductById(id);
        if (productRepository.existsByNameAndActive(productDTO.getName(), true) &&
                !existingProduct.getName().equals(productDTO.getName())){
            throw new DataNotFoundException("Product's name is already exists");
        }
        if (existingProduct != null){
            Category existingCategory = categoryRepository.findByIdAndActive(productDTO.getCategoryId(), true)
                    .orElseThrow(() ->
                            new DataNotFoundException("Cannot find category with id: " + productDTO.getCategoryId()));
            existingProduct.setName(productDTO.getName());
            existingProduct.setCategory(existingCategory);
            existingProduct.setPrice(productDTO.getPrice());
//            existingProduct.setThumnail(productDTO.getThumnail());
            existingProduct.setDescription(productDTO.getDescription());

            return productRepository.save(existingProduct);
        }
        return null;
    }

    @Override
    public Product updateProductThumnail(String thumnail, long id) throws Exception {
        Product existingProduct = getProductById(id);
        existingProduct.setThumnail(thumnail);
        return productRepository.save(existingProduct);
    }

    @Override
    public void deleteProduct(long id) {
        Optional<Product> optionalProduct = productRepository.findByIdAndActive(id, true);
        if (optionalProduct.isPresent()){           // kiểm tra xem có tồn tại không, nếu có thì xóa, nếu không thì thôi
            optionalProduct.get().setActive(false);
            productRepository.save(optionalProduct.get());
        }
    }

    @Override
    public boolean existsByName(String name) {
        return productRepository.existsByName(name);
    }

    @Override
    public boolean existProduct(long id){
        return productRepository.existsById(id);
    }
    @Override
    // Hàm xử lý lưu các file ảnh của product vào trong bảng product_image
    public ProductImage createProductImage(long productId, ProductImageDTO productImageDTO) throws Exception {
        Product existingProduct = productRepository.findByIdAndActive(productId, true)
                .orElseThrow(() ->
                        new DataNotFoundException("Cannot find product with id: " + productId));
        ProductImage newProductImage = ProductImage.builder()
                .product(existingProduct)
                .imgeUrl(productImageDTO.getImgeUrl())
                .build();
        // Không cho thêm quá 5 ảnh cho 1 sản phẩm
        int size = productImageRepository.findByProductId(productId).size();
        if ( size >= ProductImage.MAXIMUM_IMAGES_PER_PRODUCT ){
            throw new InvalidParamException("Đã quá " +ProductImage.MAXIMUM_IMAGES_PER_PRODUCT+" ảnh cho sản phẩm này");
        }
        return productImageRepository.save(newProductImage);
    }
}
