package synctest.testing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class SyncTestRunnable implements Runnable {
	SyncTestRunner 	runner;
	String 			baseDir, sourceDir, testDir, workingDir, javac, java, os;
	int 			runCount;
	double 			sleepAmnt;
	URL 			location;
	BufferedWriter 	bw, bw2;
	Thread			checkDeadlock;
	
	public SyncTestRunnable(SyncTestRunner runner) {
		this.runner = runner;
		baseDir = runner.getBaseDir();
		sourceDir = runner.getSourceDir();
		testDir = runner.getTestDir();
		runCount = runner.getRunCount();
		sleepAmnt = runner.getSleepAmnt();
		location = getClass().getProtectionDomain().getCodeSource().getLocation();
		os = System.getProperty("os.name").toLowerCase();
		
		// set the compilation/run commands based on the operating system
		// I'm like 99% sure this works
		if(os.contains("win")) {
			javac = "javac.exe";
			java = "java.exe";
		} else if(os.contains("lin")) {
			javac = "/usr/bin/javac";
			java = "/usr/bin/java";
		} else if(os.contains("mac")) {
			javac = "/usr/bin/javac";
			java = "/usr/bin/java";
		} else {
			System.out.println("OS Not Supported! Sorry.");
			System.exit(-1);
		}
	}
	
	@Override
	public void run() {		
		try {
			//clear output directory or clear it if it exists
			File outDir = new File(baseDir+"/out");
			if(!outDir.exists()) {
				outDir.mkdir();
			} else {
				for(File file: outDir.listFiles()) file.delete();
			}
			
			// get array of test files from the directory
			File[] testFiles = new File(testDir).listFiles(new FilenameFilter() {
				public boolean accept(File f, String s) {
					if(s.contains("java")) return true;
					return false;
				}
	    	});
			
			// check for a package and set the working directory accordingly
			// TODO fix hard-coding of working directory
			String packTest = testFiles[0].getClass().getPackage().getName();
			if(packTest.isEmpty()) {
				workingDir = baseDir;
			} else {
				workingDir = "/home/katarn/Documents/synctest/";
			}
			
			// copy jar files to the working directory
			File junit = new File(location.getFile()+"src/synctest/junit-4.12.jar");
			File hamcrest = new File(location.getFile()+"src/synctest/hamcrest-core-1.3.jar");
			
			Path moveFrom = FileSystems.getDefault().getPath(junit.getAbsolutePath());
			Path moveTo = FileSystems.getDefault().getPath(workingDir+"/"+junit.getName());
			Files.copy(moveFrom,  moveTo, StandardCopyOption.REPLACE_EXISTING);
			
			moveFrom = FileSystems.getDefault().getPath(hamcrest.getAbsolutePath());
			moveTo = FileSystems.getDefault().getPath(workingDir+"/"+hamcrest.getName());
			Files.copy(moveFrom,  moveTo, StandardCopyOption.REPLACE_EXISTING);
			
			// compile all source and test files
			// TODO find out why compilation doesn't work
			ProcessBuilder compile = new ProcessBuilder(javac, "-cp", junit.getName(), sourceDir+"/*.java", testDir+"/*.java");
			compile.directory(new File(workingDir));
			Process compileProc = compile.start();
			
			// make sure everything is compiled before moving on
			compileProc.waitFor();
			compileProc.destroy();
			
			outerLoop:
			for(int t = 0; t < testFiles.length; t++) {
				if(Thread.currentThread().isInterrupted()) break;
				// TODO get the package name
				//String pack = testFiles[t].getClass().getPackage().getName();
				String pack = "account.tests";
				String test = testFiles[t].getName().split("\\.")[0];
				
				// Write to file so UI thread can read it
				bw = new BufferedWriter(new FileWriter(location.getFile()+"src/synctest/testing/syncTestOutput.txt", true));
				bw.append("Running: "+test+"\n");
				bw.close();
				
				
				for(int i = 1; i <= runCount; i++) {
					if(Thread.currentThread().isInterrupted()) break outerLoop;
					// TODO implement deadlock detection
					checkDeadlock = new Thread(new CheckDeadlock(outDir.toString(), test, i, runCount, sleepAmnt));
					ProcessBuilder run = new ProcessBuilder(
							java,"-cp", junit.getName()+":"+hamcrest.getName()+":.", "org.junit.runner.JUnitCore", pack+"."+test);
					
					checkDeadlock.start();
					
					run.directory(new File(workingDir));
					
					// start the process for running the test
					Process runProc = run.start();
					BufferedReader br = new BufferedReader(new InputStreamReader(runProc.getInputStream()));
					String line;

					bw2 = new BufferedWriter(new FileWriter(outDir+"/"+test+"-"+i+".txt"));
					bw2.append("<"+test+" -- "+i+">");
					
					// get the output for the test and write it to the output file
					while ((line = br.readLine()) != null) {
						if(Thread.currentThread().isInterrupted()) break outerLoop; // user has pressed the cancel button
						
						bw = new BufferedWriter(new FileWriter(location.getFile()+"src/synctest/testing/syncTestOutput.txt", true));
						bw2.append(line+"\n");
						
						// if a deadlock is found it will get written to the output file by the checkDeadlock thread
						if(line.contains("OK")) {
							bw.append("Execution "+i+"/"+runCount+" Passed\n");
						} else if(line.contains("Failures")) {
							bw.append("Execution "+i+"/"+runCount+" Failed\n");
						}
						
						bw.close();
					}

					bw2.close();
					
					// wait until the test is finished before moving on
					runProc.waitFor();
					runProc.destroy();
					checkDeadlock.interrupt();
					br.close();
				}
				
				
				bw = new BufferedWriter(new FileWriter(location.getFile()+"src/synctest/testing/syncTestOutput.txt", true));
				bw.append(test+" Completed\n");
				bw.close();
			}
			
			checkDeadlock.interrupt();
			bw = new BufferedWriter(new FileWriter(location.getFile()+"src/synctest/testing/syncTestOutput.txt", true));
			bw.append("all tests finished\n");
			bw.close();
			
			// remove jar files from the working directory
			moveFrom = FileSystems.getDefault().getPath(workingDir+"/"+junit.getName());
			Files.delete(moveFrom);
			
			moveFrom = FileSystems.getDefault().getPath(workingDir+"/"+hamcrest.getName());
			Files.delete(moveFrom);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
