public class WordManager implements Runnable {
    private WordRecord word;
    private int totalWords;
    private int noWords;
    private int delay;

    public WordManager(WordRecord w, int tot, int num) {
        word = w;
        totalWords = tot;
        noWords = num;
        delay = w.getSpeed();
    }

    public void tick() {
        // if word has been dropped and can not be restarted
        if (!word.isActive())
            return;
        // if word is dropped
        else if (word.dropped()) {
            // increment missed and check if word can be reused
            WordApp.score.missedWord();
            if (WordApp.score.getTotal() + noWords - 1 < totalWords) // missed words+caught words+words on screen
            {
                word.resetWord();
                delay = word.getSpeed(); // change our local record of the speed which is set in the constructor
            } else {
                word.deactivate();
                // check if this is last word
                if (WordApp.score.getTotal() == totalWords)
                    WordApp.endGame("Well done! <br/>Would you like to play again?");
            }
        }
        // check if text field matches this words text
        else if (word.matchWord(WordApp.getCurrentWord())) { // resets word if true
            // increment score and check if word can be reused
            WordApp.score.caughtWord(word.getWord().length());
            if (WordApp.score.getTotal() + noWords - 1 < totalWords) // missed words+caught words+words on screen
            {
                delay = word.getSpeed(); // change our local record of the speed which is set in the constructor
            } else {
                word.deactivate();
                // check if this is last word
                if (WordApp.score.getTotal() == totalWords)
                    WordApp.endGame("Well done! <br/>Would you like to play again?");
            }
        }
        // drop word
        else
            word.drop(1);
    }

    @Override
    public void run() {
        long beforeTime, timeDiff, sleep;
        beforeTime = System.currentTimeMillis();

        while (true) {
            // while game is playing
            if (WordApp.gameIsPlaying) {
                // update word as required
                tick();

                // calculate time that has elapsed
                timeDiff = System.currentTimeMillis() - beforeTime;
                // calculate how long we should wait
                sleep = delay - timeDiff;
                if (sleep < 0) {
                    sleep = 2;
                }

                try {
                    Thread.sleep(sleep);
                } catch (InterruptedException e) {
                    System.out.println(String.format("Thread interrupted: %s", e.getMessage()));
                }
                beforeTime = System.currentTimeMillis();
            }
        }
    }

    public void resetT() {
        word.resetWord();
        word.activate();
    }
}
