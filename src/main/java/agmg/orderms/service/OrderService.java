package agmg.orderms.service;

import agmg.orderms.dto.CustomerOrdersResponse;
import agmg.orderms.dto.ItemDTO;
import agmg.orderms.dto.OrderDTO;
import agmg.orderms.dto.OrderWithoutCustomerId;
import agmg.orderms.exception.CustomerIdNotFound;
import agmg.orderms.model.Item;
import agmg.orderms.model.Order;
import agmg.orderms.publisher.RabbitMqPublisher;
import agmg.orderms.repository.OrderRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {
    private final Logger logger = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    SequenceGeneratorService sequenceGeneratorService;
    @Autowired
    RabbitMqPublisher rabbitMqPublisher;
    @Autowired
    OrderRepository orderRepository;

    public void publishOrder(OrderDTO orderDTO) {
        rabbitMqPublisher.publishOrder(orderDTO);
    }

    public void processOrder(OrderDTO orderDTO) {
        logger.info("Processing order for customer: {}", orderDTO.customerId());
        orderDTO.items().forEach(item ->
                logger.info("Item: {}, Quantity: {}, Price: {}", item.product(), item.quantity(), item.price())
        );

        Order order = new Order();
        order.setOrderId(sequenceGeneratorService.generateSequence("order_sequence"));
        order.setCustomerId(orderDTO.customerId());
        order.setItems(convertItems(orderDTO.items()));
        order.setTotal(getTotal(orderDTO.items()));

        this.orderRepository.save(order);
    }

    public CustomerOrdersResponse getListOrders(Long customerId) {
        logger.info("Fetching list orders for costumer: {}", customerId);

        List<Order> orders = this.orderRepository.findByCustomerId(customerId);

        if(orders.isEmpty()){
            logger.warn("No customer found for id: {}", customerId);
            throw new CustomerIdNotFound(customerId);
        }

        List<OrderWithoutCustomerId> orderList = orders.stream()
                .map( order -> new OrderWithoutCustomerId(order.getOrderId(), order.getItems(), order.getTotal()))
                .collect(Collectors.toList());

        logger.info("Orders list returned with success for customer id: {}", customerId);

        return new CustomerOrdersResponse(customerId, orderList);
    }

    private List<Item> convertItems(List<ItemDTO> itemsDTO) {
        return itemsDTO.stream()
                .map(ItemDTO::toItem)
                .collect(Collectors.toList());
    }

    private BigDecimal getTotal(List<ItemDTO> items){
        return items.stream()
                .map(i -> i.price().multiply(BigDecimal.valueOf(i.quantity())))
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);

    }

}
