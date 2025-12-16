# 数据库参数管理功能设计文档

## 概述

本设计文档描述了数据库参数管理功能的技术实现方案。该功能将在现有的Spring Boot + Vue3架构基础上，实现数据库参数的导入、展示、编辑和同步功能。系统将支持从实际数据库中读取配置参数，提供用户友好的界面进行参数管理，并支持分页、搜索、过滤等高级功能。

## 架构设计

### 整体架构
```
┌─────────────────┐    HTTP/REST API    ┌─────────────────┐
│   Vue3 前端     │ ◄─────────────────► │  Spring Boot    │
│                 │                     │     后端        │
│ - 参数列表组件   │                     │ - 参数控制器     │
│ - 分页组件      │                     │ - 参数服务      │
│ - 编辑组件      │                     │ - 参数仓库      │
└─────────────────┘                     └─────────────────┘
                                                │
                                                │ JDBC
                                                ▼
                                        ┌─────────────────┐
                                        │   MySQL 数据库   │
                                        │                 │
                                        │ - parameter_    │
                                        │   template      │
                                        │ - database_     │
                                        │   config        │
                                        └─────────────────┘
```

### 技术栈
- **前端**: Vue 3 + TypeScript + Tailwind CSS
- **后端**: Spring Boot + Spring Data JPA + MySQL
- **数据库**: MySQL 8.0+
- **通信**: RESTful API

## 组件和接口设计

### 前端组件设计

#### 1. DatabaseParameterManager 主组件
```typescript
interface DatabaseParameterManager {
  // 状态管理
  parameters: ParameterItem[]
  currentPage: number
  totalPages: number
  pageSize: number
  searchKeyword: string
  selectedCategory: string
  
  // 方法
  loadParameters(): Promise<void>
  importFromDatabase(dbConfigId: number): Promise<void>
  updateParameter(parameter: ParameterItem): Promise<void>
  searchParameters(keyword: string): void
  filterByCategory(category: string): void
  changePage(page: number): void
}
```

#### 2. ParameterList 列表组件
```typescript
interface ParameterListProps {
  parameters: ParameterItem[]
  onParameterUpdate: (parameter: ParameterItem) => void
}
```

#### 3. ParameterEditor 编辑组件
```typescript
interface ParameterEditorProps {
  parameter: ParameterItem
  onSave: (parameter: ParameterItem) => void
  onCancel: () => void
}
```

#### 4. Pagination 分页组件
```typescript
interface PaginationProps {
  currentPage: number
  totalPages: number
  onPageChange: (page: number) => void
}
```

### 后端API设计

#### 1. 参数管理控制器
```java
@RestController
@RequestMapping("/api/parameters")
public class ParameterController {
    
    // 获取参数列表（支持分页、搜索、过滤）
    @GetMapping
    public ResponseEntity<PagedResponse<ParameterTemplateDto>> getParameters(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size,
        @RequestParam(required = false) String search,
        @RequestParam(required = false) String category
    );
    
    // 从数据库导入参数
    @PostMapping("/import/{dbConfigId}")
    public ResponseEntity<ImportResult> importFromDatabase(
        @PathVariable Long dbConfigId
    );
    
    // 更新参数
    @PutMapping("/{id}")
    public ResponseEntity<ParameterTemplateDto> updateParameter(
        @PathVariable Long id,
        @RequestBody UpdateParameterRequest request
    );
    
    // 批量更新参数
    @PutMapping("/batch")
    public ResponseEntity<BatchUpdateResult> batchUpdateParameters(
        @RequestBody List<UpdateParameterRequest> requests
    );
}
```

#### 2. 数据库配置控制器
```java
@RestController
@RequestMapping("/api/database-configs")
public class DatabaseConfigController {
    
    // 获取数据库配置列表
    @GetMapping
    public ResponseEntity<List<DatabaseConfigDto>> getDatabaseConfigs();
    
    // 测试数据库连接
    @PostMapping("/{id}/test-connection")
    public ResponseEntity<ConnectionTestResult> testConnection(
        @PathVariable Long id
    );
}
```

### 服务层设计

#### 1. 参数服务接口
```java
public interface ParameterService {
    
    // 分页查询参数
    Page<ParameterTemplateDto> getParameters(
        int page, int size, String search, String category
    );
    
    // 从数据库导入参数
    ImportResult importParametersFromDatabase(Long dbConfigId);
    
    // 更新参数
    ParameterTemplateDto updateParameter(Long id, UpdateParameterRequest request);
    
    // 批量更新参数
    BatchUpdateResult batchUpdateParameters(List<UpdateParameterRequest> requests);
    
    // 获取数据库实际参数值
    Map<String, String> getDatabaseParameterValues(DatabaseConfig dbConfig);
}
```

#### 2. 数据库连接服务
```java
public interface DatabaseConnectionService {
    
    // 测试数据库连接
    ConnectionTestResult testConnection(DatabaseConfig config);
    
    // 获取数据库连接
    Connection getConnection(DatabaseConfig config);
    
    // 读取数据库参数
    Map<String, DatabaseParameter> readDatabaseParameters(DatabaseConfig config);
}
```

