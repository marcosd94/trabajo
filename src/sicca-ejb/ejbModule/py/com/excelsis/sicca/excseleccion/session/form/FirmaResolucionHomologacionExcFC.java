package py.com.excelsis.sicca.excseleccion.session.form;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import enums.ActividadEnum;

import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgrExc;
import py.com.excelsis.sicca.entity.ConcursoPuestoDet;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.Documentos;
import py.com.excelsis.sicca.entity.EstadoDet;
import py.com.excelsis.sicca.entity.Excepcion;
import py.com.excelsis.sicca.entity.HistorialExcepcion;
import py.com.excelsis.sicca.entity.HomologacionPerfilMatriz;
import py.com.excelsis.sicca.entity.Observacion;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.excseleccion.session.ConcursoPuestoAgrExcList;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.ConcursoPuestoAgrList;
import py.com.excelsis.sicca.session.util.JbpmUtilFormController;
import py.com.excelsis.sicca.session.util.ReferenciasUtilFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("firmaResolucionHomologacionExcFC")
public class FirmaResolucionHomologacionExcFC {

	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;

	@In(required = false)
	Usuario usuarioLogueado;

	@In(create = true)
	ConcursoPuestoAgrExcList concursoPuestoAgrExcList;

	@In(create = true)
	SeleccionUtilFormController seleccionUtilFormController;

	@In(scope = ScopeType.SESSION, required = false)
	@Out(scope = ScopeType.SESSION, required = false)
	String roles;
	@In(create = true)
	ReferenciasUtilFormController referenciasUtilFormController;

	private SinNivelEntidad sinNivelEntidad;
	private SinEntidad sinEntidad;
	private ConfiguracionUoCab configuracionUoCab;
	private ConcursoPuestoAgr concursoPuestoAgr;
	private Excepcion excepcion;
	private ConcursoPuestoAgrExc concursoPuestoAgrExc;
	private Concurso concurso;
	private String obs;
	private String nombrePantalla;
	private String direccionFisica;
	private String entity;
	private Long idConcursoPuestoAgr;
	private Long idExcepcion;
	private String fromActividad;// 21-FIRMAR_RESOLUCION_ADJUDICACION
									// 7-FIRMA_RESOL_HOMOLOG
									// 26-FIRMAR_RESOLUCION_NOMBRAMIENTO
	private String texto;
	private boolean habNombramiento = false;
	private List<ConcursoPuestoAgrExc> concursoPuestoAgrsExcs = new ArrayList<ConcursoPuestoAgrExc>();
	private int nivel;
	private SeguridadUtilFormController seguridadUtilFormController;

	public void init() {
		if (seguridadUtilFormController == null) {
			seguridadUtilFormController = (SeguridadUtilFormController) Component
					.getInstance(SeguridadUtilFormController.class, true);
		}
		concursoPuestoAgr = em.find(ConcursoPuestoAgr.class,
				idConcursoPuestoAgr);
		concurso = concursoPuestoAgr.getConcurso();
		excepcion = em.find(Excepcion.class, idExcepcion);
		findEntidades();// Trae las entidades segun el grupo que se envio
		searchAll();
		// anexar();
		if (sinNivelEntidad.getNenCodigo() != null)
			nivel = sinNivelEntidad.getNenCodigo().intValue();

		/*
		 * if (fromActividad.equals("7"))
		 * validarOee(seguridadUtilFormController.
		 * estadoActividades("ESTADOS_GRUPO", "CON DOCUMENTOS DE HOMOLOGACION")
		 * + "", ActividadEnum.FIRMA_RESOL_HOMOLOG.getValor()); if
		 * (fromActividad.equals("26"))
		 * validarOee(seguridadUtilFormController.estadoActividades
		 * ("ESTADOS_GRUPO", "CON RESOLUCION NOMBRAMIENTO") + "",
		 * ActividadEnum.FIRMAR_RESOLUCION_NOMBRAMIENTO.getValor());
		 */
		/**
		 * SE AGREGA PARA LA INCIDENCIA 0001060
		 ***/

		if (texto.equals("ADJ")) {

			if (nivel != 12)
				habNombramiento = true;
			else
				habNombramiento = false;
		}
	}

