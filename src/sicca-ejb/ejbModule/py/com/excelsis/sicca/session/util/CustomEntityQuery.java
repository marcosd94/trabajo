package py.com.excelsis.sicca.session.util;

import java.sql.Date;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.NonUniqueResultException;
import javax.transaction.SystemException;

import org.jboss.seam.annotations.Transactional;

import py.com.excelsis.sicca.session.util.CustomQuery;

import org.jboss.seam.persistence.PersistenceProvider;
import org.jboss.seam.persistence.QueryParser;
import org.jboss.seam.persistence.PersistenceProvider.Feature;
import org.jboss.seam.transaction.Transaction;

/**
 * A Query object for JPA.
 * 
 * @author Gavin King
 * @author erivas
 */
public class CustomEntityQuery<E> extends CustomQuery<EntityManager, E> {

	private List<E> resultList;
	private E singleResult;
	private Long resultCount;
	private Map<String, String> hints;
	// private javax.persistence.Query query;
	private String customEjbql;
	private Map<String, QueryValue> params = new HashMap<String, QueryValue>();
	
	/**
	 * Validate the query
	 * 
	 * @throws IllegalStateException
	 *             if the query is not valid
	 */
	@Override
	public void validate() {
		super.validate();
		if (getEntityManager() == null) {
			throw new IllegalStateException("entityManager is null");
		}

		if (!PersistenceProvider.instance().supportsFeature(Feature.WILDCARD_AS_COUNT_QUERY_SUBJECT)) {
			setUseWildcardAsCountQuerySubject(false);
		}
	}

	@Override
	@Transactional
	public boolean isNextExists() {
		/* Para que funcione el 451 */
		if (resultList == null) {
			javax.persistence.Query query = null;
			if (customEjbql != null)
				query = createQuery(customEjbql);
			else
				query = createQuery();
		 
			resultList = query == null ? null : query.getResultList();
		}
		/**/
		return resultList != null && getMaxResults() != null && resultList.size() > getMaxResults();
	}
	
	@Override
	public Integer getCurrentPage() {
		return super.getCurrentPage();
	}

	/**
	 * Get the list of results this query returns Any changed restriction values will be applied
	 */
	@Transactional
	@Override
	public List<E> getResultList() {
		if (this.customEjbql != null)
			return getResultList(customEjbql);

		if (isAnyParameterDirty()) {
			refresh();
		}
		initResultList();
		return truncResultList(resultList);
	}

	@Transactional
	public List<E> getResultList(String ejbql) {
		// if ( isAnyParameterDirty() )
		// {
		// refresh();
		// }
		//System.out.print(ejbql);

		initResultList(ejbql);
		setCustomEjbql(ejbql);
		return truncResultList(resultList);
	}

	@SuppressWarnings("unchecked")
	private void initResultList() {
		if (resultList == null) {
			javax.persistence.Query query = createQuery();
			resultList = query == null ? null : query.getResultList();
		}
	}

	@SuppressWarnings("unchecked")
	private void initResultList(String ejbql) {
		if (resultList == null) {
			javax.persistence.Query q = createQuery(ejbql);
			resultList = q == null ? null : q.getResultList();
		}
	}

	/**
	 * Get a single result from the query Any changed restriction values will be applied
	 * 
	 * @throws NonUniqueResultException
	 *             if there is more than one result
	 */
	@Transactional
	@Override
	public E getSingleResult() {
		if (isAnyParameterDirty()) {
			refresh();
		}
		initSingleResult();
		return singleResult;
	}

	private void initSingleResult() {
		if (singleResult == null) {
			javax.persistence.Query query = createQuery();
			singleResult = (E) (query == null ? null : query.getSingleResult());
		}
	}

	/**
	 * Get the number of results this query returns Any changed restriction values will be applied
	 */
	@Transactional
	@Override
	public Long getResultCount() {
		if (isAnyParameterDirty()) {
			refresh();
		}
		initResultCount();
		return resultCount;
	}

	private void initResultCount() {
		if (resultCount == null) {
			javax.persistence.Query query = createCountQuery();
			resultCount = query == null ? null : (Long) query.getSingleResult();
		}
	}

	/**
	 * The refresh method will cause the result to be cleared. The next access to the result set will cause the query to be executed. This method <b>does not</b> cause the ejbql or restrictions to
	 * reread. If you want to update the ejbql or restrictions you must call {@link #setEjbql(String)} or {@link #setRestrictions(List)}
	 */
	@Override
	public void refresh() {
		super.refresh();
		resultCount = null;
		resultList = null;
		singleResult = null;
	}

