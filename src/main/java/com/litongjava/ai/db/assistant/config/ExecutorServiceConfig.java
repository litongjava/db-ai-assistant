package com.litongjava.ai.db.assistant.config;

import java.util.concurrent.ExecutorService;

import com.litongjava.tio.boot.server.TioBootServer;
import com.litongjava.tio.utils.thread.ThreadUtils;

public class ExecutorServiceConfig {

  public void config() {
    // 创建包含10个线程的线程池
    ExecutorService executor = ThreadUtils.newFixedThreadPool(10);

    // 项目关闭时，关闭线程池
    TioBootServer.me().addDestroyMethod(() -> {
      if (executor != null && !executor.isShutdown()) {
        executor.shutdown();
      }
    });
  }
}

