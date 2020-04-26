package com.onepointsixtwo.distributedtransactions.repository;

import com.onepointsixtwo.distributedtransactions.model.TransactionOneModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionOneRepository extends JpaRepository<TransactionOneModel, Long> {
}
