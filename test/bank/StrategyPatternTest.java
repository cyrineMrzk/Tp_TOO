// test/bank/StrategyPatternTest.java
package bank;

import bank.fees.*;
import bank.tx.TransactionType;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class StrategyPatternTest {
    
    @Test
    void testNoFeePolicy() {
        Account acc = new SavingsAccount("SA-001", 1000.0, 0.02);
        acc.setFeePolicy(new NoFeePolicy());
        
        acc.withdraw(100.0);
        
        // Solde = 1000 - 100 = 900 (pas de frais)
        assertEquals(900.0, acc.getBalance(), 0.001);
        
        // Une seule transaction WITHDRAW
        assertEquals(1, acc.history().size());
        assertEquals(TransactionType.WITHDRAW, acc.history().get(0).getType());
    }
    
    @Test
    void testFixedFeePolicy() {
        Account acc = new SavingsAccount("SA-002", 1000.0, 0.02);
        acc.setFeePolicy(new FixedFeePolicy(5.0));
        
        acc.withdraw(100.0);
        
        // Solde = 1000 - 100 - 5 = 895
        assertEquals(895.0, acc.getBalance(), 0.001);
        
        // Deux transactions : WITHDRAW + FEE
        assertEquals(2, acc.history().size());
        assertEquals(TransactionType.WITHDRAW, acc.history().get(0).getType());
        assertEquals(TransactionType.FEE, acc.history().get(1).getType());
        assertEquals(5.0, acc.history().get(1).getAmount(), 0.001);
    }
    
    @Test
    void testPercentageFeePolicy() {
        Account acc = new SavingsAccount("SA-003", 1000.0, 0.02);
        acc.setFeePolicy(new PercentageFeePolicy(0.02)); // 2%
        
        acc.withdraw(100.0);
        
        // Frais = 100 * 0.02 = 2.0
        // Solde = 1000 - 100 - 2 = 898
        assertEquals(898.0, acc.getBalance(), 0.001);
        
        assertEquals(2, acc.history().size());
        assertEquals(TransactionType.FEE, acc.history().get(1).getType());
        assertEquals(2.0, acc.history().get(1).getAmount(), 0.001);
    }
    
    @Test
    void testChangingFeePolicy() {
        Account acc = new SavingsAccount("SA-004", 1000.0, 0.02);
        
        // Premier retrait sans frais
        acc.setFeePolicy(new NoFeePolicy());
        acc.withdraw(100.0);
        assertEquals(900.0, acc.getBalance(), 0.001);
        
        // Deuxième retrait avec frais fixes
        acc.setFeePolicy(new FixedFeePolicy(3.0));
        acc.withdraw(50.0);
        assertEquals(847.0, acc.getBalance(), 0.001); // 900 - 50 - 3
        
        // Troisième retrait avec frais proportionnels
        acc.setFeePolicy(new PercentageFeePolicy(0.01)); // 1%
        acc.withdraw(100.0);
        assertEquals(746.0, acc.getBalance(), 0.001); // 847 - 100 - 1
    }
    
    @Test
    void testFeePolicyOnCreditAccount() {
        Account acc = new CreditAccount("CA-001", 100.0, 500.0);
        acc.setFeePolicy(new FixedFeePolicy(2.0));
        
        // Retrait qui met le compte en découvert
        acc.withdraw(150.0);
        
        // Solde = 100 - 150 - 2 = -52
        assertEquals(-52.0, acc.getBalance(), 0.001);
        
        assertEquals(2, acc.history().size());
        assertEquals(TransactionType.WITHDRAW, acc.history().get(0).getType());
        assertEquals(TransactionType.FEE, acc.history().get(1).getType());
    }
    
    @Test
    void testFeePolicyRespectsCreditLimit() {
        Account acc = new CreditAccount("CA-002", 100.0, 500.0);
        acc.setFeePolicy(new FixedFeePolicy(10.0));
        
        // Tentative de retrait qui dépasserait la limite avec les frais
        assertThrows(BusinessRuleViolation.class, () -> acc.withdraw(596.0));
        
        // Le solde ne doit pas avoir changé
        assertEquals(100.0, acc.getBalance(), 0.001);
    }
}