package Employee.Employeesal.Controller;


import Employee.Employeesal.Model.TaxInfo;
import Employee.Employeesal.Model.Employee;
import Employee.Employeesal.Model.ValidationResult;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.*;

import Employee.Employeesal.Service.EmployeeService;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping("/add")
    public ResponseEntity<String> addEmployee(@RequestBody Employee employee) {
        ValidationResult validationResult = validateEmployeeData(employee);
        if (!validationResult.isValid()) {
            List<String> errors = validationResult.getErrors();
            StringBuilder errorMessage = new StringBuilder();
            for (String error : errors) {
                errorMessage.append(error).append("\n");
            }
            return ResponseEntity.badRequest().body(errorMessage.toString());
        }
        employeeService.addEmployee(employee);
        return ResponseEntity.ok("Employee details added successfully");
    }

    @GetMapping("/tax")
    public ResponseEntity<List<TaxInfo>> getTaxDeduction() {
        List<TaxInfo> taxInfoList = employeeService.calculateTaxDeductionForCurrentYear();
        return ResponseEntity.ok(taxInfoList);
    }

    private ValidationResult validateEmployeeData(Employee employee) {
        ValidationResult validationResult = new ValidationResult();

        if (employee.getEmployeeId() == null || employee.getEmployeeId() <= 0) {
            validationResult.addError("Employee ID is required and should be greater than 0");
        }
        if (employee.getFirstName() == null || employee.getFirstName().isEmpty()) {
            validationResult.addError("First Name is required");
        }
        if (employee.getLastName() == null || employee.getLastName().isEmpty()) {
            validationResult.addError("Last Name is required");
        }
        if (employee.getEmail() == null || employee.getEmail().isEmpty()) {
            validationResult.addError("Email is required");
        }
        if (employee.getPhoneNumbers() == null || employee.getPhoneNumbers().isEmpty()) {
            validationResult.addError("At least one Phone Number is required");
        }
        if (employee.getDoj() == null) {
            validationResult.addError("Date of Joining is required");
        }
        if (employee.getSalary() == null || employee.getSalary().compareTo(BigDecimal.ZERO) <= 0) {
            validationResult.addError("Salary should be a positive value");
        }

        return validationResult;
    }
}
