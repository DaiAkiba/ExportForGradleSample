/**
 * 
 */
package export;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import commons.arguments.ArgumentsUtil;
import commons.custom.opencsv.CSVWriter;
import commons.export.IExporter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * アクセスログエクスポート処理を行います。<br>
 * SctAccessLog、Revisions、DocMetaテーブルを結合して全ての項目をCSV形式で出力します。<br>
 * 
 * @author akiba
 *
 */
public class AccessLogCSVExporter implements IExporter {
	private static Log logger = LogFactory.getLog(AccessLogCSVExporter.class);
	
   /**
    * アクセスログエクスポートを行います。<br />
    * 呼び出し元でパラメータチェックされていることを前提としています。<br />
    * argumentsにstartとendが設定されていること。<br>
    * configInfoにexportDirectoryPathとexportFileNameが設定されていること。<br>
    * 
    * @param connection
    *            DB接続オブジェクト
    * @param arguments
    *            バッチ起動パラメータ
    * @param configInfo
    *            設定ファイル情報
    * @throws IllegalArgumentException
    * 			 バッチ起動パラメータもしくは設定ファイル情報が不正の場合
    * @throws SQLException
    * 			 DB接続オブジェクトが不正、もしくはDBエラーの場合 
    * @throws IOException
    * 			 エクスポートファイル出力エラーの場合
    */
	public void export(Connection connection, ArgumentsUtil arguments, Properties configInfo) throws Exception {
		try {
			validateParameter(connection, arguments, configInfo);
			
			StringBuilder baseQuery = new StringBuilder();
			baseQuery.append("SELECT * FROM SctAccessLog acl");
			baseQuery.append(" LEFT JOIN Revisions rev on (acl.sc_scs_dID = rev.dID)");
			baseQuery.append(" LEFT JOIN DocMeta doc on (acl.sc_scs_dID = doc.dID)");
			baseQuery.append(" WHERE acl.eventDate >= ? AND acl.eventDate < ? + 1");
			baseQuery.append(" ORDER BY acl.eventDate %s");
			
			String query = String.format(baseQuery.toString(), getOrderByArgument(arguments.getOrder()));
			
			PreparedStatement statement = connection.prepareStatement(query);
			statement.setDate(1, arguments.getStartByDate());
			statement.setDate(2, arguments.getEndByDate());
			try {
				ResultSet resultSet = statement.executeQuery();
				CSVWriter csvWriter = setupCsvWriter(configInfo, getEncodingByArgument(arguments.getEncoding()));
				csvWriter.writeAll(resultSet, true);
				resultSet.close();
				csvWriter.close();
			}
			catch (Exception e) {
				statement.close();
				logger.debug(e + ":" + e.getMessage());
				throw e;
			}
		}
		catch (Exception e) {
			logger.debug(e + ":" + e.getMessage());
			throw e;
		}
	}
	
   /**
    * パラメータチェックを行います。<br />
    * private API<br />
    * 
    * @param connection
    *            DB接続オブジェクト
    * @param arguments
    *            バッチ起動パラメータ
    * @param configInfo
    *            設定ファイル情報
    * @throws SQLException
    * 			 DB接続オブジェクトが不正の場合
    * @throws IllegarArgumentException
    * 			 バッチ起動パラメータもしくは設定ファイル情報が不正の場合 
    */
	private void validateParameter(Connection connection, ArgumentsUtil arguments, Properties configInfo) throws Exception {
		if (connection == null) {
			throw new SQLException();
		}
		else if (arguments == null || configInfo == null) {
			throw new IllegalArgumentException();
		}
		else if (arguments.getStartByDate() == null || arguments.getEndByDate() == null) {
			throw new IllegalArgumentException();
		}
	}
	
   /**
    * CSV出力クラス(CSVWriter)のセットアップを行います。<br />
    * private API<br />
    * 
    * @param configInfo
    *            設定ファイル情報
    * @param strEncode
    *            エンコード指定文字列（MS932 or UTF-8）
    * @return CSVWriter CSV出力オブジェクト
    * @throws IOException
    * 			 ファイル入出力エラーの場合
    */
	private CSVWriter setupCsvWriter(Properties configInfo, String strEncode) throws Exception {
		String strExportDirectoryPath = configInfo.getProperty("exportDirectoryPath");
		String strExportFileName = configInfo.getProperty("exportFileName");
		File exportFilePath = null;
		
		if (strExportDirectoryPath == null || strExportFileName == null) {
			throw new IOException();
		}
		
		if (strExportDirectoryPath.endsWith("/")) {
			exportFilePath = new File(strExportDirectoryPath + strExportFileName);
		}
		else {
			exportFilePath = new File(strExportDirectoryPath + "/" + strExportFileName);
		}
		
		Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(exportFilePath), strEncode));
		
		return new CSVWriter(writer,CSVWriter.DEFAULT_SEPARATOR ,CSVWriter.DEFAULT_QUOTE_CHARACTER,"\r\n");
	}
	
   /**
    * CSV出力する際のエンコード形式を取得します。<br />
    * パラメータが未指定の場合は"MS932"を返却します。<br />
    * private API<br />
    * 
    * @param encodingArgument
    *            起動パラメータで指定されたエンコード形式
    * @return String CSV出力する際のエンコード形式
    */
	private String getEncodingByArgument(String encodingArgument) {
		String encoding = "MS932";
		if (encodingArgument != null) {
			encoding = encodingArgument;
		}
		return encoding;
	}
	
   /**
    * CSV出力する際のソート順を取得します。<br />
    * パラメータが未指定の場合は"ASC"を返却します。<br />
    * private API<br />
    * 
    * @param orderArgument
    *            起動パラメータで指定されたソート順
    * @return String CSV出力する際のソート順
    */
	private String getOrderByArgument(String orderArgument) {
		String order = "ASC";
		if (orderArgument != null) {
			order = orderArgument;
		}
		return order;
	}
}
