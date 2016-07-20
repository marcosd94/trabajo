package py.com.excelsis.sicca.remuneracion.session.form;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import py.com.excelsis.sicca.entity.AlertasEvalDet;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.entity.RemunConfig;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.DatosEspecificosHome;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.ReferenciasUtilFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.General;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("configurarItemsCU714FC")
public class ConfigurarItemsCU714FC {
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In
	StatusMessages statusMessages;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(required = false, create = true)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;
	@In(create = true)
	SeguridadUtilFormController seguridadUtilFormController;
	@In(create = true)
	SeleccionUtilFormController seleccionUtilFormController;
	@In(create = true)
	DatosEspecificosHome datosEspecificosHome;
	@In(create = true)
	ReferenciasUtilFormController referenciasUtilFormController;
	@In(create = true, required = false)
	ProcesoUpdateCat722FC procesoUpdateCat722FC;
	@In(create = true, required = false)
	ProcesoUpdateEntidadOee763FC procesoUpdateEntidadOee763FC;

	private Referencias refCantDias;
	private Referencias refCantDiasUpdate;
	private Referencias refHora;
	private Referencias refHoraUpdate;
	private Referencias refAutomaticoManual;
	private Referencias refAutoDias;
	private Referencias refAutoHora;
	private Boolean mostrarPanel;
	private Boolean esEdit;
	private Long idOrigen;
	private Integer index;
	private String notificaciones;
	private List<SelectItem> origenSelectItems = new ArrayList<SelectItem>();
	private List<RemunConfig> listaConfiguraciones = new ArrayList<RemunConfig>();
	private List<RemunConfig> inactivarConfiguraciones = new ArrayList<RemunConfig>();

	public void init() {
		// cargarNiveentidadOee();
		esEdit = false;
		mostrarPanel = false;
		refCantDias = referenciasUtilFormController.getReferenciaDescLarga(
				"REMUNERACION_CONF", "CANT_DIAS");
		refHora = referenciasUtilFormController.getReferenciaDescLarga(
				"REMUNERACION_CONF", "HORA_DESDE");
		refAutomaticoManual = referenciasUtilFormController
				.getReferenciaDescLarga("REMUNERACION_STOCK",
						"AUTOMATICO_MANUAL");
		refCantDiasUpdate = referenciasUtilFormController.getReferenciaDescLarga(
				"ANEXO_CONF", "CANT_DIAS");
		refHoraUpdate = referenciasUtilFormController.getReferenciaDescLarga(
				"ANEXO_CONF", "HORA_DESDE");
		refAutoDias = new Referencias();
		refAutoHora = new Referencias();
		if (refAutomaticoManual != null
				&& refAutomaticoManual.getValorAlf().equalsIgnoreCase("A")) {
			mostrarPanel = true;

		}
		refAutoDias = referenciasUtilFormController.getReferenciaDescLarga(
				"REMUNERACION_STOCK", "CANT_DIAS");
		refAutoHora = referenciasUtilFormController.getReferenciaDescLarga(
				"REMUNERACION_STOCK", "HORA");
		obtenerSelectItems();
		obtenerDatos();
	}

	private void obtenerDatos() {
		String sql = "select config.* from remuneracion.remun_config config where config.activo is true";
		listaConfiguraciones = em.createNativeQuery(sql, RemunConfig.class)
				.getResultList();
	}

	public void actualizarStock() {
		if (procesoUpdateCat722FC == null) {
			procesoUpdateCat722FC = (ProcesoUpdateCat722FC) org.jboss.seam.Component
					.getInstance(ProcesoUpdateCat722FC.class, true);

		}
		procesoUpdateCat722FC.procesoPrincipalManual();

	}
	
	public void actualizarEntidadOee() {
		if (procesoUpdateEntidadOee763FC == null) {
			procesoUpdateEntidadOee763FC = (ProcesoUpdateEntidadOee763FC) org.jboss.seam.Component
					.getInstance(ProcesoUpdateEntidadOee763FC.class, true);

		}
		procesoUpdateEntidadOee763FC.procesoPrincipal();

	}

