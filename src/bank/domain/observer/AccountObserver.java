package bank.domain.observer;

import bank.domain.Account;
import bank.domain.Transaction;

public interface AccountObserver {
    void onTransaction(Account acc, Transaction tx);
}