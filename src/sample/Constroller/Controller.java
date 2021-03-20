package sample.Constroller;

import GameObject.Blocks.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import sample.Util.BustClass;
import sample.Util.FileWorkwer;
import sample.Util.TimeClass;
import sample.Player.Player;
import sample.Util.PlayerBD;

import java.io.File;
import java.net.URL;
import java.time.Instant;
import java.util.*;
import java.util.List;
import java.util.stream.*;

//Этот интерфейс отвечает за отрисовку
public class Controller implements Initializable {
    public double mouseX;
    public double mouseY;
    //Элементы управления
    public Button btnT1;
    public Button btnT2;
    public Button btnT3;
    public Button btnT4;
    public Button btnDeleteTower;
    public Canvas mainCanvas;
    public Label lblError;
    public AnchorPane pnlStart;
    public Button bntStart;
    public TextField txfName;
    public TableView<Player> tblLeaderBoard;
    public TableColumn<String, Player> ColumnPlayer;
    public TableColumn<Integer, Player> ColumnScore;
    public Button btnExit;
    public Button btnRePlay;
    public AnchorPane paneGameOver;
    public ComboBox<String> cmbLvl = new ComboBox<>();
    public ImageView imgLvl;
    public ComboBox<String> idLvlFilter = new ComboBox<>();

    boolean isBTNClicked;

    //Играем ли
    boolean isPlay = false;
    ArrayList<Block> blocks = new ArrayList<>();
    PlayerBD playerBD = new PlayerBD();
    Player player;
    StarBase starBase;

    boolean isFirstBust = false;
    private Path[] paths;
    double timeForLastEnemyCreate = 0;
    double enemyCreateLate = 0.5;
    int score = 0;
    double money = 50000;

    ArrayList<Button> buttonArrayList = new ArrayList<>();

    String path2lvl = "src/GameObject/Levels/LVL1/lvl1.bin";
    String path2TP = "src/GameObject/Levels/LVL1/TP1.bin";

