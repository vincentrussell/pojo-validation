package com.github.vincentrussell.validation.scanner;

import com.github.vincentrussell.validation.tree.TreeNode;
import org.reflections.Store;
import org.reflections.scanners.AbstractScanner;
import org.reflections.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import static org.reflections.ReflectionUtils.forName;

/**
 * Create a tree structure making the fields children of the class.
 */
public final class ReverseConnectionScanner extends AbstractScanner {

    private static Logger LOGGER = LoggerFactory.getLogger(ReverseConnectionScanner.class);

    private final Map<Class, TreeNode<Object>> classToTreeNodeMap = new HashMap<>();
    private final Map<Field, TreeNode<Object>> fieldToTreeNodeMap = new HashMap<>();

    private final TreeNode<Object> rootTreeNode = new TreeNode<>(Object.class);

    /**
     * Default constructor.
     */
    public ReverseConnectionScanner() {
        classToTreeNodeMap.put(Object.class, rootTreeNode);
    }


    @Override
    public void scan(final Object cls, final Store store) {
        final String className = getMetadataAdapter().getClassName(cls);
        final Class classObject = forName(className, getConfiguration().getClassLoaders());
        if (!acceptResult(className)) {
            return;
        }

        final TreeNode<Object> thisNode = getClassNode(classObject);


        for (Object field : getMetadataAdapter().getFields(cls)) {
            final String fieldName = getMetadataAdapter().getFieldName(field);
            final String classAndField = String.format("%s.%s", className, fieldName);
            final Field fieldObj = Utils.getFieldFromString(classAndField, getConfiguration().getClassLoaders());
            addFieldToTree(thisNode, fieldObj, field);
            put(store, className, classAndField);
        }

        for (Object annotation : getMetadataAdapter().getClassAnnotationNames(cls)) {
            put(store, className, "@" + annotation);
        }
    }

    private TreeNode<Object> getClassNode(final Class classObject) {
        final TreeNode<Object> thisNode = classToTreeNodeMap.computeIfAbsent(classObject, aClass -> {
            final TreeNode<Object> node = new TreeNode<>(classObject);
            rootTreeNode.addChild(node);
            return node;
        });

        return thisNode;
    }

    private TreeNode<Object> getFieldNode(final Field field) {
        final TreeNode<Object> thisNode = fieldToTreeNodeMap.computeIfAbsent(field, aClass -> {
            final TreeNode<Object> node = new TreeNode<>(field);
            return node;
        });

        return thisNode;
    }

    private void addFieldToTree(final TreeNode<Object> parentNode, final Field fieldObj, final Object field) {
        if (Iterable.class.isAssignableFrom(fieldObj.getType())) {
            addParameterizedTypeToTree(fieldObj, parentNode);
        } else if (Map.class.isAssignableFrom(fieldObj.getType())) {
            addParameterizedTypeToTree(fieldObj, parentNode);
        } else {
            TreeNode fieldNode = getFieldNode(fieldObj);
            parentNode.addChild(fieldNode);
            LOGGER.debug("adding child node parent={}, child={}", parentNode, fieldNode);
            TreeNode actualTypeNode = getClassNode(fieldObj.getType());
            fieldNode.addChild(actualTypeNode);
            LOGGER.debug("adding child node parent={}, child={}", fieldNode, actualTypeNode);
        }
    }

    private void addParameterizedTypeToTree(final Field fieldObj, final TreeNode<Object> parentNode) {
        final Type type = fieldObj.getGenericType();
        if (ParameterizedType.class.isInstance(type)) {
            for (Type actualType : ((ParameterizedType) type).getActualTypeArguments()) {
                if (Class.class.isInstance(actualType)) {
                    final  TreeNode fieldNode = getFieldNode(fieldObj);
                    parentNode.addChild(fieldNode);
                    LOGGER.debug("adding child node parent={}, child={}", parentNode, fieldNode);
                    TreeNode actualTypeNode = getClassNode((Class) actualType);
                    fieldNode.addChild(actualTypeNode);
                    LOGGER.debug("adding child node parent={}, child={}", fieldNode, actualTypeNode);
                }

            }
        } else {
            final TreeNode fieldNode = getFieldNode(fieldObj);
            parentNode.addChild(fieldNode);
            LOGGER.debug("adding child node parent={}, child={}", parentNode, fieldNode);
            TreeNode actualTypeNode = getClassNode(fieldObj.getType());
            fieldNode.addChild(actualTypeNode);
            LOGGER.debug("adding child node parent={}, child={}", fieldNode, actualTypeNode);
        }
    }

    /**
     * get the tree of the classes and fields.
     *
     * @return the tree
     */
    public TreeNode<Object> getRootTreeNode() {
        return rootTreeNode;
    }
}

