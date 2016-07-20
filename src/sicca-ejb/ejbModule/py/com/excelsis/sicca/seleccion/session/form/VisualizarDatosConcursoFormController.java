package py.com.excelsis.sicca.seleccion.session.form;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.faces.context.FacesContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.util.Naming;

import enums.Estado;
import py.com.excelsis.sicca.dto.EvalDocumentalDetDTO;
import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.ConfiguracionUoDet;
import py.com.excelsis.sicca.entity.Documentos;
import py.com.excelsis.sicca.entity.Entidad;
import py.com.excelsis.sicca.entity.FechasGrupoPuesto;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.entity.PuestoConceptoPago;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.session.ConcursoList;
import py.com.excelsis.sicca.session.ConcursoPuestoAgrList;
import py.com.excelsis.sicca.session.ConfiguracionUoCabList;
import py.com.excelsis.sicca.session.EntidadList;
import py.com.excelsis.sicca.session.SinEntidadList;
import py.com.excelsis.sicca.session.SinNivelEntidadList;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.ReportUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("visualizarDatosConcursoFormController")
public class VisualizarDatosConcursoFormController implements Serializable {

	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;

	@In(value = "entityManager")
	EntityManager em;

	@In(create = true)
	ConcursoList concursoList;

	@In(create = true)
	ConcursoPuestoAgrList concursoPuestoAgrList;
	
	@In(create = true)
	SinNivelEntidadList sinNivelEntidadList;
	@In(create = true)
	SinEntidadList sinEntidadList;
	@In(create = true)
	EntidadList entidadList;

	@In(create = true)
	ConfiguracionUoCabList configuracionUoCabList;
	@In(create = true, required = false)
	ReportUtilFormController reportUtilFormController;
	@In(required = false, create = true)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;

	private Estado estado = Estado.ACTIVO;
	private Long idtipoConcurso;
	private List<Concurso> concursoLista;
	private List<ConcursoPuestoAgr> concursoPuestoAgrs;
	private List<PlantaCargoDet> puestoCargoDets;
	private boolean paginado;
	private static final String PRE = locate();
	private BigDecimal nenCod;

	private Integer orden;

