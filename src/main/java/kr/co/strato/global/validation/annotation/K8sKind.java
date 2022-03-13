package kr.co.strato.global.validation.annotation;

import kr.co.strato.global.validation.k8sKindConstrainValidator;
import kr.co.strato.global.validation.model.K8sKindType;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Constraint(validatedBy = k8sKindConstrainValidator.class)
public @interface K8sKind {
    String message() default "kind 속성의 값이 적합하지 않습니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    K8sKindType value();
}
