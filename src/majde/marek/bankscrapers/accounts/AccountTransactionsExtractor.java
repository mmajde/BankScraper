package majde.marek.bankscrapers.accounts;

import majde.marek.bankscrapers.model.AccountTransaction;
import majde.marek.bankscrapers.utils.ParsingHelper;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AccountTransactionsExtractor {

    public static final String ZERO_TIME = "T00:00";
    public static final int DATE_INDEX = 0;
    public static final int BOOKKEEPING_INDEX = 2;
    public static final int DESCRIPTION_INDEX = 4;
    public static final int AMOUNT_INDEX = 6;
    public static final int BALANCE_LEFT_INDEX = 8;
    public static final int CURRENCY = 10;

    public ParsingHelper parsingHelper;

    public AccountTransactionsExtractor() {
        parsingHelper = new ParsingHelper();
    }

    public List<AccountTransaction> extractTransactionsFrom(Document transactionsPage) {
        List<AccountTransaction> transactions = new ArrayList<AccountTransaction>();
        Elements fieldsForRow;
        Elements transactionsLinks = getTransactionsLinks(transactionsPage);
        for (Element transactionLink : transactionsLinks) {
            fieldsForRow = getFieldsFrom(transactionLink);
            AccountTransaction transaction = extractTransactionsFrom(fieldsForRow);
            transactions.add(transaction);
        }
        return transactions;
    }

    /**
     * Returns all fields for transaction row except the first, which doesn't contain any information.
     */
    private Elements getFieldsFrom(Element transactionLink) {
        Element transactionField = transactionLink.parent();
        Element begin = transactionField.firstElementSibling();

        return begin.siblingElements();
    }

    private AccountTransaction extractTransactionsFrom(Elements fieldsForRow) {
        AccountTransaction at = new AccountTransaction();

        /** Date has to be concatenated with ZERO_TIME to fulfill the ISO specification. */
        String dateStr = fieldsForRow.get(DATE_INDEX).text().concat(ZERO_TIME);
        String bookkeepingStr = fieldsForRow.get(BOOKKEEPING_INDEX).text().concat(ZERO_TIME);
        String amountStr = fieldsForRow.get(AMOUNT_INDEX).text();
        String balanceLeftStr = fieldsForRow.get(BALANCE_LEFT_INDEX).text();
        String descriptionStr = fieldsForRow.get(DESCRIPTION_INDEX).text();
        String currencyStr = fieldsForRow.get(CURRENCY).text();

        LocalDateTime date = ParsingHelper.parseTransactionDate(dateStr);
        LocalDateTime bookkeeping = parsingHelper.parseTransactionDate(bookkeepingStr);
        BigDecimal amount = parsingHelper.parseMoney(amountStr);
        BigDecimal balanceLeft = ParsingHelper.parseMoney(balanceLeftStr);

        at.setDate(date);
        at.setBookkeeping(bookkeeping);
        at.setAmount(amount);
        at.setBalanceLeft(balanceLeft);
        at.setDescription(descriptionStr);
        at.setCurrency(currencyStr);

        return at;
    }

    private Elements getTransactionsLinks(Document transactionsPage) {
        return transactionsPage.select("a[onclick*=/account_operation_details]");
    }
}
