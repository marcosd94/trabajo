package py.com.excelsis.sicca.capacitacion.session.form;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;
import enums.ModalidadCapaSeleccione;
import py.com.excelsis.sicca.capacitacion.session.CapacitacionEnProcesoList;
import py.com.excelsis.sicca.capacitacion.session.CapacitacionFinalizadaList;
import py.com.excelsis.sicca.entity.CapacitacionEnProceso;
import py.com.excelsis.sicca.entity.CapacitacionFinalizada;
import py.com.excelsis.sicca.entity.Capacitaciones;
import py.com.excelsis.sicca.entity.Ciudad;
import py.com.excelsis.sicca.entity.Departamento;
import py.com.excelsis.sicca.entity.Documentos;
import py.com.excelsis.sicca.entity.PostulacionCap;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.CiudadList;
import py.com.excelsis.sicca.session.DepartamentoList;
import py.com.excelsis.sicca.session.PersonaList;
import py.com.excelsis.sicca.session.form.AdmDocAdjuntoFormController;
import py.com.excelsis.sicca.session.form.UsuarioPortalFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("buscarCapacitacionEnProcesoFC")
public class BuscarCapacitacionEnProcesoFC  implements Serializable {

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

	
	@In(create = true)
	CapacitacionEnProcesoList capacitacionEnProcesoList;

	@In(required = false)
	Usuario usuarioLogueado;
	
	@In(required = false, create = true)
	UsuarioPortalFormController usuarioPortalFormController;
	@In(create = true)
	CiudadList ciudadList;
	@In(create = true)
	DepartamentoList departamentoList;
	@In(create = true)
	PersonaList personaList;
	
	private List<CapacitacionEnProceso> capacEnProcesoListas= new ArrayList<CapacitacionEnProceso>();
	private Long idCiudad=null;
	private Long codPais = null;
	private Long codDepartamento = null;
	private List<SelectItem> departamentosSelecItem ;
	private List<SelectItem> ciudadSelecItem;
	private Long idTipoCapacitacion=null;
	private ModalidadCapaSeleccione modalidadCapaSeleccione=ModalidadCapaSeleccione.NINGUNO;
	private String sugestion = null;
	private Map<String, Long> mapaSuggsIds;
	
	

	/**
	 * 
	 */


	public void init() {
		
		search();
		updateDepartamento();
		updateCiudad();

	}

	
	public void search() {
		capacitacionEnProcesoList.setIdCiudad(idCiudad);
		capacitacionEnProcesoList.setIdDepartamento(codDepartamento);
		capacitacionEnProcesoList.setIdPais(codPais);
		capacitacionEnProcesoList.setIdTipoCapa(idTipoCapacitacion);
		capacitacionEnProcesoList.getCapacitacionEnProceso().setModalidad(modalidadCapaSeleccione.getValor());
		if(sugestion!=null && !sugestion.trim().equals(""))
			capacitacionEnProcesoList.getCapacitacionEnProceso().setNombre(sugestion);
		else
			capacitacionEnProcesoList.getCapacitacionEnProceso().setNombre(null);
		capacEnProcesoListas=capacitacionEnProcesoList.listaResultados();
	
		
	}

