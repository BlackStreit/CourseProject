package sample.Player;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

public class PlayerBD {
    private ObservableList<Player> players = FXCollections.observableArrayList();

    public ObservableList<Player> getPlayers() {
        return players;
    }

    public void setPlayers(ObservableList<Player> players) {
        this.players = players;
    }
    public void setPlayers(Player player) {
        players.add(player);
    }
    public void setPlayers(List<Player> players){
        this.players = FXCollections.observableArrayList();
        this.players.addAll(players);
    }
    public void setPlayers(ArrayList<Player> players){

    }
}
