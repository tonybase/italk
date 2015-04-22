package iqq.app.util.gson;

import com.google.gson.*;
import iqq.app.util.date.DateTimeZone;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Type;


/**
 * 日期格式化 yyyy-MM-dd
 * <p/>
 * Created by Tony on 1/5/15.
 */
public class DateTimeZoneFormatter implements JsonDeserializer<DateTimeZone>, JsonSerializer<DateTimeZone> {
    private static final String TAG = DateTimeZoneFormatter.class.getName();

    /**
     * string to date
     *
     * @param json
     * @param typeOfT
     * @param context
     * @return
     * @throws JsonParseException
     */
    public DateTimeZone deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {

        String value = json.getAsString();
        if (StringUtils.isEmpty(value) || value.length() == 1) {
            return null;
        }

        return DateTimeZone.parseFor(value);
    }

    /**
     * date to string
     *
     * @param date
     * @param type
     * @param context
     * @return
     */
    public JsonElement serialize(DateTimeZone date, Type type, JsonSerializationContext context) {
        return new JsonPrimitive(DateTimeZone.formatFor(date));
    }
}
