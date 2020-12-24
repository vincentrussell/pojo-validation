package com.github.vincentrussell.validation.annotation;

import com.github.vincentrussell.validation.SeparateClassloaderRule;
import com.github.vincentrussell.validation.Validator;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.BooleanMemberValue;
import javassist.bytecode.annotation.ByteMemberValue;
import javassist.bytecode.annotation.CharMemberValue;
import javassist.bytecode.annotation.ClassMemberValue;
import javassist.bytecode.annotation.DoubleMemberValue;
import javassist.bytecode.annotation.FloatMemberValue;
import javassist.bytecode.annotation.IntegerMemberValue;
import javassist.bytecode.annotation.LongMemberValue;
import javassist.bytecode.annotation.MemberValue;
import javassist.bytecode.annotation.ShortMemberValue;
import javassist.bytecode.annotation.StringMemberValue;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import javassist.bytecode.annotation.Annotation;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


public class ValidatorInterpreterTest {

    @Rule
    public SeparateClassloaderRule separateClassloaderRule = new SeparateClassloaderRule();

    public static final String CLASS_NAME =
            "com.github.vincentrussell.validation.annotation.ValidatorInterpreterTest$TestClass";

    private ClassPool cp;

    @Before
    public void before() {
        cp = ClassPool.getDefault();
    }


    @Test
    public void validatorWithOneDefaultConstructor() throws Exception {
        Class<?> clazz = addAnnotationToField(
                CLASS_NAME, "String", "field1", NotEmpty.class.getName(),
                Collections.emptyMap());
        Field field = ReflectionUtils.findField(clazz, "field1");
        ValidatorInterpreter validatorInterpreter = new ValidatorInterpreter(NotEmpty.class, field.getAnnotation(NotEmpty.class), field);
        assertEquals("com.github.vincentrussell.validation.annotation.NotEmpty", validatorInterpreter.getValidatorName());
        Validator validator = validatorInterpreter.instantiate();
        assertNotNull(validator);
    }

    @Test
    public void validatorWithConstructorWithMultipleParams() throws Exception {
        Class<?> clazz = addAnnotationToField(CLASS_NAME,
                "String", "field1", Size.class.getName(), new HashMap(){{
                    put("min", 10);
                    put("max", 20);
                }});
        Field field = ReflectionUtils.findField(clazz, "field1");
        ValidatorInterpreter validatorInterpreter = new ValidatorInterpreter(Size.class, field.getAnnotation(Size.class), field);
        assertEquals("com.github.vincentrussell.validation.annotation.Size_min_10max_20", validatorInterpreter.getValidatorName());
        Validator validator = validatorInterpreter.instantiate();
        assertNotNull(validator);
    }

    private Class<?> addAnnotationToField(
            String className,
            String fieldType,
            String fieldName,
            String annotationName,
            Map<String, ? > annotationValues) throws Exception {

        CtClass ctClass;

        try {
            ctClass = cp.makeClass(className);
        } catch (RuntimeException e) {
            ctClass = cp.get(className);
            ctClass.detach();
            ctClass = cp.makeClass(className);
        }
        ClassFile cfile = ctClass.getClassFile();
        ConstPool cpool = cfile.getConstPool();
        ctClass.addField(CtField.make(String.format("private %s %s;", fieldType, fieldName), ctClass));
        CtField cfield = ctClass.getField(fieldName);

        AnnotationsAttribute attr =
                new AnnotationsAttribute(cpool, AnnotationsAttribute.visibleTag);
        Annotation annot = new Annotation(annotationName, cpool);
        annotationValues.forEach(
                (BiConsumer<String, Object>) (s, o) -> annot.addMemberValue(s, createMemberValue(cpool, o)));
        attr.addAnnotation(annot);
        cfield.getFieldInfo().addAttribute(attr);
        return ctClass.toClass();
    }

    public static MemberValue createMemberValue(ConstPool cp, Object obj) {
        if (Boolean.class.isInstance(obj) || boolean.class.isInstance(obj)) {
            BooleanMemberValue booleanMemberValue = new BooleanMemberValue(cp);
            booleanMemberValue.setValue((Boolean) obj);
            return booleanMemberValue;
        } else if (Byte.class.isInstance(obj) || byte.class.isInstance(obj)) {
            ByteMemberValue byteMemberValue = new ByteMemberValue(cp);
            byteMemberValue.setValue((Byte) obj);
            return byteMemberValue;
        } else if (Character.class.isInstance(obj) || char.class.isInstance(obj)) {
            CharMemberValue charMemberValue = new CharMemberValue(cp);
            charMemberValue.setValue((Character) obj);
            return charMemberValue;
        } else if (Short.class.isInstance(obj) || short.class.isInstance(obj)) {
            ShortMemberValue shortMemberValue = new ShortMemberValue(cp);
            shortMemberValue.setValue((Short) obj);
            return shortMemberValue;
        } else if (Integer.class.isInstance(obj) || int.class.isInstance(obj)) {
            IntegerMemberValue integerMemberValue = new IntegerMemberValue(cp);
            integerMemberValue.setValue((Integer) obj);
            return integerMemberValue;
        } else if (Long.class.isInstance(obj) || long.class.isInstance(obj)) {
            LongMemberValue longMemberValue = new LongMemberValue(cp);
            longMemberValue.setValue((Long) obj);
            return longMemberValue;
        } else if (Float.class.isInstance(obj) || float.class.isInstance(obj)) {
            FloatMemberValue floatMemberValue = new FloatMemberValue(cp);
            floatMemberValue.setValue((Float) obj);
            return floatMemberValue;
        } else if (Double.class.isInstance(obj) || double.class.isInstance(obj)) {
            DoubleMemberValue doubleMemberValue = new DoubleMemberValue(cp);
            doubleMemberValue.setValue((Double) obj);
            return doubleMemberValue;
        } else if (obj.getClass().getName().equals("java.lang.Class")) {
            ClassMemberValue classMemberValue = new ClassMemberValue(cp);
            classMemberValue.setValue(((Class) obj).getName());
            return classMemberValue;
        } else if (obj.getClass().getName().equals("java.lang.String")) {
            StringMemberValue stringMemberValue = new StringMemberValue(cp);
            stringMemberValue.setValue((String) obj);
            return stringMemberValue;
        } else {
            throw new IllegalArgumentException("not supported " + obj );
        }
    }

//    private static class TestClass {
//
//        private String field1;
//
//    }

}