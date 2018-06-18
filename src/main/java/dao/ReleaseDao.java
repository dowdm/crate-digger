package dao;

import models.Artist;
import models.Release;


import java.util.List;

public interface ReleaseDao {
    // LIST
    List<Release> getAll();
    List<Artist> getAllArtistsByReleaseId(int releaseId);

    // CREATE
    void add(Release release);

    //READ
    Release findById(int id);



    // UPDATE
    void update(int id, String title, String label, String labelNumber, int mediaCondition, String sleeveType, int sleeveCondition, String seller, String mediaType, int price, String datePurchased, boolean isInCollection, String imageUrl);

    // DELETE
    void deleteById(int id);
}