package bank;
import bank.tx.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class BusinessAccountTest {

    @Test
    void testWithdraw() {
        BusinessAccount acc = new BusinessAccount("BA-1", 100.0, 500.0, 0.05);

        // Retrait nominal
        acc.withdraw(50.0);
        assertEquals(50.0, acc.getBalance(), 0.001);

        // Cas limite : solde final = -creditLimit
        acc.withdraw(550.0);
        assertEquals(-500.0, acc.getBalance(), 0.001);

        // Dépassement de la limite → exception
        Exception ex = assertThrows(BusinessRuleViolation.class, () -> acc.withdraw(1.0));
        assertEquals("withdraw amount must be > 0 and >= -creditLimit", ex.getMessage());
    }

    @Test
    void testApplyInterest() {
        // Cas A : solde positif
        BusinessAccount acc = new BusinessAccount("BA-2", 200.0, 500.0, 0.05);
        acc.applyInterest();
        assertEquals(210.0, acc.getBalance(), 0.001);
        assertEquals(TransactionType.INTEREST, acc.history().get(acc.history().size()-1).getType());

        // Cas B : solde nul ou négatif
        BusinessAccount acc2 = new BusinessAccount("BA-3", -100.0, 500.0, 0.05);
        acc2.applyInterest();
        assertEquals(-100.0, acc2.getBalance(), 0.001);
        assertTrue(acc2.findByType(TransactionType.INTEREST).isEmpty());
    }
}
