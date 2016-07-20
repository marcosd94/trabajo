package py.com.excelsis.sicca.juridicos.session.form;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;
import enums.AlquiladoProp;
import enums.TiposDatos;
import py.com.excelsis.sicca.entity.Barrio;
import py.com.excelsis.sicca.entity.Ciudad;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.Departamento;
import py.com.excelsis.sicca.entity.Entidad;
import py.com.excelsis.sicca.entity.Excepcionados;
import py.com.excelsis.sicca.entity.Pais;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.juridicos.session.ExcepcionadosHome;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.ConfiguracionUoCabList;
import py.com.excelsis.sicca.session.EntidadList;
import py.com.excelsis.sicca.session.SinEntidadList;
import py.com.excelsis.sicca.session.SinNivelEntidadList;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.PersonaDTO;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.General;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.SICCAAppHelper;

@Name("excepcionadosEditFC")
@Scope(ScopeType.CONVERSATION)
public class ExcepcionadosEditFC implements Serializable {

	private static final long serialVersionUID = 3174062745467083893L;

	@In(create = true)
	JpaResourceBean jpaResourceBean;

	@In
	StatusMessages statusMessages;

	@In(value = "entityManager")
	EntityManager em;
	
	@In(required = false)
	Usuario usuarioLogueado;


	@In(create = true)
	ExcepcionadosHome excepcionadosHome;
	
	private Long idExcepcionado;

	
	
	@In(required = false, create = true)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;
	@In(create = true)
	SeleccionUtilFormController seleccionUtilFormController;
	
	@In(create = true)
	SeguridadUtilFormController seguridadUtilFormController;
	
	private Excepcionados excepcionados;

	private String mensaje;
	private String codigo;

	private Integer orden;

	
	private Persona persona= new Persona();
	private String docIdentidad= null;
	private Long idPaisExp= null;
	private PersonaDTO personaDTO;
	private boolean habBtnAddPersons=false;
	private String entity;
	private String direccionFisica; 
	private boolean habSave;
	private boolean habUpdate;
	private Long idPersona;
	private String nombrePantalla;
	SimpleDateFormat sdfSoloAnio = new SimpleDateFormat("yyyy");
	private Boolean primeraEntrada = true;

