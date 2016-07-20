package py.com.excelsis.sicca.util;

import java.io.Serializable;
import java.util.Date;

import org.jboss.seam.Component;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.async.QuartzTriggerHandle;
import org.jboss.seam.annotations.Scope;

import static org.jboss.seam.ScopeType.APPLICATION;

import org.jboss.seam.annotations.Startup;





import org.jboss.seam.log.Log;

import py.com.excelsis.sicca.capacitacion.session.form.ProcesoCapacitacionFC525;
import py.com.excelsis.sicca.desvinculacion.session.form.AdmFenecimientoContratosFormController;
import py.com.excelsis.sicca.desvinculacion.session.form.AdmVencimientoInhabilitadosFC;
import py.com.excelsis.sicca.evaluacion.session.form.ProcesoEvaluacionDesempenhoFC566;
import py.com.excelsis.sicca.remuneracion.session.form.ProcesoAnexoServiceClient754FC;
import py.com.excelsis.sicca.remuneracion.session.form.ProcesoClienteWSSinarah721FC;
import py.com.excelsis.sicca.remuneracion.session.form.ProcesoUpdateCat722FC;
import py.com.excelsis.sicca.remuneracion.session.form.UploadRemuneracionesFC;
import py.com.excelsis.sicca.seleccion.session.form.AdmFecRecPosFC423;
import py.com.excelsis.sicca.seleccion.session.form.CancelacionAutomaticaFC;
import py.com.excelsis.sicca.session.form.UsuarioCambioCodigoFC;
import py.com.excelsis.sicca.session.form.UsuarioPortalFormController;

@Name("controller")
@Scope(APPLICATION)
@AutoCreate
@Startup
public class ScheduleController implements Serializable {

	private static final long serialVersionUID = 7609983147081676186L;
	@Logger
	Log log;
	@In(required = false)
	AdmFecRecPosFC423 admFecRecPosProcessor;
	@In(required = false)
	CancelacionAutomaticaFC cancelacionAutomaticaFC;
	@In(required = false)
	AdmFenecimientoContratosFormController admFenecimientoContratosFormController;
	@In(required = false)
	AdmVencimientoInhabilitadosFC admVencimientoInhabilitadosFC;
	@In(required = false)
	ProcesoCapacitacionFC525 procesoCapacitacionFC525;
	@In(required = false)
	ProcesoEvaluacionDesempenhoFC566 procesoEvaluacionDesempenhoFC566;
	@In(required = false)
	ProcesoClienteWSSinarah721FC procesoClienteWSSinarah721FC;
	@In(required = false)
	ProcesoUpdateCat722FC procesoUpdateCat722FC;
	@In(required = false)
	ProcesoAnexoServiceClient754FC procesoAnexoServiceClient754FC;
	@In(required = false)
	UploadRemuneracionesFC uploadRemuneracionesFC;
	
	//**************************************************************************
	@In(required = false)
	UsuarioCambioCodigoFC usuarioCambioCodigoFC; // Agregado; Werner.
		//**************************************************************************
	
	private QuartzTriggerHandle quartzTestTriggerHandle;
	// Cada 5 minutos...
	//private static String CRON_INTERVAL_ADM_FEC_REC_POS_PROCESSOR = "0 */1 * * * ?";
	// Cada 15 minutos... 
	private static String CRON_INTERVAL_ADM_FEC_REC_POS_PROCESSOR = "0 0/15 * 1/1 * ? *";// MODIFICADO 
	
	// Cada una hora ...
	private static String CRON_INTERVAL_PROCESSOR_721 = "0 */59 * * * ?";
	// Cada una hora ...
	private static String CRON_INTERVAL_PROCESSOR_754 = "0 */59 * * * ?";
	// Cada una hora ...
	private static String CRON_INTERVAL_PROCESSOR_722 = "0 */59 * * * ?";

	// Todos los dias a las 01:00 AM...
	// private static String CRON_INTERVAL_ADM_FEC_REC_POS_PROCESSOR = "0 0 1 * * ?";

	// Todos los dias a las 01:00 AM...
	private static String CRON_INTERVAL_PROCESAR_CAPACITACIONES_PROCESSOR = "0 */2 * * * ?";
	// private static String CRON_INTERVAL_PROCESAR_CAPACITACIONES_PROCESSOR = "0 0 5 * * ?";
	// Todos los dias a las 03:00 AM...
	private static String CRON_INTERVAL_ADM_FENECIMIENTO_CONTRATOS_PROCESSOR = "0 0 3 * * ?";

	// Todos los dias a las 04:00 AM...
	private static String CRON_INTERVAL_CANCELACION_AUTOMATICA = "0 0 4 * * ?";

