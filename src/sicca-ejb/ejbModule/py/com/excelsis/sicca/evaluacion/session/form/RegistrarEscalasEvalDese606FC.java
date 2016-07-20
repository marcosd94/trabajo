package py.com.excelsis.sicca.evaluacion.session.form;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage.Severity;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.security.AuthorizationException;

import py.com.excelsis.sicca.entity.EscalaEval;
import py.com.excelsis.sicca.entity.GrupoMetodologia;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.JbpmUtilFormController;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.ReferenciasUtilFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("registrarEscalasEvalDese606FC")
public class RegistrarEscalasEvalDese606FC {
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

	private String evalTitle;
	private String elFrom;
	private String grupo;
	private String plantilla;
	private GrupoMetodologia grupoMetodologia;
	private Long idGrupoMetodologia;
	private Integer desde;
	private Integer hasta;
	private String descripcion;
	private String nivelEvaluacion;
	private List<EscalaEval> lGrilla1;
	private List<EscalaEval> lGrilla1Eliminar;
	private Boolean modActualizar = false;
	private Integer idElemUpdate;
	
	//**********************************************agregado; Werner.
	private String idGrupoMetodologiaCadena;
	private List<Long> idGrupoMetodologia2;
	private List<EscalaEval> lGrilla2;
	private List<EscalaEval> lGrilla3;
	private List<EscalaEval> lGrilla2Eliminar;
	private String descripcionVieja;
	private String radioEscala;
	private List<String> EscalaDescrip;
	private List<String> EscalaNvlEval;
	private List<Integer> Escala100Desde;
	private List<Integer> Escala100Hasta;
	private Boolean guardar;
	//**********************************************
	
	public void init() {
//		if (!precondInit())
//			throw new AuthorizationException(SeamResourceBundle.getBundle().getString("seam_authorization_exception"));

//agregado, permitirá adelante referenciar la escala a todas las plantillas seleccionadas; Werner.**********
		String aux [] = idGrupoMetodologiaCadena.split(",");
		idGrupoMetodologia2 = new ArrayList<Long>();
		for (int i = 0; i < aux.length; i++) {
			idGrupoMetodologia2.add(Long.parseLong(aux[i]));
		}
//*********************************************************************************************
		guardar = false;
		cargarCabecera();
		cargarGrilla1();
		radioEscala = "1-5";
	}
	
	private Boolean precondInit() {
		if (idGrupoMetodologia != null) {
//			grupoMetodologia = em.find(GrupoMetodologia.class, idGrupoMetodologia);
			return true;	
//			if (usuarioLogueado.getConfiguracionUoDet().getIdConfiguracionUoDet().longValue() == grupoMetodologia.getGruposEvaluacion().getEvaluacionDesempeno().getConfiguracionUoDet().getIdConfiguracionUoDet().longValue()) {
//				return true;
//			}
		}
		return false;
	}

	private void cargarCabecera() {
//		nivelEntidadOeeUtil.setIdConfigCab(usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo());
//		nivelEntidadOeeUtil.setIdUnidadOrganizativa(usuarioLogueado.getConfiguracionUoDet().getIdConfiguracionUoDet());
		nivelEntidadOeeUtil.init2();
		grupoMetodologia = em.find(GrupoMetodologia.class, idGrupoMetodologia2.get(0));
		evalTitle = grupoMetodologia.getGruposEvaluacion().getEvaluacionDesempeno().getTitulo();
//		plantilla = grupoMetodologia.getDatosEspecifMetod().getDescripcion();
		grupo = grupoMetodologia.getGruposEvaluacion().getGrupo();

	}

