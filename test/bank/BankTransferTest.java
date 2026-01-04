package bank;

import bank.domain.Account;
import bank.domain.Bank;
import bank.domain.CreditAccount;
import bank.domain.SavingsAccount;
import bank.domain.TransactionType;
import bank.errors.BusinessRuleViolation;
import bank.errors.UnknownAccountException;
import bank.errors.TransferException;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class BankTransferTest {

    @Test
    void testSuccessfulTransfer() throws Exception {
        Bank bank = new Bank();

        Account from = new SavingsAccount("SA-1", 200.0, 0.01);
        Account to   = new CreditAccount("CA-1", 100.0, 500.0);

        bank.addAccount(from);
        bank.addAccount(to);

        bank.transfer("SA-1", "CA-1", 50.0);

        assertEquals(150.0, from.getBalance(), 0.001);
        assertEquals(150.0, to.getBalance(), 0.001);

        assertEquals(TransactionType.WITHDRAW,
            from.history().get(from.history().size() - 1).getType());

        assertEquals(TransactionType.DEPOSIT,
            to.history().get(to.history().size() - 1).getType());

        assertEquals(1, from.findByType(TransactionType.WITHDRAW).size());
        assertEquals(1, to.findByType(TransactionType.DEPOSIT).size());
    }

    @Test
    void testTransferFailsDueToInsufficientFunds() {
        Bank bank = new Bank();

        Account from = new SavingsAccount("SA-2", 50.0, 0.01);
        Account to   = new CreditAccount("CA-2", 100.0, 500.0);

        bank.addAccount(from);
        bank.addAccount(to);

        TransferException ex = assertThrows(TransferException.class, () -> {
            bank.transfer("SA-2", "CA-2", 100.0);
        });

        // Vérifie la cause
        assertTrue(ex.getCause() instanceof BusinessRuleViolation);
        assertEquals("withdraw amount must be > 0 and <= balance", ex.getCause().getMessage());

        assertEquals(50.0, from.getBalance(), 0.001);
        assertEquals(100.0, to.getBalance(), 0.001);

        assertTrue(from.findByType(TransactionType.WITHDRAW).isEmpty());
        assertTrue(to.findByType(TransactionType.DEPOSIT).isEmpty());
    }

    @Test
    void testTransferFailsDueToNonexistentDestination() {
        Bank bank = new Bank();

        Account from = new SavingsAccount("SA-3", 200.0, 0.01);
        bank.addAccount(from);

        // Maintenant on s’attend directement à UnknownAccountException
        UnknownAccountException ex = assertThrows(UnknownAccountException.class, () -> {
            bank.transfer("SA-3", "UNKNOWN", 50.0);
        });

        assertEquals("Compte introuvable", ex.getMessage());

        assertEquals(200.0, from.getBalance(), 0.001);
        assertTrue(from.findByType(TransactionType.WITHDRAW).isEmpty());
    }
}
