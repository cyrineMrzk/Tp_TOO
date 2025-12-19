// bank/DesignPatternsDemo.java
package bank;

import bank.fees.*;
import bank.observer.*;

public class DesignPatternsDemo {
    public static void main(String[] args) {
        System.out.println("=== Test des Design Patterns ===\n");
        
        // Créer un compte - ERREUR 1 FIXÉE: SavingsAccount prend 3 paramètres
        Account acc = new SavingsAccount("SA-001", 1000.0, 0.02);
        
        // === STRATEGY ===
        System.out.println("1. STRATEGY PATTERN");
        
        // Retrait sans frais
        acc.setFeePolicy(new NoFeePolicy());
        acc.withdraw(100.0);
        System.out.println("Solde après retrait sans frais: " + acc.getBalance());
        
        // Retrait avec frais fixes
        acc.setFeePolicy(new FixedFeePolicy(5.0));
        acc.withdraw(100.0);
        System.out.println("Solde après retrait avec frais fixes (5€): " + acc.getBalance());
        
        // Retrait avec frais proportionnels
        acc.setFeePolicy(new PercentageFeePolicy(0.02)); // 2%
        acc.withdraw(100.0);
        System.out.println("Solde après retrait avec frais 2%: " + acc.getBalance());
        
        // === TEMPLATE METHOD ===
        System.out.println("\n2. TEMPLATE METHOD PATTERN");
        
        // ERREUR 2 FIXÉE: SavingsAccount prend 3 paramètres
        Account savings = new SavingsAccount("SA-002", 100.0, 0.01);
        Account credit = new CreditAccount("CA-001", 100.0, 500.0);
        
        System.out.println("SavingsAccount ne permet pas le découvert:");
        try {
            savings.withdraw(200.0);
        } catch (BusinessRuleViolation e) {
            System.out.println("  Erreur: " + e.getMessage());
        }
        
        System.out.println("CreditAccount permet le découvert:");
        credit.withdraw(200.0);
        System.out.println("  Solde: " + credit.getBalance());
        
        // === OBSERVER ===
        System.out.println("\n3. OBSERVER PATTERN");
        
        // ERREUR 3 FIXÉE: SavingsAccount prend 3 paramètres
        Account observed = new SavingsAccount("SA-003", 500.0, 0.015);
        
        // Ajouter un observateur d'audit
        AuditService audit = new AuditService();
        observed.addObserver(audit);
        
        // Ajouter un observateur qui affiche dans la console
        observed.addObserver(new AccountObserver() {
            @Override
            public void onTransaction(Account a, bank.tx.Transaction tx) {
                System.out.println("  [Observer] Transaction: " + tx.getType() + 
                    " de " + tx.getAmount() + "€");
            }
        });
        
        observed.deposit(200.0);
        observed.withdraw(50.0);
        System.out.println("✓ Transactions enregistrées dans audit.log");
    }
}