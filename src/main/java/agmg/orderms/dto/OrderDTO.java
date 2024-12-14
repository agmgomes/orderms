package agmg.orderms.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record OrderDTO(
        @NotNull
        Long customerId,
        @NotEmpty
        List<@Valid ItemDTO> items
) {}
