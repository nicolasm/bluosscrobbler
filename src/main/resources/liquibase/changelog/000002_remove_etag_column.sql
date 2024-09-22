drop index uidx_track_play;
alter table track_play drop column etag;

create unique index uidx_track_play on track_play(play_status, timestamp);