	@SuppressWarnings("unchecked")
	private void findEntidades() {
		configuracionUoCab = em.find(ConfiguracionUoCab.class, concurso
				.getConfiguracionUoCab().getIdConfiguracionUo());
		if (configuracionUoCab.getEntidad() != null) {
			sinEntidad = em.find(SinEntidad.class, configuracionUoCab
					.getEntidad().getSinEntidad().getIdSinEntidad());
			List<SinNivelEntidad> sin = em.createQuery(
					"Select n from SinNivelEntidad n "
							+ " where n.aniAniopre ="
							+ sinEntidad.getAniAniopre() + " and n.nenCodigo="
							+ sinEntidad.getNenCodigo()).getResultList();
			if (!sin.isEmpty())
				sinNivelEntidad = sin.get(0);

		}

	}

	public void searchAll() {
		String query = "";
		if (texto.equals("ADJ"))
			query = getQuery()
					+ " and concursoPuestoAgrExc.estado = 'CON RES. ADJUDICACION'";
		if (texto.equals("NOM"))
			query = getQuery()
					+ " and concursoPuestoAgrExc.estado = 'CON RES.NOMBRAMIENTO'";

		concursoPuestoAgrsExcs = concursoPuestoAgrExcList
				.listaResultadosCU596(query);
	}

	public String getQuery() {

		String query = "select concursoPuestoAgrExc from ConcursoPuestoAgrExc concursoPuestoAgrExc "
				+ "join concursoPuestoAgrExc.concursoPuestoAgr agr "
				+ "join concursoPuestoAgrExc.excepcion exc "
				+ "where agr.idConcursoPuestoAgr = "
				+ idConcursoPuestoAgr
				+ " and agr.activo is true and concursoPuestoAgrExc.activo is true  "
				+ "and exc.idExcepcion = " + idExcepcion;

		return query;
	}

	@SuppressWarnings("unchecked")
	public boolean esPermanente(Long idGrupo, Long id) {
		List<ConcursoPuestoDet> dets = em.createQuery(
				"Select d from ConcursoPuestoDet d where d.activo=true"
						+ " and d.concursoPuestoAgr.idConcursoPuestoAgr="
						+ idGrupo + " and d.excepcion.idExcepcion=" + id)
				.getResultList();
		if (!dets.isEmpty() && dets.get(0).getPlantaCargoDet().getPermanente())
			return true;

		return false;
	}

	@SuppressWarnings("unchecked")
	public ConcursoPuestoDet obtenerConcursoPuestoDet() {
		List<ConcursoPuestoDet> dets = em.createQuery(
				"Select d from ConcursoPuestoDet d where d.activo=true"
						+ " and d.concursoPuestoAgr.idConcursoPuestoAgr="
						+ concursoPuestoAgr.getIdConcursoPuestoAgr()
						+ " and d.excepcion.idExcepcion="
						+ excepcion.getIdExcepcion()).getResultList();
		if (!dets.isEmpty())
			return dets.get(0);

		return null;
	}

	public String anexar(Long idConcursoPuestoAgrExc) {
		ConcursoPuestoAgrExc grupo = em.find(ConcursoPuestoAgrExc.class,
				idConcursoPuestoAgrExc);
		String url = "/seleccion/adminDocAdjunto/AdmDocAdjunto.seam";
		String nombrePantalla = "";
		String idCU = "";
		String nombreTabla = "Resolucion";
		String mostrarDoc = "true";
		String isEdit = "true";
		String from = "excepcionesSeleccion/firmaResolucionExc/FirmarResolucionHomologacionExc";

		SimpleDateFormat sdfSoloAnio = new SimpleDateFormat("yyyy");
		String anio = sdfSoloAnio.format(new Date());
		String separator = File.separator;
		direccionFisica = separator
				+ "SICCA"
				+ separator
				+ anio
				+ separator
				+ "OEE"
				+ separator
				+ configuracionUoCab.getIdConfiguracionUo()
				+ separator
				+ concurso.getDatosEspecificosTipoConc()
						.getIdDatosEspecificos() + separator
				+ concurso.getIdConcurso();
		String pagina = url + "?mostrarDoc=" + mostrarDoc + "&isEdit=" + isEdit
				+ "&from=" + from + "&nombreTabla=" + nombreTabla
				+ "&direccionFisica=" + direccionFisica;

		if (texto.equals("ADJ")) {
			nombrePantalla = "FIRMA_RESOL_ADJ_EXC";
			idCU = grupo.getResolucionAdjudicacion().getIdResolucion() + "";
		}
		if (texto.equals("NOM")) {
			nombrePantalla = "FIRMA_RESOL_NOMB_EXC";
			idCU = grupo.getResolucionNombramiento().getIdResolucion() + "";
		}
		pagina += "&nombrePantalla=" + nombrePantalla + "&idCU=" + idCU;

		return pagina;
	}

