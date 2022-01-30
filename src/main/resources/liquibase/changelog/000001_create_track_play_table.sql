create table track_play (
    id int auto_increment primary key not null,
    artist varchar(255) not null,
    album varchar(255) null,
    track varchar(255) not null,
    duration int not null,
    timestamp varchar(10) not null,
    play_status varchar(30) not null,
    lastfm_scrobble_status varchar(30) null,
    etag varchar(32),
    created_at timestamp default current_timestamp not null,
    updated_at timestamp default current_timestamp not null
);

create unique index uidx_track_play on track_play(play_status, etag);
create index idx_lastfm_scrobble_status on track_play(lastfm_scrobble_status);
