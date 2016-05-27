package com.robert.util.dao;

import java.io.Serializable;
import java.util.Map;

public interface IBaseDao<T, TID extends Serializable> {

	/**
	 * 插入一条数据
	 * 
	 * @param entity
	 *            需要插入的对象
	 * @return 返回插入后的对象
	 */
	public T insert(T entity);

	/**
	 * 根据数据库主键删除一条对象
	 * 
	 * @param entity
	 *            需要删除的对象
	 */
	public void delete(T entity);

	/**
	 * 更新数据库中一条数据
	 * 
	 * @param entity
	 *            更新的对象
	 */
	public void update(T entity);

	/**
	 * 根据数据库中的主键加载对象
	 * 
	 * @param id
	 *            需要加载对象的数据库中的主键
	 * @return 返回需要加载的对象
	 */
	public T load(TID id);

	/**
	 * 根据数据库中的主键获取对象
	 * 
	 * @param id
	 *            想要获取的对象的主键
	 * @return 返回想要获取的对象
	 */
	public T get(TID id);

	public void executeHQL(String hql, Object... args);

	public void executeHQL(String hql, Map<String, Object> alias);

	public void executeHQL(String hql, Map<String, Object> alias,
			Object... args);

}
