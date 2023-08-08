package kr.co.strato.portal.ml.v1.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CallbackData {
	public static final String STATUS_START = "start";
	public static final String STATUS_FINISH = "finish";
	
	public static final int CODE_SUCCESS = 10001;
	public static final int CODE_FAIL = 10002;
	
	private Long workJobIdx;
	private String status;
	private int code;
	private String message;
	private Object result;
	
	/**
	 * 시작 성공.
	 * @param workJobIdx
	 * @return
	 */
	public static CallbackData startSuccess(Long workJobIdx) {
		CallbackData data = CallbackData.builder()
				.status(STATUS_START)
				.code(CODE_SUCCESS)
				.workJobIdx(workJobIdx)
				.build();
		return data;
	}
	
	/**
	 * 시작 실패.
	 * @param workJobIdx
	 * @param message
	 * @return
	 */
	public static CallbackData startFail(Long workJobIdx, String message) {
		CallbackData data = CallbackData.builder()
				.status(STATUS_START)
				.code(CODE_FAIL)
				.message(message)
				.workJobIdx(workJobIdx)
				.build();
		return data;
	}
	
	/**
	 * 종료 성공.
	 * @param workJobIdx
	 * @return
	 */
	public static CallbackData finishSuccess(Long workJobIdx, Object result) {
		CallbackData data = CallbackData.builder()
				.status(STATUS_FINISH)
				.code(CODE_SUCCESS)
				.workJobIdx(workJobIdx)
				.result(result)
				.build();
		return data;
	}
	
	/**
	 * 종료 실패.
	 * @param workJobIdx
	 * @return
	 */
	public static CallbackData finishFail(Long workJobIdx, String message) {
		CallbackData data = CallbackData.builder()
				.status(STATUS_FINISH)
				.code(CODE_FAIL)
				.workJobIdx(workJobIdx)
				.message(message)
				.build();
		return data;
	}
}
