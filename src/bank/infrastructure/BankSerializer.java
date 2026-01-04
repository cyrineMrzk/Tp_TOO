package bank.infrastructure;

import bank.domain.Bank;

/**
 * Interface définissant le contrat de sérialisation/désérialisation d'une Bank.
 * 
 * Sépare la logique de format de données de la logique de transport (fichier, réseau, etc.).
 * Permet de supporter plusieurs formats (texte simple, CSV, JSON, XML, etc.) de manière interchangeable.
 */
public interface BankSerializer {
    
    /**
     * Convertit une instance de Bank en représentation textuelle.
     * 
     * @param bank L'instance à sérialiser
     * @return Une chaîne de caractères représentant l'état complet de la banque
     */
    String serialize(Bank bank);
    
    /**
     * Reconstruit une instance de Bank à partir de sa représentation textuelle.
     * 
     * @param data La chaîne de caractères contenant l'état sérialisé
     * @return Une nouvelle instance de Bank
     * @throws PersistenceException si le format est invalide ou corrompu
     */
    Bank deserialize(String data);
}