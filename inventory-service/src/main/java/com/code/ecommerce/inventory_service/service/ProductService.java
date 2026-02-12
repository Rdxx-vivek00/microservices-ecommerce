package com.code.ecommerce.inventory_service.service;

import com.code.ecommerce.inventory_service.dto.OrderRequestDto;
import com.code.ecommerce.inventory_service.dto.OrderRequestItemDto;
import com.code.ecommerce.inventory_service.dto.ProductDto;
import com.code.ecommerce.inventory_service.entity.Product;
import com.code.ecommerce.inventory_service.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;

    public List<ProductDto> getAllInventory()
    {
        log.info("fetching all inventory items: ");
        List<Product> inventories=productRepository.findAll();

         return inventories.stream()
                .map(product -> modelMapper.map(product, ProductDto.class))
                .collect(Collectors.toList());

    }

    public ProductDto getProductById(Long id)
    {
        log.info("fetching product with id {}",id);
        Product product=productRepository
                .findById(id)
                .orElseThrow(()->new NoSuchElementException("product with id not found"));

        return modelMapper.map(product, ProductDto.class);
    }

    @Transactional
    public Double reduceStocks(OrderRequestDto orderRequestDto) {

        log.info("reducing the stocks");
        Double totalPrice=0.0;
        for (OrderRequestItemDto orderRequestItemDto:orderRequestDto.getItems())
        {
            Long productId= orderRequestItemDto.getProductId();
            Integer quantity=orderRequestItemDto.getQuantity();

          Product product=productRepository.findById(productId).orElseThrow(()->new RuntimeException("product not found with id"+productId));

           if(product.getStock()<quantity)
           {
               throw new RuntimeException("product cannot be fulfilled for the given quantity:");
           }

           product.setStock(product.getStock()-quantity);
           productRepository.save(product);
           totalPrice+=quantity*product.getPrice();


        }
        return totalPrice;
    }
}
