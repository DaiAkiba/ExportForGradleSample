/**
 * 
 */
package export;

import commons.export.ExporterFactory;
import commons.export.IExporter;

/**
 * アクセスログエクスポートを行うインスタンスを生成するためのFactoryクラスです。
 * 
 * @author akiba
 *
 */
class AccessLogExporterFactory extends ExporterFactory {
	public IExporter createExporter() {
		return new AccessLogCSVExporter();
	}
}
