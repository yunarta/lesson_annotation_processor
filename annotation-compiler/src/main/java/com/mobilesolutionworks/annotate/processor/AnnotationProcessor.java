package com.mobilesolutionworks.annotate.processor;

import com.mobilesolutionworks.annotation.Managed;

import java.io.Writer;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

/**
 * Created by yunarta on 20/7/15.
 */
public class AnnotationProcessor extends AbstractProcessor
{
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment env)
    {
        for (TypeElement annotation : annotations)
        {
            System.out.println("annotation = " + annotation);
        }

        Set<? extends Element> elements = env.getElementsAnnotatedWith(Managed.class);
        for (Element element : elements)
        {
            System.out.println("element = " + element);
            System.out.println("element.asType() = " + element.asType());
            System.out.println("element.getClass() = " + element.getClass());
            System.out.println("element is TypeElement = " + (element instanceof TypeElement));

            Element enclosing = element.getEnclosingElement();
            System.out.println("element.getEnclosingElement() = " + enclosing);
            System.out.println("element.getEnclosingElement().getKind() = " + enclosing.getKind());


            if (element instanceof VariableElement)
            {
                TypeElement typeElement = (TypeElement) element.getEnclosingElement();

                String packageName = processingEnv.getElementUtils().getPackageOf(element).getQualifiedName().toString();
                String className = getClassName(typeElement, packageName);

                System.out.println("className = " + className);

                try
                {
                    String generatedClassName = className + "Binding";
                    String output = packageName + "." + generatedClassName;
                    System.out.println("output = " + output);

                    JavaFileObject jfo = processingEnv.getFiler().createSourceFile(output, typeElement);

                    Writer writer = jfo.openWriter();
                    writer.write("//Generated class\n");
                    writer.write("package " + packageName + ";\n\n");
                    writer.write("public class " + generatedClassName + " {}");
                    writer.flush();
                    writer.close();
                }
                catch (Exception exception)
                {
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, exception.getMessage());
                    // e.printStackTrace();
                }
            }

            return true;
        }

        return true;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes()
    {
        Set<String> supportedTypes = new HashSet<String>();
        supportedTypes.add(Managed.class.getCanonicalName());
        return supportedTypes;
    }

    @Override
    public SourceVersion getSupportedSourceVersion()
    {
        return SourceVersion.latestSupported();
    }

    private static String getClassName(TypeElement type, String packageName)
    {
        int packageLen = packageName.length() + 1;
        return type.getQualifiedName().toString().substring(packageLen).replace('.', '$');
    }
}
