-- 为parameter_template表添加索引
CREATE INDEX IF NOT EXISTS idx_parameter_template_param_name ON parameter_template(param_name);
CREATE INDEX IF NOT EXISTS idx_parameter_template_category ON parameter_template(category);
CREATE INDEX IF NOT EXISTS idx_parameter_template_param_type ON parameter_template(param_type);
CREATE INDEX IF NOT EXISTS idx_parameter_template_is_test_default ON parameter_template(is_test_default);
CREATE INDEX IF NOT EXISTS idx_parameter_template_create_time ON parameter_template(create_time);

-- 为database_config表添加索引
CREATE INDEX IF NOT EXISTS idx_database_config_name ON database_config(name);
CREATE INDEX IF NOT EXISTS idx_database_config_db_type ON database_config(db_type);
CREATE INDEX IF NOT EXISTS idx_database_config_status ON database_config(status);
CREATE INDEX IF NOT EXISTS idx_database_config_create_time ON database_config(create_time);

-- 复合索引用于常见查询
CREATE INDEX IF NOT EXISTS idx_parameter_template_category_name ON parameter_template(category, param_name);
CREATE INDEX IF NOT EXISTS idx_parameter_template_type_test ON parameter_template(param_type, is_test_default);