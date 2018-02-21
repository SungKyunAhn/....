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
	
	<link href="../../vendors/bootstrap/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="../../vendors/font-awesome/css/font-awesome.min.css" rel="stylesheet">
    <link href="../../vendors/nprogress/nprogress.css" rel="stylesheet">
    <link href="../../vendors/iCheck/skins/flat/green.css" rel="stylesheet">
	
    <link href="../../vendors/bootstrap-progressbar/css/bootstrap-progressbar-3.3.4.min.css" rel="stylesheet">
    <link href="../../vendors/jqvmap/dist/jqvmap.min.css" rel="stylesheet"/>
    <link href="../../vendors/bootstrap-daterangepicker/daterangepicker.css" rel="stylesheet">

    <link href="../../build/css/custom.min.css" rel="stylesheet">
    <link href="../../css/common.css" rel="stylesheet">
</head>
<body class="nav-md">
    <div class="container body">
      <div class="main_container">
        <div class="col-md-3 left_col">
          <div class="left_col scroll-view">

            <!-- sidebar menu -->
			<jsp:include page="../left.jsp" flush="true"/>
			
          </div>
        </div>

        <!-- top navigation -->
		<jsp:include page="../top.jsp" flush="true"/>
		
		<!-- page content -->
        <div class="right_col" role="main">
			<div class="row">
				<div class="col-md-12 col-sm-12 col-xs-12">
					<div class="dashboard_graph">
			            <div class="row x_title">
			              <div class="col-md-6">
			                <h3>HRM Management</h3>
			              </div>
			            </div>
			
						<div class="col-md-4 col-sm-4 col-xs-4">
							<div class="form-group">
								<label class="control-label search-label">Wearable ID</label>
								<div>
									<input id="wearableId" type="text" class="form-control search-text" placeholder="Wearable ID">
								</div>
							</div>
						</div>	
						<div class="col-md-4 col-sm-4 col-xs-4">
							<label class="control-label search-label">Search Date</label>
							<div>
			                    <div id="reportrange" class="pull-left" style="background: #fff; cursor: pointer; padding: 5px 10px; border: 1px solid #ccc; width: 278px">
			                      <i class="glyphicon glyphicon-calendar fa fa-calendar"></i>
			                      <span></span> <b class="caret"></b>
			                    </div>
							</div>
						</div>
						<div class="col-md-12 col-sm-12 col-xs-12">
							<button class="btn btn-sm btn-primary pull-right" onclick="javascript:hrmSearch()">Search</button>
						</div>
					</div>
					<div class="clearfix" style="background: #fff"></div>
					<br />
				</div>
			</div>

			<div class="row">
				<div class="col-md-12 col-sm-12 col-xs-12">
					<div>
						<ul class="grid-ul">
							<li class="grid-ul-li"><i class="fa fa-square access-24hour grid-ul-li-item"></i>Normality </li>
							<li class="grid-ul-li"><i class="fa fa-square access-48hour grid-ul-li-item"></i>HRM Low </li>
							<li class="grid-ul-li"><i class="fa fa-square access-72hour grid-ul-li-item"></i>HRM High </li>
						</ul>
					</div>
					<div class="x_panel">
						<div class="x_content">
							<table id="hrm_table" class="table table-striped jambo_table">
								<thead>
									<tr>
										<th>#</th>
										<th>Wearabke ID</th>
										<th>Status</th>
										<th>HRM</th>
										<th>Min HRM</th>
										<th>Max HRM</th>
										<th>Comm. Date</th>
									</tr>
								</thead>
							</table>
						</div>
					</div>
				</div>
			</div>
		</div>
      </div>
	</div>
		
	<script src="../../js/jquery-1.12.4.min.js"></script>
    <script src="../../vendors/bootstrap/dist/js/bootstrap.min.js"></script>
    <script src="../../vendors/bootstrap-progressbar/bootstrap-progressbar.min.js"></script>
    <script src="../../vendors/moment/min/moment.min.js"></script>
	<script src="../../vendors/DateJS/build/date.js"></script>
	<script src="../../vendors/bootstrap-daterangepicker/daterangepicker.js"></script>
	<script src="../../build/js/custom.js"></script>
	<script src="../../js/jquery.dataTables.min.js"></script>
	<script src="../../js/gadget/gadget.grid.js"></script>
	
	<script type="text/javascript">
		var hrmGrid = "";
		var searchStartTime = "";
		var searchEndTime = "";
		var pageNum = "0";
		var wdId = "";
		
		$( document ).ready(function() {
			$('#reportrange').find('span').html('');
			setActiveMenu('HRM Grid');
			getGridData();
		});
		
		function getGridData() {
			hrmGrid = $('#hrm_table').DataTable({
				processing: true,
			    serverSide: true,
			    searching: false,
			    ordering: false,
			    info: false,
			    bLengthChange: false,
			    ajax: {
			    	"type": 'GET',
			    	"url": '${ctx}/gadget/heartbeat/getHRMGrid.do',
			    	"data": function (d) {
			    		d.pageNum = pageNum;
			    		d.wdId = wdId;
			    		d.searchStartTime = searchStartTime;
			    		d.searchEndTime = searchEndTime;
			    	}
			    },
			    columns: [
		        	 { "data": null },
		        	 { "data": "deviceId" },
		        	 { "data": null },
		        	 { "data": "rate" },
		        	 { "data": "minHRM" },
		        	 { "data": "maxHRM" },
		        	 { "data": "yyyymmddhhmmss" }
		        ],
		        fnRowCallback: function( nRow, aData, iDisplayIndex, iDisplayIndexFull ) {
		        	//set row index
		        	var line = ((pageNum * 10) + (iDisplayIndexFull + 1));
		        	$(nRow).find('td').eq(0).text(line);
		        	
		        	//set row color, hrm status
		        	var minHrm = aData.minHRM;
		        	var maxHrm = aData.maxHRM;
		        	if(aData.rate <= minHrm) {
		        		$($(nRow).find('td').eq(1)).addClass("colorFBB00");
		        		$($(nRow).find('td').eq(2)).addClass("colorFBB00");
		        		$(nRow).find('td').eq(2).text('<fmt:message key="aimir.hrm.warning.low"/>');
		        	} else if(aData.rate >= maxHrm) {
		        		$($(nRow).find('td').eq(1)).addClass("colorFF5E00");
		        		$($(nRow).find('td').eq(2)).addClass("colorFF5E00");
		        		$(nRow).find('td').eq(2).text('<fmt:message key="aimir.hrm.warning.high"/>');
		        	} else {
		        		$($(nRow).find('td').eq(1)).addClass("color26B99A");
		        		$($(nRow).find('td').eq(2)).addClass("color26B99A");
		        		$(nRow).find('td').eq(2).text('<fmt:message key="aimir.hrm.normality"/>');
		        	}
		        	$(nRow).find('td')[1].style.fontWeight="bold";
		        	$(nRow).find('td')[2].style.fontWeight="bold";
		        	
		        	//set row time set
		        	$(nRow).find('td').eq(6).text(moment(aData.yyyymmddhhmmss, 'YYYYMMDDhhmmss').format('YYYY-MM-DD hh:mm:ss'));
		        }
			});
		}
		
		$('#hrm_table').on('page.dt', function(){			
			 var info = hrmGrid.page.info();
			 pageNum = info.page;
		});
	
		function hrmSearch() {
			pageNum = "0";
			wdId = $('#wearableId').val();
			var time = $('#reportrange').find('span').html();
			
			searchStartTime = getStartTime(time);
			searchEndTime = getEndTime(time);
			
			hrmGrid.ajax.reload( function ( json ) {
				 return json.data;
			});
		}
		
	</script>
</body>
</html>