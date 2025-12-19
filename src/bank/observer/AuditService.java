package bank.observer;

import bank.Account;
import bank.tx.Transaction;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AuditService implements AccountObserver {
    private static final String AUDIT_FILE = "audit.log";
    private static final DateTimeFormatter FORMATTER = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    @Override
    public void onTransaction(Account acc, Transaction tx) {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        String auditLine = String.format(
            "[%s] AUDIT - Account %s | Type: %s | Amount: %.2f | New Balance: %.2f%n",
            timestamp, 
            acc.getAccountNumber(),
            tx.getType(), 
            tx.getAmount(), 
            tx.getBalanceAfter()
        );
        
        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter(AUDIT_FILE, true))) {
            writer.write(auditLine);
        } catch (IOException e) {
            System.err.println("Erreur d'écriture dans audit.log: " + e.getMessage());
        }
    }
}