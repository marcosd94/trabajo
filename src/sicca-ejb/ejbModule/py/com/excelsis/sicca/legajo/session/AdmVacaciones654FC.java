package py.com.excelsis.sicca.legajo.session;

import java.io.File;
import java.util.Date;
import java.util.List;
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
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.security.AuthorizationException;
import org.richfaces.model.UploadItem;

import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.DatosVacaciones;
import py.com.excelsis.sicca.entity.Documentos;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.form.AdmDocAdjuntoFormController;
import py.com.excelsis.sicca.session.util.JbpmUtilFormController;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.ReferenciasUtilFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("admVacaciones654FC")
public class AdmVacaciones654FC {
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(create = true)
	JbpmUtilFormController jbpmUtilFormController;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(required = false, create = true)
	SeguridadUtilFormController seguridadUtilFormController;
	@In(create = true)
	ReferenciasUtilFormController referenciasUtilFormController;
	@In(required = false, create = true)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;
	@In(required = false, create = true)
	SeleccionUtilFormController seleccionUtilFormController;
	@In(create = true)
	AdmDocAdjuntoFormController admDocAdjuntoFormController;

	private Integer totalDias;
	private Integer diasAUtilizar;
	private Date fechaDesde;
	private Date fechaHasta;
	private String saldo;
	private Integer nroActo;
	private String observacion;
	private Date fechaActo;
	private Long idTipoDoc;
	private String descTipoDoc;
	private List<DatosVacaciones> lGrilla1;
	private Long idPersona;
	private Integer idElemUpdate;
	private byte[] uFile1 = null;
	private String cType1 = null;
	private String fName1 = null;
	private String texto;
	private String nombreDoc;

	public void init() {
		if (!precondInit())
			throw new AuthorizationException(SeamResourceBundle.getBundle().getString("seam_authorization_exception"));
		cargarCabecera();
		cargarGrilla1();

	}

	private Boolean precondInit() {

		return true;
	}

	private void cargarCabecera() {

	}

	private List<DatosEspecificos> traerTipoDoc() {
		Query q =
			em.createQuery("select DatosEspecificos from DatosEspecificos DatosEspecificos "
				+ " where DatosEspecificos.datosGenerales.descripcion = :dG "
				+ " and DatosEspecificos.activo is true and DatosEspecificos.descripcion = :dEDsc and DatosEspecificos.valorAlf = :dEValAlf order by DatosEspecificos.descripcion ");
		q.setParameter("dG", "TIPOS DE DOCUMENTOS");
		q.setParameter("dEDsc", "RESOLUCION");
		q.setParameter("dEValAlf", "RES");
		return q.getResultList();
	}

