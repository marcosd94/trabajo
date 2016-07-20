package py.com.excelsis.sicca.remuneracion.session.form;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import model.LineaPlanilla;

import org.apache.commons.beanutils.BeanUtils;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.MappingJsonFactory;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import com.csvreader.CsvReader;

import py.com.excelsis.sicca.entity.RemuneracionesMH;
import anexoserviceclient.AnexoServiceClient;
import client.ClienteMH;

@Scope(ScopeType.CONVERSATION)
@Name("clientServiceMH")
public class ClientServiceMH {
	@In(required = false, create = true)
	ImportAnexo750FC importAnexo750FC;
	@In(required = false, create = true)
	ImportaMasivaRemuNomi680FC importaMasivaremuNomi680FC;
	@In
	StatusMessages statusMessages;
	Integer anho = Calendar.getInstance().get(Calendar.YEAR);
	String nombreArchivo;

	public Integer getAnho() {
		return anho;
	}

	public void setAnho(Integer anho) {
		this.anho = anho;
	}

	public String getNombreArchivo() {
		return nombreArchivo;
	}

	public void setNombreArchivo(String nombreArchivo) {
		this.nombreArchivo = nombreArchivo;
	}

	@TransactionTimeout(9999999)
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void traerRegistrosRemuneracionesFROMJSON() {
		try {
			JsonFactory jsonFactory = new JsonFactory();
			JsonParser parser = jsonFactory.createJsonParser(new File(
					getNombreArchivo()));

			// Map where to store your field-value pairs per object
			Map<String, String> fields = new HashMap<String, String>();

			JsonToken token;
			ArrayList<LineaPlanilla> pla = new ArrayList<LineaPlanilla>();
			int cantRegistros = 0;
			int cantTotal = 0;
			importaMasivaremuNomi680FC = (ImportaMasivaRemuNomi680FC) org.jboss.seam.Component
					.getInstance(ImportaMasivaRemuNomi680FC.class, true);
			importaMasivaremuNomi680FC.borrarTemporal();
			while ((token = parser.nextToken()) != JsonToken.END_ARRAY) {
				switch (token) {

				// Starts a new object, clear the map
				case START_OBJECT:
					fields.clear();
					break;
				// For each field-value pair, store it in the map 'fields'
				case FIELD_NAME:
					String field = parser.getCurrentName();
					token = parser.nextToken();
					String value = parser.getText();
					if (field.equals("fechaActualizacion")) {

					}
					fields.put(field, value);
					break;
				// Do something with the field-value pairs
				case END_OBJECT:
					cantRegistros++;
					cantTotal++;
					pla.add(convertiraRemuneracionesBean(fields));
					break;
				}
				if (cantRegistros >= 1000) {
					importaMasivaremuNomi680FC
							.procesarLineasMHTablaParticionadoMap(pla);
					cantRegistros = 0;
					System.out
							.println("Se inserto " + cantTotal + " registros");
					pla.clear();
				}
			}
			importaMasivaremuNomi680FC.procesarLineasMHTablaParticionado(pla);
			System.out.println("Se inserto " + cantTotal + " registros");

			statusMessages
					.add(Severity.INFO,
							"Se realizo la importacion correctamente en las tablas temporales");
			if (!importaMasivaremuNomi680FC.finalizarProceso()) {
				statusMessages
						.add(Severity.ERROR,
								"No se pudo realizar el proceso en las tablas definitivas");
			} else {
				statusMessages.add(Severity.INFO,
						"Se realizo el proceso en las tablas definitivas");
			}
			parser.close();

		} catch (Exception e) {
			statusMessages.add(
					Severity.ERROR,
					"Archivo incorrecto o Error en el proceso "
							+ e.getMessage());
			e.printStackTrace();
		}
	}

