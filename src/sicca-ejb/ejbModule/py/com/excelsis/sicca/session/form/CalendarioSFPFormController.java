package py.com.excelsis.sicca.session.form;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;


import py.com.excelsis.sicca.entity.CalendarioSfp;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.CalendarioSfpHome;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.PAGE)
@Name("calendarioSFPFormController")
public class CalendarioSFPFormController implements Serializable {

	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(create = true)
	CalendarioSfpHome calendarioSfpHome;

	private CalendarioSfp calendarioSfp;

	private Boolean lunes;
	private Boolean martes;
	private Boolean miercoles;
	private Boolean jueves;
	private Boolean viernes;
	private Boolean sabado;
	private Boolean domingo;
	private Boolean calendarioOmision;
	private Boolean cargadoHorario;

	private String horaDesdeLunes;
	private String horaHastaLunes;
	private String horaDesdeMartes;
	private String horaHastaMartes;
	private String horaDesdeMiercoles;
	private String horaHastaMiercoles;
	private String horaDesdeJueves;
	private String horaHastaJueves;
	private String horaDesdeViernes;
	private String horaHastaViernes;
	private String horaDesdeSabado;
	private String horaHastaSabado;
	private String horaDesdeDomingo;
	private String horaHastaDomingo;

	/**
	 * Método que inicializa los datos
	 */
	public void init() {
		cargadoHorario = false;
		calendarioSfp = new CalendarioSfp();
		calendarioSfp = calendarioSfpHome.getInstance();
		calendarioOmision = false;
		if (calendarioSfpHome.isIdDefined()) {
			if (calendarioSfp.getDomHoraDesde() != null
					&& calendarioSfp.getDomHoraHasta() != null) {
				domingo = true;
				horaDesdeDomingo = buscarHora(calendarioSfp.getDomHoraDesde()
						.toString());
				horaHastaDomingo = buscarHora(calendarioSfp.getDomHoraHasta()
						.toString());
			}
			if (calendarioSfp.getLunHoraDesde() != null
					&& calendarioSfp.getLunHoraHasta() != null) {
				lunes = true;
				horaDesdeLunes = buscarHora(calendarioSfp.getLunHoraDesde()
						.toString());
				horaHastaLunes = buscarHora(calendarioSfp.getLunHoraHasta()
						.toString());
			}
			if (calendarioSfp.getMarHoraDesde() != null
					&& calendarioSfp.getMarHoraHasta() != null) {
				martes = true;
				horaDesdeMartes = buscarHora(calendarioSfp.getMarHoraDesde()
						.toString());
				horaHastaMartes = buscarHora(calendarioSfp.getMarHoraHasta()
						.toString());
			}

			if (calendarioSfp.getMieHoraDesde() != null
					&& calendarioSfp.getMieHoraHasta() != null) {
				miercoles = true;
				horaDesdeMiercoles = buscarHora(calendarioSfp.getMieHoraDesde()
						.toString());
				horaHastaMiercoles = buscarHora(calendarioSfp.getMieHoraHasta()
						.toString());
			}

			if (calendarioSfp.getJueHoraDesde() != null
					&& calendarioSfp.getJueHoraHasta() != null) {
				jueves = true;
				horaDesdeJueves = buscarHora(calendarioSfp.getJueHoraDesde()
						.toString());
				horaHastaJueves = buscarHora(calendarioSfp.getJueHoraHasta()
						.toString());
			}

			if (calendarioSfp.getVieHoraDesde() != null
					&& calendarioSfp.getVieHoraHasta() != null) {
				viernes = true;
				horaDesdeViernes = buscarHora(calendarioSfp.getVieHoraDesde()
						.toString());
				horaHastaViernes = buscarHora(calendarioSfp.getVieHoraHasta()
						.toString());
			}

			if (calendarioSfp.getSabHoraDesde() != null
					&& calendarioSfp.getSabHoraHasta() != null) {
				sabado = true;
				horaDesdeSabado = buscarHora(calendarioSfp.getSabHoraDesde()
						.toString());
				horaHastaSabado = buscarHora(calendarioSfp.getSabHoraHasta()
						.toString());
			}
		}
	}

