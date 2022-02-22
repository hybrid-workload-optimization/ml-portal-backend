package kr.co.strato.portal.workload.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.fabric8.kubernetes.api.model.apps.StatefulSet;
import kr.co.strato.adapter.k8s.statefulset.service.StatefulSetAdapterService;
import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.domain.cluster.service.ClusterDomainService;
import kr.co.strato.domain.namespace.service.NamespaceDomainService;
import kr.co.strato.domain.statefulset.model.StatefulSetEntity;
import kr.co.strato.domain.statefulset.service.StatefulSetDomainService;
import kr.co.strato.global.error.exception.InternalServerException;
import kr.co.strato.global.util.Base64Util;
import kr.co.strato.global.util.DateUtil;
import kr.co.strato.portal.workload.model.StatefulSetDetailDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class StatefulSetDetailService {
    @Autowired
    private StatefulSetDomainService statefulSetDomainService;

    @Autowired
    private StatefulSetAdapterService statefulSetAdapterService;

    @Transactional(rollbackFor = Exception.class)
    public boolean deleteStatefulSet(Integer id){
        StatefulSetEntity s = statefulSetDomainService.get(id.longValue());
        Long clusterId = s.getNamespace().getClusterIdx().getClusterId();
        String namespaceName = s.getNamespace().getName();
        String resourceName = s.getStatefulSetName();

        boolean isDeleted = statefulSetAdapterService.delete(clusterId.intValue(), namespaceName, resourceName);
        if(isDeleted){
            return statefulSetDomainService.delete(id.longValue());
        }else{
            throw new InternalServerException("k8s statefulSet 삭제 실패");
        }
    }

    public List<Long> updateStatefulSet(Long statefulSetId, StatefulSetDetailDto.ReqUpdateDto reqUpdateDto){
        String yaml = Base64Util.decode(reqUpdateDto.getYaml());
        ClusterEntity cluster = statefulSetDomainService.getCluster(statefulSetId);
        Long clusterId = cluster.getClusterId();

        List<StatefulSet> statefulSets = statefulSetAdapterService.update(clusterId.intValue(), yaml);

        List<Long> ids = statefulSets.stream().map( s -> {
            try {
                String namespaceName = s.getMetadata().getNamespace();
                StatefulSetEntity updateStatefulSet = toEntity(s);

                Long id = statefulSetDomainService.update(updateStatefulSet, statefulSetId, clusterId.longValue(), namespaceName);

                return id;
            } catch (JsonProcessingException e) {
                log.error(e.getMessage(), e);
                throw new InternalServerException("json 파싱 에러");
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw new InternalServerException("statefulSet update error");
            }
        }).collect(Collectors.toList());

        return ids;

    }

    /**
     * k8s statefulSet model -> statefulSet entity
     * @param s
     * @return
     * @throws JsonProcessingException
     */
    private StatefulSetEntity toEntity(StatefulSet s) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        String name = s.getMetadata().getName();
        String namespace = s.getMetadata().getNamespace();
        String uid = s.getMetadata().getUid();
        String image = s.getSpec().getTemplate().getSpec().getContainers().get(0).getImage();
        String label = mapper.writeValueAsString(s.getMetadata().getLabels());
        String annotations = mapper.writeValueAsString(s.getMetadata().getAnnotations());
        String createAt = s.getMetadata().getCreationTimestamp();

        StatefulSetEntity statefulSet = StatefulSetEntity.builder()
                .statefulSetName(name)
                .statefulSetUid(uid)
                .image(image)
                .label(label)
                .annotation(annotations)
                .createdAt(DateUtil.strToLocalDateTime(createAt))
                .build();

        return statefulSet;
    }
}
