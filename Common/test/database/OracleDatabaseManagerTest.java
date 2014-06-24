/**
 * 
 */
package commons.database;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

/**
 * @author akiba
 *
 */
public class OracleDatabaseManagerTest {
	private Properties connectionInfo = new Properties();
	private OracleDatabaseManager databaseManager;
	
	@Before
	public void 接続情報クリア() {
		if (!connectionInfo.isEmpty()) {
			connectionInfo.clear();
		}
	}
	
	private void setupValidConnectionInfo() {
		setValidAddressInfo();
		setValidAuthInfo();
	}
	
	private void setValidAddressInfo() {
		connectionInfo.setProperty("JdbcURL", "jdbc:oracle:thin:@192.168.56.199:1521/mzd");
	}
	
	private void setValidAuthInfo() {
		connectionInfo.setProperty("account", "MZD_OCS");
		connectionInfo.setProperty("password", "welcome1");
	}
	
	private void setupInvalidAddressConnectionInfo() {
		connectionInfo.setProperty("IPAddress", "192.168.56.10");
	}
	
	private void setupInvalidAuthInfo() {
		setValidAddressInfo();
		setInvalidPasswordAuthInfo();
	}
	
	private void setInvalidPasswordAuthInfo() {
		connectionInfo.setProperty("account", "MZD_OCS");
		connectionInfo.setProperty("password", "password");
	}
	
	@Test
	public void 正しく接続してConnectionオブジェクトが取得できる() {
		setupValidConnectionInfo();
		try {
			databaseManager = new OracleDatabaseManager(connectionInfo);
		}
		catch (Exception e) {
			fail(e.toString());
		}
		Boolean expected = true;
		Connection result = databaseManager.getConnection();
		assertThat(result.toString().contains("Connection"), is(expected));
	}
	
	@Test(expected = SQLException.class)
	public void 接続先が不正の場合SQLExceptionが発生する() throws Exception {
		setupInvalidAddressConnectionInfo();
		databaseManager = new OracleDatabaseManager(connectionInfo);
	}
	
	@Test(expected = SQLException.class)
	public void DB認証に失敗した場合SQLExceptionが発生する() throws Exception {
		setupInvalidAuthInfo();
		databaseManager = new OracleDatabaseManager(connectionInfo);
	}

}
