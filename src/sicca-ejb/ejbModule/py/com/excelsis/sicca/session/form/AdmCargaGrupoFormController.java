package py.com.excelsis.sicca.session.form;

import java.io.File;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.bpm.BusinessProcess;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.security.AuthorizationException;
import org.owasp.esapi.waf.rules.MustMatchRule;

import enums.ActividadEnum;
import enums.EstadoSumario;
import enums.ModalidadOcupacion;
import enums.ProcesoEnum;
import py.com.excelsis.sicca.dto.CondicionSeguridadDTO;
import py.com.excelsis.sicca.dto.CondicionTrabajoDTO;
import py.com.excelsis.sicca.dto.CondicionTrabajoEspecifDTO;
import py.com.excelsis.sicca.dto.ContenidoFuncionalDTO;
import py.com.excelsis.sicca.dto.RequisitosMinimosDTO;
import py.com.excelsis.sicca.entity.Barrio;
import py.com.excelsis.sicca.entity.Ciudad;
import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.ConcursoPuestoDet;
import py.com.excelsis.sicca.entity.CondicionSegur;
import py.com.excelsis.sicca.entity.CondicionTrabajo;
import py.com.excelsis.sicca.entity.CondicionTrabajoEspecif;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.ConfiguracionUoDet;
import py.com.excelsis.sicca.entity.ContenidoFuncional;
import py.com.excelsis.sicca.entity.Cpt;
import py.com.excelsis.sicca.entity.DatosAmonestacion;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.DatosGrupoPuesto;
import py.com.excelsis.sicca.entity.Departamento;
import py.com.excelsis.sicca.entity.DetCondicionSegur;
import py.com.excelsis.sicca.entity.DetCondicionTrabajo;
import py.com.excelsis.sicca.entity.DetCondicionTrabajoEspecif;
import py.com.excelsis.sicca.entity.DetContenidoFuncional;
import py.com.excelsis.sicca.entity.DetDescripcionContFuncional;
import py.com.excelsis.sicca.entity.DetMinimosRequeridos;
import py.com.excelsis.sicca.entity.DetOpcionesConvenientes;
import py.com.excelsis.sicca.entity.DetReqMin;
import py.com.excelsis.sicca.entity.Entidad;
import py.com.excelsis.sicca.entity.EstadoDet;
import py.com.excelsis.sicca.entity.GrupoConceptoPago;
import py.com.excelsis.sicca.entity.HomologacionPerfilMatriz;
import py.com.excelsis.sicca.entity.MatrizRefConf;
import py.com.excelsis.sicca.entity.Observacion;
import py.com.excelsis.sicca.entity.Pais;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.entity.PuestoConceptoPago;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.entity.RequisitoMinimoCpt;
import py.com.excelsis.sicca.entity.SinAnx;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.entity.SinObj;
import py.com.excelsis.sicca.entity.ValorNivelOrg;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.BarrioList;
import py.com.excelsis.sicca.session.CiudadList;
import py.com.excelsis.sicca.session.DepartamentoList;
import py.com.excelsis.sicca.session.DetContenidoFuncionalHome;
import py.com.excelsis.sicca.session.DetReqMinHome;
import py.com.excelsis.sicca.session.SinNivelEntidadList;
import py.com.excelsis.sicca.session.SinObjList;
import py.com.excelsis.sicca.session.util.JbpmUtilFormController;
import py.com.excelsis.sicca.session.util.NumeroLetra;
import py.com.excelsis.sicca.session.util.ReferenciasUtilFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.SinarhUtiles;
import py.com.excelsis.sicca.util.Utiles;
import py.com.excelsis.sicca.session.form.FuncionComisionFormController;

@Scope(ScopeType.CONVERSATION)
@Name("admCargaGrupoFormController")
public class AdmCargaGrupoFormController implements Serializable {

	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In
	StatusMessages statusMessages;
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
	SinNivelEntidadList sinNivelEntidadList;
	@In(create = true)
	JbpmUtilFormController jbpmUtilFormController;
	@In(create = true)
	ReferenciasUtilFormController referenciasUtilFormController;
	@In(create = true)
	SeleccionUtilFormController seleccionUtilFormController;
	@In(create = true)
	SeguridadUtilFormController seguridadUtilFormController;
	@In(create = true)
	SinObjList sinObjList;

	@In(scope = ScopeType.SESSION, required = false)
	@Out(scope = ScopeType.SESSION, required = false)
	String roles;

	private static String ACTIVIDAD_ESTADO = "2";
	private SinNivelEntidad nivelEntidad = new SinNivelEntidad();
	private SinEntidad sinEntidad = new SinEntidad();
	private ConfiguracionUoCab configuracionUoCab = new ConfiguracionUoCab();
	private Concurso concurso;
	private ConcursoPuestoAgr concursoPuestoAgr;
	private PlantaCargoDet plantaCargoDet;
	private List<ConcursoPuestoDet> listaConcursoPuestoDet = new ArrayList<ConcursoPuestoDet>();
	private List<SelectItem> departamentosSelecItem;
	private List<SelectItem> ciudadSelecItem;
	private List<SelectItem> barrioSelecItem;
	private Long idDpto;
	private Long idCiudad;
	private Long idBarrio;
	private Long idConcursoPuestoAgr;
	private ModalidadOcupacion modalidadOcupacion;
	private String valorRadioButton;
	private String observaciones;
	private String ubicacionFisica;
	private Boolean muestraBtnHomologados = false;
	private Boolean habilitar = true;
	private Boolean Edit = false;
	

	/**
	 * Método que inicializa los datos
	 */
	public void init() {
		concursoPuestoAgr = new ConcursoPuestoAgr();
		if (idConcursoPuestoAgr != null) {
			concursoPuestoAgr = em.find(ConcursoPuestoAgr.class,
					idConcursoPuestoAgr);
			concurso = new Concurso();
			configuracionUoCab = new ConfiguracionUoCab();
			sinEntidad = new SinEntidad();
			nivelEntidad = new SinNivelEntidad();
			concurso = concursoPuestoAgr.getConcurso();
			listaConcursoPuestoDet = new ArrayList<ConcursoPuestoDet>();
			buscarLista();
			muestraBancoPerfiles();
			if (concurso != null) {
				/* Incidencia 1014 */
				validarOee();
				/**/
				configuracionUoCab = concurso.getConfiguracionUoCab();
				if (configuracionUoCab != null)
					sinEntidad = configuracionUoCab.getEntidad()
							.getSinEntidad();
				if (sinEntidad != null)
					nivelEntidad = buscarNivel(sinEntidad.getNenCodigo());
			}
		}
		Date fechaActual = new Date();
		Integer anho = fechaActual.getYear() + 1900;
		String separador = File.separator;
		ubicacionFisica = separador
				+ "SICCA"
				+ separador
				+ anho
				+ separador
				+ "OEE"
				+ separador
				+ configuracionUoCab.getIdConfiguracionUo()
				+ separador
				+ concursoPuestoAgr.getConcurso().getDatosEspecificosTipoConc()
						.getIdDatosEspecificos() + separador
				+ concursoPuestoAgr.getConcurso().getIdConcurso() + separador
				+ concursoPuestoAgr.getIdConcursoPuestoAgr();
		updateDepartamento();
		updateCiudad();
		updateBarrio();
		habilitarPantalla();

		if (concursoPuestoAgr.getCarpeta() == null) {
			concursoPuestoAgr.setCarpeta(false);
			em.persist(concursoPuestoAgr);
			em.flush();
		}
		if (!existeFunciones(concursoPuestoAgr)) {
			cargarFuncion();
		}
		if (!existeRequisitos(concursoPuestoAgr)) {
			cargarRequisito();
		}
		if (!existeConceptoPagos(concursoPuestoAgr)) {
			if (concursoPuestoAgr != null
					&& concursoPuestoAgr.getConcursoPuestoDets().get(0)
							.getPlantaCargoDet().getPermanente())
				cargarConceptoPago();
		}
		if (!existeCondicionTrabajoGral(concursoPuestoAgr)) {
			cargarCondicionesTrabajo();
		}
		if (!existeCondicionTrabajoEspecifico(concursoPuestoAgr)) {
			cargarCondicionesTrabajoEspecifico();
		}
		if (!existeCondicionSeguridad(concursoPuestoAgr)) {
			cargarCondicionesSeguridad();
		}
		cargaSueldosRemuneracion();
	}

	/************************* <MODIFICACION> *********************************/
	/************************* MODIFICADO: 04/11/2013 *************************/
	/************************* AUTOR: RODRIGO VELAZQUEZ ***********************/
	/*
	 * >>>>>>>>>>>>>>>>>>>>>>>>Cargar Concepto
	 * Pago<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
	 */
	private List<GrupoConceptoPago> listaGrupoPago = new ArrayList<GrupoConceptoPago>();
	private List<GrupoConceptoPago> listaGrupoPagoAux = new ArrayList<GrupoConceptoPago>();
	private Long idSinAnx;
	private Long idSinCategoria;
	private Integer codigoObj;
	private String valorObj;
	private String codigoCategoria;
	private String categoria;
	private Integer monto;
	private String msgStock;
	private static String CONCURSO_INTERNO_INTERINSTITUCIONAL = "CONCURSO INTERNO DE OPOSICION INTER INSTITUCIONAL";
	@In(create = true)
	SinarhUtiles sinarhUtiles;

	@SuppressWarnings("unchecked")
	private void buscarDatos() {
		String sql = "select pag.* from seleccion.grupo_concepto_pago pag "
				+ "where pag.activo is true and pag.id_concurso_puesto_agr = "
				+ concursoPuestoAgr.getIdConcursoPuestoAgr();
		listaGrupoPago = new ArrayList<GrupoConceptoPago>();
		listaGrupoPagoAux = new ArrayList<GrupoConceptoPago>();
		listaGrupoPago = em.createNativeQuery(sql, GrupoConceptoPago.class)
				.getResultList();
		if (listaGrupoPago.size() > 0)
			listaGrupoPagoAux.addAll(listaGrupoPago);
		codigoObj = null;
		valorObj = null;
		codigoCategoria = null;
		categoria = null;
		monto = null;
	}

	public String esContratadoOpermanente(Long id) {
		Query q = em
				.createQuery("select det from ConcursoPuestoDet det "
						+ " where det.activo is true and det.concursoPuestoAgr.idConcursoPuestoAgr = :idGrupo ");
		q.setParameter("idGrupo", id);
		List<ConcursoPuestoDet> lista = q.getResultList();
		if (!lista.isEmpty()) {
			if (lista.get(0).getPlantaCargoDet().getContratado())
				return "CONTRATADO";
			if (lista.get(0).getPlantaCargoDet().getPermanente())
				return "PERMANENTE";
		}
		return null;
	}

	private boolean esPrimeraEntrada() {
		Referencias referencias = new Referencias();
		if (!concurso.getAdReferendum())
			referencias = referenciasUtilFormController.getReferencia(
					"ESTADOS_CATEGORIA_GRUPO", "RESERVADO");
		if (concurso.getAdReferendum())
			referencias = referenciasUtilFormController.getReferencia(
					"ESTADOS_CATEGORIA_GRUPO", "PENDIENTE");
		Query q = em
				.createQuery("select pago from GrupoConceptoPago pago "
						+ " where pago.concursoPuestoAgr.idConcursoPuestoAgr = :idGrupo and pago.estado = :estado");
		q.setParameter("idGrupo", concursoPuestoAgr.getIdConcursoPuestoAgr());
		q.setParameter("estado", referencias.getValorNum());
		List<GrupoConceptoPago> lista = q.getResultList();
		if (lista.isEmpty())
			return true;
		else
			return false;

	}

	public PlantaCargoDet obtenerPlantaCargoDet(Long id) {
		Query q = em
				.createQuery("select det from ConcursoPuestoDet det "
						+ " where det.concursoPuestoAgr.idConcursoPuestoAgr = :idGrupo and det.activo is true");
		q.setParameter("idGrupo", id);
		List<ConcursoPuestoDet> listaDet = q.getResultList();
		if (listaDet.isEmpty())
			return null;
		return listaDet.get(0).getPlantaCargoDet();
	}

	private PuestoConceptoPago obtenerPuestoConceptoPago(PlantaCargoDet planta) {

		Referencias referencias = new Referencias();
		if (!concurso.getAdReferendum())
			referencias = referenciasUtilFormController.getReferencia(
					"ESTADOS_CATEGORIA", "RESERVADO");
		if (concurso.getAdReferendum())
			referencias = referenciasUtilFormController.getReferencia(
					"ESTADOS_CATEGORIA", "PENDIENTE");
		Query q = em
				.createQuery("select concepto from PuestoConceptoPago concepto "
						+ " where concepto.plantaCargoDet.idPlantaCargoDet = :idPuesto and concepto.estado = :estado");
		q.setParameter("idPuesto", planta.getIdPlantaCargoDet());
		q.setParameter("estado", referencias.getValorNum());
		List<PuestoConceptoPago> lista = q.getResultList();
		if (lista.isEmpty())
			return null;
		return lista.get(0);
	}

