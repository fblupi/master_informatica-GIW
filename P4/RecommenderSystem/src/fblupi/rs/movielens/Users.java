package fblupi.rs.movielens;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Ratings of the users in Movie-Lens database
 */
public class Users {
    /**
     * Map with the user id as key and its ratings as value that is a map with movie id as key and its rating as value
     */
    private Map<Integer, Map<Integer, Integer> > ratings;

    /**
     * Average rating of each user where the key is the user id and the value its average rating
     */
    private Map<Integer, Double> averageRating;

    /**
     * Constructor
     */
    public Users() {
        ratings = new HashMap<>();
        averageRating = new HashMap<>();
    }

    /**
     * Fill map with the films in the file with the filename given
     * @param filename name of the file with the films
     */
    public void readFile(String filename) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));

            String line = br.readLine();
            while (line != null) {
                String[] splitLine = line.split("\t");
                int idUser = Integer.parseInt(splitLine[0]);
                int idMovie = Integer.parseInt(splitLine[1]);
                int rating = Integer.parseInt(splitLine[2]);

                if (ratings.containsKey(idUser)) {
                    ratings.get(idUser).put(idMovie, rating);
                    averageRating.put(idUser, averageRating.get(idUser) + rating);
                } else {
                    Map<Integer, Integer> movieRating = new HashMap<>();
                    movieRating.put(idMovie, rating);
                    ratings.put(idUser, movieRating);
                    averageRating.put(idUser, (double) rating);
                }

                line = br.readLine();
            }

            Iterator entries = averageRating.entrySet().iterator();
            while (entries.hasNext()) {
                Map.Entry entry = (Map.Entry) entries.next();
                entry.setValue((double) entry.getValue() / (double) ratings.get(entry.getKey()).size());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the k-nearest neighbourhoods using Pearson:
     *   sim(i,j) = numerator / (sqrt(userDenominator^2) * sqrt(otherUserDenominator^2))
     *     numerator = sum((r(u,i) - r(i)) * (r(u,j) - r(j)))
     *     userDenominator = sum(r(u,i) - r(i))
     *     otherUserDenominator = sum(r(u,j) - r(j))
     *     r(u,i): rating of the movie u by the user i
     *     r(i): average rating of the user i
     * @param userRatings ratings of the user
     * @param k number of output neighbourhoods
     * @return nearest neighbourhoods
     */
    public Map<Integer, Double> getNeighbourhoods(Map<Integer, Integer> userRatings, int k) {
        Map<Integer, Double> neighbourhoods = new HashMap<>();
        ValueComparator valueComparator = new ValueComparator(neighbourhoods);
        Map<Integer, Double> sortedNeighbourhoods = new TreeMap<>(valueComparator);

        double userAverage = 0;
        Iterator userEntries = userRatings.entrySet().iterator();
        while (userEntries.hasNext()) {
            Map.Entry entry = (Map.Entry) userEntries.next();
            userAverage += (int) entry.getValue();
        }
        userAverage /= userRatings.size();

        for (int user : ratings.keySet()) {
            ArrayList<Integer> matches = new ArrayList<>();
            for (int movie : userRatings.keySet()) {
                if (ratings.get(user).containsKey(movie)) {
                    matches.add(movie);
                }
            }
            double matchRate, numerator = 0, userDenominator = 0, otherUserDenominator = 0;
            if (matches.size() > 0) {
                for (int movie : matches) {
                    int u = userRatings.get(movie);
                    int v = ratings.get(user).get(movie);

                    u -= userAverage;
                    v -= averageRating.get(user);

                    numerator += u * v;
                    userDenominator += u * u;
                    otherUserDenominator += v * v;
                }
                if (userDenominator == 0 || otherUserDenominator == 0) {
                    matchRate = -1;
                } else {
                    matchRate = numerator / (Math.sqrt(userDenominator) * Math.sqrt(otherUserDenominator));
                }
            } else {
                matchRate = -1;
            }

            neighbourhoods.put(user, matchRate);
        }
        sortedNeighbourhoods.putAll(neighbourhoods);

        Map<Integer, Double> output = new TreeMap<>();

        Iterator entries = sortedNeighbourhoods.entrySet().iterator();
        int i = 0;
        while (entries.hasNext() && i < k) {
            Map.Entry entry = (Map.Entry) entries.next();
            output.put((int) entry.getKey(), (double) entry.getValue());
            i++;
        }

        return output;
    }

    /**
     * Get predictions of each movie by a user giving some ratings and its neighbourhood:
     *   r(u,i) = sum(sim(i,j) * r(u,j)) / sum(abs(sim(i,j)))
     *     sim(i,j): similarity between i and j users
     *     r(u,j): rating of the movie u by the user j
     * @param userRatings ratings of the user
     * @param neighbourhoods nearest neighbourhoods
     * @param movies movies in the database
     * @return predictions for each movie
     */
    public Map<Integer, Double> getRecommendations(Map<Integer, Integer> userRatings,
                                                   Map<Integer, Double> neighbourhoods, Map<Integer, String> movies) {
        Map<Integer, Double> predictedRatings = new HashMap<>();

        for (int movie : movies.keySet()) {
            if (!userRatings.containsKey(movie)) {
                double numerator = 0, denominator = 0;
                for (int neighbourhood : neighbourhoods.keySet()) {
                    if (ratings.get(neighbourhood).containsKey(movie)) {
                        double matchRate = neighbourhoods.get(neighbourhood);
                        numerator += matchRate * ratings.get(neighbourhood).get(movie);
                        denominator += Math.abs(matchRate);
                    }
                }
                double predictedRating = 0;
                if (denominator > 0) {
                    predictedRating = numerator / denominator;
                }
                predictedRatings.put(movie, predictedRating);
            }
        }

        return predictedRatings;
    }
}

/**
 * Comparator to sort a HashMap by its value:
 * http://stackoverflow.com/questions/109383/sort-a-mapkey-value-by-values-java
 */
class ValueComparator implements Comparator<Integer> {
    private Map<Integer, Double> base;

    public ValueComparator(Map<Integer, Double> base) {
        this.base = base;
    }

    public int compare(Integer a, Integer b) {
        if (base.get(a) >= base.get(b)) {
            return -1;
        } else {
            return 1;
        }
    }
}
