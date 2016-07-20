package py.com.excelsis.sicca.excseleccion.session.form;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import java.util.Vector;

import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;

import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.Excepcion;
import py.com.excelsis.sicca.excseleccion.session.ExcepcionListCustom;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.QueryValue;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("gestExcIngreso591FC")
public class GestExcIngreso591FC {
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(required = false, create = true)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;
	@In(required = false, create = true)
	ExcepcionListCustom excepcionListCustom;
	private Long idConcurso;
	private String tipoConcurso;
	private Long idGrupoPuesto;
	private String idTipoExcepcion;
	private String idEstado;
	private Boolean habLinkEvalDoc;
	private Boolean habLinkPublicar;
	private Boolean habLinkElabResAdj;
	private Boolean habLinkFirmaResAdj;
	private Boolean habLinkElabDecreto;
	private Boolean habLinkFirmaDecreto;
	private Boolean habLinkElabResNomb;
	private Boolean habLinkFirmaResNomb;
	private Boolean habLinkIngreso;
	private Boolean habLinkHistorial = true;
	private List<Excepcion> lExpciones;

	public void init() {
		cargarCabecera();
	}

	private void initHabLinks() {
		habLinkEvalDoc =
			habLinkPublicar =
				habLinkElabResAdj =
					habLinkFirmaResAdj =
						habLinkElabDecreto =
							habLinkFirmaDecreto =
								habLinkElabResNomb = habLinkFirmaResNomb = habLinkIngreso = false;
		habLinkHistorial = true;
	}

	private Boolean inString(String estado, String cadena) {
		String compos[] = cadena.split("#");
		for (String o : compos) {
			if (o.trim().equalsIgnoreCase(cadena.trim())) {
				return true;
			}
		}
		return false;
	}

	public Boolean habLinks(Excepcion ex) {
		String estado = ex.getEstado();
		String tipo = ex.getTipo();
		Boolean respuesta = true;
		initHabLinks();
		if (estado.equalsIgnoreCase("LISTA CORTA")) {
			habLinkEvalDoc = true;
		} else if (estado.equalsIgnoreCase("EVAL. ADJUDICADOS")) {
			habLinkPublicar = true;
		} else if (estado.equalsIgnoreCase("ADJUDICADO")) {
			habLinkElabResAdj = true;
		} else if (estado.equalsIgnoreCase("CON RES. ADJUDICACION")) {
			habLinkFirmaResAdj = true;
		} else if (estado.equalsIgnoreCase("CONTRATADO")
			&& inString("INGRESO POR CONCURSO CPO#INGRESO POR CONCURSO SIMPLIFICADO#INGRESO POR CONCURSO INSTITUCIONAL#INGRESO POR CONCURSO MERITOS", estado)) {
			habLinkIngreso = true;
		} else if (estado.equalsIgnoreCase("PERMANENTE D12")
			&& inString("INGRESO POR CONCURSO CPO#INGRESO POR CONCURSO SIMPLIFICADO#INGRESO POR CONCURSO INSTITUCIONAL", tipo)) {
			habLinkElabResNomb = true;
		} else if (estado.equalsIgnoreCase("CON RES.NOMBRAMIENTO")
			&& inString("INGRESO POR CONCURSO CPO#INGRESO POR CONCURSO SIMPLIFICADO#INGRESO POR CONCURSO INSTITUCIONAL", tipo)) {
			habLinkFirmaResNomb = true;
		} else if (estado.equalsIgnoreCase("PERMANENTE N12")
			&& inString("INGRESO POR CONCURSO CPO#INGRESO POR CONCURSO SIMPLIFICADO#INGRESO POR CONCURSO INSTITUCIONAL", tipo)) {
			habLinkElabDecreto = true;
		} else if (estado.equalsIgnoreCase("CON DECRETO")
			&& inString("INGRESO POR CONCURSO CPO#INGRESO POR CONCURSO SIMPLIFICADO#INGRESO POR CONCURSO INSTITUCIONAL", tipo)) {
			habLinkFirmaDecreto = true;
		} else if (estado.equalsIgnoreCase("FIRMADO DECRETO")
			&& inString("INGRESO POR CONCURSO CPO#INGRESO POR CONCURSO SIMPLIFICADO#INGRESO POR CONCURSO INSTITUCIONAL", tipo)) {
			habLinkIngreso = true;
		} else if (estado.equalsIgnoreCase("FIRMADO NOMBRAMIENTO")
			&& inString("INGRESO POR CONCURSO CPO#INGRESO POR CONCURSO SIMPLIFICADO#INGRESO POR CONCURSO INSTITUCIONAL", tipo)) {
			habLinkIngreso = true;
		} else if (estado.equalsIgnoreCase("INGRESADO")) {
			;// no pasa nada
		}
		return respuesta;

	}

