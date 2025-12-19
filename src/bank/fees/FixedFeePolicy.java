// bank/fees/FixedFeePolicy.java
package bank.fees;

public class FixedFeePolicy implements FeePolicy {
    private final double fee;
    
    public FixedFeePolicy(double fee) {
        this.fee = fee;
    }
    
    @Override
    public double computeFee(double amount) {
        return fee;
    }
}