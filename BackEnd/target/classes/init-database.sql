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

-- 插入真实的MySQL参数数据（基于param.txt）
INSERT IGNORE INTO parameter_template (param_name, description, category, default_value, param_type, is_test_default, min_value, max_value, allowed_values, value_range) VALUES
-- InnoDB相关参数
('bulk_insert_buffer_size', 'MyISAM表批量插入缓冲区大小', 'MEMORY', '8388608', 'INTEGER', true, '0', '20971520', '["0", "20971520"]', 'Both'),
('innodb_adaptive_hash_index', 'InnoDB自适应哈希索引', 'INNODB', 'ON', 'BOOLEAN', true, null, null, '["OFF", "ON"]', 'Global'),
('innodb_change_buffering', 'InnoDB变更缓冲', 'INNODB', 'all', 'STRING', true, null, null, '["none", "all"]', 'Global'),
('innodb_cmp_per_index_enabled', 'InnoDB每索引压缩统计', 'INNODB', 'OFF', 'BOOLEAN', true, null, null, '["ON", "OFF"]', 'Global'),
('innodb_ddl_threads', 'InnoDB DDL线程数', 'INNODB', '4', 'INTEGER', true, '1', '4', '["1", "2", "3", "4"]', 'Session'),
('innodb_file_per_table', 'InnoDB每表一个文件', 'INNODB', 'ON', 'BOOLEAN', true, null, null, '["OFF", "ON"]', 'Global'),
('innodb_flush_neighbors', 'InnoDB刷新邻居页面', 'INNODB', '1', 'STRING', true, null, null, '["1", "2"]', 'Global'),
('innodb_max_dirty_pages_pct', 'InnoDB最大脏页百分比', 'INNODB', '90', 'DECIMAL', true, '0', '99.99', '["0", "10", "50", "90"]', 'Global'),
('innodb_max_dirty_pages_pct_lwm', 'InnoDB脏页低水位标记', 'INNODB', '10', 'DECIMAL', true, '0', '99.99', '["0", "5", "10"]', 'Global'),
('innodb_random_read_ahead', 'InnoDB随机预读', 'INNODB', 'OFF', 'BOOLEAN', true, null, null, '["ON", "OFF"]', 'Global'),
('innodb_read_ahead_threshold', 'InnoDB预读阈值', 'INNODB', '56', 'INTEGER', true, '0', '64', '["0", "8", "16", "32", "56"]', 'Global'),
('innodb_stats_auto_recalc', 'InnoDB统计自动重算', 'INNODB', 'ON', 'BOOLEAN', true, null, null, '["OFF", "ON"]', 'Global'),
('innodb_stats_method', 'InnoDB统计方法', 'INNODB', 'nulls_equal', 'STRING', true, null, null, '["nulls_unequal", "nulls_ignored", "nulls_equal"]', 'Global'),
('innodb_stats_persistent_sample_pages', 'InnoDB持久化统计采样页数', 'INNODB', '20', 'INTEGER', true, '1', '1000', '["1", "10", "20"]', 'Global'),
('innodb_use_fdatasync', 'InnoDB使用fdatasync', 'INNODB', 'OFF', 'BOOLEAN', true, null, null, '["ON", "OFF"]', 'Global'),

-- 连接和并发参数
('concurrent_insert', '并发插入模式', 'CONNECTION', '1', 'STRING', true, null, null, '["0", "1", "2"]', 'Global'),
('foreign_key_checks', '外键检查', 'CONNECTION', '1', 'BOOLEAN', true, null, null, '["0", "1"]', 'Both'),

-- 存储引擎参数
('default_storage_engine', '默认存储引擎', 'ENGINE', 'InnoDB', 'STRING', true, null, null, '["MyISAM", "InnoDB"]', 'Both'),
('delay_key_write', '延迟键写入', 'ENGINE', 'ON', 'STRING', true, null, null, '["OFF", "ALL"]', 'Global'),

-- 查询优化参数
('eq_range_index_dive_limit', '等值范围索引深入限制', 'OPTIMIZER', '200', 'INTEGER', true, '0', '4294967295', '["0", "2", "10", "200"]', 'Both'),
('group_concat_max_len', 'GROUP_CONCAT最大长度', 'OPTIMIZER', '1024', 'INTEGER', true, '4', '18446744073709551615', '["4", "64", "1024"]', 'Global'),
('optimizer_prune_level', '优化器修剪级别', 'OPTIMIZER', '1', 'INTEGER', true, null, null, '["0", "1"]', 'Both'),
('optimizer_search_depth', '优化器搜索深度', 'OPTIMIZER', '62', 'INTEGER', true, '0', '62', '["0", "4", "32", "62"]', 'Both'),
('optimizer_switch', '优化器开关', 'OPTIMIZER', 'index_merge=on,index_merge_union=on', 'STRING', false, null, null, null, 'Both'),

