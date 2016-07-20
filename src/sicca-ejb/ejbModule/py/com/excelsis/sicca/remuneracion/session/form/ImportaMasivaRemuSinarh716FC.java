package py.com.excelsis.sicca.remuneracion.session.form;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;

import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.EmpleadoPuesto;
import py.com.excelsis.sicca.entity.Importacion;
import py.com.excelsis.sicca.entity.Remuneraciones;
import py.com.excelsis.sicca.session.util.PersonaDTO;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("importaMasivaRemuSinarh716FC")
public class ImportaMasivaRemuSinarh716FC {
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(create = true)
	ImportaMasivaRemuNomi680FC importaMasivaRemuNomi680FC;
	@In(value = "entityManager")
	EntityManager em;
	@In(create = true)
	SeleccionUtilFormController seleccionUtilFormController;
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

	public void init() {

	}

	private Boolean precondInsert() {
		if (importaMasivaRemuNomi680FC.lLineasArch == null
			|| importaMasivaRemuNomi680FC.lLineasArch.size() == 0) {
			System.out.println("NO HAY REGISTROS QUE PROCESAR");
			return false;

		}
		return true;
	}

	public Boolean procesarLineas(Long idUO) {
		DTO680 dto = new DTO680();
		String o = null;
		for (int i = 1; i < importaMasivaRemuNomi680FC.lLineasArch.size(); i++) {
			o = importaMasivaRemuNomi680FC.lLineasArch.get(i);
			dto = DTO680.descomponerLinea2(o);
			if (dto != null) {
				try {
					PersonaDTO personaDTO =
						seleccionUtilFormController.buscarPersona(dto.getCedula(), "PARAGUAY");
					if (personaDTO.getHabilitarBtn() == null) {
						registrarImportacionRealizada(dto, "FRACASO", personaDTO.getMensaje(), "S", null, idUO);

						continue;
					} else if (personaDTO.getHabilitarBtn()) {
						registrarImportacionRealizada(dto, "FRACASO", "Persona no registrada en el SICCA", "S", null, idUO);

						continue;
					} else {
						String composNombrePersonWS[] =
							personaDTO.getPersona().getNombres().split("[\\s]*");
						String composApellidosPersonWS[] =
							personaDTO.getPersona().getApellidos().split("[\\s]*");
						String composNombrePersonDTO[] = dto.getNombres().split("[\\s]*");
						String composApellidosPersonDTO[] = dto.getApellidos().split("[\\s]*");
						String nombreCompletoWS =
							composNombrePersonWS[0] + " " + composApellidosPersonWS[0];
						String nombreCompletoDTO =
							composNombrePersonDTO[0] + " " + composApellidosPersonDTO[0];
						if (nombreCompletoDTO.equalsIgnoreCase(nombreCompletoWS)) {
							EmpleadoPuesto ep =
								importaMasivaRemuNomi680FC.existeEnEmpleadoPuesto(personaDTO.getPersona().getIdPersona(), idUO);
							if (ep != null) {
								Remuneraciones remuneraciones =
									importaMasivaRemuNomi680FC.existeEnRemuneraciones(dto,ep.getIdEmpleadoPuesto());
								if (remuneraciones != null) {
									registrarImportacionRealizada(dto, "FRACASO", "El registro ya existe", "S", null, idUO);

									continue;
								} else {
									paso4(dto, ep.getIdEmpleadoPuesto(), i, "S",idUO);
								}
							} else {
								registrarImportacionRealizada(dto, "FRACASO", "Funcionario no registrado, registre el Ingreso", "S", null, idUO);

								continue;
							}
						} else {
							registrarImportacionRealizada(dto, "FRACASO", "Los datos no coinciden con la BD", "S", null, idUO);

							continue;
						}
					}

				} catch (Exception e) {
					e.printStackTrace();
					statusMessages.add(Severity.ERROR, "No se pudo realizar la operación");
					return false;
				}
			}
		}
		return true;
	}

	public void paso4(DTO680 dto, Long idEP, int index, String origen, Long idUO)
			throws ParseException {
		importaMasivaRemuNomi680FC.corePaso4(dto, idEP, index, origen);
		/* iii */
		String motivo = null;
		if (dto.getMovimiento() != null && dto.getMovimiento().equalsIgnoreCase("B")) {
			motivo = "Corresponde a Baja, registre desvinculación de ser necesario";
		}
		registrarImportacionRealizada(dto, "EXITOSO", "Funcionario no registrado, registre el Ingreso", "S", null, idUO);

	}

	private void registrarImportacionRealizada(DTO680 dto, String estadoImport, String motivo, String origen, Long idEmpleadoPuesto, Long idUO)
			throws ParseException {
		Importacion importacion = new Importacion();
		importacion.setMes(dto.getMes());
		importacion.setAnho(dto.getAnhio());
		importacion.setNivel(dto.getNivelEnti());
		importacion.setEntidad(dto.getEntidad());
		importacion.setPrograma(dto.getPrograma());
		importacion.setPresupuesto(dto.getTipoPresupueso());
		importacion.setDocumentoIdentidad(dto.getCedula());
		importacion.setNombres(dto.getNombres());
		importacion.setApellidos(dto.getApellidos());
		importacion.setEstado(dto.getEstado());
		importacion.setObjetoGto(dto.getObjetoGto());
		importacion.setCategoria(dto.getCateg());
		importacion.setPresupuestado(dto.getPresup());
		if (dto.getDeven() != null)
			importacion.setDevengado(dto.getDeven());
		if (dto.getMovimiento() != null)
			importacion.setMovimiento(dto.getMovimiento());
		if (dto.getFecha() != null)
			importacion.setFecha(sdf.parse(dto.getFecha()));
		if (dto.getCargo() != null)
			importacion.setCargo(dto.getCargo());
		if (dto.getDiscapaci() != null)
			importacion.setDiscapacidad(dto.getDiscapaci());
		if (dto.getDescripCatego() != null)
			importacion.setDescripCategoria(dto.getDescripCatego());
		if (dto.getDescripConcepto() != null)
			importacion.setDescripConcepto(dto.getDescripConcepto());
		importacion.setLinea(dto.getLinea());		

		importacion.setEstadoImport(estadoImport);
		importacion.setMotivo(motivo);
		importacion.setUsuAlta("SYSTEM");
		importacion.setFechaAlta(new Date());
		importacion.setOrigen(origen);

		importacion.setConfiguracionUoCab(new ConfiguracionUoCab());
		importacion.getConfiguracionUoCab().setIdConfiguracionUo(idUO);
		if (idEmpleadoPuesto != null) {
			importacion.setEmpleadoPuesto(new EmpleadoPuesto());
			importacion.getEmpleadoPuesto().setIdEmpleadoPuesto(idEmpleadoPuesto);
		}

		em.persist(importacion);
	}

	public void massiveImport(Long idUo) throws IOException, ParseException {
		if (!precondInsert()) {
			return;
		}

		if (procesarLineas(idUo)) {
			em.flush();
			importaMasivaRemuNomi680FC.enviarMail(importaMasivaRemuNomi680FC.mesIterado);
		} else {
			statusMessages.add(Severity.ERROR, "No se pudo realizar la operación");
		}
	}

}
