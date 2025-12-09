import bank.*;
import bank.tx.*;

public class Main {
    public static void main(String[] args) {

        // --- 0. Créer la banque ---
        Bank bank = new Bank();

        // --- 1. Création de deux comptes ---
        SavingsAccount a1 = new SavingsAccount("SA-1001", 200.0, 0.018);
        Account a2 = new CreditAccount("CA-9001", 0.0, 500.0);

        // Ajouter les comptes à la banque
        bank.addAccount(a1);
        bank.addAccount(a2);

        System.out.println("=== Dépôts et retraits valides ===");
        try {
            a1.deposit(50);          // Solde 250
            a1.withdraw(20);         // Solde 230
            a1.applyInterest();      // Ajout intérêt
            a2.deposit(300);         // Solde 300
            a2.withdraw(100);        // Solde 200
        } catch (BusinessRuleViolation e) {
            System.out.println("Erreur: " + e.getMessage());
        }

        // --- 2. Tentative d'opérations invalides ---
        System.out.println("\n=== Opérations invalides ===");
        try {
            a1.deposit(-10);
        } catch (BusinessRuleViolation e) {
            System.out.println("Erreur: " + e.getMessage());
        }
        try {
            a2.withdraw(1000);  // dépassement limite
        } catch (BusinessRuleViolation e) {
            System.out.println("Erreur: " + e.getMessage());
        }

        // --- 3. Affichage historique complet avant transferts ---
        System.out.println("\n=== Historique avant transferts ===");
        printHistory(a1);
        printHistory(a2);

        // --- 4. Transfert réussi ---
        System.out.println("\n=== Transfert réussi (a1 -> a2, 50) ===");
        try {
            bank.transfer(a1.getAccountNumber(), a2.getAccountNumber(), 50);
        } catch (BusinessRuleViolation e) {
            System.out.println("Erreur transfert: " + e.getMessage());
        }

        // --- 5. Transfert qui échoue (solde insuffisant) ---
        System.out.println("\n=== Transfert échoué (a1 -> a2, 500) ===");
        try {
            bank.transfer(a1.getAccountNumber(), a2.getAccountNumber(), 500);
        } catch (BusinessRuleViolation e) {
            System.out.println("Erreur transfert: " + e.getMessage());
        }

        // --- 6. Historique complet après transferts ---
        System.out.println("\n=== Historique final ===");
        printHistory(a1);
        printHistory(a2);
    }

    // --- Méthode utilitaire pour afficher l'historique d'un compte ---
    private static void printHistory(Account account) {
        System.out.printf("\nHistorique [%s]:\n", account.getAccountNumber());
        for (Transaction tx : account.history()) {
            System.out.println(tx.toString());
        }
    }
}