	@SuppressWarnings("unchecked")
	public String nextTask() {

		boolean hayDoc = true;
		statusMessages.clear();
		Long idCU = null;
		try {

			for (int i = 0; i < concursoPuestoAgrsExcs.size(); i++) {
				ConcursoPuestoAgrExc grupo = concursoPuestoAgrsExcs.get(i);

				if (texto.equals("ADJ")) {
					nombrePantalla = "FIRMA_RESOL_ADJ_EXC";
					idCU = grupo.getResolucionAdjudicacion().getIdResolucion();
				}
				if (texto.equals("NOM")) {
					nombrePantalla = "FIRMA_RESOL_NOMB_EXC";
					idCU = grupo.getResolucionNombramiento().getIdResolucion();
				}
				List<Documentos> d = em
						.createQuery(
								"Select d from Documentos d "
										+ "where lower(d.nombrePantalla) like '"
										+ nombrePantalla.toLowerCase()
										+ "' and d.activo=true and d.nombreTabla like 'Resolucion' and d.idTabla="
										+ idCU).getResultList();
				if (d.isEmpty()) {
					statusMessages.add(Severity.ERROR,
							"Debe Anexar la Resolución para el grupo: "
									+ concursoPuestoAgrsExcs.get(i)
											.getConcursoPuestoAgr()
											.getDescripcionGrupo());
					hayDoc = false;

				}
			}
			if (!hayDoc) {
				return null;
			}
			statusMessages.clear();

			Calendar calCalendario = Calendar.getInstance();

			/**
			 * Si fue llamado desde “Firmar Resolución de Adjudicación OEE por
			 * Concurso”
			 * 
			 * */
			if (texto.equals("ADJ")) {
				// PARA TODOS LOS GRUPOS SE GUARDA LA FECHA DE ADJUDICACION
				/**
				 * o Actualizar el campo FECHA_ADJUDICACION = fecha del sistema
				 * de cada grupo de puestos visualizados en la grilla
				 * */
				for (int i = 0; i < concursoPuestoAgrsExcs.size(); i++) {
					ConcursoPuestoAgrExc agr = em.find(
							ConcursoPuestoAgrExc.class, concursoPuestoAgrsExcs
									.get(i).getIdConcursoPuestoAgrExc());
					agr.setFechaAdjudicacion(new Date());
					List<ConcursoPuestoDet> dets = em
							.createQuery(
									"Select d from ConcursoPuestoDet d where d.activo=true"
											+ " and d.concursoPuestoAgr.idConcursoPuestoAgr="
											+ agr.getConcursoPuestoAgr()
													.getIdConcursoPuestoAgr()
											+ " and d.excepcion.idExcepcion="
											+ idExcepcion).getResultList();
					if (!dets.isEmpty()) {
						/**
						 * SI ES PERMANENTE
						 * */
						if (dets.get(0).getPlantaCargoDet().getPermanente()) {

							if (nivel != 12) {
								if (agr.getConcursoPuestoAgr()
										.getNombramiento() != null
										&& !agr.getConcursoPuestoAgr()
												.getNombramiento())
									agr.setNombramiento(false);
								if (agr.getConcursoPuestoAgr()
										.getNombramiento() != null
										&& agr.getConcursoPuestoAgr()
												.getNombramiento())
									agr.setNombramiento(true);
							}
						}

						for (int j = 0; j < dets.size(); j++) {
							ConcursoPuestoDet c = new ConcursoPuestoDet();
							c = dets.get(j);
							// ConcursoPuestoAgr puestoAgr = new
							// ConcursoPuestoAgr();

							PlantaCargoDet pdet = new PlantaCargoDet();
							pdet = c.getPlantaCargoDet();
							if (c.getPlantaCargoDet().getContratado() != null
									&& c.getPlantaCargoDet().getContratado()) {
								agr.setEstado("CONTRATADO");

								c.setEstadoDet(estadoContratado());
								if (pdet != null)
									pdet.setEstadoDet(estadoContratado());
							}
							if (c.getPlantaCargoDet().getPermanente() != null
									&& c.getPlantaCargoDet().getPermanente()
									&& nivel != 12) {
								agr.setEstado("PERMANENTE D12");

								c.setEstadoDet(estadoPermanenteD12());
								if (pdet != null)
									pdet.setEstadoDet(estadoPermanenteD12());
							}
							if (c.getPlantaCargoDet().getPermanente() != null
									&& c.getPlantaCargoDet().getPermanente()
									&& nivel == 12) {
								agr.setEstado("PERMANENTE N12");

								c.setEstadoDet(estadoPermanenteN12());
								if (pdet != null)
									pdet.setEstadoDet(estadoPermanenteN12());
							}

							if (pdet != null) {
								pdet.setUsuMod(usuarioLogueado
										.getCodigoUsuario());
								pdet.setFechaMod(new Date());
								em.merge(pdet);
							}
							c.setUsuMod(usuarioLogueado.getCodigoUsuario());
							c.setFechaMod(new Date());
							em.merge(c);
						}

						agr.setUsuMod(usuarioLogueado.getCodigoUsuario());
						agr.setFechaMod(new Date());
						em.merge(agr);
					}
				}

				HistorialExcepcion historialExcepcion = new HistorialExcepcion();
				historialExcepcion.setConcursoGrupoAgr(concursoPuestoAgr);
				ConcursoPuestoDet det = new ConcursoPuestoDet();
				det = obtenerConcursoPuestoDet();
				if (det.getPlantaCargoDet().getContratado() != null
						&& det.getPlantaCargoDet().getContratado()) {
					historialExcepcion.setEstado("CONTRATADO");
					excepcion.setEstado("CONTRATADO");
				}
				if (det.getPlantaCargoDet().getPermanente() != null
						&& det.getPlantaCargoDet().getPermanente()
						&& nivel != 12) {
					historialExcepcion.setEstado("PERMANENTE D12");
					excepcion.setEstado("PERMANENTE D12");
				}
				if (det.getPlantaCargoDet().getPermanente() != null
						&& det.getPlantaCargoDet().getPermanente()
						&& nivel == 12) {
					historialExcepcion.setEstado("PERMANENTE N12");
					excepcion.setEstado("PERMANENTE N12");
				}
				historialExcepcion.setExcepcion(excepcion);
				historialExcepcion.setFechaAlta(new Date());
				historialExcepcion.setUsuAlta(usuarioLogueado
						.getCodigoUsuario());
				if (obs != null && !obs.trim().isEmpty())
					historialExcepcion.setObservacion(obs);
				em.persist(historialExcepcion);

				excepcion.setFechaMod(new Date());
				excepcion.setUsuMod(usuarioLogueado.getCodigoUsuario());
				em.merge(excepcion);

				em.flush();

			}

			/**
			 * Si fue llamado desde “Firmar Resolución de Nombramiento”
			 * */

			if (texto.equals("NOM")) {
				for (int i = 0; i < concursoPuestoAgrsExcs.size(); i++) {
					ConcursoPuestoAgrExc agr = em.find(
							ConcursoPuestoAgrExc.class, concursoPuestoAgrsExcs
									.get(i).getIdConcursoPuestoAgrExc());
					agr.setEstado("FIRMADO NOMBRAMIENTO");
					agr.setFechaMod(new Date());
					agr.setUsuMod(usuarioLogueado.getCodigoUsuario());
					em.merge(agr);

					/**
					 * Deberá actualizar el estado de los Puestos de cada grupo:
					 * */

					List<ConcursoPuestoDet> d = em
							.createQuery(
									"Select c from ConcursoPuestoDet c"
											+ " where c.activo=true and  c.concursoPuestoAgr.idConcursoPuestoAgr="
											+ concursoPuestoAgrsExcs.get(i)
													.getConcursoPuestoAgr()
													.getIdConcursoPuestoAgr()
											+ " and c.estadoDet.idEstadoDet="
											+ conResolucionNombramiento()
											+ " and c.excepcion.idExcepcion = "
											+ excepcion.getIdExcepcion())
							.getResultList();
					for (int j = 0; j < d.size(); j++) {
						ConcursoPuestoDet puestoDet = em.find(
								ConcursoPuestoDet.class, d.get(j)
										.getIdConcursoPuestoDet());
						puestoDet.setEstadoDet(estadoFirmadoNombramiento());
						puestoDet.setFechaMod(new Date());
						puestoDet.setUsuMod(usuarioLogueado.getCodigoUsuario());
						em.merge(puestoDet);

						if (d.get(j).getPlantaCargoDet() != null) {
							PlantaCargoDet pdet = em.find(PlantaCargoDet.class,
									d.get(j).getPlantaCargoDet()
											.getIdPlantaCargoDet());
							pdet.setEstadoDet(estadoFirmadoNombramiento());
							pdet.setFechaMod(new Date());
							pdet.setUsuMod(usuarioLogueado.getCodigoUsuario());
							em.merge(pdet);

						}
					}

				}
				HistorialExcepcion historialExcepcion = new HistorialExcepcion();
				historialExcepcion.setConcursoGrupoAgr(concursoPuestoAgr);
				ConcursoPuestoDet det = new ConcursoPuestoDet();
				det = obtenerConcursoPuestoDet();

				historialExcepcion.setEstado("FIRMADO NOMBRAMIENTO");
				excepcion.setEstado("FIRMADO NOMBRAMIENTO");

				historialExcepcion.setExcepcion(excepcion);
				historialExcepcion.setFechaAlta(new Date());
				historialExcepcion.setUsuAlta(usuarioLogueado
						.getCodigoUsuario());
				if (obs != null && !obs.trim().isEmpty())
					historialExcepcion.setObservacion(obs);
				em.persist(historialExcepcion);

				excepcion.setFechaMod(new Date());
				excepcion.setUsuMod(usuarioLogueado.getCodigoUsuario());
				em.merge(excepcion);

			}

			em.flush();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
			return "next";
		} catch (Exception e) {
			return null;
		}

	}