	private void cargarGrilla1() {
		Query q =
				em.createQuery("select EscalaEval from EscalaEval EscalaEval "
					+ " where EscalaEval.activo is true and EscalaEval.grupoMetodologia.idGrupoMetodologia = :idGM "
					+ " order by EscalaEval.idEscalaEval");
			//		+ " where EscalaEval.activo is true and EscalaEval.grupoMetodologia.idGrupoMetodologia = :idGM ");		//original
			//q.setParameter("idGM", idGrupoMetodologia);		//original
			q.setParameter("idGM", idGrupoMetodologia2.get(0));
			
			lGrilla1 = q.getResultList();

//agregado; Werner.***********************************en paralelo	
		Query q2 =
			em.createQuery("select EscalaEval from EscalaEval EscalaEval "
				+ " where EscalaEval.activo is true and EscalaEval.grupoMetodologia.idGrupoMetodologia IN("+idGrupoMetodologiaCadena+") order by EscalaEval.idEscalaEval");
//solo es necesario una escala, ya que todas las plantillas usan la misma, (ineficiente en la BD, por el momento se adapta a la idea original); Werner.
		lGrilla2 = q2.getResultList();
//*****************************************************
		
	}

	private Boolean precondAgregar() {
//		if ((descripcion == null || descripcion.trim().isEmpty())
//			|| (desde == null || hasta == null) || nivelEvaluacion.trim().isEmpty()) {
//			statusMessages.add(org.jboss.seam.international.StatusMessage.Severity.ERROR, SeamResourceBundle.getBundle().getString("GENERICO_CAMPOS_REQUERIDOS"));
//			return false;
//		}
//		if (desde.intValue() > hasta.intValue()) {
//			statusMessages.add(org.jboss.seam.international.StatusMessage.Severity.ERROR, "El valor Desde no puede ser mayor al valor Hasta");
//			return false;
//		}
//		if (descripcion.length() > 250) {
//			statusMessages.add(org.jboss.seam.international.StatusMessage.Severity.ERROR, SeamResourceBundle.getBundle().getString("GENERICO_SUPERADA_CANT_MAX_CAR")
//				+ ". Campo Descripción(250)");
//			return false;
//		}
//		if (nivelEvaluacion.length() > 500) {
//			statusMessages.add(org.jboss.seam.international.StatusMessage.Severity.ERROR, SeamResourceBundle.getBundle().getString("GENERICO_SUPERADA_CANT_MAX_CAR")
//				+ ". Campo Nivel de Evaluación(500)");
//			return false;
//		}
		if (lGrilla1 != null) {
			for (EscalaEval o : lGrilla1) {
				if (idElemUpdate == null) {
					if (o.getDescripcion().equalsIgnoreCase(descripcion)) {
						statusMessages.add(org.jboss.seam.international.StatusMessage.Severity.ERROR, "El registro ya existe. Verifique");
						return false;
					}	
					if (o.getDesde().intValue() == desde.intValue()
						&& o.getHasta().intValue() == hasta.intValue()) {
						statusMessages.add(org.jboss.seam.international.StatusMessage.Severity.ERROR, "El registro ya existe. Verifique");
						return false;
					}
				} else {	
					if (o.getDescripcion().equalsIgnoreCase(descripcion) && o.getDesde().intValue() == desde.intValue()
						&& o.getHasta().intValue() == hasta.intValue() && o.getNivelEvaluacion().equalsIgnoreCase(nivelEvaluacion)) {
						statusMessages.add(org.jboss.seam.international.StatusMessage.Severity.ERROR, "El registro ya existe. Verifique");
						return false;
					}
				}

			}

		}
		return true;
	}

