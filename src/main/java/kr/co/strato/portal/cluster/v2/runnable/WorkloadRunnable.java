package kr.co.strato.portal.cluster.v2.runnable;

public abstract class WorkloadRunnable implements Runnable {

	private WorkloadRunnableExecuter executer;
	
	/**
	 * 작업 완료 후 결과 값 저장
	 * @param result
	 */
	protected void setResult(Object result) {
		getExecuter().setResult(this, result);
	}
	
	
	public WorkloadRunnableExecuter getExecuter() {
		return executer;
	}

	public void setExecuter(WorkloadRunnableExecuter executer) {
		this.executer = executer;
	}
}