	@SuppressWarnings("unchecked")
	private EstadoDet estadoFirmadoNombramiento() {
		List<EstadoDet> eDet = em
				.createQuery(
						" Select e from EstadoDet e "
								+ " where e.descripcion like 'FIRMADO NOMBRAMIENTO' and e.estadoCab.descripcion like 'CONCURSO'")
				.getResultList();
		if (eDet.isEmpty())
			return null;
		else
			return eDet.get(0);
	}

	@SuppressWarnings("unchecked")
	private Long conResolucionNombramiento() {
		List<EstadoDet> eDet = em
				.createQuery(
						" Select e from EstadoDet e "
								+ " where e.descripcion like 'CON RESOLUCION NOMBRAMIENTO' and e.estadoCab.descripcion like 'CONCURSO'")
				.getResultList();
		if (eDet.isEmpty())
			return null;
		else
			return eDet.get(0).getIdEstadoDet();
	}

	@SuppressWarnings("unchecked")
	private Integer firmadoNombramiento() {
		List<Referencias> ref = em
				.createQuery(
						" Select r from Referencias r "
								+ " where r.dominio like 'ESTADOS_GRUPO' and r.descAbrev like 'FIRMADO NOMBRAMIENTO'")
				.getResultList();
		if (ref.isEmpty())
			return null;
		else
			return ref.get(0).getValorNum();
	}

