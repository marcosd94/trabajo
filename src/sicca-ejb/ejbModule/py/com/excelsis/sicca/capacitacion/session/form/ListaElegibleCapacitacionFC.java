package py.com.excelsis.sicca.capacitacion.session.form;
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
import org.richfaces.model.UploadItem;

import enums.ActividadEnum;
import enums.ProcesoEnum;
import py.com.excelsis.sicca.capacitacion.session.EvaluacionInscPostList;
import py.com.excelsis.sicca.capacitacion.session.PostulacionCapList;
import py.com.excelsis.sicca.entity.Capacitaciones;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.Entidad;
import py.com.excelsis.sicca.entity.EvaluacionInscPost;
import py.com.excelsis.sicca.entity.PostulacionCap;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.entity.RevisionCapacitacion;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.form.AdmDocAdjuntoFormController;
import py.com.excelsis.sicca.session.util.JbpmUtilFormController;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;


@Scope(ScopeType.CONVERSATION)
@Name("listaElegibleCapacitacion")
public class ListaElegibleCapacitacionFC{
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
	@In(create=true)
	EvaluacionInscPostList evaluacionInscPostList;
	

	private Capacitaciones capacitaciones= new Capacitaciones();
	private Long idCapacitacion;
	private String tipoCapac=null;
	private Long idPaisExp=null;
	private String nombre=null;
	private String apellido=null;
	private String docIdentidad=null;
	private List<EvaluacionInscPost> evaluacionInscPostLista= new ArrayList<EvaluacionInscPost>();
	String tipo=null;
	private String from;
	
	



	public void init() {
		capacitaciones=em.find(Capacitaciones.class, idCapacitacion);
		seguridadUtilFormController.validarCapacitacionPerteneceUO(idCapacitacion);
		
		if(capacitaciones.getTipoSeleccion().equals("C"))
			tipo="EVAL_POST";
		if(capacitaciones.getTipoSeleccion().equals("I"))
			tipo="EVAL_INSCRIP";
		cargarCabecera();
		if(capacitaciones.getDatosEspecificosTipoCap()!=null)
			tipoCapac=capacitaciones.getDatosEspecificosTipoCap().getDescripcion();
		
		findListaEspera();
		
		
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
	private void findListaEspera(){
		
		evaluacionInscPostList.setIdCapacitacion(idCapacitacion);
		evaluacionInscPostList.setTipo(tipo);
		evaluacionInscPostLista=evaluacionInscPostList.listaResultadosCU488();
	}
	
	
	public void search(){
		evaluacionInscPostList.setIdCapacitacion(idCapacitacion);
		evaluacionInscPostList.setTipo(tipo);
		evaluacionInscPostList.setIdPaisExp(idPaisExp);
		if(nombre!=null && !nombre.trim().equals(""))
			evaluacionInscPostList.getPersona().setNombres(nombre);
		if(apellido!=null && !apellido.trim().equals(""))
			evaluacionInscPostList.getPersona().setApellidos(apellido);
		if(docIdentidad!=null && !docIdentidad.trim().equals(""))
			evaluacionInscPostList.getPersona().setDocumentoIdentidad(docIdentidad);
		evaluacionInscPostLista=evaluacionInscPostList.listaResultadosCU488();
	}
	public void searchAll(){
		nombre=null;
		apellido=null;
		docIdentidad=null;
		idPaisExp=null;
		evaluacionInscPostList.setIdCapacitacion(idCapacitacion);
		evaluacionInscPostList.setTipo(tipo);
		evaluacionInscPostLista=evaluacionInscPostList.listaResultadosCU488();
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

	public String getTipoCapac() {
		return tipoCapac;
	}

	public void setTipoCapac(String tipoCapac) {
		this.tipoCapac = tipoCapac;
	}

	public Long getIdPaisExp() {
		return idPaisExp;
	}

	public void setIdPaisExp(Long idPaisExp) {
		this.idPaisExp = idPaisExp;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApellido() {
		return apellido;
	}

	public void setApellido(String apellido) {
		this.apellido = apellido;
	}

	public String getDocIdentidad() {
		return docIdentidad;
	}

	public void setDocIdentidad(String docIdentidad) {
		this.docIdentidad = docIdentidad;
	}

	public List<EvaluacionInscPost> getEvaluacionInscPostLista() {
		return evaluacionInscPostLista;
	}

	public void setEvaluacionInscPostLista(
			List<EvaluacionInscPost> evaluacionInscPostLista) {
		this.evaluacionInscPostLista = evaluacionInscPostLista;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

}
