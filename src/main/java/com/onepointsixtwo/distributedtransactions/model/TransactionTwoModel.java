package com.onepointsixtwo.distributedtransactions.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "transactiontwo")
public class TransactionTwoModel implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
