package kr.co.strato.portal.workload.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
import kr.co.strato.domain.common.service.InNamespaceDomainService;
import kr.co.strato.domain.namespace.model.NamespaceEntity;
import kr.co.strato.domain.namespace.service.NamespaceDomainService;
import kr.co.strato.domain.project.model.ProjectEntity;
import kr.co.strato.domain.project.service.ProjectDomainService;
import kr.co.strato.domain.statefulset.model.StatefulSetEntity;
import kr.co.strato.domain.statefulset.service.StatefulSetDomainService;
import kr.co.strato.global.error.exception.InternalServerException;
import kr.co.strato.global.util.Base64Util;
import kr.co.strato.global.util.DateUtil;
import kr.co.strato.portal.common.service.InNamespaceService;
import kr.co.strato.portal.common.service.ProjectAuthorityService;
import kr.co.strato.portal.setting.model.UserDto;
import kr.co.strato.portal.workload.model.StatefulSetDetailDto;
import kr.co.strato.portal.workload.model.StatefulSetDetailDtoMapper;
import kr.co.strato.portal.workload.model.StatefulSetDto;
import kr.co.strato.portal.workload.model.StatefulSetDtoMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class StatefulSetService extends InNamespaceService {
    @Autowired
    private StatefulSetDomainService statefulSetDomainService;

    @Autowired
    private StatefulSetAdapterService statefulSetAdapterService;

    @Autowired
    private ClusterDomainService clusterDomainService;

    @Autowired
    private NamespaceDomainService namespaceDomainService;

    @Autowired
    private ProjectDomainService projectDomainService;
    
    @Autowired
	ProjectAuthorityService projectAuthorityService;
    

    @Transactional(rollbackFor = Exception.class)
    public List<Long> createStatefulSet(StatefulSetDto.ReqCreateDto reqCreateDto){
        Long clusterIdx = reqCreateDto.getClusterIdx();
        ClusterEntity cluster = clusterDomainService.get(clusterIdx);
        
        //이름 중복 채크
        duplicateCheckResourceCreation(clusterIdx, reqCreateDto.getYaml());

        String yaml = Base64Util.decode(reqCreateDto.getYaml());

        List<StatefulSet> statefulSets = statefulSetAdapterService.create(cluster.getClusterId(), yaml);

        List<Long> ids = statefulSets.stream().map( s -> {
            try {
                String namespaceName = s.getMetadata().getNamespace();
                StatefulSetEntity statefulSet = toEntity(s);
                statefulSet.setYaml(yaml);
                Long id = statefulSetDomainService.register(statefulSet, clusterIdx, namespaceName);

                return id;
            } catch (JsonProcessingException e) {
                log.error(e.getMessage(), e);
                throw new InternalServerException("Error parsing json");
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw new InternalServerException("Error registering the updated statefulSet in the db");
            }
        }).collect(Collectors.toList());

        return ids;
    }

    public Page<StatefulSetDto.ResListDto> getStatefulSets(Pageable pageable, StatefulSetDto.SearchParam searchParam){
    	Long projectIdx = searchParam.getProjectIdx();
        Long clusterIdx = searchParam.getClusterIdx();
        Long namespaceIdx = searchParam.getNamespaceIdx();
        ClusterEntity clusterEntity = clusterDomainService.get(clusterIdx);
        Long clusterId = clusterEntity.getClusterId();

        List<StatefulSet> statefulSets = new ArrayList<>();
        try {
            if (namespaceIdx == null || namespaceIdx == 0) {
                statefulSets = statefulSetAdapterService.getList(clusterId);
            } else {
                NamespaceEntity namespaceEntity = namespaceDomainService.getDetail(namespaceIdx);
                statefulSets = statefulSetAdapterService.getList(clusterId, namespaceEntity.getName());
            }
        }catch (Exception e){

        }

        Map<String, StatefulSet> maps = statefulSets.stream().collect(Collectors.toMap(
                e1 -> e1.getMetadata().getUid(),
                e2-> e2
        ));

        //Page<StatefulSetEntity> statefulSetEntities = statefulSetDomainService.getStatefulSets(pageable, clusterIdx, clusterId, namespaceIdx);
        Page<StatefulSetEntity> statefulSetEntities = statefulSetDomainService.getStatefulSets(pageable, projectIdx, clusterIdx, namespaceIdx);
        List<StatefulSetDto.ResListDto> dtos = statefulSetEntities.stream().map(
                e -> {
                    String uid = e.getStatefulSetUid();System.out.println();
                    if(maps.containsKey(uid)){
                        return StatefulSetDtoMapper.INSTANCE.toResListDto(e, maps.get(uid));
                    }
                    return StatefulSetDtoMapper.INSTANCE.toResListDto(e);
                }).collect(Collectors.toList());
        Page<StatefulSetDto.ResListDto> page = new PageImpl<>(dtos, pageable, statefulSetEntities.getTotalElements());

        return page;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean deleteStatefulSet(Long statefulSetId){
        StatefulSetEntity statefulSetEntity = statefulSetDomainService.get(statefulSetId);
        Long clusterId = statefulSetEntity.getNamespace().getCluster().getClusterId();
        String namespaceName = statefulSetEntity.getNamespace().getName();
        String statefulSetName = statefulSetEntity.getStatefulSetName();

        boolean isDeleted = statefulSetAdapterService.delete(clusterId, namespaceName, statefulSetName);
        if(isDeleted){
            return statefulSetDomainService.delete(statefulSetId);
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
                updateStatefulSet.setYaml(yaml);

                Long id = statefulSetDomainService.update(statefulSetId, updateStatefulSet);

                return id;
            } catch (JsonProcessingException e) {
                log.error(e.getMessage(), e);
                throw new InternalServerException("Error parsing json");
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw new InternalServerException("Error registering the updated statefulSet in the db");
            }
        }).collect(Collectors.toList());

        return ids;
    }

    public StatefulSetDetailDto.ResDetailDto getStatefulSet(Long statefulSetId, UserDto loginUser){
        //get statefulSet entity
        StatefulSetEntity entity = statefulSetDomainService.get(statefulSetId);
        
        //get k8s statefulSet model
        String statefulSetName = entity.getStatefulSetName();
        String namespaceName = entity.getNamespace().getName();
        Long clusterId = entity.getNamespace().getCluster().getClusterId();
        Long clusterIdx = entity.getNamespace().getCluster().getClusterIdx();
        String clusterName = entity.getNamespace().getCluster().getClusterName();
        ProjectEntity projectEntity = projectDomainService.getProjectDetailByClusterId(clusterIdx);
        String projectName = projectEntity.getProjectName();
        
        Long projectIdx = projectEntity.getId();
		
		//메뉴 접근권한 채크.
        projectAuthorityService.chechAuthority(getMenuCode(), projectIdx, loginUser);

        StatefulSet k8sStatefulSet = statefulSetAdapterService.get(clusterId, namespaceName, statefulSetName);

        StatefulSetDetailDto.ResDetailDto dto = StatefulSetDetailDtoMapper.INSTANCE.toResDetailDto(entity, k8sStatefulSet, clusterId, projectName, clusterName, clusterIdx);
        dto.setProjectIdx(projectIdx);
        return dto;
    }

    public String getStatefulSetYaml(Long statefulSetId){
        //get statefulSet entity
        StatefulSetEntity entity = statefulSetDomainService.get(statefulSetId);
        String yaml = entity.getYaml();
        
        if(yaml == null) {
        	 //get k8s statefulSet model
            String statefulSetName = entity.getStatefulSetName();
            String namespaceName = entity.getNamespace().getName();
            Long clusterId = entity.getNamespace().getCluster().getClusterId();

            yaml = statefulSetAdapterService.getYaml(clusterId, namespaceName, statefulSetName);
        }
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

	@Override
	protected InNamespaceDomainService getDomainService() {
		return statefulSetDomainService;
	}
}
