package com.robert.util.dao.impl;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;

import com.robert.util.dao.IBaseDao;
import com.robert.util.model.Pager;
import com.robert.util.model.SystemRequest;
import com.robert.util.model.SystemRequestHolder;

@SuppressWarnings("unchecked")
public class BaseDao<T, TID extends Serializable> implements IBaseDao<T, TID> {

	/**
	 * 实体的类型
	 */
	protected Class<T> entityClass;

	/**
	 * 实体类型的名称
	 */
	protected String entityName;

	/**
	 * SessionFactory
	 */
	private SessionFactory sessionFactory;

	/**
	 * 通过反射获取实体的类型
	 */
	public BaseDao() {
		ParameterizedType type = (ParameterizedType) this.getClass()
				.getGenericSuperclass();
		this.entityClass = (Class<T>) type.getActualTypeArguments()[0];
		this.entityName = this.entityClass.getSimpleName();
	}

	public SessionFactory getSessionFactory() {
		return this.sessionFactory;
	}

	/**
	 * 注入sessionFactory
	 * 
	 * @param sessionFactory
	 */
	@Inject
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	/**
	 * 获取session
	 * 
	 * @return
	 */
	protected Session getSession() {
		return this.sessionFactory.getCurrentSession();
	}

	@Override
	public T insert(T entity) {
		TID id = (TID) getSession().save(entity);
		return load(id);
	}

	@Override
	public void delete(T entity) {
		getSession().delete(entity);
	}

	@Override
	public void update(T entity) {
		getSession().update(entityName, entity);
	}

	@Override
	public T load(TID id) {
		return (T) getSession().load(entityClass, id);
	}

	@Override
	public T get(TID id) {
		return (T) getSession().get(entityClass, id);
	}

	/**
	 * 获取所有的数据
	 * 
	 * @return 返回所有对象的集合
	 */
	public List<T> getAll() {
		String hql = "FROM " + entityName;
		return this.getAll(hql);
	}

	/**
	 * 获取满足条件的数据，hql查询语句中使用占位符
	 * 
	 * @param hql
	 *            hql查询语句
	 * @param args
	 *            占位符参数的实际值
	 * @return 返回满足条件对象的集合
	 */
	public List<T> getAll(String hql, Object... args) {
		return this.getAll(hql, null, args);
	}

	/**
	 * 获取满足条件的数据，hql查询语句中使用字面量
	 * 
	 * @param hql
	 *            hql查询语句
	 * @param alias
	 *            一个字典：key为hql查询语句的字面量，value为实际值
	 * @return 返回满足条件对象的集合
	 */
	public List<T> getAll(String hql, Map<String, Object> alias) {
		return this.getAll(hql, alias, new Object[] {});
	}

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
	public List<T> getAll(String hql, Map<String, Object> alias, Object... args) {
		hql = setOrder2Query(hql);
		// 创建一个查询
		Query query = getSession().createQuery(hql);
		// 设置参数的实际值到查询
		setAliasValue2Query(alias, query);
		// 设置占位符的实际值到查询
		setAragsValue2Query(query, args);

		return query.list();
	}

	/**
	 * 没有查询条件的获取页面数据
	 * 
	 * @return 页面数据信息
	 */
	public Pager<T> getPageDatas() {
		String hql = "FROM " + entityName;
		return this.getPageDatas(hql);
	}

	/**
	 * 有查询条件的获取页面数据
	 * 
	 * @param hql
	 *            查询语句
	 * @param args
	 *            占位符参数值
	 * @return 页面数据信息
	 */
	public Pager<T> getPageDatas(String hql, Object... args) {
		return this.getPageDatas(hql, null, args);
	}

	/**
	 * 有查询条件的获取页面数据
	 * 
	 * @param hql
	 *            查询语句
	 * @param alias
	 *            别名参数值
	 * @return 页面数据信息
	 */
	public Pager<T> getPageDatas(String hql, Map<String, Object> alias) {
		return this.getPageDatas(hql, alias, null);
	}

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
	public Pager<T> getPageDatas(String hql, Map<String, Object> alias,
			Object... args) {

		int pageSize = getSystemRequest().getPageSize();
		int pageOffset = getSystemRequest().getPageOffset();
		int totalRecord = getTotalRecord(hql, alias, args);

		String queryString = this.setOrder2Query(hql);

		Query query = getSession().createQuery(queryString);

		this.setAliasValue2Query(alias, query);
		this.setAragsValue2Query(query, args);

		query.setFirstResult(pageOffset);
		query.setMaxResults(pageSize);

		Pager<T> pager = new Pager<T>();
		pager.setDatas(query.list());
		pager.setOffset(pageOffset);
		pager.setSize(pageSize);
		pager.setTotalRecord(totalRecord);

		return pager;
	}

