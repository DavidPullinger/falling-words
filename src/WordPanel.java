import java.awt.Color;
import java.awt.Graphics;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;

import javax.swing.JButton;
import javax.swing.JPanel;

public class WordPanel extends JPanel implements Runnable {
	public static volatile boolean done;
	private WordRecord[] words;
	private int noWords;
	private int maxY;

	public void paintComponent(Graphics g) {
		int width = getWidth();
		int height = getHeight();
		g.clearRect(0, 0, width, height);
		g.setColor(Color.red);
		g.fillRect(0, maxY - 10, width, height);

		g.setColor(Color.black);
		g.setFont(new Font("Helvetica", Font.PLAIN, 26));
		// draw the words
		for (int i = 0; i < noWords; i++) {
			// +20 is y-offset for skeleton so that you can see the words
			if (words[i] != null)
				g.drawString(words[i].getWord(), words[i].getX(), words[i].getY() + 20);
			else
				System.out.println("null");
		}

	}

	WordPanel(WordRecord[] words, int maxY) {
		this.words = words;
		noWords = words.length;
		done = false;
		this.maxY = maxY;
	}

	public void run() {
		// continue refreshing jpanel
		while (!done) {
			words = WordApp.words;
			repaint();
		}

	}

}
