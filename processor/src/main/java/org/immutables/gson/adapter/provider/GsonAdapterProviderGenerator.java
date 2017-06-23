package org.immutables.gson.adapter.provider;


import com.google.gson.TypeAdapterFactory;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;


public class GsonAdapterProviderGenerator {

  private final Config config;
  private final Filer filer;

  public GsonAdapterProviderGenerator(Config config, Filer filer) {
    this.config = config;
    this.filer = filer;
  }

  public void generate(Collection<TypeElement> adapters) throws IOException {
    final String adaptersFieldName = "adapters";

    ParameterizedTypeName adaptersFieldType = ParameterizedTypeName.get(
        ClassName.get(List.class), ClassName.get(TypeAdapterFactory.class)
    );

    CodeBlock.Builder adaptersInitializer = CodeBlock.builder();
    if (adapters.isEmpty()) {
      adaptersInitializer.add("$T.emptyList()", ClassName.get(Collections.class));
    } else {
      adaptersInitializer.add("$[").add("$T.asList(", ClassName.get(Arrays.class)).add("\n");
      Iterator<TypeElement> adaptersIterator = adapters.iterator();
      int adaptersIteratorIndex = 0;
      while (adaptersIterator.hasNext()) {
        TypeElement adapterElement = adaptersIterator.next();
        adaptersInitializer.add("new $T()", ClassName.get(adapterElement));
        if (adaptersIteratorIndex < adapters.size() - 1) adaptersInitializer.add(",\n");
        adaptersIteratorIndex++;
      }
      adaptersInitializer.add("\n)").add("$]");
    }

    FieldSpec adaptersFieldSpec = FieldSpec
        .builder(adaptersFieldType, adaptersFieldName, Modifier.PRIVATE, Modifier.FINAL)
        .initializer(adaptersInitializer.build())
        .build();

    MethodSpec adaptersGetterSpec = MethodSpec.methodBuilder("getAdapters")
        .addModifiers(Modifier.PUBLIC)
        .addStatement("return $L", adaptersFieldName)
        .returns(adaptersFieldType)
        .build();

    TypeSpec generatingClass = TypeSpec.classBuilder(config.getAdaptersProviderName())
        .addJavadoc("Auto generated class to provide Collection of all auto-generated TypeAdapterFactory for Immutables.org's Gson package")
        .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
        .addField(adaptersFieldSpec)
        .addMethod(adaptersGetterSpec)
        .build();

    JavaFile
        .builder(config.getAdaptersProviderPackage(), generatingClass).build()
        .writeTo(filer);
  }

}
