package Employee.Employeesal.Service;

import Employee.Employeesal.Model.ValidationResult;
import Employee.Employeesal.repository.EmployeeRepository;
import Employee.Employeesal.Model.TaxInfo;
import Employee.Employeesal.Model.Employee;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public ValidationResult validateEmployee(Employee employee) {
        ValidationResult validationResult = new ValidationResult();

        if (employee.getEmployeeId() == null || employee.getEmployeeId() <= 0) {
            validationResult.addError("Employee ID is required and should be a positive number.");
        }
        if (employee.getFirstName() == null || employee.getFirstName().isEmpty()) {
            validationResult.addError("First Name is required.");
        }
        if (employee.getLastName() == null || employee.getLastName().isEmpty()) {
            validationResult.addError("Last Name is required.");
        }
        if (employee.getEmail() == null || employee.getEmail().isEmpty()) {
            validationResult.addError("Email is required.");
        }
        if (employee.getPhoneNumbers() == null || employee.getPhoneNumbers().isEmpty()) {
            validationResult.addError("At least one Phone Number is required.");
        }
        if (employee.getDoj() == null) {
            validationResult.addError("Date of Joining is required.");
        }
        if (employee.getSalary() == null || employee.getSalary().compareTo(BigDecimal.ZERO) <= 0) {
            validationResult.addError("Salary should be a positive value.");
        }

        return validationResult;
    }

    @Override
    public void addEmployee(Employee employee) {
        employeeRepository.save(employee);
    }

    @Override
    public List<TaxInfo> calculateTaxDeductionForCurrentYear() {
        List<Employee> employees = employeeRepository.findAll();

        // List to hold tax info for all employees
        List<TaxInfo> taxInfoList = new ArrayList<>();

        for (Employee employee : employees) {
            BigDecimal yearlySalary = calculateYearlySalary(employee);

            BigDecimal taxAmount = calculateTaxAmount(yearlySalary);
            BigDecimal cessAmount = calculateCessAmount(yearlySalary);

            TaxInfo taxInfo = new TaxInfo();
            taxInfo.setEmployeeId(employee.getEmployeeId());
            taxInfo.setFirstName(employee.getFirstName());
            taxInfo.setLastName(employee.getLastName());
            taxInfo.setYearlySalary(yearlySalary);
            taxInfo.setTaxAmount(taxAmount);
            taxInfo.setCessAmount(cessAmount);

            taxInfoList.add(taxInfo);
        }

        return taxInfoList;
    }

    private BigDecimal calculateYearlySalary(Employee employee) {
        // Assuming each month has 30 days for simplicity
        int totalDaysInYear = 360;
        int daysWorked = LocalDate.now().getDayOfYear() - employee.getDoj().getDayOfYear();

        // Calculate number of months worked
        BigDecimal numberOfMonthsWorked = BigDecimal.valueOf(daysWorked)
                .divide(BigDecimal.valueOf(totalDaysInYear), 2, BigDecimal.ROUND_HALF_UP)
                .multiply(BigDecimal.valueOf(12));

        // Calculate yearly salary
        BigDecimal yearlySalary = employee.getSalary().multiply(numberOfMonthsWorked);

        return yearlySalary;
    }

    private BigDecimal calculateTaxAmount(BigDecimal yearlySalary) {
        BigDecimal taxAmount = BigDecimal.ZERO;

        if (yearlySalary.compareTo(BigDecimal.valueOf(250000)) > 0) {
            BigDecimal remainingSalary = yearlySalary.subtract(BigDecimal.valueOf(250000));
            if (remainingSalary.compareTo(BigDecimal.valueOf(250000)) <= 0) {
                taxAmount = remainingSalary.multiply(BigDecimal.valueOf(0.05));
            } else if (remainingSalary.compareTo(BigDecimal.valueOf(500000)) <= 0) {
                taxAmount = BigDecimal.valueOf(12500).add(remainingSalary.subtract(BigDecimal.valueOf(250000)).multiply(BigDecimal.valueOf(0.1)));
            } else if (remainingSalary.compareTo(BigDecimal.valueOf(1000000)) <= 0) {
                taxAmount = BigDecimal.valueOf(37500).add(remainingSalary.subtract(BigDecimal.valueOf(500000)).multiply(BigDecimal.valueOf(0.2)));
            } else {
                taxAmount = BigDecimal.valueOf(137500).add(remainingSalary.subtract(BigDecimal.valueOf(1000000)).multiply(BigDecimal.valueOf(0.3)));
            }
        }

        return taxAmount;
    }

    private BigDecimal calculateCessAmount(BigDecimal yearlySalary) {
        BigDecimal cessPercentage = BigDecimal.valueOf(0.02);
        BigDecimal cessAmount = BigDecimal.ZERO;

        if (yearlySalary.compareTo(BigDecimal.valueOf(2500000)) > 0) {
            cessAmount = yearlySalary.subtract(BigDecimal.valueOf(2500000))
                    .multiply(cessPercentage);
        }

        return cessAmount;
    }
}
