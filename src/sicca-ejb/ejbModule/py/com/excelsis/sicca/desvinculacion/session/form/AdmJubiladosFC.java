package py.com.excelsis.sicca.desvinculacion.session.form;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import py.com.excelsis.sicca.desvinculacion.session.JubiladosList;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.session.util.QueryValue;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.Desvinculacion;
import py.com.excelsis.sicca.entity.Documentos;
import py.com.excelsis.sicca.entity.Jubilados;
import py.com.excelsis.sicca.entity.Pais;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.util.GrupoPuestosController;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.Utiles;

@Scope(ScopeType.CONVERSATION)
@Name("admJubiladosFC")
public class AdmJubiladosFC {
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(required = false)
	GrupoPuestosController grupoPuestosController;
	@In(required = false, create = true)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;
	@In(create = true)
	JubiladosList jubiladosList;
	
	private Long idJubilado;
	private Long idPais;
	private Jubilados jubilados;
	private Boolean modoVista;

	private String nombres;
	private String apellidos;
	private String documento;
	private String motivo;
	
	private String inhabilitado = "S";
	private List<SelectItem> inhabilitadoSelectItem;
	
	private List<Jubilados> listaJubilados;
	
	private Date fechaDesvinculacion;
	private String motivoDesvinculacion;
	private String ubicacionFisica = "";

	public void init() {
		idPais = idParaguay();
		if (nivelEntidadOeeUtil == null || (nivelEntidadOeeUtil.getCodSinEntidad() == null && nivelEntidadOeeUtil.getNombreNivelEntidad() == null)){
			nivelEntidadOeeUtil = (NivelEntidadOeeUtil) Component.getInstance(NivelEntidadOeeUtil.class, true);
			cargarCabecera();
		}
		
		search();
	}
	
	@SuppressWarnings("unchecked")
	private Long idParaguay() {
		List<Pais> p = em.createQuery(
				" Select p from Pais p"
						+ " where lower(p.descripcion) like 'paraguay'")
				.getResultList();
		if (!p.isEmpty())
			return p.get(0).getIdPais();

		return null;
	}
	
	
	public void searchAll(){
		limpiar();
		cargarCabecera();
		search();
	}
	
	public void search(){
		if (!validar(false))
			return;
		
		buscar();
	}
	
	public void buscar(){
		String consulta = "" +
			"select jubilados from Jubilados jubilados " +
			" join jubilados.empleadoPuesto empleadoPuesto " +
			" join jubilados.configuracionUoCab configuracionUoCab "+
			" join jubilados.persona persona " +
			" join persona.paisByIdPaisExpedicionDoc pais "+
			" where configuracionUoCab.idConfiguracionUo = :idConfiguracionUo";
		
		nivelEntidadOeeUtil.init();
		
		if(idPais != null){
			consulta += " and pais.idPais = :idPais";
		}
		
		if(!Utiles.vacio(documento)){
			consulta += " and lower(persona.documentoIdentidad) like :documento";
		}
		
		if(!Utiles.vacio(nombres)){
			consulta += " and lower(persona.nombres) like :nombres";
		}
		
		if(!Utiles.vacio(apellidos)){
			consulta += " and lower(persona.apellidos) like :apellidos";
		}
		
		if(!"T".equals(inhabilitado)){
			if ("S".equals(inhabilitado))
				consulta += " and jubilados.inhabilitado is true";
			else
				consulta += " and jubilados.inhabilitado is false";
		}
		
		
		Map<String, QueryValue> params = new HashMap<String, QueryValue>();
		params.put("idConfiguracionUo", new QueryValue(nivelEntidadOeeUtil.getIdConfigCab()));
		
		if(idPais != null)
			params.put("idPais", new QueryValue(idPais));
		
		if(!Utiles.vacio(documento))
			params.put("documento", new QueryValue("%" + documento.toLowerCase() + "%"));
		
		if(!Utiles.vacio(nombres))
			params.put("nombres", new QueryValue("%" + nombres.toLowerCase() + "%"));
		
		
		if(!Utiles.vacio(apellidos))
			params.put("apellidos", new QueryValue("%" + apellidos.toLowerCase() + "%"));

		listaJubilados = jubiladosList.listaResultadosCU442(consulta, params);
	}
	
	
	private void buildInhabilitadoSelectItem() {
		inhabilitadoSelectItem = new ArrayList<SelectItem>();
		inhabilitadoSelectItem.add(new SelectItem("S", "Si"));
		inhabilitadoSelectItem.add(new SelectItem("N", "No"));
		inhabilitadoSelectItem.add(new SelectItem("T", "Todos"));
	}


