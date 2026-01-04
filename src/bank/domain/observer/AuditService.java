package bank.domain.observer;

import bank.domain.Account;
import bank.domain.Transaction;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class AuditService implements AccountObserver {
    private static final String AUDIT_FILE = "audit.log";
    private static final DateTimeFormatter FORMATTER = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    @Override
    public void onTransaction(Account acc, Transaction tx) {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        
        // Use Locale.US to ensure consistent decimal format (period instead of comma)
        String auditLine = String.format(Locale.US,
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