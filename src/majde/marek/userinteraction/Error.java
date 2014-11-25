package majde.marek.userinteraction;

public class Error {

    public void printProblemWithConnectionError() {
        System.out.println("Problem z połączeniem.");
    }

    public void printInternalError() {
        System.out.println("Aplikacja napotkała na wewnętrzny problem.");
    }

    public void printWrongCredentialsError() {
        System.out.println("Błędny numer klienta (karty) lub hasło.");
    }
}
