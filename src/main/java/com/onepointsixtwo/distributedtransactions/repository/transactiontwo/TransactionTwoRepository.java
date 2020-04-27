package com.onepointsixtwo.distributedtransactions.repository.transactiontwo;

import com.onepointsixtwo.distributedtransactions.model.transactiontwo.TransactionTwoModel;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;

public interface TransactionTwoRepository extends JpaRepository<TransactionTwoModel, Long> {
    @Transactional
    default void insert(TransactionTwoModel transaction) {
        save(transaction);
    }
}
