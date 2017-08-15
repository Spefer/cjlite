package cjlite.error;

public interface ErrorManager {

	void add(String code, Exception e);

	Exception getException(String code);

}
