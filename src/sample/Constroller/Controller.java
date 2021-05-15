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
import sample.Util.FileWorker;
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
    public Button btnStop;

    boolean isBTNClicked;

    //Играем ли
    boolean isPlay = false;
    ArrayList<Block> blocks = new ArrayList<>();
    PlayerBD playerBD;
    Player player;
    StarBase starBase;

    boolean isFirstBust = false;
    private Path[] paths;
    double timeForLastEnemyCreate = 0;
    double enemyCreateLate = 0.5;
    int score = 0;
    double money = 500;

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
        playerBD = new PlayerBD();
        lblError.setWrapText(true);
        lblError.setTextAlignment(TextAlignment.CENTER);
        timeline.setCycleCount(Timeline.INDEFINITE); //Время действия таймера
        timeline.play(); //Запуск таймера
    }
    //Заполнить комбоокы значениями
    private void setCombos(){
        //заполнить уровни
        ObservableList<String> lvls = FXCollections.observableArrayList();
        lvls.add("1 уровень");
        lvls.add("2 уровень");
        lvls.add("3 уровень");
        cmbLvl.setItems(lvls);
        cmbLvl.setValue(lvls.get(0));
        totalLvl = "1 уровень";
        //Прочитать 1 уровень
        File input = new File("src/GameObject/Levels/LVL1/level1.jpg");
        Image image = new Image(input.toURI().toString());
        imgLvl.setImage(image);

    }
    //Стиль для обычной кнопки
    private String getDefaultBTNStyle(){
        return "-fx-background-color: #F5FFFA; -fx-border-width: 1px; -fx-border-color: #000000;";
    }
    //Стиль кнопки для нажатой кнопки
    private String getClickedBTNStyle(){
        return "-fx-background-color: #C0C0C0; -fx-border-width: 1px; -fx-border-color: #000000;";
    }
    //Настрока кнопок и установка стилей
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
        //Если в списке уже есть база, удалить
        var starb = blocks.stream()
                .filter(sb -> sb instanceof StarBase)
                .map(sb -> (StarBase) sb)
                .collect(Collectors.toList());
        if(starb.size()!=0){
            blocks.removeAll(starb);
        }
        //Создать новую базу
        starBase = new StarBase(paths[0].getlX(), paths[0].getlY()); //Добавить базу на форму
        blocks.add(starBase); //Добавить в список
        //Удалить все башни
        var towerp = blocks.stream()
                .filter(tp -> tp instanceof TowerPosition)
                .map(tp -> (TowerPosition) tp)
                .collect(Collectors.toList());
        if(towerp.size()!=0){
            blocks.removeAll(towerp);
        }
        //Добавить позиции для башен
        var tp = FileWorker.readTP(path2TP);
        blocks.addAll(Arrays.asList(tp));
    }
    //Построить маршрут для крипов
    private void createPath(){
        //Удалить все предыдущие пути
        var p = blocks.stream()
                .filter(pa -> pa instanceof Path)
                .map(pa -> (Path) pa)
                .collect(Collectors.toList());
        if(p.size()!=0){
            blocks.removeAll(p);
        }
        //Прочитать путь из файла
        var arrays = FileWorker.readPath(path2lvl);
        //Инициализировать путь
        paths = new Path[arrays.length];
        for(int i = 0; i < arrays.length; i++){
            paths[i] = new Path(arrays[i].getArrayX(), arrays[i].getArrayY());
            blocks.add(paths[i]);
        }
    }
    //Событие вызываемое при смерти врага
    private void onEnemyDestroy(Enemy enemy) {
        System.out.println("Враг убит");
        score+= enemy.getMaxLife() / 5;
        money+=enemy.getMaxLife()*1.2;
    }

    private void onTimerTick(ActionEvent actionEvent) { // Действие во время тика
        //Если у базы нет HP
        if(starBase.getLife()<=0){
            gameOver();
            return;
        }
        //Если играем
        if(isPlay) {
            //Изменение состояние кнопки
            btnT1.setDisable(money < 500);
            btnT2.setDisable(money < 1000);
            btnT3.setDisable(money < 2000);
            btnT4.setDisable(money < 5000);
            //Обновить состояние объектов игры
            UpdateState();
        }
        //Перерисовать объекты
        Render();
    }

    boolean isTableWrite = false;

    //Собтиые вызываемое при окончании игры
    private void gameOver() {
        //Показать панель окончания игры
        paneGameOver.setVisible(true);
        isPlay = false;
        //Выключить все кнопки
        for (var button: buttonArrayList) {
            button.setDisable(true);
        }
        //Если данные из таблицы не было заполнены
        if(!isTableWrite) {
            player.setScore(score);
            playerBD.setLevel(totalLvl);
            playerBD.setPlayers(player);
            ColumnPlayer.setCellValueFactory(new PropertyValueFactory<>("name"));
            ColumnScore.setCellValueFactory(new PropertyValueFactory<>("score"));
            tblLeaderBoard.setItems(playerBD.getPlayers("where level = '" + totalLvl +"'"));
            cmbLvl.setValue(totalLvl);
            ObservableList<String> lvls = FXCollections.observableArrayList();
            lvls.add("1 уровень");
            lvls.add("2 уровень");
            lvls.add("3 уровень");
            lvls.add("Все уровни");
            idLvlFilter.setItems(lvls);
            idLvlFilter.setValue(totalLvl);
            isTableWrite = true;
            btnStop.setDisable(true);
        }
    }
    //Событие для отрисовки
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
    //Прописока денег и счета
    private void RenderUI(GraphicsContext graphicsContext2D) {
        graphicsContext2D.setFill(Color.GREEN);
        graphicsContext2D.setFont(Font.font("Verdana", 16));
        graphicsContext2D.fillText(String.format("score: %s", score),
                20, 20);
        graphicsContext2D.fillText(String.format("money: %s", (int)money),
                150, 20);
    }
    //Обновить состояние объектов
    void UpdateState(){
        //Кол-вол денег
        money += 0.1;
        Instant now = Instant.now();
        double delta = 0;//Колво секунд с последнего обновления
        if(TimeClass.lastUpdate != null){
            delta = (double) java.time.Duration.between(TimeClass.lastUpdate, now).toMillis() / 1000;
        }
        double noDamageTime = java.time.Duration.between(TimeClass.lastDamageTime, now).toSeconds();
        System.out.println("Время до усиления врагов:" + (60 - noDamageTime));
        //Усилить врагов, если урона не было 60 секунд
        if(noDamageTime >= 60){
            TimeClass.lastDamageTime = Instant.now();
            isFirstBust = true;
            BustClass.addBust();
        }
        System.out.println("Total bust:" + BustClass.getBust());
        generateEnemies(delta); //Генерация врагов

        for (Block block : blocks) {
            block.UpdateState(delta);
        }
        //Аналог linq
        List<LiveBlock> blocksToRemove = blocks.stream(). //запустили процесс
                filter(block -> block instanceof LiveBlock). //фильтр на поиск живых блоков
                map(block -> ((LiveBlock) block)). // приведение блоков
                filter(liveBlock -> liveBlock.getLife() <= 0). //ВЫборка живых блоков
                collect(Collectors.toList()); //Все что совпало в лист
        blocks.removeAll(blocksToRemove); //Удалить мертвые блоки
        TimeClass.lastUpdate = now;
    }
    //Создать врагов
    private void generateEnemies(double delta) {
        //Не создавать, если нет башен
        var towers = blocks.stream()
                .filter(tower -> tower instanceof Tower)
                .map(tower -> (Tower)tower)
                .collect(Collectors.toList());
        if(towers.size() == 0){
            return;
        }
        //Если не прошло интервала генерации, то не создавать
        if(timeForLastEnemyCreate < enemyCreateLate){
            timeForLastEnemyCreate += delta;
            return;
        }
        //Узнать текущую силу башен
        Double totalPower = blocks.stream()
                .filter(block -> block instanceof Tower)
                .map(block -> (Tower)block)
                .map(tower -> (1d / tower.getFireRate()) * tower.getPower())
                .reduce(0d, (Double::sum));
        //Получить слуайное значение от 0 до 1000
        int enemyCreateCount = new Random().nextInt(1000);
        //Если значение больше 100 не создаем
        if(enemyCreateCount > 100){
            return;
        }
        //Поучить макс HP крипов
        int enemyMaxLife = (int) Math.ceil(totalPower * 0.9);
        timeForLastEnemyCreate = 0;
        Enemy enemy;
        //Содать врага
        if(paths.length == 1) {
            enemy = new Enemy(paths[0].getfX(), paths[0].getfY(), starBase, paths[0]);
        }
        else {
            int rand = new Random().nextInt(100);
            enemy = new Enemy(paths[rand%2].getfX(), paths[rand%2].getfY(), starBase, paths[rand%2]);
        }
        enemy.setMaxLife(enemyMaxLife * BustClass.getBust());
        blocks.add(enemy);
        System.out.println("Создаю врага");
    }
    //Метод для добавления башни
    private void addTower(int cost, int damage, double fireRate, int radius, Color color, Button btn) {
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
        //Поиск нужной позиции для добавления
        for (var position : positions) {
            if((mouseY >= position.getY() && mouseY <= position.getY() + 20) && (mouseX >= position.getX()&&mouseX <= position.getX() + 20)) {
                System.out.println("Установка");
                if (position.isFreedoom()) {
                    money -= cost;
                    Tower tower = new Tower(
                            position.getX() + 10,
                            position.getY() + 10,
                            blocks,
                            this::onEnemyDestroy,
                            position.getNumber());
                    tower.setFireRate(fireRate);
                    tower.setRadius(radius);
                    tower.setPower(damage);
                    tower.setColor(color);
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
    //Событие вызываемое при попытке добавить башню
    public void btnAddTower(ActionEvent actionEvent) {
        Button btn = (Button)actionEvent.getTarget();
        lblError.setText("");
        if(isActionsBtn(btn)){
            return;
        }
        isBTNClicked = true;
        btn.setStyle(getClickedBTNStyle());
        System.out.println(btn.getId());
        //Выбор типа башни
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
    //Собтиые для удаления башни
    public void btnDeleteClick(ActionEvent actionEvent) {
        if (isActionsBtn((Button) actionEvent.getTarget())){
            return;
        }
        System.out.println(((Button) actionEvent.getTarget()).getId());
        isBTNClicked = true;
        btnDeleteTower.setStyle(getClickedBTNStyle());
        mainCanvas.setOnMousePressed(mouseEvent -> deleteTower(mouseEvent.getX(), mouseEvent.getY(), btnDeleteTower));
    }
    //Удаление башни
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
            double gX = tower.getX() - x;
            double gY = tower.getY() - y;
            double length = Math.sqrt(gX * gX + gY * gY);
            if((double)(tower.getRadius() / 6) > length){
                var positions = blocks.stream()
                        .filter(pos -> pos instanceof TowerPosition)
                        .map(pos -> (TowerPosition)pos)
                        .filter(pos -> pos.getNumber()==tower.getNumber())
                        .collect(Collectors.toList());
                System.out.println(positions.size());
                positions.get(0).setFreedoom(true);
                blocks.remove(tower);
                isDelete = true;
                money += tower.getPower() * 5;
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
    //Метод проверяющий нажатость кнопки
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
    //Собтиые при начале игры
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
        btnStop.setDisable(false);
    }
    //Собтиые при закрытии игры
    public void btnExitClick(ActionEvent actionEvent) {
        playerBD.closeConnect();
        System.exit(-1);
    }
    //Событие для переигровки
    public void btnRePlayClick(ActionEvent actionEvent) {
        pnlStart.setVisible(true);
        paneGameOver.setVisible(false);
        blocks.clear();
        initBlocks();
        btnSetting();
        money = 500;
        score = 0;
        isTableWrite = false;
        btnStop.setDisable(false);
        totalLvl = cmbLvl.getValue();
        createPath();
        initBlocks();
        Render();
    }
    //Соббтиые при изменении уровня игры
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
            case "3 уровень"->{
                path2lvl = "src/GameObject/Levels/LVL3/lvl3.bin";
                path2TP = "src/GameObject/Levels/LVL3/TP3.bin";
                input = new File("src/GameObject/Levels/LVL3/level3.jpg");
                totalLvl = "3 уровень";
            }
        }
        Image image = new Image(input.toURI().toString());
        imgLvl.setImage(image);
        createPath();
        initBlocks();
        Render();
    }
    //Событие для изменения уровня в таблице илдеров
    public void idLvlFilterClick(ActionEvent actionEvent) {
        lblError.setText("");
        switch (idLvlFilter.getValue()) {
            case "1 уровень", "2 уровень", "3 уровень" -> {
                var list = playerBD.getPlayers("where level = '" + idLvlFilter.getValue() + "'");
                if(list.size() == 0){
                    lblError.setText("На этом уровне еще никто не играл");
                    return;
                }
                tblLeaderBoard.setItems(playerBD.getPlayers("where level = '" + idLvlFilter.getValue() + "'"));
            }
            default -> tblLeaderBoard.setItems(playerBD.getPlayers(""));
        }

    }
    //Событие при паузе
    public void btnStopClick(ActionEvent actionEvent) {
        if(isPlay){
            isPlay = false;
            for (var btn : buttonArrayList){
                btn.setDisable(true);
            }
            lblError.setText("Игра на паузе");
        }
        else{
            isPlay = true;
            for (var btn : buttonArrayList){
                btn.setDisable(false);
            }
            lblError.setText("");
        }
    }
}
