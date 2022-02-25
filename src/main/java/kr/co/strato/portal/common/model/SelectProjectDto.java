package kr.co.strato.portal.common.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SelectProjectDto {
    private String id;
    private String text;
    private String value;
}
