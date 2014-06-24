/**
 * 
 */
package commons.arguments;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertNull;

import java.util.Calendar;

import commons.helper.BatchCommonHelper;

import org.apache.commons.cli.Options;
import org.junit.Before;
import org.junit.Rule;
import org.junit.experimental.runners.Enclosed;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

/**
 * @author akiba
 *
 */
@RunWith(Enclosed.class)
public class ArgumentsUtilTest {
	public static Options createOptions() {
		Options options = new Options();
		 
	    options.addOption(BatchCommonHelper.createOption(ArgumentsUtil.START_OPTION, false));
	    options.addOption(BatchCommonHelper.createOption(ArgumentsUtil.END_OPTION, false));
	    options.addOption(BatchCommonHelper.createOption(ArgumentsUtil.ORDER_OPTION, false));
	    options.addOption(BatchCommonHelper.createOption(ArgumentsUtil.ENCODING_OPTION, false));
		
		return options;
	}
	
	@RunWith(Theories.class)
	public static class 未対応のオプションパラメータを指定 {
		@Rule
	    public ExpectedException expectedException = ExpectedException.none();
		
		@DataPoints
		public static String[][] UNSUPPORTED_PARAM = {{"-sample", "abc"}
													 ,{"-sart", "def"}};
		
		@Theory
		public void 設定値は全てnullになっている(String[] args) {
	        expectedException.expect(IllegalArgumentException.class);
			ArgumentsUtil arguments = new ArgumentsUtil(ArgumentsUtilTest.createOptions(), args);
		}
	}
	
	@RunWith(Theories.class)
	public static class オプションパラメータが未指定 {
		@DataPoints
		public static String[][] UNSUPPORTED_PARAM = {{"start", "20140401"}
													 ,{"end", "20140530"}
													 ,{"order", "DESC"}
													 ,{"encoding", "MS932"}};
		
		@Theory
		public void 設定値は全てnullになっている(String[] args) {
			ArgumentsUtil arguments = new ArgumentsUtil(ArgumentsUtilTest.createOptions(), args);
			assertNull(arguments.getStartByDate());
			assertNull(arguments.getEndByDate());
			assertNull(arguments.getOrder());
			assertNull(arguments.getEncoding());
		}
	}
	
	@RunWith(Theories.class)
	public static class 必須オプションが未指定 {
		@Rule
	    public ExpectedException expectedException = ExpectedException.none();
		
		@DataPoints
		public static String[][] UNSUPPORTED_PARAM = {{"-start", "20140401" , "end", "20140530" , "order", "DESC"}};
		
		@Theory
		public void IllegalArgumentExceptionが発生する(String[] args) {
			Options localOptions = new Options();
		    localOptions.addOption(BatchCommonHelper.createOption(ArgumentsUtil.START_OPTION, false));
		    localOptions.addOption(BatchCommonHelper.createOption(ArgumentsUtil.END_OPTION, false));
		    localOptions.addOption(BatchCommonHelper.createOption(ArgumentsUtil.ORDER_OPTION, false));
		    localOptions.addOption(BatchCommonHelper.createOption(ArgumentsUtil.ENCODING_OPTION, true));
		    
	        expectedException.expect(IllegalArgumentException.class);
			ArgumentsUtil arguments = new ArgumentsUtil(localOptions, args);
		}
	}
	
	@RunWith(Theories.class)
	public static class 値が未指定 {
		@Rule
	    public ExpectedException expectedException = ExpectedException.none();
		
		@DataPoints
		public static String[][] NOVALUE_PARAM = {{"-start"}
												 ,{"-end"}
												 ,{"-order"}
												 ,{"-encoding"}};
		
		@Theory
		public void IllegalArgumentExceptionが発生する(String[] args) {
	        expectedException.expect(IllegalArgumentException.class);
			ArgumentsUtil arguments = new ArgumentsUtil(ArgumentsUtilTest.createOptions(), args);
		}
	}
	