	public void init() {
		if(idPersona!=null)
		{
			completarDatosPersona(idPersona);
			habBtnAddPersons=false;
		}
		nivelEntidadOeeUtil.init();
		if (primeraEntrada) {
			excepcionados = new Excepcionados();
			anexar();
			primeraEntrada=false;
			idPaisExp=General.getIdParaguay();
			if (idExcepcionado != null) {
				excepcionados= em.find(Excepcionados.class,idExcepcionado);
				excepcionadosHome.setInstance(excepcionados);
				obtenerDatos();
			} else {
				setearDatos();
			}
		}				
		
		
		

	}
	


	
	

	

	

		
		public String update() {
			try {
				if(checkDuplicate("update")){
					statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("msg_registro_duplicado"));
					return null;
				}
					
				excepcionados.setFechaMod(new Date());
				excepcionados.setUsuMod(usuarioLogueado.getCodigoUsuario().toUpperCase());
				em.merge(excepcionados);
				em.flush();
			} catch (Exception e) {
				statusMessages.add(Severity.ERROR, e.getMessage());
				return null;
			}
			mensaje = "Registro actualizado con exito";
			statusMessages.add(Severity.INFO, mensaje);
			return "updated";
		}

		public String save() {
			try {
				if(!checkDatos())
					return null;
				excepcionados.setFechaAlta(new Date());
				excepcionados.setUsuAlta(usuarioLogueado.getCodigoUsuario().toUpperCase());
				excepcionados.setConfiguracionUoCab(em.find(ConfiguracionUoCab.class, nivelEntidadOeeUtil.getIdConfigCab()));
				excepcionados.setPersona(persona);
				em.persist(excepcionados);
				habSave=false;
				em.flush();
			} catch (Exception e) {
				statusMessages.add(Severity.ERROR, e.getMessage());
				return null;
			}
			mensaje = "Registro creado con exito";
			statusMessages.add(Severity.INFO, mensaje);
			statusMessages.add(Severity.WARN,"Si desea adjuntar un documento presione el botón Anexos si no, presione Volver");
			return "persisted";
		}
		
		private boolean checkDatos(){
			if(nivelEntidadOeeUtil.getIdSinNivelEntidad()==null){
				statusMessages.add(Severity.ERROR,"Debe seleccionar el Nivel. Verifique");
				return false;
			}
			if(nivelEntidadOeeUtil.getIdSinEntidad()==null){
				statusMessages.add(Severity.ERROR,"Debe seleccionar la Entidad. Verifique");
				return false;
			}
			if(nivelEntidadOeeUtil.getIdConfigCab()==null){
				statusMessages.add(Severity.ERROR,"Debe seleccionar el Oee. Verifique");
				return false;
			}
			
			if(docIdentidad==null|| docIdentidad.trim().equals("")
				||  persona.getIdPersona()==null){
				statusMessages.add(Severity.ERROR, "Debe Seleccionar la Persona. Verifique");
				return false;
			}
			if(checkDuplicate("save"))
			{
				statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("msg_registro_duplicado"));
				return false;
			}
			if(excepcionados.getFecha()==null){
				statusMessages.add(Severity.ERROR,"Debe Ingresar la Fecha Desde. Verifique");
				return false;
			}
			if(excepcionados.getFechaHasta()==null){
				statusMessages.add(Severity.ERROR,"Debe Ingresar la Fecha Hasta. Verifique");
				return false;
			}
			if(excepcionados.getFechaHasta().after(excepcionados.getFechaHasta())){
				statusMessages.add(Severity.ERROR,"La fecha Hasta no puede ser mayor a la fecha Desde. Verifique");
				return false;
			}
		
			if(excepcionados.getMotivo().trim().equals("")){
				statusMessages.add(Severity.ERROR,"Debe Ingresar el Motivo. Verifique");
				return false;
			}
			
			return true;
			
		}
	
		 public void anexar(){
				
				String anio=sdfSoloAnio.format(new Date());
				entity="Excepcionados";
				direccionFisica="//SICCA//"+anio+"//USUARIO_PORTAL//"+persona.getDocumentoIdentidad()+persona.getIdPersona();
				nombrePantalla="excepcionados_edit";
				
			}
		 
		 public String toFindPersonaList() {
				return "/seleccion/persona/PersonaList.xhtml?from=/juridicos/excepcionados/ExcepcionadosEdit";
			}
		@SuppressWarnings("unchecked")
		public void findPersona() {
			if (persona.getDocumentoIdentidad() != null && !persona.getDocumentoIdentidad().trim().equals("")) {
				List<Persona> p =
					em.createQuery("Select p from Persona p"
						+ " where lower(p.documentoIdentidad) like '"
						+ persona.getDocumentoIdentidad().trim().toLowerCase() + "'").getResultList();
				if(p.isEmpty())
					persona=em.find(Persona.class, p.get(0).getIdPersona());
			}
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
				limpiarDatosPersona2();
			} else if (personaDTO.getHabilitarBtn()) {
				habBtnAddPersons = true;
				limpiarDatosPersona2();
				statusMessages.add(Severity.ERROR, personaDTO.getMensaje());
			} else {
				habBtnAddPersons = false;
				persona = personaDTO.getPersona();
				
			}
		}
		public void limpiarDatosPersona2() {
			persona = new Persona();
		}
		public void limpiarDatosPersona() {
			persona= new Persona();
			idPersona=null;
			docIdentidad = null;
		}
		public String toFindPersona() {
			idPersona=null;
			return "/seleccion/persona/PersonaEdit.xhtml?personaFrom=/juridicos/excepcionados/ExcepcionadosEdit";
		}
		
