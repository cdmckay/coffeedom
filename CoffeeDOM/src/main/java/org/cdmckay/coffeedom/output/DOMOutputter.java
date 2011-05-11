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


package org.cdmckay.coffeedom.output;

import org.cdmckay.coffeedom.*;
import org.cdmckay.coffeedom.adapters.DOMAdapter;
import org.w3c.dom.CDATASection;
import org.w3c.dom.EntityReference;


/**
 * Outputs a CoffeeDOM {@link org.cdmckay.coffeedom.Document Document} as a DOM {@link org.w3c.dom.Document
 * org.w3c.dom.Document}.
 *
 * @author Brett McLaughlin
 * @author Jason Hunter
 * @author Matthew Merlo
 * @author Dan Schaffer
 * @author Yusuf Goolamabbas
 * @author Bradley S. Huffman
 */
public class DOMOutputter {

    /**
     * Default adapter class
     */
    private static final String DEFAULT_ADAPTER_CLASS_NAME = "org.cdmckay.coffeedom.adapters.XercesDOMAdapter";

    /**
     * Adapter to use for interfacing with the DOM implementation
     */
    private String adapterClassName;

    /**
     * Output a DOM with namespaces but just the empty namespace
     */
    private boolean forceNamespaceAware;

    /**
     * This creates a new DOMOutputter which will attempt to first locate a DOM implementation to use via JAXP, and if
     * JAXP does not exist or there's a problem, will fall back to the default parser.
     */
    public DOMOutputter() {
    }

    /**
     * This creates a new DOMOutputter using the specified DOMAdapter implementation as a way to choose the underlying
     * parser.
     *
     * @param adapterClassName <code>String</code> name of class to use for DOM output
     */
    public DOMOutputter(String adapterClassName) {
        this.adapterClassName = adapterClassName;
    }

    /**
     * Controls how NO_NAMESPACE nodes are handeled. If true the outputter always creates a namespace aware DOM.
     *
     * @param flag
     */
    public void setForceNamespaceAware(boolean flag) {
        this.forceNamespaceAware = flag;
    }

    /**
     * Returns whether DOMs will be constructed with namespaces even when the source document has elements all in the
     * empty namespace.
     *
     * @return the forceNamespaceAware flag value
     */
    public boolean getForceNamespaceAware() {
        return forceNamespaceAware;
    }

    /**
     * This converts the CoffeeDOM <code>Document</code> parameter to a DOM Document, returning the DOM version.  The DOM
     * implementation is the one chosen in the constructor.
     *
     * @param document <code>Document</code> to output.
     * @return an <code>org.w3c.dom.Document</code> version
     */
    public org.w3c.dom.Document output(Document document) throws CoffeeDOMException {
        NamespaceStack namespaces = new NamespaceStack();

        org.w3c.dom.Document domDoc = null;
        try {
            // Assign DOCTYPE during construction
            DocType dt = document.getDocType();
            domDoc = createDOMDocument(dt);

            // Add content
            for (Content content : document.getContent()) {
                if (content instanceof Element) {
                    Element element = (Element) content;
                    org.w3c.dom.Element domElement = output(element, domDoc, namespaces);
                    // Add the root element, first check for existing root
                    org.w3c.dom.Element root = domDoc.getDocumentElement();
                    if (root == null) {
                        // Normal case
                        domDoc.appendChild(domElement); // normal case
                    } else {
                        // Xerces 1.3 creates new docs with a <root />
                        // XXX: Need to address DOCTYPE mismatch still
                        domDoc.replaceChild(domElement, root);
                    }
                } else if (content instanceof Comment) {
                    Comment comment = (Comment) content;
                    org.w3c.dom.Comment domComment = domDoc.createComment(comment.getText());
                    domDoc.appendChild(domComment);
                } else if (content instanceof ProcessingInstruction) {
                    ProcessingInstruction pi = (ProcessingInstruction) content;
                    org.w3c.dom.ProcessingInstruction domPI =
                            domDoc.createProcessingInstruction(pi.getTarget(), pi.getData());
                    domDoc.appendChild(domPI);
                } else if (content instanceof DocType) {
                    // We already dealt with the DocType above
                } else {
                    throw new CoffeeDOMException(
                            "Document contained top-level content with type:" + content.getClass().getName());
                }
            }
        } catch (Throwable e) {
            throw new CoffeeDOMException("Exception outputting Document", e);
        }

        return domDoc;
    }

