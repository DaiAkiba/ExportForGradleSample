package export;

import static jp.co.dgic.testing.framework.DJUnitTestCase.addReturnValue;
import static jp.co.dgic.testing.framework.DJUnitTestCase.assertCalled;
import static jp.co.dgic.testing.framework.DJUnitTestCase.assertNotCalled;
import static jp.co.dgic.testing.framework.DJUnitTestCase.getArgument;
import static jp.co.dgic.testing.framework.DJUnitTestCase.getCallCount;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import jp.co.dgic.testing.common.virtualmock.MockObjectManager;

import org.junit.Before;
import org.junit.experimental.runners.Enclosed;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

@RunWith(Enclosed.class)
public class AccessLogExportTest {
	@RunWith(Theories.class)
	public static class 起動パラメータにnullを指定 {
		@Before
		public void setUp() {
			MockObjectManager.initialize();
		    // System.exitメソッドを無効化
		    addReturnValue("java.lang.System", "exit");
		}

		@Theory
		public void エラー終了する() throws Exception {
			// ターゲットクラス実行
		    AccessLogExport.main(null);
		    
		    int expected = 1;
		    int result = getCallCount("java.lang.System", "exit");
		    
		    // 復帰ステータスの個数を検証
//			assertThat(result, is(expected));
		
		    // 復帰ステータスの検証
			expected = -1;
			result = ((Integer)getArgument("java.lang.System", "exit", 0)).intValue();
		    assertThat( result, is(expected) );
		}
	}
	
	@RunWith(Theories.class)
	public static class 不正な起動パラメータを指定 {
		@Before
		public void setUp() {
			MockObjectManager.initialize();
		    // System.exitメソッドを無効化
		    addReturnValue("java.lang.System", "exit");
		}

		@DataPoints
		public static String[][] INVALID_PARAM = {{"-tmp", "20140401", "-test", "20140531"}
												 ,{"-aaa", "20140401", "-end", "20140531"}
												 ,{"-start", "20140401", "-abc", "20140531"}
												 ,{"-start", "2014110", "-end", "20140531"}
												 ,{"-start", "2014011", "-end", "20140531"}
												 ,{"-start", "00000000", "-end", "20140531"}
												 ,{"-start", "99999999", "-end", "20140531"}
												 ,{"-start", "aaa", "-end", "20140531"}
												 ,{"-start", "2014abc1", "-end", "20140531"}
												 ,{"-start", "20140231", "-end", "20140531"}
												 ,{"-start", "20140501", "-end", "2014110"}
												 ,{"-start", "20140501", "-end", "2014011"}
												 ,{"-start", "20140501", "-end", "00000000"}
												 ,{"-start", "20140501", "-end", "99999999"}
												 ,{"-start", "20140501", "-end", "xyz"}
												 ,{"-start", "20140501", "-end", "20aaa531"}
												 ,{"-start", "20140131", "-end", "20140431"}};
		
		@Theory
		public void エラー終了する(String[] args) throws Exception {
		    // ターゲットクラス実行
		    AccessLogExport.main(args);
		    
		    int expected = 1;
		    int result = getCallCount("java.lang.System", "exit");
		    System.out.println(getCallCount("AccessLogExport", "errorExit" ));
		    assertNotCalled("IExporter", "export");
		    
		    // 復帰ステータスの個数を検証
//			assertThat(result, is(expected));
		
		    // 復帰ステータスの検証
			expected = -1;
			result = ((Integer)getArgument("java.lang.System", "exit", 0)).intValue();
		    assertThat( result, is(expected) );
		}
	}
	
	@RunWith(Theories.class)
	public static class 正しい起動パラメータを指定 {
		@Before
		public void setUp() {
			MockObjectManager.initialize();
		    // System.exitメソッドを無効化
		    addReturnValue("java.lang.System", "exit");
		}

		@DataPoints
		public static String[][] VALID_PARAM = {{"-start", "20140401", "-end", "20140430"}};
		
		@Theory
		public void 正常終了する(String[] args) throws Exception {
		    // ターゲットクラス実行
		    AccessLogExport.main(args);
		    
		    assertCalled("IExporter", "export");

		    // 復帰ステータスの検証
			int expected = 0;
			int result = ((Integer)getArgument("java.lang.System", "exit", 0)).intValue();
		    assertThat( result, is(expected) );
		}
	}
}
