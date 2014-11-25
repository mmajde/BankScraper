package majde.marek;

/**
 * Projekt reprezentuja scraper polskiego banku Multibank.
 * Został stworzony w celach czyso edukacyjnych.
 */
public class Main {
    public static void main(String[] args) {
        Application app = Application.getApp();
        app.scrapeMultibank();
    }
}
