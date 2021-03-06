package sample.Util;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import sample.Player.Player;

import java.sql.*;

public class PlayerBD {
    private ObservableList<Player> players = FXCollections.observableArrayList();
    private Connection connection;
    private String level;

    public void setLevel(String level) {
        this.level = level;
    }

    public PlayerBD() {
        var map = FileWorker.readSQLData();
        String url = map.get("url");
        String name = map.get("name");
        String password = map.get("password");
        try {
            connection = DriverManager.getConnection(url, name, password);
            System.out.println("Connect to SQL Server");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public ObservableList<Player> getPlayers(String sql) {
        read(sql);
        return players;
    }
    //Добавить даные в БД
    public void setPlayers(Player player) {
        players.add(player);
        String sql = "insert play values('"+player.getName()+"', " + player.getScore()+", '" + level + "')";
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
    //Разорвать соединений
    public void closeConnect(){
        try {
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
    //Считать данные из бд
    private void read(String sql){
        players = FXCollections.observableArrayList();
        String SQl = "select * from play \n" + sql + " \n order by score desc" ;
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(SQl);
            while (resultSet.next()){
                String name = resultSet.getString("name");
                int score = resultSet.getInt("score");
                players.add(new Player(name, score));
            }
        }
        catch (Exception throwables) {
            throwables.printStackTrace();
        }
    }
}