	@SuppressWarnings("unchecked")
	private EstadoDet estadoContratado() {
		List<EstadoDet> eDet = em
				.createQuery(
						" Select e from EstadoDet e "
								+ " where e.descripcion like 'CONTRATADO' and e.estadoCab.descripcion like 'CONCURSO'")
				.getResultList();
		if (eDet.isEmpty())
			return null;
		else
			return eDet.get(0);
	}

	@SuppressWarnings("unchecked")
	private EstadoDet estadoPermanenteD12() {
		List<EstadoDet> eDet = em
				.createQuery(
						" Select e from EstadoDet e "
								+ " where e.descripcion like 'PERMANENTE D12' and e.estadoCab.descripcion like 'CONCURSO'")
				.getResultList();
		if (eDet.isEmpty())
			return null;
		else
			return eDet.get(0);
	}

	@SuppressWarnings("unchecked")
	private EstadoDet estadoPermanenteN12() {
		List<EstadoDet> eDet = em
				.createQuery(
						" Select e from EstadoDet e "
								+ " where e.descripcion like 'PERMANENTE N12' and e.estadoCab.descripcion like 'CONCURSO'")
				.getResultList();
		if (eDet.isEmpty())
			return null;
		else
			return eDet.get(0);
	}

	@SuppressWarnings("unchecked")
	private Integer contratado() {
		List<Referencias> ref = em
				.createQuery(
						" Select r from Referencias r "
								+ " where r.dominio like 'ESTADOS_GRUPO' and r.descAbrev like 'CONTRATADO'")
				.getResultList();
		if (ref.isEmpty())
			return null;
		else
			return ref.get(0).getValorNum();
	}

