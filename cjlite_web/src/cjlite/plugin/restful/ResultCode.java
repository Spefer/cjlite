/**
 * 
 */
package cjlite.plugin.restful;

/**
 * <pre>
 * 200 OK 201 Created 204 No Content
 * 304 Not Modified
 * 400 Bad Request 401 Unauthorized 403 Forbidden 404 Not Found 409 Conflict
 * 500 Internal Server Error
 * </pre>
 * 
 * @author YunYang
 * @version
 */
public enum ResultCode {

	UnSpecialfy(0),

	OK(200),

	Created(201),

	NoContent(204),

	NotModified(304),

	BadRequest(400),

	Unauthorized(401),

	Forbidden(403),

	NotFound(404),

	Conflict(409),

	InternalServerError(500);

	private int code;

	ResultCode(int code) {
		this.code = code;
	}

	public int getCode() {
		return this.code;
	}

}
