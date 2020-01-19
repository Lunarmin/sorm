package com.sorm.po;

public class User {

	private String name;
	private Integer id;
	private Integer age;


	public String getName(){
		return name;
	}
	public Integer getId(){
		return id;
	}
	public Integer getAge(){
		return age;
	}
	public void setName(String name){
		this.name=name;
	}
	public void setId(Integer id){
		this.id=id;
	}
	public void setAge(Integer age){
		this.age=age;
	}
}
