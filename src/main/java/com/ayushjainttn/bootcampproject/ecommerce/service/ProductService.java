package com.ayushjainttn.bootcampproject.ecommerce.service;

import com.ayushjainttn.bootcampproject.ecommerce.dto.product.*;
import com.ayushjainttn.bootcampproject.ecommerce.entity.Category;
import com.ayushjainttn.bootcampproject.ecommerce.entity.Product;
import com.ayushjainttn.bootcampproject.ecommerce.payload.ProductImage;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;
import java.util.Map;

public interface ProductService {
    public void checkSellerProductValidity(Principal principal, Product product, long productId);
    public void checkProductVariationMetadataValidity(Product product, Map<String, String> map);
    public ResponseEntity addProduct(Principal principal, ProductAddDto productAddDto);
    public ResponseEntity addProductVariation(Principal principal, ProductVariationAddDto productVariationAddDto, String path, MultipartFile imageFile);
    public ResponseEntity updateProduct(Principal principal, int productId, ProductUpdateDto productUpdateDto);
    public ResponseEntity updateProductVariation(Principal principal, int productVariationId, ProductVariationUpdateDto productVariationUpdateDto, MultipartFile imageFile, String path);
    public ResponseEntity deleteProductById(Principal principal, int productId);
    public ResponseEntity getProductByProductId(Principal principal, int productId);
    public ResponseEntity getProductByProductIdForAdmin(int productId, String path);
    public ResponseEntity getProductVariationByProductVariationId(Principal principal, int productVariationId, String path);
    public Page getAllProducts(Principal principal, int offset, int size, String sortProperty, String sortDirection);
    public Page getAllProductVariationsByProductId(Principal principal, int productId, int offset, int size, String sortProperty, String sortDirection, String path);
    public ResponseEntity getProductForCustomerByProductId(Principal principal, int productId, String path);
    public List<Long> findLeafCategoryId(Category category);
    public Page getAllProductForCustomerByCategoryId(Principal principal, int categoryId, int offset, int size, String sortProperty, String sortDirection, String path);
    public Page getSimilarProductForCustomerByProductId(Principal principal, int productId, int offset, int size, String sortProperty, String sortDirection, String path);
    public Page getAllProductsForAdmin(Principal principal, int offset, int size, String sortProperty, String sortDirection, String path);
    public ResponseEntity activateProduct(int productId);
    public ResponseEntity deactivateProduct(int productId);
    public List<ProductViewDto> getAllActiveProductsForAdmin();
    public List<ProductViewDto> getAllInactiveProductsForAdmin();
    public void updateImage(String path, MultipartFile imageFile, Long productId, Long productVariationId, String type);
    public ProductImage retrieveImage(String path, String type);
}