	@TransactionTimeout(9999999)
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void traerRegistrosAnexoFROMJSON() {
		try {
			JsonFactory jsonFactory = new JsonFactory();
			JsonParser parser = jsonFactory.createJsonParser(new File(
					getNombreArchivo()));

			// Map where to store your field-value pairs per object
			Map<String, String> fields = new HashMap<String, String>();

			JsonToken token;
			ArrayList<LineaPlanilla> pla = new ArrayList<LineaPlanilla>();
			int cantRegistros = 0;
			int cantTotal = 0;
			importAnexo750FC = (ImportAnexo750FC) org.jboss.seam.Component
					.getInstance(ImportAnexo750FC.class, true);
			importAnexo750FC.borrarTemporal();
			statusMessages.clear();
			while ((token = parser.nextToken()) != JsonToken.END_ARRAY) {
				switch (token) {

				// Starts a new object, clear the map
				case START_OBJECT:
					fields.clear();
					break;
				// For each field-value pair, store it in the map 'fields'
				case FIELD_NAME:
					String field = parser.getCurrentName();
					token = parser.nextToken();
					String value = parser.getText();
					if (field.equals("fechaActualizacion")) {

					}
					fields.put(field, value);
					break;
				// Do something with the field-value pairs
				case END_OBJECT:
					cantRegistros++;
					cantTotal++;
					pla.add(convertiraAnexoBean(fields));
					break;
				}
				if (cantRegistros >= 1000) {
					importAnexo750FC.procesarLineasMHParticionado(pla);
					cantRegistros = 0;
					System.out
							.println("Se inserto " + cantTotal + " registros");
					pla.clear();
				}
			}
			importAnexo750FC.procesarLineasMHParticionado(pla);
			System.out.println("Se inserto " + cantTotal + " registros");

			statusMessages
					.add(Severity.INFO,
							"Se realizo la importacion correctamente en las tablas temporales");
			if (!importAnexo750FC.finalizarProceso()) {
				statusMessages
						.add(Severity.ERROR,
								"No se pudo realizar el proceso en las tablas definitivas");
			} else {
				statusMessages.add(Severity.INFO,
						"Se realizo el proceso en las tablas definitivas");
			}
			parser.close();

		} catch (Exception e) {
			statusMessages.add(
					Severity.ERROR,
					"Archivo incorrecto o Error en el proceso "
							+ e.getMessage());
			e.printStackTrace();
		}
	}

	private LineaPlanilla convertiraRemuneracionesBean(
			Map<String, String> fields) {
		LineaPlanilla remu = new LineaPlanilla();
		remu.setPlanillaUniqueId(new Integer(fields.get("planillaUniqueId")));
		remu.setFilaId(new Integer(fields.get("filaId")));
		remu.setCedula(fields.get("cedula"));
		remu.setAnio(new Integer(fields.get("anio")));
		remu.setNivel(new Integer(fields.get("nivel")));
		remu.setEntidad(new Integer(fields.get("entidad")));
		remu.setPrograma(new Integer(fields.get("programa")));
		remu.setMes(new Integer(fields.get("mes")));
		remu.setNombres(fields.get("nombres"));
		remu.setLineaPresupuestaria(fields.get("lineaPresupuestaria"));
		remu.setCodObjeto(new Integer(fields.get("codObjeto")));
		remu.setObjetoGasto(fields.get("objetoGasto"));
		remu.setFuenteFinanc(new Integer(fields.get("fuenteFinanc")));
		remu.setOrgFinanciador(new Integer(fields.get("orgFinanciador")));
		remu.setCategoria(fields.get("categoria"));
		remu.setDescripCategoria(fields.get("descripCategoria"));
		remu.setMontoPresupuestado(new Integer(fields.get("montoPresupuestado")));
		remu.setMontoDevengado(new Integer(fields.get("montoDevengado")));
		remu.setMontoLiquido(new Integer(fields.get("montoLiquido")));
		remu.setFechaActualizacion(new Date(Long.parseLong(fields
				.get("fechaActualizacion"))));
		;
		remu.setTipoPresupuesto(new Integer(fields.get("tipoPresupuesto")));
		return remu;
	}

