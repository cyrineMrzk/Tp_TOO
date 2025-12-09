package bank;

import bank.tx.Transaction;
import bank.tx.TransactionType;

public class BusinessAccount extends Account {
    private final double creditLimit;
    private final double interestRate;

    public BusinessAccount(String id, double initial, double creditLimit, double interestRate) {
        super(id, initial);
        this.creditLimit = creditLimit;
        this.interestRate = interestRate;
    }

    @Override
    public void withdraw(double amount) {
        if (amount <= 0 || balance - amount < -creditLimit) {
            throw new BusinessRuleViolation("withdraw amount must be > 0 and >= -creditLimit");
        }
        balance -= amount;
        recordTransaction(new Transaction(TransactionType.WITHDRAW, amount, balance));
    }

    public void applyInterest() {
        if (balance > 0) {
            double interest = balance * interestRate;
            balance += interest;
            recordTransaction(new Transaction(TransactionType.INTEREST, interest, balance));
        }
    }
}
