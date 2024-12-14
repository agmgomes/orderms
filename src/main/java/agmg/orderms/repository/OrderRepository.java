package agmg.orderms.repository;

import agmg.orderms.model.Order;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface OrderRepository extends MongoRepository<Order, Long> {
    List<Order> findByCustomerId(Long customerId);
}
