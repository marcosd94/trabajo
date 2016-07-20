package py.com.excelsis.sicca.session.form;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.servlet.ServletContext;

import org.hibernate.annotations.Where;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import enums.ActividadEnum;
import enums.Estado;
import py.com.excelsis.sicca.entity.Barrio;
import py.com.excelsis.sicca.entity.ConcursoPuestoDet;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.ConfiguracionUoDet;
import py.com.excelsis.sicca.entity.ContenidoFuncional;
import py.com.excelsis.sicca.entity.Cpt;
import py.com.excelsis.sicca.entity.Entidad;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.entity.ValorNivelOrg;
import py.com.excelsis.sicca.entity.VwEntidadOee;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.ConfiguracionUoCabList;
import py.com.excelsis.sicca.session.EntidadList;
import py.com.excelsis.sicca.session.PlantaCargoDetList;
import py.com.excelsis.sicca.session.SinEntidadList;
import py.com.excelsis.sicca.session.SinNivelEntidadList;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.ReportUtilFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.GrupoPuestosController;
import py.com.excelsis.sicca.util.JasperReportUtils;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.SICCAAppHelper;

@Scope(ScopeType.CONVERSATION)
@Name("detallePlantaCargoListFormController")
public class DetallePlantaCargoListFormController implements Serializable {

	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(create = true)
	PlantaCargoDetList plantaCargoDetList;
	@In(create = true)
	SinNivelEntidadList sinNivelEntidadList;
	private SinNivelEntidad nivelEntidad = new SinNivelEntidad();
	private SinEntidad sinEntidad = new SinEntidad();
	@In(create = true)
	SinEntidadList sinEntidadList;
	@In(create = true)
	EntidadList entidadList;
	@In(create = true)
	ConfiguracionUoCabList configuracionUoCabList;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(required = false)
	ReportUtilFormController reportUtilFormController;
	@In(create=true, required = false)
	SeleccionUtilFormController seleccionUtilFormController;
	@In(required = false, create = true)
	GrupoPuestosController grupoPuestosController;
	@In(required = false, create = true)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;
	@In(create = true)
	SeguridadUtilFormController seguridadUtilFormController;

	private Entidad entidad = new Entidad();

	private String puesto;

	private Long idTipoCpt;
	private Long idEstado;

	private Long idCpt;
	private Estado activo = Estado.ACTIVO;
	private String codigoCpt;
	
	private Cpt cpt = new Cpt();
	private Long idPlantaCargoDet;

	/**
	 * Método que inicia los parametros
	 */
	public void init() {

		if (nivelEntidadOeeUtil == null
				|| (nivelEntidadOeeUtil.getCodSinEntidad() == null && nivelEntidadOeeUtil
						.getNombreNivelEntidad() == null)) {
			nivelEntidadOeeUtil = (NivelEntidadOeeUtil) Component.getInstance(
					NivelEntidadOeeUtil.class, true);
			cargarCabecera();
		}

		if (idCpt != null) {
			cpt = em.find(Cpt.class, idCpt);
			buscarCodigoCpt();
		}
		search();
	}

	/**
	 * Método que carga los datos de la cabecera
	 */
	public void cargarCabecera() {
		grupoPuestosController = (GrupoPuestosController) Component
				.getInstance(GrupoPuestosController.class, true);
		grupoPuestosController.initCabecera();
		nivelEntidadOeeUtil.limpiar();

		SinNivelEntidad sinNivelEntidad = grupoPuestosController
				.getSinNivelEntidad();
		SinEntidad sinEntidad = grupoPuestosController.getSinEntidad();
		ConfiguracionUoCab configuracionUoCab = grupoPuestosController
				.getConfiguracionUoCab();

		// Nivel
		nivelEntidadOeeUtil.setIdSinNivelEntidad(sinNivelEntidad
				.getIdSinNivelEntidad());

		// Entidad
		nivelEntidadOeeUtil.setIdSinEntidad(sinEntidad.getIdSinEntidad());

		// OEE
		nivelEntidadOeeUtil.setIdConfigCab(configuracionUoCab
				.getIdConfiguracionUo());

		nivelEntidadOeeUtil.init();
	}

