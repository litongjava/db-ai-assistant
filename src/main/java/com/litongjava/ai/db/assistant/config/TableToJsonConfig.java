package com.litongjava.ai.db.assistant.config;

import javax.sql.DataSource;

import com.jfinal.template.Engine;
import com.jfinal.template.source.ClassPathSourceFactory;
import com.litongjava.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.litongjava.jfinal.plugin.activerecord.OrderedFieldContainerFactory;
import com.litongjava.jfinal.plugin.activerecord.dialect.PostgreSqlDialect;
import com.litongjava.jfinal.plugin.hikaricp.DsContainer;
import com.litongjava.tio.boot.constatns.TioBootConfigKeys;
import com.litongjava.tio.boot.server.TioBootServer;
import com.litongjava.tio.utils.dsn.DbDSNParser;
import com.litongjava.tio.utils.dsn.JdbcInfo;
import com.litongjava.tio.utils.environment.EnvUtils;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class TableToJsonConfig {

  public DataSource dataSource() {
    String dsn = EnvUtils.get("DATABASE_DSN");
    if (dsn == null) {
      return null;
    }

    JdbcInfo jdbc = new DbDSNParser().parse(dsn);

    int maximumPoolSize = EnvUtils.getInt("jdbc.MaximumPoolSize", 2);

    HikariConfig config = new HikariConfig();
    // config
    config.setJdbcUrl(jdbc.getUrl());
    config.setUsername(jdbc.getUser());
    config.setPassword(jdbc.getPswd());
    config.setMaximumPoolSize(maximumPoolSize);

    HikariDataSource hikariDataSource = new HikariDataSource(config);

    // set datasource
    DsContainer.setDataSource(hikariDataSource);
    // add destroy
    TioBootServer.me().addDestroyMethod(hikariDataSource::close);
    return hikariDataSource;
  }
  /*
   *
   * config ActiveRecordPlugin
   */
  public void activeRecordPlugin() {
    // get dataSource
    DataSource dataSource = dataSource();
    if (dataSource == null) {
      return;
    }
    // get env key
    String property = EnvUtils.get(TioBootConfigKeys.APP_ENV);

    // create arp
    ActiveRecordPlugin arp = new ActiveRecordPlugin(dataSource);
    arp.setContainerFactory(new OrderedFieldContainerFactory());
    if ("dev".equals(property)) {
      arp.setDevMode(true);
    }

    arp.setDialect(new PostgreSqlDialect());

    // config engine
    Engine engine = arp.getEngine();
    engine.setSourceFactory(new ClassPathSourceFactory());
    engine.setCompressorOn(' ');
    engine.setCompressorOn('\n');
    // add sql file
    // arp.addSqlTemplate("/sql/all_sqls.sql");
    // start
    arp.start();
    // add stop
    TioBootServer.me().addDestroyMethod(arp::stop);
  }
}
