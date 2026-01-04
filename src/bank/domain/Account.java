package bank.domain;

import bank.domain.fees.FeePolicy;
import bank.domain.fees.NoFeePolicy;
import bank.domain.observer.AccountObserver;
import bank.errors.BusinessRuleViolation;
import bank.infrastructure.Logger;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public abstract class Account {
    protected final String accountNumber;
    protected double balance;
    protected final List<Transaction> history = new ArrayList<>();
    
  
    private FeePolicy feePolicy = new NoFeePolicy(); // Par défaut, pas de frais
  
    private List<AccountObserver> observers = new ArrayList<>();
    
    // Logger statique (du TP4)
    private static Logger logger = new Logger();

    protected Account(String accountNumber, double initial) {
        this.accountNumber = accountNumber;
        this.balance = initial;
    }

    // ============================================
    // STRATEGY PATTERN : Getters/Setters
    // ============================================
    public void setFeePolicy(FeePolicy feePolicy) {
        this.feePolicy = feePolicy;
    }
    
    public FeePolicy getFeePolicy() {
        return feePolicy;
    }

    // ============================================
    // OBSERVER PATTERN : Gestion des observateurs
    // ============================================
    public void addObserver(AccountObserver obs) {
        observers.add(obs);
    }
    
    public void removeObserver(AccountObserver obs) {
        observers.remove(obs);
    }
    
    protected void notifyObservers(Transaction tx) {
        for (AccountObserver obs : observers) {
            obs.onTransaction(this, tx);
        }
    }

    // ============================================
    // TEMPLATE METHOD PATTERN : Algorithme de retrait
    // ============================================
    
    /**
     * Template Method : définit l'algorithme de retrait.
     * Cette méthode est finale, elle ne peut pas être overridée.
     */
    public final void withdraw(double amount) {
        // Étape 1 : Vérification commune du montant
        checkAmount(amount);
        
        // Étape 2 : Vérifications spécifiques (hook method)
        checkSpecificRules(amount);
        
        // Étape 3 : Calculer les frais selon la stratégie
        double fee = feePolicy.computeFee(amount);
        
        // Étape 4 : Appliquer le retrait (hook method)
        applyWithdraw(amount, fee);
        
        // Étape 5 : Logger
        logger.logInfo(String.format("Withdraw OK: %.2f from %s (fee: %.2f)", 
            amount, accountNumber, fee));
    }
    
    /**
     * Vérification commune : le montant doit être > 0
     */
    private void checkAmount(double amount) {
        if (amount <= 0) {
            throw new BusinessRuleViolation("montant invalide");
        }
    }
    
    /**
     * Hook method : vérifications spécifiques à chaque type de compte
     * À implémenter dans les sous-classes
     */
    protected abstract void checkSpecificRules(double amount);
    
    /**
     * Hook method : application du retrait spécifique à chaque type de compte
     * À implémenter dans les sous-classes
     */
    protected abstract void applyWithdraw(double amount, double fee);

    // ============================================
    // Méthode deposit (existante, améliorée)
    // ============================================
    public final void deposit(double amount) {
        // PRE: amount > 0, else throw BusinessRuleViolation
        if (amount <= 0) {
            throw new BusinessRuleViolation("montant invalide");
        }
        // POST: balance += amount
        balance += amount;
        Transaction tx = new Transaction(
            TransactionType.DEPOSIT,
            amount,
            balance
        );
        recordTransaction(tx);
        logger.logInfo(String.format("Deposit OK: %.2f to %s", amount, accountNumber));
    }

    // ============================================
    // Getters
    // ============================================
    public final String getAccountNumber() {
        return accountNumber;
    }
    
    public final double getBalance() {
        return balance;
    }
    
    // Pour le rollback dans Bank (TP4)
    public void setBalance(double balance) {
        this.balance = balance;
    }

    // ============================================
    // Gestion de l'historique
    // ============================================
    
    /**
     * Enregistre une transaction et notifie les observateurs
     */
    protected void recordTransaction(Transaction tx) {
        history.add(tx);
        notifyObservers(tx); // ← OBSERVER PATTERN : notifier après ajout
    }
    
    public List<Transaction> history() {
        return new ArrayList<>(history);
    }
    
    // Question bonus : filtre par type de transaction
    public List<Transaction> findByType(TransactionType type) {
        List<Transaction> result = new ArrayList<>();
        for (Transaction tx : history) {
            if (tx.getType() == type) {
                result.add(tx);
            }
        }
        return result;
    }
    
    // Rechercher par intervalle de dates
    public List<Transaction> findByDateRange(LocalDateTime from, LocalDateTime to) {
        List<Transaction> result = new ArrayList<>();
        for (Transaction tx : history) {
            if (!tx.getTimestamp().isBefore(from) && !tx.getTimestamp().isAfter(to)) {
                result.add(tx);
            }
        }
        return result;
    }

    // Trier l'historique
    public List<Transaction> getSortedHistory() {
        List<Transaction> sorted = new ArrayList<>(history);
        sorted.sort((t1, t2) -> t1.getTimestamp().compareTo(t2.getTimestamp()));
        return sorted;
    }
}