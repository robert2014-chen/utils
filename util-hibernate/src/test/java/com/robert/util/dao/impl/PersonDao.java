package com.robert.util.dao.impl;

import org.springframework.stereotype.Repository;

import com.robert.util.dao.IPersonDao;
import com.robert.util.domain.Person;

@Repository("personDao")
public class PersonDao extends BaseDao<Person, Integer> implements IPersonDao  {
}
