package kr.co.strato.portal.workload.service;

import kr.co.strato.adapter.k8s.statefulset.service.StatefulSetAdapterService;
import kr.co.strato.domain.statefulset.model.StatefulSetEntity;
import kr.co.strato.domain.statefulset.service.StatefulSetDomainService;
import kr.co.strato.global.error.exception.InternalServerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
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
}
