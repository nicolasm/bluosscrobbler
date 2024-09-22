alter table track_play add column md5_checksum varchar(32) not null default '';

drop index uidx_track_play;
create unique index uidx_track_play on track_play(play_status, md5_checksum, timestamp);