	public void cargarCabecera(){
		grupoPuestosController = (GrupoPuestosController) Component.getInstance(GrupoPuestosController.class, true);
		grupoPuestosController.initCabecera();
		nivelEntidadOeeUtil.limpiar();
		
		SinNivelEntidad sinNivelEntidad = grupoPuestosController.getSinNivelEntidad();
		SinEntidad sinEntidad = grupoPuestosController.getSinEntidad();
		ConfiguracionUoCab configuracionUoCab = grupoPuestosController.getConfiguracionUoCab();
		
		//Nivel
		nivelEntidadOeeUtil.setIdSinNivelEntidad(sinNivelEntidad.getIdSinNivelEntidad());
		
		//Entidad
		nivelEntidadOeeUtil.setIdSinEntidad(sinEntidad.getIdSinEntidad());
		
		//OEE
		nivelEntidadOeeUtil.setIdConfigCab(configuracionUoCab.getIdConfiguracionUo());
		
		nivelEntidadOeeUtil.init();
	}
	
	
	public void limpiar(){
		nivelEntidadOeeUtil.limpiar();
		setNombres(null);
		setApellidos(null);
		documento = null;
		inhabilitado = "S";
		idPais = idParaguay();
	}

	
	private Boolean validar(Boolean controlarCab) {
		if ( nivelEntidadOeeUtil.getIdSinNivelEntidad()== null) {
			statusMessages.add(Severity.ERROR, "Nivel es un campo requerido. Verifique.");
			return false;
		}
		else if ( nivelEntidadOeeUtil.getIdSinEntidad() == null) {
			statusMessages.add(Severity.ERROR, "Entidad es un campo requerido. Verifique.");
			return false;
		}
		else if ( nivelEntidadOeeUtil.getIdConfigCab() == null) {
			statusMessages.add(Severity.ERROR, "OEE es un campo requerido. Verifique.");
			return false;
		}
		
		
//		if (fechaInhabilitacionDesde != null && fechaInhabilitacionHasta != null && fechaInhabilitacionDesde.after(fechaInhabilitacionHasta)) {
//      	statusMessages.add(Severity.ERROR, "El rango de fechas es inv\u00E1lido, la Fecha Desde no debe superar la Fecha Hasta.");
//      	return;
//  	}
//		
		
		return true;
	}

	

	
	/** EDITAR **/
	
