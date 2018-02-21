package com.aimir.fep.iot.dao;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.hibernate.criterion.Projection;

import com.aimir.util.Condition;

/**
 * @author Toby Ilmin Lee
 * 
 * @param <T>
 *            Persistent Class
 * @param <ID>
 *            ID Class
 */
public interface GenericDao<T, ID extends Serializable> {

	public abstract Class<T> getPersistentClass();

	public abstract T findById(ID id, boolean lock);
	
	public abstract T findByCondition(final String condition, final Object value);
	
	public abstract List<T> findByConditions(final Set<Condition>condition);
	
	public abstract List<Object> findTotalCountByConditions(final Set<Condition>condition);
	
	public List<Object> getSumFieldByCondition(Set<Condition> conditions, String field, String ...groupBy);
	
	public List<Map<String, Object>> findByConditionsAndProjections(
		Set<Condition> conditions, List<Projection> projections);
	
	public abstract List<T> getAll();

	public abstract List<T> findByExample(final T exampleInstance, final String[] excludeProperty);

	public abstract T saveOrUpdate(T entity);
	
	public abstract T saveOrUpdate_requires_new(T entity);

	public abstract T add(T entity);	
	
	public abstract T add_requires_new(T entity);
	
	public boolean exists(ID id);
	
	//code
	public abstract T codeAdd(T entity);
	public abstract T codeParentAdd(T entity);
	public abstract T codeUpdate(T entity);
	public abstract void codeDelete(T entity);
	
	//group
	public abstract T groupAdd(T entity);
	public abstract T groupSaveOrUpdate(T entity);
	public abstract T groupUpdate(T entity);
	public abstract void groupDelete(T entity);

	//device
	public abstract T mcuAdd(T entity);
	public abstract T meterAdd(T entity);
	public abstract T modemAdd(T entity);
	
	public T get(ID id);

	public abstract T update(T entity);
	/*
	 * SP-454 added 2017.01.23 for Lp update
	 */
	public abstract T update(T entity, Properties addCriteria);
	public abstract T update_requires_new(T entity);

	public abstract void delete(T entity);

	public abstract int deleteById(final ID id);

	public int deleteAll();
	
	public long getRowCount();	
	
	public void flush();
	
	public void clear();
	
	public void evict(Object entity);
	
	public void merge(Object entity);
	
	public void flushAndClear();
	
	public void refresh(Object entity);
	
	public abstract void saveOrUpdateAll(Collection<?> entities);

}