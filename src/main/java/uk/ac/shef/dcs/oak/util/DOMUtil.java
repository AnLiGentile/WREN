/**
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * The Initial Developer of the Original Code is Sheffield University.
 * Portions created by Sheffield University are
 * Copyright &copy; 2005 Sheffield University (Web Intelligence Group)
 * All Rights Reserved.
 *
 * Contributor(s):
 *   Neil Ireson (N.Ireson@dcs.shef.ac.uk)
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either the GNU General Public License Version 2 or later (the "GPL"), or
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the MPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the MPL, the GPL or the LGPL.
 */

package uk.ac.shef.dcs.oak.util;

import com.gargoylesoftware.htmlunit.StringWebResponse;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HTMLParser;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.Attr;
import org.w3c.dom.NodeList;
import org.w3c.dom.Document;

import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ArrayList;
import java.util.regex.Pattern;


/**
 * Class DOMUtil
 * <p/>
 * Author: Neil Ireson (mailto:n.ireson@sheffield.ac.uk)
 * Creation Date: 10-May-2007
 * Version: 0.1
 *
 * Should use real world parse such as either jsoup or htmlunit.
 * see http://stackoverflow.com/questions/3152138/what-are-the-pros-and-cons-of-the-leading-java-html-parsers
 */
@SuppressWarnings({"ClassWithoutLogger"})
public class DOMUtil
{
    public static String INDENT = "  ";

    private static XPath xpath = null;


    public static Document parse(final byte[] html, String encoding)
            throws ParserConfigurationException, IOException
    {
        return parse(html, encoding, null);
    }

    public static Document parse(final byte[] html, String encoding, URL url)
            throws ParserConfigurationException, IOException
    {
        // todo untested code, I removed the mozillaparser dependency and used htmlunit instead
        StringWebResponse response = new StringWebResponse(new String(html), encoding, url);
        WebClient client = new WebClient();
        HtmlPage page = HTMLParser.parseHtml(response, client.getCurrentWindow());

//        response = new StringWebResponse(page.asXml(), encoding, url);
//        XmlPage xmlPage = new XmlPage(response, client.getCurrentWindow());
//        final Document document = xmlPage.getXmlDocument();

        page.normalizeDocument();
        return page;
    }

    public static XPath getXPath()
    {
        if (xpath == null)
        {
            final XPathFactory factory = XPathFactory.newInstance();
            xpath = factory.newXPath();
        }
        return xpath;
    }

    public static NodeList getNodes(final Node document, final String xPathString)
            throws XPathExpressionException
    {
        return getNodes(document, xPathString, getXPath());
    }

    public static NodeList getNodes(final Node document, final String xPathString, XPath xpath)
            throws XPathExpressionException
    {
        // Get the matching elements
        System.out.println("Searching for : " + xPathString);
        final XPathExpression expr = xpath.compile(xPathString);

        return (NodeList) expr.evaluate(document, XPathConstants.NODESET);
    }

    /**
     * Takes the list of nodes then gets the XPath to each htmlNode, then removes any duplicates (removing the latter one).
     *
     * @param nodeList The nodes to process
     * @return A set of unique XPaths
     */
    public static List<String> getXPath(final NodeList nodeList)
    {
        // Process the elements in the nodelist
        // Keep the paths ordered
        final List<String> xPaths = new ArrayList<String>();
        for (int i = 0; i < nodeList.getLength(); i++)
        {
            final Node node = nodeList.item(i);

            final String xpath = getXPath(node);
            System.out.println(xpath);

            if (!xPaths.contains(xpath))
            {
                xPaths.add(xpath);
            }
        }

        return xPaths;
    }

    /**
     * Takes the list of nodes then gets the XPath to each htmlNode, then removes any duplicates (removing the later one).
     *
     * @param nodes List of nodes to process
     * @return A set of unique XPaths
     */
    public static List<String> getXPath(final List<Node> nodes)
    {
        // Process the elements in the nodelist
        // Keep the paths ordered
        final List<String> xPaths = new ArrayList<String>();
        for (final Node node : nodes)
        {
            final String xpath = getXPath(node);
            System.out.println(xpath);

            if (!xPaths.contains(xpath))
            {
                xPaths.add(xpath);
            }
        }

        return xPaths;
    }

    /**
     * Method to return the position of a particular node as an XPath <code>String</code>.
     *
     * @param node Node
     * @return XPath <code>String</code> of Node
     */
    public static String getXPath(final Node node)
    {
        return getXPath(node, "", true);
    }

    /**
     * Method to return the position of a particular node as an XPath <code>String</code>.
     *
     * @param node           Node
     * @param positionFilter Boolean indicating whether the XPath contains position filters for the nodes,
     *                       which provides an absolute path to the node.
     * @return Single parent XPath of Node
     */
    public static String getXPath(final Node node, final boolean positionFilter)
    {
        return getXPath(node, "", positionFilter);
    }

