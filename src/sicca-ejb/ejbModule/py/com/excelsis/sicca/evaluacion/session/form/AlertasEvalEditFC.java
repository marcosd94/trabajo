package py.com.excelsis.sicca.evaluacion.session.form;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.persistence.EntityManager;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;
import enums.TiposDatos;
import py.com.excelsis.sicca.entity.AlertasEval;
import py.com.excelsis.sicca.entity.AlertasEvalDet;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
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


@Scope(ScopeType.CONVERSATION)
@Name("alertasEvalEditFC")
public class AlertasEvalEditFC {
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

	private AlertasEval alertasEval= new AlertasEval();
	private Long idAlertasEval=null;
	private boolean habPaisExp = true;
	private Long idPaisExp=null;
	private String docIdentidad;
	private Persona persona= new Persona();
	private boolean habPersona;
	private Long idPersona;
	private boolean esEdit=false;
	private boolean primeraEntrada=true;
	private int indexEdit=-1;
	private PersonaDTO personaDTO;
	private boolean habBtnAddPersons=false;
	private String emails=null;
	private List<AlertasEvalDet> alertasEvalDets= new ArrayList<AlertasEvalDet>();
	private List<AlertasEvalDet> inactivarAlertasEvalDets= new ArrayList<AlertasEvalDet>();
	private String descripcion=null;
	private Long idDetEdit=null;
	private Boolean modoUpdate=false;


	public void init() {
		if(idPersona!=null){
			completarDatosPersona(idPersona, true);
		}
		
		if(primeraEntrada){
			nivelEntidadOeeUtil.limpiar();
			limpiarDatos();
			if(idAlertasEval!=null){
				alertasEval=em.find(AlertasEval.class, idAlertasEval);
				nivelEntidadOeeUtil.setIdConfigCab(alertasEval.getConfiguracionUoCab().getIdConfiguracionUo());
				nivelEntidadOeeUtil.init2();
				listarDetalles();
				modoUpdate=true;
			}else
				modoUpdate=false;
			primeraEntrada=false;
		}
		nivelEntidadOeeUtil.init();
		
	}
	public void initVer(){
	
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
			emails=persona.getEMail();
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
		return "/seleccion/persona/PersonaEdit.xhtml?personaFrom=/evaluacionDesempenho/configurarAlerta/AlertasEvalEdit";
	}


	public String toFindPersonaList() {
		return "/seleccion/persona/PersonaList.xhtml?from=/evaluacionDesempenho/configurarAlerta/AlertasEvalEdit";
	}
	
	
	private boolean validarDatosSave(){
		if(nivelEntidadOeeUtil.getIdSinNivelEntidad()==null){
			statusMessages.add(Severity.ERROR,"Debe Seleccionar un Nivel");
			return false;
		}
		if(nivelEntidadOeeUtil.getIdSinEntidad()==null){
			statusMessages.add(Severity.ERROR,"Debe Seleccionar una Entidad");
			return false;
		}
		if(nivelEntidadOeeUtil.getIdConfigCab()==null){
			statusMessages.add(Severity.ERROR,"Debe Seleccionar un Oee");
			return false;
		}
		if(alertasEval.getPeriodo()==null){
			statusMessages.add(Severity.ERROR,"Debe Ingresar el campo Perdiodo");
			return false;
		}
		if(alertasEval.getPeriodo().longValue()<0){
			statusMessages.add(Severity.ERROR,"El campo Periodo no puede ser menor a cero");
			return false;
		}
		if(alertasEvalDets.isEmpty()){
			statusMessages.add(Severity.ERROR,"Debe agregar al menos un Destinatario, verifique");
			return false;
		}
		return true;
	}
	
