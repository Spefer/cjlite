/**
 * 
 */
package cjlite.plugin.restful;

import cjlite.web.annotations.Path;
import cjlite.web.annotations.RequestMethod;
import cjlite.web.mvc.RequestContext;

/**
 * <pre>
 * Post: use for create
 * Get: read
 * Put: Update/replace
 * Patch: Update/modify
 * Delete: delete
 * 
 * 200 OK 201 Created 204 No Content
 * 304 Not Modified
 * 400 Bad Request 401 Unauthorized 403 Forbidden 404 Not Found 409 Conflict
 * 500 Internal Server Error
 * </pre>
 * 
 * @author YunYang
 * @version
 */

public interface RestfulEntries {

	/**
	 * Get List: return all items as list
	 * 
	 * @param context
	 * @return
	 */
	@Path(method = RequestMethod.GET)
	public RestResult getList(RequestContext context);

	/**
	 * Get a specified item by id
	 * 
	 * @param context
	 * @param id
	 * @return
	 */
	@Path(value = "{id}", method = RequestMethod.GET)
	public RestResult get(RequestContext context, String id);

	/**
	 * Used for update or replace item by given id, try to use patch method to update, not this one
	 * 
	 * @param context
	 * @param id
	 * @return
	 * 
	 * @see {@link cjlite.plugin.restful.RestfulEntries.patch(RequestContext, String)}
	 */
	@Path(value = "{id}", method = RequestMethod.PUT)
	public RestResult put(RequestContext context, String id);

	/**
	 * Used for delete by given id
	 * 
	 * @param context
	 * @param id
	 * @return
	 */
	@Path(value = "{id}", method = RequestMethod.DELETE)
	public RestResult delete(RequestContext context, String id);

	/**
	 * User for create a new item
	 * 
	 * @param context
	 * @return
	 */
	@Path(method = RequestMethod.POST)
	public RestResult post(RequestContext context);

	/**
	 * used for Update/modify item by given id,
	 * 
	 * @param context
	 * @param id
	 * @return
	 */
	@Path(value = "{id}", method = RequestMethod.PATCH)
	public RestResult patch(RequestContext context, String id);

}
