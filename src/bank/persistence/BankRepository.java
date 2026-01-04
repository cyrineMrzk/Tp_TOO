package bank.persistence;
import bank.Bank;

public interface BankRepository {
    void save(Bank bank);
    Bank load();
}
