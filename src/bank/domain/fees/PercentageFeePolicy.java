// bank/fees/PercentageFeePolicy.java
package bank.domain.fees;

public class PercentageFeePolicy implements FeePolicy {
    private final double rate;
    
    public PercentageFeePolicy(double rate) {
        this.rate = rate;
    }
    
    @Override
    public double computeFee(double amount) {
        return amount * rate;
    }
}