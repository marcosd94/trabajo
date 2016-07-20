package py.com.excelsis.sicca.desvinculacion.session.form;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.io.FileUtils;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.richfaces.model.UploadItem;

import py.com.excelsis.sicca.entity.CondicionSegur;
import py.com.excelsis.sicca.entity.CondicionTrabajo;
import py.com.excelsis.sicca.entity.CondicionTrabajoEspecif;
import py.com.excelsis.sicca.entity.ConfiguracionUoDet;
import py.com.excelsis.sicca.entity.ContenidoFuncional;
import py.com.excelsis.sicca.entity.Cpt;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.DetCondicionSegur;
import py.com.excelsis.sicca.entity.DetCondicionTrabajo;
import py.com.excelsis.sicca.entity.DetCondicionTrabajoEspecif;
import py.com.excelsis.sicca.entity.DetContenidoFuncional;
import py.com.excelsis.sicca.entity.DetDescripcionContFuncional;
import py.com.excelsis.sicca.entity.DetMinimosRequeridos;
import py.com.excelsis.sicca.entity.DetOpcionesConvenientes;
import py.com.excelsis.sicca.entity.DetReqMin;
import py.com.excelsis.sicca.entity.DetTipoNombramiento;
import py.com.excelsis.sicca.entity.EmpleadoConceptoPago;
import py.com.excelsis.sicca.entity.EmpleadoPuesto;
import py.com.excelsis.sicca.entity.EstadoCab;
import py.com.excelsis.sicca.entity.Legajos;
import py.com.excelsis.sicca.entity.Pais;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.entity.RequisitoMinimoCpt;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.ConfiguracionUoCabList;
import py.com.excelsis.sicca.session.EmpleadoPuestoList;
import py.com.excelsis.sicca.session.EntidadList;
import py.com.excelsis.sicca.session.SinEntidadList;
import py.com.excelsis.sicca.session.SinNivelEntidadList;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.PersonaDTO;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.JasperReportUtils;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.SICCAAppHelper;

@Scope(ScopeType.CONVERSATION)
@Name("cargaMasivaFuncioFC")
public class CargaMasivaFuncioFC {
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
	@In(create = true)
	SeleccionUtilFormController seleccionUtilFormController;

	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;

	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	private List<SelectItem> cptSelecItem = new ArrayList<SelectItem>();
	private List<SelectItem> ptSelecItem = new ArrayList<SelectItem>();
	private Long idCptFromList;
	private Long idTipoCpt;
	private String codigoCpt;
	private Cpt cpt = new Cpt();
	private PlantaCargoDet pt = new PlantaCargoDet();
	private Long idCpt;
	public Long idPt;

	private byte[] uFile = null;
	private String cType = null;
	private String fName = null;
	List<String> lLineasArch;
	private Long idEstadoComisionado;
	private Long idEstadoContratado;
	private Long idEstadoPermanente;
	private Long idEstadoInsertMasiva;
	private Long idEstadoTipoRegistro;
	private Long idEstadoCabOcupado;
	private Long idEstado;//N
	private String puesto;//N

	public String getPuesto() {
		return puesto;
	}

	public void setPuesto(String puesto) {
		this.puesto = puesto;
	}

	public void init() {
		calIdEstados();
	}

	private void calIdEstados() {
		idEstadoComisionado =
			seleccionUtilFormController.traerDatosEspecificos("ESTADO EMPLEADO PUESTO", "COMISIONADO").getIdDatosEspecificos();
		idEstadoContratado =
			seleccionUtilFormController.traerDatosEspecificos("ESTADO EMPLEADO PUESTO", "CONTRATADO").getIdDatosEspecificos();
		idEstadoPermanente =
			seleccionUtilFormController.traerDatosEspecificos("ESTADO EMPLEADO PUESTO", "PERMANENTE").getIdDatosEspecificos();
		idEstadoInsertMasiva =
			seleccionUtilFormController.traerDatosEspecificos("TIPOS DE INGRESOS Y MOVILIDAD", "INGRESO POR INSERCION MASIVA").getIdDatosEspecificos();
		idEstadoTipoRegistro =
			seleccionUtilFormController.traerDatosEspecificos("TIPOS DE REGISTRO INGRESOS Y MOVILIDAD", "INGRESO").getIdDatosEspecificos();
		idEstadoCabOcupado =
			seleccionUtilFormController.buscarEstadoCab("OCUPADO").getIdEstadoCab();
	}

	private Boolean precondInsert() {
		if (nivelEntidadOeeUtil.getIdUnidadOrganizativa() == null ) {//|| idCpt == null|| idTipoCpt == null) {
			statusMessages.add(Severity.ERROR, "Debe cargar los campos requeridos");
			return false;
		}
		if (uFile == null) {
			statusMessages.add(Severity.ERROR, "Debe cargar un archivo");
			return false;
		}
		if (cType != null && cType.equalsIgnoreCase("")) {
			statusMessages.add(Severity.ERROR, "Se espera archivo en formato CSV (valores separados por coma)");
			return false;
		}
		return true;
	}

	private DTO547 descomponerLinea(String unaLinea) {
		String[] compos = unaLinea.split(";");
		String var = "";
		Calendar cal1 = Calendar.getInstance();
		var = cal1.get(Calendar.DATE)+"/"+(cal1.get(Calendar.MONTH)+1)+"/"+cal1.get(Calendar.YEAR); 
		if (compos.length < 17) {
			return null;
		}
		try {
			DTO547 dto547 = new DTO547();
			dto547.setAnhio(Integer.parseInt(compos[0]));
			dto547.setMes(Integer.parseInt(compos[1]));
			dto547.setNivelEnti(Integer.parseInt(compos[2]));
			dto547.setEntidad(Integer.parseInt(compos[3]));
			dto547.setDepende(Integer.parseInt(compos[4]));
			dto547.setCedula(compos[5]);
			dto547.setNombres(compos[6]);
			dto547.setApellidos(compos[7]);
			dto547.setEstado(compos[8]);
			dto547.setObjetoGto(compos[9]);
			dto547.setCateg(compos[10]);
			dto547.setPresup(Long.parseLong(compos[11]));
			dto547.setDeven(Long.parseLong(compos[12]));
			dto547.setMovimiento(compos[13]);
			//carga la fecha de hoy
			dto547.setFecha(var);
			dto547.setCargo(compos[15]);
			dto547.setDiscapaci(compos[16]);
			return dto547;
		} catch (Exception e) {
			return null;
		}
	}

	private void agregarEstadoMotivo(String estado, String motivo, int index) {
		String cSeparador = ";";
		String linea = lLineasArch.get(index);
		lLineasArch.set(index, linea + cSeparador + estado + cSeparador + motivo);
	}