-- 缓冲区参数
('join_buffer_size', '连接缓冲区大小', 'MEMORY', '262144', 'INTEGER', true, '128', '4294967295', '["128", "262144", "1048576"]', 'Both'),
('key_buffer_size', '键缓冲区大小', 'MEMORY', '8388608', 'INTEGER', true, '4096', '4294967295', '["4096", "8388608", "16777216"]', 'Global'),
('read_buffer_size', '读缓冲区大小', 'MEMORY', '131072', 'INTEGER', true, '8192', '2147483647', '["8192", "131072", "262144"]', 'Both'),
('read_rnd_buffer_size', '随机读缓冲区大小', 'MEMORY', '262144', 'INTEGER', true, '1', '2147483647', '["1", "1024", "262144"]', 'Both'),
('sort_buffer_size', '排序缓冲区大小', 'MEMORY', '262144', 'INTEGER', true, '32768', '4294967295', '["32768", "262144", "1048576"]', 'Both'),
('preload_buffer_size', '预加载缓冲区大小', 'MEMORY', '32768', 'INTEGER', true, '1024', '1073741824', '["1024", "32768", "65536"]', 'Both'),
('query_alloc_block_size', '查询分配块大小', 'MEMORY', '8192', 'INTEGER', true, '1024', '4294967295', '["1024", "8192", "16384"]', 'Both'),

-- MyISAM参数
('myisam_data_pointer_size', 'MyISAM数据指针大小', 'MYISAM', '6', 'INTEGER', true, '2', '7', '["2", "3", "4", "5", "6", "7"]', 'Global'),
('myisam_max_sort_file_size', 'MyISAM最大排序文件大小', 'MYISAM', '9223372036853727232', 'INTEGER', true, '0', '9223372036853727232', '["0", "128", "1024", "9223372036853727232"]', 'Global'),
('myisam_sort_buffer_size', 'MyISAM排序缓冲区大小', 'MYISAM', '8388608', 'INTEGER', true, '4096', '4294967295', '["4096", "8388608", "16777216"]', 'Both'),
('myisam_stats_method', 'MyISAM统计方法', 'MYISAM', 'nulls_unequal', 'STRING', true, null, null, '["nulls_equal", "nulls_ignored", "nulls_unequal"]', 'Both'),
('myisam_use_mmap', 'MyISAM使用内存映射', 'MYISAM', 'OFF', 'BOOLEAN', true, null, null, '["ON", "OFF"]', 'Global'),

-- 系统参数
('low_priority_updates', '低优先级更新', 'SYSTEM', 'OFF', 'BOOLEAN', true, null, null, '["ON", "OFF"]', 'Both'),
('max_seeks_for_key', '键的最大查找次数', 'SYSTEM', '4294967295', 'INTEGER', true, '1', '4294967295', '["1", "4", "100", "4294967295"]', 'Both'),
('max_sort_length', '最大排序长度', 'SYSTEM', '1024', 'INTEGER', true, '4', '8388608', '["4", "1024", "8192"]', 'Both'),
('max_sp_recursion_depth', '存储过程最大递归深度', 'SYSTEM', '0', 'INTEGER', true, '0', '255', '["0", "5", "10", "255"]', 'Both'),
('range_optimizer_max_mem_size', '范围优化器最大内存大小', 'SYSTEM', '8388608', 'INTEGER', true, '0', '18446744073709551615', '["0", "1048576", "8388608"]', 'Both'),
('read_only', '只读模式', 'SYSTEM', 'OFF', 'BOOLEAN', true, null, null, '["ON", "OFF"]', 'Global'),
('regexp_stack_limit', '正则表达式栈限制', 'SYSTEM', '8000000', 'INTEGER', true, '0', '2147483647', '["0", "1024", "8000000"]', 'Global'),
('rewriter_enabled', '查询重写器启用', 'SYSTEM', 'OFF', 'BOOLEAN', true, null, null, '["ON", "OFF"]', 'Global'),
('table_open_cache', '表打开缓存', 'SYSTEM', '4000', 'INTEGER', true, '1', '524288', '["1", "8", "100", "4000"]', 'Global'),

-- SQL模式参数
('sql_auto_is_null', 'SQL自动IS NULL', 'SQL_MODE', 'OFF', 'BOOLEAN', true, null, null, '["ON", "OFF"]', 'Both'),
('sql_big_selects', 'SQL大查询', 'SQL_MODE', 'ON', 'BOOLEAN', true, null, null, '["ON", "OFF"]', 'Both'),
('sql_buffer_result', 'SQL缓冲结果', 'SQL_MODE', 'OFF', 'BOOLEAN', true, null, null, '["ON", "OFF"]', 'Both'),
('sql_generate_invisible_primary_key', 'SQL生成不可见主键', 'SQL_MODE', 'OFF', 'BOOLEAN', true, null, null, '["ON", "OFF"]', 'Both'),
('sql_mode', 'SQL模式', 'SQL_MODE', 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION', 'STRING', true, null, null, '["", "ONLY_FULL_GROUP_BY", "STRICT_TRANS_TABLES", "NO_ZERO_IN_DATE", "NO_ZERO_DATE", "ERROR_FOR_DIVISION_BY_ZERO", "NO_ENGINE_SUBSTITUTION"]', 'Both');

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

-- 显示初始化时间
SELECT 'Database initialized at:' as info, NOW() as timestamp;

-- 验证Bug报告数据
SELECT '=== Bug报告验证 ===' as section;
SELECT COUNT(*) as total_bug_reports FROM bug_report;
SELECT bug_type, target_database, oracle_type, DATE(test_time) as test_date
FROM bug_report ORDER BY created_at DESC;