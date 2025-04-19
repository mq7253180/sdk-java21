package com.quincy.core.dao;

import java.math.BigInteger;

import com.quincy.sdk.annotation.jdbc.ExecuteQuery;
import com.quincy.sdk.annotation.jdbc.JDBCDao;

@JDBCDao
public interface UtilsDao {
	@ExecuteQuery(sql = "SELECT AUTO_INCREMENT FROM information_schema.TABLES WHERE table_name=?", returnItemType = BigInteger.class)
	public BigInteger selectAutoIncreament(String name);
	@ExecuteQuery(sql = "SELECT LAST_INSERT_ID()", returnItemType = BigInteger.class)
	public BigInteger selectLastInsertId();
}