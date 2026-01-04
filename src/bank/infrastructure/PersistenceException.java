package bank.infrastructure;

/**
 * Exception levée lors d'erreurs de persistance (lecture/écriture fichier, format invalide, etc.)
 * Encapsule les exceptions techniques (IOException, parsing errors, etc.) pour
 * protéger le domaine métier des détails d'implémentation.
 */
public class PersistenceException extends RuntimeException {
    
    public PersistenceException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public PersistenceException(String message) {
        super(message);
    }
}