	private LineaPlanilla convertiraAnexoBean(Map<String, String> fields) {
		LineaPlanilla anexo = new LineaPlanilla();
		if (fields.get("planillaUniqueId") != null) {
			anexo.setPlanillaUniqueId(new Integer(fields
					.get("planillaUniqueId")));
		}
		anexo.setFilaId(new Integer(fields.get("numeroFila")));
		anexo.setCedula(fields.get("cedula"));
		anexo.setAnio(new Integer(fields.get("anio")));
		anexo.setNivel(new Integer(fields.get("nivel")));
		anexo.setEntidad(new Integer(fields.get("entidad")));
		anexo.setPrograma(new Integer(fields.get("programa")));
		anexo.setTipoPresupuesto(new Integer(fields.get("tipoPresupuesto")));
		anexo.setSubprograma(new Integer(fields.get("subprograma")));
		anexo.setProyecto(new Integer(fields.get("proyecto")));
		anexo.setPais(new Integer(fields.get("pais")));
		anexo.setDpto(new Integer(fields.get("dpto")));

		anexo.setLineaPresupuestaria(fields.get("lineaPresupuestaria"));
		anexo.setTablaAgrupadora(new Integer(fields.get("tablaAgrupadora")));

		anexo.setCodObjeto(new Integer(fields.get("objetoGasto")));
		anexo.setVersion(new Integer(fields.get("version")));
		anexo.setFuenteFinanc(new Integer(fields.get("fuenteFinanc")));
		anexo.setOrgFinanciador(new Integer(fields.get("orgFinanciador")));
		anexo.setCodCategoria(fields.get("codCategoria"));
		anexo.setCategoria(fields.get("descripcionCategoria"));
		anexo.setObservaciones(fields.get("observaciones"));

		anexo.setCodigoOperacion(fields.get("codigoOperacion"));
		anexo.setTipoRegistro(fields.get("tipoRegistro"));
		anexo.setMesAplicacion(new Integer(fields.get("mesAplicacion")));
		anexo.setDuracion(new Integer(fields.get("duracion")));
		anexo.setCantidadCargos(new Integer(fields.get("cantidadCargos")));

		anexo.setMontoAnual(new Double(fields.get("montoAnual")));

		anexo.setFechaActualizacion(new Date(Long.parseLong(fields
				.get("fechaActualizacion"))));
		;
		anexo.setFechaIngreso(new Date(Long.parseLong(fields
				.get("fechaIngreso"))));
		;
		return anexo;
	}

