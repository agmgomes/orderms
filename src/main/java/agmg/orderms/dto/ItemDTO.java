package agmg.orderms.dto;

import agmg.orderms.model.Item;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record ItemDTO(
        @NotBlank
        String product,
        @NotNull @Positive
        Long quantity,
        @NotNull @DecimalMin("0.0")
        BigDecimal price
) {
        public Item toItem(){
                return new Item(product, quantity, price);
        }
}
