package bank;
import bank.tx.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class BankTransferTest {

    @Test
    void testSuccessfulTransfer() {
        // Préparation de la banque
        Bank bank = new Bank();

        Account from = new SavingsAccount("SA-1", 200.0, 0.01);
        Account to   = new CreditAccount("CA-1", 100.0, 500.0);

        bank.addAccount(from);
        bank.addAccount(to);

        // Transfert réussi
        bank.transfer("SA-1", "CA-1", 50.0);

        // 1. Vérification des soldes
        assertEquals(150.0, from.getBalance(), 0.001);
        assertEquals(150.0, to.getBalance(), 0.001);

        // 2. Vérification des transactions
        assertEquals(TransactionType.TRANSFER_OUT,
            from.history().get(from.history().size()-1).getType());

        assertEquals(TransactionType.TRANSFER_IN,
            to.history().get(to.history().size()-1).getType());

        // 3. Vérification du nombre total
        assertEquals(1, from.findByType(TransactionType.TRANSFER_OUT).size());
        assertEquals(1, to.findByType(TransactionType.TRANSFER_IN).size());
    }

    @Test
    void testTransferFailsDueToInsufficientFunds() {
        Bank bank = new Bank();

        Account from = new SavingsAccount("SA-2", 50.0, 0.01);
        Account to   = new CreditAccount("CA-2", 100.0, 500.0);

        bank.addAccount(from);
        bank.addAccount(to);

        // Amount trop élevé
        Exception ex = assertThrows(BusinessRuleViolation.class, () -> {
            bank.transfer("SA-2", "CA-2", 100.0);
        });

        assertEquals("Solde insuffisant pour le transfert", ex.getMessage());

        // Soldes inchangés
        assertEquals(50.0, from.getBalance(), 0.001);
        assertEquals(100.0, to.getBalance(), 0.001);

        // Pas de transaction
        assertTrue(from.findByType(TransactionType.TRANSFER_OUT).isEmpty());
        assertTrue(to.findByType(TransactionType.TRANSFER_IN).isEmpty());
    }

    @Test
    void testTransferFailsDueToNonexistentDestination() {
        Bank bank = new Bank();

        Account from = new SavingsAccount("SA-3", 200.0, 0.01);
        bank.addAccount(from);

        // Compte destination inexistant
        Exception ex = assertThrows(BusinessRuleViolation.class, () -> {
            bank.transfer("SA-3", "UNKNOWN", 50.0);
        });

        assertEquals("Compte introuvable", ex.getMessage());

        // Solde inchangé
        assertEquals(200.0, from.getBalance(), 0.001);

        // Pas de transaction
        assertTrue(from.findByType(TransactionType.TRANSFER_OUT).isEmpty());
    }
}
