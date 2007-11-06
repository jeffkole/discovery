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
import java.util.ArrayList;
import java.util.List;

/**
 * Source for resources within libraries within web archive (WAR) files.
 * 
 * @author  Scott Askew (scott@tacitknowledge.com)
 * @version $Id: WebArchiveResourceListSource.java,v 1.1 2004/03/19 21:38:44 scott Exp $
 */
class WebArchiveResourceListSource extends ArchiveResourceListSource
{
    /**
     * The full path on-disk to /WEB-INF
     */
    private String path = null;
    
    /**
     * Creates a new <code>WebArchiveResourceListSource</code>
     * 
     * @param path the full on-disk path to WEB-INF
     */
    public WebArchiveResourceListSource(String path)
    {
        this.path = path;
    }
    
    /**
     * {@inheritDoc}
     */
    protected List getJars()
    {
        String libPath = path + File.separator + "lib";
        List jars = new ArrayList();
        String[] entries = getLibEntries(libPath);
        for (int i = 0; i < entries.length; i++)
        {
            String jar = entries[i];
            if (jar.endsWith(".jar") || jar.endsWith(".zip"))
            {
                String jarName = libPath + File.separator + jar;
                jars.add(jarName);
            }
        }
        return jars;
    }

    /**
     * Returns a list of entries in the given directory.  This method is
     * separated so that unit tests can override it to return test-friendly
     * file names. 
     * 
     * @param  dir the dir containing the files to return
     * @return the files and directories in the given directory, or
     *         <code>null</code> if the given directory does not exist or is
     *         a file.
     */
    protected String[] getLibEntries(String dir)
    {
        File libdir = new File(dir);
        return libdir.list();
    }
}