package py.com.excelsis.sicca.remuneracion.session.form;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.servlet.ServletContext;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import org.apache.commons.io.FileUtils;
import org.jboss.wsf.spi.annotation.WebContext;

@WebService(name = "anexoService", serviceName = "anexoService")
@WebContext(contextRoot = "servicioSICCA")
@Stateless(name = "anexoService")
public class AnexoService implements AnexoServiceRemote {
	@Resource
	private WebServiceContext context;
	@WebMethod
	public List<String> getRegistros() {
		List<String> lista = new ArrayList<String>();		 
		try {
			ServletContext servletContext =
				(ServletContext) context.getMessageContext().get(MessageContext.SERVLET_CONTEXT);
			File file =
				new File(servletContext.getRealPath("/META-INF/demoWSsinarh/A_ANX_2012.csv"));
			lista = FileUtils.readLines(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return lista;
	}
}
