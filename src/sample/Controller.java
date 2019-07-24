package sample;

import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Ellipse;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

public class Controller {
    private static int killed, death;
    private static int level = 40;
    public Pane pane;
    public Button player, enemy1, enemy2, enemy3, enemy4, enemy5, enemy6, enemy7, enemy8, enemy9, enemy10, enemy11, enemy12, enemy13, enemy14, enemy15, enemy16;
    public Label message2, message3, level_label;
    private ArrayList<Button> enimes = new ArrayList<>();
    private ArrayList<Ellipse> player_shots = new ArrayList<>();
    private ArrayList<Ellipse> enemy_shots = new ArrayList<>();
    private BooleanProperty upPressed = new SimpleBooleanProperty();
    private BooleanProperty rightPressed = new SimpleBooleanProperty();
    private BooleanProperty leftPressed = new SimpleBooleanProperty();
    private BooleanBinding anyPressed = upPressed.or(rightPressed).or(leftPressed);
    private boolean won, lost = false;
    private Timeline fiveSecondsWonder;
    ///


    public void initialize() {
        print();
        Button[] enemies = {enemy1, enemy2, enemy3, enemy4, enemy5, enemy6, enemy7, enemy8, enemy9, enemy10, enemy11, enemy12, enemy13, enemy14, enemy15, enemy16};
        enimes.addAll(Arrays.asList(enemies));
        player.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.UP || e.getCode() == KeyCode.SPACE) {
                upPressed.set(true);
            }
            if (e.getCode() == KeyCode.RIGHT) {
                rightPressed.set(true);
            }
            if (e.getCode() == KeyCode.LEFT) {
                leftPressed.set(true);
            }
        });
        player.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.UP || e.getCode() == KeyCode.SPACE) {
                upPressed.set(false);
            }
            if (e.getCode() == KeyCode.RIGHT) {
                rightPressed.set(false);
            }
            if (e.getCode() == KeyCode.LEFT) {
                leftPressed.set(false);
            }
        });

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (rightPressed.get()) {
                    if (player.getLayoutX() < pane.getPrefWidth() - player.getWidth() - 3) {
                        {
                            player.setLayoutX(player.getLayoutX() + 3);
                        }
                    }
                }
                if (leftPressed.get()) {
                    if (player.getLayoutX() > 3) {
                        player.setLayoutX(player.getLayoutX() - 3);
                    }
                }
            }
        };
        AnimationTimer timer2 = new AnimationTimerExt(100) {
            @Override
            public void handle() {
                if (upPressed.get()) {
                    shoot_enemy();
                }
            }
        };
        anyPressed.addListener((obs, wasPressed, isNowPressed) -> {
            if (isNowPressed) {
                timer.start();
                timer2.start();
            } else {
                timer.stop();
                timer2.stop();
            }
        });

        fiveSecondsWonder = new Timeline(new KeyFrame(Duration.millis(5), event -> check()));
        fiveSecondsWonder.setCycleCount(Timeline.INDEFINITE);
        fiveSecondsWonder.play();

        Timeline fiveSecondsWonder2 = new Timeline(new KeyFrame(Duration.millis(30), event -> shoot()));
        fiveSecondsWonder2.setCycleCount(Timeline.INDEFINITE);
        fiveSecondsWonder2.play();

        animate();
    }

    private void shoot_enemy() {
        Ellipse shot = new Ellipse();
        pane.getChildren().add(shot);
        shot.setLayoutX(player.getLayoutX() + player.getWidth() / 2);
        shot.setLayoutY(player.getLayoutY() - 21);
        shot.setRadiusX(3);
        shot.setRadiusY(21);
        shot.setFill(Paint.valueOf("#ff1f1f"));
        player_shots.add(shot);
        Duration duration = Duration.millis(800);
        TranslateTransition transition = new TranslateTransition(duration, shot);
        transition.setByY(-pane.getHeight());
        transition.play();
    }

    private void shoot_player(Button enemy) {
        Ellipse shot = new Ellipse();
        pane.getChildren().add(shot);
        shot.setLayoutX(enemy.getBoundsInParent().getMinX() + +18.5);
        shot.setLayoutY(enemy.getBoundsInParent().getMinY() + 13.5 + 21 + 20);
        shot.setRadiusX(3);
        shot.setRadiusY(21);
        shot.setFill(Paint.valueOf("#00a9ff"));
        enemy_shots.add(shot);
        Duration duration = Duration.millis(1500);
        TranslateTransition transition = new TranslateTransition(duration, shot);
        transition.setByY(pane.getHeight());
        transition.play();
    }

    private void animate() {
        Timeline fiveSecondsWonder3 = new Timeline(new KeyFrame(Duration.seconds(2), event -> move_x()));
        fiveSecondsWonder3.setCycleCount(Timeline.INDEFINITE);
        fiveSecondsWonder3.play();

        for (Button enemy : enimes) {
            Duration duration = Duration.millis(50000);
            TranslateTransition transition = new TranslateTransition(duration, enemy);
            transition.setByY(player.getLayoutY());
            transition.play();
        }
    }

    private void check() {
        for (int i = 0; i < enemy_shots.size(); i++) {
            Ellipse shot = enemy_shots.get(i);
            if (shot.getBoundsInParent().intersects(player.getBoundsInParent())) {
                lost();
            }
            if (shot.getBoundsInParent().getMinY() >= pane.getHeight()) {
                pane.getChildren().remove(shot);
                enemy_shots.remove(shot);
            }
        }
        label1:
        for (int i = 0; i < enimes.size(); i++) {
            Button enemy = enimes.get(i);
            for (int i1 = 0; i1 < player_shots.size(); i1++) {
                Ellipse shot = player_shots.get(i1);
                if (shot.getBoundsInParent().intersects(enemy.getBoundsInParent())) {
                    pane.getChildren().remove(shot);
                    player_shots.remove(shot);
                    enimes.remove(enemy);
                    pane.getChildren().remove(enemy);
                    killed++;
                    print();
                    if (enimes.size() == 0) {
                        lost = false;
                        won();
                        break label1;
                    }
                }
                if (shot.getBoundsInParent().getMinY() <= 0) {
                    pane.getChildren().remove(shot);
                    player_shots.remove(shot);
                }
            }
            if (enemy.getBoundsInParent().getMinY() >= player.getLayoutY() - player.getHeight()) {
                death++;
                print();
                lost();
            }
        }
    }

    private void print() {
        message2.setText("Enemies Killed : " + killed);
        message3.setText("Death : " + death);
        level_label.setText("Level : " + level);
    }

    private void shoot() {
        int z = 0;
        int y = 0;
        ArrayList<Button> enemies = enimes;
        Collections.shuffle(enemies);
        for (Button enemy : enemies) {
            if (y <= 6) {
                if (1 >= new Random().nextInt(5000) + 2 + z - killed - (level * 2)) {
                    z = z + 15;
                    y++;
                    shoot_player(enemy);
                }
            }
        }
    }

    private void move_x() {
        int x = new Random().nextInt(50)-25;
        for (Button enemy : enimes) {
            Duration duration1 = Duration.millis(2000);
            TranslateTransition transition1 = new TranslateTransition(duration1, enemy);
            transition1.setByX(x);
            transition1.play();
        }
    }

    private void lost() {
        if (!won) {
            death++;
            level = 1;
            again();
            lost = true;
        }
    }

    private void won() {
        if (!lost) {
            again();
            level++;
            print();
        }
    }

    private void again() {
        fiveSecondsWonder.stop();

        pane.getChildren().removeAll(player_shots);
        player_shots.clear();

        pane.getChildren().removeAll(enemy_shots);
        enemy_shots.clear();

        pane.getChildren().removeAll(enimes);
        enimes.clear();

        won = false;
        lost = false;
        leftPressed.set(false);
        rightPressed.set(false);
        upPressed.set(false);
        killed = 0;

        print();
        reset_position();
        animate();
        fiveSecondsWonder.play();
    }

    private void reset_position() {
        player.setLayoutX(446);
        player.setLayoutY(579);

        Button[] enemies = {enemy1, enemy2, enemy3, enemy4, enemy5, enemy6, enemy7, enemy8, enemy9, enemy10, enemy11, enemy12, enemy13, enemy14, enemy15, enemy16};
        for (Button enemy : enemies) {
            Button enemy_temp = new Button();
            enemy_temp.setLayoutY(enemy.getLayoutY());
            enemy_temp.setLayoutX(enemy.getLayoutX());
            enemy_temp.setPrefHeight(enemy.getHeight());
            enemy_temp.setPrefWidth(enemy.getWidth());
            enemy_temp.setFocusTraversable(false);
            enemy_temp.setStyle("-fx-background-color: green;");
            enimes.add(enemy_temp);
        }
        pane.getChildren().addAll(enimes);
    }
}

abstract class AnimationTimerExt extends AnimationTimer {

    private long sleepNs;

    private long prevTime = 0;

    AnimationTimerExt(long sleepMs) {
        this.sleepNs = sleepMs * 1_000_000;
    }

    @Override
    public void handle(long now) {
        // some delay
        if ((now - prevTime) < sleepNs) {
            return;
        }
        prevTime = now;
        handle();
    }

    abstract void handle();
}