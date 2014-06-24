/**
 * 
 */
package commons.export;

/**
 * エクスポートを行うインスタンスを生成するための抽象Factoryクラスです。
 * 
 * @author akiba
 *
 */
public abstract class ExporterFactory {
	public abstract IExporter createExporter();
}
