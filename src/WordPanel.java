import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JLabel;

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
			if (words[i].isActive())
				g.drawString(words[i].getWord(), words[i].getX(), words[i].getY() + 20);
		}

		// update text fields
		Component[] c = WordApp.txt.getComponents();
		((JLabel) (c[0])).setText("Caught: " + WordApp.score.getCaught() + "    ");
		((JLabel) (c[1])).setText("Missed: " + WordApp.score.getMissed() + "    ");
		((JLabel) (c[2])).setText("Score: " + WordApp.score.getScore() + "    ");
	}

	WordPanel(WordRecord[] words, int maxY) {
		this.words = words;
		noWords = words.length;
		done = false;
		this.maxY = maxY;
	}

	@Override
	public void run() {
		// continue refreshing jpanel
		while (!done) {
			repaint();
		}

	}

}
