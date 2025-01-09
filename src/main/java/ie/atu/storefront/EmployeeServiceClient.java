package ie.atu.storefront;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "employee-service", url = "http://localhost:8081/employee")
public interface EmployeeServiceClient {

    @GetMapping("/{employeeId}")
    Person getEmployeeById(@PathVariable("employeeId") String employeeId);
}
