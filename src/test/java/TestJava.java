import com.isyscore.kotlin.common.HttpResponse;
import com.isyscore.kotlin.common.HttpUtilKt;
import org.junit.Test;

import java.util.Optional;

public class TestJava {

    @Test
    public void test() {
        HttpResponse ret = HttpUtilKt.httpGet(
                "https://devapi.heweather.net/v7/weather/3d?location=101210101&key=338e3ef0ebf54d8580c0b1043ec5bcef",null, null, 10000L);
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

    @Test
    public void testPrint() {
        System.out.println("a" + 1);
        System.out.println(1 + "a");
    }

    public static boolean hasText(String str) {
        return str != null && !str.isEmpty();
    }

    @Test
    public void testStream() {
        String key = "2333";
        String sKey = Optional.ofNullable(key).filter(TestJava::hasText).map(s -> "*" + s + "*").orElse(null);
        System.out.println(sKey);
    }

}
