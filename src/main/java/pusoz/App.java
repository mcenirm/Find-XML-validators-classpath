package pusoz;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Hello world!
 *
 */
public class App {

    public static void main(String[] args) throws IOException, NoSuchMethodException {
        System.out.println("Hello World!");
        ClassLoader classLoader = App.class.getClassLoader();
        Enumeration<URL> resources = classLoader.getResources("");
        while (resources.hasMoreElements()) {
            URL url = resources.nextElement();
            System.out.println("url: " + url);
        }
        Properties properties = System.getProperties();
        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            String key = (String) entry.getKey();
            Object value = entry.getValue();
            if (key.toLowerCase().matches(".*(class|path).*")) {
                System.out.println("key: " + key);
            }
        }
        String javaClassPath = System.getProperty("java.class.path");
        System.out.println("java class path: " + javaClassPath);
        String bootClassPath = System.getProperty("sun.boot.class.path");
        System.out.println("boot class path: " + bootClassPath);
        String pathSeparator = System.getProperty("path.separator");
        System.out.println("path separator: " + pathSeparator);
        String classPath = bootClassPath + pathSeparator + javaClassPath;
        String[] classPathItems = classPath.split(":");
        for (int i = 0; i < classPathItems.length; i++) {
            String classPathItemLocation = classPathItems[i];
            System.out.println(i + ": " + classPathItemLocation);
            File file = new File(classPathItemLocation);
            if (!file.exists()) {
                System.out.println("does not exist");
            } else if (file.isDirectory()) {
                File[] files = file.listFiles();
                System.out.println("list" + (null == files ? " is null" : 0 == files.length ? " is empty" : (" (" + files.length + "): " + files[0])));
            } else if (classPathItemLocation.toLowerCase().endsWith(".jar")) {
                JarInputStream is = new JarInputStream(new FileInputStream(file));
                JarEntry jarEntry;
                while (null != (jarEntry = is.getNextJarEntry())) {
                    String name = jarEntry.getName();
//                    System.out.println("entry name: " + name);
                    if (/*name.toLowerCase().contains("xml") &&*/name.endsWith(".class")) {
                        name = name.substring(0, name.length() - ".class".length());
                        name = name.replace('/', '.');
                        try {
                            Class<?> aClass = Class.forName(name, false, classLoader);
                            //                            System.out.println("class: " + aClass);
                            Method method = aClass.getMethod("main", args.getClass());
                            System.out.println(name + ": " + method);
                        } catch (ClassNotFoundException ex) {
                        } catch (NoSuchMethodException ex) {
                        } catch (SecurityException ex) {
                        } catch (Exception ex) {
                            Logger.getLogger(App.class.getName()).log(Level.SEVERE, name, ex);
                        }
                    }
                }
            }
        }
    }
}
