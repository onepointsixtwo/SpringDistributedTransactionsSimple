package com.onepointsixtwo.distributedtransactions.repository.transactionone;

import com.onepointsixtwo.distributedtransactions.model.transactionone.TransactionOneModel;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;

public interface TransactionOneRepository extends JpaRepository<TransactionOneModel, Long> {
    @Transactional
    default void insert(TransactionOneModel transaction) {
        save(transaction);
    }
}