	private boolean validarDatosAdd(){
		if(emails==null|| emails.trim().equals("")){
			statusMessages.add("Debe completar el campo email antes de realizar esta acci&oacute;n");
			return false;
		}
		String[] listaMails=emails.split(";"); 
		boolean mailsValido=true;
		for (String o : listaMails) {
			if (!General.isEmail(o)) 
				mailsValido=false;
			
		}
		if(!mailsValido)
		{
			statusMessages.add(Severity.ERROR,"El mail ingresado no es valido, verifique");
			return false;
		}
		if(indexEdit!=-1)
		{
			if(existePersonaUp(persona)){
				statusMessages.add(Severity.ERROR,"La persona seleccionada ya existe, verifique");
				return false;
			}
		}else{
			if(existePersonaAdd(persona)){
				statusMessages.add(Severity.ERROR,"La persona seleccionada ya existe, verifique");
				return false;
			}
		}
		
		return true;
	}
	private boolean existePersonaAdd(Persona persona){
		for (AlertasEvalDet det : alertasEvalDets) {
			if(det.getPersona()!=null && det.getPersona().getIdPersona()!=null && det.getPersona().getIdPersona().equals(persona.getIdPersona())){
				return true;
			}
		}
		return false;
	}
	private boolean existePersonaUp(Persona p){
		for (int i = 0; i < alertasEvalDets.size(); i++) {
			if(alertasEvalDets.get(i).getPersona()!=null && alertasEvalDets.get(i).getPersona().getIdPersona()!=null && i!=indexEdit){
				if(alertasEvalDets.get(i).getPersona().getIdPersona().equals(p.getIdPersona()))
					return true;
			}
		}
		
		return false;
	}
	
