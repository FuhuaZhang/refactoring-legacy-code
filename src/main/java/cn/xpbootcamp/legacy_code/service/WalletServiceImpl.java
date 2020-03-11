package cn.xpbootcamp.legacy_code.service;

import cn.xpbootcamp.legacy_code.entity.TransactionItem;
import cn.xpbootcamp.legacy_code.entity.User;
import cn.xpbootcamp.legacy_code.repository.UserRepository;
import cn.xpbootcamp.legacy_code.repository.UserRepositoryImpl;

import java.util.UUID;

public class WalletServiceImpl implements WalletService {
    private UserRepository userRepository = new UserRepositoryImpl();

    public String moveMoney(String id, TransactionItem transactionItem) {
        User buyer = userRepository.find(transactionItem.getBuyerId());
        if (buyer.getBalance() >= transactionItem.getAmount()) {
            User seller = userRepository.find(transactionItem.getSellerId());
            seller.setBalance(seller.getBalance() + transactionItem.getAmount());
            buyer.setBalance(buyer.getBalance() - transactionItem.getAmount());
            return UUID.randomUUID().toString() + id;
        } else {
            return null;
        }
    }
}
