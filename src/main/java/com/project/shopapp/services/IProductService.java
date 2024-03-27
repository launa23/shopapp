package com.project.shopapp.services;

import com.project.shopapp.dtos.ProductDTO;
import com.project.shopapp.dtos.ProductImageDTO;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.models.Product;
import com.project.shopapp.models.ProductImage;
import com.project.shopapp.responses.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

public interface IProductService {
    public Product createProduct(ProductDTO productDTO) throws DataNotFoundException;
    Product getProductById(long id) throws Exception;
    Page<ProductResponse> getAllProducts(PageRequest pageRequest);
    Page<ProductResponse> getProductsByCategory(PageRequest pageRequest, long cateId);

    Product updateProduct(ProductDTO productDTO, long id) throws Exception;

    Product updateProductThumnail(String thumnail, long id) throws Exception;
    void deleteProduct(long id);
    boolean existsByName(String name);

    boolean existProduct(long id);

    // Hàm xử lý lưu các file ảnh của product vào trong bảng product_image
    ProductImage createProductImage(long productId, ProductImageDTO productImageDTO) throws Exception;
}
