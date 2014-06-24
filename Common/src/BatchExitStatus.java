package commons;

public enum BatchExitStatus {
	
	/** 成功 */
	SUCCESS(0),
	
	/** 警告 */
	WARNING(1),
	
	/** エラー */
	FAILURE(-1);

	private int returnCode;

	BatchExitStatus(int returnCode) {
		this.returnCode = returnCode;
	}

	public int getReturnCode() {
		return returnCode;
	}

}
