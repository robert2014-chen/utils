package com.robert.util.dao.impl;

import org.springframework.stereotype.Repository;

import com.robert.util.dao.IAddressDao;
import com.robert.util.domain.Address;

@Repository("addressDao")
public class AddressDao extends BaseDao<Address, String> implements IAddressDao {

}
