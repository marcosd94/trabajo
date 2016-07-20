package py.com.excelsis.sicca.session;

import java.util.List;
import java.util.Vector;

import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.DatosGenerales;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.framework.EntityHome;

@Scope(ScopeType.CONVERSATION)
@Name("datosEspecificosHome")
public class DatosEspecificosHome extends EntityHome<DatosEspecificos> {

	@In(value = "entityManager")
	EntityManager em;
	@In(create = true)
	DatosGeneralesHome datosGeneralesHome;
	@In(create = true)
	DatosEspecificosList datosEspecificosList;

	// Value holders for selectItems if exists
	public static final String CONTEXT_VAR_NAME = "datosEspecificoss";
	public static final String[] CONTEXT_VAR_NAMES = { CONTEXT_VAR_NAME,
			CONTEXT_VAR_NAME + "SelectItems" };

	
	
	@SuppressWarnings("unchecked")
	public List<DatosEspecificos> getDatosEspecificoss() {
		try {
			return getEntityManager().createQuery(
					" SELECT o FROM " + DatosEspecificos.class.getName()
							+ " o "
							+ "WHERE o.activo = true ORDER BY o.descripcion")
					.getResultList();
		} catch (Exception ex) {
			return new Vector<DatosEspecificos>();
		}
	}

