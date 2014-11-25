package majde.marek.bankscrapers.login;

import com.meterware.httpunit.*;
import com.meterware.httpunit.WebRequest;
import majde.marek.bankscrapers.MultibankScraper;
import majde.marek.bankscrapers.model.UserCredentials;
import majde.marek.exception.WrongCredentialsException;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BankLoginScraper extends MultibankScraper {

    private UserCredentials userCredentials;
    private WebConversation webConversation;
    private WebForm loginForm;

    public BankLoginScraper(UserCredentials userCredentials) {
        this.userCredentials = userCredentials;
        this.webConversation = new WebConversation();
    }

    public WebConversation login() throws IOException, SAXException, WrongCredentialsException {
        fetchLoginForm();
        fillInLoginForm();
        submitLoginForm();
        checkResponse();
        return webConversation;
    }

    private void checkResponse() throws IOException, WrongCredentialsException {
        WebResponse loginResponse = webConversation.getCurrentPage();
        String responseText = loginResponse.getText();
        if (responseText.contains("Błąd"))
            throw new WrongCredentialsException();
    }

    private void fetchLoginForm() throws IOException, SAXException {
        WebRequest loginPageRequest = new GetMethodWebRequest(BASE_BANK_URL);
        WebResponse loginPageResponse = webConversation.getResponse(loginPageRequest);
        loginForm = loginPageResponse.getFormWithID("MainForm");
    }

    private void fillInLoginForm() throws SAXException {
        loginForm.setParameter("customer", userCredentials.getUsername());
        loginForm.setParameter("password", userCredentials.getPassword());
    }

    private void submitLoginForm() throws IOException, SAXException {
        loginForm.submit();
    }

}
