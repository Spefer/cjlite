package cjlite.web.handler;

/**
 * @author ming
 */
public class UriMatcher {

	public static final String UrlSplash = "/";
	public static final char LeftBrace = '{';
	public static final char RightBrace = '}';
	private final String path;
	private Segment[] segments;
	private boolean isStatic = true;


	/**
	 * @param path
	 */
	public UriMatcher(String _path) {
		this.path = _path;
		this.parse();
	}


	private void parse() {
		String cpath=this.path;
		if (cpath.startsWith(UrlSplash)) {
			cpath = cpath.substring(1);
		}
		String[] element = cpath.split(UrlSplash);
		segments = new Segment[element.length];

		for (int i = 0; i < element.length; i++) {
			segments[i] = new Segment(element[i]);
			if (segments[i].isParam) {
				this.isStatic = false;
			}
		}
	}

	class Segment {

		String string;
		boolean isParam;
		Chip[] chips;


		public Segment(String _string) {
			this.string = _string;
			this.parse();
		}


		private void parse() {
			char[] chars = this.string.toCharArray();

			int index = 0;
			char c;
			int lastEnd = 0;

			int chipCount = 0;
			Chip[] tem_chips = new Chip[chars.length / 2];
			Chip lastChip = null;
			char lastBrace = 0;
			int lastChipType = 0;// 0: start, 1: isVar; 2: un-isVar;
			// char lastChar = 0;
			while (index < chars.length) {
				c = chars[index];
				if (c == LeftBrace || c == RightBrace) {
					if (c == LeftBrace) {
						if (lastBrace == 0 || lastBrace == RightBrace) {
							if (index != 0) {
								if (lastEnd == index) {
									isParam = false;
									return;
								}

								if (lastChipType == 0 || lastChipType == 1) {
									lastChipType = 2;
								} else {
									isParam = false;
									return;
								}

								String chipString = this.string.substring(lastEnd, index);
								lastChip = new Chip(chipString, false);
								tem_chips[chipCount] = new Chip(chipString, false);
								chipCount += 1;
							}
						} else {
							String chipString;
							if (lastChip != null) {
								chipString = this.string.substring(lastEnd - lastChip.length() - 1, index);
							} else {
								chipString = this.string.substring(0, index);
							}
							tem_chips[chipCount - 1] = new Chip(chipString, false);
							lastChip = new Chip(chipString, false);
						}
						lastEnd = index + 1;
						lastBrace = LeftBrace;
					} else {
						if (lastBrace == LeftBrace) {
							if (lastEnd == index) {
								isParam = false;
								return;
							}
							if (lastChipType == 0 || lastChipType == 2) {
								lastChipType = 1;
							} else {
								isParam = false;
								return;
							}
							String chipString = this.string.substring(lastEnd, index);
							tem_chips[chipCount] = new Chip(chipString, true);
							lastChip = new Chip(chipString, true);
							chipCount += 1;
							lastEnd = index + 1;
							lastBrace = RightBrace;
						}
					}
				}
				index += 1;
			}

			if (lastEnd < index && chipCount > 0) {
				String chipString = this.string.substring(lastEnd, index);
				tem_chips[chipCount] = new Chip(chipString, false);
				chipCount += 1;
			}

			if (chipCount > 0) {
				isParam = true;
			} else {
				isParam = false;
				return;
			}

			chips = new Chip[chipCount];

			System.arraycopy(tem_chips, 0, chips, 0, chipCount);
		}


		public boolean match(String target, PathVariables pathVariables) {
			if (chips.length == 1 && chips[0].isVar) {
				pathVariables.addVar(chips[0].string, target);
				return true;
			}

			int lastPosi = 0;
			int chipPosi = 0;
			Chip lastChip = null;
			for (int i = 0; i < chips.length; i++) {
				if (i == 0) {
					if (chips[i].isVar) {
						lastChip = chips[i];
						continue;
					} else {
						chipPosi = target.indexOf(chips[i].string);
						if (chipPosi != 0) {
							return false;
						}
						lastChip = chips[i];
						lastPosi = chipPosi + chips[i].length();
					}
				} else if (i == chips.length - 1) {
					if (chips[i].isVar) {
						String value = target.substring(lastPosi);
						pathVariables.addVar(chips[i].string, value);
					} else {
						if (target.endsWith(chips[i].string)) {
							String value = target.substring(lastPosi, target.length() - chips[i].length());
							pathVariables.addVar(lastChip.string, value);
						} else {
							return false;
						}
					}

				} else {
					if (chips[i].isVar) {
						lastChip = chips[i];
						continue;
					} else {
						chipPosi = target.indexOf(chips[i].string);
						if (chipPosi - lastPosi < 1) {
							return false;
						}
						String value = target.substring(lastPosi, chipPosi);
						pathVariables.addVar(lastChip.string, value);

						lastPosi = chipPosi + chips[i].length();
						lastChip = chips[i];
					}
				}

			}
			return true;
		}
	}

	class Chip {

		String string;
		boolean isVar;
		int start = 0, end = 0;


		public Chip(String chipString, boolean b) {
			this.string = chipString;
			this.isVar = b;
		}


		int length() {
			return string == null ? 0 : string.length();
		}


		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("Chip [string=");
			builder.append(string);
			builder.append(", isVar=");
			builder.append(isVar);
			builder.append("]");
			return builder.toString();
		}

	}


	public boolean match(String requestUri, PathVariables pathVariables) {
		String target = requestUri;
		if (requestUri.startsWith(UrlSplash)) {
			target = requestUri.substring(1);
		}

		String[] element = target.split(UrlSplash);
		if (this.segments == null) {
			return false;
		}
		if (element.length != this.segments.length) {
			return false;
		}

		for (int i = 0; i < this.segments.length; i++) {
			if (!this.segments[i].isParam) {
				if (!this.segments[i].string.equalsIgnoreCase(element[i])) {
					return false;
				}
			} else {
				if (!this.segments[i].match(element[i], pathVariables)) {
					return false;
				}
			}
		}

		return true;
	}


	/**
	 * @return true if it is static Uri Path, otherwise return false;
	 */
	public boolean isStatic() {
		return isStatic;
	}
}
