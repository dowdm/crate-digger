
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import dao.Sql2oReleaseDao;
import dao.Sql2oArtistDao;
import dao.Sql2oNoteDao;
import models.Artist;
import models.Note;
import models.Release;
import org.sql2o.Sql2o;
import spark.ModelAndView;
import spark.template.handlebars.HandlebarsTemplateEngine;
import static spark.Spark.*;

public class App {
    static int getHerokuAssignedPort() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get("PORT") != null) {
            return Integer.parseInt(processBuilder.environment().get("PORT"));
        }
        return 4567; //return default port if heroku-port isn't set (i.e. on localhost)
    }
    public static void main(String[] args) {
        port(getHerokuAssignedPort());
        staticFileLocation("/public");
        String connectionString = "jdbc:postgresql://localhost:5432/cratedigger";
        Sql2o sql2o = new Sql2o(connectionString, null, null);
        Sql2oReleaseDao releaseDao = new Sql2oReleaseDao(sql2o);
        Sql2oArtistDao artistDao = new Sql2oArtistDao(sql2o);
        Sql2oNoteDao noteDao = new Sql2oNoteDao(sql2o);

        //get: show recent releases
        get("/", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            List<Release> recentReleases = releaseDao.getRecent();
            model.put("releases", recentReleases);
            return new ModelAndView(model, "index.hbs");
        }, new HandlebarsTemplateEngine());

        //get: show releases in collection
        get("/releases", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            List<Release> allReleases = releaseDao.getAll();
            model.put("releases", allReleases);
            return new ModelAndView(model, "index.hbs");
        }, new HandlebarsTemplateEngine());

        //get: show releases in wishlist
        get("/releases/wishlist", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            List<Release> wishlist = releaseDao.getWishlist();
            model.put("wishlist", wishlist);
            return new ModelAndView(model, "index.hbs");
        }, new HandlebarsTemplateEngine());

        //get: show all artists
        get("/artists", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            List<Artist> allArtists = artistDao.getAll();
            model.put("artists", allArtists);
            return new ModelAndView(model, "artists.hbs");
        }, new HandlebarsTemplateEngine());

        //get: show details and releases of single artist
        get("/artists/:id", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            int idOfArtistToFind = Integer.parseInt(req.params("id"));
            Artist artist = artistDao.findById(idOfArtistToFind);
            List<Release> releases = artistDao.getAllReleasesByArtistId(idOfArtistToFind);
            model.put("artist", artist);
            model.put("releases", releases);
            return new ModelAndView(model, "artist-detail.hbs");
        }, new HandlebarsTemplateEngine());

        //get:show new artist/release form
        get("/releases/new", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            List<Artist> allArtists = artistDao.getAll();
            model.put("artists", allArtists);
            return new ModelAndView(model, "release-form.hbs");
        }, new HandlebarsTemplateEngine());

        //get: show details of specific release
        get("/releases/:id", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            int idOfReleaseToFind = Integer.parseInt(req.params("id"));
            Release release = releaseDao.findById(idOfReleaseToFind);
            List<Artist> artists = releaseDao.getAllArtistsByReleaseId(idOfReleaseToFind);
            List<Note> notes = noteDao.getAllByReleaseId(idOfReleaseToFind);
            model.put("notes", notes);
            model.put("artists", artists);
            model.put("release", release);
            return new ModelAndView(model, "release-detail.hbs");
        }, new HandlebarsTemplateEngine());

        //post: process new artist form
        post("/artists", (req, res) -> { //new
            Map<String, Object> model = new HashMap<>();
            String name = req.queryParams("name");
            String imageUrl = req.queryParams("imageUrl");
            Artist newArtist = new Artist(name, imageUrl);
            artistDao.add(newArtist);
            res.redirect("/artists");
            return null;
        }, new HandlebarsTemplateEngine());

        //post: process update on specific artist
        post("/artists/:id/update", (req, res) -> { //new
            Map<String, Object> model = new HashMap<>();
            int idOfArtistToFind = Integer.parseInt(req.params("id"));
            String name = req.queryParams("name");
            String imageUrl = req.queryParams("imageUrl");
            Artist newArtist = new Artist(name, imageUrl);
            artistDao.update(idOfArtistToFind, name,imageUrl);
            res.redirect("/artists/" + idOfArtistToFind);
            return null;
        }, new HandlebarsTemplateEngine());

        //post: process new release form
        post("/releases", (req, res) -> { //new
            Map<String, Object> model = new HashMap<>();
            String title = req.queryParams("title");
            String label = req.queryParams("label");
            String labelNumber = req.queryParams("labelNumber");
            int mediaCondition = Integer.parseInt(req.queryParams("mediaCondition"));
            String sleeveType = req.queryParams("sleeveType");
            int sleeveCondition = Integer.parseInt(req.queryParams("sleeveCondition"));
            String seller = req.queryParams("seller");
            String mediaType = req.queryParams("mediaType");
            int price = Integer.parseInt(req.queryParams("price"));
            String datePurchased = req.queryParams("datePurchased");
            boolean isInCollection = Boolean.parseBoolean(req.queryParams("isInCollection"));
            String imageUrl = req.queryParams("imageUrl");
            Release newRelease = new Release(title, label, labelNumber, mediaCondition, sleeveType, sleeveCondition, seller, mediaType, price, datePurchased, isInCollection, imageUrl);
            releaseDao.add(newRelease);
            res.redirect("/releases");
            return null;
        }, new HandlebarsTemplateEngine());

        //post: process update on specific release
        post("/releases/:id/update", (req, res) -> { //new
            Map<String, Object> model = new HashMap<>();
            int idOfReleaseToFind = Integer.parseInt(req.params("id"));
            String title = req.queryParams("title");
            String label = req.queryParams("label");
            String labelNumber = req.queryParams("labelNumber");
            int mediaCondition = Integer.parseInt(req.queryParams("mediaCondition"));
            String sleeveType = req.queryParams("sleeveType");
            int sleeveCondition = Integer.parseInt(req.queryParams("sleeveCondition"));
            String seller = req.queryParams("seller");
            String mediaType = req.queryParams("mediaType");
            int price = Integer.parseInt(req.queryParams("price"));
            String datePurchased = req.queryParams("datePurchased");
            boolean isInCollection = Boolean.parseBoolean(req.queryParams("isInCollection"));
            String imageUrl = req.queryParams("imageUrl");
            Release newRelease = new Release(title, label, labelNumber, mediaCondition, sleeveType, sleeveCondition, seller, mediaType, price, datePurchased, isInCollection, imageUrl);
            releaseDao.update(idOfReleaseToFind, title, label, labelNumber, mediaCondition, sleeveType, sleeveCondition, seller, mediaType, price, datePurchased, isInCollection, imageUrl);
            res.redirect("/releases");
            return null;
        }, new HandlebarsTemplateEngine());

        // post:delete specific artist
        post("/artists/:id/delete", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            int idOfArtistToDelete = Integer.parseInt(req.params("id"));
            artistDao.deleteById(idOfArtistToDelete);
            res.redirect("/artists");
            return null;
        }, new HandlebarsTemplateEngine());

        //post: delete specific release
        post("/releases/:id/delete", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            int idOfReleaseToDelete = Integer.parseInt(req.params("id"));
            releaseDao.deleteById(idOfReleaseToDelete);
            res.redirect("/artists");
            return null;
        }, new HandlebarsTemplateEngine());


        //post: process form to add new note
        post("/releases/:id", (req, res) -> {
            String content = req.queryParams("content");
            int releaseId = Integer.parseInt(req.params("id"));
            Note newNote = new Note(content, releaseId);
            noteDao.add(newNote);
            res.redirect("/releases/" + releaseId);
            return null;
        }, new HandlebarsTemplateEngine());

        //post: delete specific note
        post("/note/:id", (req, res) -> {
            int idOfNoteToDelete = Integer.parseInt(req.params("id"));
            Note noteToDelete = noteDao.findById(idOfNoteToDelete);
            int releaseId = noteToDelete.getReleaseId();
            noteDao.deleteById(idOfNoteToDelete);
            res.redirect("/releases/" + releaseId);
            return null;
        }, new HandlebarsTemplateEngine());

    }
}