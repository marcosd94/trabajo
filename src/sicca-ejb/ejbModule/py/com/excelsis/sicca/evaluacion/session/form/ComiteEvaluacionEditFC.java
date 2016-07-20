package py.com.excelsis.sicca.evaluacion.session.form;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;

import py.com.excelsis.sicca.entity.ComisionCapacEval;
import py.com.excelsis.sicca.entity.ComisionEval;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.EvaluacionDesempeno;
import py.com.excelsis.sicca.entity.Pais;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.JbpmUtilFormController;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.PersonaDTO;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.General;
import py.com.excelsis.sicca.util.JpaResourceBean;
import enums.TiposDatos;


@Scope(ScopeType.CONVERSATION)
@Name("comiteEvaluacionEditFC")
public class ComiteEvaluacionEditFC {
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;   
	@In(required = false)
	Usuario usuarioLogueado;

	@In(create = true)
	JbpmUtilFormController jbpmUtilFormController;

	@In(create = true)
	SeguridadUtilFormController seguridadUtilFormController;

	@In(create = true)
	SeleccionUtilFormController seleccionUtilFormController;

	
	private Long idEvaluacionDesempeno;
	
	private String tipoCapac=null;
	NivelEntidadOeeUtil nivelEntidadOeeUtilTmp = new NivelEntidadOeeUtil();
	
	private ComisionEval comisionEval= new ComisionEval();
	private EvaluacionDesempeno evaluacionDesempeno= new EvaluacionDesempeno();
	private Long idComisionEval=null;
	private Long idPaisExp=null;
	private String docIdentidad=null;
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
	private String nombrePantalla="comiteEvaluacion_edit";
	private String direccionFisica;
	private String entity;
	private String from;
	private PersonaDTO personaDTO;
	private boolean habBtnAddPersons=false;
	private String comite=null;
	private Long idReturn;




	public void init() throws Exception {
		
		if(idPersona!=null){
			if (!seguridadUtilFormController.validarInput(idPersona.toString(), TiposDatos.LONG.getValor(), null)) {
				return ;
			}
			completarDatosPersona(idPersona, true);
		}
		if(idEvaluacionDesempeno!=null)
			seguridadUtilFormController.validarEvaluacionEstado(idEvaluacionDesempeno, seguridadUtilFormController.estadoActividades("ESTADOS_EVALUACION_DESEMPENO", "CARGA"));
		
		nivelEntidadOeeUtilTmp.setEm(em);
		nivelEntidadOeeUtilTmp
		.setVarDestinoSinEntidad("sinEntidadIdSinEntidad2");
		nivelEntidadOeeUtilTmp
				.setVarDestinoSinNivel("sinNivelEntidadIdSinNivelEntidad2");
		nivelEntidadOeeUtilTmp
				.setVarDestinoConfigUo("configuracionUoCabIdConfiguracionUo2");
		
		nivelEntidadOeeUtilTmp.init();
		cargarCabecera();
		if(primeraEntrada){
			idReturn=idEvaluacionDesempeno;
			if(idComisionEval!=null){
				
				obtenerDatos();
			}
				
			
			primeraEntrada=false;
			limpiarComision();
		}
		
		anexar();
	
		
		
	}
	private void obtenerDatos(){
		comisionEval= em.find(ComisionEval.class, idComisionEval);
		comite=comisionEval.getNombre();
		findComisiones();
		
	}
	public void initVer() throws Exception{
		
		if(idPersona!=null){
			if (!seguridadUtilFormController.validarInput(idPersona.toString(), TiposDatos.LONG.getValor(), null)) {
				return ;
			}
			completarDatosPersona(idPersona, true);
		}
		
		nivelEntidadOeeUtilTmp.setEm(em);
		nivelEntidadOeeUtilTmp
		.setVarDestinoSinEntidad("sinEntidadIdSinEntidad2");
		nivelEntidadOeeUtilTmp
				.setVarDestinoSinNivel("sinNivelEntidadIdSinNivelEntidad2");
		nivelEntidadOeeUtilTmp
				.setVarDestinoConfigUo("configuracionUoCabIdConfiguracionUo2");
		
		nivelEntidadOeeUtilTmp.init();
		cargarCabecera();
		if(primeraEntrada){
			idReturn=idEvaluacionDesempeno;
			
			if(idComisionEval!=null){
				
				obtenerDatos();
			}
				
			
			primeraEntrada=false;
			limpiarComision();
		}
		from="evaluacionDesempenho/comiteEvaluacion/ComiteEvaluacion?evaluacionDesempenoIdEvaluacionDesempeno="+idReturn;
		entity="ComisionEval";
		direccionFisica="";
	
	}
	
	
	
	
	
	

