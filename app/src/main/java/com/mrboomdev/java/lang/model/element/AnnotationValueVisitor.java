package com.mrboomdev.java.lang.model.element;

import com.mrboomdev.java.lang.model.type.TypeMirror;

import java.util.List;

public interface AnnotationValueVisitor<R, P> {
	R visit(AnnotationValue var1, P var2);

	default R visit(AnnotationValue av) {
		return this.visit(av, null);
	}

	R visitBoolean(boolean var1, P var2);

	R visitByte(byte var1, P var2);

	R visitChar(char var1, P var2);

	R visitDouble(double var1, P var3);

	R visitFloat(float var1, P var2);

	R visitInt(int var1, P var2);

	R visitLong(long var1, P var3);

	R visitShort(short var1, P var2);

	R visitString(String var1, P var2);

	R visitType(TypeMirror var1, P var2);

	R visitEnumConstant(VariableElement var1, P var2);

	R visitAnnotation(AnnotationMirror var1, P var2);

	R visitArray(List<? extends AnnotationValue> var1, P var2);

	R visitUnknown(AnnotationValue var1, P var2);
}