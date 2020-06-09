package model;

import java.util.Observable;

import controller.UpdateType;

public class SearchAlgorithms extends Observable {
	private boolean running;
	// How long the algorithm waits for after each iteration
	private int speed;
	// Flag to notify controller of type of update
	private UpdateType updateType;
	// List that search algorithm is searching through
	private int[] list;
	// Value that search algorithm is looking for
	private int target;

	public SearchAlgorithms(int s) {
		speed = s;
	}

	// Search through list by repeatedly halving it
	public void binarySearch() {
		int lower = 0;
		int upper = list.length - 1;
		int middle;

		while (lower <= upper) {
			threadSleep();

			if (running) {
				middle = lower + (upper - lower) / 2;

				notifyExploredTile(middle);

				if (list[middle] == target) {
					notifyFound(middle);
					return;
				} else if (list[middle] > target)
					upper = middle - 1;
				else
					lower = middle + 1;
			}
		}

		notifyNotFound();
	}

	// Search through list scanning element by element
	public void linearSearch() {
		int i = 0;

		while (i < list.length && list[i] <= target) {
			threadSleep();

			if (running) {
				notifyExploredTile(i);

				if (list[i] == target) {
					notifyFound(i);
					return;
				}

				i++;
			}
		}

		notifyNotFound();
	}

	// Tell controller that no element was found
	private void notifyNotFound() {
		updateType = UpdateType.NOT_FOUND;
		setChanged();
		notifyObservers();
	}

	// Tell controller that the element was found and give it the index for
	// highlighting
	private void notifyFound(int index) {
		updateType = UpdateType.FOUND;
		setChanged();
		notifyObservers(index);
	}

	// Tell controller that that a tile has been explored
	private void notifyExploredTile(int index) {
		updateType = UpdateType.EXPLORING;
		setChanged();
		notifyObservers(index);
	}

	// Wait for a specified amount of time
	private void threadSleep() {
		try {
			Thread.sleep(speed);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void setSpeed(Integer s) {
		speed = s;
	}

	public void setList(int[] l) {
		list = l;
	}

	public void setTarget(int t) {
		target = t;
	}

	public void stop() {
		running = false;
	}

	public void start() {
		running = true;
	}

	public UpdateType getUpdateType() {
		return updateType;
	}
}
