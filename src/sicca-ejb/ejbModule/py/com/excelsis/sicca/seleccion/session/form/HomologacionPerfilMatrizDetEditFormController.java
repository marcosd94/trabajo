package py.com.excelsis.sicca.seleccion.session.form;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.security.AuthorizationException;

import enums.Estado;
import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.ConcursoPuestoDet;
import py.com.excelsis.sicca.entity.EstadoDet;
import py.com.excelsis.sicca.entity.HomologacionPerfilMatriz;
import py.com.excelsis.sicca.entity.HomologacionPerfilMatrizDet;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.seleccion.session.HomologacionPerfilMatrizDetHome;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("homologacionPerfilMatrizDetEditFormController")
public class HomologacionPerfilMatrizDetEditFormController implements Serializable {

	/**
	 * 
	 */
	// private static final long serialVersionUID = -4633189665635912313L;
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;

	@In(create = true)
	HomologacionPerfilMatrizDetHome homologacionPerfilMatrizDetHome;

	@In(value = "entityManager")
	EntityManager em;

	@In(required = false)
	Usuario usuarioLogueado;

	private Long idHomologacionDet;
	private HomologacionPerfilMatrizDet homologacionPerfilMatrizDet;
	private Long idConcursoPuestoAgr;
	private HomologacionPerfilMatriz cabeceraHomologacionPerfilMatriz;
	private SeguridadUtilFormController seguridadUtilFormController;
	private String from;
	private String codActividad;

	private void validarOee(Concurso concurso) {
		if (seguridadUtilFormController == null) {
			seguridadUtilFormController =
				(SeguridadUtilFormController) Component.getInstance(SeguridadUtilFormController.class, true);
		}
		if (concurso == null) {
			throw new javax.persistence.EntityNotFoundException();
		}

		if (!seguridadUtilFormController.verificarPerteneceOee(concurso.getConfiguracionUoCab().getIdConfiguracionUo())) {
			throw new AuthorizationException(SeamResourceBundle.getBundle().getString("GENERICO_OEE_NO_VALIDA"));
		}
	}

