package adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {

    private static final DateTimeFormatter dtf = DateTimeFormatter.ISO_DATE_TIME;

    @Override
    public void write(JsonWriter jsonWriter, LocalDateTime value) throws IOException {
        if (value == null) {
            jsonWriter.nullValue();
        } else {
            jsonWriter.value(value.format(dtf));
        }
    }

    @Override
    public LocalDateTime read(JsonReader jsonReader) throws IOException {
        String dateTimeStr = jsonReader.nextString();
        if (dateTimeStr.isEmpty()) {
            return null;
        }
        return LocalDateTime.parse(dateTimeStr, dtf);
    }
}