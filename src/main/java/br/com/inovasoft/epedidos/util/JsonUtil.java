package br.com.inovasoft.epedidos.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONAware;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.enterprise.context.ApplicationScoped;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
@RequiredArgsConstructor
public class JsonUtil {

	private final ObjectMapper objectMapper;

	private final JSONParser jsonParser;

	public <T> T translateJsonToJava(@NonNull String json, @NonNull Class<T> classe) throws IOException {
		return objectMapper.readValue(json, classe);
	}

	public <T> List<T> translateJsonToJavaList(@NonNull String json, @NonNull TypeReference<List<T>> typeReference) throws IOException {
		return objectMapper.readValue(json, typeReference);
	}

	public <T, O> Map<T, O> translateJsonToJavaMap(@NonNull String json, @NonNull TypeReference<HashMap<T, O>> typeReference) throws IOException {
		return objectMapper.readValue(json, typeReference);
	}

	public <T> String translateJavaToJson(@NonNull T object) throws JsonProcessingException {
		return objectMapper.writeValueAsString(object);
	}

	public synchronized JSONObject translateStringToJSONObject(@NonNull String object) throws ParseException {
		return (JSONObject) jsonParser.parse(object);
	}

	public synchronized JSONAware translateJavaToJSONObject(@NonNull Object object) throws ParseException, JsonProcessingException {
		return (JSONAware) jsonParser.parse(translateJavaToJson(object));
	}

}
