package malayalamdictionary.samasya.app.util;

import android.util.Log;

import androidx.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Singleton wrapper class which configures the Jackson JSON parser.
 */
public final class Mapper
{
    private static ObjectMapper MAPPER;

    public static ObjectMapper get()
    {
        if (MAPPER == null)
        {
            Log.i("mapper","null");
            MAPPER = new ObjectMapper();
            MAPPER.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
            MAPPER.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
            MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            MAPPER.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);

        }
        else{
            Log.i("mapper","not null");
        }

        return MAPPER;
    }

    @Nullable
    public static String string(Object data)
    {
        try
        {
            Log.i("mapper","string "+ get().writeValueAsString(data));
            return get().writeValueAsString(data);
        }
        catch (Exception e)
        {
            Log.i("mapper","string Exception");
            e.printStackTrace();
            return null;
        }
    }
    public static <T> T objectOrThrowFromInputStream(InputStream file, TypeReference<T> type) throws JsonParseException, JsonMappingException, IOException {
        return get().readValue(file, type);
    }


    public static <T> T objectOrThrowFromFile(File file, TypeReference<T> type) throws JsonParseException, JsonMappingException, IOException {
        return get().readValue(file, type);
    }

    public static <T> T objectOrThrow(String data, Class<T> type) throws JsonParseException, JsonMappingException, IOException {
        return get().readValue(data, type);
    }

    @Nullable
    public static <T> T object(String data, Class<T> type)
    {
        try
        {
            return objectOrThrow(data, type);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    @Nullable
    public static <T> T objectFromType(String data, TypeReference<T> type)
    {
        try
        {
            Log.i("mapper","objectFromType string: "+ data + "type: "+ type);
            return  get().readValue(data, type);
        }
        catch (Exception e) {
            Log.i("mapper"," objectFromType null");
            e.printStackTrace();
            return null;
        }
    }
}