package majde.marek.bankscrapers.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AccountTransaction {
    private LocalDateTime date;
    private LocalDateTime bookkeeping;
    private BigDecimal amount;
    private BigDecimal balanceLeft;
    private String description;
    private String currency;

    public AccountTransaction() {}

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public LocalDateTime getBookkeeping() {
        return bookkeeping;
    }

    public void setBookkeeping(LocalDateTime bookkeeping) {
        this.bookkeeping = bookkeeping;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getBalanceLeft() {
        return balanceLeft;
    }

    public void setBalanceLeft(BigDecimal balanceLeft) {
        this.balanceLeft = balanceLeft;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
