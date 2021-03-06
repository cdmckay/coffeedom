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

import org.xml.sax.*;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;

/**
 * Base class for implementing an XML reader.
 * <p/>
 * Adapted from David Megginson's XMLFilterImpl and XMLFilterBase.
 */
public abstract class XMLReaderBase
        extends DefaultHandler
        implements LexicalHandler, XMLReader {

    protected static final Attributes EMPTY_ATTRIBUTES = new AttributesImpl();

    protected static final String[] LEXICAL_HANDLER_NAMES = {
            "http://xml.org/sax/properties/lexical-handler",
            "http://xml.org/sax/handlers/LexicalHandler"
    };

    private Locator locator = null;
    private EntityResolver entityResolver = null;
    private DTDHandler dtdHandler = null;
    private ContentHandler contentHandler = null;
    private ErrorHandler errorHandler = null;
    private LexicalHandler lexicalHandler = null;

    /**
     * Creates new XMLReaderBase.
     */
    public XMLReaderBase() {
    }

    /**
     * Start a new element without a qname or attributes.
     * <p/>
     * <p>This method will provide a default empty attribute list and an empty string for the qualified name. It invokes
     * {@link #startElement(String, String, String, Attributes)} directly.</p>
     *
     * @param uri       The element's Namespace URI.
     * @param localName The element's local name.
     * @throws org.xml.sax.SAXException If a filter further down the chain raises an exception.
     * @see org.xml.sax.ContentHandler#startElement(String, String, String, org.xml.sax.Attributes)
     */
    public void startElement(String uri, String localName)
            throws SAXException {
        startElement(uri, localName, "", EMPTY_ATTRIBUTES);
    }

    /**
     * Start a new element without a Namespace URI or qname.
     * <p/>
     * <p>This method will provide an empty string for the Namespace URI, and empty string for the qualified name. It
     * invokes {@link #startElement(String, String, String, Attributes)} directly.</p>
     *
     * @param localName The element's local name.
     * @param attributes      The element's attribute list.
     * @throws org.xml.sax.SAXException If a filter further down the chain raises an exception.
     * @see org.xml.sax.ContentHandler#startElement(String, String, String, org.xml.sax.Attributes)
     */
    public void startElement(String localName, Attributes attributes)
            throws SAXException {
        startElement("", localName, "", attributes);
    }

    /**
     * Start a new element without a Namespace URI, qname, or attributes.
     * <p/>
     * <p>This method will provide an empty string for the Namespace URI, and empty string for the qualified name, and a
     * default empty attribute list. It invokes {@link #startElement(String, String, String, Attributes)} directly.</p>
     *
     * @param localName The element's local name.
     * @throws org.xml.sax.SAXException If a filter further down the chain raises an exception.
     * @see org.xml.sax.ContentHandler#startElement(String, String, String, org.xml.sax.Attributes)
     */
    public void startElement(String localName)
            throws SAXException {
        startElement("", localName, "", EMPTY_ATTRIBUTES);
    }

    /**
     * End an element without a qname.
     * <p/>
     * <p>This method will supply an empty string for the qName. It invokes {@link #endElement(String, String, String)}
     * directly.</p>
     *
     * @param uri       The element's Namespace URI.
     * @param localName The element's local name.
     * @throws org.xml.sax.SAXException If a filter further down the chain raises an exception.
     * @see org.xml.sax.ContentHandler#endElement(String, String, String)
     */
    public void endElement(String uri, String localName)
            throws SAXException {
        endElement(uri, localName, "");
    }

    /**
     * End an element without a Namespace URI or qname.
     * <p/>
     * <p>This method will supply an empty string for the qName and an empty string for the Namespace URI. It invokes
     * {@link #endElement(String, String, String)} directly.</p>
     *
     * @param localName The element's local name.
     * @throws org.xml.sax.SAXException If a filter further down the chain raises an exception.
     * @see org.xml.sax.ContentHandler#endElement(String, String, String)
     */
    public void endElement(String localName)
            throws SAXException {
        endElement("", localName, "");
    }

    /**
     * Add an empty element.
     * <p/>
     * Both a {@link #startElement startElement} and an {@link #endElement endElement} event will be passed on down the
     * filter chain.
     *
     * @param uri       The element's Namespace URI, or the empty string if the element has no Namespace or if Namespace
     *                  processing is not being performed.
     * @param localName The element's local name (without prefix).  This parameter must be provided.
     * @param qName     The element's qualified name (with prefix), or the empty string if none is available.  This
     *                  parameter is strictly advisory: the writer may or may not use the prefix attached.
     * @param attributes      The element's attribute list.
     * @throws org.xml.sax.SAXException If a filter further down the chain raises an exception.
     * @see org.xml.sax.ContentHandler#startElement(String, String, String, org.xml.sax.Attributes)
     * @see org.xml.sax.ContentHandler#endElement(String, String, String)
     */
    public void emptyElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException {
        startElement(uri, localName, qName, attributes);
        endElement(uri, localName, qName);
    }

    /**
     * Add an empty element without a qname or attributes.
     * <p/>
     * <p>This method will supply an empty string for the qname and an empty attribute list.  It invokes {@link
     * #emptyElement(String, String, String, Attributes)} directly.</p>
     *
     * @param uri       The element's Namespace URI.
     * @param localName The element's local name.
     * @throws org.xml.sax.SAXException If a filter further down the chain raises an exception.
     * @see #emptyElement(String, String, String, Attributes)
     */
    public void emptyElement(String uri, String localName)
            throws SAXException {
        emptyElement(uri, localName, "", EMPTY_ATTRIBUTES);
    }

    /**
     * Add an empty element without a Namespace URI or qname.
     * <p/>
     * <p>This method will provide an empty string for the Namespace URI, and empty string for the qualified name. It
     * invokes {@link #emptyElement(String, String, String, Attributes)} directly.</p>
     *
     * @param localName The element's local name.
     * @param attributes      The element's attribute list.
     * @throws org.xml.sax.SAXException If a filter further down the chain raises an exception.
     * @see org.xml.sax.ContentHandler#startElement(String, String, String, org.xml.sax.Attributes)
     */
    public void emptyElement(String localName, Attributes attributes)
            throws SAXException {
        emptyElement("", localName, "", attributes);
    }

    /**
     * Add an empty element without a Namespace URI, qname or attributes.
     * <p/>
     * <p>This method will supply an empty string for the qname, and empty string for the Namespace URI, and an empty
     * attribute list.  It invokes {@link #emptyElement(String, String, String, Attributes)} directly.</p>
     *
     * @param localName The element's local name.
     * @throws org.xml.sax.SAXException If a filter further down the chain raises an exception.
     * @see #emptyElement(String, String, String, Attributes)
     */
    public void emptyElement(String localName)
            throws SAXException {
        emptyElement("", localName, "", EMPTY_ATTRIBUTES);
    }

    /**
     * Add an element with character data content.
     * <p/>
     * <p>This is a convenience method to add a complete element with character data content, including the start tag
     * and end tag.</p>
     * <p/>
     * <p>This method invokes {@link @see org.xml.sax.ContentHandler#startElement}, followed by {@link
     * #characters(String)}, followed by {@link @see org.xml.sax.ContentHandler#endElement}.</p>
     *
     * @param uri       The element's Namespace URI.
     * @param localName The element's local name.
     * @param qName     The element's default qualified name.
     * @param atts      The element's attributes.
     * @param content   The character data content.
     * @throws org.xml.sax.SAXException If a filter further down the chain raises an exception.
     * @see org.xml.sax.ContentHandler#startElement(String, String, String, org.xml.sax.Attributes)
     * @see #characters(String)
     * @see org.xml.sax.ContentHandler#endElement(String, String, String)
     */
    public void dataElement(String uri, String localName,
                            String qName, Attributes atts,
                            String content)
            throws SAXException {
        startElement(uri, localName, qName, atts);
        characters(content);
        endElement(uri, localName, qName);
    }

    /**
     * Add an element with character data content but no qname or attributes.
     * <p/>
     * <p>This is a convenience method to add a complete element with character data content, including the start tag
     * and end tag.  This method provides an empty string for the qname and an empty attribute list. It invokes {@link
     * #dataElement(String, String, String, Attributes, String)}} directly.</p>
     *
     * @param uri       The element's Namespace URI.
     * @param localName The element's local name.
     * @param content   The character data content.
     * @throws org.xml.sax.SAXException If a filter further down the chain raises an exception.
     * @see org.xml.sax.ContentHandler#startElement(String, String, String, org.xml.sax.Attributes)
     * @see #characters(String)
     * @see org.xml.sax.ContentHandler#endElement(String, String, String)
     */
    public void dataElement(String uri, String localName, String content)
            throws SAXException {
        dataElement(uri, localName, "", EMPTY_ATTRIBUTES, content);
    }

    /**
     * Add an element with character data content but no Namespace URI or qname.
     * <p/>
     * <p>This is a convenience method to add a complete element with character data content, including the start tag
     * and end tag.  The method provides an empty string for the Namespace URI, and empty string for the qualified name.
     * It invokes {@link #dataElement(String, String, String, Attributes, String)}} directly.</p>
     *
     * @param localName The element's local name.
     * @param attributes      The element's attributes.
     * @param content   The character data content.
     * @throws org.xml.sax.SAXException If a filter further down the chain raises an exception.
     * @see org.xml.sax.ContentHandler#startElement(String, String, String, org.xml.sax.Attributes)
     * @see #characters(String)
     * @see org.xml.sax.ContentHandler#endElement(String, String, String)
     */
    public void dataElement(String localName, Attributes attributes, String content)
            throws SAXException {
        dataElement("", localName, "", attributes, content);
    }

    /**
     * Add an element with character data content but no attributes or Namespace URI.
     * <p/>
     * <p>This is a convenience method to add a complete element with character data content, including the start tag
     * and end tag.  The method provides an empty string for the Namespace URI, and empty string for the qualified name,
     * and an empty attribute list. It invokes {@link #dataElement(String, String, String, Attributes, String)}}
     * directly.</p>
     *
     * @param localName The element's local name.
     * @param content   The character data content.
     * @throws org.xml.sax.SAXException If a filter further down the chain raises an exception.
     * @see org.xml.sax.ContentHandler#startElement(String, String, String, org.xml.sax.Attributes)
     * @see #characters(String)
     * @see org.xml.sax.ContentHandler#endElement(String, String, String)
     */
    public void dataElement(String localName, String content)
            throws SAXException {
        dataElement("", localName, "", EMPTY_ATTRIBUTES, content);
    }

    /**
     * Add a string of character data, with XML escaping.
     * <p/>
     * <p>This is a convenience method that takes an XML String, converts it to a character array, then invokes {@link
     * @see org.xml.sax.ContentHandler#characters}.</p>
     *
     * @param data The character data.
     * @throws org.xml.sax.SAXException If a filter further down the chain raises an exception.
     * @see @see org.xml.sax.ContentHandler#characters
     */
    public void characters(String data)
            throws SAXException {
        char ch[] = data.toCharArray();
        characters(ch, 0, ch.length);
    }

    /**
     * Set the state of a feature.
     * <p/>
     * <p>This will always fail.</p>
     *
     * @param name  The feature name.
     * @param state The requested feature state.
     * @throws org.xml.sax.SAXNotRecognizedException
     *          When the XMLReader does not recognize the feature name.
     * @throws org.xml.sax.SAXNotSupportedException
     *          When the XMLReader recognizes the feature name but cannot set the requested value.
     * @see org.xml.sax.XMLReader#setFeature(String, boolean)
     */
    public void setFeature(String name, boolean state)
            throws SAXNotRecognizedException, SAXNotSupportedException {
        throw new SAXNotRecognizedException("Feature: " + name);
    }

    /**
     * Look up the state of a feature.
     * <p/>
     * <p>This will always fail.</p>
     *
     * @param name The feature name.
     * @return The current state of the feature.
     * @throws org.xml.sax.SAXNotRecognizedException
     *          When the XMLReader does not recognize the feature name.
     * @throws org.xml.sax.SAXNotSupportedException
     *          When the XMLReader recognizes the feature name but cannot determine its state at this time.
     * @see org.xml.sax.XMLReader#getFeature(String)
     */
    public boolean getFeature(String name)
            throws SAXNotRecognizedException, SAXNotSupportedException {
        throw new SAXNotRecognizedException("Feature: " + name);
    }

    /**
     * Set the value of a property.
     * <p/>
     * <p>Only lexical-handler properties are recognized.</p>
     *
     * @param name  The property name.
     * @param value The requested property value.
     * @throws org.xml.sax.SAXNotRecognizedException
     *          When the XMLReader does not recognize the property name.
     * @throws org.xml.sax.SAXNotSupportedException
     *          When the XMLReader recognizes the property name but cannot set the requested value.
     * @see org.xml.sax.XMLReader#setProperty(String, Object)
     */
    public void setProperty(String name, Object value)
            throws SAXNotRecognizedException, SAXNotSupportedException {
        for (int i = 0; i < LEXICAL_HANDLER_NAMES.length; i++) {
            if (LEXICAL_HANDLER_NAMES[i].equals(name)) {
                setLexicalHandler((LexicalHandler) value);
                return;
            }
        }
        throw new SAXNotRecognizedException("Property: " + name);
    }

    /**
     * Look up the value of a property.
     * <p/>
     * <p>Only lexical-handler properties are recognized.</p>
     *
     * @param name The property name.
     * @return The current value of the property.
     * @throws org.xml.sax.SAXNotRecognizedException
     *          When the XMLReader does not recognize the feature name.
     * @throws org.xml.sax.SAXNotSupportedException
     *          When the XMLReader recognizes the property name but cannot determine its value at this time.
     * @see org.xml.sax.XMLReader#setFeature(String, boolean)
     */
    public Object getProperty(String name)
            throws SAXNotRecognizedException, SAXNotSupportedException {
        for (int i = 0; i < LEXICAL_HANDLER_NAMES.length; i++) {
            if (LEXICAL_HANDLER_NAMES[i].equals(name)) {
                return getLexicalHandler();
            }
        }
        throw new SAXNotRecognizedException("Property: " + name);
    }

    /**
     * Parse a document. Subclass must implement.
     *
     * @param input The input source for the document entity.
     * @throws org.xml.sax.SAXException Any SAX exception, possibly wrapping another exception.
     * @throws java.io.IOException      An IO exception from the parser, possibly from a byte stream or character stream
     *                                  supplied by the application.
     * @see org.xml.sax.XMLReader#parse(org.xml.sax.InputSource)
     */
    public abstract void parse(InputSource input)
            throws SAXException, IOException;

    /**
     * Parse a document.
     *
     * @param systemId The system identifier as a fully-qualified URI.
     * @throws org.xml.sax.SAXException Any SAX exception, possibly wrapping another exception.
     * @throws java.io.IOException      An IO exception from the parser, possibly from a byte stream or character stream
     *                                  supplied by the application.
     * @see org.xml.sax.XMLReader#parse(java.lang.String)
     */
    public void parse(String systemId)
            throws SAXException, IOException {
        parse(new InputSource(systemId));
    }

    /**
     * Set the entity resolver.
     *
     * @param resolver The new entity resolver.
     * @throws java.lang.NullPointerException If the resolver is null.
     * @see org.xml.sax.XMLReader#setEntityResolver(org.xml.sax.EntityResolver)
     */
    public void setEntityResolver(EntityResolver resolver) {
        if (resolver == null) {
            throw new NullPointerException("Null entity resolver");
        } else {
            entityResolver = resolver;
        }
    }

    /**
     * Get the current entity resolver.
     *
     * @return The current entity resolver, or null if none was set.
     * @see org.xml.sax.XMLReader#getEntityResolver
     */
    public EntityResolver getEntityResolver() {
        return entityResolver;
    }

    /**
     * Set the DTD event handler.
     *
     * @param handler The new DTD handler.
     * @throws java.lang.NullPointerException If the handler is null.
     * @see org.xml.sax.XMLReader#setDTDHandler(org.xml.sax.DTDHandler)
     */
    public void setDTDHandler(DTDHandler handler) {
        if (handler == null) {
            throw new NullPointerException("Null DTD handler");
        } else {
            dtdHandler = handler;
        }
    }

    /**
     * Get the current DTD event handler.
     *
     * @return The current DTD handler, or null if none was set.
     * @see org.xml.sax.XMLReader#getDTDHandler
     */
    public DTDHandler getDTDHandler() {
        return dtdHandler;
    }

    /**
     * Set the content event handler.
     *
     * @param handler The new content handler.
     * @throws java.lang.NullPointerException If the handler is null.
     * @see org.xml.sax.XMLReader#setContentHandler(org.xml.sax.ContentHandler)
     */
    public void setContentHandler(ContentHandler handler) {
        if (handler == null) {
            throw new NullPointerException("Null content handler");
        } else {
            contentHandler = handler;
        }
    }

    /**
     * Get the content event handler.
     *
     * @return The current content handler, or null if none was set.
     * @see org.xml.sax.XMLReader#getContentHandler
     */
    public ContentHandler getContentHandler() {
        return contentHandler;
    }

    /**
     * Set the error event handler.
     *
     * @param handler The new error handler.
     * @throws java.lang.NullPointerException If the handler is null.
     * @see org.xml.sax.XMLReader#setErrorHandler(org.xml.sax.ErrorHandler)
     */
    public void setErrorHandler(ErrorHandler handler) {
        if (handler == null) {
            throw new NullPointerException("Null error handler");
        } else {
            errorHandler = handler;
        }
    }

    /**
     * Get the current error event handler.
     *
     * @return The current error handler, or null if none was set.
     * @see org.xml.sax.XMLReader#getErrorHandler
     */
    public ErrorHandler getErrorHandler() {
        return errorHandler;
    }

    /**
     * Set the lexical handler.
     *
     * @param handler The new lexical handler.
     * @throws java.lang.NullPointerException If the handler is null.
     */
    public void setLexicalHandler(LexicalHandler handler) {
        if (handler == null) {
            throw new NullPointerException("Null lexical handler");
        } else {
            lexicalHandler = handler;
        }
    }

    /**
     * Get the current lexical handler.
     *
     * @return The current lexical handler, or null if none was set.
     */
    public LexicalHandler getLexicalHandler() {
        return lexicalHandler;
    }

    /**
     * Resolves an external entity.
     *
     * @param publicId The entity's public identifier, or null.
     * @param systemId The entity's system identifier.
     * @return A new InputSource or null for the default.
     * @throws org.xml.sax.SAXException The client may throw an exception during processing.
     * @throws java.io.IOException      The client may throw an I/O-related exception while obtaining the new
     *                                  InputSource.
     * @see org.xml.sax.EntityResolver#resolveEntity(String, String)
     */
    public InputSource resolveEntity(String publicId, String systemId)
            throws SAXException /* IOException added in SAX2.01 bugfix release */ {
        if (entityResolver != null) {
            try {
                return entityResolver.resolveEntity(publicId, systemId);
            } catch (IOException ex) {
                throw new SAXException(ex);
            }
        } else {
            return null;
        }
    }

    /**
     * Add notation declaration.
     *
     * @param name     The notation name.
     * @param publicId The notation's public identifier, or null.
     * @param systemId The notation's system identifier, or null.
     * @throws org.xml.sax.SAXException The client may throw an exception during processing.
     * @see org.xml.sax.DTDHandler#notationDecl(String, String, String)
     */
    public void notationDecl(String name, String publicId, String systemId)
            throws SAXException {
        if (dtdHandler != null) {
            dtdHandler.notationDecl(name, publicId, systemId);
        }
    }

    /**
     * Add unparsed entity declaration.
     *
     * @param name         The entity name.
     * @param publicId     The entity's public identifier, or null.
     * @param systemId     The entity's system identifier, or null.
     * @param notationName The name of the associated notation.
     * @throws org.xml.sax.SAXException The client may throw an exception during processing.
     * @see org.xml.sax.DTDHandler#unparsedEntityDecl(String, String, String, String)
     */
    public void unparsedEntityDecl(String name, String publicId,
                                   String systemId, String notationName)
            throws SAXException {
        if (dtdHandler != null) {
            dtdHandler.unparsedEntityDecl(name, publicId, systemId,
                    notationName);
        }
    }

    /**
     * Assigns the document locator.
     *
     * @param locator The document locator.
     * @see org.xml.sax.ContentHandler#setDocumentLocator(org.xml.sax.Locator)
     */
    public void setDocumentLocator(Locator locator) {
        this.locator = locator;
        if (contentHandler != null) {
            contentHandler.setDocumentLocator(locator);
        }
    }

    /**
     * Send start of document.
     *
     * @throws org.xml.sax.SAXException The client may throw an exception during processing.
     * @see org.xml.sax.ContentHandler#startDocument
     */
    public void startDocument()
            throws SAXException {
        if (contentHandler != null) {
            contentHandler.startDocument();
        }
    }

    /**
     * Send end of document.
     *
     * @throws org.xml.sax.SAXException The client may throw an exception during processing.
     * @see org.xml.sax.ContentHandler#endDocument
     */
    public void endDocument()
            throws SAXException {
        if (contentHandler != null) {
            contentHandler.endDocument();
        }
    }

    /**
     * Sends start of namespace prefix mapping.
     *
     * @param prefix The Namespace prefix.
     * @param uri    The Namespace URI.
     * @throws org.xml.sax.SAXException The client may throw an exception during processing.
     * @see org.xml.sax.ContentHandler#startPrefixMapping(String, String)
     */
    public void startPrefixMapping(String prefix, String uri)
            throws SAXException {
        if (contentHandler != null) {
            contentHandler.startPrefixMapping(prefix, uri);
        }
    }

    /**
     * Sends end of namespace prefix mapping.
     *
     * @param prefix The Namespace prefix.
     * @throws org.xml.sax.SAXException The client may throw an exception during processing.
     * @see org.xml.sax.ContentHandler#endPrefixMapping(String)
     */
    public void endPrefixMapping(String prefix)
            throws SAXException {
        if (contentHandler != null) {
            contentHandler.endPrefixMapping(prefix);
        }
    }

    /**
     * Sends start of element.
     *
     * @param uri       The element's Namespace URI, or the empty string.
     * @param localName The element's local name, or the empty string.
     * @param qName     The element's qualified (prefixed) name, or the empty string.
     * @param atts      The element's attributes.
     * @throws org.xml.sax.SAXException The client may throw an exception during processing.
     * @see org.xml.sax.ContentHandler#startElement(String, String, String, org.xml.sax.Attributes)
     */
    public void startElement(String uri, String localName, String qName,
                             Attributes atts)
            throws SAXException {
        if (contentHandler != null) {
            contentHandler.startElement(uri, localName, qName, atts);
        }
    }

    /**
     * Sends end of element.
     *
     * @param uri       The element's Namespace URI, or the empty string.
     * @param localName The element's local name, or the empty string.
     * @param qName     The element's qualified (prefixed) name, or the empty string.
     * @throws org.xml.sax.SAXException The client may throw an exception during processing.
     * @see org.xml.sax.ContentHandler#endElement(String, String, String)
     */
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        if (contentHandler != null) {
            contentHandler.endElement(uri, localName, qName);
        }
    }

    /**
     * Sends character data.
     *
     * @param ch     An array of characters.
     * @param start  The starting position in the array.
     * @param length The number of characters to use from the array.
     * @throws org.xml.sax.SAXException The client may throw an exception during processing.
     * @see org.xml.sax.ContentHandler#characters(char[], int, int)
     */
    public void characters(char ch[], int start, int length)
            throws SAXException {
        if (contentHandler != null) {
            contentHandler.characters(ch, start, length);
        }
    }

    /**
     * Sends ignorable whitespace.
     *
     * @param ch     An array of characters.
     * @param start  The starting position in the array.
     * @param length The number of characters to use from the array.
     * @throws org.xml.sax.SAXException The client may throw an exception during processing.
     * @see org.xml.sax.ContentHandler#ignorableWhitespace(char[], int, int)
     */
    public void ignorableWhitespace(char ch[], int start, int length)
            throws SAXException {
        if (contentHandler != null) {
            contentHandler.ignorableWhitespace(ch, start, length);
        }
    }

    /**
     * Sends processing instruction.
     *
     * @param target The processing instruction target.
     * @param data   The text following the target.
     * @throws org.xml.sax.SAXException The client may throw an exception during processing.
     * @see org.xml.sax.ContentHandler#processingInstruction(String, String)
     */
    public void processingInstruction(String target, String data)
            throws SAXException {
        if (contentHandler != null) {
            contentHandler.processingInstruction(target, data);
        }
    }

    /**
     * Sends skipped entity.
     *
     * @param name The name of the skipped entity.
     * @throws org.xml.sax.SAXException The client may throw an exception during processing.
     * @see org.xml.sax.ContentHandler#skippedEntity(String)
     */
    public void skippedEntity(String name)
            throws SAXException {
        if (contentHandler != null) {
            contentHandler.skippedEntity(name);
        }
    }

    /**
     * Sends warning.
     *
     * @param e The nwarning as an exception.
     * @throws org.xml.sax.SAXException The client may throw an exception during processing.
     * @see org.xml.sax.ErrorHandler#warning(org.xml.sax.SAXParseException)
     */
    public void warning(SAXParseException e)
            throws SAXException {
        if (errorHandler != null) {
            errorHandler.warning(e);
        }
    }

    /**
     * Sends error.
     *
     * @param e The error as an exception.
     * @throws org.xml.sax.SAXException The client may throw an exception during processing.
     * @see org.xml.sax.ErrorHandler#error(org.xml.sax.SAXParseException)
     */
    public void error(SAXParseException e)
            throws SAXException {
        if (errorHandler != null) {
            errorHandler.error(e);
        }
    }

    /**
     * Sends fatal error.
     *
     * @param e The error as an exception.
     * @throws org.xml.sax.SAXException The client may throw an exception during processing.
     * @see org.xml.sax.ErrorHandler#fatalError(org.xml.sax.SAXParseException)
     */
    public void fatalError(SAXParseException e)
            throws SAXException {
        if (errorHandler != null) {
            errorHandler.fatalError(e);
        }
    }

    /**
     * Sends start of DTD.
     *
     * @param name     The document type name.
     * @param publicId The declared public identifier for the external DTD subset, or null if none was declared.
     * @param systemId The declared system identifier for the external DTD subset, or null if none was declared.
     * @throws org.xml.sax.SAXException If a filter further down the chain raises an exception.
     * @see org.xml.sax.ext.LexicalHandler#startDTD(String, String, String)
     */
    public void startDTD(String name, String publicId, String systemId)
            throws SAXException {
        if (lexicalHandler != null) {
            lexicalHandler.startDTD(name, publicId, systemId);
        }
    }

    /**
     * Sends end of DTD.
     *
     * @throws org.xml.sax.SAXException If a filter further down the chain raises an exception.
     * @see org.xml.sax.ext.LexicalHandler#endDTD
     */
    public void endDTD()
            throws SAXException {
        if (lexicalHandler != null) {
            lexicalHandler.endDTD();
        }
    }

   /**
    * Sends start of entity.
    *
    * @param name The name of the entity.  If it is a parameter
    *        entity, the name will begin with '%', and if it is the
    *        external DTD subset, it will be "[dtd]".
    * @exception org.xml.sax.SAXException If a filter
    *            further down the chain raises an exception.
    * @see org.xml.sax.ext.LexicalHandler#startEntity(String)
    */
    public void startEntity(String name)
            throws SAXException {
        if (lexicalHandler != null) {
            lexicalHandler.startEntity(name);
        }
    }

    /**
     * Sends end of entity.
     *
     * @param name The name of the entity that is ending.
     * @exception org.xml.sax.SAXException If a filter
     *            further down the chain raises an exception.
     * @see org.xml.sax.ext.LexicalHandler#endEntity(String)
     */
    public void endEntity(String name)
            throws SAXException {
        if (lexicalHandler != null) {
            lexicalHandler.endEntity(name);
        }
    }

    /**
     * Sends start of CDATA.
     *
     * @exception org.xml.sax.SAXException If a filter
     *            further down the chain raises an exception.
     * @see org.xml.sax.ext.LexicalHandler#startCDATA
     */
    public void startCDATA()
            throws SAXException {
        if (lexicalHandler != null) {
            lexicalHandler.startCDATA();
        }
    }

   /**
    * Sends end of CDATA.
    *
    * @exception org.xml.sax.SAXException If a filter
    *            further down the chain raises an exception.
    * @see org.xml.sax.ext.LexicalHandler#endCDATA
    */
    public void endCDATA()
            throws SAXException {
        if (lexicalHandler != null) {
            lexicalHandler.endCDATA();
        }
    }

    /**
     * Sends comment.
     *
     * @param ch An array holding the characters in the comment.
     * @param start The starting position in the array.
     * @param length The number of characters to use from the array.
     * @exception org.xml.sax.SAXException If a filter
     *            further down the chain raises an exception.
     * @see org.xml.sax.ext.LexicalHandler#comment(char[], int, int)
     */
    public void comment(char[] ch, int start, int length)
            throws SAXException {
        if (lexicalHandler != null) {
            lexicalHandler.comment(ch, start, length);
        }
    }

}
