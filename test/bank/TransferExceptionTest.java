package bank;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import bank.domain.Account;
import bank.domain.Bank;
import bank.domain.SavingsAccount;
import bank.errors.InvalidAmountException;
import bank.errors.UnknownAccountException;
import bank.errors.TransferException;

class TransferExceptionTest {

    private Bank bank;
    private Account acc1, acc2;

    @BeforeEach
    void setUp() {
        bank = new Bank();
        acc1 = new SavingsAccount("SA-001", 1000.0, 0); // add 2-param constructor
        acc2 = new SavingsAccount("SA-002", 500.0, 0);
        bank.addAccount(acc1);
        bank.addAccount(acc2);
    }

    @Test
    void testInvalidAmount() {
        assertThrows(InvalidAmountException.class, () -> bank.transfer("SA-001", "SA-002", -100.0));
    }

    @Test
    void testUnknownAccount() {
        assertThrows(UnknownAccountException.class, () -> bank.transfer("SA-001", "SA-999", 100.0));
    }

    @Test
    void testTransferExceptionAtomicity() {
        SavingsAccount faulty = new SavingsAccount("SA-003", 50.0, 0);
        bank.addAccount(faulty);
        assertThrows(TransferException.class, () -> bank.transfer("SA-003", "SA-002", 100.0));
    }
}
