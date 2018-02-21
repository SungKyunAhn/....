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
			                <h3>Location Management</h3>
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
							<button class="btn btn-sm btn-primary pull-right" onclick="javascript:locationSearch()">Search</button>
						</div>
					</div>
					<div class="clearfix" style="background: #fff"></div>
					<br />
				</div>
			</div>
		</div>
      </div>
	</div>
		
	<script src="../../js/jquery-1.12.4.min.js"></script>
    <script src="../../vendors/bootstrap/dist/js/bootstrap.min.js"></script>
    <script src="../../vendors/moment/min/moment.min.js"></script>
	<script src="../../vendors/DateJS/build/date.js"></script>
	<script src="../../vendors/bootstrap-daterangepicker/daterangepicker.js"></script>
	<script src="../../build/js/custom.js"></script>
	<script src="../../js/jquery.dataTables.min.js"></script>
	<script src="../../js/gadget/gadget.grid.js"></script>
	
	<script type="text/javascript">
		$( document ).ready(function() {
			$('#reportrange').find('span').html('');
			setActiveMenu('GPS');
		});
	
	</script>
</body>
</html>