# ddns-ali

#### 介绍

Java实现阿里动态域名解析DDNS，可设置定时任务自动执行。

#### 运行环境

1. JDK 17
2. Maven 3.x

#### 配置说明

在程序jar包同级目录创建 `config.yml`：

```yaml
regionId: cn-hangzhou
accessKeyId: your_access_key_id
accessKeySecret: your_access_key_secret

domains:
  - domainName: example.com
    RR: www
    recordType: A
```

也可以使用环境变量配置阿里云AccessKey，环境变量优先级高于 `config.yml`：

```bash
export ALIYUN_ACCESS_KEY_ID=your_access_key_id
export ALIYUN_ACCESS_KEY_SECRET=your_access_key_secret
```

使用ddns前请确保运行机器可以获取公网IPv4。

#### 打包

```bash
mvn clean package
```

打包后文件位于：

```bash
target/ddns-ali.jar
```

#### 使用说明

执行一次更新：

```bash
java -jar ddns-ali.jar
```

定时自动执行，在命令后加入 `-interval [分钟数]`。

例如每10分钟执行一次：

```bash
java -jar ddns-ali.jar -interval 10
```

#### systemd 示例

```ini
[Unit]
Description=Aliyun DDNS Client
After=network-online.target

[Service]
WorkingDirectory=/opt/ddns-ali
ExecStart=/usr/bin/java -jar /opt/ddns-ali/ddns-ali.jar -interval 10
Restart=always
RestartSec=10
EnvironmentFile=-/opt/ddns-ali/.env

[Install]
WantedBy=multi-user.target
```

#### 优化说明

- 公网IP获取增加多个服务源重试和连接/读取超时。
- 定时任务单次失败不会终止后续执行。
- 查询解析记录后会按 `RR` 和 `recordType` 精确匹配，避免误更新。
- 配置文件启动时进行基础校验。
- 阿里云AccessKey支持通过环境变量提供。