	/**
	 * Método que busca el codigo CPT
	 */
	@SuppressWarnings("unchecked")
	private void buscarCodigoCpt() {
		String sql = "select cpt.* from planificacion.cpt cpt "
				+ "where cpt.id_cpt = " + cpt.getIdCpt();

		List<Cpt> lista = new ArrayList<Cpt>();
		lista = em.createNativeQuery(sql, Cpt.class).getResultList();
		codigoCpt = cpt.getNivel() + "."
				+ cpt.getGradoSalarialMin().getNumero() + "."
				+ cpt.getGradoSalarialMax().getNumero() + "." + cpt.getNumero()
				+ "." + cpt.getNroEspecifico();
	}

	public String getUrlToPageCpt() {
		if (idTipoCpt == null) {
			statusMessages.add(Severity.ERROR, "Seleccione un Tipo CPT");
			return null;
		}
		return "/planificacion/searchForms/CptList.xhtml?from=planificacion/puestosTrabajo/PlantaCargoDetList&tipoCpt="
				+ idTipoCpt;

	}

	/**
	 * Método llamado al presionar el botón todos
	 */
	public void searchAll() {
		activo = Estado.ACTIVO;
		cpt = new Cpt();
		codigoCpt = null;
		puesto = null;

		nivelEntidadOeeUtil.limpiar();
		cargarCabecera();

		idEstado = null;
		idTipoCpt = null;
		idCpt = null;

		String query = getQuery();
		List<PlantaCargoDet> puestos = new ArrayList<PlantaCargoDet>();
		puestos = plantaCargoDetList.listaResultadosPuestos(query);

	}

	/**
	 * Método que retorna el query necesario para poblar la grilla
	 * @return
	 */
	public String getQuery() {
		String query = "select distinct plantaCargoDet from PlantaCargoDet plantaCargoDet "
				+ "join plantaCargoDet.configuracionUoDet uoDet "
				+ "join uoDet.configuracionUoCab uoCab "
				+ "join uoCab.entidad entidad "
				+ "join entidad.sinEntidad sinEntidad "
				+ "join plantaCargoDet.cpt cpt " + "join cpt.tipoCpt tipoCpt ";
		// WHERE
		String where = "where uoCab.idConfiguracionUo = "
				+ nivelEntidadOeeUtil.getIdConfigCab()
				+ " and sinEntidad.idSinEntidad = "
				+ nivelEntidadOeeUtil.getIdSinEntidad();

		if (activo != null && activo.getId() != null)
			where += " and plantaCargoDet.activo = " + activo.getValor();

		if (nivelEntidadOeeUtil.getIdUnidadOrganizativa() != null)
			where += " and uoDet.idConfiguracionUoDet = "
					+ nivelEntidadOeeUtil.getIdUnidadOrganizativa();

		if (idTipoCpt != null)
			where += " and tipoCpt.idTipoCpt = " + idTipoCpt;
		if (puesto != null && !puesto.trim().isEmpty())
			where += " and lower(plantaCargoDet.descripcion) like '%"
					+ seguridadUtilFormController.caracterInvalido(puesto
							.toLowerCase()) + "%'";
		if (idEstado != null) {
			query += "join plantaCargoDet.estadoCab estado ";
			where += " and estado.idEstadoCab = " + idEstado;
		}
		if (idCpt != null)
			where += " and cpt.idCpt = " + idCpt;

		//ZD 03/11/15
		String order = " ORDER BY plantaCargoDet.fechaAlta desc ";
		return query + where + order;
	}

	/**
	 * Método llamado al presionar el botób Buscar
	 */
	public void search() {
		if (!validar(false))
			return;

		String query = getQuery();
		List<PlantaCargoDet> puestos = new ArrayList<PlantaCargoDet>();
		puestos = plantaCargoDetList.listaResultadosPuestos(query);
	}

