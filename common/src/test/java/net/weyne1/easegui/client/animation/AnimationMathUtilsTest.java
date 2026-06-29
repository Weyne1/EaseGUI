package net.weyne1.easegui.client.animation;

import net.weyne1.easegui.client.animation.AnimationProfile.PivotPoint;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AnimationMathUtilsTest {

    @Test
    void testClamp() {
        assertEquals(0.5f, AnimationMath.clamp(0.5f, 0.0f, 1.0f));
        assertEquals(0.0f, AnimationMath.clamp(-0.5f, 0.0f, 1.0f));
        assertEquals(1.0f, AnimationMath.clamp(1.5f, 0.0f, 1.0f));
    }

    @Test
    void testLerp() {
        // Начальный масштаб 0.5f, конечный 1.0f. На половине пути должно быть 0.75f
        float result = AnimationMath.lerp(0.5f, 1.0f, 0.5f);
        assertEquals(0.75f, result, 0.0001f);
    }

    @Test
    void testCalculateCurrentOffset() {
        // Начальное смещение 100px. При прогрессе 0.25 (пройдено 25%) остаток смещения должен быть 75px
        float remainingOffset = AnimationMath.calculateCurrentOffset(100f, 0.25f);
        assertEquals(75f, remainingOffset, 0.0001f);

        // В конце анимации смещение строго 0
        assertEquals(0f, AnimationMath.calculateCurrentOffset(100f, 1.0f), 0.0001f);
    }

    @ParameterizedTest
    @CsvSource({
            "TOP_LEFT,     10.0, 10.0",
            "CENTER_RIGHT, 10.0, 110.0",
            "CENTER,       10.0, 60.0"
    })
    void testCalculatePivotX(PivotPoint pivot, float startX, float expectedPivotX) {
        float width = 100f;
        float actualPivotX = AnimationMath.calculatePivotX(pivot, startX, width);
        assertEquals(expectedPivotX, actualPivotX, 0.0001f);
    }

    @ParameterizedTest
    @CsvSource({
            "TOP_CENTER,    20.0, 20.0",
            "BOTTOM_CENTER, 20.0, 70.0",
            "CENTER,        20.0, 45.0"
    })
    void testCalculatePivotY(PivotPoint pivot, float startY, float expectedPivotY) {
        float height = 50f;
        float actualPivotY = AnimationMath.calculatePivotY(pivot, startY, height);
        assertEquals(expectedPivotY, actualPivotY, 0.0001f);
    }

    @ParameterizedTest(name = "Входная альфа {0} -> ожидаемая {1}")
    @CsvSource({
            "0.0,  0.02",  // Ниже порога (0.0) -> безопасный минимум
            "0.01, 0.02",  // Ниже порога (0.01) -> безопасный минимум
            "0.5,  0.5",   // Внутри диапазона -> без изменений
            "1.5,  1.0"    // Выше максимума -> ограничение до 1.0
    })
    void testClampFontAlpha(float inputAlpha, float expectedAlpha) {
        float result = AnimationMath.clampFontAlpha(inputAlpha);
        assertEquals(expectedAlpha, result, 0.0001f);
    }

    @ParameterizedTest(name = "При времени {0} ms и длительности {1} ms прогресс должен быть {2}")
    @CsvSource({
            "-100, 1000, 0f",    // До начала
            "0,    1000, 0f",    // В самом начале
            "1000, 1000, 1f",    // В самом конце
            "1500, 1000, 1f",    // После конца
            "500,  1000, 0.5f"   // В середине
    })
    void testAnimationProgress(long timePassed, long duration, float expectedProgress) {
        float result = AnimationMath.calculateProgress(timePassed, duration, AnimationProfile.EasingType.LINEAR);
        assertEquals(expectedProgress, result, 0.0001f);
    }

    @ParameterizedTest
    @EnumSource(AnimationProfile.EasingType.class)
    void allEasingsReturnZeroAtStart(AnimationProfile.EasingType easing) {
        assertEquals(0.0f, easing.ease(0.0f), 0.0001f);
    }

    @ParameterizedTest
    @EnumSource(AnimationProfile.EasingType.class)
    void allEasingsReturnOneAtEnd(AnimationProfile.EasingType easing) {
        assertEquals(1.0f, easing.ease(1.0f), 0.0001f);
    }
}