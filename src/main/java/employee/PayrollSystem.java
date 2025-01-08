/**
 * Main PayrollSystem class that handles the user interface and interaction
 * for employee time tracking and payroll management.
 *
 * @author Hrishikesha Kyathsandra
 */
package employee;

import employee.model.Employee;
import employee.service.PayrollManager;
import employee.service.LeaveManager;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.util.List;
import java.util.Map;
import employee.model.LeaveType;
import employee.model.LeaveStatus;
import employee.model.Leave;

public class PayrollSystem {
    // Manager instance to handle payroll operations
    private PayrollManager payrollManager;
    // Scanner for user input
    private Scanner scanner;
    // Store identifier
    private String storeId;
    // Leave manager instance
    private LeaveManager leaveManager;

    /**
     * Constructor initializes the PayrollSystem with a specific store ID
     * @param storeId The unique identifier for the store
     */
    public PayrollSystem(String storeId) {
        this.storeId = storeId;
        this.payrollManager = new PayrollManager(storeId);
        this.scanner = new Scanner(System.in);
        this.leaveManager = new LeaveManager(storeId);
    }

    public void start() {
        while (true) {
            System.out.println("\nPayroll System");
            System.out.println("1. Clock In (Employee)");
            System.out.println("2. Clock Out (Employee)");
            System.out.println("3. Start Break (Employee)");
            System.out.println("4. End Break (Employee)");
            System.out.println("5. Enter Custom Hours (Employee)");
            System.out.println("6. Request Leave (Employee)");
            System.out.println("7. View Leave Balance (Employee)");
            System.out.println("8. Approve Leave Requests (Manager)");
            System.out.println("9. View Time Records (Manager)");
            System.out.println("10. Approve Overtime (Manager)");
            System.out.println("11. Generate Payroll Report (Manager)");
            System.out.println("12. Return to Main Menu");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    clockIn();
                    break;
                case 2:
                    clockOut();
                    break;
                case 3:
                    startBreak();
                    break;
                case 4:
                    endBreak();
                    break;
                case 5:
                    enterCustomHours();
                    break;
                case 6:
                    requestLeave();
                    break;
                case 7:
                    viewLeaveBalance();
                    break;
                case 8:
                    approveLeaveRequests();
                    break;
                case 9:
                    viewTimeRecords();
                    break;
                case 10:
                    approveOvertime();
                    break;
                case 11:
                    generatePayrollReport();
                    break;
                case 12:
                    return;
                default:
                    System.out.println("Invalid option");
            }
        }
    }

    private void clockIn() {
        System.out.print("Enter employee ID: ");
        String employeeId = scanner.nextLine();
        
        if (payrollManager.clockInEmployee(employeeId)) {
            System.out.println("Clock in successful");
        } else {
            System.out.println("Clock in failed. Employee may already be clocked in or ID invalid");
        }
    }

    private void clockOut() {
        System.out.print("Enter employee ID: ");
        String employeeId = scanner.nextLine();
        
        if (payrollManager.clockOutEmployee(employeeId)) {
            System.out.println("Clock out successful");
        } else {
            System.out.println("Clock out failed. Employee may not be clocked in or ID invalid");
        }
    }

    private void startBreak() {
        System.out.print("Enter employee ID: ");
        String employeeId = scanner.nextLine();
        
        if (payrollManager.startBreak(employeeId)) {
            System.out.println("Break started successfully");
        } else {
            System.out.println("Failed to start break. Employee may not be clocked in or ID invalid");
        }
    }

    private void endBreak() {
        System.out.print("Enter employee ID: ");
        String employeeId = scanner.nextLine();
        
        if (payrollManager.endBreak(employeeId)) {
            System.out.println("Break ended successfully");
        } else {
            System.out.println("Failed to end break. Employee may not be on break or ID invalid");
        }
    }

    private void enterCustomHours() {
        System.out.print("Enter employee ID: ");
        String employeeId = scanner.nextLine();
        
        System.out.print("Enter date (yyyy-MM-dd): ");
        String dateStr = scanner.nextLine();
        LocalDate date = LocalDate.parse(dateStr);
        
        System.out.print("Enter start time (HH:mm): ");
        String startTimeStr = scanner.nextLine();
        
        System.out.print("Enter end time (HH:mm): ");
        String endTimeStr = scanner.nextLine();
        
        System.out.print("Enter total break time in minutes: ");
        int breakMinutes = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        
        payrollManager.addCustomTimeRecord(employeeId, date, startTimeStr, endTimeStr, breakMinutes);
    }

    private void requestLeave() {
        System.out.print("Enter employee ID: ");
        String employeeId = scanner.nextLine();
        
        System.out.println("Leave types:");
        System.out.println("1. Vacation");
        System.out.println("2. Sick");
        System.out.println("3. Personal");
        System.out.println("4. Emergency");
        System.out.print("Choose leave type (1-4): ");
        
        LeaveType type;
        String choice = scanner.nextLine();
        switch (choice) {
            case "1":
                type = LeaveType.VACATION;
                break;
            case "2":
                type = LeaveType.SICK;
                break;
            case "3":
                type = LeaveType.PERSONAL;
                break;
            case "4":
                type = LeaveType.EMERGENCY;
                break;
            default:
                type = null;
        }
        
        if (type == null) {
            System.out.println("Invalid leave type");
            return;
        }

        System.out.print("Enter start date (yyyy-MM-dd): ");
        LocalDate startDate = LocalDate.parse(scanner.nextLine());
        
        System.out.print("Enter end date (yyyy-MM-dd): ");
        LocalDate endDate = LocalDate.parse(scanner.nextLine());
        
        System.out.print("Enter reason: ");
        String reason = scanner.nextLine();
        
        if (leaveManager.requestLeave(employeeId, startDate, endDate, type, reason)) {
            System.out.println("Leave request submitted successfully");
        } else {
            System.out.println("Failed to submit leave request. Check leave balance.");
        }
    }

    private void viewLeaveBalance() {
        System.out.print("Enter employee ID: ");
        String employeeId = scanner.nextLine();
        
        Map<LeaveType, Integer> balance = leaveManager.getLeaveBalance(employeeId);
        
        System.out.println("\nLeave Balance");
        System.out.println("--------------------------------------------------");
        for (Map.Entry<LeaveType, Integer> entry : balance.entrySet()) {
            System.out.printf("%s: %d days%n", entry.getKey(), entry.getValue());
        }
        System.out.println("--------------------------------------------------");
    }

    private void viewTimeRecords() {
        System.out.print("Enter employee ID: ");
        String employeeId = scanner.nextLine();
        System.out.print("Enter date (yyyy-MM-dd): ");
        String dateStr = scanner.nextLine();
        
        LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE);
        payrollManager.displayTimeRecords(employeeId, date);
    }

    private void approveOvertime() {
        payrollManager.displayPendingOvertimeRequests();
        
        System.out.print("\nEnter employee ID to approve (or press Enter to cancel): ");
        String employeeId = scanner.nextLine().trim();
        
        if (employeeId.isEmpty()) {
            System.out.println("Approval cancelled");
            return;
        }
        
        System.out.print("Enter date (yyyy-MM-dd): ");
        String dateStr = scanner.nextLine();
        
        try {
            LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE);
            if (payrollManager.approveOvertime(employeeId, date)) {
                System.out.println("Overtime approved successfully");
            } else {
                System.out.println("Failed to approve overtime. Check employee ID and date");
            }
        } catch (Exception e) {
            System.out.println("Invalid date format. Please use yyyy-MM-dd");
        }
    }

    private void generatePayrollReport() {
        System.out.print("Enter start date (yyyy-MM-dd): ");
        String startDateStr = scanner.nextLine();
        System.out.print("Enter end date (yyyy-MM-dd): ");
        String endDateStr = scanner.nextLine();
        
        LocalDate startDate = LocalDate.parse(startDateStr, DateTimeFormatter.ISO_LOCAL_DATE);
        LocalDate endDate = LocalDate.parse(endDateStr, DateTimeFormatter.ISO_LOCAL_DATE);
        
        if (endDate.isBefore(startDate)) {
            System.out.println("End date cannot be before start date");
            return;
        }

        double totalPayout = payrollManager.generatePayrollReport(startDate, endDate);
        System.out.printf("Payroll report generated. Total payout: $%.2f%n", totalPayout);
    }

    private void approveLeaveRequests() {
        System.out.println("\nPending Leave Requests");
        System.out.println("--------------------------------------------------");
        
        List<Leave> pendingLeaves = leaveManager.getPendingLeaves();
        if (pendingLeaves.isEmpty()) {
            System.out.println("No pending leave requests");
            return;
        }

        for (Leave leave : pendingLeaves) {
            System.out.printf("Employee ID: %s%n", leave.getEmployeeId());
            System.out.printf("Type: %s%n", leave.getType());
            System.out.printf("Period: %s to %s%n", leave.getStartDate(), leave.getEndDate());
            System.out.printf("Reason: %s%n", leave.getReason());
            System.out.print("Approve? (yes/no): ");
            
            String response = scanner.nextLine().trim().toLowerCase();
            if (response.equals("yes")) {
                leaveManager.approveLeave(leave.getEmployeeId(), leave.getStartDate());
                System.out.println("Leave approved");
            } else {
                leaveManager.rejectLeave(leave.getEmployeeId(), leave.getStartDate());
                System.out.println("Leave rejected");
            }
            System.out.println("--------------------------------------------------");
        }
    }
}