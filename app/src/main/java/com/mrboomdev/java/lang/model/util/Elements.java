package com.mrboomdev.java.lang.model.util;

import com.mrboomdev.java.lang.model.AnnotatedConstruct;
import com.mrboomdev.java.lang.model.element.AnnotationMirror;
import com.mrboomdev.java.lang.model.element.AnnotationValue;
import com.mrboomdev.java.lang.model.element.Element;
import com.mrboomdev.java.lang.model.element.ElementKind;
import com.mrboomdev.java.lang.model.element.ExecutableElement;
import com.mrboomdev.java.lang.model.element.ModuleElement;
import com.mrboomdev.java.lang.model.element.Name;
import com.mrboomdev.java.lang.model.element.PackageElement;
import com.mrboomdev.java.lang.model.element.RecordComponentElement;
import com.mrboomdev.java.lang.model.element.TypeElement;

import java.io.Writer;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public interface Elements {
	PackageElement getPackageElement(CharSequence var1);

	default PackageElement getPackageElement(ModuleElement module, CharSequence name) {
		return null;
	}

	default Set<? extends PackageElement> getAllPackageElements(CharSequence name) {
		Set<? extends ModuleElement> modules = this.getAllModuleElements();
		if(modules.isEmpty()) {
			PackageElement packageElt = this.getPackageElement(name);
			return packageElt != null ? Collections.singleton(packageElt) : Collections.emptySet();
		} else {
			Set<PackageElement> result = new LinkedHashSet<>(1);

			for(ModuleElement module : modules) {
				PackageElement packageElt = this.getPackageElement(module, name);
				if(packageElt != null) {
					result.add(packageElt);
				}
			}

			return Collections.unmodifiableSet(result);
		}
	}

	TypeElement getTypeElement(CharSequence var1);

	default TypeElement getTypeElement(ModuleElement module, CharSequence name) {
		return null;
	}

	default Set<? extends TypeElement> getAllTypeElements(CharSequence name) {
		Set<? extends ModuleElement> modules = this.getAllModuleElements();
		if(modules.isEmpty()) {
			TypeElement typeElt = this.getTypeElement(name);
			return typeElt != null ? Collections.singleton(typeElt) : Collections.emptySet();
		} else {
			Set<TypeElement> result = new LinkedHashSet<>(1);

			for(ModuleElement module : modules) {
				TypeElement typeElt = this.getTypeElement(module, name);
				if(typeElt != null) {
					result.add(typeElt);
				}
			}

			return Collections.unmodifiableSet(result);
		}
	}

	default ModuleElement getModuleElement(CharSequence name) {
		return null;
	}

	default Set<? extends ModuleElement> getAllModuleElements() {
		return Collections.emptySet();
	}

	Map<? extends ExecutableElement, ? extends AnnotationValue> getElementValuesWithDefaults(AnnotationMirror var1);

	String getDocComment(Element var1);

	boolean isDeprecated(Element var1);

	default Origin getOrigin(Element e) {
		return Elements.Origin.EXPLICIT;
	}

	default Origin getOrigin(AnnotatedConstruct c, AnnotationMirror a) {
		return Elements.Origin.EXPLICIT;
	}

	default Origin getOrigin(ModuleElement m, ModuleElement.Directive directive) {
		return Elements.Origin.EXPLICIT;
	}

	default boolean isBridge(ExecutableElement e) {
		return false;
	}

	Name getBinaryName(TypeElement var1);

	PackageElement getPackageOf(Element var1);

	default ModuleElement getModuleOf(Element e) {
		return null;
	}

	List<? extends Element> getAllMembers(TypeElement var1);

	List<? extends AnnotationMirror> getAllAnnotationMirrors(Element var1);

	boolean hides(Element var1, Element var2);

	boolean overrides(ExecutableElement var1, ExecutableElement var2, TypeElement var3);

	String getConstantExpression(Object var1);

	void printElements(Writer var1, Element... var2);

	Name getName(CharSequence var1);

	boolean isFunctionalInterface(TypeElement var1);

	default boolean isAutomaticModule(ModuleElement module) {
		return false;
	}

	default RecordComponentElement recordComponentFor(ExecutableElement accessor) {
		if(accessor.getEnclosingElement().getKind() == ElementKind.RECORD) {
			Iterator var2 = ElementFilter.recordComponentsIn(accessor.getEnclosingElement().getEnclosedElements()).iterator();

			while(var2.hasNext()) {
				RecordComponentElement rec = (RecordComponentElement) var2.next();
				if(Objects.equals(rec.getAccessor(), accessor)) {
					return rec;
				}
			}
		}

		return null;
	}

	enum Origin {
		EXPLICIT,
		MANDATED,
		SYNTHETIC;

		Origin() {
		}

		public boolean isDeclared() {
			return this != SYNTHETIC;
		}
	}
}