package py.com.excelsis.sicca.capacitacion.session.form;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.IdClass;
import javax.persistence.Query;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;

import py.com.excelsis.sicca.entity.Capacitaciones;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.form.AdmDocAdjuntoFormController;
import py.com.excelsis.sicca.session.util.JbpmUtilFormController;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.ReportUtilFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.General;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("gestionCorreosCapaFC")
public class GestionCorreosCapaFC {
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(required = false)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;
	@In(required = false)
	ReportUtilFormController reportUtilFormController;
	@In(create = true)
	SeguridadUtilFormController seguridadUtilFormController;
	@In(create = true)
	SeleccionUtilFormController seleccionUtilFormController;

	private List<SelectItem> comboGruposSI;
	private List<SelectItem> comboCapaBecaSI;
	private String idDestinatario;
	private Long idGrupo;
	private Long idCapa;
	private String asunto;
	private String para;
	private String textoMail;
	private Boolean selectAll = false;
	private byte[] uFile1 = null;
	private String cType1 = null;
	private String fName1 = null;
	private byte[] uFile2 = null;
	private String cType2 = null;
	private String fName2 = null;
	private byte[] uFile3 = null;
	private String cType3 = null;
	private String fName3 = null;
	private byte[] uFile4 = null;
	private String cType4 = null;
	private String fName4 = null;
	private byte[] uFile5 = null;
	private String cType5 = null;
	private String fName5 = null;
	private Boolean mostrarDestinatarios = true;
	private List<DocAdjuntosDTO> lDocAdjuntos;
	private List<String> adjuntos;

	private String mensajes;
	private List<Persona> lista;
	private static final String GESTION_CORREO_SFP = "GESTION_CORREO_SFP";
	private static final String GESTION_CORREO_OEE = "GESTION_CORREO_OEE";
	private Referencias referencias;

	private static final String RED_CAPACITACION = "RED DE CAPACITACION";
	private static final String CAPAC_PROCESO_SFP = "CAPAC. EN PROCESO SFP";
	private static final String CAPAC_PROCESO_OG_290 = "CAPAC. EN PROCESO OG 290";
	private static final String TODAS_CAPAC_PROCESO = "TODAS LAS CAPAC. EN PROCESO";
	private static final String CAPAC_FINALIZADAS_SFP = "CAPAC. FINALIZADAS SFP";
	private static final String CAPAC_FINALIZADAS_OG_290 = "CAPAC. FINALIZADAS OG 290";
	private static final String TODAS_CAPAC_FINALIZADAS = "TODAS LAS CAPAC. FINALIZADAS";
	private static final String TODAS_CAPACITACIONES = "TODAS LAS CAPACITACIONES";

	public void init() {
		updateGrupos();
		updateCapaBeca();
		cargarCabecera();
		if (mensajes != null) {
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
			mensajes = null;
		}
	}

	public void adjuntarArchivos() {
		if (lDocAdjuntos == null) {
			lDocAdjuntos = new ArrayList<GestionCorreosCapaFC.DocAdjuntosDTO>();
			adjuntos = new ArrayList<String>();
		}
		if (fName1 != null && !fName1.isEmpty()) {
			if (!adjuntos.contains(fName1)) {
				adjuntos.add(fName1);
				DocAdjuntosDTO docAdjuntosDTO = new DocAdjuntosDTO();
				docAdjuntosDTO.setSeleccted(true);
				docAdjuntosDTO.setNombre(fName1);
				lDocAdjuntos.add(docAdjuntosDTO);
			}
		}
		if (fName2 != null && !fName2.isEmpty()) {
			if (!adjuntos.contains(fName2)) {
				adjuntos.add(fName2);
				DocAdjuntosDTO docAdjuntosDTO = new DocAdjuntosDTO();
				docAdjuntosDTO.setSeleccted(true);
				docAdjuntosDTO.setNombre(fName2);
				lDocAdjuntos.add(docAdjuntosDTO);
			}
		}
		if (fName3 != null && !fName3.isEmpty()) {
			if (!adjuntos.contains(fName3)) {
				adjuntos.add(fName3);
				DocAdjuntosDTO docAdjuntosDTO = new DocAdjuntosDTO();
				docAdjuntosDTO.setSeleccted(true);
				docAdjuntosDTO.setNombre(fName3);
				lDocAdjuntos.add(docAdjuntosDTO);
			}
		}
		if (fName4 != null && !fName4.isEmpty()) {
			if (!adjuntos.contains(fName4)) {
				adjuntos.add(fName4);
				DocAdjuntosDTO docAdjuntosDTO = new DocAdjuntosDTO();
				docAdjuntosDTO.setSeleccted(true);
				docAdjuntosDTO.setNombre(fName4);
				lDocAdjuntos.add(docAdjuntosDTO);
			}
		}
		if (fName5 != null && !fName5.isEmpty()) {
			if (!adjuntos.contains(fName5)) {
				adjuntos.add(fName5);
				DocAdjuntosDTO docAdjuntosDTO = new DocAdjuntosDTO();
				docAdjuntosDTO.setSeleccted(true);
				docAdjuntosDTO.setNombre(fName5);
				lDocAdjuntos.add(docAdjuntosDTO);
			}
		}
	}

