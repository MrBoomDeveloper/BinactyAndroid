package com.mrboomdev.java.lang.model.element;

import com.mrboomdev.java.lang.model.type.TypeMirror;

import java.util.List;

public interface ModuleElement extends Element, QualifiedNameable {
	TypeMirror asType();

	Name getQualifiedName();

	Name getSimpleName();

	List<? extends Element> getEnclosedElements();

	boolean isOpen();

	boolean isUnnamed();

	Element getEnclosingElement();

	List<? extends Directive> getDirectives();

	interface UsesDirective extends Directive {
		TypeElement getService();
	}

	interface ProvidesDirective extends Directive {
		TypeElement getService();

		List<? extends TypeElement> getImplementations();
	}

	interface OpensDirective extends Directive {
		PackageElement getPackage();

		List<? extends ModuleElement> getTargetModules();
	}

	interface ExportsDirective extends Directive {
		PackageElement getPackage();

		List<? extends ModuleElement> getTargetModules();
	}

	interface RequiresDirective extends Directive {
		boolean isStatic();

		boolean isTransitive();

		ModuleElement getDependency();
	}

	interface DirectiveVisitor<R, P> {
		default R visit(Directive d) {
			return d.accept(this, null);
		}

		default R visit(Directive d, P p) {
			return d.accept(this, p);
		}

		R visitRequires(RequiresDirective var1, P var2);

		R visitExports(ExportsDirective var1, P var2);

		R visitOpens(OpensDirective var1, P var2);

		R visitUses(UsesDirective var1, P var2);

		R visitProvides(ProvidesDirective var1, P var2);

		default R visitUnknown(Directive d, P p) {
			throw new UnknownDirectiveException(d, p);
		}
	}

	interface Directive {
		DirectiveKind getKind();

		<R, P> R accept(DirectiveVisitor<R, P> var1, P var2);
	}

	enum DirectiveKind {
		REQUIRES,
		EXPORTS,
		OPENS,
		USES,
		PROVIDES;

		DirectiveKind() {}
	}
}