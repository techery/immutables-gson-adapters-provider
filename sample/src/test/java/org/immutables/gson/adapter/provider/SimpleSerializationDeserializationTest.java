package org.immutables.gson.adapter.provider;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapterFactory;

import org.immutables.gson.adapter.provider.model.DataModel;
import org.immutables.gson.adapter.provider.model.ImmutableDataModel;
import org.immutables.gson.adapter.provider.sample.util.ImmutablesGsonAdaptersProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SimpleSerializationDeserializationTest {

  @Test void serializationAndDeserializationSucceeds() {
    GsonBuilder gsonBuilder = new GsonBuilder();
    for (TypeAdapterFactory typeAdapterFactory : new ImmutablesGsonAdaptersProvider().getAdapters()) {
      gsonBuilder.registerTypeAdapterFactory(typeAdapterFactory);
    }
    Gson gson = gsonBuilder.create();

    DataModel dataModel = ImmutableDataModel.of("someKey1", "someKey2");
    String dataModelJson = gson.toJson(dataModel);
    DataModel dataModelFromJson = gson.fromJson(dataModelJson, DataModel.class);

    Assertions.assertEquals(dataModel, dataModelFromJson);
  }

  @Test void serializationAndDeserializationFails() {
    Gson gson = new GsonBuilder().create();

    DataModel dataModel = ImmutableDataModel.of("someKey1", "someKey2");
    String dataModelJson = gson.toJson(dataModel);
    Assertions.assertThrows(RuntimeException.class, () -> {
      DataModel dataModelFromJson = gson.fromJson(dataModelJson, DataModel.class);
    });
  }
}