	/**
	 * Método que busca el código de la configuracion uo
	 * @param uoDet
	 * @return
	 */
	public String obtenerCodigo(ConfiguracionUoDet uoDet) {
		String code = "";
		List<Integer> listCodes = obtenerListaCodigos(uoDet, null);
		for (Integer codigo : listCodes) {
			code += codigo + ".";
		}
		code = code.substring(0, code.length() - 1);
		return code;
	}

	/**
	 * Método que obtiene el codigo del puesto
	 */
	public String obtenerCodigoPuesto(PlantaCargoDet det) {
		ConfiguracionUoDet uoDet = new ConfiguracionUoDet();
		uoDet = det.getConfiguracionUoDet();
		
		String code = "";
		code += findCodNivelEntidad(uoDet.getConfiguracionUoCab().getIdConfiguracionUo())
				+ ".";
		code += findCodEntidad(uoDet.getConfiguracionUoCab().getIdConfiguracionUo())
				+ ".";
		code += uoDet.getConfiguracionUoCab().getOrden() + ".";
		List<Integer> listCodes = obtenerListaCodigos(uoDet, null);
		for (Integer codigo : listCodes) {
			code += codigo + ".";
		}
		if (det.getContratado())
			code = code + "C";
		if (det.getPermanente())
			code = code + "P";
		code = code + det.getOrden();
		return code;
	}



	/**
	 * Método que busca el nivel correspondiente al codigo ingresado
	 */
	public String findCodNivelEntidad(Long id) {
		SinNivelEntidad nivel = new SinNivelEntidad();
		if (id != null) {
			String sql = "Select v.* from planificacion.vw_entidad_oee v "+
				"join planificacion.configuracion_uo_cab oee on v.id_configuracion_uo = oee.id_configuracion_uo" +
				" where oee.id_configuracion_uo = "+ id;
			List<VwEntidadOee> sin = em.createNativeQuery(sql,
					VwEntidadOee.class).getResultList();
			if (!sin.isEmpty()) 
				nivel = em.find(SinNivelEntidad.class,
						sin.get(0).getIdNivelEntidad());
		} else
			return "";
		return nivel.getNenCodigo() + "";
	}

	public String findCodEntidad(Long id) {
		
		if (id != null) {
			String sql = "Select v.* from planificacion.vw_entidad_oee v "+
				"join planificacion.configuracion_uo_cab oee on v.id_configuracion_uo = oee.id_configuracion_uo" +
				" where oee.id_configuracion_uo = "+ id;
			List<VwEntidadOee> sin = em.createNativeQuery(sql,
					VwEntidadOee.class).getResultList();
			if (!sin.isEmpty()) 
				return sin.get(0).getEntCodigo()+"";
		} 
			return "";
		
	}
	private List<Integer> obtenerListaCodigos(ConfiguracionUoDet uoDet,
			List<Integer> listCodigos) {
		uoDet.getDenominacionUnidad();
		if (listCodigos == null)
			listCodigos = new ArrayList<Integer>();

		listCodigos.add(0, uoDet.getOrden());
		if (uoDet.getConfiguracionUoDet() != null)
			obtenerListaCodigos(uoDet.getConfiguracionUoDet(), listCodigos);

		return listCodigos;
	}
	
	

