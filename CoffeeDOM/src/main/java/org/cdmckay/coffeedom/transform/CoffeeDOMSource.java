/*-- 

 Copyright (C) 2001-2007 Jason Hunter & Brett McLaughlin.
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

package org.cdmckay.coffeedom.transform;

import org.cdmckay.coffeedom.Content;
import org.cdmckay.coffeedom.Document;
import org.cdmckay.coffeedom.Element;
import org.cdmckay.coffeedom.CoffeeDOMException;
import org.cdmckay.coffeedom.output.SAXOutputter;
import org.cdmckay.coffeedom.output.XMLOutputter;
import org.xml.sax.*;

import javax.xml.transform.sax.SAXSource;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * A holder for an XML Transformation source: a Document, Element, or list of nodes.
 * <p/>
 * The is provides input to a {@link javax.xml.transform.Transformer JAXP TrAX Transformer}.
 * <p/>
 * The following example shows how to apply an XSL Transformation to a CoffeeDOM document and get the transformation result
 * in the form of a list of CoffeeDOM nodes:
 * <pre><code>
 *   public static List&lt;Content&gt; transform(Document doc, String stylesheet)
 *                                        throws CoffeeDOMException {
 *     try {
 *       Transformer transformer = TransformerFactory.newInstance()
 *              .newTransformer(new StreamSource(stylesheet));
 *       CoffeeDOMSource in = new CoffeeDOMSource(doc);
 *       CoffeeDOMResult out = new CoffeeDOMResult();
 *       transformer.transform(in, out);
 *       return out.getResult();
 *     }
 *     catch (TransformerException e) {
 *       throw new CoffeeDOMException("XSLT Transformation failed", e);
 *     }
 *   }
 * </code></pre>
 *
 * @author Laurent Bihanic
 * @author Jason Hunter
 * @see CoffeeDOMResult
 */