    private org.w3c.dom.Document createDOMDocument(DocType dt) throws CoffeeDOMException {
        if (adapterClassName != null) {
            // The user knows that they want to use a particular impl
            try {
                DOMAdapter adapter = (DOMAdapter) Class.forName(adapterClassName).newInstance();
                // System.out.println("using specific " + adapterClassName);
                return adapter.createDocument(dt);
            } catch (ClassNotFoundException e) {
                // e.printStackTrace();
            } catch (IllegalAccessException e) {
                // e.printStackTrace();
            } catch (InstantiationException e) {
                // e.printStackTrace();
            }
        } else {
            // Try using JAXP...
            try {
                DOMAdapter adapter =
                        (DOMAdapter) Class.forName("JAXPDOMAdapter").newInstance();
                // System.out.println("using JAXP");
                return adapter.createDocument(dt);
            } catch (ClassNotFoundException e) {
                // e.printStackTrace();
            } catch (IllegalAccessException e) {
                // e.printStackTrace();
            } catch (InstantiationException e) {
                // e.printStackTrace();
            }
        }

        // If no DOM doc yet, try to use a hard coded default
        try {
            DOMAdapter adapter = (DOMAdapter) Class.forName(DEFAULT_ADAPTER_CLASS_NAME).newInstance();
            return adapter.createDocument(dt);
            // System.out.println("Using default " +
            //   DEFAULT_ADAPTER_CLASS_NAME);
        } catch (ClassNotFoundException e) {
            // e.printStackTrace();
        } catch (IllegalAccessException e) {
            // e.printStackTrace();
        } catch (InstantiationException e) {
            // e.printStackTrace();
        }

        throw new CoffeeDOMException("No JAXP or default parser available");
    }

