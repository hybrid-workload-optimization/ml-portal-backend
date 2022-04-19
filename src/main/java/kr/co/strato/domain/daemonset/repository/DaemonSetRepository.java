package kr.co.strato.domain.daemonset.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.co.strato.domain.daemonset.model.DaemonSetEntity;

public interface DaemonSetRepository extends JpaRepository<DaemonSetEntity, Long>, CustomDaemonSetRepository {

	
}