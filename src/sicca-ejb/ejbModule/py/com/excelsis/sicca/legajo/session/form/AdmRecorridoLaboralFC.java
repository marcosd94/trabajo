package py.com.excelsis.sicca.legajo.session.form;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import java.util.Calendar;

import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.persistence.ManagedPersistenceContext;
import org.richfaces.model.UploadItem;

import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.ConfiguracionUoDet;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.Documentos;
import py.com.excelsis.sicca.entity.EmpleadoConceptoPago;
import py.com.excelsis.sicca.entity.EmpleadoPuesto;
import py.com.excelsis.sicca.entity.EmpleadoPuesto651;
import py.com.excelsis.sicca.entity.EventoRecorridoLaboral;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.entity.RecorridoLegajoMigrado;
import py.com.excelsis.sicca.entity.ReferenciaAdjuntos;
import py.com.excelsis.sicca.entity.Remuneraciones;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.form.AdmDocAdjuntoFormController;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.General;

@Scope(ScopeType.CONVERSATION)
@Name("admRecorridoLaboralFC")
public class AdmRecorridoLaboralFC implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -788040635466613316L;

	@In(required=false)
	Usuario usuarioLogueado;
	
	@In(value="entityManager")
    EntityManager em;
	@In
	StatusMessages statusMessages;
	

	@In(create = true)
	SeleccionUtilFormController seleccionUtilFormController;
	
	@In(create = true)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;
	

	@In(create = true)
	AdmDocAdjuntoFormController admDocAdjuntoFormController;

	
	private Long idPersona;
	private Persona persona= new Persona();
	private String texto;
	private EmpleadoPuesto empleadoPuesto= new EmpleadoPuesto();
	private String vinculacion;
	private String situacion;
	private String ingresoPor;
	private String puesto;
	private String antiguedad;
	private List<EmpleadoConceptoPago> empleadoConceptoPagos= new Vector<EmpleadoConceptoPago>();
	private List<Remuneraciones> remuneraciones= new Vector<Remuneraciones>();
	private List<EmpleadoPuesto651> empleadoPuestoLista= new ArrayList<EmpleadoPuesto651>();
	private Date fecActo;
	private Integer actoAdmin;
	
	private Integer valorTotal=null;
	private Integer ultimo1;
	private Integer ultimo2;
	private boolean ver=false;
	private EmpleadoConceptoPago empleadoConceptoPagoAdd = new EmpleadoConceptoPago();
	private String categoria;
	private Integer objCodigo;
	
	/**
	 * 
	 * EVENTOS DE RECORRIDO LABORAL
	 * 
	 */
	
	private EmpleadoPuesto empleadoPuestoAdd= new EmpleadoPuesto();
	private EmpleadoPuesto empleadoPuestoEvento= new EmpleadoPuesto();
	private PlantaCargoDet plantaCargoDetEvento= new PlantaCargoDet();
	private ConfiguracionUoCab configuracionUoCabEvento = new ConfiguracionUoCab();
	private ConfiguracionUoDet configuracionUoDetEvento = new ConfiguracionUoDet();
	private EventoRecorridoLaboral eventoAdd;
	private List<EventoRecorridoLaboral> eventos = new ArrayList();
	private Long idTipoEventoDatosEspecificos;
	private Integer nroActoAdministrativo;
	private Date fechaActoAdministrativo;
	private String encabezado;
	private String observaciones;
	private int indiceEvento;
	private String from;
	private String accion;
	private byte[] uploadedFileEvento;
	private String contentTypeEvento;
	private String fileNameEvento;
	
	

	/**
	 * DOCUMENTO ADJUNTO
	 * **/
	private String nombreDoc;
	private Long idDoc;
	private byte[] uploadedFile;
	private String contentType;
	private String fileName;
	private Long idTipoDoc;
	private Long idDocAnterior=null;
	private String descTipoDoc;
	private Boolean esOtro;
	private Boolean esOtroTipo;
	private String descTipoEvento;
	private Long idEstado;

	private List<RecorridoLegajoMigrado> recorriloLaboralMigradoList;
	
	public void init(){
		try {
			persona=em.find(Persona.class, idPersona);
			setearDatos();
			cargarDatos();
			esOtro = false;
			esOtroTipo = false;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void init2(){
		try {
			cargarDatosMigrados();
			esOtro = false;
			esOtroTipo = false;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	private void cargarDatos(){
		try {
			
			empleadoPuesto=(EmpleadoPuesto)em.createQuery("Select d from EmpleadoPuesto d " +
					" where d.persona.idPersona=:idPersona and d.actual=true and d.activo=true " +
					" order by d.idEmpleadoPuesto ")
					.setParameter("idPersona",idPersona).getResultList().get(0);
			/**
			 * nivel-entidad-oee-uo
			 * */
			if(empleadoPuesto!=null &&empleadoPuesto.getPlantaCargoDet()!=null){
				ConfiguracionUoDet uoDet=em.find(ConfiguracionUoDet.class, empleadoPuesto.getPlantaCargoDet().getConfiguracionUoDet().getIdConfiguracionUoDet());
				nivelEntidadOeeUtil.setIdConfigCab(uoDet.getConfiguracionUoCab().getIdConfiguracionUo());
				nivelEntidadOeeUtil.setIdUnidadOrganizativa(uoDet.getIdConfiguracionUoDet());
				nivelEntidadOeeUtil.init2();
				puesto=empleadoPuesto.getPlantaCargoDet().getDescripcion();
			}
			
			//otros datos
			
			vinculacion=empleadoPuesto.getDatosEspecificosByIdDatosEspEstado()!=null?empleadoPuesto.getDatosEspecificosByIdDatosEspEstado().getDescripcion():null;
			situacion=empleadoPuesto.getDatosEspecificosByIdDatosEspSituacion()!=null?empleadoPuesto.getDatosEspecificosByIdDatosEspEstado().getDescripcion():null;
			ingresoPor=empleadoPuesto.getDatosEspecificosByIdDatosEspTipoIngresoMovilidad()!=null?empleadoPuesto.getDatosEspecificosByIdDatosEspTipoIngresoMovilidad().getDescripcion():null;
			
			
		} catch (NoResultException e) {
			// TODO: handle exception
			empleadoPuesto= new EmpleadoPuesto();
		}
		/**
		 * 	Grilla de Valor econï¿½mico SICCA
		 * */
		empleadoConceptoPagos=em.createQuery("Select d from EmpleadoConceptoPago d " +
				" where d.empleadoPuesto.persona.idPersona=:idPersona and d.empleadoPuesto.plantaCargoDet is not null and d.empleadoPuesto.actual is true").setParameter("idPersona", idPersona).getResultList();
		/**
		 *  Grilla de Ultimo pago SINARH
		 * */
		remuneraciones=em.createQuery("Select r from Remuneraciones r " +
				" where r.empleadoPuesto.persona.idPersona=:idPersona").setParameter("idPersona",idPersona).getResultList();
		/**
		 *  Grilla Listado de puestos/cargos ocupados:
		 * **/
		calcularMonto();
		cargarEmpleado();
		
	}
	@SuppressWarnings("unchecked")
	private void cargarDatosMigrados(){
		try {
			
			recorriloLaboralMigradoList=em.createQuery("Select d from RecorridoLegajoMigrado d " +
					" where d.persona.idPersona=:idPersona ")
					.setParameter("idPersona",idPersona).getResultList();
			
			
		} catch (NoResultException e) {
			// TODO: handle exception
			recorriloLaboralMigradoList=new ArrayList<RecorridoLegajoMigrado>();
		}
		/**
		 * 	Grilla de Valor econï¿½mico SICCA
		 * */
		empleadoConceptoPagos=em.createQuery("Select d from EmpleadoConceptoPago d " +
				" where d.empleadoPuesto.persona.idPersona=:idPersona").setParameter("idPersona", idPersona).getResultList();
		/**
		 *  Grilla de Ultimo pago SINARH
		 * */
		remuneraciones=em.createQuery("Select r from Remuneraciones r " +
				" where r.empleadoPuesto.persona.idPersona=:idPersona").setParameter("idPersona",idPersona).getResultList();
		/**
		 *  Grilla Listado de puestos/cargos ocupados:
		 * **/
		calcularMonto();
		cargarEmpleado();
		
	}
	private void calcularMonto(){
		try {
			Long monto =(Long)em.createQuery("Select sum(d.monto) from EmpleadoConceptoPago d " +
			" where d.empleadoPuesto.persona.idPersona=:idPersona and d.empleadoPuesto.plantaCargoDet is not null").setParameter("idPersona", idPersona).getSingleResult();
			valorTotal=monto!=null?monto.intValue():null;
		} catch (NoResultException e) {
			// TODO: handle exception
		}
		
		try {
			Long v1=(Long) em.createQuery("Select sum(r.presupuestado) from Remuneraciones r " +
			" where r.empleadoPuesto.persona.idPersona=:idPersona ").setParameter("idPersona", idPersona).getSingleResult();
			ultimo1=v1!=null?v1.intValue():null;
		} catch (NoResultException e) {
			// TODO: handle exception
		}
	
		try {
			Long v2=(Long) em.createQuery("Select sum(r.devengado) from Remuneraciones r " +
			" where r.empleadoPuesto.persona.idPersona=:idPersona ").setParameter("idPersona", idPersona).getSingleResult();
			ultimo2=v2!=null?v2.intValue():null;
			
		} catch (NoResultException e) {
			// TODO: handle exception
		}
		
		
		
	}
	@SuppressWarnings("unchecked")
	private void cargarEmpleado(){
		empleadoPuestoLista= new ArrayList<EmpleadoPuesto651>();
		String sql=" select * from general.empleado_puesto_651 r where r.id_persona=:idPersona order by fecha_inicio desc ";
		//empleadoPuestoLista=em.createQuery(sql).setParameter("idPersona", idPersona).getResultList();
		List<Object> result = em.createNativeQuery(sql).setParameter("idPersona", idPersona).getResultList();
		if (!result.isEmpty()) {
			for (Object obj : (List<Object>) result) {
				Object[] record = (Object[]) obj;
				EmpleadoPuesto651 empleadoPuesto651= new EmpleadoPuesto651();
				if(record[0]!=null)
					empleadoPuesto651.setOee((String)record[0]);
				
				if(record[1]!=null)
					empleadoPuesto651.setUo((String)record[1]);

				if(record[2]!=null)
					empleadoPuesto651.setPuesto((String)record[2]);

				if(record[3]!=null)
					empleadoPuesto651.setFechaInicio((Date)record[3]);
				if(record[4]!=null)
					empleadoPuesto651.setFechaFin((Date)record[4]);
				if(record[5]!=null)
					empleadoPuesto651.setEstado((String)record[5]);
				if(record[6]!=null)
					empleadoPuesto651.setIdPersona(Long.parseLong(record[6].toString()));
				if(record[7]!=null)
					empleadoPuesto651.setId(Long.parseLong(record[7].toString()));
				if(record[8]!=null)
					empleadoPuesto651.setTipo(((String)record[8]));
							
				empleadoPuestoLista.add(empleadoPuesto651);
			}

		}
	}
	public void calAntiguedad(){
		String sql = "select sum(e.fecha_fin - e.fecha_inicio) as dias "
			+ "from general.empleado_puesto e " + "where e.id_persona = "
			+ idPersona + "and e.incide_antiguedad is true "
			+ "and e.fecha_inicio is not null and e.fecha_fin is not null";
		Object cantdias = (Object) em.createNativeQuery(sql).getSingleResult();
		
		String sql2 = "select sum(current_date- e.fecha_inicio) as dias "
			+ "from general.empleado_puesto e " + "where e.id_persona = "
			+ idPersona + "and e.actual is true "
			+ "and e.fecha_inicio is not null and e.fecha_fin is null";
		Object cantdias2 = (Object) em.createNativeQuery(sql2).getSingleResult();
		Integer anhos = 0;
		Integer meses = 0;
		Integer dias = 0;
		Integer diferencia = 0;
		Integer diferencia2 = 0;
		if (cantdias != null)
			diferencia = new Integer(cantdias.toString());
		if (cantdias2 != null)
			diferencia2 = new Integer(cantdias2.toString());
		dias = diferencia + diferencia2;
		while (dias >= 365) {
			anhos = dias / 365;
			dias -= (365 * anhos);
			
		} 
		while (dias >= 30) {
			meses = dias / 30;
			dias -= (30 * meses);
	
		}
		antiguedad = "";
		if (anhos == 1)
			antiguedad += anhos +" año ";
		else 
				antiguedad += anhos +" años ";
		
		if (meses == 1)
			antiguedad += meses +" mes ";
		else
			antiguedad += meses +" meses ";
		
		if (dias == 1)
			antiguedad += dias +" dia ";
		else 
			antiguedad += dias +" dias ";
	}
	
	public List<SelectItem> updateTipoDocSelectItems() {
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
		for (DatosEspecificos o : datosEspecificosByTipoDocumento())
			si.add(new SelectItem(o.getIdDatosEspecificos(), "" + o.getDescripcion()));
		return si;
	}

	public List<SelectItem> updateTipoEventosSelectItems() {
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
				
		for (DatosEspecificos o : datosEspecificosByTipoEventos())
			si.add(new SelectItem(o.getIdDatosEspecificos(), "" + o.getDescripcion()));
		return si;
	}
	
	
	@SuppressWarnings("unchecked")
	public List<DatosEspecificos> datosEspecificosByTipoDocumento() {
		try {
			List<DatosEspecificos> datosEspecificosLists =
				em.createQuery("Select d from DatosEspecificos d "
					+ " where d.datosGenerales.descripcion like 'TIPOS DE DOCUMENTOS' and ((d.descripcion like 'RESOLUCION'" +
							" and d.valorAlf like 'RES') or (d.descripcion like 'DECRETO' and d.valorAlf like 'DEC')" +
							"or (d.descripcion like 'CONTRATO' and d.valorAlf like 'CON'))" +
							" or (d.descripcion like 'OTROS' and d.valorAlf like 'FPOC')) " +
							"and d.activo=true order by d.descripcion").getResultList();

			return datosEspecificosLists;
		} catch (Exception ex) {
			return new Vector<DatosEspecificos>();
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<DatosEspecificos> datosEspecificosByTipoEventos() {
		try {
			String sql = "select * from seleccion.datos_especificos where id_datos_generales = "
					+ "(select id_datos_generales from seleccion.datos_generales where descripcion = 'TIPO DE EVENTO RECORRIDO LABORAL') and activo is true order by descripcion";
			List<DatosEspecificos> datosEspecificosLists =
				em.createNativeQuery(sql,DatosEspecificos.class).getResultList();

			return datosEspecificosLists;
		} catch (Exception ex) {
			return new Vector<DatosEspecificos>();
		}
	}
	
	
	
	@SuppressWarnings("unchecked")
	public List<DatosEspecificos> getTipoVinculaciones() {
		try {
			
			 String sql = "Select * from seleccion.datos_especificos dato_e " +
					" join seleccion.datos_generales dato_g on ( dato_e.id_datos_generales = dato_g.id_datos_generales)" +
					" where dato_g.descripcion='ESTADO EMPLEADO PUESTO' and (dato_e.descripcion='CONTRATADO' OR dato_e.descripcion='PASANTE' " +
					" OR dato_e.descripcion='PERMANENTE' OR dato_e.descripcion='COMISIONADO') and dato_e.activo=true order by dato_e.descripcion";
		
			List<DatosEspecificos> especificos = em.createNativeQuery(sql, DatosEspecificos.class).getResultList();
			

			return especificos;
		} catch (Exception ex) {
			return new Vector<DatosEspecificos>();
		}
	}

	public List<SelectItem> getTipoVinculacionSelectItems() {
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
		for (DatosEspecificos obj : getTipoVinculaciones())
			si.add(new SelectItem(obj.getIdDatosEspecificos(), ""
					+ obj.getDescripcion()));
		return si;
	}
	

	public void addRow(){
		try {
			if(!toDetailOk()){
				return;
			}	
			/**
			 * Para el Caso de documento adjuntos
			 * */
			Documentos documentos=new Documentos();
			if(uploadedFile!=null){
				idDoc=guardarAdjuntos(fileName, uploadedFile.length, contentType, uploadedFile, idTipoDoc, idDocAnterior);
				if(idDoc!=null){
					documentos=em.find(Documentos.class, idDoc);
					documentos.setNroDocumento(actoAdmin);
					documentos.setFechaDoc(fecActo);
					documentos.setDescripcion(descTipoDoc);
					documentos.setPersona(persona);
					em.merge(documentos);
					
				}else 
					return ;
			}else{
				if(idDocAnterior!=null){
				documentos=em.find(Documentos.class, idDocAnterior);
				documentos.setNroDocumento(actoAdmin);
				documentos.setFechaDoc(fecActo);
				documentos.setDatosEspecificos(em.find(DatosEspecificos.class,
						idTipoDoc));
				documentos.setPersona(persona);
				documentos.setFechaMod(new Date());
				documentos.setUsuMod(usuarioLogueado.getCodigoUsuario());
				if(esOtro)
				documentos.setDescripcion(descTipoDoc);
				em.merge(documentos);
				}
			}
			empleadoPuestoAdd.setPersona(persona);
			
			DatosEspecificos datos;
			if(categoria==null || categoria.equals("")){
				categoria= "NA";
			}
			if(objCodigo==null){
				objCodigo=0;
			}
			
			if(empleadoConceptoPagoAdd.getMonto()==null){
				empleadoConceptoPagoAdd.setMonto(0);
			}
			Date date = empleadoPuestoAdd.getFechaInicio(); // obtener anho de fechaInicio
			Calendar cal = Calendar.getInstance();
		    cal.setTime(date);
		    int year = cal.get(Calendar.YEAR);
		    
			if(empleadoPuestoAdd.getIdEmpleadoPuesto()==null){
				empleadoPuestoAdd.setFechaAlta(new Date());
				empleadoPuestoAdd.setActual(false);
				empleadoPuestoAdd.setActivo(true);
				empleadoPuestoAdd.setUsuAlta(usuarioLogueado.getCodigoUsuario());
				empleadoPuestoAdd.setCategoria(categoria);
				
				if(idEstado != null){
					  datos = em.find(DatosEspecificos.class, idEstado);
					if(datos.getDescripcion().equals("CONTRATADO")){
						empleadoPuestoAdd.setContratado(true);
					}
					else{
						empleadoPuestoAdd.setContratado(false);
					}
					//SE AGREGA PARA MANEJAR LOS ESTADOS DE COMISIONADO Y PASANTE
					empleadoPuestoAdd.setDatosEspecificosByIdDatosEspEstado(datos);
				}
				empleadoPuestoAdd.setDatosEspecificosByIdDatosEspTipoIngresoMovilidad(obtenerTipoMovilidad());
				em.persist(empleadoPuestoAdd);
				
				
			    
				empleadoConceptoPagoAdd.setEmpleadoPuesto(empleadoPuestoAdd);
				empleadoConceptoPagoAdd.setUsuAlta(usuarioLogueado.getCodigoUsuario());
				empleadoConceptoPagoAdd.setAnho(year); //setea el anho de fechaInicio
				empleadoConceptoPagoAdd.setFechaAlta(new Date());
				empleadoConceptoPagoAdd.setCategoria(categoria);
				empleadoConceptoPagoAdd.setObjCodigo(objCodigo);
				em.persist(empleadoConceptoPagoAdd);
			}else{
				empleadoPuestoAdd.setFechaMod(new Date());
				empleadoPuestoAdd.setUsuMod(usuarioLogueado.getCodigoUsuario());
				empleadoPuestoAdd.setCategoria(categoria);
				if(idEstado != null){
					  datos = em.find(DatosEspecificos.class, idEstado);
					if(datos.getDescripcion().equals("CONTRATADO")){
						empleadoPuestoAdd.setContratado(true);
					}
					else{
						empleadoPuestoAdd.setContratado(false);
					}
					//SE AGREGA PARA MANEJAR LOS ESTADOS DE COMISIONADO Y PASANTE
					empleadoPuestoAdd.setDatosEspecificosByIdDatosEspEstado(datos);
				}
				empleadoPuestoAdd.setDatosEspecificosByIdDatosEspTipoIngresoMovilidad(obtenerTipoMovilidad());
				em.merge(empleadoPuestoAdd);
				
				empleadoConceptoPagoAdd.setFechaMod(new Date());
				empleadoConceptoPagoAdd.setUsuMod(usuarioLogueado.getCodigoUsuario());
				empleadoConceptoPagoAdd.setAnho(year); //setea el anho de fechaInicio
				empleadoConceptoPagoAdd.setCategoria(categoria);
				empleadoConceptoPagoAdd.setObjCodigo(objCodigo);
				em.persist(empleadoConceptoPagoAdd);
				
			}
			
			if(idDoc!=null)
			{
				documentos=em.find(Documentos.class, idDoc);
				documentos.setIdTabla(empleadoPuestoAdd.getIdEmpleadoPuesto());
				em.merge(documentos);
				insertarRerAdjunto(idDoc,empleadoPuestoAdd.getIdEmpleadoPuesto());
			}
			
			em.flush();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
			limpiar();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
	}

	
	private Long  guardarAdjuntos(String fileName, int fileSize, String contentType, byte[] file, Long idTipoDoc, Long idDocumento) {
		try {
			
			UploadItem fileItem =
				seleccionUtilFormController.crearUploadItem(fileName, fileSize, contentType, file);
			Long idDocuGenerado;
			String nombreTabla = "EmpleadoPuesto";
			String nombrePantalla = "AdmRecorridoLaboral";
			String sf=File.separator;
			String direccionFisica = sf+"LEGAJO"+sf+persona.getDocumentoIdentidad()+"_"+persona.getIdPersona()+sf+"RECORRIDO_LABORAL"+sf;
			
			idDocuGenerado =
				admDocAdjuntoFormController.guardarDoc(fileItem, direccionFisica, nombrePantalla, idTipoDoc, nombreTabla, idDocumento);
			return idDocuGenerado;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	public void editar(int index){
		setearDatos();
		EmpleadoPuesto651 puesto651=empleadoPuestoLista.get(index);
		empleadoPuestoAdd= new EmpleadoPuesto();
		if(puesto651.getId()!=null){
			empleadoPuestoAdd= em.find(EmpleadoPuesto.class, puesto651.getId());
			empleadoConceptoPagoAdd = encontrarConceptoPago(puesto651.getId());
			if((empleadoConceptoPagoAdd != null) && (empleadoPuestoAdd != null)){
				objCodigo = empleadoConceptoPagoAdd.getObjCodigo();
				categoria = empleadoPuestoAdd.getCategoria();
//				if(empleadoPuestoAdd.getContratado())
//					vinculacion = "CONTRATADO";
//				else
//					vinculacion = "PERMANENTE";
				if(empleadoPuestoAdd.getDatosEspecificosByIdDatosEspEstado() != null){
					idEstado = empleadoPuestoAdd.getDatosEspecificosByIdDatosEspEstado().getIdDatosEspecificos();
					vinculacion = empleadoPuestoAdd.getDatosEspecificosByIdDatosEspEstado().getDescripcion();
				}
			}
			Documentos doc= obtenerDocumento(puesto651.getId());
			if(doc!=null){
				idDocAnterior=doc.getIdDocumento();
				nombreDoc=doc.getNombreFisicoDoc();
				idTipoDoc=doc.getDatosEspecificos().getIdDatosEspecificos();
				fecActo=doc.getFechaDoc();
				actoAdmin=doc.getNroDocumento();
				descTipoDoc=doc.getDescripcion();
			}
		}else{
			empleadoPuestoAdd.setDescOeeHistorico(puesto651.getOee());
			empleadoPuestoAdd.setFechaInicio(puesto651.getFechaInicio());
			empleadoPuestoAdd.setFechaFin(puesto651.getFechaFin());
			empleadoPuestoAdd.setDescPuestoHistorico(puesto651.getPuesto());
			
		}
		ver = false;
	}
	public void ver(int  index){
		setearDatos();
		EmpleadoPuesto651 puesto651=empleadoPuestoLista.get(index);
		empleadoPuestoAdd= new EmpleadoPuesto();
		if(puesto651.getId()!=null){
			empleadoPuestoAdd= em.find(EmpleadoPuesto.class, puesto651.getId());
			empleadoConceptoPagoAdd = encontrarConceptoPago(puesto651.getId());
			if((empleadoConceptoPagoAdd != null) && (empleadoPuestoAdd != null)){
				objCodigo = empleadoConceptoPagoAdd.getObjCodigo();
				categoria = empleadoPuestoAdd.getCategoria();
//				if(empleadoPuestoAdd.getContratado())
//					vinculacion = "CONTRATADO";
//				else
//					vinculacion = "PERMANENTE";
				if(empleadoPuestoAdd.getDatosEspecificosByIdDatosEspEstado() != null)
					vinculacion = empleadoPuestoAdd.getDatosEspecificosByIdDatosEspEstado().getDescripcion();
			}
			Documentos doc= obtenerDocumento(puesto651.getId());
			if(doc!=null){
				idDocAnterior=doc.getIdDocumento();
				nombreDoc=doc.getNombreFisicoDoc();
				idTipoDoc=doc.getDatosEspecificos().getIdDatosEspecificos();
				fecActo=doc.getFechaDoc();
				actoAdmin=doc.getNroDocumento();
				descTipoDoc=doc.getDescripcion();
			}
		}else{
			empleadoPuestoAdd.setDescOeeHistorico(puesto651.getOee());
			empleadoPuestoAdd.setFechaInicio(puesto651.getFechaInicio());
			empleadoPuestoAdd.setFechaFin(puesto651.getFechaFin());
			empleadoPuestoAdd.setDescPuestoHistorico(puesto651.getPuesto());
			
		}
		
		ver=true;
		
	}
	
	
	
	
	
	public EmpleadoConceptoPago encontrarConceptoPago(Long empleadoPuestoId) {

		String sql = "select * from general.empleado_concepto_pago conceppago where conceppago.id_empleado_puesto = "+empleadoPuestoId.intValue();		
		Query q = em.createNativeQuery(sql, EmpleadoConceptoPago.class);
		List<EmpleadoConceptoPago> lista = q.getResultList();
		if(lista.size()>0)
			return lista.get(0);
		else
			return null;
		
	}
	
	private boolean toDetailOk(){
		if(empleadoPuestoAdd.getDescOeeHistorico()==null || empleadoPuestoAdd.getDescOeeHistorico().trim().equals("")){
			statusMessages.add(Severity.ERROR,"Debe Ingresar el campo Institucion antes de realizar la operacion, verifique");
			return false;
		}
		
		if(empleadoPuestoAdd.getFechaInicio()==null ){
			statusMessages.add(Severity.ERROR,"Debe Ingresar el campo Fecha Inicio antes de realizar la operacion, verifique");
			return false;
		}
		/*if(empleadoPuestoAdd.getFechaFin()==null ){
			statusMessages.add(Severity.ERROR,"Debe Ingresar el campo Fecha Inicio antes de realizar la operacion, verifique");
			return false;
		}*/
		if(empleadoPuestoAdd.getFechaFin()!=null){
			if(empleadoPuestoAdd.getFechaInicio().after(empleadoPuestoAdd.getFechaFin()) ){
				statusMessages.add(Severity.ERROR,"La Fecha Inicio no puede ser mayor a la Fecha Fin, verifique");
				return false;
			}
			if (!General.isFechaValida(empleadoPuestoAdd.getFechaFin())) {
				statusMessages.clear();
				statusMessages.add(Severity.ERROR, "Fecha Fin inválida. Verifique");
				return false;
			}
		}
		if (!General.isFechaValida(empleadoPuestoAdd.getFechaInicio())) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, "Fecha Inicio inválida. Verifique");
			return false;
		}
		if(actoAdmin==null ){
			statusMessages.add(Severity.ERROR,"Debe Ingresar el campo Acto Administrativo antes de realizar la operacion, verifique");
			return false;
		}
		if(actoAdmin.longValue()<0 ){
			statusMessages.add(Severity.ERROR,"El campo Acto Administrativo  debe ser mayor a cero, verifique");
			return false;
		}
		if(empleadoPuestoAdd.getDescPuestoHistorico()==null || empleadoPuestoAdd.getDescPuestoHistorico().trim().equals("")){
			statusMessages.add(Severity.ERROR,"Debe Ingresar el campo Descripcion Puesto o Cargo antes de realizar la operacion, verifique");
			return false;
		}
		if(idTipoDoc==null){
			statusMessages.add(Severity.ERROR,"Debe Seleccionar el Campo Tipo de Documento antes de realizar la operacion, verifique");
			return false;
		}
		if(uploadedFile==null && idDocAnterior==null){
			statusMessages.add(Severity.ERROR,"Debe Seleccionar el Archivo, verifique");
			return false;
		}
		
		if(!General.isFechaValida(fecActo)){
				statusMessages.clear();
				statusMessages.add(Severity.ERROR, "Fecha inválida. Verifique");
				return false;
		}
		
		if(empleadoPuestoAdd.getEncabezadoActoAdministrativo()==null || empleadoPuestoAdd.getEncabezadoActoAdministrativo().equals("")){
			statusMessages.add(Severity.ERROR,"Debe Ingresar el campo de Encabezado de Acto Administrativo antes de realizar la operacion, verifique");
			return false;
		}
		
		if(empleadoPuestoAdd.getDescripcionFunciones().equals("")){
			empleadoPuestoAdd.setDescripcionFunciones(null);
		}
		
		if(idEstado==null){
			statusMessages.add(Severity.ERROR,"Debe Seleccionar el Tipo de Vinculación, verifique");
			return false;
		}
		return true;
		
	}
	
	public void setearDatos(){
		nombreDoc=null;
		idDoc=null;
		uploadedFile=null;
		contentType=null;
		fileName=null;
		idTipoDoc=null;
		idEstado=null;
		vinculacion=null;
		ingresoPor=null;
		situacion=null;
		antiguedad=null;
		categoria=null;
		objCodigo=null;
		limpiar();
		
	}
	public void limpiar(){
		empleadoPuestoLista= new ArrayList<EmpleadoPuesto651>();
		empleadoPuestoAdd= new EmpleadoPuesto();
		empleadoPuestoAdd.setFechaInicio(null);
		empleadoPuestoAdd.setFechaFin(null);
		nombreDoc=null;
		idTipoDoc=null;
		descTipoDoc=null;
		uploadedFile=null;
		contentType=null;
		fecActo=null;
		actoAdmin=null;
		idDoc=null;
		idDocAnterior=null;
		ver=false;
		empleadoConceptoPagoAdd= new EmpleadoConceptoPago();
		categoria=null;
		objCodigo=null;
		idEstado=null;
		vinculacion=null;
		cargarEmpleado();
	}

	
	public void abrirDoc(Long id){
		
		Documentos doc=obtenerDocumento(id);
		if(doc==null)
		{
			statusMessages.add(Severity.ERROR,"Documento no encontrado");
			return;
		}
		AdmDocAdjuntoFormController.abrirDocumentoFromCU(doc.getIdDocumento(), usuarioLogueado.getIdUsuario());
		
	}
	
	public void deleteRow(int index){
		EmpleadoPuesto empleadoPuesto=em.find(EmpleadoPuesto.class, empleadoPuestoLista.get(index).getId());
		empleadoPuesto.setFechaMod(new Date());
		empleadoPuesto.setUsuMod(usuarioLogueado.getCodigoUsuario());
		empleadoPuesto.setActivo(false);
		em.merge(empleadoPuesto);
		Documentos doc=obtenerDocumento(empleadoPuesto.getIdEmpleadoPuesto());
		boolean delete=true;
		if(doc!=null){
			delete=borrarDoc(doc.getIdDocumento());
		}
		if(!delete)
		{
			statusMessages.add(Severity.ERROR,"No se puede eliminar el registro esta en uso verifique");
			return;
		}
		
		em.flush();
		statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
		cargarEmpleado();
	}
	
	


	
	/**
	 * Insertar registro en tabla REFERENCIA_ADJUNTOS para el acto administrativo
	 * */
	private void insertarRerAdjunto(Long idDocumento,Long id){
		ReferenciaAdjuntos referenciaAdjuntos= new ReferenciaAdjuntos();
		referenciaAdjuntos.setPersona(persona);
		referenciaAdjuntos.setDocumentos(new Documentos());
		referenciaAdjuntos.getDocumentos().setIdDocumento(idDocumento);
		referenciaAdjuntos.setIdRegistroTabla(id);
		referenciaAdjuntos.setUsuAlta(usuarioLogueado.getCodigoUsuario());
		referenciaAdjuntos.setFechaAlta(new Date());
		em.persist(referenciaAdjuntos);
	}
	
	private Documentos obtenerDocumento(Long idEmpleadoPuesto){
		try {
			ReferenciaAdjuntos doc =(ReferenciaAdjuntos)em.createQuery("Select d from ReferenciaAdjuntos d join d.documentos doc " +
					" where doc.activo is true and d.idRegistroTabla=:idEmpleadoPuesto").setParameter("idEmpleadoPuesto", idEmpleadoPuesto).getSingleResult();
			return em.find(Documentos.class, doc.getDocumentos().getIdDocumento());
			
		} catch (NoResultException e) {
			return null;
		}
	}
	public boolean borrarDoc(Long idDoc){
		ManagedPersistenceContext persistenceContext = (ManagedPersistenceContext)Contexts.getConversationContext().get("entityManager");
		EntityManager em;
		try {
		
			em = persistenceContext.getEntityManager();
			Documentos doc=em.find(Documentos.class,idDoc);
			doc.setActivo(false);
			doc.setFechaMod(new Date());
			doc.setUsuMod(usuarioLogueado.getCodigoUsuario());
			em.merge(doc);
			em.flush();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} 
	}
	
	public DatosEspecificos obtenerTipoMovilidad(){
	String sql = " SELECT espec.* "+ 
			" FROM seleccion.datos_generales gral"+ 
			" INNER JOIN seleccion.datos_especificos espec ON gral.id_datos_generales = espec.id_datos_generales"+
			" WHERE gral.descripcion = 'TIPOS DE INGRESOS Y MOVILIDAD' AND espec.descripcion LIKE 'HISTORICO DE PUESTOS'";
	DatosEspecificos l = (DatosEspecificos)	em.createNativeQuery(sql, DatosEspecificos.class).getSingleResult();
	return l;
	}
//	GETTERS Y SETTERS

	public Long getIdPersona() {
		return idPersona;
	}

	public void setIdPersona(Long idPersona) {
		this.idPersona = idPersona;
	}

	public Persona getPersona() {
		return persona;
	}

	public void setPersona(Persona persona) {
		this.persona = persona;
	}

	


	public String getNombreDoc() {
		return nombreDoc;
	}


	public void setNombreDoc(String nombreDoc) {
		this.nombreDoc = nombreDoc;
	}


	public Long getIdDoc() {
		return idDoc;
	}


	public void setIdDoc(Long idDoc) {
		this.idDoc = idDoc;
	}


	public byte[] getUploadedFile() {
		return uploadedFile;
	}


	public void setUploadedFile(byte[] uploadedFile) {
		this.uploadedFile = uploadedFile;
	}


	public String getContentType() {
		return contentType;
	}


	public void setContentType(String contentType) {
		this.contentType = contentType;
	}


	public String getFileName() {
		return fileName;
	}


	public void setFileName(String fileName) {
		this.fileName = fileName;
	}


	public Long getIdTipoDoc() {
		return idTipoDoc;
	}


	public void setIdTipoDoc(Long idTipoDoc) {
		this.idTipoDoc = idTipoDoc;
	}


	public EmpleadoPuesto getEmpleadoPuesto() {
		return empleadoPuesto;
	}


	public void setEmpleadoPuesto(EmpleadoPuesto empleadoPuesto) {
		this.empleadoPuesto = empleadoPuesto;
	}


	public String getVinculacion() {
		return vinculacion;
	}
	public void setVinculacion(String vinculacion) {
		this.vinculacion = vinculacion;
	}
	public String getSituacion() {
		return situacion;
	}


	public void setSituacion(String situacion) {
		this.situacion = situacion;
	}


	public String getIngresoPor() {
		return ingresoPor;
	}


	public void setIngresoPor(String ingresoPor) {
		this.ingresoPor = ingresoPor;
	}


	public String getPuesto() {
		return puesto;
	}


	public void setPuesto(String puesto) {
		this.puesto = puesto;
	}


	public String getAntiguedad() {
		return antiguedad;
	}


	public void setAntiguedad(String antiguedad) {
		this.antiguedad = antiguedad;
	}


	public List<EmpleadoConceptoPago> getEmpleadoConceptoPagos() {
		return empleadoConceptoPagos;
	}


	public void setEmpleadoConceptoPagos(
			List<EmpleadoConceptoPago> empleadoConceptoPagos) {
		this.empleadoConceptoPagos = empleadoConceptoPagos;
	}


	public List<Remuneraciones> getRemuneraciones() {
		return remuneraciones;
	}


	public void setRemuneraciones(List<Remuneraciones> remuneraciones) {
		this.remuneraciones = remuneraciones;
	}





	public List<EmpleadoPuesto651> getEmpleadoPuestoLista() {
		return empleadoPuestoLista;
	}


	public void setEmpleadoPuestoLista(List<EmpleadoPuesto651> empleadoPuestoLista) {
		this.empleadoPuestoLista = empleadoPuestoLista;
	}


	public Date getFecActo() {
		return fecActo;
	}


	public void setFecActo(Date fecActo) {
		this.fecActo = fecActo;
	}


	

	public Integer getActoAdmin() {
		return actoAdmin;
	}


	public void setActoAdmin(Integer actoAdmin) {
		this.actoAdmin = actoAdmin;
	}


	public EmpleadoPuesto getEmpleadoPuestoAdd() {
		return empleadoPuestoAdd;
	}


	public void setEmpleadoPuestoAdd(EmpleadoPuesto empleadoPuestoAdd) {
		this.empleadoPuestoAdd = empleadoPuestoAdd;
	}


	public Long getIdDocAnterior() {
		return idDocAnterior;
	}


	public void setIdDocAnterior(Long idDocAnterior) {
		this.idDocAnterior = idDocAnterior;
	}


	public String getTexto() {
		return texto;
	}


	public void setTexto(String texto) {
		this.texto = texto;
	}


	public Integer getUltimo1() {
		return ultimo1;
	}


	public void setUltimo1(Integer ultimo1) {
		this.ultimo1 = ultimo1;
	}


	public Integer getUltimo2() {
		return ultimo2;
	}


	public void setUltimo2(Integer ultimo2) {
		this.ultimo2 = ultimo2;
	}


	public Integer getValorTotal() {
		return valorTotal;
	}


	public void setValorTotal(Integer valorTotal) {
		this.valorTotal = valorTotal;
	}


	public boolean isVer() {
		return ver;
	}


	public void setVer(boolean ver) {
		this.ver = ver;
	}
	
	public EmpleadoConceptoPago getEmpleadoConceptoPagoAdd() {
		return empleadoConceptoPagoAdd;
	}

	public void setEmpleadoConceptoPagoAdd(EmpleadoConceptoPago empleadoConceptoPago) {
		this.empleadoConceptoPagoAdd = empleadoConceptoPagoAdd;
	}
	
	public String getCategoria() {
		return categoria;
	}


	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}
	
	public Integer getObjCodigo() {
		return objCodigo;
	}


	public void setObjCodigo(Integer objCodigo) {
		this.objCodigo = objCodigo;
	}

	public String getDescTipoDoc() {
		return descTipoDoc;
	}

	public void setDescTipoDoc(String descTipoDoc) {
		this.descTipoDoc = descTipoDoc;
	}
	
	public Boolean getEsOtro(){
		return esOtro;
	}
	
	public void setEsOtro(Boolean esOtro){
		this.esOtro = esOtro;
	}
	
	public void esOtroTipo() {
		if(idTipoDoc != null){
			DatosEspecificos datos= em.find(DatosEspecificos.class, idTipoDoc);
			if(datos.getDescripcion().equalsIgnoreCase("OTROS")){
				esOtro=true;
			} else {
				esOtro=false;
			}
		}
	}
	public void esOtroTipoEven() {
		if(idTipoEventoDatosEspecificos != null){
			DatosEspecificos datos= em.find(DatosEspecificos.class, idTipoEventoDatosEspecificos);
			if(datos.getDescripcion().equalsIgnoreCase("OTROS")){
				esOtroTipo=true;
			} else {
				esOtroTipo=false;
			}
		}
	}
	public List<RecorridoLegajoMigrado> getRecorriloLaboralMigradoList() {
		return recorriloLaboralMigradoList;
	}


	public void setRecorriloLaboralMigradoList(
			List<RecorridoLegajoMigrado> recorriloLaboralMigradoList) {
		this.recorriloLaboralMigradoList = recorriloLaboralMigradoList;
	}
	
	
	//EVENTOS 11/04/2016 
	
	
	public void initEvento(){
		eventos = new ArrayList();
		CargarEvento(indiceEvento);		
		
	}
	
	public void CargarEvento(int index){
		
		
		//RECUPERA DE LA LISTA EL REGISTRO PARA MOSTRARLO EN EL ENCABEZADO EL MODAL
		EmpleadoPuesto651 puesto651=empleadoPuestoLista.get(index);
		empleadoPuestoEvento= new EmpleadoPuesto();
		if(puesto651.getId()!=null){
			empleadoPuestoEvento= em.find(EmpleadoPuesto.class, puesto651.getId());
			if(empleadoPuestoEvento.getPlantaCargoDet() != null && empleadoPuestoEvento.getPlantaCargoDet().getIdPlantaCargoDet() != null)
				plantaCargoDetEvento = em.find(PlantaCargoDet.class, empleadoPuestoEvento.getPlantaCargoDet().getIdPlantaCargoDet());
			
			if(plantaCargoDetEvento.getConfiguracionUoDet() != null && plantaCargoDetEvento.getConfiguracionUoDet().getIdConfiguracionUoDet() != null)
				configuracionUoDetEvento = em.find(ConfiguracionUoDet.class, plantaCargoDetEvento.getConfiguracionUoDet().getIdConfiguracionUoDet());
			
			if(configuracionUoDetEvento.getConfiguracionUoCab() != null && configuracionUoDetEvento.getConfiguracionUoCab().getIdConfiguracionUo() != null)
				configuracionUoCabEvento = em.find(ConfiguracionUoCab.class, configuracionUoDetEvento.getConfiguracionUoCab().getIdConfiguracionUo());
		}
		
		eventoAdd = new EventoRecorridoLaboral();
		
		
		eventos = cargarEventos(empleadoPuestoEvento.getIdEmpleadoPuesto());
		
	}
	
	private List<EventoRecorridoLaboral> cargarEventos(Long idEmpleadoPuesto){
		String sqlEventos = "select * from legajo.eventos_recorrido_laboral "
				+ " where activo =  true and id_empleado_puesto = "
				+ idEmpleadoPuesto + "order by fecha_acto_administrativo desc ";
		
		return em.createNativeQuery(sqlEventos,EventoRecorridoLaboral.class).getResultList();
	}
	
	
	public boolean mostrarLinkEventos(){
		return true;
	}
	
	public void volver(){
		eventos =  new ArrayList();
		eventoAdd = new EventoRecorridoLaboral();
	}
	
	public void deleteRowEventos(int index){
		EventoRecorridoLaboral eventoDelete =em.find(EventoRecorridoLaboral.class, eventos.get(index).getIdEventoRecorridoLaboral());
		eventoDelete.setFechaMod(new Date());
		eventoDelete.setUsuMod(usuarioLogueado.getCodigoUsuario());
		eventoDelete.setActivo(false);
		em.merge(eventoDelete);
		Documentos doc= eventoDelete.getDocumento();
		boolean delete=true;
		if(doc!=null){
			delete=borrarDoc(doc.getIdDocumento());
		}
		if(!delete)
		{
			statusMessages.add(Severity.ERROR,"No se puede eliminar el registro esta en uso verifique");
			return;
		}
		
		em.flush();
		statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
		eventos = cargarEventos(eventoDelete.getEmpleadoPuesto().getIdEmpleadoPuesto());
	}
	
	
	
	public void editarEventos(int index){
		
		EventoRecorridoLaboral eventoEdit =eventos.get(index);
		eventoAdd= new EventoRecorridoLaboral();
		if(eventoEdit.getIdEventoRecorridoLaboral()!=null){
			eventoAdd= em.find(EventoRecorridoLaboral.class, eventoEdit.getIdEventoRecorridoLaboral());
			this.idTipoEventoDatosEspecificos = eventoAdd.getTipoEventoDatosEspecificos().getIdDatosEspecificos();
			this.idTipoDoc = eventoAdd.getDocumento().getDatosEspecificos().getIdDatosEspecificos();
			this.nroActoAdministrativo = eventoAdd.getNroActoAdministrativo();
			this.encabezado = eventoAdd.getEncabezado();
			this.observaciones = eventoAdd.getObservaciones();
			this.fechaActoAdministrativo = eventoAdd.getFechaActoAdministrativo();	
			this.nombreDoc = eventoAdd.getDocumento().getNombreFisicoDoc();
			this.idDocAnterior=eventoAdd.getDocumento().getIdDocumento();
			this.descTipoEvento=eventoAdd.getDescTipoEvento();
			this.descTipoDoc=eventoAdd.getDocumento().getDescripcion();
			
			
		}
	}
	
	public void addRowEvento(){
		try {
			if(!toDetailEventoOk()){
				return;
			}	
			/**
			 * Para el Caso de documento adjuntos
			 * */
			Documentos documentos = new Documentos();
			if(uploadedFile!=null){
				idDoc=guardarDocumentoEvento(fileName, uploadedFile.length, contentType, uploadedFile, idTipoDoc, idDocAnterior);
				if(idDoc!=null){
					documentos=em.find(Documentos.class, idDoc);
					documentos.setNroDocumento(nroActoAdministrativo);
					documentos.setFechaDoc(fechaActoAdministrativo);
					if(esOtro)
					documentos.setDescripcion(descTipoDoc);
					em.merge(documentos);
				}else 
					return;
			}else{
				if(idDocAnterior!=null){
				documentos=em.find(Documentos.class, idDocAnterior);
				documentos.setNroDocumento(nroActoAdministrativo);
				documentos.setFechaDoc(fechaActoAdministrativo);
				documentos.setDatosEspecificos(em.find(DatosEspecificos.class,
						idTipoDoc));
				if(esOtro)
				documentos.setDescripcion(descTipoDoc);
				em.merge(documentos);
				}
			}
			
			eventoAdd.setNroActoAdministrativo(nroActoAdministrativo);
			eventoAdd.setFechaActoAdministrativo(fechaActoAdministrativo);
			eventoAdd.setEncabezado(encabezado);
			eventoAdd.setObservaciones(observaciones);
			eventoAdd.setEmpleadoPuesto(this.empleadoPuestoEvento);
			if(esOtroTipo)
				eventoAdd.setDescTipoEvento(descTipoEvento);
			if(eventoAdd.getIdEventoRecorridoLaboral()==null){
				eventoAdd.setFechaAlta(new Date());
				eventoAdd.setActivo(true);
				eventoAdd.setUsuAlta(usuarioLogueado.getCodigoUsuario());
				
				DatosEspecificos tipoEventoDatosEspecificos;
				if(idTipoEventoDatosEspecificos != null){
					tipoEventoDatosEspecificos = em.find(DatosEspecificos.class,(idTipoEventoDatosEspecificos));
					eventoAdd.setTipoEventoDatosEspecificos(tipoEventoDatosEspecificos);
				}
				
				documentos = new Documentos();
				if(idDoc!=null){
					documentos=em.find(Documentos.class, idDoc);
				}
				
				
				eventoAdd.setDocumento(documentos);
				em.persist(eventoAdd);
								
			}else{
				
				if(idDoc!=null){
					documentos = new Documentos();
					documentos=em.find(Documentos.class, idDoc);
					eventoAdd.setDocumento(documentos);
				}
				
				DatosEspecificos tipoEventoDatosEspecificos;
				if(idTipoEventoDatosEspecificos != null){
					tipoEventoDatosEspecificos = em.find(DatosEspecificos.class,(idTipoEventoDatosEspecificos));
					eventoAdd.setTipoEventoDatosEspecificos(tipoEventoDatosEspecificos);
				}
				
				eventoAdd.setFechaMod(new Date());
				eventoAdd.setUsuMod(usuarioLogueado.getCodigoUsuario());
				em.merge(eventoAdd);
			}
			
						
			em.flush();
			if (documentos != null){
				documentos.setIdTabla(eventoAdd.getIdEventoRecorridoLaboral());
				documentos.setPersona(persona);
				em.merge(documentos);
				em.flush();
			}
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
			
			this.limpiarEvento();
			this.eventos = cargarEventos(empleadoPuestoEvento.getIdEmpleadoPuesto());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
	}
	
	
	private boolean toDetailEventoOk(){
		if(idTipoEventoDatosEspecificos ==null){
			statusMessages.add(Severity.ERROR,"Debe seleccionar un Tipo de Evento, verifique");
			return false;
		}
		
		if(nroActoAdministrativo==null ){
			statusMessages.add(Severity.ERROR,"Debe Ingresar el campo Nro. Acto Administrativo antes de realizar la operacion, verifique");
			return false;
		}
		
		if(fechaActoAdministrativo==null ){
			statusMessages.add(Severity.ERROR,"Debe Ingresar el campo Fecha para el Acto Administrativo antes de realizar la operacion, verifique");
			return false;
		}
		/*if(empleadoPuestoAdd.getFechaFin()==null ){
			statusMessages.add(Severity.ERROR,"Debe Ingresar el campo Fecha Inicio antes de realizar la operacion, verifique");
			return false;
		}*/
		if(fechaActoAdministrativo !=null){
			
			if (!General.isFechaValida(fechaActoAdministrativo)) {
				statusMessages.clear();
				statusMessages.add(Severity.ERROR, "Fecha Fin inválida. Verifique");
				return false;
			}
		}
		
		
		if(uploadedFile==null && idDocAnterior==null){
			statusMessages.add(Severity.ERROR,"Debe Seleccionar el Archivo, verifique");
			return false;
		}
		
				
		if(encabezado==null || encabezado.equals("")){
			statusMessages.add(Severity.ERROR,"Debe Ingresar el campo de Encabezado de Acto Administrativo antes de realizar la operacion, verifique");
			return false;
		}
		
		if(observaciones.equals("")){
			observaciones = null;
		}
		
		return true;
		
	}
	
	private Long  guardarDocumentoEvento(String fileName, int fileSize, String contentType, byte[] file, Long idTipoDoc, Long idDocumento) {
		try {
			
			UploadItem fileItem =
				seleccionUtilFormController.crearUploadItem(fileName, fileSize, contentType, file);
			Long idDocuGenerado;
			String nombreTabla = "EventoRecorridoLaboral";
			String nombrePantalla = "EventoRecorridoLaboral";
			String sf=File.separator;
			String direccionFisica = sf+"LEGAJO"+sf+persona.getDocumentoIdentidad()+"_"+persona.getIdPersona()+sf+"RECORRIDO_LABORAL"+sf+"EVENTOS"+sf;
			
			idDocuGenerado =
				admDocAdjuntoFormController.guardarDoc(fileItem, direccionFisica, nombrePantalla, idTipoDoc, nombreTabla, idDocumento);
			return idDocuGenerado;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	
	public void limpiarEvento(){
		eventoAdd = new EventoRecorridoLaboral();
		nroActoAdministrativo = null;
		fechaActoAdministrativo = null;
		encabezado = "";
		observaciones = "";
		idTipoEventoDatosEspecificos =  null;
		idTipoDoc = null;
		nombreDoc = null;
		idDocAnterior = null;
		descTipoEvento = null;
		descTipoDoc = null;
	}
	
public void abrirDocEvento(Long id){
		
		Documentos doc= em.find(Documentos.class, id);
		if(doc==null)
		{
			statusMessages.add(Severity.ERROR,"Documento no encontrado");
			return;
		}
		AdmDocAdjuntoFormController.abrirDocumentoFromCU(doc.getIdDocumento(), usuarioLogueado.getIdUsuario());
		
	}



	public EmpleadoPuesto getEmpleadoPuestoEvento() {
		return empleadoPuestoEvento;
	}


	public void setEmpleadoPuestoEvento(EmpleadoPuesto empleadoPuestoEvento) {
		this.empleadoPuestoEvento = empleadoPuestoEvento;
	}
	
	public EventoRecorridoLaboral getEventoAdd() {
		return eventoAdd;
	}
	
	public void setEventoAdd(EventoRecorridoLaboral eventoAdd) {
		this.eventoAdd = eventoAdd;
	}
	
	public List<EventoRecorridoLaboral> getEventos() {
		return eventos;
	}
	
	public void setEventos(List<EventoRecorridoLaboral> eventos) {
		this.eventos = eventos;
	}
	public Long getIdTipoEventoDatosEspecificos() {
		return idTipoEventoDatosEspecificos;
	}
	public void setIdTipoEventoDatosEspecificos(Long idTipoEventoDatosEspecificos) {
		this.idTipoEventoDatosEspecificos = idTipoEventoDatosEspecificos;
	}
	public Integer getNroActoAdministrativo() {
		return nroActoAdministrativo;
	}
	public void setNroActoAdministrativo(Integer nroActoAdministrativo) {
		this.nroActoAdministrativo = nroActoAdministrativo;
	}
	public Date getFechaActoAdministrativo() {
		return fechaActoAdministrativo;
	}
	public void setFechaActoAdministrativo(Date fechaActoAdministrativo) {
		this.fechaActoAdministrativo = fechaActoAdministrativo;
	}
	public String getEncabezado() {
		return encabezado;
	}
	public void setEncabezado(String encabezado) {
		this.encabezado = encabezado;
	}
	public String getObservaciones() {
		return observaciones;
	}
	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}
	public int getIndiceEvento() {
		return indiceEvento;
	}
	public void setIndiceEvento(int indiceEvento) {
		this.indiceEvento = indiceEvento;
	}
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String getAccion() {
		return accion;
	}
	public void setAccion(String accion) {
		this.accion = accion;
	}
	public PlantaCargoDet getPlantaCargoDetEvento() {
		return plantaCargoDetEvento;
	}
	public void setPlantaCargoDetEvento(PlantaCargoDet plantaCargoDetEvento) {
		this.plantaCargoDetEvento = plantaCargoDetEvento;
	}
	public ConfiguracionUoCab getConfiguracionUoCabEvento() {
		return configuracionUoCabEvento;
	}
	public void setConfiguracionUoCabEvento(
			ConfiguracionUoCab configuracionUoCabEvento) {
		this.configuracionUoCabEvento = configuracionUoCabEvento;
	}
	public ConfiguracionUoDet getConfiguracionUoDetEvento() {
		return configuracionUoDetEvento;
	}
	public void setConfiguracionUoDetEvento(
			ConfiguracionUoDet configuracionUoDetEvento) {
		this.configuracionUoDetEvento = configuracionUoDetEvento;
	}
	public byte[] getUploadedFileEvento() {
		return uploadedFileEvento;
	}
	public void setUploadedFileEvento(byte[] uploadedFileEvento) {
		this.uploadedFileEvento = uploadedFileEvento;
	}
	public String getContentTypeEvento() {
		return contentTypeEvento;
	}
	public void setContentTypeEvento(String contentTypeEvento) {
		this.contentTypeEvento = contentTypeEvento;
	}
	public String getFileNameEvento() {
		return fileNameEvento;
	}
	public void setFileNameEvento(String fileNameEvento) {
		this.fileNameEvento = fileNameEvento;
	}
	public Boolean getEsOtroTipo() {
		return esOtroTipo;
	}
	public void setEsOtroTipo(Boolean esOtroTipo) {
		this.esOtroTipo = esOtroTipo;
	}
	public String getDescTipoEvento() {
		return descTipoEvento;
	}
	public void setDescTipoEvento(String descTipoEvento) {
		this.descTipoEvento = descTipoEvento;
	}
	public Long getIdEstado() {
		return idEstado;
	}
	public void setIdEstado(Long idEstado) {
		this.idEstado = idEstado;
	}
	
}