	public void guardarPermanenteSinCategoria(GrupoConceptoPago pago) {
		Referencias referenciaGrupo = new Referencias();
		referenciaGrupo = referenciasUtilFormController.getReferencia(
				"ESTADOS_CATEGORIA_GRUPO", "RESERVADO");
		Referencias referenciaPuesto = new Referencias();
		referenciaPuesto = referenciasUtilFormController.getReferencia(
				"ESTADOS_CATEGORIA", "RESERVADO");
		pago.setEstado(referenciaGrupo.getValorNum());
		pago.setTipo("GRUPO");
		em.persist(pago);

		PuestoConceptoPago puestoPago = new PuestoConceptoPago();
		puestoPago.setActivo(true);
		puestoPago.setAnho(pago.getAnho());
		puestoPago.setEstado(referenciaPuesto.getValorNum());
		puestoPago.setFechaAlta(new Date());
		puestoPago.setMonto(pago.getMonto());
		puestoPago.setObjCodigo(pago.getObjCodigo());
		puestoPago.setCategoria(pago.getCategoria());
		puestoPago.setUsuAlta(usuarioLogueado.getCodigoUsuario());
		PlantaCargoDet planta = new PlantaCargoDet();
		planta = obtenerPlantaCargoDet(concursoPuestoAgr
				.getIdConcursoPuestoAgr());
		puestoPago.setPlantaCargoDet(planta);
		em.persist(puestoPago);

	}

	private Integer cantidadPuestosActivos() {
		Query q = em
				.createQuery("select det from ConcursoPuestoDet det "
						+ " where det.activo is true and det.concursoPuestoAgr.idConcursoPuestoAgr = :idGrupo ");
		q.setParameter("idGrupo", concursoPuestoAgr.getIdConcursoPuestoAgr());
		List<ConcursoPuestoDet> lista = q.getResultList();
		if (lista.isEmpty())
			return new Integer(0);
		else
			return lista.size();

	}

	private List<ConcursoPuestoDet> puestosActivos() {
		Query q = em
				.createQuery("select det from ConcursoPuestoDet det "
						+ " where det.activo is true and det.concursoPuestoAgr.idConcursoPuestoAgr = :idGrupo ");
		q.setParameter("idGrupo", concursoPuestoAgr.getIdConcursoPuestoAgr());
		return q.getResultList();

	}

	public void guardarPermanenteConCategoria(GrupoConceptoPago pago) {
		if (concurso.getAdReferendum() != null && !concurso.getAdReferendum()) {
			Referencias referenciaGrupo = new Referencias();
			referenciaGrupo = referenciasUtilFormController.getReferencia(
					"ESTADOS_CATEGORIA_GRUPO", "RESERVADO");
			Referencias referenciaPuesto = new Referencias();
			referenciaPuesto = referenciasUtilFormController.getReferencia(
					"ESTADOS_CATEGORIA", "RESERVADO");

			msgStock = "";

			List<SinAnx> listaAnx = sinarhUtiles.obtenerListaSinAnx(null,
					new Integer(50), codigoObj, pago.getCategoria(), null);

			if (!listaAnx.isEmpty()) {
				Integer cantADescontar = cantidadPuestosActivos();
				Integer total = new Integer(0);
				for (SinAnx a : listaAnx)
					total = total + a.getCantDisponible();
				if (total.intValue() < cantADescontar) {
					statusMessages.clear();
					statusMessages.add(Severity.ERROR,
							"No existe stock suficiente en SINARH");
					return;

				} else {
					for (SinAnx a : listaAnx) {
						if (cantADescontar.intValue() > 0) {
							if (a.getCantDisponible().intValue() > cantADescontar
									.intValue()) {
								Integer cantidad = a.getCantDisponible()
										.intValue() - cantADescontar.intValue();
								a.setCantDisponible(cantidad);
								em.merge(a);
								cantADescontar = cantADescontar - cantidad;
							} else {
								Integer cantidad = cantADescontar;
								a.setCantDisponible(cantidad);
								em.merge(a);
								cantADescontar = cantADescontar - cantidad;
							}
						}
					}
					pago.setEstado(referenciaGrupo.getValorNum());
					pago.setTipo("GRUPO");

					em.persist(pago);

					for (ConcursoPuestoDet act : puestosActivos()) {
						PuestoConceptoPago puestoPago = new PuestoConceptoPago();
						puestoPago.setActivo(true);
						puestoPago.setAnho(pago.getAnho());
						puestoPago.setEstado(referenciaPuesto.getValorNum());
						puestoPago.setFechaAlta(new Date());
						puestoPago.setMonto(pago.getMonto());
						puestoPago.setObjCodigo(pago.getObjCodigo());
						puestoPago.setUsuAlta(usuarioLogueado
								.getCodigoUsuario());
						puestoPago.setPlantaCargoDet(act.getPlantaCargoDet());
						puestoPago.setCategoria(pago.getCategoria());
						em.persist(puestoPago);
					}

				}
			}

		} else {
			Referencias referenciaGrupo = new Referencias();
			referenciaGrupo = referenciasUtilFormController.getReferencia(
					"ESTADOS_CATEGORIA_GRUPO", "PENDIENTE");
			Referencias referenciaPuesto = new Referencias();
			referenciaPuesto = referenciasUtilFormController.getReferencia(
					"ESTADOS_CATEGORIA", "PENDIENTE");
			pago.setEstado(referenciaGrupo.getValorNum());
			pago.setTipo("GRUPO");
			em.persist(pago);
			for (ConcursoPuestoDet act : puestosActivos()) {
				PuestoConceptoPago puestoPago = new PuestoConceptoPago();
				puestoPago.setActivo(true);
				puestoPago.setAnho(pago.getAnho());
				puestoPago.setEstado(referenciaPuesto.getValorNum());
				puestoPago.setFechaAlta(new Date());
				puestoPago.setMonto(pago.getMonto());
				puestoPago.setObjCodigo(pago.getObjCodigo());
				puestoPago.setUsuAlta(usuarioLogueado.getCodigoUsuario());
				puestoPago.setPlantaCargoDet(act.getPlantaCargoDet());
				puestoPago.setCategoria(pago.getCategoria());
				em.persist(puestoPago);
			}
		}
	}

	private void guardarContratado(GrupoConceptoPago p) {
		Referencias referenciaGrupo = new Referencias();
		referenciaGrupo = referenciasUtilFormController.getReferencia(
				"ESTADOS_CATEGORIA_GRUPO", "RESERVADO");
		Referencias referenciaPuesto = new Referencias();
		referenciaPuesto = referenciasUtilFormController.getReferencia(
				"ESTADOS_CATEGORIA", "RESERVADO");
		p.setEstado(referenciaGrupo.getValorNum());
		p.setTipo("GRUPO");
		em.persist(p);

		for (ConcursoPuestoDet activo : puestosActivos()) {
			PuestoConceptoPago puestoPago = new PuestoConceptoPago();
			puestoPago.setActivo(true);
			puestoPago.setAnho(p.getAnho());
			puestoPago.setEstado(referenciaPuesto.getValorNum());
			puestoPago.setFechaAlta(new Date());
			puestoPago.setMonto(p.getMonto());
			puestoPago.setObjCodigo(p.getObjCodigo());
			puestoPago.setCategoria(p.getCategoria());
			puestoPago.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			puestoPago.setPlantaCargoDet(activo.getPlantaCargoDet());
			em.persist(puestoPago);
		}
	}

	public void guardarConceptoPago(GrupoConceptoPago p) {
		if (esContratadoOpermanente(concursoPuestoAgr.getIdConcursoPuestoAgr())
				.equalsIgnoreCase("PERMANENTE")) {
			if (p.getCategoria() == null)
				guardarPermanenteSinCategoria(p);
			else
				guardarPermanenteConCategoria(p);
		}
		if (esContratadoOpermanente(concursoPuestoAgr.getIdConcursoPuestoAgr())
				.equalsIgnoreCase("CONTRATADO")) {
			guardarContratado(p);
		}
	}

	public String updatedConceptoPago() {
		try {
			for (GrupoConceptoPago p : listaGrupoPago) {
				if (p.getIdGrupoConceptoPago() == null) {
					guardarConceptoPago(p);
				}
			}

			for (GrupoConceptoPago paux : listaGrupoPagoAux) {
				Boolean esta = false;
				for (GrupoConceptoPago p : listaGrupoPago) {
					if (p.getIdGrupoConceptoPago().equals(
							paux.getIdGrupoConceptoPago()))
						esta = true;
				}
				if (!esta) {
					inactivarGrupoConceptoPago(paux);

				}
			}
			em.flush();
			/*
			 * statusMessages.clear(); statusMessages.add(Severity.INFO,
			 * SeamResourceBundle.getBundle() .getString("GENERICO_MSG"));
			 */
			return "updated";
		} catch (Exception e) {
			statusMessages.add(Severity.ERROR, e.getMessage());
			return null;
		}
	}

