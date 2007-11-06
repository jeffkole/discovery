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

import junit.framework.TestCase;

/**
 * Tests the <code>RegexResourceCriteria</code> class. 
 * 
 * @author  Scott Askew (scott@tacitknowledge.com)
 * @version $Id: RegexResourceCriteriaTest.java,v 1.1 2004/03/15 07:39:44 scott Exp $
 */
public class RegexResourceCriteriaTest extends TestCase
{
    /**
     * Constructor for RegexResourceCriteriaTest.
     * 
     * @param name the name of the test to run
     */
    public RegexResourceCriteriaTest(String name)
    {
        super(name);
    }
    
    /**
     * Validates the normal operation of the <code>matches</code> method.
     */
    public void testMatches()
    {
        RegexResourceCriteria criteria = new RegexResourceCriteria("^Test[0-9]+.txt");
        assertTrue(criteria.matches("foo/bar/Test12.txt"));
        assertTrue(criteria.matches("Test12.txt"));
        assertFalse(criteria.matches("foo/bar/Test.txt"));
        assertFalse(criteria.matches(""));
        assertFalse(criteria.matches(null));
    }
}
