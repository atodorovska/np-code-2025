package class1.bank;

import java.util.ArrayList;
import java.util.List;

public class Bank {

    private List<Account> accounts;

    public Bank() {
        accounts = new ArrayList<>();
    }

    public void addAccount(Account account) {
        accounts.add(account);
    }

    public double totalAssets() {
        double total = 0;

        for (Account account : accounts) {
            total += account.getAccountBalance();
        }
        return total;
    }

    public void addInterest() {
        for (Account account : accounts) {
            if (account.getAccountType().equals(AccountType.INTEREST)) {
                ((InterestBearingAccount) account).addInterest();
            }
        }
    }

}
