package synctest;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Random;
import java.util.Vector;

import synctest.util.*;

public class SyncTestRunner {

	private Vector<Result> results;
	
	public void runTests(String source, String test, String output, String sleep, String runs) {
		//gather input values
		String sourceDir = source;
		String testDir = test;
		String outputDir = output;
		double sleepAmnt = Double.valueOf(sleep);
		int runCount = Integer.valueOf(runs);
		
		//TODO input checking
		
		sourceDir = sourceDir.split("synctest/")[1];
		testDir = testDir.split("synctest/")[1];
		outputDir = outputDir.split("synctest/")[1];
		
		System.out.println("VARS:");
		System.out.println("SOURCE_DIR:"+sourceDir);
		System.out.println("TEST_DIR:"+testDir);
		System.out.println("OUTPUT_DIR:"+outputDir);
		System.out.println("SLEEP_COUNT:"+sleepAmnt);
		System.out.println("TEST_COUNT:"+runCount);
			
//        try {
//            ProcessBuilder pb = new ProcessBuilder("/bin/bash", "syncTest.sh", sourceDir, testDir, outputDir, Integer.toString(runCount));
//            pb.directory(new File("/home/katarn/Documents/synctest/"));
//            Process p = pb.start();
//
//            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
//            String line = "";
//            while ((line = br.readLine()) != null) {
//                System.out.println(line);
//            }
//        } catch(Exception e) {
//            e.printStackTrace();
//        }
		
		
        results = new Vector<Result>();
        Random random = new Random();
        int passCount = 0, failCount = 0, errCount = 0, deadCount = 0;
        
		//dummy data for canvas population
        for(int i = 0; i < 1000; i++) {
        	int fail = random.nextInt(26);
        	int error = random.nextInt(26);
        	int deadlock = random.nextInt(26);
        	int pass = 100 - fail - error - deadlock;
        	Result result = new Result("test_"+i, pass, fail, error, deadlock, 100);
        	result.printResult();
        	results.add(result);
        	passCount += pass;
        	failCount += fail;
        	errCount += error;
        	deadCount += 0;
        }
	}
	
	public Vector<Result> getResults() {
		return results;
	}
}
