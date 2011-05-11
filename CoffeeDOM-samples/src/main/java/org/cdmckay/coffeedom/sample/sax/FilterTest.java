/*-- 

 Copyright (C) 2000 Brett McLaughlin & Jason Hunter.
 All rights reserved.
 
 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:
 
 1. Redistributions of source code must retain the above copyright
    notice, this list of conditions, and the following disclaimer.
 
 2. Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions, and the disclaimer that follows 
    these conditions in the documentation and/or other materials 
    provided with the distribution.

 3. The name "JDOM" must not be used to endorse or promote products
    derived from this software without prior written permission.  For
    written permission, please contact license@jdom.org.
 
 4. Products derived from this software may not be called "JDOM", nor
    may "JDOM" appear in their name, without prior written permission
    from the JDOM Project Management (pm@jdom.org).
 
 In addition, we request (but do not require) that you include in the 
 end-user documentation provided with the redistribution and/or in the 
 software itself an acknowledgement equivalent to the following:
     "This product includes software developed by the
      JDOM Project (http://www.jdom.org/)."
 Alternatively, the acknowledgment may be graphical using the logos 
 available at http://www.jdom.org/images/logos.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED.  IN NO EVENT SHALL THE JDOM AUTHORS OR THE PROJECT
 CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 SUCH DAMAGE.

 This software consists of voluntary contributions made by many 
 individuals on behalf of the JDOM Project and was originally 
 created by Brett McLaughlin <brett@jdom.org> and 
 Jason Hunter <jhunter@jdom.org>.  For more information on the 
 JDOM Project, please see <http://www.jdom.org/>.
 
 */

package org.cdmckay.coffeedom.sample.sax;

import org.cdmckay.coffeedom.Document;
import org.cdmckay.coffeedom.input.SAXBuilder;
import org.cdmckay.coffeedom.output.XMLOutputter;

import java.io.InputStream;

/**
 * Tests SAXBuilder's XMLFilter feature.
 *
 * @author Joe Bowbeer
 * @author Cameron Mckay
 */
public class FilterTest {

    /**
     * Creates new FilterTest
     */
    public FilterTest() {
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) throws Exception {

        /* XMLWriter for viewing unfiltered input. */
        XMLWriter echoWriter = new XMLWriter();

        /* Add pretty formatting to unformatted XML file. */
        SAXBuilder test1Builder = new SAXBuilder();
        DataFormatFilter format = new DataFormatFilter(echoWriter);
        format.setIndentStep(4);
        test1Builder.setXMLFilter(format);
        InputStream test1Stream = FilterTest.class.getResourceAsStream("test1.xml");

        System.out.println(" -- test1.xml unfiltered -- \n");
        Document doc = test1Builder.build(test1Stream);

        System.out.println(" -- test1.xml filtered by DataFormatFilter --\n");
        XMLOutputter test1Outputter = new XMLOutputter();
        test1Outputter.output(doc, System.out);

        System.out.println("\n");

        /* Remove pretty formatting from formatted XML file. */

        SAXBuilder test2Builder = new SAXBuilder();
        test2Builder.setXMLFilter(new DataUnformatFilter(echoWriter));
        InputStream test2Stream = FilterTest.class.getResourceAsStream("test2.xml");

        System.out.println(" -- test2.xml unfiltered --\n");
        doc = test2Builder.build(test2Stream);

        System.out.println(" -- test2.xml filtered by DataUnformatFilter --\n");
        XMLOutputter test2Outputter = new XMLOutputter();
        test2Outputter.output(doc, System.out);

        System.out.println("\n");
    }

}