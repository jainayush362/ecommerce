package com.ayushjainttn.bootcampproject.ecommerce.service.impl;

import com.ayushjainttn.bootcampproject.ecommerce.dto.address.AddressDetailDto;
import com.ayushjainttn.bootcampproject.ecommerce.dto.address.AddressDto;
import com.ayushjainttn.bootcampproject.ecommerce.dto.customer.CustomerProfileDto;
import com.ayushjainttn.bootcampproject.ecommerce.dto.customer.CustomerProfileUpdateDto;
import com.ayushjainttn.bootcampproject.ecommerce.dto.customer.CustomerRegisterationDto;
import com.ayushjainttn.bootcampproject.ecommerce.dto.email.EmailDto;
import com.ayushjainttn.bootcampproject.ecommerce.entity.*;
import com.ayushjainttn.bootcampproject.ecommerce.exceptions.ResourceNotFoundException;
import com.ayushjainttn.bootcampproject.ecommerce.exceptions.GenericActivationException;
import com.ayushjainttn.bootcampproject.ecommerce.exceptions.UserAlreadyExistsException;
import com.ayushjainttn.bootcampproject.ecommerce.repository.CustomerRepository;
import com.ayushjainttn.bootcampproject.ecommerce.repository.RoleRepository;
import com.ayushjainttn.bootcampproject.ecommerce.repository.UserRepository;
import com.ayushjainttn.bootcampproject.ecommerce.repository.ConfirmationTokenRepository;
import com.ayushjainttn.bootcampproject.ecommerce.service.CustomerService;
import com.ayushjainttn.bootcampproject.ecommerce.service.EmailService;
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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Date;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CustomerServiceImpl implements CustomerService {
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ConfirmationTokenRepository confirmationTokenRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private UserService userService;
    @Value("${service.url}")
    private String baseUrl;
    @Autowired
    private MessageSource messageSource;
    private Locale locale = LocaleContextHolder.getLocale();

    /**
     * Registers a new customer
     * @param customerDto
     * @return
     */
    @Override
    public ResponseEntity registerNewCustomer(CustomerRegisterationDto customerDto){
        log.info("----inside registerNewCustomer() method----");
        if(!customerDto.getUserPassword().equals(customerDto.getUserConfirmPassword())){
            log.error("----Confirm password and password not matched----");
            throw new BadCredentialsException("Confirm Password not same as Password!");
        }
        if(userRepository.findByUserEmailIgnoreCase(customerDto.getUserEmail())!=null){
            log.error("----user already exists----");
            throw new UserAlreadyExistsException("User already exists with given email id : "+customerDto.getUserEmail()+" !");
        }
        Customer newCustomer = modelMapper.map(customerDto, Customer.class);
        String encodedPassword = passwordEncoder.encode(newCustomer.getUserPassword());
        newCustomer.setUserPassword(encodedPassword);
        newCustomer.setUserPasswordUpdateDate(new Date());
        newCustomer.setRole(roleRepository.findByAuthority("ROLE_CUSTOMER"));
        userRepository.save(newCustomer);
        log.info("----customer registered----");
        generateCustomerConfirmationToken(newCustomer);
        log.info("----customer registration success. method executed success----");
        return new ResponseEntity<String>("Please check your email to confirm account.", null, HttpStatus.CREATED);
    }

    /**
     * Retrieves customer profile
     * @param path
     * @param principal
     * @return Customer Profile
     */
    @Override
    public ResponseEntity retrieveCustomerProfile(String path, Principal principal){
        log.info("----inside retrieveCustomerProfile() method----");
        Customer customer = customerRepository.findByUserEmailIgnoreCase(principal.getName());
        customer = (Customer) userService.retrieveUserProfile(path, customer);
        CustomerProfileDto customerProfileDto = modelMapper.map(customer, CustomerProfileDto.class);
        log.info("----profile retrieved. method executed success----");
        return new ResponseEntity<CustomerProfileDto>(customerProfileDto, HttpStatus.OK);
    }

    /**
     * Retrieves customer address list
     * @param principal
     * @return Address
     */
    @Override
    public ResponseEntity retrieveCustomerAddress(Principal principal){
        log.info("----inside retrieveCustomerAddress() method----");
        Customer customer = customerRepository.findByUserEmailIgnoreCase(principal.getName());
        Set<AddressDetailDto> addressSet = customer.getAddressSet().stream().map(address -> modelMapper.map(address, AddressDetailDto.class)).collect(Collectors.toSet());
        log.info("----address retrieved. method executed success----");
        return new ResponseEntity<Set>(addressSet, HttpStatus.OK);
    }

    /**
     * Adds new address to customer address list
     * @param principal
     * @param addressDto
     * @return
     */
    @Override
    public ResponseEntity addCustomerAddress(Principal principal, AddressDto addressDto){
        log.info("----inside addCustomerAddress() method----");
        Customer customer = customerRepository.findByUserEmailIgnoreCase(principal.getName());
        Address address = modelMapper.map(addressDto, Address.class);
        customer.addAddress(address);
        customerRepository.save(customer);
        log.info("----address added. method executed success----");
        return new ResponseEntity<String>("Address added",null,HttpStatus.CREATED);
    }

    /**
     * Updates Customer profile
     * @param principal
     * @param customerProfileUpdateDto
     * @return
     */
    @Override
    public ResponseEntity updateCustomerProfileDetails(Principal principal, CustomerProfileUpdateDto customerProfileUpdateDto){
        log.info("----inside updateCustomerProfileDetails() method----");
        Customer customer = customerRepository.findByUserEmailIgnoreCase(principal.getName());
        if(customerProfileUpdateDto.getUserFirstName()!=null && !customerProfileUpdateDto.getUserFirstName().trim().isEmpty()){
            customer.setUserFirstName(customerProfileUpdateDto.getUserFirstName());
        }
        if(customerProfileUpdateDto.getUserMiddleName()!=null && !customerProfileUpdateDto.getUserMiddleName().trim().isEmpty()){
            customer.setUserMiddleName(customerProfileUpdateDto.getUserMiddleName());
        }
        if(customerProfileUpdateDto.getUserLastName()!=null && !customerProfileUpdateDto.getUserLastName().trim().isEmpty()){
            customer.setUserLastName(customerProfileUpdateDto.getUserLastName());
        }
        if(customerProfileUpdateDto.getCustomerContact()!=null && !customerProfileUpdateDto.getCustomerContact().trim().isEmpty()){
            customer.setCustomerContact(customerProfileUpdateDto.getCustomerContact());
        }
        customerRepository.save(customer);
        log.info("----profile updated. method executed success----");
        return new ResponseEntity<String>("Profile Updated.",null, HttpStatus.OK);
    }

    /**
     * Updates existing address using address ID
     * @param principal
     * @param addressId
     * @param addressDetailDto
     * @return
     */
    @Override
    public ResponseEntity updateCustomerAddress(Principal principal, int addressId, AddressDetailDto addressDetailDto){
        log.info("----inside updateCustomerAddress() method----");
        Customer customer = customerRepository.findByUserEmailIgnoreCase(principal.getName());
        Set<Address> addressSet = customer.getAddressSet();
        Address address = addressSet.stream()
                .filter(address1 -> address1.getAddressId()==addressId)
                .findFirst()
                .orElseThrow(()-> new ResourceNotFoundException("No address found with id : "+addressId));
        address = userService.updateUserAddressDetails(address, addressDetailDto);
        customerRepository.save(customer);
        log.info("----address updated. method executed success----");
        return new ResponseEntity<String>("Address Updated.", null, HttpStatus.OK);
    }

    /**
     * Removes address using address ID
     * @param principal
     * @param addressId
     * @return
     */
    @Override
    public ResponseEntity removeCustomerAddress(Principal principal, int addressId){
        log.info("----inside removeCustomerAddress() method----");
        Customer customer = customerRepository.findByUserEmailIgnoreCase(principal.getName());
        Set<Address> addressSet = customer.getAddressSet();
        Address address = addressSet.stream()
                .filter(address1 -> address1.getAddressId()==addressId)
                .findFirst()
                .orElseThrow(()->new ResourceNotFoundException("No address found with given id : "+addressId));
        customer.deleteAddress(address);
        customerRepository.save(customer);
        log.info("----address deleted. method executed success----");
        return new ResponseEntity<String>("Address Deleted Successfully", null, HttpStatus.NO_CONTENT);
    }

    /**
     * Generates new confirmation token
     * @param customer
     */
    @Override
    public void generateCustomerConfirmationToken(Customer customer) {
        log.info("----inside generateCustomerConfirmationToken() method----");
        ConfirmationToken confirmationToken = new ConfirmationToken(customer);
        confirmationTokenRepository.save(confirmationToken);
        log.info("----generated token ready to sent on user email----");
        String subject = messageSource.getMessage("email.customer.account.activation.subject",null,locale);
        String message = messageSource.getMessage("email.salutation",null,locale)+", \n" +
                messageSource.getMessage("email.customer.account.activation.message",null,locale)+" \n" +
                baseUrl+"/confirm?token="+confirmationToken.getConfirmationToken();

        emailService.sendEmail(emailService.sendAccountRelatedUpdateEmail(customer.getUserEmail(),message,subject));
        log.info("----token generated sent. method executed success----");
    }

    /**
     * Validates the given confirmation token
     * @param token
     * @return activates customer if token is valid
     */
    @Override
    public ResponseEntity checkConfirmationToken(String token) {
        log.info("----inside checkConfirmationToken() method----");
        ConfirmationToken existingToken = confirmationTokenRepository.findByConfirmationToken(token);
        if (existingToken == null) {
            log.info("----no token found. method executed success----");
            return new ResponseEntity<String>("Invalid token!", null, HttpStatus.UNAUTHORIZED);
        }
        if (existingToken.isTokenExpired(GlobalExpressions.SECONDS_ACCOUNT_ACTIVATION_TOKEN)) {
            log.info("----token is expired----");
            generateCustomerConfirmationToken((Customer) existingToken.getUser());
            confirmationTokenRepository.delete(existingToken);
            log.info("----new token sent. method executed success----");
            return new ResponseEntity<String>("Token expired, email for a new token sent!", null, HttpStatus.OK);
        }
        User customer = userRepository.findByUserEmailIgnoreCase(existingToken.getUser().getUserEmail());
        customer.setUserIsActive(true);
        userRepository.save(customer);
        confirmationTokenRepository.delete(existingToken);
        log.info("----customer activation mail sending......----");
        String subject = messageSource.getMessage("email.customer.account.register.subject",null,locale);
        String message = messageSource.getMessage("email.salutation",null,locale)+", \n" +
                messageSource.getMessage("email.customer.account.register.message",null,locale);

        emailService.sendEmail(emailService.sendAccountRelatedUpdateEmail(customer.getUserEmail(),message,subject));
        log.info("----customer activated. method executed success----");
        return new ResponseEntity<String>("Account Activated Successfully!", null, HttpStatus.OK);
    }

    /**
     * Resends the new confirmation token on user email
     * @param emailDto
     * @return new token to user email
     */
    @Override
    public ResponseEntity resendConfirmationToken(EmailDto emailDto){
        log.info("----inside resendConfirmationToken() method----");
        User customer = userRepository.findByUserEmailIgnoreCase(emailDto.getUserEmail());
        if(!customer.getRole().getAuthority().equals("ROLE_CUSTOMER")){
            log.error("----user role issue. no customer exists with given email----");
            throw new UsernameNotFoundException("No customer exists with email : "+emailDto.getUserEmail());
        }
        if(customer==null){
            log.error("----no customer exists with given email----");
            throw new UsernameNotFoundException("No customer exists with email : "+emailDto.getUserEmail());
        }
        if(customer.getUserIsActive().booleanValue()){
            log.error("----customer already active----");
            throw new GenericActivationException("The user with email : "+emailDto.getUserEmail()+" is already activated. Kindly try to login or reset password!");
        }
        ConfirmationToken existingToken = confirmationTokenRepository.findByUser(customer);
        if (existingToken!=null){
            log.info("----existing token is deleted----");
            confirmationTokenRepository.delete(existingToken);
        }
        generateCustomerConfirmationToken((Customer) customer);
        log.info("----token is resend. method executed success----");
        return new ResponseEntity<String>("Activation Link sent again. Check your mail!", null, HttpStatus.OK);
    }
}
