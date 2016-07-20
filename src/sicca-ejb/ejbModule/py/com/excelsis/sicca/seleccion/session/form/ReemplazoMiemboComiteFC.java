package py.com.excelsis.sicca.seleccion.session.form;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;
import py.com.excelsis.sicca.entity.ComisionSeleccionCab;
import py.com.excelsis.sicca.entity.ComisionSeleccionDet;
import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.entity.Excepcion;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.seguridad.entity.Rol;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.JbpmUtilFormController;
import py.com.excelsis.sicca.session.util.ReferenciasUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.GrupoPuestosController;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("reemplazoMiemboComiteFC")
public class ReemplazoMiemboComiteFC implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1661926467489160485L;
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(create = true)
	GrupoPuestosController grupoPuestosController;
	@In(create = true)
	JbpmUtilFormController jbpmUtilFormController;
	@In(create = true)
	ReferenciasUtilFormController referenciasUtilFormController;
	@In(create = true)
	SeleccionUtilFormController seleccionUtilFormController;
	
	private String entity = "ComisionSeleccionCab";
	private String idEntity = "";
	private String nombrePantalla = "ReemplazoMiembrosCS";
	private String ubicacionFisica = "";
	private String from;
	
	private String docIdentidad;
	private String nombre;
	private Long idRol;
	private String titularSuplente;
	private String motivoExclusion;
	private Date fechaExclusion = new Date();
	private Long idComisionSeleccion;
	private Integer filaActual;
	
	private final String CODIGO_TITULAR = "T";
	private final String CODIGO_SUPLENTE = "S";

	private List<SelectItem> listaRoles;
	private List<ComisionSeleccionDet> listaComisionSeleccionDet;
	
	private Boolean editComision;
	
	private Long idExcepcion;
	private Boolean exclusion;
	
	public void init() {
		cargar();
		setEditComision(false);
		
		initParametrosGrales();
	}

	private void initParametrosGrales(){
		if (from == null)
			from = "/";
		
		if (esExclusion()){
			nombrePantalla = "ExclusionMiembroCS";
		}
	}
	
	public void limpiar(){
		idComisionSeleccion = null;
	}
	
	public void cargar(){
		cargarRoles();
		cargarComision();
	}
	
	public void cargarRoles(){
		listaRoles = new ArrayList<SelectItem>();
		listaRoles.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
		
		String consulta = "" +
				" select rol from Rol rol " +
				" where rol.tipo = :tipo " +
				"    and rol.activo = :activo " +
				" order by rol.descripcion ";
		
		try{
			Query q = em.createQuery(consulta);
			q.setParameter("tipo", "COMS");
			q.setParameter("activo", true);
				
			List<Rol> roles = q.getResultList();
			if (roles != null){
				for(Rol r : roles){
					listaRoles.add(new SelectItem(r.getId(), r.getDescripcion()));
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Carga los integrantes de la comision
	 */
	@SuppressWarnings("unchecked")
	public void cargarComision(){
		listaComisionSeleccionDet = new ArrayList <ComisionSeleccionDet>();
		if (idComisionSeleccion != null){
			String consulta = "" +
					" select comisionSeleccionDet from ComisionSeleccionDet comisionSeleccionDet " +
					" join comisionSeleccionDet.comisionSeleccionCab comisionSeleccionCab " +
					" where comisionSeleccionDet.tipo = :tipo " +
					"    and comisionSeleccionDet.activo = :activo " +
					"    and comisionSeleccionCab.idComisionSel = :idComisionSel ";
			try{
				Query q = em.createQuery(consulta);
				q.setParameter("tipo", "CONCURSO");
				q.setParameter("activo", true);
				q.setParameter("idComisionSel", idComisionSeleccion);
					
				listaComisionSeleccionDet = q.getResultList();
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	
	
	public void editar(Integer fila) {
		filaActual = fila;
		ComisionSeleccionDet comisionSeleccionDet = listaComisionSeleccionDet.get(fila);
		editComision = true;
		docIdentidad = comisionSeleccionDet.getPersona().getDocumentoIdentidad();
		nombre = comisionSeleccionDet.getPersona().getNombreCompleto();
		idRol = comisionSeleccionDet.getRol().getId();
		titularSuplente = comisionSeleccionDet.getTitularSuplente();
    }
	
	
	public void inactivar(Integer fila) {
		ComisionSeleccionDet comisionSeleccionDet = listaComisionSeleccionDet.get(fila);
		comisionSeleccionDet.setActivo(false);
		comisionSeleccionDet.setSeleccionado(true);
    }
	
	public void activar(Integer fila) {
		ComisionSeleccionDet comisionSeleccionDet = listaComisionSeleccionDet.get(fila);
		comisionSeleccionDet.setActivo(true);
		comisionSeleccionDet.setSeleccionado(true);
    }
	
	
	public void actualizar() {
		ComisionSeleccionDet comisionSeleccionDet = listaComisionSeleccionDet.get(filaActual);
		//Verificar si hubo cambios
		if (idRol != null && titularSuplente != null){
			if (comisionSeleccionDet.getRol().getId().longValue() != idRol.longValue() || !comisionSeleccionDet.getTitularSuplente().equals(titularSuplente)){
				comisionSeleccionDet.setSeleccionado(true);
				comisionSeleccionDet.setRol(em.find(Rol.class, idRol));
				comisionSeleccionDet.setTitularSuplente(titularSuplente);
			}
		}
		else{
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, "Debe seleccionar un rol.");
			return;
		}
		editComision = false;
		limpiarSelComision();
    }

	
	public void cancelar() {
		editComision = false;
		ComisionSeleccionDet comisionSeleccionDet = listaComisionSeleccionDet.get(filaActual);
		comisionSeleccionDet.setSeleccionado(false);
		limpiarSelComision();
    }
	
	
	private void limpiarSelComision() {
		docIdentidad = null;
		nombre = null;
		idRol = null;
		titularSuplente = null;
	}
	
	@End
	public String save(){
		
		if (!huboCambios()){
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, "Debe realizar el reemplazo previamente antes de guardar el registro.");
			return null;
		}
		
		try{
			Excepcion excepcion = new Excepcion();
			
			if (esExclusion())
				excepcion.setTipo("EXCLUSION MIEMBRO CS");
			else
				excepcion.setTipo("REEMPLAZO MIEMBRO CS");
			
			excepcion.setEstado("REGISTRADO");
			excepcion.setActivo(true);
			excepcion.setObservacion(motivoExclusion);
			excepcion.setFechaAlta(fechaExclusion);
			excepcion.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			excepcion.setConcurso(new Concurso());
			excepcion.getConcurso().setIdConcurso(grupoPuestosController.getIdConcurso());
			em.persist(excepcion);
			
			for(ComisionSeleccionDet c :listaComisionSeleccionDet){
				boolean cambio = false;
				// Se verifica si cambio el registro
				if (esExclusion()){
					if (!c.getActivo())
						cambio = true;
				}
				else{
					if (c.getSeleccionado() != null && c.getSeleccionado())
						cambio = true;
				}
				
				if (cambio){
					c.setFechaMod(fechaExclusion);
					c.setUsuMod(usuarioLogueado.getCodigoUsuario());
					em.merge(c);
					
					ComisionSeleccionDet comisionSeleccionDet = new ComisionSeleccionDet();
					comisionSeleccionDet.setActivo(c.getActivo());
					
					comisionSeleccionDet.setComisionSeleccionCab(c.getComisionSeleccionCab());
					comisionSeleccionDet.setConfiguracionUo(c.getConfiguracionUo());
					comisionSeleccionDet.setDocumentos(c.getDocumentos());

					comisionSeleccionDet.setEquipoTecnico(c.getEquipoTecnico());
					comisionSeleccionDet.setExcepcion(excepcion);
					comisionSeleccionDet.setFechaAlta(fechaExclusion);
					
					comisionSeleccionDet.setPersona(new Persona());
					comisionSeleccionDet.getPersona().setIdPersona(c.getPersona().getIdPersona());
					
					comisionSeleccionDet.setPuesto(c.getPuesto());
					
					comisionSeleccionDet.setRol(new Rol());
					comisionSeleccionDet.getRol().setIdRol(c.getRol().getId());
					
					if (esExclusion())
						comisionSeleccionDet.setTipo("EXC. EXCLUSION");
					else
						comisionSeleccionDet.setTipo("EXC. REEMPLAZO");
					
					comisionSeleccionDet.setTitularSuplente(c.getTitularSuplente());
					comisionSeleccionDet.setUsuAlta(usuarioLogueado.getCodigoUsuario());
					
					em.persist(comisionSeleccionDet);
				}
			}
		
			em.flush();
			statusMessages.clear();
			statusMessages.add(Severity.INFO, "Operación realizada con éxito");
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		return from;
	}
	
	
	/**
	 * Verifica si alguno de los elementos de la lista ha sido modificado
	 * @return
	 */
	public boolean huboCambios(){
		if (esExclusion()){
			for(ComisionSeleccionDet c : listaComisionSeleccionDet){
				if (!c.getActivo())
					return true;
			}
		}
		else{
			for(ComisionSeleccionDet c : listaComisionSeleccionDet){
				if (c.getSeleccionado() != null && c.getSeleccionado())
					return true;
			}
		}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	public List<SelectItem> getComisionSeleccionSelectItem() {
		List<SelectItem> lista = new ArrayList<SelectItem>();
		lista.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
		
		String consulta = "" +
				" select comisionSeleccionCab from ComisionSeleccionCab comisionSeleccionCab " +
				" join comisionSeleccionCab.concurso concurso " +
				" where concurso.idConcurso = :idConcurso" +
				" order by comisionSeleccionCab.descripcion ";
		
		try{
			Query q = em.createQuery(consulta);
			q.setParameter("idConcurso", grupoPuestosController.getIdConcurso());
				
			List<ComisionSeleccionCab> comisionSeleccionCabs = q.getResultList();
			if (comisionSeleccionCabs != null){
				for(ComisionSeleccionCab c : comisionSeleccionCabs){
					lista.add(new SelectItem(c.getIdComisionSel(), c.getDescripcion()));
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		return lista;
	}
	
	
	public List<SelectItem> getTitularSuplenteSelectItem() {
		List<SelectItem> lista = new ArrayList<SelectItem>();
		lista.add(new SelectItem(CODIGO_TITULAR,  "Titular"));
		lista.add(new SelectItem(CODIGO_SUPLENTE, "Suplente"));
		return lista;
	}
	
	public void limpiarComision(){
		idComisionSeleccion = null;
		listaComisionSeleccionDet = new ArrayList <ComisionSeleccionDet>();
		
		if (grupoPuestosController.getIdConcurso() != null){
			Calendar c = Calendar.getInstance();
			Concurso concurso = em.find(Concurso.class, grupoPuestosController.getIdConcurso());
			ubicacionFisica = "\\SICCA\\" + c.get(Calendar.YEAR) + "\\OEE\\" + concurso.getConfiguracionUoCab().getIdConfiguracionUo() + "\\" +
							concurso.getDatosEspecificosTipoConc().getIdDatosEspecificos() + "\\" + 
							concurso.getIdConcurso() + "\\CS";
		}
	}
	

	/* VISTA DE REEMPLAZO */
	public void initView() {
		try{
			cargar();
			setEditComision(false);
			
			initParametrosGrales();
			
			if (idExcepcion != null){
				
				Excepcion ex = em.find(Excepcion.class, idExcepcion);
				motivoExclusion = ex.getObservacion();
				fechaExclusion = ex.getFechaAlta();
				
				cargarComision(ex);
				
				if(listaComisionSeleccionDet.size() > 0){
					ComisionSeleccionDet comisionSeleccionDet = listaComisionSeleccionDet.get(0);
					grupoPuestosController.setIdConcurso(comisionSeleccionDet.getComisionSeleccionCab().getConcurso().getIdConcurso());
					idComisionSeleccion = comisionSeleccionDet.getComisionSeleccionCab().getIdComisionSel();
				}
				
				if (grupoPuestosController.getIdConcurso() != null){
					Calendar c = Calendar.getInstance();
					Concurso concurso = em.find(Concurso.class, grupoPuestosController.getIdConcurso());
					ubicacionFisica = "\\SICCA\\" + c.get(Calendar.YEAR) + "\\OEE\\" + concurso.getConfiguracionUoCab().getIdConfiguracionUo() + "\\" +
									concurso.getDatosEspecificosTipoConc().getIdDatosEspecificos() + "\\" + 
									concurso.getIdConcurso() + "\\CS";
				}
			}
			else{
				statusMessages.clear();
				statusMessages.add(Severity.ERROR, "Identificador no válido.");
			}
		}
		catch(Exception e){
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, "Error al inicializar.");
		}
	}
	
	
	/**
	 * Carga los integrantes de la comision
	 */
	@SuppressWarnings("unchecked")
	public void cargarComision(Excepcion ex){
		listaComisionSeleccionDet = new ArrayList <ComisionSeleccionDet>();
		String consulta = "" +
					" select comisionSeleccionDet from ComisionSeleccionDet comisionSeleccionDet " +
					" join comisionSeleccionDet.excepcion excepcion " +
					" where excepcion.idExcepcion = :idExcepcion ";
		try{
			Query q = em.createQuery(consulta);
			q.setParameter("idExcepcion", ex.getIdExcepcion());
			//q.setParameter("activo", true);
					
			listaComisionSeleccionDet = q.getResultList();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	
	/* SETTER'S && GETTER'S */
	public String getEntity() {
		return entity;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}


	public String getIdEntity() {
		return idEntity;
	}

	public void setIdEntity(String idEntity) {
		this.idEntity = idEntity;
	}


	public String getNombrePantalla() {
		return nombrePantalla;
	}

	public void setNombrePantalla(String nombrePantalla) {
		this.nombrePantalla = nombrePantalla;
	}


	public String getUbicacionFisica() {
		return ubicacionFisica;
	}

	public void setUbicacionFisica(String ubicacionFisica) {
		this.ubicacionFisica = ubicacionFisica;
	}


	public void setFrom(String from) {
		this.from = from;
	}


	public String getFrom() {
		return from;
	}


	public void setIdComisionSeleccion(Long idComisionSeleccion) {
		this.idComisionSeleccion = idComisionSeleccion;
	}


	public Long getIdComisionSeleccion() {
		return idComisionSeleccion;
	}


	public String getDocIdentidad() {
		return docIdentidad;
	}


	public void setDocIdentidad(String docIdentidad) {
		this.docIdentidad = docIdentidad;
	}


	public String getNombre() {
		return nombre;
	}


	public void setNombre(String nombre) {
		this.nombre = nombre;
	}


	public Long getIdRol() {
		return idRol;
	}


	public void setIdRol(Long idRol) {
		this.idRol = idRol;
	}


	public String getTitularSuplente() {
		return titularSuplente;
	}


	public void setTitularSuplente(String titularSuplente) {
		this.titularSuplente = titularSuplente;
	}


	public List<SelectItem> getListaRoles() {
		return listaRoles;
	}


	public void setListaRoles(List<SelectItem> listaRoles) {
		this.listaRoles = listaRoles;
	}


	public void setListaComisionSeleccionDet(
			List<ComisionSeleccionDet> listaComisionSeleccionDet) {
		this.listaComisionSeleccionDet = listaComisionSeleccionDet;
	}


	public List<ComisionSeleccionDet> getListaComisionSeleccionDet() {
		return listaComisionSeleccionDet;
	}


	public void setEditComision(Boolean editComision) {
		this.editComision = editComision;
	}


	public Boolean getEditComision() {
		return editComision;
	}


	public void setMotivoExclusion(String motivoExclusion) {
		this.motivoExclusion = motivoExclusion;
	}


	public String getMotivoExclusion() {
		return motivoExclusion;
	}


	public void setFechaExclusion(Date fechaExclusion) {
		this.fechaExclusion = fechaExclusion;
	}


	public Date getFechaExclusion() {
		return fechaExclusion;
	}


	public void setIdExcepcion(Long idExcepcion) {
		this.idExcepcion = idExcepcion;
	}


	public Long getIdExcepcion() {
		return idExcepcion;
	}


	public void setExclusion(Boolean exclusion) {
		this.exclusion = exclusion;
	}


	public Boolean getExclusion() {
		return exclusion;
	}

	
	public boolean esExclusion(){
		if (exclusion == null)
			return false;
		
		return exclusion;
	}
}
