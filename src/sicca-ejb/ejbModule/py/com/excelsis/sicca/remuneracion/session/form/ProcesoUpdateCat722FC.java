package py.com.excelsis.sicca.remuneracion.session.form;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
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

import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.entity.SinAnx;
import py.com.excelsis.sicca.entity.SinAnxOriginal;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Name("procesoUpdateCat722FC")
@AutoCreate
@Scope(ScopeType.APPLICATION)
public class ProcesoUpdateCat722FC {
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
	@In(create = true)
	ProcesoClienteWSSinarah721FC procesoClienteWSSinarah721FC;

	private Map<String, List<String>> diccCodigos = new HashMap<String, List<String>>();
	private String resultadoFinal = null;

	private void cargarDiccCodigos() {
		Query q =
			em.createQuery("select ConfiguracionUoCab from ConfiguracionUoCab ConfiguracionUoCab "
				+ " where ConfiguracionUoCab.activo is true "
				+ " and ConfiguracionUoCab.codigoSinarh is not null ");
		List<ConfiguracionUoCab> lista = q.getResultList();
		diccCodigos.clear();
		for (ConfiguracionUoCab o : lista) {
			String codSinarh = o.getCodigoSinarh();
			if (codSinarh != null && !codSinarh.trim().isEmpty()) {
				String composCodSinarh[] = codSinarh.split("/");
				String elKey = "";
				for (String p : composCodSinarh) {
					elKey = o.getIdConfiguracionUo().toString();
					if (diccCodigos.get(elKey) == null) {
						diccCodigos.put(elKey, new ArrayList<String>());
					}
					diccCodigos.get(elKey).add(p);
				}
			}
		}
	}

	public SinAnx traerSinAnx() {
		Query q = em.createQuery("select SinAnx from SinAnx SinAnx ");
		q.setMaxResults(1);
		List<SinAnx> lista = q.getResultList();
		if (lista.size() > 0) {
			return lista.get(0);
		}
		return null;
	}

	public String traerFechaSinAnx(SinAnx sinAnx) {
		if (sinAnx != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("dd");
			String hora = sdf.format(sinAnx.getFechaAlta());
			return hora;
		}
		return null;
	}

	private Boolean esAutomatico() {
		Query q =
			em.createQuery("select Referencias from Referencias Referencias "
				+ " where Referencias.dominio = 'REMUNERACION_STOCK' "
				+ " and Referencias.descLarga = 'AUTOMATICO_MANUAL' and Referencias.valorAlf = 'A' ");
		List<Referencias> lista = q.getResultList();
		if (lista.size() > 0) {
			return true;
		}
		return false;
	}

	public Boolean ejecutar() throws ParseException {

		Integer dia = traerCantDias();
		String horaConf = traerHora();
		SinAnx sinAnx = traerSinAnx();
		String diaSinAnx = traerFechaSinAnx(sinAnx);
		SimpleDateFormat sdfHora = new SimpleDateFormat("HH:mm");
		String horaHoy = sdfHora.format(new Date());
		if (diaSinAnx == null) {
			return procesoClienteWSSinarah721FC.decidirEnBaseHora(horaHoy, horaConf);
		} else {
			// Verificar si estamos dento del rango
			Calendar calRemu = Calendar.getInstance();
			calRemu.setTime(sinAnx.getFechaAlta());
			Calendar calHoy = Calendar.getInstance();
			calRemu.add(Calendar.DAY_OF_MONTH, dia);
			SimpleDateFormat sdfFecha = new SimpleDateFormat("dd/MM/yyyy");
			if (sdfFecha.parse(sdfFecha.format(calHoy.getTime())).getTime() >= sdfFecha.parse(sdfFecha.format(calRemu.getTime())).getTime()) {
				// verificar si es la hora marcada
				return procesoClienteWSSinarah721FC.decidirEnBaseHora(horaHoy, horaConf);
			}
		}
		return false;
	}

	public Integer traerCantDias() {
		Query q =
			em.createQuery("select Referencias from Referencias Referencias "
				+ " where Referencias.dominio = 'REMUNERACION_CONF' "
				+ " and Referencias.descLarga = 'CANT_DIAS' ");
		Referencias ref = (Referencias) q.getSingleResult();
		return ref.getValorNum();
	}

