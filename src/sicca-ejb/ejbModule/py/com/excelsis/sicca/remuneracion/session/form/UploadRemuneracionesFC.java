package py.com.excelsis.sicca.remuneracion.session.form;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.hibernate.JDBCException;
import org.hibernate.Session;
import org.hibernate.validator.InvalidStateException;
import org.hibernate.validator.InvalidValue;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.annotations.async.Asynchronous;
import org.jboss.seam.annotations.async.Expiration;
import org.jboss.seam.annotations.async.IntervalCron;
import org.jboss.seam.async.QuartzTriggerHandle;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;
import org.richfaces.model.UploadItem;

import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.ControlRemuneracionesTmp;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.Documentos;
import py.com.excelsis.sicca.entity.EmpleadoPuesto;
import py.com.excelsis.sicca.entity.Entidad;
import py.com.excelsis.sicca.entity.EstudiosRealizados;
import py.com.excelsis.sicca.entity.ExperienciaLaboral;
import py.com.excelsis.sicca.entity.Familiares;
import py.com.excelsis.sicca.entity.HistoricoRemuneracionesTmp;
import py.com.excelsis.sicca.entity.IdiomasPersona;
import py.com.excelsis.sicca.entity.Importacion;
import py.com.excelsis.sicca.entity.Legajos;
import py.com.excelsis.sicca.entity.Pais;
import py.com.excelsis.sicca.entity.PersonaPostulante;
import py.com.excelsis.sicca.entity.Postulacion;
import py.com.excelsis.sicca.entity.PostulacionCap;
import py.com.excelsis.sicca.entity.Procesamiento;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.entity.RemunConfig;
import py.com.excelsis.sicca.entity.RemuneracionesTmp;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.ReprPersonaDiscapacidad;
import py.com.excelsis.sicca.entity.SinObj;
import py.com.excelsis.sicca.entity.Vencimientos;
import py.com.excelsis.sicca.seguridad.entity.Rol;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.seguridad.entity.UsuarioRol;
import py.com.excelsis.sicca.session.ConfiguracionUoCabList;
import py.com.excelsis.sicca.session.EmpleadoPuestoList;
import py.com.excelsis.sicca.session.EntidadList;
import py.com.excelsis.sicca.session.SinEntidadList;
import py.com.excelsis.sicca.session.SinNivelEntidadList;
import py.com.excelsis.sicca.session.form.AdmDocAdjuntoFormController;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.PersonaDTO;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.JasperReportUtils;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("uploadRemuneracionesFC")
public class UploadRemuneracionesFC {
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(create = true)
	EmpleadoPuestoList empleadoPuestoList;
	@In(create = true)
	SinNivelEntidadList sinNivelEntidadList;
	@In(create = true)
	SinEntidadList sinEntidadList;
	@In(create = true)
	EntidadList entidadList;
	@In(create = true)
	ConfiguracionUoCabList configuracionUoCabList;
	@In(required = false, create = true)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;
	@In(create = true, required = false)
	SeleccionUtilFormController seleccionUtilFormController;
	@In(create = true, required = false)
	SeguridadUtilFormController seguridadUtilFormController;

	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;

	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

	private byte[] uFile = null;
	private String cType = null;
	private String fname = null;
	public List<String> lLineasArch;
	public ArrayList<DTO680Tmp> ldto;
	public Double totalPresupuesto;
	public Double totalDevengado;
	public String error = null;

	Integer mesIterado = null;
	Boolean esAdministradorSFP = false;
	Boolean primeraVez = true;
	Boolean enviarArchivo = true;
	Integer cantFracaso = 0;
	Integer cantExito = 0;
	Integer cantDuplicado = 0;
	Integer mes;
	Integer anho;
	String registros;
	String remuneracionesTotales;
	int i;
	Long valor;
	long remuneracionesLeidas;
	String estadoImportacion;
	private Boolean mostrarModalRemplazo = false;
	private Boolean mostrarModalAgregar = false;
	private Boolean limpiarModales = false;
	private Boolean guardarArchivo = false;
	private Boolean reemplazarPlanilla = false;
	private Boolean existeEnHistorico = false;
	private Boolean existeEnRemuneraciones = false;
	private Boolean insertarEnHistoricoDirecto = false;
	private Boolean guardarProcesamiento = false;
	private Boolean actualizarProcesamiento = false;
	private Boolean maximoRectificacionesSuperado = false;
	private ConfiguracionUoCab confUoCabActual = new ConfiguracionUoCab();
	private ControlRemuneracionesTmp registroControl = new ControlRemuneracionesTmp();
	private UploadItem fileItem;
	//CABECERA DE CONTROL DE LA PLANILLA
	private String cabeceraMatriz ="ANO;MES;NIVEL_ENTI;ENTIDAD;OEE;LINEA;CEDULA;NOMBRES;APELLIDOS;ESTADO;REMUNERACION TOTAL;OBJETO_GTO;F.F.;CATEG;PRESUP;DEVENGADO;CONCEPTO;MOVIMIENTO;LUGAR;CARGO;FUNCION REAL QUE CUMPLE;CARGA;DISCAPACIDAD;TIPO;AÑO DE INGRESO";
	//CABECERA AUXILIAR PARA METODO DE CONVERSION DE PLANILLA REMUNERACIONES A PLANILLA INSERCION MASIVA FUNCIONARIOS
	private String cabeceraInsercion = "anio;mes;codigo_nivel;codigo_entidad;nro_cedula;nombre;apellido;estado;obj_gasto;categoria;presupuesto;devengado;movimiento;fecha;cargo;discapaci";
	
	private HashMap<String, DTO680Tmp> hashRegistros;
	private ArrayList<Persona> arrayPersonasNuevas = new ArrayList<Persona>();

	//ZD 18/09/15
	public ArrayList<DTO680TmpRemu> ldtoRemu;
	public String cumplimiento = "";
	private Boolean auxReemplazo = false;
	

	public void init() {
		cargarAdministradorSFP();
		if (primeraVez) {
			nivelEntidadOeeUtil.setIdConfigCab(usuarioLogueado
					.getConfiguracionUoCab().getIdConfiguracionUo());
			primeraVez = false;
			nivelEntidadOeeUtil.init2();
		} else {
			nivelEntidadOeeUtil.init();
		}
		cargarRemuMes();
	}

	private void cargarAdministradorSFP() {
		for (Rol r : seguridadUtilFormController.obtenerRolesUsuario()) {
			if (r.getHomologador() != null && r.getHomologador()) {
				esAdministradorSFP = true;
				return;
			}
		}
		esAdministradorSFP = false;
	}

	private void cargarRemuMes() {
		if (usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo() != null && usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo() > 0){
			this.ldtoRemu = new ArrayList<DTO680TmpRemu>();
			Calendar c = Calendar.getInstance();
			Integer anhio = c.get(Calendar.YEAR);
			Query q = em
					.createNativeQuery("select pro.mes, pro.anho, refe.desc_larga as mesDesc, pro.activo, refe.valor_num as mesNum  "+
									  " from( select anho, mes, activo  from remuneracion.procesamiento  "+ 
									  " where id_configuracion_uo = :oee and anho = :anhio )pro  "+ 
									  " right join seleccion.referencias refe  "+   
									  " on pro.mes = refe.valor_num  "+  
									  " where refe.dominio = 'MESES'  "+  
									  " and refe.activo is true  order by mesnum ");
			q.setParameter("anhio", anhio);
			q.setParameter("oee", usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo());
			List<Object[]> listaRemu = q.getResultList();
			for (Object[] o : listaRemu) {
				this.ldtoRemu.add(new DTO680TmpRemu((Integer) o[0], (Integer) o[1],
						(String) o[2], (Boolean) o[3]));
				if((Integer) o[1] != null)
				this.cumplimiento = "AÑO - " + (Integer) o[1];
			}
	   }
		
	}
	
	public ArrayList<DTO680Tmp> getLdto() {
		return ldto;
	}

	public void setLdto(ArrayList<DTO680Tmp> ldto) {
		this.ldto = ldto;
	}
	public ArrayList<DTO680TmpRemu> getLdtoRemu() {
		return ldtoRemu;
	}

	public void setLdtoRemu(ArrayList<DTO680TmpRemu> ldtoRemu) {
		this.ldtoRemu = ldtoRemu;
	}
	public String getCumplimiento() {
		return cumplimiento;
	}

	public void setCumplimiento(String cumplimiento) {
		this.cumplimiento = cumplimiento;
	}
	private DTO680Tmp precondCompos(String linea) {
		DTO680Tmp dto = DTO680Tmp.descomponerLinea(linea);
		if (dto != null)
			return dto;
		return null;
	}

	public void limpiar() {
		nivelEntidadOeeUtil.limpiar();
		uFile = null;
		cType = null;
		fname = null;
		primeraVez = true;
		statusMessages.clear();
		mes = null;
		anho = null;
		ldto = null;
		registros = null;
		remuneracionesTotales = null;
		estadoImportacion = null;
		reemplazarPlanilla = false;
		existeEnHistorico = false;
		existeEnRemuneraciones = false;
		insertarEnHistoricoDirecto = false;
		guardarProcesamiento = false;
		actualizarProcesamiento = false;
	}

	public List<String> getlLineasArch() {
		return lLineasArch;
	}

	public String setearError(String error) throws IOException, ParseException {
		setError(error);
		return "";
	}

	public void setlLineasArch(List<String> lLineasArch) {
		this.lLineasArch = lLineasArch;
	}

	private Boolean precondInsert() {
		if (nivelEntidadOeeUtil.getIdConfigCab() == null) {
			statusMessages.add(Severity.ERROR,
					"Debe cargar los campos requeridos");
			return false;
		}
		if (uFile == null) {
			statusMessages.add(Severity.ERROR, "Debe cargar un archivo");
			return false;
		}
		if (cType != null && cType.equalsIgnoreCase("")) {
			statusMessages
					.add(Severity.ERROR,
							"Se espera archivo en formato CSV (valores separados por coma)");
			return false;
		}
		if (!fname.endsWith(".csv")) {
			statusMessages
					.add(Severity.ERROR,
							"Se espera archivo en formato CSV (valores separados por coma)");
			return false;
		}

		return true;
	}

	private Boolean validarCabecera() {
		String composCabeceraMatriz[] = cabeceraMatriz.split(";");
		Integer cursor = 0;
		if (lLineasArch.size() > 0) {
			String compos[] = lLineasArch.get(0).split(";");
			for (int i = 0; i < composCabeceraMatriz.length; i++) {
				String o = composCabeceraMatriz[i];
				for (int j = cursor; j < compos.length; j++) {
					String p = compos[j];
					if (!o.trim().equalsIgnoreCase(p.trim())) {
						statusMessages
								.add(Severity.ERROR,
										"Error al definir las columnas. Verifique el modelo con la SFP");
						return false;
					} else {
						cursor++;
						break;
					}
				}
			}
		}
		return true;
	}

	public void preMassiveImport() throws IOException, ParseException {
		
		guardarProcesamiento = false;
		actualizarProcesamiento = false;
		maximoRectificacionesSuperado = false;
		reemplazarPlanilla = false;
		existeEnHistorico = false;
		existeEnRemuneraciones = false;
		insertarEnHistoricoDirecto = false;
		mostrarModalRemplazo = Boolean.FALSE;
		mostrarModalAgregar = Boolean.FALSE;
		
		//Omitir los puntos
		registros = registros.replace(".", "");
		remuneracionesTotales = remuneracionesTotales.replace(".", "");
		
		confUoCabActual = em.find(ConfiguracionUoCab.class, nivelEntidadOeeUtil.getIdConfigCab());
		
		//BUSCAR SI YA  EXISTE UN REGISTRO DE PROCESAMIENTO PARA EL MES-AÑO-OEE DE LA PLANILLA EN CUESTION
		String sqlProc = "select * from remuneracion.procesamiento where mes = "+mes+" and anho = "+anho+
				" and id_configuracion_uo ="+confUoCabActual.getIdConfiguracionUo()+" and activo = TRUE";
		Query qProc = em.createNativeQuery(sqlProc, Procesamiento.class);
		List<Procesamiento> listaProc = qProc.getResultList();		
		if(listaProc.size()>0){
			//CONTROLAR SI ES POSIBLE RECITIFICAR TODAVIA UNA PLANILLA
			if(plazoRectificacion()){
				if(maximoRectificacionesSuperado){
					statusMessages.add(Severity.ERROR, "No es posible procesar la planilla, número máximo de rectificaciones superado.");
					System.out.println("No es posible procesar la planilla, número máximo de rectificaciones superado.");
				}
				else{
					statusMessages.add(Severity.ERROR, "No es posible procesar la planilla, plazo de rectificación superado.");
					System.out.println("No es posible procesar la planilla, plazo de rectificación superado.");
				}
				return;
			}
		}
		else{
			//CONTROLAR SI EL MES DE LA PLANILLA A PROCESAR TODAVIA NO VENCIO
			if(finPresentacion()){
				statusMessages.add(Severity.ERROR, "No es posible procesar la planilla, plazo de presentación para dicho mes superado.");
				System.out.println("No es posible procesar la planilla, plazo de presentación para dicho mes superado.");
				return;
			}
		}
		
		//CONTROL DE PLANILLAS PARA UN MISMO MES-AÑO-OEE NO ESTAN SIENDO PROCESADAS SIMULTANEAMENTE
		if(existePlanillaEnProceso(confUoCabActual)){
			statusMessages.add(Severity.ERROR, "No es posible tratar la planilla, otra similar ya está siendo procesada.");
			System.out.println("No es posible tratar la planilla, otra planilla de su misma OEE ya está siendo procesada.");
			return;
		}
		
		if (verificarExisteRegistro()) {
			existeEnRemuneraciones = true;
			mostrarModalRemplazo = Boolean.TRUE;
			return;
		}
		else if (verificarExisteRegistroHistorico()){
			existeEnHistorico = true;
			mostrarModalRemplazo = Boolean.TRUE;
			return;
		}
		else {
			limpiarModales();
			try{
				massiveImport();
			}
			catch (InvalidStateException e) {
				for (InvalidValue invalidValue : e.getInvalidValues()) {
					System.out.println("Instance of bean class: " + invalidValue.getBeanClass().getSimpleName() +
		            " has an invalid property: " + invalidValue.getPropertyName() +
		            " with message: " + invalidValue.getMessage());
				}
			}catch (Exception e) {
				e.printStackTrace();
				statusMessages.add(Severity.ERROR, "Error desconocido. No se pudo realizar la operación, vuelva a intentarlo!");
				if (confUoCabActual != null)
				registroControl = recuperarRegistroControlInicial(confUoCabActual.getIdConfiguracionUo());
				//SE SETEA COMO ABORTADO SI EXISTE EL REGISTRO DE CONTROL PERTINENTE
				if(registroControl != null){
				registroControl.setEstadoProceso("ABORTADO");
				registroControl.setFechaFin(new Date());
				em.merge(registroControl);
				em.flush();
				}
			}
		}
	}
	
	public void registroControlInicial(Integer year, Integer month, Long idUo, Integer lines, String codUser){
		//SE PREPARA Y GUARDA EL REGISTRO (INICIAL) DE CONTROL PERTINENTE
		
		try {
			Query q = em.createNativeQuery("INSERT INTO remuneracion.control_remuneraciones_tmp"+
							"(anho,mes,id_configuracion_uo,cant_lineas,estado_proceso,usuario,fecha_inicio)"+
							"VALUES ("+year+","+month+","+idUo+","+lines+",'INICIADO','"+codUser+"',(SELECT TIMESTAMP WITHOUT TIME ZONE 'now')); COMMIT;");
			q.executeUpdate();
			
			//System.out.println("CREADO REGISTRO INICIAL");
			
		} catch (Exception e) {
			statusMessages.add(Severity.ERROR,"No se pudo insertar registro de control inicial");
			e.printStackTrace();
		}
	}
	
	public ControlRemuneracionesTmp recuperarRegistroControlInicial(Long idUo){
		
		String sql = "select * from remuneracion.control_remuneraciones_tmp control where control.id_configuracion_uo = "+idUo+
				" and control.estado_proceso = 'INICIADO'";
				
		Query q = em.createNativeQuery(sql, ControlRemuneracionesTmp.class);
		List<ControlRemuneracionesTmp> lista = q.getResultList();
		if(lista.size()>0)
			return lista.get(0);
		else
			return null;
	}

