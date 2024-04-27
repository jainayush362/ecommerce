package com.ayushjainttn.bootcampproject.ecommerce.service.impl;

import com.ayushjainttn.bootcampproject.ecommerce.dto.address.AddressDetailDto;
import com.ayushjainttn.bootcampproject.ecommerce.dto.email.EmailDto;
import com.ayushjainttn.bootcampproject.ecommerce.dto.password.PasswordDto;
import com.ayushjainttn.bootcampproject.ecommerce.dto.password.PasswordUpdateDto;
import com.ayushjainttn.bootcampproject.ecommerce.entity.*;
import com.ayushjainttn.bootcampproject.ecommerce.payload.UserImage;
import com.ayushjainttn.bootcampproject.ecommerce.repository.ConfirmationTokenRepository;
import com.ayushjainttn.bootcampproject.ecommerce.repository.UserRepository;
import com.ayushjainttn.bootcampproject.ecommerce.service.EmailService;
import com.ayushjainttn.bootcampproject.ecommerce.service.ImageService;
import com.ayushjainttn.bootcampproject.ecommerce.service.UserService;
import com.ayushjainttn.bootcampproject.ecommerce.utils.GlobalExpressions;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.Principal;
import java.util.Date;
import java.util.Locale;

@Service
@Transactional
@Slf4j
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private ConfirmationTokenRepository confirmationTokenRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private ImageService imageService;
    @Autowired
    private ModelMapper modelMapper;
    @Value("${service.url}")
    private String baseUrl;
    @Autowired
    private MessageSource messageSource;
    private Locale locale = LocaleContextHolder.getLocale();

    /**
     * Method to fetch user using email id
     * @param email
     * @return user
     */
    @Override
    public User findUserByEmail(String email){
        log.info("----inside findUserByEmail() method----");
        return userRepository.findByUserEmailIgnoreCase(email);
    }

    /**
     * Method to check whether the user has admin role or not
     * @param user
     * @return boolean
     */
    @Override
    public boolean isUserAdmin(User user){
        log.info("----inside isUserAdmin() method----");
        return user.getRole().getAuthority().equals("ROLE_ADMIN");
    }

    /**
     * Method to check whether the user credentials has expired or not
     * @param user
     */
    @Override
    public void credentialsExpired(User user){
        log.info("----inside credentialsExpired()----");
        //if user is admin then credentials cannot expire
        if(!isUserAdmin(user)){
            log.info("----given user is not admin----");
            Date date = new Date();
            //credentials will expire after 3 months
            if((((date.getTime() - user.getUserPasswordUpdateDate().getTime())/1000)> GlobalExpressions.SECONDS_CREDENTIALS_EXPIRE)){
                log.info("----given user credentials isExpired=true----");
                user.setUserIsExpired(true);
                userRepository.save(user);
                String subject = messageSource.getMessage("email.resetPassword.account.expired.subject",null,locale);
                String message = messageSource.getMessage("email.salutation",null,locale)+", \n" +
                        messageSource.getMessage("email.resetPassword.account.expired.message",null,locale)+" \n" +
                                    baseUrl+"/forget-password";
                log.info("----sending email to user to reset password----");
                emailService.sendEmail(emailService.sendAccountRelatedUpdateEmail(user.getUserEmail(), message, subject));
            }
        }
        log.info("----credentialsExpired() method executed success----");
    }

    /**
     * Method to lock user account
     * @param user
     */
    @Override
    public void accountLock(User user){
        log.info("----inside accountLock() method----");
        if(!isUserAdmin(user)){
            user.setUserInvalidAttemptCount(0);
            user.setUserIsLocked(true);
            user.setUserLockDate(new Date());
            userRepository.save(user);
            log.info("----account locked. sending email to user.......----");
            String subject = messageSource.getMessage("email.account.locked.subject",null,locale);
            String message = messageSource.getMessage("email.salutation",null,locale)+", \n" +
                    messageSource.getMessage("email.account.locked.message",null,locale);
            emailService.sendEmail(emailService.sendAccountRelatedUpdateEmail(user.getUserEmail(), message, subject));
            log.info("----accountLock() method executed success----");
        }
    }

    /**
     * Method to unlock user account is 24 hrs are complete from the time account is locked
     * @param user
     * @return
     */
    @Override
    public boolean accountUnlock(User user){
        log.info("----inside accountUnlock() method----");
        long lockTimeInMillis = user.getUserLockDate().getTime();
        long currentTimeInMillis = System.currentTimeMillis();
        if (lockTimeInMillis + GlobalExpressions.SECONDS_ACCOUNT_UNLOCK < currentTimeInMillis) {
            log.info("----account is unlocked----");
            user.setUserIsLocked(false);
            user.setUserLockDate(null);
            user.setUserInvalidAttemptCount(0);
            userRepository.save(user);
            log.info("----accountUnlock() method executed success----");
            return true;
        }
        log.info("----account is not unlocked----");
        log.info("----accountUnlock() method executed success----");
        return false;
    }

    /**
     * Method to increment invalid login attempt count of a user
     * @param user
     */
    @Override
    public void incrementInvalidAttempts(User user){
        log.info("----inside incrementInvalidAttempts() method----");
        if(!isUserAdmin(user)){
            int count = user.getUserInvalidAttemptCount()+1;
            userRepository.updateInvalidAttemptCount(user.getUserEmail(), count);
        }
        log.info("----incrementInvalidAttempts() method executed success----");
    }

    /**
     * Method to reset invalid attempts count of a user
     * @param email
     */
    @Override
    public void resetInvalidAttempts(String email){
        log.info("----inside resetInvalidAttempts() method----");
        userRepository.updateInvalidAttemptCount(email, 0);
    }

    /**
     * Method to send reset password token on user email to reset password in case user forgets password
     * @param emailDto
     * @return
     */
    @Override
    public ResponseEntity resetPasswordToken(EmailDto emailDto){
        log.info("----inside resetPasswordToken() method----");
        User user = userRepository.findByUserEmailIgnoreCase(emailDto.getUserEmail());
        if(user==null){
            log.error("----user not found----");
            throw new UsernameNotFoundException("No Account exists with given credentials.");
        }
        if(user.getUserIsLocked().booleanValue()){
            log.error("----user account is locked----");
            throw new LockedException("Account is Locked!");
        }
        if(!user.getUserIsActive().booleanValue()){
            log.error("----user account is inactive----");
            throw new RuntimeException("Account Inactive! If you are Customer then try resending Activation link or if you are seller then wail for account activation by team.");
        }
        ConfirmationToken existingToken = confirmationTokenRepository.findByUser(user);
        if(existingToken!=null){
            log.info("----existing token is deleted----");
            confirmationTokenRepository.delete(existingToken);
        }
        log.info("----new token generated----");
        ConfirmationToken confirmationToken = new ConfirmationToken(user);
        confirmationTokenRepository.save(confirmationToken);

        log.info("----sending email to user containing password reset token....----");
        String subject = messageSource.getMessage("email.resetPassword.token.subject",null,locale);
        String message = messageSource.getMessage("email.salutation",null,locale)+", \n" +
                messageSource.getMessage("email.resetPassword.token.message",null,locale)+" \n" +
                baseUrl+"/reset-password?token="+confirmationToken.getConfirmationToken();
        emailService.sendEmail(emailService.sendAccountRelatedUpdateEmail(user.getUserEmail(), message, subject));
        log.info("----resetPasswordToken() method executed success----");
        return new ResponseEntity<>("Password reset link sent to mail.", null, HttpStatus.OK);
    }

    /**
     * Method to reset user account password on validating the reset password token
     * @param passwordDto
     * @param token
     * @return
     */
    @Override
    public ResponseEntity resetPassword(PasswordDto passwordDto, String token){
        log.info("----inside resetPassword() method----");
        ConfirmationToken existingToken = confirmationTokenRepository.findByConfirmationToken(token);
        if (existingToken == null) {
            log.info("----invalid token. resetPassword() method executed success----");
            return new ResponseEntity<String>("Invalid token!", null, HttpStatus.UNAUTHORIZED);
        }
        if (existingToken.isTokenExpired(GlobalExpressions.SECONDS_PASSWORD_RESET_TOKEN)) {
            log.info("----token expired. resetPassword() method executed success----");
            confirmationTokenRepository.delete(existingToken);
            return new ResponseEntity<String>("Token expired, try forget password again!", null, HttpStatus.OK);
        }
        if(!passwordDto.getNewPassword().equals(passwordDto.getConfirmNewPassword())){
            log.error("----Confirm password and password not matched----");
            throw new BadCredentialsException("Confirm Password not same as Password Entered.");
        }
        String encodedPassword = passwordEncoder.encode(passwordDto.getConfirmNewPassword());
        userRepository.updatePassword(encodedPassword, new Date(), existingToken.getUser().getUserEmail());
        log.info("----password reset done----");
        log.info("----Sending email to user regarding password change.....----");
        String subject = messageSource.getMessage("email.resetPassword.subject",null,locale);
        String message = null;
        try {
            message = messageSource.getMessage("email.salutation",null,locale)+", \n" +
                    messageSource.getMessage("email.resetPassword.message",null,locale)+" \n" +
                    messageSource.getMessage("email.userIp",null,locale)+InetAddress.getLocalHost().getHostAddress()+" \n" +
                    messageSource.getMessage("email.userTime",null,locale)+new Date();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e.getMessage());
        }

        emailService.sendEmail(emailService.sendAccountRelatedUpdateEmail(existingToken.getUser().getUserEmail(), message, subject));
        log.info("----used token is deleted----");
        confirmationTokenRepository.delete(existingToken);
        log.info("----resetPassword() method executed success----");
        return new ResponseEntity<String>("Password Changed", null, HttpStatus.OK);
    }

    /**
     * Method to allow logged-in user to update account password
     * @param principal
     * @param passwordUpdateDto
     * @return
     */
    @Override
    public ResponseEntity updatePassword(Principal principal, PasswordUpdateDto passwordUpdateDto){
        log.info("----inside updatePassword() method----");
        User user = userRepository.findByUserEmailIgnoreCase(principal.getName());
        if(!passwordEncoder.matches(passwordUpdateDto.getCurrentPassword(),user.getUserPassword())){
            log.error("----invalid current password----");
            throw new BadCredentialsException("Invalid Current Password");
        }
        if(!passwordUpdateDto.getNewPassword().equals(passwordUpdateDto.getConfirmNewPassword())){
            log.error("----Confirm password and password not matched----");
            throw new BadCredentialsException("Password and Confirm Password not match");
        }
        user.setUserPassword(passwordEncoder.encode(passwordUpdateDto.getConfirmNewPassword()));
        Date date = new Date();
        user.setUserPasswordUpdateDate(date);
        userRepository.save(user);
        log.info("----password updated----");
        log.info("----sending email to user regarding password update.....----");
        String subject = messageSource.getMessage("email.updatePassword.subject",null,locale);
        String message = null;
        try {
            message = messageSource.getMessage("email.salutation",null,locale)+", \n" +
                    messageSource.getMessage("email.updatePassword.message",null,locale)+" \n" +
                    messageSource.getMessage("email.userIp",null,locale)+InetAddress.getLocalHost().getHostAddress()+" \n" +
                    messageSource.getMessage("email.userTime",null,locale)+date;
        } catch (UnknownHostException e) {
            throw new RuntimeException(e.getMessage());
        }

        emailService.sendEmail(emailService.sendAccountRelatedUpdateEmail(user.getUserEmail(),message,subject));
        log.info("----updatePassword() method executed success----");
        return new ResponseEntity("Password Updated",null,HttpStatus.OK);
    }

    /**
     * Method to retrieve user profile
     * @param path
     * @param user
     * @return userProfile
     */
    @Override
    public User retrieveUserProfile(String path, User user){
        log.info("----inside retrieveUserProfile() method----");
        String filePath = path+ File.separator+"users";
        String userId = user.getUserId().toString();
        String imageName = imageService.searchImage(filePath, userId);
        UserImage image = new UserImage(filePath+File.separator+imageName);
        if (imageName!=null) user.setImage(image);
        else user.setImage(null);
        log.info("----retrieveUserProfile() method executed success----");
        return user;
    }

    /**
     * Method to update profile image of a user
     * @param path
     * @param imageFile
     * @param principal
     * @return imagePath
     */
    @Override
    public ResponseEntity updateUserProfileImage(String path, MultipartFile imageFile, Principal principal){
        log.info("----inside updateUserProfileImage() method----");
        String filePath = path+File.separator+"users";
        User user = userRepository.findByUserEmailIgnoreCase(principal.getName());
        String userId = user.getUserId().toString();
        String imageName = imageService.uploadImage(filePath, imageFile, userId);
        log.info("----updateUserProfileImage() method executed success----");
        return new ResponseEntity<String>(imageName,null,HttpStatus.CREATED);
    }

    /**
     * Method to update user address details
     * @param address
     * @param addressDetailDto
     * @return address
     */
    @Override
    public Address updateUserAddressDetails(Address address, AddressDetailDto addressDetailDto){
        log.info("----inside updateUserAddressDetails() method----");
        if(addressDetailDto.getCity()!=null && !addressDetailDto.getCity().trim().isEmpty()){
            address.setCity(addressDetailDto.getCity());
        }
        if(addressDetailDto.getState()!=null && !addressDetailDto.getState().trim().isEmpty()){
            address.setState(addressDetailDto.getState());
        }
        if(addressDetailDto.getCountry()!=null && !addressDetailDto.getCountry().trim().isEmpty()){
            address.setCountry(addressDetailDto.getCountry());
        }
        if(addressDetailDto.getAddressLine()!=null && !addressDetailDto.getAddressLine().trim().isEmpty()){
            address.setAddressLine(addressDetailDto.getAddressLine());
        }
        if(addressDetailDto.getPostalCode()!=null && !addressDetailDto.getPostalCode().trim().isEmpty()){
            address.setPostalCode(addressDetailDto.getPostalCode());
        }
        if(addressDetailDto.getLabel()!=null && !addressDetailDto.getLabel().trim().isEmpty()){
            address.setLabel(addressDetailDto.getLabel());
        }
        log.info("----updateUserAddressDetails() method executed success----");
        return address;
    }
}
