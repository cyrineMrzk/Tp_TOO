package bank.errors;

public class UnknownAccountException extends RuntimeException {
    public UnknownAccountException(String msg) { super(msg); }
}