	private Date fecha_desde;
	private Date fecha_hasta;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd 00:00");
	public void init() {
		try {
			nivelEntidadOeeUtil.init();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void initGruposPublicados() {
		try {
			nivelEntidadOeeUtil.init();
			Calendar c1 = GregorianCalendar.getInstance();
			c1.add(Calendar.MONTH, -2);
			fecha_desde = c1.getTime();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void search() {
				
		concursoList.setNenCodigo(nivelEntidadOeeUtil.getCodNivelEntidad());
				
		concursoList.setIdEntidad(nivelEntidadOeeUtil.getIdSinEntidad());
			
		
		if (nivelEntidadOeeUtil.getOrdenUnidOrg() != null)
			concursoList.setIdConfiguracionUOCab(nivelEntidadOeeUtil.getIdConfigCab());
		
		concursoList.setIdTipoConcurso(idtipoConcurso);
		concursoLista = concursoList.listaResultados();
		concursoPuestoAgrs = new ArrayList<ConcursoPuestoAgr>();
		puestoCargoDets = new ArrayList<PlantaCargoDet>();
	}
	
	
	public void searchGruposPublicados() {
				
		String sql = " select cpa.*  from seleccion.concurso c "
				+ " join planificacion.configuracion_uo_cab uo on uo.id_configuracion_uo = c.id_configuracion_uo "
				+ " join planificacion.entidad e on e.id_entidad = uo.id_entidad "
				+ " join seleccion.concurso_puesto_agr cpa on cpa.id_concurso = c.id_concurso"
				+ " join seleccion.fechas_grupo_puesto fgp on fgp.id_concurso_puesto_agr = cpa.id_concurso_puesto_agr"
				+ "	where c.activo =  true and cpa.estado < 1000 ";//no se incluiran los concursos de publicacion rapida
				
		
		if(fecha_desde != null)
			sql += " and fgp.fecha_publicacion_desde >= "+ "to_date('"+sdf.format(fecha_desde)+"','yyyy-MM-dd 00:00')";
		else
			sql +=" and fgp.fecha_publicacion_desde >= (SELECT CURRENT_TIMESTAMP - interval '2 month') ";
		
		if(fecha_hasta != null)
			sql += " and fgp.fecha_publicacion_desde <= "+ "to_date('"+sdf.format(fecha_hasta)+"','yyyy-MM-dd 00:00')";
		else
			sql +=" and fgp.fecha_publicacion_desde <= (SELECT CURRENT_TIMESTAMP + '1 days') ";
		
		
		if(idtipoConcurso != null)
			sql += " and c.id_datos_esp_tipo_conc = " +idtipoConcurso;
		if(nivelEntidadOeeUtil.getCodNivelEntidad() != null)
			sql += " and e.nen_codigo = "+ nivelEntidadOeeUtil.getCodNivelEntidad();
		
		if(nivelEntidadOeeUtil.getIdSinEntidad() != null)
			sql+= " and e.id_sin_entidad = "+nivelEntidadOeeUtil.getIdSinEntidad();
		
		if (nivelEntidadOeeUtil.getOrdenUnidOrg() != null)
			sql += " and uo.orden = "+nivelEntidadOeeUtil.getOrdenUnidOrg();
		
		sql += " order by fgp.fecha_publicacion_desde desc";
		
		concursoPuestoAgrs = em.createNativeQuery(sql,ConcursoPuestoAgr.class).getResultList();
		
	}
	
	
	public String obtenerFechaPublicacion(Long idConcursoPuestoAgr){
		
		String retorno = "";
		SimpleDateFormat sdf =  new SimpleDateFormat("dd/MM/yyyy");
		
		FechasGrupoPuesto fgp = (FechasGrupoPuesto) em.createNativeQuery("select * from seleccion.fechas_grupo_puesto where id_concurso_puesto_agr = "+idConcursoPuestoAgr
									, FechasGrupoPuesto.class).getResultList().get(0);
		
		if(fgp != null)
			retorno = sdf.format(fgp.getFechaPublicacionDesde());
		
		return retorno;
	}

	public void searchAll() {
		try {
			idtipoConcurso = null;
			estado = Estado.ACTIVO;
			nivelEntidadOeeUtil.limpiar();
			puestoCargoDets= new ArrayList<PlantaCargoDet>();
			concursoPuestoAgrs= new ArrayList<ConcursoPuestoAgr>();
			search();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public void searchAllGruposPublicados() {
		try {
			idtipoConcurso = null;
			estado = Estado.ACTIVO;
			nivelEntidadOeeUtil.limpiar();
			puestoCargoDets= new ArrayList<PlantaCargoDet>();
			concursoPuestoAgrs= new ArrayList<ConcursoPuestoAgr>();
			searchGruposPublicados();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public void generarZip() throws FileNotFoundException, IOException{
		try {
				
			String destino = PRE + File.separator	+ "zipGenerados" + File.separator + "EXCEL"+ File.separator+ new SimpleDateFormat("dd-MM-yyyy HH:mm").format(new Date()) + File.separator;
			
			File directorioCreado = new File(destino);
			
				if (!directorioCreado.exists())
					if (!directorioCreado.mkdirs())
						throw new IOException("No se pudo crear el directorio: "+ directorioCreado.getAbsolutePath());
			
				
			
			for(ConcursoPuestoAgr aux:concursoPuestoAgrs){
				  				//GENERAR LOS REPORTES EN UNA URL
				this.generarPerfilMatrizExcel(aux.getIdConcursoPuestoAgr(), destino);
				
			}	
				
			String url = null;
			
			
			url =  zipDirectorio(destino, directorioCreado.getAbsolutePath()+ File.separator+ new SimpleDateFormat("dd-MM-yyyy HH:mm").format(new Date()));
						
						
			File NuevoZip = new File(url);
			
			enviarZipArchivoANavegador(new SimpleDateFormat("dd-MM-yyyy HH:mm").format(new Date()), NuevoZip);
		} catch (Exception e) {
			e.printStackTrace();

		}
	}
	public static void enviarZipArchivoANavegador(String nombreArchivoSugerido,File archivoABajar) throws FileNotFoundException, IOException {
		
		FileInputStream archivo = new FileInputStream(archivoABajar);

		HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();

		response.setContentType("application/zip");
		//response.setContentType("application/octet-stream");
		response.setContentLength(archivo.available());
		response.setHeader("Content-disposition", "attachment; filename=\""+ nombreArchivoSugerido+".zip\"");
		
		
		ServletOutputStream  salida = response.getOutputStream();
		
		byte buffer[] = new byte[archivo.available()];

		archivo.read(buffer);
		salida.write(buffer);
		
		archivo.close();
		salida.flush();
		salida.close();
		
		FacesContext.getCurrentInstance().responseComplete();
		
		
	}

	
		 
	private static String zipDirectorio(String dir, String destino) {
	// Revisa que el directorio sea direcorio y lee sus archivos.			
	        String direccion;
	        try {
	            File d = new File(dir);
	            if (!d.isDirectory()) {
	                throw new IllegalArgumentException(dir + " no es un directorio.");
	            }
	            String[] entries = d.list();
	            byte[] buffer = new byte[4096]; // Crea un buffer para copiar
	            int bytesRead;
	            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(destino + ".zip"));
	            for (int i = 0; i < entries.length; i++) {
	                File f = new File(d, entries[i]);
	                if (f.isDirectory()) {
	                    continue; //Ignora el directorio
	                }
	                FileInputStream in = new FileInputStream(f);
	                
	                
	 
	                ZipEntry entry = new ZipEntry(f.getName()); //Crea una entrada zip para cada archivo,
	                out.putNextEntry(entry);
	 
	                while ((bytesRead = in.read(buffer)) != -1) {
	                    out.write(buffer, 0, bytesRead);
	                }
	                in.close();
	            }
	            
	            out.close();
	 
	        } catch (Exception ex) {
	            ex.printStackTrace();
	        }
	        return destino + ".zip";
	    }
	
	
	@SuppressWarnings("unchecked")
	private static String locate() {
		EntityManagerFactory emf = null;
		EntityManager emsta = null;
		String dir = "";
		try {
			emf = (EntityManagerFactory) Naming.getInitialContext().lookup(
					"java:/siccaEntityManagerFactory");
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		emsta = emf.createEntityManager();
		List<Referencias> referencias = emsta
				.createQuery(
						"Select r from Referencias r "
								+ " where r.descAbrev like 'ADJUNTOS' and r.dominio like 'ADJUNTOS'")
				.getResultList();
		if (!referencias.isEmpty()) {
			dir = referencias.get(0).getDescLarga();
			if (dir.contains("\\")) {
				dir = dir.replace("\\", System.getProperty("file.separator"));

			} else if (dir.contains("//")) {
				dir = dir.replace("//", System.getProperty("file.separator"));

			} else if (dir.contains("/")) {
				dir = dir.replace("/", System.getProperty("file.separator"));

			}

		}
		return dir;

	}

	
	

	@SuppressWarnings("unchecked")
	public String findCategoria(Long id) {
		List<PuestoConceptoPago> puestos =
			em.createQuery("Select p from PuestoConceptoPago p "
				+ " where p.plantaCargoDet.idPlantaCargoDet=" + id + "" + " and p.objCodigo=111").getResultList();
		if (!puestos.isEmpty())
			return puestos.get(0).getCategoria();

		return "";
	}

	@SuppressWarnings("unchecked")
	public void verGrupos(Long idConcurso) {
		concursoPuestoAgrs =
			em.createQuery("Select c from ConcursoPuestoAgr c " + " where c.concurso.idConcurso="
				+ idConcurso).getResultList();
		if(concursoPuestoAgrs.isEmpty())
			statusMessages.add(Severity.WARN,"El Concurso Seleccionado no posee Grupo , verifique");
		puestoCargoDets= new ArrayList<PlantaCargoDet>();
	}

	@SuppressWarnings("unchecked")
	public void verPuestos(Long idGrupo) {
		puestoCargoDets =
			em.createQuery("Select c.plantaCargoDet from ConcursoPuestoDet c "
				+ " where c.concursoPuestoAgr.idConcursoPuestoAgr=" + idGrupo).getResultList();
		if(puestoCargoDets.isEmpty())
			statusMessages.add(Severity.WARN,"El Grupo Seleccionado no posee Puesto , verifique");
	}

	public void imprimirPerfilMatriz(Long idGrupo) {
		reportUtilFormController.setNombreReporte("RPT_CU015_imprimir_perfil_matriz");
		reportUtilFormController.getParametros().put("idConcursoPuestoAgr", idGrupo);
		reportUtilFormController.imprimirReportePdf();
	}

	public void imprimirPerfilMatrizExcel(Long idGrupo) {
		reportUtilFormController.setNombreReporte("RPT_CU015_imprimir_perfil_matriz_excel");
		reportUtilFormController.getParametros().put("idConcursoPuestoAgr", idGrupo);
		reportUtilFormController.imprimirReporteXLS();
	}
	
	public void generarPerfilMatrizExcel(Long idGrupo, String url) {
		reportUtilFormController.setNombreReporte("RPT_CU015_imprimir_perfil_matriz_excel");
		reportUtilFormController.getParametros().put("idConcursoPuestoAgr", idGrupo);
		reportUtilFormController.generarReporteXLS(url,""+idGrupo);
	}
	
	public String obtenerCodigo(Long idDet) {
		PlantaCargoDet det = em.find(PlantaCargoDet.class, idDet);
		ConfiguracionUoDet uoDet = new ConfiguracionUoDet();
		uoDet = det.getConfiguracionUoDet();
		String code = "";
		List<Integer> listCodes = obtenerListaCodigos(uoDet, null);
		for (Integer codigo : listCodes) {
			code += codigo + ".";

		}

		if (det.getContratado())
			code = code + "C";
		if (det.getPermanente())
			code = code + "P";
		code = code + det.getOrden();
		return code;
	}

	private List<Integer> obtenerListaCodigos(ConfiguracionUoDet uoDet, List<Integer> listCodigos) {
		uoDet.getDenominacionUnidad();
		if (listCodigos == null)
			listCodigos = new ArrayList<Integer>();

		listCodigos.add(0, uoDet.getOrden());
		if (uoDet.getConfiguracionUoDet() != null)
			obtenerListaCodigos(uoDet.getConfiguracionUoDet(), listCodigos);
		else {
			listCodigos.add(0, uoDet.getConfiguracionUoCab().getOrden());
			listCodigos.add(0, uoDet.getConfiguracionUoCab().getEntidad().getSinEntidad().getEntCodigo().intValue());
			listCodigos.add(0, uoDet.getConfiguracionUoCab().getEntidad().getSinEntidad().getNenCodigo().intValue());
		}
		return listCodigos;
	}

	// GETTERS Y SETTERS

	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}

	public BigDecimal getNenCod() {
		return nenCod;
	}

	public void setNenCod(BigDecimal nenCod) {
		this.nenCod = nenCod;
	}

	public Integer getOrden() {
		return orden;
	}

	public void setOrden(Integer orden) {
		this.orden = orden;
	}

	public Long getIdtipoConcurso() {
		return idtipoConcurso;
	}

	public void setIdtipoConcurso(Long idtipoConcurso) {
		this.idtipoConcurso = idtipoConcurso;
	}

	public List<Concurso> getConcursoLista() {
		return concursoLista;
	}

	public void setConcursoLista(List<Concurso> concursoLista) {
		this.concursoLista = concursoLista;
	}

	public List<ConcursoPuestoAgr> getConcursoPuestoAgrs() {
		return concursoPuestoAgrs;
	}

	public void setConcursoPuestoAgrs(List<ConcursoPuestoAgr> concursoPuestoAgrs) {
		this.concursoPuestoAgrs = concursoPuestoAgrs;
	}

	public List<PlantaCargoDet> getPuestoCargoDets() {
		return puestoCargoDets;
	}

	public void setPuestoCargoDets(List<PlantaCargoDet> puestoCargoDets) {
		this.puestoCargoDets = puestoCargoDets;
	}

	public boolean isPaginado() {
		return paginado;
	}

	public void setPaginado(boolean paginado) {
		this.paginado = paginado;
	}


	public Date getFecha_desde() {
		return fecha_desde;
	}


	public void setFecha_desde(Date fecha_desde) {
		this.fecha_desde = fecha_desde;
	}


	public Date getFecha_hasta() {
		return fecha_hasta;
	}


	public void setFecha_hasta(Date fecha_hasta) {
		this.fecha_hasta = fecha_hasta;
	}

}
