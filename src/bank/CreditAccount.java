package bank;
import bank.tx.Transaction;
import bank.tx.TransactionType;

public class CreditAccount extends Account {
    private final double creditLimit;

    public CreditAccount(String id, double initial, double creditLimit) {
        super(id, initial);
        this.creditLimit = creditLimit;
    }

    @Override
    public void withdraw(double amount) {
        if (amount <= 0 || amount > balance + creditLimit) {
            throw new BusinessRuleViolation("withdraw amount must be > 0 and <= balance + creditLimit");
        }
        balance -= amount;
        recordTransaction(new Transaction(TransactionType.WITHDRAW, amount, balance));
      // si le compte passe en négatif alrs ajouter un frais (coût supplémentaire quand le compte est à découvert)
    if (balance < 0) {
        double fee = 5.0;  //exemple de frais 5.0 unités
        balance -= fee;
        recordTransaction(new Transaction(TransactionType.FEE, fee, balance));
    }
    }
     
}