	/**
	 * Método que obtiene el CPT de acuerdo al nivel, grado, numero y tipo cpt
	 */
	@SuppressWarnings("unchecked")
	public void findCpt() {
		try {
			if (codigoCpt != null && !codigoCpt.equals("")) {
				Integer nivelCpt = null;
				Integer gradoMin = null;
				Integer gradoMax = null;
				Integer numero = null;
				Integer nroEspecifico = null;
				String[] arrayCodigo = codigoCpt.split("\\.");
				for (int i = 0; i < arrayCodigo.length; i++) {
					if (i == 0)
						nivelCpt = new Integer(arrayCodigo[i]);
					if (i == 1)
						gradoMin = new Integer(arrayCodigo[i]);
					if (i == 2)
						gradoMax = new Integer(arrayCodigo[i]);
					if (i == 3)
						numero = new Integer(arrayCodigo[i]);
					if (i == 4)
						nroEspecifico = new Integer(arrayCodigo[i]);
				}

				String cadena = " select cpt.* from planificacion.cpt cpt "
						+ "join planificacion.grado_salarial max "
						+ "on max.id_grado_salarial = cpt.id_grado_salarial_max "
						+ "join planificacion.grado_salarial min "
						+ "on min.id_grado_salarial = cpt.id_grado_salarial_min "
						+ "where cpt.nivel = " + nivelCpt
						+ " and max.numero = " + gradoMax
						+ " and min.numero = " + gradoMin
						+ " and cpt.numero = " + numero
						+ " and cpt.nro_especifico = " + nroEspecifico;
				if (idTipoCpt != null)
					cadena = cadena + " and id_tipo_cpt = " + idTipoCpt;
				List<Cpt> lista = new ArrayList<Cpt>();
				lista = em.createNativeQuery(cadena, Cpt.class).getResultList();
				if (lista.size() > 0)
					cpt = lista.get(0);
			}
		} catch (Exception e) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, "Ingrese un código válido");

		}
	}
	
	public void eliminarPuestoDeTrabajo(){
		
		PlantaCargoDet plantaCargoDet = em.find(PlantaCargoDet.class,
				idPlantaCargoDet);
		
		String cadena = " select * from seleccion.concurso_puesto_det "
				+ "where id_planta_cargo_det = " + idPlantaCargoDet;
		
		List<ConcursoPuestoDet> lista = new ArrayList<ConcursoPuestoDet>();
		lista = em.createNativeQuery(cadena, ConcursoPuestoDet.class).getResultList();
		if(lista.size()>0){
			statusMessages.add(Severity.ERROR,
					"No es posible inactivar un puesto que no este vacante o que haya sido utilizado en un concurso");
			return;
		}else{
			cadena = " select * from planificacion.planta_cargo_det "
					+ "where id_planta_cargo_det = " + idPlantaCargoDet +
					" and id_estado_cab = 1";
			
			List<PlantaCargoDet> lista2 = new ArrayList<PlantaCargoDet>();
			lista2 = em.createNativeQuery(cadena, PlantaCargoDet.class).getResultList();
			
			if(lista2 == null) {
		
				statusMessages.add(Severity.ERROR,
						"No es posible inactivar un puesto que no este vacante");
				return;
			}
			else{
				plantaCargoDet.setActivo(false);
				em.merge(plantaCargoDet);
				em.flush();
				statusMessages.add(Severity.INFO,
						"Se ha inactivado el concurso: ");
			}
		}
			
		/*String cadena = " select * from planificacion.planta_cargo_det cargo"
				+ "where cargo.id_planta_cargo_det = ";
				
		List<PlantaCargoDet> lista = new ArrayList<PlantaCargoDet>();
		lista = em.createNativeQuery(cadena, PlantaCargoDet.class).getResultList();*/
		
	}
	
	/**
	 * Método que llama al cu 199
	 */
	public void imprimirPuestoTrabajo() {
		reportUtilFormController = (ReportUtilFormController) Component
				.getInstance(ReportUtilFormController.class, true);
		reportUtilFormController.init();
		reportUtilFormController.setNombreReporte("RPT_CU199_imprimir_puesto");

		PlantaCargoDet plantaCargoDet = em.find(PlantaCargoDet.class,
				idPlantaCargoDet);
		String codigoUnidOrgDep = obtenerCodigo(plantaCargoDet
				.getConfiguracionUoDet());

		grupoPuestosController = (GrupoPuestosController) Component
				.getInstance(GrupoPuestosController.class, true);
		String codigoPuesto = seleccionUtilFormController.obtenerCodigoPuesto(plantaCargoDet);
		
		//IMPRIMIR EL NUEVO REPORTE CARGANDO LOS PARAMETROS A UTILIZAR. 
		
		
		reportUtilFormController.getParametros().put("idPlantaCargoDet",
				idPlantaCargoDet);
		reportUtilFormController.getParametros().put("codigoUnidOrgDep",
				codigoUnidOrgDep);
		reportUtilFormController.getParametros().put("codigoPuesto",
				codigoPuesto);
		reportUtilFormController.imprimirReportePdf();
	}
	
