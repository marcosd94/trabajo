package py.com.excelsis.sicca.remuneracion.session.form;

import java.util.List;

import javax.ejb.Remote;

@Remote
public interface RemuneracionesDemoServiceRemote {

	public List<String> traerRegistros(Integer anhio, Integer nivel, Integer entidad, Integer tipoPresupuesto, Integer programa);
}
