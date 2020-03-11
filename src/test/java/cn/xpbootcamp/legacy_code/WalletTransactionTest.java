package cn.xpbootcamp.legacy_code;

import cn.xpbootcamp.legacy_code.service.WalletServiceImpl;
import cn.xpbootcamp.legacy_code.utils.RedisDistributedLock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import javax.transaction.InvalidTransactionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class WalletTransactionTest {
    private WalletTransaction walletTransaction;
    private WalletServiceImpl walletService;
    @Mock
    private RedisDistributedLock redisDistributedLock;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        walletService = Mockito.mock(WalletServiceImpl.class);
    }

    @Test
    void given_null_buyer_id_when_execute_should_throw_InvalidTransactionException() {
        walletTransaction = new WalletTransaction("", null, 0L, 0L, "", 1d, redisDistributedLock, walletService);
        assertThrows(InvalidTransactionException.class, () -> walletTransaction.execute(), "This is an invalid transaction");
    }

    @Test
    void given_null_seller_id_when_execute_should_throw_InvalidTransactionException() {
        walletTransaction = new WalletTransaction("", 0L, null, 0L, "", 1d, redisDistributedLock, walletService);
        assertThrows(InvalidTransactionException.class, () -> walletTransaction.execute(), "This is an invalid transaction");
    }

    @Test
    void given_null_invalid_amount_when_execute_should_throw_InvalidTransactionException() {
        walletTransaction = new WalletTransaction("", 0L, 0L, 0L, "", -1d, redisDistributedLock, walletService);
        assertThrows(InvalidTransactionException.class, () -> walletTransaction.execute(), "This is an invalid transaction");
    }

    @Test
    void given_lock_failed_when_execute_should_return_false() throws InvalidTransactionException {
        when(redisDistributedLock.lock(any())).thenReturn(false);
        doAnswer((any) -> null).when(redisDistributedLock).unlock(any());
        walletTransaction = new WalletTransaction("", 0L, 0L, 0L, "", 1d, redisDistributedLock, walletService);
        assertFalse(walletTransaction.execute());
    }
}