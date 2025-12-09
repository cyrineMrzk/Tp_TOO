package bank;
import bank.tx.Transaction;
import bank.tx.TransactionType;
import java.util.ArrayList;
import java.util.List;




public class Bank {
    private List<Account> accounts = new ArrayList<>();

    private Account findAccountById(String id) {
        for (Account acc : accounts) {
            if (acc.getAccountNumber().equals(id)) {
                return acc;
            }
        }
        return null;
    }
    public void transfer(String fromId, String toId, double amount) {
        if (fromId.equals(toId)) {
            throw new BusinessRuleViolation("Impossible de transférer vers le même compte");
        }
        if (amount <= 0) {
            throw new BusinessRuleViolation("Montant du transfert invalide");
        }

        Account from = findAccountById(fromId);
        Account to   = findAccountById(toId);

        if (from == null || to == null) {
            throw new BusinessRuleViolation("Compte introuvable");
        }
        if (from.getBalance() < amount) {
            throw new BusinessRuleViolation("Solde insuffisant pour le transfert");
        }

       
        from.balance -= amount; 
        to.balance += amount;

       
        from.recordTransaction(new Transaction(TransactionType.TRANSFER_OUT, amount, from.getBalance()));
        to.recordTransaction(new Transaction(TransactionType.TRANSFER_IN, amount, to.getBalance()));
    }
  public void addAccount(Account acc) {
    accounts.add(acc);
}

}
