package employee;

import java.io.*;
import java.util.Scanner;
import java.util.HashMap;
import java.util.Map;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.InputMismatchException;

public class HealthcareManagementSystem {
    private final String storeId;
    private final Scanner scanner;
    private Map<String, EmployeeHealthcare> employeeHealthcare;
    private final String dataFilePath;

    public HealthcareManagementSystem(String storeId) {
        this.storeId = storeId;
        this.scanner = new Scanner(System.in);
        this.dataFilePath = "src/main/java/employee/data/healthcare_" + storeId + ".txt";
        loadHealthcareData();
    }

    private void loadHealthcareData() {
        employeeHealthcare = new HashMap<>();
        try {
            File file = new File(dataFilePath);
            if (file.exists()) {
                List<String> lines = Files.readAllLines(Paths.get(dataFilePath));
                for (String line : lines) {
                    String[] parts = line.split(",");
                    if (parts.length == 4) {
                        String employeeId = parts[0];
                        String name = parts[1];
                        String employmentType = parts[2];
                        String plan = parts[3];
                        employeeHealthcare.put(employeeId, 
                            new EmployeeHealthcare(employeeId, name, employmentType, plan));
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading healthcare data: " + e.getMessage());
        }
    }

    private void saveHealthcareData() {
        try {
            StringBuilder content = new StringBuilder();
            for (EmployeeHealthcare healthcare : employeeHealthcare.values()) {
                content.append(String.format("%s,%s,%s,%s%n",
                    healthcare.getEmployeeId(),
                    healthcare.getName(),
                    healthcare.getEmploymentType(),
                    healthcare.getPlan()));
            }
            Files.write(Paths.get(dataFilePath), content.toString().getBytes());
        } catch (IOException e) {
            System.out.println("Error saving healthcare data: " + e.getMessage());
        }
    }

    public void start() {
        while (true) {
            displayMenu();
            int choice = getValidMenuChoice();
            if (choice == -1) continue; // Invalid input, show menu again

            switch (choice) {
                case 1:
                    addEmployeeToHealthcarePlan();
                    break;
                case 2:
                    updateEmployeeHealthcare();
                    break;
                case 3:
                    terminateHealthcareCoverage();
                    break;
                case 4:
                    viewEmployeeHealthcare();
                    break;
                case 5:
                    listAllEmployeeHealthcare();
                    break;
                case 6:
                    return;
                default:
                    System.out.println("Invalid option. Please choose a number between 1 and 6.");
            }
        }
    }

    private void displayMenu() {
        System.out.println("\nEmployee Healthcare Management System");
        System.out.println("1. Add Employee to Healthcare Plan (HR Manager)");
        System.out.println("2. Update Employee Healthcare Details (HR Manager)");
        System.out.println("3. Terminate Employee Healthcare Coverage (HR Manager/Store Manager)");
        System.out.println("4. View Employee Healthcare Details (HR Manager/Employee)");
        System.out.println("5. List All Employees Healthcare Details (HR Manager)");
        System.out.println("6. Return to Main Menu");
    }

    private int getValidMenuChoice() {
        System.out.print("Choose an option (1-6): ");
        try {
            String input = scanner.nextLine().trim();
            int choice = Integer.parseInt(input);
            if (choice >= 1 && choice <= 6) {
                return choice;
            } else {
                System.out.println("Please enter a number between 1 and 6.");
                return -1;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number between 1 and 6.");
            return -1;
        }
    }

    private int getValidPlanChoice() {
        while (true) {
            System.out.print("Select Plan (1-3): ");
            try {
                String input = scanner.nextLine().trim();
                int choice = Integer.parseInt(input);
                if (choice >= 1 && choice <= 3) {
                    return choice;
                } else {
                    System.out.println("Please enter a number between 1 and 3.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number between 1 and 3.");
            }
        }
    }

    private String getValidEmployeeId() {
        while (true) {
            System.out.print("Enter Employee ID: ");
            String employeeId = scanner.nextLine().trim();
            if (employeeId.isEmpty()) {
                System.out.println("Employee ID cannot be empty. Please try again.");
                continue;
            }
            if (!employeeId.matches("[a-zA-Z0-9_-]+")) {
                System.out.println("Employee ID can only contain letters, numbers, hyphens, and underscores. Please try again.");
                continue;
            }
            return employeeId;
        }
    }

    private String getValidName() {
        while (true) {
            System.out.print("Enter Employee Name: ");
            String name = scanner.nextLine().trim();
            if (name.isEmpty()) {
                System.out.println("Name cannot be empty. Please try again.");
                continue;
            }
            if (!name.matches("[a-zA-Z\\s-']+")) {
                System.out.println("Name can only contain letters, spaces, hyphens, and apostrophes. Please try again.");
                continue;
            }
            return name.replace(",", ""); // Remove commas to prevent CSV issues
        }
    }

    private String getValidEmploymentType() {
        while (true) {
            System.out.print("Enter Employment Type (FULL_TIME/PART_TIME): ");
            String type = scanner.nextLine().trim().toUpperCase();
            if (type.equals("FULL_TIME") || type.equals("PART_TIME")) {
                return type;
            }
            System.out.println("Invalid employment type. Please enter either FULL_TIME or PART_TIME.");
        }
    }

    private void addEmployeeToHealthcarePlan() {
        System.out.println("\nAdd Employee to Healthcare Plan");
        
        String employeeId = getValidEmployeeId();
        if (employeeHealthcare.containsKey(employeeId)) {
            System.out.println("Employee already has a healthcare plan!");
            return;
        }
        
        String name = getValidName();
        String employmentType = getValidEmploymentType();
        
        if (!isEligible(employmentType)) {
            System.out.println("Employee is not eligible for healthcare benefits.");
            System.out.println("Only FULL_TIME employees are eligible.");
            return;
        }

        System.out.println("\nAvailable Healthcare Plans:");
        System.out.println("1. Basic Plan");
        System.out.println("2. Premium Plan");
        System.out.println("3. Family Plan");
        
        int planChoice = getValidPlanChoice();
        String selectedPlan = switch (planChoice) {
            case 1 -> "Basic Plan";
            case 2 -> "Premium Plan";
            case 3 -> "Family Plan";
            default -> throw new IllegalStateException("Invalid plan selection");
        };

        try {
            EmployeeHealthcare healthcare = new EmployeeHealthcare(employeeId, name, employmentType, selectedPlan);
            employeeHealthcare.put(employeeId, healthcare);
            saveHealthcareData();
            System.out.println("Employee successfully enrolled in healthcare plan");
            System.out.println("Notifying insurance provider and updating payroll system...");
        } catch (Exception e) {
            System.out.println("Error adding employee to healthcare plan: " + e.getMessage());
        }
    }

    private void updateEmployeeHealthcare() {
        String employeeId = getValidEmployeeId();
        EmployeeHealthcare healthcare = employeeHealthcare.get(employeeId);
        
        if (healthcare == null) {
            System.out.println("Employee not found in healthcare system");
            return;
        }

        try {
            System.out.println("\nCurrent Plan: " + healthcare.getPlan());
            System.out.println("\nAvailable Healthcare Plans:");
            System.out.println("1. Basic Plan");
            System.out.println("2. Premium Plan");
            System.out.println("3. Family Plan");
            
            int planChoice = getValidPlanChoice();
            String newPlan = switch (planChoice) {
                case 1 -> "Basic Plan";
                case 2 -> "Premium Plan";
                case 3 -> "Family Plan";
                default -> throw new IllegalStateException("Invalid plan selection");
            };

            healthcare.setPlan(newPlan);
            saveHealthcareData();
            System.out.println("Healthcare plan updated successfully");
            System.out.println("Notifying insurance provider and updating payroll system...");
        } catch (Exception e) {
            System.out.println("Error updating healthcare plan: " + e.getMessage());
        }
    }

    private void terminateHealthcareCoverage() {
        String employeeId = getValidEmployeeId();
        
        try {
            if (employeeHealthcare.remove(employeeId) != null) {
                saveHealthcareData();
                System.out.println("Healthcare coverage terminated successfully");
                System.out.println("Checking COBRA eligibility...");
                System.out.println("Notifying insurance provider and updating payroll system...");
            } else {
                System.out.println("Employee not found in healthcare system");
            }
        } catch (Exception e) {
            System.out.println("Error terminating healthcare coverage: " + e.getMessage());
        }
    }

    private void viewEmployeeHealthcare() {
        String employeeId = getValidEmployeeId();
        
        try {
            EmployeeHealthcare healthcare = employeeHealthcare.get(employeeId);
            if (healthcare != null) {
                displayEmployeeHealthcare(healthcare);
            } else {
                System.out.println("Employee not found in healthcare system");
            }
        } catch (Exception e) {
            System.out.println("Error viewing healthcare details: " + e.getMessage());
        }
    }

    private void listAllEmployeeHealthcare() {
        if (employeeHealthcare.isEmpty()) {
            System.out.println("No employees enrolled in healthcare plans");
            return;
        }

        System.out.println("\nAll Employee Healthcare Details:");
        System.out.println("--------------------------------");
        employeeHealthcare.values().forEach(this::displayEmployeeHealthcare);
    }

    private void displayEmployeeHealthcare(EmployeeHealthcare healthcare) {
        System.out.println("\nEmployee ID: " + healthcare.getEmployeeId());
        System.out.println("Name: " + healthcare.getName());
        System.out.println("Employment Type: " + healthcare.getEmploymentType());
        System.out.println("Healthcare Plan: " + healthcare.getPlan());
        System.out.println("--------------------------------");
    }

    private boolean isEligible(String employmentType) {
        return employmentType.equalsIgnoreCase("FULL_TIME");
    }

    private static class EmployeeHealthcare implements Serializable {
        private final String employeeId;
        private final String name;
        private final String employmentType;
        private String plan;

        public EmployeeHealthcare(String employeeId, String name, String employmentType, String plan) {
            this.employeeId = employeeId;
            this.name = name;
            this.employmentType = employmentType;
            this.plan = plan;
        }

        public String getEmployeeId() {
            return employeeId;
        }

        public String getName() {
            return name;
        }

        public String getEmploymentType() {
            return employmentType;
        }

        public String getPlan() {
            return plan;
        }

        public void setPlan(String plan) {
            this.plan = plan;
        }
    }
} 