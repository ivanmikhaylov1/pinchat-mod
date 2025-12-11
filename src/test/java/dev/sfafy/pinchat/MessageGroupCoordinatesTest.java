package dev.sfafy.pinchat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для проверки координат и масштаба групп сообщений
 */
@DisplayName("Тесты координат и масштаба MessageGroup")
public class MessageGroupCoordinatesTest {

    @ParameterizedTest
    @CsvSource({
        "0, 0, 1.0",
        "100, 200, 1.5",
        "-100, -200, 0.5",
        "9999, 9999, 3.0",
        "10, 20, 1.0",
        "50, 75, 2.0",
        "-50, -75, 0.1"
    })
    @DisplayName("Проверка создания группы с различными координатами и масштабом")
    void testGroupWithVariousCoordinates(int x, int y, double scale) {
        MessageGroup group = new MessageGroup("Test", x, y, scale);
        
        assertEquals(x, group.x, "Координата X должна быть установлена");
        assertEquals(y, group.y, "Координата Y должна быть установлена");
        assertEquals(scale, group.scale, 0.001, "Масштаб должен быть установлен");
    }

    @Test
    @DisplayName("Проверка изменения координат")
    void testModifyCoordinates() {
        MessageGroup group = new MessageGroup("Test", 0, 0, 1.0);
        
        group.x = 100;
        group.y = 200;
        
        assertEquals(100, group.x);
        assertEquals(200, group.y);
    }

    @Test
    @DisplayName("Проверка изменения масштаба")
    void testModifyScale() {
        MessageGroup group = new MessageGroup("Test", 0, 0, 1.0);
        
        group.scale = 2.5;
        assertEquals(2.5, group.scale, 0.001);
        
        group.scale = 0.5;
        assertEquals(0.5, group.scale, 0.001);
    }

    @Test
    @DisplayName("Проверка работы с отрицательными координатами")
    void testNegativeCoordinates() {
        MessageGroup group = new MessageGroup("Test", -100, -200, 1.0);
        
        assertEquals(-100, group.x);
        assertEquals(-200, group.y);
        
        group.x = -50;
        group.y = -75;
        assertEquals(-50, group.x);
        assertEquals(-75, group.y);
    }

    @Test
    @DisplayName("Проверка работы с очень большими координатами")
    void testLargeCoordinates() {
        MessageGroup group = new MessageGroup("Test", 10000, 20000, 1.0);
        
        assertEquals(10000, group.x);
        assertEquals(20000, group.y);
    }

    @Test
    @DisplayName("Проверка работы с очень маленьким масштабом")
    void testVerySmallScale() {
        MessageGroup group = new MessageGroup("Test", 0, 0, 0.001);
        
        assertEquals(0.001, group.scale, 0.0001);
    }

    @Test
    @DisplayName("Проверка работы с очень большим масштабом")
    void testVeryLargeScale() {
        MessageGroup group = new MessageGroup("Test", 0, 0, 10.0);
        
        assertEquals(10.0, group.scale, 0.001);
    }

    @Test
    @DisplayName("Проверка одновременного изменения всех параметров")
    void testModifyAllParameters() {
        MessageGroup group = new MessageGroup("Test", 0, 0, 1.0);
        
        group.name = "Modified";
        group.x = 50;
        group.y = 75;
        group.scale = 1.25;
        group.isCollapsed = true;
        
        assertEquals("Modified", group.name);
        assertEquals(50, group.x);
        assertEquals(75, group.y);
        assertEquals(1.25, group.scale, 0.001);
        assertTrue(group.isCollapsed);
    }
}