	public void cargarCabecera() throws Exception{
		if(idComisionEval!=null){
			if (!seguridadUtilFormController.validarInput(idComisionEval.toString(), TiposDatos.LONG.getValor(), null)) {
				return ;
			}
			ComisionEval eval=em.find(ComisionEval.class, idComisionEval);
			idEvaluacionDesempeno=eval.getEvaluacionDesempeno().getIdEvaluacionDesempeno();
		}
		seguridadUtilFormController.cargarCabeceraEvaluacion(idEvaluacionDesempeno);
		evaluacionDesempeno=em.find(EvaluacionDesempeno.class, idEvaluacionDesempeno);
		
			
	}
	
	@SuppressWarnings("unchecked")
	private void findComisiones(){
		capacEvalLista=em.createQuery("SELECT c FROM ComisionCapacEval c " +
				" WHERE c.comisionEval.idComisionEval=:idComisionEval and c.activo=true and c.tipo='E' " +
						" order by persona.paisByIdPaisExpedicionDoc.descripcion,persona.documentoIdentidad ").setParameter("idComisionEval",idComisionEval).getResultList();
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
		return "/seleccion/persona/PersonaEdit.xhtml?personaFrom=/evaluacionDesempenho/comiteEvaluacion/ComiteEvaluacionEdit";
	}

	public String toFindPersonaList() {
		return "/seleccion/persona/PersonaList.xhtml?from=/evaluacionDesempenho/comiteEvaluacion/ComiteEvaluacionEdit";
	}
	
	
	
