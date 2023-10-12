import org.junit.Test;

public class KotMainTest {
    @Test
    public void testKotMain1() {
        KotMain.main(new String[] {
                "-c", "org.kotsuite.BarTest",
                "-m", "testBar"
        });
    }

    @Test
    public void testKotMain2() {
        KotMain.main(new String[] {
                "-c", "org.kotsuite.BarTest,org.kotsuite.FooTest",
                "-m", "*"
        });
    }
}
