/**
 * 
 */
package cjlite.web.mvc;

import java.util.Map;
import java.util.Objects;

import cjlite.web.annotations.RequestMethod;

/**
 * @author YunYang
 * 
 */
public class ModelView {

	private static final String modelRequired = "Model can not null";

	private final View view;

	private final Model model;

	protected ModelView(View view, Model model) {
		this.view = view;
		this.model = model;
	}

	public View getView() {
		return this.view;
	}

	public Model getModel() {
		return this.model;
	}

	public Map<String, Object> getModelMap() {
		return this.model.getModelMap();
	}

	/**
	 * return a FreeMarker ModelView
	 * 
	 * @param fileName
	 * @param model
	 * @return
	 */
	public static ModelView FreeMarkerView(String fileName, Model model) {
		Objects.requireNonNull(model, modelRequired);
		return New(Views.FreeMarkerView(fileName), model);
	}

	/**
	 * return a Json ModelView
	 * 
	 * @param fileName
	 * @param model
	 * @return
	 */
	public static ModelView JsonView(Model model) {
		Objects.requireNonNull(model, modelRequired);
		return JsonView("", model);
	}

	/**
	 * return a Json ModelView
	 * 
	 * @param fileName
	 * @param model
	 * @return
	 */
	public static ModelView JsonView(String fileName, Model model) {
		Objects.requireNonNull(model, modelRequired);
		return New(Views.JsonView(fileName), model);
	}

	/**
	 * Exclude Context Path
	 * 
	 * @param redirectUrl
	 * @return
	 */
	public static ModelView redirect(String redirectUrl) {
		return New(Views.redirectView(redirectUrl), Model.New());
	}

	public static ModelView New(View view, Model model) {
		Objects.requireNonNull(model, modelRequired);
		return new ModelView(view, model);
	}

	/**
	 * this a Path Redirect ModelView, and default Request Method is GET,
	 * 
	 * @param pathRedirectUrl
	 * @param model
	 * @return
	 */
	public static ModelView pathRedirect(String pathRedirectUrl, Model model) {
		return new ModelView(Views.pathRedirect(pathRedirectUrl, RequestMethod.GET), model);
	}

	/**
	 * this a Path Redirect ModelView with given path and method,
	 * 
	 * @param pathRedirectUrl
	 * @param model
	 * @return
	 */
	public static ModelView pathRedirect(String pathRedirectUrl, RequestMethod method, Model model) {
		return new ModelView(Views.pathRedirect(pathRedirectUrl, method), model);
	}

	/**
	 * Response Model and view, model is empty
	 * 
	 * @return
	 */
	public static ModelView ResponseModelView() {
		return ResponseModelView(Model.New());
	}

	/**
	 * @return
	 */
	public static ModelView ResponseModelView(Model model) {
		Objects.requireNonNull(model, modelRequired);
		return new ModelView(Views.ResponseView(), model);
	}

	/**
	 * @return
	 */
	public static ModelView Empty() {
		return new ModelView(Views.ResponseView(), Model.New());
	}

	/**
	 * @param string
	 * @return
	 */
	public static ModelView TextView(String string) {
		Model model = Model.New();
		model.add(Views.Text, string);
		return new ModelView(new View(Views.Text, Views.textContentType), model);
	}

	/**
	 * @param string
	 * @return
	 */
	public static ModelView HtmlTextView(String string) {
		Model model = Model.New();
		model.add(Views.Text, string);
		return new ModelView(new View(Views.Text, Views.htmlContentType), model);
	}

}
