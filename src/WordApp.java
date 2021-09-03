import javax.swing.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Color;

import java.io.FileInputStream;
import java.io.IOException;

import java.util.Scanner;
//model is separate from the view.

public class WordApp {
	// shared variables
	static int noWords = 4;
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

	static WordPanel w;
	static JPanel txt;

	public static void setupGUI(int frameX, int frameY, int yLimit) {
		// Frame init and dimensions
		JFrame frame = new JFrame("WordGame");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(frameX, frameY);
		JPanel g = new JPanel();
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

		JPanel b = new JPanel();
		b.setLayout(new BoxLayout(b, BoxLayout.LINE_AXIS));

		JButton startB = new JButton("Start");
		// add the listener to the jbutton to handle the "pressed" event
		startB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gameIsPlaying = true; // let threads know that game has started
				textEntry.setEnabled(true); // enable text field
				textEntry.requestFocus(); // return focus to the text entry field
			}
		});

		JButton endB = new JButton("End");
		// add the listener to the jbutton to handle the "pressed" event
		endB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				endGame();
				textEntry.requestFocus(); // return focus to the text entry field
			}
		});

		JButton pauseB = new JButton("Pause");
		// add the listener to the jbutton to handle the "pressed" event
		pauseB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				textEntry.setEnabled(false); // disable text field
				gameIsPlaying = false; // let threads know that game has paused
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
		// center first 3 buttons
		b.add(Box.createHorizontalGlue());
		b.add(startB);
		b.add(pauseB);
		b.add(endB);
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
		done = true;
		score = new Score();
	}

	public static synchronized String getCurrentWord() {
		return currentWord;
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