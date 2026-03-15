package kr.co.jparangdev.boardbuddy.application.shared;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;

@ExtendWith(MockitoExtension.class)
class TxExecutorTest {

    @Mock
    private PlatformTransactionManager transactionManager;

    private TxExecutor executor;

    @BeforeEach
    void setUp() {
        TransactionStatus mockStatus = mock(TransactionStatus.class);
        given(transactionManager.getTransaction(any())).willReturn(mockStatus);
        executor = new TxExecutor(transactionManager);
    }

    @Test
    @DisplayName("write(Supplier) executes action and returns result")
    void write_supplier_executesAndReturnsResult() {
        String result = executor.write(() -> "hello");

        assertThat(result).isEqualTo("hello");
    }

    @Test
    @DisplayName("write(Runnable) executes action")
    void write_runnable_executesAction() {
        var executed = new AtomicBoolean(false);

        executor.write(() -> executed.set(true));

        assertThat(executed.get()).isTrue();
    }

    @Test
    @DisplayName("readOnly(Supplier) executes action and returns result")
    void readOnly_supplier_executesAndReturnsResult() {
        String result = executor.readOnly(() -> "data");

        assertThat(result).isEqualTo("data");
    }

    @Test
    @DisplayName("readOnly(Runnable) executes action")
    void readOnly_runnable_executesAction() {
        var executed = new AtomicBoolean(false);

        executor.readOnly(() -> executed.set(true));

        assertThat(executed.get()).isTrue();
    }

    @Test
    @DisplayName("write(Supplier) propagates exception from action")
    void write_supplier_propagatesException() {
        assertThatThrownBy(() -> executor.write(() -> { throw new RuntimeException("fail"); }))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("fail");
    }

    @Test
    @DisplayName("readOnly(Supplier) propagates exception from action")
    void readOnly_supplier_propagatesException() {
        assertThatThrownBy(() -> executor.readOnly(() -> { throw new RuntimeException("fail"); }))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("fail");
    }
}