## 数据模型设计

### 1. 前端数据模型

#### ParameterItem 接口
```typescript
interface ParameterItem {
  id: number
  paramName: string
  description: string
  category: string
  defaultValue: string
  currentValue?: string
  paramType: 'INTEGER' | 'BOOLEAN' | 'STRING' | 'DECIMAL'
  isSelected: boolean
  isTestDefault: boolean
  minValue?: number
  maxValue?: number
  allowedValues?: string[]
}
```

#### PagedResponse 接口
```typescript
interface PagedResponse<T> {
  content: T[]
  totalElements: number
  totalPages: number
  currentPage: number
  pageSize: number
  hasNext: boolean
  hasPrevious: boolean
}
```

### 2. 后端数据模型

#### ParameterTemplate 实体
```java
@Entity
@Table(name = "parameter_template")
public class ParameterTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "param_name", nullable = false, unique = true)
    private String paramName;
    
    @Column(name = "description")
    private String description;
    
    @Column(name = "category", nullable = false)
    private String category;
    
    @Column(name = "default_value")
    private String defaultValue;
    
    @Column(name = "param_type")
    @Enumerated(EnumType.STRING)
    private ParameterType paramType;
    
    @Column(name = "is_test_default")
    private Boolean isTestDefault;
    
    @Column(name = "min_value")
    private String minValue;
    
    @Column(name = "max_value")
    private String maxValue;
    
    @Column(name = "allowed_values")
    private String allowedValues; // JSON格式存储
    
    // getters and setters
}
```

#### DatabaseParameter 值对象
```java
public class DatabaseParameter {
    private String name;
    private String value;
    private String description;
    private ParameterType type;
    private String minValue;
    private String maxValue;
    private List<String> allowedValues;
    
    // constructors, getters and setters
}
```

## 核心功能实现

### 1. 数据库参数导入功能

#### 导入流程
1. 用户选择数据库配置
2. 系统建立数据库连接
3. 执行参数查询SQL
4. 解析参数信息和类型
5. 保存到parameter_template表
6. 返回导入结果

#### MySQL参数查询SQL
```sql
-- 获取系统变量
SELECT 
    VARIABLE_NAME as param_name,
    VARIABLE_VALUE as current_value,
    'SYSTEM' as category
FROM INFORMATION_SCHEMA.GLOBAL_VARIABLES 
WHERE VARIABLE_NAME IN (
    'max_connections', 'wait_timeout', 'innodb_buffer_pool_size',
    'max_allowed_packet', 'tmp_table_size', 'key_buffer_size',
    'query_cache_size', 'thread_cache_size', 'sort_buffer_size'
);

-- 获取状态变量
SELECT 
    VARIABLE_NAME as param_name,
    VARIABLE_VALUE as current_value,
    'STATUS' as category
FROM INFORMATION_SCHEMA.GLOBAL_STATUS 
WHERE VARIABLE_NAME LIKE '%buffer%' OR VARIABLE_NAME LIKE '%cache%';
```

### 2. 参数类型识别

#### 类型识别逻辑
```java
public ParameterType identifyParameterType(String paramName, String value) {
    // 布尔类型参数
    if (BOOLEAN_PARAMS.contains(paramName.toLowerCase()) || 
        "ON".equalsIgnoreCase(value) || "OFF".equalsIgnoreCase(value)) {
        return ParameterType.BOOLEAN;
    }
    
    // 数值类型参数
    if (value.matches("\\d+")) {
        return ParameterType.INTEGER;
    }
    
    if (value.matches("\\d+\\.\\d+")) {
        return ParameterType.DECIMAL;
    }
    
    // 默认为字符串类型
    return ParameterType.STRING;
}
```

### 3. 分页实现

#### 后端分页查询
```java
@Service
public class ParameterServiceImpl implements ParameterService {
    
    public Page<ParameterTemplateDto> getParameters(
            int page, int size, String search, String category) {
        
        Specification<ParameterTemplate> spec = Specification.where(null);
        
        // 搜索条件
        if (StringUtils.hasText(search)) {
            spec = spec.and((root, query, cb) -> 
                cb.or(
                    cb.like(cb.lower(root.get("paramName")), "%" + search.toLowerCase() + "%"),
                    cb.like(cb.lower(root.get("description")), "%" + search.toLowerCase() + "%")
                )
            );
        }
        
        // 分类过滤
        if (StringUtils.hasText(category)) {
            spec = spec.and((root, query, cb) -> 
                cb.equal(root.get("category"), category)
            );
        }
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("paramName"));
        Page<ParameterTemplate> parameterPage = parameterRepository.findAll(spec, pageable);
        
        return parameterPage.map(this::convertToDto);
    }
}
```

