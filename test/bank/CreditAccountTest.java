package bank;
import bank.tx.*;
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

       // 2. Cas limite : solde final = -creditLimit (before fee)
acc.withdraw(545.0); // 50 - 545 = -495, then -5 fee = -500
assertEquals(-500.0, acc.getBalance(), 0.001);
assertEquals(TransactionType.WITHDRAW, acc.history().get(1).getType());
assertEquals(TransactionType.FEE, acc.history().get(2).getType()); // Fee was applied

        // 3. Cas d’erreur : dépassement limite
        Exception exception = assertThrows(BusinessRuleViolation.class, () -> acc.withdraw(1.0));
        assertEquals("withdraw amount must be > 0 and <= balance + creditLimit", exception.getMessage());

        // 4. Vérifier que la transaction WITHDRAW n’a pas été ajoutée pour le retrait invalide
assertEquals(3, acc.history().size());  // 1 WITHDRAW + 1 WITHDRAW + 1 FEE = 3
    }
}
