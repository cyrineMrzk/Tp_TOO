package bank.infrastructure;
import bank.domain.Bank;

public interface BankRepository {
    void save(Bank bank);
    Bank load();
}
