/**
 * 
 */
package cjlite.plugin.restful;

import java.util.Map;

import cjlite.web.mvc.Model;
import cjlite.web.mvc.ModelView;
import cjlite.web.mvc.Views;

/**
 * @author YunYang
 * @version
 */
public class RestResult extends ModelView {

	private ResultCode result;

	/**
	 * @param view
	 * @param model
	 */
	protected RestResult(Model model) {
		super(Views.JsonView(""), model);
	}

	public ResultCode getResult() {
		return this.result;
	}

	public RestResult setResult(ResultCode code) {
		this.result = code;
		return this;
	}

	public RestResult add(String key, Object value) {
		this.getModel().add(key, value);
		return this;
	}

	public RestResult putAll(Map<String, ?> modelMap) {
		this.getModel().putAll(modelMap);
		return this;
	}

	public RestResult putAll(Model model) {
		this.getModel().putAll(model.getModelMap());
		return this;
	}

	public static RestResult New(Model model) {
		return new RestResult(model);
	}

	public static RestResult New() {
		return new RestResult(Model.New());
	}
}
