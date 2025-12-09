package bank;
import bank.errors.InvalidAmountException;
import bank.errors.UnknownAccountException;
import bank.errors.TransferException;
import java.util.ArrayList;
import java.util.List;

public class Bank {

    private List<Account> accounts = new ArrayList<>();
    private Logger logger = new Logger(); // instance of logger

    // Find account by ID
    private Account findAccountById(String id) {
        for (Account acc : accounts) {
            if (acc.getAccountNumber().equals(id)) {
                return acc;
            }
        }
        return null;
    }

    // Add an account
    public void addAccount(Account acc) {
        accounts.add(acc);
    }

    // Atomic transfer method
    public void transfer(String fromId, String toId, double amount) {
        if (fromId.equals(toId)) {
            throw new InvalidAmountException("Impossible de transférer vers le même compte");
        }
        if (amount <= 0) {
            throw new InvalidAmountException("Montant du transfert invalide");
        }

        Account from = findAccountById(fromId);
        Account to   = findAccountById(toId);

        if (from == null || to == null) {
            throw new UnknownAccountException("Compte introuvable");
        }

        // Save balances for rollback in case of error
        double oldFromBalance = from.getBalance();
        double oldToBalance = to.getBalance();

        try {
            // Apply withdrawal and deposit safely
            from.withdraw(amount);
            to.deposit(amount);

            // Log successful transfer
            logger.logInfo(String.format("Transfert OK : %.2f EUR %s -> %s", amount, fromId, toId));
        } catch (Exception e) {
            // Rollback balances
            from.setBalance(oldFromBalance);
            to.setBalance(oldToBalance);

            // Log failure
            logger.logError(String.format("Transfert FAILED : %.2f EUR %s -> %s : %s",
                    amount, fromId, toId, e.getMessage()));

            // Wrap and throw transfer exception
            throw new TransferException("Échec du transfert", e);
        }
    }
}
