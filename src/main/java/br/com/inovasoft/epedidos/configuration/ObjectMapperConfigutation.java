package br.com.inovasoft.epedidos.configuration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

@Provider
public class ObjectMapperConfigutation implements ContextResolver<ObjectMapper> {  

    private final ObjectMapper MAPPER;

    public ObjectMapperConfigutation() {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

        LocalDateTimeDeserializer dateTimeDeserializer = new LocalDateTimeDeserializer(formatter);
        LocalDateTimeSerializer dateTimeSerializer = new LocalDateTimeSerializer(formatter);
        
        JavaTimeModule javaTimeModule = new JavaTimeModule(); 
        javaTimeModule.addDeserializer(LocalDateTime.class, dateTimeDeserializer);
        javaTimeModule.addSerializer(LocalDateTime.class, dateTimeSerializer);
        
        MAPPER = new ObjectMapper();
        MAPPER.registerModule(javaTimeModule);
    }

    @Override
    public ObjectMapper getContext(Class<?> type) {
        return MAPPER;
    }  
}
