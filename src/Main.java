import bank.HelloBank;

public class Main {
    public static void main(String[] args) {
        HelloBank.info(); 
        HelloBank b = new HelloBank("EcoBank", "Pau", 2005);
        HelloBank b2 = new HelloBank("MyBank", "Biarritz", 2010);
        b.greetCustomer("Alice");
        b2.greetCustomer("Bob");
    }
}
