package eyetest.service;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import eyetest.model.EyeTestAppointment;

public class EyeTestManager {
    private List<EyeTestAppointment> appointments;
    private Scanner scanner;
    private static final String APPOINTMENTS_FILE = "./src/main/java/eyetest/data/eye_test_appointments.txt";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final String[] TIME_SLOTS = {
        "09:00", "09:30", "10:00", "10:30", "11:00", "11:30",
        "13:00", "13:30", "14:00", "14:30", "15:00", "15:30"
    };

    public EyeTestManager() {
        this.appointments = new ArrayList<>();
        this.scanner = new Scanner(System.in);
        loadAppointments();
    }

    private void loadAppointments() {
        try (BufferedReader reader = new BufferedReader(new FileReader(APPOINTMENTS_FILE))) {
            reader.readLine(); // Skip header
            reader.readLine(); // Skip separator
            
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    String[] parts = line.split("\\|");
                    if (parts.length >= 10) {
                        appointments.add(new EyeTestAppointment(parts));
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading appointments: " + e.getMessage());
        }
    }

    private void saveAppointments() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(APPOINTMENTS_FILE))) {
            writer.println("ID | PatientName | Date | TimeSlot | Optometrist | Status | Contact | TestType | Notes | PrescriptionIssued |");
            writer.println("-".repeat(120));
            for (EyeTestAppointment apt : appointments) {
                writer.printf("%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%n",
                    apt.getId(),
                    apt.getPatientName(),
                    apt.getAppointmentDate(),
                    apt.getTimeSlot(),
                    apt.getOptometrist(),
                    apt.getStatus(),
                    apt.getContactNumber(),
                    apt.getTestType(),
                    apt.getNotes(),
                    apt.isPrescriptionIssued());
            }
        } catch (IOException e) {
            System.err.println("Error saving appointments: " + e.getMessage());
        }
    }

    public void startEyeTestManagement() {
        while (true) {
            System.out.println("\nEye Test Management System");
            System.out.println("1. Book New Appointment");
            System.out.println("2. View Today's Appointments");
            System.out.println("3. Update Appointment Status");
            System.out.println("4. Record Test Results");
            System.out.println("5. Issue Prescription");
            System.out.println("6. View Available Time Slots");
            System.out.println("7. View All Appointments");
            System.out.println("8. Return to Main Menu");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1: bookNewAppointment(); break;
                case 2: viewTodaysAppointments(); break;
                case 3: updateAppointmentStatus(); break;
                case 4: recordTestResults(); break;
                case 5: issuePrescription(); break;
                case 6: viewAvailableTimeSlots(); break;
                case 7: viewAppointments(); break;
                case 8: return;
                default: System.out.println("Invalid option");
            }
        }
    }

    private void bookNewAppointment() {
        System.out.println("\nBook New Eye Test Appointment");
        
        System.out.print("Enter patient name: ");
        String patientName = scanner.nextLine();
        
        System.out.print("Enter contact number: ");
        String contactNumber = scanner.nextLine();
        
        // Show available dates
        LocalDate date = LocalDate.now();
        System.out.println("\nAvailable dates:");
        for (int i = 0; i < 5; i++) {
            System.out.println((i + 1) + ". " + date.plusDays(i).format(DATE_FORMATTER));
        }
        System.out.print("Choose date (1-5): ");
        int dateChoice = scanner.nextInt();
        scanner.nextLine();
        String appointmentDate = date.plusDays(dateChoice - 1).format(DATE_FORMATTER);

        // Show available time slots
        System.out.println("\nAvailable time slots:");
        for (int i = 0; i < TIME_SLOTS.length; i++) {
            System.out.println((i + 1) + ". " + TIME_SLOTS[i]);
        }
        System.out.print("Choose time slot (1-" + TIME_SLOTS.length + "): ");
        int timeChoice = scanner.nextInt();
        scanner.nextLine();
        String timeSlot = TIME_SLOTS[timeChoice - 1];

        System.out.println("\nTest Types:");
        System.out.println("1. Standard Eye Test");
        System.out.println("2. Contact Lens Fitting");
        System.out.println("3. Children's Eye Test");
        System.out.print("Choose test type (1-3): ");
        int testChoice = scanner.nextInt();
        scanner.nextLine();
        
        String testType = switch (testChoice) {
            case 1 -> "Standard Eye Test";
            case 2 -> "Contact Lens Fitting";
            case 3 -> "Children's Eye Test";
            default -> "Standard Eye Test";
        };

        String[] appointmentParts = {
            String.valueOf(appointments.size() + 1),
            patientName,
            appointmentDate,
            timeSlot,
            "Dr. Smith", // Default optometrist
            "Scheduled",
            contactNumber,
            testType,
            "", // Initial notes empty
            "false" // Prescription not issued yet
        };

        appointments.add(new EyeTestAppointment(appointmentParts));
        saveAppointments();
        System.out.println("Appointment booked successfully.");
    }

    private void viewTodaysAppointments() {
        String today = LocalDate.now().format(DATE_FORMATTER);
        List<EyeTestAppointment> todaysApts = appointments.stream()
            .filter(apt -> apt.getAppointmentDate().equals(today))
            .toList();

        if (todaysApts.isEmpty()) {
            System.out.println("No appointments scheduled for today.");
            return;
        }

        System.out.println("\nToday's Appointments:");
        System.out.println("-".repeat(100));
        for (EyeTestAppointment apt : todaysApts) {
            System.out.printf("%s | %s | %s | %s | %s | %s%n",
                apt.getId(),
                apt.getPatientName(),
                apt.getTimeSlot(),
                apt.getTestType(),
                apt.getStatus(),
                apt.isPrescriptionIssued() ? "Prescription Issued" : "No Prescription");
        }
        System.out.println("-".repeat(100));
    }

    private void viewAppointments() {
        List<EyeTestAppointment> apts = appointments.stream()
            .toList();

        if (apts.isEmpty()) {
            System.out.println("No appointments scheduled.");
            return;
        }

        System.out.println("\nAppointments Scheduled:");
        System.out.println("-".repeat(100));
        for (EyeTestAppointment apt : apts) {
            System.out.printf("%s | %s | %s | %s | %s | %s%n",
                apt.getId(),
                apt.getPatientName(),
                apt.getTimeSlot(),
                apt.getTestType(),
                apt.getStatus(),
                apt.isPrescriptionIssued() ? "Prescription Issued" : "No Prescription");
        }
        System.out.println("-".repeat(100));
    }

    private void updateAppointmentStatus() {
        System.out.print("Enter appointment ID: ");
        String id = scanner.nextLine();

        EyeTestAppointment apt = appointments.stream()
            .filter(a -> a.getId().equals(id))
            .findFirst()
            .orElse(null);

        if (apt == null) {
            System.out.println("Appointment not found.");
            return;
        }

        System.out.println("Select new status:");
        System.out.println("1. Scheduled");
        System.out.println("2. In Progress");
        System.out.println("3. Completed");
        System.out.println("4. Cancelled");
        System.out.print("Enter choice (1-4): ");

        int choice = scanner.nextInt();
        scanner.nextLine();

        String newStatus = switch (choice) {
            case 1 -> "Scheduled";
            case 2 -> "In Progress";
            case 3 -> "Completed";
            case 4 -> "Cancelled";
            default -> null;
        };

        if (newStatus != null) {
            apt.setStatus(newStatus);
            saveAppointments();
            System.out.println("Status updated successfully.");
        } else {
            System.out.println("Invalid status choice.");
        }
    }

    private void recordTestResults() {
        System.out.print("Enter appointment ID: ");
        String id = scanner.nextLine();

        EyeTestAppointment apt = appointments.stream()
            .filter(a -> a.getId().equals(id))
            .findFirst()
            .orElse(null);

        if (apt == null) {
            System.out.println("Appointment not found.");
            return;
        }

        System.out.println("Enter test results and notes:");
        String notes = scanner.nextLine();
        
        apt.setNotes(notes);
        apt.setStatus("Completed");
        saveAppointments();
        System.out.println("Test results recorded successfully.");
    }

    private void issuePrescription() {
        System.out.print("Enter appointment ID: ");
        String id = scanner.nextLine();

        EyeTestAppointment apt = appointments.stream()
            .filter(a -> a.getId().equals(id))
            .findFirst()
            .orElse(null);

        if (apt == null) {
            System.out.println("Appointment not found.");
            return;
        }

        if (!apt.getStatus().equals("Completed")) {
            System.out.println("Cannot issue prescription for incomplete test.");
            return;
        }

        apt.setPrescriptionIssued(true);
        saveAppointments();
        System.out.println("Prescription issued successfully.");
    }

    private void viewAvailableTimeSlots() {
        System.out.println("\nAvailable time slots for next 5 days:");
        LocalDate date = LocalDate.now();
        
        for (int day = 0; day < 5; day++) {
            String currentDate = date.plusDays(day).format(DATE_FORMATTER);
            System.out.println("\n" + currentDate + ":");
            
            for (String timeSlot : TIME_SLOTS) {
                boolean isAvailable = appointments.stream()
                    .noneMatch(apt -> 
                        apt.getAppointmentDate().equals(currentDate) && 
                        apt.getTimeSlot().equals(timeSlot) &&
                        !apt.getStatus().equals("Cancelled"));
                
                if (isAvailable) {
                    System.out.println("- " + timeSlot);
                }
            }
        }
    }
}