	public void agregar() {
		cargandoEscala();
		for (int j = 0; j < EscalaDescrip.size(); j++) {//agregado

			if (lGrilla1 == null)
				lGrilla1 = new ArrayList<EscalaEval>();
			EscalaEval ee = new EscalaEval();
			ee.setActivo(true);
			ee.setFechaAlta(new Date());
			ee.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			ee.setDescripcion(EscalaDescrip.get(j));
			if (radioEscala.contentEquals("1-5")) {
				ee.setDesde(j+1);
				ee.setHasta(j+1);
			}else{
				ee.setDesde(Escala100Desde.get(j));
				ee.setHasta(Escala100Hasta.get(j));
			}
			ee.setGrupoMetodologia(new GrupoMetodologia());
	//		ee.getGrupoMetodologia().setIdGrupoMetodologia(idGrupoMetodologia);//original
			ee.getGrupoMetodologia().setIdGrupoMetodologia(idGrupoMetodologia2.get(0));//modificado; Werner.
//			ee.setNivelEvaluacion(nivelEvaluacion);
			ee.setNivelEvaluacion(EscalaNvlEval.get(j));
			lGrilla1.add(ee);
	//		limpiar();
	
	//agregado; Werner **********************************************************************************************grilla2 en paralelo
	//(ineficiente en la BD, redundancia de registros; por el momento se adapta a la idea original, misma escala para todas las metodologías).
			for (int i = 0; i < idGrupoMetodologia2.size(); i++) {
				EscalaEval ee2 = new EscalaEval();
				ee2.setActivo(true);
				ee2.setFechaAlta(new Date());
				ee2.setUsuAlta(usuarioLogueado.getCodigoUsuario());
//				ee2.setDescripcion(descripcion);
//				ee2.setDesde(desde);
//				ee2.setHasta(hasta);
				ee2.setDescripcion(EscalaDescrip.get(j));
				if (radioEscala.contentEquals("1-5")) {
					ee2.setDesde(j+1);
					ee2.setHasta(j+1);
				}else{
					ee2.setDesde(Escala100Desde.get(j));
					ee2.setHasta(Escala100Hasta.get(j));
				}
				ee2.setGrupoMetodologia(new GrupoMetodologia());
				ee2.getGrupoMetodologia().setIdGrupoMetodologia(idGrupoMetodologia2.get(i));//agregado; Werner.
//				ee2.setNivelEvaluacion(nivelEvaluacion);
				ee2.setNivelEvaluacion(EscalaNvlEval.get(j));
				lGrilla2.add(ee2);
			}
	//****************************************************************************************************************		
			limpiar();
		}
		limpiandoEscala();//agregado
		guardar=true;
	}

	public void actualizar() {
		if (!precondAgregar()) {
			return;
		}
		if (idElemUpdate != null) {
			EscalaEval ee = lGrilla1.get(idElemUpdate.intValue());
			ee.setDescripcion(descripcion);
			ee.setDesde(desde);
			ee.setHasta(hasta);
			ee.setNivelEvaluacion(nivelEvaluacion);//opcional...
			
//agregado; Werner.**********************************************************grilla2 en paralelo
			for (int i = 0; i < lGrilla2.size(); i++) {
				if (lGrilla2.get(i).getDescripcion().toString().contentEquals(descripcionVieja)) {
					EscalaEval ee2 = lGrilla2.get(i);
					ee2.setDescripcion(descripcion);
					ee2.setDesde(desde);
					ee2.setHasta(hasta);
					ee2.setNivelEvaluacion(nivelEvaluacion);//opcional...
				}
			}
//****************************************************************************			
			
			idElemUpdate = null;
			limpiarNuevo();
		}

	}

	public void editar(int index) {
		descripcionVieja = lGrilla1.get(index).getDescripcion();
		limpiar();
		if (index < lGrilla1.size()) {
			idElemUpdate = index;
			EscalaEval ee = lGrilla1.get(index);
			descripcion = ee.getDescripcion();
			desde = ee.getDesde();
			hasta = ee.getHasta();
			nivelEvaluacion = ee.getNivelEvaluacion();
		}
	}

	public void eliminar(int index) {
		if (lGrilla1Eliminar == null){
			lGrilla1Eliminar = new ArrayList<EscalaEval>();
		
//agregado, en paralelo; Werner.***********************************************			
			lGrilla2Eliminar = new ArrayList<EscalaEval>();}
		List<Integer> indice = new ArrayList<Integer>();
//*****************************************************************************
		
		if (lGrilla1.get(index).getIdEscalaEval() != null) {
			lGrilla1Eliminar.add(lGrilla1.get(index));
			
//agregado, en paralelo; Werner.***********************************************
		for (int i = 0; i < lGrilla2.size(); i++) {
			if (lGrilla1.get(index).getUsuAlta().contentEquals(lGrilla2.get(i).getUsuAlta().toString()) 
					&& lGrilla1.get(index).getDescripcion().contentEquals(lGrilla2.get(i).getDescripcion().toString()) ) {
				lGrilla2Eliminar.add(lGrilla2.get(i));
				indice.add(i);
			}
		}
//*****************************************************************************
			
		}
		lGrilla1.remove(index);

//agregado, en paralelo; Werner.***********************************************		
		for (int j = 0; j < indice.size(); j++) {
			lGrilla2.remove(indice.get(j));
		}
//*****************************************************************************
		
	}

