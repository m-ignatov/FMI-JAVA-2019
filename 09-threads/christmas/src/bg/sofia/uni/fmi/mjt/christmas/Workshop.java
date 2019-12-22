package bg.sofia.uni.fmi.mjt.christmas;

import java.util.LinkedList;
import java.util.Queue;

public class Workshop {

  private static final int ELVES_COUNT = 26;

  private boolean isChristmasTime;

  private int wishCount;
  private Elf[] elves;
  private Queue<Gift> gifts;

  public Workshop() {
    this.gifts = new LinkedList<>();
    this.elves = new Elf[ELVES_COUNT];
    this.wishCount = 0;
    this.isChristmasTime = false;

    startElves();
  }

  private void startElves() {
    for (int i = 0; i < ELVES_COUNT; i++) {
      elves[i] = new Elf(i, this);
      Thread thread = new Thread(elves[i]);
      thread.start();
    }
  }

  /**
   * Adds a gift to the elves' backlog.
   **/
  public synchronized void postWish(Gift gift) {
    gifts.add(gift);
    wishCount++;

    this.notify();
  }

  /**
   * Returns an array of the elves working in Santa's workshop.
   **/
  public Elf[] getElves() {
    return elves;
  }

  /**
   * Returns the next gift from the elves' backlog that has to be manufactured.
   **/
  public synchronized Gift nextGift() {
    while (!isChristmasTime && gifts.isEmpty()) {
      try {
        this.wait();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    return isChristmasTime ? null : gifts.poll();
  }

  /**
   * Returns the total number of wishes sent to Santa's workshop by the kids.
   **/
  public synchronized int getWishCount() {
    return wishCount;
  }

  /**
   * Sets the Christmas time flag to true
   */
  public synchronized void setChristmasTime() {
    this.isChristmasTime = true;
    this.notifyAll();
  }
}