	private String buscarHora(String cod) {
		
		String[] arrayCodigo = cod.split(":");
		return arrayCodigo[0] + ":" + arrayCodigo[1];
	}

	public void calendarioPorOmision() {
		if (calendarioOmision)
			calendarioOmision = false;
		else
			calendarioOmision = true;
		if (calendarioOmision) {
			lunes = true;
			martes = true;
			miercoles = true;
			jueves = true;
			viernes = true;
			sabado = false;
			domingo = false;
			String desde = "07:00";
			String hasta = "15:00";
			horaDesdeLunes = desde;
			horaHastaLunes = hasta;
			horaDesdeMartes = desde;
			horaHastaMartes = hasta;
			horaDesdeMiercoles = desde;
			horaHastaMiercoles = hasta;
			horaDesdeJueves = desde;
			horaHastaJueves = hasta;
			horaDesdeViernes = desde;
			horaHastaViernes = hasta;
			horaDesdeSabado = null;
			horaHastaSabado = null;
			horaDesdeDomingo = null;
			horaHastaDomingo = null;
		}
		if (!calendarioOmision) {
			lunes = false;
			martes = false;
			miercoles = false;
			jueves = false;
			viernes = false;
			sabado = false;
			domingo = false;
			horaDesdeLunes = null;
			horaHastaLunes = null;
			horaDesdeMartes = null;
			horaHastaMartes = null;
			horaDesdeMiercoles = null;
			horaHastaMiercoles = null;
			horaDesdeJueves = null;
			horaHastaJueves = null;
			horaDesdeViernes = null;
			horaHastaViernes = null;
			horaDesdeSabado = null;
			horaHastaSabado = null;
			horaDesdeDomingo = null;
			horaHastaDomingo = null;
		}
	}

