package sample.Player;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

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
}
