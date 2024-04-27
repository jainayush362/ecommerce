package com.ayushjainttn.bootcampproject.ecommerce.controller;

import com.ayushjainttn.bootcampproject.ecommerce.dto.product.*;
import com.ayushjainttn.bootcampproject.ecommerce.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
@Tag(name = "Product APIs", description = "Functionalities for Product Maintenance")
@RestController
public class ProductController {
    @Autowired
    private ProductService productService;
    @Value("${project.image}")
    private String path;

    @Operation(summary = "Add Product", description = "Seller can add product", method = "POST")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Product is Added",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content) })
    @Secured("ROLE_SELLER")
    @PostMapping("/seller/add/product")
    public ResponseEntity addProduct(Principal principal, @Valid @RequestBody ProductAddDto productAddDto){
        return productService.addProduct(principal, productAddDto);
    }

    @Operation(summary = "Add Product Variation", description = "Seller can add product variants", method = "POST")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Product Variant is Added",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content) })
    @Secured("ROLE_SELLER")

    @PostMapping(path = "/seller/add/product-variation", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity addProductVariation(Principal principal, @Valid @RequestPart(value = "productdetail", required = true) ProductVariationAddDto productVariationAddDto, @Valid @RequestPart(value = "primaryimage", required = true) MultipartFile imageFile){
        return productService.addProductVariation(principal, productVariationAddDto, path, imageFile);
    }

    @Operation(summary = "Update Product", description = "Seller can update product using product id", method = "PUT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product is Updated",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content) })
    @Secured("ROLE_SELLER")
    @PatchMapping("/seller/update/product/{productId}")
    public ResponseEntity updateProduct(Principal principal, @PathVariable("productId") int productId, @Valid @RequestBody ProductUpdateDto productUpdateDto){
        return productService.updateProduct(principal, productId, productUpdateDto);
    }

    @Operation(summary = "Update Product Variation", description = "Seller can update product variation using product variation id", method = "PUT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product Variation is Updated",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content) })
    @Secured("ROLE_SELLER")
    @PatchMapping(path = "/seller/update/product-variation/{productVariationId}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity updateProductVariation(Principal principal, @PathVariable("productVariationId") int productVariationId, @Valid @RequestPart(value = "productdetail", required = false) ProductVariationUpdateDto productVariationUpdateDto, @Valid @RequestPart(value = "primaryimage", required = false) MultipartFile imageFile){
        return productService.updateProductVariation(principal, productVariationId, productVariationUpdateDto, imageFile, path);
    }

    @Operation(summary = "Delete Product", description = "Seller can delete product using product id", method = "DELETE")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Deleted! No content found",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content) })
    @Secured("ROLE_SELLER")
    @DeleteMapping("/seller/delete/product/{productId}")
    public ResponseEntity deleteProduct(Principal principal, @PathVariable("productId") int productId){
        return productService.deleteProductById(principal, productId);
    }

    @Operation(summary = "Get All Product", description = "Seller can fetch all products", method = "GET")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product Page",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content) })
    @Secured("ROLE_SELLER")
    @GetMapping("/seller/view/product")
    public Page getAllProducts(
            Principal principal,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "page", defaultValue = "0") Integer offset,
            @RequestParam(value = "sort", defaultValue = "product_id") String sortProperty,
            @RequestParam(value = "sortDirection", defaultValue = "asc") String sortDirection){
        return productService.getAllProducts(principal,offset, size, sortProperty, sortDirection);
    }

    @Operation(summary = "Get Product By ID", description = "Seller can fetch a product using product id", method = "GET")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product Details with give id",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content) })
    @Secured("ROLE_SELLER")
    @GetMapping("/seller/view/product/{productId}")
    public ResponseEntity getProductById(Principal principal, @PathVariable("productId") int productId){
        return productService.getProductByProductId(principal, productId);
    }

    @Operation(summary = "Get Product Variation By ID", description = "Seller can fetch a product variation using product variation id", method = "GET")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product Variation Details with give id",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content) })
    @Secured("ROLE_SELLER")
    @GetMapping("/seller/view/product-variation/{productVariationId}")
    public ResponseEntity getProductVariationById(Principal principal, @PathVariable("productVariationId") int productVariationId){
        return productService.getProductVariationByProductVariationId(principal, productVariationId, path);
    }

    @Operation(summary = "Get Product Variation By Product ID", description = "Seller can fetch a product variants using product id", method = "GET")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product variants page with given product id",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content) })
    @Secured("ROLE_SELLER")
    @GetMapping("/seller/view/product/product-variation/{productId}")
    public Page getProductVariationByProductId(
            Principal principal,
            @PathVariable("productId") int productId,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "page", defaultValue = "0") Integer offset,
            @RequestParam(value = "sort", defaultValue = "product_id") String sortProperty,
            @RequestParam(value = "sortDirection", defaultValue = "asc") String sortDirection){
        return productService.getAllProductVariationsByProductId(principal, productId, offset, size, sortProperty, sortDirection, path);
    }

    @Operation(summary = "Get Product By ID", description = "Customer can fetch a product using product id", method = "GET")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product Details with give id",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content) })
    @Secured("ROLE_CUSTOMER")
    @GetMapping("/customer/view/product/{productId}")
    public ResponseEntity getProductForCustomerById(Principal principal, @PathVariable("productId") int productId){
        return productService.getProductForCustomerByProductId(principal, productId, path);
    }

    @Operation(summary = "Get Product By Category ID", description = "Customer can fetch product using category id", method = "GET")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product page with given category id",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content) })
    @Secured("ROLE_CUSTOMER")
    @GetMapping("/customer/view/product/category/{categoryId}")
    public Page getAllProductForCustomerById(
            Principal principal,
            @PathVariable("categoryId") int categoryId,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "page", defaultValue = "0") Integer offset,
            @RequestParam(value = "sort", defaultValue = "product_id") String sortProperty,
            @RequestParam(value = "sortDirection", defaultValue = "asc") String sortDirection){
        return productService.getAllProductForCustomerByCategoryId(principal, categoryId, offset, size, sortProperty, sortDirection, path);
    }

    @Operation(summary = "Get Similar Product By ID", description = "Customer can fetch similar product using product id", method = "GET")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Similar Product page with given id",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content) })
    @Secured("ROLE_CUSTOMER")
    @GetMapping("/customer/view/similar/product/{productId}")
    public Page getSimilarProductForCustomerByProductId(
            Principal principal,
            @PathVariable("productId") int productId,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "page", defaultValue = "0") Integer offset,
            @RequestParam(value = "sort", defaultValue = "product_id") String sortProperty,
            @RequestParam(value = "sortDirection", defaultValue = "asc") String sortDirection){
        return productService.getSimilarProductForCustomerByProductId(principal, productId, offset, size, sortProperty, sortDirection,path);
    }

    @Operation(summary = "Get Product By ID", description = "Admin can fetch a product using product id", method = "GET")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product Details with give id",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content) })
    @Secured("ROLE_ADMIN")
    @GetMapping("/admin/view/product/{productId}")
    public ResponseEntity getProductForAdminById(Principal principal, @PathVariable("productId") int productId){
        return productService.getProductByProductIdForAdmin(productId, path);
    }

    @Operation(summary = "Get All Products", description = "Admin can fetch all product", method = "GET")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product page",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content) })
    @Secured("ROLE_ADMIN")
    @GetMapping("/admin/view/product")
    public Page getAllProductsForAdmin(
            Principal principal,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "page", defaultValue = "0") Integer offset,
            @RequestParam(value = "sort", defaultValue = "product_id") String sortProperty,
            @RequestParam(value = "sortDirection", defaultValue = "asc") String sortDirection){
        return productService.getAllProductsForAdmin(principal,offset, size, sortProperty, sortDirection, path);
    }

    @Operation(summary = "Activate Product By ID", description = "Admin can activate a product using product id", method = "PUT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product Activated",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content) })
    @Secured("ROLE_ADMIN")
    @PutMapping("/admin/activate/product/{productId}")
    public ResponseEntity activateProduct(@PathVariable("productId") int productId){
        return productService.activateProduct(productId);
    }

    @Operation(summary = "Deactivate Product By ID", description = "Admin can deactivate a product using product id", method = "PUT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product Deactivated",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content) })
    @Secured("ROLE_ADMIN")
    @PutMapping("/admin/deactivate/product/{productId}")
    public ResponseEntity deactivateProduct(@PathVariable("productId") int productId){
        return productService.deactivateProduct(productId);
    }

    @Operation(summary = "Get Inactive Products", description = "Admin can fetch all inactive products", method = "GET")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inactive Products List",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content) })
    @Secured("ROLE_ADMIN")
    @GetMapping("/admin/view/inactive/product")
    public CollectionModel<EntityModel<ProductViewDto>> getAllInactiveProductsForAdmin(){
        List<EntityModel<ProductViewDto>> products = productService.getAllInactiveProductsForAdmin().stream()
                .map(productViewDto -> EntityModel.of(productViewDto,
                        linkTo(methodOn(this.getClass()).activateProduct(productViewDto.getProductId().intValue())).withSelfRel()))
                .collect(Collectors.toList());
        return CollectionModel.of(products, linkTo(methodOn(this.getClass()).getAllInactiveProductsForAdmin()).withSelfRel());
    }

    @Operation(summary = "Get Active Products", description = "Admin can fetch all active products", method = "GET")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Active Products List",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content) })
    @Secured("ROLE_ADMIN")
    @GetMapping("/admin/view/active/product")
    public CollectionModel<EntityModel<ProductViewDto>> getAllActiveProductsForAdmin(){
        List<EntityModel<ProductViewDto>> products = productService.getAllActiveProductsForAdmin().stream()
                .map(productViewDto -> EntityModel.of(productViewDto,
                        linkTo(methodOn(this.getClass()).deactivateProduct(productViewDto.getProductId().intValue())).withSelfRel()))
                .collect(Collectors.toList());
        return CollectionModel.of(products, linkTo(methodOn(this.getClass()).getAllActiveProductsForAdmin()).withSelfRel());
    }

}
