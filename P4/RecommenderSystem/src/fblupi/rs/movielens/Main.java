package fblupi.rs.movielens;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        final int NUM_RATINGS = 20;

        Movies movies = new Movies();
        movies.readFile("data/ml-data/u.item");
        Users users = new Users();
        users.readFile("data/ml-data/u.data");

        HashMap<Integer, Integer> ratings = new HashMap<>();

        Random random = new Random();
        Scanner in = new Scanner(System.in);

        for (int i = 0; i < NUM_RATINGS; i++) {
            int idMovie = random.nextInt(movies.size());
            while (ratings.containsKey(idMovie)) {
                idMovie = random.nextInt(movies.size());
            }
            int rating;
            do {
                System.out.println("Movie: " + movies.getName(idMovie));
                System.out.println("Enter your rating (1-5):");
                rating = Integer.parseInt(in.nextLine());
            } while (rating < 0 || rating > 5);

            ratings.put(idMovie, rating);
        }

        Map<Integer, Double> neighbourhoods = users.getNeighbourhoods(ratings, 5);
        //System.out.println(neighbourhoods);
    }
}
