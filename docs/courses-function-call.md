input
```
{
  "messages":
  [
    {
      "role": "system",
      "content": "1.你是一个数据库管理员,你可以从数据库中查询所有课程的信息. 2.数库的的字段是示例如下 3.用户查询课程至少需要指定院校名称,学期和课程名称 4.你也可以根据用户的自然语言描述做一些统计类的信息,比如 院校 Kap 在Fall 2024学期一共有多少和ICS相关的课程信息 5.你支持输入多种语言,例如中文和英文"
    },
    {
      "role": "assistant",
      "content": "可以的，请提供您希望查询的学期具体信息，例如学期名称（例如：Fall 2023），我将根据这些信息获取相关课程信息。"
    },
    {
      "role": "user",
      "content": "Fall 2024 LAW 101"
    },
    {
      "role": "assistant",
      "content": null,
      "function_call":
      {
        "name": "findCourses",
        "arguments": "{\"sql\":\"SELECT * FROM course WHERE term='Fall 2024' AND course='LAW 101' LIMIT 1 OFFSET 0\"}"
      }
    },
    {
      "role": "user",
      "content": "[{\"tenant_id\":1,\"focus_on\":\"SF,TXT0\",\"section\":\"0\",\"title\":\"S-The Hawai'i Legal System\",\"curr_waitlisted\":\"0\",\"wait_avail\":\"15\",\"updater\":null,\"institution\":\"KAP\",\"update_time\":null,\"credits\":\"3\",\"course\":\"LAW 101\",\"term\":\"Fall 2024\",\"curr_enrolled\":\"12\",\"id\":383274033461665792,\"crn\":\"31128\",\"creator\":null,\"subject_abbr\":\"LAW\",\"create_time\":null,\"dates\":\"08/26-12/20\",\"details_url\":\"https://www.sis.hawaii.edu/uhdad/./avail.class?i=KAP&t=202510&c=31128\",\"sources_url\":\"https://www.sis.hawaii.edu/uhdad/avail.classes?i=KAP&t=202510&s=LAW\",\"room\":\"ONLINE ASYNC\",\"deleted\":0,\"instructor\":\"C Y Arakaki\",\"subject_name\":null,\"days\":\"TBA\",\"time\":\"TBA\",\"seats_avail\":\"8\"}]"
    }
  ],
  "functions":
  [
    {
      "name": "findCourses",
      "description": "根据SQL查询获取课程信息",
      "parameters":
      {
        "type": "object",
        "properties":
        {
          "sql":
          {
            "description": "",
            "type": "string"
          }
        },
        "required":
        [
          "sql"
        ]
      }
    }
  ],
  "model": "gpt-4o"
}
```

```
{
    "id": "chatcmpl-9SKa16wgoMSCbLxAvPkniYdhLkNgp",
    "object": "chat.completion",
    "created": 1716539321,
    "model": "gpt-4o-2024-05-13",
    "choices": [
        {
            "index": 0,
            "message": {
                "role": "assistant",
                "content": "以下是 **Fall 2024** 学期中关于 **LAW 101** 课程的详细信息：\n\n**课程标题:** S-The Hawai'i Legal System  \n**课程代码:** LAW 101  \n**学分:** 3  \n**教师:** C Y Arakaki  \n**上课时间:** 待定 (TBA)  \n**上课日期:** 08/26 - 12/20  \n**授课地点:** 在线异步 (ONLINE ASYNC)  \n**注册情况:**  \n- 当前注册人数: 12  \n- 可用名额: 8  \n- 当前候补人数: 0  \n- 候补可用名额: 15  \n\n您可以通过以下链接获得更详细的信息：\n- [课程详细信息](https://www.sis.hawaii.edu/uhdad/./avail.class?i=KAP&t=202510&c=31128)\n- [所有课程信息来源](https://www.sis.hawaii.edu/uhdad/avail.classes?i=KAP&t=202510&s=LAW)\n\n如果您还有其他问题或需要更多信息，请告诉我。"
            },
            "logprobs": null,
            "finish_reason": "stop"
        }
    ],
    "usage": {
        "prompt_tokens": 624,
        "completion_tokens": 237,
        "total_tokens": 861
    },
    "system_fingerprint": "fp_729ea513f7"
}
```

function_call数据示例
```
{
  "id": "chatcmpl-9SNGHBcszefC8VXqy1lrMaIhKwoBe",
  "object": "chat.completion",
  "created": 1716549629,
  "model": "gpt-4o-2024-05-13",
  "choices": [
    {
      "index": 0,
      "message": {
        "role": "assistant",
        "content": null,
        "function_call": {
          "name": "findCourses",
          "arguments": "{\"sql\":\"SELECT * FROM course WHERE institution='KAP' and term='Fall 2024' AND course='LAW 101' LIMIT 1 OFFSET 0\"}"
        }
      },
      "logprobs": null,
      "finish_reason": "function_call"
    }
  ],
  "usage": {
    "prompt_tokens": 2183,
    "completion_tokens": 44,
    "total_tokens": 2227
  },
  "system_fingerprint": "fp_729ea513f7"
}

```