//		METODOS PRIVADOS
		private void completarDatosPersona(Long idPersona) {
			persona=em.find(Persona.class,idPersona);
			idPaisExp = persona.getPaisByIdPaisExpedicionDoc().getIdPais();
			docIdentidad = persona.getDocumentoIdentidad();
			
		}
		
		private Date ultiaDelAnio(Integer anio){
			   Calendar cal = Calendar.getInstance();
			   Integer mesDiciembre=12;
			   Integer ulDiaDiciembre=31;
			    cal.set(anio, mesDiciembre-1, ulDiaDiciembre);

			 return cal.getTime();
		}
		
		@SuppressWarnings("unchecked")
		private boolean checkDuplicate(String operation){
			String hql ="SELECT e FROM Excepcionados e  WHERE e.anio="+excepcionados.getAnio()+"" +
					" AND e.activo=true " +
					" AND e.configuracionUoCab.idConfiguracionUo="+nivelEntidadOeeUtil.getIdConfigCab()+"" +
					" AND e.persona.idPersona="+persona.getIdPersona() ;
			if(operation.equals("update"))
				hql+=" AND e.idExcepcionado!="+excepcionados.getIdExcepcionado();
			List<Excepcionados> excepcionadosList= em.createQuery(hql).getResultList();
			return !excepcionadosList.isEmpty(); 
		}
		private void obtenerDatos(){
			anexar();
			habSave=false;
			habUpdate=true;
			
			if(excepcionados.getConfiguracionUoCab()!=null){
				nivelEntidadOeeUtil.setIdConfigCab(excepcionados.getConfiguracionUoCab().getIdConfiguracionUo());
				nivelEntidadOeeUtil.init2();
			}
			completarDatosPersona(excepcionados.getPersona().getIdPersona());
		}
		
		private void setearDatos(){
			persona= new Persona();
			excepcionados= new Excepcionados();
			excepcionados.setActivo(true);
			excepcionados.setAnio(Integer.parseInt(sdfSoloAnio.format(new Date())));
			excepcionados.setFechaHasta(ultiaDelAnio(excepcionados.getAnio()));
			excepcionadosHome.setInstance(excepcionados);
			habSave=true;
			habUpdate=false;
			nivelEntidadOeeUtil.limpiar();
			limpiarDatosPersona();
		}
		
		
		
		
	// GETTER Y SETTER

	




	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	
	public Integer getOrden() {
		return orden;
	}
	public void setOrden(Integer orden) {
		this.orden = orden;
	}
	



	public Excepcionados getExcepcionados() {
		return excepcionados;
	}


	public void setExcepcionados(Excepcionados excepcionados) {
		this.excepcionados = excepcionados;
	}


	public Persona getPersona() {
		return persona;
	}


	public void setPersona(Persona persona) {
		this.persona = persona;
	}


	public String getEntity() {
		return entity;
	}


	public void setEntity(String entity) {
		this.entity = entity;
	}


	public String getDireccionFisica() {
		return direccionFisica;
	}


	public void setDireccionFisica(String direccionFisica) {
		this.direccionFisica = direccionFisica;
	}


	public boolean isHabSave() {
		return habSave;
	}


	public void setHabSave(boolean habSave) {
		this.habSave = habSave;
	}


	public Long getIdPersona() {
		return idPersona;
	}


	public void setIdPersona(Long idPersona) {
		this.idPersona = idPersona;
	}


	public String getNombrePantalla() {
		return nombrePantalla;
	}


	public void setNombrePantalla(String nombrePantalla) {
		this.nombrePantalla = nombrePantalla;
	}


	public boolean isHabUpdate() {
		return habUpdate;
	}


	public void setHabUpdate(boolean habUpdate) {
		this.habUpdate = habUpdate;
	}

	public Long getIdExcepcionado() {
		return idExcepcionado;
	}

	public void setIdExcepcionado(Long idExcepcionado) {
		this.idExcepcionado = idExcepcionado;
	}

	public String getDocIdentidad() {
		return docIdentidad;
	}

	public void setDocIdentidad(String docIdentidad) {
		this.docIdentidad = docIdentidad;
	}

	public Long getIdPaisExp() {
		return idPaisExp;
	}

	public void setIdPaisExp(Long idPaisExp) {
		this.idPaisExp = idPaisExp;
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

	public Boolean getPrimeraEntrada() {
		return primeraEntrada;
	}

	public void setPrimeraEntrada(Boolean primeraEntrada) {
		this.primeraEntrada = primeraEntrada;
	}

}
