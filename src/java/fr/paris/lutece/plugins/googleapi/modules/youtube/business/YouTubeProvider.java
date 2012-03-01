/*
 * Copyright (c) 2002-2012, Mairie de Paris
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
package fr.paris.lutece.plugins.googleapi.modules.youtube.business;

import fr.paris.lutece.plugins.googleapi.business.VideoProvider;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.util.html.HtmlTemplate;

import java.util.HashMap;
import java.util.Locale;


/**
 * YouTubeProvider
 */
public class YouTubeProvider extends VideoProvider
{
    private static final String TEMPLATE_PLAYER = "admin/plugins/googleapi/modules/youtube/youtube_player.html";
    private static final String MARK_ID = "id";
    private static final String MARK_WIDTH = "width";
    private static final String MARK_HEIGHT = "height";
    private static final String MARK_LOCALE = "locale";

    /**
     * Returns the HTML code of the video player
     * @param strVideoUrl The video URL
     * @param strWidth The width of the player
     * @param strHeight The height of the player
     * @param locale The locale to use with the player
     * @return the HTML code of the video player
     */
    public String getPlayerHtml( String strVideoUrl, String strWidth, String strHeight, Locale locale )
    {
        HashMap model = new HashMap(  );

        model.put( MARK_ID, getIdFromUrl( strVideoUrl ) );
        model.put( MARK_WIDTH, strWidth );
        model.put( MARK_HEIGHT, strHeight );
        model.put( MARK_LOCALE, locale.getLanguage(  ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_PLAYER, locale, model );

        AppLogService.info( "Inserting player : '" + template.getHtml(  ) + "'" );

        return template.getHtml(  );
    }

    /**
     * Extracts the video Id from the Video Url
     * @param strVideoUrl The video Url
     * @return The video Id
     */
    private String getIdFromUrl( String strVideoUrl )
    {
        String strId = strVideoUrl.substring( strVideoUrl.indexOf( "=" ) + 1 );

        return strId;
    }
}
