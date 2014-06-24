/**
 * 
 */
package commons.export;

import java.sql.Connection;
import java.util.Properties;

import commons.arguments.ArgumentsUtil;

/**
 * エクスポートのインターフェースです。
 * 
 * @author akiba
 *
 */
public interface IExporter {
	void export(Connection connection, ArgumentsUtil arguments, Properties configInfo) throws Exception;
}
