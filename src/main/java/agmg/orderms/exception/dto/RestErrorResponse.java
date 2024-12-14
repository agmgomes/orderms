package agmg.orderms.exception.dto;

import java.time.LocalDateTime;

public record RestErrorResponse(int status,
                                String message,
                                LocalDateTime timestamp) {
}
