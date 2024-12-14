package agmg.orderms.publisher;

import agmg.orderms.config.RabbitMqConfig;
import agmg.orderms.dto.OrderDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RabbitMqPublisher {
    private final Logger logger = LoggerFactory.getLogger(RabbitMqPublisher.class);

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void publishOrder(OrderDTO order) {
        logger.info("Publishing order into RabbitMQ Broker.");

        rabbitTemplate.convertAndSend(
                RabbitMqConfig.ORDER_EXCHANGE_NAME,
                RabbitMqConfig.ORDER_ROUTING_KEY_NAME,
                order
        );
    }
}
