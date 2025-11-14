package bank;

public class CreditAccount extends Account {
    private final double creditLimit;

    public CreditAccount(String id, double initial, double creditLimit) {
        super(id, initial);
        this.creditLimit = creditLimit;
    }

    @Override
    public void withdraw(double amount) {
        if (amount <= 0 || amount > balance + creditLimit) {
            throw new BusinessRuleViolation("withdraw amount must be > 0 and <= balance + creditLimit");
        }
        balance -= amount;
    }
}