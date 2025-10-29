package class1.bank;

public abstract class Account {

    private static long ACCOUNT_NUMBER = 1L;

    private String accountHolder;
    private long accountNumber;
    private double accountBalance;
    private AccountType accountType;

    public Account(String accountHolder, double accountBalance) {
        this.accountHolder = accountHolder;
        this.accountBalance = accountBalance;
        this.accountNumber = ACCOUNT_NUMBER++;
    }

    public String getAccountHolder() {
        return accountHolder;
    }

    public void setAccountHolder(String accountHolder) {
        this.accountHolder = accountHolder;
    }

    public long getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(long accountNumber) {
        this.accountNumber = accountNumber;
    }

    public double getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(double accountBalance) {
        this.accountBalance = accountBalance;
    }

    public double deposit(double amount) {
        accountBalance += amount;
        return accountBalance;
    }

    public double withdraw(double amount) {
        if (accountBalance >= amount)
            accountBalance -= amount;

        return accountBalance;
    }

    public abstract AccountType getAccountType();

    @Override
    public String toString() {
        return "Account{" +
                "accountHolder='" + accountHolder + '\'' +
                ", accountNumber=" + accountNumber +
                ", accountBalance=" + accountBalance +
                '}';
    }
}
