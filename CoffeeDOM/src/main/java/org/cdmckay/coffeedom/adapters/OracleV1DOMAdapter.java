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

package org.cdmckay.coffeedom.adapters;

import org.cdmckay.coffeedom.CoffeeDOMException;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * An adapter for the Oracle Version 1 DOM parser.
 *
 * @author Brett McLaughlin
 * @author Jason Hunter
 */
public class OracleV1DOMAdapter
        extends AbstractDOMAdapter {

    /**
     * This creates a new <code>{@link Document}</code> from an existing <code>InputStream</code> by letting a DOM
     * parser handle parsing using the supplied stream.
     *
     * @param in       <code>InputStream</code> to parse.
     * @param validate <code>boolean</code> to indicate if validation should occur.
     * @return <code>Document</code> - instance ready for use.
     * @throws IOException                    when I/O error occurs.
     * @throws org.cdmckay.coffeedom.CoffeeDOMException when errors occur in parsing.
     */
    public Document getDocument(InputStream in, boolean validate) throws IOException, CoffeeDOMException {

        try {
            // Load the parser class
            Class<?> parserClass = Class.forName("oracle.xml.parser.XMLParser");
            Object parser = parserClass.newInstance();

            // Parse the document
            Method parse = parserClass.getMethod("parse", org.xml.sax.InputSource.class);
            parse.invoke(parser, new InputSource(in));

            // Get the Document object
            Method getDocument = parserClass.getMethod("getDocument");

            return (Document) getDocument.invoke(parser);
        } catch (InvocationTargetException e) {
            Throwable targetException = e.getTargetException();
            if (targetException instanceof org.xml.sax.SAXParseException) {
                SAXParseException parseException = (SAXParseException) targetException;
                throw new CoffeeDOMException("Error on line " + parseException.getLineNumber() + " of XML document: " +
                        parseException.getMessage(), parseException);
            } else if (targetException instanceof IOException) {
                throw (IOException) targetException;
            } else {
                throw new CoffeeDOMException(targetException.getMessage(), targetException);
            }
        } catch (Exception e) {
            throw new CoffeeDOMException(e.getClass().getName() + ": " + e.getMessage(), e);
        }
    }

    /**
     * This creates an empty <code>Document</code> object based on a specific parser implementation.
     *
     * @return <code>Document</code> - created DOM Document.
     * @throws org.cdmckay.coffeedom.CoffeeDOMException when errors occur.
     */
    public Document createDocument() throws CoffeeDOMException {
        try {
            return (Document) Class.forName("oracle.xml.parser.XMLDocument").newInstance();
        } catch (Exception e) {
            throw new CoffeeDOMException(e.getClass().getName() + ": " + e.getMessage() + " when creating document", e);
        }
    }
}
