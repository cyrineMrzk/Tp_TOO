package bank;

import bank.application.BankService;
import bank.domain.*;
import bank.infrastructure.InMemoryBankRepository;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test de persistance avec InMemoryBankRepository
 * TP7 - Exercice 3 - Test #4 : sauvegarde puis rechargement conservant l'état
 */
public class PersistenceTest {

    @Test
    void testSaveAndReloadPreservesState() throws Exception {
        // 1. Créer un repository en mémoire
        InMemoryBankRepository repository = new InMemoryBankRepository();
        BankService service = new BankService(repository);
        
        // 2. Créer une banque avec des comptes
        Bank originalBank = new Bank();
        SavingsAccount sa1 = new SavingsAccount("SA-1001", 1000.0, 0.05);
        CreditAccount ca1 = new CreditAccount("CR-2001", 500.0, 1000.0);
        BusinessAccount ba1 = new BusinessAccount("BA-3001", 3000.0, 0, 0, "PREMIUM");
        
        originalBank.addAccount(sa1);
        originalBank.addAccount(ca1);
        originalBank.addAccount(ba1);
        
        // 3. Effectuer des opérations
        sa1.deposit(200.0);
        ca1.withdraw(100.0);
        originalBank.transfer("SA-1001", "CR-2001", 150.0);
        
        // État avant sauvegarde
        double sa1BalanceBefore = sa1.getBalance();
        double ca1BalanceBefore = ca1.getBalance();
        int accountCountBefore = originalBank.getAllAccounts().size();
        
        System.out.println("État avant sauvegarde:");
        System.out.println("  SA-1001: " + sa1BalanceBefore + " EUR");
        System.out.println("  CR-2001: " + ca1BalanceBefore + " EUR");
        System.out.println("  Nombre de comptes: " + accountCountBefore);
        
        // 4. Sauvegarder
        service.saveBank(originalBank);
        
        // 5. Charger
        Bank reloadedBank = service.loadBank();
        
        // 6. Vérifications
        assertNotNull(reloadedBank, "La banque rechargée ne doit pas être null");
        
        // Vérifier le nombre de comptes
        assertEquals(accountCountBefore, reloadedBank.getAllAccounts().size(),
            "Le nombre de comptes doit être préservé");
        
        // Vérifier les comptes individuels
        Account reloadedSa1 = reloadedBank.getAccount("SA-1001");
        Account reloadedCa1 = reloadedBank.getAccount("CR-2001");
        Account reloadedBa1 = reloadedBank.getAccount("BA-3001");
        
        assertNotNull(reloadedSa1, "Le compte SA-1001 doit exister");
        assertNotNull(reloadedCa1, "Le compte CR-2001 doit exister");
        assertNotNull(reloadedBa1, "Le compte BA-3001 doit exister");
        
        // Vérifier les soldes
        assertEquals(sa1BalanceBefore, reloadedSa1.getBalance(), 0.001,
            "Le solde de SA-1001 doit être préservé");
        assertEquals(ca1BalanceBefore, reloadedCa1.getBalance(), 0.001,
            "Le solde de CR-2001 doit être préservé");
        
        // Vérifier les types de comptes
        assertTrue(reloadedSa1 instanceof SavingsAccount,
            "SA-1001 doit rester un SavingsAccount");
        assertTrue(reloadedCa1 instanceof CreditAccount,
            "CR-2001 doit rester un CreditAccount");
        assertTrue(reloadedBa1 instanceof BusinessAccount,
            "BA-3001 doit rester un BusinessAccount");
        
        System.out.println("\nÉtat après rechargement:");
        System.out.println("  SA-1001: " + reloadedSa1.getBalance() + " EUR");
        System.out.println("  CR-2001: " + reloadedCa1.getBalance() + " EUR");
        System.out.println("  ✓ Tous les états ont été préservés");
    }
    
    @Test
    void testMultipleSaveAndLoadCycles() throws Exception {
        InMemoryBankRepository repository = new InMemoryBankRepository();
        BankService service = new BankService(repository);
        
        // Premier cycle
        Bank bank1 = new Bank();
        bank1.addAccount(new SavingsAccount("SA-100", 500.0, 0.02));
        service.saveBank(bank1);
        
        Bank loaded1 = service.loadBank();
        assertEquals(500.0, loaded1.getAccount("SA-100").getBalance(), 0.001);
        
        // Modifier et sauvegarder à nouveau
        loaded1.getAccount("SA-100").deposit(300.0);
        service.saveBank(loaded1);
        
        // Recharger et vérifier
        Bank loaded2 = service.loadBank();
        assertEquals(800.0, loaded2.getAccount("SA-100").getBalance(), 0.001,
            "Les modifications doivent être persistées");
    }
    
    @Test
    void testLoadBeforeSaveReturnsNull() throws Exception {
        InMemoryBankRepository repository = new InMemoryBankRepository();
        BankService service = new BankService(repository);
        
        // Charger sans avoir sauvegardé
        Bank loaded = service.loadBank();
        
        assertNull(loaded, "Le chargement sans sauvegarde préalable doit retourner null");
    }
}