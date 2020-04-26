package com.onepointsixtwo.distributedtransactions.web;

import com.onepointsixtwo.distributedtransactions.model.TransactionOneModel;
import com.onepointsixtwo.distributedtransactions.repository.TransactionOneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(path = "/api",
        produces = {MediaType.APPLICATION_JSON_VALUE})
public class TransactionRestController {

    @Autowired
    TransactionOneRepository transactionOneRepository;

    @PostMapping(path = "/transaction", consumes = "application/json")
    public void createTransactions(@Valid @RequestBody TransactionOneModel transaction) {
        transactionOneRepository.save(transaction);
    }
}
