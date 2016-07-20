package py.com.excelsis.sicca.session;

import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import py.com.excelsis.sicca.entity.ClasificadorUo;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;

@Name("clasificadorUoHome")
public class ClasificadorUoHome extends EntityHome<ClasificadorUo> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4713168874087022730L;

	@In
	FacesMessages facesMessages;

	@In(required = false)
	Usuario usuarioLogueado;

	@In
	StatusMessages statusMessages;

	@In(required = false, create = true)
	SeguridadUtilFormController seguridadFC;

	@In(value = "entityManager")
	EntityManager em;

	String valorOneRadio;

	@Override
	protected ClasificadorUo loadInstance() {
		ClasificadorUo o = super.loadInstance();
		return o;
	}

	// Value holders for selectItems if exists
	public static final String CONTEXT_VAR_NAME = "clasificadorUos";
	public static final String[] CONTEXT_VAR_NAMES = { CONTEXT_VAR_NAME,
		CONTEXT_VAR_NAME + "SelectItems" };

	@SuppressWarnings("unchecked")
	@Factory(value = CONTEXT_VAR_NAME, scope = ScopeType.EVENT)
	public List<ClasificadorUo> getClasificadorUos() {
		try {
			return getEntityManager().createQuery(" select o from "
				+ ClasificadorUo.class.getName() + " o").getResultList();
		} catch (Exception ex) {
			return new Vector<ClasificadorUo>();
		}
	}

	@SuppressWarnings("unchecked")
	@Factory(value = CONTEXT_VAR_NAME + "SelectItems", scope = ScopeType.EVENT)
	public List<SelectItem> getClasificadorUoSelectItems() {
		List<SelectItem> si = new Vector<SelectItem>();
		for (ClasificadorUo o : getClasificadorUos())
			si.add(new SelectItem(o.getIdClasificadorUo(), "" + o.getDenominacionUnidad()));
		return si;
	}

	public void setClasificadorUoIdClasificadorUo(Long id) {
		setId(id);
	}

	public Long getClasificadorUoIdClasificadorUo() {
		return (Long) getId();
	}

	@Override
	protected ClasificadorUo createInstance() {
		ClasificadorUo clasificadorUo = new ClasificadorUo();
		return clasificadorUo;
	}

	public void load() {
		if (isIdDefined()) {
			wire();

		}
	}

	public void wire() {
		getInstance();
		if (getInstance().getIdClasificadorUo() == null)
			getInstance().setActivo(true);
		if (getInstance().getIdClasificadorUo() != null) {
			if (getInstance().getPolitica() && !getInstance().getLinea())
				valorOneRadio = "1";
			else
				valorOneRadio = "2";
		}
	}

	public boolean isWired() {
		return true;
	}

	public ClasificadorUo getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	@Override
	public String persist() {
		if (!checkData())
			return null;
		getInstance().setFechaAlta(new Date());
		getInstance().setUsuAlta(usuarioLogueado.getCodigoUsuario());
		getInstance().setDenominacionUnidad(getInstance().getDenominacionUnidad().trim().toUpperCase());
		if (valorOneRadio.equals("1")) {
			getInstance().setPolitica(true);
			getInstance().setLinea(false);
		}
		if (valorOneRadio.equals("2")) {
			getInstance().setPolitica(false);
			getInstance().setLinea(true);
		}
		return AppHelper.removeFromContext("persist", super.persist(), CONTEXT_VAR_NAMES, getEventContext());
	}

	@Override
	public String update() {
		if (!checkData())
			return null;
		getInstance().setFechaMod(new Date());
		getInstance().setUsuMod(usuarioLogueado.getCodigoUsuario());
		getInstance().setDenominacionUnidad(getInstance().getDenominacionUnidad().trim().toUpperCase());
		if (valorOneRadio.equals("1")) {
			getInstance().setPolitica(true);
			getInstance().setLinea(false);
		}
		if (valorOneRadio.equals("2")) {
			getInstance().setPolitica(false);
			getInstance().setLinea(true);
		}
		return AppHelper.removeFromContext("updated", super.update(), CONTEXT_VAR_NAMES, getEventContext());
	}

	private boolean checkData() {
		if (seguridadFC == null) {
			seguridadFC =
				(SeguridadUtilFormController) Component.getInstance(SeguridadUtilFormController.class, true);
		}
		String operation = isIdDefined() ? "update" : "persist";
		if (getInstance().getDenominacionUnidad().trim().isEmpty()) {
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("msg_descripcion_invalida"));
			return false;
		}
		/*try {
			if (!seguridadFC.validarInput(getInstance().getDenominacionUnidad(), "STRING", 200)) {
				statusMessages.add(Severity.ERROR, "Caracteres inválidos o longitud inapropiada (Máx. 200) en el campo Denominación ");
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}*/
		if (!checkDuplicate(operation)) {
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("msg_registro_duplicado"));
			return false;
		}
		if (!controlarRepetidoNivelSubNivelNumero(operation))
			return false;
		
		return true;
	}

	private Boolean controlarRepetidoNivelSubNivelNumero(String op) {
		String cSeparador = "#";

		cSeparador =
			getInstance().getNivel() + cSeparador + getInstance().getSubnivel() + cSeparador
				+ getInstance().getNro();
		String query = "select ClasificadorUo from ClasificadorUo ClasificadorUo where nivel =:nivel and subnivel = :subnivel and nro = :nro ";
		if (op.equalsIgnoreCase("update")) 
			query += " and  idClasificadorUo !=:id";
		Query q =
			em.createQuery(query);
		q.setParameter("nivel", getInstance().getNivel());
		q.setParameter("subnivel", getInstance().getSubnivel());
		q.setParameter("nro", getInstance().getNro());
		if (op.equalsIgnoreCase("update")) 
			q.setParameter("id", getInstance().getIdClasificadorUo());
		List lista = q.getResultList();
		if (lista.size() > 0) {
			statusMessages.add(Severity.ERROR, "Ya existe la combinación de Nivel, Subnivel y Número: "+getInstance().getNivel()+", "+getInstance().getSubnivel()+", "+getInstance().getNro()+" respectivamente.");
			return false;
		}
		return true;

	}

	@SuppressWarnings("unchecked")
	private boolean checkDuplicate(String operation) {
		String hql =
			"SELECT o FROM ClasificadorUo o WHERE lower(o.denominacionUnidad) = '"
				+ getInstance().getDenominacionUnidad().trim().toLowerCase() + "' ";
		if (operation.equalsIgnoreCase("update")) {
			hql += " AND o.idClasificadorUo != " + getInstance().getIdClasificadorUo().longValue();
		}
		List<ClasificadorUo> list = getEntityManager().createQuery(hql).getResultList();
		return list.isEmpty();
	}

	@Override
	public void setInstance(ClasificadorUo instance) {
		if (instance != null) {
			super.setId(instance.getId());
		}
		super.setInstance(instance);
	}

	public String getValorOneRadio() {
		return valorOneRadio;
	}

	public void setValorOneRadio(String valorOneRadio) {
		this.valorOneRadio = valorOneRadio;
	}

	// Public getters and setters if exists

}
