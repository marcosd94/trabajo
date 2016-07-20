package py.com.excelsis.sicca.juridicos.session;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.SumarioCab;
import py.com.excelsis.sicca.entity.SumarioDet;
import py.com.excelsis.sicca.session.DatosEspecificosHome;

@Name("sumarioDetHome")
public class SumarioDetHome extends EntityHome<SumarioDet> {

	@In(create = true)
	SumarioCabHome sumarioCabHome;
	@In(create = true)
	DatosEspecificosHome datosEspecificosHome;

	public void setSumarioDetIdSumarioDet(Long id) {
		setId(id);
	}

	public Long getSumarioDetIdSumarioDet() {
		return (Long) getId();
	}

	@Override
	protected SumarioDet createInstance() {
		SumarioDet sumarioDet = new SumarioDet();
		return sumarioDet;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		SumarioCab sumarioCab = sumarioCabHome.getDefinedInstance();
		if (sumarioCab != null) {
			getInstance().setSumarioCab(sumarioCab);
		}
		DatosEspecificos datosEspecificos = datosEspecificosHome
				.getDefinedInstance();
		if (datosEspecificos != null) {
			getInstance().setDatosEspecificos(datosEspecificos);
		}
	}

	public boolean isWired() {
		if (getInstance().getSumarioCab() == null)
			return false;
		if (getInstance().getDatosEspecificos() == null)
			return false;
		return true;
	}

	public SumarioDet getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

}
