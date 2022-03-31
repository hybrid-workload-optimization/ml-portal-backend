package kr.co.strato.portal.config.model;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.co.strato.domain.pod.model.PodPersistentVolumeClaimEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PersistentVolumeClaimDto {

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ResListDto{
        private Long id;
        private String accessType;
        private String namespace;
        private String name;
        private String status;
        private String storageClass;
        private String uid;
        private LocalDateTime createdAt;
    }

}
