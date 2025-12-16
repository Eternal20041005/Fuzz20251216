-- Insert test data into db_parameter
INSERT IGNORE INTO db_parameter (db_type, param_name, param_type, description, default_value, value_range, weight, is_test)
VALUES 
('MySQL', 'max_connections', 'INTEGER', 'Max connections', '151', '100-5000', 0.8, 1),
('MySQL', 'innodb_buffer_pool_size', 'STRING', 'InnoDB buffer pool size', '128M', '64M-16G', 0.9, 1),
('MySQL', 'query_cache_size', 'STRING', 'Query cache size', '1M', '0-1G', 0.7, 0);

-- Insert test data into test_status
INSERT IGNORE INTO test_status (task_status, test_oracle, run_time, coverage_rate, bug_count, execution_count, current_param_combo)
VALUES 
('COMPLETED', 'TLP', 3600, 85.5, 12, 10000, '{"max_connections": 200, "innodb_buffer_pool_size": "256M"}');
