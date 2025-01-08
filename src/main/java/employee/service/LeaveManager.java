package employee.service;

import employee.model.*;
import java.time.*;
import java.util.*;
import java.io.*;
import java.time.temporal.ChronoUnit;

public class LeaveManager {
    private Map<String, List<Leave>> employeeLeaves;
    private String storeId;
    private static final int ANNUAL_LEAVE_DAYS = 20;
    private static final int SICK_LEAVE_DAYS = 10;

    public LeaveManager(String storeId) {
        this.storeId = storeId;
        this.employeeLeaves = new HashMap<>();
        loadLeaveRecords();
    }

    private void loadLeaveRecords() {
        String fileName = "./src/main/java/store/data/" + storeId + "_leaves.txt";
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                String employeeId = parts[0];
                LocalDate startDate = LocalDate.parse(parts[1]);
                LocalDate endDate = LocalDate.parse(parts[2]);
                LeaveType type = LeaveType.valueOf(parts[3]);
                LeaveStatus status = LeaveStatus.valueOf(parts[4]);
                String reason = parts[5];

                Leave leave = new Leave(employeeId, startDate, endDate, type, reason);
                leave.setStatus(status);
                employeeLeaves.computeIfAbsent(employeeId, k -> new ArrayList<>()).add(leave);
            }
        } catch (IOException e) {
            System.err.println("Error loading leave records: " + e.getMessage());
        }
    }

    private void updateLeaveRecord(Leave leave) {
        List<Leave> allLeaves = new ArrayList<>();
        for (List<Leave> leaves : employeeLeaves.values()) {
            allLeaves.addAll(leaves);
        }
        
        String fileName = "./src/main/java/store/data/" + storeId + "_leaves.txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (Leave l : allLeaves) {
                writer.write(String.format("%s,%s,%s,%s,%s,%s,%s\n",
                    l.getEmployeeId(),
                    l.getStartDate(),
                    l.getEndDate(),
                    l.getType(),
                    l.getStatus(),
                    l.getReason(),
                    LocalDateTime.now()));
            }
        } catch (IOException e) {
            System.err.println("Error updating leave record: " + e.getMessage());
        }
    }

    public List<Leave> getPendingLeaves() {
        List<Leave> pendingLeaves = new ArrayList<>();
        for (List<Leave> leaves : employeeLeaves.values()) {
            for (Leave leave : leaves) {
                if (leave.getStatus() == LeaveStatus.PENDING) {
                    pendingLeaves.add(leave);
                }
            }
        }
        return pendingLeaves;
    }

    public Map<LeaveType, Integer> getLeaveBalance(String employeeId) {
        Map<LeaveType, Integer> balance = new HashMap<>();
        balance.put(LeaveType.VACATION, ANNUAL_LEAVE_DAYS);
        balance.put(LeaveType.SICK, SICK_LEAVE_DAYS);

        if (employeeLeaves.containsKey(employeeId)) {
            for (Leave leave : employeeLeaves.get(employeeId)) {
                if (leave.getStatus() == LeaveStatus.APPROVED) {
                    if (leave.getType() == LeaveType.VACATION || leave.getType() == LeaveType.SICK) {
                        long days = ChronoUnit.DAYS.between(leave.getStartDate(), leave.getEndDate()) + 1;
                        balance.merge(leave.getType(), -(int)days, Integer::sum);
                    }
                }
            }
        }
        return balance;
    }

    public void rejectLeave(String employeeId, LocalDate startDate) {
        List<Leave> leaves = employeeLeaves.get(employeeId);
        if (leaves != null) {
            for (Leave leave : leaves) {
                if (leave.getStartDate().equals(startDate) && 
                    leave.getStatus() == LeaveStatus.PENDING) {
                    leave.setStatus(LeaveStatus.REJECTED);
                    updateLeaveRecord(leave);
                    break;
                }
            }
        }
    }

    public List<Leave> getEmployeeLeaveHistory(String employeeId) {
        return employeeLeaves.getOrDefault(employeeId, new ArrayList<>());
    }

    public boolean cancelLeave(String employeeId, LocalDate startDate) {
        List<Leave> leaves = employeeLeaves.get(employeeId);
        if (leaves != null) {
            for (Leave leave : leaves) {
                if (leave.getStartDate().equals(startDate) && 
                    leave.getStatus() == LeaveStatus.APPROVED &&
                    !leave.getStartDate().isBefore(LocalDate.now())) {
                    leaves.remove(leave);
                    updateLeaveRecord(leave);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isOnLeave(String employeeId, LocalDate date) {
        List<Leave> leaves = employeeLeaves.get(employeeId);
        if (leaves != null) {
            for (Leave leave : leaves) {
                if (leave.getStatus() == LeaveStatus.APPROVED &&
                    !date.isBefore(leave.getStartDate()) &&
                    !date.isAfter(leave.getEndDate())) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean requestLeave(String employeeId, LocalDate startDate, LocalDate endDate, LeaveType type, String reason) {
        // Check if employee has enough leave balance
        if (!hasLeaveBalance(employeeId, type, startDate, endDate)) {
            return false;
        }

        Leave leave = new Leave(employeeId, startDate, endDate, type, reason);
        employeeLeaves.computeIfAbsent(employeeId, k -> new ArrayList<>()).add(leave);
        updateLeaveRecord(leave);
        return true;
    }

    public void approveLeave(String employeeId, LocalDate startDate) {
        List<Leave> leaves = employeeLeaves.get(employeeId);
        if (leaves != null) {
            for (Leave leave : leaves) {
                if (leave.getStartDate().equals(startDate) && 
                    leave.getStatus() == LeaveStatus.PENDING) {
                    leave.setStatus(LeaveStatus.APPROVED);
                    updateLeaveRecord(leave);
                    break;
                }
            }
        }
    }

    private boolean hasLeaveBalance(String employeeId, LeaveType type, LocalDate startDate, LocalDate endDate) {
        // Personal and Emergency leaves don't need balance checking
        if (type == LeaveType.PERSONAL || type == LeaveType.EMERGENCY) {
            return true;
        }

        // Calculate requested days
        long requestedDays = ChronoUnit.DAYS.between(startDate, endDate) + 1;

        // Get current balance
        Map<LeaveType, Integer> balance = getLeaveBalance(employeeId);
        Integer availableDays = balance.get(type);

        return availableDays != null && availableDays >= requestedDays;
    }
} 