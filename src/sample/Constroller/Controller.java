package sample.Constroller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import sample.Blocks.*;

import java.net.URL;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

//Этот интерфейс отвечает за отрисовку
public class Controller implements Initializable {
    public Canvas mainCanvas;
    public Button btnT1;
    public Button btnT2;
    public Button btnT3;
    public Button btnT4;
    public double mouseX;
    public double mouseY;
    public Button btnDeleteTower;
    public Label lblError;
    boolean isBTNClicked;
    ArrayList<Block> blocks = new ArrayList<>();
    StarBase starBase;
    //Это класс, отвечающий за время
    Instant lastUpdate = null;
    double timeForLastEnemyCreate = 0;
    double enemyCreateLate = 1;
    int score = 0;
    double money = 50000;

    ArrayList<Button> buttonArrayList = new ArrayList<>();
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
        btnT1.setStyle(getDefaultBTNStyle());
        btnT1.setText("Цена:500\nУрон:10\nСкорость атаки:0.1\nРадиус:100");
        buttonArrayList.add(btnT1);
        btnT2.setStyle(getDefaultBTNStyle());
        btnT2.setText("Цена:1000\nУрон:75\nСкорость атаки:0.5\nРадиус:150");
        buttonArrayList.add(btnT2);
        btnT3.setStyle(getDefaultBTNStyle());
        btnT3.setText("Цена:2000\nУрон:200\nСкорость атаки:0.75\nРадиус:100");
        buttonArrayList.add(btnT3);
        btnT4.setStyle(getDefaultBTNStyle());
        btnT4.setText("Цена:5000\nУрон:500\nСкорость атаки:1.0\nРадиус:200");
        buttonArrayList.add(btnT4);
        btnDeleteTower.setStyle(getDefaultBTNStyle());
        buttonArrayList.add(btnDeleteTower);
    }

    private void initBlocks() { //Инициализировать блоки
        starBase = new StarBase(mainCanvas.getWidth() / 2, mainCanvas.getHeight() / 2); //Добавить базу на форму
        blocks.add(starBase); //Добавить в список
    }

    private void onEnemyDestroy(Enemy enemy) {
        System.out.println("Враг убит");
        score+= enemy.maxLife / 5;
        money+=enemy.maxLife*1.2;
    }

    private void onTimerTick(ActionEvent actionEvent) { // Действие во время тика
        money+=0.1;
        btnT1.setDisable(money < 500);
        btnT2.setDisable(money < 1000);
        btnT3.setDisable(money < 2000);
        btnT4.setDisable(money < 5000);
        UpdateState();
        Render();
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
        Instant now = Instant.now();
        double delta = 0;//Колво секунд с последнего обновления
        if(lastUpdate != null){
            delta = (double) java.time.Duration.between(lastUpdate, now).toMillis() / 1000;
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

        if(starBase.life <= 0){ //Если база мертва
            return;
        }

        if(timeForLastEnemyCreate < enemyCreateLate){
            timeForLastEnemyCreate += delta;
            return;
        }

        Integer totalEnemyPower = blocks.stream().filter(block -> block instanceof Enemy).
                map(enemy -> (Enemy) enemy).
                map(enemy -> enemy.maxLife).
                reduce(0, (sum, life) -> sum + life);
        Double totalPower = blocks.stream()
                .filter(block -> block instanceof Tower)
                .map(block -> (Tower)block)
                .map(tower -> (1d / tower.fireRate) * tower.power)
                .reduce(0d, ((aDouble, aDouble2) -> aDouble + aDouble2));

        if(totalEnemyPower >= totalPower){
            return;
        }

        int enemyMaxLife = (int)(totalPower - totalEnemyPower);
        timeForLastEnemyCreate = 0;
        int diractions = ThreadLocalRandom.current().nextInt(0, 360); //Как рандом, только большк

        double x = 0;
        double y = 0;

        if(diractions >= 0 && diractions < 90){ //Если в правой стороне
            x = mainCanvas.getWidth();
            y = ThreadLocalRandom.current().nextInt(0, (int) mainCanvas.getHeight());
        }
        else if(diractions >= 90 && diractions < 180){ //Вверху экрана
            x =  ThreadLocalRandom.current().nextInt(0, (int) mainCanvas.getWidth());
            y = 0;
        }
        else if(diractions >= 180 && diractions < 270){ //Левый край
            x = 0;
            y = ThreadLocalRandom.current().nextInt(0, (int) mainCanvas.getHeight());;
        }
        else if(diractions >= 270 && diractions < 360){ //Левый край
            x = ThreadLocalRandom.current().nextInt(0, (int) mainCanvas.getWidth());
            y = mainCanvas.getHeight();
        }
        Enemy enemy = new Enemy(x, y, starBase);
        enemy.setMaxLife(enemyMaxLife);
        blocks.add(enemy);
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
        var towers = blocks.stream().filter(tower -> tower instanceof Tower)
                .map(tower -> (Tower) tower)
                .collect(Collectors.toList());
        for (var tower : towers) {
            double gX = tower.x - mouseX;
            double gY = tower.y - mouseY;
            double length = Math.sqrt(gX * gX + gY * gY);
            if (tower.radius / 1.5 > length) {
                lblError.setText("Вы не можете установить башню на это место");
                return;
            }
        }
        money -= cost;
        Tower tower = new Tower(
                mouseX,
                mouseY,
                blocks,
                this::onEnemyDestroy);
        tower.fireRate = fireRate;
        tower.radius = radius;
        tower.power = damage;
        tower.color = color;
        blocks.add(tower);
        btn.setStyle(getDefaultBTNStyle());
        isBTNClicked = false;
        btnDeleteTower.setDisable(false);
    }

    public void btnAddTower(ActionEvent actionEvent) {
        Button btn = (Button)actionEvent.getTarget();
        lblError.setText("");
        if(!isActionsBtn(btn)){
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
        if (!isActionsBtn((Button)actionEvent.getTarget())){
            return;
        }
        System.out.println(((Button) actionEvent.getTarget()).getId());
        isBTNClicked = true;
        btnDeleteTower.setStyle(getClickedBTNStyle());
        mainCanvas.setOnMousePressed(mouseEvent -> {
            deleteTower(mouseEvent.getX(), mouseEvent.getY(), btnDeleteTower);
        });
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
            if(tower.radius / 6 > length){
                isDelete = true;
                money += tower.power * 3;
                blocks.remove(tower);
                isBTNClicked = false;
                lblError.setText("");
                break;
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
            return false;
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
        return true;
    }
}
