package com.nicolasm.bluosscrobbler.bluos.persistence.repository;

import com.nicolasm.bluosscrobbler.bluos.persistence.entity.TrackPlayEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TrackPlayRepository extends CrudRepository<TrackPlayEntity, String> {
    @Query("select p from TrackPlayEntity p where p.playStatus = 'PLAYING'")
    TrackPlayEntity findPlaying();

    @Query("select p from TrackPlayEntity p where p.playStatus = 'PLAYED' and p.etag = :etag")
    TrackPlayEntity findPlayedByEtag(String etag);

    @Query("select p from TrackPlayEntity p where p.playStatus = 'PLAYED' and p.lastfmScrobbleStatus = 'TO_BE_SCROBBLED'")
    List<TrackPlayEntity> findLastfmToBeScrobbled();
}
