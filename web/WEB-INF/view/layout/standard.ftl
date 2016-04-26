<#macro standard pageTitle="FiLTH">
    <!DOCTYPE html>
    <html xmlns="http://www.w3.org/1999/xhtml">
        <head>
            <title>${pageTitle}</title>
            
            <link href='https://fonts.googleapis.com/css?family=Playfair+Display:400,400italic,700' rel='stylesheet' type='text/css'>
            <link rel="shortcut icon" href="${rc.contextPath}/favicon.ico" type="image/x-icon" />
            
            <@util.css "standard" />
            <@util.css "third-party/lightbox/lightbox" />
            <@util.css "third-party/remodal/remodal" />
            <@util.css "third-party/remodal/remodal-default-theme" />
            <@util.css "third-party/custom-scrollbar/jquery.mCustomScrollbar.min" />
            
            <@util.js "third-party/jquery/jquery-2.2.0.min" />
            <@util.js "third-party/jquery/jquery.form.min" />
            <@util.js "global_constants" />
            <@util.js "global_functions" />
            <@util.js "third-party/remodal/remodal.min" />
            <@util.js "third-party/custom-scrollbar/jquery.mCustomScrollbar.concat.min" />
            
            <#-- Set a global variable of the app's context path for urls used in javascript -->
            <script type="text/javascript">
                var contextPath = '${rc.contextPath}';
            </script>
        </head>
        
        <body style="background: url(${rc.contextPath}/${bgImagesPath}/${bgImageFilename}) no-repeat center center fixed; background-size: cover; -webkit-background-size: cover; -moz-background-size: cover; -o-background-size: cover;">
            <div class="remodal-bg">
                <div id="rootContentContainer" class="mCustomScrollbar" data-mcs-theme="minimal-dark">
                    <#nested>
                </div>
                
                <div id="movieModal" class="remodal modal" data-remodal-id="movieModal" data-remodal-options="hashTracking: false"></div>
                
                <div id="footer">
                    <span id="copyrightText">Copyright 2009-2016 Film Library of Tyler Hayes</span> |
                    Background image: <a id="bgImageMovie" data-remodal-target="movieModal" data-movie-id="${bgImageMovieId}">${bgImageMovieTitle} (${bgImageMovieYear})</a> |
                    <a data-lightbox="background" href="${rc.contextPath}/${bgImagesPath}/${bgImageFilename}">View background image</a>
                </div>
            </div>
            
            <#-- lightbox js needs to be here as per its documentation -->
            <@util.js "third-party/lightbox/lightbox.min" />
        </body>
    </html>
</#macro>