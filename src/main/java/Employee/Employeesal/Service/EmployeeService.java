package Employee.Employeesal.Service;

import Employee.Employeesal.Model.TaxInfo;
import Employee.Employeesal.Model.Employee;
import Employee.Employeesal.Model.ValidationResult;

import java.util.List;


public interface EmployeeService {
    ValidationResult validateEmployee(Employee employee);
    void addEmployee(Employee employee);
    List<TaxInfo> calculateTaxDeductionForCurrentYear();
}