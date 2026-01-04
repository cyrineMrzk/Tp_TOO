package bank.persistence;

import bank.*;
import bank.tx.Transaction;
import java.util.List;

/**
 * Sérialiseur simple au format texte ligne par ligne.
 * 
 * Format :
 * ACCOUNT;TYPE;ID;BALANCE[;PARAM1;PARAM2...]
 * TRANSACTION;ACCOUNT_ID;TYPE;AMOUNT;BALANCE_AFTER;TIMESTAMP
 * 
 * Exemples :
 * ACCOUNT;SAVINGS;SA-1001;1000.0;0.05
 * ACCOUNT;CREDIT;CR-2001;-200.0;500.0
 * ACCOUNT;BUSINESS;BA-3001;5000.0;PREMIUM
 * TRANSACTION;SA-1001;DEPOSIT;200.0;1200.0;2025-12-19T10:30:00
 * TRANSACTION;CR-2001;WITHDRAW;50.0;-250.0;2025-12-19T11:00:00
 */
public class TextBankSerializer implements BankSerializer {
    
    private static final String SEPARATOR = ";";
    private static final String ACCOUNT_PREFIX = "ACCOUNT";
    private static final String TRANSACTION_PREFIX = "TRANSACTION";
    
    @Override
    public String serialize(Bank bank) {
        StringBuilder sb = new StringBuilder();
        
        // Sérialiser chaque compte
        List<Account> accounts = bank.getAllAccounts();
        
        for (Account acc : accounts) {
            // Ligne ACCOUNT
            sb.append(serializeAccount(acc)).append("\n");
            
            // Lignes TRANSACTION pour ce compte
            for (Transaction tx : acc.history()) {
                sb.append(serializeTransaction(acc.getAccountNumber(), tx))
                  .append("\n");
            }
        }
        
        return sb.toString();
    }
    
    /**
     * Sérialise un compte selon son type.
     */
    private String serializeAccount(Account acc) {
        StringBuilder sb = new StringBuilder();
        sb.append(ACCOUNT_PREFIX).append(SEPARATOR);
        
        if (acc instanceof SavingsAccount) {
            SavingsAccount sa = (SavingsAccount) acc;
            sb.append("SAVINGS").append(SEPARATOR)
              .append(sa.getAccountNumber()).append(SEPARATOR)
              .append(sa.getBalance()).append(SEPARATOR)
              .append(sa.getInterestRate());
              
        } else if (acc instanceof CreditAccount) {
            CreditAccount ca = (CreditAccount) acc;
            sb.append("CREDIT").append(SEPARATOR)
              .append(ca.getAccountNumber()).append(SEPARATOR)
              .append(ca.getBalance()).append(SEPARATOR)
              .append(ca.getCreditLimit());
              
        } else if (acc instanceof BusinessAccount) {
            BusinessAccount ba = (BusinessAccount) acc;
            sb.append("BUSINESS").append(SEPARATOR)
              .append(ba.getAccountNumber()).append(SEPARATOR)
              .append(ba.getBalance()).append(SEPARATOR)
              .append(ba.getTier());
        }
        
        return sb.toString();
    }
    
    /**
     * Sérialise une transaction.
     */
    private String serializeTransaction(String accountId, Transaction tx) {
        return TRANSACTION_PREFIX + SEPARATOR +
               accountId + SEPARATOR +
               tx.getType() + SEPARATOR +
               tx.getAmount() + SEPARATOR +
               tx.getBalanceAfter() + SEPARATOR +
               tx.getTimestamp();
    }
    
    @Override
    public Bank deserialize(String data) {
        Bank bank = new Bank();
        
        if (data == null || data.trim().isEmpty()) {
            return bank; // Retourner une banque vide
        }
        
        String[] lines = data.split("\n");
        Account currentAccount = null;
        
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) continue;
            
            String[] parts = line.split(SEPARATOR);
            
            if (parts.length == 0) continue;
            
            String prefix = parts[0];
            
            if (ACCOUNT_PREFIX.equals(prefix)) {
                currentAccount = deserializeAccount(parts);
                bank.addAccount(currentAccount);
                
            } else if (TRANSACTION_PREFIX.equals(prefix) && currentAccount != null) {
                deserializeTransaction(parts, currentAccount);
            }
        }
        
        return bank;
    }
    
    /**
     * Désérialise un compte depuis une ligne.
     */
    private Account deserializeAccount(String[] parts) {
        if (parts.length < 4) {
            throw new PersistenceException("Format de compte invalide");
        }
        
        String type = parts[1];
        String accountNumber = parts[2];
        double balance = parseDouble(parts[3]);
        
        Account account;
        
        switch (type) {
            case "SAVINGS":
                if (parts.length < 5) {
                    throw new PersistenceException("SavingsAccount incomplet");
                }
                double interestRate = parseDouble(parts[4]);
                account = new SavingsAccount(accountNumber, balance, interestRate);
                break;
                
            case "CREDIT":
                if (parts.length < 5) {
                    throw new PersistenceException("CreditAccount incomplet");
                }
                double creditLimit = parseDouble(parts[4]);
                account = new CreditAccount(accountNumber, balance, creditLimit);
                break;
                
            case "BUSINESS":
                if (parts.length < 5) {
                    throw new PersistenceException("BusinessAccount incomplet");
                }
                String tier = parts[4];
                account = new BusinessAccount(accountNumber, balance, tier);
                break;
                
            default:
                throw new PersistenceException("Type de compte inconnu : " + type);
        }
        
        return account;
    }
    
    /**
     * Désérialise une transaction et l'ajoute au compte.
     * Note: On ne peut pas recréer exactement les transactions avec timestamp original,
     * donc on les ignore pour cette version simple.
     */
    private void deserializeTransaction(String[] parts, Account account) {
        // Pour l'instant, on ignore les transactions dans le chargement
        // car Transaction est immutable avec timestamp = now()
        // Une amélioration serait de modifier Transaction pour accepter un timestamp
        
        // Version simplifiée : on skip les transactions
        // Dans une vraie application, il faudrait soit :
        // 1. Modifier Transaction pour accepter un timestamp custom
        // 2. Utiliser la réflexion pour forcer le timestamp
        // 3. Ne sauvegarder que le solde final
    }
    
    /**
     * Parse un double de manière sûre.
     */
    private double parseDouble(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw new PersistenceException("Format numérique invalide : " + value, e);
        }
    }
}