package com.onepointsixtwo.distributedtransactions.web;

import com.onepointsixtwo.distributedtransactions.model.Message;
import com.onepointsixtwo.distributedtransactions.model.transactionone.TransactionOneModel;
import com.onepointsixtwo.distributedtransactions.model.transactiontwo.TransactionTwoModel;
import com.onepointsixtwo.distributedtransactions.repository.transactionone.TransactionOneRepository;
import com.onepointsixtwo.distributedtransactions.repository.transactiontwo.TransactionTwoRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;

@RestController
@RequestMapping(path = "/api",
        produces = {MediaType.APPLICATION_JSON_VALUE})
@EnableTransactionManagement
public class TransactionRestController {

    @Autowired
    TransactionOneRepository transactionOneRepository;
    @Autowired
    TransactionTwoRepository transactionTwoRepository;
    @Autowired
    private KafkaTemplate<Integer, String> template;

    @PostMapping(path = "/transaction", consumes = "application/json")
    @Transactional
    public void createTransactions(@Valid @RequestBody TransactionOneModel transaction) {
        // Part 1: insert into first data source
        transactionOneRepository.insert(transaction);

        // Part 2: insert into second data source. Second data source has limited string length on transaction
        // to 10 characters so anything over this will fail on both, and globally revert both commits.
        TransactionTwoModel transactionTwo = new TransactionTwoModel();
        transactionTwo.setInformation(transaction.getInformation());
        transactionTwoRepository.insert(transactionTwo);
    }

    @PostMapping(path="/message", consumes = "application/json")
    public void addKafkaMessage(@RequestBody Message message) {
        template.send("demo_topic", message.getText());
    }

    @KafkaListener(topics = "demo_topic")
    public void receiveMessageFromDemoTopic(ConsumerRecord<Integer, String> record) {
        String value = record.value();
        System.out.println("NEW MESSAGE RECEIVED:");
        System.out.println(value);
    }
}
