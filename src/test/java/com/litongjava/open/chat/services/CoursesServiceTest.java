package com.litongjava.open.chat.services;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.jfinal.kit.Kv;
import com.litongjava.ai.db.assistant.config.TableToJsonConfig;
import com.litongjava.ai.db.assistant.services.CoursesService;
import com.litongjava.jfinal.aop.Aop;
import com.litongjava.tio.boot.tesing.TioBootTest;
import com.litongjava.tio.utils.json.JsonUtils;

public class CoursesServiceTest {

  CoursesService coursesService = Aop.get(CoursesService.class);

  @BeforeClass
  public static void before() throws Exception {
    TioBootTest.before(TableToJsonConfig.class);
  }

  @Test
  public void test() {
    // String sql = "select * from course where term='Fall 2023' limit 1 offset 0";
    String sql = "SELECT * FROM course WHERE term='Fall 2024' AND course='LAW 101' LIMIT 1 OFFSET 0";
    List<Kv> kvs = coursesService.find(sql);
    String json = JsonUtils.toJson(kvs);
    System.out.println(json);
    String jsonString = "[{\"tenant_id\":1,\"focus_on\":\"SF,TXT0\",\"section\":\"0\",\"title\":\"S-The Hawai'i Legal System\",\"curr_waitlisted\":\"0\",\"wait_avail\":\"10\",\"updater\":null,\"institution\":\"KAP\",\"update_time\":null,\"credits\":\"3\",\"course\":\"LAW 101\",\"term\":\"Fall 2023\",\"curr_enrolled\":\"17\",\"id\":260959498091368448,\"crn\":\"31141\",\"creator\":null,\"subject_abbr\":\"LAW\",\"create_time\":null,\"dates\":\"08/21-12/15\",\"details_url\":\"https://www.sis.hawaii.edu/uhdad/./avail.class?i=KAP&t=202410&c=31141\",\"sources_url\":\"https://www.sis.hawaii.edu/uhdad/avail.classes?i=KAP&t=202410&s=LAW\",\"room\":\"ONLINE ASYNC\",\"deleted\":0,\"instructor\":\"S Jaworowski\",\"subject_name\":null,\"days\":\"TBA\",\"time\":\"TBA\",\"seats_avail\":\"3\"}]";
    String jsonString1="  ";
  }

}
