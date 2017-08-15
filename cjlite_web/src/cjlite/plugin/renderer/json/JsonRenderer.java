package cjlite.plugin.renderer.json;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cjlite.config.Config;
import cjlite.log.Logger;
import cjlite.web.mvc.ModelView;
import cjlite.web.mvc.Views;
import cjlite.web.render.AbstractRenderer;
import cjlite.web.render.RenderException;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.alibaba.fastjson.serializer.SerializerFeature;

public class JsonRenderer extends AbstractRenderer {

	private static final Logger logger = Logger.thisClass();

	private static String default_InputEncoding = "UTF-8";
	private static String default_OutputEncoding = "UTF-8";

	private transient final String contentType = "application/json; charset=" + default_OutputEncoding;
	private transient final String contentTypeForIE = "text/html; charset=" + default_OutputEncoding;

	@Inject
	private Config config;

	@Inject
	private JsonConfig jsonConfig;

	private SerializerFeature[] serializerFeatures;

	@Override
	public void initial() {

	}

	@Override
	public String getView() {
		return Views.Json;
	}

	@Override
	public void render(ModelView modelView, HttpServletRequest request, HttpServletResponse response)
			throws RenderException {
		try {
			boolean isIe = this.parseClientAgent(request);
			if (isIe) {
				response.setContentType(this.contentTypeForIE);
			} else {
				response.setContentType(this.contentType);
			}
			response.setHeader("Pragma", "no-cache"); // HTTP/1.0 caches might not implement Cache-Control and might
														// only implement Pragma: no-cache
			response.setHeader("Cache-Control", "no-cache");
			response.setDateHeader("Expires", 0);
			PrintWriter pw = response.getWriter();
			if (serializerFeatures != null) {
				this.writeJSONStringTo(modelView.getModelMap(), pw, serializerFeatures);
			} else {
				this.writeJSONStringTo(modelView.getModelMap(), pw);
			}
			pw.flush();
		} catch (IOException e) {
			throw new RenderException("error on render Json file data", e);
		}
	}

	private void writeJSONStringTo(Map<String, Object> modelMap, PrintWriter writer, SerializerFeature... features) {
		SerializeWriter out = new SerializeWriter(writer);

		try {
			SerializeConfig sconfig = this.jsonConfig.getSerializeConfig();
			JSONSerializer serializer = new JSONSerializer(out, sconfig);
			for (com.alibaba.fastjson.serializer.SerializerFeature feature : features) {
				serializer.config(feature, true);
			}

			serializer.write(modelMap);
		} finally {
			out.close();
		}
	}

	private boolean parseClientAgent(HttpServletRequest request) {
		String user_agent = request.getHeader("User-Agent");
		return user_agent.indexOf("MSIE") >= 0 || user_agent.indexOf("Trident") > 0;
	}

}