	@Factory(value = CONTEXT_VAR_NAME + "SelectItems", scope = ScopeType.EVENT)
	public List<SelectItem> getDatosEspecificosSelectItems() {
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString(
				"opt_select_one")));
		for (DatosEspecificos o : getDatosEspecificoss())
			si.add(new SelectItem(o.getIdDatosEspecificos(), ""
					+ o.getDescripcion()));
		return si;
	}

	@Factory(value = CONTEXT_VAR_NAME + "ByTipoConcursoCPOCMESelectItems", scope = ScopeType.EVENT)
	public List<SelectItem> getDatosEspecificosPorTipoSelectItems() {
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString(
				"opt_select_one")));
		
		for (DatosEspecificos o : getDatosEspecificosByTipoConcurso()) {
			if (o.getValorAlf().equalsIgnoreCase("CPO")
					|| o.getValorAlf().equalsIgnoreCase("CME"))
				si.add(new SelectItem(o.getIdDatosEspecificos(), ""
						+ o.getDescripcion()));
		}
		return si;
	}
	
	@Factory(value = CONTEXT_VAR_NAME + "ByTipoConcursoCircuitoCIOSelectItems", scope = ScopeType.EVENT)
	public List<SelectItem> getDatosEspecificosPorTipoCircuitoCIOSelectItems() {
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString(
				"opt_select_one")));
		
		for (DatosEspecificos o : getDatosEspecificosByTipoConcurso()) {
			if (o.getValorAlf().equalsIgnoreCase("CII")
					|| o.getValorAlf().equalsIgnoreCase("CIR")
					 	|| o.getValorAlf().equalsIgnoreCase("CIPS"))
				si.add(new SelectItem(o.getIdDatosEspecificos(), ""
						+ o.getDescripcion()));
		}
		return si;
	}

	public List<DatosEspecificos> getDatosEspecificosByTipoConcurso() {
		try {
			datosEspecificosList.getDatosGenerales().setDescripcion(
					"TIPOS DE CONCURSO");
			datosEspecificosList.setOrderColumn("datosEspecificos.descripcion");
			datosEspecificosList.setMaxResults(null);
			return datosEspecificosList.getResultList();
		} catch (Exception ex) {
			return new Vector<DatosEspecificos>();
		}
	}

	@Factory(value = CONTEXT_VAR_NAME + "ByTipoConcursoFPDISCSelectItems", scope = ScopeType.EVENT)
	public List<SelectItem> getDatosEspecificosDiscapSelectItems() {
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString(
				"opt_select_one")));
		for (DatosEspecificos o : getDatosEspecificosByTipoConcursoFPDISC())
			si.add(new SelectItem(o.getIdDatosEspecificos(), ""
					+ o.getDescripcion()));
		return si;
	}

	public List<DatosEspecificos> getDatosEspecificosByTipoConcursoFPDISC() {
		try {
			datosEspecificosList.getDatosGenerales().setDescripcion(
					"TIPOS DE DOCUMENTOS");
			datosEspecificosList.getDatosEspecificos().setValorAlf("FPDISC");
			datosEspecificosList.setOrderColumn("datosEspecificos.descripcion");
			datosEspecificosList.setMaxResults(null);
			return datosEspecificosList.getResultList();
		} catch (Exception ex) {
			return new Vector<DatosEspecificos>();
		}
	}

	@Factory(value = CONTEXT_VAR_NAME + "ByTipoConcursoSelectItems", scope = ScopeType.EVENT)
	public List<SelectItem> getDatosEspecificosByTipoConcursoSelectItems() {
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString(
				"opt_select_one")));
		for (DatosEspecificos o : getDatosEspecificosByTipoConcurso())
			
			if(!o.getDescripcion().equals("SELECCION DE JUECES") )
				si.add(new SelectItem(o.getIdDatosEspecificos(), ""	+ o.getDescripcion()));
		return si;
	}

	public List<DatosEspecificos> getDatosEspecificosByTipoEvaluacion() {
		try {
			datosEspecificosList.getDatosGenerales().setDescripcion(
					"TIPOS DE EVALUACION");
			datosEspecificosList.setOrderColumn("datosEspecificos.descripcion");
			datosEspecificosList.setMaxResults(null);

			return datosEspecificosList.getResultList();
		} catch (Exception ex) {
			return new Vector<DatosEspecificos>();
		}
	}

	@Factory(value = CONTEXT_VAR_NAME + "ByTipoEvaluacionSelectItems", scope = ScopeType.EVENT)
	public List<SelectItem> getDatosEspecificosByTipoEvaluacionSelectItems() {
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString(
				"opt_select_one")));
		for (DatosEspecificos o : getDatosEspecificosByTipoEvaluacion())
			si.add(new SelectItem(o.getIdDatosEspecificos(), ""
					+ o.getDescripcion()));
		return si;
	}

	public List<DatosEspecificos> getDatosEspecificosByMediosPublicacion() {
		try {
			datosEspecificosList.getDatosGenerales().setDescripcion(
					"MEDIOS DE PUBLICACION");
			datosEspecificosList.setOrderColumn("datosEspecificos.descripcion");
			datosEspecificosList.setMaxResults(null);

			return datosEspecificosList.getResultList();
		} catch (Exception ex) {
			return new Vector<DatosEspecificos>();
		}
	}

	@Factory(value = CONTEXT_VAR_NAME + "ByMediosPublicacionSelectItems", scope = ScopeType.EVENT)
	public List<SelectItem> getDatosEspecificosByMediosPublicacionSelectItems() {
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString(
				"opt_select_one")));
		for (DatosEspecificos o : getDatosEspecificosByMediosPublicacion())
			si.add(new SelectItem(o.getIdDatosEspecificos(), ""
					+ o.getDescripcion()));
		return si;
	}

	public List<Object[]> getDatosEspecificosByTipoDocumento2() {

		String sql = "SELECT  e.id_datos_especificos, r.desc_larga, e.descripcion  "
				+ " FROM  seleccion.datos_generales g,seleccion.datos_especificos e, seleccion.referencias r"
				+ " WHERE g.descripcion  LIKE UPPER ('%TIPOS%DE%DOCUMENTOS%') AND g.id_datos_generales = e.id_datos_generales "
				+ " AND   e.valor_alf IN ('FPOS','FPDISC','FPEXP','FPFAC','MDOC') AND   e.valor_alf  = r.desc_abrev " +
						"AND e.activo is true AND g.activo is true "
				+ " ORDER BY e.id_datos_especificos asc";
		Query q = em.createNativeQuery(sql);
		return q.getResultList();
	}

	public List<DatosEspecificos> getDatosEspecificosByTipoDocumento() {
		try {
			datosEspecificosList.getDatosGenerales().setDescripcion(
					"TIPOS DE DOCUMENTOS");
			datosEspecificosList.getDatosEspecificos().setValorAlf("MDOC");
			datosEspecificosList.setOrderColumn("datosEspecificos.descripcion");

			datosEspecificosList.setMaxResults(null);

			return datosEspecificosList.getResultList();
		} catch (Exception ex) {
			return new Vector<DatosEspecificos>();
		}
	}

	@Factory(value = CONTEXT_VAR_NAME + "ByTipoDocumentoSelectItems", scope = ScopeType.EVENT)
	public List<SelectItem> getDatosEspecificosByTipoDocumentoSelectItems() {
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString(
				"opt_select_one")));
		for (Object[] o : getDatosEspecificosByTipoDocumento2()) {

			si.add(new SelectItem(o[0], o[1] + " - " + o[2]));
		}

		return si;
	}

	public List<DatosEspecificos> getDatosEspecificosByTipoDocumentos() {
		try {
			datosEspecificosList.getDatosGenerales().setDescripcion(
					"TIPOS DE DOCUMENTOS");
			datosEspecificosList.setOrderColumn("datosEspecificos.descripcion");

			datosEspecificosList.setMaxResults(null);

			return datosEspecificosList.getResultList();
		} catch (Exception ex) {
			return new Vector<DatosEspecificos>();
		}
	}

	@Factory(value = CONTEXT_VAR_NAME + "ByTipoDocumentosSelectItems", scope = ScopeType.EVENT)
	public List<SelectItem> getDatosEspecificosByTipoDocumentosSelectItems() {
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString(
				"opt_select_one")));
		for (DatosEspecificos o : getDatosEspecificosByTipoDocumentos())
			si.add(new SelectItem(o.getIdDatosEspecificos(), ""
					+ o.getDescripcion()));
		return si;
	}

	public List<DatosEspecificos> getDatosEspecificosByTipoPregunta() {
		try {
			datosEspecificosList.getDatosGenerales().setDescripcion(
					"TIPOS DE PREGUNTAS");
			datosEspecificosList.setOrderColumn("datosEspecificos.descripcion");
			datosEspecificosList.setMaxResults(null);

			return datosEspecificosList.getResultList();
		} catch (Exception ex) {
			return new Vector<DatosEspecificos>();
		}
	}

	@Factory(value = CONTEXT_VAR_NAME + "ByTipoPreguntaSelectItems", scope = ScopeType.EVENT)
	public List<SelectItem> getDatosEspecificosByTipoPreguntaSelectItems() {
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString(
				"opt_select_one")));
		for (DatosEspecificos o : getDatosEspecificosByTipoPregunta())
			si.add(new SelectItem(o.getIdDatosEspecificos(), ""
					+ o.getDescripcion()));
		return si;
	}

	public List<DatosEspecificos> getGradoAutonomia() {
		try {
			datosEspecificosList.getDatosGenerales().setDescripcion(
					"GRADOS DE AUTONOMIA");
			datosEspecificosList.setOrderColumn("datosEspecificos.descripcion");
			datosEspecificosList.setMaxResults(null);

			return datosEspecificosList.getResultList();
		} catch (Exception ex) {
			return new Vector<DatosEspecificos>();
		}
	}

	@Factory(value = CONTEXT_VAR_NAME + "ByGradoAutonomiaSelectItems", scope = ScopeType.EVENT)
	public List<SelectItem> getGradoAutonomiaSelectItems() {
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString(
				"opt_select_one")));
		for (DatosEspecificos o : getGradoAutonomia())
			si.add(new SelectItem(o.getIdDatosEspecificos(), ""
					+ o.getDescripcion()));
		return si;
	}

	public List<DatosEspecificos> getDiscapacidades() {
		try {
			datosEspecificosList.getDatosGenerales().setDescripcion(
					"DISCAPACIDADES");
			datosEspecificosList.setOrderColumn("datosEspecificos.descripcion");
			datosEspecificosList.getDatosEspecificos().setValorAlf(null);
			datosEspecificosList.setMaxResults(null);

			return datosEspecificosList.getResultList();
		} catch (Exception ex) {
			return new Vector<DatosEspecificos>();
		}
	}

	@Factory(value = CONTEXT_VAR_NAME + "ByDiscapacidadSelectItems", scope = ScopeType.EVENT)
	public List<SelectItem> getDiscapacidadSelectItems() {
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString(
				"opt_select_one")));
		for (DatosEspecificos o : getDiscapacidades())
			si.add(new SelectItem(o.getIdDatosEspecificos(), ""
					+ o.getDescripcion()));
		return si;
	}

	public List<DatosEspecificos> getIdiomas() {
		try {
			datosEspecificosList.getDatosGenerales().setDescripcion("IDIOMAS");
			datosEspecificosList.setOrderColumn("datosEspecificos.descripcion");
			datosEspecificosList.setMaxResults(null);
			datosEspecificosList.getDatosEspecificos().setValorAlf(null);
			return datosEspecificosList.getResultList();
		} catch (Exception ex) {
			return new Vector<DatosEspecificos>();
		}
	}

	@Factory(value = CONTEXT_VAR_NAME + "ByIdiomasSelectItems", scope = ScopeType.EVENT)
	public List<SelectItem> getIdiomasSelectItems() {
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString(
				"opt_select_one")));
		for (DatosEspecificos o : getIdiomas())
			si.add(new SelectItem(o.getIdDatosEspecificos(), ""
					+ o.getDescripcion()));
		return si;
	}

	public List<DatosEspecificos> getDatosEspecificosByTipoDocumentoFPOS() {
		try {
			datosEspecificosList.getDatosGenerales().setDescripcion(
					"TIPOS DE DOCUMENTOS");
			datosEspecificosList.getDatosEspecificos().setValorAlf("FPOS");
			datosEspecificosList.setOrderColumn("datosEspecificos.descripcion");
			datosEspecificosList.setMaxResults(null);

			return datosEspecificosList.getResultList();
		} catch (Exception ex) {
			return new Vector<DatosEspecificos>();
		}
	}
	
	
	public List<DatosEspecificos> getDatosEspecificosByTipoDocumentoFPOC() {
		try {
			datosEspecificosList.getDatosGenerales().setDescripcion(
					"TIPOS DE DOCUMENTOS");
			datosEspecificosList.getDatosEspecificos().setValorAlf("FPOC");
			datosEspecificosList.setOrderColumn("datosEspecificos.descripcion");
			datosEspecificosList.setMaxResults(null);

			return datosEspecificosList.getResultList();
		} catch (Exception ex) {
			return new Vector<DatosEspecificos>();
		}
	}
	
	public List<DatosEspecificos> getDatosEspecificosByTipoDocumentoLEG_DF() {
		try {
			datosEspecificosList.getDatosGenerales().setDescripcion(
					"TIPOS DE DOCUMENTOS");
			datosEspecificosList.getDatosEspecificos().setValorAlf("LEG_DF");
			datosEspecificosList.setOrderColumn("datosEspecificos.descripcion");
			datosEspecificosList.setMaxResults(null);

			return datosEspecificosList.getResultList();
		} catch (Exception ex) {
			return new Vector<DatosEspecificos>();
		}
	}

	@Factory(value = CONTEXT_VAR_NAME + "ByTipoDocumentoFPOSSelectItems", scope = ScopeType.EVENT)
	public List<SelectItem> getDatosEspecificosByTipoDocumentoFPOSSelectItems() {
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString(
				"opt_select_one")));
		for (DatosEspecificos o : getDatosEspecificosByTipoDocumentoFPOS())
			si.add(new SelectItem(o.getIdDatosEspecificos(), ""
					+ o.getDescripcion()));
		return si;
	}
	
	
	
	@Factory(value = CONTEXT_VAR_NAME + "ByTipoDos", scope = ScopeType.EVENT)
	public List<SelectItem> getDatosEspecificosdos() {
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString(
				"opt_select_one")));
		for (DatosEspecificos o : getDatosEspecificosByTipoDocumentoFPOS())
		{
			if( o.getDescripcion().compareTo("COPIA DE CEDULA DE IDENTIDAD (AMBAS CARILLAS)")!=0)
						si.add(new SelectItem(o.getIdDatosEspecificos(), ""
								+ o.getDescripcion()));
			//System.out.println("a:" + o.getDescripcion());
		}
		for (DatosEspecificos o : getDatosEspecificosByTipoDocumentoFPOC())
			si.add(new SelectItem(o.getIdDatosEspecificos(), ""
					+ o.getDescripcion()));
		
		return si;
	}
	
	@Factory(value = CONTEXT_VAR_NAME + "ByTipoCiPartida", scope = ScopeType.EVENT)
	public List<SelectItem> getDatosEspecificosCiPartida() {
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString(
				"opt_select_one")));
		for (DatosEspecificos o : getDatosEspecificosByTipoDocumentoLEG_DF())
		{
			si.add(new SelectItem(o.getIdDatosEspecificos(), ""
							+ o.getDescripcion()));
			//System.out.println("a:" + o.getDescripcion());
		}
		
		
		return si;
	}
	
	

	public List<DatosEspecificos> getDatosEspecificosByTipoDocumentoMDOC() {
		try {
			datosEspecificosList.getDatosGenerales().setDescripcion(
					"TIPOS DE DOCUMENTOS");
			datosEspecificosList.getDatosEspecificos().setValorAlf("MDOC");
			datosEspecificosList.setOrderColumn("datosEspecificos.descripcion");
			datosEspecificosList.setMaxResults(null);

			return datosEspecificosList.getResultList();
		} catch (Exception ex) {
			return new Vector<DatosEspecificos>();
		}
	}

	@Factory(value = CONTEXT_VAR_NAME + "ByTipoDocumentoMDOCSelectItems", scope = ScopeType.EVENT)
	public List<SelectItem> getDatosEspecificosByTipoDocumentoMDOCSelectItems() {
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString(
				"opt_select_one")));
		for (DatosEspecificos o : getDatosEspecificosByTipoDocumentoMDOC())
			si.add(new SelectItem(o.getIdDatosEspecificos(), ""
					+ o.getDescripcion()));
		return si;
	}

	public List<DatosEspecificos> getDatosEspecificosByTipoDocumentoPORTAL() {
		try {
			datosEspecificosList.getDatosGenerales().setDescripcion(
					"TIPOS DE DOCUMENTOS");
			datosEspecificosList.getDatosEspecificos().setValorAlf("PORTAL");
			datosEspecificosList.setOrderColumn("datosEspecificos.descripcion");
			datosEspecificosList.setMaxResults(null);

			return datosEspecificosList.getResultList();
		} catch (Exception ex) {
			return new Vector<DatosEspecificos>();
		}
	}

	@Factory(value = CONTEXT_VAR_NAME + "ByTipoDocumentoPORTALSelectItems", scope = ScopeType.EVENT)
	public List<SelectItem> getDatosEspecificosByTipoDocumentoPORTALSelectItems() {
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString(
				"opt_select_one")));
		for (DatosEspecificos o : getDatosEspecificosByTipoDocumentoPORTAL())
			si.add(new SelectItem(o.getIdDatosEspecificos(), ""
					+ o.getDescripcion()));
		return si;
	}

	public List<DatosEspecificos> getDatosEspecificosByTipoDocumentoCOMS() {
		try {
			datosEspecificosList.getDatosGenerales().setDescripcion(
					"TIPOS DE DOCUMENTOS");
			datosEspecificosList.getDatosEspecificos().setValorAlf("COMS");
			datosEspecificosList.setOrderColumn("datosEspecificos.descripcion");
			datosEspecificosList.setMaxResults(null);

			return datosEspecificosList.getResultList();
		} catch (Exception ex) {
			return new Vector<DatosEspecificos>();
		}
	}

	@Factory(value = CONTEXT_VAR_NAME + "ByTipoDocumentoCOMSSelectItems", scope = ScopeType.EVENT)
	public List<SelectItem> getDatosEspecificosByTipoDocumentoCOMSSelectItems() {
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString(
				"opt_select_one")));
		for (DatosEspecificos o : getDatosEspecificosByTipoDocumentoCOMS())
			si.add(new SelectItem(o.getIdDatosEspecificos(), ""
					+ o.getDescripcion()));
		return si;
	}

	public List<DatosEspecificos> getTipoVinc() {
		try {
			datosEspecificosList.getDatosGenerales().setDescripcion(
					"TIPO DE VINCULACION");
			datosEspecificosList.setOrderColumn("datosEspecificos.descripcion");
			datosEspecificosList.getDatosEspecificos().setValorAlf(null);
			datosEspecificosList.setMaxResults(null);

			return datosEspecificosList.getResultList();
		} catch (Exception ex) {
			return new Vector<DatosEspecificos>();
		}
	}

	@Factory(value = CONTEXT_VAR_NAME + "BytTipoVincSelectItems", scope = ScopeType.EVENT)
	public List<SelectItem> getTipoVincSelectItems() {
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString(
				"opt_select_one")));
		for (DatosEspecificos o : getTipoVinc())
			si.add(new SelectItem(o.getIdDatosEspecificos(), ""
					+ o.getDescripcion()));
		return si;
	}
	public List<DatosEspecificos> getGradoParent() {
		try {
			datosEspecificosList.getDatosGenerales().setDescripcion(
					"GRADO DE PARENTESCO");
			datosEspecificosList.setOrderColumn("datosEspecificos.descripcion");
			datosEspecificosList.getDatosEspecificos().setValorAlf(null);
			datosEspecificosList.setMaxResults(null);

			return datosEspecificosList.getResultList();
		} catch (Exception ex) {
			return new Vector<DatosEspecificos>();
		}
	}

	@Factory(value = CONTEXT_VAR_NAME + "ByGradoParentSelectItems", scope = ScopeType.EVENT)
	public List<SelectItem> getGradoParentSelectItems() {
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString(
				"opt_select_one")));
		for (DatosEspecificos o : getGradoParent())
			si.add(new SelectItem(o.getIdDatosEspecificos(), ""
					+ o.getDescripcion()));
		return si;
	}


	public List<DatosEspecificos> getSector() {
		try {
			datosEspecificosList.getDatosGenerales().setDescripcion("SECTOR");
			datosEspecificosList.setOrderColumn("datosEspecificos.descripcion");
			datosEspecificosList.getDatosEspecificos().setValorAlf(null);
			datosEspecificosList.setMaxResults(null);

			return datosEspecificosList.getResultList();
		} catch (Exception ex) {
			return new Vector<DatosEspecificos>();
		}
	}

	@Factory(value = CONTEXT_VAR_NAME + "BySectorSelectItems", scope = ScopeType.EVENT)
	public List<SelectItem> getSectorSelectItems() {
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString(
				"opt_select_one")));
		for (DatosEspecificos o : getSector())
			si.add(new SelectItem(o.getIdDatosEspecificos(), ""
					+ o.getDescripcion()));
		return si;
	}

	public List<DatosEspecificos> getDatosEspecificosByMotivo() {
		try {
			datosEspecificosList.getDatosGenerales().setDescripcion(
					"MOTIVOS DE DESVINCULACION");
			datosEspecificosList.setOrderColumn("datosEspecificos.descripcion");
			datosEspecificosList.setMaxResults(null);

			return datosEspecificosList.getResultList();
		} catch (Exception ex) {
			return new Vector<DatosEspecificos>();
		}
	}

	@Factory(value = CONTEXT_VAR_NAME + "ByMotivoSelectItems", scope = ScopeType.EVENT)
	public List<SelectItem> getMotivoSelectItems() {
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString(
				"opt_select_one")));
		for (DatosEspecificos o : getDatosEspecificosByMotivo())
			si.add(new SelectItem(o.getIdDatosEspecificos(), ""
					+ o.getDescripcion()));
		return si;
	}

	public List<DatosEspecificos> getDatosEspecificosByTipoDocumentoFPEXP() {
		try {
			datosEspecificosList.getDatosGenerales().setDescripcion(
					"TIPOS DE DOCUMENTOS");
			datosEspecificosList.getDatosEspecificos().setValorAlf("FPEXP");
			datosEspecificosList.setOrderColumn("datosEspecificos.descripcion");
			datosEspecificosList.setMaxResults(null);

			return datosEspecificosList.getResultList();
		} catch (Exception ex) {
			return new Vector<DatosEspecificos>();
		}
	}

	@Factory(value = CONTEXT_VAR_NAME + "ByTipoDocumentoFPEXPSelectItems", scope = ScopeType.EVENT)
	public List<SelectItem> getDatosEspecificosByTipoDocumentoFPEXPSelectItems() {
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString(
				"opt_select_one")));
		for (DatosEspecificos o : getDatosEspecificosByTipoDocumentoFPEXP())
			si.add(new SelectItem(o.getIdDatosEspecificos(), ""
					+ o.getDescripcion()));
		return si;
	}

	public List<DatosEspecificos> getDatosEspecificosByTipoDocumentoFPFAC() {
		try {
			datosEspecificosList.getDatosGenerales().setDescripcion(
					"TIPOS DE DOCUMENTOS");
			datosEspecificosList.getDatosEspecificos().setValorAlf("FPFAC");
			datosEspecificosList.setOrderColumn("datosEspecificos.descripcion");
			datosEspecificosList.setMaxResults(null);

			return datosEspecificosList.getResultList();
		} catch (Exception ex) {
			return new Vector<DatosEspecificos>();
		}
	}

	@Factory(value = CONTEXT_VAR_NAME + "ByTipoDocumentoFPFACSelectItems", scope = ScopeType.EVENT)
	public List<SelectItem> getDatosEspecificosByTipoDocumentoFPFACSelectItems() {
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString(
				"opt_select_one")));
		for (DatosEspecificos o : getDatosEspecificosByTipoDocumentoFPFAC())
			si.add(new SelectItem(o.getIdDatosEspecificos(), ""
					+ o.getDescripcion()));
		return si;
	}

	public List<DatosEspecificos> getTipoCapacitacion() {
		try {
			datosEspecificosList.getDatosGenerales().setDescripcion(
					"TIPOS DE CAPACITACIONES");
			datosEspecificosList.setOrderColumn("datosEspecificos.descripcion");
			datosEspecificosList.setMaxResults(null);

			return datosEspecificosList.getResultList();
		} catch (Exception ex) {
			return new Vector<DatosEspecificos>();
		}
	}

	@Factory(value = CONTEXT_VAR_NAME + "ByTipoCapacitacionSelectItems", scope = ScopeType.EVENT)
	public List<SelectItem> getTipoCapacitacionItems() {
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString(
				"opt_select_one")));
		for (DatosEspecificos o : getTipoCapacitacion())
			si.add(new SelectItem(o.getIdDatosEspecificos(), ""
					+ o.getDescripcion()));
		return si;
	}

	@Factory(value = CONTEXT_VAR_NAME + "ByTipoCapacitacionTodosSelectItems", scope = ScopeType.EVENT)
	public List<SelectItem> getTipoCapacitacionTodosItems() {
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, "Todos"));
		for (DatosEspecificos o : getTipoCapacitacion())
			si.add(new SelectItem(o.getIdDatosEspecificos(), ""
					+ o.getDescripcion()));
		return si;
	}

	@Factory(value = CONTEXT_VAR_NAME + "ByTipoCapaTodosSelectItems", scope = ScopeType.EVENT)
	public List<SelectItem> getTipoCapaTodosItems() {
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, "Todos"));
		for (DatosEspecificos o : getTipoCapacitacion())
			si.add(new SelectItem(o.getIdDatosEspecificos(), ""
					+ o.getDescripcion()));
		return si;
	}

	public List<DatosEspecificos> getMotivoInac() {
		try {
			datosEspecificosList.getDatosGenerales().setDescripcion(
					"MOTIVO INACTIVACION PERSONAS");
			datosEspecificosList.setOrderColumn("datosEspecificos.descripcion");
			datosEspecificosList.setMaxResults(null);

			return datosEspecificosList.getResultList();
		} catch (Exception ex) {
			return new Vector<DatosEspecificos>();
		}
	}

	@Factory(value = CONTEXT_VAR_NAME + "ByMotivoInacSelectItems", scope = ScopeType.EVENT)
	public List<SelectItem> getMotivoInacItems() {
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString(
				"opt_select_one")));
		for (DatosEspecificos o : getMotivoInac())
			si.add(new SelectItem(o.getIdDatosEspecificos(), ""
					+ o.getDescripcion()));
		return si;
	}

	public List<DatosEspecificos> getOtrasInstituciones() {
		try {
			datosEspecificosList.getDatosGenerales().setDescripcion(
					"INSTITUCIONES");
			datosEspecificosList.setOrderColumn("datosEspecificos.descripcion");
			datosEspecificosList.setMaxResults(null);

			return datosEspecificosList.getResultList();
		} catch (Exception ex) {
			return new Vector<DatosEspecificos>();
		}
	}

	@Factory(value = CONTEXT_VAR_NAME + "ByOtrasInstitucionesSelectItems", scope = ScopeType.EVENT)
	public List<SelectItem> getOtrasInstitucionesItems() {
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString(
				"opt_select_one")));
		for (DatosEspecificos o : getOtrasInstituciones())
			si.add(new SelectItem(o.getIdDatosEspecificos(), ""
					+ o.getDescripcion()));
		return si;
	}

	public List<DatosEspecificos> getTipoDocCapa() {
		try {
			datosEspecificosList.getDatosGenerales().setDescripcion(
					"TIPOS DE DOCUMENTOS");
			datosEspecificosList.getDatosEspecificos().setValorAlf("PR_CAP");
			datosEspecificosList.setOrderColumn("datosEspecificos.descripcion");
			datosEspecificosList.setMaxResults(null);

			return datosEspecificosList.getResultList();
		} catch (Exception ex) {
			return new Vector<DatosEspecificos>();
		}
	}
	
	public List<DatosEspecificos> getTipoDocAltaUsuario() {
		try {
			datosEspecificosList.getDatosGenerales().setDescripcion(
					"TIPOS DE DOCUMENTOS");
			datosEspecificosList.getDatosEspecificos().setValorAlf("AUDOC");
			datosEspecificosList.setOrderColumn("datosEspecificos.descripcion");
			datosEspecificosList.setMaxResults(null);

			return datosEspecificosList.getResultList();
		} catch (Exception ex) {
			return new Vector<DatosEspecificos>();
		}
	}


	@Factory(value = CONTEXT_VAR_NAME + "ByTipoDocCapaSelectItems", scope = ScopeType.EVENT)
	public List<SelectItem> getTipoDocCapaItems() {
		List<SelectItem> si = new Vector<SelectItem>();
		// si.add(new SelectItem(null,
		// SeamResourceBundle.getBundle().getString("opt_select_one")));
		for (DatosEspecificos o : getTipoDocCapa())
			si.add(new SelectItem(o.getIdDatosEspecificos(), ""
					+ o.getDescripcion()));
		return si;
	}
	
	@Factory(value = CONTEXT_VAR_NAME + "ByTipoDocAltaUsuarioSelectItems", scope = ScopeType.EVENT)
	public List<SelectItem> getTipoDocAltaUsuarioItems() {
		List<SelectItem> si = new Vector<SelectItem>();
		// si.add(new SelectItem(null,
		// SeamResourceBundle.getBundle().getString("opt_select_one")));
		for (DatosEspecificos o : getTipoDocAltaUsuario())
			si.add(new SelectItem(o.getIdDatosEspecificos(), ""
					+ o.getDescripcion()));
		return si;
	}

	public List<DatosEspecificos> getTipoDocIstructor() {
		try {
			datosEspecificosList.getDatosGenerales().setDescripcion(
					"TIPOS DE DOCUMENTOS");
			datosEspecificosList.getDatosEspecificos().setValorAlf("INST_CAP");
			datosEspecificosList.setOrderColumn("datosEspecificos.descripcion");
			datosEspecificosList.setMaxResults(null);

			return datosEspecificosList.getResultList();
		} catch (Exception ex) {
			return new Vector<DatosEspecificos>();
		}
	}

	@Factory(value = CONTEXT_VAR_NAME + "ByTipoDocIstructorSelectItems", scope = ScopeType.EVENT)
	public List<SelectItem> getTipoDocIstructorItems() {
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString(
				"opt_select_one")));
		for (DatosEspecificos o : getTipoDocIstructor())
			si.add(new SelectItem(o.getIdDatosEspecificos(), ""
					+ o.getDescripcion()));
		return si;
	}

	public List<DatosEspecificos> getTipoDocF3CAP() {
		try {
			datosEspecificosList.getDatosGenerales().setDescripcion(
					"TIPOS DE DOCUMENTOS");
			datosEspecificosList.getDatosEspecificos().setValorAlf("F3_CAP");
			datosEspecificosList.setOrderColumn("datosEspecificos.descripcion");
			datosEspecificosList.setMaxResults(null);

			return datosEspecificosList.getResultList();
		} catch (Exception ex) {
			return new Vector<DatosEspecificos>();
		}
	}

	@Factory(value = CONTEXT_VAR_NAME + "ByTipoDocF3CAPSelectItems", scope = ScopeType.EVENT)
	public List<SelectItem> getTipoDocF3CAPItems() {
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString(
				"opt_select_one")));
		for (DatosEspecificos o : getTipoDocF3CAP())
			si.add(new SelectItem(o.getIdDatosEspecificos(), ""
					+ o.getDescripcion()));
		return si;
	}

	public List<DatosEspecificos> getTipoDocPlanCapacC() {
		try {
			datosEspecificosList.getDatosGenerales().setDescripcion(
					"TIPOS DE DOCUMENTOS");
			datosEspecificosList.getDatosEspecificos().setValorAlf("PL_CAP");
			datosEspecificosList.setOrderColumn("datosEspecificos.descripcion");
			datosEspecificosList.setMaxResults(null);

			return datosEspecificosList.getResultList();
		} catch (Exception ex) {
			return new Vector<DatosEspecificos>();
		}
	}

	@Factory(value = CONTEXT_VAR_NAME + "ByTipoDocPlanCapacCSelectItems", scope = ScopeType.EVENT)
	public List<SelectItem> getTipoDocPlanCapacCItems() {
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString(
				"opt_select_one")));
		for (DatosEspecificos o : getTipoDocPlanCapacC())
			si.add(new SelectItem(o.getIdDatosEspecificos(), ""
					+ o.getDescripcion()));
		return si;
	}

	public List<DatosEspecificos> getTipoDocPlanCapacEJCAP() {
		try {
			datosEspecificosList.getDatosGenerales().setDescripcion(
					"TIPOS DE DOCUMENTOS");
			datosEspecificosList.getDatosEspecificos().setValorAlf("EJ_CAP");
			datosEspecificosList.setOrderColumn("datosEspecificos.descripcion");
			datosEspecificosList.setMaxResults(null);

			return datosEspecificosList.getResultList();
		} catch (Exception ex) {
			return new Vector<DatosEspecificos>();
		}
	}

	@Factory(value = CONTEXT_VAR_NAME + "ByTipoDocPlanCapacEJCAPSelectItems", scope = ScopeType.EVENT)
	public List<SelectItem> getTipoDocPlanCapacEJCAPItems() {
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString(
				"opt_select_one")));
		for (DatosEspecificos o : getTipoDocPlanCapacEJCAP())
			si.add(new SelectItem(o.getIdDatosEspecificos(), ""
					+ o.getDescripcion()));
		return si;
	}

	public List<DatosEspecificos> getConsultorasCap() {
		try {
			datosEspecificosList.getDatosGenerales().setDescripcion(
					"CONSULTORAS CAPACITACION");
			datosEspecificosList.setOrderColumn("datosEspecificos.descripcion");
			datosEspecificosList.setMaxResults(null);

			return datosEspecificosList.getResultList();
		} catch (Exception ex) {
			return new Vector<DatosEspecificos>();
		}
	}

	@Factory(value = CONTEXT_VAR_NAME + "ByConsultorasCapSelectItems", scope = ScopeType.EVENT)
	public List<SelectItem> getConsultorasCapItems() {
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString(
				"opt_select_one")));
		for (DatosEspecificos o : getConsultorasCap())
			si.add(new SelectItem(o.getIdDatosEspecificos(), ""
					+ o.getDescripcion()));
		return si;
	}

	public List<DatosEspecificos> getDatosEspecificosByNacionalidad() {
		try {
			datosEspecificosList.getDatosGenerales().setDescripcion(
					"NACIONALIDAD");
			datosEspecificosList.setOrderColumn("datosEspecificos.descripcion");
			datosEspecificosList.setMaxResults(null);

			return datosEspecificosList.getResultList();
		} catch (Exception ex) {
			return new Vector<DatosEspecificos>();
		}
	}

	@Factory(value = CONTEXT_VAR_NAME + "ByNacionalidadSelectItems", scope = ScopeType.EVENT)
	public List<SelectItem> getNacionalidadSelectItems() {
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString(
				"opt_select_one")));
		for (DatosEspecificos o : getDatosEspecificosByNacionalidad())
			si.add(new SelectItem(o.getIdDatosEspecificos(), ""
					+ o.getDescripcion()));
		return si;
	}

	public DatosEspecificos getDatosIngresoMovilidad() {
		try {
			datosEspecificosList.getDatosGenerales().setDescripcion(
					"TIPOS DE INGRESOS Y MOVILIDAD");
			datosEspecificosList
					.getDatosEspecificos()
					.setDescripcion(
							"INGRESO DIRECTO DE PERSONAL PERMANENTE A PUESTO DE CONFIANZA O ELECTIVO");
			datosEspecificosList.setOrderColumn("datosEspecificos.descripcion");
			datosEspecificosList.setMaxResults(null);

			return datosEspecificosList.getResultList().get(0);
		} catch (Exception ex) {
			return new DatosEspecificos();
		}
	}

	public DatosEspecificos getDatosIngresoReposicionJudicial() {
		try {
			datosEspecificosList.getDatosGenerales().setDescripcion(
					"TIPOS DE INGRESOS Y MOVILIDAD");
			datosEspecificosList.getDatosEspecificos().setDescripcion(
					"INGRESO DIRECTO PERSONAL REPOSICION JUDICIAL");
			datosEspecificosList.setOrderColumn("datosEspecificos.descripcion");
			datosEspecificosList.setMaxResults(null);

			return datosEspecificosList.getResultList().get(0);
		} catch (Exception ex) {
			return new DatosEspecificos();
		}
	}

	public DatosEspecificos getDatosPermanenteEstadoEmpleado() {
		try {
			datosEspecificosList.getDatosGenerales().setDescripcion(
					"ESTADO EMPLEADO PUESTO");
			datosEspecificosList.getDatosEspecificos().setDescripcion(
					"PERMANENTE");
			datosEspecificosList.setOrderColumn("datosEspecificos.descripcion");
			datosEspecificosList.setMaxResults(null);

			return datosEspecificosList.getResultList().get(0);
		} catch (Exception ex) {
			return new DatosEspecificos();
		}
	}

	public DatosEspecificos getTipoRegistroIngreso() {
		try {
			datosEspecificosList.getDatosGenerales().setDescripcion(
					"TIPO REGISTRO");
			datosEspecificosList.getDatosEspecificos().setDescripcion(
					"INGRESOS");
			datosEspecificosList.setOrderColumn("datosEspecificos.descripcion");
			datosEspecificosList.setMaxResults(null);

			return datosEspecificosList.getResultList().get(0);
		} catch (Exception ex) {
			return new DatosEspecificos();
		}
	}

	public DatosEspecificos getDatosIngresoRenovacionContrato() {
		try {
			datosEspecificosList.getDatosGenerales().setDescripcion(
					"TIPOS DE INGRESOS Y MOVILIDAD");
			datosEspecificosList.getDatosEspecificos().setDescripcion(
					"INGRESO DIRECTO PERSONAL RENOVACION CONTRATO");
			datosEspecificosList.setOrderColumn("datosEspecificos.descripcion");
			datosEspecificosList.setMaxResults(null);

			return datosEspecificosList.getResultList().get(0);
		} catch (Exception ex) {
			return new DatosEspecificos();
		}
	}

	public List<DatosEspecificos> getDatosEspecificosTipoDocIngreso() {
		try {
			datosEspecificosList.getDatosGenerales().setDescripcion(
					"TIPOS DE DOCUMENTOS");
			datosEspecificosList.getDatosEspecificos().setValorAlf("CON");
			datosEspecificosList.setOrderColumn("datosEspecificos.descripcion");
			datosEspecificosList.setMaxResults(null);

			return datosEspecificosList.getResultList();
		} catch (Exception ex) {
			return new Vector<DatosEspecificos>();
		}
	}

	public DatosEspecificos getDatosIngresoPorLeyEmergencia() {
		try {
			datosEspecificosList.getDatosGenerales().setDescripcion(
					"TIPOS DE INGRESOS Y MOVILIDAD");
			datosEspecificosList.getDatosEspecificos().setDescripcion(
					"INGRESO DIRECTO DE PERSONAL POR LEY DE EMERGENCIA");
			datosEspecificosList.setOrderColumn("datosEspecificos.descripcion");
			datosEspecificosList.setMaxResults(null);

			return datosEspecificosList.getResultList().get(0);
		} catch (Exception ex) {
			return new DatosEspecificos();
		}
	}

	public DatosEspecificos getDatosIngresoCargoComplementario() {
		try {
			datosEspecificosList.getDatosGenerales().setDescripcion(
					"TIPOS DE INGRESOS Y MOVILIDAD");
			datosEspecificosList.getDatosEspecificos().setDescripcion(
					"INGRESO DIRECTO PERSONAL CARGO COMPLEMENTARIO");
			datosEspecificosList.setOrderColumn("datosEspecificos.descripcion");
			datosEspecificosList.setMaxResults(null);

			return datosEspecificosList.getResultList().get(0);
		} catch (Exception ex) {
			return new DatosEspecificos();
		}
	}

	public DatosEspecificos getDatosIngresoContrato() {
		try {
			datosEspecificosList.getDatosGenerales().setDescripcion(
					"TIPOS DE DOCUMENTOS");
			datosEspecificosList.getDatosEspecificos().setDescripcion(
					"CONTRATO");
			datosEspecificosList.getDatosEspecificos().setValorAlf("CON");
			datosEspecificosList.setOrderColumn("datosEspecificos.descripcion");
			datosEspecificosList.setMaxResults(null);

			return datosEspecificosList.getResultList().get(0);
		} catch (Exception ex) {
			return new DatosEspecificos();
		}
	}
	
	public List<DatosEspecificos> getDatosPorEtnia() {
		try {
			datosEspecificosList.getDatosGenerales().setDescripcion(
					"ETNIA");
			
			datosEspecificosList.setOrderColumn("datosEspecificos.descripcion");
			datosEspecificosList.setMaxResults(null);

			return datosEspecificosList.getResultList();
		} catch (Exception ex) {
			return null;
		}
	}
	@Factory(value = CONTEXT_VAR_NAME + "ByEtniaSelectItems", scope = ScopeType.EVENT)
	public List<SelectItem> getEtniaSelectItems() {
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString(
				"opt_select_one")));
		for (DatosEspecificos o : getDatosPorEtnia())
			si.add(new SelectItem(o.getIdDatosEspecificos(), ""
					+ o.getDescripcion()));
		return si;
	}
	
	@Factory(value = CONTEXT_VAR_NAME + "ByTipoDocIngresoSelectItems", scope = ScopeType.EVENT)
	public List<SelectItem> getTipoDocIngresoSelectItems() {
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString(
				"opt_select_one")));
		for (DatosEspecificos o : getDatosEspecificosTipoDocIngreso())
			si.add(new SelectItem(o.getIdDatosEspecificos(), ""
					+ o.getDescripcion()));
		return si;
	}

	@Factory(value = CONTEXT_VAR_NAME + "ByTipoPlantillaEvalSelectItems", scope = ScopeType.EVENT)
	public List<SelectItem> getTipoPlantillaEvalSelectItems() {
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString(
				"opt_select_one")));
		for (DatosEspecificos o : getDatosEspecificosTipoPlantillaEval())
			si.add(new SelectItem(o.getIdDatosEspecificos(), ""
					+ o.getDescripcion()));
		return si;
	}

	public List<DatosEspecificos> getDatosEspecificosTipoPlantillaEval() {
		try {
			datosEspecificosList.getDatosGenerales().setDescripcion(
					"MEDOLOGIAS EVALUACION DESEMPENO");
			datosEspecificosList.getDatosEspecificos().setValorAlf("O");
			datosEspecificosList.setOrderColumn("datosEspecificos.descripcion");
			datosEspecificosList.setMaxResults(null);

			return datosEspecificosList.getResultList();
		} catch (Exception ex) {
			return new Vector<DatosEspecificos>();
		}
	}
	@Factory(value = CONTEXT_VAR_NAME + "ByTodasPlantillaEvalSelectItems", scope = ScopeType.EVENT)
	public List<SelectItem> getTodasPlantillaEvalSelectItems() {
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString(
				"opt_select_one")));
