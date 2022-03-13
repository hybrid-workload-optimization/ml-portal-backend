package kr.co.strato.global.validation;

import kr.co.strato.global.util.Base64Util;
import kr.co.strato.global.validation.annotation.K8sKind;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.io.BufferedReader;
import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class k8sKindConstrainValidator implements ConstraintValidator<K8sKind, String> {
    private String kind;

    @Override
    public void initialize(K8sKind k8sKind) {
        kind = k8sKind.value().toString();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if(value == null){
            return false;
        }
        if(Base64.isBase64(value.getBytes())){
            value = Base64Util.decode(value);
        }
        BufferedReader reader = new BufferedReader(new StringReader(value));
        try {
            String line = "";
            Pattern p = Pattern.compile("^(?i)KIND:\\s*(.+)$");

            while((line = reader.readLine()) != null){
                Matcher m = p.matcher(line);
                if(m.matches()){
                    String kindValue = m.group(1);
                    if(!kind.toUpperCase().equals(kindValue.trim().toUpperCase())){
                        return false;
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
        return true;
    }
}