#### 前端分页组件
```vue
<template>
  <div class="flex justify-center items-center space-x-2 mt-4">
    <button 
      @click="goToPage(currentPage - 1)"
      :disabled="currentPage <= 1"
      class="px-3 py-1 border rounded disabled:opacity-50"
    >
      上一页
    </button>
    
    <span 
      v-for="page in visiblePages" 
      :key="page"
      @click="goToPage(page)"
      :class="pageClass(page)"
      class="px-3 py-1 border rounded cursor-pointer"
    >
      {{ page }}
    </span>
    
    <button 
      @click="goToPage(currentPage + 1)"
      :disabled="currentPage >= totalPages"
      class="px-3 py-1 border rounded disabled:opacity-50"
    >
      下一页
    </button>
  </div>
</template>
```

### 4. 参数编辑器实现

#### 动态输入组件
```vue
<template>
  <div class="parameter-editor">
    <!-- 整数类型 -->
    <input 
      v-if="parameter.paramType === 'INTEGER'"
      type="number"
      v-model.number="editValue"
      :min="parameter.minValue"
      :max="parameter.maxValue"
      class="w-full p-2 border rounded"
    />
    
    <!-- 布尔类型 -->
    <select 
      v-else-if="parameter.paramType === 'BOOLEAN'"
      v-model="editValue"
      class="w-full p-2 border rounded"
    >
      <option value="ON">ON</option>
      <option value="OFF">OFF</option>
    </select>
    
    <!-- 枚举类型 -->
    <select 
      v-else-if="parameter.allowedValues && parameter.allowedValues.length > 0"
      v-model="editValue"
      class="w-full p-2 border rounded"
    >
      <option 
        v-for="value in parameter.allowedValues" 
        :key="value" 
        :value="value"
      >
        {{ value }}
      </option>
    </select>
    
    <!-- 字符串类型 -->
    <input 
      v-else
      type="text"
      v-model="editValue"
      class="w-full p-2 border rounded"
    />
  </div>
</template>
```

## 错误处理策略

### 1. 数据库连接错误
```java
@ExceptionHandler(SQLException.class)
public ResponseEntity<ErrorResponse> handleSQLException(SQLException ex) {
    ErrorResponse error = new ErrorResponse(
        "DATABASE_CONNECTION_ERROR",
        "数据库连接失败: " + ex.getMessage()
    );
    return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(error);
}
```

### 2. 参数验证错误
```java
@ExceptionHandler(MethodArgumentNotValidException.class)
public ResponseEntity<ErrorResponse> handleValidationException(
        MethodArgumentNotValidException ex) {
    
    String message = ex.getBindingResult().getFieldErrors().stream()
        .map(error -> error.getField() + ": " + error.getDefaultMessage())
        .collect(Collectors.joining(", "));
        
    ErrorResponse error = new ErrorResponse("VALIDATION_ERROR", message);
    return ResponseEntity.badRequest().body(error);
}
```

### 3. 前端错误处理
```typescript
// API调用错误处理
async function loadParameters() {
  try {
    loading.value = true
    const response = await parameterApi.getParameters({
      page: currentPage.value,
      size: pageSize.value,
      search: searchKeyword.value,
      category: selectedCategory.value
    })
    parameters.value = response.content
    totalPages.value = response.totalPages
  } catch (error) {
    console.error('加载参数失败:', error)
    ElMessage.error('加载参数失败，请稍后重试')
  } finally {
    loading.value = false
  }
}
```

## 测试策略

### 1. 单元测试
- 参数类型识别逻辑测试
- 数据库连接服务测试
- 参数验证逻辑测试
- 分页查询逻辑测试

### 2. 集成测试
- API端点测试
- 数据库操作测试
- 前后端集成测试

### 3. 端到端测试
- 参数导入流程测试
- 参数编辑保存测试
- 分页导航测试
- 搜索过滤功能测试

### 4. 测试数据准备
```sql
-- 测试数据库配置
INSERT INTO database_config (name, db_type, connection_url, username, password) 
VALUES ('测试MySQL', 'MySQL', 'jdbc:mysql://localhost:3306/test', 'root', 'password');

-- 测试参数模板
INSERT INTO parameter_template (param_name, description, category, default_value, param_type) 
VALUES 
('max_connections', '最大连接数', 'CONNECTION', '151', 'INTEGER'),
('innodb_buffer_pool_size', 'InnoDB缓冲池大小', 'MEMORY', '134217728', 'INTEGER'),
('query_cache_type', '查询缓存类型', 'CACHE', 'OFF', 'BOOLEAN');
```

## 性能优化

### 1. 数据库优化
- 为parameter_template表的param_name和category字段添加索引
- 使用数据库连接池管理连接
- 实现查询结果缓存

### 2. 前端优化
- 实现虚拟滚动处理大量数据
- 使用防抖处理搜索输入
- 实现组件懒加载

### 3. API优化
- 实现响应数据压缩
- 添加适当的缓存头
- 使用分页减少数据传输量

## 安全考虑

### 1. 数据库连接安全
- 使用加密存储数据库密码
- 实现连接超时和重试机制
- 限制数据库操作权限

### 2. 输入验证
- 严格验证所有用户输入
- 防止SQL注入攻击
- 实现参数值范围检查

### 3. 访问控制
- 实现用户认证和授权
- 记录敏感操作日志
- 实现操作审计功能