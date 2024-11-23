package ma.projet.graph.repositories;

import ma.projet.graph.entities.Compte;
import ma.projet.graph.entities.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByCompte(Compte compte);

    @Query("SELECT SUM(t.montant) FROM Transaction t WHERE t.compte = :compte AND t.typeTransaction = 'DEBIT'")
    Double sumDebitByCompte(Compte compte);

    @Query("SELECT SUM(t.montant) FROM Transaction t WHERE t.compte = :compte AND t.typeTransaction = 'CREDIT'")
    Double sumCreditByCompte(Compte compte);
}
