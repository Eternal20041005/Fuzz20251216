# 参数敏感数据库模糊测试平台

本项目是一个完整的参数敏感数据库模糊测试平台，用于测试和优化数据库参数配置，提高数据库性能和稳定性。系统采用前后端分离架构，后端基于Spring Boot 3.x，前端基于Vue 3 + TypeScript + Vite。

## 项目架构

- **前端**：Vue 3 + TypeScript + Vite + Tailwind CSS
- **后端**：Spring Boot 3.2.0 + JPA + HikariCP
- **数据库**：MySQL 8.0

## 目录结构

```
E:/Fuzz/
├── BackEnd/                      # 后端 Spring Boot 应用
│   ├── pom.xml                   # Maven 项目配置和依赖
│   ├── src/main/java/com/fuzz/   # Java 源代码
│   ├── src/main/resources/       # 配置文件和资源
│   └── target/                   # 构建产物
├── FrontEnd/                     # 前端 Vue 3 应用
│   ├── design/                   # 设计原型文件
│   ├── dist/                     # 构建产物目录
│   ├── src/                      # TypeScript 源代码
│   ├── index.html                # 应用入口 HTML
│   ├── package.json              # npm 依赖配置
│   └── vite.config.ts            # Vite 构建配置
├── 数据库初始化指南.md             # 数据库初始化说明
├── setup-mysql.ps1               # MySQL 设置脚本
├── test-api.http                 # API 测试文件
├── test-apis.ps1                 # API 测试 PowerShell 脚本
├── test-frontend.html            # 前端测试文件
└── update-db-password.ps1        # 更新数据库密码脚本
```

## 核心功能模块

### 1. 数据库配置管理
- 添加、编辑、删除数据库连接配置
- 支持MySQL和PostgreSQL数据库
- 测试数据库连接有效性
- 显示数据库连接状态

### 2. 参数模板管理
- 预设常用数据库参数模板
- 按类别（连接、内存、缓存等）组织参数
- 参数搜索和过滤
- 批量更新参数设置

### 3. 参数导入功能
- 从目标数据库实时导入当前参数值
- 比较导入值与默认值差异
- 标记参数为测试项

### 4. 模糊测试执行
- 选择参数进行模糊测试
- 配置测试参数和阈值
- 执行测试并生成报告
- 可视化展示测试结果

### 5. 测试报告分析
- 测试参数性能对比
- 生成图表展示测试结果
- 提供代码示例和优化建议

## 主要文件说明

### 后端核心文件

1. **pom.xml** - Maven项目配置，定义了Spring Boot 3.2.0及相关依赖
2. **application.yml** - Spring Boot应用配置，包含服务器、数据库连接、JPA和连接池配置
3. **ParameterTemplate.java** - 参数模板实体类，定义数据库参数模板结构
4. **DatabaseConnectionServiceImpl.java** - 数据库连接服务实现，处理数据库连接和参数读取
5. **ParameterTemplateRepository.java** - 参数模板数据访问接口

### 前端核心文件

1. **DatabaseParameterManager.vue** - 数据库参数管理主组件
2. **package.json** - 前端项目依赖配置，包含Vue 3、TypeScript和Vite
3. **main.ts** - Vue应用入口文件
4. **App.vue** - 应用主组件，包含布局和路由管理
5. **parameterApi.ts** - 参数管理API接口封装

### 数据库文件

1. **BackEnd/src/main/resources/init-database.sql** - 初始化数据库结构和示例数据
   - 创建数据库和表结构
   - 插入示例数据库配置
   - 预设常用参数模板

2. **BackEnd/src/main/resources/test-database-config.sql** - 测试数据库配置和参数数据
   - 包含测试用数据库配置
   - 提供参数模板测试数据

3. **数据库初始化指南.md** - 详细的数据库初始化步骤说明

## 数据库设计

系统主要包含两个核心表：

### 1. database_config (数据库配置表)
- 存储数据库连接信息
- 包含名称、类型、URL、用户名、密码等字段
- 跟踪连接状态和更新时间

### 2. parameter_template (参数模板表)
- 存储数据库参数配置模板
- 包含参数名、描述、类别、默认值、类型等信息
- 支持参数范围限制和可选值设置

## 环境要求

- **JDK**: 21 或更高版本
- **MySQL**: 8.0 或更高版本
- **Node.js**: 16.x 或更高版本
- **npm**: 8.x 或更高版本
- **Maven**: 3.6 或更高版本

## 快速开始

### 1. 数据库准备

首先，确保MySQL数据库已安装并运行：

```bash
# 执行数据库初始化脚本
mysql -h localhost -u root -p123456 -e "source BackEnd/src/main/resources/init-database.sql" 
```

