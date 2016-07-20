package py.com.excelsis.sicca.capacitacion.session.form;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.StatusMessages;

import enums.TiposDatos;

import py.com.excelsis.sicca.entity.Capacitaciones;
import py.com.excelsis.sicca.entity.EstudiosRealizados;
import py.com.excelsis.sicca.entity.PublicacionCapacitacion;
import py.com.excelsis.sicca.entity.PublicacionPortal;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.form.AdmDocAdjuntoFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("datosPublicacionCapacitacionFC")
public class DatosPublicacionCapacitacionFC {
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;

	private Capacitaciones capacitacion = new Capacitaciones();
	private Long idCapacitacion;
	private Long idDoc;
	String imprimir;

	List<String> listaTexto;

	public void init() throws Exception {
		SeguridadUtilFormController sufc = (SeguridadUtilFormController) Component
				.getInstance(SeguridadUtilFormController.class, true);
		if (idCapacitacion != null) {
			if (!sufc.validarInput(idCapacitacion.toString(),
					TiposDatos.LONG.getValor(), null))
				return;
		}
		if (capacitacion == null || capacitacion.getIdCapacitacion() == null)
			capacitacion = em.find(Capacitaciones.class, idCapacitacion);
		
		Query q = em
				.createQuery("Select PublicacionCapacitacion "
						+ "from PublicacionCapacitacion PublicacionCapacitacion "
						+ "where PublicacionCapacitacion.capacitaciones.idCapacitacion = "
						+ idCapacitacion
						+ " and PublicacionCapacitacion.activo is true "
						+ "order by PublicacionCapacitacion.orden ASC");

		List<PublicacionCapacitacion> lista = q.getResultList();
		listaTexto = new ArrayList<String>();
		for (PublicacionCapacitacion o : lista) {
			listaTexto.add(o.getTexto());
		}
		/*if (imprimir != null) {
			abrirDoc();
		}*/
	}

	public void abrirDoc() {

		Long id = capacitacion.getDocumentos()
		.getIdDocumento();
		Long usu = null;
		AdmDocAdjuntoFormController.abrirDocumentoFromCU(id, usu);
		imprimir = null;
	}
	public Capacitaciones getCapacitacion() {
		return capacitacion;
	}

	public void setCapacitacion(Capacitaciones capacitacion) {
		this.capacitacion = capacitacion;
	}

	public Long getIdCapacitacion() {
		return idCapacitacion;
	}

	public void setIdCapacitacion(Long idCapacitacion) {
		this.idCapacitacion = idCapacitacion;
	}

	public List<String> getListaTexto() {
		return listaTexto;
	}

	public void setListaTexto(List<String> listaTexto) {
		this.listaTexto = listaTexto;
	}

	public String getImprimir() {
		return imprimir;
	}

	public void setImprimir(String imprimir) {
		this.imprimir = imprimir;
	}

	public Long getIdDoc() {
		return idDoc;
	}

	public void setIdDoc(Long idDoc) {
		this.idDoc = idDoc;
	}
	
	

}
