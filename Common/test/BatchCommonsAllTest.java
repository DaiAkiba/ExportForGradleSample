package commons;

import commons.arguments.ArgumentsUtilTest;
import commons.config.ConfigurationControllerTest;
import commons.database.OracleDatabaseManagerTest;
import commons.helper.BatchCommonHelperTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
    ArgumentsUtilTest.class,
    ConfigurationControllerTest.class,
    OracleDatabaseManagerTest.class,
    BatchCommonHelperTest.class,
    })
public class BatchCommonsAllTest {
}
