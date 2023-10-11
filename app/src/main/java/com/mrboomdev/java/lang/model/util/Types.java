package com.mrboomdev.java.lang.model.util;

import com.mrboomdev.java.lang.model.element.Element;
import com.mrboomdev.java.lang.model.element.TypeElement;
import com.mrboomdev.java.lang.model.type.ArrayType;
import com.mrboomdev.java.lang.model.type.DeclaredType;
import com.mrboomdev.java.lang.model.type.ExecutableType;
import com.mrboomdev.java.lang.model.type.NoType;
import com.mrboomdev.java.lang.model.type.NullType;
import com.mrboomdev.java.lang.model.type.PrimitiveType;
import com.mrboomdev.java.lang.model.type.TypeKind;
import com.mrboomdev.java.lang.model.type.TypeMirror;
import com.mrboomdev.java.lang.model.type.WildcardType;

import java.util.List;

public interface Types {
	Element asElement(TypeMirror var1);

	boolean isSameType(TypeMirror var1, TypeMirror var2);

	boolean isSubtype(TypeMirror var1, TypeMirror var2);

	boolean isAssignable(TypeMirror var1, TypeMirror var2);

	boolean contains(TypeMirror var1, TypeMirror var2);

	boolean isSubsignature(ExecutableType var1, ExecutableType var2);

	List<? extends TypeMirror> directSupertypes(TypeMirror var1);

	TypeMirror erasure(TypeMirror var1);

	TypeElement boxedClass(PrimitiveType var1);

	PrimitiveType unboxedType(TypeMirror var1);

	TypeMirror capture(TypeMirror var1);

	PrimitiveType getPrimitiveType(TypeKind var1);

	NullType getNullType();

	NoType getNoType(TypeKind var1);

	ArrayType getArrayType(TypeMirror var1);

	WildcardType getWildcardType(TypeMirror var1, TypeMirror var2);

	DeclaredType getDeclaredType(TypeElement var1, TypeMirror... var2);

	DeclaredType getDeclaredType(DeclaredType var1, TypeElement var2, TypeMirror... var3);

	TypeMirror asMemberOf(DeclaredType var1, Element var2);
}