package agmg.orderms.exception;

import agmg.orderms.exception.dto.RestErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

public class CustomerIdNotFound extends GlobalException {
    private final Long id;
    public CustomerIdNotFound(Long id) {
        this.id = id;
    }

    @Override
    public ResponseEntity<RestErrorResponse> restErrorResponse() {
        RestErrorResponse errorResponse = new RestErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                "Customer with id: " + id + " not found",
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
}