	/**
	 * Metodo que guarda los datos en la bd
	 * 
	 * @return
	 */
	public String save() {
		try {
			Boolean cumple = false;
			if (lunes) {
				cumple = false;
				if (horaDesdeLunes != null && !horaDesdeLunes.equals("")
						&& horaHastaLunes != null && !horaHastaLunes.equals(""))
					cumple = true;
				if (cumple) {
					int[] horaDesde = getHora(horaDesdeLunes);
					Date fechaDesde = new Date();
					fechaDesde.setHours(horaDesde[0]);
					fechaDesde.setMinutes(horaDesde[1]);
					calendarioSfp.setLunHoraDesde(fechaDesde);
					int[] horaHasta = getHora(horaHastaLunes);
					Date fechaHasta = new Date();
					fechaHasta.setHours(horaHasta[0]);
					fechaHasta.setMinutes(horaHasta[1]);
					calendarioSfp.setLunHoraHasta(fechaHasta);
					cargadoHorario = true;
				} else {
					statusMessages.clear();
					statusMessages
							.add(Severity.INFO,
									"Debe ingresar los valores de Horas correspondientes al día seleccionado");
					return null;
				}
			}
			if (martes) {
				cumple = false;
				if (horaDesdeMartes != null && !horaDesdeMartes.equals("")
						&& horaHastaMartes != null
						&& !horaHastaMartes.equals(""))
					cumple = true;
				if (cumple) {
					int[] horaDesde = getHora(horaDesdeMartes);
					Date fechaDesde = new Date();
					fechaDesde.setHours(horaDesde[0]);
					fechaDesde.setMinutes(horaDesde[1]);
					calendarioSfp.setMarHoraDesde(fechaDesde);
					int[] horaHasta = getHora(horaHastaMartes);
					Date fechaHasta = new Date();
					fechaHasta.setHours(horaHasta[0]);
					fechaHasta.setMinutes(horaHasta[1]);
					calendarioSfp.setMarHoraHasta(fechaHasta);
					cargadoHorario = true;
				} else {
					statusMessages.clear();
					statusMessages
							.add(Severity.INFO,
									"Debe ingresar los valores de Horas correspondientes al día seleccionado");
					return null;
				}
			}

			if (miercoles) {
				cumple = false;
				if (horaDesdeMiercoles != null
						&& !horaDesdeMiercoles.equals("")
						&& horaHastaMiercoles != null
						&& !horaHastaMiercoles.equals(""))
					cumple = true;
				if (cumple) {
					int[] horaDesde = getHora(horaDesdeMiercoles);
					Date fechaDesde = new Date();
					fechaDesde.setHours(horaDesde[0]);
					fechaDesde.setMinutes(horaDesde[1]);
					calendarioSfp.setMieHoraDesde(fechaDesde);
					int[] horaHasta = getHora(horaHastaMiercoles);
					Date fechaHasta = new Date();
					fechaHasta.setHours(horaHasta[0]);
					fechaHasta.setMinutes(horaHasta[1]);
					calendarioSfp.setMieHoraHasta(fechaHasta);
					cargadoHorario = true;
				} else {
					statusMessages.clear();
					statusMessages
							.add(Severity.INFO,
									"Debe ingresar los valores de Horas correspondientes al día seleccionado");
					return null;
				}
			}

			if (jueves) {
				cumple = false;
				if (horaDesdeJueves != null && !horaDesdeJueves.equals("")
						&& horaHastaJueves != null
						&& !horaHastaJueves.equals(""))
					cumple = true;
				if (cumple) {
					int[] horaDesde = getHora(horaDesdeJueves);
					Date fechaDesde = new Date();
					fechaDesde.setHours(horaDesde[0]);
					fechaDesde.setMinutes(horaDesde[1]);
					calendarioSfp.setJueHoraDesde(fechaDesde);
					int[] horaHasta = getHora(horaHastaJueves);
					Date fechaHasta = new Date();
					fechaHasta.setHours(horaHasta[0]);
					fechaHasta.setMinutes(horaHasta[1]);
					calendarioSfp.setJueHoraHasta(fechaHasta);
					cargadoHorario = true;
				} else {
					statusMessages.clear();
					statusMessages
							.add(Severity.INFO,
									"Debe ingresar los valores de Horas correspondientes al día seleccionado");
					return null;
				}
			}

			if (viernes) {
				cumple = false;
				if (horaDesdeViernes != null && !horaDesdeViernes.equals("")
						&& horaHastaViernes != null
						&& !horaHastaViernes.equals(""))
					cumple = true;
				if (cumple) {
					int[] horaDesde = getHora(horaDesdeViernes);
					Date fechaDesde = new Date();
					fechaDesde.setHours(horaDesde[0]);
					fechaDesde.setMinutes(horaDesde[1]);
					calendarioSfp.setVieHoraDesde(fechaDesde);
					int[] horaHasta = getHora(horaHastaViernes);
					Date fechaHasta = new Date();
					fechaHasta.setHours(horaHasta[0]);
					fechaHasta.setMinutes(horaHasta[1]);
					calendarioSfp.setVieHoraHasta(fechaHasta);
					cargadoHorario = true;
				} else {
					statusMessages.clear();
					statusMessages
							.add(Severity.INFO,
									"Debe ingresar los valores de Horas correspondientes al día seleccionado");
					return null;
				}
			}

			if (sabado) {
				cumple = false;
				if (horaDesdeSabado != null && !horaDesdeSabado.equals("")
						&& horaHastaSabado != null
						&& !horaHastaSabado.equals(""))
					cumple = true;
				if (cumple) {
					int[] horaDesde = getHora(horaDesdeSabado);
					Date fechaDesde = new Date();
					fechaDesde.setHours(horaDesde[0]);
					fechaDesde.setMinutes(horaDesde[1]);
					calendarioSfp.setSabHoraDesde(fechaDesde);
					int[] horaHasta = getHora(horaHastaSabado);
					Date fechaHasta = new Date();
					fechaHasta.setHours(horaHasta[0]);
					fechaHasta.setMinutes(horaHasta[1]);
					calendarioSfp.setSabHoraHasta(fechaHasta);
					cargadoHorario = true;
				} else {
					statusMessages.clear();
					statusMessages
							.add(Severity.INFO,
									"Debe ingresar los valores de Horas correspondientes al día seleccionado");
					return null;
				}
			}

			if (domingo) {
				cumple = false;
				if (horaDesdeDomingo != null && !horaDesdeDomingo.equals("")
						&& horaHastaDomingo != null
						&& !horaHastaDomingo.equals(""))
					cumple = true;
				if (cumple) {
					int[] horaDesde = getHora(horaDesdeDomingo);
					Date fechaDesde = new Date();
					fechaDesde.setHours(horaDesde[0]);
					fechaDesde.setMinutes(horaDesde[1]);
					calendarioSfp.setDomHoraDesde(fechaDesde);
					int[] horaHasta = getHora(horaHastaDomingo);
					Date fechaHasta = new Date();
					fechaHasta.setHours(horaHasta[0]);
					fechaHasta.setMinutes(horaHasta[1]);
					calendarioSfp.setDomHoraHasta(fechaHasta);
					cargadoHorario = true;
				} else {
					statusMessages.clear();
					statusMessages
							.add(Severity.INFO,
									"Debe ingresar los valores de Horas correspondientes al día seleccionado");
					return null;
				}
			}
			if (!cargadoHorario) {
				statusMessages.clear();
				statusMessages.add(Severity.ERROR, SeamResourceBundle
						.getBundle().getString("CU294_msg"));
				return null;
			}
			calendarioSfpHome.setInstance(calendarioSfp);
			String resultado = calendarioSfpHome.persist();
			if (resultado.equals("persisted")) {
				statusMessages.clear();
				statusMessages.add(Severity.INFO, SeamResourceBundle
						.getBundle().getString("GENERICO_MSG"));
			
			}
			else{
				statusMessages.clear();
				statusMessages.add(Severity.ERROR, SeamResourceBundle
						.getBundle().getString("CU400_msg4"));
			}
			return resultado;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Metodo que guarda los datos en la bd
	 * 
	 * @return
	 */
	public String updated() {
		try {
			Boolean cumple = false;
			if (lunes) {
				cumple = false;
				if (horaDesdeLunes != null && !horaDesdeLunes.equals("")
						&& horaHastaLunes != null && !horaHastaLunes.equals(""))
					cumple = true;
				if (cumple) {
					int[] horaDesde = getHora(horaDesdeLunes);
					Date fechaDesde = new Date();
					fechaDesde.setHours(horaDesde[0]);
					fechaDesde.setMinutes(horaDesde[1]);
					calendarioSfp.setLunHoraDesde(fechaDesde);
					int[] horaHasta = getHora(horaHastaLunes);
					Date fechaHasta = new Date();
					fechaHasta.setHours(horaHasta[0]);
					fechaHasta.setMinutes(horaHasta[1]);
					calendarioSfp.setLunHoraHasta(fechaHasta);
					cargadoHorario = true;
				} else {
					statusMessages.clear();
					statusMessages
							.add(Severity.INFO,
									"Debe ingresar los valores de Horas correspondientes al día seleccionado");
					return null;
				}
			}
			if (martes) {
				cumple = false;
				if (horaDesdeMartes != null && !horaDesdeMartes.equals("")
						&& horaHastaMartes != null
						&& !horaHastaMartes.equals(""))
					cumple = true;
				if (cumple) {
					int[] horaDesde = getHora(horaDesdeMartes);
					Date fechaDesde = new Date();
					fechaDesde.setHours(horaDesde[0]);
					fechaDesde.setMinutes(horaDesde[1]);
					calendarioSfp.setMarHoraDesde(fechaDesde);
					int[] horaHasta = getHora(horaHastaMartes);
					Date fechaHasta = new Date();
					fechaHasta.setHours(horaHasta[0]);
					fechaHasta.setMinutes(horaHasta[1]);
					calendarioSfp.setMarHoraHasta(fechaHasta);
					cargadoHorario = true;
				} else {
					statusMessages.clear();
					statusMessages
							.add(Severity.INFO,
									"Debe ingresar los valores de Horas correspondientes al día seleccionado");
					return null;
				}
			}

			if (miercoles) {
				cumple = false;
				if (horaDesdeMiercoles != null
						&& !horaDesdeMiercoles.equals("")
						&& horaHastaMiercoles != null
						&& !horaHastaMiercoles.equals(""))
					cumple = true;
				if (cumple) {
					int[] horaDesde = getHora(horaDesdeMiercoles);
					Date fechaDesde = new Date();
					fechaDesde.setHours(horaDesde[0]);
					fechaDesde.setMinutes(horaDesde[1]);
					calendarioSfp.setMieHoraDesde(fechaDesde);
					int[] horaHasta = getHora(horaHastaMiercoles);
					Date fechaHasta = new Date();
					fechaHasta.setHours(horaHasta[0]);
					fechaHasta.setMinutes(horaHasta[1]);
					calendarioSfp.setMieHoraHasta(fechaHasta);
					cargadoHorario = true;
				} else {
					statusMessages.clear();
					statusMessages
							.add(Severity.INFO,
									"Debe ingresar los valores de Horas correspondientes al día seleccionado");
					return null;
				}
			}

			if (jueves) {
				cumple = false;
				if (horaDesdeJueves != null && !horaDesdeJueves.equals("")
						&& horaHastaJueves != null
						&& !horaHastaJueves.equals(""))
					cumple = true;
				if (cumple) {
					int[] horaDesde = getHora(horaDesdeJueves);
					Date fechaDesde = new Date();
					fechaDesde.setHours(horaDesde[0]);
					fechaDesde.setMinutes(horaDesde[1]);
					calendarioSfp.setJueHoraDesde(fechaDesde);
					int[] horaHasta = getHora(horaHastaJueves);
					Date fechaHasta = new Date();
					fechaHasta.setHours(horaHasta[0]);
					fechaHasta.setMinutes(horaHasta[1]);
					calendarioSfp.setJueHoraHasta(fechaHasta);
					cargadoHorario = true;
				} else {
					statusMessages.clear();
					statusMessages
							.add(Severity.INFO,
									"Debe ingresar los valores de Horas correspondientes al día seleccionado");
					return null;
				}
			}

			if (viernes) {
				cumple = false;
				if (horaDesdeViernes != null && !horaDesdeViernes.equals("")
						&& horaHastaViernes != null
						&& !horaHastaViernes.equals(""))
					cumple = true;
				if (cumple) {
					int[] horaDesde = getHora(horaDesdeViernes);
					Date fechaDesde = new Date();
					fechaDesde.setHours(horaDesde[0]);
					fechaDesde.setMinutes(horaDesde[1]);
					calendarioSfp.setVieHoraDesde(fechaDesde);
					int[] horaHasta = getHora(horaHastaViernes);
					Date fechaHasta = new Date();
					fechaHasta.setHours(horaHasta[0]);
					fechaHasta.setMinutes(horaHasta[1]);
					calendarioSfp.setVieHoraHasta(fechaHasta);
					cargadoHorario = true;
				} else {
					statusMessages.clear();
					statusMessages
							.add(Severity.INFO,
									"Debe ingresar los valores de Horas correspondientes al día seleccionado");
					return null;
				}
			}

			if (sabado) {
				cumple = false;
				if (horaDesdeSabado != null && !horaDesdeSabado.equals("")
						&& horaHastaSabado != null
						&& !horaHastaSabado.equals(""))
					cumple = true;
				if (cumple) {
					int[] horaDesde = getHora(horaDesdeSabado);
					Date fechaDesde = new Date();
					fechaDesde.setHours(horaDesde[0]);
					fechaDesde.setMinutes(horaDesde[1]);
					calendarioSfp.setSabHoraDesde(fechaDesde);
					int[] horaHasta = getHora(horaHastaSabado);
					Date fechaHasta = new Date();
					fechaHasta.setHours(horaHasta[0]);
					fechaHasta.setMinutes(horaHasta[1]);
					calendarioSfp.setSabHoraHasta(fechaHasta);
					cargadoHorario = true;
				} else {
					statusMessages.clear();
					statusMessages
							.add(Severity.INFO,
									"Debe ingresar los valores de Horas correspondientes al día seleccionado");
					return null;
				}
			}

			if (domingo) {
				cumple = false;
				if (horaDesdeDomingo != null && !horaDesdeDomingo.equals("")
						&& horaHastaDomingo != null
						&& !horaHastaDomingo.equals(""))
					cumple = true;
				if (cumple) {
					int[] horaDesde = getHora(horaDesdeDomingo);
					Date fechaDesde = new Date();
					fechaDesde.setHours(horaDesde[0]);
					fechaDesde.setMinutes(horaDesde[1]);
					calendarioSfp.setDomHoraDesde(fechaDesde);
					int[] horaHasta = getHora(horaHastaDomingo);
					Date fechaHasta = new Date();
					fechaHasta.setHours(horaHasta[0]);
					fechaHasta.setMinutes(horaHasta[1]);
					calendarioSfp.setDomHoraHasta(fechaHasta);
					cargadoHorario = true;
				} else {
					statusMessages.clear();
					statusMessages
							.add(Severity.INFO,
									"Debe ingresar los valores de Horas correspondientes al día seleccionado");
					return null;
				}
			}
			if (!cargadoHorario) {
				statusMessages.clear();
				statusMessages.add(Severity.ERROR, SeamResourceBundle
						.getBundle().getString("CU294_msg"));
				return null;
			}
			calendarioSfpHome.setInstance(calendarioSfp);
			String resultado = calendarioSfpHome.update();

			if (resultado.equals("updated")) {
				statusMessages.clear();
				statusMessages.add(Severity.INFO, SeamResourceBundle
						.getBundle().getString("GENERICO_MSG"));
			
			}
			else{
				statusMessages.clear();
				statusMessages.add(Severity.ERROR, SeamResourceBundle
						.getBundle().getString("CU400_msg4"));
			}
			return resultado;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * @param horaCad
	 * @return
	 */
	private int[] getHora(String horaCad) {
		String[] horas = horaCad.split(":");
		if (horas.length != 2) {
			return null;
		} else {
			String hora = horas[0];
			String minuto = horas[1];
			try {
				int hh = Integer.parseInt(hora);
				int mm = Integer.parseInt(minuto);

				if (hh < 0 || hh > 23 || mm < 0 || mm >= 60) {
					return null;
				}
				int[] v = new int[2];
				v[0] = hh;
				v[1] = mm;
				return v;
			} catch (Exception e) {
				return null;
			}
		}
	}

	public CalendarioSfp getCalendarioSfp() {
		return calendarioSfp;
	}

	public void setCalendarioSfp(CalendarioSfp calendarioSfp) {
		this.calendarioSfp = calendarioSfp;
	}

	public Boolean getLunes() {
		return lunes;
	}

	public void setLunes(Boolean lunes) {
		this.lunes = lunes;
	}

	public Boolean getMartes() {
		return martes;
	}

	public void setMartes(Boolean martes) {
		this.martes = martes;
	}

	public Boolean getMiercoles() {
		return miercoles;
	}

	public void setMiercoles(Boolean miercoles) {
		this.miercoles = miercoles;
	}

	public Boolean getJueves() {
		return jueves;
	}

	public void setJueves(Boolean jueves) {
		this.jueves = jueves;
	}

	public Boolean getViernes() {
		return viernes;
	}

	public void setViernes(Boolean viernes) {
		this.viernes = viernes;
	}

	public Boolean getSabado() {
		return sabado;
	}

	public void setSabado(Boolean sabado) {
		this.sabado = sabado;
	}

	public Boolean getDomingo() {
		return domingo;
	}

	public void setDomingo(Boolean domingo) {
		this.domingo = domingo;
	}

	public String getHoraDesdeLunes() {
		return horaDesdeLunes;
	}

	public void setHoraDesdeLunes(String horaDesdeLunes) {
		this.horaDesdeLunes = horaDesdeLunes;
	}

	public String getHoraHastaLunes() {
		return horaHastaLunes;
	}

	public void setHoraHastaLunes(String horaHastaLunes) {
		this.horaHastaLunes = horaHastaLunes;
	}

	public String getHoraDesdeMartes() {
		return horaDesdeMartes;
	}

	public void setHoraDesdeMartes(String horaDesdeMartes) {
		this.horaDesdeMartes = horaDesdeMartes;
	}

	public String getHoraHastaMartes() {
		return horaHastaMartes;
	}

	public void setHoraHastaMartes(String horaHastaMartes) {
		this.horaHastaMartes = horaHastaMartes;
	}

	public String getHoraDesdeMiercoles() {
		return horaDesdeMiercoles;
	}

	public void setHoraDesdeMiercoles(String horaDesdeMiercoles) {
		this.horaDesdeMiercoles = horaDesdeMiercoles;
	}

	public String getHoraHastaMiercoles() {
		return horaHastaMiercoles;
	}

	public void setHoraHastaMiercoles(String horaHastaMiercoles) {
		this.horaHastaMiercoles = horaHastaMiercoles;
	}

	public String getHoraDesdeJueves() {
		return horaDesdeJueves;
	}

	public void setHoraDesdeJueves(String horaDesdeJueves) {
		this.horaDesdeJueves = horaDesdeJueves;
	}

	public String getHoraHastaJueves() {
		return horaHastaJueves;
	}

	public void setHoraHastaJueves(String horaHastaJueves) {
		this.horaHastaJueves = horaHastaJueves;
	}

	public String getHoraDesdeViernes() {
		return horaDesdeViernes;
	}

	public void setHoraDesdeViernes(String horaDesdeViernes) {
		this.horaDesdeViernes = horaDesdeViernes;
	}

	public String getHoraHastaViernes() {
		return horaHastaViernes;
	}

	public void setHoraHastaViernes(String horaHastaViernes) {
		this.horaHastaViernes = horaHastaViernes;
	}

	public String getHoraDesdeSabado() {
		return horaDesdeSabado;
	}

	public void setHoraDesdeSabado(String horaDesdeSabado) {
		this.horaDesdeSabado = horaDesdeSabado;
	}

	public String getHoraHastaSabado() {
		return horaHastaSabado;
	}

	public void setHoraHastaSabado(String horaHastaSabado) {
		this.horaHastaSabado = horaHastaSabado;
	}

	public String getHoraDesdeDomingo() {
		return horaDesdeDomingo;
	}

	public void setHoraDesdeDomingo(String horaDesdeDomingo) {
		this.horaDesdeDomingo = horaDesdeDomingo;
	}

	public String getHoraHastaDomingo() {
		return horaHastaDomingo;
	}

	public void setHoraHastaDomingo(String horaHastaDomingo) {
		this.horaHastaDomingo = horaHastaDomingo;
	}

	public Boolean getCalendarioOmision() {
		return calendarioOmision;
	}

	public void setCalendarioOmision(Boolean calendarioOmision) {
		this.calendarioOmision = calendarioOmision;
	}

	public Boolean getCargadoHorario() {
		return cargadoHorario;
	}

	public void setCargadoHorario(Boolean cargadoHorario) {
		this.cargadoHorario = cargadoHorario;
	}

	
}
