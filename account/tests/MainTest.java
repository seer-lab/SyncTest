package account.tests;
import org.junit.*;
import org.junit.Test;
import static org.junit.Assert.*;


public class MainTest {

    @Test
    public void testMain2() {
        System.out.println("\n\nTesting with 2 accounts.");
        account.Main m = new account.Main();
        m.Runner(new String[] {"2"});
        assertTrue((m.less==false)&&(m.more==false));
    }

    @Test
    public void testMain100() {
        System.out.println("\n\nTesting with 100 accounts.");
        account.Main m = new account.Main();
        m.Runner(new String[] {"100"});
        assertTrue((m.less==false)&&(m.more==false));
    }

    @Test
    public void testMain1K() {
        System.out.println("\n\nTesting with 1K accounts.");
        account.Main m = new account.Main();
        m.Runner(new String[] {"1000"});
        assertTrue((m.less==false)&&(m.more==false));
    }

    @Test
    public void testMain10K() {
        System.out.println("\n\nTesting with 10K accounts.");
        account.Main m = new account.Main();
        m.Runner(new String[] {"10000"});
        assertTrue((m.less==false)&&(m.more==false));
    }

    @Test
    public void testMain100K() {
        System.out.println("\n\nTesting with 100K accounts.");
        account.Main m = new account.Main();
        m.Runner(new String[] {"100000"});
        assertTrue((m.less==false)&&(m.more==false));
    }
}
