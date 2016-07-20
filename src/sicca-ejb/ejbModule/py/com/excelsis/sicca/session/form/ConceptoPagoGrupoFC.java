package py.com.excelsis.sicca.session.form;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.security.AuthorizationException;

import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.Entidad;
import py.com.excelsis.sicca.entity.GrupoConceptoPago;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.entity.PuestoConceptoPago;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.entity.SinAnx;
import py.com.excelsis.sicca.entity.SinCategoriaContratados;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.entity.SinObj;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.ConcursoPuestoAgrHome;
import py.com.excelsis.sicca.session.SinNivelEntidadList;
import py.com.excelsis.sicca.session.SinObjList;
import py.com.excelsis.sicca.session.util.ReferenciasUtilFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.SinarhUtiles;

@Scope(ScopeType.CONVERSATION)
@Name("conceptoPagoGrupoFC")
public class ConceptoPagoGrupoFC implements Serializable {

	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;

	@In(create = true)
	ConcursoPuestoAgrHome concursoPuestoAgrHome;
	@In(create = true)
	SinNivelEntidadList sinNivelEntidadList;
	@In(create = true)
	SinObjList sinObjList;
	@In(create = true)
	ReferenciasUtilFormController referenciasUtilFormController;
	@In(create = true)
	SinarhUtiles sinarhUtiles;

	private SinNivelEntidad nivelEntidad = new SinNivelEntidad();
	private SinEntidad sinEntidad = new SinEntidad();
	private ConfiguracionUoCab configuracionUoCab = new ConfiguracionUoCab();
	private Concurso concurso;
	private ConcursoPuestoAgr concursoPuestoAgr;
	public ConceptoPagoFormController conceptoPagoFormController;
	private SinAnx sinAnx;
	private Integer codigoObj;
	private String valorObj;
	private Integer monto;
	private Long idSinAnx;
	private Boolean habilitar = true;

	private List<GrupoConceptoPago> listaGrupoPago = new ArrayList<GrupoConceptoPago>();
	private List<GrupoConceptoPago> listaGrupoPagoAux = new ArrayList<GrupoConceptoPago>();
	private SeguridadUtilFormController seguridadUtilFormController;

	private void validarOee() {
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

	/**
	 * Método que inicializa los datos
	 */
	public void init() {
		concurso = new Concurso();

		concursoPuestoAgr = new ConcursoPuestoAgr();
		concursoPuestoAgr = concursoPuestoAgrHome.getInstance();
		concurso = concursoPuestoAgr.getConcurso();
		/* Incidencia 1014 */
		validarOee();
		/**/

		buscarDatosAsociadosUsuario();
		if (listaGrupoPago.size() == 0 && idSinAnx == null)
			buscarDatos();
		else
			idSinAnx = null;
		if (conceptoPagoFormController == null) {
			conceptoPagoFormController = (py.com.excelsis.sicca.session.form.ConceptoPagoFormController) Component
					.getInstance(ConceptoPagoFormController.class, true);
		}
		habilitarPantalla();

	}

	private void habilitarPantalla() {
		habilitar = false;

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
						.getValorNum().intValue()
				|| concursoPuestoAgr.getEstado().intValue() == refAjuste
						.getValorNum().intValue())
			habilitar = true;
		else
			habilitar = false;

	}

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

		monto = null;
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

	public void findObj() {
		if (codigoObj != null) {
			sinObjList.setCod(codigoObj);
			List<SinObj> lista = new ArrayList<SinObj>();
			lista = sinObjList.listaResultados();
			if (lista.size() > 0)
				valorObj = lista.get(0).getObjNombre();
			else
				valorObj = null;
		}
	}

