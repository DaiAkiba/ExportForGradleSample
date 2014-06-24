/**
 * 
 */
package commons.delete;


/**
 * DB削除処理を行うインスタンスを生成するための抽象Factoryクラスです。
 * 
 * @author akiba
 *
 */
public abstract class DeleterFactory {
	public abstract IDeleter createDeleter();
}
