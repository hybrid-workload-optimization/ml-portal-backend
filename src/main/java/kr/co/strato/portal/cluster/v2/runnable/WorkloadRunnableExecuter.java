package kr.co.strato.portal.cluster.v2.runnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WorkloadRunnableExecuter {
	private int TIME_INTERVAL = 100;
	
	public List<WorkloadRunnable> workloadRunnables;
	public Map<WorkloadRunnable, Object> resultMap;
	
	public WorkloadRunnableExecuter() {
		this.workloadRunnables = new ArrayList<>();
		this.resultMap = new ConcurrentHashMap<>();
	}

	public void run() {
		for(WorkloadRunnable runnable : workloadRunnables) {
			ExecutorService executorService = Executors.newSingleThreadExecutor();
			executorService.execute(runnable);
		}
		
		//작업이 완료 될 때까지 대기
		int waitTime = 0;
		while(true) {
			try {
				Thread.sleep(TIME_INTERVAL);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			waitTime += TIME_INTERVAL;
			
			if(isFinish()) {
				break;
			}
			
			//1분 이상 대기하는 경우 타임아웃 처리
			if(waitTime > 60000) {
				break;
			}
		}
	}
	
	public void addWorkloadRunnable(WorkloadRunnable runnable) {
		if(!this.workloadRunnables.contains(runnable)) {
			this.workloadRunnables.add(runnable);
			runnable.setExecuter(this);
		}
	}
	
	public void removeWorkloadRunnable(WorkloadRunnable runnable) {
		this.workloadRunnables.remove(runnable);
	}
	
	public void setResult(WorkloadRunnable runnable, Object result) {
		resultMap.put(runnable, result);
	}
	
	public Object getResult(WorkloadRunnable runnable) {
		return resultMap.get(runnable);
	}
	
	public boolean isFinish() {
		int runnableSize = workloadRunnables.size();
		int resultSize = resultMap.size();
		return runnableSize == resultSize;
	}
}
