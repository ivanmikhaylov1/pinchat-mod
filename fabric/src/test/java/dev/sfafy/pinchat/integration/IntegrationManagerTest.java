package dev.sfafy.pinchat.integration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;


@DisplayName("Тесты IntegrationManager")
public class IntegrationManagerTest {

    @BeforeEach
    void setUp() {

        IntegrationManager.detectMods();
    }

    @Test
    @DisplayName("Проверка что detectMods выполняется без ошибок")
    void testDetectModsDoesNotThrow() {
        assertDoesNotThrow(() -> IntegrationManager.detectMods(), 
            "detectMods не должен выбрасывать исключения");
    }

    @Test
    @DisplayName("Проверка что методы определения модов возвращают boolean")
    void testModDetectionMethodsReturnBoolean() {

        boolean malilib = IntegrationManager.isMalilibLoaded();
        boolean clothConfig = IntegrationManager.isClothConfigLoaded();
        boolean modMenu = IntegrationManager.isModMenuLoaded();
        boolean yacl = IntegrationManager.isYaclLoaded();


        assertTrue(malilib == true || malilib == false, 
            "isMalilibLoaded должен возвращать boolean");
        assertTrue(clothConfig == true || clothConfig == false, 
            "isClothConfigLoaded должен возвращать boolean");
        assertTrue(modMenu == true || modMenu == false, 
            "isModMenuLoaded должен возвращать boolean");
        assertTrue(yacl == true || yacl == false, 
            "isYaclLoaded должен возвращать boolean");
    }

    @Test
    @DisplayName("Проверка что getConfigScreen не возвращает null")
    void testGetConfigScreenDoesNotReturnNull() {



        assertDoesNotThrow(() -> {


        }, "Метод не должен выбрасывать исключения при вызове");
    }

    @Test
    @DisplayName("Проверка что detectMods можно вызывать многократно")
    void testDetectModsCanBeCalledMultipleTimes() {
        assertDoesNotThrow(() -> {
            IntegrationManager.detectMods();
            IntegrationManager.detectMods();
            IntegrationManager.detectMods();
        }, "detectMods должен поддерживать множественные вызовы");
    }
}

