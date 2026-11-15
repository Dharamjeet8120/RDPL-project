package rdpl_timetable_backend.config;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.dao.DataIntegrityViolationException;

/**
 * Returns clean JSON errors instead of HTTP 500 stack traces.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<Map<String, Object>> badRequest(IllegalArgumentException ex) {
		String message = ex.getMessage() == null ? "Invalid request" : ex.getMessage();
		HttpStatus status = message.toLowerCase().contains("not found") ? HttpStatus.NOT_FOUND : HttpStatus.BAD_REQUEST;
		return build(status, message);
	}

	@ExceptionHandler(MissingServletRequestParameterException.class)
	public ResponseEntity<Map<String, Object>> missingParam(MissingServletRequestParameterException ex) {
		return build(HttpStatus.BAD_REQUEST, "Missing request parameter: " + ex.getParameterName());
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<Map<String, Object>> dataConflict(DataIntegrityViolationException ex) {
		return build(HttpStatus.CONFLICT, "This record cannot be changed or deleted because it is referenced by other data.");
	}

	@ExceptionHandler(IllegalStateException.class)
	public ResponseEntity<Map<String, Object>> conflict(IllegalStateException ex) {
		return build(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
	}

	private ResponseEntity<Map<String, Object>> build(HttpStatus status, String message) {
		Map<String, Object> body = new LinkedHashMap<>();
		body.put("status", status.value());
		body.put("error", status.getReasonPhrase());
		body.put("message", message);
		return ResponseEntity.status(status).body(body);
	}
}
