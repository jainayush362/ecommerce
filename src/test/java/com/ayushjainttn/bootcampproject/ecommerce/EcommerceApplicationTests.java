package com.ayushjainttn.bootcampproject.ecommerce;

import com.ayushjainttn.bootcampproject.ecommerce.entity.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashSet;
import java.util.Set;

@SpringBootTest
class EcommerceApplicationTests {

//	@Autowired
//	private UserRepository userRepository;
//
//	@Autowired
//	private RoleRepository roleRepository;

//    @Autowired
//    private CategoryRepository categoryRepository;
//    @Autowired
//    private CategoryMetadataFieldRepository categoryMetadataFieldRepository;

    @Test
    void contextLoads() {
    }

//	@Test
//	void testAddUsers(){
//		Customer user1 = new Customer();
//		user1.setUserEmail("ayush@gmail.com");
//		user1.setUserFirstName("Ayush");
//		user1.setUserLastName("Jain");
//		user1.setUserPassword("AJ@test123");
////		java.util.Date dt = new java.util.Date();
////		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
////		user1.setUserPasswordUpdateDate(sdf.format(dt));
//		user1.setCustomerContact("9968810125");
//		Address address1 = new Address();
//		address1.setCity("Noida");
//		address1.setState("Uttar Pradesh");
//		address1.setCountry("India");
//		address1.setPostalCode("201301");
//		address1.setAddressLine("C-286, Sector-19");
//		address1.setLabel("H");
//		user1.addAddress(address1);
//		Role role = roleRepository.findById(3L).get();
//		user1.setRole(role);
//		userRepository.save(user1);

//		Seller seller1 = new Seller();
//		seller1.setUserFirstName("Sunil");
//		seller1.setUserMiddleName("Kumar");
//		seller1.setUserLastName("Jain");
//		seller1.setUserEmail("skjain@gmail.com");
//		seller1.setUserPassword("SKJ@test123");
//		java.util.Date dt = new java.util.Date();
//		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		seller1.setUserPasswordUpdateDate(sdf.format(dt));
//		seller1.setSellerGstNumber("18AABCU9603R1ZM");
//		seller1.setSellerCompanyContact("9078654521");
//		seller1.setSellerCompanyName("SKJ Enterprises Pvt. Ltd.");
//		Address address = new Address();
//		address.setCity("Delhi");
//		address.setState("Delhi");
//		address.setCountry("India");
//		address.setPostalCode("110010");
//		address.setAddressLine("11/24B, Rani Bagh");
//		address.setAddressType('S');
//		address.setLabel('O');
//		seller1.setAddress(address);
//		Role role = roleRepository.findById(2).get();
//		seller1.getRoles().add(role);
//		userRepository.save(seller1);
//	}
}