	private Boolean precondSave() {
		if (lGrilla1 == null) {
			statusMessages.add(org.jboss.seam.international.StatusMessage.Severity.ERROR, "No hay nada que guardar");
			return false;
		}
		return true;
	}

	public String save() {
		if (!precondSave())
			return "FAIL";
		try {
//			for (EscalaEval o : lGrilla1) {//original
			for (EscalaEval o : lGrilla2) {
				if (o.getIdEscalaEval() == null) {
					em.persist(o);
				} 
				else {
//					o.setFechaMod(new Date());
//					o.setUsuMod(usuarioLogueado.getCodigoUsuario());
//					em.merge(o);
					em.remove(o);
				}
			}
			if (lGrilla1Eliminar != null) {
//				for (EscalaEval o : lGrilla1Eliminar) {//original
				for (EscalaEval o : lGrilla2Eliminar) {
					if (o.getIdEscalaEval() != null) {
//						o.setActivo(false);
//						o.setFechaMod(new Date());
//						o.setUsuMod(usuarioLogueado.getCodigoUsuario());
//						em.merge(o);
						
						em.remove(o);
					}
				}
			}
			em.flush();
			cargarGrilla1();
			guardar = false;
			statusMessages.add(org.jboss.seam.international.StatusMessage.Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
			return "OK";
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add(org.jboss.seam.international.StatusMessage.Severity.ERROR, SeamResourceBundle.getBundle().getString("GENERICO_NO_MSG"));
		}

		return null;
	}

	public void limpiarNuevo() {
		descripcion = null;
		nivelEvaluacion = null;
		hasta = null;
		desde = null;

	}

	public void limpiar() {
		descripcion = null;
		nivelEvaluacion = null;
		hasta = null;
		desde = null;
		lGrilla1Eliminar = null;
		idElemUpdate = null;

	}

	private void cargandoEscala(){//agregado
			EscalaDescrip = new ArrayList<String>();
			EscalaDescrip.add("REPROBADO");EscalaDescrip.add("ACEPTABLE");EscalaDescrip.add("BUENO");
			EscalaDescrip.add("MUY BUENO");EscalaDescrip.add("SOBRESALIENTE");
			EscalaNvlEval = new ArrayList<String>();
			EscalaNvlEval.add("Representa un nivel insuficiente de desempeño o de desarrollo de las tareas.");
			EscalaNvlEval.add("Representa un nivel aceptable de desempeño o de desarrollo de las tareas.");
			EscalaNvlEval.add("Representa un nivel bueno de desempeño o de desarrollo de las tareas.");
			EscalaNvlEval.add("Representa un nivel suficiente de desempeño de las tareas.");
			EscalaNvlEval.add("Representa un nivel alto de desempeño o desarrollo de las tareas.");
			Escala100Desde = new ArrayList<Integer>();
			Escala100Desde.add(0);Escala100Desde.add(60);Escala100Desde.add(71);Escala100Desde.add(81);Escala100Desde.add(91);
			Escala100Hasta = new ArrayList<Integer>();
			Escala100Hasta.add(59);Escala100Hasta.add(70);Escala100Hasta.add(80);Escala100Hasta.add(90);Escala100Hasta.add(100);
	}
	private void limpiandoEscala(){//agregado
		for (int i = 0; i < EscalaDescrip.size(); i++) {
			EscalaDescrip.remove(i);
			EscalaNvlEval.remove(i);
			Escala100Desde.remove(i);
			Escala100Hasta.remove(i);
		}
		
	}
	
	public void eliminar2() {//agregado
		for (int k = 0; k < 5; k++) {
			if (lGrilla1Eliminar == null){
				lGrilla1Eliminar = new ArrayList<EscalaEval>();	
				lGrilla2Eliminar = new ArrayList<EscalaEval>();
			}
			List<Integer> indice = new ArrayList<Integer>();
			lGrilla1Eliminar.add(lGrilla1.get(0));
				for (int i = 0; i < lGrilla2.size(); i++) {
					if (lGrilla1.get(0).getUsuAlta().contentEquals(lGrilla2.get(i).getUsuAlta().toString()) 
							&& lGrilla1.get(0).getDescripcion().contentEquals(lGrilla2.get(i).getDescripcion().toString()) ) {
						lGrilla2Eliminar.add(lGrilla2.get(i));
						indice.add(i);
					}
				}
			lGrilla1.remove(0);
			for (int j = 0; j < indice.size(); j++) {
				lGrilla2.remove(indice.get(j));
			}
			
		}
		guardar = true;
	}
	
	public boolean habilitandoBotones() {
		Query q =
			em.createQuery("select EscalaEval from EscalaEval EscalaEval "
				+ " where EscalaEval.activo is true and EscalaEval.grupoMetodologia.idGrupoMetodologia IN("+idGrupoMetodologiaCadena+") order by EscalaEval.idEscalaEval");
		lGrilla3 = q.getResultList();
		return !lGrilla3.isEmpty();
	}
	
	public String getEvalTitle() {
		return evalTitle;
	}

	public void setEvalTitle(String evalTitle) {
		this.evalTitle = evalTitle;
	}

	public String getElFrom() {
		return elFrom;
	}

	public void setElFrom(String elFrom) {
		this.elFrom = elFrom;
	}

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

	public Integer getDesde() {
		return desde;
	}

	public void setDesde(Integer desde) {
		this.desde = desde;
	}

	public Integer getHasta() {
		return hasta;
	}

	public void setHasta(Integer hasta) {
		this.hasta = hasta;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getNivelEvaluacion() {
		return nivelEvaluacion;
	}

	public void setNivelEvaluacion(String nivelEvaluacion) {
		this.nivelEvaluacion = nivelEvaluacion;
	}

	public List<EscalaEval> getlGrilla1() {
		return lGrilla1;
	}

	public void setlGrilla1(List<EscalaEval> lGrilla1) {
		this.lGrilla1 = lGrilla1;
	}

	public Boolean getModActualizar() {
		return modActualizar;
	}

	public void setModActualizar(Boolean modActualizar) {
		this.modActualizar = modActualizar;
	}

	public GrupoMetodologia getGrupoMetodologia() {
		return grupoMetodologia;
	}

	public void setGrupoMetodologia(GrupoMetodologia grupoMetodologia) {
		this.grupoMetodologia = grupoMetodologia;
	}

	public Integer getIdElemUpdate() {
		return idElemUpdate;
	}

	public void setIdElemUpdate(Integer idElemUpdate) {
		this.idElemUpdate = idElemUpdate;
	}

	public String getIdGrupoMetodologiaCadena() {
		return idGrupoMetodologiaCadena;
	}

	public void setIdGrupoMetodologiaCadena(String idGrupoMetodologiaCadena) {
		this.idGrupoMetodologiaCadena = idGrupoMetodologiaCadena;
	}

	public List<EscalaEval> getlGrilla2() {
		return lGrilla2;
	}

	public void setlGrilla2(List<EscalaEval> lGrilla2) {
		this.lGrilla2 = lGrilla2;
	}

	public List<EscalaEval> getlGrilla2Eliminar() {
		return lGrilla2Eliminar;
	}

	public void setlGrilla2Eliminar(List<EscalaEval> lGrilla2Eliminar) {
		this.lGrilla2Eliminar = lGrilla2Eliminar;
	}

	public String getDescripcionVieja() {
		return descripcionVieja;
	}

	public void setDescripcionVieja(String descripcionVieja) {
		this.descripcionVieja = descripcionVieja;
	}

	public String getRadioEscala() {
		return radioEscala;
	}

	public void setRadioEscala(String radioEscala) {
		this.radioEscala = radioEscala;
	}

	public Boolean getGuardar() {
		return guardar;
	}

	public void setGuardar(Boolean guardar) {
		this.guardar = guardar;
	}

}
