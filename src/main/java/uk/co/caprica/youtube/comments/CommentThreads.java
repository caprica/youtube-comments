package uk.co.caprica.youtube.comments;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.CommentSnippet;
import com.google.api.services.youtube.model.CommentThread;
import com.google.api.services.youtube.model.CommentThreadListResponse;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;
import org.dhatim.fastexcel.Workbook;
import org.dhatim.fastexcel.Worksheet;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.time.Instant;
import java.util.Properties;

public class CommentThreads {

    private static final String CONFIG_FILE_NAME = "youtube-comments.config";

    private static final String API_KEY_PROPERTY = "API_KEY";

    private static final String APPLICATION_NAME = "YouTube Comments";

    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    public static void main(String[] args) {
        Options options = new Options();
        JCommander cli = JCommander.newBuilder()
            .addObject(options)
            .programName("java -jar youtube-comments-1.0.jar")
            .build();
        try {
            cli.parse(args);
        } catch (ParameterException e) {
            cli.usage();
            System.exit(-1);
        }

        try {
            String apiKey = loadApiKey();

            String videoId = options.watchId;
            String statsOutput = options.statsOutput;
            String commentsOutput = options.commentsOutput;

            YouTube youtubeService = getService();

            if (statsOutput != null) {
                System.out.printf("Writing status to '%s'%n", statsOutput);

                VideoListResponse statsResponse = youtubeService.videos()
                    .list("statistics")
                    .setKey(apiKey)
                    .setId(videoId)
                    .execute();

                Video video = statsResponse.getItems().get(0);

                try (BufferedWriter writer = Files.newBufferedWriter(Paths.get("stats.json"))) {
                    writer.write(video.getStatistics().toString());
                }

                System.out.printf("Wrote '%s'%n", statsOutput);
            }

            if (commentsOutput != null) {
                YouTube.CommentThreads.List request = youtubeService.commentThreads()
                    .list("snippet,replies")
                    .setMaxResults(300L)
                    .setKey(apiKey)
                    .setVideoId(videoId);

                System.out.printf("Writing comments to '%s'%n", commentsOutput);

                try (OutputStream os = Files.newOutputStream(Paths.get(commentsOutput))) {
                    Workbook wb = new Workbook(os, "Comments", "1.0");

                    Worksheet ws = wb.newWorksheet(Instant.now().toString());

                    ws.value(0, 0, "Date");
                    ws.value(0, 1, "Author");
                    ws.value(0, 2, "Likes");
                    ws.value(0, 3, "Text Original");
                    ws.style(0, 0).bold();
                    ws.style(0, 1).bold();
                    ws.style(0, 2).bold();
                    ws.style(0, 3).bold();

                    int row = 1;

                    int requestNumber = 1;

                    for (; ; ) {
                        System.out.printf("Request %d%n", requestNumber++);

                        CommentThreadListResponse response = request.execute();
                        if (response.isEmpty()) break;

                        for (CommentThread commentThread : response.getItems()) {
                            CommentSnippet snippet = commentThread.getSnippet().getTopLevelComment().getSnippet();
                            ws.value(row, 0, snippet.getPublishedAt().toString());
                            ws.value(row, 1, snippet.getAuthorDisplayName());
                            ws.value(row, 2, snippet.getLikeCount());
                            ws.value(row, 3, snippet.getTextOriginal());
                            row++;
                        }

                        String nextPageToken = response.getNextPageToken();
                        if (nextPageToken == null) break;

                        request.setPageToken(nextPageToken);
                    }

                    wb.finish();

                    System.out.printf("Wrote '%s'%n", commentsOutput);
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    private static String loadApiKey() throws Exception {
        try {
            Properties properties = new Properties();
            try (FileReader reader = new FileReader(CONFIG_FILE_NAME)) {
                properties.load(reader);
            }
            String apiKey = properties.getProperty(API_KEY_PROPERTY);
            if (apiKey == null || apiKey.trim().length() == 0) {
                throw missingConfigException();
            }
            return apiKey.trim();
        } catch (FileNotFoundException e) {
            throw missingConfigException();
        }
    }

    private static Exception missingConfigException() {
        return new IllegalArgumentException(
            String.format("You must set a property named '%s' in a '%s' configuration file",
                API_KEY_PROPERTY,
                CONFIG_FILE_NAME
            )
        );
    }

    private static YouTube getService() throws GeneralSecurityException, IOException {
        final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        return new YouTube.Builder(httpTransport, JSON_FACTORY, null)
            .setApplicationName(APPLICATION_NAME)
            .build();
    }
}
