package fblupi.rs.movielens;

public class Main {
    public static void main(String[] args) {
        Movies movies = new Movies();
        movies.readFile("data/ml-data/u.item");
        System.out.println(movies.toString());
    }
}
