/**
 * 
 */
package cjlite.utils;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;

/**
 * @author YunYang
 * @version Mar 21, 2017
 */
@SuppressWarnings("rawtypes")
public class ReflectUtil {

	/**
	 * Shortcut for <code>getComponentType(type.getGenericSuperclass())</code>.
	 *
	 * @see #getComponentType(java.lang.reflect.Type, int)
	 */

	public static Class getGenericSupertype(Class type, int index) {
		return getComponentType(type.getGenericSuperclass(), index);
	}

	/**
	 * Returns single component type. Index is used when type consist of many
	 * components. If negative, index will be calculated from the end of the
	 * returned array. Returns <code>null</code> if component type does not
	 * exist or if index is out of bounds.
	 *
	 * @see #getComponentTypes(java.lang.reflect.Type)
	 */
	public static Class getComponentType(Type type, int index) {
		return getComponentType(type, null, index);
	}

	/**
	 * Returns single component type for given type and implementation. Index is
	 * used when type consist of many components. If negative, index will be
	 * calculated from the end of the returned array. Returns <code>null</code>
	 * if component type does not exist or if index is out of bounds.
	 * <p>
	 *
	 * @see #getComponentTypes(java.lang.reflect.Type, Class)
	 */
	public static Class getComponentType(Type type, Class implClass, int index) {
		Class[] componentTypes = getComponentTypes(type, implClass);
		if (componentTypes == null) {
			return null;
		}

		if (index < 0) {
			index += componentTypes.length;
		}

		if (index >= componentTypes.length) {
			return null;
		}

		return componentTypes[index];
	}

	/**
	 * Shortcut for <code>getComponentTypes(type.getGenericSuperclass())</code>.
	 *
	 * @see #getComponentTypes(java.lang.reflect.Type)
	 */
	public static Class[] getGenericSupertypes(Class type) {
		return getComponentTypes(type.getGenericSuperclass());
	}

	/**
	 * @see #getComponentTypes(java.lang.reflect.Type, Class)
	 */
	public static Class[] getComponentTypes(Type type) {
		return getComponentTypes(type, null);
	}

	/**
	 * Returns all component types of the given type. For example the following
	 * types all have the component-type MyClass:
	 * <ul>
	 * <li>MyClass[]</li>
	 * <li>List&lt;MyClass&gt;</li>
	 * <li>Foo&lt;? extends MyClass&gt;</li>
	 * <li>Bar&lt;? super MyClass&gt;</li>
	 * <li>&lt;T extends MyClass&gt; T[]</li>
	 * </ul>
	 */
	public static Class[] getComponentTypes(Type type, Class implClass) {
		if (type instanceof Class) {
			Class clazz = (Class) type;
			if (clazz.isArray()) {
				return new Class[] { clazz.getComponentType() };
			}
		} else if (type instanceof ParameterizedType) {
			ParameterizedType pt = (ParameterizedType) type;

			Type[] generics = pt.getActualTypeArguments();

			if (generics.length == 0) {
				return null;
			}

			Class[] types = new Class[generics.length];

			for (int i = 0; i < generics.length; i++) {
				types[i] = getRawType(generics[i], implClass);
			}
			return types;
		} else if (type instanceof GenericArrayType) {
			GenericArrayType gat = (GenericArrayType) type;

			Class rawType = getRawType(gat.getGenericComponentType(), implClass);
			if (rawType == null) {
				return null;
			}

			return new Class[] { rawType };
		}
		return null;
	}

	/**
	 * Returns raw class for given <code>type</code> when implementation class
	 * is known and it makes difference.
	 * 
	 * @see #resolveVariable(java.lang.reflect.TypeVariable, Class)
	 */
	public static Class<?> getRawType(Type type, Class implClass) {
		if (type instanceof Class) {
			return (Class) type;
		}
		if (type instanceof ParameterizedType) {
			ParameterizedType pType = (ParameterizedType) type;
			return getRawType(pType.getRawType(), implClass);
		}
		if (type instanceof WildcardType) {
			WildcardType wType = (WildcardType) type;

			Type[] lowerTypes = wType.getLowerBounds();
			if (lowerTypes.length > 0) {
				return getRawType(lowerTypes[0], implClass);
			}

			Type[] upperTypes = wType.getUpperBounds();
			if (upperTypes.length != 0) {
				return getRawType(upperTypes[0], implClass);
			}

			return Object.class;
		}
		if (type instanceof GenericArrayType) {
			Type genericComponentType = ((GenericArrayType) type).getGenericComponentType();
			Class<?> rawType = getRawType(genericComponentType, implClass);
			// this is sort of stupid, but there seems no other way (consider
			// don't creating new instances each time)...
			return Array.newInstance(rawType, 0).getClass();
		}
		if (type instanceof TypeVariable) {
			TypeVariable<?> varType = (TypeVariable<?>) type;
			if (implClass != null) {
				Type resolvedType = resolveVariable(varType, implClass);
				if (resolvedType != null) {
					return getRawType(resolvedType, null);
				}
			}
			Type[] boundsTypes = varType.getBounds();
			if (boundsTypes.length == 0) {
				return Object.class;
			}
			return getRawType(boundsTypes[0], implClass);
		}
		return null;
	}

	/**
	 * Resolves <code>TypeVariable</code> with given implementation class.
	 */
	public static Type resolveVariable(TypeVariable variable, final Class implClass) {
		final Class rawType = getRawType(implClass, null);

		int index = ArraysUtil.indexOf(rawType.getTypeParameters(), variable);
		if (index >= 0) {
			return variable;
		}

		final Class[] interfaces = rawType.getInterfaces();
		final Type[] genericInterfaces = rawType.getGenericInterfaces();

		for (int i = 0; i <= interfaces.length; i++) {
			Class rawInterface;

			if (i < interfaces.length) {
				rawInterface = interfaces[i];
			} else {
				rawInterface = rawType.getSuperclass();
				if (rawInterface == null) {
					continue;
				}
			}

			final Type resolved = resolveVariable(variable, rawInterface);
			if (resolved instanceof Class || resolved instanceof ParameterizedType) {
				return resolved;
			}

			if (resolved instanceof TypeVariable) {
				final TypeVariable typeVariable = (TypeVariable) resolved;
				index = ArraysUtil.indexOf(rawInterface.getTypeParameters(), typeVariable);

				if (index < 0) {
					throw new IllegalArgumentException("Invalid type variable:" + typeVariable);
				}

				final Type type = i < genericInterfaces.length ? genericInterfaces[i] : rawType.getGenericSuperclass();

				if (type instanceof Class) {
					return Object.class;
				}

				if (type instanceof ParameterizedType) {
					return ((ParameterizedType) type).getActualTypeArguments()[index];
				}

				throw new IllegalArgumentException("Unsupported type: " + type);
			}
		}
		return null;
	}
}
