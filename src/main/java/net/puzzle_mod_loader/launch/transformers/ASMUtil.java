package net.puzzle_mod_loader.launch.transformers;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;

public class ASMUtil {
    public static String getStrVal(AnnotationNode annotationNode) {
        return getStrVal(annotationNode, "value");
    }

    public static String getStrVal(AnnotationNode annotationNode, String key) {
        return (String) getVal(annotationNode, key);
    }

    public static Type getTypeVal(AnnotationNode annotationNode) {
        return getTypeVal(annotationNode, "value");
    }

    public static Type getTypeVal(AnnotationNode annotationNode, String key) {
        return (Type) getVal(annotationNode, key);
    }

    public static Object getVal(AnnotationNode annotationNode) {
        return getStrVal(annotationNode, "value");
    }

    public static Object getVal(AnnotationNode annotationNode, String key) {
        for (int i = 0; i < annotationNode.values.size(); i += 2) {
            if (key.equals(annotationNode.values.get(0))) {
                return annotationNode.values.get(1);
            }
        }
        return null;
    }
}