	/*public void insertarMassive() throws IOException, ParseException {
		if (!precondInsert()) {
			return;
		}
		
		pt = em.find(PlantaCargoDet.class, idPt);
		
		UploadItem fileItem =
			seleccionUtilFormController.crearUploadItem(fName, uFile.length, cType, uFile);
		lLineasArch = FileUtils.readLines(fileItem.getFile());
		
		DTO547 dto547 = new DTO547();
		String o = null;
		for (int i = 1; i < lLineasArch.size(); i++) {
			o = lLineasArch.get(i);
			dto547 = precondCompos(o);
			if (dto547 != null) {
				try {
					// Si la fila tiene “movimiento” = B
					if (dto547.getMovimiento() != null
						&& dto547.getMovimiento().equalsIgnoreCase("B")) {
						agregarEstadoMotivo("FRACASO", "Corresponde a Baja, registre desvinculación de ser necesario", i);
					} else {
						PersonaDTO personaDTO =
							seleccionUtilFormController.buscarPersona(dto547.getCedula(), "PARAGUAY");
						if (personaDTO.getHabilitarBtn() == null) {
							agregarEstadoMotivo("FRACASO", personaDTO.getMensaje(), i);
						} else if (personaDTO.getHabilitarBtn()) {
							if (personaDTO.getPersona().getNombres().equalsIgnoreCase(dto547.getNombres())
								&& personaDTO.getPersona().getApellidos().equalsIgnoreCase(dto547.getApellidos())) {
								Persona persona = new Persona();
								persona.setActivo(true);
								persona.setNombres(personaDTO.getPersona().getNombres());
								persona.setApellidos(personaDTO.getPersona().getApellidos());
								persona.setSexo(personaDTO.getPersona().getSexo());
								//Agregado, para no insertar repetidos con "0"; Werner.
								//************************************************************************************************
								if ( personaDTO.getPersona().getDocumentoIdentidad().substring(0,1).equals("0")){
									persona.setDocumentoIdentidad(personaDTO.getPersona().getDocumentoIdentidad().substring(1) );
								}
								else{
									persona.setDocumentoIdentidad(personaDTO.getPersona().getDocumentoIdentidad());
								}
								//************************************************************************************************
								//persona.setDocumentoIdentidad(personaDTO.getPersona().getDocumentoIdentidad());
								persona.setFechaNacimiento(personaDTO.getPersona().getFechaNacimiento());
								persona.setPaisByIdPaisExpedicionDoc(personaDTO.getPersona().getPaisByIdPaisExpedicionDoc());
								persona.setDatosEspecificos(personaDTO.getPersona().getDatosEspecificos());
								persona.setFechaAlta(new Date());
								persona.setUsuAlta("SYSTEM");
								//Para acceder luego desde el portal se necesesita que el campo de teléfono no sea nulo
								persona.setTelContacto("MOVIL");
								persona.setTelefonos(" ");
								//
								em.persist(persona);
								paso4(dto547, persona);
								agregarEstadoMotivo("EXITOSO", "", i);
								em.flush();
							} else {
								agregarEstadoMotivo("FRACASO", "Los datos no coinciden con la BD Identificaciones", i);
							}
						} else {
							// 3.
							EmpleadoPuesto empleadoPuesto =
								existeEmpleadoPuesto(personaDTO.getPersona().getIdPersona(), nivelEntidadOeeUtil.getIdUnidadOrganizativa());
							if (empleadoPuesto != null) {
								DatosEspecificos deEspTipoIngresoMovilidad = null;
								if (empleadoPuesto.getDatosEspecificosByIdDatosEspTipoIngresoMovilidad() != null) {
									deEspTipoIngresoMovilidad =
										em.find(DatosEspecificos.class, empleadoPuesto.getDatosEspecificosByIdDatosEspTipoIngresoMovilidad().getIdDatosEspecificos());
								}
								if (deEspTipoIngresoMovilidad != null
									&& deEspTipoIngresoMovilidad.getDescripcion().equals("INGRESO POR INSERCION MASIVA")) {
									if (!existeCateg(dto547.getCateg())) {
										paso5(empleadoPuesto.getIdEmpleadoPuesto(), dto547);
										em.flush();
										agregarEstadoMotivo("EXITOSO", "", i);
									} else {
										agregarEstadoMotivo("-", "EL REGISTRO YA EXISTE", i);
									}
								} else {
									agregarEstadoMotivo("-", "EL REGISTRO YA EXISTE", i);
								}
							} else {
								paso4(dto547, personaDTO.getPersona());
								agregarEstadoMotivo("EXITOSO", "", i);
								em.flush();
							}
						}
					}
				} catch (Exception e) {
					agregarEstadoMotivo("FRACASO", "Error al procesar la linea", i);
					e.printStackTrace();
				}
			}
		}
		if (lLineasArch.size() > 0) {
			SimpleDateFormat sdf2 = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss");
			String nArchivo = fName + "_" + sdf2.format(new Date()) + ".csv";
			File archSalida = new File(nArchivo);
			FileUtils.writeLines(archSalida, lLineasArch);
			JasperReportUtils.respondFile(archSalida, nArchivo, false, "application/vnd.ms-excel");

		} else {
			statusMessages.add(Severity.INFO, "Archivo inválido");
		}
	}*/

	public boolean compararNomApe(String identificaciones, String parametros){
		String [] identList = identificaciones.split(" ");
		String [] paramList = parametros.split(" ");
		boolean respuesta = false;
		for(int i=0;i<identList.length;i++){
			for(int j=0;j<paramList.length;j++){
				if(identList[i].equals(paramList[j])){
					return true;
				}
				else{
					respuesta = false;
				}	
			}
		}	
		return respuesta;
	}
	
	public void insertarMassiveLessRestrictive() throws IOException, ParseException {
		if (!precondInsert()) {
			return;
		}
		
		pt = em.find(PlantaCargoDet.class, idPt);
		
		UploadItem fileItem =
			seleccionUtilFormController.crearUploadItem(fName, uFile.length, cType, uFile);
		lLineasArch = FileUtils.readLines(fileItem.getFile());
		
		DTO547 dto547 = new DTO547();
		String o = null;
		for (int i = 1; i < lLineasArch.size(); i++) {
			o = lLineasArch.get(i);
			dto547 = precondCompos(o);
			if (dto547 != null) {
				try {
					// Si la fila tiene “movimiento” = B
					if (dto547.getMovimiento() != null
						&& dto547.getMovimiento().equalsIgnoreCase("B")) {
						agregarEstadoMotivo("FRACASO", "Corresponde a Baja, registre desvinculación de ser necesario", i);
					} else {
						PersonaDTO personaDTO =
							seleccionUtilFormController.buscarPersona(dto547.getCedula(), "PARAGUAY");
						if (personaDTO.getHabilitarBtn() == null) {
							agregarEstadoMotivo("FRACASO", personaDTO.getMensaje(), i);
						} else if (personaDTO.getHabilitarBtn()) {
							//if (personaDTO.getPersona().getNombres().equalsIgnoreCase(dto547.getNombres())
								//&& personaDTO.getPersona().getApellidos().equalsIgnoreCase(dto547.getApellidos())) {
							if( compararNomApe(personaDTO.getPersona().getNombres(), dto547.getNombres()) && 
									compararNomApe(personaDTO.getPersona().getApellidos(), dto547.getApellidos()) ){
								Persona persona = new Persona();
								persona.setActivo(true);
								persona.setNombres(personaDTO.getPersona().getNombres());
								persona.setApellidos(personaDTO.getPersona().getApellidos());
								persona.setSexo(personaDTO.getPersona().getSexo());
								//Agregado, para no insertar repetidos con "0"; Werner.
								//************************************************************************************************
								if ( personaDTO.getPersona().getDocumentoIdentidad().substring(0,1).equals("0")){
									persona.setDocumentoIdentidad(personaDTO.getPersona().getDocumentoIdentidad().substring(1) );
								}
								else{
									persona.setDocumentoIdentidad(personaDTO.getPersona().getDocumentoIdentidad());
								}
								//************************************************************************************************
								//persona.setDocumentoIdentidad(personaDTO.getPersona().getDocumentoIdentidad());
								persona.setFechaNacimiento(personaDTO.getPersona().getFechaNacimiento());
								persona.setPaisByIdPaisExpedicionDoc(personaDTO.getPersona().getPaisByIdPaisExpedicionDoc());
								persona.setDatosEspecificos(personaDTO.getPersona().getDatosEspecificos());
								persona.setFechaAlta(new Date());
								persona.setUsuAlta("SYSTEM");
								//Para acceder luego desde el portal se necesesita que el campo de teléfono no sea nulo
								persona.setTelContacto("MOVIL");
								persona.setTelefonos(" ");
								//
								em.persist(persona);
								paso4(dto547, persona);
								agregarEstadoMotivo("EXITOSO", "", i);
								em.flush();
							} else {
								agregarEstadoMotivo("FRACASO", "Los datos no coinciden con la BD Identificaciones", i);
							}
						} else {
							// 3.
							EmpleadoPuesto empleadoPuesto =
								existeEmpleadoPuesto(personaDTO.getPersona().getIdPersona(), nivelEntidadOeeUtil.getIdUnidadOrganizativa());
							if (empleadoPuesto != null) {
								DatosEspecificos deEspTipoIngresoMovilidad = null;
								if (empleadoPuesto.getDatosEspecificosByIdDatosEspTipoIngresoMovilidad() != null) {
									deEspTipoIngresoMovilidad =
										em.find(DatosEspecificos.class, empleadoPuesto.getDatosEspecificosByIdDatosEspTipoIngresoMovilidad().getIdDatosEspecificos());
								}
								if (deEspTipoIngresoMovilidad != null
									&& deEspTipoIngresoMovilidad.getDescripcion().equals("INGRESO POR INSERCION MASIVA")) {
									if (!existeCateg(dto547.getCateg())) {
										paso5(empleadoPuesto.getIdEmpleadoPuesto(), dto547);
										em.flush();
										agregarEstadoMotivo("EXITOSO", "", i);
									} else {
										agregarEstadoMotivo("-", "EL REGISTRO YA EXISTE", i);
									}
								} else {
									agregarEstadoMotivo("-", "EL REGISTRO YA EXISTE", i);
								}
							} else {
								paso4(dto547, personaDTO.getPersona());
								agregarEstadoMotivo("EXITOSO", "", i);
								em.flush();
							}
						}
					}
				} catch (Exception e) {
					agregarEstadoMotivo("FRACASO", "Error al procesar la linea", i);
					e.printStackTrace();
				}
			}
		}
		if (lLineasArch.size() > 0) {
			SimpleDateFormat sdf2 = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss");
			String nArchivo = fName + "_" + sdf2.format(new Date()) + ".csv";
			File archSalida = new File(nArchivo);
			FileUtils.writeLines(archSalida, lLineasArch);
			JasperReportUtils.respondFile(archSalida, nArchivo, false, "application/vnd.ms-excel");

		} else {
			statusMessages.add(Severity.INFO, "Archivo inválido");
		}
	}

	
	private void paso5(Long idEmpleadoPuesto, DTO547 dto547) {
		EmpleadoConceptoPago ecp = new EmpleadoConceptoPago();
		ecp.setEmpleadoPuesto(new EmpleadoPuesto());
		ecp.getEmpleadoPuesto().setIdEmpleadoPuesto(idEmpleadoPuesto);
		ecp.setCategoria(dto547.getCateg());
		ecp.setObjCodigo(Integer.parseInt(dto547.getObjetoGto().substring(0, 3)));
		ecp.setMonto(dto547.getDeven().intValue());
		ecp.setAnho((dto547.getAnhio().intValue()));
		ecp.setUsuAlta("SYSTEM");
		ecp.setFechaAlta(new Date());
		ecp.setActivo(true);
		ecp.setActual(true);
		em.persist(ecp);

	}

