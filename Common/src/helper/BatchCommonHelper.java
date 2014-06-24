/**
 * 
 */
package commons.helper;

import commons.arguments.ArgumentsUtil;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;


/**
 * バッチ処理に関する共通処理を行います。<br />
 * 
 * @author akiba
 *
 */
public class BatchCommonHelper {
   /**
    * ログファイルの出力先を設定します。<br />
    * 
    * @param strLogDirectoryPath
    *            ログファイル出力先パス
    * @param strLogFileName
    *            ログファイル名
    * @return なし
    */
	public static void setLogFilePath(String strLogDirectoryPath, String strLogFileName) {

		if (strLogDirectoryPath == null || strLogDirectoryPath.isEmpty()) {
			strLogDirectoryPath = new String("./");
		}
		StringBuilder strLogFilePath = new StringBuilder(strLogDirectoryPath);
		if (!strLogDirectoryPath.endsWith("/")) {
			strLogFilePath.append("/");
		}
		
		if(strLogFileName != null) {
			strLogFilePath.append(strLogFileName);
		}
		
		//カテゴリ名を取得してLoggerを取得
		Logger logger = Logger.getRootLogger();
		// File Appenderを取得
		FileAppender appender = (FileAppender)logger.getAppender("FILE");
		appender.setFile(strLogFilePath.toString());
	}
	
   /**
    * 起動パラメータOptionオブジェクトを生成します。<br />
    * 
    * @return Option 起動パラメータチェックオブジェクト
    */
	public static Option createOption(String longOption, boolean isRequired) {
	    OptionBuilder.withLongOpt(longOption);
	    OptionBuilder.hasArg();
	    if (isRequired) {
	    	OptionBuilder.isRequired();
	    }
	    return OptionBuilder.create();
	}
}