    String totalLvl;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setCombos();
        Timeline timeline = new Timeline( //Таймер
                new KeyFrame(
                        Duration.millis(40), //Частота обновления
                        this::onTimerTick
                )
        );
        btnSetting();
        createPath();
        initBlocks();
        lblError.setWrapText(true);
        lblError.setTextAlignment(TextAlignment.CENTER);
        timeline.setCycleCount(Timeline.INDEFINITE); //Время действия таймера
        timeline.play(); //Запуск таймера
    }

    private void setCombos(){
        ObservableList<String> lvls = FXCollections.observableArrayList();
        lvls.add("1 уровень");
        lvls.add("2 уровень");
        cmbLvl.setItems(lvls);
        cmbLvl.setValue(lvls.get(0));
        totalLvl = "1 уровень";

        File input = new File("src/GameObject/Levels/LVL1/level1.jpg");
        Image image = new Image(input.toURI().toString());
        imgLvl.setImage(image);

        ObservableList<String> lvls1 = FXCollections.observableArrayList();
        lvls1.add("1 уровень");
        lvls1.add("2 уровень");
        lvls1.add("Все уровни");
        idLvlFilter.setItems(lvls1);
        idLvlFilter.setValue(lvls1.get(0));
    }

    private String getDefaultBTNStyle(){
        return "-fx-background-color: #F5FFFA; -fx-border-width: 1px; -fx-border-color: #000000;";
    }

    private String getClickedBTNStyle(){
        return "-fx-background-color: #C0C0C0; -fx-border-width: 1px; -fx-border-color: #000000;";
    }

    private void btnSetting(){
        btnT1.setText("Цена:500\nУрон:10\nСкорость атаки:0.1\nРадиус:100");
        buttonArrayList.add(btnT1);
        btnT2.setText("Цена:1000\nУрон:75\nСкорость атаки:0.5\nРадиус:150");
        buttonArrayList.add(btnT2);
        btnT3.setText("Цена:2000\nУрон:200\nСкорость атаки:0.75\nРадиус:100");
        buttonArrayList.add(btnT3);
        btnT4.setText("Цена:5000\nУрон:500\nСкорость атаки:1.0\nРадиус:200");
        buttonArrayList.add(btnT4);
        buttonArrayList.add(btnDeleteTower);
        for (var button: buttonArrayList) {
            button.setStyle(getDefaultBTNStyle());
        }
    }

    private void initBlocks() { //Инициализировать блоки
        var starb = blocks.stream()
                .filter(sb -> sb instanceof StarBase)
                .map(sb -> (StarBase) sb)
                .collect(Collectors.toList());
        if(starb.size()!=0){
            blocks.removeAll(starb);
        }
        starBase = new StarBase(paths[0].getlX(), paths[0].getlY()); //Добавить базу на форму
        blocks.add(starBase); //Добавить в список
        var towerp = blocks.stream()
                .filter(tp -> tp instanceof TowerPosition)
                .map(tp -> (TowerPosition) tp)
                .collect(Collectors.toList());
        if(towerp.size()!=0){
            blocks.removeAll(towerp);
        }
        var tp = FileWorkwer.readTP(path2TP);
        blocks.addAll(Arrays.asList(tp));
    }

    private void createPath(){
        var p = blocks.stream()
                .filter(pa -> pa instanceof Path)
                .map(pa -> (Path) pa)
                .collect(Collectors.toList());
        if(p.size()!=0){
            blocks.removeAll(p);
        }
        var arrays = FileWorkwer.readPath(path2lvl);

        paths = new Path[arrays.length];
        for(int i = 0; i < arrays.length; i++){
            paths[i] = new Path(arrays[i].getArrayX(), arrays[i].getArrayY());
            blocks.add(paths[i]);
        }
    }

    private void onEnemyDestroy(Enemy enemy) {
        System.out.println("Враг убит");
        score+= enemy.maxLife / 5;
        money+=enemy.maxLife*1.2;
    }

    private void onTimerTick(ActionEvent actionEvent) { // Действие во время тика
        if(starBase.life<=0){
            gameOver();
            return;
        }
        if(isPlay) {
            money += 0.1;
            btnT1.setDisable(money < 500);
            btnT2.setDisable(money < 1000);
            btnT3.setDisable(money < 2000);
            btnT4.setDisable(money < 5000);
            UpdateState();
        }
        Render();
    }

    boolean isTableWrite = false;

    private void gameOver() {
        paneGameOver.setVisible(true);
        isPlay = false;
        for (var button: buttonArrayList) {
            button.setDisable(true);
        }
        if(!isTableWrite) {
            player.setScore(score);
            playerBD.setLevel(totalLvl);
            playerBD.setPlayers(player);
            ColumnPlayer.setCellValueFactory(new PropertyValueFactory<>("name"));
            ColumnScore.setCellValueFactory(new PropertyValueFactory<>("score"));
            tblLeaderBoard.setItems(playerBD.getPlayers());
            cmbLvl.setValue(totalLvl);
            isTableWrite = true;
        }
    }

    void Render(){
        GraphicsContext graphicsContext2D = mainCanvas.getGraphicsContext2D(); //С помощью этого мы рисуем
        graphicsContext2D.setFill(Color.BLACK); //Залить задник
        graphicsContext2D.fillRect(0 , 0, mainCanvas.getWidth(), mainCanvas.getHeight()); //нарсовать прямоугольник
        for (Block block : blocks) {
            block.Render(graphicsContext2D); //Рисуем блоки
            graphicsContext2D.setLineWidth(1);
        }
        //Нарисовать счет
        RenderUI(graphicsContext2D);
    }

    private void RenderUI(GraphicsContext graphicsContext2D) {
        graphicsContext2D.setFill(Color.GREEN);
        graphicsContext2D.setFont(Font.font("Verdana", 16));
        graphicsContext2D.fillText(String.format("score: %s", score),
                20, 20);
        graphicsContext2D.fillText(String.format("money: %s", (int)money),
                150, 20);
    }

    void UpdateState(){
        var towers = blocks.stream()
                .filter(tower -> tower instanceof Tower)
                .map(tower -> (Tower)tower)
                .collect(Collectors.toList());
        Instant now = Instant.now();
        double delta = 0;//Колво секунд с последнего обновления
        if(TimeClass.lastUpdate != null){
            delta = (double) java.time.Duration.between(TimeClass.lastUpdate, now).toMillis() / 1000;
        }
        double noDamageTime = java.time.Duration.between(TimeClass.lastDamageTime, now).toSeconds();
        System.out.println("Время до усиления врагов:" + (60 - noDamageTime));
        if(noDamageTime >= 60){
            TimeClass.lastDamageTime = Instant.now();
            isFirstBust = true;
            BustClass.addBust();
        }
        int bust = BustClass.getBust();
        System.out.println("Total bust:" + BustClass.getBust());
        generateEnemies(delta); //Генерация врагов

        for (Block block : blocks) {
            block.UpdateState(delta);
        }
        //Аналог linq
        List<LiveBlock> blocksToRemove = blocks.stream(). //запустили процесс
                filter(block -> block instanceof LiveBlock). //фильтр на поиск живых блоков
                map(block -> ((LiveBlock) block)). // приведение блоков
                filter(liveBlock -> liveBlock.life <= 0). //ВЫборка живых блоков
                collect(Collectors.toList()); //Все что совпало в лист
        blocks.removeAll(blocksToRemove); //Удалить мертвые блоки
        TimeClass.lastUpdate = now;
    }

    private void generateEnemies(double delta) {
        var towers = blocks.stream()
                .filter(tower -> tower instanceof Tower)
                .map(tower -> (Tower)tower)
                .collect(Collectors.toList());
        if(towers.size() == 0){
            return;
        }

        if(timeForLastEnemyCreate < enemyCreateLate){
            timeForLastEnemyCreate += delta;
            return;
        }
        Double totalPower = blocks.stream()
                .filter(block -> block instanceof Tower)
                .map(block -> (Tower)block)
                .map(tower -> (1d / tower.fireRate) * tower.power)
                .reduce(0d, (Double::sum));

        int enemyCreateCount = new Random().nextInt(1000);
        if(enemyCreateCount > 100){
            return;
        }
        int enemyMaxLife = (int) Math.ceil(totalPower * 0.8);
        timeForLastEnemyCreate = 0;
        Enemy enemy = new Enemy(paths[0].getfX(), paths[0].getfY(), starBase, paths[0]);
        enemy.setMaxLife(enemyMaxLife * BustClass.getBust());
        blocks.add(enemy);
        System.out.println("Создаю врага");
    }

    public void addTower(int cost, int damage, double fireRate, int radius, Color color, Button btn) {
        if(!isBTNClicked){
            return;
        }
        lblError.setText("");
        if (money < cost) {
            lblError.setText("Недостаточно денег");
            return;
        }
        System.out.println(mouseX + "<-X, Y->" + mouseY);
        var positions = blocks.stream().filter(position -> position instanceof TowerPosition)
                .map(position -> (TowerPosition) position)
                .collect(Collectors.toList());
        for (var position : positions) {
            if((mouseY >= position.y && mouseY <= position.y + 20) && (mouseX >= position.x&&mouseX <= position.x + 20)) {
                System.out.println("Установка");
                if (position.isFreedoom()) {
                    money -= cost;
                    Tower tower = new Tower(
                            position.x + 10,
                            position.y + 10,
                            blocks,
                            this::onEnemyDestroy,
                            position.getNumber());
                    tower.fireRate = fireRate;
                    tower.radius = radius;
                    tower.power = damage;
                    tower.color = color;
                    blocks.add(tower);
                    btn.setStyle(getDefaultBTNStyle());
                    isBTNClicked = false;
                    btnDeleteTower.setDisable(false);
                    position.setFreedoom(false);
                    TimeClass.lastDamageTime = Instant.now();
                    return;
                }
                lblError.setText("Площадка уже занята. Выберите другую");
                return;
            }
        }
        lblError.setText("Вы не выбрали площадку, повторите попытку");
    }

    public void btnAddTower(ActionEvent actionEvent) {
        Button btn = (Button)actionEvent.getTarget();
        lblError.setText("");
        if(isActionsBtn(btn)){
            return;
        }
        isBTNClicked = true;
        btn.setStyle(getClickedBTNStyle());
        System.out.println(btn.getId());
        switch (btn.getId()) {
            case "btnT1" -> {
                btn.setStyle(getClickedBTNStyle());
                mainCanvas.setOnMousePressed(mouseEvent -> {
                    mouseX = mouseEvent.getX();
                    mouseY = mouseEvent.getY();
                    addTower(500, 10, 0.1, 100, Color.WHITE, btn);
                });
            }
            case "btnT2" -> {
                btn.setStyle(getClickedBTNStyle());
                mainCanvas.setOnMousePressed(mouseEvent -> {
                    mouseX = mouseEvent.getX();
                    mouseY = mouseEvent.getY();
                    addTower(1000, 75, 0.5, 150, Color.ORANGE, btn);
                });
            }
            case "btnT3" -> {
                btn.setStyle(getClickedBTNStyle());
                mainCanvas.setOnMousePressed(mouseEvent -> {
                    mouseX = mouseEvent.getX();
                    mouseY = mouseEvent.getY();
                    addTower(2000, 200, 0.75, 100, Color.BLUE, btn);
                });
            }
            case "btnT4" -> {
                btn.setStyle(getClickedBTNStyle());
                mainCanvas.setOnMousePressed(mouseEvent -> {
                    mouseX = mouseEvent.getX();
                    mouseY = mouseEvent.getY();
                    addTower(5000, 500, 1.0, 200, Color.GRAY, btn);
                });
            }
        }
    }

    public void btnDeleteClick(ActionEvent actionEvent) {
        if (isActionsBtn((Button) actionEvent.getTarget())){
            return;
        }
        System.out.println(((Button) actionEvent.getTarget()).getId());
        isBTNClicked = true;
        btnDeleteTower.setStyle(getClickedBTNStyle());
        mainCanvas.setOnMousePressed(mouseEvent -> deleteTower(mouseEvent.getX(), mouseEvent.getY(), btnDeleteTower));
    }
    void deleteTower(double x, double y, Button btn){
        if(!isBTNClicked){
            return;
        }
        btn.setStyle(getDefaultBTNStyle());
        boolean isDelete = false;
        var towers = blocks.stream().filter(tower -> tower instanceof Tower)
                .map(tower -> (Tower) tower)
                .collect(Collectors.toList());
        for (var tower: towers) {
            double gX = tower.x - x;
            double gY = tower.y - y;
            double length = Math.sqrt(gX * gX + gY * gY);
            if((double)(tower.radius / 6) > length){
                var positions = blocks.stream()
                        .filter(pos -> pos instanceof TowerPosition)
                        .map(pos -> (TowerPosition)pos)
                        .filter(pos -> pos.getNumber()==tower.getNumber())
                        .collect(Collectors.toList());
                System.out.println(positions.size());
                positions.get(0).setFreedoom(true);
                blocks.remove(tower);
                isDelete = true;
                money += tower.power * 5;
            }
        }
        if(!isDelete) {
            lblError.setText("");
            lblError.setText("Вы не выбрали башню");
        }
    }

    public void lblOnMouseEntered(MouseEvent mouseEvent) {
        lblError.setScaleX(1.5);
        lblError.setScaleY(1.5);
    }

    public void lblOnMouseExited(MouseEvent mouseEvent) {
        lblError.setScaleX(1);
        lblError.setScaleY(1);
    }

    private boolean isActionsBtn(Button btn){
        var towers = blocks.stream().
                filter(tower -> tower instanceof Tower).
                map(tower -> (Tower)tower).
                collect(Collectors.toList());
        if(towers.size() == 0 && btn.getId().equals("btnDeleteTower")){
            lblError.setText("");
            lblError.setText("У вас нет башен");
            btnDeleteTower.setDisable(true);
            return true;
        }
        for (var button: buttonArrayList) {
            if(!button.equals(btn)){
                button.setStyle(getDefaultBTNStyle());
            }
            else if(button.equals(btn) && isBTNClicked){
                isBTNClicked = false;
                btn.setStyle(getDefaultBTNStyle());
            }
        }
        return false;
    }

    public void bntStartClick(ActionEvent actionEvent) {
        if(txfName.getText().equals("")){
            lblError.setText("Для начала игры введите ваше имя");
            return;
        }
        player = new Player();
        player.setName(txfName.getText());
        isPlay = true;
        pnlStart.setVisible(false);
        txfName.clear();
    }

    public void btnExitClick(ActionEvent actionEvent) {
        playerBD.closeConnect();
        System.exit(-1);
    }

    public void btnRePlayClick(ActionEvent actionEvent) {
        pnlStart.setVisible(true);
        paneGameOver.setVisible(false);
        blocks.clear();
        initBlocks();
        btnSetting();
        money = 50000;
        score = 0;
        isTableWrite = false;
    }

    public void cmbLvlClick(ActionEvent actionEvent) {
        File input = null;
        switch (cmbLvl.getValue()){
            case "1 уровень"->{
                path2lvl = "src/GameObject/Levels/LVL1/lvl1.bin";
                path2TP = "src/GameObject/Levels/LVL1/TP1.bin";
                input = new File("src/GameObject/Levels/LVL1/level1.jpg");
                totalLvl = "1 уровень";
            }
            case "2 уровень"->{
                path2lvl = "src/GameObject/Levels/LVL2/lvl2.bin";
                path2TP = "src/GameObject/Levels/LVL2/TP2.bin";
                input = new File("src/GameObject/Levels/LVL2/level2.jpg");
                totalLvl = "2 уровень";
            }
        }
        Image image = new Image(input.toURI().toString());
        imgLvl.setImage(image);
        createPath();
        initBlocks();
        Render();
    }

    public void idLvlFilterClick(ActionEvent actionEvent) {
        switch (idLvlFilter.getValue()) {
            case "1 уровень", "2 уровень" -> tblLeaderBoard.setItems(playerBD.getPlayers("where level = '" + idLvlFilter.getValue() + "'"));
            default -> tblLeaderBoard.setItems(playerBD.getPlayers(""));
        }

    }
}
