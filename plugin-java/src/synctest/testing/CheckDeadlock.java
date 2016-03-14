package synctest.testing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.URL;

public class CheckDeadlock implements Runnable {
	String 			outDir, test, pid;
	int 			execution, runCount;
	double 			sleepAmnt;
	URL 			location;
	boolean 		found = false, detected = false;
	BufferedWriter 	bw, bw2;
	BufferedReader 	br;

	public CheckDeadlock(String outDir, String test, int execution, int runCount, double sleepAmnt) {
		this.outDir = outDir;
		this.test = test;
		this.execution = execution;
		this.runCount = runCount;
		this.sleepAmnt = sleepAmnt;
		location = getClass().getProtectionDomain().getCodeSource().getLocation();
	}
	
	@Override
	public void run() {
		while(true) {
			if(Thread.currentThread().isInterrupted()) return;
			found = false;
			try {
				Thread.sleep((long)sleepAmnt*1000);
				
				Process jps = new ProcessBuilder("jps").start();
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
				
				if(!found) {
					jps.destroy();
					continue;
				}
				
				found = false;
			
				Process jstack = new ProcessBuilder("jstack", pid).start();
				br = new BufferedReader(new InputStreamReader(jstack.getInputStream()));
				bw = new BufferedWriter(new FileWriter(outDir+"/"+test+"-"+execution+".txt"));
				
				while ((line = br.readLine()) != null) {
					if(Thread.currentThread().isInterrupted()) {
						bw.close();
						br.close();
						jstack.destroy();
						return;
					}
					
					bw.append(line+"\n");
					if(line.contains("deadlock") && !detected) {
						// found a deadlock, write it to the file
						detected = true;
						System.out.println("Deadlock detected in execution "+execution);
						bw2 = new BufferedWriter(new FileWriter(location.getFile()+"src/synctest/testing/syncTestOutput.txt", true));
						bw2.append("Execution "+execution+"/"+runCount+" Deadlocked\n");
						bw2.close();
						(new ProcessBuilder("pkill", "-KILL", "-f", "JUnitCore")).start(); // TODO can't be bash exclusive
					}
				}
				
				jstack.destroy();
				bw.close();
				br.close();
			} catch(InterruptedException e) {
				; // sleep interrupted by SyncTestRunnable because a test has finished
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

}
