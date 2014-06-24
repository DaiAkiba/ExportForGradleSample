package commons.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * コンストラクタのパラメータで指定された接続先URLにJDBCドライバによる接続を行います。<br />
 * 
 * @author akiba
 *
 */
public class OracleDatabaseManager {
	
	private Connection connection;
	private static Log logger = LogFactory.getLog(OracleDatabaseManager.class);
	
   /**
    * コンストラクタ。<br />
    * 
    * @param configInfo
    *            設定ファイル情報
    * @throws SQLException
    * 			 D続エラーに失敗した場合
    */
public OracleDatabaseManager(Properties configInfo) throws SQLException {
		logger.debug(configInfo.getProperty("JdbcURL"));
		logger.debug(configInfo.getProperty("account"));
		logger.debug(configInfo.getProperty("password"));
		
		connection = DriverManager.getConnection(configInfo.getProperty("JdbcURL"), configInfo.getProperty("account"), configInfo.getProperty("password"));
	}
	
   /**
    * DB接続オブジェクトを取得します。<br />
    * 
    * @param なし
    * @return Connection DB接続オブジェクト
    */
	public Connection getConnection() {
		return connection;
	}
}
