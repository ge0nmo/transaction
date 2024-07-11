package com.transaction.propagation;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
@SpringBootTest
public class BasicTxTest {
    @Autowired
    PlatformTransactionManager txManager;

    @TestConfiguration
    static class Config{
        @Bean
        public PlatformTransactionManager transactionManager(DataSource dataSource){
            return new DataSourceTransactionManager(dataSource);
        }
    }

    @Test
    void commit(){
        log.info("Transaction starting");
        TransactionStatus status = txManager.getTransaction(new DefaultTransactionAttribute());

        log.info("Transaction commit starting");
        txManager.commit(status);
        log.info("Transaction commit completed");
    }

    @Test
    void rollback(){
        log.info("Transaction starting");
        TransactionStatus status = txManager.getTransaction(new DefaultTransactionAttribute());

        log.info("Transaction rollback starting");
        txManager.rollback(status);
        log.info("Transaction rollback completed");
    }

    @Test
    void double_commit(){
        log.info("Transaction 1 starting");
        TransactionStatus tx1 = txManager.getTransaction(new DefaultTransactionAttribute());

        log.info("Transaction 1 commit starting");
        txManager.commit(tx1);
        log.info("Transaction 1 commit completed");

        log.info("Transaction 2 starting");
        TransactionStatus tx2 = txManager.getTransaction(new DefaultTransactionAttribute());

        log.info("Transaction 2 commit starting");
        txManager.commit(tx2);
        log.info("Transaction 2 commit completed");
    }

    @Test
    void double_commit_rollback(){
        log.info("Transaction 1 starting");
        TransactionStatus tx1 = txManager.getTransaction(new DefaultTransactionAttribute());

        log.info("Transaction 1 commit starting");
        txManager.commit(tx1);

        log.info("Transaction 2 starting");
        TransactionStatus tx2 = txManager.getTransaction(new DefaultTransactionAttribute());

        log.info("Transaction 2 rollback starting");
        txManager.rollback(tx2);
    }

    @Test
    void inner_commit(){
        log.info("===external transaction starting===");
        TransactionStatus outer = txManager.getTransaction(new DefaultTransactionAttribute());
        log.info("outer.isNewTransaction()={}", outer.isNewTransaction());

        log.info("===inner transaction starting===");
        TransactionStatus inner = txManager.getTransaction(new DefaultTransactionAttribute());
        log.info("===inner.isNewTransaction()={}===", inner.isNewTransaction());
        log.info("===inner transaction commit===");
        txManager.commit(inner);

        log.info("===outer transaction commit===");
        txManager.commit(outer);
    }

    @Test
    void outer_rollback(){
        log.info("===external transaction starting===");
        TransactionStatus outer = txManager.getTransaction(new DefaultTransactionAttribute());

        log.info("===inner transaction starting===");
        TransactionStatus inner = txManager.getTransaction(new DefaultTransactionAttribute());
        log.info("===inner transaction commit===");
        txManager.commit(inner);

        log.info("===outer transaction rollback===");
        txManager.rollback(outer);

        /*
        ===inner transaction starting===
        Participating in existing transaction
        ===inner transaction commit===
        ===outer transaction rollback===
        Initiating transaction rollback
        Rolling back JDBC transaction on Connection
        */
    }

    @Test
    void inner_rollback(){
        log.info("===external transaction starting===");
        TransactionStatus outer = txManager.getTransaction(new DefaultTransactionAttribute());

        log.info("===inner transaction starting===");
        TransactionStatus inner = txManager.getTransaction(new DefaultTransactionAttribute());
        log.info("===inner transaction rollback===");
        txManager.rollback(inner);

        log.info("===outer transaction commit===");


        assertThatThrownBy(() -> txManager.commit(outer))
                .isInstanceOf(UnexpectedRollbackException.class);
        /*
        ===inner transaction starting===
        Participating in existing transaction
        ===inner transaction rollback===
        Participating transaction failed - marking existing transaction as rollback-only
        Setting JDBC transaction  rollback-only
        ===outer transaction rollback===
        Global transaction is marked as rollback-only but transactional code requested commit
        Rolling back JDBC transaction on Connection
        Initiating transaction rollback
        Releasing JDBC Connection
        Transaction rolled back because it has been marked as rollback-only
        UnexpectedRollbackException.class
        */
    }

    @Test
    void inner_rollback_requires_new(){
        log.info("===external transaction starting===");
        TransactionStatus outer = txManager.getTransaction(new DefaultTransactionAttribute());
        log.info("outer.isNewTransaction()={}", outer.isNewTransaction());

        log.info("===inner transaction starting===");
        DefaultTransactionAttribute definition = new DefaultTransactionAttribute();
        definition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW); // create new transaction
        TransactionStatus inner = txManager.getTransaction(definition);
        log.info("inner.isNewTransaction()={}", inner.isNewTransaction()); // true

        log.info("===inner transaction rollback===");
        txManager.rollback(inner);
        log.info("===outer transaction commit===");
        txManager.commit(outer);

        /*
        * ===external transaction starting===
        * Creating new transaction with name [null]: PROPAGATION_REQUIRED,ISOLATION_DEFAULT
        * Acquired Connection
        * Switching JDBC Connection
        * outer.isNewTransaction()=true
        * ===inner transaction starting===
        * Suspending current transaction, creating new transaction with name [null]
        * Acquired Connection
        * Switching JDBC Connection
        * inner.isNewTransaction()=true
        * ===inner transaction rollback===
        * Initiating transaction rollback
        * Rolling back JDBC transaction on Connection
        * Releasing JDBC Connection after transaction
        * Resuming suspended transaction after completion of inner transaction
        * ===outer transaction commit===
        * Initiating transaction commit
        * Committing JDBC transaction on Connection
         * */
    }

}
