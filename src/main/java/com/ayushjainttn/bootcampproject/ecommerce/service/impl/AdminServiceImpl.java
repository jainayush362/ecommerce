package com.ayushjainttn.bootcampproject.ecommerce.service.impl;

import com.ayushjainttn.bootcampproject.ecommerce.dto.customer.CustomerDetailDto;
import com.ayushjainttn.bootcampproject.ecommerce.dto.seller.SellerDetailDto;
import com.ayushjainttn.bootcampproject.ecommerce.entity.User;
import com.ayushjainttn.bootcampproject.ecommerce.repository.CustomerRepository;
import com.ayushjainttn.bootcampproject.ecommerce.repository.SellerRepository;
import com.ayushjainttn.bootcampproject.ecommerce.repository.UserRepository;
import com.ayushjainttn.bootcampproject.ecommerce.service.AdminService;
import com.ayushjainttn.bootcampproject.ecommerce.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
@Slf4j
public class AdminServiceImpl implements AdminService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SellerRepository sellerRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private EmailService emailService;
    @Autowired
    private MessageSource messageSource;
    private Locale locale = LocaleContextHolder.getLocale();

    /**
     * @param pageOffset
     * @param pageSize
     * @param sortProperty
     * @param email
     * @param sortDirection
     * @return Page of Sellers
     */
    @Override
    public Page findAllSellers(int pageOffset, int pageSize, String sortProperty, Optional<String> email, String sortDirection){
        log.info("----inside findAllSellers() method----");
        Sort.Direction direct = (sortDirection.equalsIgnoreCase("ASC"))?Sort.Direction.ASC:Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(pageOffset,pageSize, Sort.by(new Sort.Order(direct, sortProperty)));
        if(email.isEmpty()){
            log.info("----filter email not provided. method execution completed----");
            return sellerRepository.findAll(pageable).map(seller -> modelMapper.map(seller, SellerDetailDto.class));
        }
        log.info("----method execution completed----");
        return sellerRepository.findByUserEmailContainingIgnoreCase(email.get(),pageable).map(seller -> modelMapper.map(seller, SellerDetailDto.class));
    }

    /**
     * @param pageOffset
     * @param pageSize
     * @param sortProperty
     * @param email
     * @param sortDirection
     * @return Page of Customers
     */
    @Override
    public Page findAllCustomers(int pageOffset, int pageSize, String sortProperty, Optional<String> email, String sortDirection){
        log.info("----inside findAllCustomers() method----");
        Sort.Direction direct = (sortDirection.equalsIgnoreCase("ASC"))?Sort.Direction.ASC:Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(pageOffset,pageSize, Sort.by(new Sort.Order(direct, sortProperty)));
        if(email.isEmpty()){
            log.info("----filter email not provided. method execution completed----");
            return customerRepository.findAll(pageable).map(customer -> modelMapper.map(customer, CustomerDetailDto.class));
        }
        log.info("----method execution completed----");
        return customerRepository.findByUserEmailContainingIgnoreCase(email.get(),pageable).map(customer -> modelMapper.map(customer, CustomerDetailDto.class));
    }

    /**
     * @param userId
     * @return activates a user
     */
    @Override
    public ResponseEntity activateUser(int userId){
        log.info("----inside activateUser() method----");
        User user = userRepository.findById((long)userId).get();
        if(user==null){
            log.error("----no user found----");
            throw new UsernameNotFoundException("No account exists with given id");
        }
        if(user.getRole().getAuthority().equals("ROLE_ADMIN")){
            log.error("----admin privilege user----");
            throw new RuntimeException("Operation not allowed as given id has admin privileges");
        }
        if (user.getUserIsActive().booleanValue()){
            log.info("----user already active. method executed success----");
            return new ResponseEntity<String>("User already active",null, HttpStatus.OK);
        }
        userRepository.updateIsActiveStatus(true, userId);
        log.info("----user activation email sending...........----");
        String subject = messageSource.getMessage("email.user.account.activated.subject",null,locale);
        String message = messageSource.getMessage("email.salutation",null,locale)+", \n" +
                messageSource.getMessage("email.user.account.activated.message",null,locale);

        emailService.sendEmail(emailService.sendAccountRelatedUpdateEmail(user.getUserEmail(),message,subject));
        log.info("----user activated. method executed success----");
        return new ResponseEntity<>("Account Activation Success",null,HttpStatus.OK);
    }

    /**
     * @param userId
     * @return deactivates a user
     */
    @Override
    public ResponseEntity deactivateUser(int userId){
        log.info("----inside deactivateUser() method----");
        User user = userRepository.findById((long)userId).get();
        if(user==null){
            log.error("----no user found----");
            throw new UsernameNotFoundException("No account exists with given id");
        }
        if(user.getRole().getAuthority().equals("ROLE_ADMIN")){
            log.error("----admin privilege user----");
            throw new RuntimeException("Operation not allowed as given id has admin privileges");
        }
        if (!user.getUserIsActive().booleanValue()){
            log.info("----user already inactive. method executed success----");
            return new ResponseEntity<String>("User already inactive",null, HttpStatus.OK);
        }
        userRepository.updateIsActiveStatus(false,userId);
        log.info("----user deactivation email sending...........----");
        String subject = messageSource.getMessage("email.user.account.deactivated.subject",null,locale);
        String message = messageSource.getMessage("email.salutation",null,locale)+", \n" +
                messageSource.getMessage("email.user.account.deactivated.message",null,locale);

        emailService.sendEmail(emailService.sendAccountRelatedUpdateEmail(user.getUserEmail(),message,subject));
        log.info("----user deactivated. method executed success----");
        return new ResponseEntity<>("Account Deactivation Success",null,HttpStatus.OK);
    }

    /**
     * @return List of inactive sellers
     */
    @Override
    public List<SellerDetailDto> getAllInactiveSeller(){
        log.info("----inside getAllInactiveSeller() method----");
        return sellerRepository.findAll().stream().filter(seller -> seller.getUserIsActive()==false).map(seller -> modelMapper.map(seller, SellerDetailDto.class)).toList();
    }

    /**
     * @return List of active sellers
     */
    @Override
    public List<SellerDetailDto> getAllActiveSeller(){
        log.info("----inside getAllActiveSeller() method----");
        return sellerRepository.findAll().stream().filter(seller -> seller.getUserIsActive()==true).map(seller -> modelMapper.map(seller, SellerDetailDto.class)).toList();
    }
}
