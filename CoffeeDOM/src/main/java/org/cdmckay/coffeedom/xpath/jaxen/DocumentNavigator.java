/*
 * ====================================================================
 * Copyright 2000-2005 Bob McWhirter & James Strachan.
 * All rights reserved.
 *
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

import org.cdmckay.coffeedom.*;
import org.cdmckay.coffeedom.input.SAXBuilder;
import org.jaxen.*;
import org.jaxen.saxpath.SAXPathException;
import org.jaxen.util.SingleObjectIterator;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Interface for navigating around the CoffeeDOM object model. <p/> <p> This class is not intended for direct usage, but is
 * used by the Jaxen engine during evaluation. </p>
 *
 * @author <a href="mailto:bob@werken.com">Bob McWhirter</a>
 * @author Stephen Colebourne
 * @author Cameron McKay
 * @see XPath
 */
public class DocumentNavigator
        extends DefaultNavigator
        implements NamedAccessNavigator {

    private static final long serialVersionUID = -1636727587303584165L;

    /**
     * Singleton implementation.
     */
    private static class Singleton {

        /** Singleton instance.
         */
        private static DocumentNavigator instance = new DocumentNavigator();
    }

    public static Navigator getInstance() {
        return Singleton.instance;
    }

    public boolean isElement(Object object) {
        return object instanceof Element;
    }

    public boolean isComment(Object object) {
        return object instanceof Comment;
    }

    public boolean isText(Object object) {
        return (object instanceof Text);
    }

    public boolean isAttribute(Object object) {
        return object instanceof Attribute;
    }

    public boolean isProcessingInstruction(Object object) {
        return object instanceof ProcessingInstruction;
    }

    public boolean isDocument(Object object) {
        return object instanceof Document;
    }

    public boolean isNamespace(Object object) {
        return object instanceof Namespace || object instanceof XPathNamespace;
    }

    public String getElementName(Object object) {
        Element element = (Element) object;

        return element.getName();
    }

    public String getElementNamespaceUri(Object object) {
        Element element = (Element) object;

        String uri = element.getNamespaceURI();
        if (uri != null && uri.length() == 0) {
            return null;
        } else {
            return uri;
        }
    }

    public String getAttributeName(Object object) {
        Attribute attr = (Attribute) object;

        return attr.getName();
    }

    public String getAttributeNamespaceUri(Object object) {
        Attribute attr = (Attribute) object;

        String uri = attr.getNamespaceURI();
        if (uri != null && uri.length() == 0) {
            return null;
        } else {
            return uri;
        }
    }

    public Iterator getChildAxisIterator(Object contextNode) {
        if (contextNode instanceof Element) {
            return ((Element) contextNode).getContent().iterator();
        } else if (contextNode instanceof Document) {
            return ((Document) contextNode).getContent().iterator();
        }

        return JaxenConstants.EMPTY_ITERATOR;
    }

    /**
     * Retrieves an <code>Iterator</code> over the child elements that
     * match the supplied local name and namespace URI.
     *
     * @param contextNode      the origin context node
     * @param localName        the local name of the children to return, always present
     * @param namespacePrefix  ignored; prefixes are not used when matching in XPath
     * @param namespaceURI     the URI of the namespace of the children to return
     * @return an Iterator     that traverses the named children, or null if none
     */
    public Iterator getChildAxisIterator(
            Object contextNode, String localName, String namespacePrefix, String namespaceURI) {

        if (contextNode instanceof Element) {
            Element node = (Element) contextNode;
            if (namespaceURI == null) {
                return node.getChildren(localName).iterator();
            }
            return node.getChildren(localName, Namespace.getNamespace(namespacePrefix, namespaceURI)).iterator();
        }
        if (contextNode instanceof Document) {
            Document node = (Document) contextNode;

            Element el = node.getRootElement();
            if (!el.getName().equals(localName)) {
                return JaxenConstants.EMPTY_ITERATOR;
            }
            if (namespaceURI != null) {
                // CoffeeDOM's equals method does not consider the prefix when comparing namespace objects
                if (!Namespace.getNamespace(namespacePrefix, namespaceURI).equals(el.getNamespace())) {
                    return JaxenConstants.EMPTY_ITERATOR;
                }
            } else if (el.getNamespace() != Namespace.NO_NAMESPACE) {
                return JaxenConstants.EMPTY_ITERATOR;
            }

            return new SingleObjectIterator(el);
        }

        return JaxenConstants.EMPTY_ITERATOR;
    }

    public Iterator getNamespaceAxisIterator(Object contextNode) {
        if (!(contextNode instanceof Element)) {
            return JaxenConstants.EMPTY_ITERATOR;
        }

        Map<String, XPathNamespace> nsMap = new HashMap<String, XPathNamespace>();

        Element element = (Element) contextNode;
        Element current = element;

        while (current != null) {

            Namespace ns = current.getNamespace();

            if (ns != Namespace.NO_NAMESPACE) {
                if (!nsMap.containsKey(ns.getPrefix())) {
                    nsMap.put(ns.getPrefix(), new XPathNamespace(element, ns));
                }
            }

            for (Namespace namespace : current.getAdditionalNamespaces()) {

                if (!nsMap.containsKey(namespace.getPrefix())) {
                    nsMap.put(namespace.getPrefix(), new XPathNamespace(element, namespace));
                }
            }

            for (Attribute attribute : current.getAttributes()) {
                Namespace attrNS = attribute.getNamespace();

                if (attrNS != Namespace.NO_NAMESPACE) {
                    if (!nsMap.containsKey(attrNS.getPrefix())) {
                        nsMap.put(attrNS.getPrefix(), new XPathNamespace(element, attrNS));
                    }
                }
            }

            if (current.getParent() instanceof Element) {
                current = (Element) current.getParent();
            } else {
                current = null;
            }
        }

        nsMap.put("xml", new XPathNamespace(element, Namespace.XML_NAMESPACE));

        return nsMap.values().iterator();
    }

    public Iterator getParentAxisIterator(Object contextNode) {
        Object parent = null;

        if (contextNode instanceof Document) {
            return JaxenConstants.EMPTY_ITERATOR;
        } else if (contextNode instanceof Element) {
            parent = ((Element) contextNode).getParent();

            if (parent == null) {
                if (((Element) contextNode).isRootElement()) {
                    parent = ((Element) contextNode).getDocument();
                }
            }
        } else if (contextNode instanceof Attribute) {
            parent = ((Attribute) contextNode).getParent();
        } else if (contextNode instanceof XPathNamespace) {
            parent = ((XPathNamespace) contextNode).getCoffeeDOMElement();
        } else if (contextNode instanceof ProcessingInstruction) {
            parent = ((ProcessingInstruction) contextNode).getParent();
        } else if (contextNode instanceof Comment) {
            parent = ((Comment) contextNode).getParent();
        } else if (contextNode instanceof Text) {
            parent = ((Text) contextNode).getParent();
        }

        if (parent != null) {
            return new SingleObjectIterator(parent);
        }

        return JaxenConstants.EMPTY_ITERATOR;
    }

    public Iterator getAttributeAxisIterator(Object contextNode) {
        if (!(contextNode instanceof Element)) {
            return JaxenConstants.EMPTY_ITERATOR;
        }

        Element element = (Element) contextNode;
        return element.getAttributes().iterator();
    }

    /**
     * Retrieves an <code>Iterator</code> over the attribute elements that
     * match the supplied name.
     *
     * @param contextNode      the origin context node
     * @param localName        the local name of the attributes to return, always present
     * @param namespacePrefix  the prefix of the namespace of the attributes to return
     * @param namespaceURI     the URI of the namespace of the attributes to return
     * @return an Iterator     that traverses the named attributes, not null
     */
    public Iterator getAttributeAxisIterator(
            Object contextNode, String localName, String namespacePrefix, String namespaceURI) {

        if (contextNode instanceof Element) {
            Element node = (Element) contextNode;
            Namespace namespace = (namespaceURI == null ? Namespace.NO_NAMESPACE :
                    Namespace.getNamespace(namespacePrefix, namespaceURI));
            Attribute attr = node.getAttribute(localName, namespace);
            if (attr != null) {
                return new SingleObjectIterator(attr);
            }
        }
        return JaxenConstants.EMPTY_ITERATOR;
    }

    /** Returns a parsed form of the given XPath string, which will be suitable
     *  for queries on JDOM documents.
     */
    public XPath parseXPath(String xpath) throws SAXPathException {
        return new CoffeeDOMXPath(xpath);
    }

    public Object getDocumentNode(Object contextNode) {
        if (contextNode instanceof Document) {
            return contextNode;
        }

        Element element = (Element) contextNode;
        return element.getDocument();
    }

    public String getElementQName(Object object) {
        Element element = (Element) object;

        String prefix = element.getNamespacePrefix();

        if (prefix == null || prefix.length() == 0) {
            return element.getName();
        }

        return prefix + ":" + element.getName();
    }

    public String getAttributeQName(Object object) {
        Attribute attr = (Attribute) object;

        String prefix = attr.getNamespacePrefix();

        if (prefix == null || "".equals(prefix)) {
            return attr.getName();
        }

        return prefix + ":" + attr.getName();
    }

    public String getNamespaceStringValue(Object object) {
        if (object instanceof Namespace) {

            Namespace ns = (Namespace) object;
            return ns.getURI();
        } else {

            XPathNamespace ns = (XPathNamespace) object;
            return ns.getJDOMNamespace().getURI();
        }

    }

    public String getNamespacePrefix(Object object) {
        if (object instanceof Namespace) {

            Namespace ns = (Namespace) object;
            return ns.getPrefix();
        } else {

            XPathNamespace ns = (XPathNamespace) object;
            return ns.getJDOMNamespace().getPrefix();
        }
    }

    public String getTextStringValue(Object object) {
        if (object instanceof Text) {
            return ((Text) object).getText();
        }

        return "";
    }

    public String getAttributeStringValue(Object object) {
        Attribute attr = (Attribute) object;

        return attr.getValue();
    }

    public String getElementStringValue(Object object) {
        Element element = (Element) object;
        StringBuilder builder = new StringBuilder();

        for (Content content : element.getContent()) {
            if (content instanceof Text) {
                builder.append(((Text) content).getText());
            } else if (content instanceof Element) {
                builder.append(getElementStringValue(content));
            }
        }

        return builder.toString();
    }

    public String getProcessingInstructionTarget(Object obj) {
        ProcessingInstruction pi = (ProcessingInstruction) obj;

        return pi.getTarget();
    }

    public String getProcessingInstructionData(Object obj) {
        ProcessingInstruction pi = (ProcessingInstruction) obj;

        return pi.getData();
    }

    public String getCommentStringValue(Object obj) {
        Comment cmt = (Comment) obj;

        return cmt.getText();
    }

    public String translateNamespacePrefixToUri(String prefix, Object context) {
        Element element = null;
        if (context instanceof Element) {
            element = (Element) context;
        } else if (context instanceof Text) {
            element = (Element) ((Text) context).getParent();
        } else if (context instanceof Attribute) {
            element = ((Attribute) context).getParent();
        } else if (context instanceof XPathNamespace) {
            element = ((XPathNamespace) context).getCoffeeDOMElement();
        } else if (context instanceof Comment) {
            element = (Element) ((Comment) context).getParent();
        } else if (context instanceof ProcessingInstruction) {
            element = (Element) ((ProcessingInstruction) context).getParent();
        }

        if (element != null) {
            Namespace namespace = element.getNamespace(prefix);

            if (namespace != null) {
                return namespace.getURI();
            }
        }
        return null;
    }

    public Object getDocument(String url) throws FunctionCallException {
        try {
            SAXBuilder builder = new SAXBuilder();

            return builder.build(url);
        } catch (Exception e) {
            throw new FunctionCallException(e.getMessage());
        }
    }
}