也可以参考 `数据库初始化指南.md` 进行更详细的数据库配置。

### 2. 后端服务启动

```bash
# 进入后端目录
cd BackEnd

# 启动后端服务
mvn spring-boot:run

```

后端服务默认运行在 `http://localhost:8080/api`。

### 3. 前端服务启动

```bash
# 进入前端目录
 cd FrontEnd

# 安装依赖
npm install

# 启动开发服务器
npm run dev

```

前端服务默认运行在 `http://localhost:5173`。如果5173端口被占用，Vite会自动切换到其他端口（如5174）。

### 4. CORS配置说明

项目已配置CORS支持，允许来自 `http://localhost:5173` 和 `http://localhost:5174` 的跨域请求。如果前端服务运行在其他端口，需要修改后端CORS配置：

1. 编辑文件：`BackEnd/src/main/java/com/fuzz/config/WebConfig.java`
2. 在 `allowedOrigins` 列表中添加新的前端端口地址
3. 重启后端服务使配置生效

### 5. 访问应用

- 前端应用: 默认 `http://localhost:5173`，或查看终端输出的实际端口
- 后端API: `http://localhost:8080/api`

### 6. 项目启动验证

成功启动后，前端页面应能正常加载，且API调用返回200状态码。如果遇到API请求失败，通常是CORS配置问题，请检查前端实际运行端口并确认已在后端配置中添加。

### 7. 常见启动问题处理

#### 端口冲突问题

如果遇到端口被占用的情况：

```bash
# 检查8080端口占用情况（Windows）
netstat -ano | findstr :8080

# 检查5173/5174端口占用情况（Windows）
netstat -ano | findstr :5173
netstat -ano | findstr :5174
```

可以选择关闭占用端口的进程，或者修改应用配置使用其他端口：

- 后端端口修改：编辑 `BackEnd/src/main/resources/application.yml` 中的 `server.port`
- 前端端口修改：在 `npm run dev` 命令后添加 `--port 自定义端口`

#### 数据库连接问题

确保MySQL服务正在运行，并且数据库配置正确：

```bash
# 启动MySQL服务（Windows）
net start mysql

# 检查数据库连接
mysql -u root -p -e "SHOW DATABASES;"
```

如果需要修改数据库连接参数，请编辑：
- 后端数据库配置：`BackEnd/src/main/resources/application.yml` 中的 `spring.datasource` 部分

#### Maven依赖问题

如果Maven依赖下载失败或版本冲突：

```bash
# 清理并重新下载依赖
cd BackEnd
mvn clean install -U
```

#### npm依赖问题

如果npm依赖安装失败：

```bash
cd FrontEnd
npm cache clean --force
npm install
```

### 8. 完整启动流程总结

1. 确保MySQL数据库服务已启动
2. 初始化数据库结构和数据
3. 启动后端Spring Boot服务
4. 安装前端依赖并启动开发服务器
5. 检查前后端服务是否正常运行
6. 访问前端应用，验证API调用是否正常
7. 如遇CORS问题，确认前后端端口配置是否匹配

按照以上步骤操作，即可顺利启动整个参数敏感数据库模糊测试平台。

## 部署说明

### 生产环境构建

```bash
# 前端构建
cd FrontEnd
npm run build

# 后端构建
cd BackEnd
mvn clean package
```

### 环境配置

- 数据库连接配置位于 `BackEnd/src/main/resources/application.yml`
- 生产环境建议创建 `application-prod.yml` 覆盖默认配置
- 前端API请求地址配置在 `FrontEnd/src/api/index.ts`

## 后续优化建议

1. **字符编码统一** - 将数据库连接字符编码参数从 `utf8` 修改为 `utf8mb4` 以支持完整的UTF-8字符集
2. **前端资源本地化** - 将Tailwind CSS、Font Awesome和Chart.js从CDN迁移到本地依赖
3. **引入路由管理** - 使用Vue Router管理页面导航
4. **添加状态管理** - 引入Pinia进行前端状态管理
5. **增加用户认证** - 实现基于Spring Security的用户认证和授权
6. **性能优化** - 添加缓存层，优化大数据量参数查询性能

## 问题排查

### 常见问题

1. **数据库连接失败** - 检查MySQL服务是否运行，连接参数是否正确
2. **中文乱码** - 检查数据库字符集配置和应用编码设置
3. **服务启动失败** - 查看日志文件，确认端口占用和依赖问题

### 故障排除

- 查看后端日志获取详细错误信息
- 使用提供的测试脚本验证数据库连接和配置
- 检查网络防火墙设置是否允许端口访问

---

© 2024 参数敏感数据库模糊测试平台 | 技术支持：Spring Boot + Vue 3 团队


