package com.github.vincentrussell.validation;

import com.github.vincentrussell.validation.testClasses.simple.SimpleObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringAnnotationContextTest.Config.class, loader = AnnotationConfigContextLoader.class)
public class SpringAnnotationContextTest {

    @Autowired
    ValidationService validationService;

    @Test
    public void testWithValidatorOnField() {
        SimpleObject object = new SimpleObject();
        ValidationResponse validationResponse = validationService.validate(object);
        assertFalse(validationResponse.isValid());
        object.setField1("not null");
        validationResponse = validationService.validate(object);
        assertTrue(validationResponse.isValid());
    }


    @Configuration
    public static class Config {

        @Bean
        public ValidationService validationService() {
            return new ValidationService(SimpleObject.class);
        }

        @Bean
        public Validator notNullValidator(final ValidationService validationService) {
            Validator notNullValidator = new Validator() {

                @Override
                public String getName() {
                    return "notNull";
                }

                @Override
                public ValidationError validate(Object object) {
                    if (object == null) {
                        return new ValidationError(new NullPointerException("the field is null"));
                    }
                    return null;
                }
            };
            validationService.addValidator(notNullValidator);
            return notNullValidator;
        }

    }
}