	public void initEdit() {
		try{
			//idJubilado
			cargarJubilado();
			cargarCabeceraEdit();
			cargarDesvinculacion();
			
			Calendar c = Calendar.getInstance();
			String separador = File.separator;
			ubicacionFisica = separador+"SICCA"+separador + c.get(Calendar.YEAR) + 
			separador+"Usuario_Portal"+separador+ jubilados.getPersona().getDocumentoIdentidad()+"_"+jubilados.getPersona().getIdPersona();
			motivo = jubilados.getMotivoHabilit();
		}
		catch(Exception e){
			e.printStackTrace();
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, "Ocurrió un error al cargar la página.");
		}
		
	}


	private void cargarJubilado() {
		jubilados = em.find(Jubilados.class, idJubilado);
		Persona persona = em.find(Persona.class, jubilados.getEmpleadoPuesto().getPersona().getIdPersona());
		jubilados.getEmpleadoPuesto().setPersona(persona);
	}


	public void cargarCabeceraEdit(){
		grupoPuestosController = (GrupoPuestosController) Component.getInstance(GrupoPuestosController.class, true);
		grupoPuestosController.setIdConfUoCab(jubilados.getConfiguracionUoCab().getIdConfiguracionUo());
		grupoPuestosController.initCabecera();
	}

	
	@SuppressWarnings("unchecked")
	private void cargarDesvinculacion() {
		fechaDesvinculacion = null;
		motivoDesvinculacion = null;
		
		try{
			String sql = "select d.* " +
			"from desvinculacion.desvinculacion d " +
			"where id_empleado_puesto = " + jubilados.getEmpleadoPuesto().getIdEmpleadoPuesto();
			
			List<Desvinculacion> lista = em.createNativeQuery(sql, Desvinculacion.class).getResultList();
			
			if(lista != null && lista.size() > 0){
				Desvinculacion desvinculacion = lista.get(0);
				fechaDesvinculacion = desvinculacion.getFechaAlta();
				DatosEspecificos datosEspecificos = em.find(DatosEspecificos.class, desvinculacion.getDatosEspecifMotivo().getIdDatosEspecificos());
				motivoDesvinculacion = datosEspecificos.getDescripcion();
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	public void habilitarFuncionario(){
		if(idJubilado != null){
			//Nuevo
			try{
				
				jubilados = em.find(Jubilados.class, idJubilado);
				
				if(!existeDocumentoAdjunto()){
					statusMessages.clear();
					statusMessages.add(Severity.ERROR, "Debe levantar un documento adjunto antes de Habilitar al Funcionario");
					return;
				}
				if(motivo != null && motivo.trim().isEmpty()){
					statusMessages.clear();
					statusMessages.add(Severity.ERROR, "Debe ingresar el motivo de habilitación");
					return;
				}
				
				
				jubilados.setInhabilitado(false);
				Date fecha = new Date();
				jubilados.setFechaHabilit(fecha);
				jubilados.setUsuHabilit(usuarioLogueado.getCodigoUsuario());
				jubilados.setFechaMod(fecha);
				jubilados.setUsuMod(usuarioLogueado.getCodigoUsuario());
				jubilados.setMotivoHabilit(motivo);
				em.merge(jubilados);
				
				em.flush();
				
				initEdit();
				
				statusMessages.clear();
				statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
		
	}
	
	
	@SuppressWarnings("unchecked")
	private boolean existeDocumentoAdjunto() {
		try{
			String sql = "" +
				" select documentos.* " +
				" from general.documentos " +
				" where id_tabla = " + idJubilado +
				" and nombre_tabla = 'jubilados' " +
				" and nombre_pantalla = 'admJubiladosSFP'";

			List<Documentos> lista = em.createNativeQuery(sql, Documentos.class).getResultList();
			if(lista != null && lista.size() > 0)
				return true;
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		return false;
	}


	public String getTitulo(){
		if (modoVista){
			return "Ver Jubilado";
		}

		return "Editar Jubilado";
	}
	
	
	
	
	
	
	
	/** GETTER'S && SETTER'S **/
	
	public NivelEntidadOeeUtil getNivelEntidadOeeUtil() {
		return nivelEntidadOeeUtil;
	}
	public void setNivelEntidadOeeUtil(NivelEntidadOeeUtil nivelEntidadOeeUtil) {
		this.nivelEntidadOeeUtil = nivelEntidadOeeUtil;
	}

	public void setNombres(String nombres) {
		this.nombres = nombres;
	}
	public String getNombres() {
		return nombres;
	}

	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}
	public String getApellidos() {
		return apellidos;
	}


	public void setDocumento(String documento) {
		this.documento = documento;
	}
	public String getDocumento() {
		return documento;
	}



	public void setInhabilitado(String inhabilitado) {
		this.inhabilitado = inhabilitado;
	}


	public String getInhabilitado() {
		return inhabilitado;
	}


	public void setInhabilitadoSelectItem(List<SelectItem> inhabilitadoSelectItem) {
		this.inhabilitadoSelectItem = inhabilitadoSelectItem;
	}

	public List<SelectItem> getInhabilitadoSelectItem() {
		if (inhabilitadoSelectItem == null)
			buildInhabilitadoSelectItem();
		
		return inhabilitadoSelectItem;
	}


	public void setListaJubilados(List<Jubilados> listaJubilados) {
		this.listaJubilados = listaJubilados;
	}
	public List<Jubilados> getListaJubilados() {
		return listaJubilados;
	}


	public void setIdJubilado(Long idJubilado) {
		this.idJubilado = idJubilado;
	}
	public Long getIdJubilado() {
		return idJubilado;
	}

	public void setJubilados(Jubilados jubilados) {
		this.jubilados = jubilados;
	}
	public Jubilados getJubilados() {
		return jubilados;
	}


	public void setModoVista(Boolean modoVista) {
		this.modoVista = modoVista;
	}


	public Boolean getModoVista() {
		return modoVista;
	}


	public void setFechaDesvinculacion(Date fechaDesvinculacion) {
		this.fechaDesvinculacion = fechaDesvinculacion;
	}


	public Date getFechaDesvinculacion() {
		return fechaDesvinculacion;
	}


	public void setMotivoDesvinculacion(String motivoDesvinculacion) {
		this.motivoDesvinculacion = motivoDesvinculacion;
	}


	public String getMotivoDesvinculacion() {
		return motivoDesvinculacion;
	}


	public void setUbicacionFisica(String ubicacionFisica) {
		this.ubicacionFisica = ubicacionFisica;
	}


	public String getUbicacionFisica() {
		return ubicacionFisica;
	}


	public Long getIdPais() {
		return idPais;
	}


	public void setIdPais(Long idPais) {
		this.idPais = idPais;
	}

	public String getMotivo() {
		return motivo;
	}

	public void setMotivo(String motivo) {
		this.motivo = motivo;
	}
	
	
	
	
}
