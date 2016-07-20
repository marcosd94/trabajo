package py.com.excelsis.sicca.util;

import java.sql.Connection;
import java.sql.SQLException;

import javax.faces.context.FacesContext;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.sql.DataSource;

import org.hibernate.Session;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name("jpaResourceBean")
//@Scope(ScopeType.APPLICATION)
@Scope(ScopeType.EVENT)//MODIFICADO RV
public class JpaResourceBean {

	@In(value="entityManager")
	EntityManager em;
	
	private Session session;

	public void setSession(Session session) {
		this.session = session;
	}

	public Session getSession() {
		if(session == null || !session.isOpen()) {
			session = (Session) em.getDelegate();
		}
		return session;
	}
	
	public static Connection getConnection() {
        String DATASOURCE_CONTEXT = "java:/siccaDatasource";

        Connection result = null;
        try {
            Context initialContext = new InitialContext();
            if (initialContext == null) {
                System.out.println("JNDI problem. Cannot get InitialContext.");
            }
            DataSource datasource = (DataSource) initialContext.lookup(DATASOURCE_CONTEXT);
            if (datasource != null) {
                result = datasource.getConnection();
            } else {
                System.out.println("Failed to lookup datasource.");
            }
        } catch (NamingException ex) {
            System.out.printf("Cannot get connection: " + ex.getMessage(), ex);
        } catch (SQLException ex) {
            System.out.printf("Cannot get connection: " + ex.getMessage(), ex);
        }
        return result;
    }
	
	public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException ex) {
                System.out.printf(ex.getMessage(), ex);
            }
        }
    }
		
	public static JpaResourceBean getInstance() {
        FacesContext fc = FacesContext.getCurrentInstance();
        return (JpaResourceBean) fc.getApplication().getELResolver().getValue(fc.getELContext(), null, "JpaResourceBean");
	}

	public EntityManager getEm() {
		return em;
	}


}
