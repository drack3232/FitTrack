package com.fittrack_app.fittrack.modules.producte.controller;

import com.fittrack_app.fittrack.modules.producte.dto.ProductCreateDto;
import com.fittrack_app.fittrack.modules.producte.dto.ProductResponseDto;
import com.fittrack_app.fittrack.modules.producte.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;

@RequestMapping
    public ResponseEntity<ProductResponseDto> createProduct(@Valid @RequestBody ProductCreateDto productCreateDto){
ProductResponseDto responseDto = productService.createProduct(productCreateDto);
return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductResponseDto>> serchProducts(@RequestBody String query){
      List <ProductResponseDto> response = productService.searchProduct(query);
        return ResponseEntity.ok(response);


    }
@GetMapping("/{id}")
    public ResponseEntity<ProductResponseDto> getProductById(@RequestBody Long id){
    ProductResponseDto response = productService.getProductById(id);
  return  ResponseEntity.ok(response);
    }

}
