<%@page import="com.google.inject.Injector"%>
<%@page import="java.util.Locale"%>
<%@page import="com.google.inject.Provider"%>
<%@page import="cz.incad.Kramerius.backend.guice.LocalesProvider"%>
<%@page import="java.io.*, cz.incad.kramerius.service.*"  %>
<%@page import="cz.incad.kramerius.utils.conf.KConfiguration"%>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page isELIgnored="false"%>
<%
    String[] tabs = kconfig.getPropertyList("search.item.tabs");
    pageContext.setAttribute("tabs", tabs);
%>
<div id="centralContent">
    <ul>
        <li><a href="#bigThumbZone" class="vertical-text" ><fmt:message bundle="${lctx}">item.tab.image</fmt:message></a>        </li>
        <li><a href="#extendedMetadata" class="vertical-text" ><fmt:message bundle="${lctx}">item.tab.metadata</fmt:message></a></li>
        <c:forEach varStatus="status" var="tab" items="${tabs}">
            <c:if test="${! empty tab}">
                <c:choose>
                    <c:when test="${tab =='VIRTUAL.audioPlayer'}">
                        <li id="audio_li" style="visibility: hidden">
                            <a href="#itemtab_${fn:substringAfter(tab, '.')}" class="vertical-text"><fmt:message bundle="${lctx}">item.tab.${tab}</fmt:message></a>
                        </li>
                    </c:when>
                    <c:otherwise>
                        <li>
                            <a href="#itemtab_${fn:substringAfter(tab, '.')}" class="vertical-text"><fmt:message bundle="${lctx}">item.tab.${tab}</fmt:message></a>
                        </li>
                    </c:otherwise>
                </c:choose>
            </c:if>
        </c:forEach>
    </ul>
    <%@include file="metadata.jsp" %>
    <%@include file="image.jsp" %>
    <%--<%@include file="audioplayer.jsp" %>--%>
    <c:forEach varStatus="status" var="tab" items="${tabs}">
        <c:if test="${! empty tab}">
            <c:set var="ds" value="${fn:substringBefore(tab, '.')}" />
            <c:set var="xsl" value="${fn:substringAfter(tab, '.')}" />
            <div id="itemtab_${xsl}" class="viewer" style="overflow:hidden;"></div>
            <script type="text/javascript">
                $(document).ready(function(){
                    $('#itemtab_${xsl}.viewer').bind('viewReady', function(event, viewerOptions){
                        var pid_path = getPidPath(viewerOptions.fullid);
                <c:choose>
                    <c:when test="${tab =='VIRTUAL.audioPlayer'}">
                                if (console) console.log("updating audioplayer tab ");
                                updateAudioplayerTab('${tab}', pid_path);
                    </c:when>
                    <c:otherwise>
                                if (console) console.log("update custom tab " + '${tab}' + ", pidPath: " + pid_path);
                                updateCustomTab('${tab}', pid_path);
                    </c:otherwise>
                </c:choose>
                        });
                    });
            </script>
        </c:if>
    </c:forEach>
</div>
<script type="text/javascript">
                
    function updateCustomTab(tab, pid_path){
        $.get('inc/details/tabs/loadCustom.jsp?tab='+tab+'&pid_path=' + pid_path, function(data){
            $('#itemtab_'+tab.split(".")[1]).html(data) ;
        });
    }
    
    function updateAudioplayerTab(tab, pid_path){
        $.get('audioTracks?action=canContainTracks&pid_path=' + pid_path, function(data){
            if (data.canContainTracks){
                $.get('inc/details/tabs/audioplayer.jsp?pid_path=' + pid_path, function(data){
                    $('#itemtab_'+tab.split(".")[1]).html(data);
                });
                $('#audio_li').css('visibility', 'visible');
            }
        });
    }
    
    
    function setMainContentWidth(){ 
        var w = $(window).width()-6-$('#itemTree').width();

        $("#mainContent").css('width', w);
        w = w-45;
        $("#centralContent").css('width', w);

        //w = $('#centralContent').width();
        $("#centralContent>div").css('width', w-30-25);
        $("#extendedMetadata").css('width', w-30-25);
        $("#bigThumbZone").css('width', w-30-25);
    }

    $(document).ready(function() {
        $("#centralContent").tabs({
            
        });//.addClass('ui-tabs-vertical ui-helper-clearfix');
        //$("#centralContent li").removeClass('ui-corner-top').addClass('ui-corner-left');
        //$("#centralContent").css('position', 'static');
    });

</script>