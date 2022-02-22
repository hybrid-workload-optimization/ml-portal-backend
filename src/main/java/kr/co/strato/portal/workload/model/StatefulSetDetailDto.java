package kr.co.strato.portal.workload.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StatefulSetDetailDto {
    @Getter
    @Setter
    @NoArgsConstructor
    public static class ReqUpdateDto{
        private String yaml;
    }

}
