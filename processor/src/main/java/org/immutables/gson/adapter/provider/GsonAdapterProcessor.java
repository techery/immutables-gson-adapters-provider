package org.immutables.gson.adapter.provider;

import com.google.auto.service.AutoService;
import com.google.gson.TypeAdapterFactory;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Generated;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;


@AutoService(Processor.class)
public class GsonAdapterProcessor extends AbstractProcessor {

  private Types typeUtils;
  private Elements elementUtils;
  private Messager messager;

  private GsonAdapterProviderGenerator generator;
  private boolean processed;

  @Override
  public synchronized void init(ProcessingEnvironment processingEnv) {
    super.init(processingEnv);
    typeUtils = processingEnv.getTypeUtils();
    elementUtils = processingEnv.getElementUtils();
    messager = processingEnv.getMessager();
    generator = new GsonAdapterProviderGenerator(new Config(processingEnv), processingEnv.getFiler());
  }

  @Override public Set<String> getSupportedAnnotationTypes() {
    return Collections.singleton(Generated.class.getCanonicalName());
  }

  @Override public Set<String> getSupportedOptions() {
    return Config.supportedOptions();
  }

  @Override public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latestSupported();
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    if (processed) return false;
    else processed = true;

    Set<TypeElement> adapters = new HashSet<TypeElement>();

    Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(Generated.class);
    for (Element annotatedElement : annotatedElements) {
      if (annotatedElement.getKind() != ElementKind.CLASS) continue;

      TypeElement typeElement = (TypeElement) annotatedElement;
      if (implementsTypedAdapter(typeElement)) adapters.add(typeElement);
    }

    try {
      generator.generate(adapters);
    } catch (Throwable throwable) {
      messager.printMessage(Diagnostic.Kind.ERROR, throwable.getMessage());
    }

    boolean allElementsConsumed = annotatedElements.size() == adapters.size();
    return allElementsConsumed;
  }

  private boolean implementsTypedAdapter(TypeElement typeElement) {
    TypeElement desiredInterface = elementUtils.getTypeElement(TypeAdapterFactory.class.getCanonicalName());
    return typeUtils.isAssignable(typeElement.asType(), desiredInterface.asType());
  }
}
