drop index uidx_track_play;
alter table track_play add column etag varchar(32);

create unique index uidx_track_play on track_play(play_status, etag);
