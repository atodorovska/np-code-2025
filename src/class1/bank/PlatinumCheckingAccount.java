package class1.bank;

public class PlatinumCheckingAccount extends InterestCheckingAccount {

    public PlatinumCheckingAccount(String accountHolder, double accountBalance) {
        super(accountHolder, accountBalance);
    }

    @Override
    public void addInterest() {
        super.setAccountBalance(super.getAccountBalance() * (1 + INTEREST_RATE * 2));
    }
}
