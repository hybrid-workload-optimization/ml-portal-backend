package kr.co.strato.portal.setting.controller;

import java.io.IOException;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import kr.co.strato.global.error.exception.PortalException;
import kr.co.strato.global.model.ResponseWrapper;
import kr.co.strato.portal.setting.model.CodeDto;
import kr.co.strato.portal.setting.model.GroupCodeDto;
import kr.co.strato.portal.setting.service.CodeMgmtService;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@RestController
@RequestMapping("/api/v1/setting/code-mgmt")
public class CodeMgmtController {

	@Autowired
	private CodeMgmtService codeService;


	/**
	 * 목록
	 * @return
	 */
	@GetMapping("group")
	@ResponseStatus(HttpStatus.OK)
	public ResponseWrapper<List<GroupCodeDto>> getGroupCodeList() {
		return new ResponseWrapper<>(codeService.getGroupCodeList());
	}
	

	@GetMapping("group/{groupCode}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseWrapper<GroupCodeDto> getGroupCode(@PathVariable String groupCode) {
		return new ResponseWrapper<>(codeService.getGroupCode(groupCode));
	}


	@PostMapping("group")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseWrapper<String> postGroupCode(@RequestBody GroupCodeDto groupCodeDto) {
		
		print("group", groupCodeDto);
		String id = null;
		
		try {
			//groupCodeDto.setCreatedAt((LocalDateTime.now()).format(DateTimeFormatter.ISO_DATE_TIME));
			groupCodeDto.setCreatedAt(LocalDateTime.now());
			id = codeService.saveGroupCode(groupCodeDto);
		} catch (Exception e) {
			log.error("Error has occured", e);
			throw new PortalException(e.getMessage());
		} finally {
		}
		
		return new ResponseWrapper<>(id);
	}

	@PatchMapping("group")
	@ResponseStatus(HttpStatus.OK)
	public ResponseWrapper<String> patchGroupCode(@RequestBody GroupCodeDto groupCodeDto) {
		
		print("group", groupCodeDto);
		String id = null;
		
		try {

			id = codeService.updateGroupCode(groupCodeDto);
		} catch (Exception e) {
			log.error("Error has occured", e);
			throw new PortalException(e.getMessage());
		} finally {
		}
		
		return new ResponseWrapper<>(id);
	}
	
	

	@GetMapping("code/{codeIdx}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseWrapper<CodeDto> getCode(@PathVariable Long codeIdx) {
		return new ResponseWrapper<>(codeService.getCode(codeIdx));
	}


	@PostMapping("code")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseWrapper<Long> postCode(@RequestBody CodeDto codeDto) {
		
		print("code", codeDto);
		Long id = null;
		
		try {
			codeDto.setCreatedAt(LocalDateTime.now());
			id = codeService.saveCode(codeDto);
		} catch (Exception e) {
			log.error("Error has occured", e);
			throw new PortalException(e.getMessage());
		} finally {
		}
		
		return new ResponseWrapper<>(id);
	}

	
	@PatchMapping("code")
	@ResponseStatus(HttpStatus.OK)
	public ResponseWrapper<Long> patchGroupCode(@RequestBody CodeDto codeDto) {
		
		print("code", codeDto);
		Long id = null;
		
		try {
			codeDto.setUpdatedAt(LocalDateTime.now());
			id = codeService.updateCode(codeDto);
		} catch (Exception e) {
			log.error("Error has occured", e);
			throw new PortalException(e.getMessage());
		} finally {
		}
		
		return new ResponseWrapper<>(id);
	}
	
	void print(String title, Object object) {
		if (object == null) {
			log.info("[{}] null", title);
			return;
		}

		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode jsonNode = mapper.convertValue(object, JsonNode.class);
			log.info("[{}] {}", title, mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNode));
		} catch (IOException e) {
			log.info("[{}] {}", title, e.getMessage());
		}
	}

	
	
}
