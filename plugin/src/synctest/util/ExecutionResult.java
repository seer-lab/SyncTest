package synctest.util;

import java.io.File;

public class ExecutionResult {
	private int number;
	private String status;
	private File output;
	
	public ExecutionResult(int number, String status) {
		this.number = number;
		this.status = status;
	}
	
	public int getExecutionNumber() {
		return this.number;
	}
	
	public String getTestStatus() {
		return this.status;
	}
}
