package com.litongjava.ai.db.assistant.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.jfinal.kit.Kv;
import com.jfinal.template.Engine;
import com.jfinal.template.Template;
import com.litongjava.ai.db.assistant.constants.Fns;
import com.litongjava.ai.db.assistant.constants.Prompts;
import com.litongjava.data.services.DbService;
import com.litongjava.jfinal.aop.Aop;
import com.litongjava.jfinal.plugin.activerecord.Db;
import com.litongjava.tio.boot.constatns.TioBootConfigKeys;
import com.litongjava.tio.utils.dsn.DbDSNParser;
import com.litongjava.tio.utils.dsn.JdbcInfo;
import com.litongjava.tio.utils.environment.EnvUtils;
import com.litongjava.tio.utils.json.FastJson2Utils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OpenaiV1ChatService {
  private DbService dbService = new DbService();

  public JSONObject beforeCompletions(JSONObject openAiRequestVo) {
    Engine engine = Engine.use();
    Template template = engine.getTemplate("init_prompt.txt");
    String dsn = EnvUtils.get(TioBootConfigKeys.DATABASE_DSN);
    JdbcInfo jdbc = new DbDSNParser().parse(dsn);
    String dbType = jdbc.getDbType();

    String markdown = dbService.getAllTableColumnsOfMarkdown(Db.use());
    String dataExamples = dbService.getAllTableDataExamplesOfMarkdown(Db.use());

    Map<String, String> values = new HashMap<>();
    values.put("dbType", dbType);
    values.put("tables", markdown);
    values.put("table_examples", dataExamples);

    String renderToString = template.renderToString(values);
    JSONObject initPrompt = FastJson2Utils.parseObject(renderToString);

    JSONArray jsonArray = openAiRequestVo.getJSONArray("messages");
    String firstContent = jsonArray.getJSONObject(0).getString("content");
    log.info("content:{}", firstContent);
    if (firstContent.startsWith("init_prompt") || firstContent.startsWith(Prompts.phrase_sentence)) {
      return openAiRequestVo;
    } else {
      JSONObject jsonObject = initPrompt.getJSONArray("messages").getJSONObject(0);
      jsonArray.add(0, jsonObject);
    }

    JSONArray functions = openAiRequestVo.getJSONArray("functions");
    if (functions == null) {
      functions = initPrompt.getJSONArray("functions");
      openAiRequestVo.put("functions", functions);
    }

    return openAiRequestVo;
  }

  public String processLine(String line) {
//    log.info("line:{}", line);
    return line;
  }

  public String processData(String data) {
    return data;

  }

  public void completionContent(StringBuffer completionContent) {

    log.info("completionContent:{}", completionContent);

  }

  /**
   * result,mesage,error 
   * @return
   */
  public Kv functionCall(StringBuffer fnCallName, StringBuffer fnCallArgs) {
    log.info("fn:{},{}", fnCallName.toString(), fnCallArgs.toString());
    if (Fns.find.equals(fnCallName.toString())) {
      JSONObject parseObject = FastJson2Utils.parseObject(fnCallArgs.toString());
      String sql = parseObject.getString("sql");
      CoursesService coursesService = Aop.get(CoursesService.class);
      try {
        if (sql.contains("COUNT(") || sql.contains("LIMIT")) {
          List<Kv> lists = coursesService.find(sql);
          return Kv.by("result", lists);
        } else {
          int indexOf = sql.indexOf("FROM");
          if (indexOf > 0) {
            String sqlExceptSelect = sql.substring(indexOf);
            String tatalRowSql = Db.use().getConfig().getDialect().forPaginateTotalRow(null, sqlExceptSelect, null);
            Long count = Db.queryLong(tatalRowSql);
            if (count > 10) {
              String string = "total records are " + count + ", please set page size to 10";
              log.info(string);
              return Kv.by("message", string);
            }else {
              List<Kv> lists = coursesService.find(sql);
              return Kv.by("result", lists);
            }
          }
        }
      } catch (Exception e) {
        return Kv.by("error", e.getMessage());
      }
    }
    return null;

  }

}
