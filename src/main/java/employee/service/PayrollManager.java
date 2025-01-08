package employee.service;

/**
 * Service class that manages all payroll-related operations including
 * time tracking, overtime approval, and payroll report generation.
 * @author Akhilesh Nevatia
 */

import employee.model.Employee;
import employee.model.TimeRecord;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.io.*;
import java.util.Scanner;

public class PayrollManager {
    private Map<String, Employee> employees;
    private String storeId;
    private static final double OVERTIME_RATE = 1.5;
    private static final int REGULAR_HOURS = 8;

    public PayrollManager(String storeId) {
        this.storeId = storeId;
        this.employees = new HashMap<>();
        // Create data directory if it doesn't exist
        new File("./src/main/java/store/data").mkdirs();
        new File("./src/main/java/employee/data").mkdirs();
        loadEmployees();
    }

    private void loadEmployees() {
        String fileName = "./src/main/java/employee/data/" + storeId + "_employees.txt";
        File file = new File(fileName);
        
        if (!file.exists()) {
            // Initialize with sample employees for the store
            initializeSampleEmployees();
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    String id = parts[0];
                    String name = parts[1];
                    double hourlyRate = Double.parseDouble(parts[2]);
                    employees.put(id, new Employee(id, name, hourlyRate));
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading employees: " + e.getMessage());
            initializeSampleEmployees();
        }
    }

    private void initializeSampleEmployees() {
        // Create sample employees based on store ID
        if (storeId.equals("ST001")) {
            employees.put("EMP001", new Employee("EMP001", "John Smith", 15.0));
            employees.put("EMP002", new Employee("EMP002", "Jane Doe", 18.0));
            employees.put("EMP003", new Employee("EMP003", "Bob Wilson", 20.0));
        } else if (storeId.equals("ST002")) {
            employees.put("EMP004", new Employee("EMP004", "Alice Brown", 16.0));
            employees.put("EMP005", new Employee("EMP005", "Charlie Davis", 19.0));
        } else if (storeId.equals("ST003")) {
            employees.put("EMP006", new Employee("EMP006", "Eva Green", 17.0));
            employees.put("EMP007", new Employee("EMP007", "David Miller", 21.0));
        }
        saveEmployees();
        System.out.println("\nAvailable Employee IDs for " + storeId + ":");
        employees.keySet().forEach(id -> System.out.println(id + " - " + employees.get(id).getName()));
    }

