package training;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class TrainingSystem {
    private static List<Trainee> trainees = new ArrayList<>();
    private static List<Trainer> trainers = new ArrayList<>();
    private static List<TrainingModule> trainingModules = new ArrayList<>();
    private static Scanner scanner = new Scanner(System.in);
    private static Trainee currentTrainee; // To hold the current trainee's information

    public static void main(String[] args) {
        initializeData(); // Initialize some data for demonstration
        while (true) {
            System.out.println("Welcome to the Online Training and Development System");
            System.out.println("1. HR Manager Tasks");
            System.out.println("2. Trainee Tasks");
            System.out.println("3. Trainer Tasks");
            System.out.println("4. Exit");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    hrManagerTasks();
                    break;
                case 2:
                    traineeTasks();
                    break;
                case 3:
                    trainerTasks();
                    break;
                case 4:
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private static void hrManagerTasks() {
        System.out.println("HR Manager Tasks:");
        System.out.println("1. Create Training Module");
        System.out.println("2. Assign Trainer to Module");
        int taskChoice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        switch (taskChoice) {
            case 1:
                createTrainingModule();
                break;
            case 2:
                assignTrainer();
                break;
            default:
                System.out.println("Invalid task choice.");
        }
    }

    private static void createTrainingModule() {
        System.out.print("Enter module name: ");
        String moduleName = scanner.nextLine();
        TrainingModule module = new TrainingModule(moduleName);
        trainingModules.add(module);
        System.out.println("Training module '" + moduleName + "' created successfully.");
    }

    private static void assignTrainer() {
        System.out.print("Enter trainer name: ");
        String trainerName = scanner.nextLine();
        Trainer trainer = new Trainer(trainerName);
        trainers.add(trainer);
        System.out.println("Trainer '" + trainerName + "' assigned successfully.");
    }

    private static void traineeTasks() {
        System.out.print("Enter your name: ");
        String traineeName = scanner.nextLine();
        System.out.print("Enter your email: ");
        String traineeEmail = scanner.nextLine();
        
        currentTrainee = new Trainee(traineeName, traineeEmail); // Create a new Trainee object

        System.out.println("Trainee Tasks:");
        System.out.println("1. View Available Trainings");
        System.out.println("2. Complete Assignment");
        int taskChoice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        switch (taskChoice) {
            case 1:
                viewAvailableTrainings();
                break;
            case 2:
                completeAssignment();
                break;
            default:
                System.out.println("Invalid task choice.");
        }
    }

    private static void viewAvailableTrainings() {
        System.out.println("Available Trainings:");
        for (TrainingModule module : trainingModules) {
            System.out.println("- " + module.getName());
        }
    }

    private static void completeAssignment() {
        System.out.print("Enter module name to complete assignment: ");
        String moduleName = scanner.nextLine();

        // Check if the module exists in the available trainings
        boolean moduleExists = false;
        for (TrainingModule module : trainingModules) {
            if (module.getName().equalsIgnoreCase(moduleName)) {
                moduleExists = true;
                break;
            }
        }

        if (!moduleExists) {
            System.out.println("The training module '" + moduleName + "' is not available.");
            return; // Exit the method if the module is not found
        }

        // Ask if the trainee wants to review the module notes
        System.out.print("Do you want to review the module notes? (yes/no): ");
        String reviewChoice = scanner.nextLine();

        if (reviewChoice.equalsIgnoreCase("yes")) {
            // Here you can add logic to display the module notes
            System.out.println("Reviewing module notes for: " + moduleName);
            // For example, you could print out some notes or information about the module
            System.out.println("Module Notes: [Insert relevant notes here]");
        }

        // Proceed to the quiz regardless of whether they reviewed the notes or not
        if (takeQuiz()) {
            String status = "Completed";
            String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            logAssignment(currentTrainee.getName(), moduleName, date, status); // Use the current trainee's name
            System.out.println("Assignment for module '" + moduleName + "' completed successfully.");
        } else {
            System.out.println("You did not pass the quiz. Please prepare and try again.");
        }
    }

    private static boolean takeQuiz() {
        // Sample questions and answers relevant to store employees
        String[] questions = {
            "What is the primary goal of customer service?",
            "Which of the following is a method to handle customer complaints?",
            "What should you do if you don't know the answer to a customer's question?"
        };

        String[][] options = {
            {"A. To sell more products", "B. To ensure customer satisfaction", "C. To reduce costs", "D. To increase inventory"},
            {"A. Ignore the complaint", "B. Listen and empathize", "C. Blame the customer", "D. Escalate immediately"},
            {"A. Guess the answer", "B. Tell them you don't know", "C. Make up an answer", "D. Refer them to a colleague"}
        };

        char[] answers = {'B', 'B', 'D'}; // Correct answers
        int score = 0;

        for (int i = 0; i < questions.length; i++) {
            System.out.println(questions[i]);
            for (String option : options[i]) {
                System.out.println(option);
            }
            System.out.print("Your answer: ");
            char userAnswer = scanner.nextLine().charAt(0);
            if (userAnswer == answers[i]) {
                score++;
            }
        }

        System.out.println("Your score: " + score + "/" + questions.length);
        return score >= 2; // Pass if score is 2 or more
    }

    private static void logAssignment(String traineeName, String moduleName, String date, String status) {
        String fileName = "assignments_log.txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true))) {
            writer.write("Trainee Name: " + traineeName);
            writer.write(", Module Name: " + moduleName);
            writer.write(", Date: " + date);
            writer.write(", Status: " + status);
            writer.newLine();
            System.out.println("Assignment logged successfully.");
        } catch (IOException e) {
            System.out.println("An error occurred while logging the assignment: " + e.getMessage());
        }
    }

    private static void trainerTasks() {
        System.out.println("Trainer Tasks:");
        System.out.println("1. View Assigned Trainees");
        System.out.println("2. Review Trainee Assignments");
        int taskChoice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        switch (taskChoice) {
            case 1:
                viewAssignedTrainees();
                break;
            case 2:
                reviewTraineeAssignments();
                break;
            default:
                System.out.println("Invalid task choice.");
        }
    }

    private static void viewAssignedTrainees() {
        System.out.println("Assigned Trainees:");
        for (Trainee trainee : trainees) {
            System.out.println("- " + trainee.getName());
        }
    }

    private static void reviewTraineeAssignments() {
        System.out.print("Enter trainee name to review assignments: ");
        String traineeName = scanner.nextLine();
        String fileName = "assignments_log.txt";

        try (Scanner fileScanner = new Scanner(new FileReader(fileName))) {
            boolean found = false;
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                if (line.contains("Trainee Name: " + traineeName)) {
                    System.out.println(line);
                    found = true;
                }
            }
            if (!found) {
                System.out.println("No assignments found for trainee: " + traineeName);
            }
        } catch (IOException e) {
            System.out.println("An error occurred while reading the assignments: " + e.getMessage());
        }
    }

    private static void initializeData() {
        // Initialize some sample data relevant to store employees
        trainees.add(new Trainee("John Doe", "john@example.com"));
        trainees.add(new Trainee("Alice Smith", "alice@example.com"));
        trainers.add(new Trainer("Jane Smith"));
        trainingModules.add(new TrainingModule("Customer Service Basics"));
        trainingModules.add(new TrainingModule("Inventory Management"));
        trainingModules.add(new TrainingModule("Sales Techniques"));
    }
}