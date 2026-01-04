package bank.application;

import bank.domain.*;
import bank.infrastructure.*;
import bank.infrastructure.PersistenceException;
import java.nio.file.Path;

/**
 * Programme principal de démonstration UPPA Bank
 * TP7 - Exercice 4 : Point d'entrée et démonstration
 * 
 * Démontre :
 * - Création d'une banque
 * - Ajout de comptes
 * - Opérations (dépôts, retraits, transferts)
 * - Sauvegarde puis rechargement
 * - Affichage de l'état final
 */
public class Main {
    
    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════════╗");
        System.out.println("║   UPPA BANK - Système de Gestion Bancaire    ║");
        System.out.println("╚═══════════════════════════════════════════════╝\n");
        
        // ══════════════════════════════════════════════
        // 1. CRÉATION DE LA BANQUE
        // ══════════════════════════════════════════════
        System.out.println("📋 Étape 1 : Création de la banque");
        System.out.println("─────────────────────────────────────────────");
        
        Bank bank = new Bank();
        System.out.println("✓ Banque créée\n");
        
        // ══════════════════════════════════════════════
        // 2. AJOUT DE COMPTES
        // ══════════════════════════════════════════════
        System.out.println("👥 Étape 2 : Ajout des comptes");
        System.out.println("─────────────────────────────────────────────");
        
        SavingsAccount savings = new SavingsAccount("SA-1001", 1000.0, 0.05);
        CreditAccount credit = new CreditAccount("CR-2001", 500.0, 1000.0);
        BusinessAccount business = new BusinessAccount("BA-3001", 5000.0, 0, 0, "PREMIUM");
        
        bank.addAccount(savings);
        bank.addAccount(credit);
        bank.addAccount(business);
        
        System.out.println("✓ Compte Épargne    [SA-1001] : 1000.00 EUR (taux: 5%)");
        System.out.println("✓ Compte Crédit     [CR-2001] :  500.00 EUR (limite: 1000 EUR)");
        System.out.println("✓ Compte Business   [BA-3001] : 5000.00 EUR (type: PREMIUM)");
        System.out.println("→ Total : " + bank.getAllAccounts().size() + " comptes créés\n");
        
        // ══════════════════════════════════════════════
        // 3. OPÉRATIONS BANCAIRES
        // ══════════════════════════════════════════════
        System.out.println("💰 Étape 3 : Opérations bancaires");
        System.out.println("─────────────────────────────────────────────");
        
        try {
            // Dépôts
            System.out.println("Dépôts :");
            savings.deposit(200.0);
            System.out.println("  ✓ +200.00 EUR → SA-1001");
            
            credit.deposit(300.0);
            System.out.println("  ✓ +300.00 EUR → CR-2001");
            
            // Retraits
            System.out.println("\nRetraits :");
            business.withdraw(500.0);
            System.out.println("  ✓ -500.00 EUR → BA-3001");
            
            // Application des intérêts
            System.out.println("\nIntérêts :");
            savings.applyInterest();
            System.out.println("  ✓ Intérêts appliqués sur SA-1001");
            
            // Transfert
            System.out.println("\nTransferts :");
            bank.transfer("SA-1001", "CR-2001", 150.0);
            System.out.println("  ✓ 150.00 EUR : SA-1001 → CR-2001");
            
        } catch (Exception e) {
            System.err.println("✗ Erreur : " + e.getMessage());
        }
        
        System.out.println("\n→ Toutes les opérations effectuées avec succès\n");
        
        // Affichage de l'état actuel
        System.out.println("📊 État actuel des comptes :");
        System.out.println("─────────────────────────────────────────────");
        printBankState(bank);
        
        // ══════════════════════════════════════════════
        // 4. SAUVEGARDE
        // ══════════════════════════════════════════════
        System.out.println("\n💾 Étape 4 : Sauvegarde de la banque");
        System.out.println("─────────────────────────────────────────────");
        
        BankRepository repository = new FileBankRepository(
            Path.of("bank.txt"),
            new TextBankSerializer()
        );
        
        try {
            repository.save(bank);
            System.out.println("✓ Banque sauvegardée dans 'bank.txt'");
            System.out.println("  → " + bank.getAllAccounts().size() + " comptes enregistrés\n");
        } catch (PersistenceException e) {
            System.err.println("✗ Erreur de sauvegarde : " + e.getMessage());
            return;
        }
        
        // ══════════════════════════════════════════════
        // 5. RECHARGEMENT
        // ══════════════════════════════════════════════
        System.out.println("📂 Étape 5 : Rechargement de la banque");
        System.out.println("─────────────────────────────────────────────");
        
        try {
            Bank loadedBank = repository.load();
            System.out.println("✓ Banque rechargée depuis 'bank.txt'");
            System.out.println("  → " + loadedBank.getAllAccounts().size() + " comptes restaurés\n");
            
            // ══════════════════════════════════════════════
            // 6. AFFICHAGE DE L'ÉTAT FINAL
            // ══════════════════════════════════════════════
            System.out.println("✅ Étape 6 : État final (après rechargement)");
            System.out.println("─────────────────────────────────────────────");
            printBankState(loadedBank);
            
            // ══════════════════════════════════════════════
            // 7. VÉRIFICATION DE LA COHÉRENCE
            // ══════════════════════════════════════════════
            System.out.println("\n🔍 Étape 7 : Vérification de la cohérence");
            System.out.println("─────────────────────────────────────────────");
            
            if (verifyBanks(bank, loadedBank)) {
                System.out.println("✓ Les données sont cohérentes");
                System.out.println("  → Tous les soldes correspondent");
                System.out.println("  → Tous les comptes sont présents");
            } else {
                System.out.println("✗ Incohérence détectée !");
            }
            
        } catch (PersistenceException e) {
            System.err.println("✗ Erreur de chargement : " + e.getMessage());
            return;
        }
        
        // ══════════════════════════════════════════════
        // FIN
        // ══════════════════════════════════════════════
        System.out.println("\n╔═══════════════════════════════════════════════╗");
        System.out.println("║          Démonstration terminée ! ✓           ║");
        System.out.println("╚═══════════════════════════════════════════════╝");
        System.out.println("\n📄 Consultez 'bank.txt' pour voir le format de persistance");
    }
    
    /**
     * Affiche l'état de tous les comptes de la banque
     */
    private static void printBankState(Bank bank) {
        for (Account acc : bank.getAllAccounts()) {
            String type = acc.getClass().getSimpleName();
            System.out.printf("  [%s] %-15s : %10.2f EUR (%d tx)%n",
                type.substring(0, Math.min(3, type.length())).toUpperCase(),
                acc.getAccountNumber(),
                acc.getBalance(),
                acc.history().size()
            );
        }
    }
    
    /**
     * Vérifie que deux banques ont le même état
     */
    private static boolean verifyBanks(Bank original, Bank loaded) {
        if (original.getAllAccounts().size() != loaded.getAllAccounts().size()) {
            return false;
        }
        
        for (Account origAcc : original.getAllAccounts()) {
            Account loadAcc = loaded.getAccount(origAcc.getAccountNumber());
            
            if (loadAcc == null) {
                return false;
            }
            
            if (Math.abs(origAcc.getBalance() - loadAcc.getBalance()) > 0.001) {
                return false;
            }
        }
        
        return true;
    }
}