package controller;
import java.util.Arrays;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ThreadLocalRandom;

import model.SearchAlgorithms;
import view.View;

public class Controller implements Observer {
	private final SearchAlgorithms search;
    private final View view;
	private final int lengthOfList;

	public Controller() {
		int defaultSpeed = 1000;
		lengthOfList = 40;

		search = new SearchAlgorithms(defaultSpeed);
		search.addObserver(this);

		view = new View(lengthOfList);
		view.addObserver(this);

		generateNewList();
	}

	private void generateNewList() {
		int[] list = new int[lengthOfList];
		int target;

		for (int i = 0; i < lengthOfList; i++)
			list[i] = ThreadLocalRandom.current().nextInt(100);

		Arrays.sort(list);
		target = ThreadLocalRandom.current().nextInt(100);

		search.setList(list);
		search.setTarget(target);

		renderNewNumbersInView(list, target);
	}

	// Update the view with new numbers
	private void renderNewNumbersInView(int[] list, int target) {
		for (int i = 0; i < lengthOfList; i++) {
			view.updateTileColour(TileType.BLANK, i);
			view.addNumberToTile(i, list[i]);
		}

		view.updateTargetLabel(target);
	}

	public void runSearchAlgorithm() {
		search.binarySearch();
	}

	@Override
	// Recieves an update from either model or view
	public void update(Observable observable, Object obj) {
		if (observable instanceof SearchAlgorithms)
			updateFromModel(obj);
		else
			updateFromView(obj);
	}

	private void updateFromView(Object obj) {
		switch (view.getUpdateType()) {

		case SHUFFLE:
			generateNewList();
			break;
		case CHANGE_SPEED:
			updateSpeed(obj);
			break;
		case START:
			search.start();
			break;
		case STOP:
			search.stop();
			break;
		default:
			throw new AssertionError("Enum + " + view.getUpdateType() + " doesn't match with any types");
		}
	}

	private void updateFromModel(Object obj) {
		switch (search.getUpdateType()) {

		case EXPLORING:
			updateExploredTile(obj);
			break;
		case FOUND:
			updateFoundStatus(obj);
			break;
		case NOT_FOUND:
			view.notFoundMessage();
			break;
		default:
			throw new AssertionError("Enum + " + search.getUpdateType() + " doesn't match with any types");
		}
	}

	private void updateFoundStatus(Object obj) {
		Integer index = (Integer) obj;

		view.updateTileColour(TileType.GOAL, index);
		view.foundMessage();
	}

	private void updateSpeed(Object obj) {
		Integer speed = (Integer) obj;

		search.setSpeed(speed);
	}

	// Update view to show that a tile has been explored
	private void updateExploredTile(Object obj) {
		Integer index = (Integer) obj;

		view.updateTileColour(TileType.EXPLORED, index);
	}
}
