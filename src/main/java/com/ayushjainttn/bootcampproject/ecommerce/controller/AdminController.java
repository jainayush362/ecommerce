package com.ayushjainttn.bootcampproject.ecommerce.controller;

import com.ayushjainttn.bootcampproject.ecommerce.dto.customer.CustomerDetailDto;
import com.ayushjainttn.bootcampproject.ecommerce.dto.product.ProductViewDto;
import com.ayushjainttn.bootcampproject.ecommerce.dto.seller.SellerDetailDto;
import com.ayushjainttn.bootcampproject.ecommerce.service.AdminService;
import com.ayushjainttn.bootcampproject.ecommerce.utils.GlobalExpressions;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Tag(name = "Admin APIs", description = "Functionalities only limited to Admin")
@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private AdminService adminService;
    @Autowired
    private PagedResourcesAssembler<CustomerDetailDto> customerDetailDtoPagedResourcesAssembler;
    @Autowired
    private PagedResourcesAssembler<SellerDetailDto> sellerDetailDtoPagedResourcesAssembler;

    @Operation(summary = "Get all Sellers", description = "Fetches all registered Sellers", method = "GET")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Page of Sellers",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content) })
    @GetMapping("/view/sellers")
    public PagedModel<EntityModel<SellerDetailDto>> getSellers(
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "page", defaultValue = "0") Integer offset,
            @RequestParam(value = "sort", defaultValue = "userId") String sortProperty,
            @RequestParam(value = "sortDirection", defaultValue = "asc") String sortDirection,
            @RequestParam(value = "email") Optional<String> email){
        Page sellerPage = adminService.findAllSellers(offset, size, sortProperty, email, sortDirection);
        return sellerDetailDtoPagedResourcesAssembler.toModel(sellerPage);
    }

    @Operation(summary = "Get all Customers", description = "Fetches all registered Customers", method = "GET")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Page of Customers",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content) })
    @GetMapping("/view/customers")
    public PagedModel<EntityModel<CustomerDetailDto>> getCustomers(
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "page", defaultValue = "0") Integer offset,
            @RequestParam(value = "sort", defaultValue = "userId") String sortProperty,
            @RequestParam(value = "sortDirection", defaultValue = "asc") String sortDirection,
            @RequestParam(value = "email") Optional<String> email){
        Page customerPage = adminService.findAllCustomers(offset, size, sortProperty, email, sortDirection);
        return customerDetailDtoPagedResourcesAssembler.toModel(customerPage);
    }

    @Operation(summary = "Activate User Account by UserID", description = "User acount can be activated using userid", method = "PATCH")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account Activated",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content) })
    @PatchMapping("/activate/users/{id}")
    public ResponseEntity activateUser(@PathVariable("id") Integer userId){
        return adminService.activateUser(userId);
    }

    @Operation(summary = "Deactivate User Account by UserID", description = "User account can be deactivated using userid", method = "PATCH")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account Deactivated",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content) })
    @PatchMapping("/deactivate/users/{id}")
    public ResponseEntity deactivateUser(@PathVariable("id") Integer userId){
        return adminService.deactivateUser(userId);
    }

    @Operation(summary = "Get all Inactive Seller", description = "Fetches all inactive seller", method = "GET")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inactive Seller List",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content) })
    @GetMapping("/view/inactive/seller")
    public CollectionModel<EntityModel<SellerDetailDto>> getAllInactiveSeller(){
        List<EntityModel<SellerDetailDto>> sellers = adminService.getAllInactiveSeller().stream()
                .map(sellerDetailDto -> EntityModel.of(sellerDetailDto,
                        linkTo(methodOn(this.getClass()).activateUser(sellerDetailDto.getUserId().intValue())).withSelfRel()))
                .collect(Collectors.toList());
        return CollectionModel.of(sellers, linkTo(methodOn(this.getClass()).getAllInactiveSeller()).withSelfRel());
    }

    @Operation(summary = "Get all Active Seller", description = "Fetches all active seller", method = "GET")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Active Seller List",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content) })
    @GetMapping("/view/active/seller")
    public CollectionModel<EntityModel<SellerDetailDto>> getAllActiveSeller(){
        List<EntityModel<SellerDetailDto>> sellers = adminService.getAllActiveSeller().stream()
                .map(sellerDetailDto -> EntityModel.of(sellerDetailDto,
                        linkTo(methodOn(this.getClass()).deactivateUser(sellerDetailDto.getUserId().intValue())).withSelfRel()))
                .collect(Collectors.toList());
        return CollectionModel.of(sellers, linkTo(methodOn(this.getClass()).getAllActiveSeller()).withSelfRel());
    }
}
