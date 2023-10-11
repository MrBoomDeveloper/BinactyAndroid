package com.mrboomdev.java.lang.model.util;

import com.mrboomdev.java.lang.model.element.Element;
import com.mrboomdev.java.lang.model.element.ElementKind;
import com.mrboomdev.java.lang.model.element.ExecutableElement;
import com.mrboomdev.java.lang.model.element.ModuleElement;
import com.mrboomdev.java.lang.model.element.PackageElement;
import com.mrboomdev.java.lang.model.element.RecordComponentElement;
import com.mrboomdev.java.lang.model.element.TypeElement;
import com.mrboomdev.java.lang.model.element.VariableElement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class ElementFilter {
	private static final Set<ElementKind> CONSTRUCTOR_KIND;
	private static final Set<ElementKind> FIELD_KINDS;
	private static final Set<ElementKind> METHOD_KIND;
	private static final Set<ElementKind> PACKAGE_KIND;
	private static final Set<ElementKind> MODULE_KIND;
	private static final Set<ElementKind> TYPE_KINDS;
	private static final Set<ElementKind> RECORD_COMPONENT_KIND;

	private ElementFilter() {
	}

	public static List<VariableElement> fieldsIn(Iterable<? extends Element> elements) {
		return listFilter(elements, FIELD_KINDS, VariableElement.class);
	}

	public static Set<VariableElement> fieldsIn(Set<? extends Element> elements) {
		return setFilter(elements, FIELD_KINDS, VariableElement.class);
	}

	public static List<RecordComponentElement> recordComponentsIn(Iterable<? extends Element> elements) {
		return listFilter(elements, RECORD_COMPONENT_KIND, RecordComponentElement.class);
	}

	public static Set<RecordComponentElement> recordComponentsIn(Set<? extends Element> elements) {
		return setFilter(elements, RECORD_COMPONENT_KIND, RecordComponentElement.class);
	}

	public static List<ExecutableElement> constructorsIn(Iterable<? extends Element> elements) {
		return listFilter(elements, CONSTRUCTOR_KIND, ExecutableElement.class);
	}

	public static Set<ExecutableElement> constructorsIn(Set<? extends Element> elements) {
		return setFilter(elements, CONSTRUCTOR_KIND, ExecutableElement.class);
	}

	public static List<ExecutableElement> methodsIn(Iterable<? extends Element> elements) {
		return listFilter(elements, METHOD_KIND, ExecutableElement.class);
	}

	public static Set<ExecutableElement> methodsIn(Set<? extends Element> elements) {
		return setFilter(elements, METHOD_KIND, ExecutableElement.class);
	}

	public static List<TypeElement> typesIn(Iterable<? extends Element> elements) {
		return listFilter(elements, TYPE_KINDS, TypeElement.class);
	}

	public static Set<TypeElement> typesIn(Set<? extends Element> elements) {
		return setFilter(elements, TYPE_KINDS, TypeElement.class);
	}

	public static List<PackageElement> packagesIn(Iterable<? extends Element> elements) {
		return listFilter(elements, PACKAGE_KIND, PackageElement.class);
	}

	public static Set<PackageElement> packagesIn(Set<? extends Element> elements) {
		return setFilter(elements, PACKAGE_KIND, PackageElement.class);
	}

	public static List<ModuleElement> modulesIn(Iterable<? extends Element> elements) {
		return listFilter(elements, MODULE_KIND, ModuleElement.class);
	}

	public static Set<ModuleElement> modulesIn(Set<? extends Element> elements) {
		return setFilter(elements, MODULE_KIND, ModuleElement.class);
	}

	private static <E extends Element> List<E> listFilter(Iterable<? extends Element> elements, Set<ElementKind> targetKinds, Class<E> clazz) {
		List<E> list = new ArrayList<>();

		for(Element e : elements) {
			if(targetKinds.contains(e.getKind())) {
				list.add((E) clazz.cast(e));
			}
		}

		return list;
	}

	private static <E extends Element> Set<E> setFilter(Set<? extends Element> elements, Set<ElementKind> targetKinds, Class<E> clazz) {
		Set<E> set = new LinkedHashSet<>();

		for(Element e : elements) {
			if(targetKinds.contains(e.getKind())) {
				set.add((E) clazz.cast(e));
			}
		}

		return set;
	}

	public static List<ModuleElement.ExportsDirective> exportsIn(Iterable<? extends ModuleElement.Directive> directives) {
		return listFilter(directives, ModuleElement.DirectiveKind.EXPORTS, ModuleElement.ExportsDirective.class);
	}

	public static List<ModuleElement.OpensDirective> opensIn(Iterable<? extends ModuleElement.Directive> directives) {
		return listFilter(directives, ModuleElement.DirectiveKind.OPENS, ModuleElement.OpensDirective.class);
	}

	public static List<ModuleElement.ProvidesDirective> providesIn(Iterable<? extends ModuleElement.Directive> directives) {
		return listFilter(directives, ModuleElement.DirectiveKind.PROVIDES, ModuleElement.ProvidesDirective.class);
	}

	public static List<ModuleElement.RequiresDirective> requiresIn(Iterable<? extends ModuleElement.Directive> directives) {
		return listFilter(directives, ModuleElement.DirectiveKind.REQUIRES, ModuleElement.RequiresDirective.class);
	}

	public static List<ModuleElement.UsesDirective> usesIn(Iterable<? extends ModuleElement.Directive> directives) {
		return listFilter(directives, ModuleElement.DirectiveKind.USES, ModuleElement.UsesDirective.class);
	}

	private static <D extends ModuleElement.Directive> List<D> listFilter(Iterable<? extends ModuleElement.Directive> directives, ModuleElement.DirectiveKind directiveKind, Class<D> clazz) {
		List<D> list = new ArrayList<>();

		for(ModuleElement.Directive d : directives) {
			if(d.getKind() == directiveKind) {
				list.add((D) clazz.cast(d));
			}
		}

		return list;
	}

	static {
		CONSTRUCTOR_KIND = Collections.unmodifiableSet(EnumSet.of(ElementKind.CONSTRUCTOR));
		FIELD_KINDS = Collections.unmodifiableSet(EnumSet.of(ElementKind.FIELD, ElementKind.ENUM_CONSTANT));
		METHOD_KIND = Collections.unmodifiableSet(EnumSet.of(ElementKind.METHOD));
		PACKAGE_KIND = Collections.unmodifiableSet(EnumSet.of(ElementKind.PACKAGE));
		MODULE_KIND = Collections.unmodifiableSet(EnumSet.of(ElementKind.MODULE));
		TYPE_KINDS = Collections.unmodifiableSet(EnumSet.of(ElementKind.CLASS, ElementKind.ENUM, ElementKind.INTERFACE, ElementKind.RECORD, ElementKind.ANNOTATION_TYPE));
		RECORD_COMPONENT_KIND = Set.of(ElementKind.RECORD_COMPONENT);
	}
}