	public EntityManager getEntityManager() {
		return getPersistenceContext();
	}

	public void setEntityManager(EntityManager entityManager) {
		setPersistenceContext(entityManager);
	}

	@Override
	protected String getPersistenceContextName() {
		return "entityManager";
	}

	protected javax.persistence.Query createQuery() {
		parseEjbql();

		evaluateAllParameters();

		joinTransaction();

		javax.persistence.Query query = getEntityManager().createQuery(getRenderedEjbql());
		setParameters(query, getQueryParameterValues(), 0);
		setParameters(query, getRestrictionParameterValues(), getQueryParameterValues().size());
		if (getFirstResult() != null)
			query.setFirstResult(getFirstResult());
		if (getMaxResults() != null)
			query.setMaxResults(getMaxResults() + 1); // add one, so we can tell if there is another page
		if (getHints() != null) {
			for (Map.Entry<String, String> me : getHints().entrySet()) {
				query.setHint(me.getKey(), me.getValue());
			}
		}
		return query;
	}

	protected javax.persistence.Query createQuery(String ejbql) {
		ejbql = parseOrder(ejbql);

		javax.persistence.Query query = getEntityManager().createQuery(ejbql);
		setParams(query);

		if (getFirstResult() != null)
			query.setFirstResult(getFirstResult());
		if (getMaxResults() != null)
			query.setMaxResults(getMaxResults() + 1); // add one, so we can tell if there is another page
		if (getHints() != null) {
			for (Map.Entry<String, String> me : getHints().entrySet()) {
				query.setHint(me.getKey(), me.getValue());
			}
		}
		return query;
	}

	/**
	 * Orderna la lista
	 * 
	 * @param ejbql
	 * @return
	 */
	private String parseOrder(String ejbql) {
		if (getOrderColumn() != null)
			ejbql += " order by " + getOrderColumn();
		else if (getOrder() != null)
			ejbql += " order by " + getOrder();

		if (getOrderDirection() != null)
			ejbql += " " + getOrderDirection();

		return ejbql;
	}

	/**
	 * Setea los parametros al query
	 * 
	 * @param query
	 */
	private void setParams(javax.persistence.Query query) {
		if (this.params != null && this.params.values().size() > 0) {
			for (String key : this.params.keySet()) {
				QueryValue queryValue = params.get(key);
				if (queryValue.getTemporalType() == null)
					query.setParameter(key, queryValue.getValue());
				else {
					if (queryValue.getValue() instanceof Date)
						query.setParameter(key, (Date) queryValue.getValue(), queryValue.getTemporalType());
					else if (queryValue.getValue() instanceof Calendar)
						query.setParameter(key, (Calendar) queryValue.getValue(), queryValue.getTemporalType());
					else
						query.setParameter(key, queryValue.getValue());
				}
			}
		}
	}

	protected javax.persistence.Query createCountQuery() {
		//System.out.println("--->> ***" + getCustomEjbql() + "***");
		if (getCustomEjbql() != null)
			setEjbql(getCustomEjbql());
		parseEjbql();

		evaluateAllParameters();

		joinTransaction();

		javax.persistence.Query query = getEntityManager().createQuery(getCountEjbql());
		setParams(query);
		setParameters(query, getQueryParameterValues(), 0);
		setParameters(query, getRestrictionParameterValues(), getQueryParameterValues().size());
		return query;
	}

	private void setParameters(javax.persistence.Query query, List<Object> parameters, int start) {
		for (int i = 0; i < parameters.size(); i++) {
			Object parameterValue = parameters.get(i);
			if (isRestrictionParameterSet(parameterValue)) {
				query.setParameter(QueryParser.getParameterName(start + i), parameterValue);
			}
		}
	}

	public Map<String, String> getHints() {
		return hints;
	}

	public void setHints(Map<String, String> hints) {
		this.hints = hints;
	}

	protected void joinTransaction() {
		try {
			Transaction.instance().enlist(getEntityManager());
		} catch (SystemException se) {
			throw new RuntimeException("could not join transaction", se);
		}
	}

	// public javax.persistence.Query getQuery() {
	// return query;
	// }
	//
	// public void setQuery(javax.persistence.Query query) {
	// this.query = query;
	// }

	public void setParams(Map<String, QueryValue> params) {
		this.params = params;
	}

	public Map<String, QueryValue> getParams() {
		return params;
	}

	public void setCustomEjbql(String customEjbql) {
		this.customEjbql = customEjbql;
	}

	public String getCustomEjbql() {
		return customEjbql;
	}

}