	private void cargarCabecera() {

		nivelEntidadOeeUtil.setIdConfigCab(usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo());
		nivelEntidadOeeUtil.init2();
	}

	public void search() {
		String sql = "select Excepcion from Excepcion Excepcion  ";
		String elWhere = " where Excepcion.activo is true and Excepcion.tipo like 'INGRESO%'";
		Map<String, QueryValue> parametros = new HashMap<String, QueryValue>();
		if (idTipoExcepcion != null) {
			parametros.put("tipo", new QueryValue(idTipoExcepcion));
			elWhere += " and Excepcion.tipo = :tipo ";
		}
		if (idConcurso != null) {
			parametros.put("idConcurso", new QueryValue(idConcurso));
			elWhere += " and Excepcion.concurso.idConcurso = :idConcurso ";
		}
		if (idGrupoPuesto != null) {
			parametros.put("idGrupoPuesto", new QueryValue(idGrupoPuesto));
			elWhere += " and Excepcion.concursoPuestoAgr.idConcursoPuestoAgr = :idGrupoPuesto ";
		}
		if (idEstado != null) {
			parametros.put("idEstado", new QueryValue(idEstado));
			elWhere += " and Excepcion.estado = :idEstado ";
		}
		sql += elWhere;
		lExpciones = excepcionListCustom.listaResultados(sql, parametros);
	}

	private List<ConcursoPuestoAgr> traerGrupoPuesto() {
		Query q =
			em.createQuery("select Excepcion.concursoPuestoAgr from Excepcion Excepcion "
				+ " where Excepcion.activo is true and Excepcion.tipo = :tipoExcepcion "
				+ " and Excepcion.concursoPuestoAgr.concurso.idConcurso = :idConcurso order by Excepcion.concursoPuestoAgr.descripcionGrupo ");
		q.setParameter("idConcurso", idConcurso);
		q.setParameter("tipoExcepcion", idTipoExcepcion);
		return q.getResultList();
	}

	public List<SelectItem> traerGrupoPuestoSI() {
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
		for (ConcursoPuestoAgr o : traerGrupoPuesto())
			si.add(new SelectItem(o.getIdConcursoPuestoAgr(), o.getDescripcionGrupo()));
		return si;
	}

	private List<Excepcion> traerTipoExcepcion() {
		Query q =
			em.createQuery("select Excepcion from Excepcion Excepcion "
				+ " where Excepcion.activo is true "
				+ " and Excepcion.concurso.configuracionUoCab.idConfiguracionUo = :idUo "
				+ " and upper(Excepcion.tipo) like '%INGRESO%' order by Excepcion.tipo ");
		q.setParameter("idUo", usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo());
		// Filtrar
		List<Excepcion> lista = q.getResultList();
		lista = delRepetidos(lista, "TIPO");
		return lista;
	}

