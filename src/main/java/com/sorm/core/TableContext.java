package com.sorm.core;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.sorm.bean.ColumnInfo;
import com.sorm.bean.TableInfo;
import com.sorm.utils.JavaFileUtils;
import com.sorm.utils.StringUtils;

/**
 * 负责获取管理数据库所有表结构和类结构的关系，并可以根据表结构生成类结构
 * 
 * @author Panlf
 *
 */
public class TableContext {
	/**
	 * 表名为key，表信息对象为value
	 */
	public static Map<String, TableInfo> tables = new HashMap<>();

	/**
	 * 将po的class对象和表信息对象关联起来，便于重用
	 */
	public static Map<Class<?>, TableInfo> poClassTableMap = new HashMap<>();

	private TableContext() {
	}

	static {
		try {
			// 初始化h获得表的信息
			Connection conn = DBManager.getConn();

			DatabaseMetaData dbmd = conn.getMetaData();

			ResultSet tableSet = dbmd.getTables(null, "%", "%", new String[] { "TABLE" });

			while (tableSet.next()) {
				String tableName = (String) tableSet.getObject("TABLE_NAME");

				TableInfo ti = new TableInfo(tableName, new ArrayList<ColumnInfo>(), new HashMap<String, ColumnInfo>());

				tables.put(tableName, ti);

				ResultSet set = dbmd.getColumns(null, "%", tableName, "%");

				while (set.next()) {
					ColumnInfo ci = new ColumnInfo(set.getString("COLUMN_NAME"), set.getString("TYPE_NAME"), 0);
					ti.getColumns().put(set.getString("COLUMN_NAME"), ci);
				}

				ResultSet set2 = dbmd.getPrimaryKeys(null, "%", tableName);
				while (set2.next()) {
					ColumnInfo ci2 = ti.getColumns().get(set2.getObject("COLUMN_NAME"));
					ci2.setKeyType(1);// 设置为主键类型
					ti.getPriKeys().add(ci2);
				}

				if (ti.getPriKeys().size() > 0) {// 取唯一主键。。方便使用。如果是联合主键则为空
					ti.setOnlyPriKey(ti.getPriKeys().get(0));
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		// 更新类结构
		// updateJavaPOFile();

		// 加载pob包下面的所有的类，便于重用，提高效率
		loadPOTables();
	}

	public static Map<String, TableInfo> getTableInfos() {
		return tables;
	}

	/**
	 * 根据表结构，更新配置po包下面的java类
	 */
	public static void updateJavaPOFile() {
		Map<String, TableInfo> map = TableContext.tables;
		for (TableInfo t : map.values()) {
			JavaFileUtils.createJavaPOFile(t, new MySqlTypeConvertor());
		}
	}

	/**
	 * 加载po包下面的类
	 */
	public static void loadPOTables() {
		for (TableInfo t : tables.values()) {
			try {
				Class<?> c = Class.forName(
						DBManager.getConf().getPoPackage() + "." + StringUtils.firstChar2UpperCase(t.getTname()));
				poClassTableMap.put(c, t);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}

		}
	}

	public static void main(String[] args) {
		Map<String, TableInfo> tables = getTableInfos();
		System.out.println(tables);
	}
}