	 public void anexar(){
			SimpleDateFormat sdfSoloAnio = new SimpleDateFormat("yyyy");
			String anio=sdfSoloAnio.format(new Date());
			entity="ComisionEval";
			direccionFisica="/SICCA/"+anio+"/OEE/"+seguridadUtilFormController.getNivelEntidadOeeUtil().getIdConfigCab()+"/UO/"
			+seguridadUtilFormController.getNivelEntidadOeeUtil().getIdUnidadOrganizativa()+"/E/"+idEvaluacionDesempeno;
			from="evaluacionDesempenho/comiteEvaluacion/ComiteEvaluacionEdit?comisionEvalIdComisionEval="+idEvaluacionDesempeno;
			
			
		}
	 @SuppressWarnings("unchecked")
	private boolean checkDato(){
		 if(comite==null || comite.trim().equals("")){
			 statusMessages.add(Severity.ERROR,"Debe completar el campo Comit&eacute; de Evaluaci&oacute;n antes de realizar esta acci&oacute;n");
			 return false;
		 }
			if(seguridadUtilFormController.contieneCaracter(comite.trim())){
				statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("msg_caracter"));
				return false;
			}
		 String sql="Select c from ComisionEval c " +
	 		" where lower(c.nombre) like :nombre and c.activo=true and c.evaluacionDesempeno.idEvaluacionDesempeno="+idEvaluacionDesempeno;
		 if(comisionEval.getIdComisionEval()!=null){
			 sql+="  and c.idComisionEval!="+comisionEval.getIdComisionEval();
		 }
		 List<ComisionEval> evals=em.createQuery(sql).setParameter("nombre", comite.toLowerCase()).getResultList();
		 if(!evals.isEmpty()){
			 statusMessages.add(Severity.ERROR,"El Comit&eacute; de Evaluaci&oacute;n ingresado ya existe, verifique ");
			 return false;
		 }
		 if(capacEvalLista.isEmpty()){
			 statusMessages.add(Severity.ERROR,"Debe Ingresar al menos un Integrante de la Comisi&oacute;n");
			 return false;
		 }
		 //Agregado; Werner.
		 //*****************************************************************************************************************
		 if(capacEvalLista.size() < 3){
			 statusMessages.add(Severity.ERROR,"La Comisión debe tener como mínimo tres Integrantes");
			 return false;
		 }
		 //*****************************************************************************************************************
		 return true;
	 }
	 
	 public String save(){
		 try {
			 if(!checkDato())
				 return null;
			 /**
			  * Tabla COMISION_EVAL (solo crear cuando se crean nuevo grupo de comisión de evaluación)
			  * */
			 comisionEval.setNombre(comite);
			 if(comisionEval.getIdComisionEval()==null){
				 comisionEval.setActivo(true);
				 comisionEval.setEvaluacionDesempeno(evaluacionDesempeno);
				 comisionEval.setFechaAlta(new Date());
				 comisionEval.setUsuAlta(usuarioLogueado.getCodigoUsuario());
				 em.persist(comisionEval);
			 }else{
				 comisionEval.setFechaMod(new Date());
				 comisionEval.setUsuMod(usuarioLogueado.getCodigoUsuario());
				 em.merge(comisionEval);
			}
			 /**
			  * 	Tabla COMISION_CAPAC_EVAL
			  * **/
			  
			 for (int i = 0; i < capacEvalLista.size(); i++) {
				 ComisionCapacEval comision= capacEvalLista.get(i);
				if(comision.getIdComision()!=null){
					em.merge(comision);
				}else{
					comision.setComisionEval(comisionEval);
					em.persist(comision);
				}
					
			
			
			}
			 
			
			 for (ComisionCapacEval c : capacEvalRemoveLista) {
				 c.setActivo(false);
				 c.setFechaMod(new Date());
				 c.setUsuMod(usuarioLogueado.getCodigoUsuario());
				 em.merge(c);
			}
			
			 em.flush();
			
			 
			 statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
						.getString("GENERICO_MSG"));
			 
			limpiarComision();
			System.out.println(idEvaluacionDesempeno);
			capacEvalRemoveLista= new ArrayList<ComisionCapacEval>();
			return "persisted";
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add(Severity.ERROR,"Ha ocurrido un error: "+e.getMessage());
			return null;
			
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
	
	 
	
	

	
	
	public void addComision(){
		try {
			if(persona.getIdPersona()==null){
				statusMessages.add(Severity.ERROR,"Debe seleccionar una Persona antes de realizar esta acci&oacute;n");
				return ;
			}
			if(!validarRepetido())
				return;
			if(puesto==null|| puesto.trim().equals("")){
				statusMessages.add(Severity.ERROR,"Debe completar el campo puesto antes de realizar esta acci&oacute;n");
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
			comisionCapacEval.setTipo("E");
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
			idPaisExp=General.getIdParaguay();
			idPersona=null;
			nivelEntidadOeeUtilTmp.limpiar();
			docIdentidad=null;
			esEdit=false;
			indexEdit=-1;
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
				statusMessages.add(Severity.ERROR,"Debe seleccionar una Persona antes de realizar esta acci&oacute;n");
				return ;
			}
			if(!validarRepetidoUp())
				return;
			if(puesto==null|| puesto.trim().equals("")){
				statusMessages.add(Severity.ERROR,"Debe completar el campo puesto antes de realizar esta acci&oacute;n");
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
			
			
			comisionCapacEval.setTipo("E");
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
				cargarOee(aux.getConfiguracionUo());
			idPersona= aux.getPersona().getIdPersona();
			persona=em.find(Persona.class,idPersona);
			idPaisExp=persona.getPaisByIdPaisExpedicionDoc().getIdPais();
			docIdentidad=persona.getDocumentoIdentidad();
			habPersona=false;
			esEdit=true;
			indexEdit=index;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void cargarOee(ConfiguracionUoCab oee) {
		nivelEntidadOeeUtilTmp.setIdConfigCab(oee.getIdConfiguracionUo());
		nivelEntidadOeeUtilTmp.init2();

	}
	public boolean habEliminar(int index){
		if(indexEdit==index)
			return false;
		
		return true;
	}
	
	/** GETTER'S && SETTER'S **/
	
	
	
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
	public Long getIdEvaluacionDesempeno() {
		return idEvaluacionDesempeno;
	}
	public void setIdEvaluacionDesempeno(Long idEvaluacionDesempeno) {
		this.idEvaluacionDesempeno = idEvaluacionDesempeno;
	}
	public ComisionEval getComisionEval() {
		return comisionEval;
	}
	public void setComisionEval(ComisionEval comisionEval) {
		this.comisionEval = comisionEval;
	}
	public EvaluacionDesempeno getEvaluacionDesempeno() {
		return evaluacionDesempeno;
	}
	public void setEvaluacionDesempeno(EvaluacionDesempeno evaluacionDesempeno) {
		this.evaluacionDesempeno = evaluacionDesempeno;
	}
	public Long getIdComisionEval() {
		return idComisionEval;
	}
	public void setIdComisionEval(Long idComisionEval) {
		this.idComisionEval = idComisionEval;
	}
	public String getComite() {
		return comite;
	}
	public void setComite(String comite) {
		this.comite = comite;
	}
	public Long getIdReturn() {
		return idReturn;
	}
	public void setIdReturn(Long idReturn) {
		this.idReturn = idReturn;
	}

}
