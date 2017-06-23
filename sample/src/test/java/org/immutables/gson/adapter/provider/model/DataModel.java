package org.immutables.gson.adapter.provider.model;

import com.google.gson.annotations.SerializedName;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Value.Immutable
@Gson.TypeAdapters
public interface DataModel {
  @SerializedName("string_key1")
  @Value.Parameter
  String key1();

  @SerializedName("string_key2")
  @Value.Parameter
  String key2();
}
