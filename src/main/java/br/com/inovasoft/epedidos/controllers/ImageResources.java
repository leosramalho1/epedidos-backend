package br.com.inovasoft.epedidos.controllers;

import br.com.inovasoft.epedidos.models.dtos.FormDataDto;
import br.com.inovasoft.epedidos.security.jwt.JwtRoles;
import br.com.inovasoft.epedidos.services.ImageService;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("/images")
@Tag(name = "Image")
public class ImageResources {

    @Inject
    ImageService service;

    @POST
    @Transactional
    @RolesAllowed(JwtRoles.USER_BACKOFFICE)
    public Response upload(@MultipartForm FormDataDto formData) {
        /*
         * 
         * if (formData.getFileName() == null || formData.getFileName().isEmpty()) {
         * return Response.status(Response.Status.BAD_REQUEST).build(); }
         * 
         * if (formData.getMimeType() == null || formData.getMimeType().isEmpty()) {
         * return Response.status(Response.Status.BAD_REQUEST).build(); }
         * 
         */
        return Response.ok().status(Response.Status.CREATED).entity(service.saveFormData(formData)).build();

    }

}
