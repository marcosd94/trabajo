package py.com.excelsis.sicca.juridicos.session.form;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import py.com.excelsis.sicca.entity.AccionInconstCab;
import py.com.excelsis.sicca.entity.AccionInconstDet;
import py.com.excelsis.sicca.entity.CalendarioOeeDet;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.EmpleadoPuesto;
import py.com.excelsis.sicca.entity.Entidad;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.juridicos.session.AccionInconstCabHome;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.SinNivelEntidadList;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("registrarAccionesInconstitucionalesFC")
public class RegistrarAccionesInconstitucionalesFC implements Serializable {

	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(create = true)
	SinNivelEntidadList sinNivelEntidadList;
	@In(create = true)
	AccionInconstCabHome accionInconstCabHome;

	private SinNivelEntidad nivelEntidad = new SinNivelEntidad();
	private SinEntidad sinEntidad = new SinEntidad();
	private ConfiguracionUoCab configuracionUoCab = new ConfiguracionUoCab();
	private Entidad entidad = new Entidad();
	private AccionInconstCab accionInconstCab = new AccionInconstCab();
	private EmpleadoPuesto empleadoPuesto = new EmpleadoPuesto();

	private String estado;
	private String otro;
	private String articulo;
	private String especificacionArticulo;
	private String ubicacionFisica;
	private Long idEmpleadoPuesto;
	private Long idLeyes;
	private Boolean mostrarOtra;
	private Boolean mostrarEspArt;
	private Boolean cargarAnexos;
	private Boolean isEdit;
	private Boolean isSave;
	private String operacion;

	private List<SelectItem> leyesSelecItem = new ArrayList<SelectItem>();
	private List<AccionInconstDet> listaDetalles = new ArrayList<AccionInconstDet>();

	/**
	 * Método que inicializa los datos
	 */
	public void init() {
		
		buscarDatosAsociadosUsuario();
	
		if (!accionInconstCabHome.isIdDefined()) {
			cargarAnexos = false;
			isEdit = false;
			isSave = false;
			accionInconstCab = new AccionInconstCab();
			accionInconstCab = accionInconstCabHome.getInstance();
			if (idEmpleadoPuesto != null) {
				empleadoPuesto = em
						.find(EmpleadoPuesto.class, idEmpleadoPuesto);
				
			}
			if (idEmpleadoPuesto == null) {
				estado = "P";
				articulo = "T";
				mostrarOtra = false;
				mostrarEspArt = false;
			}
			idEmpleadoPuesto = null;
			updatedLeyesSelectItem();
		}
		else{
			
			accionInconstCab = new AccionInconstCab();
			accionInconstCab = accionInconstCabHome.getInstance();
			empleadoPuesto = new EmpleadoPuesto();
			empleadoPuesto = accionInconstCab.getEmpleadoPuesto();
			if(listaDetalles == null || listaDetalles.size() == 0){
				listaDetalles = new ArrayList<AccionInconstDet>();
				listaDetalles = accionInconstCab.getAccionInconstDets();
			}
			estado = accionInconstCab.getEstado();
			cargarAnexos = true;
			isEdit = true;
			if(operacion == null){
				operacion = "updated";
				isSave = false;
			}
			else
				isSave = true;
			updatedLeyesSelectItem();
			articulo = "T";
			mostrarOtra = false;
			mostrarEspArt = false;
			obtenerDireccionFisica();
		}
		
	}

	/**
	 * Obtiene la direccion fisica donde se adjuntara el documento
	 */
	private void obtenerDireccionFisica(){
		Date fechaActual = new Date();
		Integer anhoActual = fechaActual.getYear() + 1900;
		String separador = File.separator;
		ubicacionFisica = separador+"SICCA"+separador
			+ anhoActual+separador+"PJ"+separador+"AI"+separador
			+ empleadoPuesto.getPersona().getIdPersona();
	}
	