	/**
	 * Valida si el registro puede agregarse a la lista y lo agrega si es
	 * posible
	 */
	@SuppressWarnings("deprecation")
	public void agregarLista() {
		if (codigoObj == null || monto == null) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, "Ingrese los campos requeridos");
			return;
		}
		if (monto.intValue() < 0) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR,
					"El valor del monto debe ser mayor a cero");
			return;
		}
		if (codigoObj == 111) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR,
					"No se puede agregar objetos de gasto 111");
			return;
		}
		if (existeRegistro()) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR,
					"Ya existe el Objeto de Gasto que desea agregar");
			return;
		}
		String tipoPuesto = conceptoPagoFormController
				.esContratadoOpermanente(concursoPuestoAgr
						.getIdConcursoPuestoAgr());
		if (!conceptoPagoFormController.obtenerObjeto(tipoPuesto, codigoObj)
				.isEmpty()) {
			if (tipoPuesto.equalsIgnoreCase("PERMANENTE")) {
				String ms = validarParaAgregar(tipoPuesto);
				if (ms != null) {
					statusMessages.clear();
					statusMessages.add(Severity.ERROR, ms);
					return;
				}

			}
			if (tipoPuesto.equalsIgnoreCase("PERMANENTE")) {
				SinCategoriaContratados sinCategoriaContratados = new SinCategoriaContratados();
				sinCategoriaContratados = conceptoPagoFormController
						.existeCategoriaContratados(codigoObj);
				if (sinCategoriaContratados == null) {
					statusMessages.clear();
					statusMessages
							.add(Severity.ERROR,
									"No existe el Objeto de Gasto en el SINARH. Verifique.");
					return;
				}
			}

			if (conceptoPagoFormController.esContratadoOpermanente(
					concursoPuestoAgr.getIdConcursoPuestoAgr())
					.equalsIgnoreCase("PERMANENTE")) {
				if (buscarListaSinAnx().isEmpty()) {
					statusMessages.clear();
					statusMessages
							.add(Severity.ERROR,
									"No existe el Objeto de Gasto en el SINARH. Verifique.");
					return;
				}

			}
			if (conceptoPagoFormController.esContratadoOpermanente(
					concursoPuestoAgr.getIdConcursoPuestoAgr())
					.equalsIgnoreCase("CONTRATADO")) {
				SinCategoriaContratados sinCategoriaContratados = new SinCategoriaContratados();
				sinCategoriaContratados = conceptoPagoFormController
						.existeCategoriaContratados(codigoObj);
				if (sinCategoriaContratados == null) {
					statusMessages.clear();
					statusMessages
							.add(Severity.ERROR,
									"El objeto de gasto ingresado no existe en el SINARH. Verifique.");
					return;
				}
			}
			GrupoConceptoPago concepto = new GrupoConceptoPago();
			if (codigoObj != null)
				concepto.setObjCodigo(codigoObj);

			Date fecha = new Date();
			Integer anho = fecha.getYear() + 1900;
			concepto.setAnho(anho);
			if (monto != null)
				concepto.setMonto(monto);
			concepto.setConcursoPuestoAgr(concursoPuestoAgr);
			concepto.setFechaAlta(fecha);
			concepto.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			concepto.setTipo("GRUPO");
			concepto.setActivo(true);
			listaGrupoPago.add(concepto);
		} else {
			if (tipoPuesto.equalsIgnoreCase("CONTRATADO")) {
				statusMessages.clear();
				statusMessages
						.add(Severity.ERROR,
								"El objeto de gasto ingresado no es un objeto de gasto definido para puestos contratados");
				return;
			}

			if (tipoPuesto.equalsIgnoreCase("PERMANENTE")) {
				statusMessages.clear();
				statusMessages
						.add(Severity.ERROR,
								"El objeto de gasto ingresado no es un objeto de gasto definido para puestos permanentes");
				return;
			}

			codigoObj = null;
			valorObj = null;
			monto = null;
		}
	}

	private String validarParaAgregar(String tipo) {
		if (tipo.equalsIgnoreCase("PERMANENTE")) {
			String codigoSinarh = configuracionUoCab.getCodigoSinarh();

			List<SinAnx> listaAnx = buscarListaSinAnx();
			if (listaAnx.isEmpty())
				return "El Objeto de Gasto ingresado no existe en SINARH";

		}
		return null;
	}

	private Boolean existeRegistro() {
		for (GrupoConceptoPago p : listaGrupoPago) {
			if (p.getObjCodigo() == codigoObj)
				return true;
		}
		return false;
	}

	/**
	 * Obtiene la lista SinAnx correspondiente al codigo sinarh
	 * 
	 * @param codigoSinarh
	 * @param codigo
	 * @return
	 */
	private List<SinAnx> buscarListaSinAnx() {
		return sinarhUtiles.obtenerListaSinAnx(null, new Integer(50),
				codigoObj, null, null);

	}

	public void eliminarLista(Integer row) {
		GrupoConceptoPago concepto = new GrupoConceptoPago();
		concepto = listaGrupoPago.get(row);
		listaGrupoPago.remove(concepto);
	}

	private void guardar(GrupoConceptoPago pago) {
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
		puestoPago.setUsuAlta(usuarioLogueado.getCodigoUsuario());
		PlantaCargoDet planta = new PlantaCargoDet();
		planta = conceptoPagoFormController
				.obtenerPlantaCargoDet(concursoPuestoAgr
						.getIdConcursoPuestoAgr());
		puestoPago.setPlantaCargoDet(planta);
		em.persist(puestoPago);
	}

	public String guardar() {
		try {
			for (GrupoConceptoPago p : listaGrupoPago) {
				if (p.getIdGrupoConceptoPago() == null) {
					guardar(p);
				}
			}
			em.flush();

			statusMessages.clear();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));

			return "persisted";
		} catch (Exception e) {
			statusMessages.add(Severity.ERROR, e.getMessage());
			return null;
		}
	}

	public String updated() {
		try {

			for (GrupoConceptoPago p : listaGrupoPago) {
				if (p.getIdGrupoConceptoPago() == null) {
					guardar(p);
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

			statusMessages.clear();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));

			return "persisted";
		} catch (Exception e) {
			statusMessages.add(Severity.ERROR, e.getMessage());
			return null;
		}
	}

	/**
	 * En caso de que uno mas registros son eliminados de la grilla estos son
	 * inactivados de la bd
	 * 
	 * @param gr
	 */
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

	public ConcursoPuestoAgr getConcursoPuestoAgr() {
		return concursoPuestoAgr;
	}

	public void setConcursoPuestoAgr(ConcursoPuestoAgr concursoPuestoAgr) {
		this.concursoPuestoAgr = concursoPuestoAgr;
	}

	public SinAnx getSinAnx() {
		return sinAnx;
	}

	public void setSinAnx(SinAnx sinAnx) {
		this.sinAnx = sinAnx;
	}

	public Integer getCodigoObj() {
		return codigoObj;
	}

	public void setCodigoObj(Integer codigoObj) {
		this.codigoObj = codigoObj;
	}

	public String getValorObj() {
		return valorObj;
	}

	public void setValorObj(String valorObj) {
		this.valorObj = valorObj;
	}

	public Integer getMonto() {
		return monto;
	}

	public void setMonto(Integer monto) {
		this.monto = monto;
	}

	public Long getIdSinAnx() {
		return idSinAnx;
	}

	public void setIdSinAnx(Long idSinAnx) {
		this.idSinAnx = idSinAnx;
	}

	public List<GrupoConceptoPago> getListaGrupoPago() {
		return listaGrupoPago;
	}

	public void setListaGrupoPago(List<GrupoConceptoPago> listaGrupoPago) {
		this.listaGrupoPago = listaGrupoPago;
	}

	public List<GrupoConceptoPago> getListaGrupoPagoAux() {
		return listaGrupoPagoAux;
	}

	public void setListaGrupoPagoAux(List<GrupoConceptoPago> listaGrupoPagoAux) {
		this.listaGrupoPagoAux = listaGrupoPagoAux;
	}

	public Boolean getHabilitar() {
		return habilitar;
	}

	public void setHabilitar(Boolean habilitar) {
		this.habilitar = habilitar;
	}

}
