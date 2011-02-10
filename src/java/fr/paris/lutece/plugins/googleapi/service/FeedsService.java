/*
 * Copyright (c) 2002-2009, Mairie de Paris
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

import fr.paris.lutece.plugins.googleapi.business.FeedProvider;
import fr.paris.lutece.plugins.googleapi.business.Item;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.url.UrlItem;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;


/**
 * This service provides an access to all feeds defined in the Spring context file : googleapi_context.xml
 */
public class FeedsService
{
    private static final String PLUGIN_NAME = "googleapi";
    private static final String BEAN_FEEDS = "googleapi.feeds";
    private static final String PROPERTY_API_KEY = "googleapi.api.key";
    private static Map<String, FeedProvider> _mapProviders = new HashMap<String, FeedProvider>(  );
    private static FeedsService _singleton;

    /** Creates a new instance of FeedsService */
    private FeedsService(  )
    {
    }

    /**
     * Returns the unique instance of the service
     * @return The instance
     */
    public static FeedsService getInstance(  )
    {
        if ( _singleton == null )
        {
            _singleton = (FeedsService) SpringContextService.getPluginBean( PLUGIN_NAME, BEAN_FEEDS );
        }

        return _singleton;
    }

    /**
     * Sets the feeds map (used for spring injection)
     * @param map The feeds map
     */
    public void setFeedsMap( Map<String, FeedProvider> map )
    {
        _mapProviders = map;
    }

    /**
     * Returns a provider from its key
     * @param strKey The provider's key
     * @return The provider
     */
    public FeedProvider getProvider( String strKey )
    {
        return _mapProviders.get( strKey );
    }

    /**
     * Returns the providers list
     * @return the providers list
     */
    public ReferenceList getProviders(  )
    {
        ReferenceList listProviders = new ReferenceList(  );

        for ( FeedProvider fp : _mapProviders.values(  ) )
        {
            listProviders.addItem( fp.getKey(  ), fp.getName(  ) );
        }

        return listProviders;
    }

    /**
     * Returns the providers list
     * @return the providers list
     */
    public ReferenceList getProviders( Class classProvider )
    {
        ReferenceList listProviders = new ReferenceList(  );

        for ( FeedProvider fp : _mapProviders.values(  ) )
        {
            if ( classProvider.isInstance( fp ) )
            {
                listProviders.addItem( fp.getKey(  ), fp.getName(  ) );
            }
        }

        return listProviders;
    }

    /**
    * Connect to the Google Base data API server, retrieve the items
    *
    * @param strQuery The query
    * @param listItems A list of item to feed
    * @param provider The feed provider
    * @throws java.io.IOException if an error occured during the process
    * @throws org.xml.sax.SAXException if an error occured during the process
    * @throws javax.xml.parsers.ParserConfigurationException if an error occured during the process
    */
    public void getItems( String strQuery, List<Item> listItems, FeedProvider provider )
        throws IOException, SAXException, ParserConfigurationException
    {
        /*
         * Create a URL object, open an Http connection on it and get the input
         * stream that reads the Http response.
         */
        String strApiKey = AppPropertiesService.getProperty( PROPERTY_API_KEY );

        UrlItem url = new UrlItem( provider.getFeedsUrl(  ) );
        url.addParameter( provider.getQueryParameter(  ), URLEncoder.encode( strQuery, "UTF-8" ) );

        if ( ( strApiKey != null ) && ( !strApiKey.equals( "" ) ) )
        {
            url.addParameter( "key", strApiKey );
        }

        URL urlFeed = new URL( url.getUrl(  ) );
        HttpURLConnection httpConnection = (HttpURLConnection) urlFeed.openConnection(  );
        InputStream inputStream = httpConnection.getInputStream(  );

        AppLogService.info( "Retrieving items URL : " + url.getUrl(  ) );

        //        AppLogService.info( "Feed : " + getFeedAsString( inputStream ) );

        /*
         * Create a SAX XML parser and pass in the input stream to the parser to extract
         * fields from the stream.
         */
        SAXParserFactory factory = SAXParserFactory.newInstance(  );
        SAXParser parser = factory.newSAXParser(  );
        FeedHandler handler = provider.getFeedHandler(  );
        handler.setItemList( listItems );
        parser.parse( inputStream, handler );
    }
}
