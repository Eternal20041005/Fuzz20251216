package com.fuzz.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * 数据库配置实体类
 */
@Entity
@Table(name = "database_config",
       uniqueConstraints = @UniqueConstraint(columnNames = "name"))
public class DatabaseConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "数据库名称不能为空")
    @Size(max = 100, message = "数据库名称长度不能超过100个字符")
    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;

    @NotBlank(message = "数据库类型不能为空")
    @Size(max = 50, message = "数据库类型长度不能超过50个字符")
    @Column(name = "db_type", nullable = false, length = 50)
    private String dbType = "MySQL";

    @NotBlank(message = "连接URL不能为空")
    @Size(max = 200, message = "连接URL长度不能超过200个字符")
    @Column(name = "connection_url", nullable = false, length = 200)
    private String connectionUrl;

    @NotBlank(message = "用户名不能为空")
    @Size(max = 100, message = "用户名长度不能超过100个字符")
    @Column(name = "username", nullable = false, length = 100)
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(max = 100, message = "密码长度不能超过100个字符")
    @Column(name = "password", nullable = false, length = 100)
    private String password;

    @Size(max = 50, message = "状态长度不能超过50个字符")
    @Column(name = "status", length = 50)
    private String status = "未测试";

    @Column(name = "create_time", nullable = false, updatable = false)
    private LocalDateTime createTime;

    @Column(name = "update_time")
    private LocalDateTime updateTime;

    // 构造函数
    public DatabaseConfig() {
    }

    public DatabaseConfig(String name, String dbType, String connectionUrl, 
                         String username, String password) {
        this.name = name;
        this.dbType = dbType;
        this.connectionUrl = connectionUrl;
        this.username = username;
        this.password = password;
    }

    // JPA生命周期回调
    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updateTime = LocalDateTime.now();
    }

    // Getter和Setter方法
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDbType() {
        return dbType;
    }

    public void setDbType(String dbType) {
        this.dbType = dbType;
    }

    public String getConnectionUrl() {
        return connectionUrl;
    }

    public void setConnectionUrl(String connectionUrl) {
        this.connectionUrl = connectionUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "DatabaseConfig{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", dbType='" + dbType + '\'' +
                ", connectionUrl='" + connectionUrl + '\'' +
                ", username='" + username + '\'' +
                ", status='" + status + '\'' +
                ", createTime=" + createTime +
                '}';
    }
}