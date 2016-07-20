package py.com.excelsis.sicca.legajo.session.form;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;

import funserviceclient.FunServiceClient;

import py.com.excelsis.sicca.entity.EquivalenciasLegajo;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Name("listaFuncioNomina751FC")
@Scope(ScopeType.CONVERSATION)
public class ListaFuncioNomina751FC {

	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false, create = true)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(required = false, create = true)
	GenMigraFuncio752FC genMigraFuncio752FC;
	@In
	StatusMessages statusMessages;
	private Boolean primeraVez = true;
	private Long tipoPersona = null;
	private Integer LIMIT_REGISTROS = 5000;

	public void init() {
		if (primeraVez) {
			nivelEntidadOeeUtil.setIdConfigCab(usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo());
			nivelEntidadOeeUtil.init2();
			primeraVez = false;
		} else
			nivelEntidadOeeUtil.init();

	}

	public void limpiar() {
		nivelEntidadOeeUtil.limpiar();
		tipoPersona = null;
		primeraVez = true;
		init();
	}

	private EquivalenciasLegajo buscarEquivalencia() {
		Query q =
			em.createQuery("select EquivalenciasLegajo from EquivalenciasLegajo EquivalenciasLegajo "
				+ " where lower(EquivalenciasLegajo.tablaSicca) = 'configuracion_uo_cab' "
				+ " and  lower(EquivalenciasLegajo.tablaLegajo) = 'detalles_funcionarios' "
				+ " and EquivalenciasLegajo.codSicca = :codSicca ");
		q.setParameter("codSicca", nivelEntidadOeeUtil.getIdConfigCab());
		List<EquivalenciasLegajo> lista = q.getResultList();
		if (lista.size() > 0)
			return lista.get(0);
		return null;
	}

	private String tipoPersona() {
		if (tipoPersona != null) {
			Referencias q = em.find(Referencias.class, tipoPersona);
			return q.getDescLarga();
		}
		return null;
	}

	private Boolean precondGenerarMigracion() {
		if (nivelEntidadOeeUtil.getIdConfigCab() == null) {
			statusMessages.add(Severity.ERROR, "Debe cargar una OEE");
			return false;
		}
		return true;
	}

	private HashMap<String, Long> traerEquivalencias() {
		Query q =
			em.createQuery("select EquivalenciasLegajo from EquivalenciasLegajo EquivalenciasLegajo "
				+ " where upper(EquivalenciasLegajo.tablaSicca) = upper(:configuracion_uo_cab)"
				+ " and upper(EquivalenciasLegajo.tablaLegajo) = upper(:detalles_funcionarios) "
				+ " and EquivalenciasLegajo.codSicca = :codSicca");
		q.setParameter("codSicca", nivelEntidadOeeUtil.getIdConfigCab());
		q.setParameter("configuracion_uo_cab", "configuracion_uo_cab");
		q.setParameter("detalles_funcionarios", "detalles_funcionarios");
		List<EquivalenciasLegajo> lista = q.getResultList();
		if (lista.size() > 0) {
			HashMap<String, Long> dicc = new HashMap<String, Long>();
			EquivalenciasLegajo el = lista.get(0);
			String[] compos = el.getCodLegajoChar().split("\\.");
			dicc.put("NIVEL", new Long(compos[0]));
			dicc.put("ENTIDAD", new Long(compos[1]));
			dicc.put("OEE", new Long(compos[2]));
			return dicc;
		}
		return null;
	}

	public String generarMigracion() {
		if (!precondGenerarMigracion())
			return "FAIL";

		EquivalenciasLegajo codEquivale = buscarEquivalencia();
		if (codEquivale == null) {
			statusMessages.add(Severity.ERROR, "El OEE no cuenta con equivalencia para realizar la Migración. Verifique");
			return "FAIL";
		}
		HashMap<String, Long> diccEquiNEO = traerEquivalencias();

		String tipoPersonaString = tipoPersona();
		Integer count = null;
		count =
			FunServiceClient.totalRegistros(diccEquiNEO.get("NIVEL").intValue(), diccEquiNEO.get("ENTIDAD").intValue(), diccEquiNEO.get("OEE").intValue(), tipoPersonaString);

		List<client.DatosFuncDTO> lista = null;
		for (int i = 1; i <= count; i += LIMIT_REGISTROS) {
			lista =
				FunServiceClient.datosNomina(diccEquiNEO.get("NIVEL").intValue(), diccEquiNEO.get("ENTIDAD").intValue(), diccEquiNEO.get("OEE").intValue(), tipoPersonaString, i, LIMIT_REGISTROS);
			if (genMigraFuncio752FC == null) {
				genMigraFuncio752FC =
					(GenMigraFuncio752FC) org.jboss.seam.Component.getInstance(GenMigraFuncio752FC.class, true);
			}
			String respuesta =
				genMigraFuncio752FC.generarMigracion(lista, nivelEntidadOeeUtil.getIdConfigCab());

			if (respuesta.equalsIgnoreCase("FINALIZADO")) {
				RptMigracionCU753FC rptMigracionCU753FC =
					(RptMigracionCU753FC) org.jboss.seam.Component.getInstance(RptMigracionCU753FC.class, true);
				rptMigracionCU753FC.setTipoPersona(tipoPersona);
				rptMigracionCU753FC.setFechaDesde(new Date());
				rptMigracionCU753FC.setFechaHasta(new Date());
				try {
					rptMigracionCU753FC.imprimir();
					statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
				} catch (Exception e) {
					e.printStackTrace();
					statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("GENERICO_NO_MSG"));
				}
			} else {
				statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("GENERICO_NO_MSG"));
			}
		}
		if (count.intValue() == 0) {
			statusMessages.add(Severity.WARN, "No existen registros que procesar.");
			return "FAIL";
		}
		return null;
	}

	/* Select Items */
	public List<SelectItem> tipoPersonasSI() {
		Query q =
			em.createQuery("select Referencias from Referencias Referencias "
				+ " where Referencias.dominio = :dominio and Referencias.activo is true order by valorNum asc ");
		q.setParameter("dominio", "TIPO_PERSONA");
		List<Referencias> lista = q.getResultList();

		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, "Todos"));
		for (Referencias o : lista)
			si.add(new SelectItem(o.getIdReferencias(), o.getDescLarga()));
		return si;
	}

	public Long getTipoPersona() {
		return tipoPersona;
	}

	public void setTipoPersona(Long tipoPersona) {
		this.tipoPersona = tipoPersona;
	}
}
