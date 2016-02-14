package synctest.testing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.Vector;

import synctest.util.Parser;
import synctest.util.Result;

public class SyncTestRunnable implements Runnable {
	SyncTestRunner runner;
	
	public SyncTestRunnable(SyncTestRunner runner) {
		this.runner = runner;
	}
	
	@Override
	public void run() {
		String baseDir = runner.getBaseDir();
		String sourceDir = runner.getSourceDir();
		String testDir = runner.getTestDir();
		int runCount = runner.getRunCount();
		double sleepAmnt = runner.getSleepAmnt();
				
		try {
			ProcessBuilder pb = new ProcessBuilder("/bin/bash", "synctest.sh", baseDir, sourceDir, testDir, 
					Integer.toString(runCount), Double.toString(sleepAmnt));
			
			pb.directory(new File("/home/katarn/Documents/synctest/plugin/src/synctest")); // can't be hardcoded!!
			Process p = pb.start();
			
			BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line;
			
			while ((line = br.readLine()) != null) {
				BufferedWriter bw = new BufferedWriter(new FileWriter("syncTestOutput.txt", true));
				//System.out.println("Writing: " + line);
				bw.append(line+"\n");
				bw.close();
			}
			
			br.close();
		} catch(Exception e) {
			e.printStackTrace();
		}

		Vector<Result> results = new Vector<Result>();
		Parser parser = new Parser();
		// parse the output
		File outDir = new File(baseDir+"/out");
		File[] files = outDir.listFiles();
		if(files != null) {
			for(File file : files) {
				if(file.toString().contains("all")) {
					Result result = parser.parse(file, runCount);
					results.add(result);
				}
			}
		}
		
		runner.setResults(results);
	}
}
