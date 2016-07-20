package py.com.excelsis.sicca.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.StatusMessages;

import py.com.excelsis.sicca.entity.Ciudad;
import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.ConcursoPuestoDet;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.EstadoDet;
import py.com.excelsis.sicca.entity.GrupoConceptoPago;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.entity.PuestoConceptoPago;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.entity.SinAnx;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.ReferenciasUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;

@Scope(ScopeType.CONVERSATION)
@Name("reponerCategoriasController")
public class ReponerCategoriasController implements Serializable {
	@In(required = false)
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(create = true,required = false)
	ReferenciasUtilFormController referenciasUtilFormController;
	@In(create = true, required = false)
	SeleccionUtilFormController seleccionUtilFormController;
	@In(create = true)
	SinarhUtiles sinarhUtiles;

	private Concurso concurso;
	private ConfiguracionUoCab oee;
	private PlantaCargoDet plantaCargoDet;
	private Integer anho;
	private String codSinarh;
	private Integer estadoGrupoDesierto;
	private Integer estadoGrupoCancelado;

	private List<PuestoConceptoPago> listaConceptoPago = new ArrayList<PuestoConceptoPago>();
	private List<ConcursoPuestoDet> listaDePuestos = new ArrayList<ConcursoPuestoDet>();
	private List<ConcursoPuestoDet> listaDePuestosDesierto = new ArrayList<ConcursoPuestoDet>();

	public void init() {

	}

