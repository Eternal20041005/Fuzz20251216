-- 数据库初始化脚本
-- 请在MySQL中执行此脚本

-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS fuzz_testing_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE fuzz_testing_db;

-- 创建数据库配置表
CREATE TABLE IF NOT EXISTS database_config (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    db_type VARCHAR(50) NOT NULL DEFAULT 'MySQL',
    connection_url VARCHAR(200) NOT NULL,
    username VARCHAR(100) NOT NULL,
    password VARCHAR(100) NOT NULL,
    status VARCHAR(50) DEFAULT 'UNTESTED',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY unique_name (name)
);

-- 创建参数模板表
CREATE TABLE IF NOT EXISTS parameter_template (
    id INT AUTO_INCREMENT PRIMARY KEY,
    param_name VARCHAR(100) NOT NULL,
    description VARCHAR(200),
    category VARCHAR(50) NOT NULL,
    default_value VARCHAR(100),
    param_type VARCHAR(20) NOT NULL DEFAULT 'STRING',
    is_test_default TINYINT(1) DEFAULT 1,
    min_value VARCHAR(50),
    max_value VARCHAR(50),
    allowed_values VARCHAR(1000),
    value_range VARCHAR(200),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY unique_param (param_name)
);

-- 创建Bug报告表
CREATE TABLE IF NOT EXISTS bug_report (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    bug_type VARCHAR(20) NOT NULL COMMENT 'Bug类型：崩溃、逻辑',
    target_database VARCHAR(50) NOT NULL COMMENT '目标数据库：MySQL、PostgreSQL等',
    oracle_type VARCHAR(10) NOT NULL COMMENT 'Oracle类型：TLP、RBR、CBR',
    test_time DATETIME NOT NULL COMMENT '测试时间',
    test_case TEXT COMMENT '测试样例SQL',
    parameter_settings_json TEXT COMMENT '参数设置JSON字符串',
    error_message VARCHAR(500) COMMENT '错误信息',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_bug_type (bug_type),
    INDEX idx_target_database (target_database),
    INDEX idx_oracle_type (oracle_type),
    INDEX idx_test_time (test_time),
    INDEX idx_created_at (created_at)
);

-- 创建模糊测试配置表
CREATE TABLE IF NOT EXISTS fuzz_config (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    config_name VARCHAR(100) NOT NULL COMMENT '配置名称',
    description VARCHAR(200) COMMENT '配置描述',
    test_oracle VARCHAR(50) NOT NULL COMMENT '测试Oracle类型',
    max_expr_depth INT NOT NULL COMMENT '最大表达式深度',
    max_retries INT NOT NULL COMMENT '尝试次数',
    timeout_seconds INT NOT NULL COMMENT '超时时间（秒）',
    random_seed BIGINT NOT NULL COMMENT '随机种子',
    max_queries INT NOT NULL COMMENT '查询数量',
    max_insert_rows INT NOT NULL COMMENT '最大插入数量',
    max_data_ordinal INT NOT NULL COMMENT '最大生成数据库数',
    db_username VARCHAR(100) COMMENT '数据库用户名',
    db_password VARCHAR(500) COMMENT '数据库密码',
    db_host VARCHAR(100) COMMENT '数据库主机',
    db_port INT COMMENT '数据库端口',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_config_name (config_name),
    INDEX idx_updated_at (updated_at)
);

-- 创建数据库参数表
CREATE TABLE IF NOT EXISTS db_parameter (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    db_type VARCHAR(20) NOT NULL COMMENT '数据库类型',
    param_name VARCHAR(100) NOT NULL COMMENT '参数名称',
    param_type VARCHAR(20) NOT NULL COMMENT '参数类型',
    description TEXT COMMENT '参数描述',
    default_value VARCHAR(255) COMMENT '参数默认值',
    value_range VARCHAR(500) COMMENT '参数取值范围/选项',
    weight DECIMAL(10,4) NOT NULL COMMENT '参数权重',
    coverage DECIMAL(5,2) DEFAULT 0.00 COMMENT '代码覆盖率',
    is_test BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否测试',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_db_param (db_type, param_name)
);

