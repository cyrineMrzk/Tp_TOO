package bank;

public class SavingsAccount extends Account {
    private final double interestRate;

    public SavingsAccount(String id, double initial, double interestRate) {
        super(id, initial);
        this.interestRate = interestRate;
    }
    public void applyInterest() {
        balance += balance * interestRate;
    }
    @Override
    public void withdraw(double amount) {
        if (amount <= 0 || amount > balance) {
            throw new BusinessRuleViolation("withdraw amount must be > 0 and <= balance");
        }
        balance -= amount;
    }

}
