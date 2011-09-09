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

package org.cdmckay.coffeedom.input;

import org.cdmckay.coffeedom.*;
import org.xml.sax.*;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Builds a CoffeeDOM document from files, streams, readers, URLs, or a SAX {@link org.xml.sax.InputSource} instance using a
 * SAX parser. The builder uses a third-party SAX parser (chosen by JAXP by default, or you can choose manually) to
 * handle the parsing duties and simply listens to the SAX events to construct a document. Details which SAX does not
 * provide, such as whitespace outside the root element, are not represented in the CoffeeDOM document. Information about SAX
 * can be found at <a href="http://www.saxproject.org">http://www.saxproject.org</a>.
 * <p/>
 * Known issues: Relative paths for a {@link DocType} or {@link EntityRef} may be converted by the SAX parser into
 * absolute paths.
 *
 * @author Jason Hunter
 * @author Brett McLaughlin
 * @author Dan Schaffer
 * @author Philip Nelson
 * @author Alex Rosen
 */
public class SAXBuilder {

    /**
     * Default parser class to use. This is used when no other parser is given and JAXP isn't available.
     */
    private static final String DEFAULT_SAX_DRIVER = "org.apache.xerces.parsers.SAXParser";

    /**
     * Whether validation should occur
     */
    private boolean validate;

    /**
     * Whether expansion of entities should occur
     */
    private boolean expand = true;

    /**
     * Adapter class to use
     */
    private String saxDriverClassName;

    /**
     * ErrorHandler class to use
     */
    private ErrorHandler saxErrorHandler = null;

    /**
     * EntityResolver class to use
     */
    private EntityResolver saxEntityResolver = null;

    /**
     * DTDHandler class to use
     */
    private DTDHandler saxDTDHandler = null;

    /**
     * XMLFilter instance to use
     */
    private XMLFilter saxXMLFilter = null;

    /**
     * The factory for creating new CoffeeDOM objects
     */
    private CoffeeDOMFactory factory = new DefaultCoffeeDOMFactory();

    /**
     * Whether to ignore ignorable whitespace
     */
    private boolean ignoringWhite = false;

    /**
     * Whether to ignore all whitespace content
     */
    private boolean ignoringBoundaryWhite = false;

    /**
     * User-specified features to be set on the SAX parser
     */
    private Map<String, Boolean> features = new HashMap<String, Boolean>(5);

    /**
     * User-specified properties to be set on the SAX parser
     */
    private Map<String, Object> properties = new HashMap<String, Object>(5);

    /**
     * Whether to use fast parser reconfiguration
     */
    private boolean fastReconfigure = false;

    /**
     * Whether to try lexical reporting in fast parser reconfiguration
     */
    private boolean skipNextLexicalReportingConfig = false;

    /**
     * Whether to to try entity expansion in fast parser reconfiguration
     */
    private boolean skipNextEntityExpandConfig = false;

    /**
     * Whether parser reuse is allowed. <p>Default: <code>true</code></p>
     */
    private boolean reuseParser = true;

    /**
     * The current SAX parser, if parser reuse has been activated.
     */
    private XMLReader saxParser = null;

    /**
     * Creates a new SAXBuilder which will attempt to first locate a parser via JAXP, then will try to use a set of
     * default SAX Drivers. The underlying parser will not validate.
     */
    public SAXBuilder() {
        this(false);
    }

    /**
     * Creates a new SAXBuilder which will attempt to first locate a parser via JAXP, then will try to use a set of
     * default SAX Drivers. The underlying parser will validate or not according to the given parameter.
     *
     * @param validate <code>boolean</code> indicating if validation should occur.
     */
    public SAXBuilder(boolean validate) {
        this.validate = validate;
    }

    /**
     * Creates a new SAXBuilder using the specified SAX parser. The underlying parser will not validate.
     *
     * @param saxDriverClassName <code>String</code> name of SAX Driver to use for parsing.
     */
    public SAXBuilder(String saxDriverClassName) {
        this(saxDriverClassName, false);
    }

    /**
     * Creates a new SAXBuilder using the specified SAX parser. The underlying parser will validate or not according to
     * the given parameter.
     *
     * @param saxDriverClassName <code>String</code> name of SAX Driver to use for parsing.
     * @param validate           <code>boolean</code> indicating if validation should occur.
     */
    public SAXBuilder(String saxDriverClassName, boolean validate) {
        this.saxDriverClassName = saxDriverClassName;
        this.validate = validate;
    }

