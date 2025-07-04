# 知识学习系统

## 项目背景

知识学习系统是一个基于Spring Boot和MongoDB开发的应用，旨在帮助用户管理学习笔记、分析知识点并计算智慧值。系统采用大模型API分析笔记内容，提取关键信息和知识点，为用户提供更有效的学习体验。

## 技术栈

- Spring Boot 2.7.9
- MongoDB
- Redis
- Spring Security + JWT认证
- 大模型API集成

## 安装方法

### 环境要求

- JDK 1.8+
- Maven 3.6+
- MongoDB 4.4+
- Redis 6.0+

### 本地开发环境搭建

2. 配置MongoDB数据库：

确保MongoDB服务已启动，然后初始化数据库：
```bash
# 使用mongosh（MongoDB 5.0+）
mongosh mongodb://localhost:27017/enjoy_learning src/main/resources/db/mongodb_init.js
```

3. 配置Redis：

确保Redis服务已启动，默认配置为localhost:6379，可在application.yml中修改。

4. 配置大模型API：

在application.yml中配置大模型API的URL和API Key：
```yaml
ai:
  model:
    api:
      url: ""
      key: "YOUR_API_KEY"  # 替换为你的API密钥
    name: "DeepSeek-R1"
```

5. 编译并运行：
```bash
mvn clean package
java -jar target/knowledge-0.0.1.jar
```

或使用Spring Boot Maven插件：
```bash
mvn spring-boot:run
```

## 功能特性

- 笔记管理：上传、查看、编辑和删除学习笔记
- AI分析：利用大模型分析笔记内容，提取关键信息和知识点
- 智慧值计算：基于笔记内容、学习方式等计算智慧值
- 知识探索：通过知识图谱探索关联知识点

## 项目结构

```
src/
├── main/
│   ├── java/com/knowledge/springboot/
│   │   ├── common/          # 通用工具和响应类
│   │   ├── config/          # 配置类
│   │   ├── controller/      # 控制器
│   │   ├── domain/          # 实体类
│   │   ├── dto/             # 数据传输对象
│   │   ├── repository/      # 数据访问层
│   │   ├── service/         # 服务层
│   │   └── SpringbootApplication.java
│   └── resources/
│       ├── db/              # 数据库初始化脚本
│       └── application.yml  # 应用配置
└── test/                    # 测试代码
```

## MongoDB集合说明

系统使用了两个主要的MongoDB集合：

1. `learning_notes` - 存储学习笔记及其AI分析结果
2. `wisdom_records` - 存储每条笔记生成的智慧值记录

详细的集合结构和索引可以在 `/src/main/resources/db/mongodb_init.js` 文件中查看。

## API文档

待完善

## 开发团队

- @主要开发者

## 参与贡献

1. Fork 本仓库
2. 创建新分支 (`git checkout -b feature/your-feature`)
3. 提交更改 (`git commit -m 'Add some feature'`)
4. 推送分支 (`git push origin feature/your-feature`)
5. 创建 Pull Request
