package synctest.testing;

/*
 * This is the runnable class that checks for deadlocks
 * and destroys the process if it is deadlocked.
 * It makes use of the jps and jstack commands to find and
 * kill deadlocked java processes.
 * 
 * */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.URL;

public class CheckDeadlock implements Runnable {
	String 			outDir, test, pid; //output directory, current test, process ID
	int 			execution, runCount; //execution and total run count
	double 			sleepAmnt; //how long to wait between deadlock checks
	Process			runningTest; //the process for the current test
	URL 			location; //where this project is
	boolean 		found = false, detected = false; //found the process, detected a deadlock
	BufferedWriter 	bw, bw2;
	BufferedReader 	br;

	public CheckDeadlock(String outDir, String test, int execution, int runCount, double sleepAmnt, Process runningTest) {
		this.outDir = outDir;
		this.test = test;
		this.execution = execution;
		this.runCount = runCount;
		this.sleepAmnt = sleepAmnt;
		this.runningTest = runningTest;
		location = getClass().getProtectionDomain().getCodeSource().getLocation();
		
		Process jps;
		try {
			jps = new ProcessBuilder("jps").start();
			br = new BufferedReader(new InputStreamReader(jps.getInputStream()));
			String line;
	
			// look for the currently running test
			while ((line = br.readLine()) != null) {
				if(Thread.currentThread().isInterrupted()) {
					br.close();
					return;
				}
				if(line.contains("JUnitCore")) {
					// found it, get its process ID
					found = true;
					jps.destroy();
					pid = line.split(" ")[0];
					br.close();
					break;
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		if(!found) return;
		while(true) {
			if(Thread.currentThread().isInterrupted()) return;
			try {
				Thread.sleep((long)sleepAmnt*1000);
			
				Process jstack = new ProcessBuilder("jstack", pid).start();
				br = new BufferedReader(new InputStreamReader(jstack.getInputStream()));
				String line;
				
				while ((line = br.readLine()) != null) {
					if(Thread.currentThread().isInterrupted()) {
						br.close();
						jstack.destroy();
						return;
					}

					bw = new BufferedWriter(new FileWriter(outDir+"/"+test+"-"+execution+".txt"));
					bw.append(line+"\n");
					bw.close();
					if(line.contains("deadlock") && !detected) {
						// found a deadlock, write it to the file and kill process
						detected = true;
						bw2 = new BufferedWriter(new FileWriter(location.getFile()+"src/synctest/testing/syncTestOutput.txt", true));
						bw2.append("Execution "+execution+"/"+runCount+" Deadlocked\n");
						bw2.close();
						runningTest.destroy();
						jstack.destroy();
					}
				}
				
				jstack.destroy();
				br.close();
			} catch(InterruptedException e) {
				; // sleep interrupted by SyncTestRunnable because a test has finished
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

}
