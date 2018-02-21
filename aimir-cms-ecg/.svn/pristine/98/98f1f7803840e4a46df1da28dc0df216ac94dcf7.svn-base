package com.aimir.cms.dao;

import java.util.List;
import java.util.Map;

import com.aimir.cms.model.DebtEnt;
import com.aimir.dao.GenericDao;

public interface DebtEntDao extends GenericDao<DebtEnt, Integer> {
	public List<DebtEnt> getDebt(String customerNo, String debtType, String debtRef);
	public List<Map<String, Object>> getPrepaymentChargeContractList(Map<String, Object> condition, boolean isCount);
	public List<Map<String,Object>> getDebtInfoByCustomerNo(String customerNo, String debtType, String debtRef);
	public void modifyDebtInfo(Map<String, Object> condition);
}