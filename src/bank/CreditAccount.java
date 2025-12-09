package bank;
import bank.tx.Transaction;
import bank.tx.TransactionType;

public class CreditAccount extends Account {
    private final double creditLimit;

    public CreditAccount(String id, double initial, double creditLimit) {
        super(id, initial);
        this.creditLimit = creditLimit;
    }
@Override
public void withdraw(double amount) {
    if (amount <= 0) {
        throw new BusinessRuleViolation("withdraw amount must be > 0 and <= balance + creditLimit");
    }
    
    double potentialBalance = balance - amount;
    
    // Determine if fee applies
    double fee = 0.0;
    if (potentialBalance < 0) {
        fee = 5.0;
    }
    
    // Check if withdrawal + potential fee exceeds limit (use <= instead of <)
    if (potentialBalance - fee < -creditLimit) {
        throw new BusinessRuleViolation("withdraw amount must be > 0 and <= balance + creditLimit");
    }
    
    // Apply withdraw
    balance = potentialBalance;
    recordTransaction(new Transaction(TransactionType.WITHDRAW, amount, balance));
    
    // Apply fee if needed
    if (fee > 0) {
        balance -= fee;
        recordTransaction(new Transaction(TransactionType.FEE, fee, balance));
    }
}
}