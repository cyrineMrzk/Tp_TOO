// test/bank/ObserverPatternTest.java
package bank;

import bank.domain.Account;
import bank.domain.SavingsAccount;
import bank.domain.Transaction;
import bank.domain.TransactionType;
import bank.domain.observer.*;
import bank.errors.BusinessRuleViolation;

import org.junit.jupiter.api.*;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class ObserverPatternTest {
    
    private static final Path AUDIT_FILE = Path.of("audit.log");
    
    @BeforeEach
    void setUp() throws IOException {
        Files.deleteIfExists(AUDIT_FILE);
    }
    
    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(AUDIT_FILE);
    }
    
    @Test
    void testAuditServiceObservesWithdraw() throws IOException {
        Account acc = new SavingsAccount("SA-001", 1000.0, 0.02);
        AuditService audit = new AuditService();
        
        acc.addObserver(audit);
        acc.withdraw(100.0);
        
        assertTrue(Files.exists(AUDIT_FILE));
        List<String> lines = Files.readAllLines(AUDIT_FILE);
        
        assertEquals(1, lines.size());
        assertTrue(lines.get(0).contains("AUDIT"));
        assertTrue(lines.get(0).contains("SA-001"));
        assertTrue(lines.get(0).contains("WITHDRAW"));
    }
    
    @Test
void testAuditServiceObservesDeposit() throws IOException {
    Account acc = new SavingsAccount("SA-002", 1000.0, 0.02);
    AuditService audit = new AuditService();

    // Ajouter l'observateur
    acc.addObserver(audit);
    acc.deposit(200.0);

    // Vérifie que le fichier existe
    assertTrue(Files.exists(AUDIT_FILE), "Audit file should exist after deposit");

    // Lecture des lignes
    List<String> lines = Files.readAllLines(AUDIT_FILE);

    // Il doit y avoir exactement une ligne
    assertEquals(1, lines.size(), "Audit file should contain exactly one line");

    String line = lines.get(0);

    // Vérifie que la ligne contient DEPOSIT et le bon ID
    assertTrue(line.contains("DEPOSIT"), "Line should contain DEPOSIT");
    assertTrue(line.contains("SA-002"), "Line should contain account ID SA-002");

    // Vérifie que le montant est correct en utilisant Locale.US pour le format
    String expectedAmount = String.format(java.util.Locale.US, "%.2f", 200.0);
    assertTrue(line.contains(expectedAmount), 
               "Line should contain the correct amount " + expectedAmount);

    // Vérifie le format du timestamp au début de la ligne
    assertTrue(line.matches("^\\[\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\].*"), 
               "Line should start with timestamp");
}

    
    @Test
    void testMultipleObservers() {
        Account acc = new SavingsAccount("SA-003", 1000.0, 0.02);
        
        class CountingObserver implements AccountObserver {
            int count = 0;
            @Override
            public void onTransaction(Account a, Transaction tx) {
                count++;
            }
        }
        
        CountingObserver counter1 = new CountingObserver();
        CountingObserver counter2 = new CountingObserver();
        
        acc.addObserver(counter1);
        acc.addObserver(counter2);
        
        acc.withdraw(100.0);
        
        // Les deux observateurs doivent être notifiés
        assertEquals(1, counter1.count);
        assertEquals(1, counter2.count);
    }
    
    @Test
    void testObserverReceivesCorrectTransactionInfo() {
        Account acc = new SavingsAccount("SA-004", 1000.0, 0.02);
        
        class CapturingObserver implements AccountObserver {
            Transaction lastTransaction;
            Account lastAccount;
            
            @Override
            public void onTransaction(Account a, Transaction tx) {
                lastAccount = a;
                lastTransaction = tx;
            }
        }
        
        CapturingObserver observer = new CapturingObserver();
        acc.addObserver(observer);
        
        acc.withdraw(150.0);
        
        assertNotNull(observer.lastTransaction);
        assertEquals(TransactionType.WITHDRAW, observer.lastTransaction.getType());
        assertEquals(150.0, observer.lastTransaction.getAmount(), 0.001);
        assertEquals(850.0, observer.lastTransaction.getBalanceAfter(), 0.001);
        assertEquals(acc, observer.lastAccount);
    }
    
    @Test
    void testRemoveObserver() {
        Account acc = new SavingsAccount("SA-005", 1000.0, 0.02);
        
        class CountingObserver implements AccountObserver {
            int count = 0;
            @Override
            public void onTransaction(Account a, Transaction tx) {
                count++;
            }
        }
        
        CountingObserver observer = new CountingObserver();
        acc.addObserver(observer);
        
        acc.withdraw(100.0);
        assertEquals(1, observer.count);
        
        // Retirer l'observateur
        acc.removeObserver(observer);
        
        acc.withdraw(50.0);
        // Le compteur ne doit pas augmenter
        assertEquals(1, observer.count);
    }
    
    @Test
    void testObserverNotifiedForFeeTransaction() throws IOException {
        Account acc = new SavingsAccount("SA-006", 1000.0, 0.02);
        acc.setFeePolicy(new bank.domain.fees.FixedFeePolicy(5.0));
        
        AuditService audit = new AuditService();
        acc.addObserver(audit);
        
        acc.withdraw(100.0);
        
        // Deux transactions : WITHDRAW + FEE
        List<String> lines = Files.readAllLines(AUDIT_FILE);
        assertEquals(2, lines.size());
        
        assertTrue(lines.get(0).contains("WITHDRAW"));
        assertTrue(lines.get(1).contains("FEE"));
    }
    
    @Test
    void testObserverNotNotifiedWhenTransactionFails() {
        Account acc = new SavingsAccount("SA-007", 100.0, 0.02);
        
        class CountingObserver implements AccountObserver {
            int count = 0;
            @Override
            public void onTransaction(Account a, Transaction tx) {
                count++;
            }
        }
        
        CountingObserver observer = new CountingObserver();
        acc.addObserver(observer);
        
        // Tentative de retrait qui échoue
        try {
            acc.withdraw(200.0); // Découvert non autorisé
        } catch (BusinessRuleViolation e) {
            // Exception attendue
        }
        
        // Aucune notification ne doit avoir été envoyée
        assertEquals(0, observer.count);
    }

    
}