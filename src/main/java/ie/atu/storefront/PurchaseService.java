package ie.atu.storefront;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PurchaseService {

    @Autowired
    private ProductServiceClient productServiceClient;

    @Autowired
    private CustomerServiceClient customerServiceClient;

    @Autowired
    private EmployeeServiceClient employeeServiceClient;

    public List<Product> getAllProducts(){
        return productServiceClient.getAllProducts();
    }

    public Person getEmployeeDetails(String employeeId) {
        // Call the Employee Microservice to fetch the employee by employeeId
        return employeeServiceClient.getEmployeeById(employeeId);
    }

    public Customer getCustomerDetails(String email) {
        // Call the Customer Microservice to fetch the customer by email
        return customerServiceClient.getCustomerByEmail(email);
    }

    public Product getProductDetails(String productId) {
        // Call the Product Microservice to fetch the product by productId
        return productServiceClient.getProductById(productId);
    }

    public String addStockToProduct(String productId, int stockToAdd) {
        try {
            // Call the ProductServiceClient to update the stock
            Product updatedProduct = productServiceClient.updateProductInventory(productId, stockToAdd);
            return "Stock updated successfully. New stock for product: " + updatedProduct.getName() + " is " + updatedProduct.getStockQuantity();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error while updating stock for product with ID " + productId;
        }
    }

    public String makePurchase(String productId, String email) {
        try {
            //Get product details from the Product Microservice
            Product product = productServiceClient.getProductById(productId);

            if (product == null || product.getStockQuantity() <= 0) {
                // If the product is not found or out of stock
                return "Product is out of stock or doesn't exist!";
            }

            // Get customer details from the Customer Microservice
            Customer customer = customerServiceClient.getCustomerByEmail(email);

            if (customer == null) {
                // If the customer is not found
                return "Customer not found!";
            }

            //Check if the customer has enough money
            if (customer.getBalance() < product.getPrice()) {
                // If the customer balance is insufficient
                return "Insufficient balance!";
            }

            //Deduct product stock
            product.setStockQuantity(product.getStockQuantity() - 1);
            productServiceClient.updateProductStock(productId, product);  // Update the product in the Product Microservice

            // Update the customer balance
            customer.setBalance(customer.getBalance() - product.getPrice());
            customerServiceClient.updateCustomerBalance(email, customer);  // Update the customer in the Customer Microservice

            return "Purchase successful! Product: " + product.getName() + " purchased by " + customer.getName() + ".";

        } catch (Exception e) {
            // Handle exception
            return "Error occurred during the purchase process: " + e.getMessage();
        }
    }
}
