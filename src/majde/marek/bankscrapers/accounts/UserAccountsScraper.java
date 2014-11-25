package majde.marek.bankscrapers.accounts;

import com.meterware.httpunit.*;
import majde.marek.bankscrapers.MultibankScraper;
import majde.marek.bankscrapers.model.BankAccount;
import majde.marek.bankscrapers.utils.ParsingHelper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UserAccountsScraper extends MultibankScraper {

    private Document accountsDoc;
    private WebConversation bankConversation;

    public UserAccountsScraper(WebConversation bankConversation){
        this.bankConversation = bankConversation;
    }

    public List<BankAccount> extractAccounts() throws IOException, SAXException {
        accountsDoc = extractDocumentFrom(BASE_BANK_URL + "accounts_list.aspx");
        List<BankAccount> bankAccounts = extractBankAccounts();
        return bankAccounts;
    }

    private Document extractDocumentFrom(String pageUrl) throws IOException, SAXException {
        WebRequest getAccountsPage = new GetMethodWebRequest(pageUrl);
        WebResponse bankAccountPageResponse = bankConversation.getResponse(getAccountsPage);
        String htmlAccountPage = bankAccountPageResponse.getText();
        return  Jsoup.parse(htmlAccountPage);
    }

    private List<BankAccount> extractBankAccounts() {
        Elements accountHyperlinks = getAccountsHyperlinks();
        List<BankAccount> bankAccounts = new ArrayList<BankAccount>();
        for (Element accountHyperlink : accountHyperlinks) {
            BankAccount account = extractAccount(accountHyperlink);
            bankAccounts.add(account);
        }
        return bankAccounts;
    }

    private Elements getAccountsHyperlinks() {
        String hrefWithAccNumber = ".tabelablue1.tbPdd a";
        return accountsDoc.select(hrefWithAccNumber);
    }

    private BankAccount extractAccount(Element accountHyperlink) {
        Element tableFieldWithAccountNumber;
        Element tableFieldWithAccountBalance;
        tableFieldWithAccountNumber = accountHyperlink.parent();
        tableFieldWithAccountBalance = tableFieldWithAccountNumber.nextElementSibling();
        return new BankAccount(tableFieldWithAccountNumber.text(), ParsingHelper.parseMoney(tableFieldWithAccountBalance.text()));
    }
}
