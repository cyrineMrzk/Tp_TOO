package bank.application;

import bank.domain.Account;
import bank.domain.Bank;
import bank.domain.BusinessAccount;
import bank.domain.CreditAccount;
import bank.domain.SavingsAccount;
import bank.infrastructure.BankRepository;
import bank.infrastructure.FileBankRepository;
import bank.infrastructure.PersistenceException;
import bank.infrastructure.TextBankSerializer;


import java.nio.file.Path;

/**
 * Programme de démonstration de la persistance UPPA Bank.
 * 
 * Exercice 4 : Intégration et tests
 */
public class PersistenceDemo {
    
    public static void main(String[] args) {
        System.out.println("=== UPPA Bank - Démonstration de Persistance ===\n");
        
        // Créer une banque avec quelques comptes et transactions
        Bank bank = createSampleBank();
        
        System.out.println("1. Banque créée avec " + bank.getAllAccounts().size() + " comptes");
        printBankStatus(bank);
        
        // Créer le repository avec le serializer
        BankRepository repo = new FileBankRepository(
            Path.of("bank.txt"),
            new TextBankSerializer()
        );
        
        // Sauvegarder
        System.out.println("\n2. Sauvegarde dans bank.txt...");
        try {
            repo.save(bank);
            System.out.println("   ✓ Sauvegarde réussie");
        } catch (PersistenceException e) {
            System.err.println("   ✗ Erreur de sauvegarde : " + e.getMessage());
            return;
        }
        
        // Charger
        System.out.println("\n3. Chargement depuis bank.txt...");
        try {
            Bank loadedBank = repo.load();
            System.out.println("   ✓ Chargement réussi");
            System.out.println("   → Banque chargée avec " + 
                loadedBank.getAllAccounts().size() + " comptes");
            
            printBankStatus(loadedBank);
            
            // Vérification
            System.out.println("\n4. Vérification de la cohérence...");
            if (verifyBanks(bank, loadedBank)) {
                System.out.println("   ✓ Les données sont cohérentes");
            } else {
                System.out.println("   ✗ Incohérence détectée !");
            }
            
        } catch (PersistenceException e) {
            System.err.println("   ✗ Erreur de chargement : " + e.getMessage());
        }
        
        System.out.println("\n=== Fin de la démonstration ===");
        System.out.println("Consultez le fichier bank.txt pour voir le format de persistance.");
    }
    
    /**
     * Crée une banque avec des comptes et transactions d'exemple.
     */
    private static Bank createSampleBank() {
        Bank bank = new Bank();
        
        // Compte épargne
        SavingsAccount sa1 = new SavingsAccount("SA-1001", 1000.0, 0.05);
        sa1.deposit(500.0);
        sa1.withdraw(200.0);
        sa1.applyInterest();
        bank.addAccount(sa1);
        
        // Compte crédit
        CreditAccount ca1 = new CreditAccount("CR-2001", 100.0, 500.0);
        ca1.deposit(300.0);
        ca1.withdraw(150.0);
        bank.addAccount(ca1);
        
        // Compte business (si vous l'avez implémenté)
        try {
            BusinessAccount ba1 = new BusinessAccount("BA-3001", 5000.0, 0, 0, "PREMIUM");
            ba1.deposit(1000.0);
            bank.addAccount(ba1);
        } catch (Exception e) {
            // Si BusinessAccount n'existe pas encore, on continue
            System.out.println("   (BusinessAccount non disponible, ignoré)");
        }
        
        return bank;
    }
    
    /**
     * Affiche le statut de tous les comptes.
     */
    private static void printBankStatus(Bank bank) {
        System.out.println("\n   Détail des comptes :");
        for (Account acc : bank.getAllAccounts()) {
            System.out.printf("   - %s : %.2f EUR (%d transactions)%n",
                acc.getAccountNumber(),
                acc.getBalance(),
                acc.history().size()
            );
        }
    }
    
    /**
     * Vérifie que deux banques ont le même état.
     */
    private static boolean verifyBanks(Bank original, Bank loaded) {
        if (original.getAllAccounts().size() != loaded.getAllAccounts().size()) {
            return false;
        }
        
        for (int i = 0; i < original.getAllAccounts().size(); i++) {
            Account origAcc = original.getAllAccounts().get(i);
            Account loadAcc = loaded.getAllAccounts().get(i);
            
            if (!origAcc.getAccountNumber().equals(loadAcc.getAccountNumber())) {
                return false;
            }
            
            if (Math.abs(origAcc.getBalance() - loadAcc.getBalance()) > 0.001) {
                return false;
            }
            
            // Note: On ne vérifie pas les transactions car elles ne sont pas
            // restaurées dans cette version simple
        }
        
        return true;
    }
}