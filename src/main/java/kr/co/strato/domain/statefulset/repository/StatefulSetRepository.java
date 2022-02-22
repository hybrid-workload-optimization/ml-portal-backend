package kr.co.strato.domain.statefulset.repository;

import kr.co.strato.domain.statefulset.model.StatefulSetEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatefulSetRepository extends JpaRepository<StatefulSetEntity, Long>, CustomStatefulSetRepository {
}
