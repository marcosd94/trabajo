package py.com.excelsis.sicca.evaluacion.session.form;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.servlet.ServletContext;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.security.AuthorizationException;

import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.Documentos;
import py.com.excelsis.sicca.entity.EscalaEval;
import py.com.excelsis.sicca.entity.EvaluacionDesempeno;
import py.com.excelsis.sicca.entity.GrupoMetodologia;
import py.com.excelsis.sicca.entity.GruposSujetos;
import py.com.excelsis.sicca.entity.PlanMejora;
import py.com.excelsis.sicca.entity.PlantillaEvalPdec;
import py.com.excelsis.sicca.entity.PlantillaEvalPdecCab;
import py.com.excelsis.sicca.entity.PlantillaEvalPdecDet;
import py.com.excelsis.sicca.entity.ResultadoEval;
import py.com.excelsis.sicca.entity.ResultadoEvalPlan;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.form.AdmDocAdjuntoFormController;
import py.com.excelsis.sicca.session.util.JbpmUtilFormController;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.ReferenciasUtilFormController;
import py.com.excelsis.sicca.session.util.ReportUtilFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.JasperReportUtils;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("cargarResultEvalPDEC572FC")
public class CargarResultEvalPDEC572FC {
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
	@In(create = true)
	CargarResultEvalMetodo571FC cargarResultEvalMetodo571FC;

	private String grupo;
	private String plantilla;
	private Long idGrupoMetodologia;
	private GrupoMetodologia grupoMetodologia;
	private String direccionFisica;
	private String evalTitle;
	private String comision;
	private List<PlantillaEvalPdec> lGrilla1;
//	agregado; Werner.
	private Double puntMin;
	private Double puntMax;
	private Double margenMin;
	private Double margenMax;
	private List<EscalaEval> lGrilla2;
	private String mensaje;
	private String estado;
	private Long idSujetos;
	private List<PlantillaEvalPdec> lGrillaIndividual;
	private List<GruposSujetos> grupoSujetos;
	private List<GruposSujetos> grupoSujetos2;
	private Boolean mensajeLink;
	private Boolean reporteIndi;
	private Boolean HabilitarAnaMat;
	private String FiltroFunc;
	private String FiltroDocIde;
	private String FiltroFunc2;
//********************
	
	public void init() {
		reporteIndi=false;
		if (cargarResultEvalMetodo571FC == null) {
			cargarResultEvalMetodo571FC =
				(CargarResultEvalMetodo571FC) Component.getInstance(CargarResultEvalMetodo571FC.class, true);
		}
		cargarResultEvalMetodo571FC.setIdGrupoMetodologia(idGrupoMetodologia);
		if (!precondInit())
			throw new AuthorizationException(SeamResourceBundle.getBundle().getString("seam_authorization_exception"));

		cargarCabecera();
		cargarResultEvalMetodo571FC.setGrupoMetodologia(grupoMetodologia);
		
		if (grupoMetodologia.getTipo().equalsIgnoreCase("P")) {
			cargarResultEvalMetodo571FC.initIDs();

			cargarGrilla1();
			cargarResultEvalMetodo571FC.cargarGrilla2();
			suPrimeraVez();
			// Inicializar plan de mejora
			initPlanMejora();
			cargarGrilla2();//agregado para la escala; Werner.
			
		
		} else {
			throw new AuthorizationException(SeamResourceBundle.getBundle().getString("seam_authorization_exception"));
		}
	}

