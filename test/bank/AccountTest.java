package bank;
import bank.domain.Account;
import bank.domain.SavingsAccount;
import bank.domain.TransactionType;
import bank.errors.BusinessRuleViolation;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class AccountTest {

    @Test
    void testDeposit() {
        //on a pris un exemple avec SavingsAccount mais on peut faire pareil avec CreditAccount
        Account acc = new SavingsAccount("SA-1001", 100.0, 0.05);

        // Cas nominal
        acc.deposit(50.0);
        assertEquals(150.0, acc.getBalance(), 0.001);
        assertEquals(TransactionType.DEPOSIT, acc.history().get(0).getType());

        // Cas limite
        acc.deposit(0.01);
        assertEquals(150.01, acc.getBalance(), 0.001);
        assertEquals(TransactionType.DEPOSIT, acc.history().get(1).getType());

        // Cas d’erreur
        Exception exception = assertThrows(BusinessRuleViolation.class, () -> acc.deposit(0));
        assertEquals("montant invalide", exception.getMessage());
    }


}
