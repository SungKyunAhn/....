<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8"%>
<%@ include file="/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="en">
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <!-- Meta, title, CSS, favicons, etc. -->
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <title><fmt:message key="aimir.version"/> </title>

    <link href="../vendors/bootstrap/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="../vendors/font-awesome/css/font-awesome.min.css" rel="stylesheet">
    <link href="../vendors/nprogress/nprogress.css" rel="stylesheet">
    <link href="../vendors/iCheck/skins/flat/green.css" rel="stylesheet">
	
    <link href="../vendors/bootstrap-progressbar/css/bootstrap-progressbar-3.3.4.min.css" rel="stylesheet">
    <link href="../vendors/jqvmap/dist/jqvmap.min.css" rel="stylesheet"/>

    <link href="../build/css/custom.min.css" rel="stylesheet">
    <link href="../css/common.css" rel="stylesheet">
    <fmt:setLocale value='${lang}' scope="session"/>

  </head>

  <body class="nav-md">
    <div class="container body">
      <div class="main_container">
        <div class="col-md-3 left_col">
          <div class="left_col scroll-view">
          	
            <!-- sidebar menu -->
			<jsp:include page="left.jsp" flush="true"/>
			
          </div>
        </div>

        <!-- top navigation -->
		<jsp:include page="top.jsp" flush="true"/>
		
		<!-- page content -->
        <div class="right_col" role="main">
        	<div class="row">
        		<div class="col-md-12 col-sm-12">
        			<div style="max-width: 100%;">
        				<div class="selector" id="vizContainer"></div>
        			</div>
        		</div>
        	</div>
        </div>
         
      </div>
    </div>
    
    <script src="../vendors/jquery/dist/jquery.min.js"></script>
    <script src="../vendors/bootstrap/dist/js/bootstrap.min.js"></script>
    <script src="../vendors/bootstrap-progressbar/bootstrap-progressbar.min.js"></script>
    <script src="../vendors/moment/min/moment.min.js"></script>
    <script src="../vendors/echarts/dist/echarts.min.js"></script>
	<script src="../js/theme/echart.theme.js"></script>
	<script src="../js/jquery.dataTables.min.js"></script>
	
    <!-- Custom Theme Scripts -->
    <script src="../build/js/custom.min.js"></script>
	<!-- Tableau Scripts -->
	<script src="../js/tableau-2.min.js"></script>
	<script src="../js/tableau-2.2.1.min.js"></script>	
	<script>
	
	 $( document ).ready(function() {
		 
		 /* $('.selector').css('width', $(window).width()-268); */
		 $('.selector').css('height', $(window).height()-100);
		    $(window).resize(function (){
		    	 /* $('.selector').css('width', $(window).width()-268); */
		 	     $('.selector').css('height', $(window).height()-100);
		 	    document.location.reload();
		   	 })
		 init();
	 });
		
	function init() {
    	$.getJSON('${ctx}/gadget/tableau/getTicketFromTableau.do', function(json) {
    		if(json.status == 'SUCCESS') {
    			if(json.status == 'SUCCESS') {
    				var containerDiv = document.getElementById("vizContainer");
    				var url = json.embedGetAddress + "/views/Tableau-Total-Main/Integration-Aimir-System?:embed=y&:showAppBanner=false&:display_count=no&:showVizHome=no&:toolbar=no&:";
    				//var url = "http://172.16.30.164:8000/trusted/" + json.ticket + "/views/IoT-New-Main/IoT-New-Main?:embed=y&:showAppBanner=false&:display_count=no&:showVizHome=no&:height=899"
 					console.log("url : " + url);
    				var option = {
 						  //width: 1650,
 						  //height: 899,
 						  hideTabs: true,
 						  hideToolbar: false
    				}
    				var viz = new tableau.Viz(containerDiv, url, option);
    			}
    		}
 	    });
	}
	
	</script>
  </body>
</html>