    /**
     * Returns the driver class assigned in the constructor, or null if none.
     *
     * @return the driver class assigned in the constructor
     */
    public String getDriverClassName() {
        return saxDriverClassName;
    }

    /**
     * Returns the current {@link org.cdmckay.coffeedom.CoffeeDOMFactory} in use.
     *
     * @return the factory in use
     */
    public CoffeeDOMFactory getFactory() {
        return factory;
    }

    /**
     * This sets a custom CoffeeDOMFactory for the builder.  Use this to build the tree with your own subclasses of the CoffeeDOM
     * classes.
     *
     * @param factory <code>CoffeeDOMFactory</code> to use
     */
    public void setFactory(CoffeeDOMFactory factory) {
        this.factory = factory;
    }

    /**
     * Returns whether validation is to be performed during the build.
     *
     * @return whether validation is to be performed during the build
     */
    public boolean getValidation() {
        return validate;
    }

    /**
     * This sets validation for the builder.
     *
     * @param validate <code>boolean</code> indicating whether validation should occur.
     */
    public void setValidation(boolean validate) {
        this.validate = validate;
    }

    /**
     * Returns the {@link ErrorHandler} assigned, or null if none.
     *
     * @return the ErrorHandler assigned, or null if none
     */
    public ErrorHandler getErrorHandler() {
        return saxErrorHandler;
    }

    /**
     * This sets custom ErrorHandler for the <code>Builder</code>.
     *
     * @param errorHandler <code>ErrorHandler</code>
     */
    public void setErrorHandler(ErrorHandler errorHandler) {
        saxErrorHandler = errorHandler;
    }

    /**
     * Returns the {@link EntityResolver} assigned, or null if none.
     *
     * @return the EntityResolver assigned
     */
    public EntityResolver getEntityResolver() {
        return saxEntityResolver;
    }

    /**
     * This sets custom EntityResolver for the <code>Builder</code>.
     *
     * @param entityResolver <code>EntityResolver</code>
     */
    public void setEntityResolver(EntityResolver entityResolver) {
        saxEntityResolver = entityResolver;
    }

    /**
     * Returns the {@link DTDHandler} assigned, or null if none.
     *
     * @return the DTDHandler assigned
     */
    public DTDHandler getDTDHandler() {
        return saxDTDHandler;
    }

    /**
     * This sets custom DTDHandler for the <code>Builder</code>.
     *
     * @param dtdHandler <code>DTDHandler</code>
     */
    public void setDTDHandler(DTDHandler dtdHandler) {
        saxDTDHandler = dtdHandler;
    }

    /**
     * Returns the {@link XMLFilter} used during parsing, or null if none.
     *
     * @return the XMLFilter used during parsing
     */
    public XMLFilter getXMLFilter() {
        return saxXMLFilter;
    }

    /**
     * This sets a custom {@link org.xml.sax.XMLFilter} for the builder.
     *
     * @param xmlFilter the filter to use
     */
    public void setXMLFilter(XMLFilter xmlFilter) {
        saxXMLFilter = xmlFilter;
    }

    /**
     * Returns whether element content whitespace is to be ignored during the build.
     *
     * @return whether element content whitespace is to be ignored during the build
     */
    public boolean getIgnoringElementContentWhitespace() {
        return ignoringWhite;
    }

    /**
     * Specifies whether or not the parser should elminate whitespace in element content (sometimes known as "ignorable
     * whitespace") when building the document.  Only whitespace which is contained within element content that has an
     * element only content model will be eliminated (see XML Rec 3.2.1).  For this setting to take effect requires that
     * validation be turned on.  The default value of this setting is <code>false</code>.
     *
     * @param ignoringWhite Whether to ignore ignorable whitespace
     */
    public void setIgnoringElementContentWhitespace(boolean ignoringWhite) {
        this.ignoringWhite = ignoringWhite;
    }

    /**
     * Returns whether or not the parser will elminate element content containing only whitespace.
     *
     * @return <code>boolean</code> - whether only whitespace content will be ignored during build.
     * @see #setIgnoringBoundaryWhitespace(boolean)
     */
    public boolean getIgnoringBoundaryWhitespace() {
        return ignoringBoundaryWhite;
    }

