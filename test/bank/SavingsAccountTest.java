package bank;
import bank.domain.Account;
import bank.domain.SavingsAccount;
import bank.domain.TransactionType;
import bank.errors.BusinessRuleViolation;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class SavingsAccountTest {

    @Test
    void testWithdraw() {
        Account acc = new SavingsAccount("SA-1001", 100.0, 0.05);

        // 1. Retrait nominal
        acc.withdraw(30.0);
        assertEquals(70.0, acc.getBalance(), 0.001);
        assertEquals(TransactionType.WITHDRAW, acc.history().get(0).getType());

        // 2. Cas limite : solde final = 0
        acc.withdraw(70.0);
        assertEquals(0.0, acc.getBalance(), 0.001);
        assertEquals(TransactionType.WITHDRAW, acc.history().get(1).getType());

        // 3. Cas d’erreur : retrait > solde
        Exception exception = assertThrows(BusinessRuleViolation.class, () -> {
            acc.withdraw(10.0);
        });
        assertEquals("withdraw amount must be > 0 and <= balance", exception.getMessage());

        // 4. Vérifier que la transaction WITHDRAW n’a pas été ajoutée pour le retrait invalide
        assertEquals(2, acc.history().size());
    }
}
