package com.example.spotifydemo.datafetchers;

import com.example.spotifydemo.datasources.SpotifyClient;
import com.example.spotifydemo.generated.types.AddItemsToPlaylistInput;
import com.example.spotifydemo.generated.types.AddItemsToPlaylistPayload;
import com.example.spotifydemo.generated.types.Playlist;
import com.example.spotifydemo.models.Snapshot;
import com.netflix.graphql.dgs.*;
import com.example.spotifydemo.models.MappedPlaylist;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Objects;

@DgsComponent
public class PlaylistDataFetcher {
    private final SpotifyClient spotifyClient;

    @Autowired
    public PlaylistDataFetcher(SpotifyClient spotifyClient) {
        this.spotifyClient = spotifyClient;
    }
    @DgsQuery
    public List<MappedPlaylist> featuredPlaylists() {
        return spotifyClient.featuredPlaylistsRequest().getPlaylists();
    }

    @DgsQuery
    public MappedPlaylist playlist(@InputArgument String id) {

        return spotifyClient.playlistRequest(id);
    }

    @DgsMutation
    public AddItemsToPlaylistPayload addItemsToPlaylist(@InputArgument AddItemsToPlaylistInput input) {
        String playlistId = input.getPlaylistId();
        Integer position = input.getPosition();
        List<String> uris = input.getUris();
        AddItemsToPlaylistPayload payload = new AddItemsToPlaylistPayload();
        Snapshot snapshot = spotifyClient.addItemsToPlaylist(playlistId, position, String.join(",", uris));
        if (snapshot != null) {
            String snapshotId = snapshot.id();
            if (Objects.equals(snapshotId, playlistId)) {
                Playlist playlist = new Playlist();
                playlist.setId(playlistId);

                payload.setCode(200);
                payload.setMessage("success");
                payload.setSuccess(true);
                payload.setPlaylist(playlist);

                return payload;
            }
        }
        payload.setCode(500);
        payload.setMessage("could not update playlist");
        payload.setSuccess(false);
        payload.setPlaylist(null);

        return payload;
    }
    @DgsData(parentType="AddItemsToPlaylistPayload", field="playlist")
    public MappedPlaylist getPayloadPlaylist(DgsDataFetchingEnvironment dfe) {
        AddItemsToPlaylistPayload payload = dfe.getSource();
        Playlist playlist = payload.getPlaylist();

        if (playlist != null) {
            String playlistId = playlist.getId();
            return spotifyClient.playlistRequest(playlistId);

        }

        return null;
    }



}
