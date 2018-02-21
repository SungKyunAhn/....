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
	<link href="../../vendors/iCheck/skins/flat/green.css" rel="stylesheet">
	
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
			                <h3>Ondemand History Management</h3>
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
							<label class="control-label search-label">Operation Date</label>
							<div>
			                    <div id="reportrange" class="pull-left" style="background: #fff; cursor: pointer; padding: 5px 10px; border: 1px solid #ccc; width: 278px">
			                      <i class="glyphicon glyphicon-calendar fa fa-calendar"></i>
			                      <span></span> <b class="caret"></b>
			                    </div>
							</div>
						</div>
						<div class="col-md-4 col-sm-4 col-xs-4">
							<label class="control-label search-label">Update Date</label>
							<div>
			                    <div id="reportrange2" class="pull-left" style="background: #fff; cursor: pointer; padding: 5px 10px; border: 1px solid #ccc; width: 278px">
			                      <i class="glyphicon glyphicon-calendar fa fa-calendar"></i>
			                      <span></span> <b class="caret"></b>
			                    </div>
							</div>
						</div>
						<div class="col-md-12 col-sm-12 col-xs-12">
							<button class="btn btn-sm btn-primary pull-right" onclick="javascript:wearableSearch()">Search</button>
						</div>
					</div>
					<div class="clearfix" style="background: #fff"></div>
					<br />
				</div>
        	</div>
        	
			<div class="row">			
				<div class="col-md-4 col-sm-4 col-xs-4">
					<div class="x_panel" style="padding: 10px 10px; min-width: 360px;">
						<div id="vizContainer" style="width: 100%; height: 100%;"></div>	
					
					<!-- <div id="undoRedo">
				    	<button id="undoButton" onclick="undo()" class="btn" disabled>Undo</button>
				    	<button id="redoButton" onclick="redo()" class="btn" disabled>Redo</button>		
				    </div> -->
				    
				    </div>
				</div>
				<div class="col-md-8 col-sm-8 col-xs-8">
					<!-- <div>
						<ul class="grid-ul">
							<li class="grid-ul-li"><i class="fa fa-square access-24hour grid-ul-li-item"></i>Access 24Hour </li>
							<li class="grid-ul-li"><i class="fa fa-square access-48hour grid-ul-li-item"></i>Access  48Hour </li>
							<li class="grid-ul-li"><i class="fa fa-square access-72hour grid-ul-li-item"></i>Access 72Hour </li>
							<li class="grid-ul-li"><i class="fa fa-square access-unknow grid-ul-li-item"></i>Access Unknow </li>
						</ul>
					</div> -->
					<div class="x_panel">
						<div class="x_content"> 
							<!-- <div style="float: right; margin-bottom: 7px;">
								<div id="btnOnDemand">
									<button class="btn btn-primary group-ondemand-btn" onclick="javascript:setGroupOnemand()">Group Ondemand</button>
								</div>
								<div id="btnOnDemandParam" style="display: none;">
									<button id="btn-group-ondemand" class="btn btn-primary group-ondemand-btn" onclick="javascript:onDemandOpen()">Ondemand</button>
									<button class="btn btn-primary group-ondemand-btn" onclick="javascript:onDemandClose()">Close</button>
								</div>
							</div> -->
							<table id="wearable_table" class="table table-striped jambo_table">
								<thead>
									<tr>
										<th>#</th>
										<th>Wearable Id</th>
										<th>Operation Command Code</th>
										<th>Operation Type</th>
										<th>Status</th>
										<th>Control Code</th>
										<th>Update Time</th>
										<th>Description</th>
										<th>Error Reason</th>
									</tr>
								</thead>
							</table>
							<table id="wearable_check_table" class="table table-striped jambo_table" style="display: none">
								<thead>
									<tr class="headings">
										<th>
											<div class="btn-group" data-toggle="buttons">
												<label id="all-check-label" class="btn btn-success ckeck-btn-grid">
													<input type="checkbox" id="all-check" autocomplete="off">
													<span class="glyphicon glyphicon-ok"></span>
												</label>
											</div>
										</th>
										<th class="column-title">Wearable Id</th>
										<th class="column-title">Operation Command Code</th>
										<th class="column-title">Operation Type</th>
										<th class="column-title">Status</th>
										<th class="column-title">Control Code</th>
										<th class="column-title">Update Time</th>
										<th class="column-title">Description</th>
										<th class="column-title">Error Reason</th>
									</tr>
								</thead>
							</table>
						</div>
					</div>
				</div>
			</div>
        	
        	<!-- <div id="wdDetails" class="row" style="display: none;">
        		<div class="col-md-4 col-sm-4 col-xs-12">
	              <div class="x_panel">
	                <div class="x_title">
	                	<h2>Wearable Details</h2>
	                	<div class="clearfix"></div>
	                </div>
	                <div class="x_content">
						<div style="width: 100%; max-height: 400px">
							<div>
								<label class="control-label" style="width: 25%;" >Wearable ID</label>
								<label id="detailId" class="control-label"></label>
							</div>
							<div style="margin-top: 8px;">
								<div style="width: 35%">
									<input type="radio" class="flat" name="command" id="periodSet" checked/> Period Command
								</div>
								<div style="margin-left: 30px; margin-top: 10px;">
									<select id="heard" name="cmd-period" class="form-control" required>
				                    	<option value="1">1 Interval Min.</option>
				                        <option value="15">15 Interval Min.</option>
				                        <option value="20">20 Interval Min.</option>
				                        <option value="25">25 Interval Min.</option>
				                        <option value="30">30 Interval Min.</option>
				                    </select>
                   				</div>
							</div>
							<div style="margin-top: 8px;">
								<div style="width: 35%">
									<input type="radio" class="flat" name="command" id="uplinkSet"/> Uplink Command
								</div>
								<div style="margin-top: 10px;">
									<ul style="list-style: none">
										<li style="display: inline;">
											<input id="cmd-gps" name="cmd-uplink" type="checkbox" class="flat"> GPS
										</li>
										<li style="display: inline; margin-left: 15px;">
											<input id="cmd-hrm" name="cmd-uplink" type="checkbox" class="flat"> HRM 
										</li>
										<li style="display: inline; margin-left: 15px;">
											<input id="cmd-3d" name="cmd-uplink" type="checkbox" class="flat"> 3D 
										</li>
									</ul>
                   				</div>
							</div>
							<div style="margin-top: 8px;">
								<div style="width: 35%">
									<input type="radio" class="flat" name="command" id="sosSet" required/> SOS Command
								</div>
								<div style="margin-left: 30px; margin-top: 10px;">
									<div>
										<label class="control-label" style="width: 30%"> Protector Number </label> 
										<label class="control-label" style="margin-left: 15px;"> 
											<input id="protectorNumber" name="cmd-number" type="text" class="form-control search-text">
										</label>
									</div>
									<div style="margin-top: 8px;">
										<label class="control-label" style="width: 30%"> Police Number </label> 
										<label class="control-label" style="margin-left: 15px;"> 
											<input id="policeNumber" name="cmd-number" type="text" class="form-control search-text">
										</label>
									</div>
                   				</div>
							</div>
							<div style="margin-top: 8px;">
								<div style="width: 35%">
									<input type="radio" class="flat" name="command" id="sosSet" required/> BCON Command
								</div>
								<div style="margin-left: 30px; margin-top: 10px;">
									<div>
										<label class="control-label" style="width: 30%"> BLE ID </label> 
										<label class="control-label" style="margin-left: 15px;"> 
											<input id="bleNumber" name="cmd-ble" type="text" class="form-control search-text">
										</label>
									</div>
                   				</div>
							</div>
							<div>
								<div>
									<button type="button" onclick="sendCommand();" 
										class="applyBtn btn btn-default btn-small btn-primary" style="float: right;">Send</button>
								</div>
							</div>
						</div>
	                </div>
	              </div>
        		</div> -->
        		
        		<!-- <div class="col-md-8 col-sm-8 col-xs-12"> -->
        			<div class="row">
        				<div class="col-md-12 col-sm-12 col-xs-12">
			              	<div class="x_panel">
				                <div class="x_title">
				                	<h2>Ondemand History Chart<small>Command Code History</small></h2>
				                	<div class="clearfix"></div>
				                </div>
				                <div class="x_content">
			                		<div id="vizContainerChart" style="width: 100%; height: 100%;"></div>
			                		<div id="markDetails">Information about selected marks displays here.</div>
								</div>
	       					</div>
        				</div>
        			</div>
        		</div>
        </div>
      </div>
    </div>

    <!-- onDemand Popup -->
	<div class="modal fade" id="onDemandPopup" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
	  <div class="modal-dialog" role="document">
	    <div class="modal-content">
	      <div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">×</span></button>
				<h4 class="modal-title" id="mySmallModalLabel">Group OnDemand</h4>
	      </div>
	      <div id="modal-body" class="modal-body">
	        <div>
	        	          
	          <div class="form-group">
				<div style="width: 35%">
					<input type="radio" class="flat" name="command-popup" id="periodSet-popup" checked/> Period Command
				</div>
				<div style="margin-left: 30px; margin-top: 10px;">
					<select id="heard-popup" name="cmd-period-popup" class="form-control" required>
                    	<option value="1">1 Interval Min.</option>
                        <option value="15">15 Interval Min.</option>
                        <option value="20">20 Interval Min.</option>
                        <option value="25">25 Interval Min.</option>
                        <option value="30">30 Interval Min.</option>
                    </select>
               	</div>
	          </div>
	          <div class="form-group">
				<div style="width: 35%">
					<input type="radio" class="flat" name="command-popup" id="uplinkSet-popup"/> Uplink Command
				</div>
				<div style="margin-top: 10px;">
					<ul style="list-style: none">
						<li style="display: inline;">
							<input id="cmd-gps-popup" name="cmd-uplink-popup" type="checkbox" class="flat"> GPS
						</li>
						<li style="display: inline; margin-left: 15px;">
							<input id="cmd-hrm-popup" name="cmd-uplink-popup" type="checkbox" class="flat"> HRM 
						</li>
						<li style="display: inline; margin-left: 15px;">
							<input id="cmd-3d-popup" name="cmd-uplink-popup" type="checkbox" class="flat"> 3D 
						</li>
					</ul>
               	</div>
	          </div>
	          <div class="form-group">
				<div style="width: 35%">
					<input type="radio" class="flat" name="command-popup" id="sosSet-popup" required/> SOS Command
				</div>
				<div style="margin-left: 30px; margin-top: 10px;">
					<div>
						<label class="control-label" style="width: 30%"> Protector Number </label> 
						<label class="control-label" style="margin-left: 15px;"> 
							<input id="protectorNumber-popup" name="cmd-number-popup" type="text" class="form-control search-text">
						</label>
					</div>
					<div style="margin-top: 8px;">
						<label class="control-label" style="width: 30%"> Police Number </label> 
						<label class="control-label" style="margin-left: 15px;"> 
							<input id="policeNumber-popup" name="cmd-number-popup" type="text" class="form-control search-text">
						</label>
					</div>
               				</div>
	          </div>
	        </div>
	      </div>
	      <div class="modal-footer">
	        <button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>
	        <button type="button" class="btn btn-primary" onclick="javascript:sendGroupCommand()">Send message</button>
	      </div>
	    </div>
	  </div>
	</div>

	<script src="../../js/jquery-1.12.4.min.js"></script>
    <script src="../../vendors/bootstrap/dist/js/bootstrap.min.js"></script>
    <script src="../../js/bootstrap-modal.js"></script>
    <script src="../../vendors/bootstrap-progressbar/bootstrap-progressbar.min.js"></script>
    <script src="../../vendors/moment/min/moment.min.js"></script>
	<script src="../../vendors/DateJS/build/date.js"></script>
	<script src="../../vendors/bootstrap-daterangepicker/daterangepicker.js"></script>
	<script src="../../build/js/custom.js"></script>
	<script src="../../js/jquery.dataTables.min.js"></script>
	<script src="../../js/gadget/gadget.grid.js"></script>
	<script src="../../vendors/iCheck/icheck.min.js"></script>
		
	<!-- Tableau Scripts -->
	<script src="../../js/tableau-2.min.js"></script>
	<script src="../../js/tableau-2.2.1.min.js"></script>
	
	<script type="text/javascript">
	var wearableCheckGrid = "";
	var wearableGrid = "";
	var installStartTime = "";
	var installEndTime =  "";
	var lastCommStartTime = "";
	var lastCommEndTime = "";
	var pageNum = "0";
	var wdId = "";
	
	$( document ).ready(function() {
		//element set
		$('#reportrange').find('span').html('');
		$('#reportrange2').find('span').html('');
		setActiveMenu('Ondemand History');
		getGridData();
		enableCmdPeriod();
		getTicketFromTableau();
		
		//init data set
	    $('.selector').css('height', $(window).height()-50);
	    $(window).resize(function (){
	    	 document.location.reload();
	 	     $('.selector').css('height', $(window).height()-50);
	    })
	});
	
	function onToolbarStateChange(toolbarEvent) {
	    // when event occurs, get state and update custom buttons
	    var toolbarState = toolbarEvent.getToolbarState();
	    updateToolbarState(toolbarState)
	}
    
	/* tableau history chart */
	function getTicketFromTableau() {
    	$.getJSON('${ctx}/gadget/tableau/getTicketFromTableau.do', function(json) {
   			if(json.status == 'SUCCESS') {
   				if(json.status == 'SUCCESS') {
	   				var containerDiv = document.getElementById("vizContainer");
	   				var url = json.embedGetAddress + "/views/IoT-Ondemand-History/Ondemand-Dashboard?:embed=y&:showAppBanner=false&:display_count=no&:showVizHome=no&:";
	   				var option = {
	 						   	hideTabs: true,
						    	hideToolbar: true,
						    	onFirstInteractive: function () {
	 							    // Listen for toolbar events
	 					            viz.addEventListener(tableau.TableauEventName.TOOLBAR_STATE_CHANGE, onToolbarStateChange);
	 					        }
	   				}
	   				var viz = new tableau.Viz(containerDiv, url, option);
   				}
   			}
 	    });
	}
	
	function setGroupOnemand() {
		pageNum = 0; 
		
		$('#wearable_table').hide();
		$('#wearable_table_paginate').hide();
		$('#wdDetails').hide();
		$('#btnOnDemand').hide();
		
		$('#wearable_check_table').show();
		$('#btnOnDemandParam').show();
		
		$('#btn-group-ondemand').attr("disabled", true);
		$('#all-check-label').removeClass('active');
		getCheckGridData();
	}
	
	//<< Check Grid >> -------- 
	function getCheckGridData() {
		wearableCheckGrid = $('#wearable_check_table').DataTable({
			processing: true,
                serverSide: true,
                searching: false,
                ordering: false,
                info: false,
                bLengthChange: false,
                bDestroy: true,
                pagingType: 'simple_numbers',
                ajax: {
		    	"type": 'GET',
		    	"url": '${ctx}/gadget/history/getOndemandHistoryGrid.do',
		    	"data": function (d) {
		    		d.pageNum = pageNum;
		    		d.wdId = wdId.trim();
		    		d.installStartTime = installStartTime.trim();
		    		d.installEndTime = installEndTime.trim();
		    		d.lastCommStartTime = lastCommStartTime.trim();
		    		d.lastCommEndTime = lastCommEndTime.trim();
		    	}
		    },
		    columns: [	   
	        	 { "data": null },
	        	 { "data": "wdId" },
	        	 { "data": "operationCommandCode" },
	        	 { "data": "operationType" },
	        	 { "data": "status" },
	        	 { "data": "controlCode" },
	        	 { "data": "updateTime" },
	        	 { "data": "description" },
	        	 { "data": "errorReason" }
	        ],
	        columnDefs : [{
	        	'targets': 0,
	        	'render': function (data, type, full, meta){
	        		return '<div class="btn-group" data-toggle="buttons">' + 
	        					'<label name="table_records" class="btn btn-success ckeck-btn-grid" onclick="javascript:clickRow(this)">' +
	        						'<input name="table_records_checkbox" type="checkbox" autocomplete="off">' +
									'<span class="glyphicon glyphicon-ok"></span>' +
								'</label>' +
							'</div>'
	        	}
	        }],
	        fnRowCallback: function( nRow, aData, iDisplayIndex, iDisplayIndexFull ) {
	        		        	
	        	//set color
				var dat = aData.lastCommDt;	
	        	var days3 = moment(new Date()).add(-3, 'days').format('YYYYMMDDhhmmss');
				var days2 = moment(new Date()).add(-2, 'days').format('YYYYMMDDhhmmss');
				var days1 = moment(new Date()).add(-1, 'days').format('YYYYMMDDhhmmss')
	        						
	        	if(Number(dat) <= Number(days3)) {
	        		$(nRow).find('td')[1].style.color = '#980000';
	        	} else if(Number(dat) <= Number(days2)) {
	        		$(nRow).find('td')[1].style.color = '#FF5E00';
	        	} else if(Number(dat) <= Number(days1)) {
	        		$(nRow).find('td')[1].style.color = '#FFBB00';
	        	} else {
	        		$(nRow).find('td')[1].style.color = '#26B99A';
	        	}
				$(nRow).find('td')[1].style.fontWeight="bold";
	        }
		});
	}

	function onDemandOpen() {
		//popup radio init
		$('#periodSet-popup').iCheck('check')
		$('#uplinkSet-popup').iCheck('uncheck')
		$('#sosSet-popup').iCheck('uncheck')
		
		//popup check init
		$('#cmd-gps-popup').iCheck('uncheck')
		$('#cmd-hrm-popup').iCheck('uncheck')
		$('#cmd-3d-popup').iCheck('uncheck')
		
		//popup sos init
		$('#protectorNumber-popup').val('');
		$('#policeNumber-popup').val('');
		
		$('#onDemandPopup').modal('show');
		enableCmdPeriod();
	}
	
	function onDemandClose() {
		pageNum = 0; 
		
		$('#wearable_table').show();
		$('#wearable_table_paginate').show();		
		$('#btnOnDemand').show();
		$('#wdDetails').hide();
		
		$('#btnOnDemandParam').hide();
		$('#wearable_check_table').hide();
		$('#wearable_check_table_paginate').hide();
	}
	
	function clickRow(el) {
		//클릭한 상태에서 'active' class가 포함되기 전(체크박스가 보이기 전)에 동작되므로
		//active class가 있다면 체크를 해제하기 위한 것이고
		//active class가 없다면 체크를 설정하기 위한 것이다.
		if($(el).hasClass('active')) {
			//체크 해제
			var enableBtn = false;
			$('label[name=table_records]').each(function(i) {
				if(this != el) {
					if($(this).hasClass('active')) {
						enableBtn = true;
						$('#btn-group-ondemand').attr("disabled", false);
						return true;
					}
				}
			});
			
			if(!enableBtn) {
				$('#btn-group-ondemand').attr("disabled", true);
			}
			
		} else {
			//체크 설정
			$('#btn-group-ondemand').attr("disabled", false);
		}
	}
	
	//checkbox group ondemand
	function sendGroupCommand() {
		var n;
		var period;
		var gpsOn;
		var hrmOn;
		var paceOn;
		var protectorNumber;
		var policeNumber;
		var wearableIds = "";
		
		$('label[name=table_records]').each(function(i) {
			if($(this).hasClass('active')) {
				var parentElement = $(this).closest('tr');
				wearableIds += $(parentElement).find('td').eq(1).text() + ",";
			}
		});
		wearableIds = wearableIds.slice(0, -1); //마지막 문자(,) 제거
		
		$('#onDemandPopup .iradio_flat-green').each(function (i) {
			if($(this).hasClass('checked')) {
				n = i;
				return false;
			}
		});
		
		switch(n) {
		  case 0:
			  period = $("#heard-popup option:selected").val();
			  alert("period:"+n +"|"+ period);
			  sendCommandByAjax(n, wearableIds, period, 0, 0);
			  break;
		  case 1:
			  $('#onDemandPopup .icheckbox_flat-green').each(function (i) {
					if(i == 0) {
						gpsOn = $(this).hasClass('checked') ? true : false;
					}else if(i == 1) {
						hrmOn = $(this).hasClass('checked') ? true : false;
					} else if(i == 2) {
						paceOn = $(this).hasClass('checked') ? true : false;
					}
			  });
			  alert("option:"+n +"|"+ gpsOn +"|"+ hrmOn +"|"+ paceOn);
			  sendCommandByAjax(n, wearableIds, gpsOn, hrmOn, paceOn);
			  break;
		  case 2:
            protectorNumber = $('#protectorNumber-popup').val();
            policeNumber = $('#policeNumber-popup').val();
            alert("option:"+n +"|"+protectorNumber +"|"+ policeNumber);
            sendCommandByAjax(n, wearableIds, protectorNumber, policeNumber, 0);
			break;
		}
	}
	
	$('#wearable_check_table').on('page.dt', function(){
		$('#all-check-label').removeClass('active');
		$('#btn-group-ondemand').attr("disabled", true);
		 var info = wearableCheckGrid.page.info();
		 pageNum = info.page;
	});
	
	//클릭한 상태에서 'active' class가 포함되기 전(체크박스가 보이기 전)에 동작되므로
	//active class가 있다면 체크를 해제하기 위한 것이고
	//active class가 없다면 체크를 설정하기 위한 것이다.
	$('#all-check-label').on('click', function(){
		if($(this).hasClass('active')) {
			$('#btn-group-ondemand').attr("disabled", true);
			$('label[name=table_records]').removeClass('active');
		} else {
			$('#btn-group-ondemand').attr("disabled", false);
			$('label[name=table_records]').addClass('active');
		}
	});
	//<< Check Grid End >> --------
	
	//<< Grid >> -------- 
	function getGridData() {
		wearableGrid = $('#wearable_table').DataTable({
			processing: true,
                serverSide: true,
                searching: false,
                ordering: false,
                info: false,
                bLengthChange: false,
                bDestroy: true,
                pagingType: 'simple_numbers',
                ajax: {
		    	"type": 'GET',
		    	"url": '${ctx}/gadget/history/getOndemandHistoryGrid.do',
		    	"data": function (d) {
		    		d.pageNum = pageNum;
		    		d.wdId = wdId.trim();
		    		d.installStartTime = installStartTime.trim();
		    		d.installEndTime = installEndTime.trim();
		    		d.lastCommStartTime = lastCommStartTime.trim();
		    		d.lastCommEndTime = lastCommEndTime.trim();
		    	}
		    },
		    columns: [	   
	        	 { "data": null },
	        	 { "data": "wdId" },
	        	 { "data": "operationCommandCode" },
	        	 { "data": "operationType" },
	        	 { "data": "status" },
	        	 { "data": "controlCode" },
	        	 { "data": "updateTime" },
	        	 { "data": "description" },
	        	 { "data": "errorReason" }
	        ],
	        fnRowCallback: function( nRow, aData, iDisplayIndex, iDisplayIndexFull ) {
	        	//set row index
	        	var line = ((pageNum * 10) + (iDisplayIndexFull + 1));
	        	$(nRow).find('td').eq(0).text(line);
	        	
	        	//set color
				var dat = aData.lastCommDt;	
	        	var days3 = moment(new Date()).add(-3, 'days').format('YYYYMMDDhhmmss');
				var days2 = moment(new Date()).add(-2, 'days').format('YYYYMMDDhhmmss');
				var days1 = moment(new Date()).add(-1, 'days').format('YYYYMMDDhhmmss')
	        						
	        	if(Number(dat) <= Number(days3)) {
	        		$(nRow).find('td')[1].style.color = '#980000';
	        	} else if(Number(dat) <= Number(days2)) {
	        		$(nRow).find('td')[1].style.color = '#FF5E00';
	        	} else if(Number(dat) <= Number(days1)) {
	        		$(nRow).find('td')[1].style.color = '#FFBB00';
	        	} else {
	        		$(nRow).find('td')[1].style.color = '#26B99A';
	        	}
				$(nRow).find('td')[1].style.fontWeight="bold";
	        }
		});
	}
	
	$('#wearable_table').on('page.dt', function(){			
		 var info = wearableGrid.page.info();
		 pageNum = info.page;
	});
	
	$(document).on('click', '#wearable_table tr', function() {
		//지도의 기본좌표는 나주시청
		var latitude = 35.016122;
		var longitude = 126.710756;
		
		var wdId = $(this).find('td').eq(1).html();
		$('#wdDetails').show();
				
		$.getJSON('${ctx}/gadget/history/getWearableDevice.do', {wdId : wdId},
				function(json) {
 	        if(json.status == "SUCCESS") {
 	        	var wdInfo = json.sensor;
 	        	$('#detailId').html(wdInfo.sid);
 	        	
 	        	//Period Command
 	        	var period = wdInfo.wakeupperiod;
 	        	switch(period / 60) {
 	        	case 15:
 	        		$('#heard').val("15").attr("selected", "selected");
 	        		break;
 	        	case 20:
 	        		$('#heard').val("20").attr("selected", "selected");
 	        		break;
 	        	case 25:
 	        		$('#heard').val("25").attr("selected", "selected");
 	        		break;
 	        	case 30:
 	        		$('#heard').val("30").attr("selected", "selected");
 	        		break;
 	        	default:
 	        		$('#heard').val("1").attr("selected", "selected");
 	        		break;
 	        	}
 	        	
 	        	//Uplink Command
 	        	var gpsOn = wdInfo.gpsperiod;
 	        	var hrmOn = wdInfo.hrmperiod;
 	        	if(gpsOn > 0) 
 	        		$('#cmd-gps').iCheck('check');
 	        	else
 	        		$('#cmd-gps').iCheck('uncheck');
 	        	
 	        	if(hrmOn > 0) 
 	        		$('#cmd-hrm').iCheck('check');
 	        	else 
 	        		$('#cmd-hrm').iCheck('uncheck');
 	        	
 	        	//SOS Command
 	        	var customer = json.customer;
 	        	if(customer != null) {
 	        		$('#protectorNumber').val(customer.guardcellphone);
 	 	        	$('#policeNumber').val(customer.guardtelno);	
 	        	}else {
 	        		$('#protectorNumber').val('');
 	 	        	$('#policeNumber').val('');
 	        	}
 	        	
 	        	//BCON Command
 	        	$('#bleNumber').val(wdInfo.pairid);
 	        	
 	        	//Ondemand History Chart
 	        	$.getJSON('${ctx}/gadget/tableau/getTicketFromTableau.do', {wdId : wdId}, function(json) {
	 	       		if(json.status == 'SUCCESS') {
	 	       			
	       				var containerDiv = document.getElementById("vizContainerChart");
	       				var url = json.embedGetAddress + "/views/IoT-Ondemand-History-Chart/History-Chart-DashBoard?:embed=y&:showAppBanner=false&:display_count=no&:showVizHome=no&:";
	       				var option = {
	 						hideTabs: false,
						    hideToolbar: true,
						    onFirstInteractive: function (e) {
	
				              workbook = viz.getWorkbook();
				              activeSheet = workbook.getActiveSheet();
	
				              var onSuccess = function(parameters) {
				                  console.debug('onSuccess : ', parameters);
				                  $.each(parameters, function(index, parameter) {
				                	  console.log('parameter : ', parameter);
				                      console.log('parameter.getName() : ', parameter.getName());
				                      console.log('parameter.getCurrentValue() : ', parameter.getCurrentValue());
				                      console.log('parameter.getDataType() : ', parameter.getDataType());
				                      console.log('parameter.getAllowableValuesType() : ', parameter.getAllowableValuesType());
				                      console.log('parameter.getAllowableValues() : ', parameter.getAllowableValues());
				                      console.log('parameter.getMinValue() : ', parameter.getMinValue());
				                      console.log('parameter.getMaxValue() : ', parameter.getMaxValue());
				                      console.log('parameter.getStepSize() : ', parameter.getStepSize());
				                      console.log('parameter.getDateStepPeriod() : ', parameter.getDateStepPeriod());
				                  });
				                  workbook
				                      .changeParameterValueAsync('SearchWdId', wdId)
				                      .then(function(updateParameter) {
				                          console.log('updateParameter : ', updateParameter);
				                      });
				              };
				              var onFault = function() {
				            	  console.log("tableau onFault ~~");
				              };
				              workbook
				                  .getParametersAsync()
				                  .then(onSuccess, onFault);
	   					          }
	       				}
	       				
	       				if (viz) { // If a viz object exists, delete it.
	       	                viz.dispose();
	       	            }
	       				
	       				try {
	       					var viz = new tableau.Viz(containerDiv, url, option);
	   	   			      } catch(e) {
	   	   			      	console.log('error : ', e);
	   	   			      }
	 	       		}
 	    	    });
 	        }
 	    });
	});
	
	function wearableSearch() {
		pageNum = 0;
		wdId = $('#wearableId').val();
		
		var installTime = $('#reportrange').find('span').html();
		var lastTime = $('#reportrange2').find('span').html();
		
		installStartTime = getStartTime(installTime);
		installEndTime = getEndTime(installTime);
		lastCommStartTime = getStartTime(lastTime);
		lastCommEndTime = getEndTime(lastTime);
		
		if($('#wearable_table').is(':visible')) {
			wearableGrid.ajax.reload( function ( json ) {
				 return json.data;
			});	
		}else {
			wearableCheckGrid.ajax.reload( function ( json ) {
				 return json.data;
			});	
		}
	}
	
	$('input[type="radio"]').on('ifChecked', function(event){
		  var el = $(this);
		  $('#wdDetails').find('input[name=command]').each(function (n) {
			  if($(this).is(el)) {
				  switch(n) {
				  case 0:
					  enableCmdPeriod();
					  break;
				  case 1:
					  enableCmdUplink();
					  break;
				  case 2:
					  enableCmdSOS();
					  break;
				  case 3:
					  enableCmdBLE();
					  break;
				  }
		  	}
		  });
		  
		  $('#modal-body').find('input[name=command-popup]').each(function (n) {
			  if($(this).is(el)) {
				  switch(n) {
				  case 0:
					  enableCmdPeriod();
					  break;
				  case 1:
					  enableCmdUplink();
					  break;
				  case 2:
					  enableCmdSOS();
					  break;
				  }
		  	}
		  });
	});
	
	function enableCmdPeriod() {
		if($('#wearable_table').is(':visible')) {
			//Period Set
			$('[name=cmd-period]').attr('disabled', false);
			
			//Uplink Set
			$('[name=cmd-uplink]').each(function (n) {
				$(this).iCheck('disable');
			});
			
			//sos number set
			$('[name=cmd-number]').each(function (n) {
				$(this).attr('disabled', true);
			});
			
			//ble number set
			$('[name=cmd-ble]').attr('disabled', true);
		} else {
			//Period Set
			$('[name=cmd-period-popup]').attr('disabled', false);
			
			//Uplink Set
			$('[name=cmd-uplink-popup]').each(function (n) {
				$(this).iCheck('disable');
			});
			
			//sos number set
			$('[name=cmd-number-popup]').each(function (n) {
				$(this).attr('disabled', true);
			});
		}
	}
		
	
	function enableCmdUplink() {
		if($('#wearable_table').is(':visible')) {
			$('[name=cmd-period]').attr('disabled', true);
			
			$('[name=cmd-uplink]').each(function (n) {
				$(this).iCheck('enable');
			});
			
			$('[name=cmd-number]').each(function (n) {
				$(this).attr('disabled', true);
			});
			
			$('[name=cmd-ble]').attr('disabled', true);
		} else {
			$('[name=cmd-period-popup]').attr('disabled', true);
			
			$('[name=cmd-uplink-popup]').each(function (n) {
				$(this).iCheck('enable');
			});
			
			$('[name=cmd-number-popup]').each(function (n) {
				$(this).attr('disabled', true);
			});
		}
	}
	
	function enableCmdSOS() {
		if($('#wearable_table').is(':visible')) {
			$('[name=cmd-period]').attr('disabled', true);
			
			$('[name=cmd-uplink]').each(function (n) {
				$(this).iCheck('disable');
			});
			
			$('[name=cmd-number]').each(function (n) {
				$(this).attr('disabled', false);
			});
			
			$('[name=cmd-ble]').attr('disabled', true);
		} else {
			$('[name=cmd-period-popup]').attr('disabled', true);
			
			$('[name=cmd-uplink-popup]').each(function (n) {
				$(this).iCheck('disable');
			});
			
			$('[name=cmd-number-popup]').each(function (n) {
				$(this).attr('disabled', false);
			});
		}
	}
	
	function enableCmdBLE() {
		$('[name=cmd-period]').attr('disabled', true);
		
		$('[name=cmd-uplink]').each(function (n) {
			$(this).iCheck('disable');
		});
		
		$('[name=cmd-number]').each(function (n) {
			$(this).attr('disabled', true);
		});
		
		$('[name=cmd-ble]').attr('disabled', false);
	}
	
	function sendCommand() {
		var n;
		var period;
		var gpsOn;
		var hrmOn;
		var paceOn;
		var protectorNumber;
		var policeNumber;
		var bleId;
		var wearableId = $('#detailId').html();
		
		pageNum = 0;
		
		$('#wdDetails .iradio_flat-green').each(function (i) {
			if($(this).hasClass('checked')) {
				n = i;
				return false;
			}
		});
		
		switch(n) {
		  case 0:
			  period = $("#heard option:selected").val();
			  sendCommandByAjax(n, wearableId, period, 0, 0);
			  break;
		  case 1:
			  $('#wdDetails .icheckbox_flat-green').each(function (i) {
					if(i == 0) {
						gpsOn = $(this).hasClass('checked') ? true : false;
					}else if(i == 1) {
						hrmOn = $(this).hasClass('checked') ? true : false;
					} else if(i == 2) {
						paceOn = $(this).hasClass('checked') ? true : false;
					}
			  });
			  sendCommandByAjax(n, wearableId, gpsOn, hrmOn, paceOn);
			  break;
		  case 2:
              protectorNumber = $('#protectorNumber').val();
              policeNumber = $('#policeNumber').val();
              sendCommandByAjax(n, wearableId, protectorNumber, policeNumber, 0);
			  break;
		  case 3:
			  bleId = $('#bleNumber').val();
			  sendCommandByAjax(n, wearableId, bleId, 0, 0);
			  break;
		}
	}

	//jquery(javascript) 시스템에서 정의한 함수만 on으로 시작하도록 되어 있는 것이 관례이며	
	//사용자 함수는 on이 아닌 동사로 부터 시작하는 것이 대부분
	//onDemand2Controller - > sendCommandByAjax
	function sendCommandByAjax(flag, wearableId, arg1, arg2, arg3){

		var sendData = {"flag" : flag, "wearableId" : wearableId, "arg1" : arg1, "arg2" : arg2, "arg3" : arg3};
		$.ajax({
            "type": 'GET',
            "url": '${ctx}/gadget/history/setOnDemand.do',
            "data": sendData,
			"dataType" : 'text',
			success : function(result) {
				//var data = JSON.parse(result);
				alert(result);
				alert("온디맨드 명령이 성공적으로 보내졌습니다.");
			},
			error: function(result) {
				alert("온디맨드 명령이 실패하였습니다.");
			}
		});
    }
	</script>
	</div>
  </body>
  </html>