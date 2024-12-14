package agmg.orderms.exception;

import agmg.orderms.exception.dto.RestErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

public class GlobalException extends RuntimeException {
    public ResponseEntity<RestErrorResponse> restErrorResponse() {
        RestErrorResponse restErrorResponse = new RestErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                LocalDateTime.now()
        );

        return new ResponseEntity<>(restErrorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
