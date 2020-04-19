package rest;

import java.util.List;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.MediaType;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dtos.LinkDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import webscraper.TagCounter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.ArrayList;
import dtos.TagDTO;
import webscraper.WebScraper;

@Path("scrape")
public class WebScraperResource {

    @Context
    private UriInfo context;

    @Operation(summary = "Scraping a list of webpages for DIV and BODY tags",
            tags = {"scrape"},
            responses = {
                    @ApiResponse(
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TagDTO.class))),
                    @ApiResponse(responseCode = "200", description = "The requested web scraping"),
                    @ApiResponse(responseCode = "403", description = "Not authenticated - do login")})
    @GET
    @RolesAllowed({"admin"})
    @Produces(MediaType.APPLICATION_JSON)
    public String getTagsFast() throws InterruptedException {
        return makeResponse();
    }

    private String makeResponse() throws InterruptedException {
        List<TagCounter> dataFetched = WebScraper.runParallel();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        List<TagDTO> results = new ArrayList();
        for(TagCounter tc: dataFetched){
            results.add(new TagDTO(tc));
        }
        return gson.toJson(results);
    }

    @Operation(summary = "Scraping a specific URL for links",
            tags = {"scrape"},
            responses = {
                    @ApiResponse(
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TagDTO.class))),
                    @ApiResponse(responseCode = "200", description = "The requested web scraping")})
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String scrapeForLinks(String jsonString) {
        JsonObject json = new JsonParser().parse(jsonString).getAsJsonObject();
        String url = json.get("url").getAsString();
        List<LinkDTO> dtos = WebScraper.findLinks(url);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(dtos);
    }
}
