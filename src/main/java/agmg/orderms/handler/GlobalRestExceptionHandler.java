package agmg.orderms.handler;

import agmg.orderms.exception.GlobalException;
import agmg.orderms.exception.dto.RestErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class GlobalRestExceptionHandler {
    @ExceptionHandler(GlobalException.class)
    public ResponseEntity<RestErrorResponse> handleGlobalException(GlobalException e){
        return e.restErrorResponse();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RestErrorListResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        var fieldErrors = e.getFieldErrors()
                .stream()
                .map(f-> new InvalidParams(f.getField(), f.getDefaultMessage()))
                .toList();

        RestErrorListResponse errorListResponse = new RestErrorListResponse(
                HttpStatus.BAD_REQUEST.value(),
                fieldErrors,
                LocalDateTime.now()
        );

        return new ResponseEntity<>(errorListResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<RestErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        RestErrorResponse errorResponse = new RestErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Malformed JSON Request",
                LocalDateTime.now()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<RestErrorResponse> handleNoResourceFoundException(NoResourceFoundException e) {
        RestErrorResponse errorResponse = new RestErrorResponse(
                e.getStatusCode().value(),
                e.getMessage(),
                LocalDateTime.now()
        );

        return new ResponseEntity<>(errorResponse, e.getStatusCode());
    }

    private record InvalidParams(String name, String reason) {}

    public record RestErrorListResponse(int status,
                                        List<InvalidParams> errorsList,
                                        LocalDateTime timestamp) {
    }
}