	@RunWith(Theories.class)
	public static class 年月日指定の項目にアルファベットを指定 {
		@Rule
	    public ExpectedException expectedException = ExpectedException.none();
		
		@DataPoints
		public static String[][] ALPHA_PARAM = {{"-start", "abcdefgh"}
												,{"-start", "aaaa1231"}
												,{"-start", "2014z201"}
												,{"-start", "20140h31"}
												,{"-start", "ABC"}
												,{"-end", "201405d1"}
												,{"-end", "201405dd"}
												,{"-end", "abcdefgh"}};
		
		@Theory
		public void IllegalArgumentExceptionが発生する(String[] args) {
	        expectedException.expect(IllegalArgumentException.class);
			ArgumentsUtil arguments = new ArgumentsUtil(ArgumentsUtilTest.createOptions(), args);
		}
	}
	
	@RunWith(Theories.class)
	public static class 年月日指定の項目に不正なフォーマットを指定 {
		@Rule
	    public ExpectedException expectedException = ExpectedException.none();
		
		@DataPoints
		public static String[][] INVALID_FORMAT_PARAM = {{"-start", "2014/04/05"}
														,{"-start", "2014-4-1"}
														,{"-end", "2014/04/05"}
														,{"-end", "2014-4-1"}};
		
		@Theory
		public void IllegalArgumentExceptionが発生する(String[] args) {
	        expectedException.expect(IllegalArgumentException.class);
			ArgumentsUtil arguments = new ArgumentsUtil(ArgumentsUtilTest.createOptions(), args);
		}
	}
	
	@RunWith(Theories.class)
	public static class 年月日指定の項目に不正な日付を指定 {
		@Rule
	    public ExpectedException expectedException = ExpectedException.none();
		
		@DataPoints
		public static String[][] INVALID_FORMAT_PARAM = {{"-start", "00000000"}
														,{"-start", "99999999"}
														,{"-start", "19691231"}
														,{"-start", "30010101"}
														,{"-start", "20140229"}
														,{"-start", "20150230"}
														,{"-end", "20141301"}
														,{"-end", "20141200"}
														,{"-end", "20150431"}
														,{"-end", "20141232"}};
		
		@Theory
		public void IllegalArgumentExceptionが発生する(String[] args) {
	        expectedException.expect(IllegalArgumentException.class);
			ArgumentsUtil arguments = new ArgumentsUtil(ArgumentsUtilTest.createOptions(), args);
		}
	}
	
	@RunWith(Theories.class)
	public static class startの年月日指定の項目に正常な値を指定 {
		@DataPoints
		public static String[][] VALID_PARAM = {{"-start", "20140101"}
												,{"-start", "20140228"}
												,{"-start", "20160229"}
												,{"-start", "20141001"}
												,{"-start", "20141231"}
												,{"-start", "19700101"}
												,{"-start", "29991231"}};
		
		@Theory
		public void 正常にDate型が取得できる(String[] args) {
			ArgumentsUtil arguments = new ArgumentsUtil(ArgumentsUtilTest.createOptions(), args);
			Calendar calDate = Calendar.getInstance();
			calDate.setTime(arguments.getStartByDate());
			int expected = Integer.parseInt(args[1].substring(0, 4));
			int result = calDate.get(Calendar.YEAR);
			assertThat(result, is(expected));
			expected = Integer.parseInt(args[1].substring(4, 6));
			result = calDate.get(Calendar.MONTH) + 1;
			assertThat(result, is(expected));
			expected = Integer.parseInt(args[1].substring(6, 8));
			result = calDate.get(Calendar.DATE);
			assertThat(result, is(expected));
		}
	}
	
