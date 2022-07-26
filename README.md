# youtube-comments

A simple program to fetch a YouTube comment thread as a spreadsheet, and optional video statistics.

_Note: It currently returns __top-level__ comments only, not the threaded replies, as fetching all replies will make
potentially very many additional API requests. By default, there is an available quota of 10,000 API requests per day,
for videos with thousands of threads then fetching all of those individual threads could easily exhaust the quota. The
stats option at least returns the total count of all comments._

## Prerequisites

### API Key

You must obtain your own YouTube API key, see:

https://developers.google.com/youtube/v3/getting-started

### Configuration File

When you have your API key, create/edit the `youtube-comments.config` file and
add your key there.

### Building

Either you need `mvn` installed, or use an IDE such as IntelliJ IDEA to build it.

https://maven.apache.org/

https://www.jetbrains.com/idea/download/

### Runtime Environment

You need a Java 8 or later runtime environment, get it from here:

https://adoptium.net/

## Building

After cloning the repo:

```shell
mvn clean install
```

This will produce an executable `jar` file, with all dependencies included.

## Running

```text
Usage: java -jar youtube-comments-1.0.jar [options] <youtube-watch-id>
  Options:
    -c, --comments
      Specify the filename for comments spreadsheet (xlsx)
    -s, --stats
      Specify the filename for video statistics (json)
```

The YouTube watch-id comes from a regular YouTube URL, for example:

```
https://www.youtube.com/watch?v=v7v1hIkYH24
```

In this YouTube URL the watch-id is `v7v1hIkYH24`.

For example, stats and comments:

```shell
java -jar youtube-comments-1.0.jar -s fake-lotr.json -c fake-lotr.xlsx v7v1hIkYH24
```

Stats only:

```shell
java -jar youtube-comments-1.0.jar -s fake-lotr.json v7v1hIkYH24
```

Comments only:

```shell
java -jar youtube-comments-1.0.jar -c fake-lotr.xlsx v7v1hIkYH24
```
