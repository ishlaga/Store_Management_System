package payment.service;
/**
 * Service class for handling payment processing across the system.
 * Manages different payment methods and transaction verification.
 *
 * @author Hrishikesha Kyathsandra
 */
public class PaymentProcessor {
    public boolean processCardPayment(String cardNumber, double amount) {
        // Simplified payment processing
        return cardNumber != null && cardNumber.length() >= 16;
    }
}