    /**
     * Method getXPath
     *
     * @param node           Node
     * @param result         XPath String which is used as a buffer to store the XPath
     * @param positionFilter Boolean indicating whether the XPath contains position filters for the nodes,
     *                       which provides an absolute path to the node.
     * @return Single parent XPath of Node
     */
    private static String getXPath(final Node node, final String result, final boolean positionFilter)
    {

        if (node == null)
        {
            return result;
        }

        final int index = positionFilter ? getChildIndex(node.getParentNode(), node, false) : -1;
        switch (node.getNodeType())
        {
            case Node.ELEMENT_NODE:
                return getXPath(node.getParentNode(),
                                "/" + ((Element) node).getTagName()
                                + ((index == -1) ? "" : "[" + index + "]")
                                + result,
                                positionFilter);

            case Node.ATTRIBUTE_NODE:
                return getXPath(((Attr) node).getOwnerElement(),
                                "/@" + node.getNodeName() + "=\""
                                + node.getNodeValue() + "\"",
                                positionFilter);

            case Node.TEXT_NODE:
                return getXPath(node.getParentNode(),
                                "/text()" + ((index == -1) ? "" : "[" + index + "]"),
                                positionFilter);

            case Node.DOCUMENT_NODE:
                if (result.length() > 0)
                {
                    return result;
                }
                else
                {
                    return "/";
                }
        }

        return result;
    }

    /**
     * Returns the index of the child htmlNode.
     *
     * @param parent   The parent htmlNode
     * @param child    The child htmlNode
     * @param absolute Determines if the index is absolute (position in terms of all child nodes) or
     *                 relative (position in terms of nodes with the same name)
     * @return The relative or absolute index of the child htmlNode [1,n],
     *         or -1 if the child htmlNode is not a child of the parent htmlNode.
     */
    public static int getChildIndex(final Node parent, final Node child, final boolean absolute)
    {
        if (parent != null && child != null)
        {
            final NodeList nodeList = parent.getChildNodes();
            if (nodeList != null)
            {
                int childIndex = 1;
                for (int i = 0; i < nodeList.getLength(); i++)
                {
                    final Node node = nodeList.item(i);
                    if (child == node)
                    {
                        return childIndex;
                    }
                    if (absolute || child.getNodeName().equals(node.getNodeName()))
                    {
                        childIndex++;
                    }
                }
            }
        }
        return -1;
    }

    public static final Pattern XPATH_ELEMENT_INDEX_PATTERN = Pattern.compile("\\[[0-9]+\\]");

    public static String removeXPathPositionFilters(final String xpath)
    {
        return XPATH_ELEMENT_INDEX_PATTERN.matcher(xpath).replaceAll("");
    }

    public static List<Node> getAncestors(Node node)
    {
        final List<Node> ancestors = new ArrayList<Node>();
        while (node != null)
        {
            ancestors.add(node);
            node = node.getParentNode();
        }
        return ancestors;
    }

    public static Node getCommonAncestor(final Node[] nodes)
    {
        List<Node> commonAncestors = getAncestors(nodes[0]);
        Node commonAncestor = commonAncestors.get(0);

        for (int i = 1; i < nodes.length; i++)
        {
            final Node node = nodes[i];
            commonAncestor = getCommonAncestor(node, commonAncestors);
            if (commonAncestor == null)
            {
                return null;
            }
            commonAncestors = commonAncestors.subList(commonAncestors.indexOf(commonAncestor), commonAncestors.size());
        }
        return commonAncestor;
    }

    public static Node getCommonAncestor(Node node, final List<Node> ancestors)
    {
        while (node != null)
        {
            if (ancestors.contains(node))
            {
                return node;
            }
            node = node.getParentNode();
        }
        return null;
    }


    public static String getNodeSubtreeXMLString(final Node node)
    {
        final StringWriter writer = new StringWriter();
        writeNodeSubtreeXMLString(node, writer);
        return writer.toString();
    }

    public static void printNodeSubtreeXMLString(final Node node)
    {
        writeNodeSubtreeXMLString(node, new PrintWriter(System.out));
    }

    public static void writeNodeSubtreeXMLString(final Node node, final Writer writer)
    {
        try
        {
            final DOMSource domSource = new DOMSource(node);
            final StreamResult result = new StreamResult(writer);
            final TransformerFactory tf = TransformerFactory.newInstance();
            final Transformer transformer = tf.newTransformer();
            transformer.transform(domSource, result);
        }
        catch (TransformerException ex)
        {
            ex.printStackTrace();
        }
    }

    public static void printNodeSubtree(final Node node)
    {
        printNodeSubtree("", node);
    }

    public static void printNodeSubtree(final String indent, final Node node)
    {
        printNode(indent, node);
        if (node.hasChildNodes())
        {
            Node child = node.getFirstChild();
            while (child != null)
            {
                printNodeSubtree(indent + INDENT, child);
                child = child.getNextSibling();
            }
        }
    }

    public static void printNode(final String indent, final Node node)
    {
        System.out.print(indent + node.getNodeName());
        System.out.print(": " + node.getNodeValue());
        if (node.hasAttributes())
        {
            System.out.print(": ");
            for (int i = 0; i < node.getAttributes().getLength(); i++)
            {
                if (i != 0)
                {
                    System.out.print("# ");
                }
                System.out.print(node.getAttributes().item(i));
            }
        }
        System.out.println();
    }
}
