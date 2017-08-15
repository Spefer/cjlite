package cjlite.web.helper;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import cjlite.asm.wrapper.EmptyClassVisitor;
import cjlite.asm.wrapper.EmptyMethodVisitor;
import cjlite.log.Logger;
import cjlite.utils.Classes;
import cjlite.web.mvc.MethodParameter;

/**
 * @author ming
 */
public class MethodParamParser {

	private static Logger logger = Logger.thisClass();
	private Class<?> clazz;

	private Map<Class<?>, Map<Member, String[]>> parameterNamesCache = new ConcurrentHashMap<Class<?>, Map<Member, String[]>>();

	private Map<Member, MethodParameter[]> methodParameterMap = new ConcurrentHashMap<Member, MethodParameter[]>();
	private Map<Member, String[]> methodParamNameMap = new ConcurrentHashMap<Member, String[]>();

	public MethodParamParser(Class<?> _clazz) {
		this.clazz = _clazz;
		this.inspectClass();
	}

	public MethodParameter[] getMethodParameters(Method method) {
		return this.methodParameterMap.get(method);
	}

	private void inspectClass() {
		InputStream is = clazz.getResourceAsStream(Classes.getClassFileName(clazz));
		if (is == null) {
			logger.debug(
					"Cannot find '.class' file for class [{0}] - unable to determine constructors/methods parameter names",
					clazz.getName());
		}

		try {
			ClassReader classReader = new ClassReader(is);
			classReader.accept(new ParameterNameVisitor(clazz, methodParamNameMap, methodParameterMap),
					ClassReader.EXPAND_FRAMES);
		} catch (IOException ex) {
			logger.debug(
					"Exception thrown while reading '.class' file for class [{0}] - unable to determine constructors/methods parameter names",
					ex, clazz.getName());
		} finally {
			try {
				is.close();
			} catch (IOException ex) {
			}
		}
	}

	/**
	 * Helper class that inspects all methods (constructor included) and then attempts to find the parameter names for
	 * that member.
	 */
	private static class ParameterNameVisitor extends EmptyClassVisitor {

		private static final String STATIC_CLASS_INIT = "<clinit>";

		private final Class<?> clazz;
		private final Map<Member, String[]> memberMap;
		private final Map<Member, MethodParameter[]> methodParameterMap;

		public ParameterNameVisitor(Class<?> clazz, Map<Member, String[]> memberMap,
				Map<Member, MethodParameter[]> _methodParameterMap) {
			this.clazz = clazz;
			this.memberMap = memberMap;
			this.methodParameterMap = _methodParameterMap;
		}

		@Override
		public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
			// exclude synthetic + bridged && static class initialization
			if (!isSyntheticOrBridged(access) && !STATIC_CLASS_INIT.equals(name)) {
				return new LocalVariableTableVisitor(clazz, memberMap, methodParameterMap, name, desc, isStatic(access));
			}
			return null;
		}

		private static boolean isSyntheticOrBridged(int access) {
			return (((access & Opcodes.ACC_SYNTHETIC) | (access & Opcodes.ACC_BRIDGE)) > 0);
		}

		private static boolean isStatic(int access) {
			return ((access & Opcodes.ACC_STATIC) > 0);
		}
	}

	/**
	 * @author ming
	 */
	private static class LocalVariableTableVisitor extends EmptyMethodVisitor {

		private static final String CONSTRUCTOR = "<init>";

		private final Class<?> clazz;
		private final Map<Member, String[]> memberMap;
		private final String name;
		private final Type[] args;
		private final boolean isStatic;

		private String[] parameterNames;
		private boolean hasLvtInfo = false;
		private final Map<Member, MethodParameter[]> methodParameterMap;
		/*
		 * The nth entry contains the slot index of the LVT table entry holding the argument name for the nth parameter.
		 */
		private final int[] lvtSlotIndex;

		public LocalVariableTableVisitor(Class<?> clazz, Map<Member, String[]> map,
				Map<Member, MethodParameter[]> _methodParameterMap, String name, String desc, boolean isStatic) {
			super(null);
			this.clazz = clazz;
			this.memberMap = map;
			this.name = name;
			this.methodParameterMap = _methodParameterMap;
			// determine args
			args = Type.getArgumentTypes(desc);
			this.parameterNames = new String[args.length];
			this.isStatic = isStatic;
			this.lvtSlotIndex = computeLvtSlotIndices(isStatic, args);

		}

		@Override
		public void visitLocalVariable(String name, String description, String signature, Label start, Label end,
				int index) {
			this.hasLvtInfo = true;
			for (int i = 0; i < lvtSlotIndex.length; i++) {
				if (lvtSlotIndex[i] == index) {
					this.parameterNames[i] = name;
				}
			}
		}

		@Override
		public void visitEnd() {
			if (this.hasLvtInfo || (this.isStatic && this.parameterNames.length == 0)) {

				MethodParameter[] mps = new MethodParameter[args.length];
				ClassLoader loader = clazz.getClassLoader();
				Class<?>[] classes = new Class<?>[args.length];

				// resolve args
				for (int i = 0; i < args.length; i++) {
					classes[i] = Classes.resolveClassName(args[i].getClassName(), loader);
					mps[i] = new MethodParameter(classes[i], this.parameterNames[i]);
				}
				Member member = resolveMember(classes);
				// visitLocalVariable will never be called for static no args methods
				// which doesn't use any local variables.
				// This means that hasLvtInfo could be false for that kind of methods
				// even if the class has local variable info.
				memberMap.put(member, parameterNames);
				methodParameterMap.put(member, mps);
			}
		}

		private Member resolveMember(Class<?>[] classes) {

			try {
				if (CONSTRUCTOR.equals(name)) {
					return clazz.getDeclaredConstructor(classes);
				}

				return clazz.getDeclaredMethod(name, classes);
			} catch (NoSuchMethodException ex) {
				throw new IllegalStateException("Method [" + name
						+ "] was discovered in the .class file but cannot be resolved in the class object", ex);
			}
		}

		private static int[] computeLvtSlotIndices(boolean isStatic, Type[] paramTypes) {
			int[] lvtIndex = new int[paramTypes.length];
			int nextIndex = (isStatic ? 0 : 1);
			for (int i = 0; i < paramTypes.length; i++) {
				lvtIndex[i] = nextIndex;
				if (isWideType(paramTypes[i])) {
					nextIndex += 2;
				} else {
					nextIndex++;
				}
			}
			return lvtIndex;
		}

		private static boolean isWideType(Type aType) {
			// float is not a wide type
			return (aType == Type.LONG_TYPE || aType == Type.DOUBLE_TYPE);
		}
	}

}
