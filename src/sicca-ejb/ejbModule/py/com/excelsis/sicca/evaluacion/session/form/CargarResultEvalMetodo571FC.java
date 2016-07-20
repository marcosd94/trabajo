package py.com.excelsis.sicca.evaluacion.session.form;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.security.AuthorizationException;
import org.richfaces.model.UploadItem;

import py.com.excelsis.sicca.entity.ComisionCapacEval;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.Documentos;
import py.com.excelsis.sicca.entity.EvaluacionDesempeno;
import py.com.excelsis.sicca.entity.GrupoMetodologia;
import py.com.excelsis.sicca.entity.GruposSujetos;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.PlanMejora;
import py.com.excelsis.sicca.entity.PlantillaEvalConfDet;
import py.com.excelsis.sicca.entity.PostulacionCapAdj;
import py.com.excelsis.sicca.entity.ResultadoEval;
import py.com.excelsis.sicca.entity.ResultadoEvalComision;
import py.com.excelsis.sicca.entity.ResultadoEvalPlan;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.form.AdmDocAdjuntoFormController;
import py.com.excelsis.sicca.session.util.JbpmUtilFormController;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.ReferenciasUtilFormController;
import py.com.excelsis.sicca.session.util.ReportUtilFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("cargarResultEvalMetodo571FC")
public class CargarResultEvalMetodo571FC {

	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(create = true)
	JbpmUtilFormController jbpmUtilFormController;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(required = false, create = true)
	SeguridadUtilFormController seguridadUtilFormController;
	@In(create = true)
	ReferenciasUtilFormController referenciasUtilFormController;
	@In(required = false, create = true)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;
	@In(required = false, create = true)
	SeleccionUtilFormController seleccionUtilFormController;

	@In(create = true)
	ReportUtilFormController reportUtilFormController;

	@In(create = true)
	AdmDocAdjuntoFormController admDocAdjuntoFormController;

	private String grupo;
	private String plantilla;
	private String equipo;
	private List<ResultadoEval> lGrilla1;
	private List<GruposSujetos> lGrillaIndividual;
	private List<ResultadoEvalComision> lGrilla2;
	private List<ResultadoEvalComision> lGrilla2Eliminar;
	private List<GruposSujetos> lGrilla3;
	private String comision;
	private String evalTitle;
	private String elFrom;
	private Long idGrupoMetodologia;
	private Long idComision;
	private Integer idRefEvaluacionEnCurso;
	private Integer idRefEvaluacionFinalizada;
	private GrupoMetodologia grupoMetodologia;
	private List<PlanMejora> planMejoraBase;
	private String direccionFisica;
	private Long idTipoDocumento;
	private byte[] uFile1 = null;
	private String cType1 = null;
	private String fName1 = null;
	private Long idGruposSujetos;
	private Boolean mostrarModal = false;
	private String promedio;
	private String nombrePantalla;
	
//	agregado; Werner.
	private String estado;

	public void init() {
		if (!precondInit())
			throw new AuthorizationException(SeamResourceBundle.getBundle().getString("seam_authorization_exception"));

		cargarCabecera();
		
		if (grupoMetodologia.getTipo().equalsIgnoreCase("O")
			&& grupoMetodologia.getValor().equalsIgnoreCase("G")) {
			initIDs();
			suPrimeraVez();
			cargarGrilla1();
			cargarGrilla2();
			cargarGrilla3();
		} else {
			throw new AuthorizationException(SeamResourceBundle.getBundle().getString("seam_authorization_exception"));
		}
	}

	private String generarDireFisica() {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy");

		String respuesta = "";
		String cSeparador = File.separator;
		respuesta =
			"SICCA"
				+ cSeparador
				+ sdf.format(new Date())
				+ "OEE"
				+ cSeparador
				+ usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo()
				+ "E"
				+ cSeparador
				+ grupoMetodologia.getGruposEvaluacion().getEvaluacionDesempeno().getIdEvaluacionDesempeno();
		return respuesta;
	}

	public String genPlanMejora(Long idRE) {
		Query q =
			em.createQuery("select ResultadoEvalPlan from ResultadoEvalPlan ResultadoEvalPlan"
				+ " where ResultadoEvalPlan.activo is true and ResultadoEvalPlan.resultadoEval.idResultadoEval = :idRE ");
		q.setParameter("idRE", idRE);
		List<ResultadoEvalPlan> lista = q.getResultList();
		String respuesta = "";
		for (ResultadoEvalPlan o : lista) {
			
			PlanMejora pm = em.find(PlanMejora.class, o.getPlanMejora().getIdPlanMejora());
			respuesta += "," + pm.getDescripcion();
		}
		respuesta = respuesta.replaceFirst(",", "");
		return respuesta;
	}

