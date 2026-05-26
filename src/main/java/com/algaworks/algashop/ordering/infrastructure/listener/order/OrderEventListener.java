package com.algaworks.algashop.ordering.infrastructure.listener.order;

import com.algaworks.algashop.ordering.domain.model.order.OrderCanceledEvent;
import com.algaworks.algashop.ordering.domain.model.order.OrderPaidEvent;
import com.algaworks.algashop.ordering.domain.model.order.OrderPlacedEvent;
import com.algaworks.algashop.ordering.domain.model.order.OrderReadyEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OrderEventListener {

    @EventListener
    public void listen(OrderPlacedEvent event) {
        log.info("CustomerRegisteredEvent listen PlacedEvent");

    }

    @EventListener
    public void listen(OrderPaidEvent event) {
        log.info("CustomerRegisteredEvent listen PaidEvent");

    }

    @EventListener
    public void listen(OrderReadyEvent event) {
        log.info("CustomerRegisteredEvent listen ReadyEvent");

    }

    @EventListener
    public void listen(OrderCanceledEvent event) {
        log.info("CustomerRegisteredEvent listen CanceledEvent");

    }

}