public void imprimirPuestoParametros() {
		
		ServletContext servletContext = (ServletContext) FacesContext
				.getCurrentInstance().getExternalContext().getContext();
		Connection conn = JpaResourceBean.getConnection();
		
		HashMap<String, Object> param = new HashMap<String, Object>();
		param.put("SUBREPORT_DIR",servletContext.getRealPath("/reports/jasper/"));
		param.put("path_logo", servletContext.getRealPath("/img/"));
		param.put("idCpt",idCpt);
		param.put("idPlantaCargoDet",idPlantaCargoDet);
		
		
		//SECCION 2.3. VALORACIÓN DEL NIVEL ORGANIZATIVO de las TAREAS
		String sql = " select  funcional.orden, "
				+ "  funcional.descripcion as componente, "
				+ "  valor.descripcion, "
				+ "  valor.desde, "
				+ "  valor.hasta, "
				+ "  det_funcional.puntaje "
				+ "  from planificacion.contenido_funcional funcional "
				+ "  join planificacion.valor_nivel_org valor on valor.id_contenido_funcional = funcional.id_contenido_funcional "
				+ "  join planificacion.det_contenido_funcional det_funcional on det_funcional.id_contenido_funcional = funcional.id_contenido_funcional "
				+ "  and det_funcional.id_planta_cargo_det = "+this.idPlantaCargoDet
				+ "  where funcional.activo is TRUE "
				+ "  order by funcional.orden asc,valor.desde "
				+ "  ";
		
		List<Object[]> lista = em.createNativeQuery(sql).getResultList();
		
		int fila = 0;
		int columna = 1;
		String componente = "";
		String escala = "";
		Float puntaje;
		
		for (Object[] obj : lista){
			
			if(!componente.equals(obj[1].toString())){
				componente = obj[1].toString(); // descripcion del componente
				fila++;
				param.put("tipo_"+fila, componente);
				
			}
			
			escala = obj[2].toString();//descripcion de la escala
			param.put("escala_"+fila+"_"+columna, escala);
			
			puntaje = Float.parseFloat(obj[5].toString());//puntaje 
			param.put("puntaje_"+fila, puntaje);
			
			if(columna < 5){
				columna ++;
			}else{
				if(columna == 5)
					columna = 1;
				else
					columna++;
				
				}
		}
		
		
		//SECCION 3.1. VALORACIÓN GENERAL DE LAS CONDICIONES de TRABAJO
		String sql2 = " select  trab.orden, "
				+ "  trab.descripcion as componente, "
				+ "  valor.descripcion, "
				+ "  valor.desde, "
				+ "  valor.hasta, "
				+ "  det_trab.puntaje_cond_trab as puntaje "
				+ "  from planificacion.condicion_trabajo trab "
				+ "  join planificacion.escala_cond_trab valor on valor.id_condicion_trabajo = trab.id_condicion_trabajo "
				+ "  join planificacion.det_condicion_trabajo det_trab on det_trab.id_condicion_trabajo = trab.id_condicion_trabajo "
				+ "  and det_trab.id_planta_cargo_det =  "+this.idPlantaCargoDet
				+ "  where trab.activo is TRUE  "
				+ "  order by trab.orden asc,valor.desde "
				+ "  ";
		
		lista = em.createNativeQuery(sql2).getResultList();
		
		 fila = 0;
		 columna = 1;
		 componente = "";
		 escala = "";
		 puntaje = new Float(0);
		
		for (Object[] obj : lista){
			
			if(!componente.equals(obj[1].toString())){
				componente = obj[1].toString(); // descripcion del componente
				fila++;
				param.put("Atipo_"+fila, componente);
				
			}
			
			escala = obj[2].toString();//descripcion de la escala
			param.put("Aescala_"+fila+"_"+columna, escala);
			
			puntaje = Float.parseFloat(obj[5].toString());//puntaje 
			param.put("Apuntaje_"+fila, puntaje);
			
			if(columna < 5){
				columna ++;
			}else{
				if(columna == 5)
					columna = 1;
				else
					columna++;
				
				}
		}
		
		
		//SECCION 3.2. VALORACIÓN ESPECÍFICA DE LAS CONDICIONES de TRABAJO
		// VALORACIÓN DEL PUESTO RESPECTO DE LAS COMPETENCIAS ESPECÍFICAS REQUERIDAS Y AJUSTES POSIBLES

		//Escalas del encabezado
		String sqlEscalas = "select descripcion from planificacion.escala_cond_trab_especif order by desde";
		
		lista = em.createNativeQuery(sqlEscalas).getResultList();
		columna = 1;
		
		for (Object obj : lista){
			
			if(!componente.equals(obj.toString())){
				componente = obj.toString(); // descripcion del componente
				param.put("3_escala_"+columna, componente);
				columna++;
				
			}
		
		}
		
		//tipos, descripcion, justificacion, ajustes y puntaje. 
		String sqlTipos = "select especif.orden "
				+ " , especif.tipo  "
				+ " ,especif.descripcion   "
				+ " , trabajo_especif.puntaje_cond_trab_especif  "
				+ " , trabajo_especif.justificacion  "
				+ " , trabajo_especif.ajustes  "
				+ " from planificacion.condicion_trabajo_especif especif   "
				+ " join planificacion.det_condicion_trabajo_especif trabajo_especif on trabajo_especif.id_condiciones_trabajo_especif = especif.id_condiciones_trabajo_especif  "
				+ " and trabajo_especif.id_planta_cargo_det =  "+this.idPlantaCargoDet
				+ " where especif.activo = true  "
				+ " order by especif.orden  "
				+ "  ";
		
		lista = em.createNativeQuery(sqlTipos).getResultList();
		
		 fila = 0;
		 columna = 1;
		 String tipo = "";
		 String orden = "";
		 String descripcion  = "";
		 String justificacion = "";
		 String ajustes = "";
		 puntaje = new Float(0);
		
		for (Object[] obj : lista){
			
			if(!tipo.equals(obj[1].toString())){
				tipo = obj[1].toString(); // descripcion del tipo
				fila++;
				param.put("3_tipo_"+fila, tipo);
				
			}
			orden = obj[0].toString();//descripcion 
			param.put("3_orden_"+fila, orden);
			descripcion = obj[2].toString();//descripcion 
			param.put("3_descripcion_"+fila, descripcion);
			
			puntaje = Float.parseFloat(obj[3].toString());//puntaje 
			param.put("3_puntaje_"+fila, puntaje);
			
			justificacion =obj[4].toString();//justificacion 
			param.put("3_justificacion_"+fila, justificacion);
			
			ajustes =obj[5].toString();//ajustes 
			param.put("3_ajustes_"+fila, ajustes);
			
			if(columna < 6){
				columna ++;
			}else{
				if(columna == 6)
					columna = 1;
				else
					columna++;
				
				}
		}
		
		
		//3.3 EVALUACIÓN DE CONDICIONES DE SEGURIDAD
		//verificar de donde traer las escalas de los encabezado
		
		//Escalas del encabezado
				sqlEscalas = "select descripcion from planificacion.escala_cond_segur order by desde";
				
				lista = em.createNativeQuery(sqlEscalas).getResultList();
				columna = 1;
				
				for (Object obj : lista){
					
					if(!componente.equals(obj.toString())){
						componente = obj.toString(); // descripcion del componente
						param.put("4_escala_"+columna, componente);
						columna++;
						
					}
				
				}
				
		//tipos, descripcion, justificacion, ajustes y puntaje. 
				 sqlTipos = "select segur.orden as tipo"
						+ " , segur.descripcion   "
						+ " , det_segur.puntaje_cond_segur  "
						+ " , det_segur.justificacion  "
						+ " , det_segur.ajustes  "
						+ " from planificacion.condicion_segur segur   "
						+ " join planificacion.det_condicion_segur det_segur on det_segur.id_condicion_segur = segur.id_condicion_segur "
						+ " and det_segur.id_planta_cargo_det =  "+this.idPlantaCargoDet
						+ " where segur.activo = true  "
						+ " order by segur.orden  "
						+ "  ";
				
				lista = em.createNativeQuery(sqlTipos).getResultList();
				
				 fila = 0;
				 columna = 1;
				  tipo = "";
				  orden = "";
				  descripcion  = "";
				  justificacion = "";
				  ajustes = "";
				 puntaje = new Float(0);
				
				for (Object[] obj : lista){
					
					if(!tipo.equals(obj[0].toString())){
						tipo = obj[0].toString(); // descripcion del tipo
						fila++;
						param.put("4_tipo_"+fila, tipo);
						
					}
					
					descripcion = obj[1].toString();//descripcion 
					param.put("4_descripcion_"+fila, descripcion);
					
					puntaje = Float.parseFloat(obj[2].toString());//puntaje 
					param.put("4_puntaje_"+fila, puntaje);
					
					justificacion =obj[3].toString();//justificacion 
					param.put("4_justificacion_"+fila, justificacion);
					
					ajustes =obj[4].toString();//ajustes 
					param.put("4_ajustes_"+fila, ajustes);
					
					if(columna < 5){
						columna ++;
					}else{
						if(columna == 5)
							columna = 1;
						else
							columna++;
						
						}
				}
		
				
		//4. REQUISITOS DEL PUESTO :
		//		4.1 DESCRIPCIÓN DE LOS REQUISITOS MÍNIMOS Y OPCIONALES
				
				
		String sqlRequisitos = "select componente.descripcion as componente	"
				+ ", minimos.minimos_requeridos as minimos	"
				+ ", opciones.opc_convenientes as opciones "
				+ "	from planificacion.det_req_min det_req_min "
				+ "	join planificacion.requisito_minimo_cpt  componente on componente.id_requisito_minimo_cpt = det_req_min.id_requisito_minimo_cpt "
				+ " left join planificacion.det_minimos_requeridos minimos on minimos.id_det_req_min = det_req_min.id_det_req_min"
				+ " left join planificacion.det_opciones_convenientes opciones on opciones.id_det_req_min =  det_req_min.id_det_req_min "
				+ "	where det_req_min.id_planta_cargo_det =  "+this.idPlantaCargoDet
				+ " order by componente.orden";
		
		lista = em.createNativeQuery(sqlRequisitos).getResultList();
		
		fila = 0;
		 columna = 1;
		  componente = "";
		  String requisito = "";
		  String opcion = " ";
		
		for (Object[] obj : lista){
				
			componente = obj[0].toString(); 
			fila++;
			param.put("5_componente_"+fila, componente);
				
			
			
			requisito = obj[1].toString();
			param.put("5_minimo_"+fila, requisito);
			
			if(obj[2] != null)		
				opcion =obj[2].toString();//ajustes 
			
			param.put("5_opcion_"+fila, opcion);
			
			if(columna < 3){
				columna ++;
			}else{
				if(columna == 3)
					columna = 1;
				else
					columna++;
				
				}
		}
		
		//4.2 VALORACIÓN DE LOS REQUISITOS DEL PUESTO
		String sqlValoracion = " select  requisito.orden "
				+ "  , requisito.descripcion as componente "
				+ "  , valor.descripcion "
				+ "  , valor.desde "
				+ "  , valor.hasta "
				+ "  , det_req_min.puntaje_req_min "
				+ "  from planificacion.requisito_minimo_cpt requisito "
				+ "  join planificacion.escala_req_min valor on valor.id_requisito_minimo_cpt = requisito.id_requisito_minimo_cpt "
				+ "  join planificacion.det_req_min det_req_min  on det_req_min.id_requisito_minimo_cpt = requisito.id_requisito_minimo_cpt "
				+ "  and det_req_min .id_planta_cargo_det = "+this.idPlantaCargoDet
				+ "  where requisito.activo is TRUE "
				+ "  order by requisito.orden asc,valor.desde ";
		
		 lista = em.createNativeQuery(sqlValoracion).getResultList();
		
		 fila = 0;
		 columna = 1;
		 componente = "";
		 escala = "";
		 puntaje = new Float(0);
		
		for (Object[] obj : lista){
			
			if(!componente.equals(obj[1].toString())){
				componente = obj[1].toString(); // descripcion del componente
				fila++;
				param.put("5_tipo_"+fila, componente);
				
			}
			
			escala = obj[2].toString();//descripcion de la escala
			param.put("5_escala_"+fila+"_"+columna, escala);
			
			puntaje = Float.parseFloat(obj[5].toString());//puntaje 
			param.put("5_puntaje_"+fila, puntaje);
			
			if(columna < 5){
				columna ++;
			}else{
				if(columna == 5)
					columna = 1;
				else
					columna++;
				
				}
		}
		
		
		
		
		
		JasperReportUtils.respondPDF("RPT_imprimirPuesto_nuevo_parametros",	param, false, conn,usuarioLogueado);
		try {
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	

	private Boolean validar(Boolean controlarCab) {
		if (nivelEntidadOeeUtil.getIdSinNivelEntidad() == null) {
			statusMessages.add(Severity.ERROR,
					"Nivel es un campo requerido. Verifique.");
			return false;
		} else if (nivelEntidadOeeUtil.getIdSinEntidad() == null) {
			statusMessages.add(Severity.ERROR,
					"Entidad es un campo requerido. Verifique.");
			return false;
		} else if (nivelEntidadOeeUtil.getIdConfigCab() == null) {
			statusMessages.add(Severity.ERROR,
					"OEE es un campo requerido. Verifique.");
			return false;
		}

		return true;
	}

	/**
	 * Getters y Setters
	 * 
	 * @return
	 */

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

	public Entidad getEntidad() {
		return entidad;
	}

	public void setEntidad(Entidad entidad) {
		this.entidad = entidad;
	}

	public String getPuesto() {
		return puesto;
	}

	public void setPuesto(String puesto) {
		this.puesto = puesto;
	}

	public Long getIdTipoCpt() {
		return idTipoCpt;
	}

	public void setIdTipoCpt(Long idTipoCpt) {
		this.idTipoCpt = idTipoCpt;
	}

	public Long getIdEstado() {
		return idEstado;
	}

	public void setIdEstado(Long idEstado) {
		this.idEstado = idEstado;
	}

	public Estado getActivo() {
		return activo;
	}

	public void setActivo(Estado activo) {
		this.activo = activo;
	}

	public String getCodigoCpt() {
		return codigoCpt;
	}

	public void setCodigoCpt(String codigoCpt) {
		this.codigoCpt = codigoCpt;
	}

	public Cpt getCpt() {
		return cpt;
	}

	public void setCpt(Cpt cpt) {
		this.cpt = cpt;
	}

	public Long getIdCpt() {
		return idCpt;
	}

	public void setIdCpt(Long idCpt) {
		this.idCpt = idCpt;
	}

	public Long getIdPlantaCargoDet() {
		return idPlantaCargoDet;
	}

	public void setIdPlantaCargoDet(Long idPlantaCargoDet) {
		this.idPlantaCargoDet = idPlantaCargoDet;
	}

}
