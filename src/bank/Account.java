package bank;
import bank.tx.Transaction;
import bank.tx.TransactionType;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public abstract class Account {
    protected final String accountNumber;
    protected double balance;
    protected final List<Transaction> history = new ArrayList<>();


    protected Account(String accountNumber, double initial) {
        this.accountNumber = accountNumber;
        this.balance = initial;
    }

    public final void deposit(double amount) {
        //PRE: amount > 0 , else throw BusinessRuleViolation
        if (amount <= 0) {
            throw new BusinessRuleViolation("montant invalide");
        }
        //POST: balance += amount
        balance += amount;
        Transaction tx = new Transaction(
            TransactionType.DEPOSIT,
            amount,
            balance
        );

    recordTransaction(tx);
}

    public abstract void withdraw (double amount);
    //subclasse - specific invariants 

    public final String getAccountNumber() {
        return accountNumber;
    }
    public final double getBalance() {
        return balance;
    }
    
    protected void recordTransaction(Transaction tx) {
        history.add(tx);
     }
    public List<Transaction> history() {
      return new ArrayList<>(history);
    }
    // qst bonus : filtre par type de transaction
    public List<Transaction> findByType(TransactionType type) {
    List<Transaction> result = new ArrayList<>();
    for (Transaction tx : history) {
        if (tx.getType() == type) {
            result.add(tx);
        }
    }
    // retourne une nouvelle liste de transactions de type de paramètre
    return result;
}
   //Rechercher par intervalle de dates
    public List<Transaction> findByDateRange(LocalDateTime from, LocalDateTime to) {
    List<Transaction> result = new ArrayList<>();
    for (Transaction tx : history) {
        if (!tx.getTimestamp().isBefore(from) && !tx.getTimestamp().isAfter(to)) {
            result.add(tx);
        }
    }
    return result;}

    //Trier l’historique
    public List<Transaction> getSortedHistory() {
    List<Transaction> sorted = new ArrayList<>(history);
    sorted.sort((t1, t2) -> t1.getTimestamp().compareTo(t2.getTimestamp()));
    return sorted;
}
public void setBalance(double balance) {
    this.balance = balance;
}

}