	// Todos los dias a las 02:00 AM...
	private static String CRON_INTERVAL_VENCIMIENTO_INHABILITADOS = "0 0 2 * * ?"; // Proceso liviano, no deberia durar mucho.
	// Todos los viernes a las 02:00 AM... "0 0 0 * 5 ?" a la media noche
	private static String CRON_INTERVAL_EVAL_DESEMPENHO = "0 0 0 * 5 ?";
	
	//**************************************************************************
	// Agregado; Werner, ejemplos: http://quartz-scheduler.org/documentation/quartz-2.1.x/examples/Example3 
	// http://www.quartz-scheduler.org/documentation/quartz-1.x/tutorials/crontrigger
	//private static String CRON_INTERVAL_CAMBIO_COD_USUARIO = "0 0/3 * 6,7 10 ? 2014";
	//**************************************************************************
	
	private static String CRON_INTERVAL_UPDATE_TABLAS_REMUNERACIONES_TMP = "0 2 13 8 2 ?"; //EJECUTA EN UNA FECHA Y HORA EN PARTICULAR. FORMATO: 0 [minutos] [hora] [dia] [mes] ?
	private static String CRON_INTERVAL_UPDATE_TABLAS_REMUNERACIONES_TMP_21 = "0 0/5 19,20,21,22 * * ?"; //EJECUTA CADA CINCO MINUTOS, ENTRE LAS 14 y las 5
	private static String CRON_INTERVAL_UPDATE_TABLAS_REMUNERACIONES_TMP_22 = "0 0/5 23,0,1,2 * * ?"; //EJECUTA CADA CINCO MINUTOS, ENTRE LAS 14 y las 5
	private static String CRON_INTERVAL_UPDATE_TABLAS_REMUNERACIONES_TMP_3 = "0 0 19 ? * *"; //EJECUTA TODOS LOS DIAS A LAS 9 PM
	
