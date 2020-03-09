package cn.xpbootcamp.legacy_code;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.transaction.InvalidTransactionException;

import static org.junit.jupiter.api.Assertions.*;

class WalletTransactionTest {
    private WalletTransaction walletTransaction;

    @Test
    void given_null_buyer_id_when_execute_should_throw_InvalidTransactionException() {
        walletTransaction = new WalletTransaction("", null, 0L, 0L, "", 1d);
        assertThrows(InvalidTransactionException.class, () -> walletTransaction.execute(), "This is an invalid transaction");
    }

    @Test
    void given_null_seller_id_when_execute_should_throw_InvalidTransactionException() {
        walletTransaction = new WalletTransaction("", 0L, null, 0L, "", 1d);
        assertThrows(InvalidTransactionException.class, () -> walletTransaction.execute(), "This is an invalid transaction");
    }

    @Test
    void given_null_invalid_amount_when_execute_should_throw_InvalidTransactionException() {
        walletTransaction = new WalletTransaction("", 0L, 0L, 0L, "", -1d);
        assertThrows(InvalidTransactionException.class, () -> walletTransaction.execute(), "This is an invalid transaction");
    }
}