	private void insertarPrimeraEntrada(PuestoConceptoPago pago) {
		listaGrupoPago = new ArrayList<GrupoConceptoPago>();
		listaGrupoPagoAux = new ArrayList<GrupoConceptoPago>();
		Referencias referencias = new Referencias();
		if (!concurso.getAdReferendum())
			referencias = referenciasUtilFormController.getReferencia(
					"ESTADOS_CATEGORIA_GRUPO", "RESERVADO");
		if (concurso.getAdReferendum())
			referencias = referenciasUtilFormController.getReferencia(
					"ESTADOS_CATEGORIA_GRUPO", "PENDIENTE");
		try {
			GrupoConceptoPago grupoConceptoPago = new GrupoConceptoPago();
			grupoConceptoPago.setAnho(pago.getAnho());
			grupoConceptoPago.setCategoria(pago.getCategoria());
			grupoConceptoPago.setConcursoPuestoAgr(concursoPuestoAgr);
			grupoConceptoPago.setEstado(referencias.getValorNum());
			grupoConceptoPago.setFechaAlta(new Date());
			grupoConceptoPago.setMonto(pago.getMonto());
			grupoConceptoPago.setObjCodigo(pago.getObjCodigo());
			grupoConceptoPago.setTipo("GRUPO");
			grupoConceptoPago.setActivo(true);
			grupoConceptoPago.setUsuAlta(usuarioLogueado.getCodigoUsuario());

			em.persist(grupoConceptoPago);
			em.flush();
			listaGrupoPago.add(grupoConceptoPago);
			listaGrupoPagoAux.add(grupoConceptoPago);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void inactivarGrupoConceptoPago(GrupoConceptoPago gr) {
		gr.setActivo(false);
		gr.setFechaMod(new Date());
		gr.setUsuMod(usuarioLogueado.getCodigoUsuario());
		em.merge(gr);
		Query q = em
				.createQuery("select pago from PuestoConceptoPago pago "
						+ " where pago.activo is true and pago.objCodigo = :cod and pago.monto = :monto ");
		q.setParameter("cod", gr.getObjCodigo());
		q.setParameter("monto", gr.getMonto());
		List<PuestoConceptoPago> listaPuestoPago = q.getResultList();
		for (PuestoConceptoPago pago : listaPuestoPago) {
			if (pago.getCategoria() == null
					|| pago.getCategoria().trim().isEmpty()) {
				pago.setActivo(false);
				pago.setUsuMod(usuarioLogueado.getCodigoUsuario());
				pago.setFechaMod(new Date());
				em.merge(pago);
			}

		}

	}

	private void cargarConceptoPago() {

		if (listaGrupoPago.size() == 0 && idSinAnx == null
				&& idSinCategoria == null)
			buscarDatos();
		else {
			idSinAnx = null;
			idSinCategoria = null;
		}

		if (esContratadoOpermanente(concursoPuestoAgr.getIdConcursoPuestoAgr())
				.equalsIgnoreCase("PERMANENTE")
				&& !concurso.getDatosEspecificosTipoConc().getDescripcion()
						.equalsIgnoreCase(CONCURSO_INTERNO_INTERINSTITUCIONAL)) {
			if (esPrimeraEntrada()) {
				// /copia
				PlantaCargoDet planta = new PlantaCargoDet();
				planta = obtenerPlantaCargoDet(concursoPuestoAgr
						.getIdConcursoPuestoAgr());
				if (planta != null) {
					PuestoConceptoPago pago = new PuestoConceptoPago();
					pago = obtenerPuestoConceptoPago(planta);
					if (pago != null)
						insertarPrimeraEntrada(pago);
				}
			}
		}

		updatedConceptoPago();
	}

	/* >>>>>>>>>>>>>>>>>>>>>>>>Cargar Funciones<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< */
	List<DetContenidoFuncional> listaAux = new ArrayList<DetContenidoFuncional>();
	private List<ContenidoFuncionalDTO> listaFuncionesDTO = new ArrayList<ContenidoFuncionalDTO>();
	List<DetDescripcionContFuncional> listaDetInactivar = new ArrayList<DetDescripcionContFuncional>();
	@In(create = true)
	DetContenidoFuncionalHome detContenidoFuncionalHome;

	@SuppressWarnings("unchecked")
	private List<DetDescripcionContFuncional> buscarDescripcion(Long id) {
		String sql = "select descr.* "
				+ "from planificacion.det_descripcion_cont_funcional descr "
				+ "join planificacion.det_contenido_funcional det "
				+ "on det.id_det_contenido_funcional = descr.id_contenido_funcional "
				+ "where descr.activo is true and det.id_det_contenido_funcional = "
				+ id;
		List<DetDescripcionContFuncional> lista = new ArrayList<DetDescripcionContFuncional>();

		lista = em.createNativeQuery(sql, DetDescripcionContFuncional.class)
				.getResultList();
		return lista;
	}

	private List<DetContenidoFuncional> cargarContenidoFuncionalDelCpt() {
		ConcursoPuestoAgr concursoPuestoAgr = em.find(ConcursoPuestoAgr.class,
				this.concursoPuestoAgr.getIdConcursoPuestoAgr());
		List<DetContenidoFuncional> nuevaLista = new ArrayList<DetContenidoFuncional>();
		if (concursoPuestoAgr.getConcursoPuestoDets() != null
				&& concursoPuestoAgr.getConcursoPuestoDets().size() > 0) {
			ConcursoPuestoDet concursoPuestoDet = concursoPuestoAgr
					.getConcursoPuestoDets().get(0);
			Cpt cpt = concursoPuestoDet.getPlantaCargoDet().getCpt();

			String cad = "select * from planificacion.det_contenido_funcional det "
					+ " where det.id_cpt = "
					+ cpt.getId()
					+ " and det.activo= true";

			List<DetContenidoFuncional> lista = new ArrayList<DetContenidoFuncional>();
			lista = em.createNativeQuery(cad, DetContenidoFuncional.class)
					.getResultList();

			if (lista != null) {
				for (DetContenidoFuncional detContenidoFuncional : lista) {
					DetContenidoFuncional nuevo = new DetContenidoFuncional();
					nuevo.setActivo(false);
					nuevo.setConcursoPuestoAgr(concursoPuestoAgr);
					nuevo.setContenidoFuncional(detContenidoFuncional
							.getContenidoFuncional());
					nuevo.setPuntaje(detContenidoFuncional.getPuntaje());
					nuevo.setTipo("GRUPO");

					// Se copian los detalles
					List<DetDescripcionContFuncional> listaDesc = buscarDescripcion(detContenidoFuncional
							.getIdDetContenidoFuncional());
					if (listaDesc != null) {
						List<DetDescripcionContFuncional> listaDetalles = new ArrayList<DetDescripcionContFuncional>();
						for (DetDescripcionContFuncional detDescripcionContFuncional : listaDesc) {
							DetDescripcionContFuncional nuevoDetalle = new DetDescripcionContFuncional();
							nuevoDetalle.setActivo(detDescripcionContFuncional
									.getActivo());
							nuevoDetalle
									.setDescripcion(detDescripcionContFuncional
											.getDescripcion());
							nuevoDetalle.setDetContenidoFuncional(nuevo);
							listaDetalles.add(nuevoDetalle);

						}
						nuevo.setDetDescripcionContFuncionals(listaDetalles);
					}

					nuevaLista.add(nuevo);
				}
			}
		}

		return nuevaLista;
	}

	private void buscarDatosAsociadosUsuario() {
		if (usuarioLogueado.getConfiguracionUoCab() != null) {

			configuracionUoCab = new ConfiguracionUoCab();
			Long id = usuarioLogueado.getConfiguracionUoCab()
					.getIdConfiguracionUo();
			configuracionUoCab = em.find(ConfiguracionUoCab.class, id);
			Entidad entidad = new Entidad();
			if (configuracionUoCab.getEntidad() != null)
				entidad = configuracionUoCab.getEntidad();
			sinEntidad = entidad.getSinEntidad();
			nivelEntidad.setNenCodigo(sinEntidad.getNenCodigo());
			findNivelEntidad();
		}
	}

	public void findNivelEntidad() {
		if (nivelEntidad.getNenCodigo() != null) {
			sinNivelEntidadList.getSinNivelEntidad().setNenCodigo(
					nivelEntidad.getNenCodigo());
			nivelEntidad = sinNivelEntidadList.nivelEntidadMaxAnho();
		} else
			nivelEntidad = new SinNivelEntidad();
	}

	@SuppressWarnings("unchecked")
	private void buscarContenidoFuncional() {
		String cad = "select * from planificacion.det_contenido_funcional cont_funcional"
				+ " where cont_funcional.activo is true and cont_funcional.id_concurso_puesto_agr = "
				+ concursoPuestoAgr.getIdConcursoPuestoAgr()
				+ " and cont_funcional.tipo = 'GRUPO'";
		listaAux = new ArrayList<DetContenidoFuncional>();

		listaAux = em.createNativeQuery(cad, DetContenidoFuncional.class)
				.getResultList();

		// Si no hay datos del grupo, se debe buscar por el CPT
		if (listaAux == null || listaAux.size() == 0) {
			listaAux = cargarContenidoFuncionalDelCpt();
		}

		String cadena = "select * from planificacion.contenido_funcional funcional"
				+ " where funcional.activo = TRUE order by funcional.orden";
		listaFuncionesDTO = new ArrayList<ContenidoFuncionalDTO>();
		List<ContenidoFuncional> listaDto = new ArrayList<ContenidoFuncional>();

		listaDto = em.createNativeQuery(cadena, ContenidoFuncional.class)
				.getResultList();

		for (ContenidoFuncional contenido : listaDto) {
			Boolean esta = false;
			for (DetContenidoFuncional det : listaAux) {
				if (det.getContenidoFuncional().getIdContenidoFuncional()
						.equals(contenido.getIdContenidoFuncional())) {
					esta = true;
					ContenidoFuncionalDTO dto = new ContenidoFuncionalDTO();
					dto.setContenidoFuncional(det.getContenidoFuncional());
					dto.setId(det.getIdDetContenidoFuncional());

					dto.setPuntaje(det.getPuntaje());

					List<DetDescripcionContFuncional> listaDesc = new ArrayList<DetDescripcionContFuncional>();

					if (det.getIdDetContenidoFuncional() == null
							&& det.getDetDescripcionContFuncionals() != null)
						listaDesc = det.getDetDescripcionContFuncionals();// Nuevo
					else
						listaDesc = buscarDescripcion(det
								.getIdDetContenidoFuncional());// Edicion

					DetDescripcionContFuncional descr = new DetDescripcionContFuncional();
					descr.setActivo(true);
					listaDesc.add(descr);

					dto.setListaDetDescripContFuncional(listaDesc);
					listaFuncionesDTO.add(dto);
				}
			}
			if (!esta) {
				if (!buscarEnListaFunciones(contenido)) {
					ContenidoFuncionalDTO dto = new ContenidoFuncionalDTO();
					dto.setContenidoFuncional(contenido);
					List<DetDescripcionContFuncional> listaDesc = new ArrayList<DetDescripcionContFuncional>();

					DetDescripcionContFuncional descr = new DetDescripcionContFuncional();
					descr.setActivo(true);
					listaDesc.add(descr);
					dto.setListaDetDescripContFuncional(listaDesc);
					listaFuncionesDTO.add(dto);
				}
			}
		}
	}

	private Boolean buscarEnListaFunciones(ContenidoFuncional conten) {
		for (ContenidoFuncionalDTO ctn : listaFuncionesDTO) {
			if (ctn.getContenidoFuncional().getIdContenidoFuncional()
					.equals(conten.getIdContenidoFuncional()))
				return true;
		}
		return false;
	}

	String mensaje;

	public Boolean verificarValoracion(ContenidoFuncionalDTO contenido) {
		List<ValorNivelOrg> v = new ArrayList<ValorNivelOrg>();
		v = contenido.getContenidoFuncional().getValorNivelOrgs();
		Float valor = contenido.getPuntaje();
		Boolean cumple = false;
		for (ValorNivelOrg o : v) {
			Float desde = new Float("" + o.getDesde());
			Float hasta = new Float("" + o.getHasta());
			if (desde.floatValue() <= valor.floatValue()
					&& hasta.floatValue() >= valor.floatValue()) {
				cumple = true;
			}
		}
		if (!cumple) {
			mensaje = "El puntaje ingresado no es valido, verifique la escala";
			return cumple;
		}
		mensaje = null;
		return cumple;
	}

	private Boolean validacionEscala() {
		for (ContenidoFuncionalDTO dto : listaFuncionesDTO) {
			if (dto.getPuntaje() != null) {
				if (!verificarValoracion(dto))
					return false;
			}
		}
		return true;
	}

	private String updatedFuncion() {
		if (!validacionEscala())
			return null;
		try {
			for (ContenidoFuncionalDTO dto : listaFuncionesDTO) {
				if (dto.getId() != null) {
					DetContenidoFuncional detContenido = new DetContenidoFuncional();
					detContenido = em.find(DetContenidoFuncional.class,
							dto.getId());
					detContenido.setPuntaje(dto.getPuntaje());

					detContenidoFuncionalHome.setInstance(detContenido);
					String resultado = detContenidoFuncionalHome.update();
					if (resultado.equals("updated")) {
						List<DetDescripcionContFuncional> listaDescripcion = new ArrayList<DetDescripcionContFuncional>();
						listaDescripcion = dto
								.getListaDetDescripContFuncional();
						for (DetDescripcionContFuncional desc : listaDescripcion) {
							if (desc.getDescripcion() != null
									&& !desc.getDescripcion().equals("")) {
								if (desc.getIdDetDescripcionContFuncional() == null) {
									desc.setDescripcion(desc.getDescripcion()
											.trim().toUpperCase());
									desc.setDetContenidoFuncional(detContenidoFuncionalHome
											.getInstance());
									em.persist(desc);

								} else {
									desc.setDescripcion(desc.getDescripcion()
											.trim().toUpperCase());
									desc.setDetContenidoFuncional(detContenidoFuncionalHome
											.getInstance());
									em.merge(desc);

								}
							}
						}
					}
				} else {
					if (dto.getPuntaje() != null) {
						DetContenidoFuncional detContenido = new DetContenidoFuncional();
						detContenido.setContenidoFuncional(dto
								.getContenidoFuncional());
						detContenido.setPuntaje(dto.getPuntaje());
						detContenido.setTipo("GRUPO");
						detContenido.setActivo(true);
						detContenido.setConcursoPuestoAgr(concursoPuestoAgr);
						detContenidoFuncionalHome.setInstance(detContenido);
						String resultado = detContenidoFuncionalHome.persist();
						if (resultado.equals("persisted")) {
							List<DetDescripcionContFuncional> listaDescripcion = new ArrayList<DetDescripcionContFuncional>();
							listaDescripcion = dto
									.getListaDetDescripContFuncional();
							for (DetDescripcionContFuncional desc : listaDescripcion) {
								if (desc.getDescripcion() != null
										&& !desc.getDescripcion().equals("")) {
									desc.setDescripcion(desc.getDescripcion()
											.trim().toUpperCase());
									desc.setDetContenidoFuncional(detContenidoFuncionalHome
											.getInstance());
									em.persist(desc);
								}
							}
							em.flush();
						}
					}
				}
				detContenidoFuncionalHome.clearInstance();
			}
			for (DetDescripcionContFuncional detail : listaDetInactivar) {
				detail.setActivo(false);
				em.merge(detail);
			}

			em.flush();
			/*
			 * statusMessages.clear(); statusMessages.add(Severity.INFO,
			 * SeamResourceBundle.getBundle() .getString("GENERICO_MSG"));
			 */
			return "updated";
		} catch (Exception e) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, e.getMessage());
			return null;
		}
	}

	private void cargarFuncion() {
		validarOee(concurso);
		// habEdit();
		buscarDatosAsociadosUsuario();
		buscarContenidoFuncional();
		habilitarPantalla();
		updatedFuncion();
	}

	private void validarOee(Concurso concurso) {
		if (seguridadUtilFormController == null) {
			seguridadUtilFormController = (SeguridadUtilFormController) Component
					.getInstance(SeguridadUtilFormController.class, true);
		}
		if (concurso == null) {
			throw new javax.persistence.EntityNotFoundException();
		}

		if (!seguridadUtilFormController.verificarPerteneceOee(concurso
				.getConfiguracionUoCab().getIdConfiguracionUo())) {
			throw new AuthorizationException(SeamResourceBundle.getBundle()
					.getString("GENERICO_OEE_NO_VALIDA"));
		}
	}

	/* >>>>>>>>>>>>>>>>>>>>>>>>Cargar Requisitos<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< */
	private List<RequisitosMinimosDTO> listaRequisitosDTO = new ArrayList<RequisitosMinimosDTO>();
	private List<DetReqMin> listaRequisitosAux = new ArrayList<DetReqMin>();
	@In(create = true)
	DetReqMinHome detReqMinHome;
	private List<DetMinimosRequeridos> listaDetMinimosInactivar = new ArrayList<DetMinimosRequeridos>();
	private List<DetOpcionesConvenientes> listaDetOpcInactivar = new ArrayList<DetOpcionesConvenientes>();

	private void cargarRequisito() {

		buscarDatosAsociadosUsuario();
		listaRequisitosDTO = new ArrayList<RequisitosMinimosDTO>();
		listaRequisitosAux = new ArrayList<DetReqMin>();
		buscarRequerimientosMinimos();
		habilitarPantalla();
		updatedRequisitos();
	}

