package py.com.excelsis.sicca.seleccion.session;

import py.com.excelsis.sicca.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("bandejaEntradaHome")
public class BandejaEntradaHome extends EntityHome<BandejaEntrada> {

	public void setBandejaEntradaIdBandejaEntrada(Long id) {
		setId(id);
	}

	public Long getBandejaEntradaIdBandejaEntrada() {
		return (Long) getId();
	}

	@Override
	protected BandejaEntrada createInstance() {
		BandejaEntrada bandejaEntrada = new BandejaEntrada();
		return bandejaEntrada;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
	}

	public boolean isWired() {
		return true;
	}

	public BandejaEntrada getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	


}
