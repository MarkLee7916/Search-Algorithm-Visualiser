package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.util.EnumMap;
import java.util.Observable;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;

import controller.TileType;
import controller.UpdateType;

public class View extends Observable {
	// Allows us to access a tile given a position on the grid
	private final JButton[] tiles;
	// Main frame that the GUI runs on
	private final JFrame frame;
	// Main panel that all tiles on the grid are placed on
	private final JPanel grid;
	// The grid's total area is length * length
	private final int length;
	// Options to control visualizer
	private final JPanel options;
	// Displays the target to the user
	private JLabel targetLabel;
	// Maps a tile's type onto the colour it's displayed in
	private final EnumMap<TileType, Color> colorToTileType;
	// Flag to signal what view wants to notify controller about
	private UpdateType updateType;

	public View(int l) {
		length = l;

		colorToTileType = new EnumMap<>(TileType.class);
		buildColorToTileTypeMapping();

		frame = new JFrame("Search Algorithm visualizer");
		grid = new JPanel(new GridLayout(0, length));

		options = new JPanel();
		setupPlayerOptions();

		tiles = new JButton[length];
		setupGridButtons();
		addComponentsToFrame();
		configureFrame();
	}

	// Add tileType-colour pairs to EnumMap
	private void buildColorToTileTypeMapping() {
		colorToTileType.put(TileType.BLANK, Color.WHITE);
		colorToTileType.put(TileType.EXPLORED, Color.PINK);
		colorToTileType.put(TileType.GOAL, Color.GREEN);
	}

	private void setupPlayerOptions() {
		setupRunButton();
		setupShuffleButton();
		setupSpeedSlider();
		setupTargetLabel();
	}

	private void setupTargetLabel() {
		targetLabel = new JLabel();

		options.add(targetLabel);
	}

	private void setupShuffleButton() {
		JButton shuffle = new JButton("Shuffle");

		shuffle.addActionListener(changeEvent -> {
			setChanged();
			updateType = UpdateType.SHUFFLE;
			notifyObservers();
		});

		options.add(shuffle);
	}

	// Set up slider that allows user to configure the speed of the algorithm
	private void setupSpeedSlider() {
		JSlider slider = new JSlider(500, 1500);

		configureSliderSettings(slider);

		slider.addChangeListener(changeEvent -> {
			setChanged();
			updateType = UpdateType.CHANGE_SPEED;
			notifyObservers(slider.getValue());
		});

		options.add(slider);
	}

	private void configureSliderSettings(JSlider slider) {
		slider.setMajorTickSpacing(20);
		slider.setMinorTickSpacing(5);
		slider.setPaintTicks(true);
		slider.setInverted(true);
	}

	// Set up button that allows to user to start and stop the algorithm
	private void setupRunButton() {
		JButton run = new JButton("Start");

		run.addActionListener(actionEvent -> {
			setChanged();

			if (run.getText().equals("Start")) {
				updateType = UpdateType.START;
				run.setText("Stop");
			} else {
				updateType = UpdateType.STOP;
				run.setText("Start");
			}

			notifyObservers();
		});

		options.add(run);
	}

	public UpdateType getUpdateType() {
		return updateType;
	}

	public void updateTargetLabel(int val) {
		targetLabel.setText("Target is " + val);
	}

	public void updateTileColour(TileType tileType, int i) {
		tiles[i].setBackground(colorToTileType.get(tileType));
	}

	private void configureFrame() {
		frame.setSize(2000, 140);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	private void addComponentsToFrame() {
		frame.getContentPane().add(BorderLayout.CENTER, grid);
		frame.getContentPane().add(BorderLayout.NORTH, options);
	}

	// Create buttons and add to panel
	private void setupGridButtons() {
		for (int i = 0; i < length; i++) {
			JButton square = new JButton();
			tiles[i] = square;
			grid.add(square);
			updateTileColour(TileType.BLANK, i);
		}
	}

	public void addNumberToTile(int i, int elem) {
		tiles[i].setText(String.valueOf(elem));
	}

	public void foundMessage() {
		JOptionPane.showMessageDialog(null, "Target found!");
	}

	public void notFoundMessage() {
		JOptionPane.showMessageDialog(null, "Target not found");
	}
}
