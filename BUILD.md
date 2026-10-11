# 二维码 App (com.zcj.qr) 构建指南

## 环境要求
- Android Studio Hedgehog (2023.1.1) 或更高版本
- JDK 17
- Android SDK API 34
- Android SDK Build-Tools 34.0.0

## 构建步骤

### 方法一：Android Studio
1. 打开 Android Studio
2. File → Open → 选择 `~/workspace` 目录
3. 等待 Gradle 同步完成
4. Build → Build Bundle(s) / APK(s) → Build APK(s)
5. APK 位于 `app/build/outputs/apk/debug/app-debug.apk`

### 方法二：命令行
```bash
cd ~/workspace
./gradlew assembleDebug
# APK 位于 app/build/outputs/apk/debug/app-debug.apk
```

## 项目结构
```
app/src/main/
├── java/com/zcj/qr/
│   ├── QrApp.kt                    # Application 入口
│   ├── MainActivity.kt             # 主界面（双 Tab）
│   ├── crypto/
│   │   ├── Aes256GcmCrypto.kt      # AES-256-GCM 加密
│   │   └── QrProtocolParser.kt     # qr:// 协议解析
│   ├── data/
│   │   ├── QrDatabase.kt           # Room 数据库
│   │   ├── dao/HistoryDao.kt       # 历史数据 DAO
│   │   └── repository/HistoryRepository.kt
│   ├── ui/
│   │   ├── scan/                   # 扫描功能
│   │   ├── generate/               # 生成功能
│   │   └── history/                # 历史记录
│   └── util/
│       ├── QrCodeGenerator.kt      # 二维码生成（支持美化）
│       └── ProtocolHandler.kt      # 特殊协议处理
└── res/                            # 资源文件
```

## 核心功能
- 扫码：相机实时扫描 + 相册图片扫描
- 生成：WiFi/联系人/网址/电话/短信/邮件/自定义文字
- 加密：AES-256-GCM 加密（qr:// 前缀）
- 历史：本地 Room 数据库管理
- UI：液态玻璃质感，白天白底/夜间黑底自动切换
