/**
 * 
 */
package cjlite.plugin.upload;

import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.Scopes;
import com.google.inject.internal.UniqueAnnotations;

import cjlite.plugin.Plugin;
import cjlite.web.handler.Handler;

/**
 * @author kevin
 * 
 */
@Deprecated
public class UploadPlugin implements Plugin {

	private static final Key<Handler> key = Key.get(Handler.class, UniqueAnnotations.create());

	@Override
	public void configure(Binder binder) {
		binder.bind(key).to(MultiPartHandler.class).in(Scopes.SINGLETON);
	}

	@Override
	public String getName() {
		return "Upload Plugin";
	}

}
