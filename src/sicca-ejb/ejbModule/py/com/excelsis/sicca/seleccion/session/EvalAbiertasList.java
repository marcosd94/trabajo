package py.com.excelsis.sicca.seleccion.session;

import py.com.excelsis.sicca.entity.*;
import py.com.excelsis.sicca.session.util.CustomEntityQuery;
import py.com.excelsis.sicca.util.SICCASessionParameters;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

@Name("evalAbiertasList")
public class EvalAbiertasList extends CustomEntityQuery<EvalAbiertas> implements Cloneable {

	private static final String EJBQL = "select evalAbiertas from EvalAbiertas evalAbiertas";

	private static final String[] RESTRICTIONS = {};
	@In(value = "entityManager")
	EntityManager em;
	private EvalAbiertas evalAbiertas;

	public EvalAbiertasList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
	}

	@SuppressWarnings("unchecked")
	public List<EvalAbiertas> cargarEvalAbiertas(Long idGrupo) {
		// Todo esto es porque no se tiene un id �nico para cada fila de la vista
		StringBuffer SQL = new StringBuffer();
		SQL.append(" SELECT ");
		SQL.append("       evalabiert0_.id_eval_abiertas, evalabiert0_.id_postulacion,  evalabiert0_.id_eval_referencial_tipoeval,");
		SQL.append("       evalabiert0_.id_eval_referencial, evalabiert0_.id_eval_documental_cab, evalabiert0_.id_datos_especificos, evalabiert0_.id_concurso_puesto_agr ");
		SQL.append(" FROM ");
		SQL.append("     seleccion.eval_abiertas evalabiert0_ ");
		SQL.append("     inner join seleccion.postulacion p on evalabiert0_.id_postulacion = p.id_postulacion ");
		SQL.append(" WHERE  ");
		SQL.append("  evalabiert0_.id_concurso_puesto_agr=  " + idGrupo);
		SQL.append("  and p.usu_cancelacion is null  ");
		SQL.append("  order by id_postulacion");
		Query q = em.createNativeQuery(SQL.toString());
		List<Object[]> lista = q.getResultList();
		List<EvalAbiertas> lAbiertas = new ArrayList<EvalAbiertas>();
		if (lista.size() > 0) {
			Integer indice = 0;
			for (Object[] o : lista) {
				lAbiertas.add(new EvalAbiertas());
				lAbiertas.get(indice).setIdEvalAbiertas(indice.longValue());
				if (o[6] != null)
					lAbiertas.get(indice).setConcursoPuestoAgr(((java.math.BigInteger) o[6]).longValue());
				if (o[5] != null)
					lAbiertas.get(indice).setDatosEspecificos(em.find(DatosEspecificos.class, ((java.math.BigInteger) o[5]).longValue()));
				if (o[4] != null)
					lAbiertas.get(indice).setEvalDocumentalCab(em.find(EvalDocumentalCab.class, ((java.math.BigInteger) o[4]).longValue()));
				if (o[3] != null)
					lAbiertas.get(indice).setEvalReferencial(em.find(EvalReferencial.class, ((java.math.BigInteger) o[3]).longValue()));
				if (o[2] != null)
					lAbiertas.get(indice).setEvalReferencialTipoeval(em.find(EvalReferencialTipoeval.class, ((java.math.BigInteger) o[2]).longValue()));
				if (o[1] != null)
					lAbiertas.get(indice).setPostulacion(em.find(Postulacion.class, ((java.math.BigInteger) o[1]).longValue()));

				indice++;
			}
		}

		//System.out.println("");

		return lAbiertas;

	}

	private List<EvalAbiertas> executeQueryEvaluados(String ejbql) {
		setEjbql(ejbql);
		setMaxResults(null);
		return getResultList(ejbql);

	}

	public EvalAbiertas getEvalAbiertas() {
		return evalAbiertas;
	}
}