    /**
     * Specifies whether or not the parser should elminate boundary whitespace, a term that indicates whitespace-only
     * text between element tags.  This feature is a lot like {@link #setIgnoringElementContentWhitespace(boolean)} but
     * this feature is more aggressive and doesn't require validation be turned on.  The {@link
     * #setIgnoringElementContentWhitespace(boolean)} call impacts the SAX parse process while this method impacts the
     * CoffeeDOM build process, so it can be beneficial to turn both on for efficiency. For implementation efficiency, this
     * method actually removes all whitespace-only text() nodes.  That can, in some cases (like between an element tag
     * and a comment), include whitespace that isn't just boundary whitespace.  The default is <code>false</code>.
     *
     * @param ignoringBoundaryWhite Whether to ignore whitespace-only text noes
     */
    public void setIgnoringBoundaryWhitespace(boolean ignoringBoundaryWhite) {
        this.ignoringBoundaryWhite = ignoringBoundaryWhite;
    }

    /**
     * Returns whether the contained SAX parser instance is reused across multiple parses.  The default is true.
     *
     * @return whether the contained SAX parser instance is reused across multiple parses
     */
    public boolean getReuseParser() {
        return reuseParser;
    }

    /**
     * Specifies whether this builder shall reuse the same SAX parser when performing subsequent parses or allocate a
     * new parser for each parse.  The default value of this setting is <code>true</code> (parser reuse). <p>
     * <strong>Note</strong>: As SAX parser instances are not thread safe, the parser reuse feature should not be used
     * with SAXBuilder instances shared among threads.</p>
     *
     * @param reuseParser Whether to reuse the SAX parser.
     */
    public void setReuseParser(boolean reuseParser) {
        this.reuseParser = reuseParser;
        this.saxParser = null;
    }

    /**
     * Specifies whether this builder will do fast reconfiguration of the underlying SAX parser when reuseParser is
     * true. This improves performance in cases where SAXBuilders are reused and lots of small documents are frequently
     * parsed. This avoids attempting to set features on the SAX parser each time build() is called which result in
     * SaxNotRecognizedExceptions. This should ONLY be set for builders where this specific case is an issue. The
     * default value of this setting is <code>false</code> (no fast reconfiguration). If reuseParser is false, calling
     * this has no effect.
     *
     * @param fastReconfigure Whether to do a fast reconfiguration of the parser
     */
    public void setFastReconfigure(boolean fastReconfigure) {
        if (this.reuseParser) {
            this.fastReconfigure = fastReconfigure;
        }
    }

    /**
     * This sets a feature on the SAX parser. See the SAX documentation for . more information. </p> <p> NOTE:
     * SAXBuilder requires that some particular features of the SAX parser be set up in certain ways for it to work
     * properly. The list of such features may change in the future. Therefore, the use of this method may cause parsing
     * to break, and even if it doesn't break anything today it might break parsing in a future CoffeeDOM version, because
     * what CoffeeDOM parsers require may change over time. Use with caution. </p>
     *
     * @param name  The feature name, which is a fully-qualified URI.
     * @param value The requested state of the feature (true or false).
     */
    public void setFeature(String name, boolean value) {
        // Save the specified feature for later.
        features.put(name, value);
    }

    /**
     * This sets a property on the SAX parser. See the SAX documentation for more information. <p> NOTE: SAXBuilder
     * requires that some particular properties of the SAX parser be set up in certain ways for it to work properly. The
     * list of such properties may change in the future. Therefore, the use of this method may cause parsing to break,
     * and even if it doesn't break anything today it might break parsing in a future CoffeeDOM version, because what CoffeeDOM
     * parsers require may change over time. Use with caution. </p>
     *
     * @param name  The property name, which is a fully-qualified URI.
     * @param value The requested value for the property.
     */
    public void setProperty(String name, Object value) {
        // Save the specified property for later.
        properties.put(name, value);
    }

