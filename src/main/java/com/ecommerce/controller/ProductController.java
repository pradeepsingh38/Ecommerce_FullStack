package com.ecommerce.controller;

import com.ecommerce.dto.ProductRequest;
import com.ecommerce.dto.ProductResponse;
import com.ecommerce.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "http://localhost:5173")
public class ProductController {

	@Autowired
	private ProductService productService;

	// POST /api/products — add a product (protected, needs JWT)
	@PostMapping
	public ResponseEntity<ProductResponse> addProduct(@Valid @RequestBody ProductRequest request) {
		return ResponseEntity.ok(productService.addProduct(request));
	}

	// GET /api/products — get all active products
	@GetMapping
	public ResponseEntity<List<ProductResponse>> getAllProducts() {
		return ResponseEntity.ok(productService.getAllProducts());
	}

	// GET /api/products/{id} — get one product by ID
	@GetMapping("/{id}")
	public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
		return ResponseEntity.ok(productService.getProductById(id));
	}

	// GET /api/products/search?keyword=phone&category=Electronics
	// Both params are optional — works with either, both, or neither
	@GetMapping("/search")
	public ResponseEntity<List<ProductResponse>> search(@RequestParam(required = false) String keyword,
			@RequestParam(required = false) String category) {
		return ResponseEntity.ok(productService.searchProducts(keyword, category));
	}
}