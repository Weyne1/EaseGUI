package net.weyne1.easegui.client.config;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class LowercaseEnumTypeAdapterFactory implements TypeAdapterFactory {
    @Override
    @SuppressWarnings("unchecked")
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        Class<? super T> rawType = type.getRawType();
        if (!Enum.class.isAssignableFrom(rawType) || rawType == Enum.class) {
            return null;
        }

        if (!rawType.isEnum()) {
            rawType = rawType.getSuperclass();
        }

        Map<String, Object> lowercaseToConstant = new HashMap<>();
        for (Object constant : rawType.getEnumConstants()) {
            lowercaseToConstant.put(((Enum<?>) constant).name().toLowerCase(Locale.ROOT), constant);
        }

        return new TypeAdapter<>() {
            @Override
            public void write(JsonWriter out, T value) throws IOException {
                if (value == null) {
                    out.nullValue();
                } else {
                    out.value(((Enum<?>) value).name().toLowerCase(Locale.ROOT));
                }
            }

            @Override
            public T read(JsonReader in) throws IOException {
                if (in.peek() == JsonToken.NULL) {
                    in.nextNull();
                    return null;
                }
                String str = in.nextString();
                return (T) lowercaseToConstant.get(str.toLowerCase(Locale.ROOT));
            }
        };
    }
}