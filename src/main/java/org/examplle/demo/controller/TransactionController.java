package org.examplle.demo.controller;

import org.examplle.demo.service.TransactionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TransactionController {
    private final TransactionService redisTransactionService;

    public TransactionController(TransactionService redisTransactionService) {
        this.redisTransactionService = redisTransactionService;
    }

    @GetMapping("/redis-test")
    public String test(@RequestParam String key,
                       @RequestParam String value) {
        redisTransactionService.executeTransaction(key, value);
        return "Transaction Executed!";
    }
}