    private void saveEmployees() {
        String fileName = "./src/main/java/employee/data/" + storeId + "_employees.txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (Employee emp : employees.values()) {
                writer.write(String.format("%s,%s,%.2f%n", 
                    emp.getEmployeeId(), 
                    emp.getName(), 
                    emp.getHourlyRate()));
            }
        } catch (IOException e) {
            System.out.println("Error saving employees: " + e.getMessage());
        }
    }

    public boolean clockInEmployee(String employeeId) {
        Employee employee = employees.get(employeeId);
        if (employee != null && !employee.isClockIn()) {
            employee.clockIn();
            return true;
        }
        return false;
    }

    public boolean clockOutEmployee(String employeeId) {
        Employee employee = employees.get(employeeId);
        if (employee != null && employee.isClockIn()) {
            employee.clockOut();
            saveTimeRecord(employee);
            return true;
        }
        return false;
    }

    private void saveTimeRecord(Employee employee) {
        String fileName = "./src/main/java/store/data/" + storeId + "_timerecords.txt";
        TimeRecord record = employee.getTimeRecords().get(employee.getTimeRecords().size() - 1);
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true))) {
            writer.write(String.format("%s,%s,%s,%s,%b,%b,%b\n",
                employee.getEmployeeId(),
                record.getStartTime(),
                record.getEndTime(),
                record.getHoursWorked(),
                record.isBreak(),
                record.isOvertime(),
                record.isApproved()));
        } catch (IOException e) {
            System.out.println("Error saving time record: " + e.getMessage());
        }
    }

    public double generatePayrollReport(LocalDate startDate, LocalDate endDate) {
        double totalPayout = 0;
        String reportFileName = "./src/main/java/store/data/" + storeId + "_payroll_" + 
                              startDate + "_to_" + endDate + ".txt";
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(reportFileName))) {
            writer.write("Payroll Report: " + startDate + " to " + endDate + "\n");
            writer.write("----------------------------------------\n");
            
            for (Employee emp : employees.values()) {
                double employeePay = calculateEmployeePay(emp, startDate, endDate);
                totalPayout += employeePay;
                
                writer.write(String.format("Employee: %s (%s)\n", emp.getName(), emp.getEmployeeId()));
                writer.write(String.format("Hours Worked: %.2f\n", getHoursWorked(emp, startDate, endDate)));
                writer.write(String.format("Total Pay: $%.2f\n\n", employeePay));
            }
            
            writer.write(String.format("Total Payroll: $%.2f\n", totalPayout));
        } catch (IOException e) {
            System.out.println("Error generating payroll report: " + e.getMessage());
        }
        
        return totalPayout;
    }

    private double calculateEmployeePay(Employee employee, LocalDate startDate, LocalDate endDate) {
        double regularPay = 0;
        double overtimePay = 0;
        
        for (TimeRecord record : employee.getTimeRecords()) {
            if (isWithinDateRange(record.getStartTime(), startDate, endDate)) {
                double hours = record.getHoursWorked();
                if (hours > REGULAR_HOURS && record.isApproved()) {
                    regularPay += REGULAR_HOURS * employee.getHourlyRate();
                    overtimePay += (hours - REGULAR_HOURS) * employee.getHourlyRate() * OVERTIME_RATE;
                } else {
                    regularPay += hours * employee.getHourlyRate();
                }
            }
        }
        
        return regularPay + overtimePay;
    }

    private boolean isWithinDateRange(LocalDateTime dateTime, LocalDate startDate, LocalDate endDate) {
        LocalDate date = dateTime.toLocalDate();
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }

    private double getHoursWorked(Employee employee, LocalDate startDate, LocalDate endDate) {
        return employee.getTimeRecords().stream()
            .filter(r -> isWithinDateRange(r.getStartTime(), startDate, endDate))
            .mapToDouble(TimeRecord::getHoursWorked)
            .sum();
    }

    public boolean startBreak(String employeeId) {
        Employee employee = employees.get(employeeId);
        if (employee != null && employee.isClockIn()) {
            List<TimeRecord> records = employee.getTimeRecords();
            if (!records.isEmpty()) {
                TimeRecord currentRecord = records.get(records.size() - 1);
                currentRecord.setBreak(true);
                saveTimeRecord(employee, currentRecord);
                return true;
            }
        }
        return false;
    }

    public boolean endBreak(String employeeId) {
        Employee employee = employees.get(employeeId);
        if (employee != null && employee.isClockIn()) {
            List<TimeRecord> records = employee.getTimeRecords();
            if (!records.isEmpty()) {
                TimeRecord currentRecord = records.get(records.size() - 1);
                if (currentRecord.isBreak()) {
                    currentRecord.setBreak(false);
                    saveTimeRecord(employee, currentRecord);
                    return true;
                }
            }
        }
        return false;
    }

    public void displayTimeRecords(String employeeId, LocalDate date) {
        Employee employee = employees.get(employeeId);
        if (employee == null) {
            System.out.println("Employee not found");
            return;
        }

        System.out.println("\nTime Records for " + employee.getName() + " on " + date);
        System.out.println("--------------------------------------------------");
        System.out.printf("%-20s %-20s %-10s %-10s%n", "Start Time", "End Time", "Break", "Overtime");
        
        for (TimeRecord record : employee.getTimeRecords()) {
            LocalDateTime recordDate = record.getStartTime();
            if (recordDate.toLocalDate().equals(date)) {
                System.out.printf("%-20s %-20s %-10s %-10s%n",
                    record.getStartTime().toLocalTime(),
                    record.getEndTime() != null ? record.getEndTime().toLocalTime() : "Active",
                    record.isBreak() ? "Yes" : "No",
                    record.isOvertime() ? "Yes" : "No");
            }
        }
        System.out.println("--------------------------------------------------");
    }

    public void displayPendingOvertimeRequests() {
        System.out.println("\nPending Overtime Approvals");
        System.out.println("--------------------------------------------------");
        System.out.printf("%-10s %-20s %-12s %-10s %-15s%n", 
            "ID", "Name", "Hours", "Date", "Status");
        
        boolean foundPending = false;
        for (Employee emp : employees.values()) {
            for (TimeRecord record : emp.getTimeRecords()) {
                double hours = record.getHoursWorked();
                if (hours > REGULAR_HOURS && !record.isApproved()) {
                    foundPending = true;
                    System.out.printf("%-10s %-20s %-12.2f %-10s %-15s%n",
                        emp.getEmployeeId(),
                        emp.getName(),
                        hours,
                        record.getStartTime().toLocalDate(),
                        "Pending");
                }
            }
        }
        
        if (!foundPending) {
            System.out.println("No pending overtime approvals found.");
        }
        System.out.println("--------------------------------------------------");
    }

    public boolean approveOvertime(String employeeId, LocalDate date) {
        Employee employee = employees.get(employeeId);
        if (employee == null) {
            System.out.println("Employee not found");
            return false;
        }

        boolean overtimeFound = false;
        for (TimeRecord record : employee.getTimeRecords()) {
            if (record.getStartTime().toLocalDate().equals(date) && 
                record.getHoursWorked() > REGULAR_HOURS && 
                !record.isApproved()) {
                record.setOvertime(true);
                record.setApproved(true);
                saveTimeRecord(employee, record);
                overtimeFound = true;
                System.out.println("Overtime approved for " + employee.getName());
                break;
            }
        }
        
        if (!overtimeFound) {
            System.out.println("No pending overtime found for this employee on " + date);
        }
        return overtimeFound;
    }

    private void saveTimeRecord(Employee employee, TimeRecord record) {
        String fileName = "./src/main/java/store/data/" + storeId + "_timerecords.txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true))) {
            writer.write(String.format("%s,%s,%s,%s,%b,%b,%b\n",
                employee.getEmployeeId(),
                record.getStartTime(),
                record.getEndTime(),
                record.getHoursWorked(),
                record.isBreak(),
                record.isOvertime(),
                record.isApproved()));
        } catch (IOException e) {
            System.out.println("Error saving time record: " + e.getMessage());
        }
    }

    public void addCustomTimeRecord(String employeeId, LocalDate date, String startTimeStr, 
                                  String endTimeStr, int breakMinutes) {
        Employee employee = employees.get(employeeId);
        if (employee == null) {
            System.out.println("Employee not found");
            return;
        }

        try {
            LocalDateTime startTime = LocalDateTime.of(date, LocalTime.parse(startTimeStr));
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.parse(endTimeStr));
            
            if (endTime.isBefore(startTime)) {
                System.out.println("End time cannot be before start time");
                return;
            }

            // Calculate total hours worked
            double totalHours = (endTime.getHour() - startTime.getHour()) + 
                                (endTime.getMinute() - startTime.getMinute()) / 60.0;
            // Subtract break time
            totalHours -= breakMinutes / 60.0;

            TimeRecord record = new TimeRecord(startTime);
            record.setEndTime(endTime);
            record.setBreakDuration(breakMinutes);
            
            // Check for overtime
            if (totalHours > REGULAR_HOURS) {
                record.setOvertime(true);
                record.setApproved(false); // Requires approval
            }

            employee.addTimeRecord(record);
            saveTimeRecord(employee, record);
            
            System.out.printf("Time record added. Total hours worked: %.2f%n", totalHours);
            if (record.isOvertime()) {
                System.out.println("Overtime detected. Pending approval.");
            }
            
        } catch (Exception e) {
            System.out.println("Error processing time entry: " + e.getMessage());
        }
    }

    public void displayOvertimeStatus(LocalDate date) {
        System.out.println("\nOvertime Status for " + date);
        System.out.println("--------------------------------------------------");
        System.out.printf("%-10s %-20s %-12s %-10s %-10s%n", 
            "ID", "Name", "Hours", "Overtime", "Approved");
        
        for (Employee emp : employees.values()) {
            for (TimeRecord record : emp.getTimeRecords()) {
                if (record.getStartTime().toLocalDate().equals(date)) {
                    double hours = record.getHoursWorked();
                    if (hours > REGULAR_HOURS) {
                        System.out.printf("%-10s %-20s %-12.2f %-10s %-10s%n",
                            emp.getEmployeeId(),
                            emp.getName(),
                            hours,
                            "Yes",
                            record.isApproved() ? "Yes" : "Pending");
                    }
                }
            }
        }
        System.out.println("--------------------------------------------------");
    }
}