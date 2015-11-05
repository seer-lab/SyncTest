package account.tests;
import org.junit.*;
import org.junit.Test;
import static org.junit.Assert.*;


public class Test100K {
    @Test
    public void testMain100K() {
        account.Main m = new account.Main();
        m.Runner(new String[] {"100000"});
        assertTrue((m.less==false)&&(m.more==false));
    }
}
