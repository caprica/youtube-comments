# youtube-comments

A simple program to fetch a YouTube comment thread as a spreadsheet.

_Note: It currently returns __top-level__ comments only, not the threaded replies, as fetching all replies will make
potentially very many additional API requests._

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

```shell
java -jar youtube-comments-1.0.jar <youtube-watch-id> <output-file>
```

The YouTube watch-id comes from a regular YouTube URL, for example:

```
https://www.youtube.com/watch?v=v7v1hIkYH24
```

In this YouTube URL the watch-id is `v7v1hIkYH24`.

For example:

```shell
java -jar youtube-comments-1.0.jar v7v1hIkYH24 fake-lotr.xlsx
```

The output file is a standard spreadsheet file that can be loaded into Libre Office or Excel.
