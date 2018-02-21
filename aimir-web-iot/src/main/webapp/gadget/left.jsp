<%@ include file="/taglibs.jsp"%>
	
	<br />
	<h1> Aimir3.6 </h1>
     <div class="navbar nav_title" style="border: 0;">
       <a href="#" onclick="javascript:pageNavigation('/gadget/index-aimir.jsp')" class="site_title" style="padding-left: 30px">
       		<span> Aimir Main </span>
       </a>
       <%-- <a href="#" onclick="javascript:pageNavigation('/gadget/index.jsp')" class="site_title" style="padding-left: 30px">
       		<span>Aimir3.6 Main </span><fmt:message key="aimir.version"/>
       </a> --%>       
     </div>
     <div class="navbar nav_title" style="border: 0;">
		<a href="#" onclick="javascript:pageNavigation('/gadget/index-tableau.jsp')" class="site_title" style="padding-left: 30px">
       		<span> Tableau Main </span>
       	</a>
     </div>
     <div class="navbar nav_title" style="border: 0;">
		<a href="#" onclick="javascript:pageNavigation('/gadget/index-tableauMap.jsp')" class="site_title" style="padding-left: 30px">
       		<span> Tableau Map </span>
       	</a>
     </div>

     <div class="clearfix"></div>
     <br />

    <div id="sidebar-menu" class="main_menu_side hidden-print main_menu">
      <div class="menu_section">
        
        <h2> Device Management</h2>
        <ul class="nav side-menu">
          <li><a><i class="fa fa-table"></i> Device Grid <span class="fa fa-chevron-down"></span></a>
            <ul class="nav child_menu">
              <li><a onclick="javascript:pageNavigation('/gadget/gateway/getMainParameter.do')">Gateway Grid</a></li>
              <li><a onclick="javascript:pageNavigation('/gadget/wearable/getMainParameter.do')">Wearable Grid</a></li>
            </ul>
          </li>
          <li><a><i class="fa fa-bar-chart-o"></i> Device Chart <span class="fa fa-chevron-down"></span></a>
            <ul class="nav child_menu">
              <li><a onclick="javascript:pageNavigation('/gadget/tabGateway/getTableauParameter.do')">Gateway Chart</a></li>
              <li><a onclick="javascript:pageNavigation('/gadget/tabWearable/getTableauParameter.do')">Wearable Chart</a></li>
            </ul>
          </li>
        </ul>
        <br>
        
		<h2> History Management</h2>
		<ul class="nav side-menu">
          <li><a><i class="fa fa-table"></i> History Grid <span class="fa fa-chevron-down"></span></a>
            <ul class="nav child_menu">
              <li><a onclick="javascript:pageNavigation('/gadget/heartbeat/getMainParameter.do')">HRM Grid</a></li>
              <!-- <li><a onclick="javascript:pageNavigation('/gadget/location/getMainParameter.do')">GPS</a></li> -->
            </ul>
          </li>
          
          <li><a><i class="fa fa-bar-chart-o"></i> History Chart <span class="fa fa-chevron-down"></span></a>
            <ul class="nav child_menu">
              <li><a onclick="javascript:pageNavigation('/gadget/tabHeartbeat/getTableauParameter.do')">HRM Chart</a></li>
              <li><a onclick="javascript:pageNavigation('/gadget/tabLocation/getTableauParameter.do')">GPS</a></li>
            </ul>
          </li>
        </ul>
        <br>
        
        <h2> Ondemand Management</h2>
		<ul class="nav side-menu">
          <li><a><i class="fa fa-edit"></i> Device Ondemand <span class="fa fa-chevron-down"></span></a>
            <ul class="nav child_menu">
              <li><a onclick="javascript:pageNavigation('/gadget/ondemand/getOndemandParameter.do')">Wearable Ondemand</a></li>
            </ul>
          </li>
          <li><a><i class="fa fa-list-alt"></i> Ondemand History <span class="fa fa-chevron-down"></span></a>
            <ul class="nav child_menu">
              <li><a onclick="javascript:pageNavigation('/gadget/history/getOndemandParameter.do')">Ondemand History</a></li>
            </ul>
          </li>
        </ul>
      </div>
    </div>
    
    <script type="text/javascript">
    	function pageNavigation(address) {
    		var urll = '${url}';
            var port = '${localPort }';
            var ctx ='${ctx}';
            var strr = urll.split(ctx);
            
            urll = strr[0] + ctx + address;
            document.location.href = urll;
            return;
    	}
    	
    	function setActiveMenu(menu) {
    		$('.menu_section').find('a:contains('+"\""+menu+"\""+')').parents('li').find('.child_menu').show();
    		$('.menu_section').find('a:contains('+"\""+menu+"\""+')').parents('li').first().addClass('active');
    	}
    		
    </script>

    