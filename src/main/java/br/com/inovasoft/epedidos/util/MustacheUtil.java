package br.com.inovasoft.epedidos.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.enterprise.context.ApplicationScoped;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@ApplicationScoped
public class MustacheUtil {

	private final JsonUtil jsonUtil;

	private final DefaultMustacheFactory mustacheFactory;

	private final TypeReference<HashMap<String, Object>> typeReference = new TypeReference<>() {
	};

	public MustacheUtil() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		objectMapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);
		objectMapper.disable(MapperFeature.USE_ANNOTATIONS);

		this.jsonUtil = new JsonUtil(objectMapper, new JSONParser());
		this.mustacheFactory = new DefaultMustacheFactory();
	}

	/**
	 * Preenche os atributos das mensagens no metadada utilizando os valores dos campos preenchidos no data
	 * utilizando o Mustache.<br/><br/>
	 * <p>
	 * - <a href="https://mustache.github.io/mustache.5.html">Documentação Mustache</a>
	 *
	 * @param metadata
	 * @param s3Metadata
	 * @return
	 * @throws IOException
	 */
	public JSONObject preencheMetadataAtributosCampos(Object metadata, String s3Metadata) {
		try {

			if (StringUtils.isBlank(s3Metadata)) {
				return new JSONObject();
			}

			HashMap<String, Object> scopes = new HashMap<>();

			if (metadata != null) {
				String dataJson = jsonUtil.translateJavaToJson(metadata);
				scopes = (HashMap<String, Object>) jsonUtil.translateJsonToJavaMap(dataJson, typeReference);
			}

			String writer = preencheAtributosMustache(s3Metadata, scopes);

			return jsonUtil.translateStringToJSONObject(writer);

		} catch (Exception e) {
			log.error("Não foi possível prencher os atributos do metadata {}", metadata, e);
		}

		return new JSONObject();
	}

	@SneakyThrows
	public String preencheAtributosMustache(String target, Object source) {
		String dataJson = jsonUtil.translateJavaToJson(source);
		return preencheAtributosMustache(target, jsonUtil.translateJsonToJavaMap(dataJson, typeReference));
	}

	public String preencheAtributosMustache(String s3Metadata, Map<String, Object> scopes) {
		StringWriter writer = new StringWriter();
		StringReader metadataReader = new StringReader(s3Metadata);
		Mustache mustache = mustacheFactory.compile(metadataReader, "report");
		mustache.execute(writer, scopes);
		writer.flush();
		return writer.toString();
	}
}
