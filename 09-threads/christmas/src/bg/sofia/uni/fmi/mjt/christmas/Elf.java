package bg.sofia.uni.fmi.mjt.christmas;

public class Elf implements Runnable {

  private int giftsCrafted;
  private int id;
  private Workshop workshop;

  public Elf(int id, Workshop workshop) {
    this.id = id;
    this.workshop = workshop;
    this.giftsCrafted = 0;
  }

  /**
   * Gets a wish from the backlog and creates the wanted gift.
   **/
  public  void craftGift() {
    Gift gift;
    while ((gift = workshop.nextGift()) != null) {
      try {
        Thread.sleep(gift.getCraftTime());
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      giftsCrafted++;
    }
  }

  /**
   * Returns the total number of gifts that the given elf has crafted.
   **/
  public int getTotalGiftsCrafted() {
    return giftsCrafted;
  }

  @Override
  public void run() {
    craftGift();
  }
}