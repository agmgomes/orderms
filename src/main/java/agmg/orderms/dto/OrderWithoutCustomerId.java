package agmg.orderms.dto;

import agmg.orderms.model.Item;

import java.math.BigDecimal;
import java.util.List;

public record OrderWithoutCustomerId(Long orderId,
                                     List<Item> items,
                                     BigDecimal total) {}
