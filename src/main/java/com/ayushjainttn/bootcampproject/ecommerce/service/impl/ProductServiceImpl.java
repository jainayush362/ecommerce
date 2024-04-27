package com.ayushjainttn.bootcampproject.ecommerce.service.impl;

import com.ayushjainttn.bootcampproject.ecommerce.dto.product.*;
import com.ayushjainttn.bootcampproject.ecommerce.entity.*;
import com.ayushjainttn.bootcampproject.ecommerce.exceptions.GenericActivationException;
import com.ayushjainttn.bootcampproject.ecommerce.exceptions.ResourceNotFoundException;
import com.ayushjainttn.bootcampproject.ecommerce.payload.ProductImage;
import com.ayushjainttn.bootcampproject.ecommerce.repository.*;
import com.ayushjainttn.bootcampproject.ecommerce.service.EmailService;
import com.ayushjainttn.bootcampproject.ecommerce.service.ImageService;
import com.ayushjainttn.bootcampproject.ecommerce.service.ProductService;
import com.nimbusds.jose.shaded.json.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.security.Principal;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductVariationRepository productVariationRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private SellerRepository sellerRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private ModelMapper modelMapper;
    @Value("${service.url}")
    private String baseUrl;
    @Value("${admin.email}")
    private String adminEmail;
    @Autowired
    private ImageService imageService;
    @Autowired
    private MessageSource messageSource;
    private Locale locale = LocaleContextHolder.getLocale();

    /**
     * Method to check whether the product belongs to a seller or not
     * @param principal
     * @param product
     * @param productId
     */
    @Override
    public void checkSellerProductValidity(Principal principal, Product product, long productId){
        log.info("----inside checkSellerProductValidity() method----");
        if(product==null || !product.getSeller().getUserEmail().equalsIgnoreCase(principal.getName())){
            log.error("----no product found with given id for seller----");
            throw new ResourceNotFoundException("No product exists with given id : "+productId);
        }
        log.info("----product is found for the seller----");
        log.info("---- checkSellerProductValidity() method executed success----");
    }

    /**
     * Method to check product variation metadata validity in terms of structure, keys and values
     * @param product
     * @param map
     */
    @Override
    public void checkProductVariationMetadataValidity(Product product, Map<String, String> map){
        log.info("----inside checkProductVariationMetadataValidity() method----");
        //checks that if one variant already exists then newvariant should also follow the same metadata structure
        if (product.getProductVariationSet()!=null){
            log.info("----product variations are present so new metadata should be valid as per existing one----");
            Object jsonObject = product.getProductVariationSet().stream().findFirst().get().getProductVariationMetadata();
            Map<String,String> jsonMap = modelMapper.map(jsonObject, HashMap.class);
            AtomicInteger counter = new AtomicInteger();
            //to check if updated metadata consist more fields than existing metadata
            log.info("----checking metadata structure validity with existing one----");
            if(jsonMap.size()!=map.size()){
                log.error("----invalid metadata structure as new metadata size is more than existing one----");
                throw new RuntimeException("Invalid metadata. All variants of a product should have exactly same metadata structure.");
            }
            //it checks for every key value present in update dto metadata is it also present in the existing metadata or not
            log.info("----checking metadata key value validity with existing one----");
            map.forEach((key,val)->{
                //checks for the correctness of updated metadata structure as per existing metadata
                if(!jsonMap.containsKey(key)){
                    log.error("----invalid metadata structure as existing metadata doesnt contains key present in new metadata----");
                    throw new RuntimeException("Invalid metadata. All variants of a product should have exactly same metadata structure.");
                }
                //checks whether the updated metadata is exactly same as existing metadata or not --- if value is same it increments the counter
                if (jsonMap.get(key).equalsIgnoreCase(val)){
                    //variables inside lambda expressions should be either final or effectively final. Therefore, used atomic integer and applied addAndGet similar to increment.
                    counter.addAndGet(1);
                }
            });
            //checks whether the updated metadata is exactly same as existing metadata or not --- if counter >= size of existing metadata then it means that it is same
            if (counter.get()>=jsonMap.size()){
                log.error("----new metadata same as existing one----");
                throw new RuntimeException("Similar Product variant already exists");
            }
        }

        //checks that the keys and values in metadata are as per the key values present in database
        log.info("----product variations are not present----");
        log.info("----checking whether new metadata contains data bound within data present in db----");
        Category category = categoryRepository.findById(product.getCategory().getCategoryId()).get();
        List<CategoryMetadataFieldValue> fieldValueList = category.getCategoryMetadataFieldValues();
        Map<String,String> fieldValueMap= new HashMap<>();
        //it converts category metadata present in list to a map where key is metadata field and value is metadata field values
        for (CategoryMetadataFieldValue value: fieldValueList){
            fieldValueMap.put(value.getCategoryMetadataField().getCategoryMetadataFieldName(), value.getCategoryMetadataFieldValues());
        }
        //checks whether the input metadata map is as per the metadata filed values map populated above or not
        map.forEach((k,v)-> {
            if (v.trim().split(",").length!=1) {
                //System.out.println(k+" : "+v);
                log.error("----one field one value is allowed----");
                throw new RuntimeException("For each variant exactly one value is allowed for one metadata field out of all available values. To add more create new variants");
            }
            if (!fieldValueMap.containsKey(k.trim())){
                log.error("----invalid metadata field present----");
                throw new ResourceNotFoundException("No such metadata field exists : "+k);
            }
            if (fieldValueMap.containsKey(k.trim())){
                if(!Arrays.stream(fieldValueMap.get(k.trim()).split(",")).toList().contains(v.trim().toUpperCase())){
                    log.error("----invalid values for metadata field present----");
                    throw new ResourceNotFoundException(v+" value doesn't exists for metadata field - " +k+
                            " . Possible values for field - "+k+" are :: "+fieldValueMap.get(k.trim()));
                }
            }
        });
        log.info("----checkProductVariationMetadataValidity() method executed success----");
    }

    /**
     * Method to add a product
     * @param principal
     * @param productAddDto
     * @return
     */
    @Override
    public ResponseEntity addProduct(Principal principal, ProductAddDto productAddDto){
        log.info("----inside addProduct() method----");
        Seller seller = sellerRepository.findByUserEmailIgnoreCase(principal.getName());
        Product product = new Product();
        product.setProductName(productAddDto.getProductName());
        product.setProductBrand(productAddDto.getProductBrand());

        Category category = categoryRepository.findById(productAddDto.getCategoryId()).orElseThrow(()->new ResourceNotFoundException("No Category found with the give id : "+productAddDto.getCategoryId()));
        if(!category.isLeafCategory()){
            log.error("----not a leaf category----");
            throw new RuntimeException("Product cannot be added. Given category id is not a leaf category");
        }
        product.setCategory(category);

        if (productRepository.findByProductNameIgnoreCaseAndProductBrandIgnoreCaseAndCategoryAndSeller(productAddDto.getProductName(), productAddDto.getProductBrand(), category, seller)!=null){
            log.error("----product already exists----");
            throw new RuntimeException("Similar product already exists");
        }
        if (productAddDto.getProductDescription()!=null){
            product.setProductDescription(productAddDto.getProductDescription());
        }
        if (productAddDto.getIsCancellable()!=null){
            product.setProductIsCancellable(productAddDto.getIsCancellable());
        }
        if (productAddDto.getIsReturnable()!=null){
            product.setProductIsReturnable(productAddDto.getIsReturnable());
        }
        product.setSeller(seller);
        productRepository.save(product);
        log.info("----product addded----");
        //send mail to seller regarding product activation status
        log.info("----sending email to seller regarding product addition.......----");
        String sellerSubject = messageSource.getMessage("email.seller.product.added.subject",null,locale);
        String sellerMessage = messageSource.getMessage("email.salutation",null,locale)+", \n" +
                messageSource.getMessage("email.seller.product.added.message",null,locale)+product.getProductId();
        emailService.sendEmail(emailService.sendAccountRelatedUpdateEmail(seller.getUserEmail(),sellerMessage,sellerSubject));
        //Used Thread.sleep because mails are being sent to two users one after another
        //but as emails are going in async manner, so a mail is sent to same user 2 times in this case
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        //send mail to admin to activate a product
        log.info("----sending email to admin regarding product activation.....----");
        String adminSubject = messageSource.getMessage("email.admin.product.added.subject",null,locale);
        String adminMessage = messageSource.getMessage("email.admin.product.added.message",null,locale)+product.getProductId()+"\n"+
                baseUrl+"/admin/activate/product/"+product.getProductId();
        emailService.sendEmail(emailService.sendAccountRelatedUpdateEmail(adminEmail,adminMessage,adminSubject));
        log.info("----addProduct() method executed success----");
        return ResponseEntity.created(null).body("Product Added Successfully");
    }

    /**
     * Method to add a product variation
     * @param principal
     * @param productVariationAddDto
     * @param path
     * @param imageFile
     * @return
     */
    @Override
    public ResponseEntity addProductVariation(Principal principal, ProductVariationAddDto productVariationAddDto, String path, MultipartFile imageFile){
        log.info("----inside addProductVariation() method----");
        ProductVariation productVariation = new ProductVariation();
        Product product = productRepository.findUsingProductId(productVariationAddDto.getProductId());
        checkSellerProductValidity(principal, product, productVariationAddDto.getProductId());
        if (!product.getProductIsActive()){
            log.error("----product is inactive----");
            throw new GenericActivationException("Not allowed. Product with given id has not been activated yet.");
        }
        if (productVariationAddDto.getProductVariationQuantity()<0){
            log.error("----quantity is less than 0----");
            throw new RuntimeException("Quantity can only be 0 or more");
        }
        if (productVariationAddDto.getProductVariationPrice()<0){
            log.error("----price is less than 0----");
            throw new RuntimeException("Price can only be 0 or more");
        }
        productVariation.setProductVariationQuantity(productVariationAddDto.getProductVariationQuantity());
        productVariation.setProductVariationPrice(productVariationAddDto.getProductVariationPrice());
        checkProductVariationMetadataValidity(product, productVariationAddDto.getProductVariationMetadata());
        //to convert input metadata map<string,string> into a json object
        JSONObject json  = new JSONObject(productVariationAddDto.getProductVariationMetadata());
        productVariation.setProduct(product);
        productVariation.setProductVariationMetadata(json);
        productVariationRepository.save(productVariation);
        log.info("----product variation is added----");
        log.info("----uploading product variation image----");
        updateImage(path, imageFile, productVariationAddDto.getProductId(), productVariation.getProductVariationId(), "primary");
        log.info("----addProductVariation() method executed success----");
        return ResponseEntity.created(null).body("Product Variation Added");
    }

    /**
     * Method to update a product
     * @param principal
     * @param productId
     * @param productUpdateDto
     * @return
     */
    @Override
    public ResponseEntity updateProduct(Principal principal, int productId, ProductUpdateDto productUpdateDto){
        log.info("----inside updateProduct() method----");
        Product product = productRepository.findUsingProductId((long) productId);
        checkSellerProductValidity(principal,product,productId);
        if (productRepository.findByProductNameIgnoreCaseAndProductBrandIgnoreCaseAndCategoryAndSeller(productUpdateDto.getProductName(), product.getProductBrand(), product.getCategory(), product.getSeller())!=null){
            log.info("----similar product exists----");
            throw new RuntimeException("Similar product already exists");
        }
        if (productUpdateDto.getProductName()!=null && !productUpdateDto.getProductName().isEmpty()){
            product.setProductName(productUpdateDto.getProductName());
        }
        if (productUpdateDto.getProductDescription()!=null && !productUpdateDto.getProductDescription().isEmpty()){
            product.setProductDescription(productUpdateDto.getProductDescription());
        }
        if (productUpdateDto.getIsCancellable()!=null){
            product.setProductIsCancellable(productUpdateDto.getIsCancellable());
        }
        if (productUpdateDto.getIsReturnable()!=null){
            product.setProductIsReturnable(productUpdateDto.getIsReturnable());
        }
        productRepository.save(product);
        log.info("----updateProduct() method executed success----");
        return ResponseEntity.ok("Product Updated");
    }

    /**
     * Method to update a product variation
     * @param principal
     * @param productVariationId
     * @param productVariationUpdateDto
     * @param imageFile
     * @param path
     * @return
     */
    @Override
    public ResponseEntity updateProductVariation(Principal principal, int productVariationId, ProductVariationUpdateDto productVariationUpdateDto, MultipartFile imageFile, String path){
        log.info("----inside updateProductVariation() method----");
        ProductVariation productVariation = productVariationRepository.findById((long)productVariationId).orElseThrow(()->new ResourceNotFoundException("No product variation exists with given id : "+productVariationId));
        checkSellerProductValidity(principal, productVariation.getProduct(), productVariation.getProduct().getProductId());
        if(productVariation.getProduct().getProductIsDeleted()){
            log.error("----product is deleted----");
            throw new ResourceNotFoundException("No product exists with given id : "+productVariation.getProduct().getProductId());
        }
        if(!productVariation.getProduct().getProductIsActive()){
            log.error("----product is inactive----");
            throw new GenericActivationException("Not allowed. Product with given id has not been activated yet.");
        }
        String response = "";
        if (productVariationUpdateDto!=null){
            if (productVariationUpdateDto.getProductVariationQuantity()<0){
                log.error("----quantity is less than 0----");
                throw new RuntimeException("Quantity can only be 0 or more");
            }
            if (productVariationUpdateDto.getProductVariationPrice()<0){
                log.error("----price is less than 0----");
                throw new RuntimeException("Price can only be 0 or more");
            }
            if (productVariationUpdateDto.getProductVariationIsActive()!=null){
                productVariation.setProductVariationIsActive(productVariationUpdateDto.getProductVariationIsActive());
            }
            if (productVariationUpdateDto.getProductVariationMetadata()!=null && !productVariationUpdateDto.getProductVariationMetadata().isEmpty()){
                checkProductVariationMetadataValidity(productVariation.getProduct(), productVariationUpdateDto.getProductVariationMetadata());
                JSONObject json  = new JSONObject(productVariationUpdateDto.getProductVariationMetadata());
                productVariation.setProductVariationMetadata(json);
            }
            productVariation.setProductVariationQuantity(productVariationUpdateDto.getProductVariationQuantity());
            productVariation.setProductVariationPrice(productVariationUpdateDto.getProductVariationPrice());
            productVariationRepository.save(productVariation);
            response.concat("Product Variation Details Updated.");
        }
        if (imageFile!=null){
            updateImage(path,imageFile,productVariation.getProduct().getProductId(),(long)productVariationId,"primary");
            response.concat("Product Variation Image Updated.");
        }
        if (response.isEmpty()){
            response.concat("No change in product variation details as no data is given.");
        }
        log.info("----updateProductVariation() method executed success----");
        return ResponseEntity.ok(response);
    }

    /**
     * Method to delete a product using product id
     * @param principal
     * @param productId
     * @return
     */
    @Override
    public ResponseEntity deleteProductById(Principal principal, int productId){
        log.info("----inside deleteProductById() method----");
        Product product = productRepository.findUsingProductId((long) productId);
        checkSellerProductValidity(principal,product,productId);
        productVariationRepository.updateIsActiveStatus(product.getProductId());
        productRepository.deleteUsingProductId(product.getProductId());
        log.info("----deleteProductById() method executed success----");
        return ResponseEntity.noContent().build();
    }

    /**
     * Method to get a product using product id
     * @param principal
     * @param productId
     * @return
     */
    @Override
    public ResponseEntity getProductByProductId(Principal principal, int productId){
        log.info("----inside getProductByProductId() method----");
        Product product = productRepository.findUsingProductId((long) productId);
        checkSellerProductValidity(principal,product,productId);
        ProductViewDto productViewDto = modelMapper.map(product, ProductViewDto.class);
        log.info("----getProductByProductId() method executed success----");
        return ResponseEntity.ok(productViewDto);
    }

    /**
     * Method to get a product using product id for admin
     * @param productId
     * @param path
     * @return
     */
    @Override
    public ResponseEntity getProductByProductIdForAdmin(int productId, String path){
        log.info("----inside getProductByProductIdForAdmin() method----");
        Product product = productRepository.findUsingProductId((long) productId);
        AdminProductViewDto adminProductViewDto = modelMapper.map(product, AdminProductViewDto.class);
        String fullPath = path+File.separator+"products"+File.separator+productId;
        adminProductViewDto.getProductVariationSet().iterator().forEachRemaining(adminProductVariationViewDto -> {
            adminProductVariationViewDto.setProductVariationImage(retrieveImage(fullPath+File.separator+adminProductVariationViewDto.getProductVariationId(),"primary"));
        });
        log.info("----getProductByProductIdForAdmin() method executed success----");
        return ResponseEntity.ok(adminProductViewDto);
    }

    /**
     * Method to gat a product variation using a product variation id
     * @param principal
     * @param productVariationId
     * @param path
     * @return
     */
    @Override
    public ResponseEntity getProductVariationByProductVariationId(Principal principal, int productVariationId, String path){
        log.info("----inside getProductVariationByProductVariationId() method----");
        ProductVariation productVariation = productVariationRepository.findById((long) productVariationId).orElseThrow(()->new RuntimeException("No product variation exists with id : "+productVariationId));
        if(!productVariation.getProduct().getSeller().getUserEmail().equalsIgnoreCase(principal.getName())){
            log.error("----product variation not exists for seller with given id----");
            throw new ResourceNotFoundException("No product variation exists with id : "+productVariationId);
        }
        if (productVariation.getProduct().getProductIsDeleted()){
            log.error("----product variation not exists (deleted) with given id----");
            throw new ResourceNotFoundException("No product variation exists with id : "+productVariationId);
        }
        ProductVariationViewDto productVariationViewDto = modelMapper.map(productVariation, ProductVariationViewDto.class);

        String fullPath = path+File.separator+"products"+File.separator+productVariation.getProduct().getProductId()+File.separator+productVariationId;
        ProductImage image = retrieveImage(fullPath,"primary");
        productVariationViewDto.setProductVariationImage(image);
        log.info("----getProductVariationByProductVariationId() method executed success----");
        return ResponseEntity.ok(productVariationViewDto);
    }

    /**
     * Method to get all products
     * @param principal
     * @param offset
     * @param size
     * @param sortProperty
     * @param sortDirection
     * @return
     */
    @Override
    public Page getAllProducts(Principal principal, int offset, int size, String sortProperty, String sortDirection){
        log.info("----inside getAllProducts() method----");
        Seller seller = sellerRepository.findByUserEmailIgnoreCase(principal.getName());
        Sort.Direction direct = (sortDirection.equalsIgnoreCase("ASC"))?Sort.Direction.ASC:Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(offset, size, Sort.by(new Sort.Order(direct, sortProperty)));
        log.info("----getAllProducts() method executed success----");
        return productRepository.findAllProducts(seller.getUserId(),pageable).map(product -> modelMapper.map(product, ProductViewDto.class));
    }

    /**
     * Methd to get all product variations using product id
     * @param principal
     * @param productId
     * @param offset
     * @param size
     * @param sortProperty
     * @param sortDirection
     * @param path
     * @return
     */
    @Override
    public Page getAllProductVariationsByProductId(Principal principal, int productId, int offset, int size, String sortProperty, String sortDirection, String path){
        log.info("----inside getAllProductVariationsByProductId() method----");
        Product product = productRepository.findUsingProductId((long) productId);
        checkSellerProductValidity(principal,product,productId);
        Sort.Direction direct = (sortDirection.equalsIgnoreCase("ASC"))?Sort.Direction.ASC:Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(offset, size, Sort.by(new Sort.Order(direct, sortProperty)));
        Page<ProductVariationViewDto> page = productVariationRepository.findAllUsingProductId(product.getProductId(), pageable).map(productVariation -> modelMapper.map(productVariation, ProductVariationViewDto.class));
        final String[] fullPath = {path + File.separator + "products" + File.separator + productId};
        page.iterator().forEachRemaining(productVariationViewDto -> {
            productVariationViewDto.setProductVariationImage(retrieveImage(fullPath[0]+File.separator+productVariationViewDto.getProductVariationId(),"primary"));
        });
        log.info("----getAllProductVariationsByProductId() method executed success----");
        return page;

    }

    /**
     * Method to find a product using produc id for customer
     * @param principal
     * @param productId
     * @param path
     * @return
     */
    @Override
    public ResponseEntity getProductForCustomerByProductId(Principal principal, int productId, String path){
        log.info("----inside getProductForCustomerByProductId() method----");
        Product product = productRepository.findActiveProductsUsingProductId((long) productId);
        if (product==null){
            log.error("---product not found----");
            throw new ResourceNotFoundException("No product exists with given id : "+productId);
        }
        if(product.getProductVariationSet().size()==0){
            log.error("----product variants not exists----");
            throw new RuntimeException("No product variants exists for given id : "+productId);
        }
        CustomerProductViewDto customerProductViewDto = modelMapper.map(product, CustomerProductViewDto.class);
        String fullPath = path+File.separator+"products"+File.separator+productId;
        customerProductViewDto.getProductVariationSet().iterator().forEachRemaining(customerProductVariationViewDto -> {
            customerProductVariationViewDto.setProductVariationImage(retrieveImage(fullPath+File.separator+customerProductVariationViewDto.getProductVariationId(),"primary"));
        });
        log.info("----getProductForCustomerByProductId() method executed success----");
        return ResponseEntity.ok(customerProductViewDto);
    }

    /**
     * Method to find all leaf categories for a given category
     * @param category
     * @return List of category id
     */
    @Override
    public List<Long> findLeafCategoryId(Category category){
        log.info("----inside findLeafCategoryId() method----");
        //this list will contain all leaf category id
        List<Long> leafCategoryIdList = new ArrayList<>();
        Stack<Category> stack = new Stack<>();
        stack.push(category);
        while(!stack.isEmpty()){
            Category currentCategory = stack.pop();
            //checks whether current category is leaf category or not
            //if not then push all the linked categories of current categories onto stack
            if (!currentCategory.isLeafCategory()){
                Set<Category> childCategory = currentCategory.getLinkedCategory();
                for (Category subCategory : childCategory){
                    stack.push(subCategory);
                }
            }else{
                //if current category is a leaf category then add its id to the categoryidlist
                leafCategoryIdList.add(currentCategory.getCategoryId());
            }
        }
        log.info("----findLeafCategoryId() method executed success----");
        return leafCategoryIdList;
    }

    /**
     * Method to get all products using category id if leaf node or if not then
     * products of all child nodes of that category for customers
     * @param principal
     * @param categoryId
     * @param offset
     * @param size
     * @param sortProperty
     * @param sortDirection
     * @param path
     * @return
     */
    @Override
    public Page getAllProductForCustomerByCategoryId(Principal principal, int categoryId, int offset, int size, String sortProperty, String sortDirection, String path){
        log.info("----inside getAllProductForCustomerByCategoryId() method----");
        Sort.Direction direct = (sortDirection.equalsIgnoreCase("ASC"))?Sort.Direction.ASC:Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(offset, size, Sort.by(new Sort.Order(direct, sortProperty)));
        Category category = categoryRepository.findById((long) categoryId).orElseThrow(()->new ResourceNotFoundException("No category exists with given id : "+categoryId));
        List<Long> leafCategoryIdList = findLeafCategoryId(category);
        Page<Product> page = productRepository.findAllProductsUsingCategoryIdList(leafCategoryIdList, pageable);
        if (page.isEmpty()){
            log.error("----products not found for given category id----");
            throw new ResourceNotFoundException("No products exists for given category id : "+categoryId);
        }
        String fullPath = path+File.separator+"products"+File.separator;
        page.iterator().forEachRemaining(product -> {
            product.getProductVariationSet().iterator().forEachRemaining(productVariation -> {
                productVariation.setImage(retrieveImage(fullPath+product.getProductId()+File.separator+productVariation.getProductVariationId(),"primary"));
            });
        });
        log.info("----getAllProductForCustomerByCategoryId() method executed success----");
        return page.map(product -> modelMapper.map(product, CustomerProductViewByCategoryDto.class));
    }

    /**
     * Method to get similar products based on same category using product id for customer
     * @param principal
     * @param productId
     * @param offset
     * @param size
     * @param sortProperty
     * @param sortDirection
     * @param path
     * @return
     */
    @Override
    public Page getSimilarProductForCustomerByProductId(Principal principal, int productId, int offset, int size, String sortProperty, String sortDirection, String path) {
        log.info("----inside getSimilarProductForCustomerByProductId() method----");
        Sort.Direction direct = (sortDirection.equalsIgnoreCase("ASC")) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(offset, size, Sort.by(new Sort.Order(direct, sortProperty)));
        Product product = productRepository.findActiveProductsUsingProductId((long)productId);
        if (product==null){
            log.error("----products not found for given product id----");
            throw new ResourceNotFoundException("No product exists with given id : "+productId);
        }
        Page<Product> page = productRepository.findAllProductsUsingCategoryId(product.getCategory().getCategoryId(), pageable);
        String fullPath = path+File.separator+"products"+File.separator;
        page.iterator().forEachRemaining(product1 -> {
            product1.getProductVariationSet().iterator().forEachRemaining(productVariation -> {
                productVariation.setImage(retrieveImage(fullPath+product1.getProductId()+File.separator+productVariation.getProductVariationId(),"primary"));
            });
        });
        log.info("----getSimilarProductForCustomerByProductId() method executed success----");
        return page.map(product1 -> modelMapper.map(product1, CustomerProductViewByCategoryDto.class));
    }

    /**
     * Method to get all products for admin
     * @param principal
     * @param offset
     * @param size
     * @param sortProperty
     * @param sortDirection
     * @param path
     * @return
     */
    @Override
    public Page getAllProductsForAdmin(Principal principal, int offset, int size, String sortProperty, String sortDirection, String path){
        log.info("----inside getAllProductsForAdmin() method----");
        Sort.Direction direct = (sortDirection.equalsIgnoreCase("ASC"))?Sort.Direction.ASC:Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(offset, size, Sort.by(new Sort.Order(direct, sortProperty)));
        Page<AdminProductViewDto> page = productRepository.findAllActiveProducts(pageable).map(product -> modelMapper.map(product, AdminProductViewDto.class));
        String fullPath = path+File.separator+"products"+File.separator;
        page.iterator().forEachRemaining(adminProductViewDto -> {
            adminProductViewDto.getProductVariationSet().iterator().forEachRemaining(adminProductVariationViewDto -> {
                adminProductVariationViewDto.setProductVariationImage(retrieveImage(fullPath+adminProductViewDto.getProductId()+File.separator+adminProductVariationViewDto.getProductVariationId(),"primary"));
            });
        });
        log.info("----getAllProductsForAdmin() method executed success----");
        return page;
    }

    /**
     * Method to activate a product using product id
     * @param productId
     * @return
     */
    @Override
    public ResponseEntity activateProduct(int productId){
        log.info("----inside activateProduct() method----");
        Product product = productRepository.findUsingProductId((long) productId);
        if(product==null){
            log.error("----product not found with given id----");
            throw new ResourceNotFoundException("No product exists with given id : "+productId);
        }
        if (product.getProductIsActive()){
            log.error("----product is active already----");
            throw new GenericActivationException("Product is Already Active");
        }
        product.setProductIsActive(true);
        productRepository.save(product);
        log.info("----sending email to seller regarding product activation.....");
        String subject = messageSource.getMessage("email.seller.product.activated.subject",null,locale);
        String message = messageSource.getMessage("email.salutation",null,locale)+", \n" +
                messageSource.getMessage("email.seller.product.activated.message",null,locale)+product.getProductId();

        emailService.sendEmail(emailService.sendAccountRelatedUpdateEmail(product.getSeller().getUserEmail(),message,subject));
        log.info("----activateProduct() method executed success----");
        return ResponseEntity.ok("Product is Activated");
    }

    /**
     * Method to deactivate a product using product id
     * @param productId
     * @return
     */
    @Override
    public ResponseEntity deactivateProduct(int productId){
        log.info("----inside deactivateProduct() method----");
        Product product = productRepository.findUsingProductId((long) productId);
        if(product==null){
            log.error("----product not found with given id----");
            throw new ResourceNotFoundException("No product exists with given id : "+productId);
        }
        if (!product.getProductIsActive()){
            log.error("----product is inactive already----");
            throw new GenericActivationException("Product is Already Inactive");
        }
        product.setProductIsActive(false);
        productRepository.save(product);
        log.info("----sending email to seller regarding product deactivation.....");
        String subject = messageSource.getMessage("email.seller.product.deactivated.subject",null,locale);
        String message = messageSource.getMessage("email.salutation",null,locale)+", \n" +
                messageSource.getMessage("email.seller.product.deactivated.message",null,locale)+product.getProductId();

        emailService.sendEmail(emailService.sendAccountRelatedUpdateEmail(product.getSeller().getUserEmail(),message,subject));
        log.info("----deactivateProduct() method executed success----");
        return ResponseEntity.ok("Product is Inactivated");
    }

    /**
     * Method to get all active products list
     * @return
     */
    @Override
    public List<ProductViewDto> getAllActiveProductsForAdmin(){
        log.info("----inside getAllActiveProductsForAdmin() method----");
        return productRepository.findActiveProducts().stream().map(product -> modelMapper.map(product, ProductViewDto.class)).toList();
    }

    /**
     * Method to get all inactiive products list
     * @return
     */
    @Override
    public List<ProductViewDto> getAllInactiveProductsForAdmin(){
        log.info("----inside getAllInactiveProductsForAdmin() method----");
        return productRepository.findInactiveProducts().stream().map(product -> modelMapper.map(product, ProductViewDto.class)).toList();
    }

    /**
     * Method to upload/update product variation image
     * @param path
     * @param imageFile
     * @param productId
     * @param productVariationId
     * @param type
     */
    @Override
    public void updateImage(String path, MultipartFile imageFile, Long productId, Long productVariationId, String type){
        log.info("----inside updateImage() method----");
        String filePath = path+File.separator+"products"+File.separator+productId+File.separator+productVariationId;
        try{
            imageService.uploadImage(filePath, imageFile, type);
        }catch (Exception e){
            log.error("----image upload error----");
            throw new RuntimeException(e.getMessage());
        }
        log.info("----updateImage() method executed success----");
    }

    /**
     * Method to retrieve product variation image
     * @param path
     * @param type
     * @return
     */
    @Override
    public ProductImage retrieveImage(String path, String type){
        log.info("----inside retrieveImage() method----");
        String imageName = imageService.searchImage(path, type);
        ProductImage image = new ProductImage(path+File.separator+imageName);
        if (imageName!=null) {
            log.info("----image returned . retrieveImage() method executed success----");
            return image;
        }
        log.info("----no image found . retrieveImage() method executed success----");
        return null;
    }
}

