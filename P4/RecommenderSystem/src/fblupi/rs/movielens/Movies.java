package fblupi.rs.movielens;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Movies {
    private Map<Integer, String> movies;

    public Movies() {
        movies = new HashMap<>();
    }

    public int size() {
        return movies.size();
    }

    public String getName(int id) {
        return movies.get(id);
    }

    public void readFile(String filename) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));

            String line = br.readLine();
            while (line != null) {
                String[] splitLine = line.split("\\|");
                movies.put(Integer.parseInt(splitLine[0]), splitLine[1]);

                line = br.readLine();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        String output = "";
        Iterator entries = movies.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry movie = (Map.Entry) entries.next();
            output += "id: " + movie.getKey() + ", name: " + movie.getValue() + "\n";
        }
        return output;
    }
}
