package com.ecommerce.config;

import com.ecommerce.entity.Product;
import com.ecommerce.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.List;

@Configuration
public class ProductDataLoader {

	@Bean
	CommandLineRunner seedProducts(ProductRepository productRepository) {
		return args -> {
			List<Product> demoProducts = List.of(
					createProduct("Wireless Headphones", "Comfortable Bluetooth headphones with deep bass and long battery life.",
							"Electronics", "2499.00", 18,
							"https://images.unsplash.com/photo-1505740420928-5e560c06d30e?auto=format&fit=crop&w=900&q=80"),
					createProduct("Smart Watch", "Fitness tracking smartwatch with heart-rate monitor and notification support.",
							"Electronics", "3999.00", 12,
							"https://images.unsplash.com/photo-1523275335684-37898b6baf30?auto=format&fit=crop&w=900&q=80"),
					createProduct("Running Shoes", "Lightweight everyday running shoes with breathable mesh.",
							"Footwear", "1899.00", 24,
							"https://images.unsplash.com/photo-1542291026-7eec264c27ff?auto=format&fit=crop&w=900&q=80"),
					createProduct("Travel Backpack", "Durable backpack with laptop storage and organized compartments.",
							"Accessories", "1299.00", 30,
							"https://images.unsplash.com/photo-1553062407-98eeb64c6a62?auto=format&fit=crop&w=900&q=80"));

			demoProducts.stream()
					.filter(product -> !productRepository.existsByNameIgnoreCaseAndActiveTrue(product.getName()))
					.forEach(productRepository::save);
		};
	}

	private Product createProduct(String name, String description, String category, String price, Integer stock,
			String imageUrl) {
		Product product = new Product();
		product.setName(name);
		product.setDescription(description);
		product.setCategory(category);
		product.setPrice(new BigDecimal(price));
		product.setStock(stock);
		product.setImageUrl(imageUrl);
		product.setActive(true);
		return product;
	}
}
