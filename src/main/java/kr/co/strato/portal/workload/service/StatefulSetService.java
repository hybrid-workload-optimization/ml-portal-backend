package kr.co.strato.portal.workload.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import kr.co.strato.domain.namespace.model.NamespaceEntity;
import kr.co.strato.domain.namespace.service.NamespaceDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.fabric8.kubernetes.api.model.apps.StatefulSet;
import kr.co.strato.adapter.k8s.statefulset.service.StatefulSetAdapterService;
import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.domain.cluster.service.ClusterDomainService;
import kr.co.strato.domain.statefulset.model.StatefulSetEntity;
import kr.co.strato.domain.statefulset.service.StatefulSetDomainService;
import kr.co.strato.global.error.exception.InternalServerException;
import kr.co.strato.global.util.Base64Util;
import kr.co.strato.global.util.DateUtil;
import kr.co.strato.portal.workload.model.StatefulSetDetailDto;
import kr.co.strato.portal.workload.model.StatefulSetDetailDtoMapper;
import kr.co.strato.portal.workload.model.StatefulSetDto;
import kr.co.strato.portal.workload.model.StatefulSetDtoMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class StatefulSetService {
    @Autowired
    private StatefulSetDomainService statefulSetDomainService;

    @Autowired
    private StatefulSetAdapterService statefulSetAdapterService;

    @Autowired
    private ClusterDomainService clusterDomainService;

    @Autowired
    private NamespaceDomainService namespaceDomainService;

    @Transactional(rollbackFor = Exception.class)
    public List<Long> createStatefulSet(StatefulSetDto.ReqCreateDto reqCreateDto){
        Long clusterIdx = reqCreateDto.getClusterIdx();
        ClusterEntity cluster = clusterDomainService.get(clusterIdx);

        String yaml = Base64Util.decode(reqCreateDto.getYaml());

        List<StatefulSet> statefulSets = statefulSetAdapterService.create(cluster.getClusterId(), yaml);

        List<Long> ids = statefulSets.stream().map( s -> {
            try {
                String namespaceName = s.getMetadata().getNamespace();
                StatefulSetEntity statefulSet = toEntity(s);

                Long id = statefulSetDomainService.register(statefulSet, cluster, namespaceName);

                return id;
            } catch (JsonProcessingException e) {
                log.error(e.getMessage(), e);
                throw new InternalServerException("json 파싱 에러");
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw new InternalServerException("Error registering the updated statefulSet in the db");
            }
        }).collect(Collectors.toList());

        return ids;
    }

    public Page<StatefulSetDto.ResListDto> getStatefulSets(Pageable pageable, StatefulSetDto.SearchParam searchParam){
        Long clusterId = searchParam.getClusterId();
        Long namespaceId = searchParam.getNamespaceId();
        List<StatefulSet> statefulSets = new ArrayList<>();

        if(namespaceId == null || namespaceId == 0){
            statefulSets = statefulSetAdapterService.getList(clusterId);
        }else{
            NamespaceEntity namespaceEntity = namespaceDomainService.getDetail(namespaceId);
            statefulSets = statefulSetAdapterService.getList(clusterId, namespaceEntity.getName());
        }
        Map<String, StatefulSet> maps = statefulSets.stream().collect(Collectors.toMap(
                e1 -> e1.getMetadata().getUid(),
                e2-> e2
        ));

        Page<StatefulSetEntity> statefulSetEntities = statefulSetDomainService.getStatefulSets(pageable, searchParam.getProjectId(), clusterId, namespaceId);
        List<StatefulSetDto.ResListDto> dtos = statefulSetEntities.stream().map(
                e -> {
                    String uid = e.getStatefulSetUid();
                    if(maps.containsKey(uid)){
                        return StatefulSetDtoMapper.INSTANCE.toResListDto(e, maps.get(uid));
                    }
                    return StatefulSetDtoMapper.INSTANCE.toResListDto(e);
                }).collect(Collectors.toList());
        Page<StatefulSetDto.ResListDto> page = new PageImpl<>(dtos, pageable, statefulSetEntities.getTotalElements());

        return page;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean deleteStatefulSet(Long id){
        StatefulSetEntity statefulSetEntity = statefulSetDomainService.get(id);
        Long clusterId = statefulSetEntity.getNamespace().getCluster().getClusterId();
        String namespaceName = statefulSetEntity.getNamespace().getName();
        String statefulSetName = statefulSetEntity.getStatefulSetName();

        boolean isDeleted = statefulSetAdapterService.delete(clusterId, namespaceName, statefulSetName);
        if(isDeleted){
            return statefulSetDomainService.delete(id);
        }else{
            throw new InternalServerException("Fail to delete the k8s statefulSet");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public List<Long> updateStatefulSet(Long statefulSetId, StatefulSetDetailDto.ReqUpdateDto reqUpdateDto){
        String yaml = Base64Util.decode(reqUpdateDto.getYaml());
        ClusterEntity cluster = statefulSetDomainService.getClusterEntity(statefulSetId);
        Long clusterId = cluster.getClusterId();

        List<StatefulSet> statefulSets = statefulSetAdapterService.update(clusterId, yaml);

        List<Long> ids = statefulSets.stream().map( s -> {
            try {
                StatefulSetEntity updateStatefulSet = toEntity(s);

                Long id = statefulSetDomainService.update(statefulSetId, updateStatefulSet);

                return id;
            } catch (JsonProcessingException e) {
                log.error(e.getMessage(), e);
                throw new InternalServerException("json 파싱 에러");
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw new InternalServerException("Error registering the updated statefulSet in the db");
            }
        }).collect(Collectors.toList());

        return ids;
    }

    public StatefulSetDetailDto.ResDetailDto getStatefulSet(Long statefulSetId){
        //get statefulSet entity
        StatefulSetEntity entity = statefulSetDomainService.get(statefulSetId);

        //get k8s statefulSet model
        String statefulSetName = entity.getStatefulSetName();
        String namespaceName = entity.getNamespace().getName();
        Long clusterId = entity.getNamespace().getCluster().getClusterId();
        StatefulSet k8sStatefulSet = statefulSetAdapterService.get(clusterId, namespaceName, statefulSetName);

        StatefulSetDetailDto.ResDetailDto dto = StatefulSetDetailDtoMapper.INSTANCE.toResDetailDto(entity, k8sStatefulSet);

        return dto;
    }

    public String getStatefulSetYaml(Long statefulSetId){
        //get statefulSet entity
        StatefulSetEntity entity = statefulSetDomainService.get(statefulSetId);

        //get k8s statefulSet model
        String statefulSetName = entity.getStatefulSetName();
        String namespaceName = entity.getNamespace().getName();
        Long clusterId = entity.getNamespace().getCluster().getClusterId();

        String yaml = statefulSetAdapterService.getYaml(clusterId, namespaceName, statefulSetName);
        yaml = Base64Util.encode(yaml);

        return yaml;
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
