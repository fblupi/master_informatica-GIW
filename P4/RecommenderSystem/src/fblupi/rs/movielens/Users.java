package fblupi.rs.movielens;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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

    @Override
    public String toString() {
        String output = "";
        Iterator userEntries = ratings.entrySet().iterator();
        while (userEntries.hasNext()) {
            Map.Entry entry = (Map.Entry) userEntries.next();
            output += "idUser: " + entry.getKey() + "\n";
            output += "ratings: " + entry.getValue() + "\n";
            output += "average:"  + averageRating.get(entry.getKey()) + "\n\n";
        }
        return  output;
    }
}
