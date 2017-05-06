package fblupi.rs.movielens;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Users {
    Map<Integer, Map<Integer, Integer> > ratings;
    Map<Integer, Double> averageRating;

    public Users() {
        ratings = new HashMap<>();
        averageRating = new HashMap<>();
    }

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

    Map<Integer, Double> getNeighbourhoods(Map<Integer, Integer> userRatings, int k) {
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

        Map<Integer, Double> output = new HashMap<>();

        Set keys = sortedNeighbourhoods.keySet();
        int iterations = 0;
        for (Iterator i = keys.iterator(); i.hasNext() && iterations < k; iterations++) {
            Integer key = (Integer) i.next();
            output.put(key, sortedNeighbourhoods.get(key));
        }

        return output;
    }

    @Override
    public String toString() {
        String output = "";
        Iterator userEntries = ratings.entrySet().iterator();
        while (userEntries.hasNext()) {
            Map.Entry entry = (Map.Entry) userEntries.next();
            output += "idUser: " + entry.getKey() + "\n";
            output += "ratings: " + entry.getValue() + "\n";
            output += "average: "  + averageRating.get(entry.getKey()) + "\n\n";
        }
        return  output;
    }
}

/**
 * http://stackoverflow.com/questions/109383/sort-a-mapkey-value-by-values-java
 */
class ValueComparator implements Comparator<Integer> {
    Map<Integer, Double> base;

    public ValueComparator(Map<Integer, Double> base) {
        this.base = base;
    }

    public int compare(Integer a, Integer b) {
        if (base.get(a) > base.get(b)) {
            return 1;
        } else {
            return -1;
        }
    }
}
