/**
 * Manages donation programs and operations in the retail system.
 *
 * @author Raghuram Guddati
 */
package donation.service;

import donation.model.DonationProgram;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DonationManager {
    private List<DonationProgram> programs;
    private Scanner scanner;
    private static final String DONATION_FILE = "./src/main/java/donation/data/donation_programs.txt";

    public DonationManager() {
        this.programs = new ArrayList<>();
        this.scanner = new Scanner(System.in);
        loadPrograms();
    }

    private void loadPrograms() {
        try (BufferedReader reader = new BufferedReader(new FileReader(DONATION_FILE))) {
            reader.readLine(); // Skip header
            reader.readLine(); // Skip separator
            
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    String[] parts = line.split("\\|");
                    if (parts.length >= 7) {
                        programs.add(new DonationProgram(parts));
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading donation programs: " + e.getMessage());
        }
    }

    public void startDonationManagement() {
        while (true) {
            System.out.println("\nCommunity Donation Program Management");
            System.out.println("1. View All Donation Programs");
            System.out.println("2. Create New Donation Program");
            System.out.println("3. Update Program Status");
            System.out.println("4. Generate Impact Report");
            System.out.println("5. Return to Main Menu");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    viewPrograms();
                    break;
                case 2:
                    createProgram();
                    break;
                case 3:
                    updateProgramStatus();
                    break;
                case 4:
                    generateImpactReport();
                    break;
                case 5:
                    return;
                default:
                    System.out.println("Invalid option");
            }
        }
    }

    private void viewPrograms() {
        if (programs.isEmpty()) {
            System.out.println("No donation programs found.");
            return;
        }

        System.out.println("\nCurrent Donation Programs:");
        System.out.println("-".repeat(100));
        for (DonationProgram program : programs) {
            System.out.printf("ID: %s | Charity: %s | Goal: %s | Timeline: %s | Status: %s | Items: %s | Value: $%.2f%n",
                program.getId(), program.getCharityName(), program.getGoal(),
                program.getTimeline(), program.getStatus(), program.getItems(), program.getValue());
        }
        System.out.println("-".repeat(100));
    }

    private void createProgram() {
        System.out.println("\nCreating New Donation Program");
        System.out.println("-".repeat(30));

        String newId = String.valueOf(programs.size() + 1);

        System.out.print("Enter charity name: ");
        String charityName = scanner.nextLine();

        System.out.print("Enter program goal: ");
        String goal = scanner.nextLine();

        System.out.print("Enter timeline (YYYY-MM-DD): ");
        String timeline = scanner.nextLine();

        System.out.print("Enter items for donation: ");
        String items = scanner.nextLine();

        System.out.print("Enter estimated value: ");
        String value = scanner.nextLine();

        String[] programParts = new String[]{
            newId,
            charityName,
            goal,
            timeline,
            "Pending",
            items,
            value
        };

        try {
            DonationProgram newProgram = new DonationProgram(programParts);
            programs.add(newProgram);
            System.out.println("Donation program created successfully! Program ID: " + newId);
        } catch (Exception e) {
            System.out.println("Error creating program: " + e.getMessage());
        }
    }

    private void updateProgramStatus() {
        System.out.print("Enter program ID: ");
        String id = scanner.nextLine();

        DonationProgram program = programs.stream()
            .filter(p -> p.getId().equals(id))
            .findFirst()
            .orElse(null);

        if (program == null) {
            System.out.println("Program not found.");
            return;
        }

        System.out.println("Select new status:");
        System.out.println("1. Pending");
        System.out.println("2. Active");
        System.out.println("3. Completed");
        System.out.print("Enter choice (1-3): ");

        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        String newStatus;
        switch (choice) {
            case 1:
                newStatus = "Pending";
                break;
            case 2:
                newStatus = "Active";
                break;
            case 3:
                newStatus = "Completed";
                break;
            default:
                newStatus = null;
                break;
        }

        if (newStatus != null) {
            program.setStatus(newStatus);
            System.out.println("Status updated successfully.");
        } else {
            System.out.println("Invalid status choice.");
        }
    }

    private void generateImpactReport() {
        System.out.println("\nDonation Program Impact Report");
        System.out.println("=" .repeat(50));

        int totalPrograms = programs.size();
        long activePrograms = programs.stream().filter(p -> p.getStatus().equals("Active")).count();
        long completedPrograms = programs.stream().filter(p -> p.getStatus().equals("Completed")).count();
        double totalValue = programs.stream().mapToDouble(DonationProgram::getValue).sum();

        System.out.println("Total Programs: " + totalPrograms);
        System.out.println("Active Programs: " + activePrograms);
        System.out.println("Completed Programs: " + completedPrograms);
        System.out.printf("Total Donation Value: $%.2f%n", totalValue);
        System.out.println("=" .repeat(50));
    }
}