	public boolean procesarLineas() {
		DTO680Tmp dto = new DTO680Tmp();
		String o = null;
		if (anho == null){
			anho = 0;
			statusMessages.add(Severity.ERROR,"Año no ingresado.");
			return false;
		}
		if (mes == null){
			mes = 0;
			statusMessages.add(Severity.ERROR,"Mes no ingresado.");
			return false;
		}
		cantDuplicado = 0;
		cantExito = 0;
		cantFracaso = 0;
		remuneracionesLeidas = 0;
		String cedulaActual = "*";
		boolean cambioPersonaLeida = false;
		PersonaDTO personaDTO = new PersonaDTO();
		
		ArrayList<DTO680Tmp> pla = new ArrayList<DTO680Tmp>();
		
		if (!validarCabecera())
			return false;
		if (!registros.equalsIgnoreCase(Integer.toString(lLineasArch.size()-1)))
			return false;
		
		//SE CREA EL REGISTRO DE CONTROL INICIAL
		registroControlInicial(anho,mes,confUoCabActual.getIdConfiguracionUo(),lLineasArch.size()-1,usuarioLogueado.getCodigoUsuario());
		
		registroControl = recuperarRegistroControlInicial(confUoCabActual.getIdConfiguracionUo());
		// ZD 20/10/15 SE OBTIENE LOS TIPOS DE MOVIMIENTOS PERMITIDOS
		List<Referencias> listaM = this.lMovimiento();
		// ZD 01/03/16 SE OBTIENE LOS TIPOS DE ESTADOS PERMITIDOS
		List<Referencias> listaE = this.lEstados();
		// ZD 02/03/16 SE OBTIENE LOS TIPOS DE FUENTE FINANCIAMIENTO PERMITIDOS
		List<Integer> listaFF = this.lFF();
		// ZD 03/03/16 SE OBTIENE LOS TIPOS DE DISCAPACIDAD PERMITIDOS
		List<Integer> listaTipoPCD = this.lPCD();
		for (i = 1; i < lLineasArch.size(); i++) {

			o = lLineasArch.get(i);
			dto = precondCompos(o);
			if (dto != null) {
				try {
					mesIterado = dto.getMes();
					if (dto.getPresup() != null)
					remuneracionesLeidas += dto.getPresup();

					if ((dto.getNivelEnti() == null) ||  (dto.getNivelEnti() != nivelEntidadOeeUtil.getCodNivelEntidad().intValue())) {
						
						//statusMessages.add(Severity.ERROR,"En linea "+i+" no coincide el Nivel ingresado");
						agregarEstadoMotivo("FRACASO", "No coincide el Nivel ingresado", i);
						cantFracaso += 1;
						continue;
					}
					if ((dto.getEntidad() == null) ||  (dto.getEntidad() != nivelEntidadOeeUtil.getCodSinEntidad().intValue())) {
						
						//statusMessages.add(Severity.ERROR,"En linea "+i+" no coincide la Entidad ingresada");
						agregarEstadoMotivo("FRACASO", "No coincide la Entidad ingresada", i);
						cantFracaso += 1;
						continue;
					}
					if ((dto.getOee() == null) ||  (dto.getOee() != nivelEntidadOeeUtil.getOrdenUnidOrg().intValue())) {
						
						//statusMessages.add(Severity.ERROR,"En linea "+i+" no coincide la OEE ingresada");
						agregarEstadoMotivo("FRACASO", "No coincide la OEE ingresada", i);
						cantFracaso += 1;
						continue;
					}
					
					//SETEAMOS LA INSTANCIA DE CONFIGURACION_UO_CAB PERTINENTE
					dto.setConfiguracionUoCab(confUoCabActual);
					
					if(dto.getMovimiento() == null)
						dto.setMovimiento("");
					
					if(dto.getDiscapaci() == null)
						dto.setDiscapaci("");
					
					if (dto.getErrorColumna() != null && !dto.getErrorColumna().equalsIgnoreCase("")) {
						agregarEstadoMotivo("FRACASO", dto.getErrorColumna(), i);
						cantFracaso += 1;
						continue;
					} else if (dto.getAnhio() != null && dto.getAnhio().intValue() != anho.intValue()) {
						agregarEstadoMotivo("FRACASO", "La columna Año no coincide con lo seleccionado", i);
						cantFracaso += 1;
						continue;
					} else if (dto.getMes() != null && dto.getMes().intValue() != mes.intValue()) {
						agregarEstadoMotivo("FRACASO", "La columna Mes no coincide con lo seleccionado", i);
						cantFracaso += 1;
						continue;
					} else if ((dto.getObjetoGto() != 0) && (!existeObjetoGto(dto.getObjetoGto()))){
						agregarEstadoMotivo("FRACASO", "No existe código de Objeto de Gasto en Base de Datos", i);
						cantFracaso += 1;
						continue;
					} else if ((dto.getObjetoGto() == 0) && (!dto.getEstado().equalsIgnoreCase("COMISIONADO"))){
						agregarEstadoMotivo("FRACASO", "Para Objeto de Gasto igual a 0 el estado debe ser COMISIONADO", i);
						cantFracaso += 1;
						continue;
					} else if ((dto.getObjetoGto() == 0) && ((dto.getPresup() != 0) || (dto.getDeven() != 0) || (dto.getFuenteFinancia() != 0))){
						agregarEstadoMotivo("FRACASO", "Para Objeto de Gasto igual a 0 los campos de Presupuestado, Devengado y FF deben ser 0", i);
						cantFracaso += 1;
						continue;
					} else if (!dto.getEstado().trim().equals("") && !listaE.contains(dto.getEstado().trim().toUpperCase())){
						agregarEstadoMotivo("FRACASO", "Valor no valido para Estado (PERMANENTE, CONTRATADO o COMISIONADO)", i);
						cantFracaso += 1;
						continue;
					} else if ((dto.getObjetoGto() == 111 || dto.getObjetoGto() == 112 || dto.getObjetoGto() == 161) && !dto.getEstado().equalsIgnoreCase("PERMANENTE")){
						agregarEstadoMotivo("FRACASO", "Código de Objeto de Gasto no valido para Estado "+dto.getEstado(), i);
						cantFracaso += 1;
						continue;
					} else if ((dto.getObjetoGto() > 139 && dto.getObjetoGto() < 150) && !dto.getEstado().equalsIgnoreCase("CONTRATADO")){
						agregarEstadoMotivo("FRACASO", "Código de Objeto de Gasto no valido para Estado "+dto.getEstado(), i);
						cantFracaso += 1;
						continue;
					} else if (((dto.getObjetoGto() == 111) ||  (dto.getObjetoGto() == 113) || (dto.getObjetoGto() == 161) || (dto.getObjetoGto() == 162)) && ((dto.getCateg() == null) || (dto.getCateg().trim().equals("")))){
						agregarEstadoMotivo("FRACASO", "Registro con OG 111, 113, 161 o 162 sin Categoria", i);
						cantFracaso += 1;
						continue;
					}
					//ZD 20/10/15 SE INCLUYE NUEVAS VALIDACIONES
					else if (!dto.getMovimiento().trim().equals("") && !listaM.contains(dto.getMovimiento().trim().toUpperCase())){
						agregarEstadoMotivo("FRACASO", "No existe el movimiento en la Base de Datos.", i);
						cantFracaso += 1;
						continue;
					}
					else if ((dto.getObjetoGto() != 111) && (dto.getObjetoGto() != 112) && (dto.getObjetoGto() != 161) && (dto.getObjetoGto() < 139 && dto.getObjetoGto() > 150) && (!dto.getEstado().equalsIgnoreCase("COMISIONADO"))){
						agregarEstadoMotivo("FRACASO", " Los codigos en la columna movimiento, solo pueden ser indicados en la fila correspondiente al sueldo principal", i);
						cantFracaso += 1;
						continue;
					}
					else if (dto.getEstado().equalsIgnoreCase("COMISIONADO") && !dto.getMovimiento().equalsIgnoreCase("TT")){
						agregarEstadoMotivo("FRACASO", " Estado COMISIONADO debe tener movimiento TT.", i);
						cantFracaso += 1;
						continue;
					}
					else if ((dto.getObjetoGto() != 111) && (dto.getObjetoGto() != 112) && (dto.getObjetoGto() != 161) && (dto.getObjetoGto() < 139 && dto.getObjetoGto() > 150) && (dto.getDiscapaci().trim().equalsIgnoreCase("S") || dto.getDiscapaci().trim().equalsIgnoreCase("SI"))){
						agregarEstadoMotivo("FRACASO", " Los codigos en la columna discapacidad, solo pueden ser indicados en la fila correspondiente al sueldo principal", i);
						cantFracaso += 1;
						continue;
					}
					else if ((dto.getPresup() < 0) && (dto.getObjetoGto() != 0)){
						agregarEstadoMotivo("FRACASO", " Presupuestado debe ser mayor a 0 para Objeto de Gasto. "+dto.getObjetoGto(), i);
						cantFracaso += 1;
						continue;
					}
					else if ((dto.getObjetoGto() != 0) && (dto.getFuenteFinancia() == 0)){
						agregarEstadoMotivo("FRACASO", " F.F. solo puede ser 0 cuando Objeto de Gasto tiene 0 ", i);
						cantFracaso += 1;
						continue;
					}
					else if ((dto.getObjetoGto() != 0) && !listaFF.contains(dto.getFuenteFinancia())){
						agregarEstadoMotivo("FRACASO", " F.F. no existe en la Base de Datos. ", i);
						cantFracaso += 1;
						continue;
					}
					else if (dto.getDiscapaci() != null && (dto.getDiscapaci().trim().equalsIgnoreCase("S") || dto.getDiscapaci().trim().equalsIgnoreCase("SI")) && !listaTipoPCD.contains(dto.getTipoDiscapaci())){
						agregarEstadoMotivo("FRACASO", " Tipo de Discapacidad no existe en la Base de Datos. ", i);
						cantFracaso += 1;
						continue;
					}
					else {
						cambioPersonaLeida = false;
						if(!cedulaActual.equalsIgnoreCase(dto.getCedula())){
							if(dto.getCedula().equalsIgnoreCase("0"))
								personaDTO = seleccionUtilFormController.buscarPersona(dto.getCedula(), "PARAGUAY");
							else if(!dto.getCedula().substring(0, 3).equalsIgnoreCase("EXT"))
								personaDTO = seleccionUtilFormController.buscarPersona(dto.getCedulaValida(dto.getCedula()), "PARAGUAY");
							else if(dto.getCedula().substring(0, 3).equalsIgnoreCase("EXT")){
									
								Persona personaLocal = new Persona();
								
								String sqlNacionalidad = "select * from seleccion.datos_especificos where datos_especificos.descripcion = 'NO SE CONOCE'";
								Query q0 = em.createNativeQuery(sqlNacionalidad, DatosEspecificos.class);
								DatosEspecificos datosEsp = (DatosEspecificos) q0.getResultList().get(0);
								
								//SE VERIFICA OBTIENE EL CODIGO CORTO DEL PAIS Y SE VERIFICA QUE EXISTA EN LA BASE DE DATOS MP - 17/05/2016	
								String sqlIdPais = "select * from general.pais where pais.prefijo = '"+dto.getCedula().substring(3,5)+"'";
								Query q1 = em.createNativeQuery(sqlIdPais, Pais.class);
									try {
										Pais paisExtranjero = (Pais) q1.getResultList().get(0);
										String codPais = paisExtranjero.getIdPais().toString();
										
										//SE BUSCA A LA PERSONA POR EL ID_PAIS_EXPEDICION_DOC Y POR NUMERO DE DOCUMENTO SIN EL EXT Y CODIGO CORTO   MP - 17/05/2016
										String sqlExtranjero = "select * from general.persona where persona.documento_identidad = '"+ dto.getCedulaValida(dto.getCedula()) +
												"' and persona.id_pais_expedicion_doc = "+codPais;
										Query q2 = em.createNativeQuery(sqlExtranjero, Persona.class);
											
										if(!q2.getResultList().isEmpty()){
											personaLocal = (Persona) q2.getResultList().get(0);
											personaDTO.setPersona(personaLocal);
											personaDTO.setInsertarSug(false);
											personaDTO.setHabilitarBtn(false);
										}
										else if (!q1.getResultList().isEmpty()){
											personaLocal.setNombres(dto.getNombres());
											personaLocal.setApellidos(dto.getApellidos());
											personaLocal.setPaisByIdPaisExpedicionDoc(paisExtranjero);
											personaLocal.setTelefonos("");
											personaLocal.setTelContacto("MOVIL");
											personaLocal.setDatosEspecificos(datosEsp);
											personaLocal.setActivo(true);
											personaLocal.setFechaAlta(new Date());
											personaLocal.setDocumentoIdentidad(dto.getCedulaValida(dto.getCedula()));
											personaLocal.setUsuAlta(this.usuarioLogueado.getCodigoUsuario());
												
											personaDTO.setPersona(personaLocal);
											personaDTO.setInsertarSug(true);
											personaDTO.setHabilitarBtn(true);
										}	
									} catch (Exception e) {
										agregarEstadoMotivo("FRACASO", " El pais de expedicion del documento especificado no existe en la Base de Datos de la SFP ", i);
										cantFracaso += 1;
										continue;
									}			
							}
							cedulaActual = dto.getCedula();
							//System.out.println("CONSULTA WEBSERVICE");
							cambioPersonaLeida = true;
						}
						
						if (personaDTO.getHabilitarBtn() == null) {
							//registrarImportacionRealizada(dto, "FRACASO", personaDTO.getMensaje(), "N", null);
							agregarEstadoMotivo("FRACASO", personaDTO.getMensaje(), i);
							cantFracaso += 1;
							continue;
						}else {
								
							if (compararNomApe(removeCadenasEspeciales(personaDTO.getPersona().getNombres()), removeCadenasEspeciales(dto.getNombres()))
									&& compararNomApe(removeCadenasEspeciales(personaDTO.getPersona().getApellidos()), removeCadenasEspeciales(dto.getApellidos())) ) {
																
								//registrarImportacionRealizada(dto, "EXITOSO", "", "N", null);
								pla.add(dto);
								agregarEstadoMotivo("EXITOSO", "", i);
								//Impresion para control en consola
								if(i%10000 == 0){
									System.out.println("[REMUNERACIONES] UO_CAB "+confUoCabActual.getIdConfiguracionUo()+" PROCESADA LÍNEA "+i);
								}
								cantExito++;
								
								//EN CASO DE getHabilitarBtn = true y getInsertarSug = true
								// se deberá insertar la personaDTO.getPersona() en general.persona
								// y ya no se emitirá el mensaje de "Persona no registrada en el SICCA"
								//ECESPEDES
								if (personaDTO.getHabilitarBtn() && personaDTO.getInsertarSug() ) {
									Persona persona = new Persona();
									persona = personaDTO.getPersona();
									persona.setFechaAlta(new Date());
									persona.setUsuAlta(usuarioLogueado.getCodigoUsuario());
									persona.setActivo(true);
									persona.setNombres(personaDTO.getPersona().getNombres());
									persona.setApellidos(personaDTO.getPersona().getApellidos());
									while (persona.getDocumentoIdentidad().substring(0,1).equals("0")){
										persona.setDocumentoIdentidad(persona.getDocumentoIdentidad().substring(1));
									}
									if(cambioPersonaLeida){
										if(!existeEnArrayPersona(personaDTO.getPersona().getDocumentoIdentidad()))
											arrayPersonasNuevas.add(persona);
									}
								}
									
								//SETEAMOS LA PERSONA, SI ES QUE ESTA YA SE ENCUENTRA GUARDADA EN LA BD, AL REGISTRO
								if(personaDTO.getPersona().getIdPersona() != null)
									dto.setPersona(em.find(Persona.class, personaDTO.getPersona().getIdPersona()));
									
							} else {
								//registrarImportacionRealizada(dto, "FRACASO", "Nombres y/o Apellidos no coinciden con BD", "N", null);
								//SE MODIFICA LA NOTIFICACION DE FRACASO A PEDIDO DE JUAN ADELFI AGUILERA   MP - 17/05/2016
								agregarEstadoMotivo("FRACASO", "Los datos Ingresados en Cedula, Nombres o Apellidos, No coincide con la Base de Datos, favor Verifique", i);
								cantFracaso += 1;
								continue;
							}
						}
					}
				}
				catch (JDBCException jdbce) {
					cantFracaso += 1;
					jdbce.getSQLException().getNextException().printStackTrace();
					//SE SETEA COMO ABORTADO EL REGISTRO DE CONTROL PERTINENTE
					registroControl.setEstadoProceso("ABORTADO");
					registroControl.setFechaFin(new Date());
					em.merge(registroControl);
					em.flush();
					//em.getTransaction().commit();
										
				}catch (Exception e) {
					cantFracaso += 1;
					agregarEstadoMotivo("FRACASO","Error desconocido al procesar la linea " + e.getMessage(), i);
					e.printStackTrace();
					statusMessages.add(Severity.ERROR, "Error desconocido. No se pudo realizar la operación.");
					
					//SE SETEA COMO ABORTADO EL REGISTRO DE CONTROL PERTINENTE
					registroControl.setEstadoProceso("ABORTADO");
					registroControl.setFechaFin(new Date());
					em.merge(registroControl);
					em.flush();
					//em.getTransaction().commit();
				}	
			}
			
			//SE SETEA LA CANTIDAD DE LINEAS PROCESADAS EN EL MOMENTO EL REGISTRO DE CONTROL PERTINENTE
			if(i%10000 == 0){
				Query qLineasProcesadas = em.createNativeQuery("UPDATE remuneracion.control_remuneraciones_tmp SET ultima_linea_procesada = "+i+
								" where id_control_remuneracion_tmp = "+registroControl.getIdControlRemuneracion()+"; COMMIT;");
				qLineasProcesadas.executeUpdate();
			}
		}
		
		//SE GUARDAN EN LA BD LAS PERSONAS NUEVAS ENCONTRADAS, INDEPENDIENTEMENTE DEL RESULTADO DEL PROCESAMIENTO DE LOS REGISTROS
		// ZD 20/08/2015
		System.out.println("[REMUNERACIONES] UO_CAB "+confUoCabActual.getIdConfiguracionUo()+" GUARDADO DE PERSONAS");
		int bat = 0;
		System.out.println("ARRAY PERSONAS "+arrayPersonasNuevas.size());
		for (Persona personaNueva : arrayPersonasNuevas) {
			em.persist(personaNueva);
			bat++;
			if ((bat % 1000) == 0) {
				em.flush();
				em.clear();
				bat = 0;
			}
		}
		em.flush();
		
		//SI SE ENCONTRO ALGUN FRACASO AL PROCESAR LOS REGISTROS O NO COINCIDIO LA SUMATORIA DE PRESUPUESTADOS, SE LIMPIA Y SE RETORNA SIN NINGUNA OTRA OPERACION
		if (cantFracaso > 0 || !remuneracionesTotales.equalsIgnoreCase(Long.toString(remuneracionesLeidas))){
			pla.clear();
			em.clear();
			arrayPersonasNuevas.clear();
			reemplazarPlanilla = false;
			
			//SE SETEA COMO FINALIZADO E INSERCION FRACASADA EL REGISTRO (INICIAL) DE CONTROL PERTINENTE
			registroControl.setUltimaLineaProcesada(i-1);
			registroControl.setEstadoProceso("FINALIZADO");
			registroControl.setInsercionExitosa(false);
			registroControl.setFechaFin(new Date());
			em.merge(registroControl);
			em.flush();
			
			return true;
		}
		
		//CONTROL DE REEMPLAZO DE PLANILLA EN TABLA REMUNERACIONES_TMP
		if (reemplazarPlanilla && existeEnRemuneraciones) {
			System.out.println("[REMUNERACIONES] UO_CAB "+confUoCabActual.getIdConfiguracionUo()+" BORRADO REGISTROS ANTERIORES REMUNERACIONES_TMP");
			borrarRegistrosAnterioresEnRemuneracionesTmp();
		}
		
		//CONTROL DE REEMPLAZO DE PLANILLA EN TABLA HISTORICO_REMUNERACIONES_TMP
		if (reemplazarPlanilla && existeEnHistorico) {
			System.out.println("[REMUNERACIONES] UO_CAB "+confUoCabActual.getIdConfiguracionUo()+" BORRADO REGISTROS ANTERIORES HISTORICO_REMUNERACIONES_TMP");
			borrarRegistrosAnterioresEnHistorico();
		}
		
		//SI NO SE ENCONTRARON FRACASOS AL PROCESAR LOS REGISTROS DE LA PLANILLA, SE PROSIGUE
		//SE VERIFICA SI LA PLANILLA INGRESADA CORRESPONDE A UN NUEVO MES, DE SER ASI SE MUEVEN LOS REGISTROS DE ESA OEE DE MESES ANTERIORES A LA TABLA HISTORICA
		if(esNuevoMes()){
			//cargarRemuneracionesAnteriores();
			System.out.println("[REMUNERACIONES] UO_CAB "+confUoCabActual.getIdConfiguracionUo()+" NUEVO MES");
			cargarRemuneracionesAnterioresDIRECTO();
		}
		int k = 1;
		int l = 1;
		Session session = (Session)em.getDelegate();
		Connection connection = session.connection();	
		String sqlTemporal = queryTmp();
		crearTemporal();
		try{
			PreparedStatement ps = connection.prepareStatement(sqlTemporal);
		
			while(!pla.isEmpty()){
				Iterator<DTO680Tmp> indicePla = pla.iterator();
				while(k <= 10000 && indicePla.hasNext()) {
					DTO680Tmp dtoRemu = indicePla.next();
					if(dtoRemu.getPersona() == null){
						Persona personaAux = buscarBDLocal(dtoRemu.getCedulaValida(dtoRemu.getCedula()));
						dtoRemu.setPersona(personaAux);
					}
					//corePaso4(dtoRemu);
					// SE GUARDAN LOS REGISTROS PROCESADOS EN UNA TABLA TEMPORAL
					cargarQueryTmp(ps, dtoRemu, connection);
					ps.addBatch();
				  
					k++;
					l++;
				}
				if(pla.size()>10000){
					ps.executeBatch();
					pla.subList(0, 10000).clear();
				}	
				else{
					pla.clear();
				}
				k=1;
				System.out.println("[REMUNERACIONES] UO_CAB "+confUoCabActual.getIdConfiguracionUo()+" CARGADA LÍNEA "+l);
			}
			ps.executeBatch();
			ps.close();
			connection.close();
		} catch (SQLException sqlexcep){
			sqlexcep.printStackTrace();
			if(sqlexcep.getNextException() != null) {
			    sqlexcep.getNextException().printStackTrace();
			}
			if(sqlexcep.getNextException().getNextException() != null) {
			    sqlexcep.getNextException().getNextException().printStackTrace();
			}
		} catch (Exception e) {
			statusMessages.add(Severity.ERROR,"Error al preparar campos en general");
			System.out.println("[REMUNERACIONES] UO_CAB "+confUoCabActual.getIdConfiguracionUo()+" ERROR AL PREPARAR CAMPOS EN GENERAL");
			e.printStackTrace();
		}
		
		//SE GUARDAN LOS REGISTROS PROCESADOS EN LA TABLA FINAL, REMUNERACIONES_TMP
		if(insertarEnHistoricoDirecto){
			System.out.println("[REMUNERACIONES] UO_CAB "+confUoCabActual.getIdConfiguracionUo()+" CARGA EN HISTORICO_REMUNERACIONES_TMP");
			// SE GUARDAN LOS REGISTROS PROCESADOS EN LA TABLA FINAL HISTORICO_REMUNERACIONES_TMP
			finalizarProcesoHistorico();
		}
		else{
			// ZD 27/08/15 SE CREA UNA TABLA TEMPORAL
			System.out.println("[REMUNERACIONES] UO_CAB "+confUoCabActual.getIdConfiguracionUo()+" CARGA EN REMUNERACIONES_TMP");
			// 27/08/15 SE GUARDAN LOS REGISTROS PROCESADOS EN LA TABLA FINAL,
			// REMUNERACIONES_TMP
			finalizarProceso();
		}
		
		//SE SETEA COMO FINALIZADO E INSERCION EXITOSA EL REGISTRO (INICIAL) DE CONTROL PERTINENTE
		registroControl.setUltimaLineaProcesada(i-1);
		registroControl.setEstadoProceso("FINALIZADO");
		registroControl.setInsercionExitosa(true);
		registroControl.setFechaFin(new Date());
		em.merge(registroControl);
		//GUARDAR O ACTUALIZAR EL REGISTRO PERTINENTE EN LA TABLA REMUNERACION.PROCESAMIENTO
		guardarProcesamientoPlanilla();
		em.flush();
		
		em.clear();
		arrayPersonasNuevas.clear();
		reemplazarPlanilla = false;
		
		//SE SETEA LA VARIABLE QUE DEFINE SI SE GUARDA O NO EL ARCHIVO DE RESULTADO EN EL SERVIDOR
		guardarArchivo = true;
		
		System.out.println("[REMUNERACIONES] UO_CAB "+confUoCabActual.getIdConfiguracionUo()+" PLANILLA PROCESADA TOTALMENTE");
		
		return true;
	}
	// ZD 27/08/15
	public void crearTemporal() {
		Query q = em.createNativeQuery("CREATE TEMPORARY TABLE remu_batch_tmp "
				+ " (anho integer, " + " mes integer, " + " nivel integer, "
				+ " entidad integer, " + " oee integer, " + " linea integer, "
				+ " documento_identidad character varying(30), "
				+ " nombres character varying(50), "
				+ " apellidos character varying(50), "
				+ " estado character varying(12), "
				+ " remuneracion_total integer, " + " obj_codigo integer, "
				+ " fuente_financiamiento integer, "
				+ " categoria character varying(10), "
				+ " presupuestado integer, " + " devengado integer, "
				+ " concepto character varying(200), "
				+ " movimiento character varying(3), "
				+ " lugar character varying(125), "
				+ " cargo character varying(250), "
				+ " funcion_cumplida character varying(500), "
				+ " carga_horaria character varying, "
				+ " discapacidad boolean, " + " tipo_discapacidad integer, "
				+ " anho_ingreso integer, " + " activo boolean, "
				+ " usu_alta character varying(50), "
				+ " fecha_alta timestamp without time zone, "
				+ " id_configuracion_uo bigint, "
				+ " id_persona bigint, "
				+ " fecha_mod timestamp without time zone, "
				+ " usu_paso_historico character varying(50), "
				+ " fecha_paso_historico timestamp without time zone "
				// + " usu_mod character varying(50), "
				// + " fecha_mod timestamp without time zone "
				+ " ) ON COMMIT DROP;");
		q.executeUpdate();
	}


