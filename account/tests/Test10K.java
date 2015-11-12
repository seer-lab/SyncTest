package account.tests;
import org.junit.*;
import org.junit.Test;
import static org.junit.Assert.*;


public class Test10K {
    @Test
    public void test10K() {
        account.Main m = new account.Main();
        m.Runner(new String[] {"10000"});
        assertTrue((m.less==false)&&(m.more==false));
    }
}
