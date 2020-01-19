package com.sorm.core;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import com.sorm.bean.Configuration;
import com.sorm.pool.DBConnPool;

/**
 * 根据配置信息，维持连接对象的管理（增加连接池功能）
 * 
 * @author Panlf
 *
 */
public class DBManager {
	/**
	 * 配置信息
	 */
	private static Configuration conf;
	
	/**
	 * 连接池对象
	 */
	private static DBConnPool pool;

	static {
		Properties pros = new Properties();

		try {
			pros.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("db.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		conf = new Configuration();
		conf.setDriver(pros.getProperty("driver"));
		conf.setPoPackage(pros.getProperty("poPackage"));
		conf.setPwd(pros.getProperty("pwd"));
		conf.setSrcPath(pros.getProperty("srcPath"));
		conf.setUrl(pros.getProperty("url"));
		conf.setUser(pros.getProperty("user"));
		conf.setUsingDB(pros.getProperty("usingDB"));
		conf.setQueryClass(pros.getProperty("queryClass"));
		conf.setPoolMaxSize(Integer.valueOf(pros.getProperty("poolMaxSize")));
		conf.setPoolMinSize(Integer.valueOf(pros.getProperty("poolMinSize")));

	}

	/**
	 * 获取Connection连接
	 * @return
	 */
	public static Connection getConn() {
		/*try {
			Class.forName(conf.getDriver());
			return DriverManager.getConnection(conf.getUrl(), conf.getUser(), conf.getPwd());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}*/
		if(pool == null){
			 pool = new DBConnPool();
		}
		return pool.getConnection();
	}
	
	/**
	 * 创建新的Connection对象
	 * @return
	 */
	public static Connection createConnection(){
		try {
			Class.forName(conf.getDriver());
			return DriverManager.getConnection(conf.getUrl(), conf.getUser(), conf.getPwd());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 关闭资源
	 * @param rs
	 * @param ps
	 * @param conn
	 */
	public static void close(ResultSet rs, Statement ps, Connection conn) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if (ps != null) {
			try {
				ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		pool.close(conn);
	}

	/**
	 * 关闭资源
	 * @param ps
	 * @param conn
	 */
	public static void close(Statement ps, Connection conn) {
		if (ps != null) {
			try {
				ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		pool.close(conn);
	}

	public static Configuration getConf() {
		return conf;
	}
}