	private Boolean precondSave() {
//		if (lGrilla1 != null) {//original
		if (lGrillaIndividual != null) {	
//		for (PlantillaEvalPdec o : lGrilla1) {//original
			for (PlantillaEvalPdec o : lGrillaIndividual) {	
				for (PlantillaEvalPdecCab p : o.getPlantillaEvalPdecCabs()) {
//					if (o.getGrupoSujeto().getPresente()) {la condición ya no es necesaria, todos presentes; Werner.
						if (p.getResultado() == null) {
							statusMessages.add(Severity.ERROR, "El campo Puntaje Obt. es requerido");
							return false;
//						} else if (p.getResultado().intValue() < 0
//							|| p.getResultado().intValue() > 10) {
//							statusMessages.add(Severity.ERROR, "Puntaje fuera de rango. El Puntaje Obt. debe estar entre 0-10, inclusive");
//							return false;
//		reemplazado por el de abajo; Werner.					
						} else if (p.getResultado().doubleValue() < puntMin
									|| p.getResultado().doubleValue() > puntMax) {
									statusMessages.add(Severity.ERROR, "Puntaje fuera de rango. El Puntaje Obt. debe estar entre "+puntMin+" - "+puntMax+", inclusive");
 									return false;
//*******************************************
//						} else if ((p.getJustificacion() == null || p.getJustificacion().trim().isEmpty())
//							&& ((p.getResultado().intValue() >= 0 && p.getResultado().intValue() <= 3) || p.getResultado().intValue() >= 7
//								&& p.getResultado().intValue() <= 10)) {
//							statusMessages.add(Severity.ERROR, "El campo Justificación es requerido cuando el Puntaje Obt. está entre 0-3 ó 7 y 10");
//							return false;
//		reemplazado por el de abajo; Werner.
						}/* else if ((p.getJustificacion() == null || p.getJustificacion().trim().isEmpty())
								&& ((p.getResultado().doubleValue() >= puntMin && p.getResultado().doubleValue() <= margenMin) || 
										(p.getResultado().doubleValue() >= margenMax && p.getResultado().intValue() <= puntMax))) {
								statusMessages.add(Severity.ERROR, "El campo Justificación es requerido cuando el Puntaje Obt. "
										+ "está entre "+puntMin+"-"+margenMin+" ó "+margenMax+" y "+puntMax);
								return false;
						}*/

//					}else{
//						p.setResultado(new Float(0));
//					}
				}
			}
		}
//		if (cargarResultEvalMetodo571FC.getlGrilla2() == null
//			|| cargarResultEvalMetodo571FC.getlGrilla2().size() == 0) {
//			statusMessages.add(Severity.ERROR, "Debe existir por lo menos un integrante registrado");
//			return false;
//		}//se usa en el otro controller antes de finalizar...
		return true;
	}