	public String urlIngreso(Excepcion excepcion) {
		if (excepcion != null) {
			if (excepcion.getTipo().equalsIgnoreCase("INGRESO POR CONCURSO CPO") || excepcion.getTipo().equalsIgnoreCase("INGRESO POR CONCURSO")) {
				// 600
				return "/seleccion/IngresoConcursoExcepcion/gestionarConcursoExcepcion600.xhtml";
			} else if (excepcion.getTipo().equalsIgnoreCase("SIMPLIFICADO")
				|| excepcion.getTipo().equalsIgnoreCase("INGRESO POR CONCURSO MERITOS")) {
				// 601
				return "/seleccion/gestConcursoInternoExc/gestConcursoInternoExc601.xhtml";
			} else if (excepcion.getTipo().equalsIgnoreCase("INGRESO POR CONCURSO INTERINSTITUCIONAL")) {
				// 602
				return "/seleccion/gestionarConcursoInterno602/gestionarConcursoInterno602.xhtml";
			}
		}
		return "/home.xhtml";

	}

	public List<SelectItem> traerTipoExcepcionSI() {
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
		for (Excepcion o : traerTipoExcepcion())
			si.add(new SelectItem(o.getTipo(), o.getTipo()));
		return si;
	}

	private List<Concurso> traerConcurso() {
		Query q =
			em.createQuery("select Excepcion.concurso from Excepcion Excepcion "
				+ " where Excepcion.activo is true "
				+ " and Excepcion.tipo = :tipoExcepcion "
				+ " and Excepcion.concurso.configuracionUoCab.idConfiguracionUo = :idConfiguracionUo "
				+ " order by Excepcion.concurso.nombre ");
		q.setParameter("idConfiguracionUo", usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo());
		q.setParameter("tipoExcepcion", idTipoExcepcion);
		return q.getResultList();
	}

	public List<SelectItem> traerConcursoSI() {
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
		for (Concurso o : traerConcurso())
			si.add(new SelectItem(o.getIdConcurso(), o.getNombre()));
		return si;
	}

	private List<Excepcion> delRepetidos(List<Excepcion> lista, String campo) {

		Iterator<Excepcion> iter = lista.iterator();
		java.util.HashSet<String> lUnicos = new java.util.HashSet<String>();
		while (iter.hasNext()) {
			Excepcion ex = iter.next();
			if (campo.equalsIgnoreCase("ESTADO")) {
				if (lUnicos.contains(ex.getEstado())) {
					iter.remove();
				} else {
					lUnicos.add(ex.getEstado());
				}
			} else if (campo.equalsIgnoreCase("TIPO")) {
				if (lUnicos.contains(ex.getTipo())) {
					iter.remove();
				} else {
					lUnicos.add(ex.getTipo());
				}
			}
		}
		return lista;
	}

	private List<Excepcion> traerEstado() {
		Query q =
			em.createQuery("select Excepcion from Excepcion Excepcion "
				+ " where Excepcion.activo is true "
				+ " and Excepcion.concurso.configuracionUoCab.idConfiguracionUo = :idUo "
				+ " and upper(Excepcion.tipo) like '%INGRESO%'  "
				+ " and Excepcion.concursoPuestoAgr.idConcursoPuestoAgr = :idGrupo order by Excepcion.estado");
		q.setParameter("idUo", usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo());
		q.setParameter("idGrupo", idGrupoPuesto);
		// Filtrar
		List<Excepcion> lista = q.getResultList();
		lista = delRepetidos(lista, "ESTADO");
		return lista;
	}

	public List<SelectItem> traerEstadoSI() {
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
		for (Excepcion o : traerEstado())
			si.add(new SelectItem(o.getEstado(), o.getEstado()));
		return si;
	}

	public void actualizarConcurso() {
		if (idConcurso != null) {
			Concurso concurso = em.find(Concurso.class, idConcurso);
			tipoConcurso = concurso.getDatosEspecificosTipoConc().getDescripcion();
			traerGrupoPuestoSI();
		}
	}

