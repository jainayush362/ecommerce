package com.ayushjainttn.bootcampproject.ecommerce.service.impl;

import com.ayushjainttn.bootcampproject.ecommerce.dto.address.AddressDetailDto;
import com.ayushjainttn.bootcampproject.ecommerce.dto.seller.SellerProfileDto;
import com.ayushjainttn.bootcampproject.ecommerce.dto.seller.SellerProfileUpdateDto;
import com.ayushjainttn.bootcampproject.ecommerce.dto.seller.SellerRegisterationDto;
import com.ayushjainttn.bootcampproject.ecommerce.entity.Address;
import com.ayushjainttn.bootcampproject.ecommerce.entity.Seller;
import com.ayushjainttn.bootcampproject.ecommerce.exceptions.UserAlreadyExistsException;
import com.ayushjainttn.bootcampproject.ecommerce.repository.RoleRepository;
import com.ayushjainttn.bootcampproject.ecommerce.repository.SellerRepository;
import com.ayushjainttn.bootcampproject.ecommerce.repository.UserRepository;
import com.ayushjainttn.bootcampproject.ecommerce.service.EmailService;
import com.ayushjainttn.bootcampproject.ecommerce.service.SellerService;
import com.ayushjainttn.bootcampproject.ecommerce.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.Principal;
import java.util.Date;
import java.util.Locale;

@Service
@Slf4j
public class SellerServiceImpl implements SellerService {
    @Autowired
    private SellerRepository sellerRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private EmailService emailService;
    @Autowired
    private UserService userService;
    @Value("${service.url}")
    private String baseUrl;
    @Value("${admin.email}")
    private String adminEmail;
    @Autowired
    private MessageSource messageSource;
    private Locale locale = LocaleContextHolder.getLocale();

    /**
     * Registers a new Seller
     * @param sellerDto
     * @return
     */
    @Override
    public ResponseEntity registerNewSeller(SellerRegisterationDto sellerDto){
        //check if password and confirm password are same
        log.info("----inside registerNewSeller() method----");
        if(!sellerDto.getUserPassword().equals(sellerDto.getUserConfirmPassword())){
            log.error("----Confirm password and password not matched----");
            throw new BadCredentialsException("Confirm Password not same as Password!");
        }

        if(userRepository.findByUserEmailIgnoreCase(sellerDto.getUserEmail())!=null){
            log.error("----user already exists----");
            throw new UserAlreadyExistsException("User already exists with given email id : "+sellerDto.getUserEmail()+" !");
        }

        if(sellerRepository.findBySellerGstNumber(sellerDto.getSellerGstNumber())!=null){
            log.error("----gst number already exists----");
            throw new UserAlreadyExistsException("Seller already exists with given GST number : "+sellerDto.getSellerGstNumber()+" !");
        }

        if(sellerRepository.findBySellerCompanyNameIgnoreCase(sellerDto.getSellerCompanyName())!=null){
            log.error("----company name already exists----");
            throw new UserAlreadyExistsException("Seller already exists with given Company Name : "+sellerDto.getSellerCompanyName()+" !");
        }

        Seller newSeller = modelMapper.map(sellerDto, Seller.class);
        String encodedPassword = passwordEncoder.encode(newSeller.getUserPassword());
        newSeller.setUserPassword(encodedPassword);
        newSeller.setUserPasswordUpdateDate(new Date());
        newSeller.setRole(roleRepository.findByAuthority("ROLE_SELLER"));
        userRepository.save(newSeller);
        log.info("----seller registered----");

        log.info("----sending email to admin regarding seller activation......----");
        String adminSubject = messageSource.getMessage("email.admin.account.activation.subject",null,locale);
        String adminMessage = null;
        try {
            adminMessage = messageSource.getMessage("email.admin.account.activation.message",null,locale)+", \n" +
                    messageSource.getMessage("email.userId",null,locale)+userRepository.findByUserEmailIgnoreCase(newSeller.getUserEmail()).getUserId()+" \n" +
                    messageSource.getMessage("email.userIp",null,locale)+InetAddress.getLocalHost().getHostAddress()+" \n" +
                    messageSource.getMessage("email.userTime",null,locale)+new Date()+"\n"+
                    baseUrl+"/admin/activate/users/"+newSeller.getUserId();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e.getMessage());
        }
        emailService.sendEmail(emailService.sendAccountRelatedUpdateEmail(adminEmail,adminMessage,adminSubject));

