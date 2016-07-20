package py.com.excelsis.sicca.session.form;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import py.com.excelsis.sicca.entity.Barrio;
import py.com.excelsis.sicca.entity.Ciudad;
import py.com.excelsis.sicca.entity.Departamento;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.entity.SelFuncionDatosEsp;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.BarrioList;
import py.com.excelsis.sicca.session.CiudadList;
import py.com.excelsis.sicca.session.DepartamentoList;
import py.com.excelsis.sicca.session.PersonaHome;
import py.com.excelsis.sicca.session.util.UtilesFromController;
import py.com.excelsis.sicca.util.General;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("admDatosPersonalesPostulantesFormController")
public class AdmDatosPersonalesPostulantesFormController implements
		Serializable {

	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(create = true)
	CiudadList ciudadList;
	@In(create = true)
	DepartamentoList departamentoList;
	@In(create = true)
	BarrioList barrioList;
	@In(create = true)
	UtilesFromController utilesFromController;

	Persona persona;
	General general;

	private List<SelectItem> departamentosSelecItem;
	private List<SelectItem> ciudadSelecItem;

	private List<SelectItem> departamentosDirSelecItem;
	private List<SelectItem> ciudadDirSelecItem;
	private List<SelectItem> localidadDirSelecItem;

	private Long idSexo;
	private Long idTelContacto;
	private Long idEstadoCivil;
	private Long idPaisNac;
	private Long idDptoNac;
	private Long idCiudadNac;
	private Long idPaisDir;
	private Long idDptoDir;
	private Long idCiudadDir;
	private Long idLocalidadDir;
	private Boolean cargadoTab1 = true;
	private Boolean guardado = false;
	private Boolean link = false;
	private Date fechaNac;
	private String callePrincipal;
	private String callePrimeraLateral;
	private String calleSegundaLateral;
	private String dptoNro;
	private String piso;
	private String direccionLaboral;
	private String otrasDirecciones;
	private String telefonos;
	private String telefonoPart;
	private String telefonoLab;
	private String telContacto;
	private String aux;

	/**
	 * Método que inicializa los datos
	 */
	public void init() {

		if (!link) {
			persona = new Persona();
			Long idPersona = null;
			idPersona = usuarioLogueado.getPersona().getIdPersona();
			persona = em.find(Persona.class, idPersona);

			if (idPersona != null) {

				fechaNac = persona.getFechaNacimiento();
				callePrincipal = persona.getCallePrincipal();
				callePrimeraLateral = persona.getPrimeraLateral();
				calleSegundaLateral = persona.getSegundaLateral();
				dptoNro = persona.getDepartamentoNro();
				piso = persona.getPiso();
				direccionLaboral = persona.getDireccionLaboral();
				otrasDirecciones = persona.getOtrasDirecciones();
				telefonos = persona.getTelefonos();
				telContacto = persona.getTelContacto();
				telefonoLab = persona.getTelefonoLab();
				telefonoPart = persona.getTelefonoPart();
				idSexo = searchSexo(persona.getSexo());
				idTelContacto = searchTelContacto(persona.getTelContacto());
				idEstadoCivil = searchEstadoCivil(persona.getEstadoCivil());
				if (persona.getCiudadByIdCiudadNac() != null) {
					idPaisNac = persona.getCiudadByIdCiudadNac()
							.getDepartamento().getPais().getIdPais();
					updateDepartamento();
					idDptoNac = persona.getCiudadByIdCiudadNac()
							.getDepartamento().getIdDepartamento();
					updateCiudad();
					idCiudadNac = persona.getCiudadByIdCiudadNac()
							.getIdCiudad();
				} else {
					updateDepartamento();
					updateCiudad();
				}

				if (persona.getCiudadByIdCiudadDirecc() != null) {
					idPaisDir = persona.getCiudadByIdCiudadDirecc()
							.getDepartamento().getPais().getIdPais();

					updateDepartamentoDir();
					idDptoDir = persona.getCiudadByIdCiudadDirecc()
							.getDepartamento().getIdDepartamento();
					updateCiudadDir();
					idCiudadDir = persona.getCiudadByIdCiudadDirecc()
							.getIdCiudad();
					updateBarrio();
					if (persona.getBarrio() != null)

						idLocalidadDir = persona.getBarrio().getIdBarrio();
					else
						idLocalidadDir = null;

				} else {
					updateDepartamentoDir();
					updateCiudadDir();
					updateBarrio();
				}
			} else {
				idSexo = null;
				idTelContacto= null;
				idEstadoCivil = null;
				idCiudadNac = null;
				idDptoNac = null;
				idPaisNac = null;
				idPaisDir = null;
				idDptoDir = null;
				idCiudadDir = null;
				idLocalidadDir = null;
			}
		} else {
			Persona p = new Persona();
			Long idPersona = null;
			idPersona = usuarioLogueado.getPersona().getIdPersona();
			p = em.find(Persona.class, idPersona);
			persona.setEsFuncionario(p.getEsFuncionario());
		}

		update();
	}

	@SuppressWarnings("unchecked")
	private Long searchSexo(String c) {
		List<Referencias> lista = new ArrayList<Referencias>();
		try {
			lista = em
					.createQuery(
							" select o from "
									+ Referencias.class.getName()
									+ " o"
									+ " WHERE o.activo = true and o.dominio = 'SEXO' and o.descAbrev = '"
									+ c + "' ORDER BY o.dominio, o.descLarga")
					.getResultList();
		} catch (Exception ex) {
			return null;
		}
		if (lista.size() == 0)
			return null;
		return lista.get(0).getIdReferencias();
	}
	
	@SuppressWarnings("unchecked")
	private Long searchTelContacto(String c) {
		List<Referencias> lista = new ArrayList<Referencias>();
		try {
			lista = em
					.createQuery(
							" select o from "
									+ Referencias.class.getName()
									+ " o"
									+ " WHERE o.activo = true and o.dominio = 'TEL_CONTACTO' and o.descAbrev = '"
									+ c + "' ORDER BY o.dominio, o.descLarga")
					.getResultList();
		} catch (Exception ex) {
			return null;
		}
		if (lista.size() == 0)
			return null;
		return lista.get(0).getIdReferencias();
	}

	@SuppressWarnings("unchecked")
	private Long searchEstadoCivil(String c) {
		List<Referencias> lista = new ArrayList<Referencias>();
		try {
			lista = em
					.createQuery(
							" select o from "
									+ Referencias.class.getName()
									+ " o"
									+ " WHERE o.activo = true and o.dominio = 'ESTADO_CIVIL' and o.descAbrev = '"
									+ c + "' ORDER BY o.dominio, o.descLarga")
					.getResultList();
		} catch (Exception ex) {
			return null;
		}
		if (lista.size() == 0)
			return null;
		return lista.get(0).getIdReferencias();
	}

	/**
	 * Combo Departamento Nacimiento
	 */
	public void updateDepartamento() {
		List<Departamento> dptoList = getDepartamentosByPais();
		departamentosSelecItem = new ArrayList<SelectItem>();
		buildDepartamentoSelectItem(dptoList);
		idDptoNac = null;
		idCiudadNac = null;

	}

	private List<Departamento> getDepartamentosByPais() {
		if (idPaisNac != null) {
			departamentoList.getPais().setIdPais(idPaisNac);
			departamentoList.setOrder("descripcion");
			departamentoList.setMaxResults(null);
			return departamentoList.getResultList();
		}
		return new ArrayList<Departamento>();
	}

	private void buildDepartamentoSelectItem(List<Departamento> dptoList) {
		if (departamentosSelecItem == null)
			departamentosSelecItem = new ArrayList<SelectItem>();
		else
			departamentosSelecItem.clear();

		departamentosSelecItem.add(new SelectItem(null, SeamResourceBundle
				.getBundle().getString("opt_select_one")));
		departamentosSelecItem.add(0, new SelectItem("",""));
		for (Departamento dep : dptoList) {
			departamentosSelecItem.add(new SelectItem(dep.getIdDepartamento(),
					dep.getDescripcion()));
		}
	}

	/**
	 * Combo ciudad Nacimiento
	 */
	public void updateCiudad() {
		List<Ciudad> ciuList = getCiudadByDpto();
		ciudadSelecItem = new ArrayList<SelectItem>();
		buildCiudadSelectItem(ciuList);
		idCiudadNac = null;
	}

	private List<Ciudad> getCiudadByDpto() {
		if (idDptoNac != null) {
			ciudadList.getDepartamento().setIdDepartamento(idDptoNac);
			ciudadList.setMaxResults(null);
			return ciudadList.getResultList();
		}
		return new ArrayList<Ciudad>();
	}

	private void buildCiudadSelectItem(List<Ciudad> ciudadList) {
		if (ciudadSelecItem == null)
			ciudadSelecItem = new ArrayList<SelectItem>();
		else
			ciudadSelecItem.clear();

		ciudadSelecItem.add(new SelectItem(null, SeamResourceBundle.getBundle()
				.getString("opt_select_one")));
		for (Ciudad dep : ciudadList) {
			ciudadSelecItem.add(new SelectItem(dep.getIdCiudad(), dep
					.getDescripcion()));
		}
	}

	/**
	 * Combo Departamento Direccion
	 */
	public void updateDepartamentoDir() {
		List<Departamento> dptoList = getDepartamentosDirByPais();
		departamentosDirSelecItem = new ArrayList<SelectItem>();
		buildDepartamentoDirSelectItem(dptoList);
		idDptoDir = null;
		idCiudadDir = null;
		idLocalidadDir = null;
	}

	private List<Departamento> getDepartamentosDirByPais() {
		if (idPaisDir != null) {
			departamentoList.getPais().setIdPais(idPaisDir);
			departamentoList.setOrder("descripcion");
			departamentoList.setMaxResults(null);
			return departamentoList.getResultList();
		}
		return new ArrayList<Departamento>();
	}

	private void buildDepartamentoDirSelectItem(List<Departamento> dptoList) {
		if (departamentosDirSelecItem == null)
			departamentosDirSelecItem = new ArrayList<SelectItem>();
		else
			departamentosDirSelecItem.clear();

		departamentosDirSelecItem.add(new SelectItem(null, SeamResourceBundle
				.getBundle().getString("opt_select_one")));
		for (Departamento dep : dptoList) {
			departamentosDirSelecItem.add(new SelectItem(dep
					.getIdDepartamento(), dep.getDescripcion()));
		}
	}

	/**
	 * Combo ciudad Direccion
	 */
	public void updateCiudadDir() {
		List<Ciudad> ciuList = getCiudadByDptoDir();
		ciudadDirSelecItem = new ArrayList<SelectItem>();
		buildCiudadDirSelectItem(ciuList);
		idCiudadDir = null;
		idLocalidadDir = null;
	}

	private List<Ciudad> getCiudadByDptoDir() {
		if (idDptoDir != null) {
			ciudadList.getDepartamento().setIdDepartamento(idDptoDir);
			ciudadList.setMaxResults(null);
			return ciudadList.getResultList();
		}
		return new ArrayList<Ciudad>();
	}

	private void buildCiudadDirSelectItem(List<Ciudad> ciudadList) {
		if (ciudadDirSelecItem == null)
			ciudadDirSelecItem = new ArrayList<SelectItem>();
		else
			ciudadDirSelecItem.clear();

		ciudadDirSelecItem.add(new SelectItem(null, SeamResourceBundle
				.getBundle().getString("opt_select_one")));
		for (Ciudad dep : ciudadList) {
			ciudadDirSelecItem.add(new SelectItem(dep.getIdCiudad(), dep
					.getDescripcion()));
		}
	}

	/**
	 * Combo Barrio
	 */
	public void updateBarrio() {
		List<Barrio> barList = getBarriosByCiudad();
		localidadDirSelecItem = new ArrayList<SelectItem>();
		buildBarrioSelectItem(barList);
		idLocalidadDir = null;
	}

	private List<Barrio> getBarriosByCiudad() {
		if (idCiudadDir != null) {
			barrioList.setIdCiudad(idCiudadDir);
			barrioList.setOrder("descripcion");
			barrioList.setMaxResults(null);
			return barrioList.getResultList();
		}
		return new ArrayList<Barrio>();
	}

	private void buildBarrioSelectItem(List<Barrio> barrioList) {
		if (localidadDirSelecItem == null)
			localidadDirSelecItem = new ArrayList<SelectItem>();
		else
			localidadDirSelecItem.clear();

		localidadDirSelecItem.add(new SelectItem(null, SeamResourceBundle
				.getBundle().getString("opt_select_one")));
		for (Barrio bar : barrioList) {
			localidadDirSelecItem.add(new SelectItem(bar.getIdBarrio(), bar
					.getDescripcion()));
		}
	}

	public String save() {
		if (fechaNac == null || callePrincipal == null || idSexo == null || idTelContacto == null
							 || callePrincipal.isEmpty()) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, "Llene los datos obligatorios");
			return null;
		}
		Date hoy = new Date();
		if (compareDate(fechaNac, hoy) >= 0) {
			statusMessages.clear();
			statusMessages
					.add(Severity.ERROR,
							"La fecha de nacimiento no puede ser mayor o igual a la fecha actual. Verifique.");
			return null;
		}
		if (!general.isFechaValida(fechaNac)) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, "Fecha de Nacimiento inválida");
			return null;
		}
		if (utilesFromController.esParaguay(idPaisNac)) {
			if (idDptoNac == null) {
				statusMessages.clear();
				statusMessages
						.add(Severity.ERROR,
								"Debe seleccionar un Departamento de Nacimiento. Verifique.");
				return null;
			} else if (idCiudadNac == null) {
				statusMessages.clear();
				statusMessages
						.add(Severity.ERROR,
								"Debe seleccionar una Ciudad de Nacimiento. Verifique.");
				return null;
			}
		}
		
		aux = new String(em.find(Referencias.class, idTelContacto).getDescAbrev());
		
		if(aux.equals("MOVIL") && telefonos.trim().isEmpty() ||	aux.equals("PARTICULAR") && telefonoPart.trim().isEmpty() || aux.equals("LABORAL") && telefonoLab.trim().isEmpty()){
			
			statusMessages.clear();
			statusMessages
					.add(Severity.ERROR, "Debe rellenar el campo del telefono seleccionado. Verifique.");
			return null;
		}

		try {
			if (idLocalidadDir != null)
				persona.setBarrio(em.find(Barrio.class, idLocalidadDir));
			if (idCiudadDir != null)
				persona.setCiudadByIdCiudadDirecc(em.find(Ciudad.class,
						idCiudadDir));
			if (idCiudadNac != null)
				persona.setCiudadByIdCiudadNac(em.find(Ciudad.class,
						idCiudadNac));
			if (idEstadoCivil != null)
				persona.setEstadoCivil(em
						.find(Referencias.class, idEstadoCivil).getDescAbrev());

			persona.setSexo(em.find(Referencias.class, idSexo).getDescAbrev());
			

			persona.setApellidos(persona.getApellidos().trim());

			persona.setCallePrincipal(callePrincipal.trim());
			if (dptoNro != null)
				persona.setDepartamentoNro(dptoNro.trim());
			if (direccionLaboral != null)
				persona.setDireccionLaboral(direccionLaboral.trim());
			persona.setDocumentoIdentidad(persona.getDocumentoIdentidad()
					.trim().toUpperCase());
			persona.setNombres(persona.getNombres().trim());
			if (otrasDirecciones != null)
				persona.setOtrasDirecciones(otrasDirecciones.trim());
			if (piso != null)
				persona.setPiso(piso.trim());
			if (callePrimeraLateral != null)
				persona.setPrimeraLateral(callePrimeraLateral.trim());
			if (calleSegundaLateral != null)
				persona.setSegundaLateral(calleSegundaLateral.trim());
			persona.setFechaNacimiento(fechaNac);
			
			
			if (telefonos != null)
				persona.setTelefonos(telefonos);
			if (telefonoLab != null)
				persona.setTelefonoLab(telefonoLab);
			if (telefonoPart != null)
				persona.setTelefonoPart(telefonoPart);
			
			persona.setTelContacto(em.find(Referencias.class, idTelContacto).getDescAbrev());
			persona.setUsuMod(usuarioLogueado.getCodigoUsuario());
			persona.setFechaMod(new Date());

			em.merge(persona);
			em.flush();
			statusMessages.clear();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
			cargadoTab1 = true;
			if (!guardado)
				guardado = true;
			return "persisted";
		} catch (Exception e) {
			statusMessages.add(Severity.ERROR, e.getMessage());
			return null;
		}
	}
	public String procesar(){
		
		if(idTelContacto != null)
			return em.find(Referencias.class, idTelContacto).getDescAbrev();
		else
			return "";
	}
	
	public void update() {

		try {

			em.merge(persona);
			em.flush();

			cargadoTab1 = true;
			if (!guardado)
				guardado = true;

		} catch (Exception e) {
			statusMessages.add(Severity.ERROR, e.getMessage());
		}
	}

	public int compareDate(Date d1, Date d2) {
		if (d1.getYear() != d2.getYear())
			return d1.getYear() - d2.getYear();
		if (d1.getMonth() != d2.getMonth())
			return d1.getMonth() - d2.getMonth();
		return d1.getDate() - d2.getDate();
	}

	public void setearDesde() {
		link = true;

	}

	public Persona getPersona() {
		return persona;
	}

	public void setPersona(Persona persona) {
		this.persona = persona;
	}

	public Long getIdSexo() {
		return idSexo;
	}

	public void setIdSexo(Long idSexo) {
		this.idSexo = idSexo;
	}
	public Long getIdTelContacto(){
		return idTelContacto;
	}
	public void setIdTelContacto(Long idTelContacto){
		
		this.idTelContacto = idTelContacto;
	}
	public Long getIdEstadoCivil() {
		return idEstadoCivil;
	}

	public void setIdEstadoCivil(Long idEstadoCivil) {
		this.idEstadoCivil = idEstadoCivil;
	}

	public Long getIdPaisNac() {
		return idPaisNac;
	}

	public void setIdPaisNac(Long idPaisNac) {
		this.idPaisNac = idPaisNac;
	}

	public Long getIdDptoNac() {
		return idDptoNac;
	}

	public void setIdDptoNac(Long idDptoNac) {
		this.idDptoNac = idDptoNac;
	}

	public Long getIdCiudadNac() {
		return idCiudadNac;
	}

	public void setIdCiudadNac(Long idCiudadNac) {
		this.idCiudadNac = idCiudadNac;
	}

	public List<SelectItem> getDepartamentosSelecItem() {
		return departamentosSelecItem;
	}

	public void setDepartamentosSelecItem(
			List<SelectItem> departamentosSelecItem) {
		this.departamentosSelecItem = departamentosSelecItem;
	}

	public List<SelectItem> getCiudadSelecItem() {
		return ciudadSelecItem;
	}

	public void setCiudadSelecItem(List<SelectItem> ciudadSelecItem) {
		this.ciudadSelecItem = ciudadSelecItem;
	}

	public List<SelectItem> getDepartamentosDirSelecItem() {
		return departamentosDirSelecItem;
	}

	public void setDepartamentosDirSelecItem(
			List<SelectItem> departamentosDirSelecItem) {
		this.departamentosDirSelecItem = departamentosDirSelecItem;
	}

	public List<SelectItem> getCiudadDirSelecItem() {
		return ciudadDirSelecItem;
	}

	public void setCiudadDirSelecItem(List<SelectItem> ciudadDirSelecItem) {
		this.ciudadDirSelecItem = ciudadDirSelecItem;
	}

	public Long getIdPaisDir() {
		return idPaisDir;
	}

	public void setIdPaisDir(Long idPaisDir) {
		this.idPaisDir = idPaisDir;
	}

	public Long getIdDptoDir() {
		return idDptoDir;
	}

	public void setIdDptoDir(Long idDptoDir) {
		this.idDptoDir = idDptoDir;
	}

	public Long getIdCiudadDir() {
		return idCiudadDir;
	}

	public void setIdCiudadDir(Long idCiudadDir) {
		this.idCiudadDir = idCiudadDir;
	}

	public Long getIdLocalidadDir() {
		return idLocalidadDir;
	}

	public void setIdLocalidadDir(Long idLocalidadDir) {
		this.idLocalidadDir = idLocalidadDir;
	}

	public List<SelectItem> getLocalidadDirSelecItem() {
		return localidadDirSelecItem;
	}

	public void setLocalidadDirSelecItem(List<SelectItem> localidadDirSelecItem) {
		this.localidadDirSelecItem = localidadDirSelecItem;
	}

	public Boolean getCargadoTab1() {
		return cargadoTab1;
	}

	public void setCargadoTab1(Boolean cargadoTab1) {
		this.cargadoTab1 = cargadoTab1;
	}

	public Boolean getGuardado() {
		return guardado;
	}

	public void setGuardado(Boolean guardado) {
		this.guardado = guardado;
	}

	public Boolean getLink() {
		return link;
	}

	public void setLink(Boolean link) {
		this.link = link;
	}

	public Date getFechaNac() {
		return fechaNac;
	}

	public void setFechaNac(Date fechaNac) {
		this.fechaNac = fechaNac;
	}

	public String getCallePrincipal() {
		return callePrincipal;
	}

	public void setCallePrincipal(String callePrincipal) {
		this.callePrincipal = callePrincipal;
	}

	public String getCallePrimeraLateral() {
		return callePrimeraLateral;
	}

	public void setCallePrimeraLateral(String callePrimeraLateral) {
		this.callePrimeraLateral = callePrimeraLateral;
	}

	public String getCalleSegundaLateral() {
		return calleSegundaLateral;
	}

	public void setCalleSegundaLateral(String calleSegundaLateral) {
		this.calleSegundaLateral = calleSegundaLateral;
	}

	public String getDptoNro() {
		return dptoNro;
	}

	public void setDptoNro(String dptoNro) {
		this.dptoNro = dptoNro;
	}

	public String getPiso() {
		return piso;
	}

	public void setPiso(String piso) {
		this.piso = piso;
	}

	public String getDireccionLaboral() {
		return direccionLaboral;
	}

	public void setDireccionLaboral(String direccionLaboral) {
		this.direccionLaboral = direccionLaboral;
	}

	public String getOtrasDirecciones() {
		return otrasDirecciones;
	}

	public void setOtrasDirecciones(String otrasDirecciones) {
		this.otrasDirecciones = otrasDirecciones;
	}

	public String getTelefonos() {
		return telefonos;
	}

	public void setTelefonos(String telefonos) {
		this.telefonos = telefonos;
	}
	public String getTelContacto(){
		return this.telContacto;
	}
	public void setTelContacto(String telContacto){
		this.telContacto=telContacto;
	}
	
	public String getTelefonoPart() {
		return telefonoPart;
	}

	public void setTelefonoPart(String telefonoPart) {
		this.telefonoPart = telefonoPart;
	}

	public String getTelefonoLab() {
		return telefonoLab;
	}

	public void setTelefonoLab(String telefonoLab) {
		this.telefonoLab = telefonoLab;
	}

}
