/**
 * 
 */
package export;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Date;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;

import commons.arguments.ArgumentsUtil;
import commons.config.ConfigurationController;
import commons.custom.opencsv.CSVReader;
import commons.database.OracleDatabaseManager;
import commons.helper.BatchCommonHelper;

import org.apache.commons.cli.Options;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.database.QueryDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlProducer;
import org.dbunit.operation.DatabaseOperation;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.InputSource;

/**
 * @author akiba
 *
 */
public class AccessLogCSVExporterTest {
	private static AccessLogCSVExporter exporter;
	private static OracleDatabaseManager database;
	private static IDatabaseConnection iConnection;
	private static File partialData;
	private static Properties validProperty;
	private static Options options;
	
	private final static String EXPORT_FILE_NAME = "exportTest.csv";
	private final static String RETURN_CODE_STRING = "\r\n";
	private final static int HEADER_LINE_COUNT = 1;
	private final static int TEST_DATA_LINE_COUNT = 7;
	private final static int TEST_LINE_COUNT = HEADER_LINE_COUNT + TEST_DATA_LINE_COUNT;
	private final static int EVENT_DATE_COLUMN_INDEX = 3;
	
	@BeforeClass
	public static void setUp() throws Exception {
		validProperty = new Properties();
		validProperty.setProperty("exportDirectoryPath", "./");
		validProperty.setProperty("exportFileName", EXPORT_FILE_NAME);
		
		options = new Options();
	    options.addOption(BatchCommonHelper.createOption(ArgumentsUtil.START_OPTION, true));
	    options.addOption(BatchCommonHelper.createOption(ArgumentsUtil.END_OPTION, true));
	    options.addOption(BatchCommonHelper.createOption(ArgumentsUtil.ORDER_OPTION, false));
	    options.addOption(BatchCommonHelper.createOption(ArgumentsUtil.ENCODING_OPTION, false));

		// DBのバックアップを取得する
		ConfigurationController config = new ConfigurationController("data/AccessLogExport.properties");
		database = new OracleDatabaseManager(config.getProperites());
		
		iConnection = new DatabaseConnection(database.getConnection());
		
		QueryDataSet partialDataSet = new QueryDataSet(iConnection);
		partialDataSet.addTable("SctAccessLog");
		
		partialData = File.createTempFile("AccessLog", "xml");
		FlatXmlDataSet.write(partialDataSet, new FileOutputStream(partialData));
		
		// テストデータを投入する
		FlatXmlProducer xmlProducer = new FlatXmlProducer(new InputSource("data/SctAccessLogTestData.xml"));
		IDataSet dataSet = new FlatXmlDataSet(xmlProducer);
		DatabaseOperation.CLEAN_INSERT.execute(iConnection, dataSet);
	}
	
	@Before
	public void beforeEachTest() {
		File fileExportFile = new File(EXPORT_FILE_NAME);
		if (fileExportFile.exists()) {
			fileExportFile.delete();
		}
	}
	
	@AfterClass
	public static void tearDown() throws Exception {
		FlatXmlProducer xmlProducer = new FlatXmlProducer(new InputSource(partialData.getPath()));
		IDataSet dataSet = new FlatXmlDataSet(xmlProducer);
		DatabaseOperation.CLEAN_INSERT.execute(iConnection, dataSet);
		
		File fileExportFile = new File(EXPORT_FILE_NAME);
		fileExportFile.delete();
		
		database.getConnection().close();
	}
	
	@Test(expected = IOException.class)
	public void 設定ファイル情報が空の場合IOExceptionが発生する() throws Exception {
		String[] args = {"-start", "20140401", "-end", "20140531"};
		ArgumentsUtil arguments = new ArgumentsUtil(options, args);
		
		Properties invalidProperty = new Properties();		
		exporter = new AccessLogCSVExporter();
		exporter.export(database.getConnection(), arguments, invalidProperty);
	}
	
