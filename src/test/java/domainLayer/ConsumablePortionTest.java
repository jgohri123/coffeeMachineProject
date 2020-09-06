package domainLayer;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ConsumablePortionTest {

    @Test
    public void testInsufficientIngredient() throws InsufficientIngredientQuantityException {
        ConsumablePortion portion = new ConsumablePortion(new Ingredient(""), 100);
        Assertions.assertThrows(InsufficientIngredientQuantityException.class, () -> {
            portion.consume(105);
        });
        assertEquals(100, portion.getQuantity());
    }

    @Test
    public void testConsumesSuccessfully() throws InsufficientIngredientQuantityException {
        ConsumablePortion portion = new ConsumablePortion(new Ingredient(""), 100);

        portion.consume(95);
        assertEquals(5, portion.getQuantity());
    }

    @Test
    public void test() throws InsufficientIngredientQuantityException {
        InsufficientIngredientQuantityException e = Assertions.assertThrows(InsufficientIngredientQuantityException.class, () -> {
            ConsumablePortion portion = new ConsumablePortion(new Ingredient("tea"), 100);
            portion.consume(105);
        });

        Portion portion = e.getCurrentPortion();
        assertEquals("tea", portion.getIngredient().getName());
        assertEquals(100, portion.getQuantity());
    }
}
