/**
 * Copyright (C) 2011 Angelo Zerr <angelo.zerr@gmail.com> and Pascal Leclercq <pascal.leclercq@gmail.com>
 *
 * All rights reserved.
 *
 * Permission is hereby granted, free  of charge, to any person obtaining
 * a  copy  of this  software  and  associated  documentation files  (the
 * "Software"), to  deal in  the Software without  restriction, including
 * without limitation  the rights to  use, copy, modify,  merge, publish,
 * distribute,  sublicense, and/or sell  copies of  the Software,  and to
 * permit persons to whom the Software  is furnished to do so, subject to
 * the following conditions:
 *
 * The  above  copyright  notice  and  this permission  notice  shall  be
 * included in all copies or substantial portions of the Software.
 *
 * THE  SOFTWARE IS  PROVIDED  "AS  IS", WITHOUT  WARRANTY  OF ANY  KIND,
 * EXPRESS OR  IMPLIED, INCLUDING  BUT NOT LIMITED  TO THE  WARRANTIES OF
 * MERCHANTABILITY,    FITNESS    FOR    A   PARTICULAR    PURPOSE    AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE,  ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package fr.opensagres.xdocreport.document.docx.preprocessor;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import fr.opensagres.xdocreport.core.document.ImageFormat;
import fr.opensagres.xdocreport.document.preprocessor.sax.BufferedDocumentContentHandler;
import fr.opensagres.xdocreport.document.preprocessor.sax.IBufferedRegion;

/**
 * Parse content of the [Content_Types].xml to add missing image format. Ex :
 * 
 * <pre>
 * <Default Extension="jpg" ContentType="image/jpeg" />
 * </pre>
 */
public class DocxContentTypesDocumentContentHandler
    extends BufferedDocumentContentHandler
{

    private static final String DEFAULT_ELT = "Default";
    private static final String EXTENSION_ATTR = "Extension";
    
    private List<ImageFormat> missingFormats = new ArrayList<ImageFormat>();

    @Override
    public void startDocument()
        throws SAXException
    {
        ImageFormat format = null;
        ImageFormat[] formats = ImageFormat.values();
        for ( int i = 0; i < formats.length; i++ )
        {
            format = formats[i];
            missingFormats.add( format );
        }
        super.startDocument();
    }

    @Override
    public boolean doStartElement( String uri, String localName, String name, Attributes attributes )
        throws SAXException
    {
        if ( DEFAULT_ELT.equals( name ) )
        {
            ImageFormat format = ImageFormat.getFormatByExtension( attributes.getValue( EXTENSION_ATTR ) );
            if ( format != null )
            {
                missingFormats.remove( format );
            }
        }
        return super.doStartElement( uri, localName, name, attributes );
    }

    @Override
    public void doEndElement( String uri, String localName, String name )
        throws SAXException
    {
        if ( "Types".equals( name ) )
        {
            for ( ImageFormat format : missingFormats )
            {
                IBufferedRegion currentRegion = getCurrentElement();
                currentRegion.append( "<Default Extension=\"" );
                currentRegion.append( format.name() );
                currentRegion.append( "\" ContentType=\"image/" );
                currentRegion.append( format.getType() );
                currentRegion.append( "\" />" );

            }
        }
        super.doEndElement( uri, localName, name );
    }
}
