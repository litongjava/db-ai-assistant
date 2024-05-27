# db-ai-assistant
## 简介
基于 Java 语言的tio-boot框架开发的数据查询框架，目前已经支持 open-webui。

[演示地址](https://www.bilibili.com/video/BV1i7421Z7cr/?vd_source=69e3cff470444b21e8c322dddec00def)
[自行编译t-boot](https://tio-boot.litongjava.com/zh/01_tio-boot%20%E7%AE%80%E4%BB%8B/06.html#%E5%AE%89%E8%A3%85-tio-%E4%BE%9D%E8%B5%96%E9%A1%B9)


## 主要功能

- text-to-sql 将用户输入的自然语言转为sql,执行sql,返回可视化的查询结果

## 启动

```sh
EXPORT OPENAI_API_KEY=""
java -jar target/db-ai-assistant-1.0.jar
```

启动后监听 80 端口，例如，我的 IP 地址是 `http://192.168.3.8`。

## 使用 open-webui 对接

```sh
docker run -d -p 3000:8080 \
  -v open-webui:/app/backend/data \
  -e OPENAI_API_BASE_URLS="http://192.168.3.8/openai/v1" \
  -e OPENAI_API_KEYS="contact https://github.com/litongjava" \
  --name open-webui \
  --restart always \
  ghcr.io/open-webui/open-webui:main
```

指定 `OPENAI_API_BASE_URLS` 为 db-ai-assistant 后端 IP，`OPENAI_API_KEYS` 可以随便填写，db-ai-assistant 会自动解析报文，并替换为指定的 `OPENAI_API_KEY`。

## 发送消息测试

测试效果：

![测试图片](readme_files/1.jpg)

## 消息流程

1. web 界面输入消息（这里是 open-webui）。
2. open-webui 将消息转发到应用端（这里是 db-ai-assistant）。
3. db-ai-assistant 将消息发送到大模型（这里是 OpenAI ChatGPT）。
4. ChatGPT 返回消息到 db-ai-assistant，db-ai-assistant 再将消息返回到 open-webui，open-webui 展示消息。

## 二次开发

开发者可以对 db-ai-assistant 进行二次开发，开发特定领域的 AI 代理。

## 联系作者
作者微信 jdk131219