	@SuppressWarnings("unchecked")
	private boolean exiteLegajo(Long idPersona) {
		List<Legajos> legajos =
			em.createQuery("Select l from Legajos l " + " where l.persona.idPersona=:idPersona").setParameter("idPersona", idPersona).getResultList();
		return !legajos.isEmpty();
	}

	private Boolean existeCateg(String categoria) {
		if (categoria == null || categoria.trim().isEmpty())
			return false;
		Query q =
			em.createQuery("select EmpleadoConceptoPago from EmpleadoConceptoPago EmpleadoConceptoPago"
				+ " where EmpleadoConceptoPago.categoria = :categoria ");
		q.setParameter("categoria", categoria);
		List<EmpleadoConceptoPago> lista = q.getResultList();
		if (lista.size() == 0) {
			return false;
		} else
			return true;
	}

	public EmpleadoPuesto existeEmpleadoPuesto(Long idPersona, Long idUndOrg) {
		Query q =
			em.createQuery("select EmpleadoPuesto from EmpleadoPuesto EmpleadoPuesto "
				+ " where EmpleadoPuesto.activo is true and EmpleadoPuesto.persona.idPersona = :idPersona "
				+ " and EmpleadoPuesto.plantaCargoDet.configuracionUoDet.idConfiguracionUoDet = :idUnOrg");
		q.setParameter("idPersona", idPersona);
		q.setParameter("idUnOrg", idUndOrg);
		List<EmpleadoPuesto> lista = q.getResultList();
		if (lista.size() > 0) {
			return lista.get(0);
		} else {
			return null;
		}
	}

