package kr.co.strato.portal.ml.v1.service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;

import kr.co.strato.adapter.cloud.sks.model.VSphereVMAction;
import kr.co.strato.adapter.cloud.sks.model.VSphereVMPower;
import kr.co.strato.adapter.cloud.sks.proxy.SKSInterfaceProxy;
import kr.co.strato.domain.machineLearning.model.MLEntity;
import kr.co.strato.domain.machineLearning.service.MLDomainService;
import kr.co.strato.global.config.ApplicationContextProvider;
import kr.co.strato.portal.cluster.v2.service.NodeService;
import kr.co.strato.portal.ml.v1.model.MLScheduleDTO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ScheduledTaskService {

    private ThreadPoolTaskScheduler taskScheduler;
    
    private SKSInterfaceProxy sksInterfaceProxy;
    
    private MLDomainService mlDomainService;
	
	private ZonedDateTime sBeforeExecution; 
	private ZonedDateTime sNextExecution; 
	private ZonedDateTime eBeforeExecution; 
	private ZonedDateTime eNextExecution;
	
	private NodeService nodeService;
	
	private ZonedDateTime startDate;
    
    public ScheduledTaskService() {
    	this.taskScheduler = new ThreadPoolTaskScheduler();
    	this.taskScheduler.setThreadNamePrefix("Scheduler-");
        this.taskScheduler.setPoolSize(10); // 원하는 쓰레드 풀 크기로 설정
        this.taskScheduler.initialize();
    }
    
    public void scheduleTask(MLScheduleDTO param) {
    	
    	sksInterfaceProxy = ApplicationContextProvider.getBean(SKSInterfaceProxy.class);
    	
    	mlDomainService = ApplicationContextProvider.getBean(MLDomainService.class);
    	
    	nodeService = ApplicationContextProvider.getBean(NodeService.class);
    	
    	// 1분마다 실행
        taskScheduler.scheduleAtFixedRate(() -> processRequest(param), 60000);
        
        
    }
    
    public void stopScheduleTask() {
    	taskScheduler.shutdown();
    }
    
    public void processRequest(MLScheduleDTO param) {   
    	Long clusterIdx = param.getClusterIdx();   	
    
    	List<MLEntity> crons = mlDomainService.getCronsByClusterIdx(clusterIdx);
    	if(crons == null || crons.size() == 0) {
    		log.info("ML Size 0. stop Schedule Task");
    		stopScheduleTask();
    		return;
    	}
		
    	String threadName = Thread.currentThread().getName();
        log.info("Executing task in thread: {}, clusterIdx: {}", threadName, clusterIdx);

        List<String> nodeNames = nodeService.getNodeNemes(clusterIdx);
        
        try {
        	String action = null;
        	
        	// 크론 워크타임일 경우 노드 start
	    	if(workTimeCheck(crons, clusterIdx)) {
	    		log.info("cluster start.---------------------------------");	    		
	    		action = "start";
	    	
	    	// 워크타임이 아닐 경우 노드 stop
	    	} else {
	    		log.info("cluster stop.---------------------------------");	    		
	    		action = "stop";
	    	}
	    	
	    	List<String> vmNames = new ArrayList<>();
	    	List<VSphereVMPower> powerState = sksInterfaceProxy.powerState(nodeNames);
	    	if(powerState == null || powerState.size() == 0) {
	    		log.info("VM Power 상태 확인 불가! VM Name:");
	    		for(String s : vmNames) {
	    			log.info(s);
	    		}
	    		return;
	    	}
	    	
	    	for(VSphereVMPower p : powerState) {
	    		if(!p.getPowerState().equals(action)) {
	    			vmNames.add(p.getName());
	    		}
	    	}
	    	
	    	if(vmNames.size() > 0) {
	    		log.info("Node Power {}", action);
	    		for(String s : vmNames) {
	    			log.info(s);
	    		}
	    		
	    		VSphereVMAction vmAction = VSphereVMAction.builder().vmNames(vmNames).action(action).build();
	    		boolean isOk = sksInterfaceProxy.powerAction(vmAction);
	    	}
        } catch(Exception e) {
        	log.error("", e);
        	//stopScheduleTask();
        }
    }

    // 최초 시작시 ML 조회하여 cron 스케줄링 실행
    //@PostConstruct
    public void initScheduleStart() {
    	
    	mlDomainService = ApplicationContextProvider.getBean(MLDomainService.class);
    	
    	List<MLEntity> list = mlDomainService.getList();
		log.info("init schedule size : {}", list.size());
		for(MLEntity ml : list) {	
			Long clusterIdx = ml.getClusterIdx();
			
			String mlId = ml.getMlId();
			String stratCron = ml.getStartCronSchedule();
			String endCron = ml.getEndCronSchedule();
//			String cronSchedule = ml.getCronSchedule();
			
			MLScheduleDTO schedule = new MLScheduleDTO(mlId, clusterIdx);
			
			if(stratCron != null && endCron != null) {
				log.info("init schedule run");
				log.info("mlId = {}", mlId);
				scheduleTask(schedule);
			}
		}
    }
    
    // 현재 datetime 과 cron datetime 비교해서 워크타임인지 체크
    public boolean workTimeCheck(List<MLEntity> crons, Long clusterIdx) {
    	for(MLEntity cron : crons) {
        	        	
    		String startCron = cron.getStartCronSchedule();
    		String endCron = cron.getEndCronSchedule();
    		
    		log.info("start cron: {}", startCron);
    		log.info("end cron: {}", endCron);
    		
        	// 현재 시간
            ZonedDateTime now = ZonedDateTime.now();
            
            if(startDate == null) {
            	startDate = now;
            }
            
            if(sBeforeExecution == null || now.getDayOfMonth() > startDate.getDayOfMonth()) {
            	sBeforeExecution = cronParser(startCron, "before");
            	sNextExecution = cronParser(startCron, "next"); 
            	eBeforeExecution = cronParser(endCron, "before"); 
            	eNextExecution = cronParser(endCron, "next"); 
            }
            
        	
        	
            log.info("current time: " + now);
            
            if(sBeforeExecution != null && sNextExecution != null && eBeforeExecution != null && eNextExecution != null) {
            	log.info("before task execution start time: " + sBeforeExecution);
            	log.info("next task execution start time: " + sNextExecution);
            	log.info("before task execution end time: " + eBeforeExecution);
            	log.info("next task execution end time: " + eNextExecution);
            } else {
            	log.info("There is no next run time.");
            	return false;
            }
            
            /**
             * 워크 타임일 경우 노드 시작
             * - 현재일과 작업요일이 일치
             * - 현재시간과 작업시간이 일치하거나 현재시간 작업시간에 사이에 실행할 경우
             */
            if(now.getDayOfMonth() == sBeforeExecution.getDayOfMonth() || 
            	now.getDayOfWeek() == sBeforeExecution.getDayOfWeek()) {
            	
            	if(now.isEqual(sBeforeExecution) || now.isEqual(sNextExecution) 
	            		|| now.isAfter(sBeforeExecution) && now.isBefore(eNextExecution)) {
	            	log.info("node start !	" + now);
	            	return true;
	            } else {
	            	log.info("node off !	" + sNextExecution + " run on day.");
	            	return false;
	            }
            } else {
            	log.info("node off !	" + sNextExecution + "run on day.");
            	return false;
            }
            
    	}

        return true;
    }
    
    // cron 표현식 -> datetime 변환
    public ZonedDateTime cronParser(String cronParam, String type) {
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
    		cron = parser.parse(cronParam);
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
        
        if(type.equals("next")) {
        	ZonedDateTime nextExecution = executionTime.nextExecution(zonedDateTime).orElse(null);
        	return nextExecution;
        } else if(type.equals("before")) {
        	ZonedDateTime beforeExecution = executionTime.lastExecution(zonedDateTime).orElse(null);
        	return beforeExecution;
        }
        
        ZonedDateTime nextExecution = executionTime.nextExecution(zonedDateTime).orElse(null);
        
        return nextExecution;
    }
    
    /*
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
    */
}
