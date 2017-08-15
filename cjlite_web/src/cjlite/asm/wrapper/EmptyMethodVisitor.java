package cjlite.asm.wrapper;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public abstract class EmptyMethodVisitor extends MethodVisitor {

	public EmptyMethodVisitor(MethodVisitor _mv) {
		super(Opcodes.ASM5, _mv);
	}

}