package kr.co.strato.portal.common.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class SelectDto {
    private String id;
    private String text;
    private String value;
}
