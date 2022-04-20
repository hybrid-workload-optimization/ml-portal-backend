package kr.co.strato.domain.menu.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.strato.domain.menu.model.MenuEntity;
import kr.co.strato.domain.menu.repository.MenuRepository;

@Service
public class MenuDomainService {
	@Autowired
	private MenuRepository menuRepository;
	
	public List<MenuEntity> getAllMenu(){
		List<MenuEntity> menuList = menuRepository.findAll();
		return menuList;
	}
	
	public MenuEntity getById(Long menuIdx) {
		return menuRepository.getOne(menuIdx);
	}
}
