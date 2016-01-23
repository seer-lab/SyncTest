package synctest.util;

public class Result {
	String name;
	int pass, fail, error, deadlock, total;
	
	public Result(String name, int pass, int fail, int error, int deadlock, int total) {
		this.name = name;
		this.pass = pass;
		this.fail = fail;
		this.error = error;
		this.deadlock = deadlock;
		this.total = total;
	}
	
	public Result() {}
	
	public String toString() {
		String result = "Test: " + name +"\n" +
				"Pass: " + pass + "\n" +
				"Fail:" + fail + "\n" +
				"Error:" + error + "\n" +
				"Deadlock: " + deadlock + "\n" +
				"Total" + total + "\n";
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

	public String getName() {return name;}
	public int getPass() {return pass;}
	public int getFail() {return fail;}
	public int getError() {return error;}
	public int getDeadlock() {return deadlock;}
	public int getTotal() {return total;}
}
