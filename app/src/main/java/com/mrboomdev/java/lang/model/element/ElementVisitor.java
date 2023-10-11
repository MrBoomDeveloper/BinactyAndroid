package com.mrboomdev.java.lang.model.element;

public interface ElementVisitor<R, P> {
	R visit(Element var1, P var2);

	default R visit(Element e) {
		return this.visit(e, null);
	}

	R visitPackage(PackageElement var1, P var2);

	R visitType(TypeElement var1, P var2);

	R visitVariable(VariableElement var1, P var2);

	R visitExecutable(ExecutableElement var1, P var2);

	R visitTypeParameter(TypeParameterElement var1, P var2);

	R visitUnknown(Element var1, P var2);

	default R visitModule(ModuleElement e, P p) {
		return this.visitUnknown(e, p);
	}

	default R visitRecordComponent(RecordComponentElement e, P p) {
		return this.visitUnknown(e, p);
	}
}