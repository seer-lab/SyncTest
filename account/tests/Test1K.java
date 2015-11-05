package account.tests;
import org.junit.*;
import org.junit.Test;
import static org.junit.Assert.*;


public class Test1K {
    @Test
    public void testMain1K() {
        account.Main m = new account.Main();
        m.Runner(new String[] {"1000"});
        assertTrue((m.less==false)&&(m.more==false));
    }
}
