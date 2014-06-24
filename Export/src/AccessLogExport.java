/**
 * 
 */
package export;

import java.sql.SQLException;

import commons.BatchExitStatus;
import commons.arguments.ArgumentsUtil;
import commons.config.ConfigurationController;
import commons.database.OracleDatabaseManager;
import commons.export.ExporterFactory;
import commons.export.IExporter;
import commons.helper.BatchCommonHelper;
import commons.message.Messages;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * アクセスログエクスポートバッチ処理のメイン処理です。<br>
 * SctAccessLog、Revisions、DocMetaテーブルを結合してエクスポートします。<br>
 * 
 * @author akiba
 *
 */
public class AccessLogExport {
	private static Log logger = LogFactory.getLog(AccessLogExport.class);
	private static final String START_MESSAGE = "アクセスログエクスポート処理を開始しました";
	private static final String END_MESSAGE = "アクセスログエクスポート処理を終了しました";
	
   /**
    * アクセスログエクスポートメイン処理を行います。<br />
    * -start、-endの起動パラメータは必須です。<br />
    * 
    * @param args
    *            起動パラメータ
    * @return なし
    */
	public static void main(String[] args) throws Exception {
		
		ConfigurationController config = new ConfigurationController("data/AccessLogExport.properties");
		BatchCommonHelper.setLogFilePath(config.getProperty("logFileDirectoryPath"), "AccessLogExport.log");
		Options options = createOptions();
		BatchExitStatus rc = BatchExitStatus.SUCCESS;
		
		logger.info(START_MESSAGE);
		
//		Console console = System.console();		
//		char[] password = console.readPassword("[%s]","DataBase接続パスワードを入力してください");
//		System.out.println(password);
		
		try {
			ArgumentsUtil arguments = new ArgumentsUtil(options, args);
			//String[] ss = {"-start", "20140401", "-end", "20140630"};
			//ArgumentsUtil arguments = new ArgumentsUtil(options, ss);
	
			OracleDatabaseManager database = new OracleDatabaseManager(config.getProperites());
			ExporterFactory exporterFactory = new AccessLogExporterFactory();
			
			IExporter exporter = exporterFactory.createExporter();
			exporter.export(database.getConnection(), arguments, config.getProperites());
			database.getConnection().close();
		}
		catch (SQLException e) {
			logger.error(Messages.DATABASE_CONNECTION_ERROR, e);
			rc = BatchExitStatus.FAILURE;
		}
		catch (IllegalArgumentException e) {
			logger.error(Messages.ARGUMENT_ERROR, e);
			rc = BatchExitStatus.FAILURE;
		}
		catch (Exception e) {
			logger.error(Messages.EXPORT_FILE_ERROR, e);
			rc = BatchExitStatus.FAILURE;
		}

		logger.info(END_MESSAGE);
		System.exit(rc.getReturnCode());
	}
	
   /**
    * 起動パラメータチェック用のOptionsオブジェクト生成します。<br />
    * private API<br />
    * 
    * @return Options 起動パラメータチェック用オブジェクト
    */
	private static Options createOptions() {
		Options options = new Options();
		 
	    options.addOption(BatchCommonHelper.createOption(ArgumentsUtil.START_OPTION, true));
	    options.addOption(BatchCommonHelper.createOption(ArgumentsUtil.END_OPTION, true));
	    options.addOption(BatchCommonHelper.createOption(ArgumentsUtil.ORDER_OPTION, false));
	    options.addOption(BatchCommonHelper.createOption(ArgumentsUtil.ENCODING_OPTION, false));
		
		return options;
	}
	

}
