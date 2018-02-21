package com.aimir.cms.dao;

import java.util.List;
import java.util.Map;

import com.aimir.cms.model.DebtLog;
import com.aimir.dao.GenericDao;

public interface DebtLogDao extends GenericDao<DebtLog, Integer> {
	public List<Map<String,Object>> getDebtArrearsLog(Long prepaymentLogId);
	public Map<String,Object> getPrepaymentLogWithDebt(Long prepaymentLogId);
	public Map<String, Object> getDepositHistoryList(Map<String,Object> condition);
	public List<DebtLog> getDebtLog(Long prepaymentLogId);
}