package com.ayushjainttn.bootcampproject.ecommerce.controller;

import com.ayushjainttn.bootcampproject.ecommerce.dto.address.AddressDetailDto;
import com.ayushjainttn.bootcampproject.ecommerce.dto.address.AddressDto;
import com.ayushjainttn.bootcampproject.ecommerce.dto.customer.CustomerProfileUpdateDto;
import com.ayushjainttn.bootcampproject.ecommerce.dto.password.PasswordUpdateDto;
import com.ayushjainttn.bootcampproject.ecommerce.entity.Address;
import com.ayushjainttn.bootcampproject.ecommerce.service.CustomerService;
import com.ayushjainttn.bootcampproject.ecommerce.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.security.Principal;

@Tag(name = "Customer APIs", description = "Provides functionalities for Customer")
@RestController
@RequestMapping("/customer")
public class CustomerController {
    @Autowired
    private UserService userService;
    @Autowired
    private CustomerService customerService;
    @Value("${project.image}")
    private String path;

    @Operation(summary = "Get Customer Profile", description = "Fetches Customer profile details", method = "GET")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customer Profile",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content) })
    @GetMapping("/view/profile")
    public ResponseEntity getCustomerProfile(Principal principal){
        return customerService.retrieveCustomerProfile(path,principal);
    }

    @Operation(summary = "Update Profile Image", description = "Customer can update profile image", method = "PATCH")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Image Updated",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content) })
    @PatchMapping("/update/profile/image")
    public ResponseEntity updateProfileImage(Principal principal, @RequestParam(value = "image", required = true) MultipartFile imageFile){
        return userService.updateUserProfileImage(path, imageFile, principal);
    }

    @Operation(summary = "Profile Update", description = "Customer can update profile details", method = "PATCH")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile Details Updated",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content) })
    @PatchMapping("/update/profile")
    public ResponseEntity updateCustomerProfile(Principal principal, @Valid @RequestBody CustomerProfileUpdateDto customerProfileUpdateDto){
        return customerService.updateCustomerProfileDetails(principal, customerProfileUpdateDto);
    }

    @Operation(summary = "Address View", description = "Customer can view Address", method = "GET")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Address Details",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content) })
    @GetMapping("/view/address")
    public ResponseEntity getCustomerAddress(Principal principal){
        return customerService.retrieveCustomerAddress(principal);
    }

    @Operation(summary = "New Address Add", description = "Customer can add new Address", method = "POST")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Address Added",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content) })
    @PostMapping("/add/address")
    public ResponseEntity addCustomerAddress(Principal principal, @Valid @RequestBody AddressDto addressDto){
        return customerService.addCustomerAddress(principal,addressDto);
    }

    @Operation(summary = "Address Update", description = "Customer can update address details", method = "PATCH")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Address Details Updated",
                    content = { @Content(schema = @Schema(implementation = Address.class)) }),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content) })
    @PatchMapping("/update/address/{addressId}")
    public ResponseEntity updateCustomerAddress(Principal principal, @PathVariable("addressId") int addressId, @Valid @RequestBody AddressDetailDto addressDetailDto){
        return customerService.updateCustomerAddress(principal, addressId, addressDetailDto);
    }

    @Operation(summary = "Delete Address", description = "Customer can delete address", method = "DELETE")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Address Deleted - No Content",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content) })
    @DeleteMapping("/delete/address/{addressId}")
    public ResponseEntity deleteCustomerAddress(Principal principal, @PathVariable("addressId") int addressId){
        return customerService.removeCustomerAddress(principal, addressId);
    }

    @Operation(summary = "Password Update", description = "Customer can update Password", method = "PATCH")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password Updated",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content) })
    @PatchMapping("/update/password")
    public ResponseEntity updateCustomerPassword(Principal principal, @Valid @RequestBody PasswordUpdateDto passwordUpdateDto){
        return userService.updatePassword(principal, passwordUpdateDto);
    }
}
