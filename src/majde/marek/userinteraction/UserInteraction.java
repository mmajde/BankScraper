package majde.marek.userinteraction;

import majde.marek.bankscrapers.model.AccountTransaction;
import majde.marek.bankscrapers.model.BankAccount;
import majde.marek.bankscrapers.model.UserCredentials;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

public class UserInteraction {

    private Scanner inputScanner;

    /**
     * Remember to call onUserInteractionEnd having finished all the interaction.
     */
    public UserInteraction() {
        inputScanner = new Scanner(System.in);
    }

    public UserCredentials getUserCredentials() {
        String username = "";
        String password = "";
        while (username.equals("")) {
            System.out.println("Podaj nazwę użytkownika:");
            username = inputScanner.nextLine();
        }
        while (password.equals("")) {
            System.out.println("Podaj hasło:");
            password = inputScanner.nextLine();
        }

        return new UserCredentials(username, password);
    }

    public void printBankAccounts(List<BankAccount> bankAccounts) {
        System.out.println("\nTwoje aktualne rachunki w MultiBanku:");
        for (BankAccount bankAccount : bankAccounts) {
            System.out.println("\nNumer konta: " + bankAccount.getAccountNumber() + ". \n\tStan konta: " + bankAccount.getAccountBalance());
        }
    }

    public LocalDateTime getTransactionDateFrom() {
        int day;
        int month;
        int year;
        LocalDateTime dateFrom = LocalDateTime.MAX;
        while (LocalDateTime.now().isBefore(dateFrom) || dateFrom.isBefore(LocalDateTime.of(1901,1,1,0,0))) {
            System.out.println("\nPodaj date od której chcesz pobrać transakcje (dzień, miesiąc, rok).");
            System.out.println("Podaj rok (liczba):");
            year = inputScanner.nextInt();
            System.out.println("Podaj miesiąc (liczba):");
            month = inputScanner.nextInt();
            System.out.println("Podaj dzień (liczba):");
            day = inputScanner.nextInt();
            dateFrom = LocalDateTime.of(year, month, day, 0, 0);
            if (LocalDateTime.now().isBefore(dateFrom)) {
                System.out.println("Data nie może być wcześniejsza niż dzisiejsza data.");
            }
            if (dateFrom.isBefore(LocalDateTime.of(1901,1,1,0,0)) ) {
                System.out.println("Data poza dopuszczalnym zakresem.");
            }
        }
        return dateFrom;
    }

    public void printTransactionsForAccount(BankAccount account, LocalDateTime dateFrom, List<AccountTransaction> at) {
        System.out.println("\n--------------------------------------------------------------------------------");
        System.out.println("\tTransackje z numeru: " + account.getAccountNumber() + " od daty: " + dateFrom.toString().substring(0, 10) + "\n");
        System.out.println("Data transakcji | Data zaksięgowania | Szczegóły transakcji | Kwota transakcji | Saldo po transakcji | Waluta\n");
        for (AccountTransaction t : at) {
            StringBuilder sb = new StringBuilder();
            sb.append(t.getDate().toString().substring(0, 10)).append(" | ")
                    .append(t.getBookkeeping().toString().substring(0, 10)).append(" | ")
                    .append(t.getDescription()).append(" | ")
                    .append(t.getAmount()).append(" | ")
                    .append(t.getBalanceLeft()).append(" | ")
                    .append(t.getCurrency());
            System.out.println(sb.toString());
        }
    }

    public void onSuccessfulLogin() {
        System.out.println("Logowanie powiodło się.");
    }

    public void onUserInteractionEnd() {
        inputScanner.close();
    };
}
