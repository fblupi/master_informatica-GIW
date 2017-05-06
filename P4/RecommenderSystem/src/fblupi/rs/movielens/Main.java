package fblupi.rs.movielens;

public class Main {
    public static void main(String[] args) {
        Movies movies = new Movies();
        movies.readFile("data/ml-data/u.item");
        Users users = new Users();
        users.readFile("data/ml-data/u.data");
    }
}
