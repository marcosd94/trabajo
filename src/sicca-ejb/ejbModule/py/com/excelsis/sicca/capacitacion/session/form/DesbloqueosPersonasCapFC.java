package py.com.excelsis.sicca.capacitacion.session.form;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.StatusMessages;

import enums.Bloqueado;
import enums.TiposDatos;

import py.com.excelsis.sicca.capacitacion.session.BandejaCapacitacionList;
import py.com.excelsis.sicca.capacitacion.session.DesbloqueoList;
import py.com.excelsis.sicca.entity.BandejaCapacitacion;
import py.com.excelsis.sicca.entity.Desbloqueo;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("desbloqueosPersonasCapFC")
public class DesbloqueosPersonasCapFC implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6472988611257770269L;
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(create = true)
	DesbloqueoList desbloqueoList;
	@In(create = true)
	SeguridadUtilFormController seguridadUtilFormController;


	private Long idPais;
	private String ci;
	private String nombres;
	private String apellidos;
	private Bloqueado bloqueado;
	List<Desbloqueo> listaDesbloqueos = new ArrayList<Desbloqueo>();

	public void init() throws Exception {
		bloqueado = Bloqueado.TODOS;
		search();
	}

	public void search() throws Exception {

		String query = getQuery();
		if (!listaDesbloqueos.isEmpty())
			listaDesbloqueos.clear();
		listaDesbloqueos = desbloqueoList.listaResultados(query);

	}

	
	public void searchAll() throws Exception {
		ci = null;
		nombres = null;
		apellidos = null;
		idPais = null;
		bloqueado = Bloqueado.TODOS;
	
		String query = getQuery();
		desbloqueoList.listaResultados(query);
	}
	public String getQuery() throws Exception {
		SeguridadUtilFormController sufc =
			(SeguridadUtilFormController) Component.getInstance(SeguridadUtilFormController.class, true);
		String query = "select distinct desbloqueo "
				+ " from Desbloqueo desbloqueo ";
		String where = "";
		if (idPais != null) {
			if (!sufc.validarInput(idPais.toString(), TiposDatos.LONG.getValor(), null)) {
				return null;
			}
			if (where.startsWith("where"))
				where += " and desbloqueo.idPais = " + idPais;
			else
				where += " where desbloqueo.idPais = " + idPais;
		}

		if (ci != null && !ci.trim().isEmpty()) {
			if (where.startsWith("where"))
				where += " and desbloqueo.ci = '" + seguridadUtilFormController.caracterInvalido(ci.toLowerCase()) + "'";
			else
				where += " where desbloqueo.ci = '" + seguridadUtilFormController.caracterInvalido(ci.toLowerCase()) + "'";
		}

		if (nombres != null && !nombres.trim().isEmpty()) {
			if (where.startsWith("where"))
				where += " and lower(desbloqueo.nombres) like lower(concat('%', concat('"
					+ seguridadUtilFormController.caracterInvalido(nombres.toLowerCase())
					+ "','%')))";
			else
				where += " where lower(desbloqueo.nombres) like lower(concat('%', concat('"
					+ seguridadUtilFormController.caracterInvalido(nombres.toLowerCase())
					+ "','%')))";
		}
		if (apellidos != null && !apellidos.trim().isEmpty()) {
			if (where.startsWith("where"))
				where += " and lower(desbloqueo.apellidos) like lower(concat('%', concat('"
					+ seguridadUtilFormController.caracterInvalido(apellidos.toLowerCase())
					+ "','%')))";
			else
				where += " where lower(desbloqueo.apellidos) like lower(concat('%', concat('"
					+ seguridadUtilFormController.caracterInvalido(apellidos.toLowerCase())
					+ "','%')))";
		}
		
		if (bloqueado != null && bloqueado.getId() != null && bloqueado.getId() == 1) {
			if (where.startsWith("where"))
				where += " and desbloqueo.motivoHabilitacion is null ";
			else
				where += " where desbloqueo.motivoHabilitacion is null";
		}
		if (bloqueado != null && bloqueado.getId() != null && bloqueado.getId() == 2) {
			if (where.startsWith("where"))
				where += " and desbloqueo.motivoHabilitacion is not null ";
			else
				where += " where desbloqueo.motivoHabilitacion is not null";
		}
		query += where;
		return query;
	}

	public Long getIdPais() {
		return idPais;
	}

	public void setIdPais(Long idPais) {
		this.idPais = idPais;
	}

	public String getCi() {
		return ci;
	}

	public void setCi(String ci) {
		this.ci = ci;
	}

	public String getNombres() {
		return nombres;
	}

	public void setNombres(String nombres) {
		this.nombres = nombres;
	}

	public String getApellidos() {
		return apellidos;
	}

	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}

	public Bloqueado getBloqueado() {
		return bloqueado;
	}

	public void setBloqueado(Bloqueado bloqueado) {
		this.bloqueado = bloqueado;
	}

	public List<Desbloqueo> getListaDesbloqueos() {
		return listaDesbloqueos;
	}

	public void setListaDesbloqueos(List<Desbloqueo> listaDesbloqueos) {
		this.listaDesbloqueos = listaDesbloqueos;
	}

}
