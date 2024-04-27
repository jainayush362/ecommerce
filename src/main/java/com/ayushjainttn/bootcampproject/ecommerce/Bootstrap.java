package com.ayushjainttn.bootcampproject.ecommerce;

import com.ayushjainttn.bootcampproject.ecommerce.entity.*;
import com.ayushjainttn.bootcampproject.ecommerce.repository.CustomerRepository;
import com.ayushjainttn.bootcampproject.ecommerce.repository.RoleRepository;
import com.ayushjainttn.bootcampproject.ecommerce.repository.SellerRepository;
import com.ayushjainttn.bootcampproject.ecommerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class Bootstrap implements ApplicationRunner {

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SellerRepository sellerRepository;
    @Autowired
    private CustomerRepository customerRepository;

    @Value("${admin.email}")
    private String adminEmail;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if(roleRepository.count()<1){
            createRoles();
        }
        if(userRepository.count()<1){
            createAdmin();
        }
    }

    private void createRoles(){
        Role roleAdmin = new Role();
        roleAdmin.setAuthority("ROLE_ADMIN");
        roleRepository.save(roleAdmin);

        Role roleSeller = new Role();
        roleSeller.setAuthority("ROLE_SELLER");
        roleRepository.save(roleSeller);

        Role roleCustomer = new Role();
        roleCustomer.setAuthority("ROLE_CUSTOMER");
        roleRepository.save(roleCustomer);
    }

    private void createAdmin(){
        User admin = new User();
        admin.setUserFirstName("Admin");
        admin.setUserLastName("AYUSH");
        admin.setUserEmail(adminEmail);
        admin.setUserIsActive(true);
        admin.setUserPassword(new BCryptPasswordEncoder().encode("Admin@123"));
        admin.setUserPasswordUpdateDate(new Date());
        Role role = roleRepository.findByAuthority("ROLE_ADMIN");
        admin.setRole(role);
        userRepository.save(admin);
    }
}
