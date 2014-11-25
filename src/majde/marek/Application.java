package majde.marek;

import com.meterware.httpunit.WebConversation;
import majde.marek.bankscrapers.accounts.AccountTransactionsScraper;
import majde.marek.bankscrapers.accounts.UserAccountsScraper;
import majde.marek.bankscrapers.login.BankLoginScraper;
import majde.marek.bankscrapers.model.*;
import majde.marek.exception.WrongAccountNumberException;
import majde.marek.exception.WrongCredentialsException;
import majde.marek.userinteraction.*;
import majde.marek.userinteraction.Error;
import org.xml.sax.SAXException;

import javax.swing.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public class Application {

    private UserInteraction userInteraction;
    private static Application app;

    private Application() {
        userInteraction = new UserInteraction();
    }

    public static synchronized Application getApp() {
        if (app == null) {
            app = new Application();
        }
        return app;
    }

    public void scrapeMultibank() {
        try {
            UserCredentials userCredentials = userInteraction.getUserCredentials();
            WebConversation webConversation = loginToBank(userCredentials);
            userInteraction.onSuccessfulLogin();
            List<BankAccount> bankAccounts = scrapeBankAccounts(webConversation);
            userInteraction.printBankAccounts(bankAccounts);
            LocalDateTime dateFrom = userInteraction.getTransactionDateFrom();
            for (BankAccount account : bankAccounts) {
                List<AccountTransaction> transactions = getAccountTransactions(webConversation, account, dateFrom);
                userInteraction.printTransactionsForAccount(account, dateFrom, transactions);
            }
            userInteraction.onUserInteractionEnd();
        } catch (IOException e) {
            new Error().printProblemWithConnectionError();
            e.printStackTrace();
        } catch (SAXException e) {
            new Error().printInternalError();
            e.printStackTrace();
        } catch (WrongAccountNumberException e) {
            new Error().printInternalError();
            e.printStackTrace();
        } catch (WrongCredentialsException e) {
            new Error().printWrongCredentialsError();
            e.printStackTrace();
        } catch (Exception e) {
            new Error().printInternalError();
            e.printStackTrace();
        }
    }

    private List<AccountTransaction> getAccountTransactions(WebConversation webConversation, BankAccount account, LocalDateTime dateFrom)
            throws IOException, SAXException, WrongAccountNumberException {
        AccountTransactionsScraper bas = new AccountTransactionsScraper(webConversation, account.getAccountNumber(), dateFrom);
        return bas.extractAccountTransactions();
    }

    private WebConversation loginToBank(UserCredentials userCredentials) throws IOException, SAXException, WrongCredentialsException {
        BankLoginScraper bankLoginScraper = new BankLoginScraper(userCredentials);
        return bankLoginScraper.login();
    }

    private List<BankAccount> scrapeBankAccounts(WebConversation webConversation) throws IOException, SAXException {
        UserAccountsScraper userAccountScraper = new UserAccountsScraper(webConversation);
        return userAccountScraper.extractAccounts();
    }
}
