package py.com.excelsis.sicca.movilidadLaboral.session;

import py.com.excelsis.sicca.entity.*;
import java.math.BigDecimal;
import java.util.Date;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("bandejaTrasladoHome")
public class BandejaTrasladoHome extends EntityHome<BandejaTraslado> {

	public void setBandejaTraslado(BandejaTraslado id) {
		setId(id);
	}

	public BandejaTraslado getBandejaTraslado() {
		return (BandejaTraslado) getId();
	}

	public BandejaTrasladoHome() {
		setBandejaTraslado(new BandejaTraslado());
	}

	@Override
	public boolean isIdDefined() {
		
		return true;
	}

	@Override
	protected BandejaTraslado createInstance() {
		BandejaTraslado bandejaTraslado = new BandejaTraslado();
	//	bandejaTraslado.setI(new BandejaTraslado());
		return bandejaTraslado;
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

	public BandejaTraslado getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

}