	/*************************************************************
	 * CASO 1 - 4
	 *************************************************************/
	/**
	 * Caso1
	 * 
	 * @param det
	 * @param variable1
	 * @param variable2
	 */
	public void reponerCategorias(ConcursoPuestoDet det, String variable1,
			String variable2) {
		try {
			if (variable1.equalsIgnoreCase("PUESTO")
					&& variable2.equalsIgnoreCase("CONCURSO")) {
				concurso = new Concurso();
				concurso = em.find(Concurso.class, det.getConcurso()
						.getIdConcurso());
				plantaCargoDet = em.find(PlantaCargoDet.class, det
						.getPlantaCargoDet().getIdPlantaCargoDet());
				oee = new ConfiguracionUoCab();
				oee = em.find(ConfiguracionUoCab.class, concurso
						.getConfiguracionUoCab().getIdConfiguracionUo());
				codSinarh = oee.getCodigoSinarh();
				anho = det.getConcurso().getFechaCreacion().getYear() + 1900;
				if (!concurso.getAdReferendum())
					esNoAdReferendum();
				else
					buscarPuestoConceptoPago();
				eliminarPuestoConceptoPago();
				em.flush();
			}
			if ((variable1.equalsIgnoreCase("PUESTO") && variable2
					.equalsIgnoreCase("DESIERTO"))
					|| (variable1.equalsIgnoreCase("PUESTO") && variable2
							.equalsIgnoreCase("DISMINUIDO"))) {

				concurso = new Concurso();
				concurso = em.find(Concurso.class, det.getConcurso()
						.getIdConcurso());
				plantaCargoDet = em.find(PlantaCargoDet.class, det
						.getPlantaCargoDet().getIdPlantaCargoDet());
				oee = new ConfiguracionUoCab();
				oee = em.find(ConfiguracionUoCab.class, concurso
						.getConfiguracionUoCab().getIdConfiguracionUo());
				codSinarh = oee.getCodigoSinarh();
				anho = det.getConcurso().getFechaCreacion().getYear() + 1900;
				inactivarCopiarPlanta(plantaCargoDet);
			}
			if (variable1.equalsIgnoreCase("GRUPO")
					&& variable2.equalsIgnoreCase("CANCELADO")) {
				Referencias refGrupoCancelado = referenciasUtilFormController
						.getReferencia("ESTADOS_GRUPO", "GRUPO CANCELADO");
				if(det.getConcursoPuestoAgr().getEstado().intValue() == refGrupoCancelado.getValorNum().intValue()){
					
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	/**
	 * Caso 1: concurso no es AdReferendum
	 */
	private void esNoAdReferendum() {
		buscarPuestoConceptoPago();

		for (PuestoConceptoPago p : listaConceptoPago) {
			List<SinAnx> listaAnx = sinarhUtiles.obtenerListaSinAnx(anho,
					new Integer(50), p.getObjCodigo(), p.getCategoria(), null);
			if (!listaAnx.isEmpty()) {
				SinAnx anx = new SinAnx();
				anx = listaAnx.get(0);
				anx.setCantDisponible(anx.getCantDisponible() + 1);
				em.merge(anx);
			}
		}

	}

	
	private void buscarPuestoConceptoPago() {
		Referencias refNoAdReferendum = referenciasUtilFormController
				.getReferencia("ESTADOS_CATEGORIA", "RESERVADO");
		Referencias refAdReferendum = referenciasUtilFormController
				.getReferencia("ESTADOS_CATEGORIA", "PENDIENTE");
		String query = "select pago from PuestoConceptoPago pago "
				+ " where pago.activo is true "
				+ " and pago.plantaCargoDet.idPlantaCargoDet = :id  "
				+ " and pago.estado = :estado ";
		Query q = em.createQuery(query);

		q.setParameter("id", plantaCargoDet.getIdPlantaCargoDet());
		if (concurso.getAdReferendum())
			q.setParameter("estado", refAdReferendum.getValorNum());
		else
			q.setParameter("estado", refNoAdReferendum.getValorNum());
		listaConceptoPago = new ArrayList<PuestoConceptoPago>();
		listaConceptoPago = q.getResultList();
	}

	private void eliminarPuestoConceptoPago() {
		for (PuestoConceptoPago p : listaConceptoPago)
			em.remove(p);

	}

	/*************************************************************
	 * CASO 2 - 3
	 *************************************************************/

	/**
	 * Caso 2
	 * 
	 * @param agr
	 * @param variable1
	 * @param variable2
	 */
	public void reponerCategorias(ConcursoPuestoAgr agr, String variable1,
			String variable2) {
		estadoGrupoDesierto = referenciasUtilFormController.getReferencia(
				"ESTADOS_GRUPO", "GRUPO DESIERTO").getValorNum();

		estadoGrupoCancelado = referenciasUtilFormController.getReferencia(
				"ESTADOS_GRUPO", "GRUPO CANCELADO").getValorNum();

		concurso = em.find(Concurso.class, agr.getConcurso().getIdConcurso());

		oee = new ConfiguracionUoCab();
		oee = em.find(ConfiguracionUoCab.class, concurso
				.getConfiguracionUoCab().getIdConfiguracionUo());
		codSinarh = oee.getCodigoSinarh();
		anho = concurso.getFechaCreacion().getYear() + 1900;
		try {
			if (variable1.equalsIgnoreCase("GRUPO")
					&& variable2.equalsIgnoreCase("CONCURSO")) {
				if (agr.getEstado().intValue() != estadoGrupoDesierto
						.intValue())
					return;
				inactivarGrupoConceptoPago(agr.getIdConcursoPuestoAgr());
				EstadoDet estadoDet = seleccionUtilFormController
						.buscarEstadoDet("CONCURSO", "DESIERTO");
				obtenerListaDePuestos(agr.getIdConcursoPuestoAgr(),
						estadoDet.getIdEstadoDet());
				em.flush();
			}

			if (variable1.equalsIgnoreCase("GRUPO")
					&& variable2.equalsIgnoreCase("CANCELADO")) {
				if (agr.getEstado().intValue() != estadoGrupoCancelado
						.intValue())
					return;
				inactivarGrupoConceptoPago(agr.getIdConcursoPuestoAgr());
				EstadoDet estadoDet = seleccionUtilFormController
						.buscarEstadoDet("CONCURSO", "CANCELADO");
				obtenerListaDePuestos(agr.getIdConcursoPuestoAgr(),
						estadoDet.getIdEstadoDet());
				em.flush();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	/**
	 * Llamado desde el caso 2 - 3 Inactiva registros de la tabla
	 * seleccion.grupo_concepto_pago que cumplan con la condicion
	 * 
	 * @param idGrupo
	 */
	private void inactivarGrupoConceptoPago(Long idGrupo) {
		String sql = "select pago.* "
				+ "from seleccion.grupo_concepto_pago pago "
				+ "where pago.activo is true "
				+ "and pago.tipo = 'GRUPO' "
				+ "and pago.id_concurso_puesto_agr = "
				+ idGrupo
				+ " and pago.estado in (SELECT ref.valor_num "
				+ "FROM seleccion.referencias ref "
				+ "WHERE dominio = 'ESTADOS_CATEGORIA_GRUPO' "
				+ "and (ref.desc_abrev = 'PENDIENTE' or ref.desc_abrev = 'RESERVADO'))";

		List<GrupoConceptoPago> listaGrupoConceptoPago = em.createNativeQuery(
				sql, GrupoConceptoPago.class).getResultList();
		for (GrupoConceptoPago gr : listaGrupoConceptoPago) {
			gr.setActivo(false);
			em.merge(gr);

		}
	}

	/**
	 * Llamado desde el caso 2 - 3 Obtiene los puestos que se encuentren en
	 * estado DESIERTO pertenecientes al grupo recibido como parametro
	 * 
	 * @param id
	 */
	private void obtenerListaDePuestos(Long id, Long idEstadoDet) {

		Query q = em
				.createQuery("Select det from ConcursoPuestoDet det "
						+ " where det.activo is false and det.estadoDet.idEstadoDet = :idEstado "
						+ "and det.concursoPuestoAgr.idConcursoPuestoAgr = :id");
		q.setParameter("idEstado", idEstadoDet);
		q.setParameter("id", id);
		listaDePuestos = q.getResultList();
		for (ConcursoPuestoDet p : listaDePuestos) {
			inactivarCopiarPlanta(p.getPlantaCargoDet());
		}
	}

	/**
	 * Llamado desde el caso 2 - 3 Inactiva los registros de la tabla
	 * planificacion.puesto_concepto_pago que cumplan con la condicion y realiza
	 * una copia de los mismos cuyo estado sea LIBERADO
	 * 
	 * @param planta
	 */
	private void inactivarCopiarPlanta(PlantaCargoDet planta) {
		Integer estadoReservado = referenciasUtilFormController.getReferencia(
				"ESTADOS_CATEGORIA", "RESERVADO").getValorNum();

		Integer estadoLiberado = referenciasUtilFormController.getReferencia(
				"ESTADOS_CATEGORIA", "LIBERADO").getValorNum();
		String sql = "select pago.* "
				+ "from planificacion.puesto_concepto_pago pago "
				+ "where pago.activo is true "
				+ "and pago.id_planta_cargo_det = "
				+ planta.getIdPlantaCargoDet()
				+ " and pago.estado in (SELECT ref.valor_num "
				+ "FROM seleccion.referencias ref "
				+ "WHERE ref.dominio = 'ESTADOS_CATEGORIA' "
				+ "and ref.desc_abrev IN ('RESERVADO','PENDIENTE'))";

		List<PuestoConceptoPago> listaPuestoConceptoPago = em
				.createNativeQuery(sql, PuestoConceptoPago.class)
				.getResultList();
		for (PuestoConceptoPago pcp : listaPuestoConceptoPago) {

			PuestoConceptoPago actual = new PuestoConceptoPago();
			actual.setActivo(false);
			actual.setAnho(pcp.getAnho());
			actual.setCategoria(pcp.getCategoria());
			actual.setDisponible(pcp.getDisponible());
			actual.setEstado(estadoLiberado);
			actual.setFechaAlta(new Date());
			actual.setMonto(pcp.getMonto());
			actual.setObjCodigo(pcp.getObjCodigo());
			actual.setPlantaCargoDet(planta);
			actual.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			em.persist(actual);
			if (pcp.getEstado().intValue() == estadoReservado.intValue())
				actualizarDisponibles(pcp.getCategoria(), pcp.getObjCodigo());

			pcp.setActivo(false);
			em.merge(pcp);
		}

	}

	/**
	 * Llamado desde el caso 2 - 3 Actualiza la cantidad disponible de la tabla
	 * sinarh.sin_anx que cumpla con las condiciones necesarias
	 * 
	 * @param categoria
	 * @param codigo
	 */
	public void actualizarDisponibles(String categoria, Integer codigo) {

		List<SinAnx> listaAnx = sinarhUtiles.obtenerListaSinAnx(anho,
				new Integer(50), codigo, categoria, null);
		if (!listaAnx.isEmpty()) {
			SinAnx anx = new SinAnx();
			anx = listaAnx.get(0);
			anx.setCantDisponible(anx.getCantDisponible() + 1);
			em.merge(anx);
		}

	}

	/*************************************************************
	 * CASO 4
	 *************************************************************/

	public Integer getAnho() {
		return anho;
	}

	public void setAnho(Integer anho) {
		this.anho = anho;
	}

	public String getCodSinarh() {
		return codSinarh;
	}

	public void setCodSinarh(String codSinarh) {
		this.codSinarh = codSinarh;
	}

	public ConfiguracionUoCab getOee() {
		return oee;
	}

	public void setOee(ConfiguracionUoCab oee) {
		this.oee = oee;
	}

	public Concurso getConcurso() {
		return concurso;
	}

	public void setConcurso(Concurso concurso) {
		this.concurso = concurso;
	}

	public List<PuestoConceptoPago> getListaConceptoPago() {
		return listaConceptoPago;
	}

	public void setListaConceptoPago(List<PuestoConceptoPago> listaConceptoPago) {
		this.listaConceptoPago = listaConceptoPago;
	}

	public PlantaCargoDet getPlantaCargoDet() {
		return plantaCargoDet;
	}

	public void setPlantaCargoDet(PlantaCargoDet plantaCargoDet) {
		this.plantaCargoDet = plantaCargoDet;
	}

	public Integer getEstadoGrupoDesierto() {
		return estadoGrupoDesierto;
	}

	public void setEstadoGrupoDesierto(Integer estadoGrupoDesierto) {
		this.estadoGrupoDesierto = estadoGrupoDesierto;
	}

	public List<ConcursoPuestoDet> getListaDePuestos() {
		return listaDePuestos;
	}

	public void setListaDePuestos(List<ConcursoPuestoDet> listaDePuestos) {
		this.listaDePuestos = listaDePuestos;
	}

	public Integer getEstadoGrupoCancelado() {
		return estadoGrupoCancelado;
	}

	public void setEstadoGrupoCancelado(Integer estadoGrupoCancelado) {
		this.estadoGrupoCancelado = estadoGrupoCancelado;
	}

	public List<ConcursoPuestoDet> getListaDePuestosDesierto() {
		return listaDePuestosDesierto;
	}

	public void setListaDePuestosDesierto(
			List<ConcursoPuestoDet> listaDePuestosDesierto) {
		this.listaDePuestosDesierto = listaDePuestosDesierto;
	}
	
	

}
