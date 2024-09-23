# BluOSScrobbler
Scrobble BluOS played tracks
https://github.com/nicolasm/bluosscrobbler

## Description

Bluesound devices like the Node 2021 don't offer support
for Last.fm scrobbling.

The app polls the playing status of your device by using the [BluOS API][1].

The Last.fm now playing status is updated when:
- playing a new track,
- playing after pause.

A track is scrobbled when either half-played or played for at least 4 minutes,

The poller no longer uses the `etag` attribute of the `/Status` response.
I misunderstood the `/Status` endpoint, I thought it was supposed to change only when
a new track was played when I developed my app.

I realized using the `etag` value was a bad idea from the start, because it changed
for every user action performed in the Controller app: volume control, playlist modifications..

The `/Playlist` endpoint pointed to the right direction:
```xml
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<playlist id="3295" modified="0" length="12">
	<song songid="8403" id="0" service="LocalMusic">
		<title>Tout éteindre</title>
		<art>Jil Caplan</art>
		<alb>Sur les cendres danser</alb>
		<fn>/var/mnt/.......................</fn>
		<composer>Emilie Marsh</composer>
		<quality>cd</quality>
	</song>
</playlist>
```

> playlist `id` is the unique id for the current queue state (e.g., 1054). It is same as
> the `<pid>` in  `/Status` response.

> song `id` is the track position in the current queue. If the track is currently selected,
> track id is same as `<song>` in `/Status` response.

The poller now relies on a MD5 hexadecimal checksum of the concatenation of the fields:
- ~~playlist `id`,~~
- song `id`,
- artist name,
- album name
- and track name

to determine if the current `/Status` response refers to a new track play.

The playlist `id` changes as soon as new songs are added to the play queue.

TODO:
- There may be a few issues when using random multiples times while adding new songs to the queue
  after a song has been scrobbled: song being scrobbled twice maybe because its position in the queue has changed...
- Handle the one song on repeat case: the only thing changing is the `etag` value. I tried something,
  but it didn't work out as I expected.

## Requirements

Java 11

## Setup

Copy `application.yaml.example` to `application.yml`.

Update your BluOS device ip address in the Yaml file.

```yaml
application:
  bluos:
    host: 192.168.1.7
    port: 11000
```

Launch the app.

In your browser, call
http://localhost:18666/v1/bluossrobbler/lastfm/auth/request

Allow access to the BluOSScrobbler application.

This will call back the app and fetch a web service session from Last.fm.

Your browser should display a response similar to this:

```json
{
	"session": {
		"name": "username",
		"key": "your-session-key",
		"subscriber": false
	}
}
```

Set the session key in the Yaml file.

```yaml
scrobblers:
    lastfm:
      ...
      sessionKey: your-session-key
```

## Package the app

```shell
./mvnw clean package
```

## Copy the app to your NAS or server and start it

```shell
nohup java -jar bluosscrobbler-1.RELEASE.jar
````

## License

Copyright (c) 2021-2024, Nicolas Meier.

[MIT][2]

[0]: https://github.com/simple-last-fm-scrobbler/sls
[1]: https://bluos.net/wp-content/uploads/2020/06/Custom-Integration-API-v1.0.pdf
[2]: https://raw.githubusercontent.com/nicolasm/bluosscrobbler/main/LICENSE
