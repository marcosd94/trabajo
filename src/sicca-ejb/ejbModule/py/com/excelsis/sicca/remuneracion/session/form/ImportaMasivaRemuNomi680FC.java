package py.com.excelsis.sicca.remuneracion.session.form;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import model.LineaPlanilla;

import org.apache.commons.io.FileUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.richfaces.model.UploadItem;

import py.com.excelsis.sicca.desvinculacion.session.form.DTO547;
import py.com.excelsis.sicca.entity.CamposLegajo;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.Cpt;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.EmpleadoPuesto;
import py.com.excelsis.sicca.entity.HistoricoRemuneraciones;
import py.com.excelsis.sicca.entity.Importacion;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.PlantillaEval;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.entity.RemunConfig;
import py.com.excelsis.sicca.entity.Remuneraciones;
import py.com.excelsis.sicca.entity.RemuneracionesMH;
import py.com.excelsis.sicca.entity.RemuneracionesMHTMP;
import py.com.excelsis.sicca.entity.SinObj;
import py.com.excelsis.sicca.seguridad.entity.Rol;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
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
@Name("importaMasivaRemuNomi680FC")
public class ImportaMasivaRemuNomi680FC {
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
	private String fName = null;
	List<String> lLineasArch;
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
	long remuneracionesLeidas;
	private Boolean mostrarModalRemplazo = false;
	private Boolean mostrarModalAgregar = false;
	private Boolean limpiarModales = false;
	private Boolean guardarArchivo = false;
	private Boolean uoCabGuardada = false;
	private ConfiguracionUoCab confUoCab;

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

	private DTO680 precondCompos(String linea) {
		DTO680 dto = DTO680.descomponerLinea(linea);
		if (dto != null)
			return dto;
		return null;
	}

	private DTO680Tmp precondCompos2(String linea) {
		DTO680Tmp dto = DTO680Tmp.descomponerLinea(linea);
		if (dto != null)
			return dto;
		return null;
	}

	public void limpiar() {
		nivelEntidadOeeUtil.limpiar();
		uFile = null;
		cType = null;
		fName = null;
		primeraVez = true;
		statusMessages.clear();
		mes = null;
		anho = null;
		registros = null;
		remuneracionesTotales = null;
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
		if (!fName.endsWith(".csv")) {
			statusMessages
					.add(Severity.ERROR,
							"Se espera archivo en formato CSV (valores separados por coma)");
			return false;
		}

		return true;
	}

