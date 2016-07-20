package py.com.excelsis.sicca.capacitacion.session.form;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.transaction.SystemException;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.persistence.ManagedPersistenceContext;
import org.richfaces.model.UploadItem;

import enums.ActividadEnum;
import enums.ProcesoEnum;
import enums.TiposDatos;
import py.com.excelsis.sicca.entity.Capacitaciones;
import py.com.excelsis.sicca.entity.ComisionCapacEval;
import py.com.excelsis.sicca.entity.ComisionSeleccionDet;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.Documentos;
import py.com.excelsis.sicca.entity.Entidad;
import py.com.excelsis.sicca.entity.Pais;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.entity.RevisionCapacitacion;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.form.AdmDocAdjuntoFormController;
import py.com.excelsis.sicca.session.util.JbpmUtilFormController;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.PersonaDTO;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;


@Scope(ScopeType.CONVERSATION)
@Name("admComisionFC")
public class AdmComisionFC {
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(create = true)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;
	@In(create = true)
	JbpmUtilFormController jbpmUtilFormController;

	@In(create = true)
	SeguridadUtilFormController seguridadUtilFormController;

	@In(create = true)
	SeleccionUtilFormController seleccionUtilFormController;

	private Capacitaciones capacitaciones= new Capacitaciones();
	private Long idCapacitacion;
	private List<RevisionCapacitacion> revisionCapacitacionList= new ArrayList<RevisionCapacitacion>();
	private String tipoCapac=null;
	NivelEntidadOeeUtil nivelEntidadOeeUtilTmp = new NivelEntidadOeeUtil();
	private boolean habPaisExp = true;
	private Long idPaisExp=null;
	private String docIdentidad;
	private Persona persona= new Persona();
	private boolean habPersona;
	private String puesto=null;
	private Long idPersona;
	private boolean esEdit=false;
	private List<ComisionCapacEval> capacEvalLista= new ArrayList<ComisionCapacEval>();
	private List<ComisionCapacEval> capacEvalRemoveLista= new ArrayList<ComisionCapacEval>();
	private boolean primeraEntrada=true;
	private Long idComisionEdit;
	private int indexEdit=-1;
	private String nombrePantalla="comision_edit";
	private String direccionFisica;
	private String entity;
	private String from;
	private PersonaDTO personaDTO;
	private boolean habBtnAddPersons=false;




	public void init() {
		capacitaciones=em.find(Capacitaciones.class, idCapacitacion);
		if(capacitaciones.getDatosEspecificosTipoCap()!=null)
			tipoCapac=capacitaciones.getDatosEspecificosTipoCap().getDescripcion();
		cargarCabecera();
		anexar();
		seguridadUtilFormController.validarCapacitacion(idCapacitacion, estadoAsignarComision(), ActividadEnum.CAPA_REGISTRAR_COMISION.getValor());
		nivelEntidadOeeUtilTmp.setEm(em);
				nivelEntidadOeeUtilTmp
				.setVarDestinoSinEntidad("sinEntidadIdSinEntidad2");
		nivelEntidadOeeUtilTmp
				.setVarDestinoSinNivel("sinNivelEntidadIdSinNivelEntidad2");
		nivelEntidadOeeUtilTmp
				.setVarDestinoConfigUo("configuracionUoCabIdConfiguracionUo2");
	
		nivelEntidadOeeUtilTmp.init();
		if(idPersona!=null){
			completarDatosPersona(idPersona, true);
		}
		
		if(primeraEntrada){
			findComisiones();
			primeraEntrada=false;
			limpiarComision();
		}
		
		
	}
	public void initVer(){
		capacitaciones=em.find(Capacitaciones.class, idCapacitacion);
		if(capacitaciones.getDatosEspecificosTipoCap()!=null)
			tipoCapac=capacitaciones.getDatosEspecificosTipoCap().getDescripcion();
		cargarCabecera();
		nivelEntidadOeeUtilTmp.setEm(em);
				nivelEntidadOeeUtilTmp
				.setVarDestinoSinEntidad("sinEntidadIdSinEntidad2");
		nivelEntidadOeeUtilTmp
				.setVarDestinoSinNivel("sinNivelEntidadIdSinNivelEntidad2");
		nivelEntidadOeeUtilTmp
				.setVarDestinoConfigUo("configuracionUoCabIdConfiguracionUo2");
	
		nivelEntidadOeeUtilTmp.init();
		if(idPersona!=null){
			completarDatosPersona(idPersona, true);
		}
		entity="Capacitaciones";
		direccionFisica="";
		if(primeraEntrada){
			findComisiones();
			primeraEntrada=false;
			limpiarComision();
		}
	}
	
	
	
	
	
	

