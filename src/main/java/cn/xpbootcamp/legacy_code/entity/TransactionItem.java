package cn.xpbootcamp.legacy_code.entity;

import javax.transaction.InvalidTransactionException;

public class TransactionItem {
    private Long buyerId;
    private Long sellerId;
    private Double amount;

    public TransactionItem(Long buyerId, Long sellerId, Double amount) {
        this.buyerId = buyerId;
        this.sellerId = sellerId;
        this.amount = amount;
    }

    public Long getBuyerId() {
        return buyerId;
    }

    public Long getSellerId() {
        return sellerId;
    }

    public Double getAmount() {
        return amount;
    }

    public void checkTransactionItemValidity() throws InvalidTransactionException {
        if (getBuyerId() == null || (getSellerId() == null || getAmount() < 0.0)) {
            throw new InvalidTransactionException("This is an invalid transaction");
        }
    }
}