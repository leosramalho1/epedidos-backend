package br.com.inovasoft.epedidos.models.dtos.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import javax.ws.rs.Produces;
import java.io.IOException;
import java.math.BigDecimal;

public class MoneySerializer extends StdSerializer<BigDecimal> {

    protected MoneySerializer() {
        this(null);
    }

    protected MoneySerializer(Class<BigDecimal> t) {
        super(t);
    }

    @Override
    public void serialize(BigDecimal value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        if(value == null) {
            jgen.writeNull();
        } else {
            jgen.writeString(value.toString());
        }
    }

    @Produces
    public SimpleModule moneyModule(){
        Version version = new Version(1, 0, 0, null);
        SimpleModule simpleModule = new SimpleModule("MoneySerializer", version);
        simpleModule.addSerializer(new MoneySerializer());
        return simpleModule;
    }
}