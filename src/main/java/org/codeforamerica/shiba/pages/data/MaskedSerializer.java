package org.codeforamerica.shiba.pages.data;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class MaskedSerializer extends StdSerializer<PageData> {
    public MaskedSerializer() { super(PageData.class); }

    @Override
    public void serialize(PageData pageData, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        pageData.keySet().forEach(inputName -> {
            try {
                gen.writeStringField(inputName, pageData.get(inputName).getValue().isEmpty() ? "" : "filled");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        gen.writeEndObject();
    }
}
