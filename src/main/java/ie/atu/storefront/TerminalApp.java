package ie.atu.storefront;
import java.util.List;
import java.util.Scanner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;

@Component
public class TerminalApp implements CommandLineRunner {

    @Autowired
    private EmployeeServiceClient employeeServiceClient;
    @Autowired
    private CustomerServiceClient customerServiceClient;
    @Autowired
    private ProductServiceClient productServiceClient;
    @Autowired
    private PurchaseService purchaseService;

    //Saves email for customers on log in
    private String loggedinEmail = "";

    public static void main(String[] args) {
        SpringApplication.run(StoreFrontApplication.class, args);
    }
    @Override
    public void run(String... args)throws Exception {
        //automatically called after application starts
        Scanner scanner = new Scanner(System.in);

        // Welcome message
        System.out.println("Welcome to AJ Stores");
        System.out.println("Are you a customer or an employee?");
        System.out.print("Enter 'c' for customer or 'e' for employee or 'n' to create a new customer account: : ");

        // Get the user input (c for customer or e for employee)
        String userInput = scanner.nextLine();

        // Check user input and handle accordingly
        if ("c".equalsIgnoreCase(userInput)) {
            System.out.println("Welcome, Customer!");
            // Ask for customer email and validate
            System.out.print("Please enter your email: ");
            String email = scanner.nextLine();
            checkCustomer(email, scanner);
        } else if ("e".equalsIgnoreCase(userInput)) {
            System.out.println("Welcome, Employee!");
            // Ask for employee ID and validate
            System.out.print("Please enter your employee ID: ");
            String employeeId = scanner.nextLine();
            checkEmployee(employeeId, scanner);
        }  else if ("n".equalsIgnoreCase(userInput)) {
            System.out.println("Creating a new customer account...");
            createCustomerAccount(scanner);
        } else {
            System.out.println("Invalid input. Please enter 'c' for customer, 'e' for employee, or 'n' to create a new customer account.");
        }

        scanner.close();
    }
    public void createCustomerAccount(Scanner scanner) {
        System.out.println("Please provide the following details to create a new account:");

        System.out.print("Name: ");
        String name = scanner.nextLine();

        System.out.print("Email: ");
        String email = scanner.nextLine();

        System.out.print("Age: ");
        String age = scanner.nextLine();

        System.out.print("Address: ");
        String address = scanner.nextLine();

        System.out.print("Phone Number: ");
        String phoneNumber = scanner.nextLine();

        System.out.print("Balance (initial deposit): ");
        double balance = Double.parseDouble(scanner.nextLine());

        Customer newCustomer = new Customer();
        newCustomer.setName(name);
        newCustomer.setEmail(email);
        newCustomer.setAge(Integer.parseInt(age));
        newCustomer.setAddress(address);
        newCustomer.setPhoneNumber(phoneNumber);
        newCustomer.setBalance(balance);

        try {
            //Call the Customer Service to create new customer
            customerServiceClient.addCustomer(newCustomer);
            System.out.println("Customer account created successfully! You can now log in.");
            checkCustomer(email,scanner);

        } catch (Exception e) {
            System.out.println("Error while creating customer account.");
            e.printStackTrace(); // Help debug
        }

    }

    public void checkEmployee(String employeeId, Scanner scanner) {
        try {
            // Call Employee Microservice to validate the employee ID
            Person person = employeeServiceClient.getEmployeeById(employeeId);
            if (person != null) {
                System.out.println("Employee found: " + person.getName());
                employeeMenu(scanner);
            } else {
                System.out.println("No employee found with ID: " + employeeId);
            }
        } catch (Exception e) {
            System.out.println("Error while fetching employee details.");
            e.printStackTrace(); //help debug
        }
    }

    public void checkCustomer(String email, Scanner scanner) {
        try {
            // Call Customer Microservice to validate the email
            Customer customer = customerServiceClient.getCustomerByEmail(email);
            if (customer != null) {
                System.out.println("Customer found: " + customer.getName());
                loggedinEmail = email;
                customerMenu(scanner);
            } else {
                System.out.println("No customer found with email: " + email);
            }
        } catch (Exception e) {
            System.out.println("Error while fetching customer details.");
        }
    }

