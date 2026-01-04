
package bank.domain.fees;

public interface FeePolicy {
    double computeFee(double amount);
}