	private void cargarCabecera() {
		if (nivelEntidadOeeUtil == null) {
			nivelEntidadOeeUtil =
				(NivelEntidadOeeUtil) Component.getInstance(NivelEntidadOeeUtil.class, true);
		}
		nivelEntidadOeeUtil.setIdConfigCab(usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo());
		nivelEntidadOeeUtil.init2();

	}

	public void copiarCorreo() {
		para = "";
		if (lista == null)
			return;
		for (Persona o : lista) {
			if (o.getSelected())
				para += ";" + o.getEMail();
		}
		para = para.replaceFirst(";", "");
	}

	private Boolean precondEnviarMail() {
		if (para == null || asunto == null || textoMail == null) {
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("GENERICO_CAMPOS_REQUERIDOS"));
			return false;
		}
		if (para.isEmpty() || asunto.isEmpty() || textoMail.isEmpty()) {
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("GENERICO_CAMPOS_REQUERIDOS"));
			return false;
		}
		if (asunto.length() > 255) {
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("GENERICO_SUPERADA_CANT_MAX_CAR")
				+ ": Asunto(255)");
		}

		return true;
	}

	public String enviarCorreo() {
		if (!precondEnviarMail())
			return null;

		SeleccionUtilFormController seleccionUtilFormController =
			(py.com.excelsis.sicca.session.util.SeleccionUtilFormController) Component.getInstance(SeleccionUtilFormController.class, true);
		String lMails[] = para.split(";");
		Boolean envioOk = false;
		// Solo los seleccionados
		if (adjuntos != null) {
			adjuntos.clear();
			for (DocAdjuntosDTO o : lDocAdjuntos) {
				if (o.getSeleccted())
					adjuntos.add(o.getNombre());
			}
		}
		if (lMails.length == 0) {
			statusMessages.add(Severity.ERROR, "No hay destinatarios");
			return null;
		}
		for (String o : lMails) {
			if (General.isEmail(o)) {
				seleccionUtilFormController.enviarMails(o, textoMail, asunto, adjuntos);
				envioOk = true;
			}
		}
		if (envioOk) {
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
			limpiar();
		}
		return "OK";
	}

	private Boolean precondSearch() {
		selectAll = false;
		if (idGrupo == null) {
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("GENERICO_CAMPOS_REQUERIDOS"));
			return false;
		}
		return true;
	}

	private Boolean precondGenerarWhere() {
		if (idGrupo == null) {
			return false;
		}
		return true;
	}

	public void search() {
		if (!precondSearch()) {
			return;
		}
		Boolean paramIdCap = false;
		Boolean paramIdUndOrg = false;
		StringBuffer var1 = new StringBuffer();

		referencias = em.find(Referencias.class, idGrupo);
		if (referencias.getDescLarga().equalsIgnoreCase(RED_CAPACITACION)) {
			var1.append(" SELECT P.* ");
			var1.append(" FROM capacitacion.red_capacitacion R  ");
			var1.append(" JOIN general.persona P ON P.id_persona = R.id_persona JOIN general.pais ON  ");
			var1.append(" pais.id_pais = P.id_pais_expedicion_doc WHERE R.activo = true ");
		} else if (idDestinatario == null) {
			var1 = new StringBuffer();
			var1.append("SELECT P.* ");
			var1.append(" FROM capacitacion.lista_det DET join  ");
			var1.append(" capacitacion.lista_cab CAB ON CAB.id_lista = DET.id_lista join capacitacion.postulacion_cap ");
			var1.append(" POST ON POST.id_postulacion = DET.id_postulacion join general.persona P ON P ");
			var1.append(" .id_persona = POST.id_persona join general.pais ON pais.id_pais = P.id_pais_expedicion_doc ");
			var1.append(" JOIN CAPACITACION.capacitaciones c ON  CAB.id_capacitacion= c.id_capacitacion ");
			var1.append(" WHERE  DET.activo = TRUE AND DET.tipo = 'P'");

			if (idCapa != null) {
				var1.append(" AND CAB.id_capacitacion = :idCapa");
				paramIdCap = true;
			}
			if (nivelEntidadOeeUtil.getIdUnidadOrganizativa() != null) {
				var1.append(" AND C.id_configuracion_uo_det =  :idUo");
				paramIdUndOrg = true;
			}
		} else if (idDestinatario.equalsIgnoreCase("L") || idDestinatario.equalsIgnoreCase("N")) {
			var1.append(" SELECT P.* ");
			var1.append(" FROM  capacitacion.evaluacion_insc_post EI JOIN capacitacion.evaluacion_tipo ET ON  ");
			var1.append(" EI.id_eval = ET.id_eval JOIN capacitacion.capacitaciones C ON C.id_capacitacion ");
			var1.append(" = ET.id_capacitacion JOIN capacitacion.postulacion_cap POST ON POST.id_postulacion ");
			var1.append(" = EI.id_postulacion JOIN general.persona P ON P.id_persona = POST.id_persona  ");
			var1.append(" JOIN general.pais ON P.id_pais_expedicion_doc = pais.id_pais WHERE EI.activo = true    ");
			var1.append(" AND CASE WHEN C.tipo_seleccion = 'C'  ");
			var1.append(" THEN ET.tipo = 'EVAL_POST' ELSE ET.tipo = 'EVAL_INSCRIP' END ");
			if (idCapa != null) {
				var1.append(" AND ET.id_capacitacion =  :idCapa");
				paramIdCap = true;
			}
			if (nivelEntidadOeeUtil.getIdUnidadOrganizativa() != null) {
				var1.append(" AND C.id_configuracion_uo_det =  :idUo");
				paramIdUndOrg = true;
			}
			if (idDestinatario.equalsIgnoreCase("L")) {
				var1.append(" AND EI.resultado = 'L'");
			} else {
				var1.append(" AND EI.resultado = 'N'");
			}
		}
		if (var1.length() == 0) {
			return;
		}
		Query q = em.createNativeQuery(var1.toString(), Persona.class);
		if (paramIdCap) {
			q.setParameter("idCapa", idCapa);
		}
		if (paramIdUndOrg) {
			q.setParameter("idUo", nivelEntidadOeeUtil.getIdUnidadOrganizativa());
		}
		lista = q.getResultList();
		// Inicializar
		for (Persona o : lista) {
			o.setSelected(false);
		}

	}

	public void limpiar() {
		if (lista != null)
			lista.clear();
		if (lDocAdjuntos != null)
			lDocAdjuntos.clear();
		if (adjuntos != null) {
			adjuntos.clear();
		}
		idGrupo = null;
		selectAll = false;
		idCapa = null;
		idDestinatario = null;
		nivelEntidadOeeUtil.setIdUnidadOrganizativa(null);
		nivelEntidadOeeUtil.setCodigoUnidOrgDep(null);
		nivelEntidadOeeUtil.setDenominacionUnidadOrgaDep(null);
		para = null;
		asunto = null;
		textoMail = null;

	}

	private String generarWhereCapaBeca() {
		if (!precondGenerarWhere()) {
			return null;
		}
		String respuesta = "";
		referencias = em.find(Referencias.class, idGrupo);
		// CAPAC. EN PROCESO SFP
		if (referencias.getDescLarga().equalsIgnoreCase(CAPAC_PROCESO_SFP)) {
			respuesta =
				" AND Capacitaciones.tipo = 'CAP_SFP' AND Referencias.descLarga IN ('ASIGNAR COMISION', 'PUBLICAR SELECCIONADOS', 'FINALIZADO CIRCUITO', 'EVALUACION PARTICIPANTES', 'EVALUACION CAPACITACION') ";
		}
		// CAPAC. EN PROCESO OG 290
		if (referencias.getDescLarga().equalsIgnoreCase(CAPAC_PROCESO_OG_290)) {
			respuesta =
				" AND Capacitaciones.tipo = 'CAP_OG290' AND Referencias.dominio= 'ESTADOS_CAPACITACION' AND Referencias.descLarga IN ('CARGAR PARTICIPANTES','EVALUACION PARTICIPANTES', 'EVALUACION CAPACITACION')";
		}
		// TODAS LAS CAPAC. EN PROCESO
		if (referencias.getDescLarga().equalsIgnoreCase(TODAS_CAPAC_PROCESO)) {
			respuesta =
				" AND Referencias.descLarga IN ('ASIGNAR COMISION', 'PUBLICAR SELECCIONADOS', 'FINALIZADO CIRCUITO', 'CARGAR PARTICIPANTES','EVALUACION PARTICIPANTES', 'EVALUACION CAPACITACION')";
		}
		// CAPAC. FINALIZADAS SFP
		if (referencias.getDescLarga().equalsIgnoreCase(CAPAC_FINALIZADAS_SFP)) {
			respuesta =
				" AND Capacitaciones.tipo = 'CAP_SFP' AND Referencias.descLarga = 'FINALIZADA' ";
		}
		// CAPAC. FINALIZADAS OG 290
		if (referencias.getDescLarga().equalsIgnoreCase(CAPAC_FINALIZADAS_OG_290)) {
			respuesta =
				" AND Capacitaciones.tipo = 'CAP_OG290' AND Referencias.descLarga = 'FINALIZADA' ";
		}
		// TODAS LAS CAPAC. FINALIZADAS
		if (referencias.getDescLarga().equalsIgnoreCase(TODAS_CAPAC_FINALIZADAS)) {
			respuesta = " AND Referencias.descLarga = 'FINALIZADA' ";
		}
		// TODAS LAS CAPACITACIONES
		if (referencias.getDescLarga().equalsIgnoreCase(TODAS_CAPACITACIONES)) {
			respuesta =
				" AND Referencias.descLarga not in  ('CANCELADA', 'CARGA', 'INICIADO CIRCUITO', 'ENVIADO A APROBACION', 'CAPACITACION APROBADA', 'REVISION CAPACITACION', 'RECEPCIONAR POSTULANTES', 'REPROGRAMAR/CANCELAR')";
		}
		return respuesta;
	}

	public void updateCapaBeca() {

		mostrarDestinatarios = true;
		if (idGrupo != null) {
			referencias = em.find(Referencias.class, idGrupo);
			if (referencias.getDescLarga().equalsIgnoreCase(RED_CAPACITACION)) {
				mostrarDestinatarios = false;
				return;
			}
		}
		String elWhere = generarWhereCapaBeca();
		List<Capacitaciones> lista = null;
		if (elWhere == null) {
			comboCapaBecaSI = buildSICapaBeca(new ArrayList<Capacitaciones>());
			return;
		}
		lista =
			getCapaBeca(elWhere, usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo(), nivelEntidadOeeUtil.getIdUnidOrg());
		comboCapaBecaSI = buildSICapaBeca(lista);
	}

	@SuppressWarnings("unchecked")
	private List<Capacitaciones> getCapaBeca(String elWhere, Long idOee, Long idUo) {
		Query q =
			em.createQuery("Select Capacitaciones from Capacitaciones Capacitaciones, Referencias Referencias  "
				+ " where Capacitaciones.activo is true and Referencias.valorNum = Capacitaciones.estado "
				+ " and Referencias.dominio ='ESTADOS_CAPACITACION' and Capacitaciones.configuracionUoCab.idConfiguracionUo = :idOee "
				+ elWhere);

		if (idUo != null) {
			elWhere = " and Capacitaciones.configuracionUoDet.idConfiguracionUoDet = :idUo ";
		}

		q.setParameter("idOee", idOee);
		if (idUo != null) {
			q.setParameter("idUo", idUo);
		}
		return q.getResultList();
	}

	private List<SelectItem> buildSICapaBeca(List<Capacitaciones> lista) {
		if (comboCapaBecaSI == null)
			comboCapaBecaSI = new ArrayList<SelectItem>();
		else
			comboCapaBecaSI.clear();
		comboCapaBecaSI.add(new SelectItem(null, "TODOS"));
		for (Capacitaciones o : lista) {
			comboCapaBecaSI.add(new SelectItem(o.getIdCapacitacion(), o.getNombre()));
		}
		return comboCapaBecaSI;
	}

	public void updateGrupos() {
		List<Referencias> lista = getGrupos();
		comboGruposSI = buildSIGrupos(lista);
	}

	private List<SelectItem> buildSIGrupos(List<Referencias> lista) {
		if (comboGruposSI == null)
			comboGruposSI = new ArrayList<SelectItem>();
		else
			comboGruposSI.clear();
		comboGruposSI.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
		for (Referencias o : lista) {
			comboGruposSI.add(new SelectItem(o.getIdReferencias(), o.getDescLarga()));
		}
		return comboGruposSI;
	}

	public void seleccionarTodo() {
		if (lista != null) {
			if (selectAll)
				for (Persona o : lista) {
					o.setSelected(true);
				}
			if (!selectAll)
				for (Persona o : lista) {
					o.setSelected(false);
				}
		}
	}

	/**
	 * Trae el id de la oee de la sfp. Este valor es parametro de la aplicacion
	 * 
	 * @return
	 */
	private Integer valorNumSFP() {
		Query q =
			em.createQuery("select Referencias from Referencias  Referencias "
				+ " where Referencias.dominio = 'OEE_SFP' "
				+ " and Referencias.descAbrev = 'OEE_SFP' and Referencias.activo is true");
		Referencias ref = (Referencias) q.getSingleResult();
		return ref.getValorNum();
	}

	@SuppressWarnings("unchecked")
	private List<Referencias> getGrupos() {
		Integer idOee = valorNumSFP();
		Query q = null;
		if (usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo().intValue() == idOee.intValue()) {
			q =
				em.createQuery("select Referencias from Referencias Referencias"
					+ " where Referencias.dominio= 'CAP_CORREO'"
					+ " order by Referencias.descLarga ");
		} else {
			q =
				em.createQuery("select Referencias from Referencias Referencias"
					+ " where Referencias.valorAlf =  'OG290'"
					+ " and Referencias.dominio= 'CAP_CORREO'" + " order by Referencias.descLarga ");
		}
		return q.getResultList();
	}

	public List<SelectItem> getComboGruposSI() {
		return comboGruposSI;
	}

	public void setComboGruposSI(List<SelectItem> comboGruposSI) {
		this.comboGruposSI = comboGruposSI;
	}

	public List<SelectItem> getComboCapaBecaSI() {
		return comboCapaBecaSI;
	}

	public void setComboCapaBecaSI(List<SelectItem> comboCapaBecaSI) {
		this.comboCapaBecaSI = comboCapaBecaSI;
	}

	public String getIdDestinatario() {
		return idDestinatario;
	}

	public void setIdDestinatario(String idDestinatario) {
		this.idDestinatario = idDestinatario;
	}

	public Long getIdGrupo() {
		return idGrupo;
	}

	public void setIdGrupo(Long idGrupo) {
		this.idGrupo = idGrupo;
	}

	public Long getIdCapa() {
		return idCapa;
	}

	public void setIdCapa(Long idCapa) {
		this.idCapa = idCapa;
	}

	public List<Persona> getLista() {
		return lista;
	}

	public void setLista(List<Persona> lista) {
		this.lista = lista;
	}

	public Referencias getReferencias() {
		return referencias;
	}

	public void setReferencias(Referencias referencias) {
		this.referencias = referencias;
	}

	public static String getGestionCorreoSfp() {
		return GESTION_CORREO_SFP;
	}

	public static String getGestionCorreoOee() {
		return GESTION_CORREO_OEE;
	}

	public String getMensajes() {
		return mensajes;
	}

	public void setMensajes(String mensajes) {
		this.mensajes = mensajes;
	}

	public String getAsunto() {
		return asunto;
	}

	public void setAsunto(String asunto) {
		this.asunto = asunto;
	}

	public String getPara() {
		return para;
	}

	public void setPara(String para) {
		this.para = para;
	}

	public String getTextoMail() {
		return textoMail;
	}

	public void setTextoMail(String textoMail) {
		this.textoMail = textoMail;
	}

	public Boolean getSelectAll() {
		return selectAll;
	}

	public void setSelectAll(Boolean selectAll) {
		this.selectAll = selectAll;
	}

	public byte[] getuFile1() {
		return uFile1;
	}

	public void setuFile1(byte[] uFile1) {
		this.uFile1 = uFile1;
	}

	public String getcType1() {
		return cType1;
	}

	public void setcType1(String cType1) {
		this.cType1 = cType1;
	}

	public String getfName1() {
		return fName1;
	}

	public void setfName1(String fName1) {
		this.fName1 = fName1;
	}

	public byte[] getuFile2() {
		return uFile2;
	}

	public void setuFile2(byte[] uFile2) {
		this.uFile2 = uFile2;
	}

	public String getcType2() {
		return cType2;
	}

	public void setcType2(String cType2) {
		this.cType2 = cType2;
	}

	public String getfName2() {
		return fName2;
	}

	public void setfName2(String fName2) {
		this.fName2 = fName2;
	}

	public byte[] getuFile3() {
		return uFile3;
	}

	public void setuFile3(byte[] uFile3) {
		this.uFile3 = uFile3;
	}

	public String getcType3() {
		return cType3;
	}

	public void setcType3(String cType3) {
		this.cType3 = cType3;
	}

	public String getfName3() {
		return fName3;
	}

	public void setfName3(String fName3) {
		this.fName3 = fName3;
	}

	public byte[] getuFile4() {
		return uFile4;
	}

	public void setuFile4(byte[] uFile4) {
		this.uFile4 = uFile4;
	}

	public String getcType4() {
		return cType4;
	}

	public void setcType4(String cType4) {
		this.cType4 = cType4;
	}

	public String getfName4() {
		return fName4;
	}

	public void setfName4(String fName4) {
		this.fName4 = fName4;
	}

	public byte[] getuFile5() {
		return uFile5;
	}

	public void setuFile5(byte[] uFile5) {
		this.uFile5 = uFile5;
	}

	public String getcType5() {
		return cType5;
	}

	public void setcType5(String cType5) {
		this.cType5 = cType5;
	}

	public String getfName5() {
		return fName5;
	}

	public void setfName5(String fName5) {
		this.fName5 = fName5;
	}

	public List<DocAdjuntosDTO> getlDocAdjuntos() {
		return lDocAdjuntos;
	}

	public void setlDocAdjuntos(List<DocAdjuntosDTO> lDocAdjuntos) {
		this.lDocAdjuntos = lDocAdjuntos;
	}

	public class DocAdjuntosDTO {

		private Boolean seleccted = false;
		private String nombre = "";

		public Boolean getSeleccted() {
			return seleccted;
		}

		public void setSeleccted(Boolean seleccted) {
			this.seleccted = seleccted;
		}

		public String getNombre() {
			return nombre;
		}

		public void setNombre(String nombre) {
			this.nombre = nombre;
		}
	}

	public Boolean getMostrarDestinatarios() {
		return mostrarDestinatarios;
	}

	public void setMostrarDestinatarios(Boolean mostrarDestinatarios) {
		this.mostrarDestinatarios = mostrarDestinatarios;
	}

}