	public String validar(String descripcion){
		if (descripcion == null){
			descripcion = "";
		}else if(descripcion.contains("'")){
			descripcion = descripcion.replace("'", "''");
		}
		return descripcion;
	}

	// ZD 27/08/15
	public void finalizarProceso() {
		try {
			Query q = em
					.createNativeQuery("insert into remuneracion.remuneraciones_tmp (anho, "
							+ "mes, "
							+ "nivel, "
							+ "entidad, "
							+ "oee, "
							+ "linea, "
							+ "documento_identidad, "
							+ "nombres, "
							+ "apellidos, "
							+ "estado, "
							+ "remuneracion_total, "
							+ "obj_codigo, "
							+ "fuente_financiamiento, "
							+ "categoria, "
							+ "presupuestado, "
							+ "devengado, "
							+ "concepto, "
							+ "movimiento, "
							+ "lugar, "
							+ "cargo, "
							+ "funcion_cumplida, "
							+ "carga_horaria, "
							+ "discapacidad, "
							+ "tipo_discapacidad, "
							+ "anho_ingreso, "
							+ "activo, "
							+ "usu_alta, "
							+ "fecha_alta, "
							+"id_configuracion_uo, "
							+"id_persona, "
							+"fecha_mod )"
							+" select anho,mes,nivel,entidad,oee,linea,documento_identidad,nombres,"
							+"apellidos,estado,remuneracion_total,obj_codigo,fuente_financiamiento,categoria,presupuestado,devengado,concepto,movimiento,lugar,cargo,"
							+"funcion_cumplida,carga_horaria,discapacidad,tipo_discapacidad,anho_ingreso,activo,usu_alta,fecha_alta,id_configuracion_uo,id_persona,"
							+"fecha_mod from remu_batch_tmp; COMMIT; ");
			q.executeUpdate();
		} catch (Exception e) {
			statusMessages.add(Severity.ERROR,
					"No se pudo realizar la operacion");
			e.printStackTrace();
		}
	}
	
	public void finalizarProcesoHistorico() {
		try {
			Query q = em
					.createNativeQuery("insert into remuneracion.historico_remuneraciones_tmp (anho, "
							+ "mes, "
							+ "nivel, "
							+ "entidad, "
							+ "oee, "
							+ "linea, "
							+ "documento_identidad, "
							+ "nombres, "
							+ "apellidos, "
							+ "estado, "
							+ "remuneracion_total, "
							+ "obj_codigo, "
							+ "fuente_financiamiento, "
							+ "categoria, "
							+ "presupuestado, "
							+ "devengado, "
							+ "concepto, "
							+ "movimiento, "
							+ "lugar, "
							+ "cargo, "
							+ "funcion_cumplida, "
							+ "carga_horaria, "
							+ "discapacidad, "
							+ "tipo_discapacidad, "
							+ "anho_ingreso, "
							+ "activo, "
							+ "usu_alta, "
							+ "fecha_alta, "
							+"id_configuracion_uo, "
							+"id_persona, "
							+"fecha_mod, usu_paso_historico, fecha_paso_historico)" + " select * from remu_batch_tmp; COMMIT; ");
			q.executeUpdate();
			
		} catch (Exception e) {
			statusMessages.add(Severity.ERROR,
					"No se pudo realizar la operacion");
			e.printStackTrace();
		}
	}
	
	String queryTmp() {
		String sql = "INSERT INTO remu_batch_tmp (anho,mes,nivel,entidad,oee,linea,documento_identidad,nombres,"+
				"apellidos,estado,remuneracion_total,obj_codigo,fuente_financiamiento,categoria,presupuestado,devengado,concepto,movimiento,lugar,cargo,"+
				"funcion_cumplida,carga_horaria,discapacidad,tipo_discapacidad,anho_ingreso,activo,usu_alta,fecha_alta,id_configuracion_uo,id_persona,fecha_mod,usu_paso_historico,fecha_paso_historico)"+
				" VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			return sql;
	}
	
	void cargarQueryTmp(PreparedStatement ps, DTO680Tmp dto, Connection con) {
		try{
			ps.setInt(1, dto.getAnhio());
			ps.setInt(2, dto.getMes());
			ps.setInt(3, dto.getNivelEnti());
			ps.setInt(4, dto.getEntidad());
			ps.setInt(5, dto.getOee());
			
			if(dto.getLinea() != null)
				ps.setInt(6, dto.getLinea());
			else
				ps.setInt(6, 0);
			
			ps.setString(7, dto.getCedula());
			ps.setString(8, dto.getNombres().toUpperCase());
			ps.setString(9, dto.getApellidos().toUpperCase());

			if (dto.getEstado() != null && !dto.getEstado().trim().isEmpty())
				ps.setString(10, dto.getEstado().toUpperCase());
			else
				ps.setString(10, dto.getEstado());
			
			if(dto.getRemuneracionTot() != null)
				ps.setInt(11, dto.getRemuneracionTot());
			else
				ps.setInt(11, 0);
			
			if(dto.getObjetoGto() != null)
				ps.setInt(12, dto.getObjetoGto());
			else
				ps.setInt(12, 0);
			
			if(dto.getFuenteFinancia() != null)
				ps.setInt(13, dto.getFuenteFinancia());
			else
				ps.setInt(13, 0);
			
			if(dto.getCateg() != null)
				ps.setString(14, dto.getCateg().toUpperCase());
			else
				ps.setString(14, dto.getCateg());
			
			if(dto.getPresup() != null)
				ps.setInt(15, dto.getPresup());
			else
				ps.setInt(15, 0);
			
			if(dto.getDeven() != null)
				ps.setInt(16, dto.getDeven());
			else
				ps.setInt(16, 0);
			
			if (dto.getConcep() != null && !dto.getConcep().trim().isEmpty())
				ps.setString(17, dto.getConcep().toUpperCase());
			else
				ps.setString(17, dto.getConcep());
			
			if (dto.getMovimiento() != null && !dto.getMovimiento().trim().isEmpty())
				ps.setString(18, dto.getMovimiento().toUpperCase());
			else
				ps.setString(18, dto.getMovimiento());
			
			if (dto.getLugar() != null && !dto.getLugar().trim().isEmpty())
				ps.setString(19, dto.getLugar().toUpperCase());
			else
				ps.setString(19, dto.getLugar());
			
			if (dto.getCargo() != null && !dto.getCargo().trim().isEmpty())
				ps.setString(20, dto.getCargo().toUpperCase());
			else
				ps.setString(20, dto.getCargo());
			
			if (dto.getFuncion() != null && !dto.getFuncion().trim().isEmpty())
				ps.setString(21, dto.getFuncion().toUpperCase());
			else
				ps.setString(21, dto.getFuncion());
			
			if (dto.getHorario() != null && !dto.getHorario().trim().isEmpty())
				ps.setString(22, dto.getHorario().toUpperCase());
			else
				ps.setString(22, dto.getHorario());
			
			if(dto.getDiscapaci() != null){
				if ((dto.getDiscapaci().equals("S")) || (dto.getDiscapaci().equals("SI"))){
					ps.setBoolean(23, true);
					ps.setInt(24, dto.getTipoDiscapaci());
				}
				else{
					ps.setBoolean(23, false);
					ps.setInt(24, 0);
				}
			}
			else{
				ps.setBoolean(23, false);
				ps.setInt(24, 0);
			}
			
			if(dto.getAnhioIngreso() != null)
				ps.setInt(25, dto.getAnhioIngreso());
			else
				ps.setInt(25, 0);
			
			ps.setBoolean(26, true);
			ps.setString(27, usuarioLogueado.getCodigoUsuario());
			ps.setTimestamp(28, new Timestamp(System.currentTimeMillis()));
			ps.setLong(29, dto.getConfiguracionUoCab().getIdConfiguracionUo());
			
			if(dto.getPersona() != null)
				ps.setLong(30, dto.getPersona().getIdPersona());
			
			if(reemplazarPlanilla)
				ps.setTimestamp(31, new Timestamp(System.currentTimeMillis()));
			else
				ps.setTimestamp(31, null);
			
			if(insertarEnHistoricoDirecto){
				ps.setString(32, usuarioLogueado.getCodigoUsuario());
				ps.setTimestamp(33, new Timestamp(System.currentTimeMillis()));
			}
			else{
				ps.setString(32, null);
				ps.setTimestamp(33, null);
			}
			
		} catch (Exception e) {
			statusMessages.add(Severity.ERROR,"Error al preparar campos");
			try{
				ps.close();
				con.close();
				System.out.println("CERRANDO CONEXION POR ERROR AL CARGAR LOTE PARA TABLA TEMPORAL");
			}
			catch (SQLException sqle) {
				System.out.println("ERROR AL CERRAR CONEXION");
				e.printStackTrace();
			}
			System.out.println("ERROR AL PREPARAR CAMPOS");
			e.printStackTrace();
		}
	}
	
	private boolean existeEnArrayPersona(String nroCI){
		for (Persona personaNueva : arrayPersonasNuevas) {
			if(personaNueva.getDocumentoIdentidad().equalsIgnoreCase(nroCI)){
				return true;
			}
		}
		return false;
	}

	private List<RemuneracionesTmp> listaRemuMesAnhoOee() {
		Query q = em
				.createQuery("select Remuneraciones from RemuneracionesTmp Remuneraciones "
						+ " where Remuneraciones.mes = :mes and Remuneraciones.anho = :anho "
						+ " and Remuneraciones.nivel = :nivel and Remuneraciones.entidad = :entidad"
						+ " and Remuneraciones.oee = :oee and Remuneraciones.activo is True");

		q.setParameter("mes", mes);
		q.setParameter("anho", anho);
		q.setParameter("nivel", nivelEntidadOeeUtil.getCodNivelEntidad().intValue());
		q.setParameter("entidad", nivelEntidadOeeUtil.getCodSinEntidad().intValue());
		q.setParameter("oee", nivelEntidadOeeUtil.getOrdenUnidOrg().intValue());

		List<RemuneracionesTmp> lista = q.setMaxResults(2).getResultList();
		System.out.println("TAMANHO LISTAREMUMESANHOOEE "+lista.size());
		return lista;

	}
	
	private List<HistoricoRemuneracionesTmp> listaRemuMesAnhoOeeHistorico() {
		Query q = em
				.createQuery("select Remuneraciones  from HistoricoRemuneracionesTmp Remuneraciones "
						+ " where Remuneraciones.mes = :mes and Remuneraciones.anho = :anho "
						+ " and Remuneraciones.nivel = :nivel and Remuneraciones.entidad = :entidad"
						+ " and Remuneraciones.oee = :oee and Remuneraciones.activo is True");

		q.setParameter("mes", mes);
		q.setParameter("anho", anho);
		q.setParameter("nivel", nivelEntidadOeeUtil.getCodNivelEntidad().intValue());
		q.setParameter("entidad", nivelEntidadOeeUtil.getCodSinEntidad().intValue());
		q.setParameter("oee", nivelEntidadOeeUtil.getOrdenUnidOrg().intValue());

		List<HistoricoRemuneracionesTmp> lista = q.setMaxResults(2).getResultList();
		System.out.println("ARRAY LISTAREMUMESANHOOEEHISTORICO "+lista.size());
		return lista;

	}

	private Boolean verificarExisteRegistro() {
		List lista = listaRemuMesAnhoOee();
		if (lista.size() > 0) {
			//EXISTEN REGISTROS PARA LA OEE A PROCESAR, EN EL MES Y ANHO DEFINIDOS
			return true;
		}
		//NO EXISTEN DICHOS REGISTROS
		return false;
	}
	
	private Boolean verificarRemuMenorAActual() {
		Query q = em.createQuery("select Remuneraciones  from RemuneracionesTmp Remuneraciones "
						+ " where Remuneraciones.anho > :anho "
						+ " and Remuneraciones.nivel = :nivel and Remuneraciones.entidad = :entidad"
						+ " and Remuneraciones.oee = :oee and Remuneraciones.activo is true");

		q.setParameter("anho", anho);
		q.setParameter("nivel", nivelEntidadOeeUtil.getCodNivelEntidad().intValue());
		q.setParameter("entidad", nivelEntidadOeeUtil.getCodSinEntidad().intValue());
		q.setParameter("oee", nivelEntidadOeeUtil.getOrdenUnidOrg().intValue());

		List<RemuneracionesTmp> lista = q.setMaxResults(2).getResultList();
		if (lista.size() > 0) {
			//EXISTEN REGISTROS CON AÑOS MAYORES PARA DICHA OEE EN LA TABLA REMUNERACIONES_TMP
			return true;
		}
		
		q = em.createQuery("select Remuneraciones  from RemuneracionesTmp Remuneraciones "
						+ " where Remuneraciones.mes > :mes and Remuneraciones.anho = :anho "
						+ " and Remuneraciones.nivel = :nivel and Remuneraciones.entidad = :entidad"
						+ " and Remuneraciones.oee = :oee and Remuneraciones.activo is true");

		q.setParameter("anho", anho);
		q.setParameter("mes", mes);
		q.setParameter("nivel", nivelEntidadOeeUtil.getCodNivelEntidad().intValue());
		q.setParameter("entidad", nivelEntidadOeeUtil.getCodSinEntidad().intValue());
		q.setParameter("oee", nivelEntidadOeeUtil.getOrdenUnidOrg().intValue());

		lista = q.setMaxResults(2).getResultList();
		if (lista.size() > 0) {
			//EXISTEN REGISTROS CON MESES MAYORES PARA DICHA OEE EN DICHO AÑO, EN LA TABLA REMUNERACIONES_TMP
			return true;
		}
		
		//NO EXISTEN DICHOS REGISTROS
		return false;
	}
	
	private Boolean verificarExisteRegistroHistorico() {
		List lista = listaRemuMesAnhoOeeHistorico();
		if (lista.size() > 0) {
			//EXISTEN REGISTROS PARA LA OEE A PROCESAR, EN EL MES Y ANHO DEFINIDOS
			return true;
		}
		//NO EXISTEN DICHOS REGISTROS
		return false;
	}

	private void procesarArchivo() throws IOException,
			ParseException {
			borrarRegistrosAnterioresEnRemuneracionesTmp();
			massiveImport();
	}

	public void procesarRespuestaModal(String tipo) throws IOException,
			ParseException {
		limpiarModales();
		if (tipo.equalsIgnoreCase("SI")) {
			reemplazarPlanilla = true;
			auxReemplazo = true;
			massiveImport();
		}else if (tipo.equalsIgnoreCase("NO")) {
			mostrarModalAgregar = Boolean.TRUE;
		}

	}

	public Boolean getMostrarModalAgregar() {
		return mostrarModalAgregar;
	}

	public void setMostrarModalAgregar(Boolean mostrarModalAgregar) {
		this.mostrarModalAgregar = mostrarModalAgregar;
	}

	@Transactional
	private void borrarRegistrosAnterioresEnRemuneracionesTmp() {
		/*List<RemuneracionesTmp> lista = listaRemuMesAnhoOee();
		for (RemuneracionesTmp o : lista) {
			em.remove(o);
		}
		em.flush();*/
		System.out.println("MOVIENDO REMUNERACIONES INHABILITADAS");
		try {
			/*Query q = em.createNativeQuery("UPDATE remuneracion.remuneraciones_tmp SET activo = False, "+
			"fecha_inactivacion = (SELECT TIMESTAMP WITHOUT TIME ZONE 'now'), usu_mod = '"+
			usuarioLogueado.getCodigoUsuario()+"', fecha_mod = (SELECT TIMESTAMP WITHOUT TIME ZONE 'now') WHERE "+
			"mes = "+mes+" and anho = "+anho+" and nivel = "+ nivelEntidadOeeUtil.getCodNivelEntidad().intValue()+
			" and entidad = "+nivelEntidadOeeUtil.getCodSinEntidad().intValue()+
			" and oee = "+nivelEntidadOeeUtil.getOrdenUnidOrg().intValue()+" and activo = True; COMMIT;");
			q.executeUpdate();*/
			String isql = "INSERT INTO remuneracion.audit_inactivos (anho,mes,nivel,entidad,oee,linea,documento_identidad,nombres,apellidos,"+
					"estado,remuneracion_total,obj_codigo,fuente_financiamiento,categoria,presupuestado,devengado,concepto,movimiento,lugar,cargo,funcion_cumplida,"+
					"carga_horaria,discapacidad,tipo_discapacidad,anho_ingreso,activo,usu_alta,fecha_alta,usu_mod,fecha_mod,id_configuracion_uo,id_persona)"+
					" SELECT anho,mes,nivel,entidad,oee,linea,documento_identidad,nombres,apellidos,estado,remuneracion_total,obj_codigo,fuente_financiamiento,"+
					"categoria,presupuestado,devengado,concepto,movimiento,lugar,cargo,funcion_cumplida,carga_horaria,discapacidad,tipo_discapacidad,anho_ingreso,"+
					"false,usu_alta,fecha_alta,'"+usuarioLogueado.getCodigoUsuario()+"',(SELECT TIMESTAMP WITHOUT TIME ZONE 'now'), id_configuracion_uo,id_persona "+
					" from remuneracion.remuneraciones_tmp WHERE "+" mes = "+mes+" and anho = "+anho+" and nivel = "+
					nivelEntidadOeeUtil.getCodNivelEntidad().intValue()+" and entidad = "+nivelEntidadOeeUtil.getCodSinEntidad().intValue()+
					" and oee = "+nivelEntidadOeeUtil.getOrdenUnidOrg().intValue()+" and activo = true; COMMIT;";
			Query q = em.createNativeQuery(isql);
			q.executeUpdate();
			System.out.println("BORRANDO REMUNERACIONES INHABILITADAS");
			q = em.createNativeQuery("DELETE FROM remuneracion.remuneraciones_tmp  WHERE "+" mes = "+mes+" and anho = "+anho+" and nivel = "+
					nivelEntidadOeeUtil.getCodNivelEntidad().intValue()+" and entidad = "+nivelEntidadOeeUtil.getCodSinEntidad().intValue()+
					" and oee = "+nivelEntidadOeeUtil.getOrdenUnidOrg().intValue()+" and activo = true; COMMIT;");
			q.executeUpdate();
			
		} catch (Exception e) {
			statusMessages.add(Severity.ERROR,"Error al inactivar registros anteriores en Remuneraciones");
			System.out.println("Error al inactivar registros anteriores en Remuneraciones");
			e.printStackTrace();
		}
}
	@Transactional
	private void borrarRegistrosAnterioresEnHistorico() {
		/*List<HistoricoRemuneracionesTmp> lista = listaRemuMesAnhoOeeHistorico();
		for (HistoricoRemuneracionesTmp o : lista) {
			em.remove(o);
		}
		em.flush();*/
		System.out.println("MOVIENDO REMUNERACIONES HISTORICAS INHABILITADAS");
		try {
			/*Query q = em.createNativeQuery("UPDATE remuneracion.historico_remuneraciones_tmp SET activo = False, "+
							"fecha_inactivacion = (SELECT TIMESTAMP WITHOUT TIME ZONE 'now'), usu_mod = '"+
							usuarioLogueado.getCodigoUsuario()+"', fecha_mod = (SELECT TIMESTAMP WITHOUT TIME ZONE 'now') WHERE "+
							"mes = "+mes+" and anho = "+anho+" and nivel = "+ nivelEntidadOeeUtil.getCodNivelEntidad().intValue()+
							" and entidad = "+nivelEntidadOeeUtil.getCodSinEntidad().intValue()+
							" and oee = "+nivelEntidadOeeUtil.getOrdenUnidOrg().intValue()+" and activo = True; COMMIT;");
			q.executeUpdate();*/
			String isql = "INSERT INTO remuneracion.audit_inactivos (anho,mes,nivel,entidad,oee,linea,documento_identidad,nombres,apellidos,"+
					"estado,remuneracion_total,obj_codigo,fuente_financiamiento,categoria,presupuestado,devengado,concepto,movimiento,lugar,cargo,funcion_cumplida,"+
					"carga_horaria,discapacidad,tipo_discapacidad,anho_ingreso,activo,usu_alta,fecha_alta,usu_mod,fecha_mod,id_configuracion_uo,id_persona,"+
					"usu_paso_historico,fecha_paso_historico)"+
					" SELECT anho,mes,nivel,entidad,oee,linea,documento_identidad,nombres,apellidos,estado,remuneracion_total,obj_codigo,fuente_financiamiento,"+
					"categoria,presupuestado,devengado,concepto,movimiento,lugar,cargo,funcion_cumplida,carga_horaria,discapacidad,tipo_discapacidad,anho_ingreso,"+
					"false,usu_alta,fecha_alta,'"+usuarioLogueado.getCodigoUsuario()+"',(SELECT TIMESTAMP WITHOUT TIME ZONE 'now'), id_configuracion_uo,id_persona, "+ 
					"usu_paso_historico, fecha_paso_historico "+
					" from remuneracion.historico_remuneraciones_tmp WHERE "+" mes = "+mes+" and anho = "+anho+" and nivel = "+
					nivelEntidadOeeUtil.getCodNivelEntidad().intValue()+" and entidad = "+nivelEntidadOeeUtil.getCodSinEntidad().intValue()+
					" and oee = "+nivelEntidadOeeUtil.getOrdenUnidOrg().intValue()+" and activo = true; COMMIT;";
			Query q = em.createNativeQuery(isql);
			q.executeUpdate();
			System.out.println("BORRANDO REMUNERACIONES HISTORICAS INHABILITADAS");
			q = em.createNativeQuery("DELETE FROM remuneracion.historico_remuneraciones_tmp  WHERE "+" mes = "+mes+" and anho = "+anho+" and nivel = "+
					nivelEntidadOeeUtil.getCodNivelEntidad().intValue()+" and entidad = "+nivelEntidadOeeUtil.getCodSinEntidad().intValue()+
					" and oee = "+nivelEntidadOeeUtil.getOrdenUnidOrg().intValue()+" and activo = true; COMMIT;");
			q.executeUpdate();
		} catch (Exception e) {
			statusMessages.add(Severity.ERROR,"Error al inactivar registros anteriores en Historico Remuneraciones");
			System.out.println("Error al inactivar registros anteriores en Historico Remuneraciones");
			e.printStackTrace();
		}
		


	}

