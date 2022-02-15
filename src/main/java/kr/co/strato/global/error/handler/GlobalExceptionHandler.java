package kr.co.strato.global.error.handler;

import kr.co.strato.global.error.exception.*;
import kr.co.strato.global.error.type.AuthErrorType;
import kr.co.strato.global.error.type.BasicErrorType;
import kr.co.strato.global.model.ResponseType;
import kr.co.strato.global.model.ResponseWrapper;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundResourceException.class)
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    ResponseWrapper handleNotFoundException(NotFoundResourceException e){
        BasicErrorType errorType = e.getErrorType();
        return getResponse(errorType);
    }


    @ExceptionHandler(AlreadyExistResourceException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    ResponseWrapper handleAlreadyExistResourceException(AlreadyExistResourceException e){
        BasicErrorType errorType = e.getErrorType();
        return getResponse(errorType);
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    ResponseWrapper handleBadRequestException(BadRequestException e){
        BasicErrorType errorType = e.getErrorType();
        return getResponse(errorType);
    }

    @ExceptionHandler(InternalServerException.class)
    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    ResponseWrapper handleInternalServerException(InternalServerException e){
        BasicErrorType errorType = e.getErrorType();
        return getResponse(errorType);
    }

    @ExceptionHandler(AuthFailException.class)
    @ResponseStatus(code = HttpStatus.UNAUTHORIZED)
    ResponseWrapper handleAuthFailException(AuthFailException e){
        AuthErrorType errorType = e.getErrorType();
        return getResponse(errorType);
    }

    @ExceptionHandler(NotFoundUserException.class)
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    ResponseWrapper handleNotFoundUserException(NotFoundUserException e){
        AuthErrorType errorType = e.getErrorType();
        return getResponse(errorType);
    }

    @ExceptionHandler(PermissionDenyException.class)
    @ResponseStatus(code = HttpStatus.FORBIDDEN)
    ResponseWrapper handlePermissionDenyException(PermissionDenyException e){
        AuthErrorType errorType = e.getErrorType();
        return getResponse(errorType);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    ResponseWrapper handleMethodArgumentNotValidException(MethodArgumentNotValidException e){
        BasicErrorType errorType = BasicErrorType.BAD_REQUEST_ARGS;
        errorType.setDetail(e.getBindingResult().getAllErrors().get(0).getDefaultMessage());
        return getResponse(errorType);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    ResponseWrapper unhandleException(Exception e){
        e.printStackTrace();
        BasicErrorType errorType = BasicErrorType.SERVER_ERROR;
        return getResponse(errorType);
    }

    private ResponseWrapper getResponse(ResponseType errorType){
        return new ResponseWrapper<>(errorType);
    }
}
