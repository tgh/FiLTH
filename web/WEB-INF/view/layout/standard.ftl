<#macro standard pageTitle="FiLTH">
    <!DOCTYPE html>
    <html xmlns="http://www.w3.org/1999/xhtml">
        <head>
            <title>${pageTitle}</title>
            
            <link rel="shortcut icon" href="${rc.contextPath}/favicon.ico" type="image/x-icon" />
            
            <@util.css "standard" />
            <@util.css "third-party/lightbox/lightbox" />
        </head>
        
        <body style="background: url(${rc.contextPath}/${bgImagesPath}/${bgImageFilename}) no-repeat center center fixed; background-size: cover; -webkit-background-size: cover; -moz-background-size: cover; -o-background-size: cover;">
            <div id="rootContentContainer">
                <#nested>
            </div>
            
            <div id="footer">
                <span id="copyrightText">Copyright 2009-2016 Film Library of Tyler Hayes</span> |
                Background image: <span id="bgImageMovie">${bgImageMovieTitle} (${bgImageMovieYear})</span> |
                <a data-lightbox="background" href="${rc.contextPath}/${bgImagesPath}/${bgImageFilename}">View background image</a>
            </div>
            
            <@util.js "third-party/jquery/jquery-2.2.0.min" />
            <@util.js "third-party/jquery/jquery.form.min" />
            <@util.js "global_constants" />
            <@util.js "global_functions" />
            <@util.js "third-party/lightbox/lightbox.min" />
        </body>
    </html>
</#macro>