	public void traerRegistrosRemuneracionesFROMCVS() {
		try {
			String csvFile = getNombreArchivo();
			ArrayList<LineaPlanilla> pla = new ArrayList<LineaPlanilla>();
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
			try {
				importaMasivaremuNomi680FC = (ImportaMasivaRemuNomi680FC) org.jboss.seam.Component
						.getInstance(ImportaMasivaRemuNomi680FC.class, true);
				importaMasivaremuNomi680FC.borrarTemporal();

				CsvReader remuneraciones = new CsvReader(csvFile);
				remuneraciones.readHeaders();
				int registro = 0;
				int contadorGral = 0;
				while (remuneraciones.readRecord()) {
					LineaPlanilla dto = new LineaPlanilla();
					dto.setFilaId(new Integer(
							removerComillasNumeros(remuneraciones.get("filaid"))));
					dto.setPlanillaUniqueId(new Integer(
							removerComillasNumeros(remuneraciones
									.get("planillaUniqueId"))));
					dto.setAnio(new Integer(
							removerComillasNumeros(remuneraciones.get("anio"))));
					dto.setNivel(new Integer(
							removerComillasNumeros(remuneraciones.get("nivel"))));
					dto.setEntidad(new Integer(
							removerComillasNumeros(remuneraciones
									.get("entidad"))));
					dto.setTipoPresupuesto(new Integer(
							removerComillasNumeros(remuneraciones
									.get("tipoPresupuesto"))));
					dto.setPrograma(new Integer(
							removerComillasNumeros(remuneraciones
									.get("programa"))));
					dto.setMes(new Integer(
							removerComillasNumeros(remuneraciones.get("mes"))));
					dto.setCedula(removerComillas(remuneraciones.get("cedula")));
					dto.setNombres(removerComillas(remuneraciones
							.get("nombres")));
					dto.setLineaPresupuestaria(removerComillas(remuneraciones
							.get("lineaPresupuestaria")));
					dto.setCodObjeto(new Integer(
							removerComillasNumeros(remuneraciones
									.get("codObjeto"))));
					dto.setObjetoGasto(removerComillas(remuneraciones
							.get("objetoGasto")));
					dto.setFuenteFinanc(new Integer(
							removerComillasNumeros((remuneraciones
									.get("fuenteFinanc")))));
					dto.setOrgFinanciador(new Integer(
							removerComillasNumeros(remuneraciones
									.get("orgFinanciador"))));
					dto.setCategoria(removerComillas(remuneraciones
							.get("codCategoria")));
					dto.setDescripCategoria(removerComillas(remuneraciones
							.get("descripCategoria")));
					dto.setMontoPresupuestado(new Integer(
							removerComillasNumeros(remuneraciones
									.get("presupuestado"))));
					dto.setMontoDevengado(new Integer(
							removerComillasNumeros(remuneraciones
									.get("devengado"))));
					dto.setMontoLiquido(new Integer(
							removerComillasNumeros(remuneraciones
									.get("liquido"))));
					dto.setFechaActualizacion(sdf
							.parse(removerComillas(remuneraciones.get("fecha"))));
					registro++;
					contadorGral++;
					pla.add(dto);
					if (registro >= 1000) {

						importaMasivaremuNomi680FC
								.procesarLineasMHTablaParticionado(pla);
						pla.clear();
						registro = 0;
						System.out.println("Se inserto " + contadorGral
								+ " registros");
					}

				}

				importaMasivaremuNomi680FC
						.procesarLineasMHTablaParticionado(pla);
				System.out.println("Se inserto " + contadorGral + " registros");

				statusMessages
						.add(Severity.INFO,
								"Se realizo la importacion correctamente en las tablas temporales");
				if (!importaMasivaremuNomi680FC.finalizarProceso()) {
					statusMessages
							.add(Severity.ERROR,
									"No se pudo realizar el proceso en las tablas definitivas");
				} else {
					statusMessages.add(Severity.INFO,
							"Se realizo el proceso en las tablas definitivas");
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				statusMessages.add(Severity.ERROR,
						"Error en la lectura del archivo");
			} catch (IOException e) {
				e.printStackTrace();
				statusMessages.add(Severity.ERROR,
						"Error en la lectura del archivo");
			} catch (Exception e) {
				e.printStackTrace();
				statusMessages.add(Severity.ERROR,
						"Error en la lectura del archivo");
			} finally {

			}

		} catch (Exception e) {
			statusMessages.add(
					Severity.ERROR,
					"Archivo incorrecto o Error en el proceso "
							+ e.getMessage());
			e.printStackTrace();
		}
	}

	@TransactionTimeout(9999999)
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void traerRegistrosAnexoFROMCVS() {
		try {
			String csvFile = getNombreArchivo();
			ArrayList<LineaPlanilla> pla = new ArrayList<LineaPlanilla>();
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm");
			try {
				importAnexo750FC = (ImportAnexo750FC) org.jboss.seam.Component
						.getInstance(ImportAnexo750FC.class, true);
				importAnexo750FC.borrarTemporal();
				statusMessages.clear();
				CsvReader anexos = new CsvReader(csvFile);
				anexos.setDelimiter(';');
				anexos.readHeaders();
				int registro = 0;
				int contadorGral = 0;
				while (anexos.readRecord()) {
					LineaPlanilla dto = new LineaPlanilla();
					dto.setAnio(new Integer(removerComillasNumeros(anexos
							.get("ANI_ANIOPRE"))));
					dto.setNivel(new Integer(removerComillasNumeros(anexos
							.get("NEN_CODIGO"))));
					dto.setEntidad(new Integer(removerComillasNumeros(anexos
							.get("ENT_CODIGO"))));
					dto.setTipoPresupuesto(new Integer(
							removerComillasNumeros(anexos.get("TIP_CODIGO"))));
					dto.setPrograma(new Integer(removerComillasNumeros(anexos
							.get("PRO_CODIGO"))));
					dto.setSubprograma(new Integer(
							removerComillasNumeros(anexos.get("SUB_CODIGO"))));
					dto.setProyecto(new Integer(removerComillasNumeros(anexos
							.get("PRY_CODIGO"))));
					dto.setFuenteFinanc(new Integer(
							removerComillasNumeros(anexos.get("FUE_CODIGO"))));
					dto.setOrgFinanciador(new Integer(
							removerComillasNumeros(anexos.get("FIN_CODIGO"))));
					dto.setPais(new Integer(removerComillasNumeros(anexos
							.get("PAI_CODIGO"))));
					dto.setDpto(new Integer(removerComillasNumeros(anexos
							.get("DPT_CODIGO"))));

					dto.setCodObjeto(new Integer(removerComillasNumeros(anexos
							.get("OBJ_CODIGO"))));

					dto.setVersion(new Integer(removerComillasNumeros(anexos
							.get("VRS_CODIGO"))));
					dto.setLineaPresupuestaria(removerComillas(anexos
							.get("ANX_LINEA")));

					dto.setCodCategoria(removerComillas(anexos
							.get("CTG_CODIGO")));

					dto.setCategoria(removerComillas(anexos.get("ANX_DESCRIP")));

					dto.setCodigoOperacion(removerComillas(anexos
							.get("MDF_CODIGO")));
					dto.setTipoRegistro(removerComillas(anexos
							.get("ANX_TIPOREG")));
					dto.setJustifica(removerComillas(anexos
							.get("ANX_JUSTIFICA")));
					dto.setMesAplicacion(new Integer(
							removerComillasNumeros(anexos.get("ANX_MESAPLIC"))));

					dto.setDuracion(new Integer(removerComillasNumeros(anexos
							.get("ANX_DURAC"))));

					dto.setMontoAnual(new Double(removerComillasNumeros(anexos
							.get("ANX_VLRAN"))));

					dto.setCantidadCargos(new Integer(
							removerComillasNumeros(anexos.get("ANX_CARGOS"))));

					dto.setTablaAgrupadora(new Integer(
							removerComillasNumeros(anexos.get("CAT_GRUPO"))));

					dto.setFechaIngreso(sdf.parse(removerComillas(anexos
							.get("ANX_FCHING"))));

					dto.setFechaActualizacion(sdf.parse(removerComillas(anexos
							.get("ANX_FCHACT"))));

					registro++;
					contadorGral++;
					pla.add(dto);
					if (registro >= 1000) {

						importAnexo750FC.procesarLineasMHParticionado(pla);
						pla.clear();
						registro = 0;
						System.out.println("Se inserto " + contadorGral
								+ " registros");
					}

				}

				importAnexo750FC.procesarLineasMHParticionado(pla);
				System.out.println("Se inserto " + contadorGral + " registros");
				statusMessages
						.add(Severity.INFO,
								"Se realizo la importacion correctamente en las tablas temporales");
				if (!importAnexo750FC.finalizarProceso()) {
					statusMessages
							.add(Severity.ERROR,
									"No se pudo realizar el proceso en las tablas definitivas");
				} else {
					statusMessages.add(Severity.INFO,
							"Se realizo el proceso en las tablas definitivas");
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				statusMessages.add(Severity.ERROR,
						"Error en la lectura del archivo");
			} catch (IOException e) {
				e.printStackTrace();
				statusMessages.add(Severity.ERROR,
						"Error en la lectura del archivo");
			} catch (Exception e) {
				e.printStackTrace();
				statusMessages.add(Severity.ERROR,
						"Error en la lectura del archivo");
			} finally {

			}
		} catch (Exception e) {
			// statusMessages.add(Severity.ERROR,
			// "Archivo incorrecto o Error en el proceso "+e.getMessage());
			e.printStackTrace();
		}
	}

	public String removerComillas(String dato) {
		String datoFormateado = "";
		if (dato != null) {
			datoFormateado = dato.replaceAll("\"", "");
		}
		return datoFormateado;

	}

	public String removerComillasNumeros(String dato) {
		String datoFormateado = "0";
		if (dato != null) {
			datoFormateado = dato.replaceAll("\"", "");
		}
		try {
			new Integer(datoFormateado);
		} catch (NumberFormatException e) {
			return "0";
		}
		return datoFormateado;

	}

	private List<String> convertirAnexo(LineaPlanilla[] pla) {
		// ANI_ANIOPRE NEN_CODIGO ENT_CODIGO TIP_CODIGO PRO_CODIGO SUB_CODIGO
		// PRY_CODIGO OBJ_CODIGO FUE_CODIGO FIN_CODIGO
		// VRS_CODIGO ANX_LINEA CAT_GRUPO CTG_CODIGO MDF_CODIGO ANX_TIPOREG
		// ANX_MESAPLIC ANX_DURAC ANX_VLRAN ANX_CARGOS
		// ANX_DESCRIP ANX_JUSTIFICA PAI_CODIGO DPT_CODIGO
		String linea = "ANI_ANIOPRE;NEN_CODIGO;ENT_CODIGO;TIP_CODIGO;PRO_CODIGO;SUB_CODIGO;PRY_CODIGO;OBJ_CODIGO;FUE_CODIGO;FIN_CODIGO;VRS_CODIGO;ANX_LINEA;CAT_GRUPO;CTG_CODIGO;MDF_CODIGO;ANX_TIPOREG;ANX_MESAPLIC;ANX_DURAC;ANX_VLRAN;ANX_CARGOS;ANX_DESCRIP;ANX_JUSTIFICA;PAI_CODIGO;DPT_CODIGO;";

		List<String> lineas = new ArrayList<String>();
		lineas.add(linea);
		for (int i = 0; i < pla.length; i++) {
			linea = pla[i].getAnio() + ";";
			linea += pla[i].getNivel() + ";";
			linea += pla[i].getEntidad() + ";";
			linea += pla[i].getTipoPresupuesto() + ";";
			linea += pla[i].getPrograma() + ";";
			linea += pla[i].getSubprograma() + ";";
			linea += pla[i].getProyecto() + ";";
			linea += pla[i].getObjetoGasto() + ";";
			linea += pla[i].getFuenteFinanc() + ";";
			linea += pla[i].getOrgFinanciador() + ";";
			linea += pla[i].getVersion() + ";";
			linea += pla[i].getLineaPresupuestaria() + ";";
			linea += pla[i].getTablaAgrupadora() + ";";
			linea += pla[i].getCodCategoria() + ";";
			linea += pla[i].getCodigoOperacion() + ";";
			linea += pla[i].getTipoRegistro() + ";";
			linea += pla[i].getMesAplicacion() + ";";
			linea += pla[i].getDuracion() + ";";
			linea += pla[i].getMontoAnual() + ";";
			linea += pla[i].getCantidadCargos() + ";";
			linea += pla[i].getDescripcionCategoria() + ";";
			linea += pla[i].getObservaciones() + ";";
			linea += pla[i].getPais() + ";";
			linea += pla[i].getDpto() + ";";
			lineas.add(linea);
		}
		return lineas;
	}

	private List<String> convertirRemuneracion(LineaPlanilla[] pla) {

		String linea = "ano;mes;nivel_entidad;entidad;dependencia;cedula;nombres;apellidos;estado;objeto_gto;fuente_financiamiento;categoria;presupuestado;devengado;movimiento;fecha;cargo;descrip_concepto;linea;descrip_categoria;discapacidad";

		List<String> lineas = new ArrayList<String>();
		lineas.add(linea);
		String nombre = "";
		for (int i = 0; i < pla.length; i++) {
			linea = pla[i].getAnio() + ";";
			linea += pla[i].getMes() + ";";
			linea += pla[i].getNivel() + ";";
			linea += pla[i].getEntidad() + ";";
			linea += "0;";// Dependencia
			linea += pla[i].getCedula() + ";";
			if (pla[i].getNombres() != null) {
				String[] nom = pla[i].getNombres().split(",");
				if (nom.length == 2) {
					linea += nom[1] + ";";
					linea += nom[0] + ";";
				} else {
					linea += "SIN DATO" + ";";
					linea += "SIN DATO" + ";";
				}
			} else {
				linea += "SIN DATO" + ";";
				linea += "SIN DATO" + ";";
			}
			linea += "A;";// ESTADO
			linea += pla[i].getCodObjeto() + ";";
			linea += "0;";// Fuente Finan
			linea += pla[i].getCategoria() + ";";
			linea += pla[i].getMontoDevengado() + ";";
			linea += "0;";// Moviemiento
			linea += formatFecha(pla[i].getFechaActualizacion()) + ";";
			linea += "0;";// Cargo
			linea += "0;";// Concepto
			linea += "0;";// Linea presupuestaria
			linea += pla[i].getDescripCategoria() + ";";
			linea += "0;";// Concepto

			lineas.add(linea);
		}
		return lineas;
	}

	private String formatFecha(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		String fechaString = sdf.format(date);
		return fechaString;
	}

	public static void main(String[] args) {
		ClientServiceMH c = new ClientServiceMH();
		c.traerRegistrosRemuneracionesFROMCVS();
	}
}
