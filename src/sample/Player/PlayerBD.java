package sample.Player;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;
import java.sql.*;

public class PlayerBD {
    private ObservableList<Player> players = FXCollections.observableArrayList();
    private Connection connection;
    public PlayerBD() {
        String url = "jdbc:sqlserver://computer;databaseName=player";
        String name = "sa";
        String password = "123";
        try {
            connection = DriverManager.getConnection(url, name, password);
            System.out.println("Connect to SQL Server");
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
    public void setPlayers(List<Player> players){
        this.players = FXCollections.observableArrayList();
        this.players.addAll(players);
    }
    public void setPlayers(ArrayList<Player> players){

    }


}
