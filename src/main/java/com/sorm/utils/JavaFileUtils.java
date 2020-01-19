package com.sorm.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.sorm.bean.ColumnInfo;
import com.sorm.bean.JavaFieldGetSet;
import com.sorm.bean.TableInfo;
import com.sorm.core.DBManager;
import com.sorm.core.MySqlTypeConvertor;
import com.sorm.core.TableContext;
import com.sorm.core.TypeConvertor;

/**
 * 封装了生成Java文件（源代码）操作常用的操作
 * 
 * @author Panlf
 *
 */
public class JavaFileUtils {

	/**
	 * 根据字段信息生成Java属性信息。
	 * 
	 * @param column
	 *            字段信息
	 * @param convertor
	 *            类型转换器
	 * @return Java属性和get/set方法
	 */
	public static JavaFieldGetSet createFieldGetSetSRC(ColumnInfo column, TypeConvertor convertor) {
		JavaFieldGetSet jfgs = new JavaFieldGetSet();
		String javaFieldType = convertor.databaseType2JavaType(column.getDataType());
		jfgs.setFieldInfo("\tprivate " + javaFieldType + " " + column.getName() + ";\n");

		// 生成get源代码
		StringBuilder getSrc = new StringBuilder();
		getSrc.append(
				"\tpublic " + javaFieldType + " get" + StringUtils.firstChar2UpperCase(column.getName()) + "(){\n");
		getSrc.append("\t\treturn " + column.getName() + ";\n");
		getSrc.append("\t}\n");
		jfgs.setGetInfo(getSrc.toString());

		// 生成set源代码
		StringBuilder setSrc = new StringBuilder();
		setSrc.append("\tpublic void set" + StringUtils.firstChar2UpperCase(column.getName()) + "(");
		setSrc.append(javaFieldType + " " + column.getName() + "){\n");
		setSrc.append("\t\tthis." + column.getName() + "=" + column.getName() + ";\n");
		setSrc.append("\t}\n");
		jfgs.setSetInfo(setSrc.toString());

		return jfgs;
	}

	/**
	 * 根据表信息生成Java类源码
	 * 
	 * @param tableInfo
	 *            表信息
	 * @param convertor
	 *            数据转换器
	 * @return Java类源码
	 */
	public static String createJavaSrc(TableInfo tableInfo, TypeConvertor convertor) {

		Map<String, ColumnInfo> columns = tableInfo.getColumns();
		List<JavaFieldGetSet> javaFields = new ArrayList<>();

		for (ColumnInfo c : columns.values()) {
			javaFields.add(createFieldGetSetSRC(c, convertor));
		}

		StringBuilder src = new StringBuilder();

		// 生成package语句
		src.append("package " + DBManager.getConf().getPoPackage() + ";\n\n");

		// 生成import语句
		// src.append("import java.sql.*;\n");
		// src.append("import java.util.*;\n\n");

		// 生成类声明语句
		src.append("public class " + StringUtils.firstChar2UpperCase(tableInfo.getTname()) + " {\n\n");

		// 生成属性列表
		for (JavaFieldGetSet f : javaFields) {
			src.append(f.getFieldInfo());
		}

		src.append("\n\n");

		// 生成get方法列表
		for (JavaFieldGetSet f : javaFields) {
			src.append(f.getGetInfo());
		}

		// 生成set方法列表
		for (JavaFieldGetSet f : javaFields) {
			src.append(f.getSetInfo());
		}

		// 生成类结束
		src.append("}\n");
		System.out.println(src);
		return src.toString();
	}

	public static void createJavaPOFile(TableInfo tableInfo, TypeConvertor convertor) {
		String src = createJavaSrc(tableInfo, convertor);

		String srcPath = DBManager.getConf().getSrcPath() + "\\";
		String packagePath = DBManager.getConf().getPoPackage().replaceAll("\\.", "/");

		File f = new File(srcPath + packagePath);
		System.out.println(f.getAbsolutePath() + "********");

		if (!f.exists()) {// 如果指定目录不存在，则创建
			f.mkdirs();
		}

		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(
					new FileWriter(f + "/" + StringUtils.firstChar2UpperCase(tableInfo.getTname()) + ".java"));
			bw.write(src);
			System.out.println("建立表" + tableInfo.getTname() + "对应的java类："
					+ StringUtils.firstChar2UpperCase(tableInfo.getTname()) + ".java");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (bw != null) {
				try {
					bw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	@Test
	public void createFieldGetSetSRCTest() {
		ColumnInfo ci = new ColumnInfo("id", "int", 0);
		JavaFieldGetSet jfgs = createFieldGetSetSRC(ci, new MySqlTypeConvertor());
		System.out.println(jfgs);
	}

	@Test
	public void createJavaSrcTest() {
		Map<String, TableInfo> map = TableContext.tables;
		TableInfo t = map.get("tbl_user");
		createJavaSrc(t, new MySqlTypeConvertor());
	}

	@Test
	public void createJavaPOFile() {
		Map<String, TableInfo> map = TableContext.tables;
		TableInfo t = map.get("tbl_user");
		createJavaPOFile(t, new MySqlTypeConvertor());
	}
}
