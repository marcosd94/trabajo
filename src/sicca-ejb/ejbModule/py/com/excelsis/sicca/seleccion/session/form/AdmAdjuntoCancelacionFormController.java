package py.com.excelsis.sicca.seleccion.session.form;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import enums.ActividadEnum;
import enums.ProcesoEnum;
import enums.TitularSuplente;
import py.com.excelsis.sicca.entity.ComisionSeleccionCab;
import py.com.excelsis.sicca.entity.ComisionSeleccionDet;
import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.Entidad;
import py.com.excelsis.sicca.entity.Excepcion;
import py.com.excelsis.sicca.entity.ExcepcionGrPuesto;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.seguridad.entity.Rol;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.ConfiguracionUoCabList;
import py.com.excelsis.sicca.session.EntidadList;
import py.com.excelsis.sicca.session.SinEntidadList;
import py.com.excelsis.sicca.session.SinNivelEntidadList;
import py.com.excelsis.sicca.session.util.JbpmUtilFormController;

import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.SICCAAppHelper;

@Scope(ScopeType.CONVERSATION)
@Name("admAdjuntoCancelacionFormController")
public class AdmAdjuntoCancelacionFormController implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6343374537970715199L;
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	
	@In(required = false)
	Usuario usuarioLogueado;

	@In(create = true)
	SinEntidadList sinEntidadList;
	@In(create = true)
	EntidadList entidadList;
	@In(create = true)
	ConfiguracionUoCabList configuracionUoCabList;
	
	@In(create = true)
	SinNivelEntidadList sinNivelEntidadList;

	
	@In(create = true)
	JbpmUtilFormController jbpmUtilFormController;
	
	@In(scope=ScopeType.SESSION, required=false)  
	@Out(scope=ScopeType.SESSION, required=false)  
	String roles; 
	
	
	private ComisionSeleccionCab comisionSeleccionCab= new ComisionSeleccionCab();
	private SinNivelEntidad sinNivelEntidad;
	private SinEntidad sinEntidad;
	private ConfiguracionUoCab configuracionUoCab;
	private Concurso concurso;
	private Excepcion  excepcion= new Excepcion();
	private Long idExepcion;
	private Long idConcurso;
	private String direccionFisica;
	private String entity;
	private String nombrePantalla;
	private boolean fromAnexar=false;
	private List<ExcepcionGrPuesto> excepcionGrPuestoList=new ArrayList<ExcepcionGrPuesto>();
	
	public void init(){
	
		
		if(!fromAnexar ){
			excepcion= em.find(Excepcion.class, idExepcion);
			if(excepcion.getConcurso()!=null){
				idConcurso=excepcion.getConcurso().getIdConcurso();
				concurso=em.find(Concurso.class,idConcurso );
				configuracionUoCab=em.find(ConfiguracionUoCab.class,concurso.getConfiguracionUoCab().getIdConfiguracionUo() );
				findEntidades();
			}
			cargarLista();
		
		}
		
	}
	
	
	 public void anexar(){
		 try {
			
			 	fromAnexar=true;
				SimpleDateFormat sdfSoloAnio = new SimpleDateFormat("yyyy");
				String anio=sdfSoloAnio.format(new Date());
				direccionFisica="//SICCA//"+anio+"//OEE//"+configuracionUoCab.getIdConfiguracionUo()+"//"+concurso.getDatosEspecificosTipoConc().getIdDatosEspecificos()+"//"+idConcurso+"//CS";
				nombrePantalla="admAdjuntoCancelacion_edit";
				entity="Excepcion";
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	 
	 public String endTask(){
		 try {
			excepcion.setUsuMod(usuarioLogueado.getCodigoUsuario());
			excepcion.setFechaMod(new Date());
			em.merge(excepcion);
			concurso= em.find(Concurso.class,idConcurso);
			if(concurso!=null && concurso.getIdConcurso()!=null){
				concurso.setActivo(false);
				concurso.setUsuMod(usuarioLogueado.getCodigoUsuario());
				concurso.setFechaMod(new Date());
				em.merge(concurso);
			}
			
			em.flush();
			
			//Se finaliza  la tarea
			jbpmUtilFormController.setExcepcion(excepcion);
			jbpmUtilFormController.setActividadActual(ActividadEnum.EXCEP_ADJUNTAR_DOC_CANCELACION_AUTOMATICA);
			jbpmUtilFormController.setTransition("end");
			jbpmUtilFormController.nextTask();
			
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
			return "end";
			 
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	 }
	 
	
	/**
	 * METODOS PRIVADOS
	 * **/
	
	

	@SuppressWarnings("unchecked")
	private void findEntidades(){
		if(configuracionUoCab!=null){
			if(configuracionUoCab.getEntidad()!=null){
				sinEntidad=em.find(SinEntidad.class, configuracionUoCab.getEntidad().getSinEntidad().getIdSinEntidad());
				List<SinNivelEntidad> sin=em.createQuery("Select n from SinNivelEntidad n " +
						" where n.aniAniopre ="+sinEntidad.getAniAniopre() +" and n.nenCodigo="+sinEntidad.getNenCodigo()).getResultList();
				if(!sin.isEmpty())
					sinNivelEntidad=sin.get(0);
				
			}
		}
		
		
	}
	 
	@SuppressWarnings("unchecked")
	private void cargarLista(){
		excepcionGrPuestoList=em.createQuery("Select c from ExcepcionGrPuesto c " +
				" where c.excepcion.idExcepcion="+idExepcion +"" +
				" and c.activo=true").getResultList();
	}

	
	
	
	
	 
//	GETTERS Y SETTERS	
	
	

	public SinNivelEntidad getSinNivelEntidad() {
		return sinNivelEntidad;
	}

	public void setSinNivelEntidad(SinNivelEntidad sinNivelEntidad) {
		this.sinNivelEntidad = sinNivelEntidad;
	}

	public SinEntidad getSinEntidad() {
		return sinEntidad;
	}

	public void setSinEntidad(SinEntidad sinEntidad) {
		this.sinEntidad = sinEntidad;
	}

	
	public Concurso getConcurso() {
		return concurso;
	}

	public void setConcurso(Concurso concurso) {
		this.concurso = concurso;
	}

	public ConfiguracionUoCab getConfiguracionUoCab() {
		return configuracionUoCab;
	}

	public void setConfiguracionUoCab(ConfiguracionUoCab configuracionUoCab) {
		this.configuracionUoCab = configuracionUoCab;
	}


	public ComisionSeleccionCab getComisionSeleccionCab() {
		return comisionSeleccionCab;
	}


	public void setComisionSeleccionCab(ComisionSeleccionCab comisionSeleccionCab) {
		this.comisionSeleccionCab = comisionSeleccionCab;
	}


	

	public Long getIdConcurso() {
		return idConcurso;
	}



	public void setIdConcurso(Long idConcurso) {
		this.idConcurso = idConcurso;
	}




	public Excepcion getExcepcion() {
		return excepcion;
	}



	public void setExcepcion(Excepcion excepcion) {
		this.excepcion = excepcion;
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



	public String getNombrePantalla() {
		return nombrePantalla;
	}



	public void setNombrePantalla(String nombrePantalla) {
		this.nombrePantalla = nombrePantalla;
	}



	public boolean isFromAnexar() {
		return fromAnexar;
	}



	public void setFromAnexar(boolean fromAnexar) {
		this.fromAnexar = fromAnexar;
	}









	public Long getIdExepcion() {
		return idExepcion;
	}









	public void setIdExepcion(Long idExepcion) {
		this.idExepcion = idExepcion;
	}









	public List<ExcepcionGrPuesto> getExcepcionGrPuestoList() {
		return excepcionGrPuestoList;
	}









	public void setExcepcionGrPuestoList(
			List<ExcepcionGrPuesto> excepcionGrPuestoList) {
		this.excepcionGrPuestoList = excepcionGrPuestoList;
	}










	
	
	
	
	

	

	
	
	
}
