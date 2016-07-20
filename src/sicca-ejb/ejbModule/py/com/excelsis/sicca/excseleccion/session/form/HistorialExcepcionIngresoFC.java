package py.com.excelsis.sicca.excseleccion.session.form;


import java.util.Vector;
import java.util.List;
import javax.persistence.EntityManager;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.StatusMessages;

import enums.TiposDatos;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgrExc;
import py.com.excelsis.sicca.entity.EmpleadoPuesto;
import py.com.excelsis.sicca.entity.Excepcion;
import py.com.excelsis.sicca.entity.ExcepcionElegibles;
import py.com.excelsis.sicca.entity.HistorialExcepcion;
import py.com.excelsis.sicca.entity.Postulacion;
import py.com.excelsis.sicca.entity.Resolucion;
import py.com.excelsis.sicca.excseleccion.session.HistorialExcepcionList;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.util.JpaResourceBean;


@Scope(ScopeType.CONVERSATION)
@Name("historialExcepcionIngresoFC")
public class HistorialExcepcionIngresoFC {
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
	HistorialExcepcionList historialExcepcionList;
	
	@In(create = true)
	SeguridadUtilFormController seguridadUtilFormController;
		
	
	private Long idConcursoPuestoAgr;
	private Long idExcepcion;
	private ConcursoPuestoAgrExc concursoPuestoAgrExc= new  ConcursoPuestoAgrExc();
	private List<ExcepcionElegibles> excepcionElegibles= new Vector<ExcepcionElegibles>();
	private  ConcursoPuestoAgr agr= new ConcursoPuestoAgr();
	private Excepcion excepcion= new Excepcion();
	private String nombreConcurso;
	private String nombregrupo;
	private List<HistorialExcepcion> historialExcepcionLista= new Vector<HistorialExcepcion>();
	
	
	public void init() throws Exception {
		if (!seguridadUtilFormController.validarInput(idConcursoPuestoAgr.toString(), TiposDatos.LONG.getValor(), null)) {
			return ;
		}
		if (!seguridadUtilFormController.validarInput(idExcepcion.toString(), TiposDatos.LONG.getValor(), null)) {
			return ;
		}
		cargarCabecera();
		cargarElegible();
		cargarHistorial();
	}
	

	
	
	

	public void cargarCabecera() throws Exception{
		
		agr= em.find(ConcursoPuestoAgr.class, idConcursoPuestoAgr);
		excepcion=em.find(Excepcion.class, idExcepcion);
		nombreConcurso=agr.getConcurso().getNombre();
		nombregrupo=agr.getDescripcionGrupo();
		//OEE
		nivelEntidadOeeUtil.setIdConfigCab(usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo());
		nivelEntidadOeeUtil.init2();
		
	}
	
	@SuppressWarnings("unchecked")
	private void cargarElegible(){
		em.clear();
		excepcionElegibles=em.createQuery("Select e from ExcepcionElegibles e " +
				" where e.excepcion.idExcepcion=:idExcepcion").setParameter("idExcepcion",idExcepcion).getResultList();
	}
	
	
	private void cargarHistorial(){
		historialExcepcionList.setIdExcepcion(idExcepcion);
		historialExcepcionLista=historialExcepcionList.listaResultados();
	}
	
