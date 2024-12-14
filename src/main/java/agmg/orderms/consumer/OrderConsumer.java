package agmg.orderms.consumer;

import agmg.orderms.config.RabbitMqConfig;
import agmg.orderms.dto.OrderDTO;
import agmg.orderms.service.OrderService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.amqp.rabbit.annotation.RabbitListener;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Validated
@Component
public class OrderConsumer {
    private final Logger logger = LoggerFactory.getLogger(OrderConsumer.class);

    @Autowired
    OrderService orderService;

    @RabbitListener(queues = RabbitMqConfig.ORDER_QUEUE_NAME,
            containerFactory = "rabbitListenerContainerFactory")
    public void consumeOrder(@Payload @Valid OrderDTO orderDTO) {
        logger.info("Processing order: {}", orderDTO);
        orderService.processOrder(orderDTO);
    }
}