-- 创建测试状态表
CREATE TABLE IF NOT EXISTS test_status (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    task_status VARCHAR(20) NOT NULL COMMENT '任务状态',
    test_oracle VARCHAR(50) NOT NULL COMMENT '测试Oracle',
    run_time INT NOT NULL COMMENT '测试时间',
    coverage_rate DECIMAL(5,2) NOT NULL COMMENT '覆盖率',
    bug_count INT NOT NULL COMMENT 'Bug数量',
    execution_count INT NOT NULL COMMENT '执行次数',
    current_param_combo VARCHAR(500) COMMENT '当前参数组合',
    throughput DECIMAL(8,2) NOT NULL COMMENT '吞吐量',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
-- 创建参数组合权重表
CREATE TABLE IF NOT EXISTS `param_combo_weight` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `param_name` varchar(500) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '参数组合名',
  `weight_value` decimal(10,4) NOT NULL COMMENT '权重值',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
-- 创建测试用例表
CREATE TABLE IF NOT EXISTS test_case (
    test_time VARCHAR(20) NOT NULL COMMENT '测试时间',
    case_id VARCHAR(36) PRIMARY KEY COMMENT '测试用例id',
    target_db VARCHAR(20) NOT NULL COMMENT '目标数据库及版本',
    oracle_type VARCHAR(50) NOT NULL COMMENT 'Oracle类型',
    triggered_bug TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否触发漏洞',
    bug_id VARCHAR(50) COMMENT '关联bugid（如果触发漏洞）',
    param_combo TEXT COMMENT '参数组合',
    weight_value DECIMAL(10,4) NOT NULL COMMENT '权重值',
    sql_statement TEXT NOT NULL COMMENT 'SQL语句'
);

-- 插入测试数据库配置
INSERT IGNORE INTO database_config (name, db_type, connection_url, username, password, status) 
VALUES 
('Local MySQL Test', 'MySQL', 'jdbc:mysql://localhost:3306/fuzz_testing_db', 'root', '123456', 'UNTESTED'),
('Sample PostgreSQL', 'PostgreSQL', 'jdbc:postgresql://localhost:5432/testdb', 'postgres', '123456', 'UNTESTED');

-- 插入真实的MySQL参数数据到 db_parameter 表
INSERT IGNORE INTO db_parameter (db_type, param_name, param_type, description, default_value, value_range, weight, is_test) VALUES
-- InnoDB相关参数
('mysql', 'bulk_insert_buffer_size', 'Integer', 'MyISAM表批量插入缓冲区大小', '8388608', '0-20971520', 5.0, true),
('mysql', 'innodb_adaptive_hash_index', 'Boolean', 'InnoDB自适应哈希索引', 'ON', 'OFF,ON', 5.0, true),
('mysql', 'innodb_change_buffering', 'Enumeration', 'InnoDB变更缓冲', 'all', 'none,all', 5.0, true),
('mysql', 'innodb_cmp_per_index_enabled', 'Boolean', 'InnoDB每索引压缩统计', 'OFF', 'ON,OFF', 5.0, true),
('mysql', 'innodb_ddl_threads', 'Integer', 'InnoDB DDL线程数', '4', '1-4', 5.0, true),
('mysql', 'innodb_file_per_table', 'Boolean', 'InnoDB每表一个文件', 'ON', 'OFF,ON', 5.0, true),
('mysql', 'innodb_flush_neighbors', 'Enumeration', 'InnoDB刷新邻居页面', '1', '1,2', 5.0, true),
('mysql', 'innodb_max_dirty_pages_pct', 'Numeric', 'InnoDB最大脏页百分比', '90', '0-99.99', 5.0, true),
('mysql', 'innodb_max_dirty_pages_pct_lwm', 'Numeric', 'InnoDB脏页低水位标记', '10', '0-99.99', 5.0, true),
('mysql', 'innodb_random_read_ahead', 'Boolean', 'InnoDB随机预读', 'OFF', 'ON,OFF', 5.0, true),
('mysql', 'innodb_read_ahead_threshold', 'Integer', 'InnoDB预读阈值', '56', '0-64', 5.0, true),
('mysql', 'innodb_stats_auto_recalc', 'Boolean', 'InnoDB统计自动重算', 'ON', 'OFF,ON', 5.0, true),
('mysql', 'innodb_stats_method', 'Enumeration', 'InnoDB统计方法', 'nulls_equal', 'nulls_unequal,nulls_ignored,nulls_equal', 5.0, true),
('mysql', 'innodb_stats_persistent_sample_pages', 'Integer', 'InnoDB持久化统计采样页数', '20', '1-1000', 5.0, true),
('mysql', 'innodb_use_fdatasync', 'Boolean', 'InnoDB使用fdatasync', 'OFF', 'ON,OFF', 5.0, true),

-- 连接和并发参数
('mysql', 'concurrent_insert', 'Enumeration', '并发插入模式', '1', '0,1,2', 5.0, true),
('mysql', 'foreign_key_checks', 'Boolean', '外键检查', '1', '0,1', 5.0, true),

-- 存储引擎参数
('mysql', 'default_storage_engine', 'Enumeration', '默认存储引擎', 'InnoDB', 'MyISAM,InnoDB', 5.0, true),
('mysql', 'delay_key_write', 'Enumeration', '延迟键写入', 'ON', 'OFF,ALL', 5.0, true),

-- 查询优化参数
('mysql', 'eq_range_index_dive_limit', 'Integer', '等值范围索引深入限制', '200', '0-4294967295', 5.0, true),
('mysql', 'group_concat_max_len', 'Integer', 'GROUP_CONCAT最大长度', '1024', '4-18446744073709551615', 5.0, true),
('mysql', 'optimizer_prune_level', 'Integer', '优化器修剪级别', '1', '0,1', 5.0, true),
('mysql', 'optimizer_search_depth', 'Integer', '优化器搜索深度', '62', '0-62', 5.0, true),
('mysql', 'optimizer_switch', 'Set', '优化器开关', 'index_merge=on,index_merge_union=on', '', 5.0, false),

-- 缓冲区参数
('mysql', 'join_buffer_size', 'Integer', '连接缓冲区大小', '262144', '128-4294967295', 5.0, true),
('mysql', 'key_buffer_size', 'Integer', '键缓冲区大小', '8388608', '4096-4294967295', 5.0, true),
('mysql', 'read_buffer_size', 'Integer', '读缓冲区大小', '131072', '8192-2147483647', 5.0, true),
('mysql', 'read_rnd_buffer_size', 'Integer', '随机读缓冲区大小', '262144', '1-2147483647', 5.0, true),
('mysql', 'sort_buffer_size', 'Integer', '排序缓冲区大小', '262144', '32768-4294967295', 5.0, true),
('mysql', 'preload_buffer_size', 'Integer', '预加载缓冲区大小', '32768', '1024-1073741824', 5.0, true),
('mysql', 'query_alloc_block_size', 'Integer', '查询分配块大小', '8192', '1024-4294967295', 5.0, true),

-- MyISAM参数
('mysql', 'myisam_data_pointer_size', 'Integer', 'MyISAM数据指针大小', '6', '2-7', 5.0, true),
('mysql', 'myisam_max_sort_file_size', 'Integer', 'MyISAM最大排序文件大小', '9223372036853727232', '0-9223372036853727232', 5.0, true),
('mysql', 'myisam_sort_buffer_size', 'Integer', 'MyISAM排序缓冲区大小', '8388608', '4096-4294967295', 5.0, true),
('mysql', 'myisam_stats_method', 'Enumeration', 'MyISAM统计方法', 'nulls_unequal', 'nulls_equal,nulls_ignored,nulls_unequal', 5.0, true),
('mysql', 'myisam_use_mmap', 'Boolean', 'MyISAM使用内存映射', 'OFF', 'ON,OFF', 5.0, true),

-- 系统参数
('mysql', 'low_priority_updates', 'Boolean', '低优先级更新', 'OFF', 'ON,OFF', 5.0, true),
('mysql', 'max_seeks_for_key', 'Integer', '键的最大查找次数', '4294967295', '1-4294967295', 5.0, true),
('mysql', 'max_sort_length', 'Integer', '最大排序长度', '1024', '4-8388608', 5.0, true),
('mysql', 'max_sp_recursion_depth', 'Integer', '存储过程最大递归深度', '0', '0-255', 5.0, true),
('mysql', 'range_optimizer_max_mem_size', 'Integer', '范围优化器最大内存大小', '8388608', '0-18446744073709551615', 5.0, true),
('mysql', 'read_only', 'Boolean', '只读模式', 'OFF', 'ON,OFF', 5.0, true),
('mysql', 'regexp_stack_limit', 'Integer', '正则表达式栈限制', '8000000', '0-2147483647', 5.0, true),
('mysql', 'rewriter_enabled', 'Boolean', '查询重写器启用', 'OFF', 'ON,OFF', 5.0, false),
('mysql', 'table_open_cache', 'Integer', '表打开缓存', '4000', '1-524288', 5.0, true),

-- SQL模式参数
('mysql', 'sql_auto_is_null', 'Boolean', 'SQL自动IS NULL', 'OFF', 'ON,OFF', 5.0, true),
('mysql', 'sql_big_selects', 'Boolean', 'SQL大查询', 'ON', 'ON,OFF', 5.0, true),
('mysql', 'sql_buffer_result', 'Boolean', 'SQL缓冲结果', 'OFF', 'ON,OFF', 5.0, true),
('mysql', 'sql_generate_invisible_primary_key', 'Boolean', 'SQL生成不可见主键', 'OFF', 'ON,OFF', 5.0, true),
('mysql', 'sql_mode', 'Set', 'SQL模式', 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION', 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION', 5.0, false);

-- 验证数据
SELECT 'Database Configs:' as info;
SELECT id, name, db_type, status FROM database_config;

SELECT 'Parameter Templates:' as info;
SELECT id, param_name, category, param_type, default_value, is_test_default FROM parameter_template LIMIT 10;

-- ========================================
-- 数据验证和完整性检查
-- ========================================

-- 验证数据库配置
SELECT '=== 数据库配置验证 ===' as section;
SELECT COUNT(*) as total_db_configs FROM database_config;
SELECT name, db_type, status FROM database_config;

-- 验证参数模板数据
SELECT '=== 参数模板验证 ===' as section;
SELECT COUNT(*) as total_parameters FROM parameter_template;

-- 验证参数类型分布
SELECT '=== 参数类型分布 ===' as section;
SELECT param_type, COUNT(*) as count FROM parameter_template GROUP BY param_type ORDER BY param_type;

-- 验证参数类别分布
SELECT '=== 参数类别分布 ===' as section;
SELECT category, COUNT(*) as count FROM parameter_template GROUP BY category ORDER BY category;

-- 验证测试用例表
SELECT '=== 测试用例表验证 ===' as section;
SELECT COUNT(*) as total_test_cases FROM test_case;

-- 验证设置范围分布 (暂时跳过，字段尚未添加)
SELECT '=== 设置范围分布 ===' as section;
SELECT 'value_range字段尚未添加，请先执行数据迁移' as message;

-- 验证约束条件统计
SELECT '=== 约束条件统计 ===' as section;
SELECT 
    COUNT(*) as total_parameters,
    SUM(CASE WHEN allowed_values IS NOT NULL AND allowed_values != '' THEN 1 ELSE 0 END) as with_candidate_values,
    SUM(CASE WHEN min_value IS NOT NULL OR max_value IS NOT NULL THEN 1 ELSE 0 END) as with_range_constraints,
    0 as with_value_range_placeholder,
    SUM(CASE WHEN is_test_default = 1 THEN 1 ELSE 0 END) as test_enabled
FROM parameter_template;

-- 验证必填字段完整性
SELECT '=== 数据完整性检查 ===' as section;
SELECT 'Parameters with missing param_name:' as check_type, COUNT(*) as count 
FROM parameter_template WHERE param_name IS NULL OR param_name = '';

SELECT 'Parameters with missing param_type:' as check_type, COUNT(*) as count 
FROM parameter_template WHERE param_type IS NULL;

SELECT 'Parameters with missing category:' as check_type, COUNT(*) as count 
FROM parameter_template WHERE category IS NULL OR category = '';

-- 验证JSON格式的候选取值
SELECT '=== JSON格式验证 ===' as section;
SELECT param_name, allowed_values 
FROM parameter_template 
WHERE allowed_values IS NOT NULL 
  AND allowed_values != ''
  AND allowed_values NOT REGEXP '^\\[.*\\]$'
LIMIT 5;

-- 显示一些示例参数
SELECT '=== 参数示例 ===' as section;
SELECT param_name, param_type, category, default_value,
       CASE WHEN allowed_values IS NOT NULL THEN 'Yes' ELSE 'No' END as has_candidates,
       CASE WHEN min_value IS NOT NULL OR max_value IS NOT NULL THEN 'Yes' ELSE 'No' END as has_range
FROM parameter_template 
WHERE param_name IN ('innodb_adaptive_hash_index', 'key_buffer_size', 'sql_mode', 'concurrent_insert')
ORDER BY param_name;

-- 最终验证消息
SELECT '=== 初始化完成 ===' as section;
SELECT 
    CASE 
        WHEN COUNT(*) >= 40 THEN 'SUCCESS: 数据库初始化成功完成'
        ELSE 'WARNING: 参数数量可能不足，请检查数据'
    END as initialization_status,
    COUNT(*) as total_parameters
FROM parameter_template;

-- 插入示例Bug报告数据
INSERT IGNORE INTO bug_report (bug_type, target_database, oracle_type, test_time, test_case, parameter_settings_json, error_message) VALUES
('崩溃', 'MySQL', 'TLP',
 '2024-12-07 10:30:00',
 'SELECT * FROM users WHERE id = 1 UNION SELECT password FROM admin;',
 '{"innodb_buffer_pool_size":"134217728","max_connections":"151","query_cache_size":"0","sort_buffer_size":"262144"}',
 'MySQL server has gone away'),

('逻辑', 'MySQL', 'RBR',
 '2024-12-07 11:15:00',
 'INSERT INTO test_table VALUES (NULL, "test", -999999999999999999999);',
 '{"sql_mode":"STRICT_TRANS_TABLES","innodb_file_per_table":"ON","foreign_key_checks":"0"}',
 'Data truncation: Out of range value for column'),

('崩溃', 'PostgreSQL', 'TLP',
 '2024-12-07 12:00:00',
 'CREATE TABLE test (id SERIAL PRIMARY KEY, data JSON); INSERT INTO test (data) VALUES (\'{"invalid": json}\');',
 '{"work_mem":"4MB","maintenance_work_mem":"64MB","shared_buffers":"128MB"}',
 'invalid input syntax for type json'),

('逻辑', 'MySQL', 'CBR',
 '2024-12-07 13:45:00',
 'SELECT COUNT(*) FROM large_table WHERE date_col = "invalid_date_format";',
 '{"optimizer_switch":"index_merge=on","eq_range_index_dive_limit":"200","max_sort_length":"1024"}',
 'Incorrect datetime value'),

('崩溃', 'SQL Server', 'TLP',
 '2024-12-07 14:30:00',
 'EXEC sp_executesql N\'SELECT * FROM sys.objects WHERE name LIKE @name\', N\'@name nvarchar(50)\', @name = \'%\'\'\'; DROP TABLE users;--\';',
 '{"max_server_memory":"2147483647","query_wait":"-1","lock_timeout":"-1"}',
 'Unclosed quotation mark after the character string');

-- 插入默认模糊测试配置
INSERT IGNORE INTO fuzz_config (
    config_name, description, test_oracle, max_expr_depth, max_retries, timeout_seconds,
    random_seed, max_queries, max_insert_rows, max_data_ordinal, db_username, db_password, db_host, db_port
) VALUES (
    'default', '系统默认模糊测试配置',
    'TLP', 3, 100, 30, -1, 10000, 30, 0, 'sqlancer', 'sqlancer', '', -1
);

-- 修复现有数据的参数类型（确保使用标准MySQL类型名称）
UPDATE db_parameter SET param_type = 'Integer' WHERE param_type = 'INTEGER';
UPDATE db_parameter SET param_type = 'Boolean' WHERE param_type = 'BOOLEAN';
UPDATE db_parameter SET param_type = 'Numeric' WHERE param_type IN ('DECIMAL', 'DOUBLE', 'FLOAT');
UPDATE db_parameter SET param_type = 'String' WHERE param_type IN ('STRING', 'VARCHAR', 'TEXT');
UPDATE db_parameter SET param_type = 'Enumeration' WHERE param_type = 'ENUM';
UPDATE db_parameter SET param_type = 'Set' WHERE param_type = 'SET';


-- 显示初始化时间
SELECT 'Database initialized at:' as info, NOW() as timestamp;

-- 验证Bug报告数据
SELECT '=== Bug报告验证 ===' as section;
SELECT COUNT(*) as total_bug_reports FROM bug_report;
SELECT bug_type, target_database, oracle_type, DATE(test_time) as test_date
FROM bug_report ORDER BY created_at DESC;