    // Customer menu logic
    public void customerMenu(Scanner scanner) {
        while (true) {
            System.out.println("Customer Menu:");
            System.out.println("1. View Products");
            System.out.println("2. Make a Purchase");
            System.out.println("3. Check account balance");
            System.out.println("4. Exit");

            System.out.print("Please choose an option: ");
            String choice = scanner.nextLine();

            // Handle different customer options
            switch (choice) {
                case "1":
                    System.out.println("Displaying product list...");
                    // Add logic to display products
                    getAllProductsCustomer();
                    break;
                case "2":
                    System.out.println("Proceeding to make a purchase...");
                    // Add logic to handle purchases
                    makePurchase(scanner);
                    break;
                case "3":
                    System.out.println("Checking your balance...");
                    checkBalance();  // Check the balance logic
                    break;
                case "4":
                    System.out.println("Exiting... Goodbye!");
                    return;
                default:
                    System.out.println("Invalid option. Returning to the main menu...");
                    customerMenu(scanner);
                    break;
            }
        }
    }
        // display all products
        public void getAllProductsCustomer () {
            try {
                // fetch all products
                List<Product> products = productServiceClient.getAllProducts();

                // Display products
                if (products != null && !products.isEmpty()) {
                    System.out.println("Product List:");
                    for (Product product : products) {
                        System.out.println("ID: " + product.getProductId() + ", Name: " + product.getName()
                                + ", Price: " + product.getPrice()
                                + ", Category:" + product.getCategory());
                    }
                } else {
                    System.out.println("No products available.");
                }
            } catch (Exception e) {
                System.out.println("Error while fetching product details.");
                e.printStackTrace();
            }
        }

        public void makePurchase (Scanner scanner){
            // Ask for the product ID to purchase
            System.out.print("Enter the Product ID to purchase: ");
            String productId = scanner.nextLine();

            // Call the Purchase Service to make the purchase
            try {
                String purchaseResult = purchaseService.makePurchase(productId, loggedinEmail);

                // Display the result of the purchase operation
                System.out.println(purchaseResult);
                //Display balance
                Customer customer = customerServiceClient.getCustomerByEmail(loggedinEmail);
                System.out.println("Remaining balance: " + customer.getBalance());
            } catch (Exception e) {
                System.out.println("Error while making the purchase.");
                e.printStackTrace(); // Help debug
            }
        }
        public void checkBalance () {
            try {
                Customer customer = customerServiceClient.getCustomerByEmail(loggedinEmail);
                if (customer != null) {
                    System.out.println("Your current balance: " + customer.getBalance());
                } else {
                    System.out.println("Customer not found.");
                }
            } catch (Exception e) {
                System.out.println("Error while fetching customer balance.");
            }
        }


    // Employee menu logic
    public void employeeMenu(Scanner scanner) {
        while (true) {
            System.out.println("Employee Menu:");
            System.out.println("1. View Products and Stock");
            System.out.println("2. Manage Product Inventory");
            System.out.println("3. Manage Product Pricing");
            System.out.println("4. Exit");

            System.out.print("Please choose an option: ");
            String choice = scanner.nextLine();

            // Handle different employee options
            switch (choice) {
                case "1":
                    System.out.println("Viewing products...");
                    // Logic for employee viewing
                    getAllProductsEmployee();
                    break;
                case "2":
                    System.out.println("Managing product inventory...");
                    // Add logic for managing products (e.g., adding stock)
                    manageProductInventory(scanner);
                    break;
                case "3":
                    // Add logic for changing product price
                    System.out.println("Changing product price");
                    break;
                case "4":
                    System.out.println("Exiting... Goodbye!");
                    return;
                default:
                    System.out.println("Invalid option. Returning to the main menu...");
                    employeeMenu(scanner);
                    break;
            }
        }
    }
    public void getAllProductsEmployee() {
        try {
            // fetch all products
            List<Product> products = productServiceClient.getAllProducts();

            // Display products
            if (products != null && !products.isEmpty()) {
                System.out.println("Product List:");
                for (Product product : products) {
                    System.out.println("ID: " + product.getProductId() + ", Name: " + product.getName()
                            + ", Price: " + product.getPrice()
                            + ", Stock available: " + product.getStockQuantity());
                }
            } else {
                System.out.println("No products available.");
            }
        } catch (Exception e) {
            System.out.println("Error while fetching product details.");
            e.printStackTrace();
        }
    }
    // New method to manage product inventory and update stock
    public void manageProductInventory(Scanner scanner) {
        // Ask for the product ID
        System.out.print("Enter the Product ID to update stock: ");
        String productId = scanner.nextLine();
        try {
            // Fetch the product by productId
            Product product = productServiceClient.getProductById(productId);

            if (product != null) {
                // Display the current stock
                System.out.println("Current stock for product " + product.getName() + ": " + product.getStockQuantity());
                // Ask for the amount of stock to add
                System.out.print("Enter the amount of stock to add: ");
                int stockToAdd = Integer.parseInt(scanner.nextLine());  // Parse the input to integer
                // Update the product stock
                String updateResult = purchaseService.addStockToProduct(productId, stockToAdd);
                // Call the ProductServiceClient to update the stock in the database
                System.out.println(updateResult);

            } else {
                System.out.println("Product with Product ID " + productId + " not found.");
            }
        } catch (Exception e) {
            System.out.println("Error while updating the product stock.");
            e.printStackTrace();  // Help debug
        }
    }



}

