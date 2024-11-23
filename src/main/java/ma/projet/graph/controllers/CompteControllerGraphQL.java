package ma.projet.graph.controllers;

import lombok.AllArgsConstructor;
import ma.projet.graph.entities.*;
import ma.projet.graph.repositories.CompteRepository;
import ma.projet.graph.repositories.TransactionRepository;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@AllArgsConstructor
public class CompteControllerGraphQL {

    private CompteRepository compteRepository;
    private TransactionRepository transactionRepository;

    @QueryMapping
    public List<Compte> allComptes(){
        return compteRepository.findAll();
    }

    @QueryMapping
    public Compte compteById(@Argument Long id){
        Compte compte =  compteRepository.findById(id).orElse(null);
        if(compte == null) throw new RuntimeException(String.format("Compte %s not found", id));
        else return compte;
    }

    @MutationMapping
    public Compte saveCompte(@Argument Compte compte){
       return compteRepository.save(compte);
    }

    @QueryMapping
    public Map<String, Object> totalSolde() {
        long count = compteRepository.count(); // Nombre total de comptes
        double sum = compteRepository.sumSoldes(); // Somme totale des soldes
        double average = count > 0 ? sum / count : 0; // Moyenne des soldes

        return Map.of(
                "count", count,
                "sum", sum,
                "average", average
        );
    }

    @QueryMapping
    public List<Compte> compteByType(@Argument TypeCompte type){
        return compteRepository.findByType(type);
    }

//APOLO
    @QueryMapping
    public boolean deleteCompte(@Argument Long id){
        if (!compteRepository.existsById(id)) throw new RuntimeException(String.format("Compte %s not found", id));
        compteRepository.deleteById(id);
        return true;
    }

    @MutationMapping
    public Transaction addTransaction(@Argument TransactionRequest transactionRequest) {
        Compte compte = compteRepository.findById(transactionRequest.getIdCompte()).orElse(null);
        if (compte == null)
            throw new RuntimeException(String.format("Compte %s not found", transactionRequest.getIdCompte()));

        Transaction transaction = new Transaction();
        transaction.setMontant(transactionRequest.getMontant());
        transaction.setDateTransaction(new Date());
        transaction.setTypeTransaction(transactionRequest.getTypeTransaction());
        transaction.setCompte(compte);

        if (transactionRequest.getTypeTransaction().equals(TypeTransaction.DEBIT)) {
            compte.setSolde(compte.getSolde() - transactionRequest.getMontant());
        } else {
            compte.setSolde(compte.getSolde() + transactionRequest.getMontant());
        }

        compteRepository.save(compte);
        return transactionRepository.save(transaction);
    }

    @QueryMapping
    public List<Transaction> compteTransactions(@Argument Long id){
        Compte compte = compteRepository.findById(id).orElse(null);
        if (compte == null)
            throw new RuntimeException(String.format("Compte %s not found", id));

        return transactionRepository.findByCompte(compte);
    }

    @QueryMapping
    public Map<String, Object> transactionState(@Argument Long id) {
        // Fetching the account (Compte) by ID
        Compte compte = compteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(String.format("Compte %s not found", id)));

        long count = transactionRepository.count();

        double sumDebit = Optional.ofNullable(transactionRepository.sumDebitByCompte(compte)).orElse(0.0);
        double sumCredit = Optional.ofNullable(transactionRepository.sumCreditByCompte(compte)).orElse(0.0);

        // Returning the response as an immutable map
        return Map.of(
                "count", count,
                "sumDebit", sumDebit,
                "sumCredit", sumCredit
        );
    }




}
