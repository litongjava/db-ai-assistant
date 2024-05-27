package com.litongjava.ai.db.assistant.config;

import org.junit.Test;

import com.litongjava.tio.utils.dsn.DbDSNParser;
import com.litongjava.tio.utils.dsn.JdbcInfo;
import com.litongjava.tio.utils.environment.EnvUtils;

public class DbConfigTest {

  @Test
  public void getDbType() {
    EnvUtils.load();
    String dsn = EnvUtils.get("DATABASE_DSN");

    JdbcInfo jdbc = new DbDSNParser().parse(dsn);
    System.out.println(jdbc.getUrl());
    System.out.println(jdbc.getDbType());
  }
}