	private void obtenerSelectItems() {
		String cad = "SELECT R.* FROM SELECCION.REFERENCIAS R "
				+ "WHERE R.ACTIVO = TRUE "
				+ "AND R.DOMINIO = 'REMUNERACION_ORIGEN' "
				+ "ORDER BY R.DESC_LARGA";
		List<Referencias> lista = em.createNativeQuery(cad, Referencias.class)
				.getResultList();
		origenSelectItems = new ArrayList<SelectItem>();
		origenSelectItems.add(new SelectItem(null, SeamResourceBundle
				.getBundle().getString("opt_select_one")));
		for (Referencias d : lista)
			origenSelectItems.add(new SelectItem(d.getIdReferencias(), ""
					+ d.getDescLarga()));
	}

	private void cargarNiveentidadOee() {

		nivelEntidadOeeUtil.init2();

	}

	public void mostrar() {
		if (refAutomaticoManual.getValorAlf().equalsIgnoreCase("A"))
			mostrarPanel = true;
		else
			mostrarPanel = false;
	}

	/**
	 * @param horaCad
	 * @return
	 */
	private int[] getHora(String horaCad) {
		String[] horas = horaCad.split(":");
		if (horas.length != 2) {
			return null;
		} else {
			String hora = horas[0];
			String minuto = horas[1];
			try {
				int hh = Integer.parseInt(hora);
				int mm = Integer.parseInt(minuto);

				if (hh < 0 || hh > 23 || mm < 0 || mm >= 60) {
					return null;
				}
				int[] v = new int[2];
				v[0] = hh;
				v[1] = mm;
				return v;
			} catch (Exception e) {
				return null;
			}
		}
	}

	private boolean validarDatosAdd() {
		if (nivelEntidadOeeUtil.getIdConfigCab() == null) {
			statusMessages
					.add(Severity.ERROR,
							"Debe completar el campo OEE antes de realizar esta acci&oacute;n");
			return false;
		}
		if (refCantDias == null || refCantDias.getValorNum() == null) {
			statusMessages
					.add(Severity.ERROR,
							"Debe completar el campo Cantidad de días antes de realizar esta acci&oacute;n");
			return false;
		}
		if (refCantDias.getValorNum() < 0) {
			statusMessages
					.add("El campo Cantidad de días antes debe ser un número positivo");
			return false;
		}

		if (refCantDiasUpdate == null || refCantDiasUpdate.getValorNum() == null) {
			statusMessages
					.add(Severity.ERROR,
							"Debe completar el campo Cantidad de días antes de realizar esta acci&oacute;n");
			return false;
		}
		if (refCantDiasUpdate.getValorNum() < 0) {
			statusMessages
					.add("El campo Cantidad de días antes debe ser un número positivo");
			return false;
		}

		if (refHora == null) {
			statusMessages
					.add(Severity.ERROR,
							"Debe completar el campo A partir de las antes de realizar esta acci&oacute;n");
			return false;
		}

		int[] horas = getHora(refHora.getValorAlf());
		if (horas == null) {
			statusMessages.add(Severity.ERROR,
					"Ingrese un valor válido en el campo A partir de ");
			return false;
		}
		if (horas[0] > 23 || horas[0] < 0 || horas[1] > 60 || horas[1] < 0) {
			statusMessages.add(Severity.ERROR,
					"Ingrese un valor válido en el campo A partir de ");
			return false;
		}
		
		if (refHora == null) {
			statusMessages
					.add(Severity.ERROR,
							"Debe completar el campo A partir de las antes de realizar esta acci&oacute;n");
			return false;
		}

		int[] horas2 = getHora(refHoraUpdate.getValorAlf());
		if (horas2 == null) {
			statusMessages.add(Severity.ERROR,
					"Ingrese un valor válido en el campo A partir de ");
			return false;
		}
		if (horas2[0] > 23 || horas2[0] < 0 || horas2[1] > 60 || horas2[1] < 0) {
			statusMessages.add(Severity.ERROR,
					"Ingrese un valor válido en el campo A partir de ");
			return false;
		}
		if (refAutomaticoManual.getValorAlf().equalsIgnoreCase("A")) {
			if (refAutoDias == null || refAutoDias.getValorNum() == null) {
				statusMessages
						.add(Severity.ERROR,
								"Debe completar el campo Cantidad de días antes de realizar esta acci&oacute;n");
				return false;
			}
			if (refAutoDias.getValorNum() < 0) {
				statusMessages
						.add(Severity.ERROR,
								"El campo Cantidad de días antes debe ser un número positivo");
				return false;
			}
			int[] hora = getHora(refAutoHora.getValorAlf());
			if (hora == null) {
				statusMessages.add(Severity.ERROR,
						"Ingrese un valor válido en el campo A partir de ");
				return false;
			}
			if (hora[0] > 23 || hora[0] < 0 || hora[1] > 60 || hora[1] < 0) {
				statusMessages.add(Severity.ERROR,
						"Ingrese un valor válido en el campo A partir de ");
				return false;
			}
		}
		if (idOrigen == null) {
			statusMessages
					.add(Severity.ERROR,
							"Seleccione Origen de cobro antes de realizar esta acci&oacute;n");
			return false;
		}
		if (em.find(Referencias.class, idOrigen).getDescLarga()
				.equalsIgnoreCase("SINARH")) {
			ConfiguracionUoCab uoCab = em.find(ConfiguracionUoCab.class,
					nivelEntidadOeeUtil.getIdConfigCab());
			String codSinarh = null;
			if (uoCab != null)
				codSinarh = uoCab.getCodigoSinarh();
			if (codSinarh == null || codSinarh.trim().isEmpty()) {
				statusMessages.add(Severity.ERROR,
						"El OEE no tiene asignado Código de SINARH. Verifique");
				return false;
			}
		}

		if (notificaciones == null || notificaciones.trim().equals("")) {
			statusMessages
					.add(Severity.ERROR,
							"Debe completar el campo email antes de realizar esta acci&oacute;n");
			return false;
		}
		String[] listaMails = notificaciones.split(";");
		boolean mailsValido = true;
		for (String o : listaMails) {
			if (!General.isEmail(o.trim()))
				mailsValido = false;

		}
		if (!mailsValido) {
			statusMessages.add(Severity.ERROR,
					"El mail ingresado no es valido, verifique");
			return false;
		}

		return true;
	}