	private void paso4(DTO547 dto547, Persona persona) throws ParseException {
		// 4.1
		PlantaCargoDet plantaCargoDet = new PlantaCargoDet();
		//plantaCargoDet.setDescripcion(cpt.getDenominacion());
		//plantaCargoDet.setDescripcion(pt.getDescripcion());
		
		//SE TOMA LA DESCRIPCION DEL CARGO QUE APARECE EN LA PLANILLA IMPORTADA. - ECESPEDES
		plantaCargoDet.setDescripcion(dto547.getCargo());
		
		plantaCargoDet.setFundamentacionTecnica("INSERCIÓN MASIVA");
		plantaCargoDet.setComisionado(false);
		if (dto547.getEstado() != null) {
			if (dto547.getEstado().equalsIgnoreCase("PERMANENTE")
				|| dto547.getEstado().equalsIgnoreCase("COMISIONADO")) {
				plantaCargoDet.setPermanente(true);
				plantaCargoDet.setContratado(false);
			}
			if (dto547.getEstado().equalsIgnoreCase("CONTRATADO")) {
				plantaCargoDet.setPermanente(false);
				plantaCargoDet.setContratado(true);
			}
		}
		plantaCargoDet.setActivo(true);
		plantaCargoDet.setUsuAlta("SYSTEM");
		plantaCargoDet.setFechaAlta(new Date());
		plantaCargoDet.setConfiguracionUoDet(new ConfiguracionUoDet());
		plantaCargoDet.getConfiguracionUoDet().setIdConfiguracionUoDet(nivelEntidadOeeUtil.getIdUnidadOrganizativa());
		plantaCargoDet.setCpt(new Cpt());
		//plantaCargoDet.getCpt().setIdCpt(idCpt);
		plantaCargoDet.getCpt().setIdCpt(pt.getCpt().getId());
		plantaCargoDet.setOrden(calcNroOrden());
		plantaCargoDet.setEstadoCab(new EstadoCab());
		plantaCargoDet.getEstadoCab().setIdEstadoCab(idEstadoCabOcupado);
		em.persist(plantaCargoDet);

		// 4.2
		List<DetTipoNombramiento> lCopia1 =
			//listadoAcopiar(idTipoCpt, idCpt, "DetTipoNombramiento", false);
			listadoAcopiar(idTipoCpt, idPt, "DetTipoNombramiento", false);
		for (DetTipoNombramiento o : lCopia1) {
			DetTipoNombramiento detTipoNombramiento = new DetTipoNombramiento();
			detTipoNombramiento.setTipoNombramiento(o.getTipoNombramiento());
			detTipoNombramiento.setPlantaCargoDet(new PlantaCargoDet());
			detTipoNombramiento.getPlantaCargoDet().setIdPlantaCargoDet(plantaCargoDet.getIdPlantaCargoDet());
			detTipoNombramiento.setTipo("PUESTO");
			detTipoNombramiento.setCpt(null);
			em.persist(detTipoNombramiento);
		}
		// 4.3
		List<DetContenidoFuncional> lCopia2 =
			//listadoAcopiar(idTipoCpt, idCpt, "DetContenidoFuncional", true);
			listadoAcopiar(idTipoCpt, idPt, "DetContenidoFuncional", true);
		for (DetContenidoFuncional o : lCopia2) {
			DetContenidoFuncional entidad = new DetContenidoFuncional();
			entidad.setContenidoFuncional(new ContenidoFuncional());
			entidad.getContenidoFuncional().setIdContenidoFuncional(o.getContenidoFuncional().getIdContenidoFuncional());
			entidad.setPlantaCargoDet(new PlantaCargoDet());
			entidad.getPlantaCargoDet().setIdPlantaCargoDet(plantaCargoDet.getIdPlantaCargoDet());
			entidad.setTipo("PUESTO");
			entidad.setPuntaje(o.getPuntaje());
			entidad.setCpt(null);
			entidad.setActivo(true);
			em.persist(entidad);
			List<DetDescripcionContFuncional> lista = o.getDetDescripcionContFuncionals();
			for (DetDescripcionContFuncional p : lista) {
				DetDescripcionContFuncional ddcf = new DetDescripcionContFuncional();
				ddcf.setActivo(true);
				ddcf.setDetContenidoFuncional(entidad);
				ddcf.setDescripcion(p.getDescripcion());
				em.persist(ddcf);
			}
		}
		// 4.4
		List<DetCondicionTrabajo> lCopia3 =
			//listadoAcopiar(idTipoCpt, idCpt, "DetCondicionTrabajo", true);
			listadoAcopiar(idTipoCpt, idPt, "DetCondicionTrabajo", true);
		for (DetCondicionTrabajo o : lCopia3) {
			DetCondicionTrabajo entidad = new DetCondicionTrabajo();

			entidad.setCondicionTrabajo(new CondicionTrabajo());
			entidad.getCondicionTrabajo().setIdCondicionTrabajo(o.getCondicionTrabajo().getIdCondicionTrabajo());
			entidad.setPlantaCargoDet(new PlantaCargoDet());
			entidad.getPlantaCargoDet().setIdPlantaCargoDet(plantaCargoDet.getIdPlantaCargoDet());
			entidad.setTipo("PUESTO");
			entidad.setPuntajeCondTrab(o.getPuntajeCondTrab());
			entidad.setCpt(null);
			entidad.setActivo(true);
			em.persist(entidad);
		}

		// 4.5
		List<DetCondicionTrabajoEspecif> lCopia4 =
			//listadoAcopiar(idTipoCpt, idCpt, "DetCondicionTrabajoEspecif", true);
			listadoAcopiar(idTipoCpt, idPt, "DetCondicionTrabajoEspecif", true);
		for (DetCondicionTrabajoEspecif o : lCopia4) {
			DetCondicionTrabajoEspecif entidad = new DetCondicionTrabajoEspecif();
			entidad.setCondicionTrabajoEspecif(new CondicionTrabajoEspecif());
			entidad.getCondicionTrabajoEspecif().setIdCondicionesTrabajoEspecif(o.getCondicionTrabajoEspecif().getIdCondicionesTrabajoEspecif());
			entidad.setPlantaCargoDet(new PlantaCargoDet());
			entidad.getPlantaCargoDet().setIdPlantaCargoDet(plantaCargoDet.getIdPlantaCargoDet());
			entidad.setTipo("PUESTO");
			entidad.setPuntajeCondTrabEspecif(o.getPuntajeCondTrabEspecif());
			entidad.setAjustes(o.getAjustes());
			entidad.setJustificacion(o.getJustificacion());
			entidad.setCpt(null);
			entidad.setActivo(true);
			em.persist(entidad);
		}
		// 4.6
		List<DetCondicionSegur> lCopia5 =
			//listadoAcopiar(idTipoCpt, idCpt, "DetCondicionSegur", true);
			listadoAcopiar(idTipoCpt, idPt, "DetCondicionSegur", true);
		for (DetCondicionSegur o : lCopia5) {
			DetCondicionSegur entidad = new DetCondicionSegur();
			entidad.setCondicionSegur(new CondicionSegur());
			entidad.getCondicionSegur().setIdCondicionSegur(o.getCondicionSegur().getIdCondicionSegur());
			entidad.setPlantaCargoDet(new PlantaCargoDet());
			entidad.getPlantaCargoDet().setIdPlantaCargoDet(plantaCargoDet.getIdPlantaCargoDet());
			entidad.setTipo("PUESTO");
			entidad.setPuntajeCondSegur(o.getPuntajeCondSegur());
			entidad.setAjustes(o.getAjustes());
			entidad.setJustificacion(o.getJustificacion());
			entidad.setCpt(null);
			entidad.setActivo(true);
			em.persist(entidad);
		}
		// 4.7
		//List<DetReqMin> lCopia6 = listadoAcopiar(idTipoCpt, idCpt, "DetReqMin", true);
		List<DetReqMin> lCopia6 = listadoAcopiar(idTipoCpt, idPt, "DetReqMin", true);
		for (DetReqMin o : lCopia6) {
			DetReqMin entidad = new DetReqMin();
			entidad.setRequisitoMinimoCpt(new RequisitoMinimoCpt());
			entidad.getRequisitoMinimoCpt().setIdRequisitoMinimoCpt(o.getRequisitoMinimoCpt().getIdRequisitoMinimoCpt());
			entidad.setPlantaCargoDet(new PlantaCargoDet());
			entidad.getPlantaCargoDet().setIdPlantaCargoDet(plantaCargoDet.getIdPlantaCargoDet());
			entidad.setTipo("PUESTO");
			entidad.setPuntajeReqMin(o.getPuntajeReqMin());
			entidad.setCpt(null);
			entidad.setActivo(true);
			em.persist(entidad);
			List<DetMinimosRequeridos> lista1 = o.getDetMinimosRequeridoses();
			List<DetOpcionesConvenientes> lista2 = o.getDetOpcionesConvenienteses();
			for (DetMinimosRequeridos p : lista1) {
				DetMinimosRequeridos dmr = new DetMinimosRequeridos();
				dmr.setActivo(true);
				dmr.setDetReqMin(entidad);
				dmr.setMinimosRequeridos(p.getMinimosRequeridos());
				em.persist(dmr);
			}
			for (DetOpcionesConvenientes p : lista2) {
				DetOpcionesConvenientes doc = new DetOpcionesConvenientes();
				doc.setActivo(true);
				doc.setDetReqMin(entidad);
				doc.setOpcConvenientes(p.getOpcConvenientes());
				em.persist(doc);
			}
		}
		
		
		// 4.8
		EmpleadoPuesto empleadoPuesto = new EmpleadoPuesto();
		empleadoPuesto.setActivo(true);
		empleadoPuesto.setIncidenAntiguedad(true);
		empleadoPuesto.setActual(true);
		empleadoPuesto.setFechaAlta(new Date());
		empleadoPuesto.setUsuAlta("SYSTEM");
		if (dto547.getFecha() != null && !dto547.getFecha().trim().isEmpty()) {
			empleadoPuesto.setFechaInicio(sdf.parse(dto547.getFecha()));			
		} else {
			empleadoPuesto.setFechaInicio(new Date());
		}
		empleadoPuesto.setPersona(persona);
		empleadoPuesto.setPlantaCargoDet(new PlantaCargoDet());
		empleadoPuesto.getPlantaCargoDet().setIdPlantaCargoDet(plantaCargoDet.getIdPlantaCargoDet());
		if (dto547.getEstado() != null) {
			if (dto547.getEstado().equalsIgnoreCase("CONTRATADO")) {
				empleadoPuesto.setContratado(true);
				empleadoPuesto.setDatosEspecificosByIdDatosEspEstado(new DatosEspecificos());
				empleadoPuesto.getDatosEspecificosByIdDatosEspEstado().setIdDatosEspecificos(idEstadoContratado);
			} else if (dto547.getEstado().equalsIgnoreCase("COMISIONADO")) {
				empleadoPuesto.setContratado(false);
				empleadoPuesto.setDatosEspecificosByIdDatosEspEstado(new DatosEspecificos());
				empleadoPuesto.getDatosEspecificosByIdDatosEspEstado().setIdDatosEspecificos(idEstadoComisionado);
			} else if (dto547.getEstado().equalsIgnoreCase("PERMANENTE")) {
				empleadoPuesto.setContratado(false);
				empleadoPuesto.setDatosEspecificosByIdDatosEspEstado(new DatosEspecificos());
				empleadoPuesto.getDatosEspecificosByIdDatosEspEstado().setIdDatosEspecificos(idEstadoPermanente);
			}
		}
		empleadoPuesto.setCategoria(dto547.getCateg());
		empleadoPuesto.setDatosEspecificosByIdDatosEspTipoIngresoMovilidad(new DatosEspecificos());
		empleadoPuesto.getDatosEspecificosByIdDatosEspTipoIngresoMovilidad().setIdDatosEspecificos(idEstadoInsertMasiva);
		empleadoPuesto.setDatosEspecificosByIdDatosEspTipoRegistro(new DatosEspecificos());
		empleadoPuesto.getDatosEspecificosByIdDatosEspTipoRegistro().setIdDatosEspecificos(idEstadoTipoRegistro);
		if (dto547.getDiscapaci() != null && dto547.getDiscapaci().matches("[0-9]+"))
			empleadoPuesto.setDiscapacitado(Integer.parseInt(dto547.getDiscapaci()));
		em.persist(empleadoPuesto);

		// 4.9
		if (!exiteLegajo(persona.getIdPersona())) {
			Legajos legajos = new Legajos();
			legajos.setPersona(persona);
			legajos.setFechaIngreso(new Date());
			legajos.setDatosEspecificosTipoIngreso(new DatosEspecificos());
			legajos.getDatosEspecificosTipoIngreso().setIdDatosEspecificos(idEstadoInsertMasiva);
			em.persist(legajos);
		}
		// 4.10
		paso5(empleadoPuesto.getIdEmpleadoPuesto(), dto547);

	}

//	public List listadoAcopiar(Long idTipoNom, Long idCpt, String tabla, Boolean activo) {
//		Query q =
//			em.createQuery("select " + tabla + " from " + tabla + " " + tabla + " where " + tabla
//				+ ".cpt.idCpt = :idCpt " + (activo ? " and " + tabla + ".activo is true" : ""));
//
//		q.setParameter("idPt", idCpt);
//		return q.getResultList();
//	}
	
	public List listadoAcopiar(Long idTipoNom, Long idPt, String tabla, Boolean activo) {
		Query q =
			em.createQuery("select " + tabla + " from " + tabla + " " + tabla + " where " + tabla
				+ ".plantaCargoDet.idPlantaCargoDet = :idPt " + (activo ? " and " + tabla + ".activo is true" : ""));

		q.setParameter("idPt", idPt);
		return q.getResultList();
	}

	public Integer calcNroOrden() {
		StringBuffer var1 = new StringBuffer();
		var1.append("SELECT Max(plantacarg0_.orden) FROM planificacion.planta_cargo_det ");
		var1.append(" plantacarg0_ WHERE plantacarg0_.activo = TRUE AND plantacarg0_.id_configuracion_uo_det = :idUndOrg ");

		Query q = em.createNativeQuery(var1.toString());
		q.setParameter("idUndOrg", nivelEntidadOeeUtil.getIdUnidadOrganizativa());
		Object obj = q.getSingleResult();
		if (obj == null)
			return 1;

		return ((Integer) q.getSingleResult()).intValue() + 1;
	}

/*	@SuppressWarnings("unchecked")
	public void updateCpt() {
		String cadena =
			" select cpt.* from planificacion.cpt cpt join planificacion.planta_cargo_det pt"
				+" on (cpt.id_cpt = pt.id_cpt) "
				+ "where cpt.activo is true "
				+ "and cpt.id_tipo_cpt = " + idTipoCpt
				+" and pt.id_planta_cargo_det = "+ idPt;

		List<Cpt> lista = new ArrayList<Cpt>();
		lista = em.createNativeQuery(cadena, Cpt.class).getResultList();
		cptSelecItem = new ArrayList<SelectItem>();
		cptSelecItem.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
		if (lista.size() > 0) {

			for (Cpt cpt : lista) {
				cptSelecItem.add(new SelectItem(cpt.getIdCpt(), cpt.getDenominacion()));
			}
		}

		idCpt = null;
		codigoCpt = null;
		idCptFromList = null;
	}
	*/
	@SuppressWarnings("unchecked")
	public List<SelectItem> updatePt() {

		String cadena =
				"select pt.* from planificacion.planta_cargo_det pt "
				+ " where pt.activo = true "
				+ " and es_plantilla  = true "
				+ " order by pt.descripcion ";
		
		List<PlantaCargoDet> lista = new ArrayList<PlantaCargoDet>();
		lista = em.createNativeQuery(cadena, PlantaCargoDet.class).getResultList();
		List<SelectItem> ptSelectItem = new ArrayList<SelectItem>();
		ptSelectItem.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
		if (lista.size() > 0) {

			for (PlantaCargoDet pt : lista) {
				ptSelectItem.add(new SelectItem(pt.getIdPlantaCargoDet(), pt.getDescripcion()));
			}
		}

		idPt = null;
		return ptSelectItem;
	}
	
	

