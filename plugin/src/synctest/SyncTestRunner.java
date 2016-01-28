package synctest;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Random;
import java.util.Vector;

import synctest.util.*;

public class SyncTestRunner {

	private Vector<Result> results;
	
	public void runTests(String base, String source, String test, String sleep, String runs) {
		//gather input values
		String baseDir = base;
		String sourceDir = source;
		String testDir = test;
		//String outputDir = output;
		double sleepAmnt = Double.valueOf(sleep);
		int runCount = Integer.valueOf(runs);
			
        try {
            ProcessBuilder pb = new ProcessBuilder("/bin/bash", "synctest.sh", baseDir, sourceDir, testDir, Integer.toString(runCount));
            pb.directory(new File("/home/katarn/Documents/synctest/plugin/src/synctest/"));
            Process p = pb.start();

            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = "";
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
		
		results = new Vector<Result>();
        // parse the output
        File outDir = new File(baseDir+"/out");
        File[] files = outDir.listFiles();
        if(files != null) {
        	for(File file : files) {
        		if(file.toString().contains("all")) {
        			Parser parser = new Parser(file,  runCount);
        			Result result = parser.parse();
        			results.add(result);
        		}
        	}
        }
        
//      Random random = new Random();
//      int passCount = 0, failCount = 0, errCount = 0, deadCount = 0;
//      
//      //dummy data for canvas population
//      for(int i = 0; i < 1000; i++) {
//    	  int fail = random.nextInt(26);
//    	  int error = random.nextInt(26);
//    	  int deadlock = random.nextInt(26);
//    	  int pass = 100 - fail - error - deadlock;
//    	  Result result = new Result("test_"+i, pass, fail, error, deadlock, 100);
//    	  //result.printResult();
//    	  results.add(result);
//    	  passCount += pass;
//    	  failCount += fail;
//    	  errCount += error;
//    	  deadCount += 0;
//      }
        
	}
	
	public Vector<Result> getResults() {
		return results;
	}
}
