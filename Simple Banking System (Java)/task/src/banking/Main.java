package banking;


public class Main {
    public static final String url = "jdbc:sqlite:/Users/tom/IdeaProjects/Simple Banking System (Java)/Simple Banking System (Java)/task/src/banking/sqlite/java/db.db";
    public static void main(String[] args){
        String url = "jdbc:sqlite:" + args[1];
        SQLManager sqlManager = new SQLManager(url);
        SystemProcessor sp = new SystemProcessor(sqlManager);
        sp.start();
    }


}