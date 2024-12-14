package agmg.orderms.controller;

import agmg.orderms.dto.CustomerOrdersResponse;
import agmg.orderms.dto.OrderDTO;
import agmg.orderms.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {
    @Autowired
    OrderService orderService;

    @PostMapping("/send")
    public ResponseEntity<String> sendOrder(@RequestBody @Valid OrderDTO orderDTO){
        orderService.publishOrder(orderDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body("Order sent to RabbitMQ.");
    }

    @GetMapping("customers/{customerId}")
    public ResponseEntity<CustomerOrdersResponse> listOrders(@PathVariable("customerId") Long customerId){
        CustomerOrdersResponse response = orderService.getListOrders(customerId);
        return ResponseEntity.ok(response);
    }
}
