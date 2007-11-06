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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Matches a resource based on a JDK 1.4-style regular expression.
 * 
 * @author  Scott Askew (scott@tacitknowledge.com)
 * @version $Id: RegexResourceCriteria.java,v 1.1 2004/03/15 07:39:43 scott Exp $
 * @see     java.util.regex.Pattern
 */
public class RegexResourceCriteria implements ResourceCriteria
{
    /**
     * The pattern to match.  This is threadsafe.
     */
    private Pattern pattern = null;
    
    /**
     * Creates a new <code>RegexResourceCriteria</code> that matches resources
     * based on the given regular expression.
     * 
     * @param regex the regular expression to match resources against
     */
    public RegexResourceCriteria(String regex)
    {
        pattern = Pattern.compile(regex);
    }
    
    /**
     * @see ResourceCriteria#matches(String)
     */
    public boolean matches(String resourceName)
    {
        if (resourceName == null)
        {
            return false;
        }
        File file = new File(resourceName);
        Matcher matcher = pattern.matcher(file.getName());
        return matcher.matches();
    }
}