    private org.w3c.dom.Element output(Element element, org.w3c.dom.Document domDoc, NamespaceStack namespaces)
            throws CoffeeDOMException {
        try {
            int previouslyDeclaredNamespaces = namespaces.size();

            org.w3c.dom.Element domElement = null;
            if (element.getNamespace() == Namespace.NO_NAMESPACE) {
                // No namespace, use createElement
                domElement = forceNamespaceAware ? domDoc.createElementNS(null, element.getQualifiedName()) :
                        domDoc.createElement(element.getQualifiedName());
            } else {
                domElement = domDoc.createElementNS(element.getNamespaceURI(), element.getQualifiedName());
            }

            // Add namespace attributes, beginning with the element's own
            // Do this only if it's not the XML namespace and it's
            // not the NO_NAMESPACE with the prefix "" not yet mapped
            // (we do output xmlns="" if the "" prefix was already used 
            // and we need to reclaim it for the NO_NAMESPACE)
            Namespace ns = element.getNamespace();
            if (ns != Namespace.XML_NAMESPACE && !(ns == Namespace.NO_NAMESPACE && namespaces.getURI("") == null)) {
                String prefix = ns.getPrefix();
                String uri = namespaces.getURI(prefix);
                if (!ns.getURI().equals(uri)) { // output a new namespace decl
                    namespaces.push(ns);
                    String attrName = getXMLNSTagFor(ns);
                    domElement.setAttribute(attrName, ns.getURI());
                }
            }

            // Add additional namespaces also
            for (Namespace namespace : element.getAdditionalNamespaces()) {
                String prefix = namespace.getPrefix();
                String uri = namespaces.getURI(prefix);
                if (!namespace.getURI().equals(uri)) {
                    String attrName = getXMLNSTagFor(namespace);
                    domElement.setAttribute(attrName, namespace.getURI());
                    namespaces.push(namespace);
                }
            }

            // Add attributes to the DOM element
            for (Attribute attribute : element.getAttributes()) {
                domElement.setAttributeNode(output(attribute, domDoc));
                Namespace ns1 = attribute.getNamespace();
                if ((ns1 != Namespace.NO_NAMESPACE) && (ns1 != Namespace.XML_NAMESPACE)) {
                    String prefix = ns1.getPrefix();
                    String uri = namespaces.getURI(prefix);
                    if (!ns1.getURI().equals(uri)) { // output a new decl
                        String attrName = getXMLNSTagFor(ns1);
                        domElement.setAttribute(attrName, ns1.getURI());
                        namespaces.push(ns1);
                    }
                }
                // Crimson doesn't like setAttributeNS() for non-NS attribs
                if (attribute.getNamespace() == Namespace.NO_NAMESPACE) {
                    // No namespace, use setAttribute
                    if (forceNamespaceAware) {
                        domElement.setAttributeNS(null, attribute.getQualifiedName(), attribute.getValue());
                    } else {
                        domElement.setAttribute(attribute.getQualifiedName(), attribute.getValue());
                    }
                } else {
                    domElement.setAttributeNS(attribute.getNamespaceURI(), attribute.getQualifiedName(),
                            attribute.getValue());
                }
            }

            // Add content to the DOM element
            for (Content content : element.getContent()) {

                if (content instanceof Element) {
                    Element e = (Element) content;
                    org.w3c.dom.Element domElt = output(e, domDoc, namespaces);
                    domElement.appendChild(domElt);
//                } else if (node instanceof String) {
//                    String str = (String) node;
//                    org.w3c.dom.Text domText = domDoc.createTextNode(str);
//                    domElement.appendChild(domText);
                } else if (content instanceof CDATA) {
                    CDATA cdata = (CDATA) content;
                    CDATASection domCdata = domDoc.createCDATASection(cdata.getText());
                    domElement.appendChild(domCdata);
                } else if (content instanceof Text) {
                    Text text = (Text) content;
                    org.w3c.dom.Text domText = domDoc.createTextNode(text.getText());
                    domElement.appendChild(domText);
                } else if (content instanceof Comment) {
                    Comment comment = (Comment) content;
                    org.w3c.dom.Comment domComment = domDoc.createComment(comment.getText());
                    domElement.appendChild(domComment);
                } else if (content instanceof ProcessingInstruction) {
                    ProcessingInstruction pi = (ProcessingInstruction) content;
                    org.w3c.dom.ProcessingInstruction domPI =
                            domDoc.createProcessingInstruction(pi.getTarget(), pi.getData());
                    domElement.appendChild(domPI);
                } else if (content instanceof EntityRef) {
                    EntityRef entity = (EntityRef) content;
                    EntityReference domEntity = domDoc.createEntityReference(entity.getName());
                    domElement.appendChild(domEntity);
                } else {
                    throw new CoffeeDOMException("Element contained content with type:" + content.getClass().getName());
                }

            }

            // Remove declared namespaces from stack
            while (namespaces.size() > previouslyDeclaredNamespaces) {
                namespaces.pop();
            }

            return domElement;
        } catch (Exception e) {
            throw new CoffeeDOMException("Exception outputting Element " + element.getQualifiedName(), e);
        }
    }

    private org.w3c.dom.Attr output(Attribute attribute, org.w3c.dom.Document domDoc) throws CoffeeDOMException {
        org.w3c.dom.Attr domAttr = null;
        try {
            if (attribute.getNamespace() == Namespace.NO_NAMESPACE) {
                // No namespace, use createAttribute
                if (forceNamespaceAware) {
                    domAttr = domDoc.createAttributeNS(null, attribute.getQualifiedName());
                } else {
                    domAttr = domDoc.createAttribute(attribute.getQualifiedName());
                }
            } else {
                domAttr = domDoc.createAttributeNS(attribute.getNamespaceURI(), attribute.getQualifiedName());
            }
            domAttr.setValue(attribute.getValue());
        } catch (Exception e) {
            throw new CoffeeDOMException("Exception outputting Attribute " + attribute.getQualifiedName(), e);
        }
        return domAttr;
    }

    /**
     * This will handle adding any <code>{@link Namespace}</code> attributes to the DOM tree.
     *
     * @param ns <code>Namespace</code> to add definition of
     * @return
     */
    private static String getXMLNSTagFor(Namespace ns) {
        String attrName = "xmlns";
        if (!ns.getPrefix().equals("")) {
            attrName += ":";
            attrName += ns.getPrefix();
        }
        return attrName;
    }
}