	/* Valor Individual */
	private Boolean precondAdjuntarDoc() {

		if (idTipoDocumento == null || uFile1 == null) {
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("GENERICO_CAMPOS_REQUERIDOS"));
			return false;
		}
		if (idGruposSujetos == null) {
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("GENERICO_NO_MSG"));
			return false;
		}
		return true;
	}

	public void habilitarModal(Boolean val) {
		mostrarModal = val;

	}

	public void descargarDocumento(Long idDocumento) {
		try {
			AdmDocAdjuntoFormController.abrirDocumentoFromCU(idDocumento, usuarioLogueado.getIdUsuario());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void adjuntarDocumento() {

		try {
			if (!precondAdjuntarDoc())
				return;

			GruposSujetos gruposSujetos = em.find(GruposSujetos.class, idGruposSujetos);

			UploadItem fileItem =
				seleccionUtilFormController.crearUploadItem(fName1, uFile1.length, cType1, uFile1);
			Long idDocuGenerado;
			String nombreTabla = "GruposSujetos";
			String direccionFisica = "";
			EvaluacionDesempeno ed =
				grupoMetodologia.getGruposEvaluacion().getEvaluacionDesempeno();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy");

			String cSeparador = File.separator;
			direccionFisica =
				"SICCA" + cSeparador + sdf.format(new Date()) + "OEE" + cSeparador
					+ ed.getConfiguracionUoCab().getIdConfiguracionUo()
					+ cSeparador + "E"
					+ cSeparador + ed.getIdEvaluacionDesempeno();
			Long idDocAnterior =
				(gruposSujetos.getDocumentos() != null
					? gruposSujetos.getDocumentos().getIdDocumento() : null);
			idDocuGenerado =
				admDocAdjuntoFormController.guardarDoc(fileItem, direccionFisica, nombrePantalla, idTipoDocumento, nombreTabla, idDocAnterior);
			if (idDocuGenerado != null) {
				gruposSujetos.setDocumentos(new Documentos());
				gruposSujetos.getDocumentos().setIdDocumento(idDocuGenerado);
				gruposSujetos.setFechaMod(new Date());
				gruposSujetos.setUsuMod(usuarioLogueado.getCodigoUsuario());
				gruposSujetos = em.merge(gruposSujetos);

				Documentos doc = em.find(Documentos.class, idDocuGenerado);
				doc.setIdTabla(gruposSujetos.getIdGrupoSujeto());
				doc.setUsuMod(usuarioLogueado.getCodigoUsuario());
				doc.setFechaMod(new Date());
				em.merge(doc);
			}

			em.flush();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("GENERICO_NO_MSG"));
		} finally {
			idGruposSujetos = null;
			mostrarModal = false;
		}

	}
	
	public void eliminandoConstancia(Long idSujeto){
		GruposSujetos gruposSujetos = em.find(GruposSujetos.class, idSujeto);
		Long aux = gruposSujetos.getDocumentos().getIdDocumento();
		gruposSujetos.setDocumentos(null);
		em.merge(gruposSujetos);
		em.flush();
		borrandoRegistroDocumento(aux);
	}
	
	private void borrandoRegistroDocumento(Long idDocumento){
		Documentos constanciaDoc = em.find(Documentos.class, idDocumento);
		em.remove(constanciaDoc);em.flush();
	}

	public void initIndividual() {
		if (!precondInit())
			throw new AuthorizationException(SeamResourceBundle.getBundle().getString("seam_authorization_exception"));

		cargarCabecera();
		
		if (grupoMetodologia.getTipo().equalsIgnoreCase("O")
			&& grupoMetodologia.getValor().equalsIgnoreCase("I")) {
			initIDs();
			suPrimeraVez();
			cargarGrillaIndividual();
			cargarGrilla2();

//			agregado para adjuntar documento sin mezclar CONTRATO DE GESTION e INDIVIDUAL; Werner.
			if (plantilla.contentEquals("POR CONTRATO DE GESTION"))
				nombrePantalla = "cargarResultEvalMetodo571Gestion";
			else
				nombrePantalla = "cargarResultEvalMetodo571";
//			*********************************************************
		} else {
			throw new AuthorizationException(SeamResourceBundle.getBundle().getString("seam_authorization_exception"));
		}

	}

	private void cargarGrillaIndividual() {

		Query q =
			em.createQuery("select GruposSujetos from GruposSujetos GruposSujetos "
				+ " where GruposSujetos.gruposEvaluacion.idGruposEvaluacion = :idGE   ");
		q.setParameter("idGE", grupoMetodologia.getGruposEvaluacion().getIdGruposEvaluacion());
		lGrillaIndividual = q.getResultList();
		// Inicializar Plan de Mejora
		initPlanMejoraIndividual();

	}

	public Float obtenerPuntajeFinal(List<ResultadoEval> lista) {
		float puntajeFinal = 0;
		int cantidadElems = 0;
		if (lista == null)
			return puntajeFinal;
		for (ResultadoEval o : lista) {
			if (o.getPuntaje() != null) {
				cantidadElems++;
				puntajeFinal += o.getPuntaje();
			}
		}
		if (cantidadElems == 0)
			return new Float("0");
		puntajeFinal = puntajeFinal / cantidadElems;
		return puntajeFinal;
	}

	public List<ResultadoEval> traerDetalleGrilla3(Long idGrupoSujeto) {
//		Query q =
//			em.createQuery("select ResultadoEval from ResultadoEval ResultadoEval where ResultadoEval.gruposSujetos.idGrupoSujeto = :idGS ");
//		q.setParameter("idGS", idGrupoSujeto);
//		return q.getResultList();
		//Se modificó el query por el de abajo, para evitar conflictos entre las planillas Percepción e Individual; Werner.
		Query q =
				em.createQuery("select ResultadoEval from ResultadoEval ResultadoEval where ResultadoEval.gruposSujetos.idGrupoSujeto = :idGS "
						+ "and ResultadoEval.grupoMetodologia.idGrupoMetodologia = :idGM");
			q.setParameter("idGS", idGrupoSujeto);
			q.setParameter("idGM", idGrupoMetodologia);
			return q.getResultList();
	}

	public String saveIndividual() {
		if (!precondSaveIndividual())
			return "FAIL";
		try {
			Iterator<GruposSujetos> iter = lGrillaIndividual.iterator();
			Iterator<ResultadoEval> iter2;
			while (iter.hasNext()) {
				GruposSujetos gs = iter.next();
				List<ResultadoEval> lDetalle = traerDetalleGrilla3(gs.getIdGrupoSujeto());
//				if (gs.getPresente() == null) {
//					gs.setPresente(false);
//				}todos presentes; Werner.
				gs.setPresente(true);
				
				gs.setUsuMod(usuarioLogueado.getCodigoUsuario());
				gs.setFechaMod(new Date());
				gs.setPuntajeFinal(obtenerPuntajeFinal(lDetalle));
				gs = em.merge(gs);
				iter2 = lDetalle.iterator();
				while (iter2.hasNext()) {
					ResultadoEval p = iter2.next();
					p.setUsuMod(usuarioLogueado.getCodigoUsuario());
					p.setFechaMod(new Date());
					p = em.merge(p);
					/* Actualizar ResultadoEvalPlan */
					actualizarPlanMejora(p);
				}
			}
			// Guardar comision
			guardarComision();
			em.flush();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
			return "OK";
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("GENERICO_NO_MSG"));
		}
		return null;

	}

	private List<DatosEspecificos> traerTipoDoc() {
		Query q =
			em.createQuery("select DatosEspecificos from DatosEspecificos DatosEspecificos "
				+ " where DatosEspecificos.datosGenerales.descripcion = :dG "
				+ " and DatosEspecificos.activo is true and DatosEspecificos.valorAlf = :dE order by DatosEspecificos.descripcion ");
		q.setParameter("dG", "TIPOS DE DOCUMENTOS");
		q.setParameter("dE", "EVAL_R");
		//*****************************************************************************
		//agregado; Werner.
		nombrePantalla="eval_r";
		//*****************************************************************************
		return q.getResultList();
	}

	public List<SelectItem> traerTipoDocumentoSI() {
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, "Seleccione un Tipo de Documento"));
		for (DatosEspecificos o : traerTipoDoc())
			si.add(new SelectItem(o.getIdDatosEspecificos(), o.getDescripcion()));
		return si;
	}

	/******************/
	private void cargarGrilla1() {
		Query q =
			em.createQuery("select ResultadoEval from ResultadoEval ResultadoEval "
				+ " where ResultadoEval.grupoMetodologia.idGrupoMetodologia = :idGM");
		q.setParameter("idGM", idGrupoMetodologia);
		lGrilla1 = q.getResultList();
		// Inicializar Plan de Mejora
		initPlanMejora();
	}

	private void initPlanMejoraIndividual() {
		Query q = null;
		Iterator<ResultadoEval> iter = null;
		for (GruposSujetos o : lGrillaIndividual) {
			List<ResultadoEval> lDetalle = traerDetalleGrilla3(o.getIdGrupoSujeto());
			iter = lDetalle.iterator();
			while (iter.hasNext()) {
				ResultadoEval resultadoEval = iter.next();
				q =
					em.createQuery("select ResultadoEvalPlan from ResultadoEvalPlan ResultadoEvalPlan"
						+ " where ResultadoEvalPlan.activo is true and ResultadoEvalPlan.resultadoEval.idResultadoEval = :idRE ");
				q.setParameter("idRE", resultadoEval.getIdResultadoEval());
				List<ResultadoEvalPlan> lista = q.getResultList();
				resultadoEval.setlPM(traerPlanMejoraBase());
				for (ResultadoEvalPlan p : lista) {
					Iterator<PlanMejora> iter2 = resultadoEval.getlPM().iterator();
					while (iter2.hasNext()) {
						PlanMejora planMejora = iter2.next();
						if (p.getPlanMejora().getIdPlanMejora().longValue() == planMejora.getIdPlanMejora().longValue()) {
							planMejora.setSelected(true);
							break;
						}
					}
				}
			}
		}
	}

	private void initPlanMejora() {
		Query q = null;
		Iterator<ResultadoEval> iter = lGrilla1.iterator();

		while (iter.hasNext()) {
			ResultadoEval resultadoEval = iter.next();
			q =
				em.createQuery("select ResultadoEvalPlan from ResultadoEvalPlan ResultadoEvalPlan"
					+ " where ResultadoEvalPlan.activo is true and ResultadoEvalPlan.resultadoEval.idResultadoEval = :idRE ");
			q.setParameter("idRE", resultadoEval.getIdResultadoEval());
			List<ResultadoEvalPlan> lista = q.getResultList();
			resultadoEval.setlPM(traerPlanMejoraBase());
			for (ResultadoEvalPlan p : lista) {
				Iterator<PlanMejora> iter2 = resultadoEval.getlPM().iterator();
				while (iter2.hasNext()) {
					PlanMejora planMejora = iter2.next();
					if (p.getPlanMejora().getIdPlanMejora().longValue() == planMejora.getIdPlanMejora().longValue()) {
						planMejora.setSelected(true);
						break;
					}
				}
			}
		}
	}

	public List<PlanMejora> traerPlanMejoraBase() {
		if (planMejoraBase == null) {
			Query q =
				em.createQuery("select PlanMejora from PlanMejora PlanMejora "
					+ " where PlanMejora.activo is true and PlanMejora.configuracionUo.idConfiguracionUo = :idUo ");
			q.setParameter("idUo", usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo());
			planMejoraBase = q.getResultList();
		}
		List<PlanMejora> respuesta = new ArrayList<PlanMejora>();
		for (PlanMejora o : planMejoraBase) {
			PlanMejora pm = new PlanMejora();
			pm.setIdPlanMejora(o.getIdPlanMejora());
			pm.setSelected(false);
			pm.setDescripcion(o.getDescripcion());
			respuesta.add(pm);
		}
		return respuesta;
	}

	public void cargarGrilla2() {
		Query q =
			em.createQuery("select ResultadoEvalComision from ResultadoEvalComision ResultadoEvalComision "
				+ " where ResultadoEvalComision.activo is true and ResultadoEvalComision.grupoMetodologia.idGrupoMetodologia = :idGM");
		q.setParameter("idGM", idGrupoMetodologia);
		if (lGrilla2Eliminar != null)
			lGrilla2Eliminar.clear();
		lGrilla2 = q.getResultList();
	}

	private void cargarGrilla3() {
		Query q =
			em.createQuery("select GruposSujetos from GruposSujetos GruposSujetos "
				+ " where GruposSujetos.activo is true and GruposSujetos.gruposEvaluacion.idGruposEvaluacion = :idGE");
		q.setParameter("idGE", grupoMetodologia.getGruposEvaluacion().getIdGruposEvaluacion());
		lGrilla3 = q.getResultList();
	}

	public void cargarCabecera() {

		nivelEntidadOeeUtil.setIdConfigCab(usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo());
		nivelEntidadOeeUtil.setIdUnidadOrganizativa(usuarioLogueado.getConfiguracionUoDet().getIdConfiguracionUoDet());
		nivelEntidadOeeUtil.init2();
		comision = grupoMetodologia.getGruposEvaluacion().getComisionEval().getNombre();
		evalTitle = grupoMetodologia.getGruposEvaluacion().getEvaluacionDesempeno().getTitulo();
		plantilla = grupoMetodologia.getDatosEspecifMetod().getDescripcion();
		grupo = grupoMetodologia.getGruposEvaluacion().getGrupo();
		equipo = generarEquipo();
		direccionFisica = generarDireFisica();
	}

	private String generarEquipo() {
		Query q =
			em.createQuery("select GruposSujetos from GruposSujetos GruposSujetos "
				+ " where  GruposSujetos.gruposEvaluacion.idGruposEvaluacion = :idGE and GruposSujetos.activo is true ");
		q.setParameter("idGE", grupoMetodologia.getGruposEvaluacion().getIdGruposEvaluacion());
		List<GruposSujetos> lista = q.getResultList();
		String resultado = "";
		for (GruposSujetos o : lista) {
			Persona persona = o.getSujetos().getEmpleadoPuesto().getPersona();
			resultado +=
				"," + persona.getDocumentoIdentidad() + "-" + persona.getNombres() + " "
					+ persona.getApellidos();
		}
		resultado = resultado.replaceFirst(",", "");
		return resultado;

	}

	private List<ComisionCapacEval> traerIntegrantes() {
		String sql =
			""
				+ "SELECT det.* FROM capacitacion.comision_capac_eval DET JOIN eval_desempeno.comision_eval "
				+ " CAB ON CAB.id_comision_eval = DET.id_comision_eval JOIN general.persona P ON "
				+ " P.id_persona = DET.id_persona JOIN eval_desempeno.grupos_evaluacion G ON G.id_comision_eval "
				+ " = DET.id_comision_eval WHERE G.id_grupos_evaluacion = :idGE  "
				+ "ORDER BY P.nombres ";
		Query q = em.createNativeQuery(sql, ComisionCapacEval.class);
		q.setParameter("idGE", grupoMetodologia.getGruposEvaluacion().getIdGruposEvaluacion());
		return q.getResultList();
	}

	public List<SelectItem> traerIntegrantesSI() {
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, "Seleccione un Integrante"));
		for (ComisionCapacEval o : traerIntegrantes())
			si.add(new SelectItem(o.getIdComision(), o.getPersona().getNombres() + " "
				+ o.getPersona().getApellidos() + " - " + o.getPersona().getDocumentoIdentidad()));
		return si;
	}

	private Boolean precondInit() {
		if (idGrupoMetodologia != null) {
			grupoMetodologia = em.find(GrupoMetodologia.class, idGrupoMetodologia);//Se deshabilitó condición UO; Werner.
//			if (usuarioLogueado.getConfiguracionUoDet().getIdConfiguracionUoDet().longValue() == grupoMetodologia.getGruposEvaluacion().getEvaluacionDesempeno().getConfiguracionUoDet().getIdConfiguracionUoDet().longValue()) {
				return true;
//			}
		}
		return false;
	}

	private void limpiar() {
		if (lGrilla2Eliminar != null)
			lGrilla2Eliminar.clear();
	}

	public void eliminarIntegrante(int index) {
		if (lGrilla2Eliminar == null)
			lGrilla2Eliminar = new ArrayList<ResultadoEvalComision>();
		if (lGrilla2 != null && index < lGrilla2.size()) {
			if (lGrilla2.get(index).getIdResultadoEvalComision() != null) {
				lGrilla2Eliminar.add(lGrilla2.get(index));
			}
			lGrilla2.remove(index);
		}
	}

	private Boolean precondAddIntegrante() {
		if (idComision == null) {
			statusMessages.add(Severity.ERROR, "Debe seleccionar un Integrante");
			return false;
		}
		if (idGrupoMetodologia == null) {
			statusMessages.add(Severity.ERROR, "Debe seleccionar un Grupo de Metodología");
			return false;
		}
		return true;
	}

	private Boolean integrantePresente(Long idPersona) {
		for (ResultadoEvalComision o : lGrilla2) {
			if (o.getComisionCapacEval().getPersona().getIdPersona().longValue() == idPersona.longValue()) {
				return true;
			}
		}
		return false;
	}

	public void addIntegrante() {
		if (lGrilla2 == null)
			lGrilla2 = new ArrayList<ResultadoEvalComision>();
		if (!precondAddIntegrante())
			return;
		ComisionCapacEval comisionCapacEval = em.find(ComisionCapacEval.class, idComision);
		if (!integrantePresente(comisionCapacEval.getPersona().getIdPersona())) {
			ResultadoEvalComision rec = new ResultadoEvalComision();
			rec.setActivo(true);
			rec.setFechaAlta(new Date());
			rec.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			rec.setComisionCapacEval(comisionCapacEval);
			rec.setGrupoMetodologia(new GrupoMetodologia());
			rec.getGrupoMetodologia().setIdGrupoMetodologia(idGrupoMetodologia);
			lGrilla2.add(rec);

		} else {
			statusMessages.add(Severity.ERROR, "El Integrante ya se encuentra en la lista de seleccionados");
		}
	}

	private Boolean precondFinalizar(String tipoMet) {
		Query q =
			em.createQuery("select count(GruposSujetos) from GruposSujetos GruposSujetos "
				+ " where GruposSujetos.presente is not null "
				+ " and GruposSujetos.activo is true "
				+ " and GruposSujetos.gruposEvaluacion.idGruposEvaluacion = :idGE");
		q.setParameter("idGE", grupoMetodologia.getGruposEvaluacion().getIdGruposEvaluacion());
		if (((Long) q.getSingleResult()).intValue() == 0) {
			statusMessages.add(Severity.ERROR, "Debe cargar previamente el resultado de la evaluación antes de realizar esta acción. Verifique");
			return false;
		}
		
		//agregado; Werner.
		if (tipoMet.contentEquals("P")) {
			Query q2 =
					em.createQuery("select GruposSujetos from GruposSujetos GruposSujetos "
						+ " where GruposSujetos.activo is true "
						+ " and GruposSujetos.puntajePdec is null "
						+ " and GruposSujetos.gruposEvaluacion.idGruposEvaluacion = :idGE");
				q2.setParameter("idGE", grupoMetodologia.getGruposEvaluacion().getIdGruposEvaluacion());
				
			if(!q2.getResultList().isEmpty()){
				statusMessages.add(Severity.ERROR, "Debe cargar previamente los resultados de todos los funcionarios antes de realizar esta acción. Verifique");
				return false;
			}
		}
		if (tipoMet.contentEquals("A")) {
			Query q2 =
					em.createQuery("select GruposSujetos from GruposSujetos GruposSujetos "
						+ " where GruposSujetos.activo is true "
						+ " and GruposSujetos.puntajePercepcion is null "
						+ " and GruposSujetos.gruposEvaluacion.idGruposEvaluacion = :idGE");
				q2.setParameter("idGE", grupoMetodologia.getGruposEvaluacion().getIdGruposEvaluacion());
				
			if(!q2.getResultList().isEmpty()){
				statusMessages.add(Severity.ERROR, "Debe cargar previamente los resultados de todos los funcionarios antes de realizar esta acción. Verifique");
				return false;
			}
		}
		guardarComision();
		if (lGrilla2 == null|| lGrilla2.size() == 0) {//agregado
			statusMessages.add(Severity.ERROR, "Debe existir por lo menos un integrante registrado");
			return false;
		}
		//*****************************************
		
//*******el usuario tiene que adjuntar documento Percepción y/o PDEC; Werner.
		/*plantilla = grupoMetodologia.getDatosEspecifMetod().getDescripcion();
		String query="";
		if (plantilla.contentEquals("PERCEPCION")) 
			query = "select * from general.documentos where documentos.activo is true and documentos.id_tabla="
					+ grupoMetodologia.getIdGrupoMetodologia() + " and nombre_pantalla = 'cargarResultEvalPerce608FC'";
		if (plantilla.contentEquals("PDEC")) 
			query = "select * from general.documentos where documentos.activo is true and documentos.id_tabla="
					+ grupoMetodologia.getIdGrupoMetodologia() + " and nombre_pantalla = 'cargarResultEvalPDEC572FC'";
		
		List<Documentos> documentoDTOList = new ArrayList<Documentos>();
		documentoDTOList = em.createNativeQuery(query, Documentos.class)
			.getResultList();
			
		if (documentoDTOList.isEmpty()) {
			statusMessages.add(Severity.ERROR, "Debe adjuntar documento "+plantilla+".");
			return false;
		}*/
//***************************************************************************************
		
		return true;
	}

	private Boolean precondSaveIndividual() {
		if (lGrillaIndividual == null) {
			statusMessages.add(Severity.ERROR, "No hay nada que guardar");
			return false;
		}
		for (GruposSujetos cab : lGrillaIndividual) {
			List<ResultadoEval> lDetalle = traerDetalleGrilla3(cab.getIdGrupoSujeto());
			for (ResultadoEval o : lDetalle) {
				
				if (o.getGruposSujetos().getPresente() != null
					/*&& o.getGruposSujetos().getPresente()*/) {
					if (o.getPuntaje() == null && grupoMetodologia.getValor().equalsIgnoreCase("G")) {
						statusMessages.add(Severity.ERROR, "El campo Puntaje es requerido");
						return false;
					}
//					agregado en este punto para evitar errores desde el formulario 
//					"Cargar Resultados Evaluación Metodología POR CONTRATO DE GESTIÓN" puntaje en nulo; Werner.
					if (o.getPuntaje() == null && grupoMetodologia.getValor().equalsIgnoreCase("I")) {
						statusMessages.add(Severity.ERROR, "El campo Puntaje es requerido");
						return false;
					}
					
					if (o.getPuntaje() != null && o.getPuntaje().intValue() < 0
						|| o.getPuntaje().intValue() > 10) {
						statusMessages.add(Severity.ERROR, "Puntaje fuera de rango. El puntaje debe estar entre 0-10, inclusive");
						return false;
					}
					if (grupoMetodologia.getValor().equalsIgnoreCase("I")) {
//						if (o.getGruposSujetos().getPresente()) {no es necesario, todos presentes; Werner.
							if (o.getPuntaje() == null) {
								statusMessages.add(Severity.ERROR, "El campo Puntaje es requerido");
								return false;
							} else if ((o.getJustificacion() == null || o.getJustificacion().trim().isEmpty())
								&& ((o.getPuntaje().intValue() >= 0 && o.getPuntaje().intValue() <= 3) || o.getPuntaje().intValue() >= 7
									&& o.getPuntaje().intValue() <= 10)) {
								statusMessages.add(Severity.ERROR, "El campo Justificación es requerido cuando el puntaje está entre 0-3 ó 7 y 10");
								return false;
							}
							if (o.getPuntaje().intValue() < 0 && o.getPuntaje().intValue() > 10) {
								statusMessages.add(Severity.ERROR, "Puntaje fuera de rango. El puntaje debe estar entre 0-10, inclusive");
								return false;
							}
//						}
					} else {

						if ((o.getJustificacion() == null || o.getJustificacion().trim().isEmpty())
							&& ((o.getPuntaje().intValue() >= 0 && o.getPuntaje().intValue() <= 3) || o.getPuntaje().intValue() >= 7
								&& o.getPuntaje().intValue() <= 10)) {
							statusMessages.add(Severity.ERROR, "El campo Justificación es requerido cuando el puntaje está entre 0-3 ó 7 y 10");
							return false;
						}
					}

				}
			}

		}

		if (lGrilla2 == null || lGrilla2.size() == 0) {
			statusMessages.add(Severity.ERROR, "Debe existir por lo menos un integrante registrado");
			return false;
		}
		return true;
	}

	private Boolean precondSave() {
		if (lGrilla1 != null) {
			for (ResultadoEval o : lGrilla1) {
				if (o.getPuntaje() == null && grupoMetodologia.getValor().equalsIgnoreCase("G")) {
					statusMessages.add(Severity.ERROR, "El campo Puntaje es requerido");
					return false;
				}
				if (o.getPuntaje() != null && o.getPuntaje().intValue() < 0
					|| o.getPuntaje().intValue() > 10) {
					statusMessages.add(Severity.ERROR, "Puntaje fuera de rango. El puntaje debe estar entre 0-10, inclusive");
					return false;
				}
				if (grupoMetodologia.getValor().equalsIgnoreCase("I")) {
					if (o.getGruposSujetos().getPresente()) {
						if (o.getPuntaje() == null) {
							statusMessages.add(Severity.ERROR, "El campo Puntaje es requerido");
							return false;
						} else if ((o.getJustificacion() == null || o.getJustificacion().trim().isEmpty())
							&& ((o.getPuntaje().intValue() >= 0 && o.getPuntaje().intValue() <= 3) || o.getPuntaje().intValue() >= 7
								&& o.getPuntaje().intValue() <= 10)) {
							statusMessages.add(Severity.ERROR, "El campo Justificación es requerido cuando el puntaje está entre 0-3 ó 7 y 10");
							return false;
						}
						if (o.getPuntaje().intValue() < 0 && o.getPuntaje().intValue() > 10) {
							statusMessages.add(Severity.ERROR, "Puntaje fuera de rango. El puntaje debe estar entre 0-10, inclusive");
							return false;
						}
					}
				} else {

					if ((o.getJustificacion() == null || o.getJustificacion().trim().isEmpty())
						&& ((o.getPuntaje().intValue() >= 0 && o.getPuntaje().intValue() <= 3) || o.getPuntaje().intValue() >= 7
							&& o.getPuntaje().intValue() <= 10)) {
						statusMessages.add(Severity.ERROR, "El campo Justificación es requerido cuando el puntaje está entre 0-3 ó 7 y 10");
						return false;
					}
				}
			}
		}
		if (lGrilla2 == null || lGrilla2.size() == 0) {
			statusMessages.add(Severity.ERROR, "Debe existir por lo menos un integrante registrado");
			return false;
		}
		return true;
	}

	public List<PlantillaEvalConfDet> plantillaEvalConfDetFirstTime() {
		Query q =
			em.createQuery("select PlantillaEvalConfDet from PlantillaEvalConfDet PlantillaEvalConfDet "
				+ " where PlantillaEvalConfDet.activo is true and PlantillaEvalConfDet.grupoMetodologia.idGrupoMetodologia = :idGM ");
		q.setParameter("idGM", grupoMetodologia.getIdGrupoMetodologia());
		return q.getResultList();
	}

	public List<GruposSujetos> grupoSujetFirstTime() {
		Query q =
			em.createQuery("select GruposSujetos from GruposSujetos GruposSujetos "
				+ " where GruposSujetos.activo is true and GruposSujetos.gruposEvaluacion.idGruposEvaluacion = :idGE ");
		q.setParameter("idGE", grupoMetodologia.getGruposEvaluacion().getIdGruposEvaluacion());
		return q.getResultList();
	}

	public Boolean detectarPrimeraVez() {

		Query q =
			em.createQuery("select count(ResultadoEval) from  ResultadoEval ResultadoEval "
				+ " where  ResultadoEval.grupoMetodologia.idGrupoMetodologia = :idGM ");
		q.setParameter("idGM", idGrupoMetodologia);
		if (((Long) q.getSingleResult()).intValue() == 0)
			return true;

		return false;
	}

	private void suPrimeraVez() {
		if (detectarPrimeraVez()) {
			if (grupoMetodologia.getValor().equalsIgnoreCase("I")) {
				List<PlantillaEvalConfDet> listaPECD = plantillaEvalConfDetFirstTime();
				for (GruposSujetos o : grupoSujetFirstTime()) {
					for (PlantillaEvalConfDet p : listaPECD) {
						ResultadoEval re = new ResultadoEval();
						re.setFechaAlta(new Date());
						re.setUsuAlta(usuarioLogueado.getCodigoUsuario());
						re.setGruposSujetos(new GruposSujetos());
						re.getGruposSujetos().setIdGrupoSujeto(o.getIdGrupoSujeto());
						re.setGrupoMetodologia(new GrupoMetodologia());
						re.getGrupoMetodologia().setIdGrupoMetodologia(idGrupoMetodologia);
						re.setPlantillaEvalConfDet(new PlantillaEvalConfDet());
						re.getPlantillaEvalConfDet().setIdPlantillaEvalConfDet(p.getIdPlantillaEvalConfDet());
						em.persist(re);
					}
				}
			} else if (grupoMetodologia.getValor().equalsIgnoreCase("G")) {
				List<PlantillaEvalConfDet> listaPECD = plantillaEvalConfDetFirstTime();
				for (PlantillaEvalConfDet p : listaPECD) {
					ResultadoEval re = new ResultadoEval();
					re.setFechaAlta(new Date());
					re.setUsuAlta(usuarioLogueado.getCodigoUsuario());
					re.setGrupoMetodologia(new GrupoMetodologia());
					re.getGrupoMetodologia().setIdGrupoMetodologia(idGrupoMetodologia);
					re.setPlantillaEvalConfDet(new PlantillaEvalConfDet());
					re.getPlantillaEvalConfDet().setIdPlantillaEvalConfDet(p.getIdPlantillaEvalConfDet());
					em.persist(re);
				}
			}
			grupoMetodologia.setEstado(idRefEvaluacionEnCurso);
			grupoMetodologia.setFechaMod(new Date());
			grupoMetodologia.setUsuMod(usuarioLogueado.getCodigoUsuario());
			em.persist(grupoMetodologia);
			em.flush();
		}
	}

	public void initIDs() {
		idRefEvaluacionEnCurso =
			seleccionUtilFormController.getIdEstadosReferencia("ESTADOS_EVALUACION_GRUPO", "EVALUACION EN CURSO");
		idRefEvaluacionFinalizada =
			seleccionUtilFormController.getIdEstadosReferencia("ESTADOS_EVALUACION_GRUPO", "EVALUACION FINALIZADA");
	}

	public void guardarComision() {
		// Crear lso nuevos
		for (ResultadoEvalComision o : lGrilla2) {
			if (o.getIdResultadoEvalComision() == null)
				em.persist(o);
		}
		// Inactivar los eliminados
		if (lGrilla2Eliminar != null)
			for (ResultadoEvalComision o : lGrilla2Eliminar) {
				if (o.getIdResultadoEvalComision() != null) {
					o.setFechaMod(new Date());
					o.setUsuMod(usuarioLogueado.getCodigoUsuario());
					o.setActivo(false);
					em.merge(o);
				}
			}
	}

	private void actualizarGruposSujetos() {
		for (GruposSujetos o : lGrilla3) {
			if (o.getPresente() == null) {
				o.setPresente(false);
			}
			o.setPuntajeFinal(obtenerPuntajeFinal());
			o.setFechaMod(new Date());
			o.setUsuMod(usuarioLogueado.getCodigoUsuario());
			em.merge(o);
		}
	}

	public Float obtenerPuntajeFinal() {
		float puntajeFinal = 0;
		int cantidadElems = 0;
		if (lGrilla1 == null)
			return puntajeFinal;
		for (ResultadoEval o : lGrilla1) {
			if (o.getPuntaje() != null) {
				cantidadElems++;
				puntajeFinal += o.getPuntaje();
			}
		}
		if (cantidadElems == 0)
			return new Float("0");
		puntajeFinal = puntajeFinal / cantidadElems;
		return puntajeFinal;
	}

	private ResultadoEvalPlan estaPresentePlanMejora(List<ResultadoEvalPlan> lista, Long idPlanMejora) {
		for (ResultadoEvalPlan o : lista) {
			if (o.getPlanMejora().getIdPlanMejora().longValue() == idPlanMejora.longValue())
				return o;
		}
		return null;
	}

	private List<ResultadoEvalPlan> listaResultadoEvalPlan(Long idRE) {
		List<ResultadoEvalPlan> respuesta = new ArrayList<ResultadoEvalPlan>();
		Query q =
			em.createQuery("select ResultadoEvalPlan from ResultadoEvalPlan ResultadoEvalPlan"
				+ " where ResultadoEvalPlan.activo is true and ResultadoEvalPlan.resultadoEval.idResultadoEval = :idRE ");
		q.setParameter("idRE", idRE);
		respuesta = q.getResultList();
		return respuesta;
	}

	public void actualizarPlanMejora(ResultadoEval o) {
		for (PlanMejora p : o.getlPM()) {

			ResultadoEvalPlan resultadoEvalPlan =
				estaPresentePlanMejora(listaResultadoEvalPlan(o.getIdResultadoEval()), p.getIdPlanMejora());
			if (resultadoEvalPlan != null) {
				if (resultadoEvalPlan.isActivo()) {
					if (!p.getSelected()) {
						// Actualizar
						ResultadoEvalPlan rep =
							em.find(ResultadoEvalPlan.class, resultadoEvalPlan.getIdResultadoEvalPlan());
						rep.setActivo(false);
						rep.setFechaMod(new Date());
						rep.setUsuMod(usuarioLogueado.getCodigoUsuario());
						em.merge(rep);
					}
				} else {
					if (p.getSelected()) {
						// Crear uno nuevo
						ResultadoEvalPlan rep = new ResultadoEvalPlan();
						rep.setActivo(true);
						rep.setPlanMejora(new PlanMejora());
						rep.getPlanMejora().setIdPlanMejora(p.getIdPlanMejora());
						rep.setResultadoEval(new ResultadoEval());
						rep.getResultadoEval().setIdResultadoEval(o.getIdResultadoEval());
						rep.setFechaAlta(new Date());
						rep.setUsuAlta(usuarioLogueado.getCodigoUsuario());
						em.persist(rep);
					}
				}
			} else {
				if (p.getSelected()) {
					// Crear uno nuevo
					ResultadoEvalPlan rep = new ResultadoEvalPlan();
					rep.setIdResultadoEvalPlan(null);
					rep.setActivo(true);
					rep.setPlanMejora(new PlanMejora());
					rep.getPlanMejora().setIdPlanMejora(p.getIdPlanMejora());
					rep.setResultadoEval(new ResultadoEval());
					rep.getResultadoEval().setIdResultadoEval(o.getIdResultadoEval());
					rep.setFechaAlta(new Date());
					rep.setUsuAlta(usuarioLogueado.getCodigoUsuario());
					em.persist(rep);
				}
			}
		}
	}

	public String saveGrupo() {
		if (!precondSave())
			return "FAIL";
		try {
			Iterator<ResultadoEval> iter = lGrilla1.iterator();
			while (iter.hasNext()) {
				ResultadoEval o = iter.next();
				o.setFechaMod(new Date());
				o.setUsuMod(usuarioLogueado.getCodigoUsuario());
				o = em.merge(o);
				/* Actualizar ResultadoEvalPlan */
				actualizarPlanMejora(o);
				/* Fin Actualizar ResultadoEvalPlan */
			}

			// Guardar comision
			guardarComision();
			// Actualizar los grupos_sujetos
			actualizarGruposSujetos();
			em.flush();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
			return "OK";
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("GENERICO_NO_MSG"));
		}
		return null;
	}

	public String finalizarEvalua(String tipoMet) {
		if (!precondFinalizar(tipoMet))
			return "FAIL";
		try {
			grupoMetodologia.setEstado(idRefEvaluacionFinalizada);
			grupoMetodologia.setUsuMod(usuarioLogueado.getCodigoUsuario());
			grupoMetodologia.setFechaMod(new Date());
			grupoMetodologia = em.merge(grupoMetodologia);
			em.flush();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
			return "OK";
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("GENERICO_NO_MSG"));
		}
		return null;
	}

	public void imprimir() {
		reportUtilFormController =
			(ReportUtilFormController) Component.getInstance(ReportUtilFormController.class, true);
		reportUtilFormController.init();
		GrupoMetodologia gm = em.find(GrupoMetodologia.class, idGrupoMetodologia);
		if (gm.getValor().equals("G"))
			reportUtilFormController.setNombreReporte("RPT_CU573_grupo_plantilla");
		else if (gm.getValor().equals("I"))
			reportUtilFormController.setNombreReporte("RPT_CU573_funcionarios");
		ConfiguracionUoCab oeeUsu=em.find(ConfiguracionUoCab.class, usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo());
		reportUtilFormController.getParametros().put("oee", oeeUsu.getDenominacionUnidad().toUpperCase());
		reportUtilFormController.getParametros().put("idGrupoMetodologia", idGrupoMetodologia);
		reportUtilFormController.imprimirReportePdf();

	}

