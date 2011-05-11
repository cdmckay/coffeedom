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

package org.cdmckay.coffeedom;

import java.util.Map;

/**
 * An interface to be used by builders when constructing CoffeeDOM objects. The <code>DefaultCoffeeDOMFactory</code> creates the
 * standard top-level CoffeeDOM classes (Element, Document, Comment, etc). Another implementation of this factory could be
 * used to create custom classes.
 *
 * @author Ken Rune Holland
 * @author Phil Nelson
 * @author Bradley S. Huffman
 */
public interface CoffeeDOMFactory {

    // **** constructing Attributes ****

    /**
     * <p> This will create a new <code>Attribute</code> with the specified (local) name and value, and in the provided
     * <code>{@link org.cdmckay.coffeedom.Namespace}</code>. </p>
     *
     * @param name      name of <code>Attribute</code>.
     * @param value     value for new attribute.
     * @param namespace the attribute namespace.
     * @return
     */
    public Attribute attribute(String name, String value, Namespace namespace);

    /**
     * This will create a new <code>Attribute</code> with the specified (local) name, value, and type, and in the
     * provided <code>{@link org.cdmckay.coffeedom.Namespace}</code>.
     *
     * @param name      <code>String</code> name of <code>Attribute</code>.
     * @param value     <code>String</code> value for new attribute.
     * @param type      <code>AttributeType</code> type for new attribute.
     * @param namespace <code>Namespace</code> namespace for new attribute.
     * @return
     */
    public Attribute attribute(String name, String value, Attribute.AttributeType type, Namespace namespace);

    /**
     * This will create a new <code>Attribute</code> with the specified (local) name and value, and does not place the
     * attribute in a <code>{@link org.cdmckay.coffeedom.Namespace}</code>. <p> <b>Note</b>: This actually explicitly puts
     * the <code>Attribute</code> in the "empty" <code>Namespace</code> (<code>{@link
     * org.cdmckay.coffeedom.Namespace#NO_NAMESPACE}</code>). </p>
     *
     * @param name  <code>String</code> name of <code>Attribute</code>.
     * @param value <code>String</code> value for new attribute.
     * @return
     */
    public Attribute attribute(String name, String value);

    /**
     * This will create a new <code>Attribute</code> with the specified (local) name, value and type, and does not place
     * the attribute in a <code>{@link org.cdmckay.coffeedom.Namespace}</code>. <p> <b>Note</b>: This actually explicitly
     * puts the <code>Attribute</code> in the "empty" <code>Namespace</code> (<code>{@link
     * org.cdmckay.coffeedom.Namespace#NO_NAMESPACE}</code>). </p>
     *
     * @param name  <code>String</code> name of <code>Attribute</code>.
     * @param value <code>String</code> value for new attribute.
     * @param type  <code>AttributeType</code> type for new attribute.
     * @return
     */
    public Attribute attribute(String name, String value, Attribute.AttributeType type);

    // **** constructing CDATA ****

    /**
     * This creates the CDATA with the supplied text.
     *
     * @param str <code>String</code> content of CDATA.
     * @return
     */
    public CDATA cdata(String str);

    // **** constructing Text ****

    /**
     * This creates the Text with the supplied text.
     *
     * @param str <code>String</code> content of Text.
     * @return
     */
    public Text text(String str);

    // **** constructing Comment ****

    /**
     * This creates the comment with the supplied text.
     *
     * @param text <code>String</code> content of comment.
     * @return
     */
    public Comment comment(String text);

    // **** constructing DocType

    /**
     * This will create the <code>DocType</code> with the specified element name and a reference to an external DTD.
     *
     * @param elementName <code>String</code> name of element being constrained.
     * @param publicID    <code>String</code> public ID of referenced DTD
     * @param systemID    <code>String</code> system ID of referenced DTD
     * @return
     */
    public DocType docType(String elementName, String publicID, String systemID);

    /**
     * This will create the <code>DocType</code> with the specified element name and reference to an external DTD.
     *
     * @param elementName <code>String</code> name of element being constrained.
     * @param systemID    <code>String</code> system ID of referenced DTD
     * @return
     */
    public DocType docType(String elementName, String systemID);

    /**
     * This will create the <code>DocType</code> with the specified element name
     *
     * @param elementName <code>String</code> name of element being constrained.
     * @return
     */
    public DocType docType(String elementName);

    // **** constructing Document

