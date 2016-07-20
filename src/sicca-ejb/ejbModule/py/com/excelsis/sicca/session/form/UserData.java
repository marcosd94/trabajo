package py.com.excelsis.sicca.session.form;



import java.io.Serializable;
import javax.faces.model.SelectItem;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Scope(ScopeType.CONVERSATION)
@Name("userData")

public class UserData implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public String data;

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
}