	 public String save(){
		 try {
			if(!validarDatosSave())
				return null;
			/**
			 * GUARDAR EN LA TABLA ALERTAS_EVAL
			 * */
			alertasEval.setConfiguracionUoCab(em.find(ConfiguracionUoCab.class, nivelEntidadOeeUtil.getIdConfigCab()));
			alertasEval.setActivo(true);
			alertasEval.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			alertasEval.setFechaAlta(new Date());
			em.persist(alertasEval);
			/**
			 * GUARDAR EL LA TABLA  ALERTAS_EVAL_DET
			 * */
			for (AlertasEvalDet det : alertasEvalDets) {
				det.setAlertasEval(alertasEval);
				if(det.getPersona()!=null && det.getPersona().getIdPersona()==null)
					det.setPersona(null);
				det.setFechaAlta(new Date());
				det.setUsuAlta(usuarioLogueado.getCodigoUsuario());
				em.persist(det);
			}
			em.flush();
			 statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
						.getString("GENERICO_MSG"));
			limpiarDatos();
			nivelEntidadOeeUtil.limpiar();
			inactivarAlertasEvalDets= new ArrayList<AlertasEvalDet>();
			return "persisted";
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add(Severity.ERROR,"Ha ocurrido un error: "+e.getMessage());
			return null;
		}
	 }
	 
	
	
	
	public String update(){
		 try {
				if(!validarDatosSave())
					return null;
				/**
				 * ACTUALIZA EN LA TABLA ALERTAS_EVAL
				 * */
				alertasEval.setActivo(true);
				alertasEval.setUsuMod(usuarioLogueado.getCodigoUsuario());
				alertasEval.setFechaMod(new Date());
				em.persist(alertasEval);
				/**
				 * ACTUALIZA EL LA TABLA  ALERTAS_EVAL_DET
				 * */
				for (AlertasEvalDet det : alertasEvalDets) {
					if(det.getPersona()!=null && det.getPersona().getIdPersona()==null)
						det.setPersona(null);
					if(det.getIdAlertasEvalDet()==null){
						det.setUsuAlta(usuarioLogueado.getCodigoUsuario());
						det.setFechaAlta(new Date());
						det.setAlertasEval(alertasEval);
						em.persist(det);
					}else {
						det.setUsuMod(usuarioLogueado.getCodigoUsuario());
						det.setFechaMod(new Date());
						em.merge(det);
					}
					
					
					
				}
				/**
				 * INACTIVA LOS REGISTROS ELIMINADOS
				 * **/
				for (AlertasEvalDet detInactiva : inactivarAlertasEvalDets) {
					detInactiva.setActivo(false);
					detInactiva.setUsuMod(usuarioLogueado.getCodigoUsuario());
					detInactiva.setFechaMod(new Date());
					em.merge(detInactiva);
				}
				
				em.flush();
				 statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
							.getString("GENERICO_MSG"));
				limpiarDatos();
				nivelEntidadOeeUtil.limpiar();
				inactivarAlertasEvalDets= new ArrayList<AlertasEvalDet>();
				return "updated";
			} catch (Exception e) {
				e.printStackTrace();
				statusMessages.add(Severity.ERROR,"Ha ocurrido un error: "+e.getMessage());
				return null;
			}
	}
	
	
	
	
	public void addRow(){
		try {
			if(!validarDatosAdd())
				return ;
			AlertasEvalDet det= new AlertasEvalDet();
			det.setActivo(true);
			det.setDescripcion(descripcion);
			det.setEMail(emails);
			det.setPersona(persona);
			if (esEdit){
				alertasEvalDets.remove(indexEdit);	
				if(idDetEdit!=null){
					det.setFechaMod(new Date());
					det.setUsuMod(usuarioLogueado.getCodigoUsuario());
				}
			}
			
			alertasEvalDets.add(det);
			limpiarDatos();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void limpiarDatos(){
		try {
			emails=null;
			persona= new Persona();
			idPaisExp=General.getIdParaguay();
			idPersona=null;
			docIdentidad=null;
			esEdit=false;
			indexEdit=-1;
			habPaisExp=true;
			habPersona=false;
			habBtnAddPersons=false;
			descripcion=null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void delectRow(int index){
		AlertasEvalDet evalDet= alertasEvalDets.get(index);
		if(evalDet.getIdAlertasEvalDet()!=null)
			inactivarAlertasEvalDets.add(evalDet);
		alertasEvalDets.remove(index);
			
	}
	
	
	public void editRow(int index){
		try {
			AlertasEvalDet evalDet= alertasEvalDets.get(index);
			emails=evalDet.getEMail();
			if(evalDet.getPersona()!=null&& evalDet.getPersona().getIdPersona()!=null){
				idPersona= evalDet.getPersona().getIdPersona();
				persona=em.find(Persona.class,idPersona);
				idPaisExp=persona.getPaisByIdPaisExpedicionDoc().getIdPais();
				docIdentidad=persona.getDocumentoIdentidad();
				habPaisExp=true;
				habPersona=false;
			}
			esEdit=true;
			indexEdit=index;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean habEliminar(int index){
		if(indexEdit==index)
			return false;
		
		return true;
	}
	
	
	@SuppressWarnings("unchecked")
	private void listarDetalles(){
		alertasEvalDets=em.createQuery("Select a from AlertasEvalDet a" +
				" where a.alertasEval.idAlertasEval=:idAlertasEval and a.activo=true " +
				" order by a.persona.paisByIdPaisExpedicionDoc.descripcion," +
				" a.persona.documentoIdentidad ").setParameter("idAlertasEval", idAlertasEval).getResultList();
		
	}
	/** GETTER'S && SETTER'S **/
	
	public NivelEntidadOeeUtil getNivelEntidadOeeUtil() {
		return nivelEntidadOeeUtil;
	}

	public void setNivelEntidadOeeUtil(NivelEntidadOeeUtil nivelEntidadOeeUtil) {
		this.nivelEntidadOeeUtil = nivelEntidadOeeUtil;
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


	public boolean isPrimeraEntrada() {
		return primeraEntrada;
	}

	public void setPrimeraEntrada(boolean primeraEntrada) {
		this.primeraEntrada = primeraEntrada;
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
	public AlertasEval getAlertasEval() {
		return alertasEval;
	}
	public void setAlertasEval(AlertasEval alertasEval) {
		this.alertasEval = alertasEval;
	}
	public Long getIdAlertasEval() {
		return idAlertasEval;
	}
	public void setIdAlertasEval(Long idAlertasEval) {
		this.idAlertasEval = idAlertasEval;
	}
	public String getEmails() {
		return emails;
	}
	public void setEmails(String emails) {
		this.emails = emails;
	}
	public List<AlertasEvalDet> getAlertasEvalDets() {
		return alertasEvalDets;
	}
	public void setAlertasEvalDets(List<AlertasEvalDet> alertasEvalDets) {
		this.alertasEvalDets = alertasEvalDets;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public Long getIdDetEdit() {
		return idDetEdit;
	}
	public void setIdDetEdit(Long idDetEdit) {
		this.idDetEdit = idDetEdit;
	}
	public List<AlertasEvalDet> getInactivarAlertasEvalDets() {
		return inactivarAlertasEvalDets;
	}
	public void setInactivarAlertasEvalDets(
			List<AlertasEvalDet> inactivarAlertasEvalDets) {
		this.inactivarAlertasEvalDets = inactivarAlertasEvalDets;
	}
	public Boolean getModoUpdate() {
		return modoUpdate;
	}
	public void setModoUpdate(Boolean modoUpdate) {
		this.modoUpdate = modoUpdate;
	}

}
