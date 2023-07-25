package kr.co.strato.portal.ml.service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;

import kr.co.strato.adapter.cloud.eks.model.NodeJobArg;
import kr.co.strato.adapter.cloud.eks.model.NodeStatusRes;
import kr.co.strato.adapter.cloud.eks.proxy.EKSInterfaceProxy;
import kr.co.strato.domain.cluster.service.ClusterDomainService;
import kr.co.strato.domain.machineLearning.model.MLEntity;
import kr.co.strato.domain.machineLearning.service.MLDomainService;
import kr.co.strato.global.config.ApplicationContextProvider;
import kr.co.strato.portal.cluster.model.ClusterDto;
import kr.co.strato.portal.cluster.service.ClusterService;
import kr.co.strato.portal.ml.model.MLDto;
import kr.co.strato.portal.ml.model.MLDtoMapper;
import kr.co.strato.portal.ml.model.MLScheduleDTO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ScheduledTaskService {

    private ThreadPoolTaskScheduler taskScheduler;

    private EKSInterfaceProxy eksInterfaceProxy;
    
	private MLDomainService mlDomainService;
	private ClusterService clusterService;
    
    public ScheduledTaskService() {
    	this.taskScheduler = new ThreadPoolTaskScheduler();
    	this.taskScheduler.setThreadNamePrefix("Scheduler-");
        this.taskScheduler.setPoolSize(10); // 원하는 쓰레드 풀 크기로 설정
        this.taskScheduler.initialize();
    }
    
    public void scheduleTask(MLScheduleDTO param) {
    	
    	eksInterfaceProxy = ApplicationContextProvider.getBean(EKSInterfaceProxy.class);
    	
    	// 1분마다 실행
        taskScheduler.scheduleAtFixedRate(() -> processRequest(param), 60000);
    }
    
    public void stopScheduleTask() {
    	taskScheduler.shutdown();
    }
    
    public void processRequest(MLScheduleDTO param) {
    	
    	clusterService = ApplicationContextProvider.getBean(ClusterService.class);

    	Long clusterIdx = param.getClusterIdx();
    	ClusterDto.Detail cluster = null;
		try {
			cluster = clusterService.getCluster(clusterIdx);
		} catch (Exception e) {
			log.error("", e);
		}
    	
//		String clusterName = cluster.getClusterName();
		String clusterName = "41-test";
    	NodeJobArg arg = new NodeJobArg();
    	arg.setClusterName(clusterName);

    	String threadName = Thread.currentThread().getName();
        log.info("Executing task in thread: " + threadName + "	clusterName: " + clusterName);

        try {
	    	// 크론 워크타임일 경우 노드 start
	    	if(workTimeCheck(clusterIdx)) {
	    		NodeJobArg.Job jobArg = getJobArg(arg, "stopped");
	    		List<String> instanceIds = jobArg.getInstanceIds();
	    		
	    		if(instanceIds.size() > 0) {
	        		log.info("start node size: {}", instanceIds.size());
	    			boolean result = eksInterfaceProxy.startNode(jobArg);
	        		log.info("result: " + result);
	            }
	    	
	    	// 워크타임이 아닐 경우 노드 stop
	    	} else {
	    		NodeJobArg.Job jobArg = getJobArg(arg, "running");
	    		List<String> instanceIds = jobArg.getInstanceIds();
	    		
	    		if(instanceIds.size() > 0) {
	    			log.info("stop node size: {}", instanceIds.size());
	    			boolean result = eksInterfaceProxy.stopNode(jobArg);
	    			log.info("result: " + result);
	    		}
	    	}
	    	
        } catch(Exception e) {
        	log.error("", e);
        	stopScheduleTask();
        }
        
    }

    // 최초 시작시 ML 조회하여 cron 스케줄링 실행
    @PostConstruct
    public void initScheduleStart() {
    	
    	mlDomainService = ApplicationContextProvider.getBean(MLDomainService.class);
    	clusterService = ApplicationContextProvider.getBean(ClusterService.class);
    	
    	List<MLEntity> list = mlDomainService.getList();
		log.info("init schedule size : {}", list.size());
		for(MLEntity ml : list) {	
			Long clusterIdx = ml.getClusterIdx();
			
			String mlId = ml.getMlId();
			String cronSchedule = ml.getCronSchedule();
			
			MLScheduleDTO schedule = new MLScheduleDTO(mlId, clusterIdx);
			
			if(cronSchedule != null) {
				log.info("init schedule run");
				log.info("mlId = {}", mlId);
				scheduleTask(schedule);
			}
		}
    }
    
    // 현재 datetime 과 cron datetime 비교해서 워크타임인지 체크
    public boolean workTimeCheck(Long clusterIdx) {
    	
    	mlDomainService = ApplicationContextProvider.getBean(MLDomainService.class);
    	
    	List<String> crons = mlDomainService.getCronsByClusterIdx(clusterIdx);
    	
    	for(String cron : crons) {
        	        	
        	// 현재 시간
            ZonedDateTime now = ZonedDateTime.now();
            ZonedDateTime nextExecution = cronParser(cron); 
        	
            log.info("current time: " + now);
            
            if(nextExecution != null) {
            	log.info("next task execution time: " + nextExecution);
            } else {
            	log.info("There is no next run time.");
            	return false;
            }

            // 현재일과 작업일이 다를 경우
            if(now.getDayOfMonth() != nextExecution.getDayOfMonth()) {
            	log.info("node off !	" + nextExecution.getDayOfMonth() + " run on day.");
            	return false;
            // 현재요일과 작업요일이 다를 경우
            } else if(now.getDayOfWeek() != nextExecution.getDayOfWeek()) {
            	log.info("node off !	" + nextExecution.getDayOfWeek() + " run on day.");
            	return false;
            }
    	}

        return true;
    }
    
    // cron 표현식 -> datetime 변환
    public ZonedDateTime cronParser(String cronExpression) {
    	CronDefinition cronDefinition =
    		    CronDefinitionBuilder.defineCron()
    		        .withSeconds().and()
    		        .withMinutes().and()
    		        .withHours().and()
    		        .withDayOfMonth()
    		            .supportsHash().supportsL().supportsW().and()
    		        .withMonth().and()
    		        .withDayOfWeek()
    		            .withIntMapping(7, 0) //we support non-standard non-zero-based numbers!
    		            .supportsHash().supportsL().supportsW().and()
    		        .instance();

    	// or get a predefined instance
    	cronDefinition = CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ);
    	
    	CronParser parser = new CronParser(cronDefinition);

    	Cron cron = null;
    	
    	try {
        	cron = parser.parse(cronExpression);
    	} catch(Exception e) {
    		stopScheduleTask();
    		log.error(""+e);
    	}

    	// 현재 시간
        ZonedDateTime now = ZonedDateTime.now();
        
        // 원하는 타임존으로 변환
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zonedDateTime = now.withZoneSameInstant(zoneId);

        // Cron 표현식에 따른 실행 시간 계산
        ExecutionTime executionTime = ExecutionTime.forCron(cron);
        ZonedDateTime nextExecution = executionTime.nextExecution(zonedDateTime).orElse(null);
        
        return nextExecution;
    }
    
    public NodeJobArg.Job getJobArg(NodeJobArg arg, String code) {
    	
    	String clusterName = arg.getClusterName();
    	String region = arg.getRegion();
    	
    	// 노드 현재 상태 체크
		List<NodeStatusRes> statusRes = eksInterfaceProxy.statusNode(arg);
		NodeJobArg.Job jobArg = new NodeJobArg.Job();

		List<String> instanceIds = new ArrayList<>();
		
		for(NodeStatusRes node : statusRes) {
			String status = node.getStatus();
			
			log.info("instance status: {}", status);
			log.info("instance id: {}", node.getInstanceId());
			
			if(status.equals(code)) {
				String instanceId = node.getInstanceId();
				instanceIds.add(instanceId);	
			}
		}

		jobArg.setClusterName(clusterName);
		jobArg.setRegion(region);
		jobArg.setInstanceIds(instanceIds);
		
		return jobArg;
    	
    }
	
    
}