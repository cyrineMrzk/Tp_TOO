package bank.errors;

public class BusinessRuleViolation extends RuntimeException {
    public BusinessRuleViolation(String message) {
        super(message);
    }
}
