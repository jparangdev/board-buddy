package kr.co.jparangdev.boardbuddy.application.shared;

import java.util.function.Supplier;

import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@Component
public class TxExecutor {

    private final TransactionTemplate writeTemplate;
    private final TransactionTemplate readOnlyTemplate;

    public TxExecutor(PlatformTransactionManager transactionManager) {
        this.writeTemplate = new TransactionTemplate(transactionManager);
        this.readOnlyTemplate = new TransactionTemplate(transactionManager);
        this.readOnlyTemplate.setReadOnly(true);
    }

    public <T> T write(Supplier<T> action) {
        return writeTemplate.execute(status -> action.get());
    }

    public void write(Runnable action) {
        writeTemplate.executeWithoutResult(status -> action.run());
    }

    public <T> T readOnly(Supplier<T> action) {
        return readOnlyTemplate.execute(status -> action.get());
    }

    public void readOnly(Runnable action) {
        readOnlyTemplate.executeWithoutResult(status -> action.run());
    }
}
