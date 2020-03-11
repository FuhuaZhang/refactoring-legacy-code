package cn.xpbootcamp.legacy_code.service;

import cn.xpbootcamp.legacy_code.entity.TransactionItem;

public interface WalletService {
    String moveMoney(String id, TransactionItem transactionItem);
}
