package com.secmask.util.tool;

import com.secmask.util.constant.DatabaseType;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 数据库监控工具类
 *
 * @Author: yhy
 * @Date: 2019/3/15
 */
public class DatebaseMonitorUtil {

    private Map<String, Connection> dbConnection = new HashMap<>(16);

    /**
     * 获取数据库线程当前连接数
     */
    public void getThreadUsed(Connection connection, String dbType) throws SQLException {



        String sql = "";

        switch (dbType) {
            case DatabaseType.MYSQL:
                sql = "show status like 'Threads_connected';";
                break;
            case DatabaseType.ORACLE:
                sql = "select count(*) from v$process;";
                break;
            case DatabaseType.SQLSERVER:
                sql = "SELECT COUNT(0) FROM (select * from sysprocesses where dbid in (select dbid from sysdatabases where name='MyDatabase')) temp";
                break;
            case DatabaseType.DB2:
                break;
            case DatabaseType.POSTGRESQL:
                sql = "select count(1) from pg_stat_activity;";
                break;
            default:
                // 类型有误
                break;
        }

        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);

        // 赋值

        // 关闭连接
        resultSet.close();
        statement.close();
        connection.close();

        return;
    }

    /**
     * 获取数据库线程最大连接数
     */
    public void getThreadTotal(Connection connection, String dbType) {
        String sql = "";

        switch (dbType) {
            case DatabaseType.MYSQL:
                sql = "show global status like 'Max_used_connections';";
                break;
            case DatabaseType.ORACLE:
                sql = "select value from v$parameter where name ='processes';";
                break;
            case DatabaseType.SQLSERVER:
                sql = "SELECT @@MAX_CONNECTIONS";
                break;
            case DatabaseType.DB2:
                break;
            case DatabaseType.POSTGRESQL:
                sql = "show max_connections;";
                break;
            default:
                // 类型有误
                break;
        }
    }

    /**
     * 获取数据库每秒query数
     */
    public void getQPS(Connection connection, String dbType) {
        String sql = "";

        switch (dbType) {
            case DatabaseType.MYSQL:
                sql = "show status like 'Questions';";
                break;
            case DatabaseType.ORACLE:
                sql = "";
                break;
            case DatabaseType.SQLSERVER:
                break;
            case DatabaseType.DB2:
                break;
            case DatabaseType.POSTGRESQL:
                break;
            default:
                // 类型有误
                break;
        }
    }

    /**
     * 获取数据库每秒事务数
     */
    public void getTPS(Connection connection, String dbType) {
        String sql = "";

        switch (dbType) {
            case DatabaseType.MYSQL:
                // 两值相加
                sql = "show status like 'Com_commit';";
                sql = "show status like 'Com_rollback';";

                break;
            case DatabaseType.ORACLE:
                sql = "";
                break;
            case DatabaseType.SQLSERVER:
                break;
            case DatabaseType.DB2:
                break;
            case DatabaseType.POSTGRESQL:
                break;
            default:
                // 类型有误
                break;
        }
    }

    /**
     * 获取数据库慢查询次数
     */
    public void getSlowQuery(Connection connection, String dbType) {

        String sql = "";

        switch (dbType) {
            case DatabaseType.MYSQL:
                sql = "show global status like '%slow_queries%';";
                break;
            case DatabaseType.ORACLE:
                sql = "SELECT COUNT(0) FROM (select * from (select sa.SQL_TEXT,sa.SQL_FULLTEXT,sa.EXECUTIONS '执行次数',round(sa.ELAPSED_TIME / 1000000, 2) '总执行时间',round(sa.ELAPSED_TIME / 1000000 / sa.EXECUTIONS, 2) '平均执行时间',sa.COMMAND_TYPE,sa.PARSING_USER_ID '用户ID',u.username '用户名',sa.HASH_VALUE " +
                        " from v$sqlarea sa left join all_users u on sa.PARSING_USER_ID = u.user_id where sa.EXECUTIONS > 0) where rownum <= 50) temp";
                break;
            case DatabaseType.SQLSERVER:
                break;
            case DatabaseType.DB2:
                break;
            case DatabaseType.POSTGRESQL:
                sql = "SELECT COUNT(*) FROM (SELECT * from pg_stat_user_tables where n_live_tup > 100000 and seq_scan > 0) temp";
                break;
            default:
                // 类型有误
                break;
        }
    }

    /**
     * 获取数据库临时表使用的相关状态量
     */
    public void getTempTableStatus(Connection connection, String dbType) {
        String sql = "";

        switch (dbType) {
            case DatabaseType.MYSQL:
                sql = "show global status like '%Created_tmp%';";
                break;
            case DatabaseType.ORACLE:
                sql = "";
                break;
            case DatabaseType.SQLSERVER:
                break;
            case DatabaseType.DB2:
                break;
            case DatabaseType.POSTGRESQL:
                break;
            default:
                // 类型有误
                break;
        }
    }

    /**
     * 获取数据库表空间使用量（总量）
     */
    public void getTableSpaceUsage(Connection connection, String dbType) {
        String sql = "";

        switch (dbType) {
            case DatabaseType.MYSQL:
                sql = "";
                break;
            case DatabaseType.ORACLE:
                sql = "SELECT a.tablespace_name, total, free, ( total - free ) AS usage  FROM " +
                        "( SELECT tablespace_name, sum( bytes ) / 1024 / 1024 AS total FROM dba_data_files GROUP BY tablespace_name ) a," +
                        "( SELECT tablespace_name, sum( bytes ) / 1024 / 1024 AS free FROM dba_free_space GROUP BY tablespace_name ) b " +
                        "WHERE a.tablespace_name = b.tablespace_name AND a.tablespace_name = 'SYSTEM';";
                break;
            case DatabaseType.SQLSERVER:
                break;
            case DatabaseType.DB2:
                break;
            case DatabaseType.POSTGRESQL:
                // 总量
                sql = "select pg_size_pretty(pg_tablespace_size('pg_default'));";
                break;
            default:
                // 类型有误
                break;
        }
    }

    /**
     * 获取数据库日志总量
     */
    public void getLogTotal(Connection connection, String dbType) {
        String sql = "";

        switch (dbType) {
            case DatabaseType.MYSQL:
                sql = "show global status like '%binlog%';";
                break;
            case DatabaseType.ORACLE:
                sql = "";
                break;
            case DatabaseType.SQLSERVER:
                break;
            case DatabaseType.DB2:
                break;
            case DatabaseType.POSTGRESQL:
                break;
            default:
                // 类型有误
                break;
        }
    }

    /**
     * 获取数据库事务锁
     */
    public void getTransactionLock(Connection connection, String dbType) {
        String sql = "";

        switch (dbType) {
            case DatabaseType.MYSQL:
                sql = "show global status like '%innodb_row_lock%';";
                break;
            case DatabaseType.ORACLE:
                sql = "";
                break;
            case DatabaseType.SQLSERVER:
                break;
            case DatabaseType.DB2:
                break;
            case DatabaseType.POSTGRESQL:
                break;
            default:
                // 类型有误
                break;
        }
    }

    private static Connection getConnection(String dbType) {
        String url = "";


        Connection connection = null;

        try {
            connection = DriverManager.getConnection(url);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return connection;
    }
}