	@SuppressWarnings({ "unchecked", "unused" })
	private void buscarRequerimientosMinimos() {
		String cad = "select * from planificacion.det_req_min det_req"
				+ " where det_req.activo is true and det_req.id_concurso_puesto_agr = "
				+ concursoPuestoAgr.getIdConcursoPuestoAgr()
				+ " and det_req.tipo = 'GRUPO'";
		listaRequisitosAux = new ArrayList<DetReqMin>();
		listaRequisitosAux = em.createNativeQuery(cad, DetReqMin.class)
				.getResultList();

		// Si no hay datos del grupo, se debe buscar por el CPT
		if (listaRequisitosAux == null || listaRequisitosAux.size() == 0) {
			listaRequisitosAux = cargarRequerimientosMinimosDelCpt();
		}

		String cadena = "select * from planificacion.requisito_minimo_cpt cpt"
				+ " where cpt.activo = TRUE order by cpt.orden";

		List<RequisitoMinimoCpt> lista = new ArrayList<RequisitoMinimoCpt>();
		lista = em.createNativeQuery(cadena, RequisitoMinimoCpt.class)
				.getResultList();
		listaRequisitosDTO = new ArrayList<RequisitosMinimosDTO>();
		for (RequisitoMinimoCpt req : lista) {
			Boolean esta = false;
			Boolean agregado = false;
			Long id2 = req.getIdRequisitoMinimoCpt();
			Integer i = 0;
			while (i < listaRequisitosAux.size()) {
				DetReqMin det = new DetReqMin();
				det = listaRequisitosAux.get(i);
				Long id1 = det.getRequisitoMinimoCpt()
						.getIdRequisitoMinimoCpt();
				if (id1.equals(id2)) {
					esta = true;
					RequisitosMinimosDTO dto = new RequisitosMinimosDTO();
					dto.setRequisitoMinimoCpt(req);
					dto.setId(det.getIdDetReqMin());
					dto.setPuntaje(det.getPuntajeReqMin());
					List<DetOpcionesConvenientes> listaConv = new ArrayList<DetOpcionesConvenientes>();

					if (det.getIdDetReqMin() == null
							&& det.getDetOpcionesConvenienteses() != null)
						listaConv = det.getDetOpcionesConvenienteses();// Nuevo
					else
						listaConv = buscarOpciones(det.getIdDetReqMin());// Edicion

					Integer tamconv = listaConv.size();
					// if (!agregado) {
					agregado = true;
					DetOpcionesConvenientes conv = new DetOpcionesConvenientes();
					conv.setActivo(true);
					listaConv.add(conv);
					dto.setListaDetOpcionesConvenientes(listaConv);

					List<DetMinimosRequeridos> listaReq = new ArrayList<DetMinimosRequeridos>();

					if (det.getIdDetReqMin() == null
							&& det.getDetMinimosRequeridoses() != null)
						listaReq = det.getDetMinimosRequeridoses();// Nuevo
					else
						listaReq = buscarMinimos(det.getIdDetReqMin());// Edicion

					Integer tamreqmin = listaReq.size();

					DetMinimosRequeridos r = new DetMinimosRequeridos();
					r.setActivo(true);
					listaReq.add(r);
					dto.setListaDetMinimosRequeridos(listaReq);
					// }
					listaRequisitosDTO.add(dto);
					// i = listaRequisitosAux.size();
				}
				i++;
			}

			if (!esta) {
				RequisitosMinimosDTO dto = new RequisitosMinimosDTO();
				dto.setRequisitoMinimoCpt(req);
				List<DetOpcionesConvenientes> listaConv = new ArrayList<DetOpcionesConvenientes>();
				DetOpcionesConvenientes conv = new DetOpcionesConvenientes();
				conv.setActivo(true);
				listaConv.add(conv);
				dto.setListaDetOpcionesConvenientes(listaConv);
				List<DetMinimosRequeridos> listaReq = new ArrayList<DetMinimosRequeridos>();
				DetMinimosRequeridos r = new DetMinimosRequeridos();
				r.setActivo(true);
				listaReq.add(r);
				dto.setListaDetMinimosRequeridos(listaReq);
				listaRequisitosDTO.add(dto);
			}
		}
	}

	private List<DetReqMin> cargarRequerimientosMinimosDelCpt() {
		ConcursoPuestoAgr concursoPuestoAgr = em.find(ConcursoPuestoAgr.class,
				this.concursoPuestoAgr.getIdConcursoPuestoAgr());
		List<DetReqMin> listaAux = new ArrayList<DetReqMin>();
		if (concursoPuestoAgr.getConcursoPuestoDets() != null
				&& concursoPuestoAgr.getConcursoPuestoDets().size() > 0) {
			ConcursoPuestoDet concursoPuestoDet = concursoPuestoAgr
					.getConcursoPuestoDets().get(0);
			Cpt cpt = concursoPuestoDet.getPlantaCargoDet().getCpt();

			String cad = "select * from planificacion.det_req_min det "
					+ " where det.id_cpt = " + cpt.getId()
					+ " and det.activo= true";

			List<DetReqMin> lista = new ArrayList<DetReqMin>();
			lista = em.createNativeQuery(cad, DetReqMin.class).getResultList();

			if (lista != null) {
				for (DetReqMin detReqMin : lista) {
					DetReqMin nuevo = new DetReqMin();
					nuevo.setActivo(true);
					nuevo.setConcursoPuestoAgr(concursoPuestoAgr);
					nuevo.setRequisitoMinimoCpt(detReqMin
							.getRequisitoMinimoCpt());
					nuevo.setPuntajeReqMin(detReqMin.getPuntajeReqMin());
					nuevo.setTipo("GRUPO");

					// Se copian los detalles de minimos
					List<DetMinimosRequeridos> listaDesc = buscarMinimos(detReqMin
							.getIdDetReqMin());
					if (listaDesc != null) {
						List<DetMinimosRequeridos> listaDetalles = new ArrayList<DetMinimosRequeridos>();
						for (DetMinimosRequeridos detMinimosRequeridos : listaDesc) {
							DetMinimosRequeridos nuevoDetalle = new DetMinimosRequeridos();
							nuevoDetalle.setActivo(detMinimosRequeridos
									.getActivo());
							nuevoDetalle
									.setMinimosRequeridos(detMinimosRequeridos
											.getMinimosRequeridos());
							nuevoDetalle.setDetReqMin(nuevo);
							listaDetalles.add(nuevoDetalle);

						}
						nuevo.setDetMinimosRequeridoses(listaDetalles);
					}

					// Se copian los detalles de opciones convenientes
					List<DetOpcionesConvenientes> listaOpciones = buscarOpciones(detReqMin
							.getIdDetReqMin());
					if (listaDesc != null) {
						List<DetOpcionesConvenientes> listaDetalles = new ArrayList<DetOpcionesConvenientes>();
						for (DetOpcionesConvenientes detOpcionesConvenientes : listaOpciones) {
							DetOpcionesConvenientes nuevoDetalle = new DetOpcionesConvenientes();
							nuevoDetalle.setActivo(detOpcionesConvenientes
									.getActivo());
							nuevoDetalle
									.setOpcConvenientes(detOpcionesConvenientes
											.getOpcConvenientes());
							nuevoDetalle.setDetReqMin(nuevo);
							listaDetalles.add(nuevoDetalle);

						}
						nuevo.setDetOpcionesConvenienteses(listaDetalles);
					}

					listaAux.add(nuevo);
				}
			}
		}

		return listaAux;
	}

	@SuppressWarnings("unchecked")
	private List<DetOpcionesConvenientes> buscarOpciones(Long id) {
		String cad = "select opc.* from planificacion.det_opciones_convenientes opc"
				+ " where opc.activo is true and opc.id_det_req_min = " + id;

		return em.createNativeQuery(cad, DetOpcionesConvenientes.class)
				.getResultList();
	}

	@SuppressWarnings("unchecked")
	private List<DetMinimosRequeridos> buscarMinimos(Long id) {
		String cad = "select min.* from planificacion.det_minimos_requeridos min"
				+ " where min.activo is true and min.id_det_req_min = " + id;

		return em.createNativeQuery(cad, DetMinimosRequeridos.class)
				.getResultList();
	}

