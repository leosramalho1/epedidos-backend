package br.com.inovasoft.epedidos;

import javax.ws.rs.core.Application;

import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.info.License;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.openapi.annotations.Components;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;

@OpenAPIDefinition(tags = {
        @Tag(name = "User", description = "User operations.") }, info = @Info(title = "E-Pedidos Service", version = "1.0.0", contact = @Contact(name = "Inovasoft", url = "http://www.inovasoft.com.br", email = "faleconosco@inovasoft.com.br"), license = @License(name = "Apache 2.0", url = "http://www.apache.org/licenses/LICENSE-2.0.html")), components = @Components(securitySchemes = {
                @SecurityScheme(securitySchemeName = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT") }), security = {
                        @SecurityRequirement(name = "bearerAuth") })
public class EPedidosApplication extends Application {

}