	private static String CRON_INTERVAL_UPDATE_CONTROL_REMUNERACIONES_TMP = "0 35 14 18 9 ?"; //EJECUTA EN UNA FECHA Y HORA EN PARTICULAR. FORMATO: 0 [minutos] [hora] [dia] [mes] ?
	private static String CRON_INTERVAL_UPDATE_CONTROL_REMUNERACIONES_TMP_2 = "0 0 5 ? * *"; //EJECUTA TODOS LOS DIAS A LAS 5 AM
	private static String CRON_INTERVAL_UPDATE_CONTROL_REMUNERACIONES_TMP_3 = "0 0 4 ? * *"; //EJECUTA TODOS LOS DIAS A LAS 4 AM
	//private static String CRON_INTERVAL_INHABILITADOS_REMUNERACIONES = "0 0 1 ? * *"; //EJECUTA TODOS LOS DIAS A LAS 1 AM

	
	@Create
	public void scheduleTimer() {
		// proceso 754
		if (procesoAnexoServiceClient754FC == null) {
			procesoAnexoServiceClient754FC =
				(ProcesoAnexoServiceClient754FC) Component.getInstance(ProcesoAnexoServiceClient754FC.class, true);
		}
		quartzTestTriggerHandle = procesoAnexoServiceClient754FC.procesoPrincipal(new Date(), CRON_INTERVAL_PROCESSOR_754);

		// proceso 722
		if (procesoUpdateCat722FC == null) {
			procesoUpdateCat722FC =
				(ProcesoUpdateCat722FC) Component.getInstance(ProcesoUpdateCat722FC.class, true);
		}
		quartzTestTriggerHandle =
			procesoUpdateCat722FC.procesoPrincipal(new Date(), CRON_INTERVAL_PROCESSOR_722);

		// proceso 721
		if (procesoClienteWSSinarah721FC == null) {
			procesoClienteWSSinarah721FC =
				(ProcesoClienteWSSinarah721FC) Component.getInstance(ProcesoClienteWSSinarah721FC.class, true);
		}
		quartzTestTriggerHandle =
			procesoClienteWSSinarah721FC.procesoPrincipal(new Date(), CRON_INTERVAL_PROCESSOR_721);
		// Procesar capacitaciones
		if (procesoCapacitacionFC525 == null) {
			procesoCapacitacionFC525 =
				(ProcesoCapacitacionFC525) Component.getInstance(ProcesoCapacitacionFC525.class, true);
		}
		quartzTestTriggerHandle =
			procesoCapacitacionFC525.procesoPrincipal(new Date(), CRON_INTERVAL_PROCESAR_CAPACITACIONES_PROCESSOR);

		// cu 423
		if (admFecRecPosProcessor == null) {
			admFecRecPosProcessor =
				(AdmFecRecPosFC423) Component.getInstance(AdmFecRecPosFC423.class, true);
		}
		quartzTestTriggerHandle =
			admFecRecPosProcessor.procesoPrincipal(new Date(), CRON_INTERVAL_ADM_FEC_REC_POS_PROCESSOR);

		// Cancelacion Automatica
		if (cancelacionAutomaticaFC == null) {
			cancelacionAutomaticaFC =
				(CancelacionAutomaticaFC) Component.getInstance(CancelacionAutomaticaFC.class, true);
		}
		quartzTestTriggerHandle =
			cancelacionAutomaticaFC.procesoPrincipal(new Date(), CRON_INTERVAL_CANCELACION_AUTOMATICA);

		// Administrar Fenecimiento Contratos
		if (admFenecimientoContratosFormController == null) {
			admFenecimientoContratosFormController =
				(AdmFenecimientoContratosFormController) Component.getInstance(AdmFenecimientoContratosFormController.class, true);
		}
		quartzTestTriggerHandle =
			admFenecimientoContratosFormController.procesoPrincipalFenecimientoContrato(new Date(), CRON_INTERVAL_ADM_FENECIMIENTO_CONTRATOS_PROCESSOR);

		// Administrar Vencimiento de Inhabilitados
		if (admVencimientoInhabilitadosFC == null) {
			admVencimientoInhabilitadosFC =
				(AdmVencimientoInhabilitadosFC) Component.getInstance(AdmVencimientoInhabilitadosFC.class, true);
		}
		quartzTestTriggerHandle =
			admVencimientoInhabilitadosFC.procesoPrincipal(new Date(), CRON_INTERVAL_VENCIMIENTO_INHABILITADOS);

		// Proceso de Evaluacion de Desempeño
		if (procesoEvaluacionDesempenhoFC566 == null) {
			procesoEvaluacionDesempenhoFC566 =
				(ProcesoEvaluacionDesempenhoFC566) Component.getInstance(ProcesoEvaluacionDesempenhoFC566.class, true);
		}
		quartzTestTriggerHandle =
			procesoEvaluacionDesempenhoFC566.procesoPrincipal(new Date(), CRON_INTERVAL_EVAL_DESEMPENHO);
		
		//Agregado; cambio, código usuario; Werner.
		//*********************************************************************************************************
//		if(usuarioCambioCodigoFC == null){
//			usuarioCambioCodigoFC =
//					(UsuarioCambioCodigoFC) Component.getInstance(UsuarioCambioCodigoFC.class, true);
//			}
//		quartzTestTriggerHandle =
//				usuarioCambioCodigoFC.procesoCambioCodigo(new Date(), CRON_INTERVAL_CAMBIO_COD_USUARIO);
		//*********************************************************************************************************
		
		//Agregado Sergio.
		//*********************************************************************************************************
		if(uploadRemuneracionesFC == null){
			uploadRemuneracionesFC =
					(UploadRemuneracionesFC) Component.getInstance(UploadRemuneracionesFC.class, true);
			}
		//ACTUALIZACION REMUNERACIONES_TMP
		//quartzTestTriggerHandle = uploadRemuneracionesFC.updateRemuneracionesTmp(new Date(), CRON_INTERVAL_UPDATE_TABLAS_REMUNERACIONES_TMP_21);
		
		//ACTUALIZACION HISTORICO_REMUNERACIONES_TMP
		//quartzTestTriggerHandle = uploadRemuneracionesFC.updateHistoricoRemuneracionesTmp(new Date(), CRON_INTERVAL_UPDATE_TABLAS_REMUNERACIONES_TMP_22);
		
		//ACTUALIZACION CONTROL_REMUNERACIONES_TMP
		quartzTestTriggerHandle = uploadRemuneracionesFC.updateControlRemuneracionesTmp(new Date(), CRON_INTERVAL_UPDATE_CONTROL_REMUNERACIONES_TMP_2);
		
		//BORRADO DE PERSONAS DUPLICADAS
		//quartzTestTriggerHandle = uploadRemuneracionesFC.seteoPersonasRepetidasABorrar(new Date(), CRON_INTERVAL_UPDATE_CONTROL_REMUNERACIONES_TMP_3);
		//quartzTestTriggerHandle = uploadRemuneracionesFC.borrarPersonasRepetidas(new Date(), CRON_INTERVAL_UPDATE_CONTROL_REMUNERACIONES_TMP_2);
		
		//MOVIDA DE REGISTROS DE REMUNERACIONES INHABILITADOS A AUDIT_INAHABILITADOS
		//ZD 24/02/16 -- LA MOVIDA SE REALIZA AL MOMENTO DE LA INSERCIÓN
//		quartzTestTriggerHandle = uploadRemuneracionesFC.moverInhabilitados(new Date(), CRON_INTERVAL_INHABILITADOS_REMUNERACIONES);
		//*********************************************************************************************************
			
	}
}