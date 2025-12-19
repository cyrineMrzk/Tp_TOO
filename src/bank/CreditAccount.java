package bank;

import bank.tx.Transaction;
import bank.tx.TransactionType;

public class CreditAccount extends Account {
    private final double creditLimit;

    public CreditAccount(String accountNumber, double initial, double creditLimit) {
        super(accountNumber, initial);
        this.creditLimit = creditLimit;
    }

    public double getCreditLimit() {
        return creditLimit;
    }

    // ============================================
    // TEMPLATE METHOD PATTERN : Hook Methods
    // ============================================
    
    @Override
    protected void checkSpecificRules(double amount) {
        // Calculer le solde après retrait
        double potentialBalance = balance - amount;
        
        // Calculer les frais potentiels selon la stratégie (Strategy Pattern)
        double fee = 0.0;
        if (potentialBalance < 0) {
            fee = getFeePolicy().computeFee(amount);
        }
        
        // Vérifier que le découvert + frais ne dépasse pas la limite de crédit
        if (potentialBalance - fee < -creditLimit) {
            throw new BusinessRuleViolation(
                "withdraw amount must be > 0 and <= balance + creditLimit");
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
}