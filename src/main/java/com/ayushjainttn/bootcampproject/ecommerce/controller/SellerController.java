package com.ayushjainttn.bootcampproject.ecommerce.controller;

import com.ayushjainttn.bootcampproject.ecommerce.dto.address.AddressDetailDto;
import com.ayushjainttn.bootcampproject.ecommerce.dto.password.PasswordUpdateDto;
import com.ayushjainttn.bootcampproject.ecommerce.dto.seller.SellerProfileDto;
import com.ayushjainttn.bootcampproject.ecommerce.dto.seller.SellerProfileUpdateDto;
import com.ayushjainttn.bootcampproject.ecommerce.service.SellerService;
import com.ayushjainttn.bootcampproject.ecommerce.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.security.Principal;

@Tag(name = "Seller APIs", description = "Provides functionalities for Seller")
@RestController
@RequestMapping("/seller")
public class SellerController {
    @Autowired
    private SellerService sellerService;
    @Autowired
    private UserService userService;

    @Value("${project.image}")
    private String path;

    @Operation(summary = "Get Seller Profile", description = "Fetches Seller profile details", method = "GET")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sellers Profile",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content) })
    @GetMapping("/view/profile")
    public ResponseEntity getSellerProfile(Principal principal){
        return sellerService.retrieveSellerProfile(path,principal);
    }

    @Operation(summary = "Update Profile Image", description = "Seller can update profile image", method = "PATCH")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Image Updated",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content) })
    @PatchMapping("/update/profile/image")
    public ResponseEntity updateProfileImage(Principal principal, @RequestParam(value = "image",required = true) MultipartFile imageFile){
        return userService.updateUserProfileImage(path, imageFile, principal);
    }

    @Operation(summary = "Profile Details Update", description = "Seller can update their profile details", method = "PATCH")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile Details Updated",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content) })
    @PatchMapping("/update/profile")
    public ResponseEntity updateSellerProfile(Principal principal, @Valid @RequestBody SellerProfileUpdateDto sellerProfileUpdateDto){
        return sellerService.updateSellerProfileDetails(principal, sellerProfileUpdateDto);
    }

    @Operation(summary = "Address Update", description = "Seller can update address details", method = "PATCH")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Address Details Updated",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content) })
    @PatchMapping("/update/address")
    public ResponseEntity updateSellerAddress(Principal principal, @Valid @RequestBody AddressDetailDto addressDetailDto){
        return sellerService.updateSellerCompanyAddress(principal, addressDetailDto);
    }

    @Operation(summary = "Password Update", description = "Seller can update account Password", method = "PATCH")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password Updated",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content) })
    @PatchMapping("/update/password")
    public ResponseEntity updateSellerPassword(Principal principal, @Valid @RequestBody PasswordUpdateDto passwordUpdateDto){
        return userService.updatePassword(principal, passwordUpdateDto);
    }
}
