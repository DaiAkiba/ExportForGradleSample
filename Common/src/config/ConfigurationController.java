package commons.config;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * コンストラクタのパラメータで指定されたpropertiesファイルから設定情報を読み出します。<br />
 * 
 * @author akiba
 *
 */
public class ConfigurationController {
	
	private Properties properties;
	
   /**
    * コンストラクタ。<br />
    * 
    * @param configFilePath
    *            設定ファイルのファイルパス
    * @throws FileNotFoundException
    * 			 設定ファイルが存在しない場合
    * @throws IOException
    * 			 入出力エラーが発生した場合
    */
	public ConfigurationController (String configFilePath) throws FileNotFoundException, IOException {
		properties = new Properties();
		FileInputStream configFile = new FileInputStream(configFilePath);
		properties.load(configFile);
	}
	
   /**
    * 設定ファイルの設定情報を全て取得します。<br />
    * 
    * @param なし
    * @return Properties 設定情報
    */
	public Properties getProperites() {
		return properties;
	}
	
   /**
    * 指定されたキーに一致する設定ファイルの設定情報をString型で取得します。<br />
    * 
    * @param Key
    * 			取得対象のキー
    * @return String 設定情報
    */
	public String getProperty(String key) {
		return properties.getProperty(key);
	}
}
