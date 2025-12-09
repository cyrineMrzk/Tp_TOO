package bank.tx;

import java.time.LocalDateTime;
//declarer une class immuable final avec des att final et private

public final class Transaction {

   
    private final LocalDateTime timestamp;
    private final TransactionType type;
    private final double amount;
    private final double balanceAfter;

 
    public Transaction(TransactionType type, double amount, double balanceAfter) {
       

        this.timestamp = LocalDateTime.now(); 
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
    }

    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public TransactionType getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public double getBalanceAfter() {
        return balanceAfter;
    }

    @Override
    public String toString() {
        return String.format(
            "[%s] %-12s %.2f → solde %.2f",
            timestamp,
            type,
            amount,
            balanceAfter
        );
    }
}

