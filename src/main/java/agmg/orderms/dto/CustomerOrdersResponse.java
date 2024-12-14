package agmg.orderms.dto;

import java.util.List;

public record CustomerOrdersResponse (Long customerId,
                                      List<OrderWithoutCustomerId> orders) {
}

