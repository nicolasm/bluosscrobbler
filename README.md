# BluOSScrobbler
Scrobble BluOS played tracks

## Description

Currently, Bluesound devices like the Node 2021 don't offer support
for Last.fm scrobbling.

I was using [Simple Scrobbler][0]
on my Android smartphone as it detected the BluOS Controller app.

But that solution has some drawbacks:
- the app is not developed actively anymore,
- it is not available on iOS,
- you must check if it is running. So, it is not super reliable, 
  especially if the Controller app is not detected anymore
  like it happened to me once.

For all of those reasons, I decided to create my own solution.

The app polls the playing status of your device by using the [BluOS API][1].

The Last.fm now playing status is updated when:
- playing a new track,
- playing after pause.

Once a track is half-played, it is marked as to be scrobbled.
Once a new track is played, the previous track is scrobbled.

## Requirements

Java 8

## TODO

* Cache scrobbles in case of Last.fm errors when scrobbling

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

[0]: "https://github.com/simple-last-fm-scrobbler/sls"
[1]: "https://bluos.net/wp-content/uploads/2020/06/Custom-Integration-API-v1.0.pdf"
