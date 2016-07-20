package py.com.excelsis.sicca.evaluacion.session.form;

import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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

import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.EscalaEval;
import py.com.excelsis.sicca.entity.GrupoMetodologia;
import py.com.excelsis.sicca.entity.GruposSujetos;
import py.com.excelsis.sicca.entity.PlanMejora;
import py.com.excelsis.sicca.entity.PlantillaEvalConfDet;
import py.com.excelsis.sicca.entity.PlantillaEvalEscala;
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
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("cargarResultEvalPerce608FC")
public class CargarResultEvalPerce608FC {
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
	private List<PlantillaEvalConfDet> lGrilla1;
	private List<EscalaEval> lGrilla2;
	private List<GruposSujetos> lGrilla3;
//	private Integer puntMin;
//	private Integer puntMax;
	
	//agregado; Werner.***************************************************
	private Double puntMin;
	private Double puntMax;
	private Double margenMin;
	private Double margenMax;
	private Long idSujetos;
	private List<GruposSujetos> lGrilla4;
	private Boolean reporteIndi;
	private Boolean HabilitarAnaMat;
	private String FiltroFunc;
	private String FiltroDocIde;
	private String FiltroFunc2;
	//********************************************************************
	
	public void init() {
		reporteIndi = false;
		if (cargarResultEvalMetodo571FC == null) {
			cargarResultEvalMetodo571FC =
				(CargarResultEvalMetodo571FC) Component.getInstance(CargarResultEvalMetodo571FC.class, true);
		}
		cargarResultEvalMetodo571FC.setIdGrupoMetodologia(idGrupoMetodologia);
		if (!precondInit())
			throw new AuthorizationException(SeamResourceBundle.getBundle().getString("seam_authorization_exception"));

		cargarCabecera();
		cargarResultEvalMetodo571FC.setGrupoMetodologia(grupoMetodologia);
		if (grupoMetodologia.getTipo().equalsIgnoreCase("A")) {
			cargarResultEvalMetodo571FC.initIDs();
			suPrimeraVez();
			cargarGrilla1();
			cargarGrilla2();
			cargarGrilla3();
			calcPuntajeMinMax();
			cargarResultEvalMetodo571FC.cargarGrilla2();
			initPlanMejora();
		} else {
			throw new AuthorizationException(SeamResourceBundle.getBundle().getString("seam_authorization_exception"));
		}
	}

	public PlantillaEvalConfDet refreshPlantillaEvalConfDet(PlantillaEvalConfDet pecd) {
		return em.find(PlantillaEvalConfDet.class, pecd.getIdPlantillaEvalConfDet());
	}

	private void calcPuntajeMinMax() {
		if (lGrilla2 != null && lGrilla2.size() > 0) {
//			puntMin = lGrilla2.get(0).getDesde();
//			puntMax = lGrilla2.get(lGrilla2.size() - 1);
//agregado; Werner.*******************************************************
			puntMin = lGrilla2.get(0).getDesde().doubleValue();
			puntMax = lGrilla2.get(lGrilla2.size() - 1).getHasta().doubleValue();
			margenMin = (puntMax * 0.3);margenMax = (puntMax * 0.7);
//************************************************************************
		}
	}