	@RunWith(Theories.class)
	public static class endの年月日指定の項目に正常な値を指定 {
		@DataPoints
		public static String[][] VALID_PARAM = {{"-end", "20140101"}
												,{"-end", "20140228"}
												,{"-end", "20160229"}
												,{"-end", "20141001"}
												,{"-end", "20141231"}
												,{"-end", "19700101"}
												,{"-end", "29991231"}};
		
		@Theory
		public void 正常にDate型が取得できる(String[] args) {
			ArgumentsUtil arguments = new ArgumentsUtil(ArgumentsUtilTest.createOptions(), args);
			Calendar calDate = Calendar.getInstance();
			calDate.setTime(arguments.getEndByDate());
			int expected = Integer.parseInt(args[1].substring(0, 4));
			int result = calDate.get(Calendar.YEAR);
			assertThat(result, is(expected));
			expected = Integer.parseInt(args[1].substring(4, 6));
			result = calDate.get(Calendar.MONTH) + 1;
			assertThat(result, is(expected));
			expected = Integer.parseInt(args[1].substring(6, 8));
			result = calDate.get(Calendar.DATE);
			assertThat(result, is(expected));
		}
	}
	
	@RunWith(Theories.class)
	public static class ソート順指定の項目に不正な文字列を指定 {
		@Rule
	    public ExpectedException expectedException = ExpectedException.none();
		
		@DataPoints
		public static String[][] INVALID_FORMAT_PARAM = {{"-order", "ABCD"}
														,{"-order", "0000"}
														,{"-order", "A1B2"}
														,{"-order", "DES"}
														,{"-order", "ASCC"}};
		
		@Theory
		public void IllegalArgumentExceptionが発生する(String[] args) {
	        expectedException.expect(IllegalArgumentException.class);
			ArgumentsUtil arguments = new ArgumentsUtil(ArgumentsUtilTest.createOptions(), args);
		}
	}

	@RunWith(Theories.class)
	public static class ソート順指定の項目に正しい文字列を指定 {
		@DataPoints
		public static String[][] INVALID_FORMAT_PARAM = {{"-order", "DESC"}
														,{"-order", "ASC"}
														,{"-order", "desc"}
														,{"-order", "asc"}};
		
		@Theory
		public void 正常にソート順が取得できる(String[] args) {
			ArgumentsUtil arguments = new ArgumentsUtil(ArgumentsUtilTest.createOptions(), args);
			String expectedString = args[1];
			String resultString = arguments.getOrder();
			assertThat(resultString, is(expectedString));
		}
	}
	
	@RunWith(Theories.class)
	public static class エンコーディング指定の項目に不正な文字列を指定 {
		@Rule
	    public ExpectedException expectedException = ExpectedException.none();
		
		@DataPoints
		public static String[][] INVALID_FORMAT_PARAM = {{"-encoding", "UTFUTF"}
														,{"-encoding", "MMMSSS"}
														,{"-encoding", "009909"}
														,{"-encoding", "A9B3M"}
														,{"-encoding", "SJIS"}
														,{"-encoding", "UTF-16"}};
		
		@Theory
		public void IllegalExceptionが発生する(String[] args) {
	        expectedException.expect(IllegalArgumentException.class);
			ArgumentsUtil arguments = new ArgumentsUtil(ArgumentsUtilTest.createOptions(), args);
		}
	}
	
	@RunWith(Theories.class)
	public static class エンコーディング指定の項目に正しい文字列を指定 {
		@DataPoints
		public static String[][] INVALID_FORMAT_PARAM = {{"-encoding", "UTF-8"}
														,{"-encoding", "MS932"}
														,{"-encoding", "utf-8"}
														,{"-encoding", "ms932"}};
		
		@Theory
		public void 正常にエンコード形式が取得できる(String[] args) {
			ArgumentsUtil arguments = new ArgumentsUtil(ArgumentsUtilTest.createOptions(), args);
			String expectedString = args[1];
			String resultString = arguments.getEncoding();
			assertThat(resultString, is(expectedString));
		}
	}
}
