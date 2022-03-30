package kr.co.strato.global.error.handler;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import kr.co.strato.global.error.exception.SsoConnectionException;
import kr.co.strato.global.error.type.SsoErrorType;
import kr.co.strato.global.model.ResponseType;
import kr.co.strato.global.model.ResponseWrapper;

@RestControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
public class SsoExceptionHandler {
	
	@ExceptionHandler(SsoConnectionException.class)
	@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
	ResponseWrapper handleSsoConncetionException(SsoConnectionException e) {
		e.printStackTrace();
		SsoErrorType errorType = e.getErrorType();
		return getResponse(errorType);
	}
	
	
	private ResponseWrapper getResponse(ResponseType errorType){
        return new ResponseWrapper<>(errorType);
    }

}
