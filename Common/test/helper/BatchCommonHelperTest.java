package commons.helper;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.apache.commons.cli.Option;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	BatchCommonHelperTest.setLogFilePathのテスト.class,
	BatchCommonHelperTest.createOptionのテスト.class,
	})
public class BatchCommonHelperTest {
	public static class setLogFilePathのテスト {
		private String LOG_FILE_NAME = "test.log";
	
		@Test
		public void ログ出力先ディレクトリがnullの場合ログ出力先にカレントディレクトリが指定される() {
			BatchCommonHelper.setLogFilePath(null, LOG_FILE_NAME);
			
			Logger logger = Logger.getRootLogger();
			FileAppender appender = (FileAppender)logger.getAppender("FILE");
			String expectedString = "./" + LOG_FILE_NAME;
			String resultString = appender.getFile();
			assertThat(resultString, is(expectedString));
		}
		
		@Test
		public void ログ出力先ディレクトリが空の場合ログ出力先にカレントディレクトリが指定される() {
			String strLogFilePath = "";
			BatchCommonHelper.setLogFilePath(strLogFilePath, LOG_FILE_NAME);
			
			Logger logger = Logger.getRootLogger();
			FileAppender appender = (FileAppender)logger.getAppender("FILE");
			String expectedString = "./" + LOG_FILE_NAME;
			String resultString = appender.getFile();
			assertThat(resultString, is(expectedString));
		}
		
		@Test
		public void ログファイル名がnullの場合ログ出力先にカレントディレクトリのみが設定される() {
			BatchCommonHelper.setLogFilePath(".", null);
			
			Logger logger = Logger.getRootLogger();
			FileAppender appender = (FileAppender)logger.getAppender("FILE");
			String expectedString = "./";
			String resultString = appender.getFile();
			assertThat(resultString, is(expectedString));
		}
		
		@Test
		public void ログファイル名が空の場合ログ出力先にカレントディレクトリのみが設定される() {
			String strLogFileName = "";
			BatchCommonHelper.setLogFilePath("./", strLogFileName);
			
			Logger logger = Logger.getRootLogger();
			FileAppender appender = (FileAppender)logger.getAppender("FILE");
			String expectedString = "./";
			String resultString = appender.getFile();
			assertThat(resultString, is(expectedString));
		}
		
		@Test
		public void ログファイル出力先ファイル名を設定できる() {
			BatchCommonHelper.setLogFilePath(".", LOG_FILE_NAME);
	
			Logger logger = Logger.getRootLogger();
			FileAppender appender = (FileAppender)logger.getAppender("FILE");
			String expectedString = "./" + LOG_FILE_NAME;
			String resultString = appender.getFile();
			assertThat(resultString, is(expectedString));
		}
	}
	
	@RunWith(Theories.class)
	public static class createOptionのテスト {
		private String SAMPLE_OPTION = "sample";
		
		@DataPoints
		public static boolean requiredFlags[] = {true, false};
		
		@Theory
		public void Requiredに関わらずOptionオブジェクトが正常に作成できる(boolean requiredFlag) {
			Option option = BatchCommonHelper.createOption(SAMPLE_OPTION, requiredFlag);
			
			String expectedString = SAMPLE_OPTION;
			String resultString = option.getLongOpt();
			assertThat(resultString, is(expectedString));
			
			boolean expected = true;
			boolean result = option.hasArg();
			assertThat(result, is(expected));
			
			expected = requiredFlag;
			result = option.isRequired();
			assertThat(result, is(expected));
		}
	}
}
