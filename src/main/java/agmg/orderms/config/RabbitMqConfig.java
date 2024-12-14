package agmg.orderms.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.amqp.core.*;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.ConditionalRejectingErrorHandler;
import org.springframework.amqp.rabbit.support.ListenerExecutionFailedException;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ErrorHandler;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

import jakarta.validation.ConstraintViolationException;

@Configuration
@EnableRabbit
public class RabbitMqConfig {
    private final Logger logger = LoggerFactory.getLogger(RabbitMqConfig.class);

    public static final String ORDER_EXCHANGE_NAME = "order-created-exchange";
    public static final String ORDER_QUEUE_NAME = "order-created-queue";
    public static final String ORDER_ROUTING_KEY_NAME = "order-created-key";

    public static final String DEAD_LETTER_EXCHANGE_NAME = "x-dead-letter-exchange";
    public static final String DEAD_LETTER_QUEUE_NAME = "dead-letter-queue";
    public static final String DEAD_LETTER_ROUTING_KEY_NAME = "x-dead-letter-key";

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        return new MethodValidationPostProcessor();
    }

    public static class FatalMessageErrorHandler extends ConditionalRejectingErrorHandler.DefaultExceptionStrategy {

        private final Logger logger = LoggerFactory.getLogger(getClass());

        @Override
        public boolean isFatal(Throwable t) {
            if (t instanceof ListenerExecutionFailedException) {
                ListenerExecutionFailedException lefe = (ListenerExecutionFailedException) t;
                Message failedMessage = lefe.getFailedMessage();

                Throwable cause = t.getCause();
                if (cause instanceof ConstraintViolationException || cause instanceof MessageConversionException) {
                    logger.error("Invalid message rejected: {}", new String(failedMessage.getBody()), cause);

                    return true;
                }

            }
            return super.isFatal(t);
        }
    }

    @Bean
    public ErrorHandler errorHandler() {
        return new ConditionalRejectingErrorHandler(new FatalMessageErrorHandler());
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            Jackson2JsonMessageConverter messageConverter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);
        // factory.setDefaultRequeueRejected(false);
        factory.setErrorHandler(errorHandler());
        return factory;
    }

    @Bean(name = "orderQueue")
    public Queue orderQueue() {
        return QueueBuilder.durable(ORDER_QUEUE_NAME)
                .withArgument("x-dead-letter-exchange", DEAD_LETTER_EXCHANGE_NAME)
                .withArgument("x-dead-letter-routing-key", DEAD_LETTER_ROUTING_KEY_NAME)
                .build();
    }

    @Bean(name = "deadLetterQueue")
    public Queue deadLetterQueue() {
        return QueueBuilder.durable(DEAD_LETTER_QUEUE_NAME)
                .build();
    }

    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(ORDER_EXCHANGE_NAME);
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(DEAD_LETTER_EXCHANGE_NAME);
    }

    @Bean
    public Binding orderQueueBinding(@Qualifier("orderQueue") Queue orderQueue, TopicExchange topicExchange) {
        return BindingBuilder.bind(orderQueue).to(topicExchange).with(ORDER_ROUTING_KEY_NAME);
    }

    @Bean
    public Binding deadLetterQueueBinding(@Qualifier("deadLetterQueue") Queue deadLetterQueue,
            DirectExchange deadLetterExchange) {
        return BindingBuilder.bind(deadLetterQueue)
                .to(deadLetterExchange)
                .with(DEAD_LETTER_ROUTING_KEY_NAME);
    }

    @Bean
    CommandLineRunner initRabbit(AmqpAdmin amqpAdmin) {
        return args -> {
            Queue orderQueue = orderQueue();
            Queue deadLetterQueue = deadLetterQueue();

            TopicExchange topicExchange = topicExchange();
            DirectExchange deadLetterExchange = deadLetterExchange();

            amqpAdmin.declareQueue(orderQueue);
            amqpAdmin.declareExchange(topicExchange());
            amqpAdmin.declareBinding(orderQueueBinding(orderQueue, topicExchange));

            amqpAdmin.declareQueue(deadLetterQueue);
            amqpAdmin.declareExchange(deadLetterExchange);
            amqpAdmin.declareBinding(deadLetterQueueBinding(deadLetterQueue, deadLetterExchange));

            logger.info("Queues and exchanges created");
        };
    }
}
