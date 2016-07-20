package py.com.excelsis.sicca.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import com.arjuna.ats.internal.jdbc.drivers.modifiers.list;

import py.com.excelsis.sicca.entity.CalendarioOeeCab;
import py.com.excelsis.sicca.entity.CalendarioOeeDet;
import py.com.excelsis.sicca.entity.CalendarioSfp;
import py.com.excelsis.sicca.entity.DetReqMin;
import py.com.excelsis.sicca.entity.Feriados;

@Name("diaHabilController")
@Scope(ScopeType.SESSION)
public class DiaHabilController {
	@In(value = "entityManager")
	EntityManager em;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	private Boolean verifCalGeneral = false;
	private static Integer MAX_ITER = 365;

	/**
	 * Metodo que calcula el siguiente dia habil de la entidad. Puede retorna null o una fecha que corresponda a la siguiente fecha habil de la entidad. Empieza a buscar desde la fecha recibida como
	 * parametro formal. Esto significa que el parametro pFecha puede ser un dia habil.
	 * 
	 * @param uoCab
	 *            Para definir que tipo de calendario utilizar
	 * @param pFecha
	 *            La fecha de inicio de la busqueda
	 * @param fechaTope
	 *            Hasta que fecha intentar
	 * @return
	 */
	public Date sgteDiaHabil(Long uoCab, Date pFecha, Date fechaTope) {
		Boolean cFeriado = false;
		Boolean cUOCab = false;
		Boolean cCalGenericoSFP = false;
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		// En cualquier caso solo se podra iterar 365 veces (equvalente a 1 año)
		Integer maxIter = 0;

		while (maxIter.intValue() < MAX_ITER) {
			// Controlar el uo Cab
			cUOCab = controlUOCab(uoCab, pFecha);

			if (verifCalGeneral) {
				// Controlar generico SFP
				cCalGenericoSFP = controlCalGenericoSFP(pFecha);
				if (cCalGenericoSFP) {
					// Controlar feriado
					cFeriado = controlControlFeriado(pFecha);
					if (!cFeriado) {
						return pFecha;
					}
				}
			} else if (cUOCab) {
				// Controlar feriado
				cFeriado = controlControlFeriado(pFecha);
				if (!cFeriado) {
					return pFecha;
				}
			}
			// Generar nueva fecha
			pFecha = generarNuevaFecha(pFecha);
			// Actualizar el valor del criterio parada while
			if (sdf.format(pFecha).equalsIgnoreCase(sdf.format(fechaTope))) {
				return null;
			}
			maxIter++;
		}
		return null;
	}

