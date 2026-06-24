package abservices.libmanagement;

import java.io.File;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Driver;
import java.sql.DriverManager;

/**
 * Attempts to ensure a MySQL JDBC driver is available. If not found on the system
 * classpath, looks for a connector jar under `libs/` and loads it dynamically.
 */
public class DriverLoader {
    private static boolean loaded = false;

    public static synchronized void ensureDriver() throws Exception {
        if (loaded) return;

        // Try common MySQL driver class names first
        String[] driverClasses = new String[] {
            "com.mysql.cj.jdbc.Driver",
            "com.mysql.jdbc.Driver"
        };

        for (String cls : driverClasses) {
            try {
                Class.forName(cls);
                loaded = true;
                return;
            } catch (ClassNotFoundException e) {
                // continue
            }
        }

        // Not found on classpath: attempt to find a connector jar in common locations.
        // Expand search to include the code location (where classes are loaded from)
        // because IDEs (VS Code) may set a different working directory.
        java.util.List<String> candidates = new java.util.ArrayList<>();
        // Allow explicit override via environment variable
        String envLibs = System.getenv("LIBS_DIR");
        if (envLibs != null && !envLibs.trim().isEmpty()) {
            candidates.add(envLibs);
        }
        candidates.add(System.getProperty("user.dir"));
        candidates.add(System.getProperty("user.dir") + File.separator + "libs");
        candidates.add("." + File.separator + "libs");
        candidates.add("libs");

        try {
            java.net.URL codeLoc = DriverLoader.class.getProtectionDomain().getCodeSource().getLocation();
            if (codeLoc != null) {
                File codeFile = new File(codeLoc.toURI());
                File baseDir = codeFile.isFile() ? codeFile.getParentFile() : codeFile;
                if (baseDir != null) {
                    candidates.add(baseDir.getAbsolutePath());
                    candidates.add(new File(baseDir, "libs").getAbsolutePath());
                    File parent = baseDir.getParentFile();
                    if (parent != null) {
                        candidates.add(parent.getAbsolutePath());
                        candidates.add(new File(parent, "libs").getAbsolutePath());
                    }
                }
            }
        } catch (Exception e) {
            // ignore and continue with existing candidates
        }

        StringBuilder checked = new StringBuilder();

        for (String dirPath : candidates) {
            if (dirPath == null) continue;
            File dir = new File(dirPath);
            try { checked.append(dir.getAbsolutePath()).append("\n"); } catch (Exception _ex) {}
            if (dir.exists() && dir.isDirectory()) {
                File[] jars = dir.listFiles((d, name) -> name.toLowerCase().startsWith("mysql") && name.toLowerCase().endsWith(".jar"));
                if (jars != null && jars.length > 0) {
                    File jar = jars[0];
                    URL jarUrl = jar.toURI().toURL();
                    URLClassLoader loader = new URLClassLoader(new URL[] { jarUrl }, DriverLoader.class.getClassLoader());

                    for (String cls : driverClasses) {
                        try {
                            Class<?> c = Class.forName(cls, true, loader);
                            Object drv = c.getDeclaredConstructor().newInstance();
                            if (drv instanceof Driver) {
                                DriverManager.registerDriver(new DriverShim((Driver) drv));
                                loaded = true;
                                return;
                            }
                        } catch (ClassNotFoundException ex) {
                            // try next class name
                        }
                    }
                }
            }
        }

        throw new ClassNotFoundException("MySQL JDBC driver not found. Searched locations:\n" + checked.toString());
    }
}
