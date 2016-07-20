package py.com.excelsis.sicca.session.form;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.ConcursoPuestoDet;
import py.com.excelsis.sicca.entity.Feriados;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.FeriadosHome;
import py.com.excelsis.sicca.session.FeriadosList;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.SICCAAppHelper;

@Scope(ScopeType.PAGE)
@Name("feriadosFormController")
public class FeriadosFormController implements Serializable {

	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(create = true)
	FeriadosHome feriadosHome;
	@In(create = true)
	FeriadosList feriadosList;

	Feriados feriados;
	Date fecha;

	/**
	 * Método que inicializa los datos
	 */
	public void init() {
		feriados = new Feriados();
		feriados = feriadosHome.getInstance();

		if (feriadosHome.isIdDefined()) {
			fecha = feriados.getFecha();
		} else {
			feriados.setActivo(true);
		}
	}

	public String save() {
		if(feriados.getDescripcion() == null || feriados.getDescripcion().trim().isEmpty()){
			statusMessages.clear();
			statusMessages.add(Severity.ERROR,
					"Ingrese datos en los campos obligatorios");
			return null;
		}
		if (check("save")) {
			try {
				Integer anho = fecha.getYear() + 1900;
				feriados.setAnho(anho);
				feriados.setFecha(fecha);
				feriadosHome.setInstance(feriados);
				String oper = feriadosHome.persist();
				if (oper.equals("persisted")) {
					statusMessages.clear();
					statusMessages.add(Severity.INFO, SeamResourceBundle
							.getBundle().getString("GENERICO_MSG"));
					return oper;
				}
				statusMessages.clear();
				statusMessages.add(Severity.ERROR,
						"El registro no pudo ser guardado");
				return null;
			} catch (Exception e) {
				return null;
			}
		}
		statusMessages.clear();
		statusMessages.add(Severity.ERROR, "El feriado ingresado ya existe");
		return null;
	}

	public String update() {

		if(feriados.getDescripcion() == null || feriados.getDescripcion().trim().isEmpty()){
			statusMessages.clear();
			statusMessages.add(Severity.ERROR,
					"Ingrese datos en los campos obligatorios");
			return null;
		}
		if (!feriados.isActivo() || (feriados.isActivo() && check("update"))) {
			try {
				feriados.setAnho(fecha.getYear() + 1900);
				feriados.setFecha(fecha);
				feriadosHome.setInstance(feriados);
				String oper = feriadosHome.update();
				if (oper.equals("updated")) {
					statusMessages.clear();
					statusMessages.add(Severity.INFO, SeamResourceBundle
							.getBundle().getString("GENERICO_MSG"));
					return oper;
				}
				statusMessages.clear();
				statusMessages.add(Severity.ERROR,
						"El registro no pudo ser actualizado");

			} catch (Exception e) {
				return null;
			}
		}
		statusMessages.clear();
		statusMessages.add(Severity.ERROR, "El feriado ingresado ya existe");
		return null;
	}

	@SuppressWarnings("unchecked")
	private Boolean check(String operacion) {
		List<Feriados> lista = new ArrayList<Feriados>();
		Integer actual = fecha.getYear() + 1900;
		String sql = "Select fer.* from seleccion.feriados fer "
				+ "where lower(fer.descripcion) =  '"
				+ feriados.getDescripcion().trim().toLowerCase() + "' "
				+ " and fer.anho = " + actual;
		if (operacion.equals("update")) {
			sql = sql + " and fer.id_feriados != " + feriados.getIdFeriados();
			sql = sql + " and fer.activo is true ";
		}
		lista = em.createNativeQuery(sql, Feriados.class).getResultList();
		if (lista.size() > 0)
			return false;
		return true;
	}

	@SuppressWarnings("unchecked")
	public String copiar() {
		if (!existenFeriadosCargados()) {
			Date fechaActual = new Date();
			Calendar calendario = Calendar.getInstance();
			calendario.setTime(fechaActual);
			Integer anho = calendario.get(Calendar.YEAR);
			anho--;
			List<Feriados> listaAux = new ArrayList<Feriados>();

			listaAux = feriadosAnteriores();
			if (listaAux == null) {
				String msg = "No existen feriados cargados";
				statusMessages.add(Severity.ERROR, msg);
			} else {
				try {
					for (int i = 0; i < listaAux.size(); i++) {
						Feriados feriado = new Feriados();
						Feriados nuevo = new Feriados();
						feriado = listaAux.get(i);
						nuevo.setFechaAlta(new Date());
						nuevo.setDescripcion(feriado.getDescripcion());
						nuevo.setUsuAlta(usuarioLogueado.getCodigoUsuario());

						Calendar cal = Calendar.getInstance();
						cal.setTime(feriado.getFecha());
						Integer an = cal.get(Calendar.YEAR);
						Integer mes = cal.get(Calendar.MONTH);
						Integer dia = cal.get(Calendar.DAY_OF_MONTH);
						an++;
						cal.set(an, mes, dia);
						nuevo.setFecha(cal.getTime());

						nuevo.setAnho(an.intValue());
						nuevo.setActivo(true);
						em.persist(nuevo);
						em.flush();

					}
				} catch (Exception e) {
					statusMessages.clear();
					statusMessages.add(Severity.ERROR, SICCAAppHelper
							.getBundleMessage("msg_already_exists"));

					return "ok";
				}
				feriadosList.limpiar();

			}

			statusMessages.clear();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
			return "ok";
		}
		
		statusMessages.clear();
		statusMessages.add(Severity.ERROR, SICCAAppHelper
				.getBundleMessage("CU299_msg3"));
		return null;
	}

	@SuppressWarnings("unchecked")
	private Boolean existenFeriadosCargados() {
		Date fechaActual = new Date();
		Integer anho = fechaActual.getYear() + 1900;

		List<Feriados> lista = new ArrayList<Feriados>();
		String sql = "Select fer.* from seleccion.feriados fer "
				+ "where fer.anho = " + anho + " and fer.activo is true";
		lista = em.createNativeQuery(sql, Feriados.class).getResultList();
		if (lista.size() > 0)
			return true;
		return false;
	}

	@SuppressWarnings("unchecked")
	private List<Feriados> feriadosAnteriores() {
		Date fechaActual = new Date();
		Integer anho = fechaActual.getYear() + 1900;
		anho = anho - 1;
		List<Feriados> lista = new ArrayList<Feriados>();
		String sql = "Select fer.* from seleccion.feriados fer "
				+ "where fer.anho = " + anho + " and fer.activo is true";
		lista = em.createNativeQuery(sql, Feriados.class).getResultList();
		return lista;
	}

	public Feriados getFeriados() {
		return feriados;
	}

	public void setFeriados(Feriados feriados) {
		this.feriados = feriados;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

}
