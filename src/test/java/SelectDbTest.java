import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.litongjava.ai.db.assistant.config.TableToJsonConfig;
import com.litongjava.data.model.DbJsonBean;
import com.litongjava.data.services.DbJsonService;
import com.litongjava.jfinal.aop.Aop;
import com.litongjava.jfinal.plugin.activerecord.Record;
import com.litongjava.tio.boot.tesing.TioBootTest;

public class SelectDbTest {

  DbJsonService dbJsonService = Aop.get(DbJsonService.class);
  
  @BeforeClass
  public static void before() throws Exception {
    TioBootTest.before(TableToJsonConfig.class);
  }

  @Test
  public void test() {
    String tableName = "institution";
    DbJsonBean<List<Record>> listAll = dbJsonService.listAll(tableName);
    System.out.println(listAll.getData().size());
  }
}