	public void addRow() {
		try {
			if (!validarDatosAdd())
				return;
			RemunConfig remunConfig = new RemunConfig();
			remunConfig.setActivo(true);
			remunConfig.setConfiguracionUoCab(em.find(ConfiguracionUoCab.class,
					nivelEntidadOeeUtil.getIdConfigCab()));
			remunConfig.setEMail(notificaciones);
			remunConfig.setFechaAlta(new Date());
			remunConfig.setOrigen(em.find(Referencias.class, idOrigen)
					.getValorAlf());
			remunConfig.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			listaConfiguraciones.add(remunConfig);
			limpiarDatos();
			statusMessages.clear();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void updatedRow() {
		try {
			if (!validarDatosAdd())
				return;
			RemunConfig remunConfig = new RemunConfig();
			remunConfig = listaConfiguraciones.get(index);
			remunConfig.setConfiguracionUoCab(em.find(ConfiguracionUoCab.class,
					nivelEntidadOeeUtil.getIdConfigCab()));
			remunConfig.setEMail(notificaciones);
			remunConfig.setFechaMod(new Date());
			remunConfig.setOrigen(em.find(Referencias.class, idOrigen)
					.getValorAlf());
			remunConfig.setUsuMod(usuarioLogueado.getCodigoUsuario());
			listaConfiguraciones.set(index, remunConfig);
			limpiarDatos();
			statusMessages.clear();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void limpiarDatos() {
		try {

			esEdit = false;
			idOrigen = null;
			nivelEntidadOeeUtil.limpiar();
			notificaciones = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void editRow(int ix) {
		esEdit = true;
		index = ix;
		RemunConfig remunConfig = new RemunConfig();
		remunConfig = listaConfiguraciones.get(index);
		notificaciones = remunConfig.getEMail();
		nivelEntidadOeeUtil.setIdConfigCab(remunConfig.getConfiguracionUoCab()
				.getIdConfiguracionUo());
		cargarNiveentidadOee();
		String x = obtenerOrigenSeleccionado(remunConfig.getOrigen());

	}

	public String obtenerOrigenSeleccionado(String valor) {
		String sql = "SELECT R.* " + "FROM SELECCION.REFERENCIAS R "
				+ "WHERE R.ACTIVO = TRUE "
				+ "AND R.DOMINIO = 'REMUNERACION_ORIGEN' "
				+ "AND R.VALOR_ALF = '" + valor + "'";
		List<Referencias> lista = em.createNativeQuery(sql, Referencias.class)
				.getResultList();
		if (!lista.isEmpty()) {
			idOrigen = lista.get(0).getIdReferencias();
			return lista.get(0).getDescLarga();
		}
		return "";
	}

	public void delectRow(int index) {
		RemunConfig config = listaConfiguraciones.get(index);
		if (config.getIdRemunConfig() != null)
			inactivarConfiguraciones.add(config);
		listaConfiguraciones.remove(index);
		statusMessages.clear();
		statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
				.getString("GENERICO_MSG"));

	}

	private boolean validarDatosSave() {

		if (refCantDias == null || refCantDias.getValorNum() == null) {
			statusMessages
					.add(Severity.ERROR,
							"Debe completar el campo Cantidad de días antes de realizar esta acci&oacute;n");
			return false;
		}
		if (refCantDias.getValorNum() < 0) {
			statusMessages
					.add("El campo Cantidad de días antes debe ser un número positivo");
			return false;
		}
		
		if (refCantDiasUpdate == null || refCantDiasUpdate.getValorNum() == null) {
			statusMessages
					.add(Severity.ERROR,
							"Debe completar el campo Cantidad de días antes de realizar esta acci&oacute;n");
			return false;
		}
		if (refCantDiasUpdate.getValorNum() < 0) {
			statusMessages
					.add("El campo Cantidad de días antes debe ser un número positivo");
			return false;
		}

		if (refHora == null) {
			statusMessages
					.add(Severity.ERROR,
							"Debe completar el campo A partir de las antes de realizar esta acci&oacute;n");
			return false;
		}

		int[] horas = getHora(refHora.getValorAlf());
		if (horas == null) {
			statusMessages.add(Severity.ERROR,
					"Ingrese un valor válido en el campo A partir de ");
			return false;
		}
		if (horas[0] > 23 || horas[0] < 0 || horas[1] > 60 || horas[1] < 0) {
			statusMessages.add(Severity.ERROR,
					"Ingrese un valor válido en el campo A partir de ");
			return false;
		}
		
		if (refHora == null) {
			statusMessages
					.add(Severity.ERROR,
							"Debe completar el campo A partir de las antes de realizar esta acci&oacute;n");
			return false;
		}

		int[] hora2 = getHora(refHoraUpdate.getValorAlf());
		if (hora2 == null) {
			statusMessages.add(Severity.ERROR,
					"Ingrese un valor válido en el campo A partir de ");
			return false;
		}
		if (hora2[0] > 23 || hora2[0] < 0 || hora2[1] > 60 || hora2[1] < 0) {
			statusMessages.add(Severity.ERROR,
					"Ingrese un valor válido en el campo A partir de ");
			return false;
		}
		if (refAutomaticoManual.getValorAlf().equalsIgnoreCase("A")) {
			if (refAutoDias == null || refAutoDias.getValorNum() == null) {
				statusMessages
						.add(Severity.ERROR,
								"Debe completar el campo Cantidad de días antes de realizar esta acci&oacute;n");
				return false;
			}
			if (refAutoDias.getValorNum() < 0) {
				statusMessages
						.add(Severity.ERROR,
								"El campo Cantidad de días antes debe ser un número positivo");
				return false;
			}
			int[] hora = getHora(refAutoHora.getValorAlf());
			if (hora == null) {
				statusMessages.add(Severity.ERROR,
						"Ingrese un valor válido en el campo A partir de ");
				return false;
			}
			if (hora[0] > 23 || hora[0] < 0 || hora[1] > 60 || hora[1] < 0) {
				statusMessages.add(Severity.ERROR,
						"Ingrese un valor válido en el campo A partir de ");
				return false;
			}
		}
		if (listaConfiguraciones.isEmpty()) {
			statusMessages
					.add(Severity.ERROR,
							"Agregue al menos un registro lista de Origenes de  Cobros por OEE antes de realizar esta acci&oacute;n");
			return false;
		}

		return true;
	}

	public void save() {
		try {
			if (!validarDatosSave())
				return;
			refCantDias.setFechaMod(new Date());
			refCantDias.setUsuMod(usuarioLogueado.getCodigoUsuario());
			em.merge(refCantDias);
			refCantDiasUpdate.setFechaMod(new Date());
			refCantDiasUpdate.setUsuMod(usuarioLogueado.getCodigoUsuario());
			em.merge(refCantDiasUpdate);
			refHora.setFechaMod(new Date());
			refHora.setUsuMod(usuarioLogueado.getCodigoUsuario());
			em.merge(refHora);
			refHoraUpdate.setFechaMod(new Date());
			refHoraUpdate.setUsuMod(usuarioLogueado.getCodigoUsuario());
			em.merge(refHoraUpdate);
			refAutomaticoManual.setFechaMod(new Date());
			refAutomaticoManual.setUsuMod(usuarioLogueado.getCodigoUsuario());
			em.merge(refAutomaticoManual);
			if (refAutomaticoManual.getValorAlf().equalsIgnoreCase("A")) {
				refAutoDias.setFechaMod(new Date());
				refAutoDias.setUsuMod(usuarioLogueado.getCodigoUsuario());
				em.merge(refAutoDias);
				refAutoHora.setFechaMod(new Date());
				refAutoHora.setUsuMod(usuarioLogueado.getCodigoUsuario());
				em.merge(refAutoHora);
			}

			for (RemunConfig c : listaConfiguraciones) {
				if (c.getIdRemunConfig() == null)
					em.persist(c);
				else {
					c.setUsuMod(usuarioLogueado.getCodigoUsuario());
					c.setFechaMod(new Date());
					em.merge(c);
				}
			}

			for (RemunConfig conf : inactivarConfiguraciones) {
				conf.setActivo(false);
				conf.setUsuMod(usuarioLogueado.getCodigoUsuario());
				conf.setFechaMod(new Date());
				em.merge(conf);
			}
			em.flush();
			statusMessages.clear();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
		} catch (Exception e) {
			statusMessages.add(Severity.ERROR, e.getMessage());
			return;
		}

	}

	public Referencias getRefCantDias() {
		return refCantDias;
	}

	public void setRefCantDias(Referencias refCantDias) {
		this.refCantDias = refCantDias;
	}

	public Referencias getRefHora() {
		return refHora;
	}

	public void setRefHora(Referencias refHora) {
		this.refHora = refHora;
	}

	public Referencias getRefAutomaticoManual() {
		return refAutomaticoManual;
	}

	public void setRefAutomaticoManual(Referencias refAutomaticoManual) {
		this.refAutomaticoManual = refAutomaticoManual;
	}

	public Referencias getRefAutoDias() {
		return refAutoDias;
	}

	public void setRefAutoDias(Referencias refAutoDias) {
		this.refAutoDias = refAutoDias;
	}

	public Referencias getRefAutoHora() {
		return refAutoHora;
	}

	public void setRefAutoHora(Referencias refAutoHora) {
		this.refAutoHora = refAutoHora;
	}

	public Boolean getMostrarPanel() {
		return mostrarPanel;
	}

	public void setMostrarPanel(Boolean mostrarPanel) {
		this.mostrarPanel = mostrarPanel;
	}

	public List<SelectItem> getOrigenSelectItems() {
		return origenSelectItems;
	}

	public void setOrigenSelectItems(List<SelectItem> origenSelectItems) {
		this.origenSelectItems = origenSelectItems;
	}

	public Long getIdOrigen() {
		return idOrigen;
	}

	public void setIdOrigen(Long idOrigen) {
		this.idOrigen = idOrigen;
	}

	public String getNotificaciones() {
		return notificaciones;
	}

	public void setNotificaciones(String notificaciones) {
		this.notificaciones = notificaciones;
	}

	public Boolean getEsEdit() {
		return esEdit;
	}

	public void setEsEdit(Boolean esEdit) {
		this.esEdit = esEdit;
	}

	public List<RemunConfig> getListaConfiguraciones() {
		return listaConfiguraciones;
	}

	public void setListaConfiguraciones(List<RemunConfig> listaConfiguraciones) {
		this.listaConfiguraciones = listaConfiguraciones;
	}

	public Integer getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}

	public List<RemunConfig> getInactivarConfiguraciones() {
		return inactivarConfiguraciones;
	}

	public void setInactivarConfiguraciones(
			List<RemunConfig> inactivarConfiguraciones) {
		this.inactivarConfiguraciones = inactivarConfiguraciones;
	}

	public Referencias getRefCantDiasUpdate() {
		return refCantDiasUpdate;
	}

	public void setRefCantDiasUpdate(Referencias refCantDiasUpdate) {
		this.refCantDiasUpdate = refCantDiasUpdate;
	}

	public Referencias getRefHoraUpdate() {
		return refHoraUpdate;
	}

	public void setRefHoraUpdate(Referencias refHoraUpdate) {
		this.refHoraUpdate = refHoraUpdate;
	}
	
	

}
