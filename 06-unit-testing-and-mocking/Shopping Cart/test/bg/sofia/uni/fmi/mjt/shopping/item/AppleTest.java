package bg.sofia.uni.fmi.mjt.shopping.item;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotEquals;

public class AppleTest {

    private static final String NAME = "Apple";
    private static final String DESCRIPTION = "Healthy";
    private static final double PRICE = 2.00;

    private Item apple;

    @Before
    public void setup() {
        apple = new Apple(NAME, DESCRIPTION, PRICE);
    }

    @After
    public void cleanup() {
        apple = null;
    }

    @Test
    public void givenSecondAppleWhenEqualsCalledThenNotEquals() {
        Item apple2 = new Apple(NAME, DESCRIPTION, PRICE + 1);
        assertNotEquals(apple, apple2);
    }
}