	/**
	 * Valida cada linea pasada como parametro y si es correcta la misma la retorna separada en sus partes
	 * 
	 * @param linea
	 *            cadena de texto separada por comas
	 * @return
	 */
	private DTO547 precondCompos(String linea) {
		DTO547 dto547 = descomponerLinea(linea);
		if (dto547 != null)
			return dto547;
		return null;
	}

	public void limpiar() {
		nivelEntidadOeeUtil.limpiar();
		uFile = null;
		cType = null;
		fName = null;
		idCpt = null;
		idPt = null;
		idTipoCpt = null;
		codigoCpt = null;
	}

	/**
	 * Método que busca el CPT
	 */
	/*
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
				String cadena = "";
				if (idPt != null) {
					cadena =
						" select cpt.* from planificacion.planta_cargo_det pt join "
							+ "planificacion.cpt cpt on (pt.id_cpt = cpt.id_cpt)"
							+ "join planificacion.grado_salarial max "
							+ "on max.id_grado_salarial = cpt.id_grado_salarial_max "
							+ "join planificacion.grado_salarial min "
							+ "on min.id_grado_salarial = cpt.id_grado_salarial_min "
							+ "where cpt.nivel = " + nivelCpt + " and max.numero = " + gradoMax
							+ " and min.numero = " + gradoMin + " and cpt.numero = " + numero
							+ " and cpt.nro_especifico = " + nroEspecifico
							+ " and pt.id_pt = "+ idPt;
				}else{
					
					cadena =
							" select cpt.* from planificacion.cpt cpt "
								+ "join planificacion.grado_salarial max "
								+ "on max.id_grado_salarial = cpt.id_grado_salarial_max "
								+ "join planificacion.grado_salarial min "
								+ "on min.id_grado_salarial = cpt.id_grado_salarial_min "
								+ "where cpt.nivel = " + nivelCpt + " and max.numero = " + gradoMax
								+ " and min.numero = " + gradoMin + " and cpt.numero = " + numero
								+ " and cpt.nro_especifico = " + nroEspecifico;
				}
				
				
				if (idTipoCpt != null)
					cadena = cadena + " and id_tipo_cpt = " + idTipoCpt;
				
				List<Cpt> lista = new ArrayList<Cpt>();
				lista = em.createNativeQuery(cadena, Cpt.class).getResultList();
				cptSelecItem = new ArrayList<SelectItem>();
				cptSelecItem.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
				if (lista.size() > 0) {
					for (Cpt cpt : lista) {
						cptSelecItem.add(new SelectItem(cpt.getIdCpt(), cpt.getDenominacion()));
						idCpt = cpt.getIdCpt();
					}
				}

			}
		} catch (Exception e) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, "Ingrese un código válido");

		}
	}

	public String getUrlToPageCpt() {
		return "/planificacion/searchForms/CptList.xhtml?from=planificacion/puestosTrabajo/PlantaCargoDetEdit&tipoCpt="
			+ idTipoCpt;
	}

	public void obtenerCodigoCpt() {

		Cpt cptActual = new Cpt();
		cptActual = em.find(Cpt.class, idCpt);

		codigoCpt =
			cptActual.getNivel() + "." + cptActual.getGradoSalarialMin().getNumero() + "."
				+ cptActual.getGradoSalarialMax().getNumero() + "." + cptActual.getNumero() + "."
				+ cptActual.getNroEspecifico();

	}*/

	public Long getIdTipoCpt() {
		return idTipoCpt;
	}

