package bank; 
import bank.domain.Account;
import bank.domain.CreditAccount;
import bank.domain.TransactionType;
import bank.errors.BusinessRuleViolation;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class CreditAccountTest {

    @Test
    void testWithdraw() {
        Account acc = new CreditAccount("CA-1001", 100.0, 500.0);

        // 1. Retrait nominal
        acc.withdraw(50.0);
        assertEquals(50.0, acc.getBalance(), 0.001);
        assertEquals(TransactionType.WITHDRAW, acc.history().get(0).getType());

        // 2. Retrait jusqu'à la limite de crédit autorisée
        acc.withdraw(550.0); // 50 - 550 = -500 max
        assertEquals(-500.0, acc.getBalance(), 0.001);
        assertEquals(TransactionType.WITHDRAW, acc.history().get(1).getType());

        // 3. Retrait impossible : dépassement limite, doit lever exception
        Exception ex = assertThrows(BusinessRuleViolation.class, () -> acc.withdraw(1.0));
        assertEquals("withdraw amount must be > 0 and <= balance + creditLimit", ex.getMessage());

        // 4. Vérification du nombre de transactions
        assertEquals(2, acc.history().size()); // 2 WITHDRAW réussis
    }
}
