package com.ayushjainttn.bootcampproject.ecommerce.service.impl;

import com.ayushjainttn.bootcampproject.ecommerce.dto.category.*;
import com.ayushjainttn.bootcampproject.ecommerce.entity.*;
import com.ayushjainttn.bootcampproject.ecommerce.exceptions.ResourceNotFoundException;
import com.ayushjainttn.bootcampproject.ecommerce.repository.CategoryMetadataFieldRepository;
import com.ayushjainttn.bootcampproject.ecommerce.repository.CategoryMetadataFieldValueRepository;
import com.ayushjainttn.bootcampproject.ecommerce.repository.CategoryRepository;
import com.ayushjainttn.bootcampproject.ecommerce.repository.ProductRepository;
import com.ayushjainttn.bootcampproject.ecommerce.service.CategoryService;
import com.ayushjainttn.bootcampproject.ecommerce.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private CategoryMetadataFieldRepository categoryMetadataFieldRepository;
    @Autowired
    private CategoryMetadataFieldValueRepository categoryMetadataFieldValueRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductRepository productRepository;

    /**
     * Method to check weather the category to be added or updated is unique within the SubTree i.e. ParentCategories and ChildCategories using DFS algorithm.
     * @param newCategoryName
     * @param parentCategory
     */
    @Override
    public void isCategoryUniqueInSubTree(String newCategoryName, Category parentCategory){
        //this iss to iterate the subtree and find out newcategory uniqueness
        log.info("----inside isCategoryUniqueInSubTree() method----");
        Set<Category> alreadyVisitedCategory = new HashSet<>();
        Stack<Category> categoryStack = new Stack<>();
        categoryStack.push(parentCategory);
        while (!categoryStack.isEmpty()){
            Category currentCategory = categoryStack.pop();
            //finding weather the currentCategory has been already checked or not, if not then mark as checked(i.e. visited)
            if(!alreadyVisitedCategory.contains(currentCategory)){
                alreadyVisitedCategory.add(currentCategory);
                if(currentCategory.getCategoryName().equalsIgnoreCase(newCategoryName)){
                    log.error("----newCategory to be added/updated already exists----");
                    throw new RuntimeException("Category Already exists with given name : "+newCategoryName);
                }
            }
            Set<Category> linkedCategory = parentCategory.getLinkedCategory();
            for(Category category: linkedCategory){
                if (!alreadyVisitedCategory.contains(category)) categoryStack.push(category);
            }
        }
        log.info("----newCategory is unique. method executed success----");
    }

    /**
     * Method to check whether new category is unique in its branch i.e. from root till leaf
     * @param newCategoryName
     * @param parentCategory
     */
    @Override
    public void isCategoryUniqueFromRootToLeaf(String newCategoryName, Category parentCategory){
        //checking whether newcategory is unique in its branch i.e. from root till leaf
        log.info("----inside isCategoryUniqueFromRootToLeaf() method----");
        Stack<Category> categoryStack = new Stack<>();
        categoryStack.push(parentCategory);
        while (!categoryStack.isEmpty()){
            Category currentCategory = categoryStack.pop();
            if (currentCategory.getCategoryName().equalsIgnoreCase(newCategoryName)){
                log.error("----newCategory to be added/updated already exists----");
                throw new RuntimeException("Category Already exists with given name : "+newCategoryName);
            }
            if (!currentCategory.isRootCategory()){
                categoryStack.push(currentCategory.getParentCategoryId());
            }
        }
        log.info("----newCategory is unique. method executed success----");
    }
    /**
     * Method to check whether the category to be added or updated is unique at the root level
     * @param newCategoryName
     */
    @Override
    public void isCategoryUniqueInRoot(String newCategoryName){
        //checking weather category is unique or not at the root level
        log.info("----inside isCategoryUniqueInRoot() method----");
        List<Category> categories = categoryRepository.findByParentCategoryId(null);
        if(categories.stream().anyMatch(category -> category.getCategoryName().equalsIgnoreCase(newCategoryName))){
            log.error("----newCategory to be added/updated already exists----");
            throw new RuntimeException("Category already exists with name : "+newCategoryName);
        }
        log.info("----newCategory is unique. method executed success----");
    }

    /**
     * Method to add a new category
     * @param categoryAddDto
     * @return
     */
    @Override
    public ResponseEntity addCategory(CategoryAddDto categoryAddDto){
        log.info("----Inside addCategory() method----");
        Category newCategory = new Category();
        newCategory.setCategoryName(categoryAddDto.getCategoryName());
        isCategoryUniqueInRoot(newCategory.getCategoryName());
        if(categoryAddDto.getParentCategoryId()!=null){
            Category parentCategory = categoryRepository.findById(categoryAddDto.getParentCategoryId()).orElseThrow(()->new ResourceNotFoundException("No parent category exists with given id : "+categoryAddDto.getParentCategoryId()));
            if(!parentCategory.getCategoryMetadataFieldValues().isEmpty()){
                log.error("----Category cannot be added parent category already has metadata field values associated with it----");
                throw new RuntimeException("Not allowed as parent category already has metadata field values associated with it");
            }
            isCategoryUniqueInSubTree(newCategory.getCategoryName(), parentCategory);
            isCategoryUniqueFromRootToLeaf(newCategory.getCategoryName(), parentCategory);
            parentCategory.addLinkedCategories(newCategory);
        }
        categoryRepository.save(newCategory);
        log.info("----newCategory is added. method executed success----");
        return new ResponseEntity<String>("Category Added Success with id : "+newCategory.getCategoryId(),null, HttpStatus.CREATED);
    }

    /**
     * Method to update already existing category
     * @param id
     * @param categoryUpdateDto
     * @return
     */
    @Override
    public ResponseEntity updateCategory(int id, CategoryUpdateDto categoryUpdateDto){
        log.info("----inside updateCategory() method----");
        Category existingCategory = categoryRepository.findById((long)id).orElseThrow(()->new ResourceNotFoundException("No category exists with given id : "+id));
        isCategoryUniqueInRoot(categoryUpdateDto.getCategoryName());
        isCategoryUniqueInSubTree(categoryUpdateDto.getCategoryName(), existingCategory);
        isCategoryUniqueFromRootToLeaf(categoryUpdateDto.getCategoryName(), existingCategory);
        existingCategory.setCategoryName(categoryUpdateDto.getCategoryName());
        categoryRepository.save(existingCategory);
        log.info("----Category is updated. method executed success----");
        return new ResponseEntity<String>("Category Updated Success",null,HttpStatus.OK);
    }

    /**
     * Method to add category metadata field
     * @param categoryMetadataFieldAddDto
     * @return
     */
    @Override
    public ResponseEntity addCategoryMetadataField(CategoryMetadataFieldAddDto categoryMetadataFieldAddDto){
        log.info("----inside addCategoryMetadataField() method----");
        CategoryMetadataField categoryMetadataField = new CategoryMetadataField();
        categoryMetadataField.setCategoryMetadataFieldName(categoryMetadataFieldAddDto.getCategoryMetadataFieldName());
        if (categoryMetadataFieldRepository.findByCategoryMetadataFieldNameIgnoreCase(categoryMetadataField.getCategoryMetadataFieldName())!=null){
            log.error("----Category Metadata Field with given name already exists----");
            throw new RuntimeException("Category metadata field already exists with given name");
        }
        categoryMetadataFieldRepository.save(categoryMetadataField);
        log.info("----Category metadata field added. method executed success----");
        return new ResponseEntity<String>("Category Metadata Field Added Success with id : "+categoryMetadataField.getCategoryMetadataFieldId(),null, HttpStatus.CREATED);
    }

    /**
     * Method to add category metadata field values
     * @param categoryMetadataFieldValuesAddDto
     * @return
     */
    @Override
    public ResponseEntity addCategoryMetadataFieldValues(CategoryMetadataFieldValuesAddDto categoryMetadataFieldValuesAddDto){
        log.info("----inside addCategoryMetadataFieldValues() method----");

        Category category = categoryRepository.findById(categoryMetadataFieldValuesAddDto.getCategoryId()).orElseThrow(()->new RuntimeException("Not category exists with id : "+categoryMetadataFieldValuesAddDto.getCategoryId()));
        log.info("---checking category for which metadata field value to be added is leaf category or not----");

        if (category.isLeafCategory()){
            log.info("---category for which metadata field value to be added is leaf category----");

            CategoryMetadataFieldValueId categoryMetadataFieldValueId = new CategoryMetadataFieldValueId(categoryMetadataFieldValuesAddDto.getCategoryId(), categoryMetadataFieldValuesAddDto.getCategoryMetadataFieldId());
            log.info("---checking category for which metadata field value to be added already has a existing value or not----");

            if(categoryMetadataFieldValueRepository.findById(categoryMetadataFieldValueId).isEmpty()){
                log.info("---category for which metadata field value to be added doesn't have existing value----");
                CategoryMetadataField categoryMetadataField = categoryMetadataFieldRepository.findById(categoryMetadataFieldValuesAddDto.getCategoryMetadataFieldId()).orElseThrow(()->new RuntimeException("Not metadata field exists with id : "+categoryMetadataFieldValuesAddDto.getCategoryMetadataFieldValues()));

                //converting user input string values to uppercase and putting them in to keep uniqueness in values
                String values = categoryMetadataFieldValuesAddDto.getCategoryMetadataFieldValues();
                Set<String> set = Stream.of(values.trim().toUpperCase().split("\\s*,\\s*")).collect(Collectors.toSet());
                String finalValues = String.join(",", set);

                CategoryMetadataFieldValue categoryMetadataFieldValue = new CategoryMetadataFieldValue(category, categoryMetadataField, finalValues);
                category.addCategoryMetadataFieldValues(categoryMetadataFieldValue);
                categoryMetadataField.addCategoryMetadataFieldValues(categoryMetadataFieldValue);
                categoryMetadataFieldValueRepository.save(categoryMetadataFieldValue);
                log.info("---metadata field value has been added. method executed success----");
                return new ResponseEntity<String>("Category Metadata Field Values Added Success",null,HttpStatus.CREATED);
            }
            log.info("---Values for given Category ID and Metadata Field ID already exists----");
            throw new RuntimeException("Values for given Category ID and Metadata Field ID already exists");//else throws data for given id already exists
        }//else throws not a leaf category
        log.info("---Not Allowed to add Values for given category id as it is not leaf category----");
        throw new RuntimeException("Not Allowed to add Values for given category id");
    }

    /**
     * Method to update already existing values for category metadata field
     * @param categoryMetadataFieldValuesAddDto
     * @return
     */
    @Override
    public ResponseEntity updateCategoryMetadataFieldValues(CategoryMetadataFieldValuesAddDto categoryMetadataFieldValuesAddDto){
        log.info("---inside updateCategoryMetadataFieldValues() method----");

        Category category = categoryRepository.findById(categoryMetadataFieldValuesAddDto.getCategoryId()).orElseThrow(()->new RuntimeException("Not category exists with id : "+categoryMetadataFieldValuesAddDto.getCategoryId()));
        CategoryMetadataField categoryMetadataField = categoryMetadataFieldRepository.findById(categoryMetadataFieldValuesAddDto.getCategoryMetadataFieldId()).orElseThrow(()->new RuntimeException("Not metadata field exists with id : "+categoryMetadataFieldValuesAddDto.getCategoryMetadataFieldValues()));
        CategoryMetadataFieldValueId categoryMetadataFieldValueId = new CategoryMetadataFieldValueId(category.getCategoryId(), categoryMetadataField.getCategoryMetadataFieldId());
        CategoryMetadataFieldValue categoryMetadataFieldValue = categoryMetadataFieldValueRepository.findById(categoryMetadataFieldValueId).orElseThrow(()->new ResourceNotFoundException("No metadata filed values exists with the given category id and metadat-field id"));
        //fetching string values from database and converting to set
        String values = categoryMetadataFieldValue.getCategoryMetadataFieldValues();
        Set<String> set = Stream.of(values.trim().toUpperCase().split("\\s*,\\s*")).collect(Collectors.toSet());

        //fetching string values from input dto and converting to set
        String valuesDto = categoryMetadataFieldValuesAddDto.getCategoryMetadataFieldValues();
        Set<String> setDto = Stream.of(valuesDto.trim().toUpperCase().split("\\s*,\\s*")).collect(Collectors.toSet());

        //merging above two sets
        set.addAll(setDto);
        //converting above merged set to string values again
        String finalValues = String.join(",", set);

        categoryMetadataFieldValue.setCategoryMetadataFieldValues(finalValues);
        categoryMetadataFieldValueRepository.save(categoryMetadataFieldValue);
        log.info("---Category metadata field value updated. method executed success----");
        return new ResponseEntity<String>("Category Metadata field Values updated Success",null,HttpStatus.OK);
    }

    /**
     * Method to return a page wise result for all category metadata fields available
     * @param pageOffset
     * @param pageSize
     * @param sortProperty
     * @param sortDirection
     * @return
     */
    @Override
    public Page getAllCategoryMetadataFields(int pageOffset, int pageSize, String sortProperty, String sortDirection){
        log.info("---inside getAllCategoryMetadataFields() method----");
        Sort.Direction direct = (sortDirection.equalsIgnoreCase("ASC"))?Sort.Direction.ASC:Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(pageOffset,pageSize, Sort.by(new Sort.Order(direct, sortProperty)));
        log.info("---method executed success----");
        return categoryMetadataFieldRepository.findAll(pageable).map(categoryMetadataField -> modelMapper.map(categoryMetadataField, CategoryMetadataFieldViewDto.class));
    }

    /**
     * Method to return a page wise result for getting all categories available
     * @param pageOffset
     * @param pageSize
     * @param sortProperty
     * @param sortDirection
     * @return
     */
    @Override
    public Page getAllCategories(int pageOffset, int pageSize, String sortProperty, String sortDirection){
        log.info("---inside getAllCategories() method----");
        Sort.Direction direct = (sortDirection.equalsIgnoreCase("ASC"))?Sort.Direction.ASC:Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(pageOffset,pageSize, Sort.by(new Sort.Order(direct, sortProperty)));
        log.info("---all categories fetched. method executed success----");
        return categoryRepository.findAll(pageable).map(category -> modelMapper.map(category, CategoryViewDto.class));

    }

    /**
     * Method to get category using category id given
     * @param categoryId
     * @return
     */
    @Override
    public ResponseEntity getCategoryUsingId(int categoryId){
        log.info("---inside getCategoryUsingId() method----");
        Category category = categoryRepository.findById((long) categoryId).orElseThrow(()->new ResourceNotFoundException("No category exists with given id : "+categoryId));
        CategoryViewDto categoryViewDto = modelMapper.map(category, CategoryViewDto.class);
        log.info("---category with given id fetched. method executed success----");
        return new ResponseEntity<CategoryViewDto>(categoryViewDto,HttpStatus.OK);
    }

    /**
     * Method to get all leaf categories for seller
     * @return
     */
    @Override
    public ResponseEntity getAllCategoriesForSeller(){
        log.info("---inside getAllCategoriesForSeller() method----");
        List list = categoryRepository.findAll().stream().filter(category -> category.isLeafCategory()).map(category -> modelMapper.map(category, CategoryViewDto.class)).toList();
        log.info("---all categories fetched. method executed success----");
        return new ResponseEntity<List>(list,HttpStatus.OK);
    }


    /**
     * Method to return all root categories for customer if no id is given
     * @return
     */
    @Override
    public ResponseEntity getCategoriesForCustomer(){
        log.info("---inside getCategoriesForCustomer() method----");
        List list = categoryRepository.findByParentCategoryId(null).stream().map(category -> modelMapper.map(category, CategoryViewForCustomerDto.class)).toList();
        log.info("---all categories fetched. method executed success----");
        return new ResponseEntity<List>(list,HttpStatus.OK);
    }

    /**
     * Method to return all child categories of given category id for customer
     * @param categoryId
     * @return
     */
    @Override
    public ResponseEntity getCategoriesForCustomerUsingId(int categoryId){
        log.info("---inside getCategoriesForCustomerUsingId() method----");
        Category category = categoryRepository.findById((long) categoryId).orElseThrow(()->new ResourceNotFoundException("No category exists with given id : "+categoryId));
        List list = category.getLinkedCategory().stream().map(category1 -> modelMapper.map(category1, CategoryViewForCustomerDto.class)).toList();
        log.info("---category with given id fetched. method executed success----");
        return new ResponseEntity<List>(list,HttpStatus.OK);
    }

    /**
     * Method to return filters for a given category Id
     * @param categoryId
     * @return
     */
    @Override
    public ResponseEntity getFilterByCategoryId(int categoryId){
        log.info("----inside getFilterByCategoryId() method----");
        Category category = categoryRepository.findById((long) categoryId).orElseThrow(()->new ResourceNotFoundException("No category found with given id : "+categoryId));
        List<CategoryMetadataFieldValueViewDto> metadataFieldValuesList = category.getCategoryMetadataFieldValues().stream().map(categoryMetadataFieldValue -> modelMapper.map(categoryMetadataFieldValue, CategoryMetadataFieldValueViewDto.class)).toList();
        List<Long> leafCategoryIdList = productService.findLeafCategoryId(category);
        List<Object[]> fetchedResult = productRepository.findProductBrandsOfCategory(leafCategoryIdList);
        Set<String> brandList = new HashSet<>();
        List<Long> productIdList = new ArrayList<>();
        for (Object[] obj : fetchedResult){
            brandList.add(obj[0].toString());
            productIdList.add(((BigInteger)obj[1]).longValue());
        }
        List<Object[]> fetchedPrice = productRepository.findMinMaxPrice(productIdList);
        Double minPrice = (Double) fetchedPrice.get(0)[0];
        Double maxPrice = (Double) fetchedPrice.get(0)[1];

        CategoryFilterViewDto categoryFilterViewDto = new CategoryFilterViewDto();
        categoryFilterViewDto.setBrands(brandList);
        categoryFilterViewDto.setMetadata(metadataFieldValuesList);
        categoryFilterViewDto.setMinimumPrice(minPrice);
        categoryFilterViewDto.setMaximumPrice(maxPrice);
        log.info("---filters for the given category id fetched. method executed success----");
        return ResponseEntity.ok(categoryFilterViewDto);
    }
}
