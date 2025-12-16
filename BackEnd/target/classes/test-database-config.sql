-- 测试用数据库配置和参数数据
-- 可以直接在MySQL中执行

USE fuzz_testing_db;

-- 插入测试数据库配置
INSERT INTO database_config (name, db_type, connection_url, username, password, status) 
VALUES 
('本地MySQL测试', 'MySQL', 'jdbc:mysql://localhost:3306/fuzz_testing_db', 'root', 'password', '未测试'),
('示例PostgreSQL', 'PostgreSQL', 'jdbc:postgresql://localhost:5432/testdb', 'postgres', 'password', '未测试')
ON DUPLICATE KEY UPDATE 
name = VALUES(name),
connection_url = VALUES(connection_url);

-- 插入测试参数模板
INSERT INTO parameter_template (param_name, description, category, default_value, param_type, is_test_default) 
VALUES 
('max_connections', '最大并发连接数', 'CONNECTION', '151', 'INTEGER', true),
('wait_timeout', '非交互连接超时时间（秒）', 'CONNECTION', '28800', 'INTEGER', true),
('innodb_buffer_pool_size', 'InnoDB缓冲池大小', 'MEMORY', '134217728', 'INTEGER', true),
('max_allowed_packet', '最大数据包大小', 'NETWORK', '4194304', 'INTEGER', true),
('tmp_table_size', '临时表最大大小', 'MEMORY', '16777216', 'INTEGER', false),
('key_buffer_size', 'MyISAM索引缓冲区大小', 'MEMORY', '8388608', 'INTEGER', false),
('query_cache_size', '查询缓存大小', 'CACHE', '0', 'INTEGER', true),
('query_cache_type', '查询缓存类型', 'CACHE', 'OFF', 'BOOLEAN', true),
('thread_cache_size', '线程缓存大小', 'CONNECTION', '8', 'INTEGER', false),
('sort_buffer_size', '排序缓冲区大小', 'MEMORY', '262144', 'INTEGER', true),
('read_buffer_size', '读缓冲区大小', 'MEMORY', '131072', 'INTEGER', false),
('autocommit', '自动提交', 'TRANSACTION', 'ON', 'BOOLEAN', true),
('sql_mode', 'SQL模式', 'SYSTEM', 'STRICT_TRANS_TABLES,NO_ZERO_DATE', 'STRING', false),
('character_set_server', '服务器字符集', 'SYSTEM', 'utf8mb4', 'STRING', false),
('collation_server', '服务器排序规则', 'SYSTEM', 'utf8mb4_unicode_ci', 'STRING', false)
ON DUPLICATE KEY UPDATE 
description = VALUES(description),
category = VALUES(category),
default_value = VALUES(default_value),
param_type = VALUES(param_type);

-- 查看插入的数据
SELECT 'Database Configs:' as info;
SELECT id, name, db_type, status FROM database_config;

SELECT 'Parameter Templates:' as info;
SELECT id, param_name, category, param_type, default_value, is_test_default FROM parameter_template LIMIT 10;