	public void limpiarModales() {
		mostrarModalAgregar = Boolean.FALSE;
		mostrarModalRemplazo =  Boolean.FALSE;
		limpiarModales =  Boolean.FALSE;
	}

	public Double getTotalPresupuesto() {
		return totalPresupuesto;
	}

	public void setTotalPresupuesto(Double totalPresupuesto) {
		this.totalPresupuesto = totalPresupuesto;
	}

	public Double getTotalDevengado() {
		return totalDevengado;
	}

	public void setTotalDevengado(Double totalDevengado) {
		this.totalDevengado = totalDevengado;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public void massiveImport() throws IOException, ParseException {
		if (!precondInsert()) {
			return;
		}
		limpiarModales();
		if(verificarRemuMenorAActual())
			insertarEnHistoricoDirecto = true;
		UploadItem fileItem = seleccionUtilFormController.crearUploadItem(fname, uFile.length, cType, uFile);
		lLineasArch = FileUtils.readLines(fileItem.getFile(), "ISO-8859-1");
		
		if (procesarLineas()) {
			agregarResumen();
			SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
			String nArchivo = sdf2.format(new Date()) + "_" + fname;
			
			String direccion = AdmDocAdjuntoFormController.ponerBarraSimple("\\vol01\\SICCA\\REMUNERACIONES\\OEE_"+nivelEntidadOeeUtil.getIdConfigCab().toString()+"\\"+anho+"\\"+mes+"\\");
			
			File archSalida = new File(nArchivo);
			FileUtils.writeLines(archSalida, lLineasArch);
			
			JasperReportUtils.respondFile(archSalida, nArchivo, false, "application/vnd.ms-excel");
			if(guardarArchivo){
				//String direccion = AdmDocAdjuntoFormController.ponerBarraSimple("\\vol01\\SICCA\\REMUNERACIONES\\OEE_"+nivelEntidadOeeUtil.getIdConfigCab().toString());
				//ZD 21/06/16 -- Si se procesa por primera vez, incluir marca de _new = nuevo
				if(!existeEnRemuneraciones && !existeEnHistorico){
						nArchivo = "new_" +nArchivo;
				}else{
					if(auxReemplazo)
						//-- Si se reemplaza, incluir marca de rem_ = reemplazo
						nArchivo = "rem_" + nArchivo;
					else
						//-- Si se agrega, incluir marca de add_ = agregado
						nArchivo = "add_" + nArchivo;
				}
				auxReemplazo = false;
				AdmDocAdjuntoFormController.guardarArchivo(nArchivo, archSalida, ".csv", direccion);
				guardarArchivo = false;
			}
			//em.flush();
			enviarMailUsuario(mesIterado);
			enviarMail(mesIterado);
		} else {
			if (!registros.equalsIgnoreCase(Integer.toString(lLineasArch.size()-1)))
				statusMessages.add(Severity.ERROR, "Número de registros en archivo no coincide con Cantidad de Registros ingresada");
			else if (mesIterado != null)
				statusMessages.add(Severity.INFO, "Archivo inválido");
			else
				statusMessages.add(Severity.ERROR, "No se pudo realizar la operación");
		}
	}

	public void descargar() throws IOException, ParseException {
		try {
			String linea = "";
			if (ldto != null && ldto.size() > 0) {
				SimpleDateFormat sdf2 = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss");
				String nArchivo = "Niv:"
						+ nivelEntidadOeeUtil.getCodNivelEntidad().toString()
						+ "_Ent:" + nivelEntidadOeeUtil.getCodSinEntidad()
						+ "_" + sdf2.format(new Date()) + ".csv";
				File archSalida = new File(nArchivo);
				ArrayList<String> listaLineas = new ArrayList<String>();
				listaLineas.add("estado;" + cabeceraMatriz);
				for (DTO680Tmp dto : ldto) {
					for (DTO680Tmp dato : ldto) {
						linea = "";
						if (dato.getAnhio() != null)
							linea += (dato.getAnhio().toString());
						linea += (";");
						
						if (dato.getMes() != null)
							linea += (dato.getMes().toString());
						linea += (";");
						
						if (dato.getNivelEnti() != null)
							linea += (dato.getNivelEnti().toString());
						linea += (";");
						
						if (dato.getEntidad() != null)
							linea += (dato.getEntidad().toString());
						linea += (";");
						
						if (dato.getOee() != null)
							linea += (dato.getOee().toString());
						linea += (";");
						
						if (dato.getLinea() != null)
							linea += (dato.getLinea().toString());
						linea += (";");
						
						if (dato.getCedula() != null)
							linea += (dato.getCedula().toString());
						linea += (";");

						if (dato.getNombres() != null)
							linea += (dato.getNombres().toString());
						linea += (";");
						
						if (dato.getApellidos() != null)
							linea += (dato.getApellidos().toString());
						linea += (";");
						
						if (dato.getEstado() != null)
							linea += (dato.getEstado().toString());
						linea += (";");
						
						if (dato.getRemuneracionTot() != null)
							linea += (dato.getRemuneracionTot().toString());
						linea += (";");
						
						if (dato.getObjetoGto() != null)
							linea += (dato.getObjetoGto().toString());
						linea += (";");
						
						if (dato.getFuenteFinancia() != null)
							linea += (dato.getFuenteFinancia().toString());
						linea += (";");

						if (dato.getCateg() != null)
							linea += (dato.getCateg().toString());
						linea += (";");
						
						if (dato.getPresup() != null)
							linea += (dato.getPresup().toString());
						linea += (";");
						
						if (dato.getDeven() != null)
							linea += (dato.getDeven().toString());
						linea += (";");
						
						if (dato.getConcep() != null)
							linea += (dato.getConcep().toString());
						linea += (";");
						
						if (dato.getMovimiento() != null)
							linea += (dato.getMovimiento().toString());
						linea += (";");
						
						if (dato.getLugar() != null)
							linea += (dato.getLugar().toString());
						linea += (";");
						
						if (dato.getCargo() != null)
							linea += (dato.getCargo().toString());
						linea += (";");
						
						if (dato.getFuncion() != null)
							linea += (dato.getFuncion().toString());
						linea += (";");
						
						if (dato.getHorario() != null)
							linea += (dato.getHorario().toString());
						linea += (";");
						
						if (dato.getDiscapaci() != null)
							linea += (dato.getDiscapaci().toString());
						linea += (";");
						
						if (dato.getTipoDiscapaci() != null)
							linea += (dato.getTipoDiscapaci().toString());
						linea += (";");
						
						if (dato.getAnhioIngreso() != null)
							linea += (dato.getAnhioIngreso().toString());

						/*if (dato.getTipoPresupueso() != null)
							linea += (dato.getTipoPresupueso().toString());
						linea += (";");
						
						if (dato.getPrograma() != null)
							linea += (dato.getPrograma().toString());
						linea += (";");
						
						if (dato.getDescripCatego() != null)
							linea += (dato.getDescripCatego().toString());
						linea += (";");*/
						
						listaLineas.add(linea);
					}
				}
				FileUtils.writeLines(archSalida, listaLineas);
				JasperReportUtils.respondFile(archSalida, nArchivo, false, "application/vnd.ms-excel");
				em.flush();
			} else {
				if (mesIterado != null)
					statusMessages.add(Severity.INFO, "Archivo inválido");
				else
					statusMessages.add(Severity.ERROR, "No se pudo realizar la operación");
			}

		} catch (Exception e) {
			statusMessages.add(Severity.ERROR, "Error en la descarga del archivo");
		}
	}

	public static void enviarArchivoANavegador(String nombreArchivoSugerido, File archivoABajar) throws FileNotFoundException, IOException {
		FileInputStream archivo = new FileInputStream(archivoABajar);
		HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();

		response.setContentType("application/binary");
		response.setHeader("Content-Disposition", "attachment;filename=" + nombreArchivoSugerido);
		response.setContentLength(archivo.available());
		OutputStream salida = response.getOutputStream();
		byte buffer[] = new byte[1000000];
		long count = 0;
		long resto = 0;

		archivo.toString().getBytes();
		count = (long) archivo.available() / 1000000;

		if (((float) archivo.available() / 1000000) > count) {
			resto = archivo.available() % 1000000;
		}

		for (long bytes = 0; bytes < count; bytes++) {
			archivo.read(buffer);
			salida.write(buffer);
		}

		if (resto != 0) {
			archivo.read(buffer, 0, (int) resto);
			salida.write(buffer, 0, (int) resto);
		} else {
			archivo.read(buffer);
			salida.write(buffer);
		}

		archivo.close();
		salida.flush();
		salida.close();
		FacesContext.getCurrentInstance().responseComplete();
	}

	public void previewImport() throws IOException, ParseException {
		if (!precondInsert()) {
			return;
		}
		fileItem = seleccionUtilFormController.crearUploadItem(fname, uFile.length, cType, uFile);
		lLineasArch = FileUtils.readLines(fileItem.getFile());
		//System.out.println(lLineasArch.get(0));
		procesarLineas();
		agregarResumen();

	}

	private List<RemunConfig> traerDestinatatioriosMail() {
		Query q = em.createQuery("select RemunConfig from RemunConfig RemunConfig where RemunConfig.activo is true and "
						+ " RemunConfig.configuracionUoCab.idConfiguracionUo = :idUO ");
		// q.setParameter("idUO", nivelEntidadOeeUtil.getIdConfigCab());
		q.setParameter("idUO", 1000L);

		return q.getResultList();
	}

	public void enviarMail(Integer mesIterado) {
		Long idUser = usuarioLogueado.getIdUsuario();
		Query q = em.createQuery("select Usuario from Usuario Usuario "
				+ " where Usuario.idUsuario = :nro "
				+ " and Usuario.activo is true");
		q.setParameter("nro", idUser);
		Usuario userActual = (Usuario) q.getResultList().get(0);
		
		List<RemunConfig> lDestinatarios = traerDestinatatioriosMail();
		for (RemunConfig o : lDestinatarios) {
			VelocityEngine ve = new VelocityEngine();
			java.util.Properties p = new java.util.Properties();
			p.setProperty("resource.loader", "class");
			p.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
			ve.init(p);
			VelocityContext context = new VelocityContext();
			context.put("denoUnidad", nivelEntidadOeeUtil.getDenominacionUnidad());
			context.put("codUser", usuarioLogueado.getCodigoUsuario());
			context.put("nombreMes", nombreMes(mes));
			context.put("nombreUsuario", userActual.getPersona().getNombreCompleto());
			context.put("codigoNivel", nivelEntidadOeeUtil.getCodNivelEntidad().intValue());
			context.put("codigoEntidad", nivelEntidadOeeUtil.getCodSinEntidad().intValue());
			context.put("codigoOEE", nivelEntidadOeeUtil.getOrdenUnidOrg().intValue());
			Template t = new Template();
			if(cantFracaso > 0 || !remuneracionesTotales.equalsIgnoreCase(Long.toString(remuneracionesLeidas))){
				context.put("tabla", getTablaMailFracaso(mesIterado, nivelEntidadOeeUtil.getIdConfigCab()));
				t = ve.getTemplate("/templates/email680Fracaso.vm");
			}else{
				context.put("tabla", genTablaMail(mesIterado, nivelEntidadOeeUtil.getIdConfigCab()));
				//SE VERIFICA QUE EXISTAN ALERTAS (VARIACION EXCESIVA(50%) ENTRE PRESUPUESTADO/DEVENGADO ó MENORES DE EDAD) PARA CREAR LA TABLA ALERTAS MP - 17/05/2016
				String rem =getVarRem(insertarEnHistoricoDirecto);
				String edad = getCantMenores(insertarEnHistoricoDirecto);
				if(!rem.equalsIgnoreCase("0") || !edad.equalsIgnoreCase("0")){
					context.put("alerta", getCuadroMailAlertas(rem,edad));
				}
				else{
				context.put("alerta", "");
				}
				context.put("cuadro", getCuadroMail());
				t = ve.getTemplate("/templates/email680.vm");
			}
			StringWriter writer = new StringWriter();
			t.merge(context, writer);
			writer.toString();
			seleccionUtilFormController
					.enviarMails(
							o.getEMail(),
							writer.toString(), " SICCA - Nominas y Movimientos de Funcionarios: " + nivelEntidadOeeUtil.getDenominacionUnidad(), null);
		}
	}
	
	public void enviarMailUsuario(Integer mesIterado) {
		
		//String emailUsuarioLogueado = usuarioLogueado.getPersona().getEMail();
		Long idUser = usuarioLogueado.getIdUsuario();
		Query q = em.createQuery("select Usuario from Usuario Usuario "
				+ " where Usuario.idUsuario = :nro "
				+ " and Usuario.activo is true");
		q.setParameter("nro", idUser);
		Usuario userActual = (Usuario) q.getResultList().get(0);
		//ZD 19/10/15 String emailUsuarioLogueado = userActual.getPersona().getEMail();
		String emailUsuarioLogueado = userActual.getPersona().getEMailFuncionario();
		
		VelocityEngine ve = new VelocityEngine();
		java.util.Properties p = new java.util.Properties();
		p.setProperty("resource.loader", "class");
		p.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
		ve.init(p);
		VelocityContext context = new VelocityContext();
		context.put("denoUnidad", nivelEntidadOeeUtil.getDenominacionUnidad());
		context.put("codUser", usuarioLogueado.getCodigoUsuario());
		context.put("nombreMes", nombreMes(mes));
		context.put("nombreUsuario", userActual.getPersona().getNombreCompleto());
		context.put("codigoNivel", nivelEntidadOeeUtil.getCodNivelEntidad().intValue());
		context.put("codigoEntidad", nivelEntidadOeeUtil.getCodSinEntidad().intValue());
		context.put("codigoOEE", nivelEntidadOeeUtil.getOrdenUnidOrg().intValue());
		Template t = new Template();
		if(cantFracaso > 0 || !remuneracionesTotales.equalsIgnoreCase(Long.toString(remuneracionesLeidas))){
			context.put("tabla", getTablaMailFracaso(mesIterado, nivelEntidadOeeUtil.getIdConfigCab()));
			t = ve.getTemplate("/templates/email680Fracaso.vm");
		}else{
			context.put("tabla", genTablaMail(mesIterado, nivelEntidadOeeUtil.getIdConfigCab()));
			//SE VERIFICA QUE EXISTAN ALERTAS (VARIACION EXCESIVA(50%) ENTRE PRESUPUESTADO/DEVENGADO ó MENORES DE EDAD) PARA CREAR LA TABLA ALERTAS MP - 17/05/2016
			String rem =getVarRem(insertarEnHistoricoDirecto);
			String edad = getCantMenores(insertarEnHistoricoDirecto);
			if(!rem.equalsIgnoreCase("0") || !edad.equalsIgnoreCase("0")){
				context.put("alerta", getCuadroMailAlertas(rem,edad));
			}
			else{
			context.put("alerta", "");
			}
			context.put("cuadro", getCuadroMail());
			t = ve.getTemplate("/templates/email680.vm");
		}
		StringWriter writer = new StringWriter();
		t.merge(context, writer);
		writer.toString();
		seleccionUtilFormController.enviarMails(emailUsuarioLogueado, writer.toString(), " SICCA - Nominas y Movimientos de Funcionarios: " + nivelEntidadOeeUtil.getDenominacionUnidad(), null);
	}

	private String genTablaMail(Integer mes, Long idUO) {
		/*List<DTO680StatsTmp> lista = traerResumenImportacion(mes, idUO);
		if (lista.size() == 0) {
			return "";
		}
		String respuesta = "<table  border='1'>";
		respuesta += "<tr><th>Estado</th><th>Motivo</th><th>Cantidad</th></tr>";
		for (DTO680StatsTmp o : lista) {
			respuesta += "<tr>";
			respuesta += "<td>" + o.getEstadoImport() + "</td>";
			respuesta += "<td>" + o.getMotivo() + "</td>";
			//respuesta += "<td>" + o.getCantProcesada() + "</td>";
			respuesta += "<td>" + getRemuneracionesProcesadas() + "</td>";
			respuesta += "</tr>";
		}
		respuesta += "</table>";*/
		
		String respuesta = "<table  border='1' style ='background-color: #04D61F'>";
		respuesta += "<tr><th>Estado</th><th>Cantidad de Registros Insertados</th></tr>";
		
		respuesta += "<tr>";
		respuesta += "<td>" + "EXITOSO" + "</td>";
		respuesta += "<td>" + getRemuneracionesProcesadas(insertarEnHistoricoDirecto) + "</td>";
		respuesta += "</tr>";
		
		respuesta += "</table>";
		
		return respuesta;
	}
	private String getTablaMailFracaso(Integer mes, Long idUO) {
		String respuesta = "<table  border='1' style ='background-color: #FF0000'>";
		respuesta += "<tr><th>Estado</th><th>Cantidad de Registros</th></tr>";
		
		respuesta += "<tr>";
		respuesta += "<td>" + "FRACASO" + "</td>";
		respuesta += "<td>" + cantFracaso+ "</td>";
		respuesta += "</tr>";
		
		respuesta += "</table>";
		
		return respuesta;
	}
	//SE CREA LA TABLA ALERTAS (VARIACION EXCESIVA(50%) ENTRE PRESUPUESTADO/DEVENGADO ó MENORES DE EDAD) MP - 17/05/2016
	private String getCuadroMailAlertas(String rem, String edad) {
		String respuesta = "<br><br><table  border='1' style ='background-color: yellow'>";
		respuesta += "<tr><th colspan=\"2\">ALERTAS<br>Favor verificar los siguientes puntos,<br> si los registros est&aacute;n correctos omitir est&aacute; alerta</th></tr>";
		if(!rem.equalsIgnoreCase("0")){
			respuesta += "<tr>";
			respuesta += "<td>" + "Cantidad de Registros con monto Devengado<br> inferior al 50% del Presupuestado" + "</td>";
			respuesta += "<td>" + getVarRem(insertarEnHistoricoDirecto) + "</td>";
			respuesta += "</tr>";
		}
		
		if(!edad.equalsIgnoreCase("0")){
		respuesta += "<tr>";
		respuesta += "<td>" + "Cantidad de Personas menores de Edad " + "</td>";
		respuesta += "<td>" + getCantMenores(insertarEnHistoricoDirecto) + "</td>";
		respuesta += "</tr>";
		}
		
		respuesta += "</table>";
		
		return respuesta;
	}
	
	private String getCuadroMail() {
	
		String respuesta = "<table  border='1'>";
		respuesta += "<tr><th colspan=\"2\">RESUMEN DE DATOS REPORTADOS</th></tr>";
		
		respuesta += "<tr>";
		respuesta += "<td>" + "Cantidad de Funcionarios Permanentes" + "</td>";
		respuesta += "<td>" + getCantFuncPermanentes(insertarEnHistoricoDirecto) + "</td>";
		respuesta += "</tr>";
		
		respuesta += "<tr>";
		respuesta += "<td>" + "Cantidad de Funcionarios Contratados" + "</td>";
		respuesta += "<td>" + getCantFuncContratados(insertarEnHistoricoDirecto) + "</td>";
		respuesta += "</tr>";
		
		respuesta += "<tr>";
		respuesta += "<td>" + "Cantidad de Funcionarios PcD (Con Discapacidad)" + "</td>";
		respuesta += "<td>" + getCantPCD(insertarEnHistoricoDirecto) + "</td>";
		respuesta += "</tr>";
		
		respuesta += "<tr>";
		respuesta += "<td>" + "Cantidad de Altas de Funcionarios (A)" + "</td>";
		respuesta += "<td>" + getCantAltas(insertarEnHistoricoDirecto) + "</td>";
		respuesta += "</tr>";
		
		respuesta += "<tr>";
		respuesta += "<td>" + "Cantidad de Bajas de Funcionarios (B)" + "</td>";
		respuesta += "<td>" + getCantBajas(insertarEnHistoricoDirecto) + "</td>";
		respuesta += "</tr>";
		
		respuesta += "<tr>";
		respuesta += "<td>" + "Cantidad de Traslados Temporales de Funcionarios (TT)" + "</td>";
		respuesta += "<td>" + getCantTT(insertarEnHistoricoDirecto) + "</td>";
		respuesta += "</tr>";
		
		respuesta += "<tr>";
		respuesta += "<td>" + "Cantidad de Rubros Vacantes" + "</td>";
		respuesta += "<td>" + getCantVacantes(insertarEnHistoricoDirecto) + "</td>";
		respuesta += "</tr>";
		respuesta += "</table>";
		
		return respuesta;
	}
	private Integer getRemuneracionesProcesadas(Boolean buscarEnHistorico) {
		
		if(buscarEnHistorico){
			String sqlCantHistRemuneraciones = "select * from remuneracion.historico_remuneraciones_tmp where mes = "+mes+" and anho = "+anho+
					" and nivel = "+nivelEntidadOeeUtil.getCodNivelEntidad().intValue()+" and entidad = "+nivelEntidadOeeUtil.getCodSinEntidad().intValue()+
					" and oee = "+nivelEntidadOeeUtil.getOrdenUnidOrg().intValue();

			Query qCant = em.createNativeQuery(sqlCantHistRemuneraciones, HistoricoRemuneracionesTmp.class);
			return (Integer) qCant.getResultList().size();
		}
		else{
			String sqlCantRemuneraciones = "select * from remuneracion.remuneraciones_tmp where mes = "+mes+" and anho = "+anho+
				" and nivel = "+nivelEntidadOeeUtil.getCodNivelEntidad().intValue()+" and entidad = "+nivelEntidadOeeUtil.getCodSinEntidad().intValue()+
				" and oee = "+nivelEntidadOeeUtil.getOrdenUnidOrg().intValue();

			Query qCant = em.createNativeQuery(sqlCantRemuneraciones, RemuneracionesTmp.class);
			return (Integer) qCant.getResultList().size();
		}
	}

	private String getCantFuncPermanentes(Boolean buscarEnHistorico) {
		String sql = "SELECT count(*) FROM( " +
					     " SELECT DISTINCT id_persona ";
						 if(buscarEnHistorico)
							 sql = sql + "FROM remuneracion.historico_remuneraciones_tmp ";
						 else
							 sql = sql + "FROM remuneracion.remuneraciones_tmp ";
						sql = sql + " WHERE nivel = "+nivelEntidadOeeUtil.getCodNivelEntidad().intValue()+
								" AND entidad = "+nivelEntidadOeeUtil.getCodSinEntidad().intValue()+
								" AND oee = " +nivelEntidadOeeUtil.getOrdenUnidOrg().intValue()+
								" AND mes = "+mes+" AND anho = "+anho+
								" AND obj_codigo = 111 AND documento_identidad <> '0' " +
								" ) as permanentes";
		Query q = em
				.createNativeQuery(sql);
		Object cant = (Object) q.getSingleResult();
		return ""+cant;
	}
	private String getCantFuncContratados(Boolean buscarEnHistorico) {
		String sql = "SELECT count(*) FROM( " +
					     " SELECT DISTINCT id_persona ";
						 if(buscarEnHistorico)
							 sql = sql + "FROM remuneracion.historico_remuneraciones_tmp ";
						 else
							 sql = sql + "FROM remuneracion.remuneraciones_tmp ";
						sql = sql + " WHERE nivel = "+nivelEntidadOeeUtil.getCodNivelEntidad().intValue()+
								" AND entidad = "+nivelEntidadOeeUtil.getCodSinEntidad().intValue()+
								" AND oee = " +nivelEntidadOeeUtil.getOrdenUnidOrg().intValue()+
								" AND mes = "+mes+" AND anho = "+anho+
								" AND (obj_codigo BETWEEN 140 AND 149) AND documento_identidad <> '0' " +
								" ) as contratados";
		Query q = em
				.createNativeQuery(sql);
		Object cant = (Object) q.getSingleResult();
		return ""+cant;
	}
	
	private String getCantAltas(Boolean buscarEnHistorico) {
		String sql = "SELECT count(distinct documento_identidad) ";
						 if(buscarEnHistorico)
							 sql = sql + "FROM remuneracion.historico_remuneraciones_tmp ";
						 else
							 sql = sql + "FROM remuneracion.remuneraciones_tmp ";
						sql = sql + " WHERE nivel = "+nivelEntidadOeeUtil.getCodNivelEntidad().intValue()+
								" AND entidad = "+nivelEntidadOeeUtil.getCodSinEntidad().intValue()+
								" AND oee = " +nivelEntidadOeeUtil.getOrdenUnidOrg().intValue()+
								" AND mes = "+mes+" AND anho = "+anho+
								" AND movimiento LIKE 'A' ";
		Query q = em
				.createNativeQuery(sql);
		Object cant = (Object) q.getSingleResult();
		return ""+cant;
	}
	private String getCantBajas(Boolean buscarEnHistorico) {
		String sql = "SELECT count(distinct documento_identidad) ";
						 if(buscarEnHistorico)
							 sql = sql + "FROM remuneracion.historico_remuneraciones_tmp "
							 		+ " ";
						 else
							 sql = sql + "FROM remuneracion.remuneraciones_tmp ";
						sql = sql + " WHERE nivel = "+nivelEntidadOeeUtil.getCodNivelEntidad().intValue()+
								" AND entidad = "+nivelEntidadOeeUtil.getCodSinEntidad().intValue()+
								" AND oee = " +nivelEntidadOeeUtil.getOrdenUnidOrg().intValue()+
								" AND mes = "+mes+" AND anho = "+anho+
								" AND movimiento LIKE 'B' ";
		Query q = em
				.createNativeQuery(sql);
		Object cant = (Object) q.getSingleResult();
		return ""+cant;
	}
	
	private String getCantTT(Boolean buscarEnHistorico) {
		String sql = "SELECT count(distinct documento_identidad) ";
						 if(buscarEnHistorico)
							 sql = sql + "FROM remuneracion.historico_remuneraciones_tmp ";
						 else
							 sql = sql + "FROM remuneracion.remuneraciones_tmp ";
						sql = sql + " WHERE nivel = "+nivelEntidadOeeUtil.getCodNivelEntidad().intValue()+
								" AND entidad = "+nivelEntidadOeeUtil.getCodSinEntidad().intValue()+
								" AND oee = " +nivelEntidadOeeUtil.getOrdenUnidOrg().intValue()+
								" AND mes = "+mes+" AND anho = "+anho+
								" AND movimiento LIKE 'TT' ";
		Query q = em
				.createNativeQuery(sql);
		Object cant = (Object) q.getSingleResult();
		return ""+cant;
	}
	private String getCantPCD(Boolean buscarEnHistorico) {
		String sql = "SELECT count(distinct documento_identidad) ";
						 if(buscarEnHistorico)
							 sql = sql + "FROM remuneracion.historico_remuneraciones_tmp ";
						 else
							 sql = sql + "FROM remuneracion.remuneraciones_tmp ";
						sql = sql + " WHERE nivel = "+nivelEntidadOeeUtil.getCodNivelEntidad().intValue()+
								" AND entidad = "+nivelEntidadOeeUtil.getCodSinEntidad().intValue()+
								" AND oee = " +nivelEntidadOeeUtil.getOrdenUnidOrg().intValue()+
								" AND mes = "+mes+" AND anho = "+anho+
								" AND discapacidad IS TRUE ";
		Query q = em
				.createNativeQuery(sql);
		Object cant = (Object) q.getSingleResult();
		return ""+cant;
	}
	private String getCantVacantes(Boolean buscarEnHistorico) {
		String sql = "SELECT count(*) ";
						 if(buscarEnHistorico)
							 sql = sql + "FROM remuneracion.historico_remuneraciones_tmp ";
						 else
							 sql = sql + "FROM remuneracion.remuneraciones_tmp ";
						sql = sql + " WHERE nivel = "+nivelEntidadOeeUtil.getCodNivelEntidad().intValue()+
								" AND entidad = "+nivelEntidadOeeUtil.getCodSinEntidad().intValue()+
								" AND oee = " +nivelEntidadOeeUtil.getOrdenUnidOrg().intValue()+
								" AND mes = "+mes+" AND anho = "+anho+
								" AND documento_identidad LIKE '0' ";
		Query q = em
				.createNativeQuery(sql);
		Object cant = (Object) q.getSingleResult();
		return ""+cant;
	}
	//METODO QUE VERIFICA SI EXISTEN REGISTROS CON UNA VARIACION MAYOR DEL 50% DEL PRESUPUESTADO CON RELACION AL DEVENGADO  MP - 17/05/2016
	private String getVarRem(Boolean buscarEnHistorico) {
		String sql = "SELECT count(*) FROM (SELECT * ";
						 if(buscarEnHistorico)
							 sql = sql + "FROM remuneracion.historico_remuneraciones_tmp ";
						 else
							 sql = sql + "FROM remuneracion.remuneraciones_tmp ";
						sql = sql + " WHERE nivel = "+nivelEntidadOeeUtil.getCodNivelEntidad().intValue()+
								" AND entidad = "+nivelEntidadOeeUtil.getCodSinEntidad().intValue()+
								" AND oee = " +nivelEntidadOeeUtil.getOrdenUnidOrg().intValue()+
								" AND mes = "+mes+" AND anho = "+anho+
								" AND devengado > 0) as count WHERE presupuestado/devengado >= 2 ";
		Query q = em
				.createNativeQuery(sql);
		Object cant = (Object) q.getSingleResult();
		return ""+cant;
	}
	//METODO QUE VERIFICA SI EXISTEN MENORES DE EDAD EN EL MES Y AÑO PROCESADO  MP - 17/05/2016
	private String getCantMenores(Boolean buscarEnHistorico) {
		String sql = "SELECT count(distinct id_persona) FROM (SELECT trunc(((DATE_PART('YEAR',now())-DATE_PART('YEAR',p.fecha_nacimiento))* 372 + "
				+ "(DATE_PART('MONTH',now()) - DATE_PART('MONTH',p.fecha_nacimiento))*31 +"
				+ " (DATE_PART('DAY',now())-DATE_PART('DAY',p.fecha_nacimiento)))/372) AS edad,tmp1.id_persona,nivel,entidad,oee,mes,anho FROM";
						 if(buscarEnHistorico)
							 sql = sql + "(SELECT id_persona,id_configuracion_uo,nivel,entidad,oee,mes,anho FROM remuneracion.historico_remuneraciones_tmp) AS tmp1 ";
						 else
							 sql = sql + " (SELECT id_persona,id_configuracion_uo,nivel,entidad,oee,mes,anho FROM remuneracion.remuneraciones_tmp) AS tmp1 ";
						 sql = sql + " INNER JOIN general.persona p ON p.id_persona = tmp1.id_persona) edades "
								+ "WHERE nivel = "+nivelEntidadOeeUtil.getCodNivelEntidad().intValue()+
								" AND entidad = "+nivelEntidadOeeUtil.getCodSinEntidad().intValue()+
								" AND oee = " +nivelEntidadOeeUtil.getOrdenUnidOrg().intValue()+
								" AND mes = "+mes+" AND anho = "+anho+
								" AND edad < 18 ";
		Query q = em
				.createNativeQuery(sql);
		Object cant = (Object) q.getSingleResult();
		return ""+cant;
	}

	private List<RemuneracionesTmp> cargarRemuneracionesTmp(String doc, int nivel, int entidad, int oee) {
		if (!doc.equalsIgnoreCase("0")){
			Query q = em.createQuery("select RemuneracionesTmp from RemuneracionesTmp RemuneracionesTmp "
							+ " where RemuneracionesTmp.documentoIdentidad = :doc "
							+ " and RemuneracionesTmp.activo is true");
			q.setParameter("doc", doc);
			return q.getResultList();
		}
		else{
			Query q = em.createQuery("select RemuneracionesTmp from RemuneracionesTmp RemuneracionesTmp "
					+ " where RemuneracionesTmp.documentoIdentidad = :doc "
					+ " and RemuneracionesTmp.nivel = :nivel"
					+ " and RemuneracionesTmp.entidad = :entidad"
					+ " and RemuneracionesTmp.oee = :oee"
					+ " and RemuneracionesTmp.activo is true");
			q.setParameter("doc", doc);
			q.setParameter("nivel", nivel);
			q.setParameter("entidad", entidad);
			q.setParameter("oee", oee);
			return q.getResultList();
		}
	}

	private void cargarRemuneracionesAnterioresDIRECTO() {
		//COPIA DE REMUNERACIONES_TMP A HISTORICO_REMUNERACIONES_TMP DE LOS REGISTROS (AHORA) VIEJOS
		try {
			Query q = em.createNativeQuery("INSERT INTO remuneracion.historico_remuneraciones_tmp (anho,mes,nivel,entidad,oee,linea,documento_identidad,nombres,"+
						"apellidos,estado,remuneracion_total,obj_codigo,fuente_financiamiento,categoria,presupuestado,devengado,concepto,movimiento,lugar,cargo,"+
						"funcion_cumplida,carga_horaria,discapacidad,tipo_discapacidad,anho_ingreso,activo,usu_alta,fecha_alta,id_configuracion_uo,id_persona,"+
						"usu_paso_historico,fecha_paso_historico,usu_mod,fecha_mod)"+
						" SELECT anho,mes,nivel,entidad,oee,linea,documento_identidad,nombres,apellidos,estado,remuneracion_total,obj_codigo,fuente_financiamiento,"+
						"categoria,presupuestado,devengado,concepto,movimiento,lugar,cargo,funcion_cumplida,carga_horaria,discapacidad,tipo_discapacidad,anho_ingreso,"+
						"activo,usu_alta,fecha_alta,id_configuracion_uo,id_persona,'"+usuarioLogueado.getCodigoUsuario()+"',(SELECT TIMESTAMP WITHOUT TIME ZONE 'now'),"+
						"usu_mod,fecha_mod from remuneracion.remuneraciones_tmp WHERE ( mes < "+mes+" or anho <= "+anho+" ) "+
						" and nivel = "+nivelEntidadOeeUtil.getCodNivelEntidad().intValue()+" and entidad = "+nivelEntidadOeeUtil.getCodSinEntidad().intValue()+
						" and oee = "+nivelEntidadOeeUtil.getOrdenUnidOrg().intValue()+"; COMMIT;");
			q.executeUpdate();
		} catch (Exception e) {
			statusMessages.add(Severity.ERROR,"ERROR AL CARGAR REMUNERACIONES ANTERIORES");
			System.out.println("ERROR AL CARGAR REMUNERACIONES ANTERIORES");
			e.printStackTrace();
		}
		//BORRADO DE DICHOS REGISTROS (AHORA) VIEJOS DE REMUNERACIONES_TMP
		try {
			Query q = em.createNativeQuery("DELETE FROM remuneracion.remuneraciones_tmp WHERE "+
							" ( mes < "+mes+" or anho <= "+anho+" ) and nivel = "+ nivelEntidadOeeUtil.getCodNivelEntidad().intValue()+
							" and entidad = "+nivelEntidadOeeUtil.getCodSinEntidad().intValue()+
							" and oee = "+nivelEntidadOeeUtil.getOrdenUnidOrg().intValue()+"; COMMIT;");
			q.executeUpdate();
			
		} catch (Exception e) {
			statusMessages.add(Severity.ERROR,"Error al borrar registros anteriores en Remuneraciones");
			System.out.println("Error al borrar registros anteriores en Remuneraciones");
			e.printStackTrace();
		}
	}
	
	private void cargarRemuneracionesAnteriores() {
		
		String sqlRemuneracionesViejas = "select * from remuneracion.remuneraciones_tmp where mes < "+mes+" and anho <= "+anho+
				" and nivel = "+nivelEntidadOeeUtil.getCodNivelEntidad().intValue()+" and entidad = "+nivelEntidadOeeUtil.getCodSinEntidad().intValue()+
				" and oee = "+nivelEntidadOeeUtil.getOrdenUnidOrg().intValue();

		Query qRemuneracionesViejas = em.createNativeQuery(sqlRemuneracionesViejas, RemuneracionesTmp.class);
		
		List<RemuneracionesTmp> lRemuneracionesTmp = qRemuneracionesViejas.getResultList();
		
		int j = 1;
		
		if(!lRemuneracionesTmp.isEmpty()){
			Iterator<RemuneracionesTmp> iter = lRemuneracionesTmp.iterator();
			while (iter.hasNext()) {
				RemuneracionesTmp remuneracionAnterior = iter.next();
				HistoricoRemuneracionesTmp hr = new HistoricoRemuneracionesTmp();
			
				hr.setActivo(true);
				hr.setFechaAlta(remuneracionAnterior.getFechaAlta());
				hr.setUsuAlta(remuneracionAnterior.getUsuAlta());
				hr.setAnho(remuneracionAnterior.getAnho());
				hr.setMes(remuneracionAnterior.getMes());
				hr.setNivel(remuneracionAnterior.getNivel());
				hr.setEntidad(remuneracionAnterior.getEntidad());
				hr.setOee(remuneracionAnterior.getOee());
				hr.setLinea(remuneracionAnterior.getLinea());
				hr.setDocumentoIdentidad(remuneracionAnterior.getDocumentoIdentidad());
				hr.setNombres(remuneracionAnterior.getNombres());
				hr.setApellidos(remuneracionAnterior.getApellidos());
				hr.setEstado(remuneracionAnterior.getEstado());
				hr.setRemuneracionTotal(remuneracionAnterior.getRemuneracionTotal());
				hr.setObjCodigo(remuneracionAnterior.getObjCodigo());
				hr.setFuenteFinanciamiento(remuneracionAnterior.getFuenteFinanciamiento());
				hr.setCategoria(remuneracionAnterior.getCategoria());
				hr.setPresupuestado(remuneracionAnterior.getPresupuestado());
				hr.setDevengado(remuneracionAnterior.getDevengado());
				hr.setConcepto(remuneracionAnterior.getConcepto());
				hr.setMovimiento(remuneracionAnterior.getMovimiento());
				hr.setLugar(remuneracionAnterior.getLugar());
				hr.setCargo(remuneracionAnterior.getCargo());
				hr.setFuncionCumplida(remuneracionAnterior.getFuncionCumplida());
				hr.setCargaHoraria(remuneracionAnterior.getCargaHoraria());
				hr.setDiscapacidad(remuneracionAnterior.getDiscapacidad());
				hr.setTipoDiscapacidad(remuneracionAnterior.getTipoDiscapacidad());
				hr.setAnhoIngreso(remuneracionAnterior.getAnhoIngreso());
				hr.setConfiguracionUoCab(remuneracionAnterior.getConfiguracionUoCab());
				hr.setPersona(remuneracionAnterior.getPersona());
			
				em.persist(hr);
				em.remove(remuneracionAnterior);
				em.flush();
				
				if(j%1000 == 0){
					System.out.println("Copiada linea "+j);
				}
				j++;
			}
		}
		System.out.println("FIN COPIA");
	}

	void corePaso4(DTO680Tmp dto) {
		
		//List<RemuneracionesTmp> lRemuneracionesTmp = cargarRemuneracionesTmp(dto.getCedula(), dto.getNivelEnti(), dto.getEntidad(), dto.getOee());
		RemuneracionesTmp remu = new RemuneracionesTmp();
		remu.setActivo(true);
		remu.setFechaAlta(new Date());
		remu.setUsuAlta(usuarioLogueado.getCodigoUsuario());
		
		remu.setAnho(dto.getAnhio());
		remu.setMes(dto.getMes());
		remu.setNivel(nivelEntidadOeeUtil.getCodNivelEntidad().intValue());
		remu.setEntidad(nivelEntidadOeeUtil.getCodSinEntidad().intValue());
		remu.setOee(nivelEntidadOeeUtil.getOrdenUnidOrg());
		remu.setLinea(dto.getLinea());
		remu.setDocumentoIdentidad(dto.getCedula());
		remu.setNombres(dto.getNombres());
		remu.setApellidos(dto.getApellidos());
		remu.setEstado(dto.getEstado());
		remu.setRemuneracionTotal(dto.getRemuneracionTot());
		remu.setObjCodigo(dto.getObjetoGto());
		remu.setFuenteFinanciamiento(dto.getFuenteFinancia());
		remu.setCategoria(dto.getCateg());
		remu.setPresupuestado(dto.getPresup());
		remu.setDevengado(dto.getDeven());
		remu.setConcepto(dto.getConcep());
		remu.setMovimiento(dto.getMovimiento());
		remu.setLugar(dto.getLugar());
		remu.setCargo(dto.getCargo());
		remu.setFuncionCumplida(dto.getFuncion());
		remu.setCargaHoraria(dto.getHorario());
		if(!dto.getNombres().equalsIgnoreCase("VACANTE")){
			if(dto.getDiscapaci() == null)
				remu.setDiscapacidad(false);
			else{
				if((dto.getDiscapaci().equals("S")) || (dto.getDiscapaci().equals("SI"))){
					remu.setDiscapacidad(true);
					remu.setTipoDiscapacidad(dto.getTipoDiscapaci());
				}
				if((dto.getDiscapaci().equals("N")) || (dto.getDiscapaci().equals("NO")))
					remu.setDiscapacidad(false);
			}
		}
		remu.setAnhoIngreso(dto.getAnhioIngreso());
		remu.setConfiguracionUoCab(dto.getConfiguracionUoCab());
		remu.setPersona(dto.getPersona());
		
		em.persist(remu);
		//em.flush();

	}
	
	void corePaso4Historico(DTO680Tmp dto) {
		
		HistoricoRemuneracionesTmp histoRemu = new HistoricoRemuneracionesTmp();
		histoRemu.setActivo(true);
		histoRemu.setFechaAlta(new Date());
		histoRemu.setUsuAlta(usuarioLogueado.getCodigoUsuario());
		
		histoRemu.setAnho(dto.getAnhio());
		histoRemu.setMes(dto.getMes());
		histoRemu.setNivel(nivelEntidadOeeUtil.getCodNivelEntidad().intValue());
		histoRemu.setEntidad(nivelEntidadOeeUtil.getCodSinEntidad().intValue());
		histoRemu.setOee(nivelEntidadOeeUtil.getOrdenUnidOrg());
		histoRemu.setLinea(dto.getLinea());
		histoRemu.setDocumentoIdentidad(dto.getCedula());
		histoRemu.setNombres(dto.getNombres());
		histoRemu.setApellidos(dto.getApellidos());
		histoRemu.setEstado(dto.getEstado());
		histoRemu.setRemuneracionTotal(dto.getRemuneracionTot());
		histoRemu.setObjCodigo(dto.getObjetoGto());
		histoRemu.setFuenteFinanciamiento(dto.getFuenteFinancia());
		histoRemu.setCategoria(dto.getCateg());
		histoRemu.setPresupuestado(dto.getPresup());
		histoRemu.setDevengado(dto.getDeven());
		histoRemu.setConcepto(dto.getConcep());
		histoRemu.setMovimiento(dto.getMovimiento());
		histoRemu.setLugar(dto.getLugar());
		histoRemu.setCargo(dto.getCargo());
		histoRemu.setFuncionCumplida(dto.getFuncion());
		histoRemu.setCargaHoraria(dto.getHorario());
		if(!dto.getNombres().equalsIgnoreCase("VACANTE")){
			if(dto.getDiscapaci() == null)
				histoRemu.setDiscapacidad(false);
			else{
				if((dto.getDiscapaci().equals("S")) || (dto.getDiscapaci().equals("SI"))){
					histoRemu.setDiscapacidad(true);
					histoRemu.setTipoDiscapacidad(dto.getTipoDiscapaci());
				}
				if((dto.getDiscapaci().equals("N")) || (dto.getDiscapaci().equals("NO")))
					histoRemu.setDiscapacidad(false);
			}
		}
		histoRemu.setAnhoIngreso(dto.getAnhioIngreso());
		histoRemu.setConfiguracionUoCab(dto.getConfiguracionUoCab());
		histoRemu.setPersona(dto.getPersona());
		
		em.persist(histoRemu);
		//em.flush();

	}

	public void paso4(DTO680Tmp dto, int index, String origen) throws ParseException {
		corePaso4(dto);
		/* iii */
		String motivo = null;
		/*if (dto.getMovimiento() != null && dto.getMovimiento().equalsIgnoreCase("B")) {
			motivo = "Corresponde a Baja, registre desvinculación de ser necesario";
		}*/
		registrarImportacionRealizada(dto, "EXITOSO", motivo, origen, null);
		agregarEstadoMotivo("EXITOSO", motivo, index);

	}

	public RemuneracionesTmp existeEnRemuneraciones(DTO680 dto) {
		Query q = em.createQuery("select RemuneracionesTmp from RemuneracionesTmp RemuneracionesTmp "
						+ " where RemuneracionesTmp.anho = :anho "
						+ " and RemuneracionesTmp.mes  = :mes"
						+ " and RemuneracionesTmp.categoria = :idCatego "
						+ " and RemuneracionesTmp.presupuestado = :presupuestado "
						+ " and RemuneracionesTmp.documentoIdentidad = :cedula "
						+ " and RemuneracionesTmp.objCodigo = :objCodigo ");
		q.setParameter("anho", dto.getAnhio());
		q.setParameter("mes", dto.getMes());
		q.setParameter("idCatego", dto.getCateg());
		q.setParameter("presupuestado", dto.getPresup());
		q.setParameter("cedula", dto.getCedula());
		q.setParameter("objCodigo", dto.getObjetoGto());
		List<RemuneracionesTmp> lista = q.getResultList();
		if (lista.size() > 0)
			return lista.get(0);
		return null;
	}

	public EmpleadoPuesto existeEnEmpleadoPuesto(Long idPersona, Long idUo) {
		Query q = em.createQuery("select EmpleadoPuesto from EmpleadoPuesto EmpleadoPuesto "
						+ " where EmpleadoPuesto.persona.idPersona = :idPersona"
						+ "  and EmpleadoPuesto.plantaCargoDet.configuracionUoDet.configuracionUoCab.idConfiguracionUo = :idUo "
						+ " and EmpleadoPuesto.activo is true");
		q.setParameter("idPersona", idPersona);
		q.setParameter("idUo", idUo);
		List<EmpleadoPuesto> lista = q.getResultList();
		if (lista.size() > 0)
			return lista.get(0);

		return null;
	}

	private void registrarImportacionRealizada(DTO680Tmp dto, String estadoImport, String motivo, String origen, Long idEmpleadoPuesto) throws ParseException {
		Importacion importacion = new Importacion();
		
		importacion.setAnho(dto.getAnhio());
		importacion.setMes(dto.getMes());
		importacion.setNivel(dto.getNivelEnti());
		importacion.setEntidad(dto.getEntidad());
		importacion.setOee(dto.getOee());
		importacion.setLinea(dto.getLinea());
		importacion.setDocumentoIdentidad(dto.getCedula());
		importacion.setNombres(dto.getNombres());
		importacion.setApellidos(dto.getApellidos());
		importacion.setEstado(dto.getEstado());
		importacion.setRemuneracionTotal(dto.getRemuneracionTot());
		importacion.setObjetoGto(dto.getObjetoGto());
		importacion.setFuenteFinanciamiento(dto.getFuenteFinancia());
		importacion.setCategoria(dto.getCateg());
		importacion.setPresupuestado(dto.getPresup());
		importacion.setDevengado(dto.getDeven());
		importacion.setMovimiento(dto.getMovimiento());
		importacion.setLugar(dto.getLugar());
		importacion.setCargo(dto.getCargo());
		importacion.setFuncionCumplida(dto.getFuncion());
		importacion.setCargaHoraria(dto.getHorario());
		if(!dto.getNombres().equalsIgnoreCase("VACANTE")){
			if(dto.getDiscapaci() == null)
				importacion.setDiscapacidad("N");
			else{
				if((dto.getDiscapaci().equals("S")) || (dto.getDiscapaci().equals("SI"))){
					importacion.setDiscapacidad("S");
					importacion.setTipoDiscapacidad(dto.getTipoDiscapaci());
				}
				if((dto.getDiscapaci().equals("N")) || (dto.getDiscapaci().equals("NO")))
					importacion.setDiscapacidad("N");
			}
		}
		importacion.setTipoDiscapacidad(dto.getTipoDiscapaci());
		importacion.setAnhoIngreso(dto.getAnhioIngreso());
		importacion.setConcepto(dto.getConcep());
		
		/*if (dto.getFecha() != null)
			importacion.setFecha(sdf.parse(dto.getFecha()));
		importacion.setDescripCategoria(dto.getDescripCatego());*/

		importacion.setEstadoImport(estadoImport);
		importacion.setMotivo(motivo);
		importacion.setUsuAlta(usuarioLogueado.getCodigoUsuario());
		importacion.setFechaAlta(new Date());
		importacion.setOrigen(origen);

		importacion.setConfiguracionUoCab(new ConfiguracionUoCab());
		importacion.getConfiguracionUoCab().setIdConfiguracionUo(nivelEntidadOeeUtil.getIdConfigCab());

		em.persist(importacion);
	}

	private void agregarEstadoMotivo(String estado, String motivo, int index) {
		if (motivo == null || motivo.trim().equals("null")) {
			motivo = "";
		}
		String cSeparador = ";";
		String linea = lLineasArch.get(index);
		lLineasArch.set(index, linea + cSeparador + estado + cSeparador + motivo);
	}

	private void agregarResumen() {
		String cSeparador = ";";
		lLineasArch.add("Cantidad de Registros Correctos: " + cantExito  + cSeparador);
		lLineasArch.add("Cantidad de Registros con Fracaso: " + cantFracaso  + cSeparador);
		//lLineasArch.add("Cantidad de Registros Duplicados: " + cSeparador + cantDuplicado);
		if (!remuneracionesTotales.equalsIgnoreCase(Long.toString(remuneracionesLeidas)))
			lLineasArch.add("Sumatoria de montos presupuestados no coincide con el total ingresado");
		if(cantFracaso > 0 || !remuneracionesTotales.equalsIgnoreCase(Long.toString(remuneracionesLeidas)))
			lLineasArch.add("Estado de la Importación: FRACASADA");
		if(cantFracaso < 1 && remuneracionesTotales.equalsIgnoreCase(Long.toString(remuneracionesLeidas)))
			lLineasArch.add("Estado de la Importación: EXITOSA");
		//SE VERIFICA QUE EXISTAN ALERTAS (VARIACION EXCESIVA(50%) ENTRE PRESUPUESTADO/DEVENGADO ó MENORES DE EDAD) MP - 17/05/2016
		String rem =getVarRem(insertarEnHistoricoDirecto);
		String edad = getCantMenores(insertarEnHistoricoDirecto);
		if(!rem.equalsIgnoreCase("0") || !edad.equalsIgnoreCase("0")){
			lLineasArch.add("ALERTAS: Favor verificar los siguientes puntos, si los registros están correctos omitir esta alerta");
			if(!rem.equalsIgnoreCase("0")){
				lLineasArch.add("Cantidad de Registros con monto Devengado inferior al 50% del Presupuestado: " + rem);
			}
			if(!edad.equalsIgnoreCase("0")){
				lLineasArch.add("Cantidad de Personas menores de Edad: "+ edad);
			}
		}
	}

	/* Select Items */

	public List<SelectItem> mesAnhoSI(String dominio) {
		Query q = em.createQuery("select Referencias from Referencias Referencias "
						+ " where Referencias.dominio = :dominio and Referencias.activo is true order by valorNum asc ");
		q.setParameter("dominio", dominio);
		List<Referencias> lista = q.getResultList();

		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
		if (dominio.equalsIgnoreCase("MESES")) {
			for (Referencias o : lista)
				si.add(new SelectItem(o.getValorNum(), "" + o.getDescLarga()));
			return si;
		} else {
			for (Referencias o : lista)
				si.add(new SelectItem(o.getValorNum(), "" + o.getValorNum()));
			return si;
		}
	}
	
	private boolean existeObjetoGto(Integer codObjGto){
		Query q = em.createQuery("select SinObj from SinObj SinObj "
				+ " where SinObj.objCodigo = :codObjGto"
				+ " and SinObj.aniAniopre = :anho");
		q.setParameter("codObjGto", codObjGto);
		q.setParameter("anho", anho);
		List<SinObj> lista = q.getResultList();
		if (lista.size() > 0)
			return true;
		return false;
	}

	private List<Referencias> lMovimiento(){
		Query q = em.createQuery("select Referencias.descAbrev from Referencias Referencias "
				+ " where Referencias.dominio = :dominio and Referencias.activo is true ");
		q.setParameter("dominio", "REMUNERACIONES MOVIMIENTO");
		List<Referencias> lista = q.getResultList();
		return lista;
	}
	
	private List<Referencias> lEstados(){
		Query q = em.createQuery("select Referencias.descLarga from Referencias Referencias "
				+ " where Referencias.dominio = :dominio and Referencias.activo is true ");
		q.setParameter("dominio", "REMUNERACIONES ESTADO");
		List<Referencias> lista = q.getResultList();
		return lista;
	}

	private List<Integer> lFF(){
		Query q = em.createQuery("select Referencias.valorNum from Referencias Referencias "
				+ " where Referencias.dominio = :dominio and Referencias.activo is true ");
		q.setParameter("dominio", "REMUNERACIONES FF");
		List<Integer> lista = q.getResultList();
		return lista;
	}
	
	private List<Integer> lPCD(){
		Query q = em.createQuery("select Referencias.valorNum from Referencias Referencias "
				+ " where Referencias.dominio = :dominio and Referencias.activo is true ");
		q.setParameter("dominio", "REMUNERACIONES TIPO PCD");
		List<Integer> lista = q.getResultList();
		return lista;
	}
	
	public Boolean getEsAdministradorSFP() {
		return esAdministradorSFP;
	}

	public void setEsAdministradorSFP(Boolean esAdministradorSFP) {
		this.esAdministradorSFP = esAdministradorSFP;
	}

	public byte[] getuFile() {
		return uFile;
	}

	public void setuFile(byte[] uFile) {
		this.uFile = uFile;
	}

	public String getcType() {
		return cType;
	}

	public void setcType(String cType) {
		this.cType = cType;
	}

	public String getFname() {
		return fname;
	}

	public void setFname(String fName) {
		this.fname = fName;
	}

	public Boolean getEnviarArchivo() {
		return enviarArchivo;
	}

	public void setEnviarArchivo(Boolean enviarArchivo) {
		this.enviarArchivo = enviarArchivo;
	}

	public Integer getCantFracaso() {
		return cantFracaso;
	}

	public void setCantFracaso(Integer cantFracaso) {
		this.cantFracaso = cantFracaso;
	}

	public Integer getCantExito() {
		return cantExito;
	}

	public void setCantExito(Integer cantExito) {
		this.cantExito = cantExito;
	}

	public Integer getCantDuplicado() {
		return cantDuplicado;
	}

	public void setCantDuplicado(Integer cantDuplicado) {
		this.cantDuplicado = cantDuplicado;
	}

	public Integer getMes() {
		return mes;
	}

	public void setMes(Integer mes) {
		this.mes = mes;
	}

	public Integer getAnho() {
		return anho;
	}

	public void setAnho(Integer anho) {
		this.anho = anho;
	}

	public Boolean getMostrarModalRemplazo() {
		return mostrarModalRemplazo;
	}

	public void setMostrarModalRemplazo(Boolean mostrarModalRemplazo) {
		this.mostrarModalRemplazo = mostrarModalRemplazo;
	}

	public Boolean getLimpiarModales() {
		return limpiarModales;
	}

	public void setLimpiarModales(Boolean limpiarModales) {
		this.limpiarModales = limpiarModales;
	}
	
	public String getRegistros() {
		return registros;
	}

	public void setRegistros(String registros) {
		this.registros = registros;
	}
	
	public String getRemuneracionesTotales() {
		return remuneracionesTotales;
	}

	public void setRemuneracionesTotales(String remuneracionesTotales) {
		this.remuneracionesTotales = remuneracionesTotales;
	}
	
	public String getEstadoImportacion() {
		return estadoImportacion;
	}

	public void setEstadoImportacion(String estadoImportacion) {
		this.estadoImportacion = estadoImportacion;
	}
	
	public void convertirPlanilla() throws IOException, ParseException {
		limpiarModales();
		try{
			//conversionPlanilla();
		}
		catch (InvalidStateException e) {
			for (InvalidValue invalidValue : e.getInvalidValues()) {
				System.out.println("Instance of bean class: " + invalidValue.getBeanClass().getSimpleName() +
					" has an invalid property: " + invalidValue.getPropertyName() + " with message: " + invalidValue.getMessage());
			}
		}
	}
	
	/*public void conversionPlanilla() throws IOException, ParseException {
		limpiarModales();
		UploadItem fileItem = seleccionUtilFormController.crearUploadItem(fname, uFile.length, cType, uFile);
		lLineasArch = FileUtils.readLines(fileItem.getFile());
		
		if (procesarLineasConvertirPlanilla()) {
			SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
			String nArchivo = sdf2.format(new Date()) + "_" + fname;
			File archSalida = new File(nArchivo);
			FileUtils.writeLines(archSalida, lLineasArch);
			JasperReportUtils.respondFile(archSalida, nArchivo, false, "application/vnd.ms-excel");
		} else {
			if (mesIterado != null)
				statusMessages.add(Severity.INFO, "Archivo inválido");
			else
				statusMessages.add(Severity.ERROR, "No se pudo realizar la operación");
		}
	}*/
	
	public boolean procesarLineasConvertirPlanilla() {
		DTO680Tmp dto = new DTO680Tmp();
		String o = null;
		int discapacidad = 0;
		ArrayList<DTO680Tmp> pla = new ArrayList<DTO680Tmp>();
		
		if (!validarCabecera())
			return false;

		for (int i = 1; i < lLineasArch.size(); i++) {
			
			o = lLineasArch.get(i);
			dto = precondCompos(o);
			String cSeparador = ";";
			if (dto != null) {
				try {
					if(!dto.getNombres().equalsIgnoreCase("VACANTE")){
						//String linea = lLineasArch.get(i);
						if(dto.getDiscapaci() == null)
							discapacidad = 0;
						else{
							if((dto.getDiscapaci().equals("S")) || (dto.getDiscapaci().equals("SI"))){
								discapacidad = 1;
							}
							if((dto.getDiscapaci().equals("N")) || (dto.getDiscapaci().equals("NO")))
								discapacidad = 0;
						}
						String linea = dto.getAnhio() + cSeparador + dto.getMes() + cSeparador + dto.getNivelEnti() + cSeparador + dto.getOee()
									+ cSeparador + cSeparador + dto.getCedula() + cSeparador + dto.getNombres()
									+ cSeparador + dto.getApellidos() + cSeparador + dto.getEstado() + cSeparador + dto.getObjetoGto()
									+ cSeparador + dto.getCateg() + cSeparador + dto.getPresup() + cSeparador + dto.getDeven() + cSeparador + "A"
									+ cSeparador + cSeparador + dto.getCargo() + cSeparador + discapacidad;
						lLineasArch.set(i, linea);
					}
				}
				catch (Exception e) {
					agregarEstadoMotivo("FRACASO","Error al procesar la linea " + e.getMessage(), i);
					e.printStackTrace();
					statusMessages.add(Severity.ERROR, "No se pudo realizar la operación");
				}
			}
		}
		lLineasArch.set(0, cabeceraInsercion);
		return true;
	}
	
	public boolean compararNomApe(String identificaciones, String parametros){
		String [] identList = identificaciones.split(" ");
		String [] paramList = parametros.split(" ");
		boolean respuesta = false;
		for(int i=0;i<identList.length;i++){
			for(int j=0;j<paramList.length;j++){
				if(identList[i].equalsIgnoreCase(paramList[j])){
					return true;
				}
				else{
					respuesta = false;
				}	
			}
		}	
		return respuesta;
	}
	
	public String removeCadenasEspeciales(String input) {
		String original = "áàäéèëíìïóòöúùüÁÀÄÉÈËÍÌÏÓÒÖÚÙÜçÇ";
		String ascii = "aaaeeeiiiooouuuAAAEEEIIIOOOUUUcC";
		String output = input;
		for (int i = 0; i < original.length(); i++) {
			output = output.replace(original.charAt(i), ascii.charAt(i));
		}
		return output;
	}
	
	public void renombrarPersonas() {

		/*Date fechaAlta = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(fechaAlta);
		int month = cal.get(Calendar.MONTH);*/
		String sqlPersonas = "select * from general.persona where fecha_alta > '2015-07-08'and fecha_alta < '2015-07-17' and id_pais_expedicion_doc = 100 order by fecha_alta";
		Query qPersona = em.createNativeQuery(sqlPersonas, Persona.class);
		List<Persona> listaPersonas = qPersona.getResultList();
		int i=0;

		if(listaPersonas.size() > 0){
			for (Persona individuo : listaPersonas) {
				PersonaDTO personaDTOauxiliar = seleccionUtilFormController.buscarPersonaAuxiliarRenombrarPersonas(individuo.getDocumentoIdentidad(), "PARAGUAY");
				if(personaDTOauxiliar != null){
					if(personaDTOauxiliar.getPersona() != null){
						if(personaDTOauxiliar.getPersona().getNombres() != null && personaDTOauxiliar.getPersona().getApellidos() != null){
							System.out.println("Procesada linea "+i+" fecha_alta "+individuo.getFechaAlta());
							individuo.setNombres(personaDTOauxiliar.getPersona().getNombres());
							individuo.setApellidos(personaDTOauxiliar.getPersona().getApellidos());
							em.persist(individuo);
							i++;
						}
					}
				}
				/*if(fechaAlta == null || fechaAlta != individuo.getFechaAlta()){
					System.out.println("CAMBIO FECHA");
					em.flush();
					fechaAlta = individuo.getFechaAlta();
				}*/
			}
			em.flush();
			statusMessages.add(Severity.INFO, "Personas renombradas exitosamente");
		}
	}
	
	private String nombreMes(Integer mesObjetivo){
		String sqlMes = "select * from seleccion.referencias where referencias.dominio = 'MESES' and referencias.activo = true and referencias.valor_num = "+mesObjetivo.intValue();
		Query qMes = em.createNativeQuery(sqlMes, Referencias.class);
		Referencias refMes = (Referencias) qMes.getResultList().get(0);
		return refMes.getDescAbrev();
	}
	
	private Boolean esNuevoMes(){
		//SI EL MES EN CUESTION ES ENERO
		/*if(mes == 1){
			String sqlRemuneracionesNuevoMes = "select * from remuneracion.remuneraciones_tmp where anho < "+anho+
					" and nivel = "+nivelEntidadOeeUtil.getCodNivelEntidad().intValue()+" and entidad = "+nivelEntidadOeeUtil.getCodSinEntidad().intValue()+
					" and oee = "+nivelEntidadOeeUtil.getOrdenUnidOrg().intValue();

			Query qRemuneracionesNuevoMes = em.createNativeQuery(sqlRemuneracionesNuevoMes, RemuneracionesTmp.class);
			
			if(qRemuneracionesNuevoMes.setMaxResults(2).getResultList().size() > 0)
				return true;
			else
				return false;
		}
		else{
			String sqlRemuneracionesNuevoMes = "select * from remuneracion.remuneraciones_tmp where ( mes >= "+mes+" or anho >= "+anho+
					" ) and nivel = "+nivelEntidadOeeUtil.getCodNivelEntidad().intValue()+" and entidad = "+nivelEntidadOeeUtil.getCodSinEntidad().intValue()+
					" and oee = "+nivelEntidadOeeUtil.getOrdenUnidOrg().intValue();

			Query qRemuneracionesNuevoMes = em.createNativeQuery(sqlRemuneracionesNuevoMes, RemuneracionesTmp.class);
			
			if(qRemuneracionesNuevoMes.setMaxResults(2).getResultList().isEmpty())
				return true;
			else
				return false;

		}*/
		//ZD 09/03/16 -- Obtener el MAX (anho) y no hacer un select * que implica mayor tiempo de procesamiento
		Integer maxAnho = 0;
		Integer maxMes = 0;
		String sqlRemuneracionesNuevoMes = "select MAX (anho) as anho from remuneracion.remuneraciones_tmp where nivel = "+nivelEntidadOeeUtil.getCodNivelEntidad().intValue()+
				" and entidad = "+nivelEntidadOeeUtil.getCodSinEntidad().intValue()+" and oee = "+nivelEntidadOeeUtil.getOrdenUnidOrg().intValue();

		Query qRemuneracionesNuevoMes = em.createNativeQuery(sqlRemuneracionesNuevoMes);
		maxAnho = (Integer) qRemuneracionesNuevoMes.getSingleResult();
		if(maxAnho != null){
			if(anho.intValue() > maxAnho.intValue()){
				return true;
			}else{
			sqlRemuneracionesNuevoMes = "select MAX (mes) as mes from remuneracion.remuneraciones_tmp where nivel = "+nivelEntidadOeeUtil.getCodNivelEntidad().intValue()+
					" and entidad = "+nivelEntidadOeeUtil.getCodSinEntidad().intValue()+" and oee = "+nivelEntidadOeeUtil.getOrdenUnidOrg().intValue() + " and anho = "+maxAnho.intValue();
			qRemuneracionesNuevoMes = em.createNativeQuery(sqlRemuneracionesNuevoMes);
			maxMes = (Integer) qRemuneracionesNuevoMes.getSingleResult();
			}
			if(anho.compareTo(maxAnho.intValue()) == 0){
				if(maxMes != null && mes > maxMes.intValue())
					return true;
				else
					return false;	
			}
		}
		
		return false;
}
	
	public Persona buscarBDLocal(String documento) {
		Query q = em.createQuery("select Persona from Persona Persona "
						+ " where Persona.documentoIdentidad =:documento and Persona.activo = true");
		q.setParameter("documento", documento);

		List<Persona> lista = q.getResultList();

		if (lista.size() > 0)
			return lista.get(0);

		return null;
	}
	
	@Transactional
	@Asynchronous
	public QuartzTriggerHandle updateHistoricoRemuneracionesTmp(@Expiration Date when,@IntervalCron String interval) {
	
		//ACTUALIZACION DE LA TABLA REMUNERACIONES_TMP
		Date fechaIniUpdate = new Date();
		List<HistoricoRemuneracionesTmp> listaHistoRemuTmp = new ArrayList();
		
		System.out.println("PROCESANDO ACTUALIZACION DE LOTE DE HISTORICO_REMUNERACIONES_TMP");

		String sqlHistoricoRemuneracionesTmp = "select * from remuneracion.historico_remuneraciones_tmp where historico_remuneraciones_tmp.id_persona is null and historico_remuneraciones_tmp.id_configuracion_uo is null limit 2000";
		Query qHistoRemuTmp = em.createNativeQuery(sqlHistoricoRemuneracionesTmp, HistoricoRemuneracionesTmp.class);
		listaHistoRemuTmp = qHistoRemuTmp.getResultList();
		int i=1;

		if(listaHistoRemuTmp.size() > 0){
			//System.out.println("PROCESANDO ACTUALIZACION DE LOTE DE REMUNERACIONES_TMP. TRATANDO "+listaRemuTmp.size()+" LINEAS");
			//OBTENEMOS LA TRIPLETA INICIAL (DEL PRIMER REGISTRO)
			Integer nivel = listaHistoRemuTmp.get(0).getNivel();
			Integer entidad = listaHistoRemuTmp.get(0).getEntidad();
			Integer oee = listaHistoRemuTmp.get(0).getOee();
			//OBTENEMOS LA CONFIGURACION_UO_CAB PARA DICHA TRIPLETA
			ConfiguracionUoCab uoCab = getConfUoCab(nivel, entidad, oee);
			//System.out.println("OBTENIDA PRIMERA CONFIG_UO");
			
			for (HistoricoRemuneracionesTmp remu : listaHistoRemuTmp) {
				
				if(!remu.getDocumentoIdentidad().substring(0, 1).equalsIgnoreCase("E")){
					//SI HUBO UN CAMBIO EN LA TRIPLETA LEIDA, ACTUALIZAMOS LA CONFIGURACION_UO_CAB
					if((remu.getNivel() != nivel) || (remu.getEntidad() != entidad) || (remu.getOee() != oee)){
						uoCab = getConfUoCab(remu.getNivel(), remu.getEntidad(), remu.getOee());
						nivel = remu.getNivel();
						entidad = remu.getEntidad();
						oee = remu.getOee();
						//System.out.println("ACTUALIZADA CONF_UO_CAB");
					}
					//Buscamos a la persona
					Persona personaAux = buscarBDLocal(remu.getDocumentoIdentidad());
					//System.out.println("PERSONA ENCONTRADA");
					
					if(personaAux != null){
						remu.setPersona(personaAux);
						remu.setConfiguracionUoCab(uoCab);
						remu.setUsuMod("SYSTEM");
						remu.setFechaMod(new Date());;
						em.persist(remu);
					}
				}
				/*if(i%25 == 0){
					//System.out.println("PROCESADA LINEA "+i);
					em.flush();
					//System.out.println("FLUSH");
				}*/
				i++;
			}
			em.flush();
			System.out.println("FLUSH");
			//System.out.println("FINALIZADA ACTUALIZACION DE LOTE DE REMUNERACIONES_TMP");
		}
		System.out.println("FINALIZADA ACTUALIZACION DE LOTE DE HISTORICO_REMUNERACIONES_TMP");
		Date fechaFinUpdate = new Date();
		String mensajeEmail = "ACTUALIZACION FINALIZADA. Procesadas "+listaHistoRemuTmp.size()+" lineas en HISTORICO_REMUNERACIONES_TMP. INICIO: "+fechaIniUpdate.toString()+
							". FIN: "+fechaFinUpdate.toString();
		//seleccionUtilFormController.enviarMails("sorue@dataworks.com.py", mensajeEmail, "Update RemuneracionesTmp", null);
		//seleccionUtilFormController.enviarMails("rveron@dataworks.com.py", mensajeEmail, "Update RemuneracionesTmp", null);
		return null;
	}
	
	@Transactional
	@Asynchronous
	public QuartzTriggerHandle updateRemuneracionesTmp(@Expiration Date when,@IntervalCron String interval) {
		
		//ACTUALIZACION DE LA TABLA REMUNERACIONES_TMP
		Date fechaIniUpdate = new Date();
		List<RemuneracionesTmp> listaRemuTmp = new ArrayList();
		
		System.out.println("PROCESANDO ACTUALIZACION DE LOTE DE REMUNERACIONES_TMP");

		String sqlRemuneracionesTmp = "select * from remuneracion.remuneraciones_tmp where remuneraciones_tmp.id_persona is null and remuneraciones_tmp.id_configuracion_uo is null limit 2000";
		Query qRemuTmp = em.createNativeQuery(sqlRemuneracionesTmp, RemuneracionesTmp.class);
		listaRemuTmp = qRemuTmp.getResultList();
		int i=1;

		if(listaRemuTmp.size() > 0){
			//System.out.println("PROCESANDO ACTUALIZACION DE LOTE DE REMUNERACIONES_TMP. TRATANDO "+listaRemuTmp.size()+" LINEAS");
			//OBTENEMOS LA TRIPLETA INICIAL (DEL PRIMER REGISTRO)
			Integer nivel = listaRemuTmp.get(0).getNivel();
			Integer entidad = listaRemuTmp.get(0).getEntidad();
			Integer oee = listaRemuTmp.get(0).getOee();
			//OBTENEMOS LA CONFIGURACION_UO_CAB PARA DICHA TRIPLETA
			ConfiguracionUoCab uoCab = getConfUoCab(nivel, entidad, oee);
			//System.out.println("OBTENIDA PRIMERA CONFIG_UO");
			
			for (RemuneracionesTmp remu : listaRemuTmp) {
				
				if(!remu.getDocumentoIdentidad().substring(0, 1).equalsIgnoreCase("E")){
					//SI HUBO UN CAMBIO EN LA TRIPLETA LEIDA, ACTUALIZAMOS LA CONFIGURACION_UO_CAB
					if((remu.getNivel() != nivel) || (remu.getEntidad() != entidad) || (remu.getOee() != oee)){
						uoCab = getConfUoCab(remu.getNivel(), remu.getEntidad(), remu.getOee());
						nivel = remu.getNivel();
						entidad = remu.getEntidad();
						oee = remu.getOee();
						//System.out.println("ACTUALIZADA CONF_UO_CAB");
					}
					//Buscamos a la persona
					Persona personaAux = buscarBDLocal(remu.getDocumentoIdentidad());
					//System.out.println("PERSONA ENCONTRADA");
					
					if(personaAux != null){
						remu.setPersona(personaAux);
						remu.setConfiguracionUoCab(uoCab);
						remu.setUsuMod("SYSTEM");
						remu.setFechaMod(new Date());;
						em.persist(remu);
					}
				}
				/*if(i%25 == 0){
					//System.out.println("PROCESADA LINEA "+i);
					em.flush();
					//System.out.println("FLUSH");
				}*/
				i++;
			}
			em.flush();
			System.out.println("FLUSH");
			//System.out.println("FINALIZADA ACTUALIZACION DE LOTE DE REMUNERACIONES_TMP");
		}
		System.out.println("FINALIZADA ACTUALIZACION DE LOTE DE REMUNERACIONES_TMP");
		Date fechaFinUpdate = new Date();
		String mensajeEmail = "ACTUALIZACION FINALIZADA. Procesadas "+listaRemuTmp.size()+" lineas en REMUNERACIONES_TMP. INICIO: "+fechaIniUpdate.toString()+
							". FIN: "+fechaFinUpdate.toString();
		//seleccionUtilFormController.enviarMails("sorue@dataworks.com.py", mensajeEmail, "Update RemuneracionesTmp", null);
		//seleccionUtilFormController.enviarMails("rveron@dataworks.com.py", mensajeEmail, "Update RemuneracionesTmp", null);
		return null;
	}	
	public ConfiguracionUoCab getConfUoCab(Integer nivel, Integer entidad, Integer oee) {

		String sql = "select * from planificacion.entidad entidad "+
		"join planificacion.configuracion_uo_cab conf_uo_cab on conf_uo_cab.id_configuracion_uo = entidad.id_configuracion_uo "+
		"where entidad.nen_codigo = "+nivel.intValue()+" and entidad.ent_codigo = "+entidad.intValue()+" and conf_uo_cab.orden = "+oee.intValue();
		
		Query q = em.createNativeQuery(sql, Entidad.class);
		List<Entidad> lista = q.getResultList();
		if(lista.size()>0)
			return lista.get(0).getConfiguracionUoCab();
		else
			return null;
		
	}
	
	public boolean existePlanillaEnProceso(ConfiguracionUoCab confUo) {

		String sql = "select * from remuneracion.control_remuneraciones_tmp control where "+
		"control.id_configuracion_uo = "+confUo.getIdConfiguracionUo()+" and control.estado_proceso = 'INICIADO'";
		
		Query q = em.createNativeQuery(sql, ControlRemuneracionesTmp.class);
		List<ControlRemuneracionesTmp> lista = q.getResultList();
		if(lista.size()>0)
			return true;
		else
			return false;
		
	}
	
	@Transactional
	@Asynchronous
	public QuartzTriggerHandle updateControlRemuneracionesTmp(@Expiration Date when,@IntervalCron String interval) {
		System.out.println("ACTUALIZANDO CONTROL_REMUNERACIONES_TMP");
		
		String sqlControlRemuneracionesTmp = "select * from remuneracion.control_remuneraciones_tmp where control_remuneraciones_tmp.estado_proceso = 'INICIADO'";
		Query qControlRemuTmp = em.createNativeQuery(sqlControlRemuneracionesTmp, ControlRemuneracionesTmp.class);
		List<ControlRemuneracionesTmp> listaControlRemuTmp = qControlRemuTmp.getResultList();

		if(listaControlRemuTmp.size() > 0){
			for (ControlRemuneracionesTmp controlRemu : listaControlRemuTmp) {
				controlRemu.setEstadoProceso("ABORTADO");
				controlRemu.setFechaUpdate(new Date());
				em.merge(controlRemu);
				em.flush();
			}
		}
		return null;
	}
	
	@Transactional
	@Asynchronous
	public QuartzTriggerHandle seteoPersonasRepetidasABorrar(@Expiration Date when,@IntervalCron String interval) {
		eliminarPersonasRepetidasPortal();
		return null;
	}
	
	@Transactional
	@Asynchronous
	public QuartzTriggerHandle borrarPersonasRepetidas(@Expiration Date when,@IntervalCron String interval) {
		eliminarPersonasRepetidasRemuneraciones();
		return null;
	}
	
	public void eliminarPersonasRepetidasPortal() {

		Query q = em
				.createQuery("select Persona.documentoIdentidad from Persona Persona "
						//+ "where Persona.documentoIdentidad =:ci "
						+ "group by Persona.documentoIdentidad "
						+ "having count(Persona)> 1");
		//q.setParameter("ci","3963852");

		List<String> lista = q.getResultList();

		for (String documento : lista) {
			q = em.createQuery("select Persona from Persona Persona "
					+ " where Persona.documentoIdentidad =:documento "
					+ "order by Persona.fechaAlta");
			q.setParameter("documento", documento);

			List<Persona> listaPersonasRepetidas = q.getResultList();

			Persona personaNoBorrar = getPersonaNoBorrar(listaPersonasRepetidas);
			
			if (personaNoBorrar != null){
				
				for (Persona persona : listaPersonasRepetidas) {

					if (persona != personaNoBorrar) {

						q = em.createQuery("select PersonaPostulante from PersonaPostulante PersonaPostulante "
								+ " where PersonaPostulante.persona =:persona");
						q.setParameter("persona", persona);

						List<PersonaPostulante> listaPersonaPostulante = q.getResultList();

						if (listaPersonaPostulante.isEmpty()) {
							//if (persona.getUsuAlta().equals("PORTAL")) {

								q = em.createQuery("select usuario from Usuario usuario "
										+ " where usuario.persona =:persona");
								q.setParameter("persona", persona);

								List<Usuario> listaUsuario = q.getResultList();

								if (!listaUsuario.isEmpty()) {
									q = em.createQuery("select usuarioRol from UsuarioRol usuarioRol "
											+ " where usuarioRol.usuario =:usuario");
									q.setParameter("usuario", listaUsuario.get(0));

									UsuarioRol usu = (UsuarioRol) q.getResultList().get(0);
									
									UsuarioRol usuarioRol = em.find(UsuarioRol.class, usu.getIdUsuarioRol());
									em.remove(usuarioRol);
									
									
									Usuario usuario = em.find(Usuario.class, listaUsuario.get(0).getIdUsuario());
									em.remove(usuario);
									
								}

							//}

						} else {
							for (PersonaPostulante personaPostulante : listaPersonaPostulante) {
								personaPostulante.setPersona(personaNoBorrar);
								em.merge(personaPostulante);
								em.flush();
							}
						}

						q = em.createQuery("select postulacion from Postulacion postulacion "
								+ " where postulacion.persona =:persona");
						q.setParameter("persona", persona);

						List<Postulacion> listaPostulacion = q.getResultList();

						if (!listaPostulacion.isEmpty()) {
							for (Postulacion postulacion : listaPostulacion) {
								postulacion.setPersona(personaNoBorrar);
								em.merge(postulacion);
								em.flush();
							}
						}
						if (persona.getUsuAlta().equals("PORTAL")) {
							q = em.createQuery("select documentos from Documentos documentos "
									+ " where documentos.persona =:persona");
							q.setParameter("persona", persona);

							List<Documentos> listaDocumentos = q.getResultList();

							if (!listaDocumentos.isEmpty()) {
								for (Documentos documentos : listaDocumentos) {
									documentos.setPersona(personaNoBorrar);
									em.merge(documentos);
									em.flush();
								}
							}
						}
						//--------------------------------------------------------------------
						q = em.createQuery("select idiomasPersona from IdiomasPersona idiomasPersona "
									+ " where idiomasPersona.persona =:persona");
						q.setParameter("persona", persona);

						List<IdiomasPersona> listaIdiomasPersona = q.getResultList();

						if (!listaIdiomasPersona.isEmpty()) {
							for (IdiomasPersona idiomasPersona : listaIdiomasPersona) {
								idiomasPersona.setPersona(personaNoBorrar);
								em.merge(idiomasPersona);
								em.flush();
							}
						}
						
						q = em.createQuery("select familiares from Familiares familiares "
								+ " where familiares.personaFamiliar =:persona");
						q.setParameter("persona", persona);
						List<Familiares> listaFamiliares = q.getResultList();
						if (!listaFamiliares.isEmpty()) {
							for (Familiares familiares : listaFamiliares) {
								familiares.setPersona(personaNoBorrar);
								em.merge(familiares);
								em.flush();
							}
						}
						
						q = em.createQuery("select experienciaLaboral from ExperienciaLaboral experienciaLaboral "
								+ " where experienciaLaboral.persona =:persona");
						q.setParameter("persona", persona);

						List<ExperienciaLaboral> listaExperienciaLaboral = q.getResultList();

						if (!listaExperienciaLaboral.isEmpty()) {
							for (ExperienciaLaboral experienciaLaboral : listaExperienciaLaboral) {
								experienciaLaboral.setPersona(personaNoBorrar);
								em.merge(experienciaLaboral);
								em.flush();
							}
						}
						
						q = em.createQuery("select empleadoPuesto from EmpleadoPuesto empleadoPuesto "
								+ " where empleadoPuesto.persona =:persona");
						q.setParameter("persona", persona);

						List<EmpleadoPuesto> listaEmpleadoPuesto = q.getResultList();

						if (!listaEmpleadoPuesto.isEmpty()) {
							for (EmpleadoPuesto empleadoPuesto : listaEmpleadoPuesto) {
								empleadoPuesto.setPersona(personaNoBorrar);
								em.merge(empleadoPuesto);
								em.flush();
							}
						}
						
						q = em.createQuery("select legajos from Legajos legajos "
								+ " where legajos.persona =:persona");
						q.setParameter("persona", persona);

						List<Legajos> listaLegajos = q.getResultList();

						if (!listaLegajos.isEmpty()) {
							for (Legajos legajo : listaLegajos) {
								legajo.setPersona(personaNoBorrar);
								em.merge(legajo);
								em.flush();
							}
						}
						
						q = em.createQuery("select estudiosRealizados from EstudiosRealizados estudiosRealizados "
								+ " where estudiosRealizados.persona =:persona");
						q.setParameter("persona", persona);

						List<EstudiosRealizados> listaEstudiosRealizados = q.getResultList();

						if (!listaEstudiosRealizados.isEmpty()) {
							for (EstudiosRealizados estudioRealizado : listaEstudiosRealizados) {
								estudioRealizado.setPersona(personaNoBorrar);
								em.merge(estudioRealizado);
								em.flush();
							}
						}
						
						q = em.createQuery("select personaDiscapacidad from ReprPersonaDiscapacidad personaDiscapacidad "
								+ " where personaDiscapacidad.persona =:persona");
						q.setParameter("persona", persona);

						List<ReprPersonaDiscapacidad> listaPersonaDiscapacidad = q.getResultList();

						if (!listaPersonaDiscapacidad.isEmpty()) {
							for (ReprPersonaDiscapacidad personaDiscapacidad : listaPersonaDiscapacidad) {
								personaDiscapacidad.setPersona(personaNoBorrar);
								em.merge(personaDiscapacidad);
								em.flush();
							}
						}
						
						q = em.createQuery("select personaDiscapacidad from ReprPersonaDiscapacidad personaDiscapacidad "
								+ " where personaDiscapacidad.personaRep =:persona");
						q.setParameter("persona", persona);

						List<ReprPersonaDiscapacidad> listaPersonaDiscapacidadRep = q.getResultList();

						if (!listaPersonaDiscapacidadRep.isEmpty()) {
							for (ReprPersonaDiscapacidad personaDiscapacidadRep : listaPersonaDiscapacidadRep) {
								personaDiscapacidadRep.setPersona(personaNoBorrar);
								em.merge(personaDiscapacidadRep);
								em.flush();
							}
						}
						
						q = em.createQuery("select postulacionCap from PostulacionCap postulacionCap "
								+ " where postulacionCap.persona =:persona");
						q.setParameter("persona", persona);

						List<PostulacionCap> listaPostulacionCap = q.getResultList();

						if (!listaPostulacionCap.isEmpty()) {
							for (PostulacionCap postulacionCap : listaPostulacionCap) {
								postulacionCap.setPersona(personaNoBorrar);
								em.merge(postulacionCap);
								em.flush();
							}
						}
						
					}
				}
			}
		}
		System.out.println("SETEADAS PERSONAS A NO BORRAR SEGUN TABLAS PORTAL");
	}

	private Persona getPersonaNoBorrar(List<Persona> listaPersonasRepetidas) {
		int ref;
		int refMax = 0;
		Query q;
		Persona personaNoBorrar = null;

		for (Persona persona : listaPersonasRepetidas) {
			ref = 0;

			q = em.createQuery("select PersonaPostulante from PersonaPostulante PersonaPostulante "
					+ " where PersonaPostulante.persona =:persona");
			q.setParameter("persona", persona);
			ref = ref + q.getResultList().size();
			
			q = em.createQuery("select usuario from Usuario usuario "
					+ " where usuario.persona =:persona");
			q.setParameter("persona", persona);
			ref = ref + q.getResultList().size();

			q = em.createQuery("select Postulacion from Postulacion Postulacion "
					+ " where Postulacion.persona =:persona");
			q.setParameter("persona", persona);
			ref = ref + q.getResultList().size();
			
			q = em.createQuery("select familiares from Familiares familiares "
					+ " where familiares.personaFamiliar =:persona");
			q.setParameter("persona", persona);
			ref = ref + q.getResultList().size();
			
			q = em.createQuery("select empleadoPuesto from EmpleadoPuesto empleadoPuesto "
					+ " where empleadoPuesto.persona =:persona");
			q.setParameter("persona", persona);
			ref = ref + q.getResultList().size();
			
			q = em.createQuery("select legajos from Legajos legajos "
					+ " where legajos.persona =:persona");
			q.setParameter("persona", persona);
			ref = ref + q.getResultList().size();
			
			q = em.createQuery("select estudiosRealizados from EstudiosRealizados estudiosRealizados "
					+ " where estudiosRealizados.persona =:persona");
			q.setParameter("persona", persona);
			ref = ref + q.getResultList().size();
			
			q = em.createQuery("select personaDiscapacidad from ReprPersonaDiscapacidad personaDiscapacidad "
					+ " where personaDiscapacidad.persona =:persona");
			q.setParameter("persona", persona);
			ref = ref + q.getResultList().size();
			
			q = em.createQuery("select personaDiscapacidad from ReprPersonaDiscapacidad personaDiscapacidad "
					+ " where personaDiscapacidad.personaRep =:persona");
			q.setParameter("persona", persona);
			ref = ref + q.getResultList().size();
			
			q = em.createQuery("select postulacionCap from PostulacionCap postulacionCap "
					+ " where postulacionCap.persona =:persona");
			q.setParameter("persona", persona);
			ref = ref + q.getResultList().size();
			
			if (persona.getUsuAlta().equals("PORTAL")) {
				ref++;
				q = em.createQuery("select Documentos from Documentos Documentos "
						+ " where Documentos.persona =:persona");
				q.setParameter("persona", persona);
				ref = ref + q.getResultList().size();
				
				q = em.createQuery("select idiomasPersona from IdiomasPersona idiomasPersona "
						+ " where idiomasPersona.persona =:persona");
				q.setParameter("persona", persona);
				ref = ref + q.getResultList().size();
				
				q = em.createQuery("select experienciaLaboral from ExperienciaLaboral experienciaLaboral "
						+ " where experienciaLaboral.persona =:persona");
				q.setParameter("persona", persona);
				ref = ref + q.getResultList().size();
			}

			if (ref!=0 && ref > refMax) {
				refMax = ref;
				personaNoBorrar = persona;
			}
		}
		if (personaNoBorrar != null)
		{
			personaNoBorrar.setTipo("NoBorrar");
			em.merge(personaNoBorrar);
			em.flush();
		}
		
		return personaNoBorrar;
	}
	
	public void eliminarPersonasRepetidasRemuneraciones() {
		String sqlRepetidas = "select * from general.persona persona1 " 
				+"where persona1.documento_identidad in "
				+"(select persona.documento_identidad from general.persona persona " 
				+"group by persona.documento_identidad having count(persona)> 1) "
				+"order by persona1.documento_identidad, persona1.fecha_alta asc;";
		Query qRepetidas = em.createNativeQuery(sqlRepetidas, Persona.class);
		List<Persona> repetidas = qRepetidas.getResultList();
		System.out.println("PERSONAS REPETIDAS: "+repetidas.size());
		
		if(repetidas.size() > 0){
			ArrayList<Persona> cedulaRepetida = new ArrayList<Persona>();
			String cedulaLeida = repetidas.get(0).getDocumentoIdentidad();
			for (Persona repetido : repetidas) {
				if(repetido.getDocumentoIdentidad().equalsIgnoreCase(cedulaLeida)){
					cedulaRepetida.add(repetido);
				}
				else{
					actualizarRemuneraciones(cedulaRepetida);
					cedulaRepetida.clear();
					cedulaLeida = repetido.getDocumentoIdentidad();
					cedulaRepetida.add(repetido);
				}
				em.flush();
			}
			actualizarRemuneraciones(cedulaRepetida);
		}
		System.out.println("FIN DEL BORRADO DE PERSONAS REPETIDAS");
	}
	
	private List<RemuneracionesTmp> repetidasRemuneracionesTmp(String cedulaPersona){
		String sqlRemu = "select * from remuneracion.remuneraciones_tmp rem join general.persona per on rem.id_persona = per.id_persona where per.documento_identidad = '"+cedulaPersona+"'";
		Query qRemu = em.createNativeQuery(sqlRemu, RemuneracionesTmp.class);
		List<RemuneracionesTmp> listaRemuRepetidas = qRemu.getResultList();
		return listaRemuRepetidas;
	}
	
	private List<HistoricoRemuneracionesTmp> repetidasHistoricoRemuneracionesTmp(String cedulaPersona){
		String sqlHistRemu = "select * from remuneracion.historico_remuneraciones_tmp hist_rem join general.persona per on hist_rem.id_persona = per.id_persona where per.documento_identidad = '"+cedulaPersona+"'";
		Query qHistRemu = em.createNativeQuery(sqlHistRemu, HistoricoRemuneracionesTmp.class);
		List<HistoricoRemuneracionesTmp> listaHistRemuRepetidas = qHistRemu.getResultList();
		return listaHistRemuRepetidas;
	}
	
	private Persona personaReemplazante(List<Persona> lista){
		for (Persona individuo : lista){
			if(individuo.getTipo() != null){
				if(individuo.getTipo().equalsIgnoreCase("NoBorrar"))
					return individuo;
			}
		}
		for (Persona individuo : lista){
			if(individuo.getUsuAlta().equalsIgnoreCase("PORTAL") || individuo.getUsuAlta().equalsIgnoreCase("MMORINIGO"))
				return individuo;
		}
		return lista.get(0);
	}
	
	private void actualizarRemuneraciones(List<Persona> listaRepetidas){
		System.out.println("TRATANDO CEDULA: "+listaRepetidas.get(0).getDocumentoIdentidad());
		boolean eliminarReemplazante = false;
		Persona personaReemplazante = personaReemplazante(listaRepetidas);
		
		List<RemuneracionesTmp> remusTmp = repetidasRemuneracionesTmp(personaReemplazante.getDocumentoIdentidad());
		List<HistoricoRemuneracionesTmp> histRemusTmp = repetidasHistoricoRemuneracionesTmp(personaReemplazante.getDocumentoIdentidad());
		
		if(remusTmp.size() > 0){
			for (RemuneracionesTmp remuneracion : remusTmp){
				remuneracion.setPersona(personaReemplazante);
				em.merge(remuneracion);
			}
		}
		if(histRemusTmp.size() > 0){
			for (HistoricoRemuneracionesTmp remuneracion : histRemusTmp){
				remuneracion.setPersona(personaReemplazante);
				em.merge(remuneracion);
			}
		}
		if((remusTmp.size() == 0) && (histRemusTmp.size() == 0)){
			eliminarReemplazante = true;
		}
		
		for (Persona personaDuplicada : listaRepetidas){
			if(!personaDuplicada.getUsuAlta().equalsIgnoreCase("dataworks")){
				if(personaDuplicada.getTipo() != null){
					if (!personaDuplicada.getTipo().equalsIgnoreCase("NoBorrar")){
						if(personaDuplicada == personaReemplazante){
							if(eliminarReemplazante)
								em.remove(em.merge(personaDuplicada));
						}
						else{
							em.remove(em.merge(personaDuplicada));
						}
					}
				}
				else{
					if(personaDuplicada == personaReemplazante){
						if(eliminarReemplazante)
							em.remove(em.merge(personaDuplicada));
					}
					else{
						em.remove(em.merge(personaDuplicada));
					}
				}
			}
		}
		remusTmp.clear();
		histRemusTmp.clear();
	}
	
	public boolean finPresentacion() {
		//BUSCAR EL VENCIMIENTO CORRESPONDIENTE AL MES-AÑO DE LA PLANILLA A PROCESAR
		String sqlVenc = "select * from remuneracion.vencimientos where mes = "+mes+" and anho = "+anho+" and activo = TRUE";
		Query qVenc = em.createNativeQuery(sqlVenc, Vencimientos.class);
		List<Vencimientos> listaVenc = qVenc.getResultList();
		
		//BUSCAR LA REFERENCIA QUE ESPECIFICA LA CANTIDAD MAXIMA DE DIAS LUEGO DEL VENCIMIENTO PARA PROCESAR UNA PLANILLA
		String sqlLimite = "select * from seleccion.referencias where dominio = 'TIEMPO_PRESENTACION'";
		Query qLimite = em.createNativeQuery(sqlLimite, Referencias.class);
		List<Referencias> listaLimite = qLimite.getResultList();
		
		if(listaVenc.size()>0 && listaLimite.size()>0){
			//System.out.println("PRORROGA "+listaVenc.get(0).getProrroga().toString());
			Calendar fechaActual = Calendar.getInstance();
			Calendar fechaLimite = Calendar.getInstance();
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

			dateFormat.setTimeZone(fechaActual.getTimeZone());
			//System.out.println("FECHA ACTUAL "+dateFormat.format(fechaActual.getTime()));
			
			fechaLimite.setTime(listaVenc.get(0).getProrroga());
			dateFormat.setTimeZone(fechaLimite.getTimeZone());
			//System.out.println("FECHA PRORROGA "+dateFormat.format(fechaLimite.getTime()));
			
			//CALCULO DE LA FECHA LIMITE (PRORROGA DEL MES-AÑO + CANTIDAD MAXIMA DE DIAS) PARA PROCESAR UNA PLANILLA
			fechaLimite.add(Calendar.DAY_OF_YEAR, listaLimite.get(0).getValorNum());
			dateFormat.setTimeZone(fechaLimite.getTimeZone());
			//System.out.println("FECHA PRORROGA LIMITE "+dateFormat.format(fechaLimite.getTime()));
			
			if(fechaActual.getTime().compareTo(fechaLimite.getTime()) > 0)				
				return true;
			else
				return false;
		}else{
			return true;
		}
		
	}
	
	public boolean plazoRectificacion() {
		//BUSCAR SI YA  EXISTE UN REGISTRO DE PROCESAMIENTO PARA EL MES-AÑO-OEE DE LA PLANILLA EN CUESTION
		String sqlProc = "select * from remuneracion.procesamiento where mes = "+mes+" and anho = "+anho+
				" and id_configuracion_uo ="+confUoCabActual.getIdConfiguracionUo()+" and activo = TRUE";
		Query qProc = em.createNativeQuery(sqlProc, Procesamiento.class);
		List<Procesamiento> listaProc = qProc.getResultList();
		
		if(listaProc.size()>0){
			//BUSCAR LA REFERENCIA QUE ESPECIFICA LA CANTIDAD MAXIMA DE RECTIFICACIONES PARA UNA PLANILLA
			String sqlLimCant = "select * from seleccion.referencias where dominio = 'RECTIFICACIONES'";
			Query qLimCant = em.createNativeQuery(sqlLimCant, Referencias.class);
			List<Referencias> listaLimCant = qLimCant.getResultList();
			
			if(listaLimCant.size() > 0){
				//CANTIDAD MAXIMA DE RECTIFICACIONES POSIBLES
				int rectificaciones = listaLimCant.get(0).getValorNum();
				//SI LA PLANILLA EN CUESTION TODAVIA NO ALCANZO EL LIMITE DE RECTIFICACIONES POSIBLES
				if(listaProc.get(0).getCantProc() < rectificaciones){
					//BUSCAR LA REFERENCIA QUE ESPECIFICA EL LAPSO MAXIMO DE DIAS DURANTE SE PUEDE REALIZAR UNA RECTIFICACION
					String sqlLimFecha = "select * from seleccion.referencias where dominio = 'TIEMPO_RECTIFICACION'";
					Query qLimFecha = em.createNativeQuery(sqlLimFecha, Referencias.class);
					List<Referencias> listaLimFecha = qLimFecha.getResultList();
					
					if(listaLimFecha.size() > 0){
						//CANTIDAD DE DIAS DURANTE LOS CUALES SE PUEDE RECTIFICAR UNA PLANILLA
						int dias = listaLimFecha.get(0).getValorNum();
						
						Calendar fechaActual = Calendar.getInstance();
						Calendar fechaLimite = Calendar.getInstance();
						SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

						dateFormat.setTimeZone(fechaActual.getTimeZone());
						System.out.println("FECHA ACTUAL "+dateFormat.format(fechaActual.getTime()));
						
						fechaLimite.setTime(listaProc.get(0).getMinFechaAlta());
						dateFormat.setTimeZone(fechaLimite.getTimeZone());
						System.out.println("MIN FECHA ALTA "+dateFormat.format(fechaLimite.getTime()));
						
						//CALCULO DE LA FECHA LIMITE (MIN_FECHA_ALTA + CANTIDAD DE DIAS RECTIFICABLES) PARA LA RECTIFICACION
						fechaLimite.add(Calendar.DAY_OF_YEAR, rectificaciones*dias);
						dateFormat.setTimeZone(fechaLimite.getTimeZone());
						System.out.println("MIN FECHA ALTA + DIAS "+dateFormat.format(fechaLimite.getTime()));
						
						if(fechaActual.getTime().compareTo(fechaLimite.getTime()) > 0){
							return true;
						}
						else{
							//actualizarProcesamiento = true;
							return false;
						}
					}
					else{
						return true;
					}
				}
				else{
					maximoRectificacionesSuperado = true;
					return true;
				}
			}
			else{
				return true;
			}
		}else{
			//guardarProcesamiento = true;
			return false;
		}
	}
	
	public void guardarProcesamientoPlanilla(){
		//ACTUALIZACION DE LA CANTIDAD DE VECES QUE FUE PROCESADA UNA PLANILLA (PARA CONTROL/LIMITACION DE RECTIFICACIONES POSTERIORES)
		String sqlProc = "select * from remuneracion.procesamiento where mes = "+mes+" and anho = "+anho+
				" and id_configuracion_uo ="+confUoCabActual.getIdConfiguracionUo()+" and activo = TRUE";
		Query qProc = em.createNativeQuery(sqlProc, Procesamiento.class);
		List<Procesamiento> listaProc = qProc.getResultList();
		
		if(listaProc.size()>0){
			Procesamiento proceso = listaProc.get(0);
			proceso.setCantProc(proceso.getCantProc()+1);
			proceso.setUsuMod(usuarioLogueado.getCodigoUsuario());
			proceso.setFechaMod(new Date());
			proceso.setCantLineas(getRemuneracionesProcesadas(insertarEnHistoricoDirecto));
			em.merge(proceso);
		}
		else{
			//GUARDADO DE REGISTRO DE PROCESAMIENTO DE UNA PLANILLA INGRESADA POR PRIMERA VEZ (PARA CONTROL/LIMITACION DE RECTIFICACIONES POSTERIORES)
			Procesamiento proceso = new Procesamiento();
			proceso.setAnho(anho);
			proceso.setMes(mes);
			proceso.setConfiguracionUoCab(confUoCabActual);
			proceso.setCantProc(1);
			proceso.setMinFechaAlta(new Date());
			proceso.setActivo(true);
			proceso.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			proceso.setFechaAlta(new Date());
			proceso.setCantLineas(getRemuneracionesProcesadas(insertarEnHistoricoDirecto));
			em.persist(proceso);
		}
	}
	//ZD 24/02/16 -- LA MOVIDA SE REALIZA AL MOMENTO DE LA INSERCIÓN	/*
	@Transactional
	@Asynchronous
	public QuartzTriggerHandle moverInhabilitados(@Expiration Date when,@IntervalCron String interval) {
		//MOVIDA Y BORRADO DE REGISTROS INACTIVOS EN REMUNERACIONES_TMP
		String sqlRem = "select * from remuneracion.remuneraciones_tmp where activo = FALSE";
		Query qRem = em.createNativeQuery(sqlRem, RemuneracionesTmp.class);
		List<RemuneracionesTmp> listaRem = qRem.getResultList();
		if(listaRem.size()>0){
			System.out.println("MOVIENDO REMUNERACIONES INHABILITADAS");
			try {
				Query q = em.createNativeQuery("INSERT INTO remuneracion.audit_inactivos (anho,mes,nivel,entidad,oee,linea,documento_identidad,nombres,"+
						"apellidos,estado,remuneracion_total,obj_codigo,fuente_financiamiento,categoria,presupuestado,devengado,concepto,movimiento,lugar,cargo,"+
						"funcion_cumplida,carga_horaria,discapacidad,tipo_discapacidad,anho_ingreso,activo,usu_alta,fecha_alta,id_configuracion_uo,id_persona,fecha_inactivacion)"+
						" SELECT anho,mes,nivel,entidad,oee,linea,documento_identidad,nombres,apellidos,estado,remuneracion_total,obj_codigo,fuente_financiamiento,"+
						"categoria,presupuestado,devengado,concepto,movimiento,lugar,cargo,funcion_cumplida,carga_horaria,discapacidad,tipo_discapacidad,anho_ingreso,"+
						"activo,usu_alta,fecha_alta,id_configuracion_uo,id_persona,fecha_inactivacion from remuneracion.remuneraciones_tmp WHERE activo = False; COMMIT;");
				q.executeUpdate();
			} catch (Exception e) {
				statusMessages.add(Severity.ERROR,"ERROR AL MOVER REMUNERACIONES INHABILITADAS");
				System.out.println("ERROR AL MOVER REMUNERACIONES INHABILITADAS");
				e.printStackTrace();
			}
			System.out.println("BORRANDO REMUNERACIONES INHABILITADAS");
			try {
				Query q = em.createNativeQuery("DELETE FROM remuneracion.remuneraciones_tmp WHERE activo = False; COMMIT;");
				q.executeUpdate();
			
			} catch (Exception e) {
				statusMessages.add(Severity.ERROR,"ERROR AL BORRAR REMUNERACIONES INHABILITADAS");
				System.out.println("ERROR AL BORRAR REMUNERACIONES INHABILITADAS");
				e.printStackTrace();
			}
		}
		
		//MOVIDA Y BORRADO DE REGISTROS INACTIVOS EN REMUNERACIONES_TMP
		String sqlHistRem = "select * from remuneracion.historico_remuneraciones_tmp where activo = FALSE";
		Query qHistRem = em.createNativeQuery(sqlHistRem, HistoricoRemuneracionesTmp.class);
		List<HistoricoRemuneracionesTmp> listaHistRem = qHistRem.getResultList();
		if(listaHistRem.size()>0){
			System.out.println("MOVIENDO REMUNERACIONES HISTORICAS INHABILITADAS");
			try {
				Query q = em.createNativeQuery("INSERT INTO remuneracion.audit_inactivos (anho,mes,nivel,entidad,oee,linea,documento_identidad,nombres,"+
						"apellidos,estado,remuneracion_total,obj_codigo,fuente_financiamiento,categoria,presupuestado,devengado,concepto,movimiento,lugar,cargo,"+
						"funcion_cumplida,carga_horaria,discapacidad,tipo_discapacidad,anho_ingreso,activo,usu_alta,fecha_alta,id_configuracion_uo,id_persona,"+
						"usu_paso_historico,fecha_paso_historico,fecha_inactivacion,usu_mod,fecha_mod)"+
						" SELECT anho,mes,nivel,entidad,oee,linea,documento_identidad,nombres,apellidos,estado,remuneracion_total,obj_codigo,fuente_financiamiento,"+
						"categoria,presupuestado,devengado,concepto,movimiento,lugar,cargo,funcion_cumplida,carga_horaria,discapacidad,tipo_discapacidad,anho_ingreso,"+
						"activo,usu_alta,fecha_alta,id_configuracion_uo,id_persona,usu_paso_historico,fecha_paso_historico,fecha_inactivacion,usu_mod,fecha_mod"+
						" from remuneracion.historico_remuneraciones_tmp WHERE activo = False; COMMIT;");
				q.executeUpdate();
			} catch (Exception e) {
				statusMessages.add(Severity.ERROR,"ERROR AL MOVER REMUNERACIONES HISTORICAS INHABILITADAS");
				System.out.println("ERROR AL MOVER REMUNERACIONES HISTORICAS INHABILITADAS");
				e.printStackTrace();
			}
			try {
				System.out.println("BORRANDO REMUNERACIONES HISTORICAS INHABILITADAS");
				Query q = em.createNativeQuery("DELETE FROM remuneracion.historico_remuneraciones_tmp WHERE activo = False; COMMIT;");
				q.executeUpdate();
			
			} catch (Exception e) {
				statusMessages.add(Severity.ERROR,"ERROR AL BORRAR REMUNERACIONES HISTORICAS INHABILITADAS");
				System.out.println("ERROR AL BORRAR REMUNERACIONES HISTORICAS INHABILITADAS");
				e.printStackTrace();
			}
		}
		
		return null;
	}
*/

	public Boolean getAuxRemplazo() {
		return auxReemplazo;
	}

	public void setAuxRemplazo(Boolean auxRemplazo) {
		this.auxReemplazo = auxRemplazo;
	}}
