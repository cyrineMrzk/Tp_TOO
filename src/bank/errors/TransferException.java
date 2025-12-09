package bank.errors;

public class TransferException extends RuntimeException {
    public TransferException(String msg, Throwable cause) { super(msg, cause); }
}
