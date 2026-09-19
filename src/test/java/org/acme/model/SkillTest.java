package org.acme.model;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class SkillTest {

    @Test
    void shouldAcceptValidSkillAndRejectInvalidOne() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Skill validSkill = new Skill();
        validSkill.name = "Java";
        validSkill.category = "Backend";

        assertTrue(validator.validate(validSkill).isEmpty());

        Skill invalidSkill = new Skill();
        invalidSkill.name = "J";
        invalidSkill.category = "Backend";

        assertFalse(validator.validate(invalidSkill).isEmpty());

        Skill invalidNameSkill = new Skill();
        invalidNameSkill.name = "Java 2025";
        invalidNameSkill.category = "Backend";

        assertFalse(validator.validate(invalidNameSkill).isEmpty());
    }
}
