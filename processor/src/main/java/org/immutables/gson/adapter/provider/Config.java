package org.immutables.gson.adapter.provider;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;

public class Config {

  private static final String PROVIDER_CLASS_OPTION = "org.immutables.gson.adapter.provider_name";
  private static final String PROVIDER_CLASS_DEFAULT = "ImmutablesGsonAdaptersProvider";
  private static final String PROVIDER_PACKAGE_OPTION = "org.immutables.gson.adapter.package";
  private static final String PROVIDER_PACKAGE_DEFAULT = "org.immutables.gson.adapter.util";

  private final Map<String, String> options;

  public Config(ProcessingEnvironment env) {
    options = env.getOptions();
  }

  public String getAdaptersProviderName() {
    String value = options.get(PROVIDER_CLASS_OPTION);
    return value == null ? PROVIDER_CLASS_DEFAULT : value;
  }

  public String getAdaptersProviderPackage() {
    String value = options.get(PROVIDER_PACKAGE_OPTION);
    return value == null ? PROVIDER_PACKAGE_DEFAULT : value;
  }

  public static Set<String> supportedOptions() {
    return new HashSet<String>(Arrays.asList(
        PROVIDER_CLASS_OPTION, PROVIDER_PACKAGE_OPTION)
    );
  }
}
