package py.com.excelsis.sicca.excseleccion.session.form;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import enums.TiposDatos;
import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgrExc;
import py.com.excelsis.sicca.entity.ConcursoPuestoDet;
import py.com.excelsis.sicca.entity.Documentos;
import py.com.excelsis.sicca.entity.EstadoDet;
import py.com.excelsis.sicca.entity.Excepcion;
import py.com.excelsis.sicca.entity.HistorialExcepcion;
import py.com.excelsis.sicca.entity.Observacion;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.entity.Resolucion;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.JbpmUtilFormController;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("firmarDecretoExcFC")
public class FirmarDecretoExcFC implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6343374537970715199L;
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;

	@In(create = true)
	JbpmUtilFormController jbpmUtilFormController;

	@In(create = true)
	SeleccionUtilFormController seleccionUtilFormController;

	@In(scope = ScopeType.SESSION, required = false)
	@Out(scope = ScopeType.SESSION, required = false)
	String roles;

	@In(required = false)
	Usuario usuarioLogueado;
	
	@In(required = false,create=true)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;

	@In(required = false,create=true)
	SeguridadUtilFormController seguridadUtilFormController;

	private List<ConcursoPuestoAgrExc> puestoAgrList = new ArrayList<ConcursoPuestoAgrExc>();

	private Long idConcurso;// Recibe del cu que le llama
	private Concurso concurso;// enviado por el CU
	private String obs;

	private List<Resolucion> decretoList = new ArrayList<Resolucion>();
	private String paramMemo;
	private String paramNota;
	private String paramReso;
	private Long paramIdConcurso;

	private Long idResoEdit = null;
	private Long idResoConsurso;
	private Long idConcursoPuestoAgr;
	private ConcursoPuestoAgr concursoPuestoAgr;
	private String nombrePantalla = "FIRMA_DECRETO_EXC";
	private String direccionFisica;
	private String entity;
	private Long idSave = null;
	private Long idExcepcion;
	

	@SuppressWarnings("unchecked")
	public void init() throws Exception {
		
		if (!seguridadUtilFormController.validarInput(idConcursoPuestoAgr.toString(), TiposDatos.LONG.getValor(), null)) {
			return ;
		}
		if (!seguridadUtilFormController.validarInput(idExcepcion.toString(), TiposDatos.LONG.getValor(), null)) {
			return ;
		}
		concursoPuestoAgr = em.find(ConcursoPuestoAgr.class, idConcursoPuestoAgr);
		
		idConcurso = concursoPuestoAgr.getConcurso().getIdConcurso();
		concurso = em.find(Concurso.class, idConcurso);
		puestoAgrList =
			em.createQuery("Select c from ConcursoPuestoAgrExc c  " + " where c.concursoPuestoAgr.concurso.idConcurso="
				+ idConcurso + " and c.estado='CON DECRETO' and c.excepcion.idExcepcion="+idExcepcion 
				+ " and c.activo=true").getResultList();
		cargarCabecera();// Trae las entidades segun el grupo que se envio
		cargarListas();
	
	}

	

	private void cargarCabecera(){
		nivelEntidadOeeUtil.setIdConfigCab(concurso.getConfiguracionUoCab().getIdConfiguracionUo());
		nivelEntidadOeeUtil.init2();
	}
	
	

	

	@SuppressWarnings("unchecked")
	public String nextTask() {
		
		if (!tieneDecreto()) {
			statusMessages.add(Severity.ERROR, "Debe poseer al menos un Decreto, verifique");
			return null;
		}
		if(!tieneDocAjunto()){
			statusMessages.add(Severity.ERROR," Debe adjuntar almenos un Documento, verifique");
			return null;
		}

		/**
		 * En la tabla SEL_HISTORIAL_EXCEPCION se inserta nuevo registro:
		 * */
		HistorialExcepcion historialExcepcion = new HistorialExcepcion();
		historialExcepcion.setConcursoGrupoAgr(concursoPuestoAgr);
		historialExcepcion.setEstado("FIRMADO DECRETO");
		historialExcepcion.setExcepcion(new Excepcion());
		historialExcepcion.getExcepcion().setIdExcepcion(idExcepcion);
		historialExcepcion.setObservacion(obs);
		historialExcepcion.setUsuAlta(usuarioLogueado.getCodigoUsuario());
		historialExcepcion.setFechaAlta(new Date());
		em.persist(historialExcepcion);
		
		/**
		 * 	Actualizar estado de la excepción: SEL_EXCEPCION a 'FIRMADO DECRETO’
		 * **/
		Excepcion excepcion= em.find(Excepcion.class,idExcepcion);
		excepcion.setFechaMod(new Date());
		excepcion.setUsuMod(usuarioLogueado.getCodigoUsuario());
		excepcion.setEstado("FIRMADO DECRETO");
		em.merge(excepcion);

		for (int i = 0; i < puestoAgrList.size(); i++) {
			ConcursoPuestoAgrExc agrExc= em.find(ConcursoPuestoAgrExc.class, puestoAgrList.get(i).getIdConcursoPuestoAgrExc());
			ConcursoPuestoAgr agr =
				em.find(ConcursoPuestoAgr.class, puestoAgrList.get(i).getConcursoPuestoAgr().getIdConcursoPuestoAgr());
			/**
			 * Se actualiza el estado del grupo a ‘FIRMADO DECRETO’ . 
			 * */
			agrExc.setEstado("FIRMADO DECRETO");
			agrExc.setUsuMod(usuarioLogueado.getCodigoUsuario());
			agrExc.setFechaMod(new Date());
			em.merge(agr);
			/**
			 * Al igual que el estado de los puestos en PLANTA_CARGO_DET y CONCURSO_PUESTO_DET. 
			 * */
			List<ConcursoPuestoDet> d =
				em.createQuery("Select c from ConcursoPuestoDet c"
					+ " where c.activo=true and  c.concursoPuestoAgr.idConcursoPuestoAgr="
					+ agr.getIdConcursoPuestoAgr()).getResultList();
			for (int j = 0; j < d.size(); j++) {
				ConcursoPuestoDet puestoDet =
					em.find(ConcursoPuestoDet.class, d.get(j).getIdConcursoPuestoDet());
				puestoDet.setEstadoDet(conFirmadoDecretoEstadoDet());
				puestoDet.setFechaMod(new Date());
				puestoDet.setUsuMod(usuarioLogueado.getCodigoUsuario());
				em.merge(puestoDet);
				if (d.get(j).getPlantaCargoDet() != null) {
					PlantaCargoDet pdet =
						em.find(PlantaCargoDet.class, d.get(j).getPlantaCargoDet().getIdPlantaCargoDet());
					pdet.setEstadoDet(conFirmadoDecretoEstadoDet());
					pdet.setFechaMod(new Date());
					pdet.setUsuMod(usuarioLogueado.getCodigoUsuario());
					em.merge(pdet);
				}
			}
		}

		em.flush();
		statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
		
		return "next";

	}

	
	@SuppressWarnings("unchecked")
	private EstadoDet conFirmadoDecretoEstadoDet() {
		List<EstadoDet> eDet =
			em.createQuery(" Select e from EstadoDet e "
				+ " where e.descripcion like 'FIRMADO DECRETO' and e.estadoCab.descripcion like 'CONCURSO'").getResultList();
		if (eDet.isEmpty())
			return null;
		else
			return eDet.get(0);
	}

	private boolean tieneDecreto() {
		for (int i = 0; i < puestoAgrList.size(); i++) {
			if (puestoAgrList.get(i).getDecreto() != null)
				return true;
		}
		return false;
	}

	
	public String anexar(Long idConcursoPuestoAgrExc) {
		ConcursoPuestoAgrExc grupoExc = em.find(ConcursoPuestoAgrExc.class, idConcursoPuestoAgrExc);
		if(grupoExc.getDecreto()==null){
			statusMessages.add(Severity.ERROR,"No se puede anexar documento no posee Decreto, verifique");
		}
		String url = "/seleccion/adminDocAdjunto/AdmDocAdjunto.seam";
		String nombrePantalla = "FIRMA_DECRETO_EXC";
		String idCU = grupoExc.getDecreto().getIdResolucion()+"";
		String nombreTabla = "Decreto";
		String mostrarDoc = "true";
		String isEdit = "true";
		String from = "excepcionesSeleccion/firmarDecretoExc/FirmarDecretoExc";

		SimpleDateFormat sdfSoloAnio = new SimpleDateFormat("yyyy");
		String anio = sdfSoloAnio.format(new Date());
		direccionFisica =
			"//SICCA//" + anio + "//OEE//"
				+ nivelEntidadOeeUtil.getIdConfigCab() + "//"
				+ concurso.getDatosEspecificosTipoConc().getIdDatosEspecificos() + "//"
				+ idConcurso;
		String pagina =
			url + "?mostrarDoc=" + mostrarDoc + "&isEdit=" + isEdit + "&from=" + from
				+ "&nombreTabla=" + nombreTabla + "&direccionFisica=" + direccionFisica+
				"&nombrePantalla=" + nombrePantalla + "&idCU=" + idCU;

		
		;
		return pagina;
	}
	
	/**
	 * verifica que almenos un adjunto por grupo exista
	 * */
	@SuppressWarnings("unchecked")
	private boolean tieneDocAjunto()
	{
		
		for (ConcursoPuestoAgrExc agr : puestoAgrList) {
			if( agr.getDecreto()!=null){
				List<Documentos> d =
					em.createQuery("Select d from Documentos d "
						+ "where lower(d.nombrePantalla) like '" + nombrePantalla.toLowerCase()
						+ "' and d.activo=true and d.nombreTabla like 'Decreto' and d.idTabla=" + agr.getDecreto().getIdResolucion()).getResultList();
				if(!d.isEmpty())
					return true;	
			}
			
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public String finIdConcursoReso(Long id, String dir) {
		idResoEdit = id;
		Resolucion m = em.find(Resolucion.class, id);
		List<ConcursoPuestoAgrExc> p =
			em.createQuery("select c from ConcursoPuestoAgrExc c "
				+ " where c.decreto.idResolucion=" + m.getIdResolucion()).getResultList();
		if (p.isEmpty())
			idResoConsurso = idConcurso;
		else
			idResoConsurso = p.get(0).getConcursoPuestoAgr().getConcurso().getIdConcurso();

		return dir;

	}


	private void cargarListas() {
		try {
			decretoList = new ArrayList<Resolucion>();

			for (int i = 0; i < puestoAgrList.size(); i++) {
				Resolucion decreto = new Resolucion();
				if (puestoAgrList.get(i).getDecreto() != null) {
					decreto = em.find(Resolucion.class, puestoAgrList.get(i).getDecreto().getIdResolucion());
					if (!decretoList.contains(decreto))
						decretoList.add(decreto);
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	

	public String resolucion() {
		paramReso = "";
		idResoEdit = null;
		if (puestoAgrList.isEmpty()) {
			statusMessages.add(Severity.WARN, "No posee grupos para generar el Decreto. Verifique");
			return null;
		}

		for (ConcursoPuestoAgrExc f : puestoAgrList) {
			paramReso += f.getConcursoPuestoAgr().getIdConcursoPuestoAgr() + ",";
			paramIdConcurso = f.getConcursoPuestoAgr().getConcurso().getIdConcurso();

		}
		return "ir";

	}

	
	

	// GETTERS Y SETTERS

	

	
	public Long getIdConcurso() {
		return idConcurso;
	}

	public List<ConcursoPuestoAgrExc> getPuestoAgrList() {
		return puestoAgrList;
	}

	public void setPuestoAgrList(List<ConcursoPuestoAgrExc> puestoAgrList) {
		this.puestoAgrList = puestoAgrList;
	}

	public Long getIdExcepcion() {
		return idExcepcion;
	}

	public void setIdExcepcion(Long idExcepcion) {
		this.idExcepcion = idExcepcion;
	}

	public void setIdSave(Long idSave) {
		this.idSave = idSave;
	}

	public void setIdConcurso(Long idConcurso) {
		this.idConcurso = idConcurso;
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

	public List<Resolucion> getDecretoList() {
		return decretoList;
	}

	public void setDecretoList(List<Resolucion> decretoList) {
		this.decretoList = decretoList;
	}

	public String getParamMemo() {
		return paramMemo;
	}

	public void setParamMemo(String paramMemo) {
		this.paramMemo = paramMemo;
	}

	public String getParamNota() {
		return paramNota;
	}

	public void setParamNota(String paramNota) {
		this.paramNota = paramNota;
	}

	public String getParamReso() {
		return paramReso;
	}

	public void setParamReso(String paramReso) {
		this.paramReso = paramReso;
	}

	public Long getParamIdConcurso() {
		return paramIdConcurso;
	}

	public void setParamIdConcurso(Long paramIdConcurso) {
		this.paramIdConcurso = paramIdConcurso;
	}

	public Long getIdResoEdit() {
		return idResoEdit;
	}

	public void setIdResoEdit(Long idResoEdit) {
		this.idResoEdit = idResoEdit;
	}

	public Long getIdResoConsurso() {
		return idResoConsurso;
	}

	public void setIdResoConsurso(Long idResoConsurso) {
		this.idResoConsurso = idResoConsurso;
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

	public Long getIdSave() {
		return idSave;
	}

	
}