	public void setIdTipoCpt(Long idTipoCpt) {
		this.idTipoCpt = idTipoCpt;
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

	public String getfName() {
		return fName;
	}

	public void setfName(String fName) {
		this.fName = fName;
	}

	public Long getIdCptFromList() {
		return idCptFromList;
	}

	public void setIdCptFromList(Long idCptFromList) {
		this.idCptFromList = idCptFromList;
	}

	public List<SelectItem> getCptSelecItem() {
		return cptSelecItem;
	}

	public void setCptSelecItem(List<SelectItem> cptSelecItem) {
		this.cptSelecItem = cptSelecItem;
	}

	private Integer progress;

	public Integer getProgress() {
		if (progress == null)
			progress = 0;
		else {
			progress = progress + (int) (Math.random() * 35);

			if (progress > 100)
				progress = 100;
		}

		return progress;
	}

	public void setProgress(Integer progress) {
		this.progress = progress;
	}

	public void onComplete() {
		statusMessages.add(Severity.INFO, "-----");
	}

	public void cancel() {
		progress = null;
	}
	
	public Long getIdEstado() {
		return idEstado;
	}

	public void setIdEstado(Long idEstado) {
		this.idEstado = idEstado;
	}
	
	public Long getIdPt() {
		return idPt;
	}

	public void setIdPt(Long idPt) {
		this.idPt = idPt;
	}

}



/*package py.com.excelsis.sicca.desvinculacion.session.form;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.io.FileUtils;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.richfaces.model.UploadItem;

import py.com.excelsis.sicca.entity.CondicionSegur;
import py.com.excelsis.sicca.entity.CondicionTrabajo;
import py.com.excelsis.sicca.entity.CondicionTrabajoEspecif;
import py.com.excelsis.sicca.entity.ConfiguracionUoDet;
import py.com.excelsis.sicca.entity.ContenidoFuncional;
import py.com.excelsis.sicca.entity.Cpt;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.DetCondicionSegur;
import py.com.excelsis.sicca.entity.DetCondicionTrabajo;
import py.com.excelsis.sicca.entity.DetCondicionTrabajoEspecif;
import py.com.excelsis.sicca.entity.DetContenidoFuncional;
import py.com.excelsis.sicca.entity.DetDescripcionContFuncional;
import py.com.excelsis.sicca.entity.DetMinimosRequeridos;
import py.com.excelsis.sicca.entity.DetOpcionesConvenientes;
import py.com.excelsis.sicca.entity.DetReqMin;
import py.com.excelsis.sicca.entity.DetTipoNombramiento;
import py.com.excelsis.sicca.entity.EmpleadoConceptoPago;
import py.com.excelsis.sicca.entity.EmpleadoPuesto;
import py.com.excelsis.sicca.entity.EstadoCab;
import py.com.excelsis.sicca.entity.Legajos;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.entity.RequisitoMinimoCpt;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.ConfiguracionUoCabList;
import py.com.excelsis.sicca.session.EmpleadoPuestoList;
import py.com.excelsis.sicca.session.EntidadList;
import py.com.excelsis.sicca.session.SinEntidadList;
import py.com.excelsis.sicca.session.SinNivelEntidadList;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.PersonaDTO;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.JasperReportUtils;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.SICCAAppHelper;

@Scope(ScopeType.CONVERSATION)
@Name("cargaMasivaFuncioFC")
public class CargaMasivaFuncioFC {
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
	@In(create = true)
	SeleccionUtilFormController seleccionUtilFormController;

	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;

	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	private List<SelectItem> cptSelecItem = new ArrayList<SelectItem>();
	private Long idCptFromList;
	private Long idTipoCpt;
	private String codigoCpt;
	private Cpt cpt = new Cpt();
	private Long idCpt;

	private byte[] uFile = null;
	private String cType = null;
	private String fName = null;
	List<String> lLineasArch;
	private Long idEstadoComisionado;
	private Long idEstadoContratado;
	private Long idEstadoPermanente;
	private Long idEstadoInsertMasiva;
	private Long idEstadoTipoRegistro;
	private Long idEstadoCabOcupado;

	public void init() {
		calIdEstados();
	}

	private void calIdEstados() {
		idEstadoComisionado =
			seleccionUtilFormController.traerDatosEspecificos("ESTADO EMPLEADO PUESTO", "COMISIONADO").getIdDatosEspecificos();
		idEstadoContratado =
			seleccionUtilFormController.traerDatosEspecificos("ESTADO EMPLEADO PUESTO", "CONTRATADO").getIdDatosEspecificos();
		idEstadoPermanente =
			seleccionUtilFormController.traerDatosEspecificos("ESTADO EMPLEADO PUESTO", "PERMANENTE").getIdDatosEspecificos();
		idEstadoInsertMasiva =
			seleccionUtilFormController.traerDatosEspecificos("TIPOS DE INGRESOS Y MOVILIDAD", "INGRESO POR INSERCION MASIVA").getIdDatosEspecificos();
		idEstadoTipoRegistro =
			seleccionUtilFormController.traerDatosEspecificos("TIPOS DE REGISTRO INGRESOS Y MOVILIDAD", "INGRESO").getIdDatosEspecificos();
		idEstadoCabOcupado =
			seleccionUtilFormController.buscarEstadoCab("OCUPADO").getIdEstadoCab();
	}

	private Boolean precondInsert() {
		if (nivelEntidadOeeUtil.getIdUnidadOrganizativa() == null || idCpt == null
			|| idTipoCpt == null) {
			statusMessages.add(Severity.ERROR, "Debe cargar los campos requeridos");
			return false;
		}
		if (uFile == null) {
			statusMessages.add(Severity.ERROR, "Debe cargar un archivo");
			return false;
		}
		if (cType != null && cType.equalsIgnoreCase("")) {
			statusMessages.add(Severity.ERROR, "Se espaera archivo en formato CSV (valores separados por coma)");
			return false;
		}
		return true;
	}

	private DTO547 descomponerLinea(String unaLinea) {
		String[] compos = unaLinea.split(";");
		if (compos.length < 17) {
			return null;
		}
		try {
			DTO547 dto547 = new DTO547();
			dto547.setAnhio(Integer.parseInt(compos[0]));
			dto547.setMes(Integer.parseInt(compos[1]));
			dto547.setNivelEnti(Integer.parseInt(compos[2]));
			dto547.setEntidad(Integer.parseInt(compos[3]));
			dto547.setDepende(Integer.parseInt(compos[4]));
			dto547.setCedula(compos[5]);
			dto547.setNombres(compos[6]);
			dto547.setApellidos(compos[7]);
			dto547.setEstado(compos[8]);
			dto547.setObjetoGto(compos[9]);
			dto547.setCateg(compos[10]);
			dto547.setPresup(Long.parseLong(compos[11]));
			dto547.setDeven(Long.parseLong(compos[12]));
			dto547.setMovimiento(compos[13]);
			dto547.setFecha(compos[14]);
			dto547.setCargo(compos[15]);
			dto547.setDiscapaci(compos[16]);
			return dto547;
		} catch (Exception e) {
			return null;
		}
	}

	private void agregarEstadoMotivo(String estado, String motivo, int index) {
		String cSeparador = ";";
		String linea = lLineasArch.get(index);
		lLineasArch.set(index, linea + cSeparador + estado + cSeparador + motivo);
	}

	public void insertarMassive() throws IOException, ParseException {
		if (!precondInsert()) {
			return;
		}
		cpt = em.find(Cpt.class, idCpt);
		UploadItem fileItem =
			seleccionUtilFormController.crearUploadItem(fName, uFile.length, cType, uFile);
		lLineasArch = FileUtils.readLines(fileItem.getFile());
		DTO547 dto547 = new DTO547();
		String o = null;
		for (int i = 1; i < lLineasArch.size(); i++) {
			o = lLineasArch.get(i);
			dto547 = precondCompos(o);
			if (dto547 != null) {
				try {
					// Si la fila tiene “movimiento” = B
					if (dto547.getMovimiento() != null
						&& dto547.getMovimiento().equalsIgnoreCase("B")) {
						agregarEstadoMotivo("FRACASO", "Corresponde a Baja, registre desvinculación de ser necesario", i);
					} else {
						PersonaDTO personaDTO =
							seleccionUtilFormController.buscarPersona(dto547.getCedula(), "PARAGUAY");
						if (personaDTO.getHabilitarBtn() == null) {
							agregarEstadoMotivo("FRACASO", personaDTO.getMensaje(), i);
						} else if (personaDTO.getHabilitarBtn()) {
							if (personaDTO.getPersona().getNombres().equalsIgnoreCase(dto547.getNombres())
								&& personaDTO.getPersona().getApellidos().equalsIgnoreCase(dto547.getApellidos())) {
								Persona persona = new Persona();
								persona.setActivo(true);
								persona.setNombres(personaDTO.getPersona().getNombres());
								persona.setApellidos(personaDTO.getPersona().getApellidos());
								persona.setSexo(personaDTO.getPersona().getSexo());
								persona.setDocumentoIdentidad(personaDTO.getPersona().getDocumentoIdentidad());
								persona.setFechaNacimiento(personaDTO.getPersona().getFechaNacimiento());
								persona.setPaisByIdPaisExpedicionDoc(personaDTO.getPersona().getPaisByIdPaisExpedicionDoc());
								persona.setDatosEspecificos(personaDTO.getPersona().getDatosEspecificos());
								persona.setFechaAlta(new Date());
								persona.setUsuAlta("SYSTEM");
								em.persist(persona);
								paso4(dto547, persona);
								agregarEstadoMotivo("EXITOSO", "", i);
								em.flush();
							} else {
								agregarEstadoMotivo("FRACASO", "Los datos no coinciden con la BD Identificaciones", i);
							}
						} else {
							// 3.
							EmpleadoPuesto empleadoPuesto =
								existeEmpleadoPuesto(personaDTO.getPersona().getIdPersona(), nivelEntidadOeeUtil.getIdUnidadOrganizativa());
							if (empleadoPuesto != null) {
								DatosEspecificos deEspTipoIngresoMovilidad = null;
								if (empleadoPuesto.getDatosEspecificosByIdDatosEspTipoIngresoMovilidad() != null) {
									deEspTipoIngresoMovilidad =
										em.find(DatosEspecificos.class, empleadoPuesto.getDatosEspecificosByIdDatosEspTipoIngresoMovilidad().getIdDatosEspecificos());
								}
								if (deEspTipoIngresoMovilidad != null
									&& deEspTipoIngresoMovilidad.getDescripcion().equals("INGRESO POR INSERCION MASIVA")) {
									if (!existeCateg(dto547.getCateg())) {
										paso5(empleadoPuesto.getIdEmpleadoPuesto(), dto547);
										em.flush();
										agregarEstadoMotivo("EXITOSO", "", i);
									} else {
										agregarEstadoMotivo("-", "EL REGISTRO YA EXISTE", i);
									}
								} else {
									agregarEstadoMotivo("-", "EL REGISTRO YA EXISTE", i);
								}
							} else {
								paso4(dto547, personaDTO.getPersona());
								agregarEstadoMotivo("EXITOSO", "", i);
								em.flush();
							}
						}
					}
				} catch (Exception e) {
					agregarEstadoMotivo("FRACASO", "Error al procesar la linea", i);
					e.printStackTrace();
				}
			}
		}
		if (lLineasArch.size() > 0) {
			SimpleDateFormat sdf2 = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss");
			String nArchivo = fName + "_" + sdf2.format(new Date()) + ".csv";
			File archSalida = new File(nArchivo);
			FileUtils.writeLines(archSalida, lLineasArch);
			JasperReportUtils.respondFile(archSalida, nArchivo, false, "application/vnd.ms-excel");

		} else {
			statusMessages.add(Severity.INFO, "Archivo inválido");
		}
	}

	private void paso5(Long idEmpleadoPuesto, DTO547 dto547) {
		EmpleadoConceptoPago ecp = new EmpleadoConceptoPago();
		ecp.setEmpleadoPuesto(new EmpleadoPuesto());
		ecp.getEmpleadoPuesto().setIdEmpleadoPuesto(idEmpleadoPuesto);
		ecp.setCategoria(dto547.getCateg());
		ecp.setObjCodigo(Integer.parseInt(dto547.getObjetoGto().substring(0, 3)));
		ecp.setMonto(dto547.getDeven().intValue());
		ecp.setAnho((dto547.getAnhio().intValue()));
		ecp.setUsuAlta("SYSTEM");
		ecp.setFechaAlta(new Date());
		em.persist(ecp);

	}

	@SuppressWarnings("unchecked")
	private boolean exiteLegajo(Long idPersona) {
		List<Legajos> legajos =
			em.createQuery("Select l from Legajos l " + " where l.persona.idPersona=:idPersona").setParameter("idPersona", idPersona).getResultList();
		return !legajos.isEmpty();
	}

	private Boolean existeCateg(String categoria) {
		if (categoria == null || categoria.trim().isEmpty())
			return false;
		Query q =
			em.createQuery("select EmpleadoConceptoPago from EmpleadoConceptoPago EmpleadoConceptoPago"
				+ " where EmpleadoConceptoPago.categoria = :categoria ");
		q.setParameter("categoria", categoria);
		List<EmpleadoConceptoPago> lista = q.getResultList();
		if (lista.size() == 0) {
			return false;
		} else
			return true;
	}

	public EmpleadoPuesto existeEmpleadoPuesto(Long idPersona, Long idUndOrg) {
		Query q =
			em.createQuery("select EmpleadoPuesto from EmpleadoPuesto EmpleadoPuesto "
				+ " where EmpleadoPuesto.activo is true and EmpleadoPuesto.persona.idPersona = :idPersona "
				+ " and EmpleadoPuesto.plantaCargoDet.configuracionUoDet.idConfiguracionUoDet = :idUnOrg");
		q.setParameter("idPersona", idPersona);
		q.setParameter("idUnOrg", idUndOrg);
		List<EmpleadoPuesto> lista = q.getResultList();
		if (lista.size() > 0) {
			return lista.get(0);
		} else {
			return null;
		}
	}

	private void paso4(DTO547 dto547, Persona persona) throws ParseException {
		// 4.1
		PlantaCargoDet plantaCargoDet = new PlantaCargoDet();
		plantaCargoDet.setDescripcion(cpt.getDenominacion());
		plantaCargoDet.setFundamentacionTecnica("INSERCIÓN MASIVA");
		if (dto547.getEstado() != null) {
			if (dto547.getEstado().equalsIgnoreCase("PERMANENTE")
				|| dto547.getEstado().equalsIgnoreCase("COMISIONADO")) {
				plantaCargoDet.setPermanente(true);
				plantaCargoDet.setContratado(false);
			}
			if (dto547.getEstado().equalsIgnoreCase("CONTRATADO")) {
				plantaCargoDet.setPermanente(false);
				plantaCargoDet.setContratado(true);
			}
		}
		plantaCargoDet.setActivo(true);
		plantaCargoDet.setUsuAlta("SYSTEM");
		plantaCargoDet.setFechaAlta(new Date());
		plantaCargoDet.setConfiguracionUoDet(new ConfiguracionUoDet());
		plantaCargoDet.getConfiguracionUoDet().setIdConfiguracionUoDet(nivelEntidadOeeUtil.getIdUnidadOrganizativa());
		plantaCargoDet.setCpt(new Cpt());
		plantaCargoDet.getCpt().setIdCpt(idCpt);
		plantaCargoDet.setOrden(calcNroOrden());
		plantaCargoDet.setEstadoCab(new EstadoCab());
		plantaCargoDet.getEstadoCab().setIdEstadoCab(idEstadoCabOcupado);
		em.persist(plantaCargoDet);

		// 4.2
		List<DetTipoNombramiento> lCopia1 =
			listadoAcopiar(idTipoCpt, idCpt, "DetTipoNombramiento", false);
		for (DetTipoNombramiento o : lCopia1) {
			DetTipoNombramiento detTipoNombramiento = new DetTipoNombramiento();
			detTipoNombramiento.setTipoNombramiento(o.getTipoNombramiento());
			detTipoNombramiento.setPlantaCargoDet(new PlantaCargoDet());
			detTipoNombramiento.getPlantaCargoDet().setIdPlantaCargoDet(plantaCargoDet.getIdPlantaCargoDet());
			detTipoNombramiento.setTipo("PUESTO");
			detTipoNombramiento.setCpt(null);
			em.persist(detTipoNombramiento);
		}
		// 4.3
		List<DetContenidoFuncional> lCopia2 =
			listadoAcopiar(idTipoCpt, idCpt, "DetContenidoFuncional", true);
		for (DetContenidoFuncional o : lCopia2) {
			DetContenidoFuncional entidad = new DetContenidoFuncional();
			entidad.setContenidoFuncional(new ContenidoFuncional());
			entidad.getContenidoFuncional().setIdContenidoFuncional(o.getContenidoFuncional().getIdContenidoFuncional());
			entidad.setPlantaCargoDet(new PlantaCargoDet());
			entidad.getPlantaCargoDet().setIdPlantaCargoDet(plantaCargoDet.getIdPlantaCargoDet());
			entidad.setTipo("PUESTO");
			entidad.setPuntaje(o.getPuntaje());
			entidad.setCpt(null);
			entidad.setActivo(true);
			em.persist(entidad);
			List<DetDescripcionContFuncional> lista = o.getDetDescripcionContFuncionals();
			for (DetDescripcionContFuncional p : lista) {
				DetDescripcionContFuncional ddcf = new DetDescripcionContFuncional();
				ddcf.setActivo(true);
				ddcf.setDetContenidoFuncional(entidad);
				ddcf.setDescripcion(p.getDescripcion());
				em.persist(ddcf);
			}
		}
		// 4.4
		List<DetCondicionTrabajo> lCopia3 =
			listadoAcopiar(idTipoCpt, idCpt, "DetCondicionTrabajo", true);
		for (DetCondicionTrabajo o : lCopia3) {
			DetCondicionTrabajo entidad = new DetCondicionTrabajo();

			entidad.setCondicionTrabajo(new CondicionTrabajo());
			entidad.getCondicionTrabajo().setIdCondicionTrabajo(o.getCondicionTrabajo().getIdCondicionTrabajo());
			entidad.setPlantaCargoDet(new PlantaCargoDet());
			entidad.getPlantaCargoDet().setIdPlantaCargoDet(plantaCargoDet.getIdPlantaCargoDet());
			entidad.setTipo("PUESTO");
			entidad.setPuntajeCondTrab(o.getPuntajeCondTrab());
			entidad.setCpt(null);
			entidad.setActivo(true);
			em.persist(entidad);
		}

		// 4.5
		List<DetCondicionTrabajoEspecif> lCopia4 =
			listadoAcopiar(idTipoCpt, idCpt, "DetCondicionTrabajoEspecif", true);
		for (DetCondicionTrabajoEspecif o : lCopia4) {
			DetCondicionTrabajoEspecif entidad = new DetCondicionTrabajoEspecif();
			entidad.setCondicionTrabajoEspecif(new CondicionTrabajoEspecif());
			entidad.getCondicionTrabajoEspecif().setIdCondicionesTrabajoEspecif(o.getCondicionTrabajoEspecif().getIdCondicionesTrabajoEspecif());
			entidad.setPlantaCargoDet(new PlantaCargoDet());
			entidad.getPlantaCargoDet().setIdPlantaCargoDet(plantaCargoDet.getIdPlantaCargoDet());
			entidad.setTipo("PUESTO");
			entidad.setPuntajeCondTrabEspecif(o.getPuntajeCondTrabEspecif());
			entidad.setAjustes(o.getAjustes());
			entidad.setJustificacion(o.getJustificacion());
			entidad.setCpt(null);
			entidad.setActivo(true);
			em.persist(entidad);
		}
		// 4.6
		List<DetCondicionSegur> lCopia5 =
			listadoAcopiar(idTipoCpt, idCpt, "DetCondicionSegur", true);
		for (DetCondicionSegur o : lCopia5) {
			DetCondicionSegur entidad = new DetCondicionSegur();
			entidad.setCondicionSegur(new CondicionSegur());
			entidad.getCondicionSegur().setIdCondicionSegur(o.getCondicionSegur().getIdCondicionSegur());
			entidad.setPlantaCargoDet(new PlantaCargoDet());
			entidad.getPlantaCargoDet().setIdPlantaCargoDet(plantaCargoDet.getIdPlantaCargoDet());
			entidad.setTipo("PUESTO");
			entidad.setPuntajeCondSegur(o.getPuntajeCondSegur());
			entidad.setAjustes(o.getAjustes());
			entidad.setJustificacion(o.getJustificacion());
			entidad.setCpt(null);
			entidad.setActivo(true);
			em.persist(entidad);
		}
		// 4.7
		List<DetReqMin> lCopia6 = listadoAcopiar(idTipoCpt, idCpt, "DetReqMin", true);
		for (DetReqMin o : lCopia6) {
			DetReqMin entidad = new DetReqMin();
			entidad.setRequisitoMinimoCpt(new RequisitoMinimoCpt());
			entidad.getRequisitoMinimoCpt().setIdRequisitoMinimoCpt(o.getRequisitoMinimoCpt().getIdRequisitoMinimoCpt());
			entidad.setPlantaCargoDet(new PlantaCargoDet());
			entidad.getPlantaCargoDet().setIdPlantaCargoDet(plantaCargoDet.getIdPlantaCargoDet());
			entidad.setTipo("PUESTO");
			entidad.setPuntajeReqMin(o.getPuntajeReqMin());
			entidad.setCpt(null);
			entidad.setActivo(true);
			em.persist(entidad);
			List<DetMinimosRequeridos> lista1 = o.getDetMinimosRequeridoses();
			List<DetOpcionesConvenientes> lista2 = o.getDetOpcionesConvenienteses();
			for (DetMinimosRequeridos p : lista1) {
				DetMinimosRequeridos dmr = new DetMinimosRequeridos();
				dmr.setActivo(true);
				dmr.setDetReqMin(entidad);
				dmr.setMinimosRequeridos(p.getMinimosRequeridos());
				em.persist(dmr);
			}
			for (DetOpcionesConvenientes p : lista2) {
				DetOpcionesConvenientes doc = new DetOpcionesConvenientes();
				doc.setActivo(true);
				doc.setDetReqMin(entidad);
				doc.setOpcConvenientes(p.getOpcConvenientes());
				em.persist(doc);
			}
		}
		// 4.8
		EmpleadoPuesto empleadoPuesto = new EmpleadoPuesto();
		empleadoPuesto.setActivo(true);
		empleadoPuesto.setIncidenAntiguedad(true);
		empleadoPuesto.setActual(true);
		empleadoPuesto.setFechaAlta(new Date());
		empleadoPuesto.setUsuAlta("SYSTEM");
		if (dto547.getFecha() != null && !dto547.getFecha().trim().isEmpty()) {
			empleadoPuesto.setFechaInicio(sdf.parse(dto547.getFecha()));
		} else {
			empleadoPuesto.setFechaInicio(new Date());
		}
		empleadoPuesto.setPersona(persona);
		empleadoPuesto.setPlantaCargoDet(new PlantaCargoDet());
		empleadoPuesto.getPlantaCargoDet().setIdPlantaCargoDet(plantaCargoDet.getIdPlantaCargoDet());
		if (dto547.getEstado() != null) {
			if (dto547.getEstado().equalsIgnoreCase("CONTRATADO")) {
				empleadoPuesto.setContratado(true);
				empleadoPuesto.setDatosEspecificosByIdDatosEspEstado(new DatosEspecificos());
				empleadoPuesto.getDatosEspecificosByIdDatosEspEstado().setIdDatosEspecificos(idEstadoContratado);
			} else if (dto547.getEstado().equalsIgnoreCase("COMISIONADO")) {
				empleadoPuesto.setContratado(false);
				empleadoPuesto.setDatosEspecificosByIdDatosEspEstado(new DatosEspecificos());
				empleadoPuesto.getDatosEspecificosByIdDatosEspEstado().setIdDatosEspecificos(idEstadoComisionado);
			} else if (dto547.getEstado().equalsIgnoreCase("PERMANENTE")) {
				empleadoPuesto.setContratado(false);
				empleadoPuesto.setDatosEspecificosByIdDatosEspEstado(new DatosEspecificos());
				empleadoPuesto.getDatosEspecificosByIdDatosEspEstado().setIdDatosEspecificos(idEstadoPermanente);
			}
		}
		empleadoPuesto.setDatosEspecificosByIdDatosEspTipoIngresoMovilidad(new DatosEspecificos());
		empleadoPuesto.getDatosEspecificosByIdDatosEspTipoIngresoMovilidad().setIdDatosEspecificos(idEstadoInsertMasiva);
		empleadoPuesto.setDatosEspecificosByIdDatosEspTipoRegistro(new DatosEspecificos());
		empleadoPuesto.getDatosEspecificosByIdDatosEspTipoRegistro().setIdDatosEspecificos(idEstadoTipoRegistro);
		if (dto547.getDiscapaci() != null && dto547.getDiscapaci().matches("[0-9]+"))
			empleadoPuesto.setDiscapacitado(Integer.parseInt(dto547.getDiscapaci()));
		em.persist(empleadoPuesto);

		// 4.9
		if (!exiteLegajo(persona.getIdPersona())) {
			Legajos legajos = new Legajos();
			legajos.setPersona(persona);
			legajos.setFechaIngreso(new Date());
			legajos.setDatosEspecificosTipoIngreso(new DatosEspecificos());
			legajos.getDatosEspecificosTipoIngreso().setIdDatosEspecificos(idEstadoInsertMasiva);
			em.persist(legajos);
		}
		// 4.10
		paso5(empleadoPuesto.getIdEmpleadoPuesto(), dto547);

	}

	public List listadoAcopiar(Long idTipoNom, Long idCpt, String tabla, Boolean activo) {
		Query q =
			em.createQuery("select " + tabla + " from " + tabla + " " + tabla + " where " + tabla
				+ ".cpt.idCpt = :idCpt " + (activo ? " and " + tabla + ".activo is true" : ""));

		q.setParameter("idCpt", idCpt);
		return q.getResultList();
	}

	public Integer calcNroOrden() {
		StringBuffer var1 = new StringBuffer();
		var1.append("SELECT Max(plantacarg0_.orden) FROM planificacion.planta_cargo_det ");
		var1.append(" plantacarg0_ WHERE plantacarg0_.activo = TRUE AND plantacarg0_.id_configuracion_uo_det = :idUndOrg ");

		Query q = em.createNativeQuery(var1.toString());
		q.setParameter("idUndOrg", nivelEntidadOeeUtil.getIdUnidadOrganizativa());
		Object obj = q.getSingleResult();
		if (obj == null)
			return 1;

		return ((Integer) q.getSingleResult()).intValue() + 1;
	}

	@SuppressWarnings("unchecked")
	public void updateCpt() {
		String cadena =
			" select cpt.* from planificacion.cpt cpt " + "where cpt.activo is true "
				+ "and cpt.id_tipo_cpt = " + idTipoCpt;

		List<Cpt> lista = new ArrayList<Cpt>();
		lista = em.createNativeQuery(cadena, Cpt.class).getResultList();
		cptSelecItem = new ArrayList<SelectItem>();
		cptSelecItem.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
		if (lista.size() > 0) {

			for (Cpt cpt : lista) {
				cptSelecItem.add(new SelectItem(cpt.getIdCpt(), cpt.getDenominacion()));
			}
		}

		idCpt = null;
		codigoCpt = null;
		idCptFromList = null;
	}

	/**
	 * Valida cada linea pasada como parametro y si es correcta la misma la retorna separada en sus partes
	 * 
	 * @param linea
	 *            cadena de texto separada por comas
	 * @return
	 *//*
	private DTO547 precondCompos(String linea) {
		DTO547 dto547 = descomponerLinea(linea);
		if (dto547 != null)
			return dto547;
		return null;
	}

	public void limpiar() {
		nivelEntidadOeeUtil.limpiar();
		uFile = null;
		cType = null;
		fName = null;
		idCpt = null;
		idTipoCpt = null;
		codigoCpt = null;
	}

	/**
	 * Método que busca el CPT
	 *//*
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
				String cadena =
					" select cpt.* from planificacion.cpt cpt "
						+ "join planificacion.grado_salarial max "
						+ "on max.id_grado_salarial = cpt.id_grado_salarial_max "
						+ "join planificacion.grado_salarial min "
						+ "on min.id_grado_salarial = cpt.id_grado_salarial_min "
						+ "where cpt.nivel = " + nivelCpt + " and max.numero = " + gradoMax
						+ " and min.numero = " + gradoMin + " and cpt.numero = " + numero
						+ " and cpt.nro_especifico = " + nroEspecifico;
				if (idTipoCpt != null)
					cadena = cadena + " and id_tipo_cpt = " + idTipoCpt;
				List<Cpt> lista = new ArrayList<Cpt>();
				lista = em.createNativeQuery(cadena, Cpt.class).getResultList();
				cptSelecItem = new ArrayList<SelectItem>();
				cptSelecItem.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
				if (lista.size() > 0) {
					for (Cpt cpt : lista) {
						cptSelecItem.add(new SelectItem(cpt.getIdCpt(), cpt.getDenominacion()));
						idCpt = cpt.getIdCpt();
					}
				}

			}
		} catch (Exception e) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, "Ingrese un código válido");

		}
	}

	public String getUrlToPageCpt() {
		return "/planificacion/searchForms/CptList.xhtml?from=planificacion/puestosTrabajo/PlantaCargoDetEdit&tipoCpt="
			+ idTipoCpt;
	}

	public void obtenerCodigoCpt() {

		Cpt cptActual = new Cpt();
		cptActual = em.find(Cpt.class, idCpt);

		codigoCpt =
			cptActual.getNivel() + "." + cptActual.getGradoSalarialMin().getNumero() + "."
				+ cptActual.getGradoSalarialMax().getNumero() + "." + cptActual.getNumero() + "."
				+ cptActual.getNroEspecifico();

	}

	public Long getIdTipoCpt() {
		return idTipoCpt;
	}

	public void setIdTipoCpt(Long idTipoCpt) {
		this.idTipoCpt = idTipoCpt;
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

	public String getfName() {
		return fName;
	}

	public void setfName(String fName) {
		this.fName = fName;
	}

	public Long getIdCptFromList() {
		return idCptFromList;
	}

	public void setIdCptFromList(Long idCptFromList) {
		this.idCptFromList = idCptFromList;
	}

	public List<SelectItem> getCptSelecItem() {
		return cptSelecItem;
	}

	public void setCptSelecItem(List<SelectItem> cptSelecItem) {
		this.cptSelecItem = cptSelecItem;
	}

	// //
	private Integer progress;

	public Integer getProgress() {
		if (progress == null)
			progress = 0;
		else {
			progress = progress + (int) (Math.random() * 35);

			if (progress > 100)
				progress = 100;
		}

		return progress;
	}

	public void setProgress(Integer progress) {
		this.progress = progress;
	}

	public void onComplete() {
		statusMessages.add(Severity.INFO, "-----");
	}

	public void cancel() {
		progress = null;
	}

}*/
