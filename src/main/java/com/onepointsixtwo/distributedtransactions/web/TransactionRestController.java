package com.onepointsixtwo.distributedtransactions.web;

import com.onepointsixtwo.distributedtransactions.model.TransactionOneModel;
import com.onepointsixtwo.distributedtransactions.model.TransactionTwoModel;
import com.onepointsixtwo.distributedtransactions.repository.TransactionOneRepository;
import com.onepointsixtwo.distributedtransactions.repository.TransactionTwoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(path = "/api",
        produces = {MediaType.APPLICATION_JSON_VALUE})
public class TransactionRestController {

    @Autowired
    TransactionOneRepository transactionOneRepository;
    @Autowired
    TransactionTwoRepository transactionTwoRepository;

    @PostMapping(path = "/transaction", consumes = "application/json")
    @Transactional
    public void createTransactions(@Valid @RequestBody TransactionOneModel transaction) {
        transactionOneRepository.save(transaction);

        TransactionTwoModel transactionTwo = new TransactionTwoModel();
        transactionTwo.setInformation(transaction.getInformation());
        transactionTwoRepository.save(transactionTwo);
    }
}
