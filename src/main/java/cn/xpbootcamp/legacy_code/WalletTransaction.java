package cn.xpbootcamp.legacy_code;

import cn.xpbootcamp.legacy_code.entity.TransactionItem;
import cn.xpbootcamp.legacy_code.enums.STATUS;
import cn.xpbootcamp.legacy_code.service.WalletServiceImpl;
import cn.xpbootcamp.legacy_code.utils.IdGenerator;
import cn.xpbootcamp.legacy_code.utils.RedisDistributedLock;

import javax.transaction.InvalidTransactionException;

public class WalletTransaction {
    private String id;
    private Long createdTimestamp;
    private STATUS status;
    private String walletTransactionId;
    private RedisDistributedLock redisDistributedLock;
    private WalletServiceImpl walletService;
    private TransactionItem transactionItem;

    public WalletTransaction(String preAssignedId, TransactionItem transactionItem, RedisDistributedLock redisDistributedLock, WalletServiceImpl walletService) {
        if (preAssignedId != null && !preAssignedId.isEmpty()) {
            this.id = preAssignedId;
        } else {
            this.id = IdGenerator.generateTransactionId();
        }
        if (!this.id.startsWith("t_")) {
            this.id = "t_" + preAssignedId;
        }
        this.transactionItem = transactionItem;
        this.status = STATUS.TO_BE_EXECUTED;
        this.createdTimestamp = System.currentTimeMillis();
        this.redisDistributedLock = redisDistributedLock == null ? RedisDistributedLock.getSingletonInstance() : redisDistributedLock;
        this.walletService = walletService;
    }

    public boolean execute() throws InvalidTransactionException {
        transactionItem.checkTransactionItemValidity();
        if (status == STATUS.EXECUTED) return true;
        boolean isLocked = false;
        try {
            isLocked = redisDistributedLock.lock(id);

            if (!isLocked) {
                return false;
            }
            if (status == STATUS.EXECUTED) return true; // double check

            if (transactionOver20Days()) return false;

            String walletTransactionId = walletService.moveMoney(id, transactionItem);

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
}