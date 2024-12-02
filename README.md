# ApkChannelTool

## 介绍
支持一键签名，多渠道(美团Walle)打包工具。

![Image text](/screenshot/img1.png)

点击下载：[ApkSignTool_v1.0.2](https://gitee.com/pizhuzz/apk-channel-tool/releases/download/v1.0.2/ApkSignTool_v1.0.2.exe)

## 更新日志

### 2024/11/22
1. 增加日志输出

## 使用
执行gradle task：`launch4j`->`createExe`

命令使用：
```groovy
./gradlew createExe 
```

会在build/launch4j 生成对应ApkSignTool_xxx.exe文件，直接拿来使用即可。
