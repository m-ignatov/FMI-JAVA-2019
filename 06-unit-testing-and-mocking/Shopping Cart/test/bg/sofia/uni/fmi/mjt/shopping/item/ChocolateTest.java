package bg.sofia.uni.fmi.mjt.shopping.item;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ChocolateTest {

    private static final String NAME = "Milka";
    private static final String DESCRIPTION = "Yummy";
    private static final double PRICE = 3.00;

    private Item chocolate;

    @Before
    public void setup() {
        chocolate = new Chocolate(NAME, DESCRIPTION, PRICE);
    }

    @After
    public void cleanup() {
        chocolate = null;
    }

    @Test
    public void givenSecondChocolateWhenEqualsCalledThenNotEquals() {
        Item chocolate2 = new Chocolate(NAME, DESCRIPTION, PRICE + 1);
        assertNotEquals(chocolate, chocolate2);
    }
}
