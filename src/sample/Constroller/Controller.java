package sample.Constroller;
//TODO 1)реализовать обратный буст у врагов
//TODO 2)Отсортировать список игроков
//
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import sample.Blocks.*;
import sample.Player.Player;
import sample.Player.PlayerBD;

import java.net.URL;
import java.time.Instant;
import java.util.*;
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

    boolean isBTNClicked;

    //Играем ли
    boolean isPlay = false;
    ArrayList<Block> blocks = new ArrayList<>();
    PlayerBD playerBD = new PlayerBD();
    Player player;
    StarBase starBase;
    //Это класс, отвечающий за время

    int lifeBust = 1;
    boolean isFirstBust = false;

    double timeForLastEnemyCreate = 0;
    double enemyCreateLate = 0.5;
    int score = 0;
    double money = 50000;

    ArrayList<Button> buttonArrayList = new ArrayList<>();
    private Instant lastUpdate = null;
    private Instant lastDamageTime = Instant.now();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Timeline timeline = new Timeline( //Таймер
                new KeyFrame(
                        Duration.millis(40), //Частота обновления
                        this::onTimerTick
                )
        );
        btnSetting();
        lblError.setWrapText(true);
        lblError.setTextAlignment(TextAlignment.CENTER);
        timeline.setCycleCount(Timeline.INDEFINITE); //Время действия таймера
        timeline.play(); //Запуск таймера
        initBlocks();
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
        starBase = new StarBase(mainCanvas.getWidth() / 2, mainCanvas.getHeight() / 2); //Добавить базу на форму
        blocks.add(starBase); //Добавить в список
        //Верхняя часть
        blocks.add(new TowerPosition(100,40, 0));
        blocks.add(new TowerPosition(300,40, 1));
        blocks.add(new TowerPosition(500,40, 2));
        blocks.add(new TowerPosition(700,40, 3));
        //Правая часть
        blocks.add(new TowerPosition(700,250, 4));
        blocks.add(new TowerPosition(700,460, 5));
        blocks.add(new TowerPosition(700,670, 6));
        //Нижняя часть
        blocks.add(new TowerPosition(130,670, 9));
        blocks.add(new TowerPosition(320,670, 8));
        blocks.add(new TowerPosition(510,670, 7));
        //Левая часть
        blocks.add(new TowerPosition(130,500, 10));
        blocks.add(new TowerPosition(130,330, 11));
        blocks.add(new TowerPosition(130,160, 12));
        //Верх второй уровень
        blocks.add(new TowerPosition(350,160, 13));
        blocks.add(new TowerPosition(570,160, 14));
        //Финальаня часть
        blocks.add(new TowerPosition(mainCanvas.getWidth() / 2 - 100, mainCanvas.getHeight() / 2 - 100, 15));
        blocks.add(new Path());

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
            if(playerBD.getPlayers().size()>1){
                //Тут должна быть сортировка
                System.out.println();
            }
            player.setScore(score);
            playerBD.setPlayers(player);
            ColumnPlayer.setCellValueFactory(new PropertyValueFactory<>("name"));
            ColumnScore.setCellValueFactory(new PropertyValueFactory<>("score"));
            tblLeaderBoard.setItems(playerBD.getPlayers());
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
        if(lastUpdate != null){
            delta = (double) java.time.Duration.between(lastUpdate, now).toMillis() / 1000;
        }
        double noDamageTime = java.time.Duration.between(lastDamageTime, now).toSeconds();
        System.out.println("Время до усиления врагов:" + (60 - noDamageTime));
        if(noDamageTime >= 60){
            lastDamageTime = Instant.now();
            isFirstBust = true;
            lifeBust += 1;
        }
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
        lastUpdate = now;
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

        Integer totalEnemyPower = blocks.stream().filter(block -> block instanceof Enemy).
                map(enemy -> (Enemy) enemy).
                map(enemy -> enemy.maxLife).
                reduce(0, Integer::sum);
        Double totalPower = blocks.stream()
                .filter(block -> block instanceof Tower)
                .map(block -> (Tower)block)
                .map(tower -> (1d / tower.fireRate) * tower.power)
                .reduce(0d, (Double::sum));
        int enemyCreateCount = new Random().nextInt(1000);
        if(totalEnemyPower >= totalPower && enemyCreateCount > 100){
            return;
        }
        int enemyMaxLife = ((int)(totalPower - totalEnemyPower));
        System.out.println(enemyMaxLife);
        if(enemyMaxLife <= 0){
            int max = 0;
            var enemies = blocks.stream()
                    .filter(enemy -> enemy instanceof Enemy)
                    .map(enemy -> (Enemy)enemy)
                    .collect(Collectors.toList());
            for (var enemy:enemies){
                if(enemy.maxLife>max){
                    max = enemy.maxLife;
                }
            }
            enemyMaxLife = max;
        }
        timeForLastEnemyCreate = 0;
        Enemy enemy = new Enemy(0, 120, starBase);
        if(isFirstBust) {
            enemy.setMaxLife(enemyMaxLife * lifeBust);
            isFirstBust = false;
        }
        else{
            enemy.setMaxLife(enemyMaxLife);
        }
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
                    lastDamageTime = Instant.now();
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
        System.exit(-1);
    }

    public void btnRePlayClick(ActionEvent actionEvent) {
        pnlStart.setVisible(true);
        paneGameOver.setVisible(false);
        blocks.clear();
        initBlocks();
        btnSetting();
        money = 500;
        score = 0;
        isTableWrite = false;
    }
}
