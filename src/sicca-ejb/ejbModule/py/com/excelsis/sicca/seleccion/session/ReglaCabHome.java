package py.com.excelsis.sicca.seleccion.session;

import py.com.excelsis.sicca.entity.*;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.AppHelper;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.taglibs.standard.tag.common.core.SetSupport;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;

@Name("reglaCabHome")
public class ReglaCabHome extends EntityHome<ReglaCab> {

	@In(required = false)
	Usuario usuarioLogueado;

	@In
	StatusMessages statusMessages;

	@In(create = true)
	SeguridadUtilFormController seguridadUtilFormController;

	// Value holders for selectItems if exists
	public static final String CONTEXT_VAR_NAME = "reglaCabs";
	public static final String[] CONTEXT_VAR_NAMES = { CONTEXT_VAR_NAME,
			CONTEXT_VAR_NAME + "SelectItems" };

	public void setReglaCabIdReglaCab(Long id) {
		setId(id);
	}

	public Long getReglaCabIdReglaCab() {
		return (Long) getId();
	}

	@Override
	protected ReglaCab createInstance() {
		ReglaCab reglaCab = new ReglaCab();
		return reglaCab;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
	}

	public boolean isWired() {
		return true;
	}

	public ReglaCab getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	public List<ReglaDet> getReglaDets() {
		return getInstance() == null ? null : new ArrayList<ReglaDet>(
				getInstance().getReglaDets());
	}

	@Override
	public String persist() {
		if (!checkData())
			return null;

		getInstance().setFechaAlta(new Date());
		getInstance().setActivo(true);
		getInstance().setUsuAlta(
				usuarioLogueado.getCodigoUsuario().toUpperCase());
		getInstance().setDescripcion(
				getInstance().getDescripcion().trim().toUpperCase());

		return AppHelper.removeFromContext("persist", super.persist(),
				CONTEXT_VAR_NAMES, getEventContext());
	}

	@Override
	public String update() {
		if (!checkData())
			return null;

		if (getInstance().isActivo()) {
			if (!esFechaSolapada("update")) {
				statusMessages.add(Severity.ERROR,
						"La fecha de vigencia esta solapada");
				return null;
			}
		}
		getInstance().setFechaMod(new Date());
		getInstance().setUsuMod(
				usuarioLogueado.getCodigoUsuario().toUpperCase());
		getInstance().setDescripcion(
				getInstance().getDescripcion().trim().toUpperCase());

		actualizarDetalles(getInstance().isActivo());

		return AppHelper.removeFromContext("updated", super.update(),
				CONTEXT_VAR_NAMES, getEventContext());
	}

	private boolean checkData() {
		String operation = isIdDefined() ? "update" : "persist";
		if (getInstance().getDescripcion().trim().isEmpty()) {
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle()
					.getString("msg_descripcion_invalida"));
			return false;
		}
		if (seguridadUtilFormController.contieneCaracter(getInstance()
				.getDescripcion().trim())) {
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle()
					.getString("msg_caracter"));
			return false;
		}
		if (getInstance().getFechaVigenciaDesde().after(
				getInstance().getFechaVigenciaHasta())) {
			statusMessages
					.add(Severity.ERROR,
							"Fecha de Vigencia Desde no puede ser mayor a Fecha de Vigencia Hasta");
			return false;
		}
		if (!esFechaSolapada(operation)) {
			statusMessages.add(Severity.ERROR,
					"La fecha de vigencia esta solapada");
			return false;
		}

		return true;
	}

	private void actualizarDetalles(boolean activo) {
		String hql = null;
		if (!activo)
			hql = "SELECT o FROM ReglaDet o WHERE o.activo is true and o.reglaCab.idReglaCab = "
					+ getInstance().getIdReglaCab().longValue();
		else
			hql = "SELECT o FROM ReglaDet o WHERE o.activo is false and o.reglaCab.idReglaCab = "
					+ getInstance().getIdReglaCab().longValue();
		List<ReglaDet> list = getEntityManager().createQuery(hql)
				.getResultList();
		for (ReglaDet det : list) {
			det.setActivo(activo);
			det.setUsuMod(usuarioLogueado.getCodigoUsuario());
			det.setFechaMod(new Date());
			getEntityManager().merge(det);
		}
	}

	private Boolean esFechaSolapada(String operation) {
		String hql = "SELECT o FROM ReglaCab o WHERE o.activo is true and lower(o.descripcion) =:desc ";
		if (operation.equalsIgnoreCase("update")) {
			hql += " AND o.idReglaCab != "
					+ getInstance().getIdReglaCab().longValue();
		}

		List<ReglaCab> list = getEntityManager()
				.createQuery(hql)
				.setParameter("desc",
						getInstance().getDescripcion().trim().toLowerCase())
				.getResultList();

		for (ReglaCab cab : list) {
			if (getInstance().getFechaVigenciaDesde().compareTo(
					cab.getFechaVigenciaDesde()) > 0
					&& getInstance().getFechaVigenciaHasta().compareTo(
							cab.getFechaVigenciaDesde()) < 0)
				return false;
		}
		return true;
	}

}