	public void cargarCabecera(){
		
		//Nivel
		ConfiguracionUoCab oee=em.find(ConfiguracionUoCab.class, capacitaciones.getConfiguracionUoCab().getIdConfiguracionUo());
		Entidad enti= em.find(Entidad.class, oee.getEntidad().getIdEntidad());
		Long idSinNivelEntidad=nivelEntidadOeeUtil.getIdSinNivelEntidad(enti.getSinEntidad().getNenCodigo());
		nivelEntidadOeeUtil.setIdSinNivelEntidad(idSinNivelEntidad);
		
		//Entidad
		nivelEntidadOeeUtil.setIdSinEntidad(enti.getSinEntidad().getIdSinEntidad());
		
		//OEE
		nivelEntidadOeeUtil.setIdConfigCab(oee.getIdConfiguracionUo());
		
		if(capacitaciones.getConfiguracionUoDet()!=null)
			nivelEntidadOeeUtil.setIdUnidadOrganizativa(capacitaciones.getConfiguracionUoDet().getIdConfiguracionUoDet());
		
		nivelEntidadOeeUtil.init();
		
			
	}
	
	@SuppressWarnings("unchecked")
	private void findComisiones(){
		capacEvalLista=em.createQuery("SELECT c FROM ComisionCapacEval c " +
				" WHERE c.capacitaciones.idCapacitacion="+idCapacitacion +" and c.activo=true and c.tipo='C' " +
						" order by persona.paisByIdPaisExpedicionDoc.descripcion,persona.documentoIdentidad ").getResultList();
	}
	
	
	public void buscarPersona() throws Exception {
			
			/* Validaciones */
			if (idPaisExp == null
				|| !seguridadUtilFormController.validarInput(idPaisExp.toString(), TiposDatos.LONG.getValor(), null)) {
				return;
			}
			if (docIdentidad == null
					|| !seguridadUtilFormController.validarInput(docIdentidad.toString(), TiposDatos.STRING.getValor(), null)) {
					return;
				}
			Pais pais = em.find(Pais.class, idPaisExp);
			if (pais == null)
				return;
			/* fin validaciones */
			personaDTO = seleccionUtilFormController.buscarPersona(docIdentidad, pais.getDescripcion());
			if (personaDTO.getHabilitarBtn() == null) {
				statusMessages.add(Severity.ERROR, personaDTO.getMensaje());
				habBtnAddPersons = false;
				persona = new Persona();
			} else if (personaDTO.getHabilitarBtn()) {
				habBtnAddPersons = true;
				persona = new Persona();
				statusMessages.add(Severity.ERROR, personaDTO.getMensaje());
			} else {
				habBtnAddPersons = false;
				completarDatosPersona(personaDTO.getPersona().getIdPersona(), false);
			}
		}
	private void completarDatosPersona(Long idPersona, Boolean completo) {
		persona= em.find(Persona.class,idPersona );
		if (completo) {
			idPaisExp = persona.getPaisByIdPaisExpedicionDoc().getIdPais();
			docIdentidad = persona.getDocumentoIdentidad();
		}
	}

	

	
	public void limpiarDatosPersona() {
		persona= new Persona();
		idPersona=null;
		persona= new Persona();
		docIdentidad = null;
		habBtnAddPersons=false;
	}
	public String toFindPersona() {
		idPersona=null;
		return "/seleccion/persona/PersonaEdit.xhtml?personaFrom=/capacitacion/comision/ComisionEdit";
	}

