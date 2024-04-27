package com.ayushjainttn.bootcampproject.ecommerce.controller;

import com.ayushjainttn.bootcampproject.ecommerce.dto.customer.CustomerRegisterationDto;
import com.ayushjainttn.bootcampproject.ecommerce.dto.email.EmailDto;
import com.ayushjainttn.bootcampproject.ecommerce.dto.password.PasswordDto;
import com.ayushjainttn.bootcampproject.ecommerce.dto.seller.SellerRegisterationDto;
import com.ayushjainttn.bootcampproject.ecommerce.service.CustomerService;
import com.ayushjainttn.bootcampproject.ecommerce.service.SellerService;
import com.ayushjainttn.bootcampproject.ecommerce.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
@Tag(name = "User APIs", description = "Common functionalities for all Users")
@RestController
public class UserController {
    @Autowired
    private SellerService sellerService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private UserService userService;
    @Autowired
    private TokenStore tokenStore;

    @Operation(summary = "Register a new Seller", description = "User can register as a new Seller", method = "POST")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Seller is created",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content) })
    @PostMapping("/register/seller")
    public ResponseEntity registerSeller(@Valid @RequestBody SellerRegisterationDto sellerDto){
        return sellerService.registerNewSeller(sellerDto);
    }

    @Operation(summary = "Register a new Customer", description = "User can register as a new Customer", method = "POST")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Customer is created",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content) })
    @PostMapping("/register/customer")
    public ResponseEntity registerCustomer(@Valid @RequestBody CustomerRegisterationDto customerDto){
        return customerService.registerNewCustomer(customerDto);
    }

    @Operation(summary = "Activate user account using token", description = "Activates user account by validating token recieved", method = "PUT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account Activated",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content) })
    @PutMapping("/confirm")
    public ResponseEntity confirmRegistration(@RequestParam("token") String token){
        return customerService.checkConfirmationToken(token);
    }

    @Operation(summary = "Resend a new activation token", description = "Resends a new token to activate user account", method = "POST")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "New Token Generated",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content) })
    @PostMapping("/resend-email-activation")
    public ResponseEntity resendActivateLink(@Valid @RequestBody EmailDto emailDto){
        return customerService.resendConfirmationToken(emailDto);
    }

    @Operation(summary = "Forget Password Token", description = "Sends a token to entered email if user forgets account password", method = "POST")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "New Token Generated",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content) })
    @PostMapping("/forget-password")
    public ResponseEntity forgetPassword(@Valid @RequestBody EmailDto emailDto){
        return userService.resetPasswordToken(emailDto);
    }

    @Operation(summary = "Reset Password using token", description = "Account Password is changed on validating the recieved token", method = "PATCH")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password Reset Done",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content) })
    @PatchMapping("/reset-password")
    public ResponseEntity updatePassword(@RequestParam("token") String token, @Valid @RequestBody PasswordDto passwordDto){
        return userService.resetPassword(passwordDto, token);
    }

    @Operation(summary = "Account Logout", description = "Logs out user from their account", method = "GET")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "No Content or Logged Out",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content) })
    @GetMapping("/dologout")
    public ResponseEntity logout(HttpServletRequest request){
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null) {
            String tokenValue = authHeader.replace("Bearer", "").trim();
            OAuth2AccessToken accessToken = tokenStore.readAccessToken(tokenValue);
            tokenStore.removeAccessToken(accessToken);
            OAuth2RefreshToken refreshToken = accessToken.getRefreshToken();
            tokenStore.removeRefreshToken(refreshToken);
        }
        return new ResponseEntity<String>("Logged Out Success!", null, HttpStatus.NO_CONTENT);
    }
}
