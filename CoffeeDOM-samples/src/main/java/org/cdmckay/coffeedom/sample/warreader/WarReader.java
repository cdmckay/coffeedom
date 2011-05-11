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

package org.cdmckay.coffeedom.sample.warreader;

import org.cdmckay.coffeedom.Document;
import org.cdmckay.coffeedom.Element;
import org.cdmckay.coffeedom.CoffeeDOMException;
import org.cdmckay.coffeedom.input.SAXBuilder;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * <p><code>WarReader</code> demonstrates how to read a Servlet 2.2 Web Archive file with CoffeeDOM. </p>
 *
 * @author Brett McLaughlin
 * @author Jason Hunter
 * @author Cameron McKay
 * @version 2.0
 */
public class WarReader {

    public static void main(String[] args) throws IOException, CoffeeDOMException {
        if (args.length != 1) {
            System.err.println("Usage: java WarReader [web.xml]");
            return;
        }

        String filename = args[0];

        SAXBuilder builder = new SAXBuilder();
        Document doc = builder.build(new File(filename));

        // Get the root element.
        Element root = doc.getRootElement();

        // Print servlet information.
        List<Element> servlets = root.getChildren("servlet");
        System.out.println("This WAR has " + servlets.size() + " registered servlets:");
        for (Element servlet : servlets) {

            System.out.format("\t%s for %s",
                    servlet.getChild("servlet-name").getTextTrim(),
                    servlet.getChild("servlet-class").getTextTrim());
            List<Element> initParams = servlet.getChildren("init-param");
            System.out.println(" (it has " + initParams.size() + " init-params)");
        }

        // Print security role information.
        List<Element> securityRoles = root.getChildren("security-role");
        if (securityRoles.isEmpty()) {
             System.out.println("This WAR contains no roles.");
        } else {
            Element securityRole = securityRoles.get(0);
            List<Element> roleNames = securityRole.getChildren("role-name");
            System.out.println("This WAR contains " + roleNames.size() + " roles:");

            for (Element roleName : roleNames) {
                System.out.println("\t" + roleName.getTextTrim());
            }
        }

        // Print distributed information (notice this is out of order)
        List<Element> distributed = root.getChildren("distributed");
        if (distributed.isEmpty()) {
            System.out.println("This WAR is not distributed.");
        } else {
            System.out.println("This WAR is distributed.");
        }
    }
}
