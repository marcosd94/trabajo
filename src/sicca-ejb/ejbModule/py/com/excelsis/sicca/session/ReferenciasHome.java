package py.com.excelsis.sicca.session;

import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.faces.model.SelectItem;

import py.com.excelsis.sicca.entity.CondicionTrabajoEspecif;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;

@Name("referenciasHome")
public class ReferenciasHome extends EntityHome<Referencias> {

	@In
	StatusMessages statusMessages;

	@In(required = false)
	Usuario usuarioLogueado;

	public static final String CONTEXT_VAR_NAME = "referencias";
	public static final String[] CONTEXT_VAR_NAMES = { CONTEXT_VAR_NAME,
			CONTEXT_VAR_NAME + "SelectItems" };

	@SuppressWarnings("unchecked")
	@Factory(value = CONTEXT_VAR_NAME, scope = ScopeType.EVENT)
	public List<Referencias> getReferencias() {
		try {
			return getEntityManager()
					.createQuery(
							" select o from "
									+ Referencias.class.getName()
									+ " o"
									+ " WHERE o.activo = true ORDER BY o.dominio, o.descLarga")
					.getResultList();
		} catch (Exception ex) {
			return new Vector<Referencias>();
		}
	}

	@Factory(value = CONTEXT_VAR_NAME + "SelectItems", scope = ScopeType.EVENT)
	public List<SelectItem> getReferenciasSelectItems() {
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString(
				"opt_select_one")));
		for (Referencias o : getReferencias())
			si.add(new SelectItem(o.getIdReferencias(), "" + o.getDominio()
					+ " - " + o.getDescAbrev()));
		return si;
	}

	@SuppressWarnings("unchecked")
	public List<Referencias> getReferenciasBySexo() {
		try {
			return getEntityManager()
					.createQuery(
							" select o from "
									+ Referencias.class.getName()
									+ " o"
									+ " WHERE o.activo = true and o.dominio = 'SEXO' ORDER BY o.dominio, o.descLarga")
					.getResultList();
		} catch (Exception ex) {
			return new Vector<Referencias>();
		}
	}

	@Factory(value = CONTEXT_VAR_NAME + "BySexoSelectItems", scope = ScopeType.EVENT)
	public List<SelectItem> getReferenciasBySexoSelectItems() {
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
		for (Referencias o : getReferenciasBySexo())
			si.add(new SelectItem(o.getIdReferencias(), "" + o.getDescLarga()));
		return si;
	}
	
	@SuppressWarnings("unchecked")
	public List<Referencias> getReferenciasByTelContacto() {
		try {
			return getEntityManager()
					.createQuery(
							" select o from "
									+ Referencias.class.getName()
									+ " o"
									+ " WHERE o.activo = true and o.dominio = 'TEL_CONTACTO' ORDER BY o.dominio, o.descLarga")
					.getResultList();
		} catch (Exception ex) {
			return new Vector<Referencias>();
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<Referencias> getReferenciasByEstadoActual() {
		try {
			return getEntityManager()
					.createQuery(
							" select o from "
									+ Referencias.class.getName()
									+ " o"
									+ " WHERE o.activo = true and o.dominio = 'ESTADO_ACTUAL' ORDER BY o.dominio, o.descLarga")
					.getResultList();
		} catch (Exception ex) {
			return new Vector<Referencias>();
		}
	}	
	
	@Factory(value = CONTEXT_VAR_NAME + "ByTelContactoSelectItems", scope = ScopeType.EVENT)
	public List<SelectItem> getReferenciasByTelContactoSelectItems() {
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
		for (Referencias o : getReferenciasByTelContacto())
			si.add(new SelectItem(o.getIdReferencias(), "" + o.getDescLarga()));
		return si;
	}
	
	@Factory(value = CONTEXT_VAR_NAME + "ByEstadoActualSelectItems", scope = ScopeType.EVENT)
	public List<SelectItem> getReferenciasByEstadoActualSelectItems() {
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
		for (Referencias o : getReferenciasByEstadoActual())
			si.add(new SelectItem(o.getIdReferencias(), "" + o.getDescLarga()));
		return si;
	}
	
	@SuppressWarnings("unchecked")
	public List<Referencias> getReferenciasByEstadoCivil() {
		try {
			return getEntityManager()
					.createQuery(
							" select o from "
									+ Referencias.class.getName()
									+ " o"
									+ " WHERE o.activo = true and o.dominio = 'ESTADO_CIVIL' ORDER BY o.dominio, o.descLarga")
					.getResultList();
		} catch (Exception ex) {
			return new Vector<Referencias>();
		}
	}

	@Factory(value = CONTEXT_VAR_NAME + "ByEstadoCivilSelectItems", scope = ScopeType.EVENT)
	public List<SelectItem> getReferenciasByEstadoCivilSelectItems() {
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
		for (Referencias o : getReferenciasByEstadoCivil())
			si.add(new SelectItem(o.getIdReferencias(), "" + o.getDescLarga()));
		return si;
	}

	@SuppressWarnings("unchecked")
	@Factory(value = "referenciasUnidadMedida", scope = ScopeType.EVENT)
	public List<Referencias> getReferenciasUnidadMedida() {
		try {
			return getEntityManager()
					.createQuery(
							" select o from "
									+ Referencias.class.getName()
									+ " o"
									+ " WHERE o.activo = true and o.dominio like 'UNIDAD_MEDIDA' "
									+ " ORDER BY  o.descLarga").getResultList();
		} catch (Exception ex) {
			return new Vector<Referencias>();
		}
	}

	@Factory(value = "referenciasUnidadMedidaSelectItems", scope = ScopeType.EVENT)
	public List<SelectItem> getReferenciasUnidadMedidaSelectItems() {
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString(
				"opt_select_one")));
		for (Referencias o : getReferenciasUnidadMedida())
			si.add(new SelectItem(o.getIdReferencias(), "" + o.getDescLarga()));
		return si;
	}

	@SuppressWarnings("unchecked")
	public List<Referencias> getIdiomaOpc() {
		try {
			return getEntityManager()
					.createQuery(
							" select o from "
									+ Referencias.class.getName()
									+ " o"
									+ " WHERE o.activo = true and o.dominio like 'IDIOMAS_OPC' "
									+ " ORDER BY  o.descLarga").getResultList();
		} catch (Exception ex) {
			return new Vector<Referencias>();
		}
	}

	@Factory(value = "idiomaOpcSelectItems", scope = ScopeType.EVENT)
	public List<SelectItem> getIdiomaOpcSelectItems() {
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString(
				"opt_select_one")));
		for (Referencias o : getIdiomaOpc())
			si.add(new SelectItem(o.getIdReferencias(), "" + o.getDescLarga()));
		return si;
	}
	
	
	
	@SuppressWarnings("unchecked")
	public List<Referencias> getReferenciasBecas() {
		try {
			return getEntityManager()
					.createQuery(
							" select o from "
									+ Referencias.class.getName()
									+ " o"
									+ " WHERE o.activo = true and o.dominio like 'COSTO_CAPOG290' "
									+ " ORDER BY  o.descLarga").getResultList();
		} catch (Exception ex) {
			return new Vector<Referencias>();
		}
	}

	@Factory(value = "becasSelectItems", scope = ScopeType.EVENT)
	public List<SelectItem> getReferenciasBecasSelectItems() {
		List<SelectItem> si = new Vector<SelectItem>();
		for (Referencias o : getReferenciasBecas())
			si.add(new SelectItem(o.getValorAlf(), "" + o.getDescLarga()));
		return si;
	}
	
	@SuppressWarnings("unchecked")
	public List<Referencias> getReferenciasNivelAgrupacionCAPCU503() {
		try {
			return getEntityManager()
					.createQuery(
							" select o from "
									+ Referencias.class.getName()
									+ " o"
									+ " WHERE o.activo = true and o.dominio like 'CAP_CU503' "
									+ " ORDER BY  o.descLarga desc").getResultList();
		} catch (Exception ex) {
			return new Vector<Referencias>();
		}
	}

	@Factory(value = "nivelAgrupacionCAPCU503SelectItems", scope = ScopeType.EVENT)
	public List<SelectItem> getNivelAgrupacionCAPCU503SelectItems() {
		List<SelectItem> si = new Vector<SelectItem>();
		for (Referencias o : getReferenciasNivelAgrupacionCAPCU503())
			si.add(new SelectItem(o.getValorAlf(), "" + o.getDescLarga()));
		return si;
	}

	public void setReferenciasIdReferencias(Long id) {
		setId(id);
	}

	public Long getReferenciasIdReferencias() {
		return (Long) getId();
	}

	
	@SuppressWarnings("unchecked")
	public List<Referencias> getReferenciasByMes() {
		try {
			return getEntityManager()
					.createQuery(
							" select o from "
									+ Referencias.class.getName()
									+ " o"
									+ " WHERE o.activo = true and o.dominio = 'MESES' ORDER BY o.valorNum")
					.getResultList();
		} catch (Exception ex) {
			return new Vector<Referencias>();
		}
	}

	@Factory(value = CONTEXT_VAR_NAME + "ByMesSelectItems", scope = ScopeType.EVENT)
	public List<SelectItem> getReferenciasByMesSelectItems() {
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString(
				"opt_select_one")));
		for (Referencias o : getReferenciasByMes())
			si.add(new SelectItem(o.getIdReferencias(), "" + o.getDescAbrev()));
		return si;
	}

	
	@Override
	protected Referencias createInstance() {
		Referencias referencias = new Referencias();
		return referencias;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		if (!isIdDefined()) {
			getInstance().setActivo(true);
		}
	}

	public boolean isWired() {
		return true;
	}

	public Referencias getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	@Override
	public String persist() {
		if (!checkData())
			return null;
		getInstance().setDominio(
				getInstance().getDominio().trim().toUpperCase());
		getInstance().setDescAbrev(
				getInstance().getDescAbrev() != null ? getInstance()
						.getDescAbrev().trim().toUpperCase() : null);
		getInstance().setDescLarga(
				getInstance().getDescLarga() != null ? getInstance()
						.getDescLarga().trim().toUpperCase() : null);
		getInstance().setValorAlf(
				getInstance().getValorAlf() != null ? getInstance()
						.getValorAlf().trim().toUpperCase() : null);
		getInstance().setUsuAlta(
				usuarioLogueado.getCodigoUsuario().trim().toUpperCase());
		getInstance().setFechaAlta(new Date());

		return AppHelper.removeFromContext("persist", super.persist(),
				CONTEXT_VAR_NAMES, getEventContext());
	}

	@Override
	public String update() {
		if (!checkData())
			return null;
		getInstance().setDominio(
				getInstance().getDominio().trim().toUpperCase());
		getInstance().setDescAbrev(
				getInstance().getDescAbrev() != null ? getInstance()
						.getDescAbrev().trim().toUpperCase() : null);
		getInstance().setDescLarga(
				getInstance().getDescLarga() != null ? getInstance()
						.getDescLarga().trim().toUpperCase() : null);
		getInstance().setValorAlf(
				getInstance().getValorAlf() != null ? getInstance()
						.getValorAlf().trim().toUpperCase() : null);
		getInstance().setUsuMod(
				usuarioLogueado.getCodigoUsuario().trim().toUpperCase());
		getInstance().setFechaMod(new Date());

		return AppHelper.removeFromContext("updated", super.update(),
				CONTEXT_VAR_NAMES, getEventContext());
	}

	private boolean checkData() {
		String operation = isIdDefined() ? "update" : "persist";
		SeguridadUtilFormController sufc =
			(SeguridadUtilFormController) Component.getInstance(SeguridadUtilFormController.class, true);
		if(getInstance().getDominio()!=null && sufc.contieneCaracter(getInstance().getDominio())){
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("msg_caracter"));
			return false;
		}
		if(getInstance().getDescAbrev()!=null &&  sufc.contieneCaracter(getInstance().getDescAbrev())){
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("msg_caracter"));
			return false;
		}
		if(getInstance().getDescLarga()!=null && sufc.contieneCaracter(getInstance().getDescLarga())){
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("msg_caracter"));
			return false;
		}
		if(getInstance().getValorAlf()!=null && sufc.contieneCaracter(getInstance().getValorAlf())){
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("msg_caracter"));
			return false;
		}
		
		if(getInstance().getValorNum()!=null && getInstance().getValorNum().intValue()<=0){
			statusMessages.add(Severity.ERROR,"El valor no debe ser menor a cero");
			return false;
		}
		if (!checkDuplicate(operation)) {
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle()
					.getString("msg_registro_duplicado"));
			return false;
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	private boolean checkDuplicate(String operation) {
		String hql = "SELECT o FROM Referencias o WHERE lower(o.dominio) = :dominio ";
		hql += " AND lower(o.descLarga) = :descLarga AND lower(o.descAbrev) = :descAbrev";
		if(operation.equals("update"))
			hql += " AND o.idReferencias != " + getInstance().getIdReferencias().longValue();

		List<Referencias> list = getEntityManager().createQuery(hql).setParameter("dominio", getInstance().getDominio().trim().toLowerCase() )
		.setParameter("descLarga",getInstance().getDescLarga().trim().toLowerCase()).setParameter("descAbrev",getInstance().getDescAbrev())
				.getResultList();
		return list.isEmpty();
	}
}
