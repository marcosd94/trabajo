package py.com.excelsis.sicca.session.form;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import py.com.excelsis.sicca.entity.Cpt;
import py.com.excelsis.sicca.entity.DetTipoNombramiento;
import py.com.excelsis.sicca.entity.TipoNombramiento;
import py.com.excelsis.sicca.entity.TipoPlanta;

@Scope(ScopeType.CONVERSATION)
@Name("verDetallesCptFormController")
public class VerDetallesCptFormController {
	
	
	
	@In(value = "entityManager")
	EntityManager em;
	
	private Cpt cptPopUp = new Cpt();
	private Long idCptPopUp;
	private List<TipoPlanta> listaTipoPlanta = new ArrayList<TipoPlanta>();
	private List<DetTipoNombramiento> ListaTipoNombramiento = new ArrayList<DetTipoNombramiento>();

	
	public void init(){
		buscarNombramientos();
		
	}

	
	private void buscarNombramientos() {
		buscarTipoPlanta();
		System.out.println("LISTA DE PLANTA: ");
		for(int i = 0; i<listaTipoPlanta.size();i++)
			System.out.println("ELEMENTO : "+listaTipoPlanta.get(i).getDescripcion());
		String cad =
			"select * from planificacion.det_tipo_nombramiento det_tipo"
				+ " where det_tipo.id_cpt = " + idCptPopUp;
		ListaTipoNombramiento = new ArrayList<DetTipoNombramiento>();
		ListaTipoNombramiento = em.createNativeQuery(cad, DetTipoNombramiento.class).getResultList();
		
		System.out.println("LISTA DE PLANTA: ");
		for(int i = 0; i<ListaTipoNombramiento.size();i++)
			System.out.println("ELEMENTO : "+ListaTipoNombramiento.get(i).getTipo());
		
		for (int i = 0; i < listaTipoPlanta.size(); i++) {
			TipoPlanta planta = new TipoPlanta();
			planta = listaTipoPlanta.get(i);
			List<TipoNombramiento> listaNombramientos = planta.getTipoNombramientos();
			for (DetTipoNombramiento detalle : ListaTipoNombramiento) {
				for (TipoNombramiento nombramiento : listaNombramientos) {
					if (nombramiento.getIdTipoNombramiento().equals(detalle.getTipoNombramiento().getIdTipoNombramiento())) {
						nombramiento.setSeleccionado(true);
						System.out.println("Seleccionado: SI\nNombramiento: "+detalle.getTipo());
					}
				}
			}
			planta.setTipoNombramientos(listaNombramientos);
			listaTipoPlanta.set(i, planta);
		}
	}
	
	public List<TipoNombramiento> traerTipoNombramiento(Long id) {
		Query q =
			em.createQuery("select TipoNombramiento from TipoNombramiento TipoNombramiento "
				+ " where TipoNombramiento.tipoPlanta.idTipoPlanta = :idTipoPlanta "
				+ " and TipoNombramiento.activo is true " + " order by descripcion ");
		q.setParameter("idTipoPlanta", id);
		return q.getResultList();
	}
	
	private void buscarTipoPlanta() {
		String cadena =
			"select * from planificacion.tipo_planta tipo " + "where tipo.activo IS TRUE";
		listaTipoPlanta = new ArrayList<TipoPlanta>();
		listaTipoPlanta = em.createNativeQuery(cadena, TipoPlanta.class).getResultList();
	}
	
	//GETTERS Y SETTERS
	public Cpt getCptPopUp() {
		return cptPopUp;
	}


	public void setCptPopUp(Cpt cptPopUp) {
		this.cptPopUp = cptPopUp;
	}


	public List<TipoPlanta> getListaTipoPlanta() {
		return listaTipoPlanta;
	}


	public void setListaTipoPlanta(List<TipoPlanta> listaTipoPlanta) {
		this.listaTipoPlanta = listaTipoPlanta;
	}


	public List<DetTipoNombramiento> getListaTipoNombramiento() {
		return ListaTipoNombramiento;
	}


	public void setListaTipoNombramiento(List<DetTipoNombramiento> listaTipoNombramiento) {
		ListaTipoNombramiento = listaTipoNombramiento;
	}


	public Long getIdCptPopUp() {
		return idCptPopUp;
	}


	public void setIdCptPopUp(Long idCptPopUp) {
		this.idCptPopUp = idCptPopUp;
	}
}