	public List<SelectItem> traerTipoDocumentoSI() {
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, "Seleccione un Tipo de Documento"));
		for (DatosEspecificos o : traerTipoDoc())
			si.add(new SelectItem(o.getIdDatosEspecificos(), o.getDescripcion()));
		return si;
	}

	private void cargarGrilla1() {
		Query q =
			em.createQuery("select DatosVacaciones from DatosVacaciones DatosVacaciones "
				+ " where DatosVacaciones.activo is true and DatosVacaciones.persona.idPersona = :idPersona order by DatosVacaciones.fechaFin desc");
		q.setParameter("idPersona", idPersona);
		lGrilla1 = q.getResultList();
	}

	private Boolean precondAgregar(Boolean valDocumento) {
		if (valDocumento) {
			if (/*totalDias == null || diasAUtilizar == null || */nroActo == null || fechaActo == null
				|| fechaDesde == null || fechaHasta == null || uFile1 == null || idTipoDoc == null) {
				statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("GENERICO_CAMPOS_REQUERIDOS"));
				return false;
			}
		} else {
			//if (totalDias == null || diasAUtilizar == null || fechaDesde == null
			if (fechaDesde == null || fechaHasta == null) {
				statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("GENERICO_CAMPOS_REQUERIDOS"));
				return false;
			}
		}
		if (fechaDesde.getTime() > fechaHasta.getTime()) {
			statusMessages.add(Severity.ERROR, "La Fecha Desde no puede ser mayor a la Fecha Hasta");
			return false;
		}

		/*if (totalDias.intValue() < diasAUtilizar.intValue()) {
			statusMessages.add(Severity.ERROR, "Total de Días no puede ser menor a Dias a Utilizar");
			return false;
		}*/
		if (observacion != null && observacion.length() > 500) {
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("GENERICO_SUPERADA_CANT_MAX_CAR")
				+ ": Observación (500)");
			return false;
		}
		return true;
	}

	public void updateSaldo() {
		if (totalDias != null && diasAUtilizar != null) {
			if (totalDias.intValue() < diasAUtilizar.intValue()) {
				statusMessages.add(Severity.ERROR, "Total de Días no puede ser menor a Dias a Utilizar");
				saldo = "0";
			}
			saldo = (totalDias - diasAUtilizar) + "";

		} else
			saldo = "0";
	}

	public String agregar() {
		if (!precondAgregar(true))
			return "FAIL";
		try {
			DatosVacaciones dv = new DatosVacaciones();
			// Adjuntar documento
			Long idDocuGenerado = guardarDocumento(null);
			if (idDocuGenerado != null) {
				dv.setDocumentos(new Documentos());
				dv.getDocumentos().setIdDocumento(idDocuGenerado);
				dv.setActivo(true);
				//dv.setTotalDias(totalDias);
				dv.setPersona(new Persona());
				dv.getPersona().setIdPersona(idPersona);
				//dv.setDiasUtilizar(diasAUtilizar);
				dv.setFechaInicio(fechaDesde);
				dv.setFechaFin(fechaHasta);
				dv.setObsVacaciones(observacion);
				dv.setNroActo(nroActo);
				dv.setFechaActo(fechaActo);
				dv.setFechaAlta(new Date());
				//dv.setSaldo(totalDias - diasAUtilizar);
				dv.setUsuAlta(usuarioLogueado.getCodigoUsuario());
				em.persist(dv);
				em.flush();
				Documentos doc = new Documentos();
				doc = em.find(Documentos.class, idDocuGenerado);
				doc.setPersona(em.find(Persona.class, idPersona));
				doc.setIdTabla(dv.getIdDatosVacaciones());
				em.merge(doc);
				em.flush();
			}else{
				return null;
			}
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
			// refresh
			cargarGrilla1();
			limpiar();
			return "OK";
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("GENERICO_NO_MSG"));
			return null;

		}

	}

	private Long guardarDocumento(Documentos docu) {
		if (uFile1 != null) {
			Long idDocAnterior = (docu != null ? docu.getIdDocumento() : null);
			UploadItem fileItem =
				seleccionUtilFormController.crearUploadItem(fName1, uFile1.length, cType1, uFile1);
			Long idDocuGenerado;
			String nombreTabla = "DatosVacaciones";
			String nombrePantalla = "admVacaciones654FC";
			String cSeparador = File.separator;
			Persona persona = em.find(Persona.class, usuarioLogueado.getPersona().getIdPersona());
			String direccionFisica =
				cSeparador + "LEGAJO" + cSeparador
					+ persona.getDocumentoIdentidad() + "_" + persona.getIdPersona()+ cSeparador+"VACACIONES"+ cSeparador;
			idDocuGenerado =
				admDocAdjuntoFormController.guardarDoc(fileItem, direccionFisica, nombrePantalla, idTipoDoc, nombreTabla, idDocAnterior);
			return idDocuGenerado;
		}
		return null;
	}

	public String actualizar() {
		if (!precondAgregar(false)) {
			return null;
		}
		if (idElemUpdate != null) {
			try {
				DatosVacaciones dv = lGrilla1.get(idElemUpdate.intValue());
				// Adjuntar documento
				if (uFile1 != null) {
					Long idDocuGenerado = guardarDocumento(dv.getDocumentos());
					if (idDocuGenerado != null) {
						dv.setDocumentos(new Documentos());
						dv.getDocumentos().setIdDocumento(idDocuGenerado);
					
						Documentos doc = new Documentos();
						doc = em.find(Documentos.class, idDocuGenerado);
						doc.setPersona(em.find(Persona.class, idPersona));
						doc.setIdTabla(dv.getIdDatosVacaciones());
						em.merge(doc);
						em.flush();
					}else{
						return null;
					}
				}
				dv.setTotalDias(totalDias);
				dv.setDiasUtilizar(diasAUtilizar);
				dv.setFechaInicio(fechaDesde);
				dv.setFechaFin(fechaHasta);
				dv.setObsVacaciones(observacion);
				dv.setNroActo(nroActo);
				dv.setFechaActo(fechaActo);
				dv.setSaldo(totalDias - diasAUtilizar);
				dv.setFechaMod(new Date());
				dv.setUsuMod(usuarioLogueado.getCodigoUsuario());
				lGrilla1.set(idElemUpdate.intValue(), em.merge(dv));
				idElemUpdate = null;
				limpiarNuevo();
				em.flush();
				statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
				return "OK";
			} catch (Exception e) {
				e.printStackTrace();
				statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("GENERICO_NO_MSG"));
				return null;

			}
		}
		return null;
	}

	public void editar(int index) {
		limpiar();
		if (index < lGrilla1.size()) {
			idElemUpdate = index;
			DatosVacaciones dv = lGrilla1.get(idElemUpdate);
			totalDias = dv.getTotalDias();
			diasAUtilizar = dv.getDiasUtilizar();
			fechaDesde = dv.getFechaInicio();
			fechaHasta = dv.getFechaFin();
			saldo = dv.getSaldo() + "";
			nroActo = dv.getNroActo();
			observacion = dv.getObsVacaciones();
			if (dv.getDocumentos() != null) {
				Documentos doc = em.find(Documentos.class, dv.getDocumentos().getIdDocumento());
				idTipoDoc = doc.getDatosEspecificos().getIdDatosEspecificos();
				nombreDoc = doc.getNombreFisicoDoc();
			}

			fechaActo = dv.getFechaActo();
		}
	}

	public void eliminar(int index) {
		try {
			if (lGrilla1.get(index).getIdDatosVacaciones() != null) {
				DatosVacaciones dv = lGrilla1.get(index);
				dv.setFechaMod(new Date());
				dv.setUsuMod(usuarioLogueado.getCodigoUsuario());
				dv.setActivo(false);

				dv = em.merge(dv);
				// Inactivar el documento
				if (dv.getDocumentos() != null) {
					Documentos docu =
						em.find(Documentos.class, dv.getDocumentos().getIdDocumento());
					docu.setActivo(false);
					docu.setUsuMod(usuarioLogueado.getCodigoUsuario());
					docu.setFechaMod(new Date());
					em.merge(docu);
				}
				em.flush();
			}
			lGrilla1.remove(index);
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
		} catch (Exception e) {
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("GENERICO_NO_MSG"));
			e.printStackTrace();
		}
	}

	private Boolean precondSave() {
		if (lGrilla1 == null) {
			statusMessages.add(org.jboss.seam.international.StatusMessage.Severity.ERROR, "No hay nada que guardar");
			return false;
		}
		return true;
	}

	public String save() {
		if (!precondSave())
			return "FAIL";
		try {
			em.flush();
			statusMessages.add(org.jboss.seam.international.StatusMessage.Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
			return "OK";
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add(org.jboss.seam.international.StatusMessage.Severity.ERROR, SeamResourceBundle.getBundle().getString("GENERICO_NO_MSG"));
		}
		return null;
	}

	public void limpiarNuevo() {
		cType1 = null;
		descTipoDoc = null;
		diasAUtilizar = null;
		fechaActo = null;
		fechaDesde = null;
		fechaHasta = null;
		fName1 = null;
		idElemUpdate = null;
		idTipoDoc = null;
		totalDias = null;
		saldo = null;
		nroActo = null;
		observacion = null;
		nombreDoc = null;
	}

	public void limpiar() {
		cType1 = null;
		descTipoDoc = null;
		diasAUtilizar = null;
		fechaActo = null;
		fechaDesde = null;
		fechaHasta = null;
		fName1 = null;
		idElemUpdate = null;
		idTipoDoc = null;
		totalDias = null;
		saldo = null;
		nroActo = null;
		observacion = null;
		nombreDoc = null;
	}

	public Integer getTotalDias() {
		return totalDias;
	}

	public void setTotalDias(Integer totalDias) {
		this.totalDias = totalDias;
	}

	public Integer getDiasAUtilizar() {
		return diasAUtilizar;
	}

	public void setDiasAUtilizar(Integer diasAUtilizar) {
		this.diasAUtilizar = diasAUtilizar;
	}

	public Date getFechaDesde() {
		return fechaDesde;
	}

	public void setFechaDesde(Date fechaDesde) {
		this.fechaDesde = fechaDesde;
	}

	public Date getFechaHasta() {
		return fechaHasta;
	}

	public void setFechaHasta(Date fechaHasta) {
		this.fechaHasta = fechaHasta;
	}

	public String getSaldo() {
		return saldo;
	}

	public void setSaldo(String saldo) {
		this.saldo = saldo;
	}

	public String getObservacion() {
		return observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}

	public Long getIdTipoDoc() {
		return idTipoDoc;
	}

	public void setIdTipoDoc(Long idTipoDoc) {
		this.idTipoDoc = idTipoDoc;
	}

	public String getDescTipoDoc() {
		return descTipoDoc;
	}

	public void setDescTipoDoc(String descTipoDoc) {
		this.descTipoDoc = descTipoDoc;
	}

	public List<DatosVacaciones> getlGrilla1() {
		return lGrilla1;
	}

	public void setlGrilla1(List<DatosVacaciones> lGrilla1) {
		this.lGrilla1 = lGrilla1;
	}

	public Long getIdPersona() {
		return idPersona;
	}

	public void setIdPersona(Long idPersona) {
		this.idPersona = idPersona;
	}

	public byte[] getuFile1() {
		return uFile1;
	}

	public void setuFile1(byte[] uFile1) {
		this.uFile1 = uFile1;
	}

	public String getcType1() {
		return cType1;
	}

	public void setcType1(String cType1) {
		this.cType1 = cType1;
	}

	public String getfName1() {
		return fName1;
	}

	public void setfName1(String fName1) {
		this.fName1 = fName1;
	}

	public Integer getIdElemUpdate() {
		return idElemUpdate;
	}

	public void setIdElemUpdate(Integer idElemUpdate) {
		this.idElemUpdate = idElemUpdate;
	}

	public Integer getNroActo() {
		return nroActo;
	}

	public void setNroActo(Integer nroActo) {
		this.nroActo = nroActo;
	}

	public Date getFechaActo() {
		return fechaActo;
	}

	public void setFechaActo(Date fechaActo) {
		this.fechaActo = fechaActo;
	}

	public String getTexto() {
		return texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}

	public String getNombreDoc() {
		return nombreDoc;
	}

	public void setNombreDoc(String nombreDoc) {
		this.nombreDoc = nombreDoc;
	}

}