//		for (DatosEspecificos o : getDatosEspecificosTodasPlantillaEval())
//			si.add(new SelectItem(o.getIdDatosEspecificos(), ""
//					+ o.getDescripcion()));
//		return si; 
		for (DatosEspecificos o : getDatosEspecificosTodasPlantillaEval()){
			if (!o.getDescripcion().contentEquals("POR CONTRATO DE GESTION") && !o.getDescripcion().contentEquals("RESULTADOS POR AREA") && 
					!o.getDescripcion().contentEquals("RESULTADOS INDIVIDUALES")) {//se optó por mostrar solo las plantillas PDEC y PERCEPCIÓN; Werner.
				si.add(new SelectItem(o.getIdDatosEspecificos(), ""
						+ o.getDescripcion()));
			}
		}
		return si;
	}

	public List<DatosEspecificos> getDatosEspecificosTodasPlantillaEval() {
		try {
			datosEspecificosList.getDatosGenerales().setDescripcion(
					"MEDOLOGIAS EVALUACION DESEMPENO");
			datosEspecificosList.setOrderColumn("datosEspecificos.descripcion");
			datosEspecificosList.setMaxResults(null);

			return datosEspecificosList.getResultList();
		} catch (Exception ex) {
			return new Vector<DatosEspecificos>();
		}
	}
	
	
	public List<DatosEspecificos> getCausaDisc() {
		try {
			datosEspecificosList.getDatosGenerales().setDescripcion(
					"DISCAPACIDAD CAUSA");
			datosEspecificosList.setOrderColumn("datosEspecificos.descripcion");
			datosEspecificosList.setMaxResults(null);

			return datosEspecificosList.getResultList();
		} catch (Exception ex) {
			return new Vector<DatosEspecificos>();
		}
	}

	@Factory(value = CONTEXT_VAR_NAME + "ByCausaDiscSelectItems", scope = ScopeType.EVENT)
	public List<SelectItem> getCausaDiscSelectItems() {
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString(
				"opt_select_one")));
		for (DatosEspecificos o : getCausaDisc())
			si.add(new SelectItem(o.getIdDatosEspecificos(), ""
					+ o.getDescripcion()));
		return si;
	}
	public List<DatosEspecificos> getMotivoAmonestaciones() {
		try {
			datosEspecificosList.getDatosGenerales().setDescripcion(
					"MOTIVO DE AMONESTACION");
			datosEspecificosList.setOrderColumn("datosEspecificos.descripcion");
			datosEspecificosList.setMaxResults(null);

			return datosEspecificosList.getResultList();
		} catch (Exception ex) {
			return new Vector<DatosEspecificos>();
		}
	}

	@Factory(value = CONTEXT_VAR_NAME + "ByMotivoAmonestacionesSelectItems", scope = ScopeType.EVENT)
	public List<SelectItem> getMotivoAmonestacionesSelectItems() {
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString(
				"opt_select_one")));
		for (DatosEspecificos o : getMotivoAmonestaciones())
			si.add(new SelectItem(o.getIdDatosEspecificos(), ""
					+ o.getDescripcion()));
		return si;
	}
	public List<DatosEspecificos> getPremioRec() {
		try {
			datosEspecificosList.getDatosGenerales().setDescripcion(
					"TIPOS DE DOCUMENTOS");
			datosEspecificosList.getDatosEspecificos().setDescripcion(
					"RESOLUCION");
			datosEspecificosList.getDatosEspecificos().setValorAlf("RES");
			datosEspecificosList.setOrderColumn("datosEspecificos.descripcion");
			datosEspecificosList.setMaxResults(null);

			return datosEspecificosList.getResultList();
		} catch (Exception ex) {
			return new Vector<DatosEspecificos>();
		}
	}

	@Factory(value = CONTEXT_VAR_NAME + "ByPremioRecSelectItems", scope = ScopeType.EVENT)
	public List<SelectItem> getPremioRecSelectItems() {
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString(
				"opt_select_one")));
		for (DatosEspecificos o : getPremioRec())
			si.add(new SelectItem(o.getIdDatosEspecificos(), ""
					+ o.getDescripcion()));
		return si;
	}

	public List<DatosEspecificos> getEnfermedades() {
		try {
			datosEspecificosList.getDatosGenerales().setDescripcion(
					"ENFERMEDADES");
			datosEspecificosList.setOrderColumn("datosEspecificos.descripcion");
			datosEspecificosList.setMaxResults(null);

			return datosEspecificosList.getResultList();
		} catch (Exception ex) {
			return new Vector<DatosEspecificos>();
		}
	}

	@Factory(value = CONTEXT_VAR_NAME + "ByEnfermedadesSelectItems", scope = ScopeType.EVENT)
	public List<SelectItem> getEnfermedadesSelectItems() {
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString(
				"opt_select_one")));
		for (DatosEspecificos o : getEnfermedades())
			si.add(new SelectItem(o.getIdDatosEspecificos(), ""
					+ o.getDescripcion()));
		return si;
	}
	
	public List<DatosEspecificos> getDatosEspecificosByTipoDocumentoEnfermedad() {
		try {
			datosEspecificosList.getDatosGenerales().setDescripcion(
					"TIPOS DE DOCUMENTOS");
			datosEspecificosList.getDatosEspecificos().setValorAlf("FPOS");
			datosEspecificosList.getDatosEspecificos().setDescripcion("CERTIFICADO");
			datosEspecificosList.setOrderColumn("datosEspecificos.descripcion");
			datosEspecificosList.setMaxResults(null);

			return datosEspecificosList.getResultList();
		} catch (Exception ex) {
			return new Vector<DatosEspecificos>();
		}
	}

	@Factory(value = CONTEXT_VAR_NAME + "ByTipoDocumentoEnfermedadesSelectItems", scope = ScopeType.EVENT)
	public List<SelectItem> getDatosEspecificosByTipoDocumentoEnfermedadesSelectItems() {
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString(
				"opt_select_one")));
		for (DatosEspecificos o : getDatosEspecificosByTipoDocumentoEnfermedad())
			si.add(new SelectItem(o.getIdDatosEspecificos(), ""
					+ o.getDescripcion()));
		return si;
	}

	
	public void setDatosEspecificosIdDatosEspecificos(Long id) {
		setId(id);
	}

	public Long getDatosEspecificosIdDatosEspecificos() {
		return (Long) getId();
	}

	@Override
	protected DatosEspecificos createInstance() {
		DatosEspecificos datosEspecificos = new DatosEspecificos();
		return datosEspecificos;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		DatosGenerales datosGenerales = datosGeneralesHome.getDefinedInstance();
		if (datosGenerales != null) {
			getInstance().setDatosGenerales(datosGenerales);
		}
	}

	public boolean isWired() {
		if (getInstance().getDatosGenerales() == null)
			return false;
		return true;
	}

	public DatosEspecificos getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

}
