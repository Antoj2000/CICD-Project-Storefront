package ie.atu.storefront;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "customer-service", url = "http://localhost:8080/customer")  // URL of the Customer Microservice
public interface CustomerServiceClient {

    @GetMapping("/{email}")
    Customer getCustomerByEmail(@PathVariable("email") String email);

    @PostMapping
    Customer addCustomer(@RequestBody Customer customer);

    @PutMapping("/balance/{email}")
    public ResponseEntity<?> updateCustomerBalance(@PathVariable String email, Customer updatedCustomer);




}
