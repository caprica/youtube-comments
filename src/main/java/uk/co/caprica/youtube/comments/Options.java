package uk.co.caprica.youtube.comments;

import com.beust.jcommander.Parameter;

public class Options {

    @Parameter(description = "<youtube-watch-id>", required = true)
    public String watchId;

    @Parameter(names = {"-s", "--stats" }, description = "Specify the filename for video statistics (json)")
    public String statsOutput;

    @Parameter(names = {"-c", "--comments" }, description = "Specify the filename for comments spreadsheet (xlsx)")
    public String commentsOutput;
}
