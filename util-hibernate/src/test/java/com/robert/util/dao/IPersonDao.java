package com.robert.util.dao;

import java.util.List;
import java.util.Map;

import com.robert.util.domain.Person;
import com.robert.util.model.Pager;

public interface IPersonDao extends IBaseDao<Person, Integer> {
	/**
	 * 获取所有的数据
	 * 
	 * @return 返回所有对象的集合
	 */
	public List<Person> getAll();
	
	/**
	 * 获取满足条件的数据，hql查询语句中使用占位符
	 * 
	 * @param hql
	 *            hql查询语句
	 * @param args
	 *            占位符参数的实际值
	 * @return 返回满足条件对象的集合
	 */
	public List<Person> getAll(String hql, Object... args);
	
	/**
	 * 获取满足条件的数据，hql查询语句中使用字面量
	 * 
	 * @param hql
	 *            hql查询语句
	 * @param alias
	 *            一个字典：key为hql查询语句的字面量，value为实际值
	 * @return 返回满足条件对象的集合
	 */
	public List<Person> getAll(String hql, Map<String, Object> alias);
	
	/**
	 * 没有查询条件的获取页面数据
	 * 
	 * @return 页面数据信息
	 */
	public Pager<Person> getPageDatas() ;

	/**
	 * 有查询条件的获取页面数据
	 * 
	 * @param hql
	 *            查询语句
	 * @param args
	 *            占位符参数值
	 * @return 页面数据信息
	 */
	public Pager<Person> getPageDatas(String hql, Object... args);
	
	/**
	 * 有查询条件的获取页面数据
	 * 
	 * @param hql
	 *            查询语句
	 * @param alias
	 *            别名参数值
	 * @return 页面数据信息
	 */
	public Pager<Person> getPageDatas(String hql, Map<String, Object> alias) ;
	
	/**
	 * 获取满足条件的数据，hql查询语句中既使用使用字面量，有使用了占位符参数
	 * 
	 * @param hql
	 *            hql查询语句
	 * @param alias
	 *            一个字典：key为hql查询语句的字面量，value为实际值
	 * @param args
	 *            占位符参数的实际值
	 * @return 返回满足条件对象的集合
	 */
	public List<Person> getAll(String hql, Map<String, Object> alias, Object... args);
	
	/**
	 * 有查询条件的获取页面数据
	 * 
	 * @param hql
	 *            查询语句
	 * @param alias
	 *            别名参数值
	 * @param args
	 *            占位符参数值
	 * @return 页面数据信息
	 */
	public Pager<Person> getPageDatas(String hql, Map<String, Object> alias,
			Object... args) ;
	
	/**
	 * 查询出一组对象结合
	 * 
	 * @param hql
	 *            查询语句
	 * @param args
	 *            不定参数，
	 * @return
	 */
	public List<Object> getAll4Properties(String hql, Object... args);

	/**
	 * 查询出一组对象结合
	 * 
	 * @param hql
	 *            查询语句
	 * @param args
	 *            不定参数，
	 * @param alias
	 * @return
	 */
	public List<Object> getAll4Properties(String hql,
			Map<String, Object> alias, Object... args);
}
