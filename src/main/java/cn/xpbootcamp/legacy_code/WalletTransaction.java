package cn.xpbootcamp.legacy_code;

import cn.xpbootcamp.legacy_code.enums.STATUS;
import cn.xpbootcamp.legacy_code.service.WalletService;
import cn.xpbootcamp.legacy_code.service.WalletServiceImpl;
import cn.xpbootcamp.legacy_code.utils.IdGenerator;
import cn.xpbootcamp.legacy_code.utils.RedisDistributedLock;

import javax.transaction.InvalidTransactionException;

public class WalletTransaction {
    private String id;
    private Long buyerId;
    private Long sellerId;
    private Long productId;
    private String orderId;
    private Long createdTimestamp;
    private Double amount;
    private STATUS status;
    private String walletTransactionId;
    private RedisDistributedLock redisDistributedLock;



    public WalletTransaction(String preAssignedId, Long buyerId, Long sellerId, Long productId, String orderId, Double amount, RedisDistributedLock redisDistributedLock) {
        if (preAssignedId != null && !preAssignedId.isEmpty()) {
            this.id = preAssignedId;
        } else {
            this.id = IdGenerator.generateTransactionId();
        }
        if (!this.id.startsWith("t_")) {
            this.id = "t_" + preAssignedId;
        }
        this.buyerId = buyerId;
        this.sellerId = sellerId;
        this.productId = productId;
        this.orderId = orderId;
        this.amount = amount;
        this.status = STATUS.TO_BE_EXECUTED;
        this.createdTimestamp = System.currentTimeMillis();
        this.redisDistributedLock = redisDistributedLock == null ? RedisDistributedLock.getSingletonInstance() : redisDistributedLock;
    }

    public boolean execute() throws InvalidTransactionException {
        if (buyerId == null || (sellerId == null || amount < 0.0)) {
            throw new InvalidTransactionException("This is an invalid transaction");
        }
        if (status == STATUS.EXECUTED) return true;
        boolean isLocked = false;
        try {
            isLocked = redisDistributedLock.lock(id);

            if (!isLocked) {
                return false;
            }
            if (status == STATUS.EXECUTED) return true; // double check

            if (transactionOver20Days()) return false;

            WalletService walletService = getWalletService();
            String walletTransactionId = walletService.moveMoney(id, buyerId, sellerId, amount);

            if (transactionSuccessfullyExecuted(walletTransactionId)) return true;

            this.status = STATUS.FAILED;
            return false;
        } finally {
            if (isLocked) {
                RedisDistributedLock.getSingletonInstance().unlock(id);
            }
        }
    }

    private boolean transactionSuccessfullyExecuted(String walletTransactionId) {
        if (walletTransactionId != null) {
            this.walletTransactionId = walletTransactionId;
            this.status = STATUS.EXECUTED;
            return true;
        }
        return false;
    }

    private boolean transactionOver20Days() {
        long executionInvokedTimestamp = System.currentTimeMillis();
        if (executionInvokedTimestamp - createdTimestamp > 1728000000) {
            this.status = STATUS.EXPIRED;
            return true;
        }
        return false;
    }

    private WalletServiceImpl getWalletService() {
        return new WalletServiceImpl();
    }
}