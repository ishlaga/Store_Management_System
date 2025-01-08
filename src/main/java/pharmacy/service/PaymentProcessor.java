package pharmacy.service;

/**
 * Service class for processing various types of payments in the pharmacy.
 * Handles insurance claims, cash transactions, and card payments.
 *
 * @author Hrishikesha Kyathsandra
 */
public class PaymentProcessor {
    public boolean processPayment(String paymentMethod, double amount, String... details) {
        System.out.printf("\nProcessing %s payment of $%.2f\n", paymentMethod, amount);
        
        switch (paymentMethod) {
            case "INSURANCE":
                if (details.length >= 2) {
                    String memberId = details[0];
                    String medication = details[1];
                    System.out.println("Verifying insurance coverage for member: " + memberId);
                    System.out.println("Checking coverage for: " + medication);
                    
                    try {
                        Thread.sleep(2000);  // Simulate verification delay
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    
                    if (memberId.length() >= 8) {
                        System.out.println("Insurance coverage verified");
                        System.out.printf("Covered amount: $%.2f%n", amount);
                        return true;
                    }
                    System.out.println("Insurance verification failed");
                }
                return false;
                
            case "CASH":
                System.out.printf("Cash payment received: $%.2f%n", amount);
                return true;
                
            case "CARD":
                if (details.length >= 3) {
                    String cardNumber = details[0];
                    String expDate = details[1];
                    String cvv = details[2];
                    
                    boolean isValid = cardNumber.length() >= 15 && 
                                    cardNumber.length() <= 16 &&
                                    expDate.matches("\\d{2}/\\d{2}") &&
                                    cvv.length() >= 3;
                    
                    if (isValid) {
                        System.out.printf("Card payment processed: $%.2f%n", amount);
                        return true;
                    }
                    System.out.println("Invalid card details");
                }
                return false;
                
            default:
                return false;
        }
    }
}