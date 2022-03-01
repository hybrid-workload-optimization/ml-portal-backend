package kr.co.strato.global.error.handler;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import kr.co.strato.global.error.exception.AleadyUserClusterException;
import kr.co.strato.global.error.exception.AlreadyExistResourceException;
import kr.co.strato.global.error.exception.AuthFailException;
import kr.co.strato.global.error.exception.BadRequestException;
import kr.co.strato.global.error.exception.CreateProjectFailException;
import kr.co.strato.global.error.exception.DeleteProjectFailException;
import kr.co.strato.global.error.exception.InternalServerException;
import kr.co.strato.global.error.exception.NotFoundProjectException;
import kr.co.strato.global.error.exception.NotFoundResourceException;
import kr.co.strato.global.error.exception.NotFoundUserException;
import kr.co.strato.global.error.exception.PermissionDenyException;
import kr.co.strato.global.error.exception.PortalException;
import kr.co.strato.global.error.exception.UpdateProjectFailException;
import kr.co.strato.global.error.type.AuthErrorType;
import kr.co.strato.global.error.type.BasicErrorType;
import kr.co.strato.global.error.type.ProjectErrorType;
import kr.co.strato.global.model.ResponseType;
import kr.co.strato.global.model.ResponseWrapper;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalExceptionHandler {

	
	@ExceptionHandler(PortalException.class)
    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    ResponseWrapper handlePortalException(PortalException e){
        BasicErrorType errorType = e.getErrorType();
        return getResponse(errorType);
    }
	
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

    @ExceptionHandler(CreateProjectFailException.class)
    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    ResponseWrapper unhandleCreateProjectFailException(CreateProjectFailException e) {
        e.printStackTrace();
    	ProjectErrorType errorType = ProjectErrorType.FAIL_PROJECT_CREATE;
        return getResponse(errorType);
    }
    
    @ExceptionHandler(UpdateProjectFailException.class)
    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    ResponseWrapper unhandleUpdateProjectFailException(UpdateProjectFailException e) {
    	e.printStackTrace();
    	ProjectErrorType errorType = ProjectErrorType.FAIL_PROJECT_UPDATE;
        return getResponse(errorType);
    }
    
    @ExceptionHandler(DeleteProjectFailException.class)
    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    ResponseWrapper unhandleDeleteProjectFailException(DeleteProjectFailException e) {
    	e.printStackTrace();
    	ProjectErrorType errorType = ProjectErrorType.FAIL_PROJECT_DELETE;
        return getResponse(errorType);
    }
    
    @ExceptionHandler(NotFoundProjectException.class)
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    ResponseWrapper unhandleNotFoundProjectException(NotFoundProjectException e) {
        ProjectErrorType errorType = ProjectErrorType.NOT_FOUND_PROJECT;
        return getResponse(errorType);
    }
    
    @ExceptionHandler(AleadyUserClusterException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    ResponseWrapper unhandleAleadyUserClusterException(AleadyUserClusterException e) {
        ProjectErrorType errorType = ProjectErrorType.ALEADY_USE_CLUSTER;
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
