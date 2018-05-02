/*
 * Copyright (c) 2002-2017, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.googleapi.service;

import fr.paris.lutece.plugins.googleapi.business.Item;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.List;
import java.util.Stack;


/**
 * Simple SAX event handler, which prints out the titles of all entries in the
 * Atom response feed.
 */
public class FeedHandler extends DefaultHandler
{
    protected List<Item> _listItems;

    /**
     * Stack containing the opening XML tags of the response.
     */
    protected Stack<String> xmlTags = new Stack<String>(  );

    /**
     * True if we are inside of a data entry's title, false otherwise.
     */
    protected boolean _bInsideEntryTitle;
    protected boolean _bInsideEntryContent;
    protected Item _item;

    /**
     * Receive notification of an opening XML tag: push the tag to
     * <code>xmlTags</code>. If the tag is a title tag inside an entry tag,
     * turn <code>insideEntryTitle</code> to <code>true</code>.
     * @param listItems
     */
    public void setItemList( List<Item> listItems )
    {
        _listItems = listItems;
    }

    /**
     *
     * @param uri
     * @param localName
     * @param qName
     * @param attributes
     * @throws org.xml.sax.SAXException
     */
    public void startElement( String uri, String localName, String qName, Attributes attributes )
        throws SAXException
    {
        if ( qName.equals( "title" ) && xmlTags.peek(  ).equals( "entry" ) )
        {
            _bInsideEntryTitle = true;
            _item = new Item(  );
            _listItems.add( _item );
        }
        else if ( qName.equals( "content" ) && xmlTags.peek(  ).equals( "entry" ) )
        {
            _bInsideEntryContent = true;
        }
        else if ( qName.equals( "link" ) && xmlTags.peek(  ).equals( "entry" ) )
        {
            if ( attributes.getValue( "rel" ).equals( "alternate" ) )
            {
                _item.setLink( attributes.getValue( "href" ) );
            }
        }
        else if ( qName.equals( "media:thumbnail" ) )
        {
            _item.setThumbnailUrl( attributes.getValue( "url" ) );
        }

        xmlTags.push( qName );
    }

    /**
     * Receive notification of a closing XML tag: remove the tag from teh stack.
     * If we were inside of an entry's title, turn <code>insideEntryTitle</code>
     * to <code>false</code>.
     * @param uri
     * @param localName
     * @param qName
     * @throws org.xml.sax.SAXException
     */
    public void endElement( String uri, String localName, String qName )
        throws SAXException
    {
        // If a "title" element is closed, we start a new line, to prepare
        // printing the new title.
        xmlTags.pop(  );

        if ( _bInsideEntryTitle )
        {
            _bInsideEntryTitle = false;
        }
        else if ( _bInsideEntryContent )
        {
            _bInsideEntryContent = false;
        }
    }

    /**
     * Callback method for receiving notification of character data inside an
     * XML element.
     * @param ch
     * @param start
     * @param length
     * @throws org.xml.sax.SAXException
     */
    public void characters( char[] ch, int start, int length )
        throws SAXException
    {
        // display the character data if the opening tag is "title" and its parent is
        // "entry"
        if ( _bInsideEntryTitle )
        {
            _item.setTitle( new String( ch, start, length ) );
        }
        else if ( _bInsideEntryContent )
        {
            _item.setContent( new String( ch, start, length ) );
        }
    }
}