	/**
	 * Metodo que agrega datos a la lista
	 */
	public void agregarLista() {
		if ((!mostrarOtra && idLeyes == null)
				|| (mostrarOtra && (otro == null || otro.trim().isEmpty()))
				|| (mostrarEspArt && (especificacionArticulo == null || especificacionArticulo
						.trim().isEmpty()))) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR,
					"Llene los datos obligatorios para agregar a la grilla");
			return;
		}
		AccionInconstDet det = new AccionInconstDet();
		if (mostrarEspArt) {
			det.setArtEspecif(especificacionArticulo);
			det.setArtTodos(false);
		}
		if (!mostrarEspArt)
			det.setArtTodos(true);
		if (mostrarOtra)
			det.setOtraLey(otro);
		if (!mostrarOtra && idLeyes != null && idLeyes.intValue() != 0)
			det.setDatosEspecifLey(em.find(DatosEspecificos.class, idLeyes));

		listaDetalles.add(det);
		idLeyes = null;
		mostrarEspArt = false;
		mostrarOtra = false;
		especificacionArticulo = null;
		otro = null;
	}

	
	public void eliminarLista(Integer row) {
		AccionInconstDet det = new AccionInconstDet();
		det = listaDetalles.get(row);
		listaDetalles.remove(det);

	}

	public void save() {
		if (!check()) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR,
					"Debe llenar los campos obligatorios");
			return;
		}
		accionInconstCab.setEmpleadoPuesto(empleadoPuesto);
		accionInconstCab.setEstado(estado);
		accionInconstCab.setPersona(empleadoPuesto.getPersona());
		try {
			accionInconstCabHome.setInstance(accionInconstCab);
			String resultado = accionInconstCabHome.persist();
			if (resultado.equals("persisted")) {
				for (AccionInconstDet d : listaDetalles) {
					d.setAccionInconstCab(accionInconstCabHome.getInstance());
					d.setFechaAlta(new Date());
					d.setUsuAlta(usuarioLogueado.getCodigoUsuario());
					em.persist(d);
					em.flush();
				}
			}
		} catch (Exception e) {
			statusMessages.add(Severity.ERROR, e.getMessage());
			return;
		}
		obtenerDireccionFisica();
		cargarAnexos = true;
		isSave = true;
		operacion = "persisted";
		statusMessages.clear();
		statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
				.getString("GENERICO_MSG"));
		statusMessages.add(Severity.WARN, "Si desea adjuntar un documento presione el botón Anexos si no, presione el Volver");
	}

	public String updated() {
		if (!chequear()) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR,
					"Debe llenar los campos obligatorios");
			return null;
		}
		accionInconstCab.setEstado(estado);
		try {
			accionInconstCabHome.setInstance(accionInconstCab);
			String resultado = accionInconstCabHome.update();
			
		} catch (Exception e) {
			statusMessages.add(Severity.ERROR, e.getMessage());
			return null;
		}
		Date fechaActual = new Date();
		Integer anhoActual = fechaActual.getYear() + 1900;
		
		statusMessages.clear();
		statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
				.getString("GENERICO_MSG"));
		return "updated";
	}

	private Boolean check() {
		if (estado == null || empleadoPuesto == null
				|| empleadoPuesto.getIdEmpleadoPuesto() == null
				|| listaDetalles == null || listaDetalles.size() == 0)
			return false;
		if(estado.equals("A") && (accionInconstCab.getResultado() == null || accionInconstCab.getResultado().trim().isEmpty()))
			return false;
		return true;
	}
	
	private Boolean chequear() {
		if (estado == null)
			return false;
		if(estado.equals("A") && (accionInconstCab.getResultado() == null || accionInconstCab.getResultado().trim().isEmpty()))
			return false;
		return true;
	}

	/**
	 * Método que recupera los datos del usuario logueado
	 */
	private void buscarDatosAsociadosUsuario() {
		if (usuarioLogueado.getConfiguracionUoCab() != null) {

			configuracionUoCab = new ConfiguracionUoCab();
			Long id = usuarioLogueado.getConfiguracionUoCab()
					.getIdConfiguracionUo();
			configuracionUoCab = em.find(ConfiguracionUoCab.class, id);

			if (configuracionUoCab.getEntidad() != null)
				entidad = configuracionUoCab.getEntidad();
			sinEntidad = entidad.getSinEntidad();
			nivelEntidad.setNenCodigo(sinEntidad.getNenCodigo());
			findNivelEntidad();
		}
	}

	public void findNivelEntidad() {
		if (nivelEntidad.getNenCodigo() != null) {
			sinNivelEntidadList.getSinNivelEntidad().setNenCodigo(
					nivelEntidad.getNenCodigo());
			nivelEntidad = sinNivelEntidadList.nivelEntidadMaxAnho();
		} else
			nivelEntidad = new SinNivelEntidad();
	}

	/**
	 * Métodos que cargan los combos a ser desplegados
	 */
	// Combo leyes
	@SuppressWarnings("unchecked")
	public void updatedLeyesSelectItem() {
		String cadena = "SELECT DE.* " + "FROM SELECCION.DATOS_ESPECIFICOS DE "
				+ "JOIN SELECCION.DATOS_GENERALES DG "
				+ "ON DG.ID_DATOS_GENERALES = DE.ID_DATOS_GENERALES "
				+ "WHERE DG.DESCRIPCION = 'NORMATIVA' " + "AND DE.ACTIVO = TRUE ";

		List<DatosEspecificos> lista = new ArrayList<DatosEspecificos>();
		lista = em.createNativeQuery(cadena, DatosEspecificos.class)
				.getResultList();
		leyesSelecItem = new ArrayList<SelectItem>();
		leyesSelecItem.add(new SelectItem(null, SeamResourceBundle.getBundle()
				.getString("opt_select_one")));
		if (lista.size() > 0) {

			for (DatosEspecificos datos : lista) {
				leyesSelecItem.add(new SelectItem(
						datos.getIdDatosEspecificos(), datos.getDescripcion()));
			}
			leyesSelecItem.add(new SelectItem(0, "OTRA"));
		}
	}

	/**
	 * Método que llama al cu 159 - Buscar Funcionario
	 * 
	 * @return
	 */
	public String getUrlToPageSearchFuncionario() {

		return "/busquedas/funcionarios/EmpleadoPuestoList.xhtml?from=juridicos/registrarInconstitucionalidades/AccionInconstCabEdit";
	}

	public void esParaMostrarOtra() {
		if (idLeyes != null && idLeyes.intValue() == 0)
			mostrarOtra = true;
		else
			mostrarOtra = false;
	}

	public void esParaMostrarEspecificacionArt() {
		if (articulo != null && articulo.equals("E"))
			mostrarEspArt = true;
		else
			mostrarEspArt = false;
	}

	public SinNivelEntidad getNivelEntidad() {
		return nivelEntidad;
	}

	public void setNivelEntidad(SinNivelEntidad nivelEntidad) {
		this.nivelEntidad = nivelEntidad;
	}

	public SinEntidad getSinEntidad() {
		return sinEntidad;
	}

	public void setSinEntidad(SinEntidad sinEntidad) {
		this.sinEntidad = sinEntidad;
	}

	public ConfiguracionUoCab getConfiguracionUoCab() {
		return configuracionUoCab;
	}

	public void setConfiguracionUoCab(ConfiguracionUoCab configuracionUoCab) {
		this.configuracionUoCab = configuracionUoCab;
	}

	public Entidad getEntidad() {
		return entidad;
	}

	public void setEntidad(Entidad entidad) {
		this.entidad = entidad;
	}

	public AccionInconstCab getAccionInconstCab() {
		return accionInconstCab;
	}

	public void setAccionInconstCab(AccionInconstCab accionInconstCab) {
		this.accionInconstCab = accionInconstCab;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public EmpleadoPuesto getEmpleadoPuesto() {
		return empleadoPuesto;
	}

	public void setEmpleadoPuesto(EmpleadoPuesto empleadoPuesto) {
		this.empleadoPuesto = empleadoPuesto;
	}

	public Long getIdEmpleadoPuesto() {
		return idEmpleadoPuesto;
	}

	public void setIdEmpleadoPuesto(Long idEmpleadoPuesto) {
		this.idEmpleadoPuesto = idEmpleadoPuesto;
	}

	public List<SelectItem> getLeyesSelecItem() {
		return leyesSelecItem;
	}

	public void setLeyesSelecItem(List<SelectItem> leyesSelecItem) {
		this.leyesSelecItem = leyesSelecItem;
	}

	public Long getIdLeyes() {
		return idLeyes;
	}

	public void setIdLeyes(Long idLeyes) {
		this.idLeyes = idLeyes;
	}

	public String getOtro() {
		return otro;
	}

	public void setOtro(String otro) {
		this.otro = otro;
	}

	public Boolean getMostrarOtra() {
		return mostrarOtra;
	}

	public void setMostrarOtra(Boolean mostrarOtra) {
		this.mostrarOtra = mostrarOtra;
	}

	public String getArticulo() {
		return articulo;
	}

	public void setArticulo(String articulo) {
		this.articulo = articulo;
	}

	public Boolean getMostrarEspArt() {
		return mostrarEspArt;
	}

	public void setMostrarEspArt(Boolean mostrarEspArt) {
		this.mostrarEspArt = mostrarEspArt;
	}

	public String getEspecificacionArticulo() {
		return especificacionArticulo;
	}

	public void setEspecificacionArticulo(String especificacionArticulo) {
		this.especificacionArticulo = especificacionArticulo;
	}

	public List<AccionInconstDet> getListaDetalles() {
		return listaDetalles;
	}

	public void setListaDetalles(List<AccionInconstDet> listaDetalles) {
		this.listaDetalles = listaDetalles;
	}

	public String getUbicacionFisica() {
		return ubicacionFisica;
	}

	public void setUbicacionFisica(String ubicacionFisica) {
		this.ubicacionFisica = ubicacionFisica;
	}

	public Boolean getCargarAnexos() {
		return cargarAnexos;
	}

	public void setCargarAnexos(Boolean cargarAnexos) {
		this.cargarAnexos = cargarAnexos;
	}

	public String getOperacion() {
		return operacion;
	}

	public void setOperacion(String operacion) {
		this.operacion = operacion;
	}

	public Boolean getIsEdit() {
		return isEdit;
	}

	public void setIsEdit(Boolean isEdit) {
		this.isEdit = isEdit;
	}

	public Boolean getIsSave() {
		return isSave;
	}

	public void setIsSave(Boolean isSave) {
		this.isSave = isSave;
	}

	
	
	

}
