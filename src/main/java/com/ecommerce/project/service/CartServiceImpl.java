package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.APIException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Cart;
import com.ecommerce.project.model.CartItem;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.payload.CartDTO;
import com.ecommerce.project.payload.ProductDto;
import com.ecommerce.project.repositories.CartItemRepository;
import com.ecommerce.project.repositories.CartRepository;
import com.ecommerce.project.repositories.ProductRepository;
import com.ecommerce.project.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
   private final ModelMapper modelMapper;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    private final AuthUtil authUtil;

    @Override
    public CartDTO addProductToCart(Long productId, Integer quantity) {
        Cart cart  = createCart();

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cart.getCartId(), productId);

        if (cartItem != null) {
            throw new APIException("Product " + product.getProductName() + " already exists in the cart");
        }

        if (product.getQuantity() == 0) {
            throw new APIException(product.getProductName() + " is not available");
        }

        if (product.getQuantity() < quantity) {
            throw new APIException("Please, make an order of the " + product.getProductName()
                    + " less than or equal to the quantity " + product.getQuantity() + ".");
        }

        CartItem newCartItem = new CartItem();

        newCartItem.setProduct(product);
        newCartItem.setCart(cart);
        newCartItem.setQuantity(quantity);
        newCartItem.setDiscount(product.getDiscount());
        newCartItem.setProductPrice(product.getSpecialPrice());

        cartItemRepository.save(newCartItem);

        product.setQuantity(product.getQuantity());

        cart.setTotalPrice(cart.getTotalPrice() + (product.getSpecialPrice() * quantity));

        cartRepository.save(cart);

        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

        List<CartItem> cartItems = cart.getCartItems();

        Stream<ProductDto> productStream = cartItems.stream().map(item -> {
            ProductDto map = modelMapper.map(item.getProduct(), ProductDto.class);
            map.setQuantity(item.getQuantity());
            return map;
        });

        cartDTO.setProducts(productStream.toList());

        return cartDTO;
    }

    private Cart createCart(){
        Cart userCart=cartRepository.findCartByEmail(authUtil.loggedInEmail());
             if(userCart!=null){
                 return userCart;
             }
             Cart cart=new Cart();
             cart.setTotalPrice(0.0);
             cart.setUser(authUtil.loggedInUser());
             Cart newCart=cartRepository.save(cart);

        return newCart;
    }


    //updating quantity after adding to cart create a controller for this donot forget
    @Override
    public CartDTO updateCartItemQuantity(Long productId, Integer newQuantity) {
        Cart cart = createCart();

        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cart.getCartId(), productId);

        if (cartItem == null) {
            throw new ResourceNotFoundException("CartItem", "productId", productId);
        }

        Product product = cartItem.getProduct();

        if (newQuantity <= 0) {
            throw new APIException("Quantity must be greater than zero");
        }

        if (product.getQuantity() < newQuantity) {
            throw new APIException("Only " + product.getQuantity() + " items available in stock.");
        }

        // Adjust total price
        cart.setTotalPrice(cart.getTotalPrice() - (cartItem.getProductPrice() * cartItem.getQuantity())); // Subtract old price
        cartItem.setQuantity(newQuantity);
        cart.setTotalPrice(cart.getTotalPrice() + (cartItem.getProductPrice() * newQuantity)); // Add new price

        cartItemRepository.save(cartItem);
        cartRepository.save(cart);

        return getCartDetails();
    }

    @Override
    public CartDTO getCartDetails() {
        Cart cart = createCart();

        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

        List<CartItem> cartItems = cart.getCartItems();

        Stream<ProductDto> productStream = cartItems.stream().map(item -> {
            ProductDto map = modelMapper.map(item.getProduct(), ProductDto.class);
            map.setQuantity(item.getQuantity());
            return map;
        });

        cartDTO.setProducts(productStream.toList());

        return cartDTO;
    }

}
