package guru.qa.niffler.condition;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementCondition;
import com.codeborne.selenide.WebElementsCondition;
import org.apache.commons.lang.ArrayUtils;
import org.eclipse.jetty.util.ArrayUtil;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebElement;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.codeborne.selenide.CheckResult.accepted;
import static com.codeborne.selenide.CheckResult.rejected;

public class StatConditions {

    public static WebElementCondition color(Color expectedColor) {

        return new WebElementCondition("color") {
            @NotNull
            @Override
            public CheckResult check(Driver driver, WebElement webElement) {
                final String rgba = webElement.getCssValue("background-color");
                return new CheckResult(
                        expectedColor.rgb.equals(rgba),
                        rgba
                );
            }
        };
    }

    public static WebElementsCondition color(@Nonnull Color... expectedColors) {
        return new WebElementsCondition() {
            private final  String expectedRgba = Arrays.stream(expectedColors).map(c -> c.rgb).toList().toString();

            @NotNull
            @Override
            public CheckResult check(Driver driver, List<WebElement> elements) {
                if (ArrayUtils.isEmpty(expectedColors)) {
                    throw new IllegalArgumentException("No expected colors given");
                }

                if (expectedColors.length != elements.size()) {
                    String message = String.format("List size mismatch (expected: %s, actual: %s)",
                            expectedColors.length, elements.size());
                    return rejected(message, elements);
                }

                boolean passed = true;
                List<String> actualRgbaList = new ArrayList<>();
                for (int i=0; i<elements.size(); i++) {
                    final WebElement elementToCheck = elements.get(i);
                    final Color colorToCheck = expectedColors[i];
                    final String rgba = elementToCheck.getCssValue("background-color");
                    actualRgbaList.add(rgba);
                    if (passed) {
                        passed = colorToCheck.rgb.equals(rgba);
                    }
                }

                if (!passed) {
                    final String actualRgba = actualRgbaList.toString();
                    String message = String.format("List color mismatch (expected: %s, actual: %s)",
                            expectedRgba, actualRgba
                    );
                    return rejected(message, elements);
                }

                return accepted();
            }

            @Override
            public String toString() {
                return expectedRgba;
            }
        };
    }
}