	public String traerHora() {
		Query q =
			em.createQuery("select Referencias from Referencias Referencias "
				+ " where Referencias.dominio = 'REMUNERACION_CONF'"
				+ " and Referencias.descLarga = 'HORA_DESDE' ");
		Referencias ref = (Referencias) q.getSingleResult();
		return ref.getValorAlf();
	}

	public String procesoPrincipalManual() {
		try {
			if (esAutomatico()) {
				statusMessages.add(Severity.ERROR, "El proceso es automático. No puede ser ejecutado de manera manual.");
			}
			core();
			if (resultadoFinal == null || resultadoFinal.equalsIgnoreCase("FAIL")) {
				statusMessages.clear();
				statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("GENERICO_NO_MSG"));
				return "FAIL";
			} else {
				statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
				return "OK";
			}

		} catch (Exception e1) {
			e1.printStackTrace();
			return "FAIL";
		}
	}

	private void core() {
		cargarDiccCodigos();
		List<SinAnxOriginal> lSinAnxOriginal = null;
		resultadoFinal = null;
		try {
			for (String o : diccCodigos.keySet()) {
				for (String p : diccCodigos.get(o)) {
					String compos[] = p.split("\\.");
					lSinAnxOriginal =
						cargarSinAnxOriginal(Integer.parseInt(compos[0]), Integer.parseInt(compos[1]), Integer.parseInt(compos[2]), Integer.parseInt(compos[3]));
					for (SinAnxOriginal m : lSinAnxOriginal) {
						SinAnx sinAnx =
							cargarSinAnx(m.getNenCodigo(), m.getEntCodigo(), m.getTipCodigo(), m.getProCodigo(), m.getObjCodigo(), m.getCtgCodigo());
						if (sinAnx != null) {
							Integer cantAsignada =
								sinAnx.getAnxCargos().intValue()
									- sinAnx.getCantDisponible().intValue();
							Integer nuevaCantDisponible =
								m.getAnxCargos().intValue() - cantAsignada.intValue();
							sinAnx.setCantDisponible(nuevaCantDisponible);
							sinAnx.setAniAniopre(m.getAniAniopre());
							sinAnx.setAnxCargos(m.getAnxCargos());
							em.merge(sinAnx);
						} else {
							sinAnx = new SinAnx();
							sinAnx.setAniAniopre(m.getAniAniopre());
							sinAnx.setNenCodigo(m.getNenCodigo());
							sinAnx.setEntCodigo(m.getEntCodigo());
							sinAnx.setTipCodigo(m.getTipCodigo());
							sinAnx.setProCodigo(m.getProCodigo());
							sinAnx.setObjCodigo(m.getObjCodigo());
							sinAnx.setCtgCodigo(m.getCtgCodigo());
							sinAnx.setAnxDescrip(traerDescripcion(m.getNenCodigo(), m.getEntCodigo(), m.getTipCodigo(), m.getProCodigo(), m.getCtgCodigo()));
							sinAnx.setVrsCodigo(50);
							sinAnx.setAnxCargos(m.getAnxCargos());
							sinAnx.setCantDisponible(m.getAnxCargos());
							sinAnx.setFechaAlta(new Date());
							sinAnx.setCatGrupo(m.getCatGrupo());
							em.persist(sinAnx);
						}
					}
				}
			}

			resultadoFinal = "OK";
			em.flush();
		} catch (Exception e) {
			e.printStackTrace();
			resultadoFinal = "FAIL";
		}
	}

	@Transactional
	@Asynchronous
	public QuartzTriggerHandle procesoPrincipal(@Expiration Date when, @IntervalCron String interval) {
		
		if (!esAutomatico()) {
			return null;
		}
		try {
			if (!ejecutar()) {
				return null;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		core();
		return null;
	}

	private String traerDescripcion(Integer codNivel, Integer codEntidad, Integer tipoCod, Integer proCodigo, String ctgCodigo) {
		String codSinarh = codNivel + "." + codEntidad + "." + tipoCod + "." + proCodigo;
		StringBuffer var1 = new StringBuffer();
		var1.append("SELECT anx_descrip FROM sinarh.sin_anx_original WHERE nen_codigo  ");
		var1.append("|| '.' || ent_codigo || '.' || tip_codigo || '.' || pro_codigo = (  ");
		var1.append(":codSinarh ) AND ctg_codigo = :ctgCodigo AND ani_aniopre = (SELECT Max ");
		var1.append("(ani_aniopre) FROM sinarh.sin_anx_original) ");
		Query q = em.createNativeQuery(var1.toString());
		q.setParameter("ctgCodigo", ctgCodigo);
		q.setParameter("codSinarh", codSinarh);
		List<String> lista = q.getResultList();
		if (lista.size() > 0) {
			String respuesta = lista.get(0);

			return respuesta;
		}
		return null;
	}

	private List<SinAnxOriginal> cargarSinAnxOriginal(Integer codNivel, Integer codEntidad, Integer tipoCod, Integer proCodigo) {
		String codSinarh = codNivel + "." + codEntidad + "." + tipoCod + "." + proCodigo;

		StringBuffer sql = new StringBuffer();
		sql.append("SELECT ani_aniopre, nen_codigo, ent_codigo, tip_codigo, pro_codigo,  ");
		sql.append("obj_codigo, ctg_codigo, Sum(anx_cargos) anx_cargo_sinAnxOriginal, cat_grupo FROM sinarh.sin_anx_original ");
		sql.append(" WHERE nen_codigo || '.' || ent_codigo || '.' || tip_codigo || '.' ||  ");
		sql.append("pro_codigo = ( :codSinarh ) AND ani_aniopre = (SELECT Max(ani_aniopre)  ");
		sql.append("FROM sinarh.sin_anx_original) GROUP BY  ani_aniopre, nen_codigo, ent_codigo,  ");
		sql.append("tip_codigo, pro_codigo, obj_codigo, ctg_codigo, cat_grupo  ORDER BY ani_aniopre,  ");
		sql.append("nen_codigo, ent_codigo, tip_codigo, pro_codigo, obj_codigo, ctg_codigo  ");
		Query q = em.createNativeQuery(sql.toString());
		q.setParameter("codSinarh", codSinarh);
		List<Object[]> lista = q.getResultList();
		List<SinAnxOriginal> lRespuesta = new ArrayList<SinAnxOriginal>();
		int index = 0;
		for (Object[] o : lista) {
			lRespuesta.add(new SinAnxOriginal());
			index = lRespuesta.size() - 1;
			lRespuesta.get(index).setAniAniopre(((Integer) o[0]).intValue());
			lRespuesta.get(index).setNenCodigo(((Integer) o[1]).intValue());
			lRespuesta.get(index).setEntCodigo(((Integer) o[2]).intValue());
			lRespuesta.get(index).setTipCodigo(((Integer) o[3]).intValue());
			lRespuesta.get(index).setProCodigo(((Integer) o[4]).intValue());
			lRespuesta.get(index).setObjCodigo(((Integer) o[5]).intValue());
			lRespuesta.get(index).setCtgCodigo(((String) o[6]));
			lRespuesta.get(index).setAnxCargos(((java.math.BigInteger) o[7]).intValue());
			lRespuesta.get(index).setCatGrupo(((Integer) o[8]));
		}
		return lRespuesta;
	}

	private SinAnx cargarSinAnx(Integer codNivel, Integer codEntidad, Integer tipoCod, Integer proCodigo, Integer objCodigo, String ctgCodigo) {
		Query q =
			em.createQuery("select SinAnx from SinAnx SinAnx "
				+ " where SinAnx.nenCodigo =:codNivel " + " and SinAnx.entCodigo = :codEntidad "
				+ " and SinAnx.tipCodigo = :tipoCod " + " and SinAnx.proCodigo = :proCod "
				+ " and SinAnx.objCodigo = :objCodigo " + " and SinAnx.ctgCodigo = :ctgCodigo");
		q.setParameter("codNivel", codNivel);
		q.setParameter("codEntidad", codEntidad);
		q.setParameter("tipoCod", tipoCod);
		q.setParameter("proCod", proCodigo);
		q.setParameter("objCodigo", objCodigo);
		q.setParameter("ctgCodigo", ctgCodigo);

		List<SinAnx> lista = q.getResultList();
		if (lista.size() > 0) {
			return lista.get(0);
		}
		return null;
	}

	public String getResultadoFinal() {
		return resultadoFinal;
	}

	public void setResultadoFinal(String resultadoFinal) {
		this.resultadoFinal = resultadoFinal;
	}

}
