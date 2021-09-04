import javax.swing.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.Component;

import java.io.FileInputStream;
import java.io.IOException;

import java.util.Scanner;

public class WordApp {
	// shared variables
	static int noWords;
	static int totalWords;

	static int frameX = 1000;
	static int frameY = 600;
	static int yLimit = 480;

	static WordDictionary dict = new WordDictionary(); // use default dictionary, to read from file eventually

	static WordRecord[] words;
	static volatile boolean done; // must be volatile
	static volatile boolean gameIsPlaying = false; // must be volatile
	static Score score = new Score();
	static Color red = new Color(222, 10, 2);
	private static String currentWord = "";
	static Color background = Color.white;
	static Color text = new Color(24, 26, 27);

	// jcomponents used in setupGUI
	static JFrame frame;
	static JPanel g;
	static WordPanel w;
	static JPanel txt;
	static JPanel b;

	public static void setupGUI(int frameX, int frameY, int yLimit) {
		// Frame init and dimensions
		frame = new JFrame("WordGame");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(frameX, frameY);
		g = new JPanel();
		g.setLayout(new BoxLayout(g, BoxLayout.PAGE_AXIS));
		g.setSize(frameX, frameY);

		w = new WordPanel(words, yLimit);
		w.setSize(frameX, yLimit + 100);
		g.add(w);

		txt = new JPanel();
		txt.setLayout(new BoxLayout(txt, BoxLayout.LINE_AXIS));
		JLabel caught = new JLabel("Caught: " + score.getCaught() + "    ");
		JLabel missed = new JLabel("Missed:" + score.getMissed() + "    ");
		JLabel scr = new JLabel("Score:" + score.getScore() + "    ");
		txt.add(caught);
		txt.add(missed);
		txt.add(scr);

		final JTextField textEntry = new JTextField("", 20);
		textEntry.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				String text = textEntry.getText();
				currentWord = text;
				textEntry.setText("");
				textEntry.requestFocus();
			}
		});
		textEntry.setEnabled(false);

		txt.add(textEntry);
		txt.setMaximumSize(txt.getPreferredSize());
		g.add(txt);

		b = new JPanel();
		b.setLayout(new BoxLayout(b, BoxLayout.LINE_AXIS));

		JButton endB = new JButton("End Game");
		// add the listener to the jbutton to handle the "pressed" event
		endB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				endGame();
				textEntry.requestFocus(); // return focus to the text entry field
			}
		});
		endB.setEnabled(false);

		JButton pauseB = new JButton("Pause");
		// add the listener to the jbutton to handle the "pressed" event
		pauseB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// toggle resume/pause
				if (gameIsPlaying) {
					textEntry.setEnabled(false); // disable text field
					gameIsPlaying = false; // let threads know that game has paused
					pauseB.setText("Resume");
				} else {
					textEntry.setEnabled(true); // enable text field
					gameIsPlaying = true; // let threads know that game has started
					pauseB.setText("Pause");
				}
				textEntry.requestFocus(); // return focus to the text entry field
			}
		});
		pauseB.setEnabled(false);

		JButton startB = new JButton("Start Game");
		// add the listener to the jbutton to handle the "pressed" event
		startB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gameIsPlaying = true; // let threads know that game has started
				textEntry.setEnabled(true); // enable text field
				endB.setEnabled(true);
				pauseB.setEnabled(true);
				textEntry.requestFocus(); // return focus to the text entry field
			}
		});

		JButton quitB = new JButton("QUIT");
		// add the listener to the jbutton to handle the "pressed" event
		quitB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		quitB.setForeground(red);

		JButton toggleB = new JButton("Toggle Appearance");
		// add the listener to the jbutton to handle the "pressed" event
		toggleB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				toggleAppearance();
				textEntry.requestFocus(); // return focus to the text entry field
			}
		});

		// left align toggle
		b.add(toggleB);
		b.add(Box.createHorizontalGlue());
		// center first 3 buttons
		b.add(startB);
		b.add(endB);
		b.add(pauseB);
		// right align quit button
		b.add(Box.createHorizontalGlue());
		b.add(quitB);

		g.add(b);

		frame.setLocationRelativeTo(null); // Center window on screen.
		frame.add(g); // add contents to window
		frame.setContentPane(g);
		// frame.pack(); // don't do this - packs it into small space
		frame.setVisible(true);
	}

	public static String[] getDictFromFile(String filename) {
		String[] dictStr = null;
		try {
			Scanner dictReader = new Scanner(new FileInputStream(filename));
			int dictLength = dictReader.nextInt();
			// System.out.println("read '" + dictLength+"'");

			dictStr = new String[dictLength];
			for (int i = 0; i < dictLength; i++) {
				dictStr[i] = new String(dictReader.next());
				// System.out.println(i+ " read '" + dictStr[i]+"'"); //for checking
			}
			dictReader.close();
		} catch (IOException e) {
			System.err.println("Problem reading file " + filename + " default dictionary will be used");
		}
		return dictStr;
	}

	public static void endGame() {
		// halt words
		gameIsPlaying = false;

		// display dialog
		int res = JOptionPane.showOptionDialog(frame, endGamePanel(), "End of Game", JOptionPane.YES_NO_OPTION,
				JOptionPane.PLAIN_MESSAGE, null, new String[] { "No, QUIT", "Yes" }, null);

		// see what user chose
		if (res == 0) // if they dont want to play
			System.exit(0);
		else if (res == JOptionPane.CLOSED_OPTION) { // if they click cancel
			if (score.getTotal() == totalWords) { // check if game is over
				System.exit(0);
			}
			gameIsPlaying = true;
		} else { // if they do want to play
			score = new Score();
		}
	}

	public static JPanel endGamePanel() {
		// picture of game
		Icon icon = new ImageIcon("data/scrabble.jpg");
		JLabel iconLbl = new JLabel(icon);

		// text for user prompt
		JLabel lbl = new JLabel("<html><br/>Would you like to play again?</html>");
		JPanel txt = new JPanel();
		txt.add(Box.createHorizontalGlue());
		txt.add(lbl, BorderLayout.CENTER);
		txt.add(Box.createHorizontalGlue());

		// jpanel to center icon and text
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(iconLbl, BorderLayout.NORTH);
		mainPanel.add(txt, BorderLayout.SOUTH);

		return mainPanel;
	}

	public static synchronized String getCurrentWord() {
		return currentWord;
	}

	public static void toggleAppearance() {
		// swap color of text and background
		Color temp = background;
		background = text;
		text = temp;

		// set background colors
		g.setBackground(background);
		txt.setBackground(background);
		b.setBackground(background);

		// set text colors
		for (Component c : txt.getComponents()) {
			if (!(c instanceof JTextField))
				c.setForeground(text);
		}
	}

	public static void main(String[] args) {

		// deal with command line arguments
		totalWords = Integer.parseInt(args[0]); // total words to fall
		noWords = Integer.parseInt(args[1]); // total words falling at any point
		assert (totalWords >= noWords); // this could be done more neatly
		String[] tmpDict = getDictFromFile(args[2]); // file of words
		if (tmpDict != null)
			dict = new WordDictionary(tmpDict);

		WordRecord.dict = dict; // set the class dictionary for the words.

		words = new WordRecord[noWords]; // shared array of current words

		// TODO: [snip]

		setupGUI(frameX, frameY, yLimit);
		// Start WordPanel thread - for redrawing animation
		Thread wordPanelT = new Thread(w);
		wordPanelT.start();

		int x_inc = (int) frameX / noWords;

		// initialize shared array of current words
		for (int i = 0; i < noWords; i++) {
			words[i] = new WordRecord(dict.getNewWord(), i * x_inc, yLimit);
		}

		// Start WordManager threads - to control movement and state of words
		for (int i = 0; i < noWords; i++) {
			WordManager w = new WordManager(words[i], totalWords, noWords);
			new Thread(w).start();
		}
	}
}