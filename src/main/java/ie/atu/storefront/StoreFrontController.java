package ie.atu.storefront;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class StoreFrontController {

    private final  PurchaseService purchaseService;

    public StoreFrontController(PurchaseService purchaseService) {
        this.purchaseService = purchaseService;
    }

    @GetMapping("/storefront/product")
    public List<Product> getAllProducts(){
        return purchaseService.getAllProducts();
    }


    @GetMapping("/storefront/employee/{employeeId}")
    public ResponseEntity<?> getEmployee(@PathVariable String employeeId) {
        Person employee = purchaseService.getEmployeeDetails(employeeId);

        if (employee != null) {
            return ResponseEntity.ok(employee);
        } else {
            return ResponseEntity.status(404).body("Employee not found with ID: " + employeeId);
        }
    }

    @GetMapping("/storefront/customer/{email}")
    public ResponseEntity<?> getCustomer(@PathVariable String email) {
        Customer customer = purchaseService.getCustomerDetails(email);

        if (customer != null) {
            return ResponseEntity.ok(customer);
        } else {
            return ResponseEntity.status(404).body("Customer not found with ID: " + email);
        }
    }

    @GetMapping("/storefront/product/{productId}")
    public ResponseEntity<?> getProduct(@PathVariable String productId) {
        Product product = purchaseService.getProductDetails(productId);

        if (product != null) {
            return ResponseEntity.ok(product);
        } else {
            return ResponseEntity.status(404).body("Product not found with ID: " + productId);
        }
    }


    @PutMapping("/storefront/updateStock/{productId}")
    public ResponseEntity<String> updateProductStock(@PathVariable String productId, @RequestParam int stockToAdd) {
        // Call the service to update the stock
        String result = purchaseService.addStockToProduct(productId, stockToAdd);

        // Return success or error message based on the result
        if (result.contains("successful")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
    }
    // Handle the purchase and update product stock
    @PutMapping("/storefront/purchase/{productId}")
    public ResponseEntity<String> makePurchase(@PathVariable String productId, @RequestParam String email) {
        // Call the purchaseService to make the purchase and update stock
        String result = purchaseService.makePurchase(productId, email);

        if (result.contains("successful")) {
            return ResponseEntity.status(HttpStatus.OK).body(result);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
    }

}

