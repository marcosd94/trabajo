package py.com.excelsis.sicca.remuneracion.session.form;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import model.LineaPlanilla;

import org.apache.commons.io.FileUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.richfaces.model.UploadItem;

import py.com.excelsis.sicca.entity.EmpleadoPuesto;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.entity.Remuneraciones;
import py.com.excelsis.sicca.entity.SinAnxOriginal;
import py.com.excelsis.sicca.entity.SinAnxOriginalTmp;
import py.com.excelsis.sicca.seguridad.entity.Rol;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.ConfiguracionUoCabList;
import py.com.excelsis.sicca.session.EmpleadoPuestoList;
import py.com.excelsis.sicca.session.EntidadList;
import py.com.excelsis.sicca.session.SinEntidadList;
import py.com.excelsis.sicca.session.SinNivelEntidadList;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.PersonaDTO;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.JasperReportUtils;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.Utiles;

@Scope(ScopeType.CONVERSATION)
@Name("importAnexo750FC")
public class ImportAnexo750FC {
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

	public void init() {

	}

	private Boolean precondInsert() {

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
		String cabeceraMatriz = "ANI_ANIOPRE;NEN_CODIGO;ENT_CODIGO;TIP_CODIGO;PRO_CODIGO;SUB_CODIGO;PRY_CODIGO;OBJ_CODIGO;FUE_CODIGO;FIN_CODIGO;VRS_CODIGO;ANX_LINEA;CAT_GRUPO;CTG_CODIGO;MDF_CODIGO;ANX_TIPOREG;ANX_MESAPLIC;ANX_DURAC;ANX_VLRAN;ANX_CARGOS;ANX_DESCRIP;ANX_JUSTIFICA;PAI_CODIGO;DPT_CODIGO;";
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
										"Error al definir las columnas. Verifique el archivo.");
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

	private DTO750 precondCompos(String linea) {
		DTO750 dto = DTO750.descomponerLinea(linea);
		if (dto != null)
			return dto;
		return null;
	}

	private List<Referencias> traerDestinatatioriosMail() {
		Query q = em
				.createQuery("select Referencias from Referencias Referencias "
						+ " where Referencias.dominio = 'EMAIL_COPIA_SFP' "
						+ " AND Referencias.descAbrev = 'ANEXO_SINARH' "
						+ "AND Referencias.activo is true  ");
		List<Referencias> lista = q.getResultList();
		return lista;
	}

