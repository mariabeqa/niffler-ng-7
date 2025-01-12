package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.meta.User;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.service.CategoryDbClient;
import guru.qa.niffler.service.SpendDbClient;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.Date;

public class SpendingExtension implements BeforeEachCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(SpendingExtension.class);

    private final SpendDbClient spendDbClient = new SpendDbClient();
    private final CategoryDbClient categoryDbClient = new CategoryDbClient();

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), User.class)
                .ifPresent(anno -> {
                    Spending[] spendings = anno.spendings();
                    SpendJson spendJson = null;


                    if (spendings.length > 0) {
                        //Проверяем существует ли категория из предусловия
                        //Если такая категория уже есть, то сеттим ее поля в SpendJson
                        CategoryJson category = categoryDbClient
                                .findCategoryByUsernameAndCategoryName(
                                        anno.username(),
                                        spendings[0].category()
                                )
                                .orElse(new CategoryJson(
                                        null,
                                        spendings[0].category(),
                                        anno.username(),
                                        false
                                        )
                                );

                        spendJson = new SpendJson(
                                null,
                                new Date(),
                                category,
                                spendings[0].currency(),
                                spendings[0].amount(),
                                spendings[0].description(),
                                anno.username()
                        );

                        SpendJson createdSpend = spendDbClient.createSpend(spendJson);

                        context.getStore(NAMESPACE).put(
                                context.getUniqueId(),
                                createdSpend
                        );
                    }
                });
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(SpendJson.class);
    }

    @Override
    public SpendJson resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return extensionContext.getStore(SpendingExtension.NAMESPACE).get(
                extensionContext.getUniqueId(),
                SpendJson.class
        );
    }

}