    /**
     * This builds a document from the supplied input source.
     *
     * @param in <code>InputSource</code> to read from
     * @return <code>Document</code> resultant Document object
     * @throws CoffeeDOMException when errors occur in parsing
     * @throws IOException when an I/O error prevents a document
     *         from being fully parsed
     */
    public Document build(InputSource in) throws CoffeeDOMException, IOException {
        SAXHandler contentHandler = null;

        try {
            // Create and configure the content handler.
            contentHandler = createContentHandler();
            configureContentHandler(contentHandler);

            XMLReader parser = this.saxParser;
            if (parser == null) {
                // Create and configure the parser.
                parser = createParser();

                // Install optional filter
                if (saxXMLFilter != null) {
                    // Connect filter chain to parser
                    XMLFilter root = saxXMLFilter;
                    while (root.getParent() instanceof XMLFilter) {
                        root = (XMLFilter) root.getParent();
                    }
                    root.setParent(parser);

                    // Read from filter
                    parser = saxXMLFilter;
                }

                // Configure parser
                configureParser(parser, contentHandler);

                if (reuseParser) {
                    this.saxParser = parser;
                }
            } else {
                // Reset content handler as SAXHandler instances cannot
                // be reused
                configureParser(parser, contentHandler);
            }

            // Parse the document.
            parser.parse(in);

            return contentHandler.getDocument();
        } catch (SAXParseException e) {
            Document doc = contentHandler.getDocument();
            if (!doc.hasRootElement()) {
                doc = null;
            }

            String systemId = e.getSystemId();
            if (systemId != null) {
                throw new CoffeeDOMParseException("Error on line " + e.getLineNumber() + " of document " + systemId, e, doc);
            } else {
                throw new CoffeeDOMParseException("Error on line " + e.getLineNumber(), e, doc);
            }
        } catch (SAXException e) {
            throw new CoffeeDOMParseException("Error in building: " + e.getMessage(), e, contentHandler.getDocument());
        }
    }

    /**
     * This creates the SAXHandler that will be used to build the Document.
     *
     * @return <code>SAXHandler</code> - resultant SAXHandler object.
     */
    protected SAXHandler createContentHandler() {
        return new SAXHandler(factory);
    }

    /**
     * This configures the SAXHandler that will be used to build the Document. <p> The default implementation simply
     * passes through some configuration settings that were set on the SAXBuilder: setExpandEntities() and
     * setIgnoringElementContentWhitespace(). </p>
     *
     * @param contentHandler The SAXHandler to configure
     */
    protected void configureContentHandler(SAXHandler contentHandler) {
        // Setup pass through behavior
        contentHandler.setExpandEntities(expand);
        contentHandler.setIgnoringElementContentWhitespace(ignoringWhite);
        contentHandler.setIgnoringBoundaryWhitespace(ignoringBoundaryWhite);
    }

    /**
     * This creates the XMLReader to be used for reading the XML document. <p> The default behavior is to (1) use the
     * saxDriverClassName, if it has been set, (2) try to obtain a parser from JAXP, if it is available, and (3) if all
     * else fails, use a hard-coded default parser (currently the Xerces parser). Subclasses may override this method to
     * determine the parser to use in a different way. </p>
     *
     * @return resultant XMLReader object.
     * @throws CoffeeDOMException if there is trouble loading the SAX driver.
     */
    protected XMLReader createParser() throws CoffeeDOMException {
        XMLReader parser = null;
        if (saxDriverClassName != null) {
            // The user knows that they want to use a particular class
            try {
                parser = XMLReaderFactory.createXMLReader(saxDriverClassName);

                // Configure parser
                setFeaturesAndProperties(parser, true);
            } catch (SAXException e) {
                throw new CoffeeDOMException("Could not load " + saxDriverClassName, e);
            }
        } else {
            // Try using JAXP...
            // Note we need JAXP 1.1, and if JAXP 1.0 is all that's
            // available then the getXMLReader call fails and we skip
            // to the hard coded default parser
            try {
                // Get factory class and method.
                Class<?> factoryClass = Class.forName("JAXPParserFactory");
                Method createParser = factoryClass.getMethod("createParser", boolean.class, Map.class, Map.class);

                // Create SAX parser.
                parser = (XMLReader) createParser.invoke(null, validate, features, properties);

                // Configure parser.
                setFeaturesAndProperties(parser, false);
            } catch (NoClassDefFoundError e) {
                // The class loader failed to resolve the dependencies
                // of JAXPParserFactory. This probably means
                // that no JAXP parser is present in its class path.
                // => Ignore and try allocating default SAX parser instance.
            } catch (Exception e) {
                // Ignore and try allocating default SAX parser instance.
            }
        }

        // Check to see if we got a parser yet, if not, try to use a
        // hard coded default
        if (parser == null) {
            try {
                parser = XMLReaderFactory.createXMLReader(DEFAULT_SAX_DRIVER);
                saxDriverClassName = parser.getClass().getName();

                // Configure parser
                setFeaturesAndProperties(parser, true);
            } catch (SAXException e) {
                throw new CoffeeDOMException("Could not load default SAX parser: " + DEFAULT_SAX_DRIVER, e);
            }
        }

        return parser;
    }

