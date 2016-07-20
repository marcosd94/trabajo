package py.com.excelsis.sicca.session;

import py.com.excelsis.sicca.entity.Ciudad;
import py.com.excelsis.sicca.entity.Departamento;
import py.com.excelsis.sicca.entity.EmprTercerizada;
import py.com.excelsis.sicca.entity.Pais;
import py.com.excelsis.sicca.util.SICCASessionParameters;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.framework.EntityQuery;

import enums.Estado;

import java.util.Arrays;


@Scope(ScopeType.CONVERSATION)
@Name("emprTercerizadaList")
public class EmprTercerizadaList extends EntityQuery<EmprTercerizada> {

	private static final String EJBQL = "select emprTercerizada from EmprTercerizada emprTercerizada";

	private static final String[] RESTRICTIONS = {
		"lower(emprTercerizada.nombre) like lower(concat('%',concat(#{emprTercerizadaList.emprTercerizada.nombre},'%')))",
		"lower(emprTercerizada.ruc) like lower(concat(#{emprTercerizadaList.emprTercerizada.ruc},'%'))",
		"emprTercerizada.ciudad.departamento.pais.idPais = #{emprTercerizadaList.pais.idPais}",
		"emprTercerizada.ciudad.departamento.idDepartamento = #{emprTercerizadaList.departamento.idDepartamento}",
		"emprTercerizada.ciudad.idCiudad = #{emprTercerizadaList.ciudad.idCiudad}", 
		"emprTercerizada.activo = #{emprTercerizadaList.emprTercerizada.activo}",
	};

	private EmprTercerizada emprTercerizada = new EmprTercerizada();
	private Pais pais = new Pais();
	private Departamento departamento = new Departamento();
	private Ciudad ciudad = new Ciudad();

	public EmprTercerizadaList() {
		emprTercerizada.setActivo(Estado.ACTIVO.getValor());
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		assignOrder();
	}
	
	private void assignOrder(){
		setOrderColumn("emprTercerizada.nombre");
		setOrderColumn("emprTercerizada.ciudad.departamento.pais.descripcion");
		setOrderColumn("emprTercerizada.ciudad.departamento.descripcion");
		setOrderColumn("emprTercerizada.ciudad.descripcion");
	}

//	GETTERS Y SETTERS
	public EmprTercerizada getEmprTercerizada() {
		return emprTercerizada;
	}
	public Pais getPais() {
		return pais;
	}
	public Departamento getDepartamento() {
		return departamento;
	}
	public Ciudad getCiudad() {
		return ciudad;
	}

}
