package bank.application;

import bank.domain.*;
import bank.infrastructure.BankRepository;
import bank.errors.*;

public class BankService {
    private BankRepository repository;

    public BankService(BankRepository repository) {
        this.repository = repository;
    }

    public void transfer(String from, String to, double amount) throws BusinessRuleViolation {
        Bank bank = repository.load();
        bank.transfer(from, to, amount);
        repository.save(bank);
    }

    public Bank createSampleBank() {
        Bank bank = new Bank();
        bank.addAccount(new SavingsAccount("SA-1001", 1000, 0.05));
        bank.addAccount(new CreditAccount("CR-2001", 200, 500));
        bank.addAccount(new BusinessAccount("BA-3001", 5000, 0, 0, "PREMIUM"));
        return bank;
    }

    public void saveBank(Bank bank) throws Exception {
        repository.save(bank);
    }

    public Bank loadBank() throws Exception {
        return repository.load();
    }
}