    /**
     * This configures the XMLReader to be used for reading the XML document. <p> The default implementation sets
     * various options on the given XMLReader, such as validation, DTD resolution, entity handlers, etc., according to
     * the options that were set (e.g. via <code>setEntityResolver</code>) and set various SAX properties and features
     * that are required for CoffeeDOM internals. These features may change in future releases, so change this behavior at
     * your own risk. </p>
     *
     * @param parser The parser.
     * @param contentHandler The SAX content handler.
     * @throws CoffeeDOMException when errors occur in parsing.
     */
    protected void configureParser(XMLReader parser, SAXHandler contentHandler) throws CoffeeDOMException {

        // Setup SAX handlers.

        parser.setContentHandler(contentHandler);

        if (saxEntityResolver != null) {
            parser.setEntityResolver(saxEntityResolver);
        }

        if (saxDTDHandler != null) {
            parser.setDTDHandler(saxDTDHandler);
        } else {
            parser.setDTDHandler(contentHandler);
        }

        if (saxErrorHandler != null) {
            parser.setErrorHandler(saxErrorHandler);
        } else {
            parser.setErrorHandler(new BuilderErrorHandler());
        }

        // If fastReconfigure is enabled and we failed in the previous attempt
        // in configuring lexical reporting, then we skip this step.  This
        // saves the work of repeated exception handling on each parse.
        if (!skipNextLexicalReportingConfig) {
            boolean success = false;

            try {
                parser.setProperty("http://xml.org/sax/handlers/LexicalHandler", contentHandler);
                success = true;
            } catch (SAXNotSupportedException e) {
                // No lexical reporting available
            } catch (SAXNotRecognizedException e) {
                // No lexical reporting available
            }

            // Some parsers use alternate property for lexical handling (grr...)
            if (!success) {
                try {
                    parser.setProperty("http://xml.org/sax/properties/lexical-handler", contentHandler);
                    success = true;
                } catch (SAXNotSupportedException e) {
                    // No lexical reporting available
                } catch (SAXNotRecognizedException e) {
                    // No lexical reporting available
                }
            }

            // If unable to configure this property and fastReconfigure is
            // enabled, then setup to avoid this code path entirely next time.
            if (!success && fastReconfigure) {
                skipNextLexicalReportingConfig = true;
            }
        }

        // If fastReconfigure is enabled and we failed in the previous attempt
        // in configuring entity expansion, then skip this step.  This
        // saves the work of repeated exception handling on each parse.
        if (!skipNextEntityExpandConfig) {
            boolean success = false;

            // Try setting the DeclHandler if entity expansion is off
            if (!expand) {
                try {
                    parser.setProperty("http://xml.org/sax/properties/declaration-handler", contentHandler);
                    success = true;
                } catch (SAXNotSupportedException e) {
                    // No lexical reporting available
                } catch (SAXNotRecognizedException e) {
                    // No lexical reporting available
                }
            }

            /* If unable to configure this property and fastReconfigure is
             * enabled, then setup to avoid this code path entirely next time.
             */
            if (!success && fastReconfigure) {
                skipNextEntityExpandConfig = true;
            }
        }
    }

    private void setFeaturesAndProperties(XMLReader parser, boolean coreFeatures) throws CoffeeDOMException {
        // Set any user-specified features on the parser.
        Iterator<String> it = features.keySet().iterator();
        while (it.hasNext()) {
            String name = (String) it.next();
            Boolean value = features.get(name);
            internalSetFeature(parser, name, value, name);
        }

        // Set any user-specified properties on the parser.
        it = properties.keySet().iterator();
        while (it.hasNext()) {
            String name = it.next();
            internalSetProperty(parser, name, properties.get(name), name);
        }

        if (coreFeatures) {
            // Set validation.
            try {
                internalSetFeature(parser, "http://xml.org/sax/features/validation", validate, "Validation");
            } catch (CoffeeDOMException e) {
                // If validation is not supported, and the user is requesting
                // that we don't validate, that's fine - don't throw an
                // exception.
                if (validate) {
                    throw e;
                }
            }

            // Setup some namespace features.
            internalSetFeature(parser, "http://xml.org/sax/features/namespaces", true, "Namespaces");
            internalSetFeature(parser, "http://xml.org/sax/features/namespace-prefixes", true, "Namespace prefixes");
        }

        // Set entity expansion
        // Note SAXHandler can work regardless of how this is set, but when
        // entity expansion it's worth it to try to tell the parser not to
        // even bother with external general entities.
        // Apparently no parsers yet support this feature.
        // XXX It might make sense to setEntityResolver() with a resolver
        // that simply ignores external general entities
        try {
            if (parser.getFeature("http://xml.org/sax/features/external-general-entities") != expand) {
                parser.setFeature("http://xml.org/sax/features/external-general-entities", expand);
            }
        } catch (SAXNotRecognizedException e) {
            /* Ignore... */
        } catch (SAXNotSupportedException e) {
            /* Ignore... */
        }
    }

