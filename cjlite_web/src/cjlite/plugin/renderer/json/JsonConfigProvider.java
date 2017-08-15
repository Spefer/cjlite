/**
 * 
 */
package cjlite.plugin.renderer.json;

import javax.inject.Provider;

import com.alibaba.fastjson.serializer.SerializeConfig;

/**
 * @author YunYang
 * @version Aug 29, 2015 11:24:46 AM
 */
public class JsonConfigProvider implements Provider<JsonConfig> {

	@Override
	public JsonConfig get() {
		return new JsonConfig() {

			@Override
			public SerializeConfig getSerializeConfig() {
				return SerializeConfig.getGlobalInstance();
			}
		};
	}

}
