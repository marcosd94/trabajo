package py.com.excelsis.sicca.seleccion.session;

import py.com.excelsis.sicca.entity.*;
import py.com.excelsis.sicca.session.util.CustomEntityQuery;
import py.com.excelsis.sicca.session.util.QueryValue;
import py.com.excelsis.sicca.util.SICCASessionParameters;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TemporalType;

@Name("evalCerradasList")
public class EvalCerradasList extends CustomEntityQuery<EvalAbiertas> {

	private static final String EJBQL = "select evalAbiertas from EvalAbiertas evalAbiertas";

	private static final String[] RESTRICTIONS = {};
	@In(value = "entityManager")
	EntityManager em;
	private EvalAbiertas evalCerradas;

	public EvalCerradasList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
	}

	public EvalAbiertas getEvalCerradas() {
		return evalCerradas;
	}

	public void setEvalCerradas(EvalAbiertas evalCerradas) {
		this.evalCerradas = evalCerradas;
	}

	/**
	 * Transforma el resultser de las evaluaciones cerradas, poniendole id a cada fila
	 * 
	 * @return
	 */
	private List<EvalAbiertas> transformarResultSetCerradas(List<Object[]> lista) {
		List<EvalAbiertas> lCerradas = new ArrayList<EvalAbiertas>();
		if (lista.size() > 0) {
			Integer indice = 0;
			for (Object[] o : lista) {
				lCerradas.add(new EvalAbiertas());
				lCerradas.get(indice).setIdEvalAbiertas(indice.longValue());
				if (o[1] != null)
					lCerradas.get(indice).setConcursoPuestoAgr(((java.math.BigInteger) o[1]).longValue());
				if (o[2] != null)
					lCerradas.get(indice).setDatosEspecificos(em.find(DatosEspecificos.class, ((java.math.BigInteger) o[2]).longValue()));
				if (o[3] != null)
					lCerradas.get(indice).setEvalDocumentalCab(em.find(EvalDocumentalCab.class, ((java.math.BigInteger) o[3]).longValue()));
				if (o[4] != null)
					lCerradas.get(indice).setEvalReferencial(em.find(EvalReferencial.class, ((java.math.BigInteger) o[4]).longValue()));
				if (o[5] != null)
					lCerradas.get(indice).setEvalReferencialTipoeval(em.find(EvalReferencialTipoeval.class, ((java.math.BigInteger) o[5]).longValue()));
				if (o[6] != null)
					lCerradas.get(indice).setPostulacion(em.find(Postulacion.class, ((java.math.BigInteger) o[6]).longValue()));

				indice++;
			}
		}

		return lCerradas;

	}

	public List<EvalAbiertas> cargarEvalCerradas(Long idGrupo) {
		StringBuffer SQL = new StringBuffer();
		SQL.append(" SELECT ");
		SQL.append("     evalabiert0_.id_eval_abiertas , ");
		SQL.append("     evalabiert0_.id_concurso_puesto_agr , ");
		SQL.append("     evalabiert0_.id_datos_especificos , ");
		SQL.append("     evalabiert0_.id_eval_documental_cab , ");
		SQL.append("     evalabiert0_.id_eval_referencial , ");
		SQL.append("     evalabiert0_.id_eval_referencial_tipoeval , ");
		SQL.append("     evalabiert0_.id_postulacion ");
		SQL.append(" FROM ");
		SQL.append("     seleccion.eval_abiertas evalabiert0_ ");
		SQL.append(" LEFT OUTER JOIN seleccion.eval_referencial_tipoeval evalrefere1_ ");
		SQL.append(" ON ");
		SQL.append("     evalabiert0_.id_eval_referencial_tipoeval=evalrefere1_.id_eval_referencial_tipoeval ");
		SQL.append(" WHERE ");
		SQL.append("     ( ");
		SQL.append("         evalrefere1_.usu_cierre_evaluacion IS NOT NULL ");
		SQL.append("     ) ");
		SQL.append(" AND evalabiert0_.id_concurso_puesto_agr=  " + idGrupo);

		Query q = em.createNativeQuery(SQL.toString());
		List<Object[]> lista = q.getResultList();
		List<EvalAbiertas> lCerradas = transformarResultSetCerradas(lista);
		return lCerradas;

	}

	public List<EvalAbiertas> cargarEvalCerradas(Long idTipoEval, Date fechaDesde, Date fechaHasta, Long idGrupo) {
		StringBuffer elWhere = new StringBuffer();
		elWhere.append(" WHERE ");
		elWhere.append("     ( ");
		elWhere.append("         evalrefere1_.usu_cierre_evaluacion IS NOT NULL ");
		elWhere.append("     ) ");
		elWhere.append(" AND evalabiert0_.id_concurso_puesto_agr=  " + idGrupo);
		StringBuffer elJoin = new StringBuffer();

		if (idTipoEval != null) {
			elWhere.append(" AND evalrefere1_.id_datos_especificos_tipo_eval = " + idTipoEval);
		}
		if (fechaDesde != null && fechaHasta != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
			elJoin.append(" INNER JOIN seleccion.eval_Referencial evalreferencial ");
			elJoin.append(" ON ");
			elJoin.append("     evalabiert0_.id_eval_referencial=evalreferencial.id_eval_referencial ");
			elWhere.append("  AND to_date( to_char(evalreferencial.fecha_evaluacion,'DD-MM-YYYY'),'DD-MM-YYYY') between to_date('"
				+ sdf.format(fechaDesde)
				+ "','DD-MM-YYYY') and to_date( '"
				+ sdf.format(fechaHasta) + "','DD-MM-YYYY')");
		}
		StringBuffer SQL = new StringBuffer();
		SQL.append(" SELECT ");
		SQL.append("     evalabiert0_.id_eval_abiertas , ");
		SQL.append("     evalabiert0_.id_concurso_puesto_agr , ");
		SQL.append("     evalabiert0_.id_datos_especificos , ");
		SQL.append("     evalabiert0_.id_eval_documental_cab , ");
		SQL.append("     evalabiert0_.id_eval_referencial , ");
		SQL.append("     evalabiert0_.id_eval_referencial_tipoeval , ");
		SQL.append("     evalabiert0_.id_postulacion ");
		SQL.append(" FROM ");
		SQL.append("     seleccion.eval_abiertas evalabiert0_ ");
		SQL.append(" LEFT OUTER JOIN seleccion.eval_referencial_tipoeval evalrefere1_ ");
		SQL.append(" ON ");
		SQL.append("     evalabiert0_.id_eval_referencial_tipoeval=evalrefere1_.id_eval_referencial_tipoeval ");
		SQL.append(elJoin);
		SQL.append(elWhere);

		Query q = em.createNativeQuery(SQL.toString());
		setMaxResults(null);
		List<Object[]> lista = q.getResultList();
		List<EvalAbiertas> lCerradas = transformarResultSetCerradas(lista);
		return lCerradas;
	}

	private void executeQueryEvaluados(String ejbql) {
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		getResultList(ejbql);
		//System.out.println("EN EL EJECUTAR :" + this.getResultList().size());

	}

}
