# 毕业设计管理系统

这是一个基于 JavaWeb 的毕业设计管理系统课程设计项目，采用 Maven 管理依赖，使用 Servlet、JSP、JSTL、MySQL 实现学生、教师、管理员三类用户的毕业设计流程管理。

## 技术栈

- Java 8
- Maven
- Servlet 3.1
- JSP / JSTL
- MySQL 8
- Tomcat 9

## 项目结构

```text
.
├── pom.xml
├── src/main/java
│   ├── bean        # 实体类
│   ├── controller  # Servlet 控制器
│   ├── dao         # 数据访问层
│   ├── dbutil      # 数据库连接工具
│   ├── filter      # 登录权限和编码过滤器
│   └── util        # 工具类
├── src/main/webapp
│   ├── login.jsp
│   └── WEB-INF     # JSP 页面、公共布局、web.xml
├── sql
│   └── init.sql    # 数据库初始化脚本
├── scripts         # 本地 Tomcat 部署脚本
└── docs            # 课程文档
```

## 已实现功能

管理员模块：

- 用户管理：学生、教师、管理员信息增删改查。
- 课题审核：审核教师提交的课题，并支持修改、通过、驳回。
- 在线选题管理：控制选题轮次、审批学生选题、查看未选学生和未被选择课题、手动分配课题。
- 文件模板管理：上传和下载模板文件。
- 文档管理：查看学生提交的开题报告、中期报告、毕业论文等材料。
- 公告管理：发布和维护系统公告。
- 系统开关：控制教师出题、学生选题、文档上传等流程状态。

教师模块：

- 课题管理：提交毕业设计课题。
- 任务管理：发布阶段任务。
- 资料管理：上传参考资料供学生下载。
- 选题审批：查看并处理学生选题申请。
- 文档审核：下载学生材料，填写审核意见和分数。
- 答疑管理：回复学生提问。
- 答辩评分：填写答辩意见和成绩。

学生模块：

- 浏览课题并提交选题申请。
- 查看自己的选题状态。
- 下载任务、资料和文件模板。
- 上传开题报告、中期报告、毕业论文等材料。
- 向教师提问并查看答复。
- 查看阶段成绩和答辩成绩。

## 仍需继续补齐的流程

当前项目还在按任务书继续完善，后续重点包括：

- 教务员批量导入有资格参加毕业设计的学生和教师信息。
- 明确第二轮选题学生名单、第二轮可选题目名单。
- 明确最终未选题学生名单、未被选择题目名单。
- 增加源代码材料提交类型。
- 增加教师自评意见、互评意见和综合成绩录入。
- 增加答辩结束后的归档/流程结束状态。

## 数据库初始化

先创建并初始化数据库：

```powershell
mysql --default-character-set=utf8mb4 -uroot -p < sql/init.sql
```

如果使用绝对路径，也可以执行：

```powershell
mysql --default-character-set=utf8mb4 -uroot -p -e "source E:/Desktop/web_course_design/sql/init.sql;"
```

数据库连接配置位于：

```text
src/main/java/dbutil/SQLHelper.java
```

项目默认连接本机 `graduation_design` 数据库，用户名默认 `root`，密码默认空。为了避免把本机密码提交到 GitHub，实际运行时建议通过环境变量配置：

```powershell
setx GD_DB_USERNAME "root"
setx GD_DB_PASSWORD "你的MySQL密码"
setx GD_DB_URL "jdbc:mysql://localhost:3306/graduation_design?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=utf8&allowPublicKeyRetrieval=true"
```

`setx` 设置后需要重新打开 Cursor、终端和 Tomcat 才会生效。当前 PowerShell 会话临时运行时可以这样设置：

```powershell
$env:GD_DB_USERNAME="root"
$env:GD_DB_PASSWORD="你的MySQL密码"
```

## 编译运行

编译 WAR 包：

```powershell
mvn clean package
```

使用本地 Tomcat 9 快速部署：

```powershell
.\scripts\quick-deploy-tomcat9.ps1 -ProjectDir "E:\Desktop\web_course_design" -TomcatHome "E:\apache-tomcat-9.0.115" -ContextPath "graduation-design" -Port 8080
```

部署成功后访问：

```text
http://localhost:8080/graduation-design/
```

## 测试账号

| 角色 | 用户名 | 密码 |
| --- | --- | --- |
| 管理员 | admin | admin123 |
| 教师 | teacher01 | 123456 |
| 教师 | teacher02 | 123456 |
| 学生 | student01 | 123456 |
| 学生 | student02 | 123456 |
| 学生 | student03 | 123456 |

## GitHub 上传说明

建议上传当前 Maven 项目主体即可，`target/`、IDE 配置、临时文件、上传文件目录已经通过 `.gitignore` 排除。

如果任务书文档仍在根目录，是因为该文件可能正在被 Word 占用；关闭 Word 后可以移动到 `docs/` 目录再上传。
