package py.com.excelsis.sicca.session.form;

import java.io.Serializable;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import py.com.excelsis.sicca.session.util.ReportUtilFormController;

@Scope(ScopeType.CONVERSATION)
@Name("imprimirDatosGrupoFC")
public class ImprimirDatosGrupoFC implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6593866729970919086L;

	public void imprimir(Long idConcursoPuestoAgr) {
		ReportUtilFormController reportUtilFormController = (ReportUtilFormController) Component.getInstance(ReportUtilFormController.class, true);
		reportUtilFormController.init();
		reportUtilFormController.setNombreReporte("RPT_CU466_imprimir_datos_grupo");
		reportUtilFormController.getParametros().put("idConcursoPuestoAgr", idConcursoPuestoAgr);
		reportUtilFormController.imprimirReportePdf();
	}

}
