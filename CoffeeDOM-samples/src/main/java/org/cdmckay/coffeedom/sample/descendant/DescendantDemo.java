/*--

 Copyright (C) 2000-2007 Jason Hunter & Brett McLaughlin.
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
    written permission, please contact <request_AT_jdom_DOT_org>.

 4. Products derived from this software may not be called "JDOM", nor
    may "JDOM" appear in their name, without prior written permission
    from the JDOM Project Management <request_AT_jdom_DOT_org>.

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
 created by Jason Hunter <jhunter_AT_jdom_DOT_org> and
 Brett McLaughlin <brett_AT_jdom_DOT_org>.  For more information
 on the JDOM Project, please see <http://www.jdom.org/>.

 */

package org.cdmckay.coffeedom.sample.descendant;

import org.cdmckay.coffeedom.Content;
import org.cdmckay.coffeedom.Document;
import org.cdmckay.coffeedom.filter.ElementFilter;
import org.cdmckay.coffeedom.filter.Filter;
import org.cdmckay.coffeedom.input.SAXBuilder;
import org.cdmckay.coffeedom.output.XMLOutputter;

import java.util.Iterator;

/**
 * Demonstrates the use of {@link org.cdmckay.coffeedom.Parent#getDescendants}.
 *
 * @author Jason Hunter
 * @author Brett McLaughlin
 * @author Cameron McKay
 * @version 2.0
 */
public class DescendantDemo {

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.err.println("Usage: java DescendantDemo [web.xml]");
            return;
        }

        SAXBuilder builder = new SAXBuilder();
        Document doc = builder.build(args[0]);

        System.out.println("All content:");
        for (Content descendant : doc.getDescendants()) {
            System.out.println(descendant);
        }

        System.out.println();
        System.out.println("All elements:");
        for (Content elementDescendants : doc.getDescendants(new ElementFilter())) {
            System.out.println(elementDescendants);
        }

        System.out.println();
        System.out.println("All non-elements:");
        for (Content nonElementDescendants : doc.getDescendants(new ElementFilter().negate())) {
            System.out.println(nonElementDescendants);
        }

        System.out.println();
        System.out.println("Elements with localname of servlet:");
        for (Content servletElements : doc.getDescendants(new ElementFilter("servlet"))) {
            System.out.println(servletElements);
        }


        System.out.println();
        System.out.println("Elements with localname of servlet-name or servlet-class:");
        Filter servletNameOrServletClass = new ElementFilter("servlet-name").or(new ElementFilter("servlet-class"));
        for (Content content : doc.getDescendants(servletNameOrServletClass)) {
            System.out.println(content);
        }

        System.out.println();
        System.out.println("Remove elements with localname of servlet:");
        Iterator<Content> it = doc.getDescendants(new ElementFilter("servlet")).iterator();
        while (it.hasNext()) {
            it.next();
            it.remove();
        }

        XMLOutputter outputter = new XMLOutputter();
        outputter.output(doc, System.out);
    }
}
