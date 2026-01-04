package bank.infrastructure;

import bank.domain.Bank;

public class InMemoryBankRepository implements BankRepository {
    private Bank bank;

    @Override
    public void save(Bank bank) {
        this.bank = bank;
    }

    @Override
    public Bank load() {
        return bank;
    }
}