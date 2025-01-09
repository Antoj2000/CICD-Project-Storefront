package ie.atu.storefront;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@FeignClient(name = "product-service", url = "http://localhost:8082/product")  // Corrected URL for Product Microservice
public interface ProductServiceClient {



    // Endpoint to retrieve a product by productId
    @GetMapping
    List<Product> getAllProducts();

    @GetMapping("/{productId}")
    Product getProductById(@PathVariable("productId") String productId);

    @PutMapping("/{productId}")
    Product updateProductStock(@PathVariable String productId, Product product);

    @PutMapping("/add/{productId}")
    Product updateProductInventory(@PathVariable String productId, @RequestParam int stockToAdd);


    // Endpoint to update the product stock
    /*@PutMapping("/{productId}")
    Product updateProductStock(@PathVariable String productId, @RequestParam int stockQuantity);*/
}