	private Boolean validarCabecera() {
		String cabeceraMatriz = "ano;mes;nivel_entidad;entidad;cedula;nombres;apellidos;estado;objeto_gto;fuente_financiamiento;categoria;presupuestado;devengado;movimiento;fecha;cargo;descrip_concepto;linea;descrip_categoria;discapacidad";
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

	private Boolean validarCabeceraNUEVO() {
		String cabeceraMatriz = "ANO;MES;NIVEL_ENTI;ENTIDAD;OEE;LINEA;CEDULA;NOMBRES;APELLIDOS;ESTADO;REMUNERACION TOTAL;OBJETO_GTO;F.F.;CATEG;PRESUP;DEVENGADO;CONCEPTO;MOVIMIENTO;LUGAR;CARGO;FUNCION REAL QUE CUMPLE;CARGA;DISCAPACIDAD;TIPO;AÑO DE INGRESO";
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

	public boolean procesarLineas() {
		// DTO680 dto = new DTO680();
		DTO680Tmp dto = new DTO680Tmp();
		String o = null;
		cantDuplicado = 0;
		cantExito = 0;
		cantFracaso = 0;
		remuneracionesLeidas = 0;
		guardarArchivo = false;
		uoCabGuardada = false;
		ArrayList<DTO680Tmp> pla = new ArrayList<DTO680Tmp>();
		System.out.println("Antes de validar cabecera");
		if (!validarCabeceraNUEVO())
			return false;
		System.out.println("Valido cabecera");
		if (!registros
				.equalsIgnoreCase(Integer.toString(lLineasArch.size() - 1)))
			return false;
		for (int i = 1; i < lLineasArch.size(); i++) {
			o = lLineasArch.get(i);
			dto = precondCompos2(o);
			System.out.println("DTO" + dto);
			if (dto != null) {
				try {
					mesIterado = dto.getMes();
					// System.out.println("Procesa");
					if (dto.getNivelEnti().longValue() != nivelEntidadOeeUtil
							.getCodNivelEntidad().longValue()) {
						statusMessages.add(Severity.ERROR,
								"No coincide el Nivel ingresado");
						return false;
					}
					if (dto.getEntidad().longValue() != nivelEntidadOeeUtil
							.getCodSinEntidad().longValue()) {
						statusMessages.add(Severity.ERROR,
								"No coincide la Entidad ingresada");
						return false;
					}
					if (dto.getOee() != nivelEntidadOeeUtil.getOrdenUnidOrg()
							.intValue()) {
						statusMessages.add(Severity.ERROR,
								"No coincide la OEE ingresada");
						return false;
					}
					if (dto.getAnhio() != null
							&& dto.getAnhio().intValue() != anho.intValue()) {
						registrarImportacionRealizadaNUEVO(
								dto,
								"FRACASO",
								"La columna Año no coincide con lo seleccionado",
								"N", null);
						agregarEstadoMotivo(
								"FRACASO",
								"La columna Año no coincide con lo seleccionado",
								i);
						remuneracionesLeidas += dto.getPresup();
						cantFracaso += 1;
						continue;
					} else if (dto.getMes() != null
							&& dto.getMes().intValue() != mes.intValue()) {
						registrarImportacionRealizadaNUEVO(
								dto,
								"FRACASO",
								"La columna Mes no coincide con lo seleccionado",
								"N", null);
						agregarEstadoMotivo(
								"FRACASO",
								"La columna Mes no coincide con lo seleccionado",
								i);
						remuneracionesLeidas += dto.getPresup();
						cantFracaso += 1;
						continue;
					} else if (dto.getMovimiento() != null
							&& dto.getMovimiento().equalsIgnoreCase("A")) {
						registrarImportacionRealizadaNUEVO(dto, "FRACASO",
								"Corresponde a Alta, registre el Ingreso", "N",
								null);
						agregarEstadoMotivo("FRACASO",
								"Corresponde a Alta, registre el Ingreso", i);
						remuneracionesLeidas += dto.getPresup();
						cantFracaso += 1;
						continue;
					} else if (!existeObjetoGto(dto.getObjetoGto())) {
						// registrarImportacionRealizada(dto, "FRACASO",
						// "No existe código de Objeto de Gasto en BD", "N",
						// null);
						agregarEstadoMotivo("FRACASO",
								"No existe código de Objeto de Gasto en BD", i);
						remuneracionesLeidas += dto.getPresup();
						cantFracaso += 1;
						continue;
					} else if (((dto.getObjetoGto() == 111) || (dto
							.getObjetoGto() == 113))
							&& ((dto.getCateg() == null) || (dto.getCateg()
									.trim().equals("")))) {
						agregarEstadoMotivo("FRACASO",
								"Concepto de OG no válido", i);
						registrarImportacionRealizadaNUEVO(dto, "FRACASO",
								"Concepto de OG no válido", "N", null);
						remuneracionesLeidas += dto.getPresup();
						cantFracaso += 1;
						continue;
					} else {
						if (dto.getNombres().equalsIgnoreCase("VACANTE")
								&& dto.getApellidos().equalsIgnoreCase(
										"VACANTE")) {
							registrarImportacionRealizadaNUEVO(dto, "EXITOSO",
									"", "N", null);
							dto.setIdEp(0L);
							dto.setIndex(i);
							pla.add(dto);
							agregarEstadoMotivo("EXITOSO", "", i);
							remuneracionesLeidas += dto.getPresup();
							cantExito++;
						} else {
							PersonaDTO personaDTO = seleccionUtilFormController
									.buscarPersona(dto.getCedula(), "PARAGUAY");
							if (personaDTO.getHabilitarBtn() == null) {
								registrarImportacionRealizadaNUEVO(dto,
										"FRACASO", personaDTO.getMensaje(),
										"N", null);
								agregarEstadoMotivo("FRACASO",
										personaDTO.getMensaje(), i);
								remuneracionesLeidas += dto.getPresup();
								cantFracaso += 1;
								continue;
							} else if (personaDTO.getHabilitarBtn()) {
								registrarImportacionRealizadaNUEVO(dto,
										"FRACASO",
										"Persona no registrada en el SICCA",
										"N", null);
								agregarEstadoMotivo("FRACASO",
										"Persona no registrada en el SICCA", i);
								remuneracionesLeidas += dto.getPresup();
								cantFracaso += 1;
								continue;
							} else {
								String composNombrePersonWS[] = personaDTO
										.getPersona().getNombres()
										.split("[\\s]*");
								String composApellidosPersonWS[] = personaDTO
										.getPersona().getApellidos()
										.split("[\\s]*");
								String composNombrePersonDTO[] = dto
										.getNombres().split("[\\s]*");
								String composApellidosPersonDTO[] = dto
										.getApellidos().split("[\\s]*");
								// Primer nombre, primer apellido
								String nombreCompletoWS1 = composNombrePersonWS[0]
										+ " " + composApellidosPersonWS[0];
								String nombreCompletoDTO1 = composNombrePersonDTO[0]
										+ " " + composApellidosPersonDTO[0];
								// Segundo nombre, primer apellido
								String nombreCompletoWS2 = composNombrePersonWS[1]
										+ " " + composApellidosPersonWS[0];
								String nombreCompletoDTO2 = composNombrePersonDTO[1]
										+ " " + composApellidosPersonDTO[0];
								// Primer nombre, segundo apellido
								String nombreCompletoWS3 = composNombrePersonWS[0]
										+ " " + composApellidosPersonWS[1];
								String nombreCompletoDTO3 = composNombrePersonDTO[0]
										+ " " + composApellidosPersonDTO[1];
								// Segundo nombre, segundo apellido
								String nombreCompletoWS4 = composNombrePersonWS[1]
										+ " " + composApellidosPersonWS[1];
								String nombreCompletoDTO4 = composNombrePersonDTO[1]
										+ " " + composApellidosPersonDTO[1];

								if (nombreCompletoDTO1
										.equalsIgnoreCase(nombreCompletoWS1)
										|| nombreCompletoDTO2
												.equalsIgnoreCase(nombreCompletoWS2)
										|| nombreCompletoDTO3
												.equalsIgnoreCase(nombreCompletoWS3)
										|| nombreCompletoDTO4
												.equalsIgnoreCase(nombreCompletoWS4)) {

									EmpleadoPuesto ep = existeEnEmpleadoPuesto(
											personaDTO.getPersona()
													.getIdPersona(),
											nivelEntidadOeeUtil
													.getIdConfigCab());
									if (!uoCabGuardada) {
										confUoCab = ep.getPlantaCargoDet()
												.getConfiguracionUoDet()
												.getConfiguracionUoCab();
										uoCabGuardada = true;
									}

									if (ep != null) {
										Remuneraciones remuneraciones = existeEnRemuneracionesNUEVO(
												dto, ep.getIdEmpleadoPuesto());
										if (remuneraciones != null) {
											registrarImportacionRealizadaNUEVO(
													dto, "DUPLICADO",
													"El registro ya existe",
													"N", null);
											agregarEstadoMotivo("DUPLICADO",
													"El registro ya existe", i);
											remuneracionesLeidas += dto
													.getPresup();
											cantDuplicado += 1;
											continue;
										} else {
											// paso4NUEVO(dto,
											// ep.getIdEmpleadoPuesto(), i,"N");
											registrarImportacionRealizadaNUEVO(
													dto, "EXITOSO", "", "N",
													null);
											dto.setIdEp(ep
													.getIdEmpleadoPuesto());
											dto.setIndex(i);
											pla.add(dto);
											agregarEstadoMotivo("EXITOSO", "",
													i);
											remuneracionesLeidas += dto
													.getPresup();
											cantExito += 1;
										}
									} else {
										registrarImportacionRealizadaNUEVO(
												dto,
												"FRACASO",
												"Funcionario no registrado, registre el Ingreso",
												"N", null);
										agregarEstadoMotivo(
												"FRACASO",
												"Funcionario no registrado, registre el Ingreso",
												i);
										remuneracionesLeidas += dto.getPresup();
										cantFracaso += 1;
										continue;
									}
								} else {
									registrarImportacionRealizadaNUEVO(dto,
											"FRACASO",
											"Los datos no coinciden con la BD",
											"N", null);
									agregarEstadoMotivo("FRACASO",
											"Los datos no coinciden con la BD",
											i);
									remuneracionesLeidas += dto.getPresup();
									cantFracaso += 1;
									continue;
								}
							}
						}
					}
				} catch (Exception e) {
					agregarEstadoMotivo("FRACASO",
							"Error al procesar la linea", i);
					e.printStackTrace();
					statusMessages.add(Severity.ERROR,
							"No se pudo realizar la operación");
				}
			}
		}
		if (!remuneracionesTotales.equalsIgnoreCase(Long
				.toString(remuneracionesLeidas))) {
			pla.clear();
			em.clear();
			return false;
		}

		if (cantFracaso > 0) {
			pla.clear();
			em.clear();
			return true;
		}

		for (DTO680Tmp dtoRemu : pla) {
			corePaso4NUEVO(dtoRemu, dtoRemu.getIdEp(), dtoRemu.getIndex(), "N");
		}
		guardarArchivo = true;
		em.flush();
		em.clear();
		pla.clear();

		return true;
	}

	public void procesarLineasMH() {
		DTO680 dto = new DTO680();
		String o = null;
		System.out.println("Antes de validar cabecera");
		if (!validarCabecera())
			return;
		System.out.println("Valido cabecera");
		for (int i = 1; i < lLineasArch.size(); i++) {
			o = lLineasArch.get(i);
			dto = precondCompos(o);
			System.out.println("DTO" + dto);
			if (dto != null) {
				try {
					mesIterado = dto.getMes();
					System.out.println("Procesa");
					if (dto.getMovimiento() != null
							&& dto.getMovimiento().equalsIgnoreCase("A")) {
						registrarImportacionRealizada(dto, "FRACASO",
								"Corresponde a Alta, registre el Ingreso", "N",
								null);
						agregarEstadoMotivo("FRACASO",
								"Corresponde a Alta, registre el Ingreso", i);
						cantFracaso += 1;
						continue;
					} else if (dto.getObjetoGto() != null
							&& (dto.getObjetoGto() != 133 || dto.getObjetoGto() != 137)
							&& (dto.getDescripConcepto() != null && !dto
									.getDescripConcepto().trim().equals(""))) {
						agregarEstadoMotivo("FRACASO",
								"Concepto de OG no válido", i);
						registrarImportacionRealizada(dto, "FRACASO",
								"Concepto de OG no válido", "N", null);
						cantFracaso += 1;
						continue;
					} else {
						PersonaDTO personaDTO = seleccionUtilFormController
								.buscarPersona(dto.getCedula(), "PARAGUAY");
						if (personaDTO.getHabilitarBtn() == null) {
							registrarImportacionRealizada(dto, "FRACASO",
									personaDTO.getMensaje(), "N", null);
							agregarEstadoMotivo("FRACASO",
									personaDTO.getMensaje(), i);
							cantFracaso += 1;
							continue;
						} else if (personaDTO.getHabilitarBtn()) {
							registrarImportacionRealizada(dto, "FRACASO",
									"Persona no registrada en el SICCA", "N",
									null);
							agregarEstadoMotivo("FRACASO",
									"Persona no registrada en el SICCA", i);
							cantFracaso += 1;
							continue;
						} else {
							String composNombrePersonWS[] = personaDTO
									.getPersona().getNombres().split("[\\s]*");
							String composApellidosPersonWS[] = personaDTO
									.getPersona().getApellidos()
									.split("[\\s]*");
							String composNombrePersonDTO[] = dto.getNombres()
									.split("[\\s]*");
							String composApellidosPersonDTO[] = dto
									.getApellidos().split("[\\s]*");
							// Primer nombre, primer apellido
							String nombreCompletoWS1 = composNombrePersonWS[0]
									+ " " + composApellidosPersonWS[0];
							String nombreCompletoDTO1 = composNombrePersonDTO[0]
									+ " " + composApellidosPersonDTO[0];
							// Segundo nombre, primer apellido
							String nombreCompletoWS2 = composNombrePersonWS[1]
									+ " " + composApellidosPersonWS[0];
							String nombreCompletoDTO2 = composNombrePersonDTO[1]
									+ " " + composApellidosPersonDTO[0];
							// Primer nombre, segundo apellido
							String nombreCompletoWS3 = composNombrePersonWS[0]
									+ " " + composApellidosPersonWS[1];
							String nombreCompletoDTO3 = composNombrePersonDTO[0]
									+ " " + composApellidosPersonDTO[1];

							// Segundo nombre, segundo apellido
							String nombreCompletoWS4 = composNombrePersonWS[1]
									+ " " + composApellidosPersonWS[1];
							String nombreCompletoDTO4 = composNombrePersonDTO[1]
									+ " " + composApellidosPersonDTO[1];

							if (nombreCompletoDTO1
									.equalsIgnoreCase(nombreCompletoWS1)
									|| nombreCompletoDTO2
											.equalsIgnoreCase(nombreCompletoWS2)
									|| nombreCompletoDTO3
											.equalsIgnoreCase(nombreCompletoWS3)
									|| nombreCompletoDTO4
											.equalsIgnoreCase(nombreCompletoWS4)) {
								EmpleadoPuesto ep = existeEnEmpleadoPuesto(
										personaDTO.getPersona().getIdPersona(),
										nivelEntidadOeeUtil.getIdConfigCab());
								if (ep != null) {
									Remuneraciones remuneraciones = existeEnRemuneraciones(
											dto, ep.getIdEmpleadoPuesto());
									if (remuneraciones != null) {
										registrarImportacionRealizada(dto,
												"DUPLICADO",
												"El registro ya existe", "N",
												null);
										agregarEstadoMotivo("DUPLICADO",
												"El registro ya existe", i);
										cantDuplicado += 1;
										continue;
									} else {
										paso4(dto, ep.getIdEmpleadoPuesto(), i,
												"N");
										cantExito += 1;
									}
								} else {
									registrarImportacionRealizada(
											dto,
											"FRACASO",
											"Funcionario no registrado, registre el Ingreso",
											"N", null);
									agregarEstadoMotivo(
											"FRACASO",
											"Funcionario no registrado, registre el Ingreso",
											i);
									cantFracaso += 1;
									continue;
								}
							} else {
								registrarImportacionRealizada(dto, "FRACASO",
										"Los datos no coinciden con la BD",
										"N", null);
								agregarEstadoMotivo("FRACASO",
										"Los datos no coinciden con la BD", i);
								cantFracaso += 1;
								continue;
							}
						}
					}
				} catch (Exception e) {
					agregarEstadoMotivo("FRACASO",
							"Error al procesar la linea", i);
					e.printStackTrace();
					statusMessages.add(Severity.ERROR,
							"No se pudo realizar la operación");
				}
			}
		}
	}

	private List<Remuneraciones> listaRemuMesAnhoOee() {
		Query q = em
				.createQuery("select Remuneraciones  from Remuneraciones Remuneraciones "
						+ " where Remuneraciones.mes = :mes and Remuneraciones.anho = :anho "
						+ " and Remuneraciones.empleadoPuesto.plantaCargoDet.configuracionUoDet.configuracionUoCab.idConfiguracionUo = :idConfiguracionUo");
		q.setParameter("idConfiguracionUo",
				nivelEntidadOeeUtil.getIdConfigCab());
		q.setParameter("mes", mes);
		q.setParameter("anho", anho);
		List<Remuneraciones> lista = q.getResultList();
		return lista;

	}

	public void borrarTemporal() {
		Query q = em
				.createNativeQuery("truncate table remuneracion.remuneraciones_mh_tmp");
		q.executeUpdate();
	}

	private Boolean verificarExisteRegistro() {
		List lista = listaRemuMesAnhoOee();
		if (lista.size() > 0) {
			// Existe
			return true;
		}
		// No existe
		return false;
	}

	private void procesarArchivo(String tipo) throws IOException,
			ParseException {
		if (tipo.equalsIgnoreCase("SI")) {
			borrarRegistrosAnteriores();
			massiveImport();
			limpiarModales();
			// limpiarModales = true;

		} else if (tipo.equalsIgnoreCase("NO")) {
			mostrarModalAgregar = true;
			mostrarModalRemplazo = false;
		}
	}

	public void procesarRespuestaModal(String tipo) throws IOException,
			ParseException {
		if (mostrarModalRemplazo) {
			procesarArchivo(tipo);

		}

		else if (!mostrarModalRemplazo && tipo.equals("SI")) {

			statusMessages.add(Severity.WARN,
					"El archivo ya fue reemplazado, verifique");
		}

	}

	public Boolean getMostrarModalAgregar() {
		return mostrarModalAgregar;
	}

	public void setMostrarModalAgregar(Boolean mostrarModalAgregar) {
		this.mostrarModalAgregar = mostrarModalAgregar;
	}

	private void borrarRegistrosAnteriores() {
		List<Remuneraciones> lista = listaRemuMesAnhoOee();
		for (Remuneraciones o : lista) {
			em.remove(o);
		}

	}

	public void massiveImport2() throws IOException, ParseException {
		if (verificarExisteRegistro()) {
			mostrarModalRemplazo = true;
			return;
		} else {
			mostrarModalRemplazo = false;
			mostrarModalRemplazo = false;
			massiveImport();
		}
	}

	public void limpiarModales() {
		mostrarModalAgregar = false;
		mostrarModalRemplazo = false;
		limpiarModales = false;
	}

	public void massiveImport() throws IOException, ParseException {
		if (!precondInsert()) {
			return;
		}
		limpiarModales();
		UploadItem fileItem = seleccionUtilFormController.crearUploadItem(
				fName, uFile.length, cType, uFile);
		lLineasArch = FileUtils.readLines(fileItem.getFile(), "ISO-8859-1");
		if (procesarLineas()) {
			agregarResumen();
			SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
			String nArchivo = sdf2.format(new Date()) + "_" + fName;
			File archSalida = new File(nArchivo);
			FileUtils.writeLines(archSalida, lLineasArch);
			JasperReportUtils.respondFile(archSalida, nArchivo, false,
					"application/vnd.ms-excel");
			if (guardarArchivo) {
				String direccion = AdmDocAdjuntoFormController
						.ponerBarraSimple("\\vol01\\SICCA\\SICCA\\REMUNERACIONES\\OEE_"
								+ nivelEntidadOeeUtil.getIdConfigCab()
										.toString());
				AdmDocAdjuntoFormController.guardarArchivo(nArchivo,
						archSalida, ".csv", direccion);
				guardarArchivo = false;
			}
			em.flush();
			if (cantFracaso < 1) {
				enviarMailUsuario(mesIterado);
				enviarMail(mesIterado);
			}
		} else {
			if (!registros
					.equalsIgnoreCase(Integer.toString(lLineasArch.size() - 1)))
				statusMessages
						.add(Severity.ERROR,
								"Número de registros en archivo no coincide con Cantidad de Registros ingresada");
			else if (!remuneracionesTotales.equalsIgnoreCase(Long
					.toString(remuneracionesLeidas))) {
				statusMessages
						.add(Severity.ERROR,
								"Sumatoria de remuneraciones totales no coincide con Total Remuneraciones ingresado");
			} else if (mesIterado != null)
				statusMessages.add(Severity.INFO, "Archivo inválido");
			else
				statusMessages.add(Severity.ERROR,
						"No se pudo realizar la operación");
		}
	}

	public void procesarLineasMH(List<String> lLineasArch, Boolean enviarMail)
			throws IOException, ParseException {

		this.lLineasArch = lLineasArch;
		procesarLineasMH();
		agregarResumen();
		if (lLineasArch.size() > 0) {
			SimpleDateFormat sdf2 = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss");
			String nArchivo = "MH_" + sdf2.format(new Date()) + ".csv";
			File archSalida = new File(nArchivo);
			FileUtils.writeLines(archSalida, lLineasArch);
			JasperReportUtils.respondFile(archSalida, nArchivo, false,
					"application/vnd.ms-excel");
			em.flush();
			enviarMail(mesIterado);
		} else {
			if (mesIterado != null)
				statusMessages.add(Severity.INFO, "Archivo inválido");
			else
				statusMessages.add(Severity.ERROR,
						"No se pudo realizar la operación");
		}

	}

	public void procesarLineasMHTabla(LineaPlanilla[] lLineasArch)
			throws IOException, ParseException {
		try {
			int i = 0;
			int contadorGeneral = 0;
			for (LineaPlanilla dto : lLineasArch) {
				RemuneracionesMH remu = new RemuneracionesMH();
				remu.setPlanillaUniqueId(new Long(dto.getPlanillaUniqueId()));
				remu.setFilaId(new Long(dto.getFilaId()));
				remu.setCedula(dto.getCedula());
				remu.setAnio(dto.getAnio());
				remu.setNivel(dto.getNivel());
				remu.setEntidad(dto.getEntidad());
				remu.setPrograma(dto.getPrograma());
				remu.setMes(dto.getMes());
				remu.setNombres(dto.getNombres());
				remu.setLineaPresupuestaria(dto.getLineaPresupuestaria());
				remu.setCodObjeto(dto.getCodObjeto().toString());
				remu.setObjetoGasto(dto.getObjetoGasto());
				remu.setFuenteFinanc(dto.getFuenteFinanc());
				remu.setOrgFinanciador(dto.getOrgFinanciador());
				remu.setCategoria(dto.getCategoria());
				remu.setDescripCategoria(dto.getDescripCategoria());
				remu.setMontoPresupuestado(new Double(dto
						.getMontoPresupuestado()));
				remu.setMontoDevengado(new Double(dto.getMontoDevengado()));
				remu.setMontoLiquido(new Double(dto.getMontoLiquido()));
				remu.setFechaActualizacion(dto.getFechaActualizacion());

				em.persist(remu);
				i++;
				contadorGeneral++;
				if (i == 1000) {
					em.flush();
					em.clear();
					i = 0;
					System.out.println("Se inserto " + contadorGeneral
							+ " registros");
				}
			}
			em.flush();
			System.out.println("Se inserto " + contadorGeneral + " registros");

		} catch (Exception e) {
			statusMessages.add(Severity.ERROR,
					"No se pudo realizar la operación");

		}
		statusMessages.add(Severity.INFO,
				"Se realizo la importacion correctamente");

	}

	public void procesarLineasMHTabla(ArrayList<LineaPlanilla> lLineasArch)
			throws IOException, ParseException {
		try {
			int i = 0;
			for (LineaPlanilla dto : lLineasArch) {
				RemuneracionesMH remu = new RemuneracionesMH();
				remu.setPlanillaUniqueId(new Long(dto.getPlanillaUniqueId()));
				remu.setFilaId(new Long(dto.getFilaId()));
				remu.setCedula(dto.getCedula());
				remu.setAnio(dto.getAnio());
				remu.setNivel(dto.getNivel());
				remu.setEntidad(dto.getEntidad());
				remu.setPrograma(dto.getPrograma());
				remu.setMes(dto.getMes());
				remu.setNombres(dto.getNombres());
				remu.setLineaPresupuestaria(dto.getLineaPresupuestaria());
				remu.setCodObjeto(dto.getCodObjeto().toString());
				remu.setObjetoGasto(dto.getObjetoGasto());
				remu.setFuenteFinanc(dto.getFuenteFinanc());
				remu.setOrgFinanciador(dto.getOrgFinanciador());
				remu.setCategoria(dto.getCategoria());
				remu.setDescripCategoria(dto.getDescripCategoria());
				remu.setMontoPresupuestado(new Double(dto
						.getMontoPresupuestado()));
				remu.setMontoDevengado(new Double(dto.getMontoDevengado()));
				remu.setMontoLiquido(new Double(dto.getMontoLiquido()));
				remu.setFechaActualizacion(dto.getFechaActualizacion());

				em.persist(remu);
				i++;
				if (i == 1000) {
					em.flush();
					em.clear();
					i = 0;
					System.out.println("Se inserto 1000 registros");
				}
			}
			em.flush();

		} catch (Exception e) {
			statusMessages.add(Severity.ERROR,
					"No se pudo realizar la operación");

		}
		statusMessages.add(Severity.INFO,
				"Se realizo la importacion correctamente");

	}

	public void procesarLineasMHTablaParticionado(
			ArrayList<LineaPlanilla> lLineasArch) throws IOException,
			ParseException {
		try {
			int i = 0;
			for (LineaPlanilla dto : lLineasArch) {
				RemuneracionesMHTMP remu = new RemuneracionesMHTMP();
				remu.setPlanillaUniqueId(new Long(dto.getPlanillaUniqueId()));
				remu.setFilaId(new Long(dto.getFilaId()));
				remu.setCedula(dto.getCedula());
				remu.setAnio(dto.getAnio());
				remu.setNivel(dto.getNivel());
				remu.setEntidad(dto.getEntidad());
				remu.setPrograma(dto.getPrograma());
				remu.setMes(dto.getMes());
				remu.setNombres(dto.getNombres());
				remu.setLineaPresupuestaria(dto.getLineaPresupuestaria());
				remu.setCodObjeto(dto.getCodObjeto().toString());
				remu.setObjetoGasto(dto.getObjetoGasto());
				remu.setFuenteFinanc(dto.getFuenteFinanc());
				remu.setOrgFinanciador(dto.getOrgFinanciador());
				remu.setCategoria(dto.getCategoria());
				remu.setDescripCategoria(dto.getDescripCategoria());
				remu.setMontoPresupuestado(new Double(dto
						.getMontoPresupuestado()));
				remu.setMontoDevengado(new Double(dto.getMontoDevengado()));
				remu.setMontoLiquido(new Double(dto.getMontoLiquido()));
				remu.setFechaActualizacion(dto.getFechaActualizacion());
				remu.setTipoPresupuesto(dto.getTipoPresupuesto());
				remu.setFechaDescarga(new Date());
				em.persist(remu);

			}
			em.flush();
			em.clear();

		} catch (Exception e) {
			statusMessages.add(Severity.ERROR,
					"No se pudo realizar la operación");

		}

	}

	public void procesarLineasMHTablaParticionadoMap(
			ArrayList<LineaPlanilla> lLineasArch) throws Exception {
		try {
			int i = 0;
			for (LineaPlanilla dto : lLineasArch) {
				RemuneracionesMHTMP remu = new RemuneracionesMHTMP();
				remu.setPlanillaUniqueId(dto.getPlanillaUniqueId().longValue());
				remu.setFilaId(dto.getFilaId().longValue());
				remu.setCedula(dto.getCedula());
				remu.setAnio(dto.getAnio());
				remu.setNivel(dto.getNivel());
				remu.setEntidad(dto.getEntidad());
				remu.setPrograma(dto.getPrograma());
				remu.setMes(dto.getMes());
				remu.setNombres(dto.getNombres());
				remu.setLineaPresupuestaria(dto.getLineaPresupuestaria());
				remu.setCodObjeto(dto.getCodObjeto().toString());
				remu.setObjetoGasto(dto.getObjetoGasto());
				remu.setFuenteFinanc(dto.getFuenteFinanc());
				remu.setOrgFinanciador(dto.getOrgFinanciador());
				remu.setCategoria(dto.getCategoria());
				remu.setDescripCategoria(dto.getDescripCategoria());
				remu.setMontoPresupuestado(dto.getMontoPresupuestado()
						.doubleValue());
				remu.setMontoDevengado(dto.getMontoDevengado().doubleValue());
				remu.setMontoLiquido(dto.getMontoLiquido().doubleValue());
				remu.setFechaActualizacion(dto.getFechaActualizacion());
				remu.setTipoPresupuesto(dto.getTipoPresupuesto());
				remu.setFechaDescarga(new Date());
				em.persist(remu);

			}
			em.flush();
			em.clear();

		} catch (Exception e) {
			statusMessages.add(Severity.ERROR,
					"No se pudo realizar la operaci—n");
			e.printStackTrace();
			throw e;

		}

	}

	public boolean finalizarProceso() {
		try {

			Query q = em
					.createNativeQuery("insert into remuneracion.remuneraciones_mh_historico "
							+ " select * from remuneracion.remuneraciones_mh d "
							+ " where   exists ( select * from remuneracion.remuneraciones_mh_tmp t where d.planilla_unique_id = t.planilla_unique_id); "
							+

							" delete from remuneracion.remuneraciones_mh d "
							+ " where   exists ( select * from remuneracion.remuneraciones_mh_tmp t where d.planilla_unique_id = t.planilla_unique_id); "
							+

							" insert into remuneracion.remuneraciones_mh "
							+ " select * from remuneracion.remuneraciones_mh_tmp t "
							+ " where not  exists ( select * from remuneracion.remuneraciones_mh d where d.planilla_unique_id = t.planilla_unique_id)");
			q.executeUpdate();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private List<RemunConfig> traerDestinatatioriosMail() {
		Query q = em
				.createQuery("select RemunConfig from RemunConfig RemunConfig where RemunConfig.activo is true and "
						+ " RemunConfig.configuracionUoCab.idConfiguracionUo = :idUO ");
		q.setParameter("idUO", nivelEntidadOeeUtil.getIdConfigCab());
		return q.getResultList();
	}

	public void enviarMail(Integer mesIterado) {
		List<RemunConfig> lDestinatarios = traerDestinatatioriosMail();
		for (RemunConfig o : lDestinatarios) {
			VelocityEngine ve = new VelocityEngine();
			java.util.Properties p = new java.util.Properties();
			p.setProperty("resource.loader", "class");
			p.setProperty("class.resource.loader.class",
					"org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
			ve.init(p);
			VelocityContext context = new VelocityContext();
			context.put(
					"tabla",
					genTablaMail(mesIterado,
							nivelEntidadOeeUtil.getIdConfigCab()));
			context.put("denoUnidad",
					nivelEntidadOeeUtil.getDenominacionUnidad());
			Template t = ve.getTemplate("/templates/email680.vm");
			StringWriter writer = new StringWriter();
			t.merge(context, writer);
			writer.toString();
			seleccionUtilFormController
					.enviarMails(
							o.getEMail(),
							writer.toString(),
							" Portal Paraguay Concursa – SICCA: Importación Masiva: "
									+ nivelEntidadOeeUtil
											.getDenominacionUnidad(), null);
		}
	}

	public void enviarMailUsuario(Integer mesIterado) {

		String emailUsuarioLogueado = usuarioLogueado.getPersona().getEMail();
		VelocityEngine ve = new VelocityEngine();
		java.util.Properties p = new java.util.Properties();
		p.setProperty("resource.loader", "class");
		p.setProperty("class.resource.loader.class",
				"org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
		ve.init(p);
		VelocityContext context = new VelocityContext();
		context.put("tabla",
				genTablaMail(mesIterado, nivelEntidadOeeUtil.getIdConfigCab()));
		context.put("denoUnidad", nivelEntidadOeeUtil.getDenominacionUnidad());
		Template t = ve.getTemplate("/templates/email680.vm");
		StringWriter writer = new StringWriter();
		t.merge(context, writer);
		writer.toString();
		seleccionUtilFormController.enviarMails(emailUsuarioLogueado,
				writer.toString(),
				" Portal Paraguay Concursa  SICCA: Importaci—n Masiva: "
						+ nivelEntidadOeeUtil.getDenominacionUnidad(), null);
	}

	private String genTablaMail(Integer mes, Long idUO) {
		List<DTO680Stats> lista = traerResumenImportacion(mes, idUO);
		if (lista.size() == 0) {
			return "";
		}
		String respuesta = "<table  border='1'>";
		respuesta += " <tr><th>Estado</th><th>Motivo</th><th>Cantidad</th></tr>";
		for (DTO680Stats o : lista) {
			respuesta += "<t>";
			respuesta += "<td>" + o.getEstadoImport() + "</td>";
			respuesta += "<td>" + o.getMotivo() + "</td>";
			respuesta += "<td>" + o.getCantProcesada() + "</td>";
			respuesta += "</tr>";
		}
		respuesta += "</table>";
		return respuesta;
	}

	private List<DTO680Stats> traerResumenImportacion(Integer mes, Long idUO) {
		StringBuffer var1 = new StringBuffer();
		var1.append(" SELECT DISTINCT oee.denominacion_unidad, I.estado_import, CASE WHEN I.motivo  ");
		var1.append(" IS NULL THEN 'SIN MOTIVO' ELSE I.motivo END MOTIVO, Count (*)  ");
		var1.append(" cantidad_procesada FROM remuneracion.importacion I JOIN planificacion.configuracion_uo_cab ");
		var1.append(" OEE ON I.id_configuracion_uo = OEE.id_configuracion_uo WHERE I.mes = :mes ");
		var1.append(" AND OEE.id_configuracion_uo = :idUO GROUP BY OEE.denominacion_unidad, ");
		var1.append(" I.estado_import, CASE WHEN I.motivo IS NULL THEN 'SIN MOTIVO' ELSE I.motivo ");
		var1.append(" END ");
		Query q = em.createNativeQuery(var1.toString());
		q.setParameter("mes", mes);
		q.setParameter("idUO", idUO);

		List<Object[]> lista = q.getResultList();
		List<DTO680Stats> listaStats = new ArrayList<DTO680Stats>();
		for (Object[] o : lista) {
			System.out.println(" --" + o[0].getClass() + " --"
					+ o[1].getClass() + " --" + o[2].getClass());
			listaStats.add(new DTO680Stats((String) o[0], (String) o[1],
					(String) o[2], (java.math.BigInteger) o[3]));
		}

		return listaStats;
	}

	private List<Remuneraciones> cargarRemuneraciones(Long idEP,
			ConfiguracionUoCab uoCab) {
		if (idEP != 0) {
			Query q = em
					.createQuery("select Remuneraciones from Remuneraciones Remuneraciones "
							+ " where Remuneraciones.empleadoPuesto.idEmpleadoPuesto = :idEP "
							+ " and Remuneraciones.activo is true");
			q.setParameter("idEP", idEP);
			return q.getResultList();
		} else {
			Query q = em
					.createQuery("select Remuneraciones from Remuneraciones Remuneraciones "
							+ " where Remuneraciones.empleadoPuesto.idEmpleadoPuesto is null "
							+ " and Remuneraciones.configuracionUo.idConfiguracionUo = :confUoCab.idConfiguracionUo "
							+ " and Remuneraciones.activo is true");
			q.setParameter("confUoCab", confUoCab);
			return q.getResultList();
		}
	}

	void corePaso4(DTO680 dto, Long idEP, int index, String origen) {
		List<Remuneraciones> lRemuneraciones = cargarRemuneraciones(idEP,
				confUoCab);
		Iterator<Remuneraciones> iter = lRemuneraciones.iterator();
		while (iter.hasNext()) {
			Remuneraciones remu = iter.next();
			HistoricoRemuneraciones hr = new HistoricoRemuneraciones();
			hr.setActivo(true);
			hr.setFechaAlta(new Date());
			hr.setUsuAlta("SYSTEM");
			hr.setEmpleadoPuesto(new EmpleadoPuesto());
			hr.setConfiguracionUo(confUoCab);
			hr.getEmpleadoPuesto().setIdEmpleadoPuesto(idEP);
			hr.setCategoria(remu.getCategoria());
			hr.setDescripCategoria(remu.getDescripCategoria());

			hr.setObjCodigo(remu.getObjCodigo());
			hr.setDescripConcepto(remu.getDescripConcepto());
			hr.setFuenteFinanciamiento(remu.getFuenteFinanciamiento());
			hr.setLinea(remu.getLinea());
			hr.setPresupuestado(remu.getPresupuestado());
			hr.setDevengado(remu.getDevengado());
			hr.setMes(remu.getMes());
			hr.setAnho(remu.getAnho());
			em.persist(hr);
			em.remove(remu);
		}
		Remuneraciones remu = new Remuneraciones();
		remu.setActivo(true);
		remu.setEmpleadoPuesto(new EmpleadoPuesto());
		remu.getEmpleadoPuesto().setIdEmpleadoPuesto(idEP);
		remu.setConfiguracionUo(confUoCab);
		remu.setCategoria(dto.getCateg());
		remu.setObjCodigo(dto.getObjetoGto());
		remu.setFuenteFinanciamiento(dto.getFuenteFinancia());
		remu.setDescripConcepto(dto.getDescripConcepto());
		remu.setPresupuestado(dto.getPresup());
		remu.setDevengado(dto.getDeven());
		remu.setMes(dto.getMes());
		remu.setAnho(dto.getAnhio());
		remu.setFechaAlta(new Date());
		remu.setUsuAlta("SYSTEM");
		em.persist(remu);

	}

	void corePaso4NUEVO(DTO680Tmp dto, Long idEP, int index, String origen) {
		List<Remuneraciones> lRemuneraciones = cargarRemuneraciones(idEP,
				confUoCab);
		Iterator<Remuneraciones> iter = lRemuneraciones.iterator();
		while (iter.hasNext()) {
			Remuneraciones remu = iter.next();
			HistoricoRemuneraciones hr = new HistoricoRemuneraciones();
			if (idEP != 0) {
				hr.setEmpleadoPuesto(new EmpleadoPuesto());
				hr.getEmpleadoPuesto().setIdEmpleadoPuesto(idEP);
			}
			hr.setObjCodigo(remu.getObjCodigo());
			hr.setCategoria(remu.getCategoria());
			hr.setAnho(remu.getAnho());
			hr.setMes(remu.getMes());
			hr.setPresupuestado(remu.getPresupuestado());
			hr.setDevengado(remu.getDevengado());
			hr.setActivo(true);
			hr.setUsuAlta("SYSTEM");
			hr.setFechaAlta(new Date());
			hr.setLinea(remu.getLinea());
			hr.setDescripConcepto(remu.getDescripConcepto());
			hr.setFuenteFinanciamiento(remu.getFuenteFinanciamiento());
			// hr.setDescripCategoria(remu.getDescripCategoria());
			hr.setRemuneracionTotal(remu.getRemuneracionTotal());
			hr.setLugar(remu.getLugar());
			hr.setCargaHoraria(remu.getCargaHoraria());
			hr.setMovimiento(remu.getMovimiento());

			em.persist(hr);
			em.remove(remu);
			em.flush();
		}
		Remuneraciones remu = new Remuneraciones();
		if (idEP != 0) {
			remu.setEmpleadoPuesto(new EmpleadoPuesto());
			remu.getEmpleadoPuesto().setIdEmpleadoPuesto(idEP);
		}
		remu.setObjCodigo(dto.getObjetoGto());
		remu.setCategoria(dto.getCateg());
		remu.setAnho(dto.getAnhio());
		remu.setMes(dto.getMes());
		remu.setPresupuestado(dto.getPresup());
		remu.setDevengado(dto.getDeven());
		remu.setActivo(true);
		remu.setUsuAlta("SYSTEM");
		remu.setFechaAlta(new Date());
		remu.setLinea(remu.getLinea());
		remu.setDescripConcepto(remu.getDescripConcepto());
		remu.setFuenteFinanciamiento(dto.getFuenteFinancia());
		remu.setRemuneracionTotal(remu.getRemuneracionTotal());
		remu.setLugar(remu.getLugar());
		remu.setCargaHoraria(remu.getCargaHoraria());
		remu.setMovimiento(remu.getMovimiento());

		em.persist(remu);

	}

	public void paso4(DTO680 dto, Long idEP, int index, String origen)
			throws ParseException {
		corePaso4(dto, idEP, index, origen);
		/* iii */
		String motivo = null;
		if (dto.getMovimiento() != null
				&& dto.getMovimiento().equalsIgnoreCase("B")) {
			motivo = "Corresponde a Baja, registre desvinculación de ser necesario";
		}
		registrarImportacionRealizada(dto, "EXITOSO", motivo, origen, null);
		agregarEstadoMotivo("EXITOSO", motivo, index);

	}

	public void paso4NUEVO(DTO680Tmp dto, Long idEP, int index, String origen)
			throws ParseException {
		corePaso4NUEVO(dto, idEP, index, origen);
		/* iii */
		String motivo = null;
		if (dto.getMovimiento() != null
				&& dto.getMovimiento().equalsIgnoreCase("B")) {
			motivo = "Corresponde a Baja, registre desvinculación de ser necesario";
		}
		registrarImportacionRealizadaNUEVO(dto, "EXITOSO", motivo, origen, null);
		agregarEstadoMotivo("EXITOSO", motivo, index);

	}

	public Remuneraciones existeEnRemuneraciones(DTO680 dto, Long idEP) {
		Query q = em
				.createQuery("select Remuneraciones from Remuneraciones Remuneraciones "
						+ " where Remuneraciones.empleadoPuesto.idEmpleadoPuesto = :idEP "
						+ " and Remuneraciones.anho = :anho "
						+ " and Remuneraciones.mes  = :mes"
						+ " and Remuneraciones.categoria = :idCatego "
						+ " and Remuneraciones.presupuestado = :presupuestado "
						+ " and Remuneraciones.objCodigo = :objCodigo ");
		q.setParameter("anho", dto.getAnhio());
		q.setParameter("mes", dto.getMes());
		q.setParameter("idCatego", dto.getCateg());
		q.setParameter("presupuestado", dto.getPresup());
		q.setParameter("idEP", idEP);
		q.setParameter("objCodigo", dto.getObjetoGto());
		List<Remuneraciones> lista = q.getResultList();
		if (lista.size() > 0)
			return lista.get(0);
		return null;
	}

	public Remuneraciones existeEnRemuneracionesNUEVO(DTO680Tmp dto, Long idEP) {
		Query q = em
				.createQuery("select Remuneraciones from Remuneraciones Remuneraciones "
						+ " where Remuneraciones.empleadoPuesto.idEmpleadoPuesto = :idEP "
						+ " and Remuneraciones.anho = :anho "
						+ " and Remuneraciones.mes  = :mes"
						+ " and Remuneraciones.categoria = :idCatego "
						+ " and Remuneraciones.presupuestado = :presupuestado "
						+ " and Remuneraciones.objCodigo = :objCodigo ");
		q.setParameter("anho", dto.getAnhio());
		q.setParameter("mes", dto.getMes());
		q.setParameter("idCatego", dto.getCateg());
		q.setParameter("presupuestado", dto.getPresup());
		q.setParameter("idEP", idEP);
		q.setParameter("objCodigo", dto.getObjetoGto());
		List<Remuneraciones> lista = q.getResultList();
		if (lista.size() > 0)
			return lista.get(0);
		return null;
	}

	public EmpleadoPuesto existeEnEmpleadoPuesto(Long idPersona, Long idUo) {
		Query q = em
				.createQuery("select EmpleadoPuesto from EmpleadoPuesto EmpleadoPuesto "
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

	private void registrarImportacionRealizada(DTO680 dto, String estadoImport,
			String motivo, String origen, Long idEmpleadoPuesto)
			throws ParseException {
		Importacion importacion = new Importacion();
		importacion.setMes(dto.getMes());
		importacion.setAnho(dto.getAnhio());
		importacion.setNivel(dto.getNivelEnti());
		importacion.setEntidad(dto.getEntidad());
		importacion.setDocumentoIdentidad(dto.getCedula());
		importacion.setNombres(dto.getNombres());
		importacion.setApellidos(dto.getApellidos());
		importacion.setEstado(dto.getEstado());
		importacion.setObjetoGto(dto.getObjetoGto());
		importacion.setCategoria(dto.getCateg());
		importacion.setPresupuestado(dto.getPresup());
		importacion.setDevengado(dto.getDeven());
		importacion.setMovimiento(dto.getMovimiento());
		if (dto.getFecha() != null)
			importacion.setFecha(sdf.parse(dto.getFecha()));
		importacion.setCargo(dto.getCargo());
		importacion.setDiscapacidad(dto.getDiscapaci());

		importacion.setDescripCategoria(dto.getDescripCatego());
		importacion.setDescripConcepto(dto.getDescripConcepto());
		importacion.setLinea(dto.getLinea());

		importacion.setEstadoImport(estadoImport);
		importacion.setMotivo(motivo);
		importacion.setUsuAlta("SYSTEM");
		importacion.setFechaAlta(new Date());
		importacion.setOrigen(origen);

		importacion.setConfiguracionUoCab(new ConfiguracionUoCab());
		importacion.getConfiguracionUoCab().setIdConfiguracionUo(
				nivelEntidadOeeUtil.getIdConfigCab());
		if (idEmpleadoPuesto != null) {
			importacion.setEmpleadoPuesto(new EmpleadoPuesto());
			importacion.getEmpleadoPuesto().setIdEmpleadoPuesto(
					idEmpleadoPuesto);
		}

		em.persist(importacion);
	}

	private void registrarImportacionRealizadaNUEVO(DTO680Tmp dto,
			String estadoImport, String motivo, String origen,
			Long idEmpleadoPuesto) throws ParseException {
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
		if (!dto.getNombres().equalsIgnoreCase("VACANTE")) {
			if (dto.getDiscapaci() == null)
				importacion.setDiscapacidad("N");
			else {
				if ((dto.getDiscapaci().equals("S"))
						|| (dto.getDiscapaci().equals("SI"))) {
					importacion.setDiscapacidad("S");
					importacion.setTipoDiscapacidad(dto.getTipoDiscapaci());
				}
				if ((dto.getDiscapaci().equals("N"))
						|| (dto.getDiscapaci().equals("NO")))
					importacion.setDiscapacidad("N");
			}
		}
		importacion.setTipoDiscapacidad(dto.getTipoDiscapaci());
		importacion.setAnhoIngreso(dto.getAnhioIngreso());
		importacion.setConcepto(dto.getConcep());

		/*
		 * if (dto.getFecha() != null)
		 * importacion.setFecha(sdf.parse(dto.getFecha()));
		 * 
		 * 
		 * importacion.setDescripCategoria(dto.getDescripCatego());
		 */

		importacion.setEstadoImport(estadoImport);
		importacion.setMotivo(motivo);
		importacion.setUsuAlta("SYSTEM");
		importacion.setFechaAlta(new Date());
		importacion.setOrigen(origen);

		importacion.setConfiguracionUoCab(new ConfiguracionUoCab());
		importacion.getConfiguracionUoCab().setIdConfiguracionUo(
				nivelEntidadOeeUtil.getIdConfigCab());

		if (idEmpleadoPuesto != null) {
			importacion.setEmpleadoPuesto(new EmpleadoPuesto());
			importacion.getEmpleadoPuesto().setIdEmpleadoPuesto(
					idEmpleadoPuesto);
		}

		em.persist(importacion);
	}

	private void agregarEstadoMotivo(String estado, String motivo, int index) {
		if (motivo == null || motivo.trim().equals("null")) {
			motivo = "";
		}
		String cSeparador = ";";
		String linea = lLineasArch.get(index);
		lLineasArch.set(index, linea + cSeparador + estado + cSeparador
				+ motivo);
	}

	private void agregarResumen() {
		String cSeparador = ";";
		lLineasArch.add("Cantidad de Registros Insertados: " + cSeparador
				+ cantExito);
		lLineasArch.add("Cantidad de Registros con Fracaso: " + cSeparador
				+ cantFracaso);
		lLineasArch.add("Cantidad de Registros Duplicados: " + cSeparador
				+ cantDuplicado);
	}

	/* Select Items */

	public List<SelectItem> mesAnhoSI(String dominio) {
		Query q = em
				.createQuery("select Referencias from Referencias Referencias "
						+ " where Referencias.dominio = :dominio and Referencias.activo is true order by valorNum asc ");
		q.setParameter("dominio", dominio);
		List<Referencias> lista = q.getResultList();

		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString(
				"opt_select_one")));
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

	public String getfName() {
		return fName;
	}

	public void setfName(String fName) {
		this.fName = fName;
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

	private boolean existeObjetoGto(Integer codObjGto) {
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

}
