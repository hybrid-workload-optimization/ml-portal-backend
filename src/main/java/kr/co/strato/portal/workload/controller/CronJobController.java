package kr.co.strato.portal.workload.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import kr.co.strato.global.model.PageRequest;
import kr.co.strato.global.model.ResponseWrapper;
import kr.co.strato.portal.common.controller.CommonController;
import kr.co.strato.portal.workload.model.CronJobArgDto;
import kr.co.strato.portal.workload.model.CronJobDto;
import kr.co.strato.portal.workload.service.CronJobService;

@RequestMapping("/api/v1/workload")
@RestController
public class CronJobController extends CommonController {

	@Autowired
	CronJobService cronJobService;
	

	@GetMapping("/cron-jobs")
	@ResponseStatus(HttpStatus.OK)
	public ResponseWrapper<Page<CronJobDto>> getCronJobList(PageRequest pageRequest, CronJobArgDto args) {
		Page<CronJobDto> result = cronJobService.getList(pageRequest, args);
		return new ResponseWrapper<Page<CronJobDto>>(result);
	}
	
	@GetMapping("/cron-jobs/{idx}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseWrapper<CronJobDto> getCronJob (@PathVariable(name = "idx") Long idx) {
		CronJobDto result = cronJobService.get(idx, getLoginUser());
		return new ResponseWrapper<CronJobDto>(result);
	}
	
	@GetMapping("/cron-jobs/{idx}/yaml")
	@ResponseStatus(HttpStatus.OK)
	public ResponseWrapper<String> getCronJobYaml (@PathVariable(name = "idx") Long idx) {
		String result = cronJobService.getYaml(idx);
		return new ResponseWrapper<String>(result);
	}
	
	@PostMapping("/cron-jobs")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseWrapper<CronJobDto> postCronJob (@RequestBody CronJobArgDto CronJobArgDto) {
		ResponseWrapper<CronJobDto> result = null;
		cronJobService.create(CronJobArgDto);
		return result;
	}
	
	@PutMapping("/cron-jobs/{idx}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseWrapper<CronJobDto> putCronJob (@PathVariable(name = "idx") Long idx, @RequestBody CronJobArgDto CronJobArgDto) {
		CronJobArgDto.setJobIdx(idx);
		cronJobService.update( CronJobArgDto);
		return new ResponseWrapper<CronJobDto>();
	}
	
	@DeleteMapping("/cron-jobs/{idx}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseWrapper<CronJobDto> deleteCronJob (@PathVariable(name = "idx") Long idx, @RequestBody CronJobArgDto CronJobArgDto) {
		CronJobArgDto.setJobIdx(idx);
		cronJobService.delete(CronJobArgDto);
		return new ResponseWrapper<CronJobDto>();
	}
}
