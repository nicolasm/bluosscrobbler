package com.nicolasm.bluosscrobbler.bluos.persistence.repository;

import com.nicolasm.bluosscrobbler.bluos.persistence.entity.TrackPlayEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TrackPlayRepository extends CrudRepository<TrackPlayEntity, String> {
    @Query("select p from TrackPlayEntity p where p.playStatus = 'PLAYING'")
    TrackPlayEntity findLastfmPlaying();

    @Query("select p from TrackPlayEntity p where p.playStatus = 'PLAYING' AND p.md5Checksum = :md5Checksum")
    TrackPlayEntity findLastfmByMd5Checksum(String md5Checksum);

    @Query("select p from TrackPlayEntity p where p.lastfmScrobbleStatus = 'TO_BE_SCROBBLED'")
    List<TrackPlayEntity> findLastfmToBeScrobbled();

    @Modifying
    @Query("delete from TrackPlayEntity p where p.playStatus = 'PLAYED' and p.lastfmScrobbleStatus = 'SCROBBLED'")
    void deleteScrobbled();
}
