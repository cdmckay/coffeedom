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

package org.cdmckay.coffeedom.filter;

import org.cdmckay.coffeedom.*;

import java.util.EnumSet;

/**
 * A general purpose Filter able to represent all legal CoffeeDOM objects or a specific subset. Filtering is accomplished by
 * way of a filtering enum set in which each enum constant represents whether a CoffeeDOM object is visible or not. For
 * example to view all Text and CDATA nodes in the content of element x.
 * <pre><code>
 *      Filter filter = new ContentFilter(EnumSet.of(ContentType.Text, ContentType.CDATA));
 *      List&lt;Content&gt; content = x.getContents(filter);
 * </code></pre>
 * <p/>
 * For those who don't like enum sets, set methods are provided as an alternative.  For example to allow everything
 * except Comment nodes.
 * <pre><code>
 *      Filter filter =  new ContentFilter();
 *      filter.setCommentVisible(false);
 *      List&lt;Content&gt; content = x.getContents(filter);
 * </code></pre>
 * <p/>
 * The default is to allow all valid CoffeeDOM objects.
 *
 * @author Bradley S. Huffman
 * @author Cameron McKay
 */
public class ContentFilter
        extends AbstractFilter {

    public enum ContentType {

        /**
         * Mask for CoffeeDOM {@link Element} objects
         */
        ELEMENT,

        /**
         * Mask for CoffeeDOM {@link CDATA} objects
         */
        CDATA,

        /**
         * Mask for CoffeeDOM {@link Text} objects
         */
        TEXT,

        /**
         * Mask for CoffeeDOM {@link Comment} objects
         */
        COMMENT,

        /**
         * Mask for CoffeeDOM {@link ProcessingInstruction} objects
         */
        PI,

        /**
         * Mask for CoffeeDOM {@link EntityRef} objects
         */
        ENTITYREF,

        /**
         * Mask for CoffeeDOM {@link Document} object
         */
        DOCUMENT,

        /**
         * Mask for CoffeeDOM {@link DocType} object
         */
        DOCTYPE

    }

    /**
     * The CoffeeDOM filter set, defaults to all showing.
     */
    private EnumSet<ContentType> filterSet;

    /**
     * Default constructor that allows any legal CoffeeDOM objects.
     */
    public ContentFilter() {
        filterSet = EnumSet.allOf(ContentType.class);
    }

    /**
     * Set whether all CoffeeDOM objects are visible or not.
     *
     * @param allVisible <code>true</code> all CoffeeDOM objects are visible, <code>false</code> all CoffeeDOM objects are
     *                   hidden.
     */
    public ContentFilter(boolean allVisible) {
        filterSet = EnumSet.noneOf(ContentType.class);
    }

    /**
     * Filter out CoffeeDOM objects according to a filtering set.
     *
     * @param set Set of CoffeeDOM content types to allow.
     */
    public ContentFilter(EnumSet<ContentType> set) {
        setFilterSet(set);
    }

    /**
     * Return current filtering set.
     *
     * @return The current filtering set.
     */
    public EnumSet<ContentType> getFilterSet() {
        return filterSet.clone();
    }

    /**
     * Set filtering set.
     *
     * @param set the new filtering set
     */
    public void setFilterSet(EnumSet<ContentType> set) {
        filterSet = set.clone();
    }

    /**
     * Reset this filter to allow all legal CoffeeDOM objects.
     */
    public void resetFilterSet() {
        filterSet = EnumSet.allOf(ContentType.class);
    }

    /**
     * Set filter to match only CoffeeDOM objects that are legal document content.
     */
    public void filterDocumentContent() {
        filterSet = EnumSet.of(ContentType.ELEMENT, ContentType.COMMENT, ContentType.PI, ContentType.DOCTYPE);
    }

    /**
     * Set filter to match only CoffeeDOM objects that are legal element content.
     */
    public void filterElementContent() {
        filterSet = EnumSet.of(ContentType.ELEMENT, ContentType.CDATA, ContentType.TEXT, ContentType.COMMENT,
                ContentType.PI, ContentType.ENTITYREF);
    }

    /**
     * Check to see if the object matches according to the filter mask.
     *
     * @param object The object to verify.
     * @return <code>true</code> if the objected matched a predfined set of rules.
     */
    public boolean matches(Object object) {
        if (object instanceof Element) {
            return filterSet.contains(ContentType.ELEMENT);
        } else if (object instanceof CDATA) {  // Must come before Text check.
            return filterSet.contains(ContentType.CDATA);
        } else if (object instanceof Text) {
            return filterSet.contains(ContentType.TEXT);
        } else if (object instanceof Comment) {
            return filterSet.contains(ContentType.COMMENT);
        } else if (object instanceof ProcessingInstruction) {
            return filterSet.contains(ContentType.PI);
        } else if (object instanceof EntityRef) {
            return filterSet.contains(ContentType.ENTITYREF);
        } else if (object instanceof Document) {
            return filterSet.contains(ContentType.DOCUMENT);
        } else if (object instanceof DocType) {
            return filterSet.contains(ContentType.DOCTYPE);
        }

        return false;
    }

    /**
     * Returns whether the two filters are equivalent (i&#46;e&#46; the matching mask values are identical).
     *
     * @param object the object to compare against
     * @return whether the two filters are equal
     */
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof ContentFilter)) {
            return false;
        }

        final ContentFilter filter = (ContentFilter) object;

        if (filterSet != filter.filterSet) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        return filterSet.hashCode();
    }
}
