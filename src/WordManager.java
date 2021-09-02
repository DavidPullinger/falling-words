public class WordManager implements Runnable {
    private WordRecord word;
    private int totalWords;
    private int DELAY = 5;

    public WordManager(WordRecord w, int tot) {
        word = w;
        totalWords = tot;
    }

    public void tick() {
        if (word == null)
            return;
        if (word.dropped()) {
            word.setWord("");
            word = null;
            return;
        }
        word.drop(1);
    }

    @Override
    public void run() {
        long beforeTime, timeDiff, sleep;
        beforeTime = System.currentTimeMillis();

        while (true) {
            // update word as required
            tick();

            // calculate time that has elapsed
            timeDiff = System.currentTimeMillis() - beforeTime;
            // calculate how long we should wait
            sleep = DELAY - timeDiff;

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
