package maintenance;

import maintenance.service.MaintenanceManager;
import maintenance.model.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class MaintenanceSystem {
    private MaintenanceManager maintenanceManager;
    private Scanner scanner;
    private String storeId;

    public MaintenanceSystem(String storeId) {
        this.storeId = storeId;
        this.maintenanceManager = new MaintenanceManager(storeId);
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        while (true) {
            System.out.println("\nStore Cleaning and Maintenance System");
            System.out.println("1. View Pending Tasks(Store Manager)");
            System.out.println("2. Assign Cleaning Task(Store Manager)");
            System.out.println("3. Complete Cleaning Task(Cleaning Staff)");
            System.out.println("4. Report Maintenance Issue(Maintenance Staff)");
            System.out.println("5. Assign Maintenance Task(Store Manager)");
            System.out.println("6. Complete Maintenance Task(Maintenance Staff)");
            System.out.println("7. Generate Maintenance Report(Head Office Manager)");
            System.out.println("8. Return to Main Menu");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    maintenanceManager.displayPendingTasks();
                    break;
                case 2:
                    assignCleaningTask();
                    break;
                case 3:
                    completeCleaningTask();
                    break;
                case 4:
                    reportMaintenanceIssue();
                    break;
                case 5:
                    assignMaintenanceTask();
                    break;
                case 6:
                    completeMaintenanceTask();
                    break;
                case 7:
                    generateMaintenanceReport();
                    break;
                case 8:
                    return;
                default:
                    System.out.println("Invalid option");
            }
        }
    }

    private void assignCleaningTask() {
        maintenanceManager.displayPendingTasks();
        System.out.print("Enter task ID: ");
        String taskId = scanner.nextLine();
        System.out.print("Enter staff ID: ");
        String staffId = scanner.nextLine();
        maintenanceManager.assignCleaningTask(taskId, staffId);
        System.out.println("Task assigned successfully");
    }

    private void completeCleaningTask() {
        maintenanceManager.displayPendingTasks();
        System.out.print("Enter task ID: ");
        String taskId = scanner.nextLine();
        System.out.print("Enter completion notes: ");
        String notes = scanner.nextLine();
        maintenanceManager.completeCleaningTask(taskId, notes);
        System.out.println("Task marked as completed");
    }

    private void reportMaintenanceIssue() {
        System.out.print("Enter issue description: ");
        String description = scanner.nextLine();
        System.out.print("Enter area: ");
        String area = scanner.nextLine();
        System.out.println("Select priority level:");
        System.out.println("1. LOW");
        System.out.println("2. MEDIUM");
        System.out.println("3. HIGH");
        System.out.println("4. EMERGENCY");
        System.out.print("Enter choice: ");
        
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        
        TaskPriority priority;
        switch (choice) {
            case 1: priority = TaskPriority.LOW; break;
            case 2: priority = TaskPriority.MEDIUM; break;
            case 3: priority = TaskPriority.HIGH; break;
            case 4: priority = TaskPriority.EMERGENCY; break;
            default: 
                System.out.println("Invalid choice. Setting to MEDIUM priority.");
                priority = TaskPriority.MEDIUM;
        }
        
        maintenanceManager.reportMaintenanceIssue(description, area, priority);
        System.out.println("Maintenance issue reported successfully");
    }

    private void assignMaintenanceTask() {
        maintenanceManager.displayPendingTasks();
        System.out.print("Enter task ID: ");
        String taskId = scanner.nextLine();
        System.out.print("Enter technician ID: ");
        String technicianId = scanner.nextLine();
        maintenanceManager.assignMaintenanceTask(taskId, technicianId);
        System.out.println("Task assigned successfully");
    }

    private void completeMaintenanceTask() {
        maintenanceManager.displayPendingTasks();
        System.out.print("Enter task ID: ");
        String taskId = scanner.nextLine();
        System.out.print("Enter completion notes: ");
        String notes = scanner.nextLine();
        maintenanceManager.completeMaintenanceTask(taskId, notes);
        System.out.println("Task marked as completed");
    }

    private void generateMaintenanceReport() {
        System.out.print("Enter start date (yyyy-MM-dd): ");
        String startDateStr = scanner.nextLine();
        System.out.print("Enter end date (yyyy-MM-dd): ");
        String endDateStr = scanner.nextLine();
        
        try {
            LocalDate startDate = LocalDate.parse(startDateStr, DateTimeFormatter.ISO_LOCAL_DATE);
            LocalDate endDate = LocalDate.parse(endDateStr, DateTimeFormatter.ISO_LOCAL_DATE);
            
            if (endDate.isBefore(startDate)) {
                System.out.println("End date cannot be before start date");
                return;
            }
            
            maintenanceManager.generateMaintenanceReport(startDate, endDate);
            System.out.println("Maintenance report generated successfully");
        } catch (Exception e) {
            System.out.println("Invalid date format. Please use yyyy-MM-dd");
        }
    }
}