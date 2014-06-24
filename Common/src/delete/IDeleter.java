/**
 * 
 */
package commons.delete;

import java.sql.Connection;
import java.util.Properties;

import commons.arguments.ArgumentsUtil;

/**
 * DB削除処理のインターフェースです。
 * 
 * @author akiba
 *
 */
public interface IDeleter {
	int delete(Connection connection, ArgumentsUtil arguments, Properties configInfo) throws Exception;
}
