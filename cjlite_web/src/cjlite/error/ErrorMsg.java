package cjlite.error;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import cjlite.utils.Maps;
import cjlite.utils.Strings;

public final class ErrorMsg {

	private AtomicInteger id;

	private Map<String, List<String>> msgMap;

	public ErrorMsg() {
		this(new AtomicInteger(0));
	}

	public ErrorMsg(String msg) {
		this(new AtomicInteger(0), msg);
	}

	public ErrorMsg(int id, String msg) {
		this(new AtomicInteger(0), msg);
		this.add(id, msg);
	}

	public ErrorMsg(String key, String msg) {
		this(new AtomicInteger(0), msg);
		this.add(key, msg);
	}

	private ErrorMsg(AtomicInteger atomicInteger) {
		id = atomicInteger;
		msgMap = Maps.newHashMap();
	}

	private ErrorMsg(AtomicInteger atomicInteger, String msg) {
		id = atomicInteger;
		msgMap = Maps.newHashMap();
		this.add(id.incrementAndGet(), msg);
	}

	public List<String> add(String msg) {
		return add(id.incrementAndGet(), msg);
	}

	public List<String> add(int id, String msg) {
		return add(String.valueOf(id), msg);
	}

	public List<String> add(String key, String msg) {
		List<String> msgList = this.getOrCreate(key);
		msgList.add(msg);
		return msgList;
	}

	/**
	 * @param key
	 * @return
	 */
	private List<String> getOrCreate(String key) {
		List<String> list = this.msgMap.get(key);
		if (list == null) {
			list = new ArrayList<String>();
			this.msgMap.put(key, list);
		}

		return list;
	}

	public void addFormatMsg(String format, Object... msgs) {
		String msg = Strings.fillArgs(format, msgs);
		this.add(msg);
	}

	/**
	 * @return error message collection
	 */
	public Collection<String> getErrorMsgs() {
		List<String> result = new ArrayList<String>();
		this.msgMap.values().forEach(v -> {
			result.addAll(v);
		});
		return result;
	}

	/**
	 * @return error message map
	 */
	public Map<String, List<String>> getErrorMsgMap() {
		return this.msgMap;
	}

	public boolean hasError() {
		return this.msgMap.size() > 0;
	}

}