    /**
     * This will create a new <code>Document</code>, with the supplied <code>{@link org.cdmckay.coffeedom.Element}</code> as
     * the root element and the supplied <code>{@link org.cdmckay.coffeedom.DocType}</code> declaration.
     *
     * @param rootElement <code>Element</code> for document root.
     * @param docType     <code>DocType</code> declaration.
     * @return
     */
    public Document document(Element rootElement, DocType docType);

    /**
     * This will create a new <code>Document</code>, with the supplied <code>{@link org.cdmckay.coffeedom.Element}</code> as
     * the root element and the supplied <code>{@link org.cdmckay.coffeedom.DocType}</code> declaration.
     *
     * @param rootElement <code>Element</code> for document root.
     * @param docType     <code>DocType</code> declaration.
     * @param baseURI     the URI from which this doucment was loaded.
     * @return
     */
    public Document document(Element rootElement, DocType docType, String baseURI);

    /**
     * This will create a new <code>Document</code>, with the supplied <code>{@link org.cdmckay.coffeedom.Element}</code> as
     * the root element, and no <code>{@link org.cdmckay.coffeedom.DocType}</code> declaration.
     *
     * @param rootElement <code>Element</code> for document root
     * @return
     */
    public Document document(Element rootElement);

    // **** constructing Elements ****

    /**
     * This will create a new <code>Element</code> with the supplied (local) name, and define the <code>{@link
     * org.cdmckay.coffeedom.Namespace}</code> to be used.
     *
     * @param name      <code>String</code> name of element.
     * @param namespace <code>Namespace</code> to put element in.
     * @return
     */
    public Element element(String name, Namespace namespace);

    /**
     * This will create an <code>Element</code> in no <code>{@link org.cdmckay.coffeedom.Namespace}</code>.
     *
     * @param name <code>String</code> name of element.
     * @return
     */
    public Element element(String name);

    /**
     * This will create a new <code>Element</code> with the supplied (local) name, and specifies the URI of the
     * <code>{@link org.cdmckay.coffeedom.Namespace}</code> the <code>Element</code> should be in, resulting it being
     * unprefixed (in the default namespace).
     *
     * @param name <code>String</code> name of element.
     * @param uri  <code>String</code> URI for <code>Namespace</code> element should be in.
     * @return
     */
    public Element element(String name, String uri);

    /**
     * This will create a new <code>Element</code> with the supplied (local) name, and specifies the prefix and URI of
     * the <code>{@link org.cdmckay.coffeedom.Namespace}</code> the <code>Element</code> should be in.
     *
     * @param name   <code>String</code> name of element.
     * @param prefix
     * @param uri    <code>String</code> URI for <code>Namespace</code> element should be in.
     */
    public Element element(String name, String prefix, String uri);

    // **** constructing ProcessingInstruction ****

    /**
     * This will create a new <code>ProcessingInstruction</code> with the specified target and data.
     *
     * @param target <code>String</code> target of PI.
     * @param data   <code>Map</code> data for PI, in name/value pairs
     * @return
     */
    public ProcessingInstruction processingInstruction(String target, Map<String, String> data);

    /**
     * This will create a new <code>ProcessingInstruction</code> with the specified target and data.
     *
     * @param target <code>String</code> target of PI.
     * @param data   <code>String</code> data for PI.
     * @return
     */
    public ProcessingInstruction processingInstruction(String target, String data);

    // **** constructing EntityRef ****

    /**
     * This will create a new <code>EntityRef</code> with the supplied name.
     *
     * @param name <code>String</code> name of element.
     * @return
     */
    public EntityRef entityRef(String name);

    /**
     * This will create a new <code>EntityRef</code> with the supplied name, public ID, and system ID.
     *
     * @param name     <code>String</code> name of element.
     * @param publicID <code>String</code> public ID of element.
     * @param systemID <code>String</code> system ID of element.
     * @return
     */
    public EntityRef entityRef(String name, String publicID, String systemID);

    /**
     * This will create a new <code>EntityRef</code> with the supplied name and system ID.
     *
     * @param name     <code>String</code> name of element.
     * @param systemID <code>String</code> system ID of element.
     * @return
     */
    public EntityRef entityRef(String name, String systemID);

    // =====================================================================
    // List manipulation
    // =====================================================================

    public void addContent(Parent parent, Content content);

    public void setAttribute(Element element, Attribute a);

    public void addNamespaceDeclaration(Element element, Namespace additional);
}
