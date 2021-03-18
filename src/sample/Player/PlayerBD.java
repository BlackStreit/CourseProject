package sample.Player;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class PlayerBD {
    private ObservableList<Player> players = FXCollections.observableArrayList();
    private Connection connection;
    public PlayerBD() {
        var map = FileWorkwer.readSQLData();
        String url = map.get("url");
        String name = map.get("name");
        String password = map.get("password");
        try {
            connection = DriverManager.getConnection(url, name, password);
            System.out.println("Connect to SQL Server");
            read();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public ObservableList<Player> getPlayers() {
        return players;
    }

    public void setPlayers(ObservableList<Player> players) {
        this.players = players;
    }
    public void setPlayers(Player player) {
        players.add(player);
        String sql = "insert play values('"+player.getName()+"', " + player.getScore()+")";
        try {
            Statement statement = connection.createStatement();
            int rows = statement.executeUpdate(sql);
            if(rows > 0){
                System.out.println("Rows");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
    public void closeConnect(){
        try {
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private void read(){
        String sql = "select * from play";
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()){
                String name = resultSet.getString("name");
                int score = resultSet.getInt("score");
                players.add(new Player(name, score));
            }
        } catch (Exception throwables) {
            throwables.printStackTrace();
        }
    }
}
