package bank.domain;

import bank.errors.BusinessRuleViolation;

public class SavingsAccount extends Account {
    private final double interestRate;

    public SavingsAccount(String accountNumber, double initial, double interestRate) {
        super(accountNumber, initial);
        this.interestRate = interestRate;
    }

    // ============================================
    // TEMPLATE METHOD PATTERN : Hook Methods
    // ============================================
    
    @Override
    protected void checkSpecificRules(double amount) {
        // Règle spécifique : pas de découvert autorisé
        if (balance - amount < 0) {
            throw new BusinessRuleViolation(
                "withdraw amount must be > 0 and <= balance");
        }
    }
    
    @Override
    protected void applyWithdraw(double amount, double fee) {
        // Appliquer le retrait
        balance -= amount;
        recordTransaction(new Transaction(TransactionType.WITHDRAW, amount, balance));
        
        // Appliquer les frais si nécessaire
        if (fee > 0) {
            balance -= fee;
            recordTransaction(new Transaction(TransactionType.FEE, fee, balance));
        }
    }

    // ============================================
    // Méthode spécifique : Intérêts
    // ============================================
    
    public void applyInterest() {
        double interest = balance * interestRate;
        
        if (interest < 0) {
            throw new BusinessRuleViolation("Interest cannot be negative");
        }
        
        balance += interest;
        
        Transaction tx = new Transaction(
            TransactionType.INTEREST,
            interest,
            balance
        );
        
        recordTransaction(tx);
    }

    // ============================================
    // Bonus : Résumé des intérêts cumulés
    // ============================================
    
    public double getTotalInterestEarned() {
        double totalInterest = 0.0;
        for (Transaction tx : history()) {
            if (tx.getType() == TransactionType.INTEREST) {
                totalInterest += tx.getAmount();
            }
        }
        return totalInterest;
    }
    
    public double getInterestRate() {
        return interestRate;
    }
}