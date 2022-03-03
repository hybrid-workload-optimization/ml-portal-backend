package kr.co.strato.domain.menu.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.co.strato.domain.menu.model.MenuEntity;

public interface MenuRepository extends JpaRepository<MenuEntity, Long> {

}
