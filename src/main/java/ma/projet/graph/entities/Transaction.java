package ma.projet.graph.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double montant;

    @Enumerated(EnumType.STRING)
    private TypeTransaction typeTransaction;

    @Temporal(TemporalType.DATE)
    private Date dateTransaction;

    @ManyToOne
    @JoinColumn(name = "compte_id", nullable = false)
    private Compte compte;



}
