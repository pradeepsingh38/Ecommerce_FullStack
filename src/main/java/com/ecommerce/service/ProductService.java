package com.ecommerce.service;

import com.ecommerce.dto.ProductRequest;
import com.ecommerce.dto.ProductResponse;
import java.util.List;

public interface ProductService {
	ProductResponse addProduct(ProductRequest request);

	List<ProductResponse> getAllProducts();

	ProductResponse getProductById(Long id);

	List<ProductResponse> searchProducts(String keyword, String category);
}