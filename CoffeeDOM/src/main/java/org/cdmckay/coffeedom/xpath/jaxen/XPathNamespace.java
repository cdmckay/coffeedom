/* 
 * ====================================================================
 *
 * Copyright 2000-2002 Bob McWhirter & James Strachan.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 * 
 *   * Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 * 
 *   * Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 * 
 *   * Neither the name of the Jaxen Project nor the names of its
 *     contributors may be used to endorse or promote products derived 
 *     from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * ====================================================================
 * This software consists of voluntary contributions made by many 
 * individuals on behalf of the Jaxen Project and was originally 
 * created by Bob McWhirter <bob@werken.com> and 
 * James Strachan <jstrachan@apache.org>.  For more information on the 
 * Jaxen Project, please see <http://www.jaxen.org/>.
 * 
 */

package org.cdmckay.coffeedom.xpath.jaxen;

import org.cdmckay.coffeedom.Element;
import org.cdmckay.coffeedom.Namespace;

/**
 * Wrapper for CoffeeDOM namespace nodes to give them a parent, as required by the XPath data model.
 *
 * @author Erwin Bolwidt
 * @author Cameron McKay
 */
public class XPathNamespace {

    private Element coffeedomElement;

    private Namespace coffeedomNamespace;

    /**
     * Creates a namespace-node wrapper for a namespace node that hasn't been assigned to an element yet.
     */
    public XPathNamespace(Namespace coffeedomNamespace) {
        this.coffeedomNamespace = coffeedomNamespace;
    }

    /**
     * Creates a namespace-node wrapper for a namespace node that is assigned to the given CoffeeDOM element.
     */
    public XPathNamespace(Element coffeedomElement, Namespace coffeedomNamespace) {
        this.coffeedomElement = coffeedomElement;
        this.coffeedomNamespace = coffeedomNamespace;
    }

    /**
     * Returns the CoffeeDOM element from which this namespace node has been retrieved. The result may be null when the
     * namespace node has not yet been assigned to an element.
     */
    public Element getCoffeeDOMElement() {
        return coffeedomElement;
    }

    /**
     * Sets or changes the element to which this namespace node is assigned.
     */
    public void setCoffeeDOMElement(Element coffeedomElement) {
        this.coffeedomElement = coffeedomElement;
    }

    /**
     * Returns the CoffeeDOM namespace object of this namespace node; the CoffeeDOM namespace object contains the prefix and URI
     * of the namespace.
     */
    public Namespace getJDOMNamespace() {
        return coffeedomNamespace;
    }

    public String toString() {
        return ("[xmlns:" + coffeedomNamespace.getPrefix() + "=\"" +
                coffeedomNamespace.getURI() + "\", element=" +
                coffeedomElement.getName() + "]");
    }
} 
