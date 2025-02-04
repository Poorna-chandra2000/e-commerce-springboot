package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.payload.ProductDto;
import com.ecommerce.project.payload.ProductResponseDto;
import com.ecommerce.project.repositories.CategoryRepository;
import com.ecommerce.project.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;




@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;
    private final FileService fileService;

    @Value("${project.image}")//import from annotions
    String path;


    @Override
    public ProductDto addProduct(ProductDto productDto, Long categoryId) {
       Category category=categoryRepository.findById(categoryId)
               .orElseThrow(()->new ResourceNotFoundException("Category","categoryId",categoryId));
       Product product=modelMapper.map(productDto,Product.class);

       product.setCategory(category);//must do it manually

      double specialPrice= product.getPrice()-(product.getDiscount() * 0.01)*product.getPrice();
      product.setSpecialPrice(specialPrice);//calc and set seperately
      Product saveproduct=productRepository.save(product);

      ProductDto productsavedDto=modelMapper.map(saveproduct,ProductDto.class);


      return productsavedDto;
    }
//productdto
@Override
public List<ProductDto> getAllProductsDtos() {
    List<Product> product=productRepository.findAll();
    return  product
            .stream()
            .map(prod->modelMapper.map(prod,ProductDto.class))
            .collect(Collectors.toList());
}

    @Override
    public ProductResponseDto getById(Long productId) {
        Optional<Product> product=productRepository.findById(productId);
        //for sending to productResponseDto you neeed to send list
        //through you get single just store it in a list and send it to productresponse
        List<ProductDto> productDto=  product.stream()
                .map(prod->modelMapper.map(prod,ProductDto.class))
                .collect(Collectors.toList());
        ProductResponseDto productResponseDto= ProductResponseDto.builder()
                .content((List<ProductDto>) productDto).build();

        return productResponseDto;
    }

    @Override
    public Optional<ProductDto> getByIdproductDto(Long productId) {
        Optional<Product> product=productRepository.findById(productId);
        return product.map(prod->modelMapper.map(prod,ProductDto.class));
    }



    //productresponseDto
    @Override
    public ProductResponseDto getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        //implementing pagination using Pageable class and Sort class
        Sort sortbyAndOrder=sortOrder.equalsIgnoreCase("asc")
                ?Sort.by(sortBy).ascending():Sort.by(sortBy).descending();

        //now just add the above values to Pagebale instance
        //the order is same for page  request
        Pageable pageDetails= PageRequest.of(pageNumber,pageSize,sortbyAndOrder);

        Page<Product> product=productRepository.findAll(pageDetails);


        //conver to Dto and send it to ProductResponse
        List<ProductDto> productDtos=product
                .stream()
                .map(prod->modelMapper.map(prod,ProductDto.class))
                .collect(Collectors.toList());

     ProductResponseDto productResponseDto= ProductResponseDto
             .builder()
             .content(productDtos)//very important
             .pageNumber(product.getNumber())
             .pageSize(product.getSize())
             .totalElements(product.getTotalElements())
             .totalPages(product.getTotalPages())
             .lastpage(product.isLast())
             .build();
        return productResponseDto;
    }

    @Override
    public ProductResponseDto getProductsByCategory(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Optional<Category> category=categoryRepository.findById(categoryId);
    //if you want bidirectional mapping

        Sort sortbyAndOrder=sortOrder.equalsIgnoreCase("asc")
                ?Sort.by(sortBy).ascending():Sort.by(sortBy).descending();


        Pageable pageDetails= PageRequest.of(pageNumber,pageSize,sortbyAndOrder);
        //just instead of get page make t return page
        Page<Product> products=  productRepository.findByCategoryOrderByPriceAsc(category,pageDetails);//you can remove Orderby if you want which is Jpa query

        List<ProductDto> productDtos=products.stream().map(product -> modelMapper.map(product,ProductDto.class)).collect(Collectors.toList());
        //if you want bidirectional mapping

        ProductResponseDto productResponseDto= ProductResponseDto
                .builder()
                .content(productDtos)
                .pageNumber(products.getNumber())
                .totalPages(products.getTotalPages())
                .totalElements(products.getTotalElements())
                .pageSize(products.getSize())
                .lastpage(products.isLast())
                .build();


        return productResponseDto;
    }

    @Override
    public ProductResponseDto getProductsByCategoryName(String categoryName, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        //pagination and sorting
        Sort sortbyAndOrder=sortOrder.equalsIgnoreCase("asc")
                ?Sort.by(sortBy).ascending():Sort.by(sortBy).descending();


        Pageable pageDetails= PageRequest.of(pageNumber,pageSize,sortbyAndOrder);



        Category category = categoryRepository.findByCategoryNameContaining(categoryName);
        if (category == null) {
            throw new ResourceNotFoundException("Category", "categoryName", categoryName);
        }


        // Find products by category
        Page<Product> products =  productRepository.findByCategory(Optional.of(category),pageDetails);

        // Convert products to ProductDto list
        List<ProductDto> productDtos = products.stream()
                .map(product -> modelMapper.map(product, ProductDto.class))
                .collect(Collectors.toList());


        ProductResponseDto productResponseDto= ProductResponseDto
                .builder()
                .content(productDtos)
                .pageNumber(products.getNumber())
                .totalPages(products.getTotalPages())
                .totalElements(products.getTotalElements())
                .pageSize(products.getSize())
                .lastpage(products.isLast())
                .build();

        return productResponseDto;
    }

    @Override
    public ProductResponseDto getProductsByKeyword(String keyword, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        //pagination and sorting
        Sort sortbyAndOrder=sortOrder.equalsIgnoreCase("asc")
                ?Sort.by(sortBy).ascending():Sort.by(sortBy).descending();


        Pageable pageDetails= PageRequest.of(pageNumber,pageSize,sortbyAndOrder);
        Page<Product> products=  productRepository.findByProductNameContainingIgnoreCase(keyword,pageDetails);
        //convert all to dtos and send it to porductresponse
        List<ProductDto> productDtos=products.stream()
                .map(prod->modelMapper.map(prod,ProductDto.class))
                .collect(Collectors.toList());

        ProductResponseDto productResponseDto= ProductResponseDto
                .builder()
                .content(productDtos)
                .pageNumber(products.getNumber())
                .totalPages(products.getTotalPages())
                .totalElements(products.getTotalElements())
                .pageSize(products.getSize())
                .lastpage(products.isLast())
                .build();
        return productResponseDto;
    }

    @Override
    public ProductDto updateproductByid(ProductDto productDto, Long productId) {
        //in update mapping we need to update all fields
        //better thn update is Patch mapping
        //because you can update an field and changes will be found only in that field
        Optional<Product> productbyid=productRepository.findById(productId);
        if (!productbyid.isPresent()) {
            throw new ResourceNotFoundException("Product", "productid", productId);
        }
        //make sure you fetch category and update it gain
        //because when you update any field all the other fields get updated
        Category category=productbyid.get().getCategory();

        //now convert updated dto entity
        Product product=modelMapper.map(productDto,Product.class);
        //now set its id it will set everything
        product.setProductId(productId);
        product.setCategory(category);

        Product saved=productRepository.save(product);
        ProductDto productDtos=modelMapper.map(saved,ProductDto.class);

        return productDto;
    }

    @Override
    public String deleteProduct(Long productId) {
        if (productRepository.existsById(productId)) {
            productRepository.deleteById(productId);
            return "Product with ID " + productId + " deleted SUCCESSFULLY";
        }
        return "Product with ID " + productId + " does not exist";
    }

    @Value("${project.image}")
    private String imagePath;


    @Override
    public ProductDto updateProductImage(Long productId, MultipartFile image) throws IOException {
        Product productFromDb = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        // Upload the image
        String fileName = fileService.uploadImage(imagePath, image);

        // Create the URL for the uploaded image
        String imageUrl = "http://localhost:8080/images/" + fileName;

        // Update the product with the new image URL
        productFromDb.setImage(imageUrl);

        Product updatedProduct = productRepository.save(productFromDb);

        return modelMapper.map(updatedProduct, ProductDto.class);
    }