	/**
	 * Genera una nueva sumandole un dia a la fecha pasada como parametro
	 * 
	 * @param fechaVieja
	 * @return
	 */
	private Date generarNuevaFecha(Date fechaVieja) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(fechaVieja);
		cal.add(Calendar.DAY_OF_MONTH, 1);
		return cal.getTime();
	}

	private Boolean verifTrabajaDia(String elDia, CalendarioOeeCab elCalenCab) {
		if (elDia.equals("2")) {
			if (elCalenCab.getLunHoraDesde() != null) {
				return true;
			}
		}
		if (elDia.equals("3")) {
			if (elCalenCab.getMarHoraDesde() != null) {
				return true;
			}
		}
		if (elDia.equals("4")) {
			if (elCalenCab.getMieHoraDesde() != null) {
				return true;
			}
		}
		if (elDia.equals("5")) {
			if (elCalenCab.getJueHoraDesde() != null) {
				return true;
			}
		}
		if (elDia.equals("6")) {
			if (elCalenCab.getVieHoraDesde() != null) {
				return true;
			}
		}
		if (elDia.equals("7")) {
			if (elCalenCab.getSabHoraDesde() != null) {
				return true;
			}
		}
		return false;
	}

	private Boolean verifTrabajaDia2(String elDia, CalendarioSfp elCalendario) {

		if (elDia.equals("2")) {
			if (elCalendario.getLunHoraDesde() != null) {
				return true;
			}
		}
		if (elDia.equals("3")) {
			if (elCalendario.getMarHoraDesde() != null) {
				return true;
			}
		}
		if (elDia.equals("4")) {
			if (elCalendario.getMieHoraDesde() != null) {
				return true;
			}
		}
		if (elDia.equals("5")) {
			if (elCalendario.getJueHoraDesde() != null) {
				return true;
			}
		}
		if (elDia.equals("6")) {
			if (elCalendario.getVieHoraDesde() != null) {
				return true;
			}
		}
		if (elDia.equals("7")) {
			if (elCalendario.getSabHoraDesde() != null) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Metodo que retorna true si la fecha es valida usando el calendario particular de UO_CAB. False en el caso que no tenga calendario UO_CAB y en caso de tenerlo, retorna false si es una fecha
	 * festiva
	 * 
	 * @param laFecha
	 * @return
	 */

	private Boolean isDiaFestivo(Calendar cal, Long elUoCab) {
		Session session = jpaResourceBean.getSession();
		Criteria crit = session.createCriteria(CalendarioOeeDet.class);
		Criteria critCab = crit.createCriteria("calendarioOeeCab", "cab1");
		Criteria critCabCab = critCab.createCriteria("configuracionUoCab", "cab2");
		crit.add(Restrictions.eq("cab2.idConfiguracionUo", elUoCab));
		crit.add(Restrictions.eq("cab1.anho", cal.get(Calendar.YEAR)));
		crit.add(Restrictions.eq("fechaFestiva", cal.getTime()));
		if (crit.list().size() == 0) {
			return true;
		}
		return false;

	}

	private Boolean controlUOCab(Long elUoCab, Date laFecha) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(laFecha);
		Session session = jpaResourceBean.getSession();
		Query q =
			em.createQuery("select calendarioOeeCab  from CalendarioOeeCab calendarioOeeCab "
				+ " where calendarioOeeCab.configuracionUoCab.idConfiguracionUo = " + elUoCab
				+ " and anho = " + cal.get(Calendar.YEAR));

		List<CalendarioOeeCab> lista = q.getResultList();
		if (lista.size() == 0) {
			// No tiene Configuracion particular. Verificar la configuracion
			// general
			verifCalGeneral = true;
			return false;
		} else {
			CalendarioOeeCab elCab = lista.get(0);
			/*
			 * Tiene configuracion particular.Verificar si no trabaja ese dia y Verificar si es un dia fetivo. Sino trabaja ese dia, retorna false. Si es dia festivo retorna false
			 */
			/* Verificar si trabaja este dia */
			verifCalGeneral = false;
			if (verifTrabajaDia(cal.get(Calendar.DAY_OF_WEEK) + "", elCab)) {
				/* Verificar si es un dia festivo. */
				if (isDiaFestivo(cal, elUoCab)) {
					return false;
				} else {
					return true;
				}
			}
			return false;
		}
	}

	/**
	 * Metodo que retorna true si laFecha es no es festiva, false en caso contrario
	 * 
	 * @param laFecha
	 * @return
	 */
	private Boolean controlCalGenericoSFP(Date laFecha) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(laFecha);
		CalendarioSfp calendario = new CalendarioSfp();
		calendario.setAnho(cal.get(Calendar.YEAR));
		Session session = jpaResourceBean.getSession();
		List<CalendarioSfp> results =
			session.createCriteria(CalendarioSfp.class).add(Example.create(calendario)).list();
		if (results.size() == 1) {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

			return verifTrabajaDia2(cal.get(Calendar.DAY_OF_WEEK) + "", results.get(0));
		}
		return false;
	}

	/**
	 * Metodo que verifica si una fecha es feriodo o no. True si es; false, si no lo es
	 * 
	 * @param laFecha
	 * @return
	 */

	private Boolean controlControlFeriado(Date laFecha) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(laFecha);
		Session session = jpaResourceBean.getSession();
		Feriados feriado = new Feriados();
		feriado.setActivo(true);
		feriado.setAnho(cal.get(Calendar.YEAR));
		feriado.setFecha(laFecha);
		List<CalendarioSfp> results =
			session.createCriteria(Feriados.class).add(Example.create(feriado)).list();
		if (results.size() == 1) {
			return true;
		}
		return false;
	}

	public Boolean isDiaHabil(Date fecha) {
		return false;
	}
}