	@SuppressWarnings("unchecked")
	public void init() {

		/* Incidencia 1014 */
		ConcursoPuestoAgr grupo = em.find(ConcursoPuestoAgr.class, idConcursoPuestoAgr);
		validarOee(grupo.getConcurso());
		/**/
		try {
			homologacionPerfilMatrizDet = new HomologacionPerfilMatrizDet();
			List<HomologacionPerfilMatriz> h =
				em.createQuery(" select h from  HomologacionPerfilMatriz h "
					+ " where h.concursoPuestoAgr.idConcursoPuestoAgr=" + idConcursoPuestoAgr).getResultList();
		/*	String q="select * from  seleccion.homologacion_perfil_matriz "
					+ "where homologacion_perfil_matriz.id_concurso_puesto_agr = 1040";*/
			
			if (!h.isEmpty()) {
				cabeceraHomologacionPerfilMatriz = h.get(0);

			}
			if (idHomologacionDet != null) {
				homologacionPerfilMatrizDet =
					em.find(HomologacionPerfilMatrizDet.class, idHomologacionDet);
			}
			homologacionPerfilMatrizDetHome.setInstance(homologacionPerfilMatrizDet);
			if (!homologacionPerfilMatrizDetHome.isIdDefined()) {
				homologacionPerfilMatrizDet.setActivo(Estado.ACTIVO.getValor());
				homologacionPerfilMatrizDet.setNroObs(nObs());
				homologacionPerfilMatrizDet.setFechaObs(new Date());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	

	@SuppressWarnings("unchecked")
	public String save() {
		
		try {
			if(homologacionPerfilMatrizDet.getObservacion().trim().equals("")){
				statusMessages.add(Severity.ERROR,"Debe Ingresar la observación. Verifique");
				return null;
			}
			if (cabeceraHomologacionPerfilMatriz == null) {
				statusMessages.add(Severity.ERROR,"No se puede guardar el registro no posee cabecera. Verifique");
				return null;
			}
			homologacionPerfilMatrizDet.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			homologacionPerfilMatrizDet.setUsuObs(usuarioLogueado.getCodigoUsuario());
			homologacionPerfilMatrizDet.setFechaAlta(new Date());
			homologacionPerfilMatrizDet.setFechaObs(new Date());
			homologacionPerfilMatrizDet.setAceptaSfp(false);
			homologacionPerfilMatrizDet.setAjustadoOee(true); //JD 25/11
			homologacionPerfilMatrizDet.setAjusteEnviado(true);
			homologacionPerfilMatrizDet.setFirmaResolucion(false);
			homologacionPerfilMatrizDet.setHomologacionPerfilMatriz(cabeceraHomologacionPerfilMatriz);
			em.persist(homologacionPerfilMatrizDet);

			//CAMBIA EL ESTADO DEL GRUPO A PENDIENTE DE REVISION
			ConcursoPuestoAgr agr = em.find(ConcursoPuestoAgr.class, idConcursoPuestoAgr);
			agr.setEstado(pendienteRevision());
			agr.setFechaMod(new Date());
			agr.setUsuMod(usuarioLogueado.getCodigoUsuario());
			em.merge(agr);
			em.flush();
			/** 
			 * Actualiza los datos en la tabla: PLANTA_CARGO_DET y tambien Actualiza de la tabla  ConcursoPuestoDet para la incidencia 0001073
			 * **/
			List<ConcursoPuestoDet> concursoPuestoDets= em.createQuery("Select d from ConcursoPuestoDet d" +
					" where d.concursoPuestoAgr.idConcursoPuestoAgr= "+idConcursoPuestoAgr).getResultList();
			
			EstadoDet ed= ependienteRevision();
			for (int i = 0; i < concursoPuestoDets.size(); i++) {
				if(ed!=null)
				{
					ConcursoPuestoDet puestoDet =em.find(ConcursoPuestoDet.class, concursoPuestoDets.get(i).getIdConcursoPuestoDet());
					puestoDet.setEstadoDet(ed);
					puestoDet.setUsuAlta(usuarioLogueado.getCodigoUsuario());
					puestoDet.setFechaAlta(new Date());
					em.merge(puestoDet);
					if(concursoPuestoDets.get(i).getPlantaCargoDet()!=null){
						PlantaCargoDet aux= em.find(PlantaCargoDet.class, concursoPuestoDets.get(i).getPlantaCargoDet().getIdPlantaCargoDet());
						aux.setEstadoCab(ed.getEstadoCab());
						aux.setEstadoDet(ed);
						aux.setUsuMod(usuarioLogueado.getCodigoUsuario());
						aux.setFechaMod(new Date());
						em.merge(aux);
					}
					
					em.flush();
				}
			}

		

			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
			statusMessages.add("Debe finalizar esta tarea para enviar la Obs. cargada recientemente");

			return "persisted";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public String update() {
		try {
			if(homologacionPerfilMatrizDet.getObservacion().trim().equals("")){
				statusMessages.add(Severity.ERROR,"Debe Ingresar la observación. Verifique");
				return null;
			}
			homologacionPerfilMatrizDet.setUsuMod(usuarioLogueado.getCodigoUsuario());
			homologacionPerfilMatrizDet.setFechaMod(new Date());
			em.merge(homologacionPerfilMatrizDet);
			em.flush();
			//Si inactiva una observación, el estado del grupo pasa a “ENVIADO A HOMOLOGACION”
			if(!homologacionPerfilMatrizDet.getActivo()){
				ConcursoPuestoAgr agr = em.find(ConcursoPuestoAgr.class, idConcursoPuestoAgr);
				agr.setEstado(enviadoAhomologacion());
				agr.setFechaMod(new Date());
				agr.setUsuMod(usuarioLogueado.getCodigoUsuario());
				em.merge(agr);
				em.flush();
			
				/** 
				 * Actualiza los datos en la tabla: PLANTA_CARGO_DET y tambien Actualiza de la tabla  ConcursoPuestoDet para la incidencia 0001073
				 * **/
				List<ConcursoPuestoDet> concursoPuestoDets= em.createQuery("Select d from ConcursoPuestoDet d" +
						" where d.concursoPuestoAgr.idConcursoPuestoAgr= "+idConcursoPuestoAgr).getResultList();
				
				EstadoDet ed= eEnviadoHomologacion();
				for (int i = 0; i < concursoPuestoDets.size(); i++) {
					if(ed!=null)
					{
						ConcursoPuestoDet puestoDet =em.find(ConcursoPuestoDet.class, concursoPuestoDets.get(i).getIdConcursoPuestoDet());
						puestoDet.setEstadoDet(ed);
						puestoDet.setUsuAlta(usuarioLogueado.getCodigoUsuario());
						puestoDet.setFechaAlta(new Date());
						em.merge(puestoDet);
						if(concursoPuestoDets.get(i).getPlantaCargoDet()!=null){
							PlantaCargoDet aux= em.find(PlantaCargoDet.class, concursoPuestoDets.get(i).getPlantaCargoDet().getIdPlantaCargoDet());
							aux.setEstadoCab(ed.getEstadoCab());
							aux.setEstadoDet(ed);
							aux.setUsuMod(usuarioLogueado.getCodigoUsuario());
							aux.setFechaMod(new Date());
							em.merge(aux);
						}
						
						em.flush();
					}
				}
			}

			
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
			return "updated";
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add(Severity.ERROR,"Error al Editar el registro");
			
		}
		return null;
	}

	// METODOS PRIVADOS
	@SuppressWarnings("unchecked")
	private Integer nObs() {
		List<Integer> dets =
			em.createQuery("Select max(d.nroObs) from HomologacionPerfilMatrizDet d ").getResultList();

		if (!dets.isEmpty() && dets.get(0) != null)
			return dets.get(0) + 1;
		else
			return 1;
	}

	@SuppressWarnings("unchecked")
	private Integer pendienteRevision() {
		List<Referencias> ref =
			em.createQuery("select r from Referencias r " + " where r.dominio like 'ESTADOS_GRUPO'"
				+ " and r.descAbrev like 'PENDIENTE REVISION'").getResultList();
		if (ref.isEmpty())
			return null;

		return ref.get(0).getValorNum();
	}
	@SuppressWarnings("unchecked")
	private Integer enviadoAhomologacion() {
		List<Referencias> ref =
			em.createQuery("select r from Referencias r " + " where r.dominio like 'ESTADOS_GRUPO'"
				+ " and r.descAbrev like 'ENVIADO A HOMOLOGACION'").getResultList();
		if (ref.isEmpty())
			return null;

		return ref.get(0).getValorNum();
	}
	@SuppressWarnings("unchecked")
	private  EstadoDet ependienteRevision(){
		List<EstadoDet>dets=em.createQuery("Select d from EstadoDet d " +
				" where d.descripcion like 'PENDIENTE REVISION' and " +
				" d.estadoCab.descripcion like 'CONCURSO' ").getResultList();
		if(dets.isEmpty())
			return null;
		else
			return dets.get(0);
	}
	@SuppressWarnings("unchecked")
	private  EstadoDet eEnviadoHomologacion(){
		List<EstadoDet>dets=em.createQuery("Select d from EstadoDet d " +
				" where d.descripcion like 'ENVIADO A HOMOLOGACION' and " +
				" d.estadoCab.descripcion like 'CONCURSO' ").getResultList();
		if(dets.isEmpty())
			return null;
		else
			return dets.get(0);
	}

	// GETTERS Y SETTERS

	public Long getIdHomologacionDet() {
		return idHomologacionDet;
	}

	public void setIdHomologacionDet(Long idHomologacionDet) {
		this.idHomologacionDet = idHomologacionDet;
	}

	public HomologacionPerfilMatrizDet getHomologacionPerfilMatrizDet() {
		return homologacionPerfilMatrizDet;
	}

	public void setHomologacionPerfilMatrizDet(HomologacionPerfilMatrizDet homologacionPerfilMatrizDet) {
		this.homologacionPerfilMatrizDet = homologacionPerfilMatrizDet;
	}

	public Long getIdConcursoPuestoAgr() {
		return idConcursoPuestoAgr;
	}

	public void setIdConcursoPuestoAgr(Long idConcursoPuestoAgr) {
		this.idConcursoPuestoAgr = idConcursoPuestoAgr;
	}

	public HomologacionPerfilMatrizDetHome getHomologacionPerfilMatrizDetHome() {
		return homologacionPerfilMatrizDetHome;
	}

	public void setHomologacionPerfilMatrizDetHome(HomologacionPerfilMatrizDetHome homologacionPerfilMatrizDetHome) {
		this.homologacionPerfilMatrizDetHome = homologacionPerfilMatrizDetHome;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getCodActividad() {
		return codActividad;
	}

	public void setCodActividad(String codActividad) {
		this.codActividad = codActividad;
	}

	
}
