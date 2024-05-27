package com.litongjava.ai.db.assistant.instance;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;

public enum OkHttpClientPool {
  INSTANCE;

  static okhttp3.OkHttpClient.Builder builder;
  static {
    builder = new OkHttpClient().newBuilder();
    // 连接池
    builder.connectionPool(pool());
    // 信任连接
    builder.sslSocketFactory(sslSocketFactory(), x509TrustManager());
    // 连接超时
    builder.connectTimeout(120L, TimeUnit.SECONDS).readTimeout(120L, TimeUnit.SECONDS).build();

  }

  public static OkHttpClient getHttpClient() {
    return builder.build();
  }

  private static ConnectionPool pool() {
    return new ConnectionPool(200, 5, TimeUnit.MINUTES);
  }

  public static X509TrustManager x509TrustManager() {
    return new X509TrustManager() {
      @Override
      public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
      }

      @Override
      public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
      }

      @Override
      public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
      }
    };
  }

  public static SSLSocketFactory sslSocketFactory() {
    try {
      // 信任任何链接
      SSLContext sslContext = SSLContext.getInstance("TLS");
      sslContext.init(null, new TrustManager[] { x509TrustManager() }, new SecureRandom());
      return sslContext.getSocketFactory();
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    } catch (KeyManagementException e) {
      e.printStackTrace();
    }
    return null;
  }

}