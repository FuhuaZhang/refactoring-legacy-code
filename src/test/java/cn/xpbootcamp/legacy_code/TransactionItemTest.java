package cn.xpbootcamp.legacy_code;

import cn.xpbootcamp.legacy_code.entity.TransactionItem;
import org.junit.jupiter.api.Test;

import javax.transaction.InvalidTransactionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

class TransactionItemTest {
    private TransactionItem transactionItem;

    @Test
    void given_null_invalid_buyer_id_when_execute_should_throw_InvalidTransactionException() {
        transactionItem = new TransactionItem(null, 0L, 1d);
        assertThrows(InvalidTransactionException.class, () -> transactionItem.checkTransactionItemValidity(), "This is an invalid transaction");
    }

    @Test
    void given_null_seller_id_when_execute_should_throw_InvalidTransactionException() {
        transactionItem = new TransactionItem(0L, null, 1d);
        assertThrows(InvalidTransactionException.class, () -> transactionItem.checkTransactionItemValidity(), "This is an invalid transaction");
    }

    @Test
    void given_null_invalid_amount_when_execute_should_throw_InvalidTransactionException() {
        transactionItem = new TransactionItem(0L, 0L, -1d);
        assertThrows(InvalidTransactionException.class, () -> transactionItem.checkTransactionItemValidity(), "This is an invalid transaction");
    }
}