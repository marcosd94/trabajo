package py.com.excelsis.sicca.session.form;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.model.SelectItem;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.StatusMessages;

import org.jboss.seam.international.StatusMessage.Severity;

import enums.Estado;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.Documentos;
import py.com.excelsis.sicca.entity.EstudiosRealizados;
import py.com.excelsis.sicca.entity.ExperienciaLaboral;
import py.com.excelsis.sicca.entity.InstitucionEducativa;
import py.com.excelsis.sicca.entity.Pais;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.entity.TipoArchivo;
import py.com.excelsis.sicca.entity.TituloAcademico;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.entity.Parentesco;
import py.com.excelsis.sicca.session.ParentescoHome;
import py.com.excelsis.sicca.session.ParentescoList;
import py.com.excelsis.sicca.util.SICCAAppHelper;
import sun.util.logging.resources.logging;

@Scope(ScopeType.CONVERSATION)
@Name("tab7ParentescoFormController")
public class Tab7ParentescoFormController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(value = "entityManager")
	EntityManager em;
	@In
	StatusMessages statusMessages;
	@In(create = true)
	ParentescoList parentescoList;
	@In(create = true)
	ParentescoHome parentescoHome;
	private Parentesco parentesco;
	private Long idDatosEspecificos; // por default
	private DatosEspecificos datosEspecificos;
	private Long idPersona;
	private Persona persona;
	private String nombres;
	private String apellidos;
	private String documentoIdentidad;
	private String institucion;
	private boolean esEdit = false;
	private int indexUp;
	private Boolean tienePariente;

	private List<Parentesco> listDetails = new ArrayList<Parentesco>();
	private List<Parentesco> listDetails2 = new ArrayList<Parentesco>();

	@SuppressWarnings("unchecked")
	public void init() {
		try {

			setIdPersona(usuarioLogueado.getPersona().getIdPersona());
			datosEspecificos = new DatosEspecificos();
			parentescoList.getPersona().setIdPersona(
					usuarioLogueado.getPersona().getIdPersona());
			persona = em.find(Persona.class, usuarioLogueado.getPersona()
					.getIdPersona());
			parentescoList.getParentesco().setActivo(Estado.ACTIVO.getValor());
			parentescoList.setMaxResults(null);

			listDetails = new ArrayList<Parentesco>();
			listDetails2 = new ArrayList<Parentesco>();
			tienePariente = persona.getTienePariente();
			
			//Predeterminado es SI
			if(tienePariente==null) tienePariente = true;
				
			
			Query q = em
					.createQuery(" select e from Parentesco e "
							+ " where e.persona.idPersona= "
							+ persona.getIdPersona()
							+ " and e.activo = 'true' and e.datosEspecificos.valorAlf like 'C'");

			listDetails = (ArrayList<Parentesco>) q.getResultList();

			listDetails2 = em
					.createQuery(
							" select e from Parentesco e "
									+ " where e.persona.idPersona= "
									+ persona.getIdPersona()
									+ "and e.activo = 'true'  and e.datosEspecificos.valorAlf like 'A'")
					.getResultList();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public String save() {

		String result = null;

		try {

			parentesco = new Parentesco();
			parentesco.setDatosEspecificos(em.find(DatosEspecificos.class,
					idDatosEspecificos));
			parentesco.setNombres(getNombres());
			parentesco.setApellidos(getApellidos());
			parentesco.setDocumentoIdentidad(getDocumentoIdentidad());
			parentesco.setInstitucion(getInstitucion());

			Persona persona = em.find(Persona.class, usuarioLogueado
					.getPersona().getIdPersona());
			parentesco.setPersona(persona);
			parentesco.setActivo(Estado.ACTIVO.getValor());

			parentescoHome.setInstance(parentesco);
			parentescoHome.update();
			result = parentescoHome.save();
			parentescoHome.clearInstance();

			return result;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (result != null) {
				statusMessages.clear();
				statusMessages.add(Severity.INFO, SICCAAppHelper
						.getBundleMessage("msg_operacion_exitosa"));
			}
		}
		return result;
	}

	public void addRow() {

		if (!toDetailOk()) {
			return;
		}

		save();
		// em.flush();
		if( !tienePariente ) {
			tienePariente = true;
			actualizarTieneParientes();
		}
		displaySuccesfullMessage();
		clearDataDetail();

		parentescoHome.clearInstance();

	}

	public void removeRow1(int pos) {

		statusMessages.clear();
		listDetails.get(pos).setActivo(Estado.INACTIVO.getValor());
		parentescoHome.setInstance(listDetails.get(pos));
		parentescoHome.save();
		displaySuccesfullMessage();
		listDetails.remove(pos);
		if( listDetails.isEmpty() && listDetails2.isEmpty() ) {
			tienePariente = false;
			actualizarTieneParientes();
		}

	}

	public void removeRow2(int pos) {

		statusMessages.clear();
		listDetails2.get(pos).setActivo(Estado.INACTIVO.getValor());
		parentescoHome.setInstance(listDetails2.get(pos));
		parentescoHome.save();
		displaySuccesfullMessage();
		listDetails2.remove(pos);
		if( listDetails.isEmpty() && listDetails2.isEmpty() ) {
			tienePariente = false;
			actualizarTieneParientes();
		}
	}
	
	private boolean toDetailOk() {

		if (getIdDatosEspecificos() == null) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR,
					"Debe completar todos los campos");

			return false;
		}
		
		if (getNombres() == null || getNombres().isEmpty()) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR,
					"Debe completar todos los campos");

			return false;
		}

		if (getApellidos() == null || getApellidos().isEmpty()) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR,
					"Debe completar todos los campos");
			
			return false;
		}

		/*
		 * if(getDocumentoIdentidad() == null ||
		 * getDocumentoIdentidad().isEmpty()){ statusMessages.clear();
		 * statusMessages.add(Severity.ERROR,
		 * "Debe completar todos los campos");
		 * 
		 * return false; }
		 */

		if (getInstitucion() == null || getInstitucion().isEmpty()) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR,
					"Debe completar todos los campos");

			return false;
		}

		return true;
	}

	public void clearDataDetail() {
		parentesco = new Parentesco();
		idDatosEspecificos = (long) 1255;
		datosEspecificos = null;
		nombres = null;
		apellidos = null;
		documentoIdentidad = null;
		institucion = null;
		esEdit = false;

	}

	public void editar1(int index) {
		esEdit = true;
		parentesco = em.find(Parentesco.class, listDetails.get(index)
				.getIdParentesco());
		datosEspecificos = parentesco.getDatosEspecificos();
		idDatosEspecificos = parentesco.getDatosEspecificos()
				.getIdDatosEspecificos();
		if (!parentesco.getNombres().equals(""))
			nombres = parentesco.getNombres();
		if (!parentesco.getApellidos().equals(""))
			apellidos = parentesco.getApellidos();
		if (!parentesco.getDocumentoIdentidad().equals(""))
			documentoIdentidad = parentesco.getDocumentoIdentidad();
		if (!parentesco.getInstitucion().equals(""))
			institucion = parentesco.getInstitucion();
		indexUp = index;
	}

	public void editar2(int index) {
		esEdit = true;
		parentesco = em.find(Parentesco.class, listDetails2.get(index)
				.getIdParentesco());
		datosEspecificos = parentesco.getDatosEspecificos();
		idDatosEspecificos = parentesco.getDatosEspecificos()
				.getIdDatosEspecificos();
		if (!parentesco.getNombres().equals(""))
			nombres = parentesco.getNombres();
		if (!parentesco.getApellidos().equals(""))
			apellidos = parentesco.getApellidos();
		if (!parentesco.getDocumentoIdentidad().equals(""))
			documentoIdentidad = parentesco.getDocumentoIdentidad();
		if (!parentesco.getInstitucion().equals(""))
			institucion = parentesco.getInstitucion();
		indexUp = index;
	}
	
	public void update() {

		if (!toDetailOk()) {
			return;
		}
		parentesco.setActivo(false);
		save();
		clearDataDetail();

		/*
		 * em.createQuery(" update Parentesco e set e.documentoIdentidad = '"
		 * +getDocumentoIdentidad
		 * ()+"' , e.nombres = '"+getNombres()+"' , e.apellidos= '"
		 * +getApellidos()+"', e.institucion = '"
		 * +getInstitucion()+"', e.datosEspecificos = '"
		 * +parentesco.getDatosEspecificos
		 * ()+"' where e.id_parentesco = "+parentesco.getIdParentesco()+";");
		 */

	}

	public List<SelectItem> tieneParientes() {
		List<SelectItem> listaItem = new ArrayList<SelectItem>();
		listaItem.add(new SelectItem(true, "Si"));
		listaItem.add(new SelectItem(false, "No"));
		return listaItem;
	}

	public void actualizarTieneParientes() {
		persona.setTienePariente(tienePariente);
		em.persist(persona);
		em.flush();
	}

	// GETTERS Y SETTERS
	public void setParentesco(Parentesco parentesco) {
		this.parentesco = parentesco;
	}

	public Parentesco getParentesco() {
		return parentesco;
	}

	public Persona getPersona() {
		return persona;
	}

	public void setPersona(Persona persona) {
		this.persona = persona;
	}

	private void displaySuccesfullMessage() {
		statusMessages.clear();
		statusMessages.add(Severity.INFO,
				SICCAAppHelper.getBundleMessage("msg_operacion_exitosa"));
	}

	// GETTERS Y SETTERS
	public List<Parentesco> getListDetails() {
		return listDetails;
	}

	public void setListDetails(List<Parentesco> listDetails) {
		this.listDetails = listDetails;
	}

	public String getNombres() {
		return nombres;
	}

	public void setNombres(String nombres) {
		this.nombres = nombres;
	}

	public Long getIdPersona() {
		return idPersona;
	}

	public void setIdPersona(Long idPersona) {
		this.idPersona = idPersona;
	}

	public String getApellidos() {
		return apellidos;
	}

	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}

	public String getDocumentoIdentidad() {
		return documentoIdentidad;
	}

	public void setDocumentoIdentidad(String documentoIdentidad) {
		this.documentoIdentidad = documentoIdentidad;
	}

	public String getInstitucion() {
		return institucion;
	}

	public void setInstitucion(String institucion) {
		this.institucion = institucion;
	}

	public Long getIdDatosEspecificos() {
		return idDatosEspecificos;
	}

	public void setIdDatosEspecificos(Long idDatosEspecificos) {
		this.idDatosEspecificos = idDatosEspecificos;
	}

	public List<Parentesco> getListDetails2() {
		return listDetails2;
	}

	public void setListDetails2(List<Parentesco> listDetails2) {
		this.listDetails2 = listDetails2;
	}

	public boolean isEsEdit() {
		return esEdit;
	}

	public void setEsEdit(boolean esEdit) {
		this.esEdit = esEdit;
	}

	public int getIndexUp() {
		return indexUp;
	}

	public void setIndexUp(int indexUp) {
		this.indexUp = indexUp;
	}

	public Boolean getTienePariente() {
		return tienePariente;
	}

	public void setTienePariente(Boolean tienePariente) {
		this.tienePariente = tienePariente;
	}

}