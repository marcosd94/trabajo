package py.com.excelsis.sicca.legajo.session;

import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage.Severity;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;

import py.com.excelsis.sicca.entity.AuditLegajo;
import py.com.excelsis.sicca.entity.AuditLegajoDet;
import py.com.excelsis.sicca.entity.AuditLegajoObs;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.JbpmUtilFormController;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.PersonaDTO;
import py.com.excelsis.sicca.session.util.ReferenciasUtilFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("valDatosFamiliares677FC")
public class ValDatosFamiliares677FC {
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(create = true)
	JbpmUtilFormController jbpmUtilFormController;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(required = false, create = true)
	SeguridadUtilFormController seguridadUtilFormController;
	@In(create = true)
	ReferenciasUtilFormController referenciasUtilFormController;
	@In(required = false, create = true)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;
	@In(required = false, create = true)
	SeleccionUtilFormController seleccionUtilFormController;
	@In(required = false, create = true)
	AdmDatosFamilia644FC admDatosFamilia644FC;

	private Long idPersona;
	private String texto;
	private AuditLegajoObs auditLegajoObs = new AuditLegajoObs();
	private Boolean selectAll = false;

	private List<AuditLegajoDet> lGrilla2;

	public void init() {
		admDatosFamilia644FC.setIdPersona(idPersona);
		admDatosFamilia644FC.setTexto(texto);
		admDatosFamilia644FC.init();
		cargarGrilla2();
		initObs();
	}

	public void cargarGrilla2() {
		Query q =
			em.createQuery("select AuditLegajoDet from AuditLegajoDet AuditLegajoDet "
				+ " where AuditLegajoDet.estado = 2 and AuditLegajoDet.auditLegajo.persona.idPersona = :idPersona and AuditLegajoDet.estado = 2 ");
		q.setParameter("idPersona", idPersona);
		lGrilla2 = q.getResultList();
	}

	private Boolean precondSave() {
		if (lGrilla2 == null) {
			statusMessages.add(org.jboss.seam.international.StatusMessage.Severity.ERROR, "No hay nada que guardar");
			return false;
		}
		return true;
	}

	private void initObs() {
		Query q =
			em.createQuery("select AuditLegajoObs from AuditLegajoObs AuditLegajoObs "
				+ " where AuditLegajoObs.ficha = 2 and AuditLegajoObs.auditLegajo.persona.idPersona = :idPersona");
		q.setParameter("idPersona", idPersona);
		List<AuditLegajoObs> lista = q.getResultList();
		if (lista.size() > 0) {
			auditLegajoObs = lista.get(0);
		}
	}

	public void seleccionarTodo() {
		if (lGrilla2 != null) {
			if (selectAll)
				for (AuditLegajoDet o : lGrilla2) {
					o.setCheck(true);
				}
			if (!selectAll)
				for (AuditLegajoDet o : lGrilla2) {
					o.setCheck(false);
				}
		}
	}

	private void cambiarAuditEstadoCab() {
		Query q =
			em.createQuery("select AuditLegajoDet from AuditLegajoDet AuditLegajoDet "
				+ " where AuditLegajoDet.auditLegajo.persona.idPersona = :idPersona and AuditLegajoDet.estado = 2 ");
		q.setParameter("idPersona", idPersona);
		List<AuditLegajoDet> lista = q.getResultList();
		Boolean estadoDif = false;
		for (AuditLegajoDet o : lista) {
			if (o.getEstado().intValue() == 2) {
				estadoDif = true;
			}
		}

		if (!estadoDif && lista.size() > 0) {
			AuditLegajo auditLegajo = lista.get(0).getAuditLegajo();
			auditLegajo.setEstado(1);
			auditLegajo.setFechaMod(new Date());
			auditLegajo.setUsuMod(usuarioLogueado.getCodigoUsuario());
			em.merge(auditLegajo);
		}

	}

	public String save() {
		if (!precondSave())
			return "FAIL";
		try {

			for (AuditLegajoDet o : lGrilla2) {
				if (o.getCheck() && o.getEstado().intValue() == 2) {
					o.setUsuMod(usuarioLogueado.getCodigoUsuario());
					o.setFechaMod(new Date());
					o.setEstado(1);
					em.merge(o);
				}
			}
			cambiarAuditEstadoCab();
			// guardar Obs
			if (auditLegajoObs.getIdAuditLegajoObs() != null) {
				auditLegajoObs.setUsuMod(usuarioLogueado.getCodigoUsuario());
				auditLegajoObs.setFechaMod(new Date());
				auditLegajoObs = em.merge(auditLegajoObs);
			} else {
				auditLegajoObs.setFechaAlta(new Date());
				auditLegajoObs.setUsuAlta(usuarioLogueado.getCodigoUsuario());
				Query q =
					em.createQuery("select AuditLegajo from AuditLegajo AuditLegajo where AuditLegajo.persona.idPersona = :idPersona");
				q.setParameter("idPersona", idPersona);
				List<AuditLegajo> lista = q.getResultList();
				if (lista.size() > 0) {
					auditLegajoObs.setAuditLegajo(new AuditLegajo());
					auditLegajoObs.getAuditLegajo().setAuditLegajo(lista.get(0).getAuditLegajo());
				} else {
					AuditLegajo auditLegajo = new AuditLegajo();
					auditLegajo.setPersona(new Persona());
					auditLegajo.getPersona().setIdPersona(idPersona);
					auditLegajo.setUsuAlta(usuarioLogueado.getCodigoUsuario());
					auditLegajo.setFechaAlta(new Date());
					auditLegajoObs.setAuditLegajo(auditLegajo);
				}
				auditLegajoObs.setFicha(new Long(2));

				em.persist(auditLegajoObs);

			}

			cargarGrilla2();
			em.flush();
			statusMessages.add(org.jboss.seam.international.StatusMessage.Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
			return "OK";
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add(org.jboss.seam.international.StatusMessage.Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_NO_MSG"));
			return "FAIL";
		}

	}

	public AuditLegajoObs getAuditLegajoObs() {
		return auditLegajoObs;
	}

	public void setAuditLegajoObs(AuditLegajoObs auditLegajoObs) {
		this.auditLegajoObs = auditLegajoObs;
	}

	public List<AuditLegajoDet> getlGrilla2() {
		return lGrilla2;
	}

	public void setlGrilla2(List<AuditLegajoDet> lGrilla2) {
		this.lGrilla2 = lGrilla2;
	}

	public Long getIdPersona() {
		return idPersona;
	}

	public void setIdPersona(Long idPersona) {
		this.idPersona = idPersona;
	}

	public Boolean getSelectAll() {
		return selectAll;
	}

	public void setSelectAll(Boolean selectAll) {
		this.selectAll = selectAll;
	}

}
