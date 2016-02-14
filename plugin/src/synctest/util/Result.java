package synctest.util;

import java.util.Vector;

public class Result {
	private String name, raw;
	private double pass, fail, error, deadlock, total;
	private Vector<ExecutionResult> executionResults;

	public Result(String name, double pass, double fail, double error, double deadlock, double total, String raw) {
		this.name = name;
		this.pass = pass;
		this.fail = fail;
		this.error = error;
		this.deadlock = deadlock;
		this.total = total;
        this.raw = raw;
	}
	
	public Result(String name) {
		this.name = name;
	}
	
	public Result(String name, Vector<ExecutionResult> executionResults) {
		this.name = name;
		this.executionResults = executionResults;
	}
	
	public String toString() {
		String result = "Test: " + name +"\n" +
				"Pass: " + pass + "\n" +
				"Fail:" + fail + "\n" +
				"Error:" + error + "\n" +
				"Deadlock: " + deadlock + "\n" +
				"Total: " + total + "\n";
		return result;
	}
	
	public void printResult() {
		System.out.println("Test: " + name);
		System.out.println("Pass: " + pass);
		System.out.println("Fail: " + fail);
		System.out.println("Error: " + error);
		System.out.println("Deadlock: " + deadlock);
		System.out.println("Total: " + total);
	}

	public String getName() {
		return name;
	}
	
	public double getPass() {
		return pass;
	}
	
	public double getFail() {
		return fail;
	}
	
	public double getError() {
		return error;
	}
	
	public double getDeadlock() {
		return deadlock;
	}
	
	public double getTotal() {
		return total;
	}
    
	public String getRaw() {
		return raw;
	}
}