	public Float obtenerPuntajePdec(Set<PlantillaEvalPdecCab> lista) {//agregado; Werner.
		Float puntajePdec = 0F;
		int cantidadElems = 0;
		if (lista == null)
			return puntajePdec;
		for (PlantillaEvalPdecCab o : lista) {
			cantidadElems++;
			if( o.getResultado()!=null){				
				if (puntMax==5) {
					if (o.getResultado()==5) 
						puntajePdec +=o.getPesoPdec()*1F;	
					if (o.getResultado()>=4 && o.getResultado() < 5) 
						puntajePdec +=o.getPesoPdec()*0.8F;
					if (o.getResultado()>=3 && o.getResultado() < 4) 
						puntajePdec +=o.getPesoPdec()*0.7F;
					if (o.getResultado()>=2 && o.getResultado() < 3) 
						puntajePdec +=o.getPesoPdec()*0.6F;	
					if (o.getResultado()>=0 && o.getResultado() < 2) 
						puntajePdec +=o.getPesoPdec()*0.5F;	
				}else
				puntajePdec +=o.getPesoPdec()*(o.getResultado()/100);
//				puntajePdec += o.getResultado();	
			}			
		}
		if (cantidadElems == 0)
			return new Float("0");
//		puntajePdec = puntajePdec / cantidadElems;
		puntajePdec = round(puntajePdec,2);
		return puntajePdec;
	}
	//agregado; Werner.
	private static float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }
	
	public String genPlanMejora(Long idPE) {
		Query q =
			em.createQuery("select ResultadoEvalPlan from ResultadoEvalPlan ResultadoEvalPlan"
				+ " where ResultadoEvalPlan.activo is true and ResultadoEvalPlan.plantillaEvalPdecCab.idPlantillaEvalPdecCab = :idPE ");
		q.setParameter("idPE", idPE);
		List<ResultadoEvalPlan> lista = q.getResultList();
		String respuesta = "";
		for (ResultadoEvalPlan o : lista) {
			em.refresh(o);
			respuesta += "," + o.getPlanMejora().getDescripcion();
		}
		respuesta = respuesta.replaceFirst(",", "");
		return respuesta;
	}

	public String save() {
		if (!precondSave())
			return "FAIL";
		try {
//			Iterator<PlantillaEvalPdec> iter = lGrilla1.iterator();//original
			Iterator<PlantillaEvalPdec> iter = lGrillaIndividual.iterator();
			Iterator<PlantillaEvalPdecCab> iter2;
			while (iter.hasNext()) {
				PlantillaEvalPdec o = iter.next();
//******** Agregado; Werner.*************************				
				Query q =
						em.createQuery("select GruposSujetos from GruposSujetos GruposSujetos where GruposSujetos.sujetos.idSujetos =:idSujeto ");
//					q.setParameter("idSujeto", o.getSujetos().getIdSujetos());
				q.setParameter("idSujeto", idSujetos);
					List<GruposSujetos> lista = q.getResultList();
					if (lista.size() > 0) {
						o.setGrupoSujeto(lista.get(0));
					}
//***************************************************
					
//				if (o.getGrupoSujeto().getPresente() == null) {
//					o.getGrupoSujeto().setPresente(false);
//				}
					
//				todos presentes; Werner.
				o.getGrupoSujeto().setPresente(true);
				o.getGrupoSujeto().setUsuMod(usuarioLogueado.getCodigoUsuario());
				o.getGrupoSujeto().setFechaMod(new Date());
//				o.getGrupoSujeto().setPuntajeFinal(obtenerPuntajeFinal(o.getPlantillaEvalPdecCabs()));//original
				//agregado; Werner.
				o.getGrupoSujeto().setPuntajePdec(obtenerPuntajePdec(o.getPlantillaEvalPdecCabs()));
				//*****************
				o.setGrupoSujeto(em.merge(o.getGrupoSujeto()));
				iter2 = o.getPlantillaEvalPdecCabs().iterator();
				while (iter2.hasNext()) {
					PlantillaEvalPdecCab p = iter2.next();
					p.setUsuMod(usuarioLogueado.getCodigoUsuario());
					p.setFechaMod(new Date());
					p = em.merge(p);
					/* Actualizar ResultadoEvalPlan */
					actualizarPlanMejora(p);
				}
			}
//			if (!precondSave())
//				return "FAIL";
			// Guardar comision
//			cargarResultEvalMetodo571FC.guardarComision();
			em.flush();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
			return "OK";
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("GENERICO_NO_MSG"));
		}
		return null;
	}

	private ResultadoEvalPlan estaPresentePlanMejora(List<ResultadoEvalPlan> lista, Long idPlanMejora) {
		for (ResultadoEvalPlan o : lista) {
			if (o.getPlanMejora().getIdPlanMejora().longValue() == idPlanMejora.longValue())
				return o;
		}
		return null;
	}

	private List<ResultadoEvalPlan> listaResultadoEvalPlan(Long idPEP) {
		List<ResultadoEvalPlan> respuesta = new ArrayList<ResultadoEvalPlan>();
		Query q =
			em.createQuery("select ResultadoEvalPlan from ResultadoEvalPlan ResultadoEvalPlan"
				+ " where ResultadoEvalPlan.activo is true and ResultadoEvalPlan.plantillaEvalPdecCab.idPlantillaEvalPdecCab = :idPEP ");
		q.setParameter("idPEP", idPEP);
		respuesta = q.getResultList();
		return respuesta;
	}

	private void actualizarPlanMejora(PlantillaEvalPdecCab o) {
		for (PlanMejora p : o.getlPM()) {

			ResultadoEvalPlan resultadoEvalPlan =
				estaPresentePlanMejora(listaResultadoEvalPlan(o.getIdPlantillaEvalPdecCab()), p.getIdPlanMejora());
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
						rep.setPlantillaEvalPdecCab(new PlantillaEvalPdecCab());
						rep.getPlantillaEvalPdecCab().setIdPlantillaEvalPdecCab(o.getIdPlantillaEvalPdecCab());
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
					rep.setPlantillaEvalPdecCab(new PlantillaEvalPdecCab());
					rep.getPlantillaEvalPdecCab().setIdPlantillaEvalPdecCab(o.getIdPlantillaEvalPdecCab());
					rep.setFechaAlta(new Date());
					rep.setUsuAlta(usuarioLogueado.getCodigoUsuario());
					em.persist(rep);
				}
			}
		}
	}

	public String traerTareas(Long idPlantilla) {
		String respuesta = "";
		Query q =
			em.createQuery("select PlantillaEvalPdecDet from PlantillaEvalPdecDet PlantillaEvalPdecDet "
				+ " where PlantillaEvalPdecDet.plantillaEvalPdecCab.idPlantillaEvalPdecCab = :idPlantilla ");
		q.setParameter("idPlantilla", idPlantilla);
		List<PlantillaEvalPdecDet> lista = q.getResultList();
		for (PlantillaEvalPdecDet o : lista) {
			respuesta += "." + o.getDescripcion();
		}
		respuesta = respuesta.replaceFirst("\\.", "");
		return respuesta;
	}

	public PlantillaEvalPdec traerPresente(PlantillaEvalPdec pepdec) {

		Query q =
			em.createQuery("select GruposSujetos from GruposSujetos GruposSujetos where GruposSujetos.sujetos.idSujetos =:idSujeto ");
		q.setParameter("idSujeto", pepdec.getSujetos().getIdSujetos());
		List<GruposSujetos> lista = q.getResultList();
		if (lista.size() > 0) {
			pepdec.setGrupoSujeto(lista.get(0));
		}
		return pepdec;
	}

	public PlantillaEvalPdec traerDocumento(PlantillaEvalPdec pepdec) {
		if (pepdec.getGrupoSujeto() != null) {
			return pepdec;
		}
		Query q =
			em.createQuery("select GruposSujetos from GruposSujetos GruposSujetos where GruposSujetos.sujetos.idSujetos =:idSujeto ");
		q.setParameter("idSujeto", pepdec.getSujetos().getIdSujetos());
		List<GruposSujetos> lista = q.getResultList();
		if (lista.size() > 0) {
			pepdec.setGrupoSujeto(lista.get(0));
		}
		return pepdec;
	}
	
	public Boolean habilitandoDescarga(Long idSujeto){
		Query q =
				em.createQuery("select GruposSujetos from GruposSujetos GruposSujetos where GruposSujetos.sujetos.idSujetos =:idSujeto and GruposSujetos.documentos IS NOT NULL");
			q.setParameter("idSujeto", idSujeto);
			List<GruposSujetos> lista = q.getResultList();
		return !lista.isEmpty();
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

	public void cargarGrilla1() {

		Query q =
			em.createQuery("select PlantillaEvalPdec from PlantillaEvalPdec PlantillaEvalPdec "
				+ " where PlantillaEvalPdec.grupoMetodologia.idGrupoMetodologia = :idGM order by PlantillaEvalPdec.sujetos.empleadoPuesto.persona.nombres");
		q.setParameter("idGM", idGrupoMetodologia);
		lGrilla1 = q.getResultList();

	}
	
	public void cargarGrilla1Filtro() {

		Query q =
			em.createQuery("select PlantillaEvalPdec from PlantillaEvalPdec PlantillaEvalPdec "
				+ " where PlantillaEvalPdec.grupoMetodologia.idGrupoMetodologia = :idGM "
				+ " and upper(PlantillaEvalPdec.sujetos.empleadoPuesto.persona.nombres) LIKE upper('%"+FiltroFunc.toUpperCase()+"%') "
				+ " and upper(PlantillaEvalPdec.sujetos.empleadoPuesto.persona.apellidos) LIKE upper('%"+FiltroFunc2.toUpperCase()+"%') "
				+ " and upper(PlantillaEvalPdec.sujetos.empleadoPuesto.persona.documentoIdentidad) LIKE upper('"+FiltroDocIde.toUpperCase()+"%') "
				+ " order by PlantillaEvalPdec.sujetos.empleadoPuesto.persona.nombres");
		q.setParameter("idGM", idGrupoMetodologia);
		lGrilla1 = q.getResultList();

	}

	public List<PlantillaEvalPdecCab> traerPlantillaEvalPdecCabs(Long idCab) {
		Query q =
			em.createQuery("select PlantillaEvalPdecCab from PlantillaEvalPdecCab PlantillaEvalPdecCab "
				+ " where PlantillaEvalPdecCab.plantillaEvalPdec.idPlantillaEvalPdec = :idCab and PlantillaEvalPdecCab.activo is true "
				+ "order by PlantillaEvalPdecCab.orden");
		q.setParameter("idCab", idCab);
		return q.getResultList();
	}

	public Boolean detectarPrimeraVez() {

		Query q =
			em.createQuery("select count(GruposSujetos) from  GruposSujetos GruposSujetos "
				+ " where  GruposSujetos.gruposEvaluacion.idGruposEvaluacion = :idGE and GruposSujetos.presente is null");
		q.setParameter("idGE", grupoMetodologia.getGruposEvaluacion().getIdGruposEvaluacion());
		
		if (((Long) q.getSingleResult()).intValue() != 0)
			return true;

		return false;
	}

	private void suPrimeraVez() {
		if (detectarPrimeraVez()) {
			grupoMetodologia.setEstado(cargarResultEvalMetodo571FC.getIdRefEvaluacionEnCurso());
			grupoMetodologia.setFechaMod(new Date());
			grupoMetodologia.setUsuMod(usuarioLogueado.getCodigoUsuario());
			em.persist(grupoMetodologia);
			em.flush();
		}
	}

	private void initPlanMejora() {
		if (lGrilla1 == null)
			return;
		Query q = null;
		Iterator<PlantillaEvalPdec> iter = lGrilla1.iterator();

		while (iter.hasNext()) {
			PlantillaEvalPdec pepdec = iter.next();
			for (PlantillaEvalPdecCab o : traerPlantillaEvalPdecCabs(pepdec.getIdPlantillaEvalPdec())) {
				q =
					em.createQuery("select ResultadoEvalPlan from ResultadoEvalPlan ResultadoEvalPlan"
						+ " where ResultadoEvalPlan.activo is true and ResultadoEvalPlan.plantillaEvalPdecCab.idPlantillaEvalPdecCab = :idPlan ");
				q.setParameter("idPlan", o.getIdPlantillaEvalPdecCab());
				List<ResultadoEvalPlan> lista = q.getResultList();
				o.setlPM(cargarResultEvalMetodo571FC.traerPlanMejoraBase());
				for (ResultadoEvalPlan p : lista) {
					Iterator<PlanMejora> iter2 = o.getlPM().iterator();
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

	private Boolean precondInit() {
		if (idGrupoMetodologia != null) {
			grupoMetodologia = em.find(GrupoMetodologia.class, idGrupoMetodologia);//se deshabilita condición UO; Werner.
//			if (usuarioLogueado.getConfiguracionUoDet().getIdConfiguracionUoDet().longValue() == grupoMetodologia.getGruposEvaluacion().getEvaluacionDesempeno().getConfiguracionUoDet().getIdConfiguracionUoDet().longValue()) {
				return true;
//			}
		}
		return false;
	}

	private void cargarCabecera() {

		nivelEntidadOeeUtil.setIdConfigCab(usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo());
//		nivelEntidadOeeUtil.setIdUnidadOrganizativa(usuarioLogueado.getConfiguracionUoDet().getIdConfiguracionUoDet());
		nivelEntidadOeeUtil.init2();
		comision = grupoMetodologia.getGruposEvaluacion().getComisionEval().getNombre();
		evalTitle = grupoMetodologia.getGruposEvaluacion().getEvaluacionDesempeno().getTitulo();
		plantilla = grupoMetodologia.getDatosEspecifMetod().getDescripcion();
		grupo = grupoMetodologia.getGruposEvaluacion().getGrupo();
		direccionFisica = generarDireFisica();
		cargarResultEvalMetodo571FC.setNombrePantalla("cargarResultEvalPDEC572FC");
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
				+ cSeparador
				+ "E"
				+ cSeparador
				+ grupoMetodologia.getGruposEvaluacion().getEvaluacionDesempeno().getIdEvaluacionDesempeno();
		return respuesta;
	}

	public void pdf() throws SQLException {
		
		Connection conn = null;
		try {
			HashMap<String, Object> mapa = obtenerMapaParametros();
			if (mapa == null) {
				return;
			} else
				statusMessages.clear();
			conn = JpaResourceBean.getConnection();
			if (reporteIndi == true) {
				JasperReportUtils.respondPDF("RPT_Resultado_Evaluacion_PDECindivi_CU574",
						mapa, false, conn, usuarioLogueado);
			}else{
				JasperReportUtils.respondPDF("RPT_Resultado_Evaluacion_PDEC_CU574",
						mapa, false, conn, usuarioLogueado);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (conn != null)
				conn.close();
		}
	}
	
	private HashMap<String, Object> obtenerMapaParametros() throws Exception {
		
		HashMap<String, Object> param = new HashMap<String, Object>();
		param.put("usuario", usuarioLogueado.getCodigoUsuario());
		ServletContext servletContext = (ServletContext) FacesContext
				.getCurrentInstance().getExternalContext().getContext();
		
		param.put("oee", nivelEntidadOeeUtil.getDenominacionUnidad());
	
		if (nivelEntidadOeeUtil.getCodNivelEntidad() != null)
			param.put("nivel", nivelEntidadOeeUtil.getNombreNivelEntidad());
		if (nivelEntidadOeeUtil.getCodSinEntidad() != null)
			param.put("entidad", nivelEntidadOeeUtil.getNombreSinEntidad());
		if (nivelEntidadOeeUtil.getIdConfigCab() != null)
			param.put("uo", nivelEntidadOeeUtil.getDenominacionUnidad());
//		if (nivelEntidadOeeUtil.getIdUnidadOrganizativa() != null)
//			param.put("uo", nivelEntidadOeeUtil.getDenominacionUnidadOrgaDep());
		GrupoMetodologia metodologia = em.find(GrupoMetodologia.class, idGrupoMetodologia);
		param.put("evaluacion",metodologia.getGruposEvaluacion().getEvaluacionDesempeno().getTitulo());
		param.put("grupo",metodologia.getGruposEvaluacion().getGrupo());
		param.put("fecha_desde",metodologia.getGruposEvaluacion().getEvaluacionDesempeno().getFechaEvalDesde());
		param.put("fecha_hasta",metodologia.getGruposEvaluacion().getEvaluacionDesempeno().getFechaEvalHasta());
		param.put("oee_actual", metodologia.getGruposEvaluacion().getEvaluacionDesempeno().getConfiguracionUoCab().getDenominacionUnidad());
		param.put("id", idGrupoMetodologia);
	
		if (reporteIndi == true) {
			param.put("idSujeto", idSujetos );
		}
		
		param.put("SUBREPORT_DIR",
				servletContext.getRealPath("/reports/jasper/"));
		param.put("path_logo", servletContext.getRealPath("/img/"));
		return param;
	}
	//agregado; Werner*************************************************************	
	private void calcPuntajeMinMax() {
		if (getlGrilla2() != null && getlGrilla2().size() > 0) {
			puntMin = getlGrilla2().get(0).getDesde().doubleValue();
			puntMax = getlGrilla2().get(getlGrilla2().size() - 1).getHasta().doubleValue();
			margenMin = (puntMax * 0.3);margenMax = (puntMax * 0.7);
		}
	}
	public String mensajePuntajeMinMax(){
		String mens = "Será obligatoria la justificación del motivo de asignación de puntaje bajos de "
				+ ""+puntMin+" al "+margenMin+" y los altos del "+margenMax+" al "+puntMax;
		return mens;
	}
	private void cargarGrilla2() {

		Query q =
			em.createQuery("select EscalaEval from EscalaEval EscalaEval "
				+ " where EscalaEval.grupoMetodologia.idGrupoMetodologia = :idGM and EscalaEval.activo is true order by EscalaEval.desde ");
		q.setParameter("idGM", idGrupoMetodologia);
		setlGrilla2(q.getResultList());
		calcPuntajeMinMax();

	}
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
	
	public String calcPuntaje(Integer desde, Integer hasta) {

		if (desde.intValue() == hasta.intValue()) {
			return desde.toString();
		}
		return desde + "-" + hasta;
	}
	
	public List<PlantillaEvalPdec> traerDetalleGrilla4(Long idSujetos) {
		Query q =
				em.createQuery("select PlantillaEvalPdec from PlantillaEvalPdec PlantillaEvalPdec where PlantillaEvalPdec.sujetos.idSujetos = :idS ");
			q.setParameter("idS", idSujetos);

		List<PlantillaEvalPdec> lista = q.getResultList();
		return lista;
	}
	
	public void cargarGrilla1Link() {
		reporteIndi=true;
		Query q =
			em.createQuery("select PlantillaEvalPdec from PlantillaEvalPdec PlantillaEvalPdec "
				+ " where PlantillaEvalPdec.grupoMetodologia.idGrupoMetodologia = :idGM and PlantillaEvalPdec.sujetos.idSujetos = :idS");
		q.setParameter("idGM", idGrupoMetodologia);
		q.setParameter("idS", idSujetos);
		lGrillaIndividual=q.getResultList();bloqueandoMatrizIndivi();
		Query q2 =
				em.createQuery("select GruposSujetos from GruposSujetos GruposSujetos "
					+ " where GruposSujetos.sujetos.idSujetos = :idS");
			q2.setParameter("idS", idSujetos);
		List<GruposSujetos> grupoSujetos3;
		grupoSujetos3 = q2.getResultList();	
		cargarResultEvalMetodo571FC.setIdGruposSujetos(grupoSujetos3.get(0).getIdGrupoSujeto());
	}
	private void bloqueandoMatrizIndivi(){
		Query q =
				em.createQuery("select GruposSujetos from GruposSujetos GruposSujetos "
					+ " where GruposSujetos.sujetos.idSujetos = :idS"
					+ " and GruposSujetos.puntajeFinal IS NOT NULL");
			q.setParameter("idS", idSujetos);
		grupoSujetos2 = q.getResultList();	
		if (grupoSujetos2.isEmpty()) 
			HabilitarAnaMat = true;
		else
			HabilitarAnaMat = false;
	}
	
	public boolean cambiandoMensLink(Long idSujeto) {
		Query q =
			em.createQuery("select GruposSujetos from GruposSujetos GruposSujetos "
				+ " where GruposSujetos.sujetos.idSujetos = :idS"
				+ " and GruposSujetos.puntajePdec IS NOT NULL");
		q.setParameter("idS", idSujeto);
		setGrupoSujetos(q.getResultList());
		return !grupoSujetos.isEmpty();
	}
	
	public void imprimirAnalisisMatriz(){
		reportUtilFormController =
				(ReportUtilFormController) Component.getInstance(ReportUtilFormController.class, true);
			reportUtilFormController.init();

			reportUtilFormController.setNombreReporte("RPT_CU580_AnalisisMatrizDesempIndivi");
			ConfiguracionUoCab oeeUsuario =
				em.find(ConfiguracionUoCab.class, usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo());
			GrupoMetodologia gm = em.find(GrupoMetodologia.class, idGrupoMetodologia);
			reportUtilFormController.getParametros().put("oee_usuario", oeeUsuario.getDenominacionUnidad().toUpperCase());
			reportUtilFormController.getParametros().put("id", gm.getGruposEvaluacion().getEvaluacionDesempeno().getIdEvaluacionDesempeno());
			Map<String, String> diccDesc = nivelEntidadOeeUtil.descNivelEntidadOee();
			reportUtilFormController.getParametros().put("nivel", diccDesc.get("NIVEL"));
			reportUtilFormController.getParametros().put("entidad", diccDesc.get("ENTIDAD"));
			reportUtilFormController.getParametros().put("oee", diccDesc.get("OEE"));
			reportUtilFormController.getParametros().put("idSujeto", idSujetos);
			
			reportUtilFormController.imprimirReportePdf();
	}
	//*****************************************************************************
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

	public Long getIdGrupoMetodologia() {
		return idGrupoMetodologia;
	}

	public void setIdGrupoMetodologia(Long idGrupoMetodologia) {
		this.idGrupoMetodologia = idGrupoMetodologia;
	}

	public GrupoMetodologia getGrupoMetodologia() {
		return grupoMetodologia;
	}

	public void setGrupoMetodologia(GrupoMetodologia grupoMetodologia) {
		this.grupoMetodologia = grupoMetodologia;
	}

	public String getDireccionFisica() {
		return direccionFisica;
	}

	public void setDireccionFisica(String direccionFisica) {
		this.direccionFisica = direccionFisica;
	}

	public String getEvalTitle() {
		return evalTitle;
	}

	public void setEvalTitle(String evalTitle) {
		this.evalTitle = evalTitle;
	}

	public String getComision() {
		return comision;
	}

	public void setComision(String comision) {
		this.comision = comision;
	}

	public List<PlantillaEvalPdec> getlGrilla1() {
		return lGrilla1;
	}

	public void setlGrilla1(List<PlantillaEvalPdec> lGrilla1) {
		this.lGrilla1 = lGrilla1;
	}

	public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public List<EscalaEval> getlGrilla2() {
		return lGrilla2;
	}

	public void setlGrilla2(List<EscalaEval> lGrilla2) {
		this.lGrilla2 = lGrilla2;
	}

	public Long getIdSujetos() {
		return idSujetos;
	}

	public void setIdSujetos(Long idSujetos) {
		this.idSujetos = idSujetos;
	}

	public List<PlantillaEvalPdec> getlGrillaIndividual() {
		return lGrillaIndividual;
	}

	public void setlGrillaIndividual(List<PlantillaEvalPdec> lGrillaIndividual) {
		this.lGrillaIndividual = lGrillaIndividual;
	}

	public Boolean getMensajeLink() {
		return mensajeLink;
	}

	public void setMensajeLink(Boolean mensajeLink) {
		this.mensajeLink = mensajeLink;
	}

	public List<GruposSujetos> getGrupoSujetos() {
		return grupoSujetos;
	}

	public void setGrupoSujetos(List<GruposSujetos> grupoSujetos) {
		this.grupoSujetos = grupoSujetos;
	}

	public Boolean getReporteIndi() {
		return reporteIndi;
	}

	public void setReporteIndi(Boolean reporteIndi) {
		this.reporteIndi = reporteIndi;
	}

	public Boolean getHabilitarAnaMat() {
		return HabilitarAnaMat;
	}

	public void setHabilitarAnaMat(Boolean habilitarAnaMat) {
		HabilitarAnaMat = habilitarAnaMat;
	}

	public String getFiltroFunc() {
		return FiltroFunc;
	}

	public void setFiltroFunc(String filtroFunc) {
		FiltroFunc = filtroFunc;
	}

	public String getFiltroDocIde() {
		return FiltroDocIde;
	}

	public void setFiltroDocIde(String filtroDocIde) {
		FiltroDocIde = filtroDocIde;
	}

	public String getFiltroFunc2() {
		return FiltroFunc2;
	}

	public void setFiltroFunc2(String filtroFunc2) {
		FiltroFunc2 = filtroFunc2;
	}

}
