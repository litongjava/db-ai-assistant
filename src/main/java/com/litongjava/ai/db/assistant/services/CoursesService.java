package com.litongjava.ai.db.assistant.services;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.jfinal.kit.Kv;
import com.litongjava.ai.db.assistant.constants.UhEduConstants;
import com.litongjava.jfinal.plugin.activerecord.Db;
import com.litongjava.jfinal.plugin.activerecord.Record;

public class CoursesService {

  public List<Kv> find(String sql) {
    List<Record> records = Db.find(sql);
    return records.stream().map(record -> {
      return recordToKv(record);
    }).collect(Collectors.toList());
  }

  private Kv recordToKv(Record record) {
    Map<String, Object> map = record.toMap();
    Kv kv = Kv.create();
    kv.set(map);
    String details_url = UhEduConstants.SIS + "/" + kv.getStr("details_url");
    kv.set("details_url", details_url);
    return kv;
  }
}
