package account.tests;
import org.junit.*;
import org.junit.Test;
import static org.junit.Assert.*;


public class Test100 {
    @Test
    public void testMain100() {
        account.Main m = new account.Main();
        m.Runner(new String[] {"100"});
        assertTrue((m.less==false)&&(m.more==false));
    }
}
