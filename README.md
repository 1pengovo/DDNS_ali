# ddns-ali

#### 介绍

java实现阿里动态域名解析DDNS，可设置定时任务自动执行

#### 运行环境

1.JDK版本 17.0.4.1 其他版本未测试

#### 使用说明

1. 使用ddns前请确保已取得公网ip

2. 修改\application下的config.yml配置文件

3. 将控制台目录切换到\application目录下，输入`java -jar ddns-ali-1.0.jar`命令执行一次更新

4. 如需实现定时自动执行，在命令后加入`-interval [分钟数]`。

   例如需要每10分钟执行一次，输入 `java -jar ddns-ali-1.0.jar -interval 10` 命令。

#### 运行截图

1.单次执行

![](https://webdemo-1.oss-cn-hangzhou.aliyuncs.com/%E5%BE%AE%E4%BF%A1%E6%88%AA%E5%9B%BE_20230916133658%281%29%281%29.png)

2.定时自动执行

![](https://webdemo-1.oss-cn-hangzhou.aliyuncs.com/%E5%BE%AE%E4%BF%A1%E6%88%AA%E5%9B%BE_20230916135414%281%29%281%29.png)
