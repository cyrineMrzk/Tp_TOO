package bank;
import bank.tx.Transaction;
import bank.tx.TransactionType;
public class SavingsAccount extends Account {
    private final double interestRate;

    public SavingsAccount(String id, double initial, double interestRate) {
        super(id, initial);
        this.interestRate = interestRate;
    }

    public void applyInterest() {
        double interest = balance * interestRate;
        double newBalance = balance + interest; 
        if (interest < 0) {
            throw new BusinessRuleViolation("Interest cannot be negative");
        }
        balance += interest;
        Transaction tx = new Transaction(
                TransactionType.INTEREST,
                interest,
                newBalance
        );

       recordTransaction(tx);

    }

    @Override
    public void withdraw(double amount) {
         if (amount <= 0) {
        throw new BusinessRuleViolation("montant invalide");
    }
       if (amount > balance) {
        throw new BusinessRuleViolation("insufficient balance");
    }
         balance -= amount;
         recordTransaction(new Transaction(
        TransactionType.WITHDRAW,
        amount,
        balance
    )); }
    //bonus question : resume des interets cumules 
    public double getTotalInterestEarned() {
        double totalInterest = 0.0;
        for (Transaction tx : history) {
            if (tx.getType() == TransactionType.INTEREST) {
                totalInterest += tx.getAmount();
            }
        }
        return totalInterest;
    }
    
}