	public void searchAll() {
		idCiudad=null;
		idTipoCapacitacion=null;
		codPais=null;
		codDepartamento=null;
		modalidadCapaSeleccione=ModalidadCapaSeleccione.NINGUNO;
		sugestion=null;
		capacEnProcesoListas=capacitacionEnProcesoList.limpiar();
	}
	public void updateDepartamento() {
		List<Departamento> dptoList = getDepartamentosByPais();
		departamentosSelecItem = new ArrayList<SelectItem>();
		buildDepartamentoDirSelectItem(dptoList);
	}
	public void updateDepartamentoChange() {
		List<Departamento> dptoList = getDepartamentosByPais();
		departamentosSelecItem = new ArrayList<SelectItem>();
		buildDepartamentoDirSelectItem(dptoList);
		codDepartamento = null;
		idCiudad = null;
		updateCiudad();

	}
	public void abrirDocumento(Long idCapacitacion) {

		try {
			Capacitaciones c= em.find(Capacitaciones.class,idCapacitacion);
			if(c.getDocumentos()!=null){
				AdmDocAdjuntoFormController.abrirDocumentoFromCU( c.getDocumentos().getIdDocumento(),"SYSTEM");
			}
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		
	}
	@SuppressWarnings("unchecked")
	public boolean habPostularse(Long idCapacitacion){
		List<PostulacionCap> aux= em.createQuery("Select p from PostulacionCap p" +
				" where p.capacitacion.idCapacitacion="+idCapacitacion+"" +
						"  and p.persona.idPersona="+usuarioLogueado.getPersona().getIdPersona() +"" +
						" and p.activo=true and p.ficha=false").getResultList();
		if(aux.isEmpty())
			return true;
		
		return false;
	}
	
	@SuppressWarnings("unchecked")
	public String postularse(){
		try {
			List<PostulacionCap> caps=em.createQuery("Select p from PostulacionCap p " +
					" where p.idPostulacion is null and p.persona.idPersona="+usuarioLogueado.getPersona().getIdPersona()+" " +
					" and p.ficha=true and p.activo=true").getResultList();
			
			if(caps.isEmpty()){
				statusMessages.add(Severity.ERROR," Debe completar la Ficha de Inscripción en el apartado “Mi Cuenta” para postularse a la Capacitación/Beca");
				return null;
			}
			return null;//aki deber ir CU 482 Registrar/Imprimir Postulación Y LE PASA EL PARAMETRO ID_CAPACITACION
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	public void updateCiudad() {
		List<Ciudad> ciuList = getCiudadByDpto();
		ciudadSelecItem = new ArrayList<SelectItem>();
		buildCiudadSelectItem(ciuList);
		
	}
	private List<Departamento> getDepartamentosByPais(){
		if(codPais != null){
			departamentoList.getPais().setIdPais(codPais);
			departamentoList.setOrder("descripcion");
			
			return departamentoList.litarPorPais();
		}
		return new ArrayList<Departamento>();
	}
	private List<Ciudad> getCiudadByDpto(){
		if(codDepartamento!= null){
			ciudadList.getDepartamento().setIdDepartamento(codDepartamento);
			return ciudadList.listarPorDpto();
		}
		return new ArrayList<Ciudad>();
	}
	private void buildDepartamentoDirSelectItem(List<Departamento> dptoList) {
		if (departamentosSelecItem == null)
			departamentosSelecItem = new ArrayList<SelectItem>();
		else
			departamentosSelecItem.clear();

		departamentosSelecItem.add(new SelectItem(null, SeamResourceBundle
				.getBundle().getString("opt_select_one")));
		for (Departamento dep : dptoList) {
			departamentosSelecItem.add(new SelectItem(dep
					.getIdDepartamento(), dep.getDescripcion()));
		}
		updateCiudad();
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

	@SuppressWarnings("unchecked")
	public List<String> autocompletar(String suggest) {
		
		ArrayList<String> result = new ArrayList<String>();
		List<CapacitacionEnProceso> lista= new ArrayList<CapacitacionEnProceso>();
		if(suggest!=null) 
			lista=em.createQuery("Select c from CapacitacionEnProceso c where lower(c.nombre) like :desc ").setParameter("desc","%"+suggest+"%").getResultList();
		
		if (mapaSuggsIds != null) {
			mapaSuggsIds.clear();
		} else {
			mapaSuggsIds = new HashMap<String, Long>();
		}
		for (CapacitacionEnProceso o : lista) {
			if (!mapaSuggsIds.keySet().contains(o.getNombre())) {
				mapaSuggsIds.put(o.getNombre(), o.getIdCapacitacion());
				result.add(o.getNombre());
			}
		}
		return result;
	}
	

	
	// GETTERS Y SETTERS
	

	public Long getIdCiudad() {
		return idCiudad;
	}


	public void setIdCiudad(Long idCiudad) {
		this.idCiudad = idCiudad;
	}


	public Long getCodPais() {
		return codPais;
	}


	public void setCodPais(Long codPais) {
		this.codPais = codPais;
	}


	public Long getCodDepartamento() {
		return codDepartamento;
	}


	public void setCodDepartamento(Long codDepartamento) {
		this.codDepartamento = codDepartamento;
	}


	public List<SelectItem> getDepartamentosSelecItem() {
		return departamentosSelecItem;
	}


	public void setDepartamentosSelecItem(List<SelectItem> departamentosSelecItem) {
		this.departamentosSelecItem = departamentosSelecItem;
	}


	public List<SelectItem> getCiudadSelecItem() {
		return ciudadSelecItem;
	}


	public void setCiudadSelecItem(List<SelectItem> ciudadSelecItem) {
		this.ciudadSelecItem = ciudadSelecItem;
	}


	public String getSugestion() {
		return sugestion;
	}


	public void setSugestion(String sugestion) {
		this.sugestion = sugestion;
	}


	public Map<String, Long> getMapaSuggsIds() {
		return mapaSuggsIds;
	}


	public void setMapaSuggsIds(Map<String, Long> mapaSuggsIds) {
		this.mapaSuggsIds = mapaSuggsIds;
	}


	


	public Long getIdTipoCapacitacion() {
		return idTipoCapacitacion;
	}


	public void setIdTipoCapacitacion(Long idTipoCapacitacion) {
		this.idTipoCapacitacion = idTipoCapacitacion;
	}


	public ModalidadCapaSeleccione getModalidadCapaSeleccione() {
		return modalidadCapaSeleccione;
	}


	public void setModalidadCapaSeleccione(
			ModalidadCapaSeleccione modalidadCapaSeleccione) {
		this.modalidadCapaSeleccione = modalidadCapaSeleccione;
	}


	public List<CapacitacionEnProceso> getCapacEnProcesoListas() {
		return capacEnProcesoListas;
	}


	public void setCapacEnProcesoListas(
			List<CapacitacionEnProceso> capacEnProcesoListas) {
		this.capacEnProcesoListas = capacEnProcesoListas;
	}


	

	
	
	

}
