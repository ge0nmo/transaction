
package com.transaction.apply;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
@SpringBootTest
public class InitTxTest
{
    @Autowired Hello hello;

    @Test
    void go()
    {
        // 초기화 코드는 스프링이 초기화 시점에 호출
        hello.init();
    }

    @TestConfiguration
    static class InitTxTestConfig
    {
        @Bean
        public Hello hello()
        {
            return new Hello();
        }
    }

    static class Hello
    {
        @Transactional
        @PostConstruct
        public void init()
        {
            boolean isActive = TransactionSynchronizationManager.isActualTransactionActive();
            log.info("Hello init @PostConstruct tx active={}", isActive);
        }

        @EventListener(ApplicationReadyEvent.class)
        @Transactional
        public void init2()
        {
            boolean isActive = TransactionSynchronizationManager.isActualTransactionActive();
            log.info("Hello init ApplicationEventListener tx active={}", isActive);
        }
    }
}
