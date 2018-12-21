/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.uga.miashs.sempic.restservices;

import fr.uga.miashs.sempic.SempicException;
import fr.uga.miashs.sempic.services.PhotoStorage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

/**
 *
 * @author Jerome David <jerome.david@univ-grenoble-alpes.fr>
 */
@ApplicationScoped
@Path("/picture/{albumId}/{pictureId}")
public class PhotoStore {

    @Context
    private ServletContext context;

    @Inject
    private PhotoStorage storage;

    @PostConstruct
    public void init() {
        /*String photoStorePath=context.getInitParameter("PhotoStorePath");
        String thumbnailStorePath=context.getInitParameter("ThumbnailStorePath");
        try {
            storage = new PhotoStorage(Paths.get(photoStorePath),Paths.get(thumbnailStorePath));
        } catch (IOException ex) {
            Logger.getLogger(PhotoStore.class.getName()).log(Level.SEVERE, null, ex);
        }*/
    }

    @GET
    @Produces("text/html")
    public String getHtml(@Context UriInfo uri, @QueryParam("width") String width) throws IOException {
        return "<html><img src=\"" + uri.getAbsolutePath() + "?width=" + width + "\"/></html>";
    }

    @GET
    @Produces("image/*")
    public InputStream getFile(@PathParam("albumId") String albumId, @PathParam("pictureId") String pictureId, @QueryParam("width") String width) throws IOException, SempicException {
        if (width != null &&  !"null".equals(width)) {
            try {
                int w = Integer.parseInt(width);

                return Files.newInputStream(storage.getThumbnailPath(Paths.get(albumId, pictureId), w));
            } catch (NumberFormatException e) {
                throw new SempicException("width must be a number: "+width,e);
            }
        }
        return Files.newInputStream(storage.getPicturePath(Paths.get(albumId, pictureId)));
    }

}
