
import org.junit.Test;

import com.alibaba.fastjson2.JSON;

public class StringJsonTest {

  @Test
  public void test01() {
    String str = "What is the future?";
    byte[] jsonBytes = JSON.toJSONBytes(str);
    System.out.println(jsonBytes);
    
    Object parse = JSON.parse(jsonBytes);
    System.out.println(parse);
  }

}
