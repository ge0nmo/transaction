package com.transaction.order;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class OrderService {
    private final OrderRepository orderRepository;

    @Transactional
    public void order(Order order) throws NotEnoughMoneyException {
        log.info("call order");
        orderRepository.save(order);

        log.info("payment process has started");
        if(order.getUsername().equals("예외")){
            log.info("System exception!!!");
            throw new RuntimeException("System Exception!!!");
        }

        else if(order.getUsername().equals("잔고부족")){
            log.info("Not enough money. Business Logic Exception");
            order.setPayStatus("PENDING");
            throw new NotEnoughMoneyException("Not enough money");
        } else {
            log.info("payment completed successfully");
            order.setPayStatus("COMPLETED");
        }
        log.info("payment process has finished");
    }

}
