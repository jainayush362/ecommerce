package com.ayushjainttn.bootcampproject.ecommerce.controller;

import com.ayushjainttn.bootcampproject.ecommerce.dto.category.CategoryAddDto;
import com.ayushjainttn.bootcampproject.ecommerce.dto.category.CategoryMetadataFieldAddDto;
import com.ayushjainttn.bootcampproject.ecommerce.dto.category.CategoryMetadataFieldValuesAddDto;
import com.ayushjainttn.bootcampproject.ecommerce.dto.category.CategoryUpdateDto;
import com.ayushjainttn.bootcampproject.ecommerce.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
@Tag(name = "Category APIs", description = "Functionalities for Category Maintenance")
@RestController
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @Operation(summary = "Add Category", description = "Logged in Admin can add new category", method = "POST")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Category Added",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content) })
    @Secured("ROLE_ADMIN")
    @PostMapping("/admin/add/category")
    public ResponseEntity addCategory(@Valid @RequestBody CategoryAddDto categoryAddDto){
        return categoryService.addCategory(categoryAddDto);
    }

    @Operation(summary = "Update Category", description = "Logged in Admin can update existing category", method = "PUT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category Updated",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content) })
    @Secured("ROLE_ADMIN")
    @PutMapping("/admin/update/category/{categoryId}")
    public ResponseEntity updateCategoryUsingId(@PathVariable("categoryId") int categoryId, @Valid @RequestBody CategoryUpdateDto categoryUpdateDto){
        return categoryService.updateCategory(categoryId, categoryUpdateDto);
    }

    @Operation(summary = "Get Category using ID", description = "Logged in Admin can view category details using categoryID", method = "GET")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category Details",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content) })
    @Secured("ROLE_ADMIN")
    @GetMapping("/admin/view/category/{categoryId}")
    public ResponseEntity getCategoryUsingId(@PathVariable("categoryId") int categoryId){
        return categoryService.getCategoryUsingId(categoryId);
    }

    @Operation(summary = "Get All Categories", description = "Logged in Admin can view about all available categories", method = "GET")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Page of Categories",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content) })
    @Secured("ROLE_ADMIN")
    @GetMapping("/admin/view/category")
    public Page getAllCategories(@RequestParam(value = "size", defaultValue = "10") Integer size,
                                 @RequestParam(value = "page", defaultValue = "0") Integer offset,
                                 @RequestParam(value = "sort", defaultValue = "categoryId") String sortProperty,
                                 @RequestParam(value = "sortDirection", defaultValue = "asc") String sortDirection){
        return categoryService.getAllCategories(offset,size,sortProperty,sortDirection);
    }

    @Operation(summary = "Add Metadata Field", description = "Logged in Admin can add category metadata field", method = "POST")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Metadata Field Added",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content) })
    @Secured("ROLE_ADMIN")
    @PostMapping("/admin/add/metadata-field")
    public ResponseEntity addCategoryMetadataFields(@Valid @RequestBody CategoryMetadataFieldAddDto categoryMetadataFieldAddDto){
        return categoryService.addCategoryMetadataField(categoryMetadataFieldAddDto);
    }

    @Operation(summary = "Get Metadata Field", description = "Logged in Admin can view about all available category metadata fields", method = "GET")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Page of Metadata Fields",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content) })
    @Secured("ROLE_ADMIN")
    @GetMapping("/admin/view/metadata-field")
    public Page getAllCategoryMetadataFields(@RequestParam(value = "size", defaultValue = "10") Integer size,
                                             @RequestParam(value = "page", defaultValue = "0") Integer offset,
                                             @RequestParam(value = "sort", defaultValue = "categoryMetadataFieldId") String sortProperty,
                                             @RequestParam(value = "sortDirection", defaultValue = "asc") String sortDirection){
        return categoryService.getAllCategoryMetadataFields(offset,size,sortProperty,sortDirection);
    }

    @Operation(summary = "Add Metadata Field Values", description = "Logged in Admin can add values for category metadata field", method = "POST")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Metadata Field Values Added",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content) })
    @Secured("ROLE_ADMIN")
    @PostMapping("/admin/add/metadata-field-values")
    public ResponseEntity addCategoryMetadataFieldValues(@Valid @RequestBody CategoryMetadataFieldValuesAddDto categoryMetadataFieldValuesAddDto){
        return categoryService.addCategoryMetadataFieldValues(categoryMetadataFieldValuesAddDto);
    }

    @Operation(summary = "Update Metadata Field Values", description = "Logged in Admin can update existing category metadata field values", method = "PUT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Metadata Field Values Updated",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content) })
    @Secured("ROLE_ADMIN")
    @PutMapping("/admin/update/metadata-field-values")
    public ResponseEntity updateCategoryMetadataFieldValues(@Valid @RequestBody CategoryMetadataFieldValuesAddDto categoryMetadataFieldValuesAddDto){
        return categoryService.updateCategoryMetadataFieldValues(categoryMetadataFieldValuesAddDto);
    }

    @Operation(summary = "Get All Categories", description = "Logged in Seller can view details about all available categories", method = "GET")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of Categories",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content) })
    @Secured("ROLE_SELLER")
    @GetMapping("/seller/view/category")
    public ResponseEntity getCategories(){
        return categoryService.getAllCategoriesForSeller();
    }

    @Operation(summary = "Get All Categories", description = "Logged in Customer can view details about all available categories", method = "GET")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of categories",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content) })
    @Secured("ROLE_CUSTOMER")
    @GetMapping("/customer/view/category")
    public ResponseEntity getCategory(){
        return categoryService.getCategoriesForCustomer();
    }

    @Operation(summary = "Get Category using ID", description = "Logged in Customer can view details about category for a given categoryID", method = "GET")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category Details",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content) })
    @Secured("ROLE_CUSTOMER")
    @GetMapping("/customer/view/category/{categoryId}")
    public ResponseEntity getChildCategoryUsingId(@PathVariable("categoryId") int categoryId){
        return categoryService.getCategoriesForCustomerUsingId(categoryId);
    }

    @Operation(summary = "Get CFilters using Category ID", description = "Logged in Customer can view product filters for given categoryID", method = "GET")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Filter Details",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content) })
    @Secured("ROLE_CUSTOMER")
    @GetMapping("/customer/view/filter/{categoryId}")
    public ResponseEntity getFilterUsingCategoryId(@PathVariable("categoryId") int categoryId){
        return categoryService.getFilterByCategoryId(categoryId);
    }
}
