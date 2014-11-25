package majde.marek.bankscrapers.model;

import java.math.BigDecimal;

public class BankAccount {

    private String accountNumber;
    private BigDecimal accountBalance;

    public BankAccount(String accountNumber, BigDecimal accountBalance) {
        this.accountNumber = accountNumber;
        this.accountBalance = accountBalance;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public BigDecimal getAccountBalance() {
        return accountBalance;
    }

}