        //Used Thread.sleep because mails are being sent to two users one after another
        //but as emails are going in async manner, so a mail is sent to same user 2 times in this case
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        log.info("----sending email to seller regarding registration......----");
        String sellerSubject = messageSource.getMessage("email.seller.account.activation.subject",null,locale);
        String sellerMessage = messageSource.getMessage("email.salutation",null,locale)+", \n" +
                messageSource.getMessage("email.seller.account.activation.message",null,locale);
        emailService.sendEmail(emailService.sendAccountRelatedUpdateEmail(newSeller.getUserEmail(), sellerMessage, sellerSubject));
        log.info("----senller registered. method executed success----");
        return new ResponseEntity<String>("Account activation waiting for approval!", null, HttpStatus.CREATED);
    }

    /**
     * Retrieves seller profile details
     * @param path
     * @param principal
     * @return seller profile
     */
    @Override
    public ResponseEntity retrieveSellerProfile(String path, Principal principal){
        log.info("----inside retrieveSellerProfile() method----");
        Seller seller = sellerRepository.findByUserEmailIgnoreCase(principal.getName());
        seller = (Seller) userService.retrieveUserProfile(path, seller);
        SellerProfileDto sellerProfileDto = modelMapper.map(seller, SellerProfileDto.class);
        log.info("----profile retrieved. method executed success----");
        return new ResponseEntity<SellerProfileDto>(sellerProfileDto, HttpStatus.OK);
    }

    /**
     * Updates seller Profile details
     * @param principal
     * @param sellerProfileUpdateDto
     * @return
     */
    @Override
    public ResponseEntity updateSellerProfileDetails(Principal principal, SellerProfileUpdateDto sellerProfileUpdateDto){
        log.info("----inside updateSellerProfileDetails() method----");
        Seller seller = sellerRepository.findByUserEmailIgnoreCase(principal.getName());
        if(sellerProfileUpdateDto.getUserFirstName()!=null && !sellerProfileUpdateDto.getUserFirstName().trim().isEmpty()){
            seller.setUserFirstName(sellerProfileUpdateDto.getUserFirstName());
        }
        if(sellerProfileUpdateDto.getUserMiddleName()!=null && !sellerProfileUpdateDto.getUserMiddleName().trim().isEmpty()){
            seller.setUserMiddleName(sellerProfileUpdateDto.getUserMiddleName());
        }
        if(sellerProfileUpdateDto.getUserLastName()!=null && !sellerProfileUpdateDto.getUserLastName().trim().isEmpty()){
            seller.setUserLastName(sellerProfileUpdateDto.getUserLastName());
        }
        if(sellerProfileUpdateDto.getSellerCompanyContact()!=null && !sellerProfileUpdateDto.getSellerCompanyContact().trim().isEmpty()){
            seller.setSellerCompanyContact(sellerProfileUpdateDto.getSellerCompanyContact());
        }
        if(sellerProfileUpdateDto.getSellerCompanyName()!=null && !sellerProfileUpdateDto.getSellerCompanyName().trim().isEmpty() && sellerRepository.findBySellerCompanyNameIgnoreCase(sellerProfileUpdateDto.getSellerCompanyName())==null){
            seller.setSellerCompanyName(sellerProfileUpdateDto.getSellerCompanyName());
        }
        sellerRepository.save(seller);
        log.info("----profile updated. method executed success----");
        return new ResponseEntity<String>("Profile Updated.",null, HttpStatus.OK);
    }

    /**
     * Updates seller company address
     * @param principal
     * @param addressDetailDto
     * @return
     */
    @Override
    public ResponseEntity updateSellerCompanyAddress(Principal principal, AddressDetailDto addressDetailDto){
        log.info("----inside updateSellerCompanyAddress() method----");
        Seller seller = sellerRepository.findByUserEmailIgnoreCase(principal.getName());
        Address address = seller.getAddress();
        address = userService.updateUserAddressDetails(address,addressDetailDto);
        sellerRepository.save(seller);
        log.info("----company address updated. method executed success----");
        return new ResponseEntity<String>("Address Updated.", null, HttpStatus.OK);
    }
}
