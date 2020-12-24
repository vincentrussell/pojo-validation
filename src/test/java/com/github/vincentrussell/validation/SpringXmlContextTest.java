package com.github.vincentrussell.validation;

import com.github.vincentrussell.validation.testClasses.simple.SimpleObject;
import com.github.vincentrussell.validation.util.ValidationUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:validation-service-example.xml")
public class SpringXmlContextTest {

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


    public static class FactoryService {

        public ValidationService buildValidationService() {
            ValidationService validationService = new ValidationService(SimpleObject.class);
            validationService.addValidator(new NotnullValidator());
            return validationService;
        }
    }

    public static class NotnullValidator implements Validator {

        @Override
        public String getName() {
            return "notNull";
        }

        @Override
        public ValidationError validate(Object object) {
            return ValidationUtils.isTrue(object != null, "field is null");
        }
    }
}
