package agmg.orderms.model;

import agmg.orderms.service.SequenceGeneratorService;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "tb_orders")
public class Order {
    @MongoId
    private Long orderId;

    @Indexed(name = "customer_id_index")
    private Long customerId;

    private List<Item> items;

    @Field(targetType = FieldType.DECIMAL128)
    private BigDecimal total;

    public Order(Long customerId, List<Item> items, BigDecimal total, SequenceGeneratorService sequenceGeneratorService) {
        this.orderId = sequenceGeneratorService.generateSequence("order_sequence");
        this.customerId = customerId;
        this.items = items;
        this.total = total;
    }
}