    /**
     * Tries to set a feature on the parser. If the feature cannot be set, throws a CoffeeDOMException describing the
     * problem.
     */
    private void internalSetFeature(XMLReader parser, String feature, boolean value, String displayName)
            throws CoffeeDOMException {
        try {
            parser.setFeature(feature, value);
        } catch (SAXNotSupportedException e) {
            throw new CoffeeDOMException(
                    displayName + " feature not supported for SAX driver " + parser.getClass().getName());
        } catch (SAXNotRecognizedException e) {
            throw new CoffeeDOMException(
                    displayName + " feature not recognized for SAX driver " + parser.getClass().getName());
        }
    }

    /**
     * <p> Tries to set a property on the parser. If the property cannot be set, throws a CoffeeDOMException describing the
     * problem. </p>
     *
     * @param parser
     * @param property
     * @param value
     * @param displayName
     * @throws CoffeeDOMException if a property is not supported for the SAX driver
     */
    private void internalSetProperty(XMLReader parser, String property, Object value, String displayName)
            throws CoffeeDOMException {
        try {
            parser.setProperty(property, value);
        } catch (SAXNotSupportedException e) {
            throw new CoffeeDOMException(
                    displayName + " property not supported for SAX driver " + parser.getClass().getName());
        } catch (SAXNotRecognizedException e) {
            throw new CoffeeDOMException(
                    displayName + " property not recognized for SAX driver " + parser.getClass().getName());
        }
    }

    /**
     * <p> This builds a document from the supplied input stream. </p>
     *
     * @param in <code>InputStream</code> to read from
     * @return <code>Document</code> resultant Document object
     * @throws CoffeeDOMException when errors occur in parsing
     * @throws IOException when an I/O error prevents a document
     *         from being fully parsed.
     */
    public Document build(InputStream in) throws CoffeeDOMException, IOException {
        return build(new InputSource(in));
    }

    /**
     * <p> This builds a document from the supplied filename. </p>
     *
     * @param file <code>File</code> to read from
     * @return <code>Document</code> resultant Document object
     * @throws CoffeeDOMException when errors occur in parsing
     * @throws IOException when an I/O error prevents a document
     *         from being fully parsed
     */
    public Document build(File file) throws CoffeeDOMException, IOException {
        try {
            URL url = fileToURL(file);
            return build(url);
        } catch (MalformedURLException e) {
            throw new CoffeeDOMException("Error in building", e);
        }
    }

    /**
     * <p> This builds a document from the supplied URL. </p>
     *
     * @param url <code>URL</code> to read from.
     * @return <code>Document</code> - resultant Document object.
     * @throws CoffeeDOMException when errors occur in parsing
     * @throws IOException when an I/O error prevents a document
     *         from being fully parsed.
     */
    public Document build(URL url) throws CoffeeDOMException, IOException {
        String systemId = url.toExternalForm();
        return build(new InputSource(systemId));
    }

    /**
     * <p> This builds a document from the supplied input stream. </p>
     *
     * @param in <code>InputStream</code> to read from.
     * @param systemId base for resolving relative URIs
     * @return <code>Document</code> resultant Document object
     * @throws CoffeeDOMException when errors occur in parsing
     */
    public Document build(InputStream in, String systemId) throws CoffeeDOMException, IOException {
        InputSource src = new InputSource(in);
        src.setSystemId(systemId);
        return build(src);
    }

