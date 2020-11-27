package edu.montana.csci.csci440.model;

import edu.montana.csci.csci440.util.DB;
import redis.clients.jedis.Client;
import redis.clients.jedis.Jedis;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Track extends Model {

    private Long trackId;
    private Long albumId;
    private Long mediaTypeId;
    private Long genreId;
    private String name;
    private Long milliseconds;
    private Long bytes;
    private BigDecimal unitPrice;

    //Cached stuff
    private String albumTitle;
    private String artistName;

    public static final String REDIS_CACHE_KEY = "cs440-tracks-count-cache";

    public Track() {
        mediaTypeId = 1l;
        genreId = 1l;
        milliseconds  = 0l;
        bytes  = 0l;
        unitPrice = new BigDecimal("0");
    }

    public Track(ResultSet results) throws SQLException {
        name = results.getString("Name");
        milliseconds = results.getLong("Milliseconds");
        bytes = results.getLong("Bytes");
        unitPrice = results.getBigDecimal("UnitPrice");
        trackId = results.getLong("TrackId");
        albumId = results.getLong("AlbumId");
        mediaTypeId = results.getLong("MediaTypeId");
        genreId = results.getLong("GenreId");
        artistName = results.getString("ArtistName");
        albumTitle = results.getString("AlbumName");
    }

    /*
    private Track(ResultSet results) throws SQLException {

    }*/

    public static Track find(long i) {
        try (Connection conn = DB.connect();
             PreparedStatement stmt = conn.prepareStatement("SELECT tracks.*, artists.Name as ArtistName," +
                     " albums.Title as AlbumName FROM tracks " +
                     "join albums ON albums.AlbumId=tracks.AlbumId " +
                     "join artists ON artists.ArtistId=albums.ArtistId  WHERE TrackId=?")) {
            stmt.setLong(1, i);
            ResultSet results = stmt.executeQuery();
            if (results.next()) {
                return new Track(results);
            } else {
                return null;
            }
        } catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }
    }

    public static Long count() {
        Jedis redisClient = new Jedis(); // use this class to access redis and create a cache
        try (Connection conn = DB.connect();
             PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) as Count FROM tracks")) {
            ResultSet results = stmt.executeQuery();
            if (results.next()) {
                return results.getLong("Count");
            } else {
                throw new IllegalStateException("Should find a count!");
            }
        } catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }
    }

    public Album getAlbum() {
        return Album.find(albumId);
    }

    public MediaType getMediaType() {
        return null;
    }
    public Genre getGenre() {
        return null;
    }
    public List<Playlist> getPlaylists(){
        //return Collections.emptyList();
        try (Connection conn = DB.connect();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT playlists.* FROM playlists join playlist_track on" +
                             " playlists.PlaylistId=playlist_track.PlaylistId join " +
                             "tracks ON playlist_track.TrackId=tracks.TrackId WHERE" +
                             " tracks.TrackId=? ORDER BY playlists.Name"
             )) {
            stmt.setLong(1, this.getTrackId());
            ResultSet results = stmt.executeQuery();
            List<Playlist> resultList = new LinkedList<>();
            while (results.next()) {
                resultList.add(new Playlist(results));
            }
            return resultList;
        } catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }


    }

    public Long getTrackId() {
        return trackId;
    }

    public void setTrackId(Long trackId) {
        this.trackId = trackId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getMilliseconds() {
        return milliseconds;
    }

    public void setMilliseconds(Long milliseconds) {
        this.milliseconds = milliseconds;
    }

    public Long getBytes() {
        return bytes;
    }

    public void setBytes(Long bytes) {
        this.bytes = bytes;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(Long albumId) {
        this.albumId = albumId;
    }

    public void setAlbum(Album album) {
        albumId = album.getAlbumId();
    }

    public Long getMediaTypeId() {
        return mediaTypeId;
    }

    public void setMediaTypeId(Long mediaTypeId) {
        this.mediaTypeId = mediaTypeId;
    }

    public Long getGenreId() {
        return genreId;
    }

    public void setGenreId(Long genreId) {
        this.genreId = genreId;
    }

    public String getArtistName() {
        // TODO implement more efficiently
        //  hint: cache on this model object
        //return getAlbum().getArtist().getName();
        return this.artistName;
    }

    public String getAlbumTitle() {
        // TODO implement more efficiently
        //  hint: cache on this model object
        //return getAlbum().getTitle();
        return this.albumTitle;
    }

    public static List<Track> advancedSearch(int page, int count,
                                             String search, Integer artistId, Integer albumId,
                                             Integer maxRuntime, Integer minRuntime) {
        LinkedList<Object> args = new LinkedList<>();

        String query = "SELECT * FROM tracks " +
                "JOIN albums ON tracks.AlbumId = albums.AlbumId " +
                "WHERE name LIKE ?";
        args.add("%" + search + "%");

        // Conditionally include the query and argument
        if (artistId != null) {
            query += " AND ArtistId=? ";
            args.add(artistId);
        }

        query += " LIMIT ?";
        args.add(count);

        try (Connection conn = DB.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            for (int i = 0; i < args.size(); i++) {
                Object arg = args.get(i);
                stmt.setObject(i + 1, arg);
            }
            ResultSet results = stmt.executeQuery();
            List<Track> resultList = new LinkedList<>();
            while (results.next()) {
                resultList.add(new Track(results));
            }
            return resultList;
        } catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }
    }

    public static List<Track> search(int page, int count, String orderBy, String search) {
        String query = "SELECT * FROM tracks WHERE name LIKE ? LIMIT ?";
        search = "%" + search + "%";
        try (Connection conn = DB.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, search);
            stmt.setInt(2, count);
            ResultSet results = stmt.executeQuery();
            List<Track> resultList = new LinkedList<>();
            while (results.next()) {
                resultList.add(new Track(results));
            }
            return resultList;
        } catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }
    }

    public static List<Track> forAlbum(Long albumId) {
        String query = "SELECT * FROM tracks WHERE AlbumId=?";
        try (Connection conn = DB.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setLong(1, albumId);
            ResultSet results = stmt.executeQuery();
            List<Track> resultList = new LinkedList<>();
            while (results.next()) {
                resultList.add(new Track(results));
            }
            return resultList;
        } catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }
    }

    // Sure would be nice if java supported default parameter values
    public static List<Track> all() {
        return all(0, Integer.MAX_VALUE);
    }

    public static List<Track> all(int page, int count) {
        return all(page, count, "TrackId");
    }

    public static List<Track> all(int page, int count, String orderBy) {
        try (Connection conn = DB.connect();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT tracks.*, artists.Name as ArtistName, " +
                             "albums.Title as AlbumName FROM tracks " +
                             "join albums ON albums.AlbumId=tracks.AlbumId " +
                             "join artists ON artists.ArtistId=albums.ArtistId " +
                             "ORDER BY " + orderBy + " LIMIT ? OFFSET ?"
             )) {
            //stmt.setString(1, orderBy);
            stmt.setInt(1, count);
            stmt.setInt(2, (page-1)*count);
            ResultSet results = stmt.executeQuery();
            List<Track> resultList = new LinkedList<>();
            while (results.next()) {
                Track track = new Track(results);

                resultList.add(track);
                if(track.getName() == "É Uma Partida De Futebol"){
                    return resultList;
                }
            }
            return resultList;
        } catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }
    }
    @Override
    public boolean create() {
        if (verify()) {
            try (Connection conn = DB.connect();
                 PreparedStatement stmt = conn.prepareStatement(
                         "INSERT INTO tracks (AlbumId, MediaTypeId, GenreId," +
                                 " Name, Milliseconds,Bytes, UnitPrice) VALUES (?, ?, ?, ?, ?, ?, ?)")) {
                stmt.setLong(1, this.getAlbumId());
                stmt.setLong(2, this.getMediaTypeId());
                stmt.setLong(3, this.getGenreId());
                stmt.setString(4, this.getName());
                stmt.setLong(5, this.getMilliseconds());
                stmt.setLong(6, this.getBytes());
                stmt.setBigDecimal(7, this.getUnitPrice());


                stmt.executeUpdate();
                this.trackId = DB.getLastID(conn);
                return true;
            } catch (SQLException sqlException) {
                throw new RuntimeException(sqlException);
            }
        } else {
            return false;
        }
    }

    @Override
    public boolean verify() {
        _errors.clear(); // clear any existing errors
        if (name == null || "".equals(name)) {
            addError("Track name can't be null or blank!");
        }
        if (albumId == null || albumId == 0) {
            addError("Album can't be null!");
        }



        return !hasErrors();
    }

    @Override
    public void delete() {
        try (Connection conn = DB.connect();
             PreparedStatement stmt = conn.prepareStatement(
                     "DELETE FROM tracks WHERE TrackId=?")) {
            stmt.setLong(1, this.getTrackId());
            stmt.executeUpdate();
        } catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }
    }

    @Override
    public boolean update() {
        if (verify()) {
            try (Connection conn = DB.connect();
                 PreparedStatement stmt = conn.prepareStatement(
                         "UPDATE tracks SET AlbumId=?, MediaTypeId=?, GenreId=?, Name=?, Milliseconds=?, Bytes=?, UnitPrice=? " +
                                 "WHERE TrackId=?")) {
                stmt.setLong(1, this.getAlbumId());
                stmt.setLong(2, this.getMediaTypeId());
                stmt.setLong(3, this.getGenreId());
                stmt.setString(4, this.getName());
                stmt.setLong(5, this.getMilliseconds());
                stmt.setLong(6, this.getBytes());
                stmt.setBigDecimal(7, this.getUnitPrice());
                stmt.setLong(8, this.getTrackId());
                stmt.executeUpdate();
                return true;
            } catch (SQLException sqlException) {
                throw new RuntimeException(sqlException);
            }
        } else {
            return false;
        }
    }


}