	public String verAnexos(Long idHistorial ){
		HistorialExcepcion historialExcepcion=em.find(HistorialExcepcion.class, idHistorial);
		ConcursoPuestoAgr agr=em.find(ConcursoPuestoAgr.class, historialExcepcion.getConcursoGrupoAgr().getIdConcursoPuestoAgr());
		String estado=historialExcepcion.getEstado();
		String url = "/seleccion/adminDocAdjunto/AdmDocAdjunto.seam";
		String nombrePantalla = "";
		String idCU = "";
		String nombreTabla = "";
		String mostrarDoc = "false";
		String isEdit = "false";
		String from = "excepcionesSeleccion/historialExcepcionIngreso/HistorialExcepcionIngreso";

		String pagina =
			url + "?mostrarDoc=" + mostrarDoc + "&isEdit=" + isEdit + "&from=" + from;
		if(estado!=null && !estado.trim().equals("")){
			if(estado.equals("LISTA CORTA")){
				nombreTabla="ConcursoPuestoAgrExc";
				idCU=agr.getIdConcursoPuestoAgr()+"";
				nombrePantalla="EVALUACION_DOC_ADJUDICADOS_EXC";
			}else if(estado.equals("EVAL. ADJUDICADOS")){
				nombreTabla="ConcursoPuestoAgrExc";
				idCU=agr.getIdConcursoPuestoAgr()+"";
				nombrePantalla="PUBLICACION_SELECCIONADOS_EXC";
			}else if(estado.equals("ADJUDICADO")){
				nombreTabla="Concurso";
				idCU=agr.getConcurso().getIdConcurso()+"";
				nombrePantalla="ELABORAR_RESOLUCION_EXC";
			}else if(estado.equals("CON RES. ADJUDICACION")){
				nombreTabla="Resolucion";
				idCU=agr.getIdConcursoPuestoAgr()+"";
				nombrePantalla="FIRMA_RESOL_ADJ_EXC";
			}else if(estado.equals("PERMANENTE N12")){
				nombreTabla="Concurso";
				idCU=agr.getConcurso().getIdConcurso().toString();
				nombrePantalla="ELABORAR_DECRETO_EXC";
				
			}else if(estado.equals("CON DECRETO")){
				nombreTabla="Decreto";
				idCU=agr.getConcurso().getIdConcurso().toString();
				nombrePantalla="FIRMA_DECRETO_EXC";
				
			}else if(estado.equals("PERMANENTE D12")){
				nombreTabla="Concurso";
				idCU=agr.getConcurso().getIdConcurso().toString();
				nombrePantalla="ELABORAR_RESOL_NOMB_EXC";
			}else if(estado.equals("CON RES. NOMBRAMIENTO")){
				nombreTabla="Resolucion";
				idCU=agr.getResolucionNombramiento().getIdResolucion().toString();
				nombrePantalla="INGRESO_EXC";
			}else if(estado.equals("CONTRATADO")|| estado.equals("FIRMADO NOMBRAMIENTO")){
				nombreTabla="EmpleadoPuesto";
				nombrePantalla="INGRESO_EXC";
				idCU=agr.getConcurso().getIdConcurso().toString();
			}
			else if(estado.equals("FIRMADO DECRETO")){
				nombreTabla="Decreto";
				nombrePantalla="FIRMA_DECRETO_EXC";
				idCU=agr.getConcurso().getIdConcurso().toString();
			}else if(estado.equals("INGRESADO")){
				nombreTabla="EmpleadoPuesto";
				idCU=agr.getConcurso().getIdConcurso().toString();
				nombrePantalla="INGRESO_EXC";
			}
			pagina +=
				"&nombrePantalla=" + nombrePantalla + "&idCU=" + idCU + "&nombreTabla="
					+ nombreTabla;
			return pagina;

		}
		return null;
	}
	
	public String refresh(int index){
		Long id= excepcionElegibles.get(index).getPostulacion().getIdPostulacion();
		Postulacion postulacion=em.find(Postulacion.class,id);
		excepcionElegibles.get(index).setPostulacion(postulacion);
		String cod=null;
		if(postulacion.getPersonaPostulante()!=null )
			cod=postulacion.getPersonaPostulante().getUsuAlta();
		
		return cod; 
	}
	

	/** GETTER'S && SETTER'S **/

	public NivelEntidadOeeUtil getNivelEntidadOeeUtil() {
		return nivelEntidadOeeUtil;
	}

	public void setNivelEntidadOeeUtil(NivelEntidadOeeUtil nivelEntidadOeeUtil) {
		this.nivelEntidadOeeUtil = nivelEntidadOeeUtil;
	}

	public SeguridadUtilFormController getSeguridadUtilFormController() {
		return seguridadUtilFormController;
	}

	public void setSeguridadUtilFormController(
			SeguridadUtilFormController seguridadUtilFormController) {
		this.seguridadUtilFormController = seguridadUtilFormController;
	}

	public Long getIdConcursoPuestoAgr() {
		return idConcursoPuestoAgr;
	}

	public void setIdConcursoPuestoAgr(Long idConcursoPuestoAgr) {
		this.idConcursoPuestoAgr = idConcursoPuestoAgr;
	}

	public Long getIdExcepcion() {
		return idExcepcion;
	}

	public void setIdExcepcion(Long idExcepcion) {
		this.idExcepcion = idExcepcion;
	}

	public ConcursoPuestoAgrExc getConcursoPuestoAgrExc() {
		return concursoPuestoAgrExc;
	}

	public void setConcursoPuestoAgrExc(
			ConcursoPuestoAgrExc concursoPuestoAgrExc) {
		this.concursoPuestoAgrExc = concursoPuestoAgrExc;
	}

	public List<ExcepcionElegibles> getExcepcionElegibles() {
		return excepcionElegibles;
	}

	public void setExcepcionElegibles(
			List<ExcepcionElegibles> excepcionElegibles) {
		this.excepcionElegibles = excepcionElegibles;
	}

	public ConcursoPuestoAgr getAgr() {
		return agr;
	}

	public void setAgr(ConcursoPuestoAgr agr) {
		this.agr = agr;
	}

	public Excepcion getExcepcion() {
		return excepcion;
	}

	public void setExcepcion(Excepcion excepcion) {
		this.excepcion = excepcion;
	}

	public String getNombreConcurso() {
		return nombreConcurso;
	}

	public void setNombreConcurso(String nombreConcurso) {
		this.nombreConcurso = nombreConcurso;
	}

	public String getNombregrupo() {
		return nombregrupo;
	}

	public void setNombregrupo(String nombregrupo) {
		this.nombregrupo = nombregrupo;
	}






	public List<HistorialExcepcion> getHistorialExcepcionLista() {
		return historialExcepcionLista;
	}






	public void setHistorialExcepcionLista(
			List<HistorialExcepcion> historialExcepcionLista) {
		this.historialExcepcionLista = historialExcepcionLista;
	}
	
	
}