//    //updating product images
//    @Override
//    public ProductDto updateProductImage(Long productId, MultipartFile image) throws IOException {
//        //get the product from DB
//        Product productFromeDb=productRepository.findById(productId)
//                .orElseThrow(()->new ResourceNotFoundException("Product","productId",productId));
//        //upload image to server
//        //get the file name of upladed image
////        String path="images/";//remove this and add in application properties
//        String fileName=fileService.uploadImage(path,image);
//        //Updating the new file name to the product
//        productFromeDb.setImage(fileName);
//        Product updatedProduct=productRepository.save(productFromeDb);
//        //return Dto after mapping product to DTO
//        return modelMapper.map(updatedProduct,ProductDto.class);
//    }

//just for modularity keep this method in seprate Service i.e file Service
    //and dependency inject file service in this productserviceimpl
    //as you can see this service is reuable
    //image is just
//    private String uploadImage(String path, MultipartFile file) throws IOException {//file can be replsced as image also
//        //get file names of currrent or original file
//        String originalFileName=file.getOriginalFilename();
//        //Generate a unique file name we random uui
//        String randomId= UUID.randomUUID().toString();
//        //example:mat.jpg --> 1234 --> 1234.jpg
//        String fileName=randomId.concat(originalFileName.substring(originalFileName.lastIndexOf('.')));//this preserves original extension
//        String filePath=path + File.separator + fileName;
//
//        //check if path exist and create
//        File folder=new File(path);
//        if(!folder.exists()) folder.mkdir();
//
//        //upload to server
//        Files.copy(file.getInputStream(), Paths.get(filePath));
//        //returing file name
//        return fileName;
//    }


}