	public void actualizarGrupo() {
		if (idConcurso != null) {
			Concurso concurso = em.find(Concurso.class, idConcurso);
			tipoConcurso = concurso.getDatosEspecificosTipoConc().getDescripcion();
			traerGrupoPuestoSI();
		}
	}

	public void actualizarEstado() {
		if (idConcurso != null) {
			Concurso concurso = em.find(Concurso.class, idConcurso);
			tipoConcurso = concurso.getDatosEspecificosTipoConc().getDescripcion();
			traerGrupoPuestoSI();
		}
	}

	public Long getIdConcurso() {
		return idConcurso;
	}

	public void setIdConcurso(Long idConcurso) {
		this.idConcurso = idConcurso;
	}

	public Long getIdGrupoPuesto() {
		return idGrupoPuesto;
	}

	public void setIdGrupoPuesto(Long idGrupoPuesto) {
		this.idGrupoPuesto = idGrupoPuesto;
	}

	public String getIdTipoExcepcion() {
		return idTipoExcepcion;
	}

	public void setIdTipoExcepcion(String idTipoExcepcion) {
		this.idTipoExcepcion = idTipoExcepcion;
	}

	public String getIdEstado() {
		return idEstado;
	}

	public void setIdEstado(String idEstado) {
		this.idEstado = idEstado;
	}

	public Boolean getHabLinkEvalDoc() {
		return habLinkEvalDoc;
	}

	public void setHabLinkEvalDoc(Boolean habLinkEvalDoc) {
		this.habLinkEvalDoc = habLinkEvalDoc;
	}

	public Boolean getHabLinkPublicar() {
		return habLinkPublicar;
	}

	public void setHabLinkPublicar(Boolean habLinkPublicar) {
		this.habLinkPublicar = habLinkPublicar;
	}

	public Boolean getHabLinkElabResAdj() {
		return habLinkElabResAdj;
	}

	public void setHabLinkElabResAdj(Boolean habLinkElabResAdj) {
		this.habLinkElabResAdj = habLinkElabResAdj;
	}

	public Boolean getHabLinkFirmaResAdj() {
		return habLinkFirmaResAdj;
	}

	public void setHabLinkFirmaResAdj(Boolean habLinkFirmaResAdj) {
		this.habLinkFirmaResAdj = habLinkFirmaResAdj;
	}

	public Boolean getHabLinkElabDecreto() {
		return habLinkElabDecreto;
	}

	public void setHabLinkElabDecreto(Boolean habLinkElabDecreto) {
		this.habLinkElabDecreto = habLinkElabDecreto;
	}

	public Boolean getHabLinkFirmaDecreto() {
		return habLinkFirmaDecreto;
	}

	public void setHabLinkFirmaDecreto(Boolean habLinkFirmaDecreto) {
		this.habLinkFirmaDecreto = habLinkFirmaDecreto;
	}

	public Boolean getHabLinkElabResNomb() {
		return habLinkElabResNomb;
	}

	public void setHabLinkElabResNomb(Boolean habLinkElabResNomb) {
		this.habLinkElabResNomb = habLinkElabResNomb;
	}

	public Boolean getHabLinkFirmaResNomb() {
		return habLinkFirmaResNomb;
	}

	public void setHabLinkFirmaResNomb(Boolean habLinkFirmaResNomb) {
		this.habLinkFirmaResNomb = habLinkFirmaResNomb;
	}

	public Boolean getHabLinkIngreso() {
		return habLinkIngreso;
	}

	public void setHabLinkIngreso(Boolean habLinkIngreso) {
		this.habLinkIngreso = habLinkIngreso;
	}

	public Boolean getHabLinkHistorial() {
		return habLinkHistorial;
	}

	public void setHabLinkHistorial(Boolean habLinkHistorial) {
		this.habLinkHistorial = habLinkHistorial;
	}

	public List<Excepcion> getlExpciones() {
		return lExpciones;
	}

	public void setlExpciones(List<Excepcion> lExpciones) {
		this.lExpciones = lExpciones;
	}

}