	private void suPrimeraVez() {
		if (cargarResultEvalMetodo571FC.detectarPrimeraVez()) {
			List<PlantillaEvalConfDet> listaPECD =
				cargarResultEvalMetodo571FC.plantillaEvalConfDetFirstTime();
			for (GruposSujetos o : cargarResultEvalMetodo571FC.grupoSujetFirstTime()) {
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
			grupoMetodologia.setEstado(cargarResultEvalMetodo571FC.getIdRefEvaluacionEnCurso());
			grupoMetodologia.setFechaMod(new Date());
			grupoMetodologia.setUsuMod(usuarioLogueado.getCodigoUsuario());
			em.persist(grupoMetodologia);
			em.flush();
		}
	}

	public List<PlantillaEvalEscala> traerDetalleGrilla1(Long idPEC) {
		Query q =
			em.createQuery("select PlantillaEvalEscala from PlantillaEvalEscala PlantillaEvalEscala "
				+ " where  PlantillaEvalEscala.plantillaEvalConfDet.idPlantillaEvalConfDet = :idPEC and PlantillaEvalEscala.activo is true ");
		q.setParameter("idPEC", idPEC);

		return q.getResultList();
	}

	public String calcPuntaje(Integer desde, Integer hasta) {

		if (desde.intValue() == hasta.intValue()) {
			return desde.toString();
		}
		return desde + "-" + hasta;
	}

	private void initPlanMejora() {
		Query q = null;
		Iterator<ResultadoEval> iter = null;
		for (GruposSujetos o : lGrilla3) {
			List<ResultadoEval> lDetalle = traerDetalleGrilla3(o.getIdGrupoSujeto());
			iter = lDetalle.iterator();
			while (iter.hasNext()) {
				ResultadoEval resultadoEval = iter.next();
				q =
					em.createQuery("select ResultadoEvalPlan from ResultadoEvalPlan ResultadoEvalPlan"
						+ " where ResultadoEvalPlan.activo is true and ResultadoEvalPlan.resultadoEval.idResultadoEval = :idRE ");
				q.setParameter("idRE", resultadoEval.getIdResultadoEval());
				List<ResultadoEvalPlan> lista = q.getResultList();
				resultadoEval.setlPM(cargarResultEvalMetodo571FC.traerPlanMejoraBase());
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

	public String genPlanMejora(Long idPE) {
		Query q =
			em.createQuery("select ResultadoEvalPlan from ResultadoEvalPlan ResultadoEvalPlan"
				+ " where ResultadoEvalPlan.activo is true and ResultadoEvalPlan.plantillaEvalPdecCab.idPlantillaEvalPdecCab = :idPE ");
		q.setParameter("idPE", idPE);
		List<ResultadoEvalPlan> lista = q.getResultList();
		String respuesta = "";
		for (ResultadoEvalPlan o : lista) {
			respuesta += "," + o.getPlanMejora().getDescripcion();
		}
		respuesta = respuesta.replaceFirst(",", "");
		return respuesta;
	}

	private Boolean precondSave() {
//		if (lGrilla4 != null) {//original
		if (lGrilla4 != null) {
//			for (GruposSujetos cab : lGrilla3) {//original
			for (GruposSujetos cab : lGrilla4) {
				List<ResultadoEval> lDetalle = traerDetalleGrilla3(cab.getIdGrupoSujeto());
				for (ResultadoEval o : lDetalle) {
//					if (o.getGruposSujetos().getPresente()) {
						if (o.getPuntaje() == null) {
							statusMessages.add(Severity.ERROR, "El campo Puntaje es requerido");
							return false;
						}
						if (o.getPuntaje() != null
							&& o.getPuntaje().intValue() < puntMin.intValue()
							|| o.getPuntaje().intValue() > puntMax.intValue()) {
							statusMessages.add(Severity.ERROR, "Puntaje fuera de rango. El puntaje debe estar entre "
								+ puntMin + "-" + puntMax + ", inclusive");
							return false;
						}
//						if ((o.getJustificacion() == null || o.getJustificacion().trim().isEmpty())) {//original
//							statusMessages.add(Severity.ERROR, "El campo Justificación es requerido");
//							return false;
//						}
//						agregado; Werner.
						/*
						if ((o.getJustificacion() == null || o.getJustificacion().trim().isEmpty())
								&& ((o.getPuntaje().doubleValue() >= puntMin && o.getPuntaje().doubleValue() <= margenMin) || 
										(o.getPuntaje().doubleValue() >= margenMax && o.getPuntaje().intValue() <= puntMax))) {
							statusMessages.add(Severity.ERROR, "El campo Justificación es requerido cuando el Puntaje Obt. "
									+ "está entre "+puntMin+"-"+margenMin+" ó "+margenMax+" y "+puntMax);
							return false;
						}*/
//						*********************
//					}
				}
			}
		} else {
			statusMessages.add(Severity.ERROR, "No existe datos que evaluar");
			return false;
		}
//		if (cargarResultEvalMetodo571FC.getlGrilla2() == null
//			|| cargarResultEvalMetodo571FC.getlGrilla2().size() == 0) {
//			statusMessages.add(Severity.ERROR, "Debe existir por lo menos un integrante registrado");
//			return false;
//		}
		return true;
	}

	public String save() {
		if (!precondSave())
			return "FAIL";
		try {
//			Iterator<GruposSujetos> iter = lGrilla3.iterator();//original
			Iterator<GruposSujetos> iter = lGrilla4.iterator();
			Iterator<ResultadoEval> iter2;
			while (iter.hasNext()) {
				GruposSujetos gs = iter.next();
//				List<ResultadoEval> lDetalle = traerDetalleGrilla3(gs.getIdGrupoSujeto());
				List<ResultadoEval> lDetalle = traerDetalleGrilla5(gs.getSujetos().getIdSujetos());
//				if (gs.getPresente() == null) {
//					gs.setPresente(false);
//				}
//				Se cambia a... todos presentes... Werner.
				gs.setPresente(true);
				
				gs.setUsuMod(usuarioLogueado.getCodigoUsuario());
				gs.setFechaMod(new Date());
//				gs.setPuntajeFinal(obtenerPuntajeFinal(lDetalle));//original
				//agregado; Werner.
				gs.setPuntajePercepcion(obtenerPuntajePercepcion(lDetalle));
				gs = em.merge(gs);
				iter2 = lDetalle.iterator();
				while (iter2.hasNext()) {
					ResultadoEval p = iter2.next();
					p.setUsuMod(usuarioLogueado.getCodigoUsuario());
					p.setFechaMod(new Date());
					p = em.merge(p);
					/* Actualizar ResultadoEvalPlan */
					cargarResultEvalMetodo571FC.actualizarPlanMejora(p);
				}
			}			
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

	public Float obtenerPuntajePercepcion(List<ResultadoEval> lista) {//agregado; Werner.
		Float puntajePercepcion = 0F;
		int cantidadElems = 0;
		if (lista == null)
			return puntajePercepcion;
		for (ResultadoEval o : lista) {
			cantidadElems++;
			if( o.getPuntaje()!=null){				
				if (puntMax==5) {
					if (o.getPuntaje()==5) 
						puntajePercepcion += o.getPlantillaEvalConfDet().getPesoPercepcion()*1F;	
					if (o.getPuntaje()>=4 && o.getPuntaje() < 5) 
						puntajePercepcion += o.getPlantillaEvalConfDet().getPesoPercepcion()*0.8F;
					if (o.getPuntaje()>=3 && o.getPuntaje() < 4) 
						puntajePercepcion += o.getPlantillaEvalConfDet().getPesoPercepcion()*0.7F;
					if (o.getPuntaje()>=2 && o.getPuntaje() < 3) 
						puntajePercepcion += o.getPlantillaEvalConfDet().getPesoPercepcion()*0.6F;	
					if (o.getPuntaje()>=0 && o.getPuntaje() < 2) 
						puntajePercepcion += o.getPlantillaEvalConfDet().getPesoPercepcion()*0.5F;	
				}else
				puntajePercepcion += o.getPlantillaEvalConfDet().getPesoPercepcion()*(o.getPuntaje()/100);
			}	
//			if (o.getPuntaje() != null) {
//				cantidadElems++;
//				puntajePercepcion += o.getPuntaje();
//			}
		}
		if (cantidadElems == 0)
			return new Float("0");
//		puntajePercepcion = puntajePercepcion / cantidadElems;
		puntajePercepcion = round(puntajePercepcion,2);
		return puntajePercepcion;
	}
	//agregado; Werner.
	private static float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
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

	public List<ResultadoEval> traerDetalleGrilla3(Long idGrupoSujeto) {
		
//		Query q =
//			em.createQuery("select ResultadoEval from ResultadoEval ResultadoEval where ResultadoEval.gruposSujetos.idGrupoSujeto = :idGS ");
//		q.setParameter("idGS", idGrupoSujeto);
		//Se modificó el query por el de abajo, para evitar conflictos entre las planillas Percepción e Individual; Werner.
		Query q =
				em.createQuery("select ResultadoEval from ResultadoEval ResultadoEval where ResultadoEval.gruposSujetos.idGrupoSujeto = :idGS "
						+ "and ResultadoEval.grupoMetodologia.idGrupoMetodologia = :idGM order by ResultadoEval.plantillaEvalConfDet.orden");
			q.setParameter("idGS", idGrupoSujeto);
			q.setParameter("idGM", idGrupoMetodologia);

		List<ResultadoEval> lista = q.getResultList();
		return lista;
	}

	public ResultadoEval refreshResultadoEval(ResultadoEval resultadoEval) {
		if (resultadoEval.getPlantillaEvalConfDet() != null
			|| resultadoEval.getPlantillaEvalConfDet().getOrden() == null) {
			PlantillaEvalConfDet ped =
				em.find(PlantillaEvalConfDet.class, resultadoEval.getPlantillaEvalConfDet().getIdPlantillaEvalConfDet());
			resultadoEval.setPlantillaEvalConfDet(ped);
		}
		return resultadoEval;
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

	public void cargarGrilla3() {

		Query q =
			em.createQuery("select GruposSujetos from GruposSujetos GruposSujetos "
				+ " where GruposSujetos.gruposEvaluacion.idGruposEvaluacion = :idGE "
				+ " and GruposSujetos.activo = true order by GruposSujetos.sujetos.empleadoPuesto.persona.nombres");
		q.setParameter("idGE", grupoMetodologia.getGruposEvaluacion().getIdGruposEvaluacion());
		lGrilla3 = q.getResultList();

	}
	
	public void cargarGrilla3Filtro() {

		Query q =
			em.createQuery("select GruposSujetos from GruposSujetos GruposSujetos "
				+ " where GruposSujetos.gruposEvaluacion.idGruposEvaluacion = :idGE "
				+ " and GruposSujetos.activo = true and upper(GruposSujetos.sujetos.empleadoPuesto.persona.nombres) LIKE upper('%"+FiltroFunc.toUpperCase()+"%') "
				+ " and upper(GruposSujetos.sujetos.empleadoPuesto.persona.documentoIdentidad) LIKE upper('"+FiltroDocIde.toUpperCase()+"%') "
				+ " and upper(GruposSujetos.sujetos.empleadoPuesto.persona.apellidos) LIKE upper('%"+FiltroFunc2.toUpperCase()+"%') "
				+ " order by GruposSujetos.sujetos.empleadoPuesto.persona.nombres");
		q.setParameter("idGE", grupoMetodologia.getGruposEvaluacion().getIdGruposEvaluacion());
		lGrilla3 = q.getResultList();

	}

	private void cargarGrilla2() {

		Query q =
			em.createQuery("select EscalaEval from EscalaEval EscalaEval "
				+ " where EscalaEval.grupoMetodologia.idGrupoMetodologia = :idGM and EscalaEval.activo is true order by EscalaEval.desde ");
		q.setParameter("idGM", idGrupoMetodologia);
		lGrilla2 = q.getResultList();

	}

	private void cargarGrilla1() {

		Query q =
			em.createQuery("select PlantillaEvalConfDet from PlantillaEvalConfDet PlantillaEvalConfDet "
				+ " where PlantillaEvalConfDet.grupoMetodologia.idGrupoMetodologia = :idGM and PlantillaEvalConfDet.activo is true ");
		q.setParameter("idGM", idGrupoMetodologia);
		lGrilla1 = q.getResultList();

	}

	public List<PlantillaEvalPdecCab> traerPlantillaEvalPdecCabs(Long idCab) {
		Query q =
			em.createQuery("select PlantillaEvalPdecCab from PlantillaEvalPdecCab PlantillaEvalPdecCab "
				+ " where PlantillaEvalPdecCab.plantillaEvalPdec.idPlantillaEvalPdec = :idCab");
		q.setParameter("idCab", idCab);
		return q.getResultList();
	}

	private Boolean precondInit() {
		if (idGrupoMetodologia != null) {
			grupoMetodologia = em.find(GrupoMetodologia.class, idGrupoMetodologia);
			//se deshabilitó (UO); Werner.
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

	public void imprimirCriterios() {
		reportUtilFormController =
			(ReportUtilFormController) Component.getInstance(ReportUtilFormController.class, true);
		reportUtilFormController.init();

		reportUtilFormController.setNombreReporte("RPT_CU641_1");
		ConfiguracionUoCab oeeUsuario =
			em.find(ConfiguracionUoCab.class, usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo());
		reportUtilFormController.getParametros().put("oee_usuario", oeeUsuario.getDenominacionUnidad().toUpperCase());
		reportUtilFormController.getParametros().put("idGrupoMetodologia", grupoMetodologia.getIdGrupoMetodologia());
		reportUtilFormController.imprimirReportePdf();

	}

	public void imprimirResultados() {
		reportUtilFormController =
			(ReportUtilFormController) Component.getInstance(ReportUtilFormController.class, true);
		reportUtilFormController.init();

		if (reporteIndi==true) 
			reportUtilFormController.setNombreReporte("RPT_CU642_resultadoEvalPercepcionIndivi");
		else
			reportUtilFormController.setNombreReporte("RPT_CU642_resultadoEvalPercepcion");
		
		
		ConfiguracionUoCab oeeUsuario =
			em.find(ConfiguracionUoCab.class, usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo());
		reportUtilFormController.getParametros().put("oee_usuario", oeeUsuario.getDenominacionUnidad().toUpperCase());
		reportUtilFormController.getParametros().put("idGrupoMetodologia", idGrupoMetodologia);
		Map<String, String> diccDesc = nivelEntidadOeeUtil.descNivelEntidadOee();
		reportUtilFormController.getParametros().put("nivel", diccDesc.get("NIVEL"));
		reportUtilFormController.getParametros().put("entidad", diccDesc.get("ENTIDAD"));
		reportUtilFormController.getParametros().put("oee", diccDesc.get("OEE"));
		
		if (reporteIndi==true) {
			reportUtilFormController.getParametros().put("idSujeto", idSujetos);
		}
		
		
		reportUtilFormController.imprimirReportePdf();

	}
	
//	agregado; Werner.************************************************************************************
	
	public String mensajePuntajeMinMax(){
		String mens = "Será obligatoria la justificación del motivo de asignación de puntaje bajos de "
				+ ""+puntMin+" al "+margenMin+" y los altos del "+margenMax+" al "+puntMax;
		return mens;
	}

	
	public List<GruposSujetos> traerDetalleGrilla4(Long idGrupoSujeto) {
		Query q =
				em.createQuery("select GruposSujetos from GruposSujetos GruposSujetos where GruposSujetos.idGrupoSujeto = :idGS ");
			q.setParameter("idGS", idGrupoSujeto);

		List<GruposSujetos> lista = q.getResultList();
		return lista;
	}
	
	public boolean cambiandoMensLink(Long idSujeto) {
		Query q =
			em.createQuery("select GruposSujetos from GruposSujetos GruposSujetos "
				+ " where GruposSujetos.sujetos.idSujetos = :idS"
				+ " and GruposSujetos.puntajePercepcion IS NOT NULL");
		q.setParameter("idS", idSujeto);
		List<GruposSujetos> lista = q.getResultList();
//		if(lista.isEmpty()){
//			return false;
//		}else{
//			return true;
//		}
		 return !lista.isEmpty();
	}
	
	public List<ResultadoEval> traerDetalleGrilla5(Long idSujeto) {
		
//		Query q =
//			em.createQuery("select ResultadoEval from ResultadoEval ResultadoEval where ResultadoEval.gruposSujetos.idGrupoSujeto = :idGS ");
//		q.setParameter("idGS", idGrupoSujeto);
		//Se modificó el query por el de abajo, para evitar conflictos entre las planillas Percepción e Individual; Werner.
		Query q =
				em.createQuery("select ResultadoEval from ResultadoEval ResultadoEval where ResultadoEval.gruposSujetos.sujetos.idSujetos = :idS "
						+ "and ResultadoEval.grupoMetodologia.idGrupoMetodologia = :idGM order by ResultadoEval.plantillaEvalConfDet.orden");
			q.setParameter("idS", idSujeto);
			q.setParameter("idGM", idGrupoMetodologia);

		List<ResultadoEval> lista = q.getResultList();
		return lista;
	}
	
	public void cargarGrilla5() {
		reporteIndi=true;
		Query q =
			em.createQuery("select GruposSujetos from GruposSujetos GruposSujetos "
				+ " where GruposSujetos.gruposEvaluacion.idGruposEvaluacion = :idGE "
				+ " and GruposSujetos.sujetos.idSujetos = :idS "
				+ " order by GruposSujetos.sujetos.empleadoPuesto.persona.nombres");
		q.setParameter("idGE", grupoMetodologia.getGruposEvaluacion().getIdGruposEvaluacion());
		q.setParameter("idS", idSujetos);
		lGrilla4 = q.getResultList();
			if (lGrilla4.get(0).getPuntajeFinal() != null) 
				HabilitarAnaMat=true;
			else
				HabilitarAnaMat=false;
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
//*******************************************************************************************************
	
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

	public List<PlantillaEvalConfDet> getlGrilla1() {
		return lGrilla1;
	}

	public void setlGrilla1(List<PlantillaEvalConfDet> lGrilla1) {
		this.lGrilla1 = lGrilla1;
	}

	public List<EscalaEval> getlGrilla2() {
		return lGrilla2;
	}

	public void setlGrilla2(List<EscalaEval> lGrilla2) {
		this.lGrilla2 = lGrilla2;
	}

	public List<GruposSujetos> getlGrilla3() {
		return lGrilla3;
	}

	public void setlGrilla3(List<GruposSujetos> lGrilla3) {
		this.lGrilla3 = lGrilla3;
	}

	public Long getIdSujetos() {
		return idSujetos;
	}

	public void setIdSujetos(Long idSujetos) {
		this.idSujetos = idSujetos;
	}

	public List<GruposSujetos> getlGrilla4() {
		return lGrilla4;
	}

	public void setlGrilla4(List<GruposSujetos> lGrilla4) {
		this.lGrilla4 = lGrilla4;
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
