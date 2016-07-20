package py.com.excelsis.sicca.util;

import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.StatusMessages;

import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.SinAnx;
import py.com.excelsis.sicca.seguridad.entity.Usuario;

@Scope(ScopeType.CONVERSATION)
@Name("sinarhUtiles")
public class SinarhUtiles {

	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;

	private ConfiguracionUoCab oee = new ConfiguracionUoCab();

	public void init() {
		
	}

	public List<SinAnx> obtenerListaSinAnx(Integer anho, Integer vrsCodigo,
			Integer objCodigo, String cgtCodigo, String anxDesc) {
		Long id = usuarioLogueado
		.getConfiguracionUoCab().getIdConfiguracionUo();
		oee = em.find(ConfiguracionUoCab.class, id);
		String[] sinarh = oee.getCodigoSinarh().split("\\/");
		Integer anhoRecibido;
		if (anho == null)
			anhoRecibido = buscarMaximoAnho();
		else
			anhoRecibido = anho;

		String cadena = "select * from sinarh.sin_anx anx "
				+ "where (anx.nen_codigo||'.'||anx.ent_codigo||'.'||anx.tip_codigo||'.'||anx.pro_codigo) in ('";

		for (int i = 0; i < sinarh.length; i++) {
			cadena += sinarh[i];
			if (i < sinarh.length - 1)
				cadena += "', '";
			else
				cadena += "')";
		}
		cadena += " and anx.ani_aniopre = " + anhoRecibido;
		if (vrsCodigo != null)
			cadena += " and anx.vrs_codigo = " + vrsCodigo;
		if (objCodigo != null)
			cadena += " and anx.obj_codigo = " + objCodigo;
		if (cgtCodigo != null && !cgtCodigo.trim().isEmpty())
			cadena += " and anx.ctg_codigo = '" + cgtCodigo + "'";
		if (anxDesc != null && !anxDesc.trim().isEmpty())
			cadena += " and anx.anx_descrip = '" + anxDesc + "'";
		return em.createNativeQuery(cadena, SinAnx.class).getResultList();

	}
	public List<SinAnx> obtenerListaSinAnx(Integer anho, Integer vrsCodigo,
			Integer objCodigo, String cgtCodigo, String anxDesc,String codigoSinarh) {
	
		String[] sinarh = codigoSinarh.split("\\/");
		Integer anhoRecibido;
		if (anho == null)
			anhoRecibido = buscarMaximoAnho();
		else
			anhoRecibido = anho;

		String cadena = "select * from sinarh.sin_anx anx "
				+ "where (anx.nen_codigo||'.'||anx.ent_codigo||'.'||anx.tip_codigo||'.'||anx.pro_codigo) in ('";

		for (int i = 0; i < sinarh.length; i++) {
			cadena += sinarh[i];
			if (i < sinarh.length - 1)
				cadena += "', '";
			else
				cadena += "')";
		}
		cadena += " and anx.ani_aniopre = " + anhoRecibido;
		if (vrsCodigo != null)
			cadena += " and anx.vrs_codigo = " + vrsCodigo;
		if (objCodigo != null)
			cadena += " and anx.obj_codigo = " + objCodigo;
		if (cgtCodigo != null && !cgtCodigo.trim().isEmpty())
			cadena += " and anx.ctg_codigo = '" + cgtCodigo + "'";
		if (anxDesc != null && !anxDesc.trim().isEmpty())
			cadena += " and anx.anx_descrip = '" + anxDesc + "'";
		return em.createNativeQuery(cadena, SinAnx.class).getResultList();

	}
	public List<SinAnx> buscarListaSinAnx(Integer anho, Integer vrsCodigo,
			Integer objCodigo, String cgtCodigo, String anxDesc) {
		Long id = usuarioLogueado
		.getConfiguracionUoCab().getIdConfiguracionUo();
		oee = em.find(ConfiguracionUoCab.class, id);
		//ZD 17/06/16 VALIDAR QUE TENGA ASIGNADO CODIGO SINARH
		if(oee.getCodigoSinarh() == null || oee.getCodigoSinarh().equals(""))
			return null;
		String[] sinarh = oee.getCodigoSinarh().split("\\/");
		Integer anhoRecibido;
		if (anho == null)
			anhoRecibido = buscarMaximoAnho();
		else
			anhoRecibido = anho;

		String cadena = "select * from sinarh.sin_anx anx "
				+ "where (anx.nen_codigo||'.'||anx.ent_codigo||'.'||anx.tip_codigo||'.'||anx.pro_codigo) in ('";

		for (int i = 0; i < sinarh.length; i++) {
			cadena += sinarh[i];
			if (i < sinarh.length - 1)
				cadena += "', '";
			else
				cadena += "')";
		}
		cadena += " and anx.ani_aniopre = " + anhoRecibido;
		if (vrsCodigo != null)
			cadena += " and anx.vrs_codigo = " + vrsCodigo;
		if (objCodigo != null)
			cadena += " and anx.obj_codigo = " + objCodigo;
		if (cgtCodigo != null && !cgtCodigo.trim().isEmpty())
			cadena += " and anx.ctg_codigo ilike '%" + cgtCodigo + "%'";
		if (anxDesc != null && !anxDesc.trim().isEmpty())
			cadena += " and anx.anx_descrip ilike translate ('%"+anxDesc+"%', 'áéíóúÁÉÍÓÚäëïöüÄËÏÖÜñ', 'aeiouAEIOUaeiouAEIOUÑ') ";
	
		return em.createNativeQuery(cadena, SinAnx.class).getResultList();

	}

	private Integer buscarMaximoAnho() {
		String sql = "select max(anx.ani_aniopre) from sinarh.sin_anx anx";
		Object config = em.createNativeQuery(sql).getSingleResult();
		return new Integer(config.toString());
	}

	public ConfiguracionUoCab getOee() {
		return oee;
	}

	public void setOee(ConfiguracionUoCab oee) {
		this.oee = oee;
	}

}
