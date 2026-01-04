package bank.infrastructure;

import java.nio.file.Files;
import java.nio.file.Path;

import bank.domain.Bank;

import java.io.IOException;


public class FileBankRepository implements BankRepository {
    
    private final Path path;
    private final BankSerializer serializer;
    
    /**
     * Crée un repository fichier avec un chemin et un serializer spécifiques.
     * 
     * @param path Chemin du fichier de persistance
     * @param serializer Stratégie de sérialisation à utiliser
     */
    public FileBankRepository(Path path, BankSerializer serializer) {
        this.path = path;
        this.serializer = serializer;
    }
    
    /**
     * Sauvegarde la banque dans le fichier.
     * 
     * @param bank L'instance de Bank à sauvegarder
     * @throws PersistenceException si l'écriture échoue
     */
    @Override
    public void save(Bank bank) throws PersistenceException {
        try {
            // Sérialiser la banque en chaîne de caractères
            String data = serializer.serialize(bank);
            
            // Écrire dans le fichier
            Files.writeString(path, data);
            
        } catch (IOException e) {
            throw new PersistenceException(
                "Erreur lors de la sauvegarde dans " + path, e);
        }
    }
    
    /**
     * Charge la banque depuis le fichier.
     * 
     * @return Une nouvelle instance de Bank restaurée
     * @throws PersistenceException si la lecture échoue ou si le fichier n'existe pas
     */
    @Override
    public Bank load() throws PersistenceException {
        try {
            // Vérifier si le fichier existe
            if (!Files.exists(path)) {
                throw new PersistenceException(
                    "Le fichier " + path + " n'existe pas");
            }
            
            // Lire le contenu du fichier
            String data = Files.readString(path);
            
            // Désérialiser et retourner la banque
            return serializer.deserialize(data);
            
        } catch (IOException e) {
            throw new PersistenceException(
                "Erreur lors du chargement depuis " + path, e);
        }
    }
}