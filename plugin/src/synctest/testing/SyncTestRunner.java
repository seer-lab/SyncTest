package synctest.testing;

import java.util.Vector;

import synctest.util.Result;

public class SyncTestRunner {

	private Vector<Result> results;
	private String baseDir, sourceDir, testDir;
	private double sleepAmnt;
	private int runCount;
	
	public SyncTestRunner(String base, String source, String test, String sleep, String runs) {
		this.baseDir = base;
		this.sourceDir = source;
		this.testDir = test;
		this.sleepAmnt = Double.valueOf(sleep);
		this.runCount = Integer.valueOf(runs);
	}
	
	public Vector<Result> getResults() {
		return results;
	}
	
	public void setResults(Vector<Result> results) {
		this.results = results;
	}
	
	public String getBaseDir() {
		return baseDir;
	}
	
	public String getSourceDir() {
		return sourceDir;
	}
	
	public String getTestDir() {
		return testDir;
	}
	
	public double getSleepAmnt() {
		return sleepAmnt;
	}
	
	public int getRunCount() {
		return runCount;
	}
}
