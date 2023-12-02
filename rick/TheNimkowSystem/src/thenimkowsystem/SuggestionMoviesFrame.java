package thenimkowsystem;

import javax.swing.JFrame;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author metal
 */
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class SuggestionMoviesFrame extends JFrame {
    private JPanel contentPane;
    private MovieDetailsFrame movieDetailsFrame;

    private static final String API_KEY = "f846867b6184611eeff179631d3f9e26";

    public SuggestionMoviesFrame(List<String> recommendedMovies, MovieDetailsFrame movieDetailsFrame) {
        this.movieDetailsFrame = movieDetailsFrame;
        setTitle("Películas Recomendadas");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        contentPane = new JPanel();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane(contentPane);

        // Mostrar las películas recomendadas
        displayRecommendedMovies(recommendedMovies);

        add(scrollPane);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void displayRecommendedMovies(List<String> recommendedMovies) {
        for (String movieTitle : recommendedMovies) {
            JPanel moviePanel = new JPanel();
            moviePanel.setLayout(new BorderLayout());
            moviePanel.setBackground(Color.LIGHT_GRAY);

            // Obtener información detallada de la película al hacer clic
            JsonObject movieDetails = fetchMovieDetailsByTitle(movieTitle);

            if (movieDetails != null) {
                // Mostrar la imagen en la parte superior
                try {
                    String posterPath = movieDetails.get("poster_path").getAsString();
                    URL posterURL = new URL("https://image.tmdb.org/t/p/w200" + posterPath);
                    ImageIcon posterIcon = new ImageIcon(posterURL);
                    JLabel posterLabel = new JLabel(posterIcon);

                    moviePanel.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            // Obtener información adicional (género y director) al hacer clic
                            String director = fetchDirector(movieDetails.get("id").getAsInt());
                            List<String> genres = fetchGenres(movieDetails.get("id").getAsInt());
                            String trailerLink = fetchTrailerLink(movieDetails.get("id").getAsInt());

                            // Pasar esta información a MovieDetailsFrame
                            new MovieDetailsFrame(movieTitle, movieDetails.get("overview").getAsString(), director,
                                    movieDetails.get("release_date").getAsString(), genres, fetchCast(movieDetails.get("id").getAsInt()), trailerLink);
                        }
                    });

                    moviePanel.add(posterLabel, BorderLayout.CENTER);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                contentPane.add(moviePanel);
                contentPane.add(Box.createRigidArea(new Dimension(0, 10))); // Espaciado vertical
            }
        }
    }

    private JsonObject fetchMovieDetailsByTitle(String movieTitle) {
        try {
            String urlStr = "https://api.themoviedb.org/3/search/movie?api_key=" + API_KEY + "&language=es-ES&page=1&query=" + movieTitle;
            URL url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            StringBuilder response;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            }

            JsonObject jsonObject = JsonParser.parseString(response.toString()).getAsJsonObject();
            JsonArray resultsArray = jsonObject.getAsJsonArray("results");

            if (resultsArray.size() > 0) {
                return resultsArray.get(0).getAsJsonObject();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private String fetchDirector(int movieId) {
        try {
            // ... (Método fetchDirector existente)
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "Desconocido";
    }

    private List<String> fetchGenres(int movieId) {
        try {
            // ... (Método fetchGenres existente)
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new java.util.ArrayList<>();
    }

    private List<String> fetchCast(int movieId) {
        try {
            // ... (Método fetchCast existente)
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new java.util.ArrayList<>();
    }

    private String fetchTrailerLink(int movieId) {
        try {
            String urlStr = "https://api.themoviedb.org/3/movie/" + movieId + "/videos?api_key=" + API_KEY;
            URL url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            StringBuilder response;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            }

            JsonObject jsonObject = JsonParser.parseString(response.toString()).getAsJsonObject();
            JsonArray videosArray = jsonObject.getAsJsonArray("results");

            for (JsonElement element : videosArray) {
                JsonObject video = element.getAsJsonObject();
                if (video.get("type").getAsString().equals("Trailer")) {
                    return "https://www.youtube.com/watch?v=" + video.get("key").getAsString();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            List<String> recommendedMovies = List.of("Recommended Movie 1", "Recommended Movie 2", "Recommended Movie 3");
            new SuggestionMoviesFrame(recommendedMovies, null);  // La instancia de MovieDetailsFrame se asignará después
        });
    }
}
