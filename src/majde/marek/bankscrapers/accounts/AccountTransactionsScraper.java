package majde.marek.bankscrapers.accounts;

import com.meterware.httpunit.*;
import majde.marek.bankscrapers.MultibankScraper;
import majde.marek.bankscrapers.model.AccountTransaction;
import majde.marek.exception.WrongAccountNumberException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public class AccountTransactionsScraper extends MultibankScraper {

    private static final int DYNAMIC_ID_PARAMETER = 3;

    private WebConversation bankConversation;
    private String accountNumber;
    private LocalDateTime dateFrom;
    private Document currentPage;
    private WebRequest currentWebRequest;
    private List<AccountTransaction> transactions;

    public AccountTransactionsScraper(WebConversation bankConversation, String accountNumber, LocalDateTime dateFrom) {
        this.bankConversation = bankConversation;
        this.accountNumber = accountNumber;
        this.dateFrom = dateFrom;
    }

    public List<AccountTransaction> extractAccountTransactions() throws IOException, SAXException, WrongAccountNumberException {
        goToAccountsListPage();
        goToAccountHistoryPage();
        goToAllTransactionsPage();
        extractAllTransactions();
        return transactions;
    }

    private void goToAccountsListPage() throws IOException, SAXException {
        WebRequest getAccountsPage = new GetMethodWebRequest(BASE_BANK_URL + "accounts_list.aspx");
        bankConversation.getResponse(getAccountsPage);
        setCurrentPage();
    }

    private void goToAccountHistoryPage() throws IOException, SAXException, WrongAccountNumberException {
        WebRequest accountHistoryRequest = getAccountHistoryRequest();
        bankConversation.getResponse(accountHistoryRequest);
        setCurrentPage();
    }

    private void goToAllTransactionsPage() throws IOException, SAXException {
        createTransactionsRequest();
        sendRequest();
        setCurrentPage();
    }

    private void extractAllTransactions() throws IOException, SAXException {
        AccountTransactionsExtractor extractor = new AccountTransactionsExtractor();
        transactions = extractor.extractTransactionsFrom(currentPage);
        while (previousPageExists()) {
            goToPreviousPageWithTransactions();
            transactions.addAll(extractor.extractTransactionsFrom(currentPage));
        }
    }

    private WebRequest getAccountHistoryRequest() throws WrongAccountNumberException, IOException {
        String accountDynamicId = getAccountDynamicId();
        WebRequest webRequest = new PostMethodWebRequest(BASE_BANK_URL + "account_operations_list.aspx");
        webRequest.setHeaderField("referer", "https://moj.multibank.pl/accounts_list.aspx");
        String accountHistoryParam = "actmn1";
        webRequest.setParameter("actmn", accountHistoryParam);
        webRequest.setParameter("__PARAMETERS", accountDynamicId);
        return webRequest;
    }

    private void goToPreviousPageWithTransactions() throws IOException, SAXException {
        createTransactionsRequest();
        setPreviousPageAdditionalParameters();
        sendRequest();
        setCurrentPage();
    }

    private void createTransactionsRequest() {
        currentWebRequest = new PostMethodWebRequest(BASE_BANK_URL + "account_operations_list.aspx");
        currentWebRequest.setHeaderField("referer", "https://moj.multibank.pl/account_operations_list.aspx");
        currentWebRequest.setParameter("daterange_from_day", Integer.toString(dateFrom.getDayOfMonth()));
        currentWebRequest.setParameter("daterange_from_month", Integer.toString(dateFrom.getMonth().getValue()));
        currentWebRequest.setParameter("daterange_from_year", Integer.toString(dateFrom.getYear()));

        LocalDateTime timePoint = LocalDateTime.now();
        currentWebRequest.setParameter("daterange_to_day", Integer.toString(timePoint.getDayOfMonth()));
        currentWebRequest.setParameter("daterange_to_month", Integer.toString(timePoint.getMonth().getValue()));
        currentWebRequest.setParameter("daterange_to_year", Integer.toString(timePoint.getYear()));

        Element __STATE = currentPage.getElementById("__STATE");
        Element __EVENTVALIDATION = currentPage.getElementById("__EVENTVALIDATION");
        currentWebRequest.setParameter("__STATE", __STATE.attr("value"));
        currentWebRequest.setParameter("__EVENTVALIDATION", __EVENTVALIDATION.attr("value"));
        currentWebRequest.setParameter("accoperlist_filter_group", "ALL@{WSZYSTKIE");
        currentWebRequest.setParameter("rangepanel_groupleft", "daterange");
    }

    private void setPreviousPageAdditionalParameters() {
        Element Activity_Data = currentPage.getElementById("Activity_Data");
        String  __PARAMETERS = getDynamicId("PrevPage");
        currentWebRequest.setParameter("Activity_Data", Activity_Data.attr("value"));
        currentWebRequest.setParameter("__PARAMETERS", __PARAMETERS);

    }

    private void sendRequest() throws IOException, SAXException {
        bankConversation.sendRequest(currentWebRequest);
    }

    private String getAccountDynamicId() throws WrongAccountNumberException, IOException {
        String accountId = getAccountHyperlink();
        return getDynamicId(accountId);
    }

    private String getAccountHyperlink() throws WrongAccountNumberException {
        String hrefWithAccNumber = "a:containsOwn(" + accountNumber + ")";
        Elements accountHyperlinks = currentPage.select(hrefWithAccNumber);
        if (accountHyperlinks.isEmpty()) {
            throw new WrongAccountNumberException();
        }
        return accountHyperlinks.first().id();
    }

    private String getDynamicId(String prevPageId) {
        Element prevPageButton = currentPage.getElementById(prevPageId);
        String onclickAttr = prevPageButton.attr("onclick");
        String[] attributes = onclickAttr.split(",");
        String accountParameter = attributes[DYNAMIC_ID_PARAMETER];
        return skipQuotationMarks(accountParameter.trim());
    }

    private void setCurrentPage() throws IOException {
        String pageInHtml = bankConversation.getCurrentPage().getText();
        currentPage = Jsoup.parse(pageInHtml);
    }

    private boolean previousPageExists() throws IOException {
        return currentPage.getElementById("PrevPage") != null;
    }

    private String skipQuotationMarks(String s) {
        return s.trim().substring(1, s.length() - 1);
    }
}