	public void enviarMail(Integer anioPre) {
		if (seleccionUtilFormController == null)
			seleccionUtilFormController = (SeleccionUtilFormController) org.jboss.seam.Component
					.getInstance(SeleccionUtilFormController.class, true);
		List<Referencias> lDestinatarios = traerDestinatatioriosMail();
		for (Referencias o : lDestinatarios) {
			VelocityEngine ve = new VelocityEngine();
			java.util.Properties p = new java.util.Properties();
			p.setProperty("resource.loader", "class");
			p.setProperty("class.resource.loader.class",
					"org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
			ve.init(p);
			VelocityContext context = new VelocityContext();
			context.put("ANI_ANIOPRE", anioPre);

			Template t = ve.getTemplate("/templates/email754.vm");
			StringWriter writer = new StringWriter();
			t.merge(context, writer);
			writer.toString();
			seleccionUtilFormController
					.enviarMails(
							o.getDescLarga(),
							writer.toString(),
							" Portal Paraguay Concursa – SICCA: Importación Anexo SINARH ",
							null);
		}
	}

	public Boolean procesarLineas(List<String> lLineasArch, Boolean enviarMail) {
		DTO750 dto = new DTO750();
		String o = null;
		if (!validarCabecera())
			return false;
		Integer anioReferencia = null;
		if (lLineasArch.size() > 0) {
			o = lLineasArch.get(1);
			dto = precondCompos(o);
			anioReferencia = dto.getAniAniopre();
		}
		for (int i = 1; i < lLineasArch.size(); i++) {
			o = lLineasArch.get(i);
			dto = precondCompos(o);
			if (dto != null) {
				try {
					if (anioReferencia.intValue() != dto.getAniAniopre()
							.intValue()) {
						statusMessages
								.add(Severity.ERROR,
										"Error al levantar el anexo de diferentes años. Verifique el archivo");
						return false;
					}
				} catch (Exception e) {
					e.printStackTrace();
					statusMessages.add(Severity.ERROR,
							"No se pudo realizar la operación");
					return false;
				}
			}
		}
		try {
			paso1(anioReferencia);
			paso2(lLineasArch);
			enviarMail(anioReferencia);
			em.flush();
			statusMessages.clear();

			return true;
		} catch (Exception e) {
			statusMessages.add(Severity.ERROR,
					"No se pudo realizar la operación");
			e.printStackTrace();
			return false;
		}
	}

	public Boolean procesarLineasMH(List<String> lLineasArch) {

		try {

			paso2(lLineasArch);
			em.flush();
			statusMessages.clear();
			statusMessages.add(Severity.INFO,
					"Se realizo la operación satisfactoriamente");
			return true;
		} catch (Exception e) {
			statusMessages.add(Severity.ERROR,
					"No se pudo realizar la operación");
			e.printStackTrace();
			return false;
		}
	}

	public void procesarLineasMHParticionado(
			ArrayList<LineaPlanilla> lLineasArch) throws Exception {
		try {
			int i = 0;
			for (LineaPlanilla dto : lLineasArch) {

				SinAnxOriginalTmp sinAnxOriginal = new SinAnxOriginalTmp();
				sinAnxOriginal.setAniAniopre(dto.getAnio());
				sinAnxOriginal.setAnxCargos(dto.getCantidadCargos());
				sinAnxOriginal.setAnxDescrip(dto.getCategoria());
				sinAnxOriginal.setAnxDurac(dto.getDuracion());
				sinAnxOriginal.setAnxJustifica(dto.getJustifica());
				sinAnxOriginal.setAnxLinea(dto.getLineaPresupuestaria());
				sinAnxOriginal.setAnxMesaplic(dto.getMesAplicacion());
				sinAnxOriginal.setAnxTiporeg(dto.getTipoRegistro());
				sinAnxOriginal.setAnxVlran(dto.getMontoAnual().longValue());
				sinAnxOriginal.setCatGrupo(dto.getTablaAgrupadora());
				sinAnxOriginal.setCtgCodigo(dto.getCodCategoria());
				sinAnxOriginal.setDptCodigo(dto.getDpto());
				sinAnxOriginal.setEntCodigo(dto.getEntidad());
				sinAnxOriginal.setFinCodigo(dto.getOrgFinanciador());
				sinAnxOriginal.setFueCodigo(dto.getFuenteFinanc());
				sinAnxOriginal.setMdfCodigo(dto.getCodigoOperacion());
				sinAnxOriginal.setNenCodigo(dto.getNivel());
				sinAnxOriginal.setObjCodigo(dto.getCodObjeto());
				sinAnxOriginal.setPaiCodigo(dto.getPais());
				sinAnxOriginal.setProCodigo(dto.getPrograma());
				sinAnxOriginal.setPryCodigo(dto.getProyecto());
				sinAnxOriginal.setSubCodigo(dto.getSubprograma());
				sinAnxOriginal.setTipCodigo(dto.getTipoPresupuesto());
				sinAnxOriginal.setVrsCodigo(dto.getVersion());
				sinAnxOriginal.setAnxFching(dto.getFechaIngreso());
				sinAnxOriginal.setAnxFchact(dto.getFechaActualizacion());
				if (dto.getFilaId() != null)
					sinAnxOriginal.setFilaId(new Long(dto.getFilaId()));
				if (dto.getPlanillaUniqueId() != null)

					sinAnxOriginal.setPlanillaUniqueId(new Long(dto
							.getPlanillaUniqueId()));

				sinAnxOriginal.setAnxUsring(usuarioLogueado.getCodigoUsuario());
				sinAnxOriginal.setFechaDescarga(new Date());
				em.persist(sinAnxOriginal);
			}
			em.flush();
			em.clear();
			

		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.clear();
			statusMessages.add(Severity.ERROR,
					"No se pudo realizar la operaci—n");
			throw e;
		}
	}

	public void borrarTemporal() {
		Query q = em
				.createNativeQuery("truncate table sinarh.sin_anx_original_tmp");
		q.executeUpdate();
	}
	public boolean finalizarProceso() {
		try {

			Query q = em
					.createNativeQuery("insert into sinarh.sin_anx_original_historico (ani_aniopre, nen_codigo, ent_codigo, tip_codigo, "
							+ " pro_codigo, sub_codigo, pry_codigo, obj_codigo, fue_codigo, fin_codigo,  "
							+ "        vrs_codigo, anx_linea, cat_grupo, ctg_codigo, mdf_codigo, anx_tiporeg,  "
							+ "       anx_mesaplic, anx_durac, anx_vlran, anx_cargos, anx_descrip,  "
							+ "        pai_codigo, dpt_codigo, anx_justifica, anx_fching, anx_usring,  "
							+ "        anx_fchact, anx_usract, cant_disponible, fila_id, planilla_unique_id,fecha_descarga) "
							+ " select ani_aniopre, nen_codigo, ent_codigo, tip_codigo,  "
							+ "        pro_codigo, sub_codigo, pry_codigo, obj_codigo, fue_codigo, fin_codigo,  "
							+ "        vrs_codigo, anx_linea, cat_grupo, ctg_codigo, mdf_codigo, anx_tiporeg,  "
							+ "        anx_mesaplic, anx_durac, anx_vlran, anx_cargos, anx_descrip,  "
							+ "        pai_codigo, dpt_codigo, anx_justifica, anx_fching, anx_usring,  "
							+ "        anx_fchact, anx_usract, cant_disponible, fila_id, planilla_unique_id,fecha_descarga from sinarh.sin_anx_original d "
							+ " where   exists ( select * from sinarh.sin_anx_original_tmp t where d.ani_aniopre = t.ani_aniopre and d.nen_codigo = t.nen_codigo and d.ent_codigo = t.ent_codigo and d.tip_codigo = t.tip_codigo  and d.pro_codigo = t.pro_codigo   and d.sub_codigo = t.sub_codigo and d.pry_codigo = t.pry_codigo  and d.obj_codigo = t.obj_codigo  and d.fue_codigo = t.fue_codigo  and d.fin_codigo = t.fin_codigo  and d.anx_linea = t.anx_linea  and d.ctg_codigo = t.ctg_codigo  ); "
							+

							" delete from sinarh.sin_anx_original d "
							+ " where   exists ( select * from sinarh.sin_anx_original_tmp t where d.ani_aniopre = t.ani_aniopre and d.nen_codigo = t.nen_codigo and d.ent_codigo = t.ent_codigo and d.tip_codigo = t.tip_codigo  and d.pro_codigo = t.pro_codigo   and d.sub_codigo = t.sub_codigo and d.pry_codigo = t.pry_codigo  and d.obj_codigo = t.obj_codigo  and d.fue_codigo = t.fue_codigo  and d.fin_codigo = t.fin_codigo  and d.anx_linea = t.anx_linea  and d.ctg_codigo = t.ctg_codigo ); "
							+

							" insert into sinarh.sin_anx_original (ani_aniopre, nen_codigo, ent_codigo, tip_codigo,  "
							+ "        pro_codigo, sub_codigo, pry_codigo, obj_codigo, fue_codigo, fin_codigo,  "
							+ "        vrs_codigo, anx_linea, cat_grupo, ctg_codigo, mdf_codigo, anx_tiporeg,  "
							+ "        anx_mesaplic, anx_durac, anx_vlran, anx_cargos, anx_descrip,  "
							+ "        pai_codigo, dpt_codigo, anx_justifica, anx_fching, anx_usring,  "
							+ "        anx_fchact, anx_usract, cant_disponible, fila_id, planilla_unique_id,fecha_descarga) "
							+ " select ani_aniopre, nen_codigo, ent_codigo, tip_codigo,  "
							+ "        pro_codigo, sub_codigo, pry_codigo, obj_codigo, fue_codigo, fin_codigo,  "
							+ "        vrs_codigo, anx_linea, cat_grupo, ctg_codigo, mdf_codigo, anx_tiporeg,  "
							+ "        anx_mesaplic, anx_durac, anx_vlran, anx_cargos, anx_descrip,  "
							+ "        pai_codigo, dpt_codigo, anx_justifica, anx_fching, anx_usring,  "
							+ "        anx_fchact, anx_usract, cant_disponible, fila_id, planilla_unique_id,fecha_descarga from sinarh.sin_anx_original_tmp t "
							+ " where not  exists ( select * from sinarh.sin_anx_original d where d.ani_aniopre = t.ani_aniopre and d.nen_codigo = t.nen_codigo and d.ent_codigo = t.ent_codigo and d.tip_codigo = t.tip_codigo  and d.pro_codigo = t.pro_codigo   and d.sub_codigo = t.sub_codigo and d.pry_codigo = t.pry_codigo  and d.obj_codigo = t.obj_codigo  and d.fue_codigo = t.fue_codigo  and d.fin_codigo = t.fin_codigo  and d.anx_linea = t.anx_linea  and d.ctg_codigo = t.ctg_codigo ) ");
			q.executeUpdate();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public void procesarLineasMHParticionado2(
			ArrayList<LineaPlanilla> lLineasArch) throws Exception {
		try {
			int i = 0;
			for (LineaPlanilla dto : lLineasArch) {

				SinAnxOriginal sinAnxOriginal = new SinAnxOriginal();
				sinAnxOriginal.setAniAniopre(dto.getAnio());
				sinAnxOriginal.setAnxCargos(dto.getCantidadCargos());
				sinAnxOriginal.setAnxDescrip(dto.getCategoria());
				sinAnxOriginal.setAnxDurac(dto.getDuracion());
				sinAnxOriginal.setAnxJustifica(dto.getJustifica());
				sinAnxOriginal.setAnxLinea(dto.getLineaPresupuestaria());
				sinAnxOriginal.setAnxMesaplic(dto.getMesAplicacion());
				sinAnxOriginal.setAnxTiporeg(dto.getTipoRegistro());
				sinAnxOriginal.setAnxVlran(dto.getMontoAnual().longValue());
				sinAnxOriginal.setCatGrupo(dto.getTablaAgrupadora());
				sinAnxOriginal.setCtgCodigo(dto.getCodCategoria());
				sinAnxOriginal.setDptCodigo(dto.getDpto());
				sinAnxOriginal.setEntCodigo(dto.getEntidad());
				sinAnxOriginal.setFinCodigo(dto.getOrgFinanciador());
				sinAnxOriginal.setFueCodigo(dto.getFuenteFinanc());
				sinAnxOriginal.setMdfCodigo(dto.getCodigoOperacion());
				sinAnxOriginal.setNenCodigo(dto.getNivel());
				sinAnxOriginal.setObjCodigo(dto.getCodObjeto());
				sinAnxOriginal.setPaiCodigo(dto.getPais());
				sinAnxOriginal.setProCodigo(dto.getPrograma());
				sinAnxOriginal.setPryCodigo(dto.getProyecto());
				sinAnxOriginal.setSubCodigo(dto.getSubprograma());
				sinAnxOriginal.setTipCodigo(dto.getTipoPresupuesto());
				sinAnxOriginal.setVrsCodigo(dto.getVersion());
				sinAnxOriginal.setAnxFching(dto.getFechaIngreso());
				sinAnxOriginal.setAnxFchact(dto.getFechaActualizacion());

				sinAnxOriginal.setAnxUsring(usuarioLogueado.getCodigoUsuario());
				em.persist(sinAnxOriginal);
				em.flush();
			}
			em.flush();
			em.clear();
			statusMessages.clear();
			statusMessages.add(Severity.INFO,
					"Se realizo la operaci—n satisfactoriamente");

		} catch (Exception e) {
			statusMessages.add(Severity.ERROR,
					"No se pudo realizar la operaci—n");
			throw e;
		}
	}

	private void paso1(Integer anio) {
		Query q = em
				.createQuery("select SinAnxOriginal from SinAnxOriginal SinAnxOriginal "
						+ " where SinAnxOriginal.aniAniopre = :aniAniopre");
		q.setParameter("aniAniopre", anio);
		List<SinAnxOriginal> lista = q.getResultList();
		for (SinAnxOriginal o : lista) {
			em.remove(o);
		}
	}

	private void paso2(List<String> lLineasArch)
			throws IllegalArgumentException, SecurityException,
			IllegalAccessException, NoSuchFieldException {
		String o;
		DTO750 dto;
		if (lLineasArch.size() > 0) {
			for (int i = 1; i < lLineasArch.size(); i++) {
				o = lLineasArch.get(i);
				dto = precondCompos(o);
				SinAnxOriginal sinAnxOriginal = new SinAnxOriginal();
				sinAnxOriginal.setAniAniopre(dto.getAniAniopre());
				sinAnxOriginal.setAnxCargos(dto.getAnxCargos());
				sinAnxOriginal.setAnxDescrip(dto.getAnxDescrip());
				sinAnxOriginal.setAnxDurac(dto.getAnxDurac());
				sinAnxOriginal.setAnxJustifica(dto.getAnxJustifica());
				sinAnxOriginal.setAnxLinea(dto.getAnxLinea().toString());
				sinAnxOriginal.setAnxMesaplic(dto.getAnxMesaplic());
				sinAnxOriginal.setAnxTiporeg(dto.getAnxTiporeg());
				sinAnxOriginal.setAnxVlran(dto.getAnxVlran());
				sinAnxOriginal.setCatGrupo(dto.getCatGrupo());
				sinAnxOriginal.setCtgCodigo(dto.getCtgCodigo());
				sinAnxOriginal.setDptCodigo(dto.getDptCodigo());
				sinAnxOriginal.setEntCodigo(dto.getEntCodigo());
				sinAnxOriginal.setFinCodigo(dto.getFinCodigo());
				sinAnxOriginal.setFueCodigo(dto.getFueCodigo());
				sinAnxOriginal.setMdfCodigo(dto.getMdfCodigo());
				sinAnxOriginal.setNenCodigo(dto.getNenCodigo());
				sinAnxOriginal.setObjCodigo(dto.getObjCodigo());
				sinAnxOriginal.setPaiCodigo(dto.getPaiCodigo());
				sinAnxOriginal.setProCodigo(dto.getProCodigo());
				sinAnxOriginal.setPryCodigo(dto.getPryCodigo());
				sinAnxOriginal.setSubCodigo(dto.getSubCodigo());
				sinAnxOriginal.setTipCodigo(dto.getTipCodigo());
				sinAnxOriginal.setVrsCodigo(dto.getVrsCodigo());
				sinAnxOriginal.setAnxFching(new Date());
				sinAnxOriginal.setAnxUsring(usuarioLogueado.getCodigoUsuario());
				em.persist(sinAnxOriginal);
			}
		}
	}

	public void massiveImport() throws IOException, ParseException {
		if (!precondInsert()) {
			return;
		}
		UploadItem fileItem = seleccionUtilFormController.crearUploadItem(
				fName, uFile.length, cType, uFile);
		lLineasArch = FileUtils.readLines(fileItem.getFile());
		if (procesarLineas(lLineasArch, false)) {
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
			limpiar();
		}
	}

	public void limpiar() {
		uFile = null;
		cType = null;
		fName = null;
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

}
