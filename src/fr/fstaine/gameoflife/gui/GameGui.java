package fr.fstaine.gameoflife.gui;

import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;

import fr.fstaine.gameoflife.game.Game;
import fr.fstaine.gameoflife.game.State;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class GameGui extends Application implements Observer {
	
	private int size = 50;
	private int windowSize = 1000;
	private int delay = 200;

	private double ratio;

	private Canvas canvas;
	private GraphicsContext gc;
	
	private Game game;
	Timer timer;
	private boolean started = false;

	@Override
	public void start(Stage primaryStage) {
		Map<String, String> params = getParameters().getNamed();
		size = Integer.parseInt(params.getOrDefault("size", String.valueOf(size)));
		windowSize = Integer.parseInt(params.getOrDefault("window", String.valueOf(windowSize)));
		delay = Integer.parseInt(params.getOrDefault("delay", String.valueOf(delay)));
		
		primaryStage.setTitle("Game of life");
		Group root = new Group();
		canvas = new Canvas(windowSize, windowSize);
		gc = canvas.getGraphicsContext2D();
		gc.setStroke(Color.GREY);
		root.getChildren().add(canvas);
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.show();
		initGame();
		
		scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
            	if (event.getCode() == KeyCode.ENTER) {
            		if (started) {
            			timer.cancel();
            		} else {
            			startGame();
            		}
            		started = !started;
            	} else if (event.getCode() == KeyCode.C) {
            		game.clean();
            	}
            }
        });
		
		scene.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				int x = (int) ((int) event.getSceneX() / ratio);
				int y = (int) ((int) event.getSceneY() / ratio);
				game.invert(x, y);
			}
		});
		
		primaryStage.setOnCloseRequest(e -> {
			if (started) {
				timer.cancel();
			}
		});
	}
	
	private void initGame() {
		game = new Game(100);
		game.addObserver(this);
		ratio = 1.0 * windowSize / size;
	}
	
	private void startGame() {
		timer = new Timer();
		timer.schedule(new TimerTask() {
	        @Override
	        public void run() {
	        	game.update();
	        }
	    }, 0, delay);
	}

	private void drawBoard(State[][] board) {
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				drawCell(i, j, board[i][j]);
			}
		}
	}

	private void drawCell(int i, int j, State state) {
		if (state == State.On) {
			gc.setFill(Color.BLACK);
		} else {
			gc.setFill(Color.WHITE);
		}
		gc.fillRect(ratio * i, ratio * j, ratio, ratio);
	}

	@Override
	public void update(Observable o, Object arg) {
		Game game = (Game) o;
		State[][] board = game.getBoard();
		drawBoard(board);
	}
}