	/**
	 * 查询出一组对象结合
	 * 
	 * @param hql
	 *            查询语句
	 * @param args
	 *            不定参数，
	 * @return
	 */
	public List<Object> getAll4Properties(String hql, Object... args) {
		return this.getAll4Properties(hql, null, args);
	}

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
			Map<String, Object> alias, Object... args) {
		hql = setOrder2Query(hql);
		Query query = getSession().createQuery(hql);
		setAliasValue2Query(alias, query);
		setAragsValue2Query(query, args);
		return query.list();
	}

	/**
	 * 获取查询的结果总数
	 * 
	 * @param hql
	 *            查询语句
	 * @param alias
	 *            别名参数值
	 * @param args
	 *            占位符参数值
	 * @return 总的数据条数
	 */
	protected int getTotalRecord(String hql, Map<String, Object> alias,
			Object... args) {
		int beginIndex = hql.indexOf("from") > 0 ? hql.indexOf("from") : hql
				.indexOf("FROM");
		String queryString = "SELECT count(*) " + hql.substring(beginIndex);
		Query query = getSession().createQuery(queryString);
		this.setAliasValue2Query(alias, query);
		this.setAragsValue2Query(query, args);
		String result = String.valueOf(query.uniqueResult());
		return null == result ? 0 : Integer.parseInt(result);
	}

	/**
	 * 设置占位符的实际值到查询
	 * 
	 * @param query
	 *            查询对象
	 * @param args
	 *            占位符的实际值
	 */
	protected void setAragsValue2Query(Query query, Object... args) {
		boolean isNeedSetArgs = null == args ? false : true;
		if (isNeedSetArgs) {
			for (int i = 0; i < args.length; i++) {
				query.setParameter(i, args[i]);
			}
		}
	}

	/**
	 * 设置别名参数的实际值到查询
	 * 
	 * @param alias
	 *            别名参数与其实际值构成的字典
	 * @param query
	 *            查询对象
	 */
	protected void setAliasValue2Query(Map<String, Object> alias, Query query) {
		if (null != alias) {
			for (Map.Entry<String, Object> entry : alias.entrySet()) {
				if (entry.getValue() instanceof Collection) {
					query.setParameterList(entry.getKey(),
							(Collection) entry.getValue());
				} else {
					query.setParameter(entry.getKey(), entry.getValue());
				}
			}
		}
	}

	/**
	 * 给查询语句添加排序
	 * 
	 * @param queryString
	 *            查询语句
	 * @return 添加排序语句后的新查询语句
	 */
	protected String setOrder2Query(String queryString) {
		String sort = getSystemRequest().getSort();
		String order = getSystemRequest().getOrder();
		boolean isNeedOrder = null != sort && !("".equals(sort.trim()));
		if (isNeedOrder) {
			order = order == null ? "asc"
					: "desc".equals(order.trim()) ? "desc" : "asc";
			queryString += " order by " + sort + " " + order;
		}
		return queryString;
	}

	protected SystemRequest getSystemRequest() {
		SystemRequest sr = SystemRequestHolder.getSystemRequest();
		if (null == sr)
			sr = new SystemRequest();
		return sr;
	}

	/***********************************************************************/

	/**
	 * SQL查询所有 返回对象集合
	 * 
	 * @param sql
	 *            查询语句
	 * @param clz
	 *            数据库实体的类型
	 * @param hasEntity
	 *            是否为数据库实体
	 * @param args
	 *            占位符的实际值
	 * @return 对象集合
	 */
	public <N extends Object> List<N> listBySql(String sql, Class<?> clz,
			boolean hasEntity, Object... args) {
		return this.listBySql(sql, null, clz, hasEntity, args);
	}

	/**
	 * SQL查询所有 返回对象集合
	 * 
	 * @param sql
	 *            查询语句
	 * @param alias
	 *            字面量参数及其实际值的Map集合
	 * @param clz
	 *            数据库实体的类型
	 * @param hasEntity
	 *            是否为数据库实体
	 * @return 对象集合
	 */
	public <N extends Object> List<N> listBySql(String sql,
			Map<String, Object> alias, Class<?> clz, boolean hasEntity) {
		return this.listBySql(sql, alias, clz, hasEntity);
	}

	/**
	 * SQL查询所有 返回对象集合
	 * 
	 * @param sql
	 *            查询语句
	 * @param alias
	 *            字面量参数及其实际值的Map集合
	 * @param clz
	 *            数据库实体的类型
	 * @param hasEntity
	 *            是否为数据库实体
	 * @param args
	 *            占位符的实际值
	 * @return 对象集合
	 */
	public <N extends Object> List<N> listBySql(String sql,
			Map<String, Object> alias, Class<?> clz, boolean hasEntity,
			Object... args) {
		sql = setOrder2Query(sql);
		SQLQuery sq = getSession().createSQLQuery(sql);
		setAliasValue2Query(alias, sq);
		setAragsValue2Query(sq, args);
		if (hasEntity) {
			sq.addEntity(clz);
		} else
			sq.setResultTransformer(Transformers.aliasToBean(clz));
		return sq.list();
	}

	public <N extends Object> Pager<N> findBySql(String sql, Class<?> clz,
			boolean hasEntity, Object... args) {
		return this.findBySql(sql, null, clz, hasEntity, args);
	}

	public <N extends Object> Pager<N> findBySql(String sql,
			Map<String, Object> alias, Class<?> clz, boolean hasEntity) {
		return this.findBySql(sql, alias, clz, hasEntity, null);
	}

	public <N extends Object> Pager<N> findBySql(String sql,
			Map<String, Object> alias, Class<?> clz, boolean hasEntity,
			Object... args) {
		sql = setOrder2Query(sql);
		SQLQuery sQuery = getSession().createSQLQuery(sql);

		setAliasValue2Query(alias, sQuery);
		setAragsValue2Query(sQuery, args);

		Pager<N> pager = new Pager<N>();

		settingPager(sQuery, pager);

		if (hasEntity) {
			sQuery.addEntity(clz);
		} else {
			sQuery.setResultTransformer(Transformers.aliasToBean(clz));
		}
		List<N> datas = sQuery.list();
		pager.setDatas(datas);
		int total = getTotalRecord(sql, alias, args);
		pager.setTotalRecord(total);
		return pager;
	}

	public Object queryObject(String hql, Object... args) {
		return this.queryObject(hql, null, args);
	}

	public Object queryObject(String hql, Map<String, Object> alias) {
		return this.queryObject(hql, alias, null);
	}

	public Object queryObject(String hql, Map<String, Object> alias,
			Object... args) {
		Query query = getSession().createQuery(hql);
		setAliasValue2Query(alias, query);
		setAragsValue2Query(query, args);
		return query.uniqueResult();
	}

	public int getMaxOrder(Integer pid, String clz) {
		String hql = "select max(o.orderNum) from " + clz
				+ " o where o.parent.id=" + pid;
		if (pid == null || pid == 0)
			hql = "select max(o.orderNum) from " + clz
					+ " o where o.parent is null";
		Object obj = this.queryObject(hql);
		if (obj == null)
			return 0;
		return (Integer) obj;
	}

	public int getMaxOrder(String clz) {
		String hql = "select max(o.orderNum) from " + clz + " o ";
		Object obj = this.queryObject(hql);
		if (obj == null)
			return 0;
		return (Integer) obj;
	}

	public Object addEntity(Object entity) {
		getSession().save(entity);
		return entity;
	}

	public void saveOrUpdateEntity(Object entity) {
		this.getSession().saveOrUpdate(entity);
	}

	public void updateEntity(Object entity) {
		getSession().save(entity);
	}

	public List<?> listObj(String hql, Object... args) {
		return this.listObj(hql, null, args);
	}

	public List<?> listObj(String hql, Map<String, Object> alias) {
		return this.listObj(hql, alias, null);
	}

	public List<?> listObj(String hql, Map<String, Object> alias,
			Object... args) {
		hql = setOrder2Query(hql);
		Query query = getSession().createQuery(hql);
		setAliasValue2Query(alias, query);
		setAragsValue2Query(query, args);
		return query.list();
	}

	public Object loadBySn(String sn, String entityClassName) {
		String hql = "select e from " + entityClassName + " e where e.sn=?";
		return this.queryObject(hql, sn);
	}

	public void executeHQL(String hql,
			Object... args) {
		this.executeHQL(hql, null, args);
	}
	public void executeHQL(String hql, Map<String, Object> alias
			) {
		this.executeHQL(hql, alias, null);
	}
	public void executeHQL(String hql, Map<String, Object> alias,
			Object... args) {
		Query query = getSession().createQuery(hql);
		setAliasValue2Query(alias, query);
		setAragsValue2Query(query, args);
		query.executeUpdate();
	}

	/**
	 * @param query
	 * @param pager
	 */
	protected void settingPager(Query query, Pager pager) {
		int pageSize = getSystemRequest().getPageSize();
		int pageOffset = getSystemRequest().getPageOffset();
		query.setFirstResult(pageOffset);
		query.setMaxResults(pageSize);
		pager.setOffset(pageOffset);
		pager.setSize(pageSize);
	}

}
