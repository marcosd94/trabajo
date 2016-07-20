package py.com.excelsis.sicca.remuneracion.session.form;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.faces.context.FacesContext;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.servlet.ServletContext;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import org.apache.commons.io.FileUtils;
import org.jboss.seam.contexts.ApplicationContext;
import org.jboss.seam.contexts.Contexts;
import org.jboss.wsf.spi.annotation.WebContext;

@WebService(name = "RemuneracionDemoService", serviceName = "RemuneracionDemoService")
@WebContext(contextRoot = "servicioSICCA")
@Stateless(name="RemuneracionesDemoService")
public class RemuneracionesDemoService implements RemuneracionesDemoServiceRemote {
	@Resource
	private WebServiceContext context;

	@WebMethod
	public List<String> traerRegistros(Integer anhio, Integer nivel, Integer entidad, Integer tipoPresupuesto, Integer programa) {

		List<String> lista = new ArrayList<String>();
		List<String> lRespuesta = new ArrayList<String>();
		try {

			ServletContext servletContext =
			    (ServletContext) context.getMessageContext().get(MessageContext.SERVLET_CONTEXT);
			File file =
				new File(servletContext.getRealPath("/META-INF/demoWSsinarh/demoDataWsSinarh.csv"));
			lista = FileUtils.readLines(file);

			for (String o : lista) {
				DTO680 dto = DTO680.descomponerLinea2(o);
				if (dto != null) {
					if (dto.getAnhio().intValue() == anhio.intValue()
						&& dto.getNivelEnti().intValue() == nivel.intValue()
						&& dto.getEntidad().intValue() == entidad.intValue()
						&& dto.getPresup().intValue() == tipoPresupuesto.intValue()
						&& dto.getPrograma().intValue() == programa.intValue()) {
						lRespuesta.add(o);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return lRespuesta;
	}
}
