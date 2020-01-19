package com.sorm.core;

/**
 * 创建Query对象的工厂类
 * 
 * @author Panlf
 *
 */
public class QueryFactory {

	private static Query prototypeObj;// 原型对象

	static {
		
		try {
			// 加载指定类
			Class<?> c = Class.forName(DBManager.getConf().getQueryClass());
			prototypeObj = (Query) c.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private QueryFactory() {// 私有构造器

	}

	public static Query createQuery() {
		try {
			return (Query) prototypeObj.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}

}