	@Test(expected = IOException.class)
	public void エクスポートファイルが未指定の場合IOExceptionが発生する() throws Exception {
		String[] args = {"-start", "20140401", "-end", "20140531"};
		ArgumentsUtil arguments = new ArgumentsUtil(options, args);
		
		Properties invalidProperty = new Properties();		
		invalidProperty.setProperty("exportDirectoryPath", "");
		invalidProperty.setProperty("exportFileName", "");
		exporter = new AccessLogCSVExporter();
		exporter.export(database.getConnection(), arguments, invalidProperty);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void 起動パラメータにstartとendを指定しないとIllegalArgumentExceptionが発生する() throws Exception {
		String[] invalidArgs = {"-tmp", "20140401", "-test", "20140531"};
		ArgumentsUtil invalidArguments = new ArgumentsUtil(options, invalidArgs);
		
		exporter = new AccessLogCSVExporter();
		exporter.export(database.getConnection(), invalidArguments, validProperty);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void 起動パラメータにstartを指定しないとIllegarArgumentExceptionが発生する() throws Exception {
		String[] noStartArgs = {"-sample", "20140401", "-end", "20140531"};
		ArgumentsUtil noStartArguments = new ArgumentsUtil(options, noStartArgs);
		
		exporter = new AccessLogCSVExporter();
		exporter.export(database.getConnection(), noStartArguments, validProperty);
	}

	@Test(expected = IllegalArgumentException.class)
	public void 起動パラメータにendを指定しないとIllegarArgumentExceptionが発生する() throws Exception {
		String[] noEndArgs = {"-start", "20140401", "-ending", "20140531"};
		ArgumentsUtil noEndArguments = new ArgumentsUtil(options, noEndArgs);
		
		exporter = new AccessLogCSVExporter();
		exporter.export(database.getConnection(), noEndArguments, validProperty);
	}
	
	@Test(expected = SQLException.class)
	public void connectionパラメータがNullの場合はSQLExceptionが発生する() throws Exception {
		String[] args = {"-start", "20140401", "-end", "20140531"};
		ArgumentsUtil arguments = new ArgumentsUtil(options, args);
		
		exporter = new AccessLogCSVExporter();
		exporter.export(null, arguments, validProperty);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void argumentsパラメータがNullの場合はIllegalArgumentExceptionが発生する() throws Exception {
		exporter = new AccessLogCSVExporter();
		exporter.export(database.getConnection(), null, validProperty);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void configInfoパラメータがNullの場合はIllegalArgumentExceptionが発生する() throws Exception {
		String[] args = {"-start", "20140401", "-end", "20140531"};
		ArgumentsUtil arguments = new ArgumentsUtil(options, args);
		
		exporter = new AccessLogCSVExporter();
		exporter.export(database.getConnection(), arguments, null);
	}

	@Test
	public void エンコーディングが未指定の場合MS932で改行コードはCRLFである() throws Exception {
		String[] args = {"-start", "20140401", "-end", "20140531"};
		ArgumentsUtil arguments = new ArgumentsUtil(options, args);
		
		exporter = new AccessLogCSVExporter();
		exporter.export(database.getConnection(), arguments, validProperty);
		List<String[]> exportedData = new ArrayList<String[]>();
		getMS932ExportedData(exportedData);
		boolean expected = true;
		boolean result = exportedData.get(1)[30].equals(new String(exportedData.get(1)[30].getBytes("MS932"), "MS932"));
		assertThat(result, is(expected));
		
		String expectedReturnCode = RETURN_CODE_STRING;
		String resultReturnCode = getReturnCode("MS932");
		assertThat(resultReturnCode, is(expectedReturnCode));
	}
	
	@Test
	public void エンコーディングにMS932を指定した場合MS932で改行コードはCRLFである() throws Exception {
		String[] args = {"-start", "20140401", "-end", "20140531", "-encoding", "MS932"};
		ArgumentsUtil arguments = new ArgumentsUtil(options, args);
		
		exporter = new AccessLogCSVExporter();
		exporter.export(database.getConnection(), arguments, validProperty);
		List<String[]> exportedData = new ArrayList<String[]>();
		getMS932ExportedData(exportedData);
		boolean expected = true;
		boolean result = exportedData.get(1)[30].equals(new String(exportedData.get(1)[30].getBytes("MS932"), "MS932"));
		assertThat(result, is(expected));
		
		String expectedReturnCode = RETURN_CODE_STRING;
		String resultReturnCode = getReturnCode("MS932");
		assertThat(resultReturnCode, is(expectedReturnCode));
	}
	
	@Test
	public void エンコーディングにUTF8を指定した場合UTF8で改行コードはCRLFである() throws Exception {
		String[] args = {"-start", "20140401", "-end", "20140531", "-encoding", "UTF-8"};
		ArgumentsUtil arguments = new ArgumentsUtil(options, args);
		
		exporter = new AccessLogCSVExporter();
		exporter.export(database.getConnection(), arguments, validProperty);
		List<String[]> exportedData = new ArrayList<String[]>();
		getUTF8ExportedData(exportedData);
		boolean expected = true;
		boolean result = exportedData.get(1)[30].equals(new String(exportedData.get(1)[30].getBytes("UTF-8"), "UTF-8"));
		assertThat(result, is(expected));
		
		String expectedReturnCode = RETURN_CODE_STRING;
		String resultReturnCode = getReturnCode("UTF-8");
		assertThat(resultReturnCode, is(expectedReturnCode));
	}
	
	@Test
	public void ディレクトリパスの終端がスラッシュなしの場合にアクセスログをエクスポートできる() throws Exception {
		String[] args = {"-start", "20140401", "-end", "20140531"};
		ArgumentsUtil arguments = new ArgumentsUtil(options, args);
		
		Properties nonSlashValidProperty = new Properties();
		nonSlashValidProperty.setProperty("exportDirectoryPath", ".");
		nonSlashValidProperty.setProperty("exportFileName", EXPORT_FILE_NAME);
		
		exporter = new AccessLogCSVExporter();
		exporter.export(database.getConnection(), arguments, nonSlashValidProperty);
		int expected = TEST_LINE_COUNT;
		int result = getExportedLineCount();
		assertThat(result, is(expected));
	}
	
	@Test
	public void ディレクトリパスの終端がスラッシュありの場合にアクセスログをエクスポートできる() throws Exception {
		String[] args = {"-start", "20140401", "-end", "20140531"};
		ArgumentsUtil arguments = new ArgumentsUtil(options, args);
		
		exporter = new AccessLogCSVExporter();
		exporter.export(database.getConnection(), arguments, validProperty);
		int expected = TEST_LINE_COUNT;
		int result = getExportedLineCount();
		assertThat(result, is(expected));
	}
	
	@Test
	public void エクスポートしたファイルの先頭行が列名になっている() throws Exception {
		String[] args = {"-start", "20140401", "-end", "20140531"};
		ArgumentsUtil arguments = new ArgumentsUtil(options, args);
		
		exporter = new AccessLogCSVExporter();
		exporter.export(database.getConnection(), arguments, validProperty);
		List<String[]> exportedData = new ArrayList<String[]>();
		getMS932ExportedData(exportedData);
		String expected = "SCTDATESTAMP";
		String result = exportedData.get(0)[0];
		assertThat(result, is(expected));
		
		expected = "EVENTDATE";
		result = exportedData.get(0)[EVENT_DATE_COLUMN_INDEX];
		assertThat(result, is(expected));
	}
	
	@Test
	public void ソートを指定しないとeventDateの昇順でソートされる() throws Exception {
		String[] args = {"-start", "20140401", "-end", "20140531"};
		ArgumentsUtil arguments = new ArgumentsUtil(options, args);
		
		exporter = new AccessLogCSVExporter();
		exporter.export(database.getConnection(), arguments, validProperty);
		List<String[]> exportedData = new ArrayList<String[]>();
		getMS932ExportedData(exportedData);
		String expected = getExpectedDateString(2014,4,10);
		String result = exportedData.get(1)[EVENT_DATE_COLUMN_INDEX];
		assertThat(result, is(expected));
		
		expected = getExpectedDateString(2014,5,27);
		result = exportedData.get(TEST_DATA_LINE_COUNT)[EVENT_DATE_COLUMN_INDEX];
		assertThat(result, is(expected));
	}
	
	
	@Test
	public void eventDateによるソートができる() throws Exception {
		String[] descArgs = {"-start", "20140401", "-end", "20140531", "-order", "DESC"};
		ArgumentsUtil descArguments = new ArgumentsUtil(options, descArgs);
		
		exporter = new AccessLogCSVExporter();
		exporter.export(database.getConnection(), descArguments, validProperty);
		List<String[]> exportedData = new ArrayList<String[]>();
		getMS932ExportedData(exportedData);
		String expected = getExpectedDateString(2014,5,27);
		String result = exportedData.get(1)[EVENT_DATE_COLUMN_INDEX];
		assertThat(result, is(expected));

		String[] ascArgs = {"-start", "20140401", "-end", "20140531", "-order", "ASC"};
		ArgumentsUtil ascArguments = new ArgumentsUtil(options, ascArgs);
		
		exporter.export(database.getConnection(), ascArguments, validProperty);
		getMS932ExportedData(exportedData);
		expected = getExpectedDateString(2014,4,10);
		result = exportedData.get(1)[EVENT_DATE_COLUMN_INDEX];
		assertThat(result, is(expected));
	}
	
	@Test
	public void 取得範囲の指定ができる() throws Exception {
		String[] mayArgs = {"-start", "20140501", "-end", "20140531", "-order", "DESC"};
		ArgumentsUtil mayArguments = new ArgumentsUtil(options, mayArgs);
		
		exporter = new AccessLogCSVExporter();
		exporter.export(database.getConnection(), mayArguments, validProperty);
		List<String[]> exportedData = new ArrayList<String[]>();
		getMS932ExportedData(exportedData);
		String expected = getExpectedDateString(2014,5,27);
		String result = exportedData.get(1)[EVENT_DATE_COLUMN_INDEX];
		assertThat(result, is(expected));
		
		int expectedLineCount = HEADER_LINE_COUNT + 2;
		int resultLineCount = exportedData.size();
		assertThat(resultLineCount, is(expectedLineCount));
		
		String[] aprilArgs = {"-start", "20140401", "-end", "20140430"};
		ArgumentsUtil aprilArguments = new ArgumentsUtil(options, aprilArgs);
		
		exporter.export(database.getConnection(), aprilArguments, validProperty);
		getMS932ExportedData(exportedData);
		expected = getExpectedDateString(2014,4,10);
		result = exportedData.get(1)[EVENT_DATE_COLUMN_INDEX];
		assertThat(result, is(expected));
		
		expectedLineCount = HEADER_LINE_COUNT + 5;
		resultLineCount = exportedData.size();
		assertThat(resultLineCount, is(expectedLineCount));
	}
	
	@Test
	public void 取得開始日と取得終了日を含むデータが取得できる() throws Exception {
		String[] args = {"-start", "20140427", "-end", "20140430"};
		ArgumentsUtil arguments = new ArgumentsUtil(options, args);
		
		exporter = new AccessLogCSVExporter();
		exporter.export(database.getConnection(), arguments, validProperty);
		List<String[]> exportedData = new ArrayList<String[]>();
		getMS932ExportedData(exportedData);
		String expected = getExpectedDateString(2014,4,27);
		String result = exportedData.get(1)[EVENT_DATE_COLUMN_INDEX];
		assertThat(result, is(expected));
		
		expected = "30-4-2014 23:59:59 JST";
		result = exportedData.get(4)[EVENT_DATE_COLUMN_INDEX];
		assertThat(result, is(expected));
	}
	
	@Test
	public void 取得開始日と取得終了日が同じでもデータが取得できる() throws Exception {
		String[] args = {"-start", "20140427", "-end", "20140427"};
		ArgumentsUtil arguments = new ArgumentsUtil(options, args);
		
		exporter = new AccessLogCSVExporter();
		exporter.export(database.getConnection(), arguments, validProperty);
		List<String[]> exportedData = new ArrayList<String[]>();
		getMS932ExportedData(exportedData);
		int expected = HEADER_LINE_COUNT + 1;
		int result = getExportedLineCount();
		assertThat(result, is(expected));
	}
	
	@Test
	public void データがない場合は列名だけ取得できる() throws Exception {
		String[] args = {"-start", "20140101", "-end", "20140131"};
		ArgumentsUtil arguments = new ArgumentsUtil(options, args);
		
		exporter = new AccessLogCSVExporter();
		exporter.export(database.getConnection(), arguments, validProperty);
		List<String[]> exportedData = new ArrayList<String[]>();
		getMS932ExportedData(exportedData);
		String expected = "SCTDATESTAMP";
		String result = exportedData.get(0)[0];
		assertThat(result, is(expected));
		
		int expectedLineCount = HEADER_LINE_COUNT;
		int resultLineCount = exportedData.size();
		assertThat(resultLineCount, is(expectedLineCount));
	}
	
	private int getExportedLineCount() throws Exception {
		CSVReader reader = new CSVReader(new FileReader(new File(EXPORT_FILE_NAME)));
		List<String[]> exportedData = reader.readAll();
		reader.close();
		return exportedData.size();
	}
	
	private void getMS932ExportedData(List<String[]> exportedData) throws Exception {
		exportedData.clear();
		Reader reader = new BufferedReader(new InputStreamReader(new FileInputStream(EXPORT_FILE_NAME), "MS932"));
		CSVReader csvReader = new CSVReader(reader);
		exportedData.addAll(csvReader.readAll());
		csvReader.close();
	}
	
	private void getUTF8ExportedData(List<String[]> exportedData) throws Exception {
		exportedData.clear();
		Reader reader = new BufferedReader(new InputStreamReader(new FileInputStream(EXPORT_FILE_NAME), "UTF-8"));
		CSVReader csvReader = new CSVReader(reader);
		exportedData.addAll(csvReader.readAll());
		csvReader.close();
	}
	
	private String getExpectedDateString(int year, int month, int day) {
		Calendar calDate = Calendar.getInstance();
		calDate.set(year,month-1,day,0,0,0);
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyy HH:mm:ss z");
		
		return dateFormat.format(new Date(calDate.getTimeInMillis()));
		
	}
	
	private String getReturnCode(String strEncoding) throws Exception {
		InputStreamReader in = new InputStreamReader(new FileInputStream(EXPORT_FILE_NAME), strEncoding);
		
		int code = 0;
		int next = 0;
		
		while((code = in.read()) != -1){
			if (code == 0x0d || code == 0x0a) {
				next = in.read();
				break;
			}
		}
		in.close();
		
		return String.format("%c%c", code,next);
		
	}
}
