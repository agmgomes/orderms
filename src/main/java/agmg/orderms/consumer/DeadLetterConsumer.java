package agmg.orderms.consumer;

import agmg.orderms.config.RabbitMqConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
public class DeadLetterConsumer {
    private final Logger logger = LoggerFactory.getLogger(DeadLetterConsumer.class);

    @RabbitListener(queues = RabbitMqConfig.DEAD_LETTER_QUEUE_NAME)
    public void listen(Message<?> message) {
        logger.warn("Invalid message or not processed correctly: {}", message.getPayload());
    }
}