    /**
     * <p> This builds a document from the supplied Reader.  It's the programmer's responsibility to make sure the
     * reader matches the encoding of the file.  It's often easier and safer to use an InputStream rather than a Reader,
     * and to let the parser auto-detect the encoding from the XML declaration. </p>
     *
     * @param characterStream <code>Reader</code> to read from
     * @return <code>Document</code> resultant Document object
     * @throws CoffeeDOMException when errors occur in parsing
     * @throws IOException when an I/O error prevents a document
     *         from being fully parsed.
     */
    public Document build(Reader characterStream) throws CoffeeDOMException, IOException {
        return build(new InputSource(characterStream));
    }

    /**
     * <p> This builds a document from the supplied Reader.  It's the programmer's responsibility to make sure the
     * reader matches the encoding of the file.  It's often easier and safer to use an InputStream rather than a Reader,
     * and to let the parser auto-detect the encoding from the XML declaration. </p>
     *
     * @param characterStream <code>Reader</code> to read from.
     * @param systemId        base for resolving relative URIs
     * @return <code>Document</code> resultant Document object
     * @throws CoffeeDOMException when errors occur in parsing
     * @throws IOException when an I/O error prevents a document
     *         from being fully parsed.
     */
    public Document build(Reader characterStream, String systemId) throws CoffeeDOMException, IOException {
        InputSource src = new InputSource(characterStream);
        src.setSystemId(systemId);
        return build(src);
    }

    /**
     * <p> This builds a document from the supplied URI. </p>
     *
     * @param systemId URI for the input
     * @return <code>Document</code> resultant Document object
     * @throws CoffeeDOMException when errors occur in parsing
     * @throws IOException when an I/O error prevents a document
     *         from being fully parsed.
     */
    public Document build(String systemId) throws CoffeeDOMException, IOException {
        return build(new InputSource(systemId));
    }

    /**
     * Custom File.toUrl() implementation to handle special chars in file names
     *
     * @param file file object whose path will be converted
     * @return URL form of the file, with special characters handled
     * @throws MalformedURLException if there's a problem constructing a URL
     */
    private static URL fileToURL(File file) throws MalformedURLException {
        StringBuilder buffer = new StringBuilder();
        String path = file.getAbsolutePath();

        // Convert non-URL style file separators
        if (File.separatorChar != '/') {
            path = path.replace(File.separatorChar, '/');
        }

        // Make sure it starts at root
        if (!path.startsWith("/")) {
            buffer.append('/');
        }

        // Copy, converting URL special characters as we go
        int len = path.length();
        for (int i = 0; i < len; i++) {
            char c = path.charAt(i);
            if (c == ' ') {
                buffer.append("%20");
            } else if (c == '#') {
                buffer.append("%23");
            } else if (c == '%') {
                buffer.append("%25");
            } else if (c == '&') {
                buffer.append("%26");
            } else if (c == ';') {
                buffer.append("%3B");
            } else if (c == '<') {
                buffer.append("%3C");
            } else if (c == '=') {
                buffer.append("%3D");
            } else if (c == '>') {
                buffer.append("%3E");
            } else if (c == '?') {
                buffer.append("%3F");
            } else if (c == '~') {
                buffer.append("%7E");
            } else {
                buffer.append(c);
            }
        }

        // Make sure directories end with slash
        if (!path.endsWith("/") && file.isDirectory()) {
            buffer.append('/');
        }

        // Return URL
        return new URL("file", "", buffer.toString());
    }

    /**
     * Returns whether or not entities are being expanded into normal text content.
     *
     * @return whether entities are being expanded
     */
    public boolean getExpandEntities() {
        return expand;
    }

    /**
     * <p> This sets whether or not to expand entities for the builder. A true means to expand entities as normal
     * content.  A false means to leave entities unexpanded as <code>EntityRef</code> objects.  The default is true.
     * </p> <p> When this setting is false, the internal DTD subset is retained; when this setting is true, the internal
     * DTD subset is not retained. </p> <p> Note that Xerces (at least up to 1.4.4) has a bug where entities in
     * attribute values will be misreported if this flag is turned off, resulting in entities to appear within element
     * content.  When turning entity expansion off either avoid entities in attribute values, or use another parser like
     * Crimson. http://nagoya.apache.org/bugzilla/show_bug.cgi?id=6111 </p>
     *
     * @param expand <code>boolean</code> indicating whether entity expansion should occur.
     */
    public void setExpandEntities(boolean expand) {
        this.expand = expand;
    }
}
