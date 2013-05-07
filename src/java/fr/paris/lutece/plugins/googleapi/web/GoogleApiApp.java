/*
 * Copyright (c) 2002-2013, Mairie de Paris
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
package fr.paris.lutece.plugins.googleapi.web;

import fr.paris.lutece.plugins.googleapi.business.FeedProvider;
import fr.paris.lutece.plugins.googleapi.business.Item;
import fr.paris.lutece.plugins.googleapi.service.FeedsService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.web.xpages.XPage;
import fr.paris.lutece.portal.web.xpages.XPageApplication;
import fr.paris.lutece.util.html.HtmlTemplate;

import org.xml.sax.SAXException;

import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import javax.xml.parsers.ParserConfigurationException;


/**
 * Google Base API Application
 *
 */
public class GoogleApiApp implements XPageApplication
{
    private static final String TEMPLATE_PROJECT = "skin/plugins/googleapi/home.html";
    private static final String KEY_PAGE_TITLE = "googleapi.pageTitle";
    private static final String KEY_PAGE_PATH = "googleapi.pagePath";
    private static final String MARK_QUERY = "query";
    private static final String MARK_PROVIDER = "provider";
    private static final String MARK_ITEMS_LIST = "items_list";
    private static final String MARK_FEEDS_LIST = "feeds_list";
    private static final String PARAMETER_SEARCH = "search";
    private static final String PARAMETER_PROVIDER = "provider";

    /**
     * Build the XPage
     * @param request The HTTP request
     * @param nMode The current mode
     * @param plugin The current plugin
     * @return The XPage
     */
    public XPage getPage( HttpServletRequest request, int nMode, Plugin plugin )
    {
        XPage page = new XPage(  );

        Locale locale = request.getLocale(  );
        HashMap model = new HashMap(  );

        String strQuery = request.getParameter( PARAMETER_SEARCH );
        String strProvider = request.getParameter( PARAMETER_PROVIDER );

        FeedProvider fp = FeedsService.getInstance(  ).getProvider( strProvider );

        List<Item> listItems = new ArrayList<Item>(  );

        if ( strQuery != null )
        {
            try
            {
                FeedsService.getInstance(  ).getItems( strQuery, listItems, fp );
            }
            catch ( ParserConfigurationException e )
            {
                AppLogService.error( "Error retrieving items : " + e.getMessage(  ), e );
            }
            catch ( SAXException e )
            {
                AppLogService.error( "Error retrieving items : " + e.getMessage(  ), e );
            }
            catch ( IOException e )
            {
                AppLogService.error( "Error retrieving items : " + e.getMessage(  ), e );
            }
        }

        model.put( MARK_QUERY, ( strQuery != null ) ? strQuery : "" );
        model.put( MARK_PROVIDER, ( strProvider != null ) ? strProvider : "" );
        model.put( MARK_ITEMS_LIST, listItems );
        model.put( MARK_FEEDS_LIST, FeedsService.getInstance(  ).getProviders(  ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_PROJECT, locale, model );
        page.setContent( template.getHtml(  ) );

        // Set page title and path
        Object[] args = { model.get( "name" ) };
        String strTitle = I18nService.getLocalizedString( KEY_PAGE_TITLE, args, locale );
        String strPath = I18nService.getLocalizedString( KEY_PAGE_PATH, args, locale );
        page.setTitle( strTitle );
        page.setPathLabel( strPath );

        return page;
    }
}
