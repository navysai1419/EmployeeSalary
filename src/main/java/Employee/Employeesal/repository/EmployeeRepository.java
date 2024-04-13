package Employee.Employeesal.repository;
import Employee.Employeesal.Model.TaxInfo;
import Employee.Employeesal.Model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    // Add custom query methods if needed
    List<Employee> findByFirstName(String firstName);
}

