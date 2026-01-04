package bank.domain;

import bank.errors.BusinessRuleViolation;

public class BusinessAccount extends Account {
    private final double creditLimit;
    private final double interestRate;
     private String tier;

    public BusinessAccount(String accountNumber, double initial, double creditLimit, double interestRate, String tier) {
        super(accountNumber, initial);
        this.creditLimit = creditLimit;
        this.interestRate = interestRate;
        this.tier = tier;
    }

    public BusinessAccount(String accountNumber, double balance, String tier) {
    this(
        accountNumber,
        balance,
        0.0,      // creditLimit par défaut
        0.0,      // interestRate par défaut
        tier
    );
}

     public String getTier() {
        return tier;
    }
    public double getCreditLimit() {
        return creditLimit;
    }

    public double getInterestRate() {
        return interestRate;
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
                "withdraw amount must be > 0 and >= -creditLimit");
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
    // Méthode spécifique : Intérêts (seulement si solde positif)
    // ============================================
    
    public void applyInterest() {
        if (balance > 0) {
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
}