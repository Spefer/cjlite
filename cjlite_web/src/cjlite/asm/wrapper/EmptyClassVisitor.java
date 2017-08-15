package cjlite.asm.wrapper;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;

public abstract class EmptyClassVisitor extends ClassVisitor {

	protected EmptyClassVisitor() {
		super(Opcodes.ASM5);
	}

}