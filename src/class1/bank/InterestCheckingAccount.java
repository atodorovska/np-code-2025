package class1.bank;

public class InterestCheckingAccount extends Account implements InterestBearingAccount {

    protected static double INTEREST_RATE = 0.03;

    public InterestCheckingAccount(String accountHolder, double accountBalance) {
        super(accountHolder, accountBalance);
    }

    @Override
    public AccountType getAccountType() {
        return AccountType.INTEREST;
    }

    @Override
    public void addInterest() {
        super.setAccountBalance(super.getAccountBalance() * (1 + INTEREST_RATE));
    }
}
