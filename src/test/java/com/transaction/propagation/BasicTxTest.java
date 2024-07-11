package com.transaction.propagation;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.sql.DataSource;

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
}
