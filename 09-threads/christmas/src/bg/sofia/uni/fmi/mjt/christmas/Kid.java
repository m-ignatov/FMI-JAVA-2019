package bg.sofia.uni.fmi.mjt.christmas;

import java.util.Random;

public class Kid implements Runnable {

  private static final int MAX_TIME = 2000;
  private static Random random = new Random();

  private Workshop workshop;

  public Kid(Workshop workshop) {
    this.workshop = workshop;
  }

  @Override
  public void run() {
    try {
      Thread.sleep(random.nextInt(MAX_TIME));
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    workshop.postWish(Gift.getGift());
  }
}