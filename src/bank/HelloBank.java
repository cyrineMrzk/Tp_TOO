package bank;

public class HelloBank {
    private final String bankName;
    private final String city;
    private final int yearFounded;

    public HelloBank(String bankName, String city, int yearFounded) {
        this.bankName = bankName;
        this.city = city;
        this.yearFounded = yearFounded;
    }

    public void greetCustomer(String name) {
        System.out.println("Bonjour " + name + ", bienvenue à " + bankName + " de " + city + "!" + " Fondee en " + yearFounded + ".");
    }

    public static void info() {
        System.out.println("Bienvenue à HelloBank, votre banque en ligne.");
    }
}
