package com.robert.util.test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.orm.hibernate4.SessionHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.robert.dbunit.util.DBUtil;
import com.robert.dbunit.util.EntityHelper;
import com.robert.util.dao.IAddressDao;
import com.robert.util.dao.IPersonDao;
import com.robert.util.domain.Address;
import com.robert.util.domain.Person;
import com.robert.util.model.Pager;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/applicationContext.xml")
public class BaseDaoTest extends com.robert.dbunit.util.AbstractDbUnitTestCase {

	private IPersonDao personDao;

	private IAddressDao addressDao;

	private SessionFactory sessionFactory;

	public IPersonDao getPersonDao() {
		return personDao;
	}

	@Inject
	public void setPersonDao(IPersonDao personDao) {
		this.personDao = personDao;
	}

	public IAddressDao getAddressDao() {
		return addressDao;
	}

	@Inject
	public void setAddressDao(IAddressDao addressDao) {
		this.addressDao = addressDao;
	}

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	@Inject
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	private Address expect;
	
	@BeforeClass
	public static void initConnection() throws DatabaseUnitException{
		DBUtil.url="jdbc:mysql://10.203.138.231:3306/util_test";
		DBUtil.username="root";
		DBUtil.password="root";
		
		dbuintCon = new DatabaseConnection(DBUtil.getInstance().getConnection());
	}

	@Before
	public void backup() throws SQLException, IOException,
			DatabaseUnitException {
		expect = new Address();
		expect.setId("1");
		expect.setName("beijing");

		IDataSet dataSet = createDataSet("t_address");
		DatabaseOperation.CLEAN_INSERT.execute(dbuintCon, dataSet);

//		backupAllTable();

		try {
			Session s = sessionFactory.openSession();
			TransactionSynchronizationManager.bindResource(sessionFactory,
					new SessionHolder(s));
		} catch (IllegalStateException e) {
		}

	}

	@After
	public void restore() throws FileNotFoundException, DatabaseUnitException,
			SQLException {

//		resumeTalbe();

		SessionHolder holder = (SessionHolder) TransactionSynchronizationManager
				.getResource(sessionFactory);
		Session s = holder.getSession();
		s.flush();
		TransactionSynchronizationManager.unbindResource(sessionFactory);
		s.close();
	}

	@Test
	public void testGet() throws FileNotFoundException, DatabaseUnitException,
			SQLException, NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		Address actual = addressDao.get("1");

		new EntityHelper<Address>().assertEqualEntity(expect, actual,
				Address.class);
	}

	@Test
	public void testLoad() throws DatabaseUnitException, SQLException,
			NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		Address actual = addressDao.load("1");

		new EntityHelper<Address>().assertEqualEntity(expect, actual,
				Address.class);
	}

	@Test
	public void testInsert() {
		Address actual = new Address();
		actual.setName("hangzhou");
		Address expect = addressDao.insert(actual);

		Assert.assertNotNull(expect.getId());
	}

	@Test
	public void delete() {
		Address actual = addressDao.get("3");
		addressDao.delete(actual);
		
		Assert.assertNull("已删除的对象还存在", addressDao.get("3"));
	}

	@Test
	public void update() throws NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		Address actual = new Address();
		actual.setId("1");
		actual.setName("shanghai");
		addressDao.update(actual);

		new EntityHelper<Address>().assertEqualEntity(actual,
				addressDao.get("1"), Address.class);
	}

	@Test
	public void testGetAll() throws NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		List<Address> list = addressDao.getAll();

		List<Address> expect = new ArrayList<>();
		Address e = new Address();
		e.setId("1");
		e.setName("beijing");
		expect.add(e);
		e = new Address();
		e.setId("2");
		e.setName("wuhan");
		expect.add(e);
		e = new Address();
		e.setId("3");
		e.setName("hainan");
		expect.add(e);

		new EntityHelper<Address>()
				.assertEqualList(expect, list, Address.class);

	}

	@Test
	public void testGetAllByArgs() throws NoSuchMethodException,
			SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		List<Address> list = addressDao.getAll("FROM Address a WHERE a.id=?",
				"1");

		List<Address> expect = new ArrayList<>();
		Address e = new Address();
		e.setId("1");
		e.setName("beijing");
		expect.add(e);

		new EntityHelper<Address>()
				.assertEqualList(expect, list, Address.class);

	}

	@Test
	public void testGetAllByAlias() throws NoSuchMethodException,
			SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		Map<String, Object> alias = new HashMap<String, Object>();
		List list1 = new ArrayList();
		list1.add("1");
		alias.put("id", list1);
		alias.put("name", "wuhan");
		addressDao.executeHQL("UPDATE Address a SET a.name=:name WHERE a.id in :id",
				alias);

//		List<Address> expect = new ArrayList<>();
//		Address e = new Address();
//		e.setId("1");
//		e.setName("beijing");
//		expect.add(e);
//
//		new EntityHelper<Address>()
//				.assertEqualList(expect, list, Address.class);

	}

	@Test
	public void testGetAllByAliasAndArgs() throws NoSuchMethodException,
			SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		Map<String, Object> alias = new HashMap<String, Object>();
		alias.put("name", "wuhan");
		List<Address> list = addressDao.getAll(
				"FROM Address a WHERE a.id=? and name=:name", alias, "2");

		List<Address> expect = new ArrayList<>();
		Address e = new Address();
		e.setId("2");
		e.setName("wuhan");
		expect.add(e);

		new EntityHelper<Address>()
				.assertEqualList(expect, list, Address.class);

	}

	@Test
	public void testGetPageDatas() {
		Pager<Address> pager = addressDao.getPageDatas();
		Assert.assertNotNull(pager);
		Assert.assertEquals(3, pager.getTotalRecord());
		Assert.assertEquals(1, pager.getTotalPage());
	}

	@Test
	public void testGetPageDatasByPersonDao() {
		Map<String, Object> alias = new HashMap<>();
		alias.put("name", "wangwu");
		Pager<Person> pager = personDao.getPageDatas(
				"FROM Person p WHERE p.address.id=? and p.name=:name", alias,
				"2");
		Assert.assertEquals(1, pager.getTotalRecord());
		Assert.assertEquals(1, pager.getTotalPage());
	}
	
	@Test
	public void testGetAll4Properties(){
		Map<String, Object> alias = new HashMap<>();
		alias.put("name", "wangwu");
		List<Object> list = personDao.getAll4Properties(
				"SELECT p.id FROM Person p WHERE p.address.id=? and p.name=:name", alias,
				"2");
		for(Object i : list){
			System.out.println(i);
		}
	}
}
