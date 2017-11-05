package fr.fstaine.gameoflife.game;

import java.util.Observable;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Game extends Observable {
	private final int size;
	private final State[][] board1, board2;
	private boolean first = true;
	
	public Game(int size) {
		super();
		this.size = size;
		board1 = new State[size][size];
		board2 = new State[size][size];
		
		clean();
	}
	
	public void clean() {
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				board2[i][j] = board1[i][j] = State.Off;
			}
		}
		setChanged();
		notifyObservers();
	}
	
	public void set(int i, int j, State s) {
		getOther()[i][j] = s;
		getBoard()[i][j] = s;
		setChanged();
		notifyObservers();
	}
	
	public void set(int i, int j) {
		set(i, j, State.On);
	}
	
	public void invert(int i, int j) {
		State s;
		if (getOther()[i][j] == State.On) {
			s = State.Off;
		} else {
			s = State.On;
		}
		set(i, j, s);
	}
	
	public void update() {
		final State[][] next = getBoard();
		final State[][] current = getOther();
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				next[i][j] = update(current, i, j);
			}
		}
		first = !first;
		setChanged();
		notifyObservers();
	}
	
	private State update(State[][] current, int i, int j) {
		Stream<Optional<State>> neighbors = Stream.of(
				get(current, i-1, j-1),
				get(current, i-1, j),
				get(current, i-1, j+1),
				get(current, i, j-1),
				get(current, i, j+1),
				get(current, i+1, j-1),
				get(current, i+1, j),
				get(current, i+1, j+1)
		);
		Long nbNeighbors = neighbors
			    .filter(Optional::isPresent)
			    .map(Optional::get)
			    .filter(e -> e == State.On)
			    .collect(Collectors.counting());
		if (nbNeighbors == 3 || (current[i][j] == State.On && nbNeighbors == 2)) {
			return State.On;
		} else {
			return State.Off;
		}
	}
	
	private Optional<State> get(State[][] current, int i, int j) {
		if (i < 0 || j < 0 || i >= size || j >= size) {
			return Optional.empty();
		} else {
			return Optional.of(current[i][j]);
		}
	}
	
	public State[][] getBoard() {
		return (first ? board1 : board2);
	}
	
	private  State[][] getOther() {
		return (first ? board2 : board1);
	}
}