//	agregado; Werner.
	public String retornandoView(){
		if (estado!=null) {
			if (!estado.contentEquals("FINALIZADA")) 
				return "/evaluacionDesempenho/cargaResultadoEvaluacion/GrupoMetodologiaList.xhtml";
			else
				return "/evaluacionDesempenho/cargaResultadoEvaluacion/VerGrupoMetodologiaList.xhtml";
		}else{
			return "/evaluacionDesempenho/cargaResultadoEvaluacion/GrupoMetodologiaList.xhtml";
		}
	}
//**********************************
	
	public String getGrupo() {
		return grupo;
	}

	public void setGrupo(String grupo) {
		this.grupo = grupo;
	}

	public String getPlantilla() {
		return plantilla;
	}

	public void setPlantilla(String plantilla) {
		this.plantilla = plantilla;
	}

	public String getEquipo() {
		return equipo;
	}

	public void setEquipo(String equipo) {
		this.equipo = equipo;
	}

	public String getComision() {
		return comision;
	}

	public void setComision(String comision) {
		this.comision = comision;
	}

	public List<ResultadoEvalComision> getlGrilla2() {
		return lGrilla2;
	}

	public void setlGrilla2(List<ResultadoEvalComision> lGrilla2) {
		this.lGrilla2 = lGrilla2;
	}

	public List<GruposSujetos> getlGrilla3() {
		return lGrilla3;
	}

	public void setlGrilla3(List<GruposSujetos> lGrilla3) {
		this.lGrilla3 = lGrilla3;
	}

	public String getElFrom() {
		return elFrom;
	}

	public void setElFrom(String elFrom) {
		this.elFrom = elFrom;
	}

	public Long getIdGrupoMetodologia() {
		return idGrupoMetodologia;
	}

	public void setIdGrupoMetodologia(Long idGrupoMetodologia) {
		this.idGrupoMetodologia = idGrupoMetodologia;
	}

	public String getEvalTitle() {
		return evalTitle;
	}

	public void setEvalTitle(String evalTitle) {
		this.evalTitle = evalTitle;
	}

	public Long getIdComision() {
		return idComision;
	}

	public void setIdComision(Long idComision) {
		this.idComision = idComision;
	}

	public List<ResultadoEval> getlGrilla1() {
		return lGrilla1;
	}

	public void setlGrilla1(List<ResultadoEval> lGrilla1) {
		this.lGrilla1 = lGrilla1;
	}

	public String getDireccionFisica() {
		return direccionFisica;
	}

	public void setDireccionFisica(String direccionFisica) {
		this.direccionFisica = direccionFisica;
	}

	public Long getIdTipoDocumento() {
		return idTipoDocumento;
	}

	public void setIdTipoDocumento(Long idTipoDocumento) {
		this.idTipoDocumento = idTipoDocumento;
	}

	public byte[] getuFile1() {
		return uFile1;
	}

	public void setuFile1(byte[] uFile1) {
		this.uFile1 = uFile1;
	}

	public String getcType1() {
		return cType1;
	}

	public void setcType1(String cType1) {
		this.cType1 = cType1;
	}

	public String getfName1() {
		return fName1;
	}

	public void setfName1(String fName1) {
		this.fName1 = fName1;
	}

	public Boolean getMostrarModal() {
		return mostrarModal;
	}

	public void setMostrarModal(Boolean mostrarModal) {
		this.mostrarModal = mostrarModal;
	}

	public Long getIdGruposSujetos() {
		return idGruposSujetos;
	}

	public void setIdGruposSujetos(Long idGruposSujetos) {
		this.idGruposSujetos = idGruposSujetos;
	}

	public String getPromedio() {
		return promedio;
	}

	public void setPromedio(String promedio) {
		this.promedio = promedio;
	}

	public GrupoMetodologia getGrupoMetodologia() {
		return grupoMetodologia;
	}

	public void setGrupoMetodologia(GrupoMetodologia grupoMetodologia) {
		this.grupoMetodologia = grupoMetodologia;
	}

	public String getNombrePantalla() {
		return nombrePantalla;
	}

	public void setNombrePantalla(String nombrePantalla) {
		this.nombrePantalla = nombrePantalla;
	}

	public Integer getIdRefEvaluacionEnCurso() {
		return idRefEvaluacionEnCurso;
	}

	public void setIdRefEvaluacionEnCurso(Integer idRefEvaluacionEnCurso) {
		this.idRefEvaluacionEnCurso = idRefEvaluacionEnCurso;
	}

	public List<GruposSujetos> getlGrillaIndividual() {
		return lGrillaIndividual;
	}

	public void setlGrillaIndividual(List<GruposSujetos> lGrillaIndividual) {
		this.lGrillaIndividual = lGrillaIndividual;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

}
