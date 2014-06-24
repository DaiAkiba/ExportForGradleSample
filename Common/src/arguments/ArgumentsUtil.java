/**
 * 
 */
package commons.arguments;

import java.sql.Date;
import java.util.Calendar;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * バッチ処理の起動パラメータに指定される文字列を解析し、適切な形式で返却します。<br />
 * 対応している起動パラメータは、"-start"、"-end"、"-order"、"-encoding"。<br />
 * "-start"、"-end"は、yyyyMMddで指定されることを前提としています。<br />
 * "-order"は、ASC or DESCで指定されることを前提としています。<br />
 * "-encoding"は、MS932 or UTF-8で指定されることを前提としています。<br />
 * 
 * @author akiba
 *
 */
public class ArgumentsUtil {
	public static final String START_OPTION = "start";
	public static final String END_OPTION = "end";
	public static final String ORDER_OPTION = "order";
	public static final String ENCODING_OPTION = "encoding";
	
	private String strStart;
	private String strEnd;
	private String strOrder;
	private String strEncoding;
	
	private static Log logger = LogFactory.getLog(ArgumentsUtil.class);
	
   /**
    * コンストラクタ。<br />
    * 
    * @param options
    *            起動パラメータチェック用Optionsオブジェクト
    * @param args
    *            バッチ起動パラメータ
    * @throws IllegalArgumentException
    * 			 パラメータエラーの場合
    */
	public ArgumentsUtil(Options options, String[] args) throws IllegalArgumentException {
		try {
			CommandLineParser parser = new BasicParser();
			CommandLine commandLine = parser.parse( options, args );
			
			if (commandLine.hasOption(START_OPTION)) {
				setStart(commandLine.getOptionValue(START_OPTION));
			}
			if (commandLine.hasOption(END_OPTION)) {
				setEnd(commandLine.getOptionValue(END_OPTION));
			}
			if (commandLine.hasOption(ORDER_OPTION)) {
				setOrder(commandLine.getOptionValue(ORDER_OPTION));
			}
			if (commandLine.hasOption(ENCODING_OPTION)) {
				setEncoding(commandLine.getOptionValue(ENCODING_OPTION));
			}
		}
		catch (ParseException e) {
			logger.debug(e + ":" + e.getMessage());
			throw new IllegalArgumentException();
		}
		catch(IllegalArgumentException e) {
			logger.debug(e + ":" + e.getMessage());
			throw e;
		}
	}
	
   /**
    * 起動パラメータ-startで指定された文字列をDate型で取得します。<br />
    * 
    * @param なし
    * @return Date 開始日付
    */
	public Date getStartByDate() {
		return getDateByString(strStart);
	}
	
   /**
    * 起動パラメータ-endで指定された文字列をDate型で取得します。<br />
    * 
    * @param なし
    * @return Date 終了日付
    */
	public Date getEndByDate() {
		return getDateByString(strEnd);
	}
	
   /**
    * 起動パラメータ-orderで指定された文字列を取得します。<br />
    * 
    * @param なし
    * @return String ソート順
    */
	public String getOrder() {
		return strOrder;
	}
	
   /**
    * 起動パラメータ-encodingで指定された文字列を取得します。<br />
    * 
    * @param なし
    * @return String ソート順
    */
	public String getEncoding() {
		return strEncoding;
	}
	
   /**
    * 起動パラメータ-startで指定された文字列の形式チェックを行いローカル変数に設定します。<br />
    * private API<br />
    * 
    * @param start
    * 			-startで指定された文字列
    * @throws IllegalArgumentException
    * 			パラメータが不正の場合
    */
	private void setStart(String start) throws IllegalArgumentException {
		try {
			validateDateArgument(start);
			strStart = start;
		}
		catch (Exception e) {
			logger.debug(e + ":" + e.getMessage());
			throw new IllegalArgumentException();
		}
	}
	
   /**
    * 起動パラメータ-endで指定された文字列の形式チェックを行いローカル変数に設定します。<br />
    * private API<br />
    * 
    * @param end
    * 			-endで指定された文字列
    * @throws IllegalArgumentException
    * 			パラメータが不正の場合
    */
	private void setEnd(String end) throws IllegalArgumentException {
		try {
			validateDateArgument(end);
			strEnd = end;
		}
		catch (Exception e) {
			logger.debug(e + ":" + e.getMessage());
			throw new IllegalArgumentException();
		}
	}
	
   /**
    * 起動パラメータ-orderで指定された文字列の形式チェックを行いローカル変数に設定します。<br />
    * private API<br />
    * 
    * @param order
    * 			-orderで指定された文字列
    * @throws IllegalArgumentException
    * 			パラメータが不正の場合
    */
	private void setOrder(String order) throws IllegalArgumentException {
		try {
			validateOrderArgument(order);
			strOrder = order;
		}
		catch (Exception e) {
			logger.debug(e + ":" + e.getMessage());
			throw new IllegalArgumentException();
		}
	}
	
