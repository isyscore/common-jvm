import com.isyscore.kotlin.common.HttpUtilKt;
import org.junit.Test;

public class TestJava {

    @Test
    public void test() {
        String ret = HttpUtilKt.httpGet("https://devapi.heweather.net/v7/weather/3d?location=101210101&key=338e3ef0ebf54d8580c0b1043ec5bcef");
        System.out.println(ret);
    }

    @Test
    public void testMR() {
        int[] ia = new int[] {1, 2, 3, 4, 5};
        String s = "";
        for (int i: ia) {
            s += i;
        }
        System.out.println(s);
    }

}