public class CoffeeDOMSource
        extends SAXSource {

    /**
     * If {@link javax.xml.transform.TransformerFactory#getFeature(String)} returns <code>true</code> when passed this
     * value as an argument, the Transformer natively supports CoffeeDOM. <p> <strong>Note</strong>: This implementation does
     * not override the {@link SAXSource#FEATURE} value defined by its superclass to be considered as a SAXSource by
     * Transformer implementations not natively supporting CoffeeDOM. </p>
     */
    public final static String CoffeeDOM_FEATURE = "http://CoffeeDOMSource/feature";

    /**
     * The XMLReader object associated to this source or <code>null</code> if no XMLReader has yet been requested.
     *
     * @see #getXMLReader
     */
    private XMLReader xmlReader = null;

    /**
     * Optional entity resolver associated to the source of this document or <code>null</code> if no EntityResolver was
     * supplied with this CoffeeDOMSource.
     *
     * @see #buildDocumentReader()
     */
    private EntityResolver resolver = null;

    /**
     * Creates a CoffeeDOM TrAX source wrapping a CoffeeDOM document.
     *
     * @param source the CoffeeDOM document to use as source for the transformations
     * @throws IllegalArgumentException if <code>source</code> is <code>null</code>.
     */
    public CoffeeDOMSource(Document source) {
        setDocument(source);
    }

    /**
     * Creates a CoffeeDOM TrAX source wrapping a list of CoffeeDOM nodes.
     *
     * @param source the CoffeeDOM nodes to use as source for the transformations.
     * @throws IllegalArgumentException if <code>source</code> is <code>null</code>.
     */
    public CoffeeDOMSource(List<Content> source) {
        setNodes(source);
    }

    /**
     * Creates a CoffeeDOM TrAX source wrapping a CoffeeDOM element.
     *
     * @param source the CoffeeDOM element to use as source for the transformations
     * @throws IllegalArgumentException if <code>source</code> is <code>null</code>.
     */
    public CoffeeDOMSource(Element source) {
        List<Content> nodes = new ArrayList<Content>();
        nodes.add(source);

        setNodes(nodes);
    }

    /**
     * Creates a CoffeeDOM TrAX source wrapping a CoffeeDOM element with an associated EntityResolver to resolve external
     * entities.
     *
     * @param source   The CoffeeDOM Element to use as source for the transformations
     * @param resolver Entity resolver to use for the source transformation
     * @throws IllegalArgumentException if<code>source</code> is <code>null</code>
     */
    public CoffeeDOMSource(Document source, EntityResolver resolver) {
        setDocument(source);
        this.resolver = resolver;
    }

    /**
     * Sets the source document used by this TrAX source.
     *
     * @param source the CoffeeDOM document to use as source for the transformations
     * @throws IllegalArgumentException if <code>source</code> is <code>null</code>.
     * @see #getDocument
     */
    public void setDocument(Document source) {
        super.setInputSource(new CoffeeDOMInputSource(source));
    }

    /**
     * Returns the source document used by this TrAX source.
     *
     * @return the source document used by this TrAX source or <code>null</code> if the source is a node list.
     * @see #setDocument(org.cdmckay.coffeedom.Document)
     */
    public Document getDocument() {
        Object source = ((CoffeeDOMInputSource) getInputSource()).getSource();
        Document doc = null;

        if (source instanceof Document) {
            doc = (Document) source;
        }
        return doc;
    }

    /**
     * Sets the source node list used by this TrAX source.
     *
     * @param source the CoffeeDOM nodes to use as source for the transformations
     * @throws IllegalArgumentException if <code>source</code> is <code>null</code>.
     * @see #getNodes
     */
    public void setNodes(List<Content> source) {
        super.setInputSource(new CoffeeDOMInputSource(source));
    }

    /**
     * Returns the source node list used by this TrAX source.
     *
     * @return the source node list used by this TrAX source or <code>null</code> if the source is a CoffeeDOM document.
     * @see #setDocument(org.cdmckay.coffeedom.Document)
     */
    @SuppressWarnings("unchecked")
    public List<Content> getNodes() {
        Object source = ((CoffeeDOMInputSource) getInputSource()).getSource();
        List<Content> nodes = null;

        if (source instanceof List) {
            nodes = (List<Content>) source;
        }

        return nodes;
    }

    //-------------------------------------------------------------------------
    // SAXSource overwritten methods
    //-------------------------------------------------------------------------

    /**
     * Sets the SAX InputSource to be used for the Source. <p> As this implementation only supports CoffeeDOM document as
     * data source, this method always throws an {@link UnsupportedOperationException}. </p>
     *
     * @param inputSource a valid InputSource reference.
     * @throws UnsupportedOperationException always!
     */
    public void setInputSource(InputSource inputSource) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    /**
     * Set the XMLReader to be used for the Source. <p> As this implementation only supports CoffeeDOM document as data
     * source, this method throws an {@link UnsupportedOperationException} if the provided reader object does not
     * implement the SAX {@link XMLFilter} interface.  Otherwise, the CoffeeDOM document reader will be attached as parent of
     * the filter chain.</p>
     *
     * @param reader a valid XMLReader or XMLFilter reference.
     * @throws UnsupportedOperationException if <code>reader</code> is not a SAX {@link XMLFilter}.
     * @see #getXMLReader
     */
    public void setXMLReader(XMLReader reader) throws UnsupportedOperationException {
        if (reader instanceof XMLFilter) {
            // Connect the filter chain to a document reader.
            XMLFilter filter = (XMLFilter) reader;
            while (filter.getParent() instanceof XMLFilter) {
                filter = (XMLFilter) (filter.getParent());
            }
            filter.setParent(buildDocumentReader());

            // Read XML data from filter chain.
            this.xmlReader = reader;
        } else {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * Returns the XMLReader to be used for the Source. <p> This implementation returns a specific XMLReader reading the
     * XML data from the source CoffeeDOM document. </p>
     *
     * @return an XMLReader reading the XML data from the source CoffeeDOM document.
     */
    public XMLReader getXMLReader() {
        if (this.xmlReader == null) {
            this.xmlReader = buildDocumentReader();
        }
        return this.xmlReader;
    }

    /**
     * Build an XMLReader to be used for the source. This will create a new instance of DocumentReader with an
     * EntityResolver instance if available.
     *
     * @return XMLReader reading the XML data from the source CoffeeDOM document with an optional EntityResolver
     */
    private XMLReader buildDocumentReader() {
        DocumentReader reader = new DocumentReader();
        if (resolver != null) {
            reader.setEntityResolver(resolver);
        }
        return reader;
    }

    /**
     * A subclass of the SAX InputSource interface that wraps a CoffeeDOM Document. <p> This class is nested in CoffeeDOMSource as
     * it is not intended to be used independently of its friend: DocumentReader. </p>
     *
     * @see org.cdmckay.coffeedom.Document
     */
    private static class CoffeeDOMInputSource
            extends InputSource {

        /**
         * The source as a CoffeeDOM document or a list of CoffeeDOM nodes.
         */
        // TODO Might be a good idea to separate this into two variables.
        private Object source = null;

        /**
         * Builds a InputSource wrapping the specified CoffeeDOM Document.
         *
         * @param document the source document.
         */
        public CoffeeDOMInputSource(Document document) {
            this.source = document;
        }

        /**
         * Builds a InputSource wrapping a list of CoffeeDOM nodes.
         *
         * @param nodes the source CoffeeDOM nodes.
         */
        public CoffeeDOMInputSource(List<Content> nodes) {
            this.source = nodes;
        }

        /**
         * Returns the source.
         *
         * @return the source as a CoffeeDOM document or a list of CoffeeDOM nodes.
         */
        public Object getSource() {
            return source;
        }

        /**
         * Sets the character stream for this input source. <p> This implementation always throws an {@link
         * UnsupportedOperationException} as the only source stream supported is the source CoffeeDOM document. </p>
         *
         * @param characterStream a character stream containing an XML document.
         * @throws UnsupportedOperationException always!
         */
        @Override
        public void setCharacterStream(Reader characterStream) throws UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }

        /**
         * Gets the character stream for this input source. <p> Note that this method is only provided to make this
         * InputSource implementation acceptable by any XML parser.  As it generates an in-memory string representation
         * of the CoffeeDOM document, it is quite inefficient from both speed and memory consumption points of view. </p>
         *
         * @return a Reader to a string representation of the source CoffeeDOM document.
         */
        @SuppressWarnings("unchecked")
        public Reader getCharacterStream() {
            Object source = this.getSource();
            Reader reader = null;

            if (source instanceof Document) {
                // Get an in-memory string representation of the document
                // and return a reader on it.
                reader = new StringReader(new XMLOutputter().outputString((Document) source));
            } else {
                if (source instanceof List) {
                    reader = new StringReader(new XMLOutputter().outputString((List<Content>) source));
                }
                // Else: No source, no reader!
            }
            return reader;
        }
    }

    /**
     * An implementation of the SAX2 XMLReader interface that presents a SAX view of a CoffeeDOM Document.  The actual
     * generation of the SAX events is delegated to CoffeeDOM's SAXOutputter.
     *
     * @see org.cdmckay.coffeedom.Document
     * @see org.cdmckay.coffeedom.output.SAXOutputter
     */
    private static class DocumentReader
            extends SAXOutputter
            implements XMLReader {

        /**
         * Public default constructor.
         */
        public DocumentReader() {
            super();
        }

        //----------------------------------------------------------------------
        // SAX XMLReader interface support
        //----------------------------------------------------------------------

        /**
         * Parses an XML document from a system identifier (URI). <p> This implementation does not support reading XML
         * data from system identifiers, only from CoffeeDOM documents.  Hence, this method always throws a {@link
         * SAXNotSupportedException}. </p>
         *
         * @param systemId the system identifier (URI).
         * @throws SAXNotSupportedException always!
         */
        public void parse(String systemId) throws SAXNotSupportedException {
            throw new SAXNotSupportedException("Only CoffeeDOM Documents are supported as input");
        }

        /**
         * Parses an XML document. <p> The methods accepts only <code>CoffeeDOMInputSource</code>s instances as input
         * sources. </p>
         *
         * @param input the input source for the top-level of the XML document.
         * @throws SAXException             any SAX exception, possibly wrapping another exception.
         * @throws SAXNotSupportedException if the input source does not wrap a CoffeeDOM document.
         */
        @SuppressWarnings("unchecked")
        public void parse(InputSource input) throws SAXException {
            if (input instanceof CoffeeDOMInputSource) {
                try {
                    Object source = ((CoffeeDOMInputSource) input).getSource();
                    if (source instanceof Document) {
                        this.output((Document) source);
                    } else {
                        this.output((List<Content>) source);
                    }
                } catch (CoffeeDOMException e) {
                    throw new SAXException(e.getMessage(), e);
                }
            } else {
                throw new SAXNotSupportedException("Only CoffeeDOM Documents are supported as input");
            }
        }
    }
}

