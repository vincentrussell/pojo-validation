package com.github.vincentrussell.validation;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.net.URLClassLoader;

public class SeparateClassloaderRule implements TestRule {

    @Override
    public Statement apply(final Statement base, final Description description) {
        try {
            final Statement statement = createStatement(base);
            return statement;
        } finally {

        }
    }

    private Statement createStatement(final Statement base) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
                try {
                    Thread.currentThread().setContextClassLoader(new TestClassLoader());
                    base.evaluate();
                }  finally {
                    Thread.currentThread().setContextClassLoader(classLoader);
                }
            }
        };
    }

    public static class TestClassLoader extends URLClassLoader {
        public TestClassLoader() {
            super(((URLClassLoader)getSystemClassLoader()).getURLs());
        }

        @Override
        public Class<?> loadClass(final String name) throws ClassNotFoundException {
            return super.loadClass(name);
        }
    }


}
