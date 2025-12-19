package bank.observer;

import bank.Account;
import bank.tx.Transaction;

public interface AccountObserver {
    void onTransaction(Account acc, Transaction tx);
}