	@SuppressWarnings("unchecked")
	private Integer permanenteD12() {
		List<Referencias> ref = em
				.createQuery(
						" Select r from Referencias r "
								+ " where r.dominio like 'ESTADOS_GRUPO' and r.descAbrev like 'PERMANENTE D12'")
				.getResultList();
		if (ref.isEmpty())
			return null;
		else
			return ref.get(0).getValorNum();
	}

	@SuppressWarnings("unchecked")
	private Integer permanenteN12() {
		List<Referencias> ref = em
				.createQuery(
						" Select r from Referencias r "
								+ " where r.dominio like 'ESTADOS_GRUPO' and r.descAbrev like 'PERMANENTE N12'")
				.getResultList();
		if (ref.isEmpty())
			return null;
		else
			return ref.get(0).getValorNum();
	}

	public SinNivelEntidad getSinNivelEntidad() {
		return sinNivelEntidad;
	}

	public void setSinNivelEntidad(SinNivelEntidad sinNivelEntidad) {
		this.sinNivelEntidad = sinNivelEntidad;
	}

	public SinEntidad getSinEntidad() {
		return sinEntidad;
	}

	public void setSinEntidad(SinEntidad sinEntidad) {
		this.sinEntidad = sinEntidad;
	}

	public ConfiguracionUoCab getConfiguracionUoCab() {
		return configuracionUoCab;
	}

	public void setConfiguracionUoCab(ConfiguracionUoCab configuracionUoCab) {
		this.configuracionUoCab = configuracionUoCab;
	}

	public ConcursoPuestoAgr getConcursoPuestoAgr() {
		return concursoPuestoAgr;
	}

	public void setConcursoPuestoAgr(ConcursoPuestoAgr concursoPuestoAgr) {
		this.concursoPuestoAgr = concursoPuestoAgr;
	}

	public Concurso getConcurso() {
		return concurso;
	}

	public void setConcurso(Concurso concurso) {
		this.concurso = concurso;
	}

	public String getObs() {
		return obs;
	}

	public void setObs(String obs) {
		this.obs = obs;
	}

	public String getNombrePantalla() {
		return nombrePantalla;
	}

	public void setNombrePantalla(String nombrePantalla) {
		this.nombrePantalla = nombrePantalla;
	}

	public String getDireccionFisica() {
		return direccionFisica;
	}

	public void setDireccionFisica(String direccionFisica) {
		this.direccionFisica = direccionFisica;
	}

	public String getEntity() {
		return entity;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}

	public Long getIdConcursoPuestoAgr() {
		return idConcursoPuestoAgr;
	}

	public void setIdConcursoPuestoAgr(Long idConcursoPuestoAgr) {
		this.idConcursoPuestoAgr = idConcursoPuestoAgr;
	}

	public String getFromActividad() {
		return fromActividad;
	}

	public void setFromActividad(String fromActividad) {
		this.fromActividad = fromActividad;
	}

	public boolean isHabNombramiento() {
		return habNombramiento;
	}

	public void setHabNombramiento(boolean habNombramiento) {
		this.habNombramiento = habNombramiento;
	}

	public int getNivel() {
		return nivel;
	}

	public void setNivel(int nivel) {
		this.nivel = nivel;
	}

	public Excepcion getExcepcion() {
		return excepcion;
	}

	public void setExcepcion(Excepcion excepcion) {
		this.excepcion = excepcion;
	}

	public Long getIdExcepcion() {
		return idExcepcion;
	}

	public void setIdExcepcion(Long idExcepcion) {
		this.idExcepcion = idExcepcion;
	}

	public ConcursoPuestoAgrExc getConcursoPuestoAgrExc() {
		return concursoPuestoAgrExc;
	}

	public void setConcursoPuestoAgrExc(
			ConcursoPuestoAgrExc concursoPuestoAgrExc) {
		this.concursoPuestoAgrExc = concursoPuestoAgrExc;
	}

	public String getTexto() {
		return texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}

	public List<ConcursoPuestoAgrExc> getConcursoPuestoAgrsExcs() {
		return concursoPuestoAgrsExcs;
	}

	public void setConcursoPuestoAgrsExcs(
			List<ConcursoPuestoAgrExc> concursoPuestoAgrsExcs) {
		this.concursoPuestoAgrsExcs = concursoPuestoAgrsExcs;
	}

}
