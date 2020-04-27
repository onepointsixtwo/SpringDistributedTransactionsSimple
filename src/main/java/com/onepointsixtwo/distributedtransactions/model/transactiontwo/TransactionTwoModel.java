package com.onepointsixtwo.distributedtransactions.model.transactiontwo;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "transactiontwo")
public class TransactionTwoModel implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(columnDefinition = "id")
    private Long id;

    @Column(columnDefinition = "information")
    String information;

    public TransactionTwoModel() {}

    public Long getId() {
        return id;
    }

    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information;
    }
}
