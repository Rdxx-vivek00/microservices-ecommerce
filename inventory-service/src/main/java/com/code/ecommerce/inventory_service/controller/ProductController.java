package com.code.ecommerce.inventory_service.controller;

import com.code.ecommerce.inventory_service.clients.OrdersFeignClient;
import com.code.ecommerce.inventory_service.dto.OrderRequestDto;
import com.code.ecommerce.inventory_service.dto.ProductDto;
import com.code.ecommerce.inventory_service.repository.ProductRepository;
import com.code.ecommerce.inventory_service.service.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {

  private final ProductService productService;
  private final DiscoveryClient discoveryClient;
  private final RestClient restClient;

  private final OrdersFeignClient ordersFeignClient;

  @GetMapping("/fetchOrders")
  public String fetchFromOrdersService(HttpServletRequest httpServletRequest)
  {


      ServiceInstance orderService=discoveryClient.getInstances("order-service").getFirst();

//     return restClient.get()
//              .uri(orderService.getUri()+"/orders/core/helloOrders")
//              .retrieve()
//              .body(String.class);

      return ordersFeignClient.helloOrders();
  }

  @GetMapping
    private ResponseEntity<List<ProductDto>> getAllInventory()
  {
      List<ProductDto> inventories=productService.getAllInventory();
      return ResponseEntity.ok(inventories);
  }

  @PutMapping("reduce-stocks")
    public ResponseEntity<Double> reduceStocks(@RequestBody OrderRequestDto orderRequestDto)
  {
      Double totalPrice=productService.reduceStocks(orderRequestDto);
      return ResponseEntity.ok(totalPrice);
  }
}
