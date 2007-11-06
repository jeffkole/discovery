/* Copyright 2007 Tacit Knowledge LLC
 * 
 * Licensed under the Tacit Knowledge Open License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License. You may
 * obtain a copy of the License at http://www.tacitknowledge.com/licenses-1.0.
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tacitknowledge.util.discovery;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.logging.LogFactory;

/**
 * Utility class for dealing with the classpath. 
 * 
 * @author  Scott Askew (scott@tacitknowledge.com)
 */
public final class ClasspathUtils
{
    /**
     * Hidden constructor for utility class.
     */
    private ClasspathUtils()
    {
        // Hidden constructor
    }
    
    /**
     * Returns the classpath as a list of directories.  Any classpath component
     * that is not a directory will be ignored. 
     * 
     * @return the classpath as a list of directories; if no directories can
     *         be found then an empty list will be returned
     */
    public static List getClasspathDirectories()
    {
        List directories = new ArrayList();
        List components = getClasspathComponents();
        for (Iterator i = components.iterator(); i.hasNext();)
        {
            String possibleDir = (String) i.next();
            File file = new File(possibleDir);
            if (file.isDirectory())
            {
                directories.add(possibleDir);
            }
        }
        List tomcatPaths = getTomcatPaths();
        if (tomcatPaths != null)
        {
            directories.addAll(tomcatPaths);
        }
        return directories;
    }
    
    /**
     * If the system is running on Tomcat, this method will parse the 
     * <code>common.loader</code> property to reach deeper into the 
     * classpath to get Tomcat common paths
     * 
     * @return a list of paths or null if tomcat paths not found
     */
    private static List getTomcatPaths()
    {
        String tomcatPath = System.getProperty("catalina.home");
        if (tomcatPath == null)
        {
            //not running Tomcat
            return null;
        }
        String commonClasspath = System.getProperty("common.loader");
        if (commonClasspath == null)
        {
            //didn't find the common classpath
            return null;
        }
        StringBuffer buffer = new StringBuffer(commonClasspath);
        String pathDeclaration = "${catalina.home}";
        int length = pathDeclaration.length();
        boolean doneReplace = false;
        do
        {
            int start = commonClasspath.indexOf(pathDeclaration);
            if (start >= 0)
            {
                buffer.replace(start, (start + length), tomcatPath);
                commonClasspath = buffer.toString();
            }
            else
            {
                doneReplace = true;
            }
        }
        while (!doneReplace);
        String[] paths = commonClasspath.split(",");
        List pathList = Arrays.asList(paths);
        return pathList;
    }
    
    /**
     * Returns the classpath as a list of the names of archive files.  Any
     * classpath component that is not an archive will be ignored. 
     * 
     * @return the classpath as a list of archive file names; if no archives can
     *         be found then an empty list will be returned
     */
    public static List getClasspathArchives()
    {
        List archives = new ArrayList();
        List components = getClasspathComponents();
        for (Iterator i = components.iterator(); i.hasNext();)
        {
            String possibleDir = (String) i.next();
            File file = new File(possibleDir);
            if (file.isFile()
                && (file.getName().endsWith(".jar") || file.getName().endsWith(".zip")))
            {
                archives.add(possibleDir);
            }
        }
        return archives;
    }
    
    /**
     * Returns the classpath as a list directory and archive names.
     * 
     * @return the classpath as a list of directory and archive file names; if
     *         no components can be found then an empty list will be returned
     */
    public static List getClasspathComponents()
    {
        List components = new LinkedList();

        // walk the classloader hierarchy, trying to get all the components we can
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        
        while ((null != cl) && (cl instanceof URLClassLoader)) 
        {
            URLClassLoader ucl = (URLClassLoader) cl;
            components.addAll(getUrlClassLoaderClasspathComponents(ucl));
            
            try
            {
                cl = ucl.getParent();
            }
            catch (SecurityException se)
            {
                cl = null;
            }
        }

        // walking the hierarchy doesn't guarantee we get everything, so 
        // lets grab the system classpath for good measure.
        String classpath = System.getProperty("java.class.path");
        String separator = System.getProperty("path.separator");
        StringTokenizer st = new StringTokenizer(classpath, separator);
        while (st.hasMoreTokens())
        {
            String component = st.nextToken();
            // Calling File.getPath() cleans up the path so that it's using
            // the proper path separators for the host OS
            component = new File(component).getPath();
            components.add(component);
        }

        // Set removes any duplicates, return a list for the api.
        return new LinkedList(new HashSet(components));
    }

    /**
     * Get the list of classpath components
     * 
     * @param ucl url classloader
     * @return List of classpath components
     */
    protected static List getUrlClassLoaderClasspathComponents(URLClassLoader ucl)
    {
        List components = new ArrayList();

        URL[] urls = new URL[0];

        // Workaround for running on JBoss with UnifiedClassLoader3 usage
        // We need to invoke getClasspath() method instead of getURLs()
        if (ucl.getClass().getName().equals("org.jboss.mx.loading.UnifiedClassLoader3"))
        {
            try
            {
                Method classPathMethod = ucl.getClass().getMethod("getClasspath", new Class[] {});
                urls = (URL[]) classPathMethod.invoke(ucl, new Object[0]);
            }
            catch(Exception e)
            {
                LogFactory.getLog(ClasspathUtils.class).debug("Error invoking getClasspath on UnifiedClassLoader3: ", e);
            }
        }
        else
        {
        	// Use regular ClassLoader method to get classpath
            urls = ucl.getURLs();
        }

        for (int i = 0; i < urls.length; i++)
        {
            URL url = urls[i];
            components.add(new File(url.getPath()).getPath());
        }
        
        return components;
    }
}