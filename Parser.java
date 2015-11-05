import java.io.*;
import java.util.regex.*;

public class Parser {
	public static void main(String[] args) {
		Pattern p0 = Pattern.compile("OK");
		Pattern p1 = Pattern.compile("Failures");

		int c0 = 0; int c1 = 0;
        int testsRun = Integer.parseInt(args[1]);

		try {
			File file = new File(args[0]);
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line;

			while((line = br.readLine()) != null) {
				Matcher m0 = p0.matcher(line);
				Matcher m1 = p1.matcher(line);

				while(m0.find()) {
					c0++;
				}

				while(m1.find()) {
					c1++;
				}

			}

            double percent = ((double) c0/(double) testsRun)*100;
            System.out.println(c0 + "/" + c1 + "/" + testsRun + " -- " + percent + "%");

            br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