	private String updatedRequisitos() {
		if (!validacionEscala())
			return null;
		try {
			for (RequisitosMinimosDTO dto : listaRequisitosDTO) {
				if (dto.getId() != null) {
					DetReqMin detReqMin = new DetReqMin();
					detReqMin = em.find(DetReqMin.class, dto.getId());
					detReqMin.setPuntajeReqMin(dto.getPuntaje());
					em.merge(detReqMin);
					detReqMinHome.setInstance(detReqMin);
					// String resultado = detReqMinHome.update();

					List<DetMinimosRequeridos> listaMinimos = new ArrayList<DetMinimosRequeridos>();
					listaMinimos = dto.getListaDetMinimosRequeridos();
					for (DetMinimosRequeridos min : listaMinimos) {
						if (min.getMinimosRequeridos() != null
								&& !min.getMinimosRequeridos().equals("")) {
							min.setMinimosRequeridos(min.getMinimosRequeridos()
									.trim().toUpperCase());
							min.setDetReqMin(detReqMinHome.getInstance());
							if (min.getIdMinimosRequeridos() == null)
								em.persist(min);
							else
								min = em.merge(min);
							// em.flush();
						}
					}

					List<DetOpcionesConvenientes> listaOpc = new ArrayList<DetOpcionesConvenientes>();
					listaOpc = dto.getListaDetOpcionesConvenientes();
					for (DetOpcionesConvenientes op : listaOpc) {

						if (op.getOpcConvenientes() != null
								&& !op.getOpcConvenientes().equals("")) {
							op.setOpcConvenientes(op.getOpcConvenientes()
									.trim().toUpperCase());
							op.setDetReqMin(detReqMinHome.getInstance());
							if (op.getIdDetOpcionesConvenientes() == null)
								em.persist(op);
							else
								em.merge(op);
						}

					}
					// em.flush();

				} else {
					if (dto.getPuntaje() != null) {
						DetReqMin detMin = new DetReqMin();
						detMin.setRequisitoMinimoCpt(dto
								.getRequisitoMinimoCpt());
						detMin.setConcursoPuestoAgr(concursoPuestoAgr);
						detMin.setTipo("GRUPO");
						detMin.setActivo(true);
						detMin.setPuntajeReqMin(dto.getPuntaje());

						em.persist(detMin);
						detReqMinHome.setInstance(detMin);
						// String resultado = detReqMinHome.persist();

						listaRequisitosAux.add(detReqMinHome.getInstance());
						// if (resultado.equals("persisted")) {
						List<DetMinimosRequeridos> listaDetMinimos = new ArrayList<DetMinimosRequeridos>();
						listaDetMinimos = dto.getListaDetMinimosRequeridos();
						for (DetMinimosRequeridos dMin : listaDetMinimos) {
							if (dMin.getMinimosRequeridos() != null
									&& !dMin.getMinimosRequeridos().equals("")) {
								dMin.setMinimosRequeridos(dMin
										.getMinimosRequeridos().trim()
										.toUpperCase());
								dMin.setDetReqMin(detReqMinHome.getInstance());

								em.persist(dMin);
							}
						}
						// em.flush();
						List<DetOpcionesConvenientes> listaDetOpc = new ArrayList<DetOpcionesConvenientes>();
						listaDetOpc = dto.getListaDetOpcionesConvenientes();
						for (DetOpcionesConvenientes detOpc : listaDetOpc) {
							if (detOpc.getOpcConvenientes() != null
									&& !detOpc.getOpcConvenientes().equals("")) {
								detOpc.setOpcConvenientes(detOpc
										.getOpcConvenientes().trim()
										.toUpperCase());
								detOpc.setDetReqMin(detReqMinHome.getInstance());
								em.persist(detOpc);
							}
						}
						// em.flush();
						// }
					}
					detReqMinHome.clearInstance();
				}
			}

			for (DetMinimosRequeridos dMin : listaDetMinimosInactivar) {
				dMin.setActivo(false);
				em.merge(dMin);
			}

			for (DetOpcionesConvenientes detOpc : listaDetOpcInactivar) {
				detOpc.setActivo(false);
				em.merge(detOpc);
			}

			em.flush();
			/*
			 * statusMessages.clear(); statusMessages.add(Severity.INFO,
			 * SeamResourceBundle.getBundle() .getString("GENERICO_MSG"));
			 */
			return "updated";
		} catch (Exception e) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, e.getMessage());
			return null;
		}
	}

	/*
	 * >>>>>>>>>>>>>>>>>>>>>>>>Cargar Condiciones de Trabajo
	 * Generales<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
	 */
	private List<CondicionTrabajoDTO> listaCondicionDto = new ArrayList<CondicionTrabajoDTO>();
	private String fromCU = null;

	private void cargarCondicionesTrabajo() {
		buscarCondicionTrabajoEdit();
		guardarCondicionesTrabajo();
	}

	private List<DetCondicionTrabajo> cargarCondicionesdelPuestoTrabajo() {
		ConcursoPuestoAgr concursoPuestoAgr = em.find(ConcursoPuestoAgr.class,
				idConcursoPuestoAgr);
		List<DetCondicionTrabajo> listaLink8Aux = new ArrayList<DetCondicionTrabajo>();
		if (concursoPuestoAgr.getConcursoPuestoDets() != null
				&& concursoPuestoAgr.getConcursoPuestoDets().size() > 0) {
			ConcursoPuestoDet concursoPuestoDet = concursoPuestoAgr
					.getConcursoPuestoDets().get(0);
			// Cpt cpt = concursoPuestoDet.getPlantaCargoDet().getCpt();

			String cad = "select * from planificacion.det_condicion_trabajo det_trab "
					+ " where det_trab.id_planta_cargo_det = "
					+ concursoPuestoDet.getPlantaCargoDet()
							.getIdPlantaCargoDet()
					+ " and det_trab.activo= true";

			List<DetCondicionTrabajo> lista = new ArrayList<DetCondicionTrabajo>();
			lista = em.createNativeQuery(cad, DetCondicionTrabajo.class)
					.getResultList();

			if (lista != null) {
				for (DetCondicionTrabajo detCondicionTrabajo : lista) {
					DetCondicionTrabajo nuevo = new DetCondicionTrabajo();
					nuevo.setActivo(false);
					nuevo.setConcursoPuestoAgr(concursoPuestoAgr);
					nuevo.setCondicionTrabajo(detCondicionTrabajo
							.getCondicionTrabajo());
					nuevo.setPuntajeCondTrab(detCondicionTrabajo
							.getPuntajeCondTrab());
					nuevo.setTipo("GRUPO");
					listaLink8Aux.add(nuevo);
				}
			}
		}

		return listaLink8Aux;
	}

	@SuppressWarnings("unchecked")
	private void buscarCondicionTrabajoEdit() {
		String cad = "select * from planificacion.det_condicion_trabajo det_trab "
				+ " where det_trab.id_concurso_puesto_agr = "
				+ idConcursoPuestoAgr;

		List<DetCondicionTrabajo> listaLink8Aux = new ArrayList<DetCondicionTrabajo>();
		listaLink8Aux = em.createNativeQuery(cad, DetCondicionTrabajo.class)
				.getResultList();

		// Si no hay datos del grupo, se debe buscar por el Puesto de Trabajo
		if (listaLink8Aux == null || listaLink8Aux.size() == 0) {
			listaLink8Aux = cargarCondicionesdelPuestoTrabajo();
		}

		String cadena = "select * from planificacion.condicion_trabajo cond "
				+ " where cond.activo is true order by cond.orden";

		List<CondicionTrabajo> lista = new ArrayList<CondicionTrabajo>();
		lista = em.createNativeQuery(cadena, CondicionTrabajo.class)
				.getResultList();
		listaCondicionDto = new ArrayList<CondicionTrabajoDTO>();
		for (CondicionTrabajo condicion : lista) {
			Boolean esta = false;
			for (DetCondicionTrabajo det : listaLink8Aux) {
				if (det.getCondicionTrabajo().getIdCondicionTrabajo()
						.equals(condicion.getIdCondicionTrabajo())) {
					esta = true;
					CondicionTrabajoDTO dto = new CondicionTrabajoDTO();
					dto.setCondicionTrabajo(det.getCondicionTrabajo());
					dto.setId(det.getIdDetCondiconTrabajo());
					dto.setActivo(det.getActivo());

					dto.setPuntaje(det.getPuntajeCondTrab());
					listaCondicionDto.add(dto);
				}
			}
			if (!esta) {
				CondicionTrabajoDTO dto = new CondicionTrabajoDTO();
				dto.setCondicionTrabajo(condicion);
				listaCondicionDto.add(dto);
				System.out.println("#### " + condicion.getDescripcion());
			}
		}
	}

	private String guardarCondicionesTrabajo() {
		if (!validacionEscala())
			return null;
		try {
			String mensaje = "";
			for (CondicionTrabajoDTO dto : listaCondicionDto) {
				if (dto.getPuntaje() != null) {
					if (dto.getId() == null) {
						DetCondicionTrabajo detCondTrab = new DetCondicionTrabajo();
						detCondTrab.setActivo(dto.getActivo());
						detCondTrab.setCondicionTrabajo(dto
								.getCondicionTrabajo());
						detCondTrab.setPuntajeCondTrab(dto.getPuntaje());
						detCondTrab.setTipo("GRUPO");

						ConcursoPuestoAgr concursoPuestoAgr = em.find(
								ConcursoPuestoAgr.class, idConcursoPuestoAgr);
						detCondTrab.setConcursoPuestoAgr(concursoPuestoAgr);
						em.persist(detCondTrab);
						/*
						 * mensaje = SeamResourceBundle.getBundle().getString(
						 * "GENERICO_MSG");
						 */
					} else {
						DetCondicionTrabajo detCondTrab = new DetCondicionTrabajo();
						detCondTrab = em.find(DetCondicionTrabajo.class,
								dto.getId());
						if (fromCU != null
								&& fromCU
										.equals("admPerfilMatrizHomologInstitucion_list")) {
							if (!detCondTrab.getPuntajeCondTrab().equals(
									dto.getPuntaje())) {
								detCondTrab.setActivo(false);
								em.merge(detCondTrab);
								DetCondicionTrabajo detCondTrabCopia = new DetCondicionTrabajo();// se
																									// crea
																									// una
																									// copia
																									// con
																									// la
																									// modificacion
								detCondTrabCopia.setActivo(dto.getActivo());
								detCondTrabCopia.setCondicionTrabajo(dto
										.getCondicionTrabajo());
								detCondTrabCopia.setPuntajeCondTrab(dto
										.getPuntaje());
								detCondTrabCopia.setTipo("GRUPO");
								ConcursoPuestoAgr concursoPuestoAgr = em.find(
										ConcursoPuestoAgr.class,
										idConcursoPuestoAgr);
								detCondTrabCopia
										.setConcursoPuestoAgr(concursoPuestoAgr);
								em.persist(detCondTrabCopia);
								/*
								 * mensaje = SeamResourceBundle.getBundle()
								 * .getString("GENERICO_MSG");
								 */
							}

						} else {
							detCondTrab.setActivo(dto.getActivo());
							detCondTrab.setPuntajeCondTrab(dto.getPuntaje());
							em.merge(detCondTrab);
							/*
							 * mensaje =
							 * SeamResourceBundle.getBundle().getString(
							 * "GENERICO_MSG");
							 */
						}
					}
					em.flush();
				} else {
					if (dto.getId() != null) {
						DetCondicionTrabajo detCondTrab = new DetCondicionTrabajo();
						detCondTrab = em.find(DetCondicionTrabajo.class,
								dto.getId());
						em.remove(detCondTrab);
						/*
						 * mensaje = SeamResourceBundle.getBundle().getString(
						 * "GENERICO_MSG");
						 */
					}
				}
				em.flush();

			}
			buscarCondicionTrabajoEdit();

			statusMessages.clear();
			if ("".equals(mensaje))
				statusMessages.add(Severity.ERROR,
						"No se detectaron cambios que guardar.");
			/*
			 * else statusMessages.add(Severity.INFO, mensaje);
			 */

			return "ok";
		} catch (Exception e) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, e.getMessage());
			return null;
		}
	}

	/*
	 * >>>>>>>>>>>>>>>>>>>>>>>>Cargar Condiciones de Trabajo
	 * Especifico<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
	 */
	private List<CondicionTrabajoEspecifDTO> listaDto = new ArrayList<CondicionTrabajoEspecifDTO>();

	private void cargarCondicionesTrabajoEspecifico() {
		buscarCondicionTrabajoEspecificaEdit();
		guardarCondicionesTrabajoEspecifico();
	}

	private List<DetCondicionTrabajoEspecif> cargarCondicionesDelPuestoTrabajoEspecif() {
		ConcursoPuestoAgr concursoPuestoAgr = em.find(ConcursoPuestoAgr.class,
				idConcursoPuestoAgr);
		List<DetCondicionTrabajoEspecif> nuevaLista = new ArrayList<DetCondicionTrabajoEspecif>();
		if (concursoPuestoAgr.getConcursoPuestoDets() != null
				&& concursoPuestoAgr.getConcursoPuestoDets().size() > 0) {
			ConcursoPuestoDet concursoPuestoDet = concursoPuestoAgr
					.getConcursoPuestoDets().get(0);
			// Cpt cpt = concursoPuestoDet.getPlantaCargoDet().getCpt();

			String cad = "select * from planificacion.det_condicion_trabajo_especif det_trab "
					+ " where det_trab.id_planta_cargo_det = "
					+ concursoPuestoDet.getPlantaCargoDet()
							.getIdPlantaCargoDet()
					+ " and det_trab.activo= true";

			List<DetCondicionTrabajoEspecif> lista = new ArrayList<DetCondicionTrabajoEspecif>();
			lista = em.createNativeQuery(cad, DetCondicionTrabajoEspecif.class)
					.getResultList();

			if (lista != null) {
				for (DetCondicionTrabajoEspecif detCondicionTrabajoEspecif : lista) {
					DetCondicionTrabajoEspecif nuevo = new DetCondicionTrabajoEspecif();
					nuevo.setActivo(false);
					nuevo.setConcursoPuestoAgr(concursoPuestoAgr);
					nuevo.setCondicionTrabajoEspecif(detCondicionTrabajoEspecif
							.getCondicionTrabajoEspecif());
					nuevo.setPuntajeCondTrabEspecif(detCondicionTrabajoEspecif
							.getPuntajeCondTrabEspecif());
					nuevo.setAjustes(detCondicionTrabajoEspecif.getAjustes());
					nuevo.setJustificacion(detCondicionTrabajoEspecif
							.getJustificacion());
					nuevo.setTipo("GRUPO");
					nuevaLista.add(nuevo);
				}
			}
		}

		return nuevaLista;
	}

	@SuppressWarnings("unchecked")
	private void buscarCondicionTrabajoEspecificaEdit() {
		String cad = "select * from planificacion.det_condicion_trabajo_especif det_trab"
				+ " where det_trab.id_concurso_puesto_agr = "
				+ idConcursoPuestoAgr + " and det_trab.activo=true";

		List<DetCondicionTrabajoEspecif> listaDet = new ArrayList<DetCondicionTrabajoEspecif>();
		listaDet = em.createNativeQuery(cad, DetCondicionTrabajoEspecif.class)
				.getResultList();

		/**
		 * Incidencia 0001747
		 * */
		// Si no hay datos del grupo, se debe buscar por el Puesto de Trabajo
		if (listaDet == null || listaDet.size() == 0) {
			listaDet = cargarCondicionesDelPuestoTrabajoEspecif();
		}

		/** fin **/

		String cadena = "select * from planificacion.condicion_trabajo_especif cond"
				+ " where cond.activo = TRUE order by cond.orden";
		List<CondicionTrabajoEspecif> lista = new ArrayList<CondicionTrabajoEspecif>();

		lista = em.createNativeQuery(cadena, CondicionTrabajoEspecif.class)
				.getResultList();
		listaDto = new ArrayList<CondicionTrabajoEspecifDTO>();
		for (CondicionTrabajoEspecif condicion : lista) {
			Boolean esta = false;
			for (DetCondicionTrabajoEspecif det : listaDet) {
				if (det.getCondicionTrabajoEspecif()
						.getIdCondicionesTrabajoEspecif()
						.equals(condicion.getIdCondicionesTrabajoEspecif())) {
					esta = true;
					CondicionTrabajoEspecifDTO dto = new CondicionTrabajoEspecifDTO();
					dto.setCondicionTrabajoEspecif(det
							.getCondicionTrabajoEspecif());
					dto.setId(det.getIdDetCondicionTrabajoEspecif());
					dto.setActivo(det.getActivo());
					dto.setAjustes(det.getAjustes());
					dto.setJustificacion(det.getJustificacion());
					dto.setPuntaje(det.getPuntajeCondTrabEspecif());
					listaDto.add(dto);
				}
			}
			if (!esta) {
				CondicionTrabajoEspecifDTO dto = new CondicionTrabajoEspecifDTO();
				dto.setCondicionTrabajoEspecif(condicion);
				listaDto.add(dto);
			}
		}
	}

	public void guardarCondicionesTrabajoEspecifico() {
		if (!validacionEscala())
			return;
		try {
			for (CondicionTrabajoEspecifDTO dto : listaDto) {
				if (dto.getPuntaje() != null && dto.getAjustes() != null
						&& dto.getJustificacion() != null) {
					if (dto.getId() == null) {
						DetCondicionTrabajoEspecif detCondTrab = new DetCondicionTrabajoEspecif();
						detCondTrab.setActivo(dto.getActivo());
						detCondTrab.setCondicionTrabajoEspecif(dto
								.getCondicionTrabajoEspecif());
						detCondTrab.setPuntajeCondTrabEspecif(dto.getPuntaje());
						detCondTrab.setAjustes(dto.getAjustes().trim()
								.toUpperCase());
						detCondTrab.setJustificacion(dto.getJustificacion()
								.trim().toUpperCase());

						detCondTrab.setTipo("GRUPO");
						ConcursoPuestoAgr concursoPuestoAgr = em.find(
								ConcursoPuestoAgr.class, idConcursoPuestoAgr);
						detCondTrab.setConcursoPuestoAgr(concursoPuestoAgr);

						em.persist(detCondTrab);
					} else {
						DetCondicionTrabajoEspecif detCondTrabBD = new DetCondicionTrabajoEspecif();
						detCondTrabBD = em.find(
								DetCondicionTrabajoEspecif.class, dto.getId());
						if (cu == null || !cu.equals("404")) {
							if (!detCondTrabBD.getPuntajeCondTrabEspecif()
									.equals(dto.getPuntaje())) {
								detCondTrabBD.setActivo(false);// se inactiva
								em.merge(detCondTrabBD);
								/** COPIA */
								DetCondicionTrabajoEspecif detCondTrabEspcCopia = new DetCondicionTrabajoEspecif();// se
																													// crea
																													// una
																													// copia
																													// con
																													// la
																													// modificacion
								detCondTrabEspcCopia.setActivo(true);
								detCondTrabEspcCopia
										.setCondicionTrabajoEspecif(dto
												.getCondicionTrabajoEspecif());
								detCondTrabEspcCopia
										.setPuntajeCondTrabEspecif(dto
												.getPuntaje());
								detCondTrabEspcCopia.setTipo("GRUPO");
								detCondTrabEspcCopia.setAjustes(dto
										.getAjustes().trim().toUpperCase());
								detCondTrabEspcCopia.setJustificacion(dto
										.getJustificacion().trim()
										.toUpperCase());
								ConcursoPuestoAgr concursoPuestoAgr = em.find(
										ConcursoPuestoAgr.class,
										idConcursoPuestoAgr);
								detCondTrabEspcCopia
										.setConcursoPuestoAgr(concursoPuestoAgr);
								em.persist(detCondTrabEspcCopia);
							}
						} else {
							detCondTrabBD.setActivo(dto.getActivo());
							detCondTrabBD.setPuntajeCondTrabEspecif(dto
									.getPuntaje());
							detCondTrabBD.setAjustes(dto.getAjustes().trim()
									.toUpperCase());
							detCondTrabBD.setJustificacion(dto
									.getJustificacion().trim().toUpperCase());
							em.merge(detCondTrabBD);
						}

					}
					em.flush();
				} else {
					if (dto.getId() != null) {
						DetCondicionTrabajoEspecif detCondTrab = new DetCondicionTrabajoEspecif();
						detCondTrab = em.find(DetCondicionTrabajoEspecif.class,
								dto.getId());
						em.remove(detCondTrab);
					}
				}
				em.flush();
			}
			buscarCondicionTrabajoEspecificaEdit();
			/*
			 * statusMessages.clear(); statusMessages.add(Severity.INFO,
			 * SeamResourceBundle.getBundle() .getString("GENERICO_MSG"));
			 */
			return;
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, e.getMessage());
			return;
		}
	}

	/*
	 * >>>>>>>>>>>>>>>>>>>>>>>>Cargar Condiciones de
	 * Seguridad<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
	 */
	private List<CondicionSeguridadDTO> listaCondicionDTO = new ArrayList<CondicionSeguridadDTO>();
	private List<DetCondicionSegur> listaCondicionAux = new ArrayList<DetCondicionSegur>();
	private String cu;// STRING CU RODRIGO

	private void cargarCondicionesSeguridad() {
		buscarDetCondicionSegur();
		guardarCondicionesSeguridad();
	}

	public String guardarCondicionesSeguridad() {
		if (!validacionEscala())
			return null;
		try {
			for (CondicionSeguridadDTO dto : listaCondicionDTO) {
				if (dto.getPuntaje() != null && dto.getAjustes() != null
						&& dto.getJustificacion() != null) {
					DetCondicionSegur det = new DetCondicionSegur();
					det.setActivo(dto.getActivo());
					det.setAjustes(dto.getAjustes().trim().toUpperCase());
					det.setCondicionSegur(dto.getCondicionSegur());
					det.setConcursoPuestoAgr(concursoPuestoAgr);
					det.setJustificacion(dto.getJustificacion().trim()
							.toUpperCase());
					det.setPuntajeCondSegur(dto.getPuntaje());
					det.setTipo("GRUPO");

					em.persist(det);
				}

			}
			em.flush();
			em.flush();
			/*
			 * statusMessages.clear(); statusMessages.add(Severity.INFO,
			 * SeamResourceBundle.getBundle() .getString("GENERICO_MSG"));
			 */
			return "persisted";

		} catch (Exception e) {
			statusMessages.add(Severity.ERROR, e.getMessage());
			return null;
		}
	}

	private List<DetCondicionSegur> cargarDetCondicionSegurDelPuestoTrabajo() {
		ConcursoPuestoAgr concursoPuestoAgr = em.find(ConcursoPuestoAgr.class,
				this.concursoPuestoAgr.getIdConcursoPuestoAgr());
		List<DetCondicionSegur> nuevaLista = new ArrayList<DetCondicionSegur>();
		if (concursoPuestoAgr.getConcursoPuestoDets() != null
				&& concursoPuestoAgr.getConcursoPuestoDets().size() > 0) {
			ConcursoPuestoDet concursoPuestoDet = concursoPuestoAgr
					.getConcursoPuestoDets().get(0);
			// Cpt cpt = concursoPuestoDet.getPlantaCargoDet().getCpt();

			String cad = "select * from planificacion.det_condicion_segur det_trab "
					+ " where det_trab.id_planta_cargo_det = "
					+ concursoPuestoDet.getPlantaCargoDet()
							.getIdPlantaCargoDet()
					+ " and det_trab.activo= true";

			List<DetCondicionSegur> lista = new ArrayList<DetCondicionSegur>();
			lista = em.createNativeQuery(cad, DetCondicionSegur.class)
					.getResultList();

			if (lista != null) {
				for (DetCondicionSegur detCondicionSegur : lista) {
					DetCondicionSegur nuevo = new DetCondicionSegur();
					nuevo.setActivo(false);
					nuevo.setConcursoPuestoAgr(concursoPuestoAgr);
					nuevo.setCondicionSegur(detCondicionSegur
							.getCondicionSegur());
					nuevo.setPuntajeCondSegur(detCondicionSegur
							.getPuntajeCondSegur());
					nuevo.setAjustes(detCondicionSegur.getAjustes());
					nuevo.setJustificacion(detCondicionSegur.getJustificacion());
					nuevo.setTipo("GRUPO");
					nuevaLista.add(nuevo);
				}
			}
		}

		return nuevaLista;
	}

	@SuppressWarnings("unchecked")
	private void buscarDetCondicionSegur() {
		String cad = "select * from planificacion.det_condicion_segur det_cond"
				+ " where det_cond.activo is true and det_cond.id_concurso_puesto_agr = "
				+ concursoPuestoAgr.getIdConcursoPuestoAgr();
		listaCondicionAux = new ArrayList<DetCondicionSegur>();
		listaCondicionAux = em.createNativeQuery(cad, DetCondicionSegur.class)
				.getResultList();

		if (listaCondicionAux == null || listaCondicionAux.size() == 0) {
			listaCondicionAux = cargarDetCondicionSegurDelPuestoTrabajo();
		}

		String cadena = "select * from planificacion.condicion_segur cond"
				+ " where cond.activo = TRUE order by cond.orden";
		List<CondicionSegur> lista = new ArrayList<CondicionSegur>();

		lista = em.createNativeQuery(cadena, CondicionSegur.class)
				.getResultList();
		listaCondicionDTO = new ArrayList<CondicionSeguridadDTO>();
		for (CondicionSegur seg : lista) {
			Boolean esta = false;
			for (DetCondicionSegur det : listaCondicionAux) {
				if (det.getCondicionSegur().getIdCondicionSegur()
						.equals(seg.getIdCondicionSegur())) {
					esta = true;
					CondicionSeguridadDTO dto = new CondicionSeguridadDTO();
					dto.setCondicionSegur(det.getCondicionSegur());
					dto.setId(det.getIdDetCondicionSegur());
					dto.setActivo(true);
					dto.setAjustes(det.getAjustes());
					dto.setJustificacion(det.getJustificacion());
					dto.setPuntaje(det.getPuntajeCondSegur());
					listaCondicionDTO.add(dto);
				}
			}
			if (!esta) {
				CondicionSeguridadDTO dto = new CondicionSeguridadDTO();
				dto.setActivo(true);
				dto.setCondicionSegur(seg);
				listaCondicionDTO.add(dto);
			}
		}

	}

	/*
	 * >>>>>>>>>>>>>>>>>>>>>>>>Cargar Otras Remuneraciones
	 * Sueldos<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
	 */
	private String findObj(Integer codigoObj) {
		String valorObj = null;
		if (codigoObj != null) {
			sinObjList.setCod(codigoObj);
			List<SinObj> lista = new ArrayList<SinObj>();
			lista = sinObjList.listaResultados();
			if (lista.size() > 0)
				valorObj = lista.get(0).getObjNombre();
		}
		return valorObj;
	}
	
	private void cargaSueldosRemuneracion() {
		/* OTRAS REMUNERACIONES */
		Query q;
		q = em.createQuery("select concepto from GrupoConceptoPago concepto "
				+ " where concepto.concursoPuestoAgr.idConcursoPuestoAgr = :idGrupo"
				+ " order by concepto.categoria asc");
		q.setParameter("idGrupo", concursoPuestoAgr.getIdConcursoPuestoAgr());
		List<GrupoConceptoPago> grupoConceptoPagos = (List<GrupoConceptoPago>) q
				.getResultList();
		String otrasRemuneraciones = "";
		for (GrupoConceptoPago grupoConceptoPago : grupoConceptoPagos) {
			if (grupoConceptoPago.getObjCodigo() != 111) {
				otrasRemuneraciones = otrasRemuneraciones
						+ "- "
						+ findObj(grupoConceptoPago.getObjCodigo())
						+ (grupoConceptoPago.getCategoria() == null ? ""
								: " Cat. " + grupoConceptoPago.getCategoria())
						+ " Monto "
						+ grupoConceptoPago.getMonto()
						+ " (Gs. "
						+ new NumeroLetra().Convertir(
								grupoConceptoPago.getMonto(), true) + ") \n";
			}
		}
		/* SUELDOS */
		List<PuestoConceptoPago> puestoConceptoPagos = new ArrayList<PuestoConceptoPago>();
		for (ConcursoPuestoDet cp : concursoPuestoAgr.getConcursoPuestoDets()) {
			q = em.createQuery("select concepto from PuestoConceptoPago concepto "
					+ " where concepto.plantaCargoDet.idPlantaCargoDet = :idPuesto"
					+ " order by concepto.categoria asc");
			q.setParameter("idPuesto", cp.getPlantaCargoDet()
					.getIdPlantaCargoDet());
			puestoConceptoPagos.addAll((List<PuestoConceptoPago>) q
					.getResultList());
		}
		// puestoConceptoPagos = quicksort(puestoConceptoPagos);
		String remuneracionBasica = "";
		String categoriaActual;

		for (int j = 0; j < puestoConceptoPagos.size(); j++) {
			categoriaActual = puestoConceptoPagos.get(j).getCategoria();
			int i = 0;

			while (puestoConceptoPagos.size() > (j + i)) {
				if (categoriaActual == null) {
					i++;
					break;
				}

				if (puestoConceptoPagos.get(j + i).getCategoria() != null)
					if (categoriaActual.compareTo(puestoConceptoPagos
							.get(j + i).getCategoria()) == 0)
						i++;
					else
						break;
				else
					break;
			}
			if (puestoConceptoPagos.get(j).getPlantaCargoDet().getPermanente() ? puestoConceptoPagos
					.get(j).getObjCodigo() == 111 : false) {
				remuneracionBasica = remuneracionBasica
						+ " - "
						+ i
						+ " vacancia(s) de "
						+ (puestoConceptoPagos.get(j).getCategoria() == null ? ""
								: " Categoría "
										+ puestoConceptoPagos.get(j)
												.getCategoria())
						+ " Monto "
						+ puestoConceptoPagos.get(j).getMonto()
						+ " (Gs. "
						+ new NumeroLetra().Convertir(puestoConceptoPagos
								.get(j).getMonto(), true) + ") \n";
			}
			j = j + i - 1;

		}

		em.createQuery(
				"update ConcursoPuestoAgr concursoPuestoAgr "
						+ " set concursoPuestoAgr.remuneracion='"
						+ remuneracionBasica + "',"
						+ " concursoPuestoAgr.otrasRemuneraciones='"
						+ otrasRemuneraciones + "'"
						+ " where concursoPuestoAgr.idConcursoPuestoAgr ="
						+ concursoPuestoAgr.getIdConcursoPuestoAgr())
				.executeUpdate();
		// concursoPuestoAgr.setOtrasRemuneraciones(otrasRemuneraciones);
		// concursoPuestoAgr.setRemuneracion(remuneracionBasica);
		// em.persist(concursoPuestoAgr);
		// em.flush();
	}

	/************************* </MODIFICACION> ********************************/

	private void habilitarPantalla() {
		habilitar = false;
		if (concursoPuestoAgr.getHomologar() != null
				&& !concursoPuestoAgr.getHomologar()
				&& concursoPuestoAgr.getHomologacionPerfilMatriz() != null)
			habilitar = false;
		else {
			Referencias refIniciado = new Referencias();
			refIniciado = referenciasUtilFormController.getReferencia(
					"ESTADOS_GRUPO", "INICIADO CIRCUITO");
			Referencias refPendiente = new Referencias();
			refPendiente = referenciasUtilFormController.getReferencia(
					"ESTADOS_GRUPO", "PENDIENTE REVISION");
			Referencias refAjuste = new Referencias();
			refAjuste = referenciasUtilFormController.getReferencia(
					"ESTADOS_GRUPO", "AJUSTE PUBLICACION");
			if (concursoPuestoAgr.getEstado().intValue() == refIniciado
					.getValorNum().intValue()
					|| concursoPuestoAgr.getEstado().intValue() == refPendiente
							.getValorNum().intValue())
				habilitar = true;
			if (concursoPuestoAgr.getEstado().intValue() == refAjuste
					.getValorNum().intValue())
				habilitar = false;
		}

	}

	private void muestraBancoPerfiles() {

		Query q = em
				.createQuery("select m from MatrizRefConf m "
						+ "  where m.activo is true and m.concursoPuestoAgr.idConcursoPuestoAgr = "
						+ concursoPuestoAgr.getIdConcursoPuestoAgr());
		List<MatrizRefConf> l = q.getResultList();
		if (l.isEmpty())
			muestraBtnHomologados = true;
		else
			muestraBtnHomologados = false;
	}

	private void validarOee() {
		/* Incidencia 1014 */
		if (seguridadUtilFormController == null) {
			seguridadUtilFormController = (SeguridadUtilFormController) Component
					.getInstance(SeguridadUtilFormController.class, true);
		}
		seguridadUtilFormController.verificarPerteneceOee(
				concurso.getConfiguracionUoCab().getIdConfiguracionUo(),
				concursoPuestoAgr.getIdConcursoPuestoAgr(),
				seguridadUtilFormController.estadoActividades("ESTADOS_GRUPO",
						"INICIADO CIRCUITO") + "", ActividadEnum.CARGA_GRUPO
						.getValor());

	}

	@SuppressWarnings("unchecked")
	private void buscarLista() {
		String sql = "select det.* "
				+ "from seleccion.concurso_puesto_det det "
				+ "where det.activo is true "
				+ "and det.id_concurso_puesto_agr = "
				+ concursoPuestoAgr.getIdConcursoPuestoAgr();
		listaConcursoPuestoDet = em.createNativeQuery(sql,
				ConcursoPuestoDet.class).getResultList();
	}

	private SinNivelEntidad buscarNivel(BigDecimal nenCodigo) {

		sinNivelEntidadList.getSinNivelEntidad().setNenCodigo(nenCodigo);
		nivelEntidad = sinNivelEntidadList.nivelEntidadMaxAnho();
		return nivelEntidad;
	}

	/**
	 * Combo Departamento
	 */
	public void updateDepartamento() {
		List<Departamento> dptoList = getDepartamentos();
		departamentosSelecItem = new ArrayList<SelectItem>();
		buildDepartamentoSelectItem(dptoList);
		idDpto = null;
		idCiudad = null;
		idBarrio = null;
	}

	private List<Departamento> getDepartamentos() {
		Long idPaisDir = buscarIdPais();
		if (idPaisDir != null) {
			departamentoList.getPais().setIdPais(idPaisDir);
			departamentoList.setOrder("descripcion");
			departamentoList.setMaxResults(null);
			return departamentoList.getResultList();
		}
		return new ArrayList<Departamento>();
	}

	/**
	 * Busca el id correspondiente a Paraguay
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Long buscarIdPais() {
		String cad = "select p.* from general.pais p where lower(p.descripcion) = 'paraguay'";
		List<Pais> lista = new ArrayList<Pais>();
		lista = em.createNativeQuery(cad, Pais.class).getResultList();
		if (lista.size() > 0)
			return lista.get(0).getIdPais();
		else
			return null;
	}

	private void buildDepartamentoSelectItem(List<Departamento> dptoList) {
		if (departamentosSelecItem == null)
			departamentosSelecItem = new ArrayList<SelectItem>();
		else
			departamentosSelecItem.clear();

		departamentosSelecItem.add(new SelectItem(null, SeamResourceBundle
				.getBundle().getString("opt_select_one")));
		for (Departamento dep : dptoList) {
			departamentosSelecItem.add(new SelectItem(dep.getIdDepartamento(),
					dep.getDescripcion()));
		}
	}

	/**
	 * Combo ciudad
	 */
	public void updateCiudad() {
		List<Ciudad> ciuList = getCiudadByDpto();
		ciudadSelecItem = new ArrayList<SelectItem>();
		buildCiudadSelectItem(ciuList);
		idCiudad = null;
		idBarrio = null;
	}

	private List<Ciudad> getCiudadByDpto() {
		if (idDpto != null) {
			ciudadList.getDepartamento().setIdDepartamento(idDpto);
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
	 * Combo Barrio
	 */
	public void updateBarrio() {
		List<Barrio> barList = getBarriosByCiudad();
		barrioSelecItem = new ArrayList<SelectItem>();
		buildBarrioSelectItem(barList);
		idBarrio = null;
	}

	private List<Barrio> getBarriosByCiudad() {
		if (idCiudad != null) {
			barrioList.setIdCiudad(idCiudad);
			barrioList.setOrder("descripcion");
			barrioList.setMaxResults(null);
			return barrioList.getResultList();
		}
		return new ArrayList<Barrio>();
	}

	private void buildBarrioSelectItem(List<Barrio> barrioList) {
		if (barrioSelecItem == null)
			barrioSelecItem = new ArrayList<SelectItem>();
		else
			barrioSelecItem.clear();

		barrioSelecItem.add(new SelectItem(null, SeamResourceBundle.getBundle()
				.getString("opt_select_one")));
		for (Barrio bar : barrioList) {
			barrioSelecItem.add(new SelectItem(bar.getIdBarrio(), bar
					.getDescripcion()));
		}
	}

	/**
	 * Método que obtiene el codigo del puesto, concatenado con la unidad
	 * organizativa cabeza unidad organizativa dependiente, entidad, nivel,
	 * orden
	 * 
	 * @param det
	 * @return
	 */
	public String obtenerCodigo(PlantaCargoDet det) {
		ConfiguracionUoDet uoDet = new ConfiguracionUoDet();
		uoDet = det.getConfiguracionUoDet();
		String code = "";
		List<Integer> listCodes = obtenerListaCodigos(uoDet, null);
		for (Integer codigo : listCodes) {
			code += codigo + ".";

		}
		/*if (det.getContratado())
			code = code + "C";
		if (det.getPermanente())
			code = code + "P";*/
		code = code + "P";
		code = code + det.getOrden();
		return code;
	}

	private List<Integer> obtenerListaCodigos(ConfiguracionUoDet uoDet,
			List<Integer> listCodigos) {
		uoDet.getDenominacionUnidad();
		if (listCodigos == null)
			listCodigos = new ArrayList<Integer>();

		listCodigos.add(0, uoDet.getOrden());
		if (uoDet.getConfiguracionUoDet() != null)
			obtenerListaCodigos(uoDet.getConfiguracionUoDet(), listCodigos);
		else {
			listCodigos.add(0, uoDet.getConfiguracionUoCab().getOrden());
			listCodigos.add(0, uoDet.getConfiguracionUoCab().getEntidad()
					.getSinEntidad().getEntCodigo().intValue());
			listCodigos.add(0, uoDet.getConfiguracionUoCab().getEntidad()
					.getSinEntidad().getNenCodigo().intValue());
		}
		return listCodigos;
	}

	@SuppressWarnings("unchecked")
	public String obtenerCategoria(PlantaCargoDet det) {
		if (det != null) {
			String cad = "select pago.* from planificacion.puesto_concepto_pago pago "
					+ "where pago.obj_codigo = 111 "
					+ "and pago.id_planta_cargo_det = "
					+ det.getIdPlantaCargoDet();

			List<PuestoConceptoPago> lista = em.createNativeQuery(cad,
					PuestoConceptoPago.class).getResultList();
			if (lista.size() > 0)
				return lista.get(0).getCategoria();
		}
		return "Sin Categoria";
	}

	@SuppressWarnings("unchecked")
	public void search() {
		String sql = "select * from seleccion.concurso_puesto_det det "
				+ "join seleccion.concurso_puesto_agr agr  "
				+ "on agr.id_concurso_puesto_agr = det.id_concurso_puesto_agr "
				+ "join planificacion.planta_cargo_det cargo_det "
				+ "on cargo_det.id_planta_cargo_det = det.id_planta_cargo_det ";
		String where = " where agr.id_concurso_puesto_agr = "
				+ idConcursoPuestoAgr;
		if (idBarrio != null || idCiudad != null || idDpto != null)
			sql = sql + "join planificacion.oficina oficina  "
					+ "on oficina.id_oficina = cargo_det.id_oficina ";
		if (modalidadOcupacion != null && modalidadOcupacion.getValor() != null) {
			if (modalidadOcupacion.getValor().equals("CONTRATADO"))
				where = where + " and cargo_det.contratado is true ";
			if (modalidadOcupacion.getValor().equals("PERMANENTE"))
				where = where + " and cargo_det.permanente is true ";
		}

		if (idDpto != null) {
			sql = sql
					+ " join general.ciudad ciudad on ciudad.id_ciudad = oficina.id_ciudad "
					+ "join general.departamento dpto on dpto.id_departamento = ciudad.id_departamento ";
			where = where + " and dpto.id_departamento = " + idDpto;
		}
		if (idCiudad != null)
			where = where + " and ciudad.id_ciudad = " + idCiudad;
		if (idBarrio != null) {
			sql = sql
					+ " join general.barrio barrio on barrio.id_barrio = oficina.id_barrio ";
			where = where + " and barrio.id_barrio = " + idBarrio;
		}

		listaConcursoPuestoDet = new ArrayList<ConcursoPuestoDet>();
		listaConcursoPuestoDet = em.createNativeQuery(sql + where,
				ConcursoPuestoDet.class).getResultList();
	}

	public void searchAll() {
		listaConcursoPuestoDet = new ArrayList<ConcursoPuestoDet>();
		listaConcursoPuestoDet = concursoPuestoAgr.getConcursoPuestoDets();
		idBarrio = null;
		idCiudad = null;
		idDpto = null;
		modalidadOcupacion = null;
	}
	
	public boolean tareaCerrada(){
		
		Referencias ref = new Referencias();
		ref = referenciasUtilFormController.getReferencia(
				"ESTADOS_GRUPO", "INICIADO CIRCUITO");
		
		ConcursoPuestoAgr aux = em.find(ConcursoPuestoAgr.class, this.idConcursoPuestoAgr);
		em.refresh(aux);
		
		if (aux.getEstado().equals(ref.getValorNum()))
			return false;
		else
			return true;
		
	}

	public String nextTask() {

		ConcursoPuestoAgr concursoPuestoAgr = em.find(ConcursoPuestoAgr.class,
				idConcursoPuestoAgr);

		if (!validate(concursoPuestoAgr)) {
			return null;
		}
		
		//AGREGADO JD
		if(tareaCerrada()){
			System.out.println("Esta Tarea ya fue cerrada..");
			return null;
		}
		
		// Se actualiza el estado
		Referencias referencias = referenciasUtilFormController.getReferencia(
				"ESTADOS_GRUPO", "FINALIZADO CARGA");
		concursoPuestoAgr.setEstado(referencias.getValorNum());
		em.merge(concursoPuestoAgr);

		// Se actualiza los estados de las plantas
		EstadoDet estadoDet = seleccionUtilFormController.buscarEstadoDet(
				"CONCURSO", "FINALIZADO CARGA");
		List<ConcursoPuestoDet> lista = concursoPuestoAgr
				.getConcursoPuestoDets();
		if (lista != null && lista.size() > 0) {
			for (ConcursoPuestoDet concursoPuestoDet : lista) {
				PlantaCargoDet plantaCargoDet = concursoPuestoDet
						.getPlantaCargoDet();
				plantaCargoDet.setEstadoDet(estadoDet);
				em.merge(plantaCargoDet);

				concursoPuestoDet.setEstadoDet(estadoDet);
				em.merge(concursoPuestoDet);
			}
		}

		// Se pasa a la siguiente tarea
		jbpmUtilFormController.setConcursoPuestoAgr(concursoPuestoAgr);
		jbpmUtilFormController.setActividadActual(ActividadEnum.CARGA_GRUPO);

		/************************* <MODIFICACION> *********************************/
		/************************* MODIFICADO: 01/11/2013 *************************/
		/************************* AUTOR: RODRIGO VELAZQUEZ ***********************/
		/*
		 * if (concursoPuestoAgr.getHomologar() == null ||
		 * concursoPuestoAgr.getHomologar()) { // Se asume que por defecto se
		 * envie a homologacion si no hay nada cargado
		 * jbpmUtilFormController.setActividadSiguiente
		 * (ActividadEnum.ENVIAR_HOMOLOGACION);
		 * jbpmUtilFormController.setTransition("next"); } else {
		 * jbpmUtilFormController
		 * .setActividadSiguiente(ActividadEnum.MODIFICAR_DATOS_CONCURSO);
		 * jbpmUtilFormController.setTransition("next2"); }
		 */
		jbpmUtilFormController
				.setActividadSiguiente(ActividadEnum.APROBAR_HOMOLOG_SFP);
		jbpmUtilFormController.setTransition("next");
		/************************* </MODIFICACION> ********************************/
		jbpmUtilFormController.nextTask();

		ConcursoPuestoAgr agr = new ConcursoPuestoAgr();
		agr = em.find(ConcursoPuestoAgr.class, idConcursoPuestoAgr);
		Observacion obs = new Observacion();
		obs.setConcurso(agr.getConcurso());
		obs.setFechaAlta(new Date());
		obs.setUsuAlta(usuarioLogueado.getCodigoUsuario());

		// Guardar HOMOLOGACION_PERFIL_MATRIZ AGREGADO JD
		Date fecha = new Date();
		HomologacionPerfilMatriz homologacionPerfilMatriz = new HomologacionPerfilMatriz();
		homologacionPerfilMatriz.setConcursoPuestoAgr(concursoPuestoAgr);
		homologacionPerfilMatriz.setFechaAlta(fecha);
		homologacionPerfilMatriz.setUsuAlta(usuarioLogueado.getCodigoUsuario());
		homologacionPerfilMatriz.setFechaPresentacion(fecha);

		ConcursoPuestoDet concursoPuestoDet = concursoPuestoAgr
				.getConcursoPuestoDets().get(0);
		homologacionPerfilMatriz.setCpt(concursoPuestoDet.getPlantaCargoDet()
				.getCpt());
		em.persist(homologacionPerfilMatriz);

		em.flush();

		Long idTaskInstance = jbpmUtilFormController
				.getIdTaskInstanceActual(agr.getIdProcessInstance());
		obs.setIdTaskInstance(idTaskInstance);

		if (observaciones != null && !observaciones.trim().isEmpty())
			obs.setObservacion(observaciones);
		try {
			em.persist(obs);
			em.flush();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return "nextTask";
	}

	/**
	 * Validacion
	 * 
	 * @return
	 */
	private boolean validate(ConcursoPuestoAgr concursoPuestoAgr) {

		String mensaje = "";

		if (!existeDatosGrupoPuestos(concursoPuestoAgr)) {
			mensaje = "No puede pasar a la Siguiente Tarea porque el Grupo '"
					+ concursoPuestoAgr.getDescripcionGrupo()
					+ "' no tiene Criterios de Selección.";
		} else if (!existeFunciones(concursoPuestoAgr)) {
			mensaje = "No puede pasar a la Siguiente Tarea porque el Grupo '"
					+ concursoPuestoAgr.getDescripcionGrupo()
					+ "' no tiene Funciones.";
		} else if (!existeRequisitos(concursoPuestoAgr)) {
			mensaje = "No puede pasar a la Siguiente Tarea porque el Grupo '"
					+ concursoPuestoAgr.getDescripcionGrupo()
					+ "' no tiene Requisitos.";
		} else if (!existeConceptoPagos(concursoPuestoAgr)) {
			mensaje = "No puede pasar a la Siguiente Tarea porque el Grupo '"
					+ concursoPuestoAgr.getDescripcionGrupo()
					//+ "' no tiene Concepto de Pago.";
					+"' no tiene Remuneracion.";
		} else if (!existeCondicionTrabajoGral(concursoPuestoAgr)) {
			mensaje = "No puede pasar a la Siguiente Tarea porque el Grupo '"
					+ concursoPuestoAgr.getDescripcionGrupo()
					+ "' no tiene Condiciones de Trabajo Generales.";
		} else if (!existeCondicionTrabajoEspecifico(concursoPuestoAgr)) {
			mensaje = "No puede pasar a la Siguiente Tarea porque el Grupo '"
					+ concursoPuestoAgr.getDescripcionGrupo()
					+ "' no tiene Condiciones de Trabajo Específicas.";
		} else if (!existeCondicionSeguridad(concursoPuestoAgr)) {
			mensaje = "No puede pasar a la Siguiente Tarea porque el Grupo '"
					+ concursoPuestoAgr.getDescripcionGrupo()
					+ "' no tiene Condiciones de Seguridad.";
		} else if (!existeMatrizReferencial(concursoPuestoAgr)) {
			mensaje = "No puede pasar a la Siguiente Tarea porque el Grupo '"
					+ concursoPuestoAgr.getDescripcionGrupo()
					+ "' no tiene Matríz Referencial.";
		} else if (!existeMatrizDocumental(concursoPuestoAgr)) {
			mensaje = "No puede pasar a la Siguiente Tarea porque el Grupo '"
					+ concursoPuestoAgr.getDescripcionGrupo()
					+ "' no tiene Matríz Documental.";
		} else if (Utiles.vacio(this.concursoPuestoAgr.getModalidad())){
			mensaje = "No puede pasar a la Siguiente Tarea al Grupo '"
					+ concursoPuestoAgr.getDescripcionGrupo()
					+ "', Falta Cargar datos de Remuneracion. (revise en 'otros datos')";
			//	+ "' no tiene Modalidad. (revise 'otros datos')";
		} else {
			Concurso c = em.find(Concurso.class, concursoPuestoAgr
					.getConcurso().getIdConcurso());
			if (c.getPcd() != null && !c.getPcd()) {
				if (existeDatosGrupoPuestosNullPCD(concursoPuestoAgr)) {
					mensaje = "No puede pasar a la Siguiente Tarea porque el Grupo '"
							+ concursoPuestoAgr.getDescripcionGrupo()
							+ "' no tiene Preferencia Personas con Discapacidad.";
				}
			}
		}

		if (!Utiles.vacio(mensaje)) {
			// SEAM ERROR
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, mensaje);
			return false;
		}

		return true;
	}

	/**
	 * Verifica que existan datos del grupo de puestos para el grupo
	 * 
	 * @param concursoPuestoAgr
	 * @return
	 */
	private boolean existeDatosGrupoPuestos(ConcursoPuestoAgr concursoPuestoAgr) {
		String query = "SELECT * FROM seleccion.datos_grupo_puesto where id_concurso_puesto_agr = "
				+ concursoPuestoAgr.getIdConcursoPuestoAgr();
		return seleccionUtilFormController.existeNative(query);
	}

	/**
	 * Verifica que existan funciones para el grupo de puestos
	 * 
	 * @param concursoPuestoAgr
	 * @return
	 */
	private boolean existeFunciones(ConcursoPuestoAgr concursoPuestoAgr) {
		String query = " SELECT * FROM planificacion.det_contenido_funcional "
				+ " where id_concurso_puesto_agr = "
				+ concursoPuestoAgr.getIdConcursoPuestoAgr()
				+ " and tipo = 'GRUPO' ";
		return seleccionUtilFormController.existeNative(query);
	}

	/**
	 * Verifica que existan requisitos para el grupo de puestos
	 * 
	 * @param concursoPuestoAgr
	 * @return
	 */
	private boolean existeRequisitos(ConcursoPuestoAgr concursoPuestoAgr) {
		String query = " SELECT * FROM planificacion.det_req_min "
				+ " where id_concurso_puesto_agr = "
				+ concursoPuestoAgr.getIdConcursoPuestoAgr()
				+ " and tipo = 'GRUPO' ";
		return seleccionUtilFormController.existeNative(query);
	}

	/**
	 * Verifica que existan concepto de pagos para el grupo de puestos
	 * 
	 * @param concursoPuestoAgr
	 * @return
	 */
	private boolean existeConceptoPagos(ConcursoPuestoAgr concursoPuestoAgr) {
		String query = " SELECT * FROM seleccion.grupo_concepto_pago "
				+ " where id_concurso_puesto_agr = "
				+ concursoPuestoAgr.getIdConcursoPuestoAgr()
				+ " and tipo = 'GRUPO' ";
		return seleccionUtilFormController.existeNative(query);
	}

	/**
	 * Verifica que existan condiciones de trabajos para el grupo de puestos
	 * 
	 * @param concursoPuestoAgr
	 * @return
	 */
	private boolean existeCondicionTrabajoGral(
			ConcursoPuestoAgr concursoPuestoAgr) {
		String query = " SELECT * FROM planificacion.det_condicion_trabajo "
				+ " where id_concurso_puesto_agr = "
				+ concursoPuestoAgr.getIdConcursoPuestoAgr()
				+ " and tipo = 'GRUPO' ";
		return seleccionUtilFormController.existeNative(query);
	}

	/**
	 * Verifica que existan condiciones de trabajos especificos para el grupo de
	 * puestos
	 * 
	 * @param concursoPuestoAgr
	 * @return
	 */
	private boolean existeCondicionTrabajoEspecifico(
			ConcursoPuestoAgr concursoPuestoAgr) {
		String query = " SELECT * FROM planificacion.det_condicion_trabajo_especif "
				+ " where id_concurso_puesto_agr = "
				+ concursoPuestoAgr.getIdConcursoPuestoAgr()
				+ " and tipo = 'GRUPO' ";
		return seleccionUtilFormController.existeNative(query);
	}

	/**
	 * Verifica que existan condiciones de seguridad para el grupo de puestos
	 * 
	 * @param concursoPuestoAgr
	 * @return
	 */
	private boolean existeCondicionSeguridad(ConcursoPuestoAgr concursoPuestoAgr) {
		String query = " SELECT * FROM planificacion.det_condicion_segur "
				+ " where id_concurso_puesto_agr = "
				+ concursoPuestoAgr.getIdConcursoPuestoAgr()
				+ " and tipo = 'GRUPO' ";
		return seleccionUtilFormController.existeNative(query);
	}

	/**
	 * Verifica que exista matriz referencial para el grupo de puestos
	 * 
	 * @param concursoPuestoAgr
	 * @return
	 */
	private boolean existeMatrizReferencial(ConcursoPuestoAgr concursoPuestoAgr) {
		String query = " SELECT * FROM seleccion.matriz_ref_conf "
				+ " where id_concurso_puesto_agr = "
				+ concursoPuestoAgr.getIdConcursoPuestoAgr()
				+ " and tipo = 'GRUPO' ";
		return seleccionUtilFormController.existeNative(query);
	}

	/**
	 * Verifica que exista matriz documental para el grupo de puestos
	 * 
	 * @param concursoPuestoAgr
	 * @return
	 */
	private boolean existeMatrizDocumental(ConcursoPuestoAgr concursoPuestoAgr) {
		String query = " SELECT * FROM seleccion.matriz_doc_config_enc "
				+ " where id_concurso_puesto_agr = "
				+ concursoPuestoAgr.getIdConcursoPuestoAgr();
		return seleccionUtilFormController.existeNative(query);
	}

	private boolean existeDatosGrupoPuestosNullPCD(
			ConcursoPuestoAgr concursoPuestoAgr) {
		String query = "SELECT * FROM seleccion.datos_grupo_puesto "
				+ " where id_concurso_puesto_agr = "
				+ concursoPuestoAgr.getIdConcursoPuestoAgr()
				+ " and preferencia_pers_discapacidad is null";
		return seleccionUtilFormController.existeNative(query);
	}

	public String usarBancoHomologados() {
		String resultado = null;
		try {

			if (valorRadioButton != null) {
				if (valorRadioButton.equals("si")) {
					concursoPuestoAgr.setHomologar(true);
					resultado = "ir";
				}
				if (valorRadioButton.equals("no")) {
					concursoPuestoAgr.setHomologar(false);
					resultado = "ir";
				}
				em.merge(concursoPuestoAgr);
				em.flush();

			}
		} catch (Exception e) {
			return null;
		}
		return resultado;
	}

	public SinNivelEntidad getNivelEntidad() {
		return nivelEntidad;
	}

	public void setNivelEntidad(SinNivelEntidad nivelEntidad) {
		this.nivelEntidad = nivelEntidad;
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

	public Concurso getConcurso() {
		return concurso;
	}

	public void setConcurso(Concurso concurso) {
		this.concurso = concurso;
	}

	public Long getIdDpto() {
		return idDpto;
	}

	public void setIdDpto(Long idDpto) {
		this.idDpto = idDpto;
	}

	public Long getIdCiudad() {
		return idCiudad;
	}

	public void setIdCiudad(Long idCiudad) {
		this.idCiudad = idCiudad;
	}

	public Long getIdBarrio() {
		return idBarrio;
	}

	public void setIdBarrio(Long idBarrio) {
		this.idBarrio = idBarrio;
	}

	public ModalidadOcupacion getModalidadOcupacion() {
		return modalidadOcupacion;
	}

	public void setModalidadOcupacion(ModalidadOcupacion modalidadOcupacion) {
		this.modalidadOcupacion = modalidadOcupacion;
	}

	public Long getIdConcursoPuestoAgr() {
		return idConcursoPuestoAgr;
	}

	public void setIdConcursoPuestoAgr(Long idConcursoPuestoAgr) {
		this.idConcursoPuestoAgr = idConcursoPuestoAgr;
	}

	public ConcursoPuestoAgr getConcursoPuestoAgr() {
		return concursoPuestoAgr;
	}

	public void setConcursoPuestoAgr(ConcursoPuestoAgr concursoPuestoAgr) {
		this.concursoPuestoAgr = concursoPuestoAgr;
	}

	public PlantaCargoDet getPlantaCargoDet() {
		return plantaCargoDet;
	}

	public void setPlantaCargoDet(PlantaCargoDet plantaCargoDet) {
		this.plantaCargoDet = plantaCargoDet;
	}

	public List<ConcursoPuestoDet> getListaConcursoPuestoDet() {
		return listaConcursoPuestoDet;
	}

	public void setListaConcursoPuestoDet(
			List<ConcursoPuestoDet> listaConcursoPuestoDet) {
		this.listaConcursoPuestoDet = listaConcursoPuestoDet;
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

	public List<SelectItem> getBarrioSelecItem() {
		return barrioSelecItem;
	}

	public void setBarrioSelecItem(List<SelectItem> barrioSelecItem) {
		this.barrioSelecItem = barrioSelecItem;
	}

	public String getValorRadioButton() {
		return valorRadioButton;
	}

	public void setValorRadioButton(String valorRadioButton) {
		this.valorRadioButton = valorRadioButton;
	}

	public String getObservaciones() {
		return observaciones;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}

	public String getUbicacionFisica() {
		return ubicacionFisica;
	}

	public void setUbicacionFisica(String ubicacionFisica) {
		this.ubicacionFisica = ubicacionFisica;
	}

	public Boolean getMuestraBtnHomologados() {
		return muestraBtnHomologados;
	}

	public void setMuestraBtnHomologados(Boolean muestraBtnHomologados) {
		this.muestraBtnHomologados = muestraBtnHomologados;
	}

	public Boolean getHabilitar() {
		return habilitar;
	}

	public void setHabilitar(Boolean habilitar) {
		this.habilitar = habilitar;
	}

	public Boolean getEdit() {
		return Edit;
	}

	public void setEdit(Boolean isEdit) {
		this.Edit = isEdit;
	}

}