   /**
    * 起動パラメータ-encodingで指定された文字列の形式チェックを行いローカル変数に設定します。<br />
    * private API<br />
    * 
    * @param encoding
    * 			-encodingで指定された文字列
    * @throws IllegalArgumentException
    * 			パラメータが不正の場合
    */
	private void setEncoding(String encoding) throws IllegalArgumentException {
		try {
			validateEncodingArgument(encoding);
			strEncoding = encoding;
		}
		catch (Exception e) {
			logger.debug(e + ":" + e.getMessage());
			throw new IllegalArgumentException();
		}
	}
	
   /**
    * yyyyMMddで指定されている文字列の形式チェックを行います。<br />
    * private API<br />
    * 
    * @param strDate
    * 			yyyyMMdd形式で指定された文字列
    * @throws NullPointerException
    * 			パラメータがnullの場合
    * @throws NumberFormatException
    * 			パラメータが数値に変換できない場合
    * @throws IllegalArgumentException
    * 			パラメータが不正な場合
    */
	private void validateDateArgument(String strDate) throws Exception {
		if (strDate.length() != 8) {
			throw new IllegalArgumentException();
		}
		
		Integer.parseInt(strDate);
	
		IntYMD ymd = getIntYMD(strDate);
	
		if (ymd.year < 1970 || ymd.year > 3000 ||
			ymd.month < 1 || ymd.month > 12 ||
			ymd.day < 1 || ymd.day > getLastDayOfMonth(ymd)) {
			throw new IllegalArgumentException();
		}
	}
	
   /**
    * 月末の日付を取得します。<br />
    * private API<br />
    * 
    * @param ymd
    * 			取得対象の年、月が設定されたIntYMD形式のオブジェクト
    * @return int 処理結果（0：成功、-1；失敗）
    */
	private int getLastDayOfMonth(IntYMD ymd) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, ymd.year);
		cal.set(Calendar.MONTH, ymd.month-1);
		return cal.getActualMaximum(Calendar.DATE);
	}
	
   /**
    * ソート順に指定されている文字列の形式チェックを行います。<br />
    * private API<br />
    * 
    * @param strOrder
    * 			-orderで指定された文字列
    * @throws NullPointerException
    * 			パラメータがnullの場合
    * @throws IllegalArgumentException
    * 			パラメータが不正な場合
    */
	private void validateOrderArgument(String strOrder) throws Exception {
		if (!strOrder.equalsIgnoreCase("DESC") && !strOrder.equalsIgnoreCase("ASC")) {
			throw new IllegalArgumentException();
		}
	}

   /**
    * エンコード形式に指定されている文字列の形式チェックを行います。<br />
    * private API<br />
    * 
    * @param strEncoding
    * 			-encodingで指定された文字列
    * @throws NullPointerException
    * 			パラメータがnullの場合
    * @throws IllegalArgumentException
    * 			パラメータが不正な場合
    */
	private void validateEncodingArgument(String strEncoding) throws Exception {
		if (!strEncoding.equalsIgnoreCase("MS932") && !strEncoding.equalsIgnoreCase("UTF-8")) {
			throw new IllegalArgumentException();
		}
	}

   /**
    * yyyyMMdd形式で指定されている文字列の形式チェックを行いDate型に変換する。<br />
    * private API<br />
    * 
    * @param dateArgument
    * 			yyyyMMddで指定された文字列
    * @return Date パラメータに指定された文字列をDate型に変換したオブジェクト
    */
	private Date getDateByString(String dateArgument) {
		if (dateArgument == null || dateArgument.length() == 0) {
			return null;
		}
		
		IntYMD ymd = getIntYMD(dateArgument);
		
		Calendar calDate = Calendar.getInstance();
		calDate.set(ymd.year,ymd.month-1,ymd.day,0,0,0);
		
		return new Date(calDate.getTimeInMillis());
	}
	
	class IntYMD {
		int year;
		int month;
		int day;
	}
	
   /**
    * yyyyMMdd形式で指定されている文字列をIntYMD形式に変換する。<br />
    * private API<br />
    * 
    * @param dateString
    * 			yyyyMMddで指定された文字列
    * @return IntYMD パラメータに指定された文字列をIntYMD形式に変換したオブジェクト
    */
	private IntYMD getIntYMD(String dateString){
		IntYMD intYMD = new IntYMD();
		
		intYMD.year = Integer.parseInt(dateString.substring(0, 4));
		intYMD.month = Integer.parseInt(dateString.substring(4,6));
		intYMD.day = Integer.parseInt(dateString.substring(6, 8));
		
		return intYMD;
	}
}