	public String toFindPersonaList() {
		return "/seleccion/persona/PersonaList.xhtml?from=/capacitacion/comision/ComisionEdit";
	}
	
	
	@SuppressWarnings("unchecked")
	private Long idParaguay() {
		List<Pais> p =
			em.createQuery(" Select p from Pais p" + " where lower(p.descripcion) like 'paraguay'").getResultList();
		if (!p.isEmpty())
			return p.get(0).getIdPais();

		return null;
	}
	 public void anexar(){
			SimpleDateFormat sdfSoloAnio = new SimpleDateFormat("yyyy");
			String anio=sdfSoloAnio.format(new Date());
			entity="Capacitaciones";
			direccionFisica="/SICCA/"+anio+"/OEE/"+capacitaciones.getConfiguracionUoCab().getIdConfiguracionUo()+"/UO/"
			+capacitaciones.getConfiguracionUoDet().getIdConfiguracionUoDet()+"/C/"+idCapacitacion;
			from="capacitacion/comision/ComisionEdit?capacitacionesIdCapacitacion="+idCapacitacion;
			
			
		}
	 
	 public void save(){
		 try {
			 if(capacEvalLista.isEmpty()){
				 statusMessages.add(Severity.ERROR,"Debe Ingresar al menos un Integrante de la Comisión");
				 return ;
			 }
			ComisionCapacEval del= new ComisionCapacEval();
			 for (ComisionCapacEval c : capacEvalRemoveLista) {
				 try{
					 	del=(ComisionCapacEval)em.createQuery("select c from ComisionCapacEval c where c.idComision =:id").setParameter("id",c.getIdComision() ).getSingleResult();
						borrar(del);
					}catch(NoResultException e){
					}
			}
			for (int i = 0; i < capacEvalLista.size(); i++) {
				 ComisionCapacEval comision= capacEvalLista.get(i);
				if(comision.getIdComision()!=null){
					em.merge(comision);
				}else
					em.persist(comision);
				em.flush();
			
			}
				
			 statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
						.getString("GENERICO_MSG"));
			limpiarComision();
			capacEvalRemoveLista= new ArrayList<ComisionCapacEval>();
			
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add(Severity.ERROR,"Ha ocurrido un error: "+e.getMessage());
			
		}
	 }
	 
	 private  void borrar(ComisionCapacEval comision){
			ManagedPersistenceContext persistenceContext = (ManagedPersistenceContext)Contexts.getConversationContext().get("entityManager");
			EntityManager em;
			try {
				em = persistenceContext.getEntityManager();
				em.remove(comision);
				em.flush();
			} catch (NamingException e) {
				e.printStackTrace();
			} catch (SystemException e) {
				e.printStackTrace();
			}
		}
	 
		 private boolean validarRepetido() {
				if (capacEvalLista != null && capacEvalLista.size() > 0) {
					for (ComisionCapacEval det : capacEvalLista) {
						if (det.getPersona() != null
							&& det.getPersona().getIdPersona() != null
							&& det.getPersona().getIdPersona().longValue() == persona.getIdPersona().longValue()) {
							statusMessages.add(Severity.ERROR, "Ya existe la persona seleccionada. Verifique");
							return false;
						}
					}
				}
				return true;
			}
		 private boolean validarRepetidoUp() {
				if (capacEvalLista != null && capacEvalLista.size() > 0) {
					for (int i = 0; i < capacEvalLista.size(); i++) {
						if (capacEvalLista.get(i).getPersona() != null
							&& capacEvalLista.get(i).getPersona().getIdPersona() != null
							&& capacEvalLista.get(i).getPersona().getIdPersona().longValue() == persona.getIdPersona().longValue()) {
							if(i!=indexEdit){
								statusMessages.add(Severity.ERROR, "Ya existe la persona seleccionada. Verifique");
								return false;
							}
							
						}
					}
				}
				return true;
			}
	
	 
	public String finalizarTarea(){
		try {
			if(!tieneDoc()){
				statusMessages.add(Severity.ERROR,"Debe anexar previamente Documentos Adjuntos del Comité.");
				return null;
			}
			if(capacEvalLista.isEmpty()){
				statusMessages.add(Severity.ERROR,"Debe Agregar al menos un Integrante. Verifique");
				return null;
			}
			
			 /**
			 * SE ACTUALIZA LA TABLA
			 * **/
			
			capacitaciones.setEstado(estadoRealizarEvaluacion());
			capacitaciones.setUsuMod(usuarioLogueado.getCodigoUsuario());
			capacitaciones.setFechaMod(new Date());
			em.merge(capacitaciones);
			/**
			 *SE PASA A SGT. TAREA
			 * */
			jbpmUtilFormController.setActividadActual(ActividadEnum.CAPA_REGISTRAR_COMISION);
			jbpmUtilFormController.setActividadSiguiente(ActividadEnum.CAPA_EVALUAR_POSTULANTES);
		    jbpmUtilFormController.setTransition("regCom_TO_evaPos");
		    jbpmUtilFormController.setProcesoEnum(ProcesoEnum.CAPACITACION);
		    jbpmUtilFormController.setCapacitacion(capacitaciones);
		    
		    if (jbpmUtilFormController.nextTask()){
				em.flush();
			}
			
		   
			 statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
						.getString("GENERICO_MSG"));
			return "next";
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add(Severity.ERROR,"Ha ocurrido un error: "+e.getMessage());
			return null;
		}
	}
	@SuppressWarnings("unchecked")
	private boolean tieneDoc(){
		List<Documentos> docs=em.createQuery("Select d from Documentos d where d.idTabla="+idCapacitacion+
				" and d.nombreTabla= 'Capacitaciones' and d.nombrePantalla='comision_edit'").getResultList();
		return !docs.isEmpty();
	}
	@SuppressWarnings("unchecked")
	private int estadoRealizarEvaluacion(){
		List<Referencias> referencias= em.createQuery(" Select r from Referencias r " +
				" where r.dominio='ESTADOS_CAPACITACION' and r.descAbrev= 'REALIZAR EVALUACION' and r.activo=true").getResultList();
		if(referencias.isEmpty())
			return 0;
		 return referencias.get(0).getValorNum(); 
	}
	@SuppressWarnings("unchecked")
	private int estadoAsignarComision(){
		List<Referencias> referencias= em.createQuery(" Select r from Referencias r " +
				" where r.dominio='ESTADOS_CAPACITACION' and r.descAbrev= 'ASIGNAR COMISION' and r.activo=true").getResultList();
		if(referencias.isEmpty())
			return 0;
		 return referencias.get(0).getValorNum(); 
	}
	
	
	public void addComision(){
		try {
			if(persona.getIdPersona()==null){
				statusMessages.add(Severity.ERROR,"Debe seleccionar una Persona antes de realizar esta acción");
				return ;
			}
			if(!validarRepetido())
				return;
			if(puesto==null|| puesto.trim().equals("")){
				statusMessages.add(Severity.ERROR,"Debe completar el campo puesto antes de realizar esta acción");
				return ;
			}
			ComisionCapacEval comisionCapacEval= new ComisionCapacEval();
			comisionCapacEval.setPersona(persona);
			comisionCapacEval.setPuesto(puesto);
			if(nivelEntidadOeeUtilTmp.getIdConfigCab()!=null)
				comisionCapacEval.setConfiguracionUo(em.find(ConfiguracionUoCab.class, nivelEntidadOeeUtilTmp.getIdConfigCab()));
			comisionCapacEval.setActivo(true);
			comisionCapacEval.setFechaAlta(new Date());
			comisionCapacEval.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			comisionCapacEval.setCapacitaciones(capacitaciones);
			comisionCapacEval.setTipo("C");
			capacEvalLista.add(comisionCapacEval);
			limpiarComision();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void limpiarComision(){
		try {
			puesto=null;
			persona= new Persona();
			idPaisExp=idParaguay();
			idPersona=null;
			nivelEntidadOeeUtilTmp.limpiar();
			docIdentidad=null;
			esEdit=false;
			indexEdit=-1;
			habPaisExp=true;
			habPersona=false;
			idComisionEdit=null;
			habBtnAddPersons=false;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void upComision(){
		try {
			if(persona.getIdPersona()==null){
				statusMessages.add(Severity.ERROR,"Debe seleccionar una Persona antes de realizar esta acción");
				return ;
			}
			if(!validarRepetidoUp())
				return;
			if(puesto==null|| puesto.trim().equals("")){
				statusMessages.add(Severity.ERROR,"Debe completar el campo puesto antes de realizar esta acción");
				return ;
			}
			ComisionCapacEval comisionCapacEval= new ComisionCapacEval();
			if(idComisionEdit!=null){
				comisionCapacEval=em.find(ComisionCapacEval.class,idComisionEdit);
				comisionCapacEval.setFechaMod(new Date());
				comisionCapacEval.setUsuMod(usuarioLogueado.getCodigoUsuario());
			}else{
				
				comisionCapacEval.setFechaAlta(new Date());
				comisionCapacEval.setUsuAlta(usuarioLogueado.getCodigoUsuario());
				comisionCapacEval.setActivo(true);
			}
			comisionCapacEval.setActivo(true);
			comisionCapacEval.setPersona(persona);
			comisionCapacEval.setPuesto(puesto);
			if(nivelEntidadOeeUtilTmp.getIdConfigCab()!=null)
				comisionCapacEval.setConfiguracionUo(em.find(ConfiguracionUoCab.class, nivelEntidadOeeUtilTmp.getIdConfigCab()));
			
			
			comisionCapacEval.setCapacitaciones(capacitaciones);
			comisionCapacEval.setTipo("C");
			capacEvalLista.remove(indexEdit);
			capacEvalLista.add(indexEdit, comisionCapacEval);
		
			limpiarComision();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void eliminarComision(int index){
		try {
			ComisionCapacEval aux= capacEvalLista.get(index);
			if(aux.getIdComision()!=null)
				capacEvalRemoveLista.add(aux);
			capacEvalLista.remove(index);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void editarComision(int index){
		try {
			ComisionCapacEval aux= capacEvalLista.get(index);
			idComisionEdit=aux.getIdComision();
			puesto=aux.getPuesto();
			if(aux.getConfiguracionUo()!=null)
				cargarOeeTmp(aux.getConfiguracionUo());
			idPersona= aux.getPersona().getIdPersona();
			persona=em.find(Persona.class,idPersona);
			idPaisExp=persona.getPaisByIdPaisExpedicionDoc().getIdPais();
			docIdentidad=persona.getDocumentoIdentidad();
			habPaisExp=true;
			habPersona=false;
			esEdit=true;
			indexEdit=index;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void cargarOeeTmp(ConfiguracionUoCab oee) {

		// Nivel
		Long idSinNivelEntidad = nivelEntidadOeeUtilTmp
				.getIdSinNivelEntidad(oee.getEntidad().getSinEntidad()
						.getNenCodigo());
		nivelEntidadOeeUtilTmp.setIdSinNivelEntidad(idSinNivelEntidad);

		// Entidad
		nivelEntidadOeeUtilTmp.setIdSinEntidad(oee.getEntidad()
				.getSinEntidad().getIdSinEntidad());

		// OEE
		nivelEntidadOeeUtilTmp.setIdConfigCab(oee
				.getIdConfiguracionUo());

		nivelEntidadOeeUtilTmp.init();

	}
	public boolean habEliminar(int index){
		if(indexEdit==index)
			return false;
		
		return true;
	}
	
	/** GETTER'S && SETTER'S **/
	
	public NivelEntidadOeeUtil getNivelEntidadOeeUtil() {
		return nivelEntidadOeeUtil;
	}

	public void setNivelEntidadOeeUtil(NivelEntidadOeeUtil nivelEntidadOeeUtil) {
		this.nivelEntidadOeeUtil = nivelEntidadOeeUtil;
	}

	public Capacitaciones getCapacitaciones() {
		return capacitaciones;
	}

	public void setCapacitaciones(Capacitaciones capacitaciones) {
		this.capacitaciones = capacitaciones;
	}

	public Long getIdCapacitacion() {
		return idCapacitacion;
	}

	public void setIdCapacitacion(Long idCapacitacion) {
		this.idCapacitacion = idCapacitacion;
	}

	public List<RevisionCapacitacion> getRevisionCapacitacionList() {
		return revisionCapacitacionList;
	}

	public void setRevisionCapacitacionList(
			List<RevisionCapacitacion> revisionCapacitacionList) {
		this.revisionCapacitacionList = revisionCapacitacionList;
	}

	public String getTipoCapac() {
		return tipoCapac;
	}

	public void setTipoCapac(String tipoCapac) {
		this.tipoCapac = tipoCapac;
	}

	public NivelEntidadOeeUtil getNivelEntidadOeeUtilTmp() {
		return nivelEntidadOeeUtilTmp;
	}

	public void setNivelEntidadOeeUtilTmp(
			NivelEntidadOeeUtil nivelEntidadOeeUtilTmp) {
		this.nivelEntidadOeeUtilTmp = nivelEntidadOeeUtilTmp;
	}

	public boolean isHabPaisExp() {
		return habPaisExp;
	}

	public void setHabPaisExp(boolean habPaisExp) {
		this.habPaisExp = habPaisExp;
	}

	public Long getIdPaisExp() {
		return idPaisExp;
	}

	public void setIdPaisExp(Long idPaisExp) {
		this.idPaisExp = idPaisExp;
	}

	public String getDocIdentidad() {
		return docIdentidad;
	}

	public void setDocIdentidad(String docIdentidad) {
		this.docIdentidad = docIdentidad;
	}

	public Persona getPersona() {
		return persona;
	}

	public void setPersona(Persona persona) {
		this.persona = persona;
	}

	public boolean isHabPersona() {
		return habPersona;
	}

	public void setHabPersona(boolean habPersona) {
		this.habPersona = habPersona;
	}

	public String getPuesto() {
		return puesto;
	}

	public void setPuesto(String puesto) {
		this.puesto = puesto;
	}

	public Long getIdPersona() {
		return idPersona;
	}

	public void setIdPersona(Long idPersona) {
		this.idPersona = idPersona;
	}

	public boolean isEsEdit() {
		return esEdit;
	}

	public void setEsEdit(boolean esEdit) {
		this.esEdit = esEdit;
	}

	public List<ComisionCapacEval> getCapacEvalLista() {
		return capacEvalLista;
	}

	public void setCapacEvalLista(List<ComisionCapacEval> capacEvalLista) {
		this.capacEvalLista = capacEvalLista;
	}

	public List<ComisionCapacEval> getCapacEvalRemoveLista() {
		return capacEvalRemoveLista;
	}

	public void setCapacEvalRemoveLista(
			List<ComisionCapacEval> capacEvalRemoveLista) {
		this.capacEvalRemoveLista = capacEvalRemoveLista;
	}

	public boolean isPrimeraEntrada() {
		return primeraEntrada;
	}

	public void setPrimeraEntrada(boolean primeraEntrada) {
		this.primeraEntrada = primeraEntrada;
	}

	public Long getIdComisionEdit() {
		return idComisionEdit;
	}

	public void setIdComisionEdit(Long idComisionEdit) {
		this.idComisionEdit = idComisionEdit;
	}

	public String getNombrePantalla() {
		return nombrePantalla;
	}

	public void setNombrePantalla(String nombrePantalla) {
		this.nombrePantalla = nombrePantalla;
	}

	public String getDireccionFisica() {
		return direccionFisica;
	}

	public void setDireccionFisica(String direccionFisica) {
		this.direccionFisica = direccionFisica;
	}

	public String getEntity() {
		return entity;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public int getIndexEdit() {
		return indexEdit;
	}

	public void setIndexEdit(int indexEdit) {
		this.indexEdit = indexEdit;
	}

	public PersonaDTO getPersonaDTO() {
		return personaDTO;
	}

	public void setPersonaDTO(PersonaDTO personaDTO) {
		this.personaDTO = personaDTO;
	}

	public boolean isHabBtnAddPersons() {
		return habBtnAddPersons;
	}

	public void setHabBtnAddPersons(boolean habBtnAddPersons) {
		this.habBtnAddPersons = habBtnAddPersons;
	}

}
