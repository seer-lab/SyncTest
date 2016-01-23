package synctest.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
	Result result;
	
	public Result parse(String[] args) {
		Pattern pass = Pattern.compile("OK");
		Pattern fail = Pattern.compile("Failures");
		Pattern err = Pattern.compile("(?d)[)] test");
		Pattern dead = Pattern.compile("Found one");
		Pattern assrt = Pattern.compile("at account.tests.Test(.*).test(.*)[(]Test(.*).java:(.*)[)]");
		String errors = "";

		int passCount = 0;
		int failCount = 0;
		int deadCount = 0;
    	int testsRun = Integer.parseInt(args[1]);

		try {
			File file = new File(args[0]);
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line;

			while((line = br.readLine()) != null) {
				Matcher m0 = pass.matcher(line);
				Matcher m1 = fail.matcher(line);
				Matcher m2 = err.matcher(line);
				Matcher m3 = dead.matcher(line);
				Matcher m4 = assrt.matcher(line);

				while(m0.find()) {
					passCount++;
				}

				while(m1.find()) {
					failCount++;
				}

//				while(m2.find()) {
//					errors += "\n" + (failCount+1) + ")" + br.readLine();
//				}
				
				while(m3.find()) {
					deadCount++;
				}

//				while(m4.find()) {
//					//open test file
//					File testFile = new File("account/tests/Test"+m3.group(1)+".java");
//					BufferedReader testBR = new BufferedReader(new FileReader(testFile));
//					//go to the line where the error is
//					for(int i = 0; i < Integer.parseInt(m3.group(4)); i ++) {
//						line = testBR.readLine();
//					}
//					//get rid of extra spaces
//					line = line.replaceAll(" ", "");
//					//print line to terminal
//					errors += " @ Test"+m3.group(1)+".java:"+m3.group(4)+"\n  " + line + "\n";
//				}

			}
			
			int errCount = testsRun - passCount - failCount - deadCount;
			

			result = new Result(file.toString(), passCount, failCount, errCount, deadCount, testsRun);
			result.printResult();
			br.close();
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
