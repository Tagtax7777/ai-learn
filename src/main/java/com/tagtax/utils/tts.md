# 流式文本在线合成

更新时间：2025-12-08

# 接口描述

流式文本在线合成基于websocket协议，可以将输入的文本合成为二进制格式的语音数据。与发起多次短文本语音合成相比，可以获得更高的实时性，在用户输入文本的同时就能接近同步的返回合成音频数据，达到“边合成边播放”的效果。

## 功能说明

- 建议文本不超过2000 GBK字节，即1000个汉字或者字母数字；
- 输入的文本必须采用UTF-8编码；
- 支持多音字通过标注自行定义发音。格式如：重(chong2)报集团。

# 接口调用主流程

## 交互流程

![image.png](https://bce.bdstatic.com/doc/ai-cloud-share/SPEECH/image_6c7df8f.png)

## 握手建立连接

### 连接请求

握手阶段，客户端主动发起 websocket 连接请求

#### 请求地址

请求地址：wss://aip.baidubce.com/ws/2.0/speech/publiccloudspeech/v1/tts

#### 请求参数

URL中放置请求参数，参数如下：

| 参数名        | 类型   | 参数位置 | 是否必填 | 备注                                                         |
| ------------- | ------ | -------- | -------- | ------------------------------------------------------------ |
| access_token  | string | query    | 二选一   | [鉴权令牌](https://ai.baidu.com/ai-doc/REFERENCE/Ck3dwjhhu)  |
| Authorization | string | header   | 二选一   | [鉴权API Key](https://ai.baidu.com/ai-doc/REFERENCE/Ck3dwjhhu#二、api-key鉴权机制) |
| per           | string | query    | 必填     | [tts发音人](https://ai.baidu.com/ai-doc/SPEECH/Rluv3uq3d)    |

#### 请求示例

Text复制

```text
curl --location 'wss://aip.baidubce.com/ws/2.0/speech/publiccloudspeech/v1/tts?access_token=xxx&per=xxx'
```

### 连接响应

如果握手成功，会返回 101状态码，表示协议握手成功；如果握手失败，则根据不同错误类型返回不同 http status，详细说明如下：
**成功：**状态码101 Switching Protocols
**失败：**错误码及描述如下

| 状态码 | 状态信息              | 描述                       | 解决方案                       |
| ------ | --------------------- | -------------------------- | ------------------------------ |
| 400    | Bad Request           | 参数错误、参数缺失         | 请参考官网文档，自查token和per |
| 401    | Unauthorized          | 鉴权失败                   |                                |
| 403    | Forbidden             | 无访问权限，接口功能未开通 |                                |
| 404    | Not Found             | 输入的url错误              |                                |
| 429    | Too Many Requests     | 触发限流                   |                                |
| 500    | Internal Server Error | 服务器内部错误             |                                |
| 502    | Bad Request           | 后端服务连接失败           |                                |

##### 示例

Text复制

```text
429 Too Many Requests

{
    "code": 276001,
    "message": "Rate limit exceeded error"
}
```

##### 参数说明

| 参数名  | 类型   | 是否必填 | 说明                              |
| ------- | ------ | -------- | --------------------------------- |
| code    | int    | 必填     | 错误码， 使用能力引擎标准的错误码 |
| message | string | 必填     | 错误消息                          |

## 初始化

握手成功后客户端和服务端会建立websocket连接，客户端通过websocket连接可以同时上传和接收数据。

### 客户端请求参数

- **帧类型（Opcode）：**Text
- **序列化方式：**JSON

##### 示例

Text复制

```text
{
    "type": "system.start",
    "payload": {
        "spd": 5,
        "pit": 5,
        "vol": 5,
        "audio_ctrl": "{\"sampling_rate\":16000}",
        "aue": 3
    }
}
```

##### 参数说明

| 参数名称 | 类型   | 是否必填 | 说明                              |
| -------- | ------ | -------- | --------------------------------- |
| type     | string | 必填     | 开始帧的类型，固定值 system.start |
| payload  | object | 可选     | 合成参数，详见下表                |

###### payload 字段说明

| 参数名称   | 类型   | 是否必填 | 说明                                                         |
| ---------- | ------ | -------- | ------------------------------------------------------------ |
| spd        | int    | 可选     | 语速，取值 0-15，默认为 5                                    |
| pit        | int    | 可选     | 音调，取值 0-15，默认为 5                                    |
| vol        | int    | 可选     | 音量，基础音库取值0-9，其他音库取值 0-15，默认为 5           |
| aue        | int    | 可选     | 音频格式，3=mp3-16k/24k，4=pcm-16k/24k，5=pcm-8k，6=wav-16k/24k，默认为3 |
| audio_ctrl | string | 可选     | 采样率，仅支持将采样率降采为16k。（格式：{"sampling_rate":16000}） |

### 服务端响应

- **帧类型（Opcode）：**Text
- **序列化方式：**JSON

#### 正常响应

- 服务端对客户端「开始合成」的响应
  - code 等于 0：表示通道各个参数已经设置完成；
  - code 不等于 0：表示通道参数设置有误，服务端会断开所有ws连接。

##### 示例

Text复制

```text
{
    "type": "system.started",
    "code": 0,
    "message": "success",
    "headers": {
        "session_id": "xxx"
               }
}
```

##### 参数说明

| 参数名称     | 类型   | 是否必填 | 说明                                    |
| ------------ | ------ | -------- | --------------------------------------- |
| type         | string | 必填     | 固定值 system.started，表示开始帧的类型 |
| code         | int    | 必填     | 错误码，0 表示成功                      |
| message      | string | 必填     | 错误信息                                |
| headers      | object | 必填     | 系统定义的请求头                        |
| + session_id | string | 必填     | sn 信息，格式为 [0-9a-zA-Z_-]{1,64}     |

#### 异常响应

##### 示例

Text复制

```text
{
    "type": "system.started",
    "code": 216100,
    "message": "语速参数错误, 请输入0-15的整数",
    "headers": {
        "session_id": "xxx"
               }
}
```

##### 异常响应列表

| type             | code   | message                                         | 说明                      |
| ---------------- | ------ | ----------------------------------------------- | ------------------------- |
| `system.started` | 216100 | 参数错误                                        | 参数类型错误              |
| `system.started` | 216100 | 语速参数错误, 请输入0-15的整数                  | 语速取值超限              |
| `system.started` | 216100 | 音调参数错误, 请输入0-15的整数                  | 音调取值超限              |
| `system.started` | 216100 | 基础音库音量参数错误, 请输入0-9的整数           | 基础音库音量取值超限      |
| `system.started` | 216100 | 精品/臻品音库音量参数错误, 请输入0-15的整数     | 精品/臻品音库音量取值超限 |
| `system.started` | 216100 | 音频格式错误, 3:mp3, 4:pcm-16k, 5:pcm-8k, 6:wav | 音频格式取值错误          |

#### 不响应

- 已经发送过开始合成

## 发送文本

### 客户端请求

- **帧类型：**Text
- **序列化方式：**JSON

##### 示例

Text复制

```text
{
    "type": "text",
    "payload": {
        "text": "需要进行语音合成的文字"
    }
}
```

##### 参数说明

| 参数名称 | 类型   | 是否必填 | 说明                      |
| -------- | ------ | -------- | ------------------------- |
| type     | string | 必填     | 数据帧的类型，固定值 text |
| payload  | array  | 可选     | 具体见下表                |

###### payload 字段说明

| 参数名称 | 类型   | 是否必填 | 说明                                 |
| -------- | ------ | -------- | ------------------------------------ |
| + text   | string | 必填     | 需要进行语音合成的文字，不超过1000字 |

### 服务端响应

#### 正常响应

Text复制

```text
WebSocket opcode`：`binary
WebSocket消息体：音频二进制数据
```

#### 异常响应

##### 示例

Text复制

```text
{
    "type": "system.error",
    "code": 216103,
    "message": "文本过长, 请控制在1000字以内",
    "headers": {
        "session_id": "xxx"
    }
}
```

##### 异常响应列表-文本限制说明

1. 单次发送文本不超过1000汉字，若超过，下发提示帧【文本过长，请控制在1000字以内】
2. 未处理文本长度超过10000汉字后不处理新增文本，下发提示帧【当前待处理文本过长，请稍后发送】

| type | code   | message                        | 说明                         |
| ---- | ------ | ------------------------------ | ---------------------------- |
| text | 216101 | 参数缺失                       | 缺少必需的参数               |
| text | 216103 | 文本过长, 请控制在1000字以内   | 单次文本不能超过1000字       |
| text | 216419 | 当前待处理文本过长，请稍后发送 | 发送频率过快，待处理文本过长 |

#### 不响应

- 未发送开始合成就发送文本
- 已经发送结束合成仍发送文本
- 发送文本信息无内容
- 发送文本信息无标点隔断，server等待标点或字符数变多再进行合成

## 结束合成

### 客户端请求

- **帧类型（Opcode）：**Text
- **序列化方式：**JSON

#### 正常响应

- 表示客户端没有文本需要合成了，客户端要求服务端立即合成所有缓存的文本；
- 注意事项：
  - 由于流式文本语音合成接口，服务端会分句合成音频，所以服务端会缓存客户端的文本，客户端需要再没有文本合成时，立刻发送此消息，否则可能丢失文本。
  - 客户端发送此消息后，服务端不在接收任何客户端后续请求。

##### 示例

Text复制

```text
{
    "type":"system.finish",
}
```

##### 参数说明

| 参数名称 | 类型   | 是否必填 | 说明                               |
| -------- | ------ | -------- | ---------------------------------- |
| type     | string | 必填     | 结束帧的类型，固定值 system.finish |

### 服务端响应

- **帧类型：**Text
- **序列化方式：**JSON

#### 正常响应

- 服务端对客户端「合成结束」的响应，表示服务端已经完成所有文本的合成，且所有音频数据下发完毕。

##### 示例

Text复制

```text
{
    "type":"system.finished",
    "code": 0,
    "message": "success",
}
```

##### 参数说明

| 参数名称 | 类型   | 是否必填 | 说明                                 |
| -------- | ------ | -------- | ------------------------------------ |
| type     | string | 必填     | 结束帧的类型，固定值 system.finished |
| code     | int    | 必填     | 错误码，0表示成功                    |
| message  | string | 必填     | 错误信息                             |

#### 异常响应

暂无

#### 不响应

- 文本未合成完成，合成结束后会响应结束

## 断开连接

### 正常断开

服务端响应结束合成后，会主动断开连接

### 异常断开

客户端超过一分钟不发送消息，服务端会异常断开连接

## DEMO

**python**

~~~python
#coding=utf8
import asyncio
import websockets
import json
from enum import Enum
import time


class BaiduTTSErrorCode(Enum):
    """
    百度 TTS 错误码枚举
    """
    SUCCESS = (0, "成功")
    PARAMETER_MISSING = (216101, "参数缺失")
    TEXT_TOO_LONG = (216103, "文本过长，请控制在1000字以内")
    TEXT_PENDING_TOO_LONG = (216419, "当前待处理文本过长，请稍后发送")
    SPEED_OUT_OF_RANGE = (216100, "语速参数错误，请输入0-15的整数")
    PITCH_OUT_OF_RANGE = (216100, "音调参数错误，请输入0-15的整数")
    VOLUME_OUT_OF_RANGE = (216100, "音量参数错误，请输入0-9或0-15的整数")
    AUDIO_FORMAT_ERROR = (216100, "音频格式错误，支持 3:mp3, 4:pcm-16k, 5:pcm-8k, 6:wav")
    AUTH_FAILED = (401, "鉴权失败")
    FORBIDDEN = (403, "无访问权限，接口功能未开通")
    NOT_FOUND = (404, "输入的 URL 错误")
    TOO_MANY_REQUESTS = (429, "触发限流")
    INTERNAL_SERVER_ERROR = (500, "服务器内部错误")
    BACKEND_CONNECTION_FAILED = (502, "后端服务连接失败")

    @classmethod
    def get_message(cls, code):
        """
        根据错误码获取错误信息
        :param code: 错误码
        :return: 错误信息
        """
        for error in cls:
            if error.value[0] == code:
                return error.value[1]
        return "未知错误码"


class BaiduTTSWebSocketSDK:
    def __init__(self, authorization, per="4146", base_url="wss://aip.baidubce.com/ws/2.0/speech/publiccloudspeech/v1/tts"):
        """
        初始化 SDK 实例
        :param access_token: 鉴权令牌
        :param per: 发音人参数，默认值为 4146
        :param base_url: WebSocket 服务的基础 URL
        """
        self.authorization = authorization
        self.per = per
        self.base_url = base_url
        self.url = f"{self.base_url}?access_token={self.authorization}&per={self.per}"
        self.websocket = None

    async def connect(self):
        """
        建立 WebSocket 连接
        """
        try:
            headers = {
                "Authorizaion": "Bearer " + self.authorization
			}
            self.websocket = await websockets.connect(self.url, extra_headers=headers)
            print("WebSocket 连接已建立")
        except Exception as e:
            print("WebSocket 连接失败:", e)
            raise

    async def send_start_request(self, spd=5, pit=5, vol=5, audio_ctrl="{\"sampling_rate\":16000}", aue=3):
        """
        发送开始合成请求
        :param spd: 语速，默认值为 5
        :param pit: 音调，默认值为 5
        :param vol: 音量，默认值为 5
        :param audio_ctrl: 采样率控制，默认值为 {"sampling_rate":16000}
        :param aue: 音频格式，默认值为 3 (mp3)
        """
        start_payload = {
            "type": "system.start",
            "payload": {
                "spd": spd,
                "pit": pit,
                "vol": vol,
                "audio_ctrl": audio_ctrl,
                "aue": aue
            }
        }
        try:
            await self.websocket.send(json.dumps(start_payload))
            print("发送开始合成请求:", start_payload)
            response = await self.websocket.recv()
            response_data = json.loads(response)
            print("收到服务端响应:", response_data)

            # 检查错误码
            code = response_data.get("code", -1)
            if code != 0:
                error_message = BaiduTTSErrorCode.get_message(code)
                raise Exception(f"错误码: {code}, 错误信息: {error_message}")

            return response_data
        except Exception as e:
            print("发送开始合成请求失败:", e)
            raise

    async def send_text_request(self, text):
        """
        发送文本合成请求
        :param text: 需要合成的文本
        """
        text_payload = {
            "type": "text",
            "payload": {
                "text": text
            }
        }
        try:
            await self.websocket.send(json.dumps(text_payload))
            print("发送文本合成请求:", text_payload)
        except Exception as e:
            print("发送文本合成请求失败:", e)
            raise

    async def receive_audio(self, output_file="output_file", timeout=5):
        """
        接收音频数据并保存到文件
        :param output_file: 保存音频的文件名，默认值为 output.mp3
        :param timeout: 接收音频数据的超时时间（秒），默认值为 10 秒
        """
        try:
            with open(output_file, "wb") as f:
                start_time = 0
                while True:
                    try:
                        # 设置超时时间
                        response = await asyncio.wait_for(self.websocket.recv(), timeout=timeout)
                    except asyncio.TimeoutError:
                        if (time.time() - start_time) >= timeout:
                            return
                        raise Exception("接收音频数据超时")

                    start_time = time.time()
                    if isinstance(response, bytes):
                        print("收到音频数据 (二进制)")
                        f.write(response)
                    else:
                        response_json = json.loads(response)
                        print("收到服务端响应:", response_json)
                        if response_json.get("type") == "system.error":
                            code = response_json.get("code", -1)
                            error_message = BaiduTTSErrorCode.get_message(code)
                            raise Exception(f"错误码: {code}, 错误信息: {error_message}")
                        break
        except Exception as e:
            print("接收音频数据失败:", e)
            raise

    async def send_finish_request(self):
        """
        发送结束合成请求
        """
        finish_payload = {
            "type": "system.finish",
        }
        try:
            await self.websocket.send(json.dumps(finish_payload))
            print("发送结束合成请求:", finish_payload)
            response = await self.websocket.recv()
            response_data = json.loads(response)
            print("收到结束响应:", response_data)

            # 检查错误码
            code = response_data.get("code", -1)
            if code != 0:
                error_message = BaiduTTSErrorCode.get_message(code)
                raise Exception(f"错误码: {code}, 错误信息: {error_message}")

            return response_data
        except Exception as e:
            print("发送结束合成请求失败:", e)
            raise

    async def close_connection(self):
        """
        关闭 WebSocket 连接
        """
        try:
            await self.websocket.close()
            print("WebSocket 连接已关闭")
        except Exception as e:
            print("关闭 WebSocket 连接失败:", e)
            raise

    async def synthesize(self, text, output_file="output_file", spd=5, pit=5, vol=5, audio_ctrl="", aue=3):
        """
        完整的语音合成流程：连接 -> 开始合成 -> 发送文本 -> 接收音频 -> 结束合成 -> 关闭连接
        :param text: 需要合成的文本
        :param output_file: 保存音频的文件名
        :param spd: 语速
        :param pit: 音调
        :param vol: 音量
        :param audio_ctrl: 采样率控制
        :param aue: 音频格式
        """
        try:
            await self.connect()
            await self.send_start_request(spd=spd, pit=pit, vol=vol, audio_ctrl=audio_ctrl, aue=aue)
            await self.send_text_request(text)
            await self.receive_audio(output_file=output_file)
            await self.send_finish_request()
        finally:
            await self.close_connection()


# 示例：使用 SDK
if __name__ == "__main__":
    Authorization = "YOUR_TOKEN/YOUR_API_KEY" # iam API_KEY或TOKEN二选一
    PER = 4146  # 替换为你的发音人参数
    sdk = BaiduTTSWebSocketSDK(authorization=Authorization, per=PER, base_url="wss://aip.baidubce.com/ws/2.0/speech/publiccloudspeech/v1/tts")

    async def main():
        text = "欢迎体验百度流式文本在线合成。"  # 替换为需要合成的文本
        output_file = "output.mp3"  # 保存音频的文件名
        await sdk.synthesize(text, output_file=output_file)

    asyncio.run(main())

~~~

