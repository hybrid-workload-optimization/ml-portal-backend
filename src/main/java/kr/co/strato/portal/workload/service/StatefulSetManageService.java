package kr.co.strato.portal.workload.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.fabric8.kubernetes.api.model.apps.StatefulSet;
import kr.co.strato.adapter.k8s.statefulset.service.StatefulSetAdapterService;
import kr.co.strato.domain.namespace.service.NamespaceDomainService;
import kr.co.strato.domain.statefulset.model.StatefulSetEntity;
import kr.co.strato.domain.statefulset.service.StatefulSetDomainService;
import kr.co.strato.global.error.exception.InternalServerException;
import kr.co.strato.global.util.DateUtil;
import kr.co.strato.portal.cluster.model.ClusterNodeDto;
import kr.co.strato.portal.workload.model.StatefulSetDto;
import kr.co.strato.portal.workload.model.StatefulSetDtoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class StatefulSetManageService {
    @Autowired
    private StatefulSetDomainService statefulSetDomainService;

    @Autowired
    private StatefulSetAdapterService statefulSetAdapterService;

    @Autowired
    private NamespaceDomainService namespaceDomainService;


    @Transactional(rollbackFor = Exception.class)
    public List<Long> createStatefulSet(StatefulSetDto.ReqCreateDto reqCreateDto){
        Integer clusterId = reqCreateDto.getClusterId();
        String yaml = reqCreateDto.getYaml();

        List<StatefulSet> statefulSets = statefulSetAdapterService.create(clusterId, yaml);

        ObjectMapper mapper = new ObjectMapper();
        List<Long> ids = statefulSets.stream().map( s -> {
            try {
                //k8s Object -> Entity
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

                //save
                Long id = statefulSetDomainService.registerStatefulSet(statefulSet, clusterId, namespace);

                return id;
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw new InternalServerException("statefulSet register error");
            }
        }).collect(Collectors.toList());

        return ids;
    }

    public Page<StatefulSetDto.ResListDto> getStatefulSetList(Pageable pageable, StatefulSetDto.SearchParam searchParam){
        Page<StatefulSetEntity> statefulSets = statefulSetDomainService.getStatefulSetList(pageable, searchParam.getProjectId(), searchParam.getClsuterId(), searchParam.getNamespaceId());
        List<StatefulSetDto.ResListDto> dtoList = statefulSets.stream().map(e -> StatefulSetDtoMapper.INSTANCE.toResListDto(e)).collect(Collectors.toList());
        Page<StatefulSetDto.ResListDto> pages = new PageImpl<>(dtoList, pageable, statefulSets.getTotalElements());

        return pages;
    }
}
