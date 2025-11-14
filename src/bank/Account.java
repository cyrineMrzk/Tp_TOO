package bank;

public abstract class Account {
    protected final String accountNumber;
    protected double balance;

    protected Account(String accountNumber, double initial) {
        this.accountNumber = accountNumber;
        this.balance = initial;
    }

    public final void deposit(double amount) {
        //PRE: amount > 0 , else throw BusinessRuleViolation
        if (amount <= 0) {
            throw new IllegalArgumentException("deposit amount must be > 0");
        }
        //POST: balance += amount
        balance += amount;
}

    public abstract void withdraw (double amount);
    //subclasse - specific invariants 

    public final String getAccountNumber() {
        return accountNumber;
    }
    public final double getBalance() {
        return balance;
    }
     
}